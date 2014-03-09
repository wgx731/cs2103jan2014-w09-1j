package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    public SortedSet<TodoTask> getAllTodoTasks() {
        return _uncompletedTodoTasks;
    }

    public SortedSet<DeadlineTask> getAllDeadlineTasks() {
        return _uncompletedDeadlineTasks;
    }

    public SortedSet<EventTask> getAllEventTasks() {
        return _uncompletedEventTasks;
    }

    /* load methods: completed tasks */
    public SortedSet<TodoTask> getPastTodoTasks() {
        return _completedTodoTasks;
    }

    public SortedSet<DeadlineTask> getPastDeadlineTasks() {
        return _completedDeadlineTasks;
    }

    public SortedSet<EventTask> getPastEventTasks() {
        return _completedEventTasks;
    }
    
    /* synclists controls */
    public List<SyncDataParameter> getSyncList() {
        return _syncList;
    }

    public void clearSyncList() {
        _syncList.clear();
    }

    /**
     * This method creates a Task depending on its type and parameters. If
     * changes are successfully saved by DAO, it returns a Task object to the
     * method which called it.
     * 
     * @param String addParameters
     * @return
     */
    public Task<?> addTask(DataParameter addParameter) {
        switch (addParameter.getNewTaskType()) {
            case TODO:
                TodoTask newTodoTask = createTodoTask(addParameter);
                if (_uncompletedTodoTasks.add(newTodoTask)) {
                    return syncTasks(TaskType.TODO, newTodoTask);
                }
                return null;
            
            case DEADLINE:
                DeadlineTask newDeadlineTask = createDeadlineTask(addParameter);
                if (_uncompletedDeadlineTasks.add(newDeadlineTask)) {
                    return syncTasks(TaskType.DEADLINE, newDeadlineTask);
                }
                return null;
            
            case EVENT:
                EventTask newEventTask = createEventTask(addParameter);
                if (_uncompletedEventTasks.add(newEventTask)) {
                    return syncTasks(TaskType.EVENT, newEventTask);
                }
                return null;
            
            default:
                return null;
        }
    }

	private TodoTask createTodoTask(DataParameter addParameter) {
		return new TodoTask(addParameter.getDescription(),
				addParameter.getPriority());
	}

	private DeadlineTask createDeadlineTask(DataParameter addParameter) {
		return new DeadlineTask(addParameter.getDescription(),
				addParameter.getEndDate(), addParameter.getPriority());
	}

	private EventTask createEventTask(DataParameter addParameter) {
		return new EventTask(addParameter.getDescription(),
				addParameter.getStartDate(), addParameter.getEndDate(),
				addParameter.getPriority());
	}

	private Task<?> syncTasks(TaskType taskType, Task<?> newTask) {
		SyncDataParameter dataToSync;

		switch (taskType) {
			case TODO :
				dataToSync = new SyncDataParameter(_uncompletedTodoTasks, taskType,
						false);
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
				return null;
		}

		if (!_syncList.contains(dataToSync)) {
			assert (_syncList.add(dataToSync));
		}
		return newTask;
	}

    /**
     * This method deletes a specific task by identifying the Task with its type
     * and id number. If changes are successfully saved by DAO, it returns a
     * Task object to the method which called it.
     * 
     * @param deleteParameters
     * @return Task<?> (if successful), null otherwise
     */
    public Task<?> deleteTask(DataParameter deleteParameters) {
        switch (deleteParameters.getOriginalTaskType()) {
            case TODO:
                TodoTask addedTodoTask = deleteTodoTask(deleteParameters.getTaskId());
                return addedTodoTask;
            case DEADLINE:
                DeadlineTask addedDeadlineTask = deleteDeadlineTask(deleteParameters.getTaskId());
                return addedDeadlineTask;
            case EVENT:
                EventTask addedEventTask = deleteEventTask(deleteParameters.getTaskId());
                return addedEventTask;
            default:
                return null;
        }
    }

    private TodoTask deleteTodoTask(int deleteTaskId) {
    	Iterator<TodoTask> completedTodoTaskIterator =  _uncompletedTodoTasks.iterator();
    	TodoTask deletedTodoTask;
    	
    	for (int i = 0; completedTodoTaskIterator.hasNext(); i++) {
    		deletedTodoTask = completedTodoTaskIterator.next();
    		if(i == (deleteTaskId - 1)) {
    			completedTodoTaskIterator.remove();
    			return deletedTodoTask;
    		}
    	}
    	
        return null;
    }

    private DeadlineTask deleteDeadlineTask(int deleteTaskId) {
    	Iterator<DeadlineTask> completedTodoTaskIterator =  _uncompletedDeadlineTasks.iterator();
    	DeadlineTask deletedDeadlineTask;
    	
    	for (int i = 0; completedTodoTaskIterator.hasNext(); i++) {
    		deletedDeadlineTask = completedTodoTaskIterator.next();
    		if(i == (deleteTaskId - 1)) {
    			completedTodoTaskIterator.remove();
    			return deletedDeadlineTask;
    		}
    	}
    	
        return null;
    }

    private EventTask deleteEventTask(int deleteTaskId) {
    	Iterator<EventTask> completedTodoTaskIterator =  _uncompletedEventTasks.iterator();
    	EventTask deletedEventTask;
    	
    	for (int i = 0; completedTodoTaskIterator.hasNext(); i++) {
    		deletedEventTask = completedTodoTaskIterator.next();
    		if(i == (deleteTaskId - 1)) {
    			completedTodoTaskIterator.remove();
    			return deletedEventTask;
    		}
    	}
    	
        return null;
    }

    /**
     * This method checks what Task the user wants to modify, calls the command
     * of DAO to make the amendments, then returns the modified task.
     * 
     * @param modifyParameters
     * @return
     */
    public Task<?> modifyTask(DataParameter modifyParameters) {
        // TODO: finish modify task
        return null;
    }

    // TODO: markCompleted
    // TODO: getAll___Task
    // TODO: getAllCompleted___Task

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
