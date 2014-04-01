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

    // HashMaps for recurring and block tasks
    private HashMap<String, ArrayList<Task<?>>> _recurringTasks;
    private HashMap<String, ArrayList<EventTask>> _blockTasks;
    private int maxRecurTagInt = 0;
    private int maxBlockTagInt = 0;

    private static final int TAG_INT_POS = 1;

    // Sync tools
    private final List<TaskSetDataParameter> allDataList = new ArrayList<TaskSetDataParameter>(
            6);

    private List<MemoryDataObserver> _observers;
    private DataSyncManager _syncManager;

    public TaskDataManager() {
        initiateVariables();
    }

    private void initiateVariables() {
        _uncompletedTodoTasks = new TreeSet<TodoTask>();
        _uncompletedDeadlineTasks = new TreeSet<DeadlineTask>();
        _uncompletedEventTasks = new TreeSet<EventTask>();
        _completedTodoTasks = new TreeSet<TodoTask>();
        _completedDeadlineTasks = new TreeSet<DeadlineTask>();
        _completedEventTasks = new TreeSet<EventTask>();

        _recurringTasks = new HashMap<String, ArrayList<Task<?>>>();
        _blockTasks = new HashMap<String, ArrayList<EventTask>>();

        _observers = new ArrayList<MemoryDataObserver>();
        _syncManager = null;
    }

    @SuppressWarnings("unchecked")
    public TaskDataManager(DataSyncManager syncManager) {
        SortedSet<? extends Task<?>> tempTasks = null;

        _syncManager = syncManager;

        _observers = new ArrayList<MemoryDataObserver>();
        _observers.add((MemoryDataObserver) syncManager);

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

        // TODO: load from data sync manager, then add to observer
        _recurringTasks = new HashMap<String, ArrayList<Task<?>>>();
        _blockTasks = new HashMap<String, ArrayList<EventTask>>();

        updateHashMaps();
    }

    /* load methods: uncompleted tasks */
    public SortedSet<TodoTask> getUncompletedTodoTasks() {
        return _uncompletedTodoTasks;
    }

    public SortedSet<DeadlineTask> getUncompletedDeadlineTasks() {
        return _uncompletedDeadlineTasks;
    }

    public SortedSet<EventTask> getUncompletedEventTasks() {
        return _uncompletedEventTasks;
    }

    /* load methods: completed tasks */
    public SortedSet<TodoTask> getCompletedTodoTasks() {
        return _completedTodoTasks;
    }

    public SortedSet<DeadlineTask> getCompletedDeadlineTasks() {
        return _completedDeadlineTasks;
    }

    public SortedSet<EventTask> getCompletedEventTasks() {
        return _completedEventTasks;
    }

    /* load methods: HashMaps */
    public HashMap<String, ArrayList<Task<?>>> getRecurringTasks() {
        return _recurringTasks;
    }

    public HashMap<String, ArrayList<EventTask>> getBlockTasks() {
        return _blockTasks;
    }

    /**
     * Updates HashMaps if user has modified their JSON files.
     */
    private void updateHashMaps() {
        // if
        // TODO: check if _recurringTasks has been modified
        updateTasksMaps(); // iterates through both Event and Deadline Tasks

        // else if
        // TODO: check if _blockTasks has been modified
        updateBlockTaskMap(); // iterates through Event Tasks only

        // else load old HashMaps, return
    }

    private void updateTasksMaps() {
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
                checkBlock(currEventTask);
                currEventTask = eventTaskIterator.next();
            }
        }
    }

    private void updateBlockTaskMap() {
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
    

    private void checkBlock(EventTask currEventTask) {
        if (currEventTask.getTag().contains("BLOCK")) {
            includeInBlockMap(currEventTask, currEventTask.getTag());   
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
            ArrayList<Task<?>> taskList = _recurringTasks.remove(recurTag);
            taskList.add(taskToInclude);

            _recurringTasks.put(recurTag, taskList);
        } else {
            ArrayList<Task<?>> taskList = new ArrayList<Task<?>>();
            taskList.add(taskToInclude);

            _recurringTasks.put(recurTag, taskList);
        }
    }

    private void includeInBlockMap(EventTask taskToInclude, String blockTag) {
        if (!isValidBlockTag(blockTag)) {
            return;
        }

        if (_blockTasks.containsKey(blockTag)) {
            // TODO: check if description is the same
            ArrayList<EventTask> taskList = _blockTasks.remove(blockTag);
            taskList.add(taskToInclude);

            _blockTasks.put(blockTag, taskList);
        } else {
            ArrayList<EventTask> taskList = new ArrayList<EventTask>();
            taskList.add(taskToInclude);

            _blockTasks.put(blockTag, taskList);
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

            maxRecurTagInt = recurTagInt > maxRecurTagInt ? recurTagInt
                    : maxRecurTagInt;

            return true;
        }
    }

    private boolean isValidBlockTag(String tag) {
        int blockTagInt = -1;

        if (!tag.contains("_")) {
            return false;
        } else {
            String[] tagTokens = tag.split("_", 2);

            try {
                blockTagInt = Integer.parseInt(tagTokens[TAG_INT_POS]);
            } catch (NumberFormatException e) {
                // task is not added to HashMap, it is not treated as a
                // recurring task anymore
                return false;
            }

            maxBlockTagInt = blockTagInt > maxBlockTagInt ? blockTagInt
                    : maxBlockTagInt;

            return true;
        }
    }

    /**
     * Creates a Task depending on its type and parameters. If changes are
     * successfully saved by DAO, it returns a Task object to the method which
     * called it.
     * 
     * @param DataParameter addParameters
     * @return added Task
     */
    public Task<?> addTask(DataParameter addParameters) {
        assert (addParameters.getNewTaskType() != null);

        if (addParameters.getTag() != null && addParameters.getTag().equals(
                "RECUR")) {
            assert (addParameters.getNewTaskType().equals(TaskType.EVENT) || addParameters
                    .getNewTaskType().equals(TaskType.DEADLINE));

            return addRecurringTask(addParameters);

        } else if (addParameters.getTag() != null && addParameters.getTag()
                .equals("BLOCK")) {
            assert (addParameters.getNewTaskType().equals(TaskType.EVENT));

            return addBlockTask(addParameters);

        } else {
            return addRegTask(addParameters);
        }
    }

    private Task<?> addRecurringTask(DataParameter addParameters) {
        String recurTag = "RECUR_" + maxRecurTagInt++;

        assert (!_recurringTasks.containsKey(recurTag));
        assert (addParameters.getFreqOfTimeType() != 0);

        Date endRecurOn = addParameters.getEndRecurOn() == null ? generateStartOfNextYear()
                : addParameters.getEndRecurOn();

        switch (addParameters.getNewTaskType()) {
            case DEADLINE :
                Date currDeadline = addParameters.getEndDate();

                while (currDeadline.compareTo(endRecurOn) < 0) {
                    includeInRecurMap(addDeadlineTask(addParameters), recurTag);

                    currDeadline = updateDate(currDeadline,
                            addParameters.getTimeType(),
                            addParameters.getFreqOfTimeType());

                    addParameters.setEndDate(currDeadline);
                }

                return _recurringTasks.get(recurTag).get(0);

            case EVENT :
                Date currStartDate = addParameters.getStartDate();
                Date currEndDate = addParameters.getEndDate();

                while (currStartDate.compareTo(endRecurOn) < 0) {
                    includeInRecurMap(addEventTask(addParameters), recurTag);

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

    private Task<?> addBlockTask(DataParameter addParameters) {
        String blockTag = "BLOCK_" + maxBlockTagInt++;

        Date currStartDate = addParameters.getStartDate();
        Date currEndDate = addParameters.getEndDate();

        for (int i = 1; i <= addParameters.getTimeSlots().size(); i++) {
            includeInBlockMap((EventTask) addEventTask(addParameters), blockTag);

            currStartDate = addParameters.getTimeSlots().get(i).getStartDate();
            currEndDate = addParameters.getTimeSlots().get(i).getEndDate();

            addParameters.setStartDate(currStartDate);
            addParameters.setEndDate(currEndDate);

        }

        return _recurringTasks.get(blockTag).get(0);
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

    private Task<?> addTodoTask(DataParameter addParameter) {
        TodoTask newTodoTask = createTodoTask(addParameter);
        if (_uncompletedTodoTasks.add(newTodoTask)) {
            syncUncompletedTasks(TaskType.TODO);

            return newTodoTask;
        }
        return null;
    }

    private Task<?> addEventTask(DataParameter addParameter) {
        EventTask newEventTask = createEventTask(addParameter);
        if (_uncompletedEventTasks.add(newEventTask)) {
            syncUncompletedTasks(TaskType.EVENT);
            
            return newEventTask;
        }
        return null;
    }

    private Task<?> addDeadlineTask(DataParameter addParameter) {
        DeadlineTask newDeadlineTask = createDeadlineTask(addParameter);
        if (_uncompletedDeadlineTasks.add(newDeadlineTask)) {
            syncUncompletedTasks(TaskType.DEADLINE);

            return newDeadlineTask;
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
        if (deleteParameters.getTaskObject().getTag().contains("RECUR")) {
            assert (deleteParameters.getNewTaskType().equals(TaskType.EVENT) || deleteParameters
                    .getNewTaskType().equals(TaskType.DEADLINE));
            
            return deleteRecurringTasks(deleteParameters);
            
        } else if (deleteParameters.getTaskObject().getTag().contains("BLOCK")) {
            assert (deleteParameters.getNewTaskType().equals(TaskType.EVENT));
            
            return deleteBlockTasks(deleteParameters);
            
        } else {
            return deleteRegTask(deleteParameters);
            
        }
    }

    private Task<?> deleteRecurringTasks(DataParameter deleteParameters) {
        Task<?> recurTaskToDelete = deleteParameters.getTaskObject();
        
        if (_recurringTasks.containsKey(recurTaskToDelete.getTag())) {
            List<Task<?>> listOfRecTasks = _recurringTasks.remove(recurTaskToDelete.getTag());
            
            if (deleteParameters.isModifyAll()) {
                while (!listOfRecTasks.isEmpty()) {
                    deleteParameters.setTaskObject(listOfRecTasks.remove(0));
                    deleteRegTask(deleteParameters);
                }
            } else {
                List<Task<?>> tempListOfRecTasks = _recurringTasks.remove(recurTaskToDelete.getTag());
                tempListOfRecTasks.remove(deleteParameters.getTaskObject());
                deleteRegTask(deleteParameters);
            }            
            
            return recurTaskToDelete;
        } else {
            return null;
        }
    }

    private Task<?> deleteBlockTasks(DataParameter deleteParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    private Task<?> deleteRegTask(DataParameter deleteParameters) {
        switch (deleteParameters.getTaskObject().getType()) {
            case TODO :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedTodoTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.TODO);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                } else {
                    if (_completedTodoTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.TODO);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                }
            case DEADLINE :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedDeadlineTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.DEADLINE);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                } else {
                    if (_completedDeadlineTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.DEADLINE);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                }
            case EVENT :
                if (!deleteParameters.getTaskObject().isCompleted()) {
                    if (_uncompletedEventTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.EVENT);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                } else {
                    if (_completedEventTasks.remove(deleteParameters
                            .getTaskObject())) {
                        syncUncompletedTasks(TaskType.EVENT);

                        return deleteParameters.getTaskObject();
                    } else {
                        return null;
                    }
                }
            default :
                // System.out.println("Unable to determine Task Type.");
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
        Task<?> prevTask = deleteTask(modifyParameters);
        if (prevTask == null) {
            return null;
        } else {
            DataParameter newSetOfParameters = new DataParameter();

            newSetOfParameters.loadOldTask(prevTask);
            newSetOfParameters.loadNewParameters(modifyParameters);

            Task<?> newTask = addTask(newSetOfParameters);
            newTask.setLastEditedTime(new Date());

            return newTask;
        }
    }

    /**
     * Marks a given task as completed by setting its completed tag to true
     * 
     * @param completeParameters
     * @return
     */
    public Task<?> markCompleted(DataParameter completeParameters) {
        switch (completeParameters.getTaskObject().getType()) {
            case TODO :
                if (_uncompletedTodoTasks.remove(completeParameters
                        .getTaskObject())) {
                    TodoTask finTodoTask = (TodoTask) completeParameters
                            .getTaskObject();

                    finTodoTask.setCompleted(true);
                    finTodoTask.setLastEditedTime(new Date());

                    _completedTodoTasks.add(finTodoTask);

                    syncCompletedTasks(TaskType.TODO);
                    syncUncompletedTasks(TaskType.TODO);

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

                    syncCompletedTasks(TaskType.DEADLINE);
                    syncUncompletedTasks(TaskType.DEADLINE);

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

                    syncCompletedTasks(TaskType.EVENT);
                    syncUncompletedTasks(TaskType.EVENT);

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
     * Marks a given task as uncompleted by setting its completed tag to false.
     * <p>
     * Also used for undo function.
     * 
     * @param uncompleteParameters
     * @return
     */
    public Task<?> markUncompleted(DataParameter uncompleteParameters) {
        switch (uncompleteParameters.getTaskObject().getType()) {
            case TODO :
                if (_completedTodoTasks.remove(uncompleteParameters
                        .getTaskObject())) {
                    TodoTask finTodoTask = (TodoTask) uncompleteParameters
                            .getTaskObject();

                    finTodoTask.setCompleted(false);
                    finTodoTask.setLastEditedTime(new Date());

                    _uncompletedTodoTasks.add(finTodoTask);

                    syncCompletedTasks(TaskType.TODO);
                    syncUncompletedTasks(TaskType.TODO);

                    return finTodoTask;
                } else {
                    return null;
                }
            case DEADLINE :
                if (_completedDeadlineTasks.remove(uncompleteParameters
                        .getTaskObject())) {
                    DeadlineTask finDeadlineTask = (DeadlineTask) uncompleteParameters
                            .getTaskObject();

                    finDeadlineTask.setCompleted(false);
                    finDeadlineTask.setLastEditedTime(new Date());

                    _uncompletedDeadlineTasks.add(finDeadlineTask);

                    syncCompletedTasks(TaskType.DEADLINE);
                    syncUncompletedTasks(TaskType.DEADLINE);

                    return finDeadlineTask;
                } else {
                    return null;
                }
            case EVENT :
                if (_completedEventTasks.remove(uncompleteParameters
                        .getTaskObject())) {
                    EventTask finEventTask = (EventTask) uncompleteParameters
                            .getTaskObject();

                    finEventTask.setCompleted(false);
                    finEventTask.setLastEditedTime(new Date());

                    _uncompletedEventTasks.add(finEventTask);

                    syncCompletedTasks(TaskType.EVENT);
                    syncUncompletedTasks(TaskType.EVENT);

                    return finEventTask;
                } else {
                    return null;
                }
            default :
                System.out.println("Unable to determine Task Type.");
                return null;
        }

    }

    /* Sync Methods */
    private void syncUncompletedTasks(TaskType taskType) {
        for (MemoryDataObserver observer : _observers) {
            switch (taskType) {
                case TODO :
                    observer.updateChange(new TaskSetDataParameter(
                            _uncompletedTodoTasks, taskType, false));
                    break;
                case DEADLINE :
                    observer.updateChange(new TaskSetDataParameter(
                            _uncompletedDeadlineTasks, taskType, false));
                    break;
                case EVENT :
                    observer.updateChange(new TaskSetDataParameter(
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
                    observer.updateChange(new TaskSetDataParameter(
                            _completedTodoTasks, taskType, true));
                    break;
                case DEADLINE :
                    observer.updateChange(new TaskSetDataParameter(
                            _completedDeadlineTasks, taskType, true));
                    break;
                case EVENT :
                    observer.updateChange(new TaskSetDataParameter(
                            _completedEventTasks, taskType, true));
                    break;
                default :
                    System.out.println("Unable to determine task type.");
                    return;
            }
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
    }

    /**
     * only to be used for testing
     */
    public void resetTrees() {
        _completedTodoTasks.clear();
        _completedDeadlineTasks.clear();
        _completedEventTasks.clear();

        _uncompletedTodoTasks.clear();
        _uncompletedDeadlineTasks.clear();
        _uncompletedEventTasks.clear();

    }
}
