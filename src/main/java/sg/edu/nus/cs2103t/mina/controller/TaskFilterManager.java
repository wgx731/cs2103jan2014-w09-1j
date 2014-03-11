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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {

	private static final boolean IS_END = false;

    private static final boolean IS_START = true;

    private static final int FIRST_LETTER = 0;
	
	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final int ONE_HOUR = ONE_MINUTE * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;
    
    public static final int HOUR = 0;
    public static final int MIN = 0;
    public static final int SEC = 0;
    public static final int START_TIME[] = {23, 59, 59};
    public static final int END_TIME[] = {0,0,0};
    
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
	public ArrayList<Task<?>> filterTask(FilterParameter filters) {
		
		// GuardClause
		assert(filters.getFilters()!=null);

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

	private ArrayList<Task<?>> filterUncompletedTasks(FilterParameter filters) {
		
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
		
		if(hasDateRange(filters)) {
		    result = filterByDate(result, filters);
		}
		
		return result;
	}
	
	/**
	 * Filter each task by its date.
	 * @param result 
	 * @param filters 
	 * @param start Starting date
	 * @param end ending date
	 * @return The pruned arraylist
	 */
	private ArrayList<Task<?>> filterByDate(ArrayList<Task<?>> result, 
	                                        FilterParameter filters) {
	    
	    ArrayList<Task<?>> filteredResult = new ArrayList<Task<?>>();
	    Date start = filters.getStart();
	    Date end = filters.getEnd();
	    boolean hasTime = filters.hasTime();
	    
        if (!hasTime) {
            start = sanitiseDate(start, IS_START);
            end = sanitiseDate(end, IS_END);
        }
        
        //Only EventTask / DeadlineTask will be checked
	    for (Task<?> task: result) {
	        if(task instanceof TodoTask || 
	                isInDateRange(task, start, end, hasTime)) {
	            filteredResult.add(task);
	        }
	    }
	    
        return filteredResult;
    }
	
	/**
	 * Check to see whether is it in date range
	 * @param task
	 * @param start
	 * @param end
	 * @param hasTime 
	 * @return
	 */
    private boolean isInDateRange(Task<?> task, 
                                 Date start, Date end, 
                                 boolean hasTime) {
        
        //Guard clause
        assert(task instanceof EventTask || 
                task instanceof DeadlineTask);
        assert(start!=null || end!=null);
        
        Date targetDate;
        if (task instanceof EventTask) {
            targetDate = ((EventTask) task).getStartTime();
        } else {
            targetDate = ((DeadlineTask) task).getEndTime();
        }
        
        if (start!=null && end!=null) {
            return targetDate.after(start) && targetDate.before(end);
        } else if(start!=null) {
            return targetDate.after(start);
        } else {
            return targetDate.before(end);
        } 
    }
    
    /**
     * Sanitise the date based on its start or end.
     *
     * @param date 
     * The date in question
     * @param isStart 
     * Is it a start date or end
     * @return 
     * If it's start, set to 23:59:59 of yesterday's start date
     * If it's end, set to 23:59 of end date. 
     */
    private Date sanitiseDate(Date date, boolean isStart) {
        
        //Guard clause
        if (date==null) {
            return null;
        }
        
        Calendar currDate = Calendar.getInstance();
        currDate.setTime(date);
        
        if (isStart) {
            currDate.set(currDate.get(Calendar.YEAR), 
                        currDate.get(Calendar.MONTH), 
                        currDate.get(Calendar.DATE) - 1, 
                        START_TIME[HOUR], START_TIME[MIN], START_TIME[SEC]);
        } else {
            currDate.set(currDate.get(Calendar.YEAR), 
                    currDate.get(Calendar.MONTH), 
                    currDate.get(Calendar.DATE) + 1, 
                    END_TIME[HOUR], END_TIME[MIN], END_TIME[SEC]);            
        }
        
        return currDate.getTime();
    }

    /**
	 * Check to see if there is date range
	 * @param filters
	 * @return
	 */
	private boolean hasDateRange(FilterParameter filters) {
        return filters.contains(FilterType.START) || 
               filters.contains(FilterType.END);
    }

    private ArrayList<Task<?>> filterCompletedTasks(FilterParameter filters) {
		
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
	private FilterParameter fillEmptyFilter() {
		
		ArrayList<String> newFilters = new ArrayList<String>();
		
		newFilters.add(FilterType.DEADLINE.getType());
		newFilters.add(FilterType.EVENT.getType());
		newFilters.add(FilterType.TODO.getType());
		
		return new FilterParameter(newFilters);
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
		
		FilterParameter allTypes = fillEmptyFilter();
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
