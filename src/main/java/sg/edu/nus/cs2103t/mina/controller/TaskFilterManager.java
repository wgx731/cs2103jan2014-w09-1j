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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {

	private TaskDataManager _taskStore;

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
	public ArrayList<Task<?>> filterTask(FilterParameter param) {
		
		// GuardClause
		assert(param==null);

		ArrayList<FilterType> filters = getFilters(param.getFilters());
		ArrayList<Task<?>> result = new ArrayList<Task<?>>();
		
		//All tasks are already sorted. This is added for user's brevity 
		filters.remove(FilterType.PRIORITY);
		
		//Based on the task status
		if (filters.contains(FilterType.COMPLETE)) {
			
			filters.remove(FilterType.COMPLETE);
			result = filterCompletedTasks(filters);
			
		} else if(filters.contains(FilterType.COMPLETE_PLUS)) {
			
			filters.remove(FilterType.COMPLETE_PLUS);
			result.addAll(filterCompletedTasks(filters));
			result.addAll(filterUncompletedTasks(filters));
			
		} else {
			
			if(filters.isEmpty()) {
				filters = fillEmptyFilter();
			}
			
			result = filterUncompletedTasks(filters);
			
		}
		return result;
		
	}
	
	
	/**
	 * Convert the String into their appropriate types 
	 * 
	 * @param filters
	 * @return Arraylist of filter type
	 */
	private ArrayList<FilterType> getFilters(ArrayList<String> rawFilters) {
		
		// TODO Discuss with the team about this.
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		
		for (FilterType filterType: FilterType.values()) {
			if(rawFilters.contains(filterType.getType())) {
				filters.add(filterType);
			}
		}
		return filters;
	}

	private ArrayList<Task<?>> filterUncompletedTasks(ArrayList<FilterType> filters) {
		
		ArrayList<Task<?>> result = new ArrayList<Task<?>>();
		ArrayList<Task<?>> neededTasks;
		
		if(filters.isEmpty()) {
			filters = fillEmptyFilter();
		}
		
		if (filters.contains(FilterType.DEADLINE)) {
			neededTasks = getTasks(_taskStore.getAllDeadlineTasks());
			result.addAll(neededTasks);
		}

		if (filters.contains(FilterType.TODO)) {
			neededTasks = getTasks(_taskStore.getAllTodoTasks());
			result.addAll(neededTasks);
		}

		if (filters.contains(FilterType.EVENT)) {
			neededTasks = getTasks(_taskStore.getAllEventTasks());
			result.addAll(neededTasks);
		}
		
		return result;
	}

	private ArrayList<Task<?>> filterCompletedTasks(ArrayList<FilterType> filters) {
		
		ArrayList<Task<?>> result = new ArrayList<Task<?>>();
		ArrayList<Task<?>> neededTasks;
		
		if(filters.isEmpty()) {
			filters = fillEmptyFilter();
		}
		
		if (filters.contains(FilterType.DEADLINE)) {
			neededTasks = getTasks(_taskStore.getPastDeadlineTasks());
			result.addAll(neededTasks);
		}

		if (filters.contains(FilterType.TODO)) {
			neededTasks = getTasks(_taskStore.getPastTodoTasks());
			result.addAll(neededTasks);
		}

		if (filters.contains(FilterType.EVENT)) {
			neededTasks = getTasks(_taskStore.getPastEventTasks());
			result.addAll(neededTasks);
		}
		
		return result;
		
	}

	//TODO change SortedSet to SortedMap when we implement it.
	/**
	 * Convert the Tasks TreeSet into an arraylist. Note: All tasks 
	 * set must be under the same superclass and iterable/has a way
	 * to iterate its elements.
	 * 
	 * @param taskSet
	 * @return The converted arraylist
	 */
	private ArrayList<Task<?>> getTasks(SortedSet<? extends Task<?>> taskSet) {
		
		Iterator<? extends Task<?>> tasksIter = taskSet.iterator();
		ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();

		while (tasksIter.hasNext()) {
			tasks.add(tasksIter.next());
		}
		return tasks;		
	}
	
	/**
	 * If empty filters, put in every tasktype filters.
	 * @return
	 * 				a few filter that contains every tasktype filters
	 */
	private ArrayList<FilterType> fillEmptyFilter() {
		
		ArrayList<FilterType> newFilters = new ArrayList<FilterType>();
		
		newFilters.add(FilterType.DEADLINE);
		newFilters.add(FilterType.EVENT);
		newFilters.add(FilterType.TODO);
		
		return newFilters;
	}

	
	/**.getType()
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
