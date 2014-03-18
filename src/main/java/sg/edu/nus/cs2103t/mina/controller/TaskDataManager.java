package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SyncDataParameter;

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
    public static final int SYNC_LIST_MAX = 6;

    private SortedSet<TodoTask> _uncompletedTodoTasks;
    private SortedSet<DeadlineTask> _uncompletedDeadlineTasks;
    private SortedSet<EventTask> _uncompletedEventTasks;

    private SortedSet<TodoTask> _completedTodoTasks;
    private SortedSet<EventTask> _completedEventTasks;
    private SortedSet<DeadlineTask> _completedDeadlineTasks;

    private List<SyncDataParameter> _syncList;

    public TaskDataManager() {
        _uncompletedTodoTasks = new TreeSet<TodoTask>();
        _uncompletedDeadlineTasks = new TreeSet<DeadlineTask>();
        _uncompletedEventTasks = new TreeSet<EventTask>();
        _completedTodoTasks = new TreeSet<TodoTask>();
        _completedDeadlineTasks = new TreeSet<DeadlineTask>();
        _completedEventTasks = new TreeSet<EventTask>();
        _syncList = new ArrayList<SyncDataParameter>(SYNC_LIST_MAX);
    }

    @SuppressWarnings("unchecked")
    public TaskDataManager(TaskDao storage) {
        SortedSet<? extends Task<?>> tempTasks = null;
        try {
            tempTasks = storage.loadTaskSet(TaskType.TODO, false);
            _uncompletedTodoTasks = tempTasks == null ? new TreeSet<TodoTask>()
                    : (SortedSet<TodoTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.DEADLINE, false);
            _uncompletedDeadlineTasks = tempTasks == null ? new TreeSet<DeadlineTask>()
                    : (SortedSet<DeadlineTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.EVENT, false);
            _uncompletedEventTasks = tempTasks == null ? new TreeSet<EventTask>()
                    : (SortedSet<EventTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.TODO, true);
            _completedTodoTasks = tempTasks == null ? new TreeSet<TodoTask>()
                    : (SortedSet<TodoTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.DEADLINE, true);
            _completedDeadlineTasks = tempTasks == null ? new TreeSet<DeadlineTask>()
                    : (SortedSet<DeadlineTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.EVENT, true);
            _completedEventTasks = tempTasks == null ? new TreeSet<EventTask>()
                    : (SortedSet<EventTask>) tempTasks;

            _syncList = new ArrayList<SyncDataParameter>(SYNC_LIST_MAX);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
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

    /* sync-lists controls */
    public List<SyncDataParameter> getSyncList() {
        return _syncList;
    }

    public void clearSyncList() {
        _syncList.clear();
    }

    /**
     * Creates a Task depending on its type and parameters. If
     * changes are successfully saved by DAO, it returns a Task object to the
     * method which called it.
     * 
     * @param DataParameter addParameters
     * @return added Task
     */
    public Task<?> addTask(DataParameter addParameter) {
        switch (addParameter.getNewTaskType()) {
            case TODO :
                TodoTask newTodoTask = createTodoTask(addParameter);
                if (_uncompletedTodoTasks.add(newTodoTask)) {
                    syncUncompletedTasks(TaskType.TODO);
                    
                    return newTodoTask;
                }
                return null;

            case DEADLINE :
                DeadlineTask newDeadlineTask = createDeadlineTask(addParameter);
                if (_uncompletedDeadlineTasks.add(newDeadlineTask)) {
                    syncUncompletedTasks(TaskType.DEADLINE);
                    
                    return newDeadlineTask;
                }
                return null;

            case EVENT :
                EventTask newEventTask = createEventTask(addParameter);
                if (_uncompletedEventTasks.add(newEventTask)) {
                    syncUncompletedTasks(TaskType.EVENT);
                    
                    return newEventTask;
                }
                return null;

            default :
                return null;
        }
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
     * Deletes a specific task by identifying the Task with its type
     * and id number. If changes are successfully saved by DAO, it returns a
     * Task object to the method which called it.
     * 
     * @param DataParameter deleteParameters
     * @return Task<?> (if successful), null otherwise
     */
    public Task<?> deleteTask(DataParameter deleteParameters) {
        switch (deleteParameters.getTaskObject().getType()) {
            case TODO :
                if (_uncompletedTodoTasks.remove(deleteParameters
                        .getTaskObject())) {
                    syncUncompletedTasks(TaskType.TODO);
                    
                    return deleteParameters.getTaskObject();
                } else {
                    return null;
                }
            case DEADLINE :
                if (_uncompletedDeadlineTasks.remove(deleteParameters
                        .getTaskObject())) {
                    syncUncompletedTasks(TaskType.DEADLINE);
                    
                    return deleteParameters.getTaskObject();
                } else {
                    return null;
                }
            case EVENT :
                if (_uncompletedEventTasks.remove(deleteParameters
                        .getTaskObject())) {
                    syncUncompletedTasks(TaskType.EVENT);
                    
                    return deleteParameters.getTaskObject();
                } else {
                    return null;
                }
            default :
                // System.out.println("Unable to determine Task Type.");
                return null;
        }
    }

    /**
     * Checks what Task the user wants to modify, calls the command
     * of DAO to make the amendments, then returns the modified task.
     * 
     * @param DataParameter modifyParameters
     * @return task modified
     */
    public Task<?> modifyTask(DataParameter modifyParameters) {
        DataParameter newSetOfParameters = new DataParameter();

        newSetOfParameters.loadOldTask(deleteTask(modifyParameters));
        newSetOfParameters.loadNewParameters(modifyParameters);

        return addTask(newSetOfParameters);
    }

    public Task<?> markCompleted(DataParameter completeParameters) {
        switch (completeParameters.getTaskObject().getType()) {
            case TODO :
               if (_uncompletedTodoTasks.remove(completeParameters.getTaskObject())) {
                   TodoTask finTodoTask = (TodoTask) completeParameters.getTaskObject();
                   
                   finTodoTask.setCompleted(true);
                   _completedTodoTasks.add(finTodoTask);
                   
                   syncCompletedTasks(TaskType.TODO);
                   syncUncompletedTasks(TaskType.TODO);
                   
                   return finTodoTask;
               } else {
                   return null;
               }
            case DEADLINE :
                if (_uncompletedDeadlineTasks.remove(completeParameters.getTaskObject())) {
                    DeadlineTask finDeadlineTask = (DeadlineTask) completeParameters.getTaskObject();
                
                    finDeadlineTask.setCompleted(true);
                    _completedDeadlineTasks.add(finDeadlineTask);
                    
                    syncCompletedTasks(TaskType.DEADLINE);
                    syncUncompletedTasks(TaskType.DEADLINE);
                    
                    return finDeadlineTask;
                } else {
                    return null;
                }
            case EVENT :
                if (_uncompletedEventTasks.remove(completeParameters.getTaskObject())) {
                    EventTask finEventTask = (EventTask) completeParameters.getTaskObject();
                
                    finEventTask.setCompleted(true);
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
    
    /* Sync Methods */
    private void syncUncompletedTasks(TaskType taskType) {
        SyncDataParameter dataToSync;

        switch (taskType) {
            case TODO :
                dataToSync = new SyncDataParameter(_uncompletedTodoTasks,
                        taskType, false);
                break;
            case DEADLINE :
                dataToSync = new SyncDataParameter(_uncompletedDeadlineTasks,
                        taskType, false);
                break;
            case EVENT :
                dataToSync = new SyncDataParameter(_uncompletedEventTasks,
                        taskType, false);
                break;
            default :
                System.out.println("Unable to determine task type.");
                return;
        }

        if (!_syncList.contains(dataToSync)) {
            assert (_syncList.add(dataToSync));
        }
        //TODO: Interact with the stub from DAO
    }
    
    private void syncCompletedTasks(TaskType taskType) {
        SyncDataParameter dataToSync;

        switch (taskType) {
            case TODO :
                dataToSync = new SyncDataParameter(_completedTodoTasks,
                        taskType, true);
                break;
            case DEADLINE :
                dataToSync = new SyncDataParameter(_completedDeadlineTasks,
                        taskType, true);
                break;
            case EVENT :
                dataToSync = new SyncDataParameter(_completedEventTasks,
                        taskType, true);
                break;
            default :
                System.out.println("Unable to determine task type.");
                return;
        }

        if (!_syncList.contains(dataToSync)) {
            assert (_syncList.add(dataToSync));
        }

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
