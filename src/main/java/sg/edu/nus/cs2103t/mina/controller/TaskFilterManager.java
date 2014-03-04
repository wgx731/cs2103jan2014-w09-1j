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
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {

	private TaskDataManager _taskStore;

	public static final String DEADLINE = "deadline";
	public static final String TODO = "todo";
	public static final String EVENT = "event";
	public static final String COMPLETE = "complete";
	public static final String COMPLETE_PLUS = "+complete";

	public TaskFilterManager(TaskDataManager taskStore) {
		_taskStore = taskStore;
	}

	/**
	 * Filter the tasks based on its critieria
	 * 
	 * @param param
	 *          a FilterParameter object that represents the criteria
	 * @return An arraylist of tasks that satisfied the task. Empty if there's
	 *         none
	 */
	public ArrayList<Task<?>> filterTask(FilterParameter param)
			throws NullPointerException {
		// GuardClause
		if (param == null) {
			throw new NullPointerException();
		}

		ArrayList<String> filters = param.getFilters();
		ArrayList<Task<?>> result = new ArrayList<Task<?>>();

		if (filters.isEmpty()) {
			result = getAllUncompletedTasks();
			return result;
		}

		if (filters.contains(DEADLINE)) {
			result.addAll(getDeadlines());
		}

		if (filters.contains(TODO)) {
			result.addAll(getTodos());
		}

		if (filters.contains(EVENT)) {
			result.addAll(getEvents());
		}
		
		if (filters.contains(COMPLETE)) {
			result.addAll(getCompletedTasks());
		}
		
		if (filters.contains(COMPLETE_PLUS)) {
			result.addAll(getCompletedTasks());
			result.addAll(getAllUncompletedTasks());
		}
		
		return result;
	}

	private ArrayList<Task<?>> getCompletedTasks() {
			
		ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();
		
		TreeSet<TodoTask> todos = _taskStore.getCompTodoTasks();
		Iterator<TodoTask> todoIter = todos.iterator();
		
		TreeSet<EventTask> events = _taskStore.getCompEventTasks();
		Iterator<EventTask> eventIter = events.iterator();
		
		TreeSet<DeadlineTask> deadlines = _taskStore.getCompDeadlineTasks();
		Iterator<DeadlineTask> deadlineIter = deadlines.iterator();
		
		while (todoIter.hasNext()) {
			tasks.add(todoIter.next());
		}
		
		while (eventIter.hasNext()) {
			tasks.add(eventIter.next());
		}
		
		while (deadlineIter.hasNext()) {
			tasks.add(deadlineIter.next());
		}
		
		return tasks;
	}

	public ArrayList<Task<?>> getAllUncompletedTasks() {

		ArrayList<Task<?>> result = new ArrayList<Task<?>>();

		result.addAll(getTodos());
		result.addAll(getEvents());
		result.addAll(getDeadlines());

		return result;
	} 
	
	private ArrayList<TodoTask> getTodos() {
		// TODO clarify getAllTask from Joanne. For now, just use
		// stub methods.
		// Waiting for TreeMap edition.
		TreeSet<TodoTask> todoSet = _taskStore.getTodoTasks();
		Iterator<TodoTask> todoIter = todoSet.iterator();

		ArrayList<TodoTask> todos = new ArrayList<TodoTask>();

		while (todoIter.hasNext()) {
			todos.add(todoIter.next());
		}
		return todos;
	}

	private ArrayList<EventTask> getEvents() {
		// TODO clarify getAllTask from Joanne. For now, just use
		// stub methods.
		// Waiting for TreeMap edition.
		TreeSet<EventTask> eventSet = _taskStore.getEventTasks();
		Iterator<EventTask> eventIter = eventSet.iterator();

		ArrayList<EventTask> events = new ArrayList<EventTask>();

		while (eventIter.hasNext()) {
			events.add(eventIter.next());
		}
		return events;
	}

	private ArrayList<DeadlineTask> getDeadlines() {
		// TODO clarify getAllTask from Joanne. For now, just use
		// stub methods.
		TreeSet<DeadlineTask> deadlinesSet = _taskStore.getDeadlineTasks();
		Iterator<DeadlineTask> deadlinesIter = deadlinesSet.iterator();

		ArrayList<DeadlineTask> deadlines = new ArrayList<DeadlineTask>();

		while (deadlinesIter.hasNext()) {
			deadlines.add(deadlinesIter.next());
		}
		return deadlines;
	}

	/**
	 * Search for tasks based on its keywords.
	 * 
	 * @param param
	 *          a SearchParameter object that represents the keywords used
	 * @return An arraylist of task that satisfied the keywords. Empty if there's
	 *         none.
	 */
	public ArrayList<Task<?>> searchTasks(SearchParameter param) {
		return null;
	}

}
