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
	public Task<?> deleteTask(DataParameter deleteParameters) {
		switch (deleteParameters.getOriginalTaskType()) {
			case TODO :
				TodoTask addedTodoTask = deleteTodoTask(deleteParameters);
				return addedTodoTask;
			case DEADLINE :
				DeadlineTask addedDeadlineTask = deleteDeadlineTask(deleteParameters);
				return addedDeadlineTask;
			case EVENT :
				EventTask addedEventTask = deleteEventTask(deleteParameters);
				return addedEventTask;
			default :
				return null;
		}
	}
	

    private TodoTask deleteTodoTask(DataParameter deleteParameters) {
    	getAllTodoTasks();
    	TreeSet<TodoTask> tempTodoTreeSet = new TreeSet<TodoTask>();
    	
    	// assumes that TreeSet ordering matches the ID
    	// TODO map delete id to TreeSetID
		for (int i = 0; i < deleteParameters.getTaskId(); i++) {
			tempTodoTreeSet.add(_completedTodoTreeSet.first());
		}
    	
		TodoTask deletedTodoTask = _completedTodoTreeSet.first();
		_completedTodoTreeSet.addAll(tempTodoTreeSet);
		
		saveAllTodoTasks();
    	
		return deletedTodoTask;
	}

	private DeadlineTask deleteDeadlineTask(DataParameter deleteParameters) {
		getAllDeadlineTasks();
    	TreeSet<DeadlineTask> tempDeadlineTreeSet = new TreeSet<DeadlineTask>();
    	
    	// assumes that TreeSet ordering matches the ID
    	// TODO map delete id to TreeSetID
		for (int i = 0; i < deleteParameters.getTaskId(); i++) {
			tempDeadlineTreeSet.add(_completedDeadlineTreeSet.first());
		}
    	
		DeadlineTask deletedDeadlineTask = _completedDeadlineTreeSet.first();
		_completedDeadlineTreeSet.addAll(tempDeadlineTreeSet);
		
		saveAllDeadlineTasks();
    	
		return deletedDeadlineTask;
	}

	private EventTask deleteEventTask(DataParameter deleteParameters) {
		getAllEventTasks();
    	TreeSet<EventTask> tempEventTreeSet = new TreeSet<EventTask>();
    	
    	// assumes that TreeSet ordering matches the ID
    	// TODO map delete id to TreeSetID
		for (int i = 0; i < deleteParameters.getTaskId(); i++) {
			tempEventTreeSet.add(_completedEventTreeSet.first());
		}
    	
		EventTask deletedEventTask = _completedEventTreeSet.first();
		_completedEventTreeSet.addAll(tempEventTreeSet);
		
		saveAllEventTasks();
    	
		return deletedEventTask;
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
