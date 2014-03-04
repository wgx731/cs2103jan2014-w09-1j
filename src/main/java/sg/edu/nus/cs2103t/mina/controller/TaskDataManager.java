package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskSetDaoImpl;
import sg.edu.nus.cs2103t.mina.model.DataParameter;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

/**
 * Task data manager: checks user's input determines the type of tasks breaks up
 * parameters for the tasks passes tasks to DAO to retrieve data from files
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class TaskDataManager {
	
	
    // error messages
    public static final int ERROR_MISSING_TASK_DESCRIPTION = -2;

    // parameters of String after trimming
    public static final int PARAM_TASK_DESCRIPTION = 0;

    private TreeSet<TodoTask> _uncompletedTodoTreeSet = new TreeSet<TodoTask>();
    private TreeSet<DeadlineTask> _uncompletedDeadlineTreeSet = new TreeSet<DeadlineTask>();
    private TreeSet<EventTask> _uncompletedEventTreeSet = new TreeSet<EventTask>();
    
    private TreeSet<TodoTask> _completedTodoTreeSet = new TreeSet<TodoTask>();
    private TreeSet<DeadlineTask> _completedDeadlineTreeSet = new TreeSet<DeadlineTask>();
    private TreeSet<EventTask> _completedEventTreeSet = new TreeSet<EventTask>();

    public TaskDataManager() {
        // empty constructor class
    }
    
    /** load methods: uncompleted tasks */
	@SuppressWarnings("unchecked")
	public TreeSet<TodoTask> getAllTodoTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_completedTodoTreeSet = ((TreeSet<TodoTask>) dao.loadTaskSet(
					TaskType.TODO, false));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return _completedTodoTreeSet;
	}

	@SuppressWarnings("unchecked")
	public TreeSet<DeadlineTask> getAllDeadlineTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_completedDeadlineTreeSet = ((TreeSet<DeadlineTask>) dao.loadTaskSet(
					TaskType.DEADLINE, false));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _completedDeadlineTreeSet;
	}

	@SuppressWarnings("unchecked")
	public TreeSet<EventTask> getAllEventTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_completedEventTreeSet = ((TreeSet<EventTask>) dao
					.loadTaskSet(TaskType.EVENT, false));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _completedEventTreeSet;
	}
	
	/** load methods: completed tasks */
	@SuppressWarnings("unchecked")
	public TreeSet<TodoTask> getPastTodoTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_uncompletedTodoTreeSet = ((TreeSet<TodoTask>) dao.loadTaskSet(
					TaskType.TODO, true));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return _uncompletedTodoTreeSet;
	}

	@SuppressWarnings("unchecked")
	public TreeSet<DeadlineTask> getPastDeadlineTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_uncompletedDeadlineTreeSet = ((TreeSet<DeadlineTask>) dao.loadTaskSet(
					TaskType.DEADLINE, true));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _uncompletedDeadlineTreeSet;
	}

	@SuppressWarnings("unchecked")
	public TreeSet<EventTask> getPastEventTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			_uncompletedEventTreeSet = ((TreeSet<EventTask>) dao
					.loadTaskSet(TaskType.EVENT, true));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _uncompletedEventTreeSet;
	}
	
	/**
	 * save methods WARNING: if any of the TreeSet object is null, this method
	 * will overwrite it!! need to find a way to check if this is the latest
	 * version or provide a way to undo
	 */
	private void saveAllTodoTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			dao.saveTaskSet(_completedTodoTreeSet, TaskType.TODO,
		            false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void saveAllDeadlineTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			dao.saveTaskSet(_completedDeadlineTreeSet, TaskType.DEADLINE,
		            false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void saveAllEventTasks() {
		// TODO: fill in Map parameters
		FileTaskSetDaoImpl dao = new FileTaskSetDaoImpl(null);

		try {
			dao.saveTaskSet(_completedEventTreeSet, TaskType.EVENT,
		            false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /**
     * This method creates a Task depending on its type and parameters. If
     * changes are successfully saved by DAO, it returns a Task object to the
     * method which called it.
     * 
     * @param String addParameters
     * @return
     */
    public Task<?> addTask(DataParameter addParameters) {
        switch (addParameters.getOriginalTaskType()) {
            case TODO:
            	TodoTask addedTodoTask = addTodoTask(addParameters);
            	return addedTodoTask;
            case DEADLINE:
                DeadlineTask addedDeadlineTask = addDeadlineTask(addParameters);
                return addedDeadlineTask;
            case EVENT:
                EventTask addedEventTask = addEventTask(addParameters);
                return addedEventTask;
            default:
                return null;
        }
    }

	private TodoTask addTodoTask(DataParameter addParameters) {
		getAllTodoTasks();
		
		TodoTask newTodoTask = new TodoTask(addParameters.getDescription(),
				addParameters.getPriority());

		if (_completedTodoTreeSet.add(newTodoTask)) {	
			return newTodoTask;
		}
		
		saveAllTodoTasks();

		return null;
	}

	private DeadlineTask addDeadlineTask(DataParameter addParameters) {
		getAllDeadlineTasks();
		
		DeadlineTask newDeadlineTask = new DeadlineTask(
				addParameters.getDescription(), addParameters.getEnd(),
				addParameters.getPriority());

		if (_completedDeadlineTreeSet.add(newDeadlineTask)) {
			return newDeadlineTask;
		}
		
		saveAllDeadlineTasks();

		return null;
	}
	
	private EventTask addEventTask(DataParameter addParameters) {
		getAllEventTasks();
		
		EventTask newEventTask = new EventTask(addParameters.getDescription(),
				addParameters.getStart(), addParameters.getEnd(),
				addParameters.getPriority());

		if (_completedEventTreeSet.add(newEventTask)) {
			return newEventTask;
		}
		
		saveAllEventTasks();

		return null;
	}

    /**
     * This method deletes a specific task by identifying the Task with its type
     * and id number. If changes are successfully saved by DAO, it returns a
     * Task object to the method which called it.
     * 
     * @param deleteParameters
     * @return Task<?> (if successful), null otherwise
     */
    public Task<?> deleteTask(String deleteParameters) {
        String[] parameters = deleteParameters.split(" ", 2);
        String taskTypeString = parameters[0].trim();

        switch (taskTypeString) {
            case ("todo"):
                return deleteTodo(Integer.parseInt(parameters[1].trim()));
            case ("deadline"):
                return deleteDeadline(Integer.parseInt(parameters[1].trim()));
            case ("event"):
                return deleteEvent(Integer.parseInt(parameters[1].trim()));
            default:
                break;
        }
        System.out.println("Unregconised task: " + taskTypeString);
        return null;
    }

    private TodoTask deleteTodo(int todoId) {
        TreeSet<TodoTask> tempTodoTreeSet = new TreeSet<TodoTask>();

        for (int i = 0; i < (todoId - 1); i++) {
            tempTodoTreeSet.add(_todoTreeSet.pollFirst());
        }

        TodoTask removedTodo = _todoTreeSet.pollFirst();
        _todoTreeSet.addAll(tempTodoTreeSet);

        return removedTodo;
    }

    private DeadlineTask deleteDeadline(int deadlineId) {
        TreeSet<DeadlineTask> tempDeadlineTreeSet = new TreeSet<DeadlineTask>();

        for (int i = 0; i < (deadlineId - 1); i++) {
            tempDeadlineTreeSet.add(_deadlineTreeSet.pollFirst());
        }

        DeadlineTask removeDeadline = _deadlineTreeSet.pollFirst();
        _deadlineTreeSet.addAll(tempDeadlineTreeSet);

        return removeDeadline;
    }

    private EventTask deleteEvent(int eventId) {
        TreeSet<EventTask> tempEventTreeSet = new TreeSet<EventTask>();

        for (int i = 0; i < (eventId - 1); i++) {
            tempEventTreeSet.add(_eventTreeSet.pollFirst());
        }

        EventTask removeEvent = _eventTreeSet.pollFirst();
        _eventTreeSet.addAll(tempEventTreeSet);

        return removeEvent;
    }

    /**
     * This method checks what Task the user wants to modify, calls the command
     * of DAO to make the amendments, then returns the modified task.
     * 
     * @param modifyParameters
     * @return
     */
    public Task<?> modifyTask(DataParameter modifyParameters) {
        // check old type, check new type, load trees, update trees, save
    	
    	return null;
    }

   
    // TODO: markCompleted
    // TODO: getAll___Task
    // TODO: getAllCompleted___Task

    /**
     * only to be used for testing
     */
    public void resetTrees() {
        _completedTodoTreeSet.clear();
        _completedDeadlineTreeSet.clear();
        _completedEventTreeSet.clear();
        
        _uncompletedTodoTreeSet.clear();
        _uncompletedDeadlineTreeSet.clear();
        _uncompletedEventTreeSet.clear();
    }
}
