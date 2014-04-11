package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;

/**
 * Task data manager: checks user's input determines the type of tasks breaks up
 * parameters for the tasks passes tasks to DAO to retrieve data from files.
 * <p>
 * Existing functions related to MINA: addTask(), deleteTask()
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0080412W
public class TaskDataManager {
    private static Logger logger = LogManager.getLogger(TaskDataManager.class
            .getName());

    // error messages
    public static final int ERROR_MISSING_TASK_DESCRIPTION = -2;

    // parameters of String after trimming
    public static final int PARAM_TASK_DESCRIPTION = 0;

    // Sets for completed and uncompleted tasks
    private SortedSet<TodoTask> _uncompletedTodoTasks;
    private SortedSet<DeadlineTask> _uncompletedDeadlineTasks;
    private SortedSet<EventTask> _uncompletedEventTasks;

    private SortedSet<TodoTask> _completedTodoTasks;
    private SortedSet<EventTask> _completedEventTasks;
    private SortedSet<DeadlineTask> _completedDeadlineTasks;

    // HashMaps for recurring tasks
    private HashMap<String, ArrayList<Task<?>>> _recurringTasks;
    private int _maxRecurTagInt = 0;

    private static final int TAG_INT_POS = 1;

    // Sync tools
    private final List<TaskSetDataParameter> allDataList = new ArrayList<TaskSetDataParameter>(
            6);

    private List<MemoryDataObserver> _observers;
    private DataSyncManager _syncManager;

    public TaskDataManager() {
        initiateVariables();
    }

    // @author A0909865X
    private void initiateVariables() {
        _uncompletedTodoTasks = new TreeSet<TodoTask>();
        _uncompletedDeadlineTasks = new TreeSet<DeadlineTask>();
        _uncompletedEventTasks = new TreeSet<EventTask>();
        _completedTodoTasks = new TreeSet<TodoTask>();
        _completedDeadlineTasks = new TreeSet<DeadlineTask>();
        _completedEventTasks = new TreeSet<EventTask>();

        _recurringTasks = new HashMap<String, ArrayList<Task<?>>>();

        _observers = new ArrayList<MemoryDataObserver>();
        _syncManager = null;
    }

    @SuppressWarnings("unchecked")
    public TaskDataManager(DataSyncManager syncManager) {
        SortedSet<? extends Task<?>> tempTasks = null;

        _syncManager = syncManager;

        _observers = new ArrayList<MemoryDataObserver>();
        _observers.add(syncManager.getDataOberserver());

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.TODO, false);
            _uncompletedTodoTasks = (SortedSet<TodoTask>) tempTasks;

        } catch (IOException e) {
            _uncompletedTodoTasks = new TreeSet<TodoTask>();
            logger.error(e, e);
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.DEADLINE, false);
            _uncompletedDeadlineTasks = (SortedSet<DeadlineTask>) tempTasks;
        } catch (IOException e) {
            _uncompletedDeadlineTasks = new TreeSet<DeadlineTask>();
            logger.error(e, e);
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.EVENT, false);
            _uncompletedEventTasks = (SortedSet<EventTask>) tempTasks;
        } catch (IOException e) {
            _uncompletedEventTasks = new TreeSet<EventTask>();
            logger.error(e, e);
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.TODO, true);
            _completedTodoTasks = (SortedSet<TodoTask>) tempTasks;
        } catch (IOException e) {
            _completedTodoTasks = new TreeSet<TodoTask>();
            logger.error(e, e);
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.DEADLINE, true);
            _completedDeadlineTasks = (SortedSet<DeadlineTask>) tempTasks;
        } catch (IOException e) {
            _completedDeadlineTasks = new TreeSet<DeadlineTask>();
            logger.error(e, e);
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.EVENT, true);
            _completedEventTasks = (SortedSet<EventTask>) tempTasks;
        } catch (IOException e) {
            _completedEventTasks = new TreeSet<EventTask>();
            logger.error(e, e);
        }

        TaskMapDataParameter taskMapData = _syncManager.loadTaskMap();
        if (taskMapData == null) {
            _recurringTasks = new HashMap<String, ArrayList<Task<?>>>();
            updateRecurMap();
        } else {
            _recurringTasks = taskMapData.getRecurringTasks();
            _maxRecurTagInt = taskMapData.getMaxRecurTagInt();
        }

    }

    private void updateRecurMap() {
        // loop through deadlines
        Iterator<DeadlineTask> deadlineTaskIterator = _uncompletedDeadlineTasks
                .iterator();
        if (deadlineTaskIterator.hasNext()) {
            DeadlineTask currDeadlineTask = deadlineTaskIterator.next();
            while (deadlineTaskIterator.hasNext()) {
                checkRecur(currDeadlineTask);
                currDeadlineTask = deadlineTaskIterator.next();
            }
        }

        // loop through events
        Iterator<EventTask> eventTaskIterator = _uncompletedEventTasks
                .iterator();
        if (eventTaskIterator.hasNext()) {
            EventTask currEventTask = eventTaskIterator.next();
            while (eventTaskIterator.hasNext()) {
                checkRecur(currEventTask);
                currEventTask = eventTaskIterator.next();
            }
        }
    }

    private void checkRecur(Task<?> currTask) {
        if (currTask.getTag().contains("RECUR")) {
            includeInRecurMap(currTask, currTask.getTag());
        }
    }

    private void includeInRecurMap(Task<?> taskToInclude, String recurTag) {
        if (!isValidRecurTag(recurTag)) {
            return;
        }

        if (_recurringTasks.containsKey(recurTag)) {
            _recurringTasks.get(recurTag).add(taskToInclude);

        } else {
            ArrayList<Task<?>> taskList = new ArrayList<Task<?>>();
            taskList.add(taskToInclude);

            _recurringTasks.put(recurTag, taskList);

        }
    }

    private boolean isValidRecurTag(String tag) {
        int recurTagInt = -1;

        if (!tag.contains("_")) {
            return false;
        } else {
            String[] tagTokens = tag.split("_", 2);

            try {
                recurTagInt = Integer.parseInt(tagTokens[TAG_INT_POS]);
            } catch (NumberFormatException e) {
                // task is not added to HashMap, it is not treated as a
                // recurring task anymore
                return false;
            }

            _maxRecurTagInt = recurTagInt > _maxRecurTagInt ? recurTagInt
                    : _maxRecurTagInt;

            return true;
        }
    }

    /* getter methods: uncompleted tasks */
    /**
     * Returns the all the uncompleted to-do tasks currently in memory.
     * 
     * @return uncompletedTodoTasks
     */
    public SortedSet<TodoTask> getUncompletedTodoTasks() {
        return _uncompletedTodoTasks;
    }

    /**
     * Returns the all the uncompleted deadline tasks currently in memory.
     * 
     * @return uncompletedDeadlineTasks
     */
    public SortedSet<DeadlineTask> getUncompletedDeadlineTasks() {
        return _uncompletedDeadlineTasks;
    }

    /**
     * Returns the all the uncompleted event tasks currently in memory.
     * 
     * @return uncompletedEventTasks
     */
    public SortedSet<EventTask> getUncompletedEventTasks() {
        return _uncompletedEventTasks;
    }

    /* getter methods: completed tasks */
    /**
     * Returns the all the completed to-do tasks currently in memory.
     * 
     * @return completedTodoTasks
     */
    public SortedSet<TodoTask> getCompletedTodoTasks() {
        return _completedTodoTasks;
    }

    /**
     * Returns the all the completed deadline tasks currently in memory.
     * 
     * @return completedDeadlineTasks
     */
    public SortedSet<DeadlineTask> getCompletedDeadlineTasks() {
        return _completedDeadlineTasks;
    }

    /**
     * Returns the all the completed event tasks currently in memory.
     * 
     * @return completedEventTasks
     */
    public SortedSet<EventTask> getCompletedEventTasks() {
        return _completedEventTasks;
    }

    /* getter methods: recurring map */
    /**
     * Returns the HashMap containing the mapping of all recurring tasks
     * currently in memory.
     * 
     * @return recurringTasks
     */
    public HashMap<String, ArrayList<Task<?>>> getRecurringTasks() {
        return _recurringTasks;
    }

    /**
     * Creates a Task depending on its type and parameters, then saves it to
     * memory and DAO. If changes are successfully saved by DAO, it returns a
     * Task object.
     * 
     * @param DataParameter addParameters
     * @return added Task
     */
    public Task<?> addTask(DataParameter addParameters) {
        assert (addParameters.getNewTaskType() != null);
        if (addParameters.getTaskObject() != null) {
            return directAddTask(addParameters.getTaskObject());

        } else if (addParameters.getTag() != null && addParameters.getTag()
                .equals("RECUR")) {
            assert (addParameters.getNewTaskType().equals(TaskType.EVENT) || addParameters
                    .getNewTaskType().equals(TaskType.DEADLINE));

            String recurTag = "RECUR_" + _maxRecurTagInt++;
            assert (!_recurringTasks.containsKey(recurTag));

            Task<?> taskToRetrun = addRecurringTask(addParameters, recurTag);

            syncUncompletedTasks(TaskType.DEADLINE);
            syncUncompletedTasks(TaskType.EVENT);
            syncHashMaps();

            return taskToRetrun;

        } else {
            Task<?> taskToRetrun = addRegTask(addParameters);

            syncUncompletedTasks(TaskType.TODO);
            syncUncompletedTasks(TaskType.DEADLINE);
            syncUncompletedTasks(TaskType.EVENT);

            return taskToRetrun;
        }
    }

    private Task<?> directAddTask(Task<?> taskObject) {
        switch (taskObject.getType()) {
            case TODO :
                if (_uncompletedTodoTasks.add((TodoTask) taskObject)) {
                    syncUncompletedTasks(TaskType.TODO);

                    return taskObject;
                }
                return null;
            case DEADLINE :
                if (_uncompletedDeadlineTasks.add((DeadlineTask) taskObject)) {
                    syncUncompletedTasks(TaskType.DEADLINE);

                    return taskObject;
                }
                return null;
            case EVENT :
                if (_uncompletedEventTasks.add((EventTask) taskObject)) {
                    syncUncompletedTasks(TaskType.EVENT);

                    return taskObject;
                }
                return null;

            default :
                return null;
        }
    }

    private Task<?> addRecurringTask(DataParameter addParameters,
            String recurTag) {

        assert (addParameters.getFreqOfTimeType() != 0);

        Date endRecurOn = addParameters.getEndRecurOn() == null ? generateStartOfNextYear()
                : addParameters.getEndRecurOn();

        switch (addParameters.getNewTaskType()) {
            case DEADLINE :
                Date currDeadline = addParameters.getEndDate();
                DeadlineTask currDeadlineTask;

                while (currDeadline.compareTo(endRecurOn) <= 0) {
                    currDeadlineTask = (DeadlineTask) addDeadlineTask(addParameters);
                    currDeadlineTask.setTag(recurTag);
                    includeInRecurMap(currDeadlineTask, recurTag);

                    currDeadline = updateDate(currDeadline,
                            addParameters.getTimeType(),
                            addParameters.getFreqOfTimeType());

                    addParameters.setEndDate(currDeadline);

                }
                return _recurringTasks.get(recurTag).get(0);

            case EVENT :
                Date currStartDate = addParameters.getStartDate();
                Date currEndDate = addParameters.getEndDate();
                EventTask currEventTask;

                while (currStartDate.compareTo(endRecurOn) <= 0) {
                    currEventTask = (EventTask) addEventTask(addParameters);
                    currEventTask.setTag(recurTag);

                    includeInRecurMap(currEventTask, recurTag);

                    currStartDate = updateDate(currStartDate,
                            addParameters.getTimeType(),
                            addParameters.getFreqOfTimeType());
                    currEndDate = updateDate(currEndDate,
                            addParameters.getTimeType(),
                            addParameters.getFreqOfTimeType());

                    addParameters.setStartDate(currStartDate);
                    addParameters.setEndDate(currEndDate);

                }
                return _recurringTasks.get(recurTag).get(0);

            default :
                return null;
        }
    }

    private Date updateDate(Date currDate, String timeType, int freqOfTimeType) {
        Calendar updateCal = Calendar.getInstance();
        updateCal.setTime(currDate);

        switch (timeType) {
            case ("YEAR") :
                updateCal.add(Calendar.YEAR, freqOfTimeType);
                break;
            case ("MONTH") :
                updateCal.add(Calendar.MONTH, freqOfTimeType);
                break;
            case ("WEEK") :
                updateCal.add(Calendar.WEEK_OF_YEAR, freqOfTimeType);
                break;
            case ("DAY") :
                updateCal.add(Calendar.DAY_OF_MONTH, freqOfTimeType);
                break;
            case ("HOUR") :
                updateCal.add(Calendar.HOUR_OF_DAY, freqOfTimeType);
                break;
            default :
                return null;

        }

        return updateCal.getTime();
    }

    private Task<?> addRegTask(DataParameter addParameters) {
        switch (addParameters.getNewTaskType()) {
            case TODO :
                return addTodoTask(addParameters);

            case DEADLINE :
                return addDeadlineTask(addParameters);

            case EVENT :
                return addEventTask(addParameters);

            default :
                return null;
        }
    }

    private Date generateStartOfNextYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int currYear = calendar.get(Calendar.YEAR);
        calendar.set(currYear + 1, 0, 1);

        Date startOfNextYear = calendar.getTime();

        return startOfNextYear;
    }

    private Task<?> addTodoTask(DataParameter addParameters) {
        TodoTask newTodoTask = createTodoTask(addParameters);
        newTodoTask.setLastEditedTime(new Date());

        if (_uncompletedTodoTasks.add(newTodoTask)) {
            return newTodoTask;
        }
        return null;
    }

    private Task<?> addDeadlineTask(DataParameter addParameters) {
        DeadlineTask newDeadlineTask = createDeadlineTask(addParameters);
        newDeadlineTask.setLastEditedTime(new Date());

        if (_uncompletedDeadlineTasks.add(newDeadlineTask)) {

            return newDeadlineTask;
        }
        return null;
    }

    private Task<?> addEventTask(DataParameter addParameters) {
        EventTask newEventTask = createEventTask(addParameters);
        newEventTask.setLastEditedTime(new Date());

        if (_uncompletedEventTasks.add(newEventTask)) {

            return newEventTask;
        }
        return null;
    }

    private TodoTask createTodoTask(DataParameter addParameters) {
        return new TodoTask(addParameters.getDescription(),
                addParameters.getPriority());
    }

    private DeadlineTask createDeadlineTask(DataParameter addParameters) {
        return new DeadlineTask(addParameters.getDescription(),
                addParameters.getEndDate(), addParameters.getPriority());
    }

    private EventTask createEventTask(DataParameter addParameters) {
        return new EventTask(addParameters.getDescription(),
                addParameters.getStartDate(), addParameters.getEndDate(),
                addParameters.getPriority());
    }

    /**
     * Deletes a specific task by identifying the Task with its type and id
     * number. If changes are successfully saved by DAO, it returns a Task
     * object to the method which called it.
     * 
     * @param DataParameter deleteParameters
     * @return Task<?> (if successful), null otherwise
     */
    public Task<?> deleteTask(DataParameter deleteParameters) {
        if (deleteParameters.getTaskObject().getTag() != null && deleteParameters
                .getTaskObject().getTag().contains("RECUR")) {
            assert (deleteParameters.getTaskObject().getType()
                    .equals(TaskType.EVENT) || deleteParameters.getTaskObject()
                    .getType().equals(TaskType.DEADLINE));

            Task<?> taskToReturn = deleteRecurringTasks(deleteParameters);

            syncUncompletedTasks(TaskType.DEADLINE);
            syncUncompletedTasks(TaskType.EVENT);

            return taskToReturn;

        } else {
            Task<?> taskToReturn = deleteRegTask(deleteParameters);

            syncUncompletedTasks(TaskType.TODO);
            syncUncompletedTasks(TaskType.DEADLINE);
            syncUncompletedTasks(TaskType.EVENT);

            return taskToReturn;

        }
    }

    private Task<?> deleteRecurringTasks(DataParameter deleteParameters) {
        Task<?> recurTaskToDelete = deleteParameters.getTaskObject();

        if (_recurringTasks.containsKey(recurTaskToDelete.getTag())) {
            ArrayList<Task<?>> listOfRecTasks = _recurringTasks
                    .remove(recurTaskToDelete.getTag());

            if (deleteParameters.isModifyAll()) {
                while (!listOfRecTasks.isEmpty()) {
                    deleteParameters.setTaskObject(listOfRecTasks.remove(0));
                    deleteRegTask(deleteParameters);
                }
            } else {
                listOfRecTasks.remove(recurTaskToDelete);
                if (listOfRecTasks.size() > 0) {
                    _recurringTasks.put(recurTaskToDelete.getTag(),
                            listOfRecTasks);

                }
                deleteRegTask(deleteParameters);
            }

            syncHashMaps();

            return recurTaskToDelete;
        } else {
            return null;
        }
    }

    private Task<?> deleteRegTask(DataParameter deleteParameters) {
        switch (deleteParameters.getTaskObject().getType()) {
            case TODO :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedTodoTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                } else {
                    if (_completedTodoTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                }
            case DEADLINE :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedDeadlineTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();

                    } else {
                        return null;
                    }
                } else {
                    if (_completedDeadlineTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();

                    } else {
                        return null;

                    }
                }
            case EVENT :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedEventTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();

                    } else {
                        return null;

                    }
                } else {
                    if (_completedEventTasks.remove(deleteParameters
                            .getTaskObject())) {

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                }
            default :
                System.out.println("Unable to determine Task Type.");
                return null;
        }
    }

    /**
     * Checks what Task the user wants to modify, calls the command of DAO to
     * make the amendments, then returns the modified task.
     * 
     * @param DataParameter modifyParameters
     * @return task modified
     */
    public Task<?> modifyTask(DataParameter modifyParameters) {
        if (modifyParameters.getTaskObject().getTag() != null && modifyParameters
                .getTaskObject().getTag().contains("RECUR")) {
            return modifyRecurringTask(modifyParameters);

        } else {
            return modifyRegTask(modifyParameters);

        }
    }

    private Task<?> modifyRecurringTask(DataParameter modifyParameters) {
        if (modifyParameters.getTaskObject().getType() == TaskType.DEADLINE || modifyParameters
                .getTaskObject().getType() == TaskType.EVENT) {

            if (modifyParameters.isModifyAll()) {
                return modifyAllRecurringTasks(modifyParameters);

            } else {
                return modfiyOneRecurringTask(modifyParameters);
            }

        } else {
            return null;
        }
    }

    private Task<?> modifyAllRecurringTasks(DataParameter modifyParameters) {
        // Note: Task does not contain information on frequency
        Task<?> prevTask = modifyParameters.getTaskObject();
        Task<?> returnTask = null;

        if (prevTask != null) {
            if (modifyParameters.getStartDate() != null || modifyParameters
                    .getEndDate() != null ||
                    modifyParameters.getTimeType() != null ||
                    modifyParameters.getFreqOfTimeType() != 0) {
                // modify frequency of task, on top of modify description,
                // priority ,task type
                assert (modifyParameters.getNewTaskType() != TaskType.TODO);

                returnTask = rearrangeRecurringTasks(modifyParameters, prevTask);

            } else {
                // only modify description, priority ,task type

                Task<?> currTask = prevTask;
                ArrayList<Task<?>> newRecurTaskList = new ArrayList<Task<?>>();
                ArrayList<Task<?>> prevRecurTaskList = _recurringTasks
                        .get(prevTask.getTag());

                for (int i = 0; i < prevRecurTaskList.size(); i++) {
                    currTask = prevRecurTaskList.get(i);

                    DataParameter newSetOfParameters = createNewParameters(
                            modifyParameters, currTask);

                    newSetOfParameters.setTaskObject(currTask);
                    Task<?> modifiedTask = modifyRegTask(newSetOfParameters);
                    modifiedTask.setTag(prevTask.getTag());

                    newRecurTaskList.add(modifiedTask);

                }

                _recurringTasks.put(prevTask.getTag(), newRecurTaskList);

                returnTask = _recurringTasks.get(prevTask.getTag()).get(0);

            }
            return returnTask;
        }

        return null;
    }

    private Task<?> rearrangeRecurringTasks(DataParameter modifyParameters,
            Task<?> prevTask) {
        DataParameter newParameters = createNewParameters(modifyParameters,
                prevTask);

        if (prevTask.getType() == TaskType.DEADLINE && newParameters
                .getNewTaskType() == TaskType.EVENT) {
            DeadlineTask prevDeadlineTask = (DeadlineTask) prevTask;

            if (newParameters.getStartDate() == null) {
                newParameters
                        .setStartDate(newParameters.getEndDate().compareTo(
                                prevDeadlineTask.getEndTime()) < 0 ? newParameters
                                .getEndDate() : prevDeadlineTask.getEndTime());
                newParameters.setEndDate(newParameters.getEndDate().compareTo(
                        prevDeadlineTask.getEndTime()) > 0 ? newParameters
                        .getEndDate() : prevDeadlineTask.getEndTime());

            } else if (newParameters.getEndDate() == null) {
                newParameters.setEndDate(newParameters.getEndDate().compareTo(
                        prevDeadlineTask.getEndTime()) > 0 ? newParameters
                        .getEndDate() : prevDeadlineTask.getEndTime());
                newParameters
                        .setStartDate(newParameters.getEndDate().compareTo(
                                prevDeadlineTask.getEndTime()) < 0 ? newParameters
                                .getEndDate() : prevDeadlineTask.getEndTime());

            } else {
                return null;

            }

            if (_recurringTasks.get(prevTask.getTag()).indexOf(prevTask) != 0) {
                int i = _recurringTasks.get(prevTask.getTag())
                        .indexOf(prevTask);

                Calendar currStartDate = Calendar.getInstance();
                Calendar currEndDate = Calendar.getInstance();

                currStartDate.setTime(newParameters.getStartDate());
                currEndDate.setTime(newParameters.getEndDate());

                while (i-- >= 0) {
                    newParameters.setStartDate(updateDate(
                            newParameters.getStartDate(),
                            newParameters.getTimeType(),
                            -1 * newParameters.getFreqOfTimeType()));
                    newParameters.setEndDate(updateDate(
                            newParameters.getEndDate(),
                            newParameters.getTimeType(),
                            -1 * newParameters.getFreqOfTimeType()));

                }

            }
        }

        newParameters.setTaskObject(prevTask);

        deleteRecurringTasks(newParameters);

        return addRecurringTask(newParameters, prevTask.getTag());

    }

    private Task<?> modfiyOneRecurringTask(DataParameter modifyParameters) {
        // Note: Task does not contain information on frequency

        Task<?> prevTask = deleteRegTask(modifyParameters);

        if (prevTask != null) {
            _recurringTasks.get(prevTask.getTag()).remove(prevTask);

            // the moment one of the recurring task is modified, it does not
            // belong to the group of recurring tasks anymore

            DataParameter newSetOfParameters = createNewParameters(
                    modifyParameters, prevTask);

            Task<?> newTask = addTask(newSetOfParameters);
            newTask.setTag("");
            newTask.setLastEditedTime(new Date());

            return newTask;
        } else {
            return null;

        }
    }

    private Task<?> modifyRegTask(DataParameter modifyParameters) {
        if (modifyParameters.getTaskObject() == null || modifyParameters
                .getNewTaskType() == TaskType.UNKNOWN) {
            return null;
        } else {

            Task<?> prevTask = deleteRegTask(modifyParameters);

            if (prevTask == null) {
                return null;
            }

            DataParameter newSetOfParameters = createNewParameters(
                    modifyParameters, prevTask);

            Task<?> newTask = addRegTask(newSetOfParameters);

            return newTask;
        }
    }

    private DataParameter createNewParameters(DataParameter modifyParameters,
            Task<?> prevTask) {
        DataParameter newSetOfParameters = new DataParameter();
        newSetOfParameters.loadOldTask(prevTask);
        newSetOfParameters.loadNewParameters(modifyParameters);

        return newSetOfParameters;
    }

    /**
     * Marks a given task as completed by setting its completed tag to true
     * 
     * @param completeParameters
     * @return completedTask
     */
    public Task<?> markCompleted(DataParameter completeParameters) {
        if (completeParameters.getTaskObject() != null) {
            if (completeParameters.getTaskObject().getTag().contains("RECUR")) {
                Task<?> taskToReturn = markCompletedRecurTask(completeParameters);

                syncCompletedTasks(TaskType.DEADLINE);
                syncUncompletedTasks(TaskType.DEADLINE);
                syncCompletedTasks(TaskType.EVENT);
                syncUncompletedTasks(TaskType.EVENT);

                syncHashMaps();

                return taskToReturn;

            } else if (completeParameters.getTaskObject().getTag().equals("")) {
                Task<?> taskToReturn = markCompletedRegTask(completeParameters);

                syncCompletedTasks(TaskType.TODO);
                syncUncompletedTasks(TaskType.TODO);
                syncCompletedTasks(TaskType.DEADLINE);
                syncUncompletedTasks(TaskType.DEADLINE);
                syncCompletedTasks(TaskType.EVENT);
                syncUncompletedTasks(TaskType.EVENT);

                return taskToReturn;

            } else {
                return null;

            }
        }

        return null;

    }

    private Task<?> markCompletedRecurTask(DataParameter completeParameters) {
        Task<?> prevTask = completeParameters.getTaskObject();

        if (completeParameters.isModifyAll()) {
            return markAllRecurComplete(prevTask);

        } else {
            return markOneRecurComplete(prevTask);

        }

    }

    private Task<?> markAllRecurComplete(Task<?> prevTask) {
        int listSize = _recurringTasks.get(prevTask.getTag()).size();

        Task<?> currTask = null;
        Task<?> returnTask = null;

        if (prevTask.getType().equals(TaskType.DEADLINE)) {
            for (int i = 0; i < listSize; i++) {
                currTask = _recurringTasks.get(prevTask.getTag()).get(i);

                _uncompletedDeadlineTasks.remove(currTask);

                currTask.setCompleted(true);
                currTask.setLastEditedTime(new Date());

                if (i == 0) {
                    returnTask = currTask;

                }
                _completedDeadlineTasks.add((DeadlineTask) currTask);
            }
            _recurringTasks.remove(prevTask.getTag());

            return returnTask;

        } else if (prevTask.getType().equals(TaskType.EVENT)) {
            for (int i = 0; i < listSize; i++) {
                ;
                currTask = _recurringTasks.get(prevTask.getTag()).get(i);

                _uncompletedEventTasks.remove(currTask);

                currTask.setCompleted(true);
                currTask.setLastEditedTime(new Date());

                if (i == 0) {
                    returnTask = currTask;

                }

                _completedEventTasks.add((EventTask) currTask);
            }
            _recurringTasks.remove(prevTask.getTag());

            return returnTask;

        } else {
            return null;

        }
    }

    private Task<?> markOneRecurComplete(Task<?> prevTask) {
        Task<?> completedTask = prevTask;

        completedTask.setCompleted(true);
        completedTask.setLastEditedTime(new Date());

        if (prevTask.getType().equals(TaskType.DEADLINE)) {
            _completedDeadlineTasks.add((DeadlineTask) completedTask);
            _recurringTasks.get(prevTask.getTag()).remove(prevTask);
            _uncompletedDeadlineTasks.remove(prevTask);
            return completedTask;
        } else if (prevTask.getType().equals(TaskType.EVENT)) {
            _completedEventTasks.add((EventTask) completedTask);
            _recurringTasks.get(prevTask.getTag()).remove(prevTask);
            _uncompletedEventTasks.remove(prevTask);

            return completedTask;
        } else {
            return null;
        }
    }

    private Task<?> markCompletedRegTask(DataParameter completeParameters) {
        switch (completeParameters.getTaskObject().getType()) {
            case TODO :
                if (_uncompletedTodoTasks.remove(completeParameters
                        .getTaskObject())) {
                    TodoTask finTodoTask = (TodoTask) completeParameters
                            .getTaskObject();

                    finTodoTask.setCompleted(true);
                    finTodoTask.setLastEditedTime(new Date());

                    _completedTodoTasks.add(finTodoTask);

                    return finTodoTask;
                } else {
                    return null;
                }
            case DEADLINE :
                if (_uncompletedDeadlineTasks.remove(completeParameters
                        .getTaskObject())) {
                    DeadlineTask finDeadlineTask = (DeadlineTask) completeParameters
                            .getTaskObject();

                    finDeadlineTask.setCompleted(true);
                    finDeadlineTask.setLastEditedTime(new Date());

                    _completedDeadlineTasks.add(finDeadlineTask);

                    return finDeadlineTask;
                } else {
                    return null;
                }
            case EVENT :
                if (_uncompletedEventTasks.remove(completeParameters
                        .getTaskObject())) {
                    EventTask finEventTask = (EventTask) completeParameters
                            .getTaskObject();

                    finEventTask.setCompleted(true);
                    finEventTask.setLastEditedTime(new Date());

                    _completedEventTasks.add(finEventTask);

                    return finEventTask;
                } else {
                    return null;
                }
            default :
                System.out.println("Unable to determine Task Type.");
                return null;
        }
    }

    /**
     * Takes in all 6 of the Task TreeSets and overwrites them in TDM.
     * <p>
     * Used when doing undo.
     * 
     * @param newUncompletedTodoTasks
     * @param newUncompletedDeadlineTasks
     * @param newUncompletedEventTasks
     * @param newCompletedTodoTasks
     * @param newCompletedDeadlineTasks
     * @param newCompletedEventTasks
     */

    public void setAllTreeSets(TreeSet<TodoTask> newUncompletedTodoTasks,
            TreeSet<DeadlineTask> newUncompletedDeadlineTasks,
            TreeSet<EventTask> newUncompletedEventTasks,
            TreeSet<TodoTask> newCompletedTodoTasks,
            TreeSet<DeadlineTask> newCompletedDeadlineTasks,
            TreeSet<EventTask> newCompletedEventTasks) {

        _uncompletedTodoTasks = newUncompletedTodoTasks;
        _uncompletedDeadlineTasks = newUncompletedDeadlineTasks;
        _uncompletedEventTasks = newUncompletedEventTasks;
        _completedTodoTasks = newCompletedTodoTasks;
        _completedDeadlineTasks = newCompletedDeadlineTasks;
        _completedEventTasks = newCompletedEventTasks;

        syncCompletedTasks(TaskType.TODO);
        syncUncompletedTasks(TaskType.TODO);

        syncCompletedTasks(TaskType.DEADLINE);
        syncUncompletedTasks(TaskType.DEADLINE);

        syncCompletedTasks(TaskType.EVENT);
        syncUncompletedTasks(TaskType.EVENT);

    }

    /**
     * Takes in the Recurring HashMaps and overwrites it in TDM.
     * <p>
     * Used when doing undo.
     * 
     * @param newRecurringTasks
     * @param newMaxRecurTagInt
     */
    public void setAllHashMaps(
            HashMap<String, ArrayList<Task<?>>> newRecurringTasks,
            int newMaxRecurTagInt) {
        _recurringTasks = newRecurringTasks;
        _maxRecurTagInt = newMaxRecurTagInt;
        syncHashMaps();

    }

    /* Sync Methods */
    private void syncUncompletedTasks(TaskType taskType) {
        for (MemoryDataObserver observer : _observers) {
            switch (taskType) {
                case TODO :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _uncompletedTodoTasks, taskType, false));
                    break;
                case DEADLINE :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _uncompletedDeadlineTasks, taskType, false));
                    break;
                case EVENT :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _uncompletedEventTasks, taskType, false));
                    break;
                default :
                    System.out.println("Unable to determine task type.");
                    return;
            }
        }
    }

    private void syncCompletedTasks(TaskType taskType) {
        for (MemoryDataObserver observer : _observers) {
            switch (taskType) {
                case TODO :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _completedTodoTasks, taskType, true));
                    break;
                case DEADLINE :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _completedDeadlineTasks, taskType, true));
                    break;
                case EVENT :
                    observer.updateTaskSet(new TaskSetDataParameter(
                            _completedEventTasks, taskType, true));
                    break;
                default :
                    System.out.println("Unable to determine task type.");
                    return;
            }
        }
    }

    private void syncHashMaps() {
        for (MemoryDataObserver observer : _observers) {
            observer.updateTaskMap(new TaskMapDataParameter(_recurringTasks,
                    _maxRecurTagInt));
        }
    }

    /**
     * Saves all tasks into storage by calling all the sync methods
     */
    public void saveAllTasks() {
        allDataList.add(new TaskSetDataParameter(_completedEventTasks,
                TaskType.EVENT, true));
        allDataList.add(new TaskSetDataParameter(_uncompletedEventTasks,
                TaskType.EVENT, false));
        allDataList.add(new TaskSetDataParameter(_completedDeadlineTasks,
                TaskType.DEADLINE, true));
        allDataList.add(new TaskSetDataParameter(_uncompletedDeadlineTasks,
                TaskType.DEADLINE, false));
        allDataList.add(new TaskSetDataParameter(_completedTodoTasks,
                TaskType.TODO, true));
        allDataList.add(new TaskSetDataParameter(_uncompletedTodoTasks,
                TaskType.TODO, false));

        _syncManager.saveAll(allDataList);

        syncHashMaps();

    }

    /**
     * Clears all the trees in memory. Does not sync with actual data storage.
     */
    public void resetTrees() {
        _completedTodoTasks.clear();
        _completedDeadlineTasks.clear();
        _completedEventTasks.clear();

        _uncompletedTodoTasks.clear();
        _uncompletedDeadlineTasks.clear();
        _uncompletedEventTasks.clear();

    }

    public void updateTrees(SortedSet<TodoTask> uncompletedTodoTasks,
            SortedSet<DeadlineTask> uncompletedDeadlineTasks,
            SortedSet<EventTask> uncompletedEventTasks,
            SortedSet<TodoTask> completedTodoTasks,
            SortedSet<DeadlineTask> completedDeadlineTasks,
            SortedSet<EventTask> completedEventTasks) {
        _completedTodoTasks = completedTodoTasks;
        _completedDeadlineTasks = completedDeadlineTasks;
        _completedEventTasks = completedEventTasks;

        _uncompletedTodoTasks = uncompletedTodoTasks;
        _uncompletedDeadlineTasks = uncompletedDeadlineTasks;
        _uncompletedEventTasks = uncompletedEventTasks;
        syncHashMaps();
    }
}
