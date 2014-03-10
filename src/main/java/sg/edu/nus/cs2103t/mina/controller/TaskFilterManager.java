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
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {

	private static final int FIRST_LETTER = 0;
	private TaskDataManager _taskStore;

	private static Logger logger = LogManager
			.getLogger(TaskFilterManager.class.getName());
	
	public TaskFilterManager(TaskDataManager taskStore) {
		_taskStore = taskStore;
	}

	/**
	 * Filter the tasks based on its critieria
	 * 
	 * @param param
	 * a FilterParameter object that represents the criteria
	 * @return 
	 * An arraylist of tasks that satisfied the task. 
	 * Empty if there's none
	 */
	public ArrayList<Task<?>> filterTask(FilterParameter param) {
		
		// GuardClause
		assert(param!=null);

		ArrayList<FilterType> filters = param.getFilters();
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

	//TODO change SortedSet to SortedMap when we move to Treemap.
	/**
	 * Convert the Tasks TreeSet into an arraylist. Note: All tasks 
	 * set must be under the same superclass and iterable/has a way
	 * to iterate its elements.
	 * 
	 * @param taskSet 
	 * The set of specific task type
	 * @return 
	 * The converted arraylist
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

	
	/**
	 * Search for tasks based on its keywords.
	 * 
	 * @param param
	 * a SearchParameter object that represents the keywords used
	 * @return 
	 * An arraylist of task that satisfied the keywords. 
	 * Empty if there's none.
	 */
	public ArrayList<Task<?>> searchTasks(SearchParameter param) {
		
		ArrayList<String> keywords = param.getKeywords();
		ArrayList<Task<?>> result = new ArrayList<Task<?>>();		
		
		//Guard clause
		assert(param!=null);
		if(keywords.isEmpty()) {
			return result;
		}
		
		ArrayList<FilterType> allTypes = fillEmptyFilter();
		ArrayList<Task<?>> uncompletedTasks = filterUncompletedTasks(allTypes);
		
		for (Task<?> task: uncompletedTasks) {
			if (searchKeywords(task, keywords)){
				result.add(task);
			}
		}
		
		return result;
	}

	private boolean searchKeywords(Task<?> task, ArrayList<String> keywords) {
		
		String description = task.getDescription().toLowerCase();
		
		for (String keyword: keywords) {
			if (description.contains(keyword.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	private boolean hasKeyword(String description, String keyword) {
		
		String[] tokens = sanitiseTokens(description);
		
		for (int i=0; i<tokens.length; i++) {
			if(tokens[i].equalsIgnoreCase(keyword)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Tokenise and strip any leading punctuation or whitespace.
	 * @param description
	 * @return
	 */
	private String[] sanitiseTokens(String description) {
		
		String[] rawTokens = description.split(" ");
		ArrayList<String> tokens = new ArrayList<String>();
		
		for (int i=0; i<rawTokens.length; i++) {
			int lastLetter = rawTokens.length - 1;
			rawTokens[i] = rawTokens[i].trim();
			if(rawTokens[i].equals("")) {
				continue;
			}
			
			Character first = rawTokens[i].charAt(FIRST_LETTER);
			Character last = rawTokens[i].charAt(lastLetter);
			
			if(!Character.isLetterOrDigit(first)) {
				rawTokens[i] = rawTokens[i].substring(FIRST_LETTER + 1);
				
			}
			
		}
		
		return rawTokens;
	}

}
