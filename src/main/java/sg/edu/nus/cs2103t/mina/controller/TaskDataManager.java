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

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Checks user's input determines the type of tasks breaks up parameters for the
 * tasks passes tasks to DAO to retrieve data from files.
 */
//@author A0080412W
public class TaskDataManager {

    private static final String CLASS_NAME = TaskDataManager.class.getName();

    // error messages
    public static final int ERROR_MISSING_TASK_DESCRIPTION = -2;

    // parameters of String after trimming
    public static final int PARAM_TASK_DESCRIPTION = 0;

    // Sets for completed and uncompleted tasks
    private SortedSet<TodoTask> _uncompletedTodoTasks;
    private SortedSet<TodoTask> _completedTodoTasks;
    private SortedSet<DeadlineTask> _uncompletedDeadlineTasks;
    private SortedSet<DeadlineTask> _completedDeadlineTasks;
    private SortedSet<EventTask> _uncompletedEventTasks;
    private SortedSet<EventTask> _completedEventTasks;

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

    /**
     * Creates a new TaskDataManager, loads 6 TreeSets containing completed and
     * uncompleted TodoTasks, Deadline Tasks and EventTasks, 1 HashMap
     * containing all recurring tasks. These maps are then added to dataObserver
     * in syncManager.
     * 
     * @param syncManager
     */
    @SuppressWarnings("unchecked")
    public TaskDataManager(DataSyncManager syncManager) {
        SortedSet<? extends Task<?>> tempTasks = null;

        _syncManager = syncManager;

        _observers = new ArrayList<MemoryDataObserver>();
        _observers.add(syncManager.getDataObserver());

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.TODO, false);
            _uncompletedTodoTasks = (SortedSet<TodoTask>) tempTasks;
        } catch (IOException e) {
            _uncompletedTodoTasks = new TreeSet<TodoTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.DEADLINE, false);
            _uncompletedDeadlineTasks = (SortedSet<DeadlineTask>) tempTasks;
        } catch (IOException e) {
            _uncompletedDeadlineTasks = new TreeSet<DeadlineTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.EVENT, false);
            _uncompletedEventTasks = (SortedSet<EventTask>) tempTasks;
        } catch (IOException e) {
            _uncompletedEventTasks = new TreeSet<EventTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.TODO, true);
            _completedTodoTasks = (SortedSet<TodoTask>) tempTasks;
        } catch (IOException e) {
            _completedTodoTasks = new TreeSet<TodoTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.DEADLINE, true);
            _completedDeadlineTasks = (SortedSet<DeadlineTask>) tempTasks;
        } catch (IOException e) {
            _completedDeadlineTasks = new TreeSet<DeadlineTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }

        try {
            tempTasks = _syncManager.loadTaskSet(TaskType.EVENT, true);
            _completedEventTasks = (SortedSet<EventTask>) tempTasks;
        } catch (IOException e) {
            _completedEventTasks = new TreeSet<EventTask>();
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
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
        updateFromDeadlineTasksSet();
        updateFromEventTasksSet();
    }

    private void updateFromDeadlineTasksSet() {
        Iterator<DeadlineTask> deadlineTaskIterator = _uncompletedDeadlineTasks
                .iterator();
        DeadlineTask currDeadlineTask;
        while (deadlineTaskIterator.hasNext()) {
            currDeadlineTask = deadlineTaskIterator.next();
            checkRecur(currDeadlineTask);
        }
    }

    private void updateFromEventTasksSet() {
        Iterator<EventTask> eventTaskIterator = _uncompletedEventTasks
                .iterator();
        EventTask currEventTask;
        while (eventTaskIterator.hasNext()) {
            currEventTask = eventTaskIterator.next();
            checkRecur(currEventTask);
        }
    }

    private void checkRecur(Task<?> currTask) {
        if (currTask.getTag().contains("RECUR")) {
            includeInRecurMap(currTask, currTask.getTag());
        }
    }

    private void includeInRecurMap(Task<?> taskToInclude, String recurTag) {
        if (!isValidRecurTag(recurTag)) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "Invalid RECUR tag: " + recurTag);
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
        if (!tag.contains("_")) {
            return false;
        } else {
            String[] tagTokens = tag.split("_", 2);

            try {
                int recurTagInt = Integer.parseInt(tagTokens[TAG_INT_POS]);
                _maxRecurTagInt = recurTagInt > _maxRecurTagInt ? recurTagInt
                        : _maxRecurTagInt;

                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

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
        } else if (isForRecurring(addParameters.getTag())) {
            assert isValidRecurType(addParameters.getNewTaskType());

            String recurTag = "RECUR_" + _maxRecurTagInt++;
            assert (!_recurringTasks.containsKey(recurTag));

            Task<?> taskToRetrun = addRecurringTask(addParameters, recurTag);

            syncAllUncompletedTasks();
            syncHashMaps();

            return taskToRetrun;
        } else {
            Task<?> taskToRetrun = addRegularTask(addParameters);

            syncAllUncompletedTasks();

            return taskToRetrun;
        }
    }

    private Task<?> directAddTask(Task<?> taskObject) {
        switch (taskObject.getType()) {
            case TODO :
                if (_uncompletedTodoTasks.add((TodoTask) taskObject)) {
                    syncAllUncompletedTasks();

                    return taskObject;
                }
                return null;

            case DEADLINE :
                if (_uncompletedDeadlineTasks.add((DeadlineTask) taskObject)) {
                    syncAllUncompletedTasks();

                    return taskObject;
                }
                return null;

            case EVENT :
                if (_uncompletedEventTasks.add((EventTask) taskObject)) {
                    syncAllUncompletedTasks();

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
                addRecurringDeadline(addParameters, recurTag, endRecurOn);
                return _recurringTasks.get(recurTag).get(0);

            case EVENT :
                addRecurringEvent(addParameters, recurTag, endRecurOn);
                return _recurringTasks.get(recurTag).get(0);

            default :
                return null;
        }
    }

    private void addRecurringDeadline(DataParameter addParameters,
            String recurTag, Date endRecurOn) {
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
    }

    private void addRecurringEvent(DataParameter addParameters,
            String recurTag, Date endRecurOn) {
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
            currEndDate = updateDate(currEndDate, addParameters.getTimeType(),
                    addParameters.getFreqOfTimeType());

            addParameters.setStartDate(currStartDate);
            addParameters.setEndDate(currEndDate);
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

    private Task<?> addRegularTask(DataParameter addParameters) {
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
        TodoTask newTodoTask = new TodoTask(addParameters.getDescription(),
                addParameters.getPriority());
        newTodoTask.setLastEditedTime(new Date());

        if (_uncompletedTodoTasks.add(newTodoTask)) {
            return newTodoTask;
        } else {
            return null;
        }
    }

    private Task<?> addDeadlineTask(DataParameter addParameters) {
        DeadlineTask newDeadlineTask = new DeadlineTask(
                addParameters.getDescription(), addParameters.getEndDate(),
                addParameters.getPriority());
        newDeadlineTask.setLastEditedTime(new Date());

        if (_uncompletedDeadlineTasks.add(newDeadlineTask)) {
            return newDeadlineTask;
        } else {
            return null;
        }
    }

    private Task<?> addEventTask(DataParameter addParameters) {
        EventTask newEventTask = new EventTask(addParameters.getDescription(),
                addParameters.getStartDate(), addParameters.getEndDate(),
                addParameters.getPriority());
        newEventTask.setLastEditedTime(new Date());

        if (_uncompletedEventTasks.add(newEventTask)) {
            return newEventTask;
        } else {
            return null;
        }
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
        if (isForRecurring(deleteParameters.getTaskObject().getTag())) {
            assert (isValidRecurType(deleteParameters.getTaskObject().getType()));

            Task<?> taskToReturn = deleteRecurringTasks(deleteParameters);

            syncAllUncompletedTasks();
            syncHashMaps();

            return taskToReturn;
        } else {
            Task<?> taskToReturn = deleteRegTask(deleteParameters);

            syncAllUncompletedTasks();

            return taskToReturn;
        }
    }

    private Task<?> deleteRecurringTasks(DataParameter deleteParameters) {
        Task<?> recurTaskToDelete = deleteParameters.getTaskObject();

        if (_recurringTasks.containsKey(recurTaskToDelete.getTag())) {
            @SuppressWarnings("unchecked")
            ArrayList<Task<?>> listOfRecTasks = (ArrayList<Task<?>>) _recurringTasks
                    .get(recurTaskToDelete.getTag()).clone();
            int sizeOfList = listOfRecTasks.size();

            if (deleteParameters.isModifyAll()) {
                Task<?> currTask;

                for (int i = 0; i < sizeOfList; i++) {
                    currTask = _recurringTasks.get(recurTaskToDelete.getTag())
                            .get(i);
                    if (currTask.isCompleted() == recurTaskToDelete
                            .isCompleted()) {
                        listOfRecTasks.remove(currTask);
                        deleteParameters.setTaskObject(currTask);

                        deleteRegTask(deleteParameters);
                    } // else, leave the task there
                }
                if (listOfRecTasks.size() > 0) {
                    _recurringTasks.put(recurTaskToDelete.getTag(),
                            listOfRecTasks);
                } else {
                    _recurringTasks.remove(recurTaskToDelete.getTag());
                }
            } else {
                listOfRecTasks.remove(recurTaskToDelete);

                if (listOfRecTasks.size() > 0) {
                    _recurringTasks.put(recurTaskToDelete.getTag(),
                            listOfRecTasks);
                }
                deleteRegTask(deleteParameters);
            }
            return recurTaskToDelete;
        } else {
            return null;
        }
    }

    private Task<?> deleteRegTask(DataParameter deleteParameters) {
        switch (deleteParameters.getTaskObject().getType()) {
            case TODO :
                return deleteTodoTask(deleteParameters);

            case DEADLINE :
                return deleteDeadlineTask(deleteParameters);

            case EVENT :
                return deleteEventTask(deleteParameters);

            default :
                LogHelper.log(CLASS_NAME, Level.ERROR,
                        "Unable to determine Task Type.");
                return null;
        }
    }

    private Task<?> deleteTodoTask(DataParameter deleteParameters) {
        if (!deleteParameters.getTaskObject().isCompleted()) {
            if (_uncompletedTodoTasks.remove(deleteParameters.getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        } else {
            if (_completedTodoTasks.remove(deleteParameters.getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        }

        return null;
    }

    private Task<?> deleteDeadlineTask(DataParameter deleteParameters) {
        if (!deleteParameters.getTaskObject().isCompleted()) {
            if (_uncompletedDeadlineTasks.remove(deleteParameters
                    .getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        } else {
            if (_completedDeadlineTasks
                    .remove(deleteParameters.getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        }

        return null;
    }

    private Task<?> deleteEventTask(DataParameter deleteParameters) {
        if (!deleteParameters.getTaskObject().isCompleted()) {
            if (_uncompletedEventTasks.remove(deleteParameters.getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        } else {
            if (_completedEventTasks.remove(deleteParameters.getTaskObject())) {
                return deleteParameters.getTaskObject();
            }
        }

        return null;
    }

    /**
     * Checks what Task the user wants to modify, calls the command of DAO to
     * make the amendments, then returns the modified task.
     * 
     * @param DataParameter modifyParameters
     * @return task modified
     */
    public Task<?> modifyTask(DataParameter modifyParameters) {
        if (isForRecurring(modifyParameters.getTaskObject().getTag())) {
            assert (isValidRecurType(modifyParameters.getTaskObject().getType()));

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
        Task<?> prevTask = modifyParameters.getTaskObject();
        Task<?> returnTask = null;

        if (prevTask != null) {
            if (isModifyTimeParameters(modifyParameters)) {
                LogHelper.log(CLASS_NAME, Level.ERROR,
                        "Unable to modify frequency of recurring task.");

                returnTask = null;
            } else {
                // only modify description, priority ,task type
                Task<?> currTask = prevTask;
                ArrayList<Task<?>> newRecurTaskList = new ArrayList<Task<?>>();
                ArrayList<Task<?>> prevRecurTaskList = _recurringTasks
                        .get(prevTask.getTag());

                for (int i = 0; i < prevRecurTaskList.size(); i++) {
                    currTask = prevRecurTaskList.get(i);

                    Task<?> modifiedTask = modifyCurrTask(modifyParameters,
                            prevTask, currTask);

                    newRecurTaskList.add(modifiedTask);
                }
                _recurringTasks.put(prevTask.getTag(), newRecurTaskList);

                returnTask = _recurringTasks.get(prevTask.getTag()).get(0);
            }
            return returnTask;
        } else {
            return null;
        }
    }

    private Task<?> modifyCurrTask(DataParameter modifyParameters,
            Task<?> prevTask, Task<?> currTask) {
        DataParameter newSetOfParameters = createNewParameters(
                modifyParameters, currTask);

        newSetOfParameters.setTaskObject(currTask);
        Task<?> modifiedTask = modifyRegTask(newSetOfParameters);
        modifiedTask.setTag(prevTask.getTag());

        return modifiedTask;
    }

    private boolean isModifyTimeParameters(DataParameter modifyParameters) {
        return modifyParameters.getStartDate() != null || modifyParameters
                .getEndDate() != null ||
                modifyParameters.getTimeType() != null ||
                modifyParameters.getFreqOfTimeType() != 0;
    }

    private Task<?> modfiyOneRecurringTask(DataParameter modifyParameters) {
        Task<?> prevTask = deleteRegTask(modifyParameters);

        if (prevTask != null) {
            _recurringTasks.get(prevTask.getTag()).remove(prevTask);
            // the moment one of the recurring task is modified, it does not
            // belong to the group of recurring tasks anymore

            DataParameter newSetOfParameters = createNewParameters(
                    modifyParameters, prevTask);

            Task<?> newTask = addRegularTask(newSetOfParameters);
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

            Task<?> newTask = addRegularTask(newSetOfParameters);

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

                syncAllUncompletedTasks();
                syncAllCompletedTasks();
                syncHashMaps();

                return taskToReturn;
            } else if (completeParameters.getTaskObject().getTag().equals("")) {
                Task<?> taskToReturn = markCompletedRegTask(completeParameters);

                syncAllUncompletedTasks();
                syncAllCompletedTasks();

                return taskToReturn;
            } else {
                return null;
            }
        } else {
            return null;
        }
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

        if (prevTask.getType().equals(TaskType.DEADLINE)) {
            return markAllRecurDeadlineComplete(prevTask, listSize);
        } else if (prevTask.getType().equals(TaskType.EVENT)) {
            return markAllRecurEventComplete(prevTask, listSize);
        } else {
            return null;
        }
    }

    private Task<?> markAllRecurDeadlineComplete(Task<?> prevTask, int listSize) {
        Task<?> currTask;
        Task<?> returnTask;

        for (int i = 0; i < listSize; i++) {
            currTask = _recurringTasks.get(prevTask.getTag()).get(i);
            _uncompletedDeadlineTasks.remove(currTask);

            currTask.setCompleted(true);
            currTask.setLastEditedTime(new Date());

            _completedDeadlineTasks.add((DeadlineTask) currTask);
        }
        returnTask = _recurringTasks.get(prevTask.getTag()).get(0);

        return returnTask;
    }

    private Task<?> markAllRecurEventComplete(Task<?> prevTask, int listSize) {
        Task<?> currTask;
        Task<?> returnTask;

        for (int i = 0; i < listSize; i++) {
            currTask = _recurringTasks.get(prevTask.getTag()).get(i);

            _uncompletedEventTasks.remove(currTask);

            currTask.setCompleted(true);
            currTask.setLastEditedTime(new Date());

            _completedEventTasks.add((EventTask) currTask);
        }
        returnTask = _recurringTasks.get(prevTask.getTag()).get(0);

        return returnTask;
    }

    private Task<?> markOneRecurComplete(Task<?> prevTask) {
        DataParameter completeParameters = new DataParameter();
        completeParameters.setTaskObject(prevTask);

        if (prevTask.getType().equals(TaskType.DEADLINE)) {
            Task<?> completedTask = completeRegDeadlineTask(completeParameters);

            int prevIndex = _recurringTasks.get(prevTask.getTag()).indexOf(
                    prevTask);
            _recurringTasks.get(prevTask.getTag())
                    .set(prevIndex, completedTask);

            return completedTask;
        } else if (prevTask.getType().equals(TaskType.EVENT)) {
            Task<?> completedTask = completeRegEventTask(completeParameters);

            int prevIndex = _recurringTasks.get(prevTask.getTag()).indexOf(
                    prevTask);
            _recurringTasks.get(prevTask.getTag())
                    .set(prevIndex, completedTask);

            return completedTask;
        } else {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "Tasktype is invalid for a recurring task.");
            return null;
        }
    }

    private Task<?> markCompletedRegTask(DataParameter completeParameters) {
        switch (completeParameters.getTaskObject().getType()) {
            case TODO :
                return completeRegTodoTask(completeParameters);

            case DEADLINE :
                return completeRegDeadlineTask(completeParameters);

            case EVENT :
                return completeRegEventTask(completeParameters);

            default :
                LogHelper.log(CLASS_NAME, Level.ERROR,
                        "Unable to determine Task Type.");
                return null;
        }
    }

    private Task<?> completeRegTodoTask(DataParameter completeParameters) {
        if (_uncompletedTodoTasks.remove(completeParameters.getTaskObject())) {
            TodoTask completedTodoTask = (TodoTask) completeParameters
                    .getTaskObject();

            completedTodoTask.setCompleted(true);
            completedTodoTask.setLastEditedTime(new Date());

            _completedTodoTasks.add(completedTodoTask);

            return completedTodoTask;
        } else {
            return null;
        }
    }

    private Task<?> completeRegDeadlineTask(DataParameter completeParameters) {
        if (_uncompletedDeadlineTasks
                .remove(completeParameters.getTaskObject())) {
            DeadlineTask completedDeadlineTask = (DeadlineTask) completeParameters
                    .getTaskObject();

            completedDeadlineTask.setCompleted(true);
            completedDeadlineTask.setLastEditedTime(new Date());

            _completedDeadlineTasks.add(completedDeadlineTask);

            return completedDeadlineTask;
        } else {
            return null;
        }
    }

    private Task<?> completeRegEventTask(DataParameter completeParameters) {
        if (_uncompletedEventTasks.remove(completeParameters.getTaskObject())) {
            EventTask completedEventTask = (EventTask) completeParameters
                    .getTaskObject();

            completedEventTask.setCompleted(true);
            completedEventTask.setLastEditedTime(new Date());

            _completedEventTasks.add(completedEventTask);

            return completedEventTask;
        } else {
            return null;
        }
    }

    private boolean isForRecurring(String tag) {
        return tag != null && tag.contains("RECUR");
    }

    private boolean isValidRecurType(TaskType taskType) {
        return taskType.equals(TaskType.EVENT) || taskType
                .equals(TaskType.DEADLINE);
    }

    /* Sync Methods */
    private void syncAllUncompletedTasks() {
        for (MemoryDataObserver observer : _observers) {
            observer.updateTaskSet(new TaskSetDataParameter(
                    _uncompletedTodoTasks, TaskType.TODO, false));
            observer.updateTaskSet(new TaskSetDataParameter(
                    _uncompletedDeadlineTasks, TaskType.DEADLINE, false));
            observer.updateTaskSet(new TaskSetDataParameter(
                    _uncompletedEventTasks, TaskType.EVENT, false));
        }
    }

    private void syncAllCompletedTasks() {
        for (MemoryDataObserver observer : _observers) {
            observer.updateTaskSet(new TaskSetDataParameter(
                    _completedTodoTasks, TaskType.TODO, true));
            observer.updateTaskSet(new TaskSetDataParameter(
                    _completedDeadlineTasks, TaskType.DEADLINE, true));
            observer.updateTaskSet(new TaskSetDataParameter(
                    _completedEventTasks, TaskType.EVENT, true));
        }
    }

    private void syncHashMaps() {
        for (MemoryDataObserver observer : _observers) {
            observer.updateTaskMap(new TaskMapDataParameter(_recurringTasks,
                    _maxRecurTagInt));
        }
    }

    /**
     * Replaces all 6 TreeSets with the respective TreeSets given. The map
     * containing recurring tasks is then updated accordingly.
     * <p>
     * Used when doing undo.
     * 
     * @param uncompletedTodoTasks
     * @param uncompletedDeadlineTasks
     * @param uncompletedEventTasks
     * @param completedTodoTasks
     * @param completedDeadlineTasks
     * @param completedEventTasks
     */
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

        syncAllUncompletedTasks();
        syncAllCompletedTasks();

        updateRecurMap();
        syncHashMaps();
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

        syncHashMaps();

        if (!_syncManager.saveAll(allDataList)) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "Unable to save Tasks. All data is lost.");
        }
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
        _recurringTasks.clear();
    }

}
