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
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
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
    
    private static final int HOUR = 0;
    private static final int MIN = 0;
    private static final int SEC = 0;
    private static final int START_TIME[] = {23, 59, 59};
    private static final int END_TIME[] = {0,0,0};
    
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
	public HashMap<TaskType, ArrayList<Task<?>>> filterTask(FilterParameter filters) {
		
		// GuardClause
		assert(filters.getFilters()!=null);
		
		HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();
		
		//All tasks are already sorted. This is added for user's brevity 
		filters.remove(FilterType.PRIORITY);
		
		//Based on the task status
		if (filters.contains(FilterType.COMPLETE)) {
			
			filters.remove(FilterType.COMPLETE);
			result = filterCompletedTasks(filters);
			
		} else if(filters.contains(FilterType.COMPLETE_PLUS)) {
			
			filters.remove(FilterType.COMPLETE_PLUS);
			result = combineCompUncompTasks(filters);
			
		} else {
			
			if(filters.isEmpty()) {
				filters = fillEmptyFilter();
			}
			
			result = filterUncompletedTasks(filters);
			
		}
		
		return result;
		
	}

    private HashMap<TaskType, ArrayList<Task<?>>> combineCompUncompTasks(FilterParameter filters) {
        
        HashMap<TaskType, ArrayList<Task<?>>> completedTasks;
        HashMap<TaskType, ArrayList<Task<?>>> uncompletedTasks;
        HashMap<TaskType, ArrayList<Task<?>>> result;
        completedTasks = filterCompletedTasks(filters);
        uncompletedTasks = filterUncompletedTasks(filters);
        result = new HashMap<TaskType, ArrayList<Task<?>>>();
        
        for (TaskType type: uncompletedTasks.keySet()) {
            ArrayList<Task<?>> currentTasks = uncompletedTasks.get(type);
            currentTasks.addAll(completedTasks.get(type));
            result.put(type, currentTasks);
        }
        
        
        return result;
    }

    private HashMap<TaskType, ArrayList<Task<?>>> filterUncompletedTasks(FilterParameter filters) {
		
        HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();
		ArrayList<Task<?>> neededTasks;
		
		if(filters.isEmpty()) {
			filters = fillEmptyFilter();
		}
		
		if (filters.contains(FilterType.DEADLINE)) {
			neededTasks = getTasks(_taskStore.getUncompletedDeadlineTasks());
			result.put(TaskType.DEADLINE, neededTasks);
		}

		if (filters.contains(FilterType.TODO)) {
			neededTasks = getTasks(_taskStore.getUncompletedTodoTasks());
			result.put(TaskType.TODO, neededTasks);
		}

		if (filters.contains(FilterType.EVENT)) {
			neededTasks = getTasks(_taskStore.getUncompletedEventTasks());
			result.put(TaskType.EVENT, neededTasks);
		}
		
		if(hasDateRange(filters)) {
		    //Note that the individual type will mix up
		    result = filterResultsByDate(TaskType.DEADLINE, result, filters);
		    result = filterResultsByDate(TaskType.EVENT, result, filters);  
		}
		
		return result;
	}
	
    
    /**
     * @author Du Zhiyuan
     * 
     * Filter the result further by filter's date range. 
     * If the type does not exist in the result, nothing will change
     * 
     * @param currType
     * @param result
     * @param filters
     * @return
     */
	private HashMap<TaskType, ArrayList<Task<?>>> filterResultsByDate(TaskType currType, 
                                                                    HashMap<TaskType, ArrayList<Task<?>>> result,
                                                                    FilterParameter filters) {
	    
	    assert(result!=null);
	    assert(filters!=null);
	    
	    ArrayList<Task<?>> filteredResult;
	    ArrayList<Task<?>> timedTasks;
	    
	    if (result.containsKey(currType)) {
	        timedTasks = result.get(currType);
	        filteredResult = filterByDate(timedTasks, filters);
	        result.put(currType, filteredResult);
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
	        assert(!(task instanceof TodoTask));
	        if(isInDateRange(task, start, end, hasTime)) {
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
     * The date in questionexit
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

    private HashMap<TaskType, ArrayList<Task<?>>> filterCompletedTasks(FilterParameter filters) {
		
        HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();
		ArrayList<Task<?>> neededTasks;
		
		if(filters.isEmpty()) {
			filters = fillEmptyFilter();
		}
		
        if (filters.contains(FilterType.DEADLINE)) {
            neededTasks = getTasks(_taskStore.getCompletedDeadlineTasks());
            result.put(TaskType.DEADLINE, neededTasks);
        }

        if (filters.contains(FilterType.TODO)) {
            neededTasks = getTasks(_taskStore.getCompletedTodoTasks());
            result.put(TaskType.TODO, neededTasks);
        }

        if (filters.contains(FilterType.EVENT)) {
            neededTasks = getTasks(_taskStore.getCompletedEventTasks());
            result.put(TaskType.EVENT, neededTasks);
        }
		
        if(hasDateRange(filters)) {
            //Note that the individual type will mix up
            result = filterResultsByDate(TaskType.DEADLINE, result, filters);
            result = filterResultsByDate(TaskType.EVENT, result, filters);  
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
	public HashMap<TaskType, ArrayList<Task<?>>> searchTasks(SearchParameter param) {
		
		ArrayList<String> keywords = param.getKeywords();
		HashMap<TaskType, ArrayList<Task<?>>> result; 	
		result = new HashMap<TaskType, ArrayList<Task<?>>>();
		
		//Guard clause
		assert(param!=null);
		if(keywords.isEmpty()) {
			return result;
		}
		
		FilterParameter allTypes = fillEmptyFilter();
		HashMap<TaskType, ArrayList<Task<?>>> uncompletedTasks;
		uncompletedTasks = filterUncompletedTasks(allTypes);
		
		for(TaskType type: uncompletedTasks.keySet()){
		    
		    ArrayList<Task<?>> specificResult = new ArrayList<Task<?>>();
    		for (Task<?> task: uncompletedTasks.get(type)) {
    			if (searchKeywords(task, keywords)) {
    			    specificResult.add(task);
    			}
    		}
    		result.put(type, specificResult);
    		
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

}
