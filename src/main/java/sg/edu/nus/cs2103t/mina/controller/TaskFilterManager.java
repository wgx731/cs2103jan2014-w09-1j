package sg.edu.nus.cs2103t.mina.controller;
/**
 * This class is in charged to filtering tasks based on certain
 * criteria
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {
	
	private TaskDataManager _taskStore;
	
	public TaskFilterManager(TaskDataManager taskStore){
		_taskStore = taskStore;
	}
	
	/**
	 * Filter the tasks based on its critieria
	 * 
	 * @param param a FilterParameter object that represents the 
	 * 							criteria
	 * @return An arraylist of tasks that satisfied the task. 
	 * 				 Empty if there's none
	 */
	public ArrayList<Task> filterTask(FilterParameter param) 
																		throws NullPointerException{
		//GuardClause
		if (param==null) { 
			throw new NullPointerException();
		}
		
		//TODO remove this once TaskDataManager is stable
		TaskDataManagerStub _taskStore;
		_taskStore = (TaskDataManagerStub)this._taskStore;
		
		//TODO clarify getAllTask from Joanne. For now, just use 
		//stub methods.
		TreeSet<TodoTask> todos = _taskStore.getTodoTasks();
		TreeSet<EventTask> events = _taskStore.getEventTasks();
		TreeSet<DeadlineTask> deadlines = _taskStore.getDeadlineTasks();		
		
		Iterator<TodoTask> todoIter = todos.iterator();
		Iterator<EventTask> eventIter = events.iterator();
		Iterator<DeadlineTask> deadlineIter = deadlines.iterator();
		
		ArrayList<Task> result = new ArrayList<Task>();
		
		while (todoIter.hasNext()) {
			result.add(todoIter.next());
		}
		while (eventIter.hasNext()) {
			result.add(eventIter.next());
		}
		while (deadlineIter.hasNext()) {
			result.add(deadlineIter.next());
		}
		
		return result;
	}
	
	/**
	 * Search for tasks based on its keywords.
	 * 
	 * @param param a SearchParameter object that represents the 
	 * 				keywords used
	 * @return An arraylist of task that satisfied the keywords. 
	 * 				 Empty if there's none.
	 */
	public ArrayList<Task> searchTasks(SearchParameter param){
		return null;
	}
	

	
}
