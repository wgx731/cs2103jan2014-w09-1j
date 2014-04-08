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
import java.util.Collections;
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

//@author A0099151B
public class TaskFilterManager {

    private static final boolean IS_END = false;
    private static final boolean IS_START = true;

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final int ONE_HOUR = ONE_MINUTE * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;

    private static final int HOUR = 0;
    private static final int MIN = 1;
    private static final int SEC = 2;
    private static final int START_TIME[] = { 0, 0, 0 };
    private static final int END_TIME[] = { 23, 59, 59 };
    private static final int START = 0;
    private static final int END = 1;

    private TaskDataManager _taskStore;

    private static Logger logger = LogManager.getLogger(TaskFilterManager.class
            .getName());

    public TaskFilterManager(TaskDataManager taskStore) {
        _taskStore = taskStore;
    }

    /**
     * Filter the tasks based on its criteria
     * 
     * @param param a FilterParameter object that represents the criteria
     * @return An arraylist of tasks that satisfied the task. Empty if there's
     * none
     */
    public HashMap<TaskType, ArrayList<Task<?>>> filterTask(
            FilterParameter filters) {

        // GuardClause
        assert (filters != null);
        logger.info("Filtering tasks with " + filters.getFilters());

        HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();

        // All tasks are already sorted. This is added for user's brevity
        filters.remove(FilterType.PRIORITY);

        // Based on the task status
        if (filters.contains(FilterType.COMPLETE)) {

            filters.remove(FilterType.COMPLETE);
            result = filterCompletedTasks(filters);

        } else if (filters.contains(FilterType.COMPLETE_PLUS)) {

            filters.remove(FilterType.COMPLETE_PLUS);
            result = combineCompUncompTasks(filters);

        } else {

            if (filters.hasNoTaskTypes()) {
                filters = fillNoTasksFilter(filters);
            }

            result = filterUncompletedTasks(filters);

        }

        return result;

    }

    private HashMap<TaskType, ArrayList<Task<?>>> combineCompUncompTasks(
            FilterParameter filters) {

        HashMap<TaskType, ArrayList<Task<?>>> completedTasks;
        HashMap<TaskType, ArrayList<Task<?>>> uncompletedTasks;
        HashMap<TaskType, ArrayList<Task<?>>> result;
        completedTasks = filterCompletedTasks(filters);
        uncompletedTasks = filterUncompletedTasks(filters);
        result = new HashMap<TaskType, ArrayList<Task<?>>>();

        for (TaskType type : uncompletedTasks.keySet()) {
            ArrayList<Task<?>> currentTasks = uncompletedTasks.get(type);
            currentTasks.addAll(completedTasks.get(type));
            //Collections.sort(currentTasks, new GenericTaskComparator);
            result.put(type, currentTasks);
        }

        return result;
    }

    private HashMap<TaskType, ArrayList<Task<?>>> filterUncompletedTasks(
            FilterParameter filters) {

        HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();
        ArrayList<Task<?>> neededTasks;

        if (filters.hasNoTaskTypes()) {
            filters = fillNoTasksFilter(filters);
        }

        if (filters.contains(FilterType.DEADLINE)) {
            logger.info("Getting uncompleted deadlines");
            neededTasks = getTasks(_taskStore.getUncompletedDeadlineTasks());
            result.put(TaskType.DEADLINE, neededTasks);
        }

        if (filters.contains(FilterType.TODO)) {
            logger.info("Getting uncompleted todos");
            neededTasks = getTasks(_taskStore.getUncompletedTodoTasks());
            result.put(TaskType.TODO, neededTasks);
        }

        if (filters.contains(FilterType.EVENT)) {
            logger.info("Getting uncompleted events");
            neededTasks = getTasks(_taskStore.getUncompletedEventTasks());
            result.put(TaskType.EVENT, neededTasks);
        }

        if (hasDateRange(filters)) {
            // Note that the individual type will mix up
            logger.info("Filtering uncompleted tasks by date range");
            result = filterResultsByDate(TaskType.DEADLINE, result, filters);
            result = filterResultsByDate(TaskType.EVENT, result, filters);
        }

        return result;
    }

    /**
     * 
     * Filter the result further by filter's date range. If the type does not
     * exist in the result, nothing will change
     * 
     * @param currType
     * @param result
     * @param filters
     * @return
     */
    private HashMap<TaskType, ArrayList<Task<?>>> filterResultsByDate(
            TaskType currType, HashMap<TaskType, ArrayList<Task<?>>> result,
            FilterParameter filters) {

        assert (result != null);
        assert (filters != null);

        ArrayList<Task<?>> filteredResult;
        ArrayList<Task<?>> timedTasks;

        logger.info("Checking to see it contains " + currType);
        if (result.containsKey(currType)) {
            logger.info("Found " + currType + " filtering date.");
            timedTasks = result.get(currType);
            filteredResult = filterByDate(timedTasks, filters);
            result.put(currType, filteredResult);
        }

        return result;
    }

    /**
     * Filter each task by its date.
     * 
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
        boolean hasStartTime = filters.hasStartTime();
        boolean hasEndTime = filters.hasEndTime();

        if (!hasStartTime) {
            logger.info("Writing date to appropriate format for START: " + start);
            start = sanitiseDate(start, IS_START);
        }

        if (!hasEndTime) {
            logger.info("Writing date to appropriate format for END: " + end);
            end = sanitiseDate(end, IS_END);
        }
        logger.info("Date returned START: " + start + "and END: " + end);

        // Only EventTask / DeadlineTask will be checked
        for (Task<?> task : result) {
            assert (!(task instanceof TodoTask));
            logger.info("START: " + start + " END: " + end);
            logger.info(task);
            if (isInDateRange(task, start, end)) {
                logger.info("Within range");
                filteredResult.add(task);
            } else {
                logger.info("Not within range");
            }
        }

        return filteredResult;
    }

    /**
     * Check to see whether is it in date range
     * 
     * @param task
     * @param start
     * @param end
     * @param hasTime
     * @return
     */
    private boolean isInDateRange(Task<?> task, Date start, Date end) {

        // Guard clause
        assert (task instanceof EventTask || task instanceof DeadlineTask);
        assert (start != null || end != null);

        Date[] targetDate = new Date[2];

        if (task instanceof EventTask) {
            targetDate[START] = ((EventTask) task).getStartTime();
            targetDate[END] = ((EventTask) task).getEndTime();
        } else {
            targetDate[START] = targetDate[END] = ((DeadlineTask) task)
                    .getEndTime();
        }

        if (start != null && end != null) {

            logger.info("StartTargetDate: " + targetDate[START].toString() +
                    "\n" +
                    "EndTargetDate: " +
                    targetDate[END].toString() +
                    "\n" +
                    "Start: " +
                    start.toString() +
                    "\n" +
                    "End: " +
                    end.toString());

            return !(end.before(targetDate[START]) || start
                    .after(targetDate[END]));

        } else if (start != null) {
            return isTargetAfterOrEqual(targetDate[END], start);
        } else {
            return isTargetBeforeOrEqual(targetDate[START], end);
        }
    }

    private boolean isTargetBeforeOrEqual(Date targetDate, Date end) {
        return targetDate.before(end) || targetDate.equals(end);
    }

    private boolean isTargetAfterOrEqual(Date targetDate, Date start) {
        return targetDate.after(start) || targetDate.equals(start);
    }

    /**
     * Sanitise the date based on its start or end.
     * 
     * @param date The date in question
     * @param isStart Is it a start date or end
     * @return If it's start, set to 23:59:59 of yesterday's start date If it's
     * end, set to 23:59 of end date.
     */
    private Date sanitiseDate(Date date, boolean isStart) {

        // Guard clause
        if (date == null) {
            return null;
        }

        Calendar currDate = Calendar.getInstance();
        currDate.setTime(date);

        if (isStart) {
            currDate.set(currDate.get(Calendar.YEAR),
                    currDate.get(Calendar.MONTH),
                    currDate.get(Calendar.DATE), START_TIME[HOUR],
                    START_TIME[MIN], START_TIME[SEC]);
        } else {
            currDate.set(currDate.get(Calendar.YEAR),
                    currDate.get(Calendar.MONTH),
                    currDate.get(Calendar.DATE), END_TIME[HOUR],
                    END_TIME[MIN], END_TIME[SEC]);
        }

        return currDate.getTime();
    }

    /**
     * Check to see if there is date range
     * 
     * @param filters
     * @return
     */
    private boolean hasDateRange(FilterParameter filters) {
        return filters.contains(FilterType.START) || filters
                .contains(FilterType.END);
    }

    private HashMap<TaskType, ArrayList<Task<?>>> filterCompletedTasks(
            FilterParameter filters) {

        HashMap<TaskType, ArrayList<Task<?>>> result = new HashMap<TaskType, ArrayList<Task<?>>>();
        ArrayList<Task<?>> neededTasks;

        if (filters.hasNoTaskTypes()) {
            filters = fillNoTasksFilter(filters);
        }

        if (filters.contains(FilterType.DEADLINE)) {
            logger.info("Getting completed deadlines");
            neededTasks = getTasks(_taskStore.getCompletedDeadlineTasks());
            result.put(TaskType.DEADLINE, neededTasks);
        }

        if (filters.contains(FilterType.TODO)) {
            logger.info("Getting completed todos");
            neededTasks = getTasks(_taskStore.getCompletedTodoTasks());
            result.put(TaskType.TODO, neededTasks);
        }

        if (filters.contains(FilterType.EVENT)) {
            logger.info("Getting completed events");
            neededTasks = getTasks(_taskStore.getCompletedEventTasks());
            result.put(TaskType.EVENT, neededTasks);
        }

        if (hasDateRange(filters)) {
            // Note that the individual type will mix up
            logger.info("Filtering completed tasks by date range");
            result = filterResultsByDate(TaskType.DEADLINE, result, filters);
            result = filterResultsByDate(TaskType.EVENT, result, filters);
        }

        return result;

    }

    /**
     * Convert the Tasks TreeSet into an arraylist. Note: All tasks set must be
     * under the same superclass and iterable/has a way to iterate its elements.
     * 
     * @param taskSet The set of specific task type
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
     * 
     * @return a few filter that contains every tasktype filters
     */
    private FilterParameter fillNoTasksFilter(FilterParameter existingFilter) {

        logger.info("Add default filter types: deadlines, events and todos");

        ArrayList<FilterType> newFilters = existingFilter.getFilters();

        newFilters.add(FilterType.DEADLINE);
        newFilters.add(FilterType.EVENT);
        newFilters.add(FilterType.TODO);

        return existingFilter;
    }

    /**
     * Search for tasks based on its keywords.
     * 
     * @param param a SearchParameter object that represents the keywords used
     * @return An arraylist of task that satisfied the keywords. Empty if
     * there's none.
     */
    public HashMap<TaskType, ArrayList<Task<?>>> searchTasks(
            SearchParameter param) {

        ArrayList<String> keywords = param.getKeywords();
        HashMap<TaskType, ArrayList<Task<?>>> result;
        result = new HashMap<TaskType, ArrayList<Task<?>>>();

        // Guard clause
        assert (param != null);
        if (keywords.isEmpty()) {
            return result;
        }

        logger.info("Getting task set");

        FilterParameter allTypes = fillNoTasksFilter(new FilterParameter());
        HashMap<TaskType, ArrayList<Task<?>>> uncompletedTasks;
        uncompletedTasks = filterUncompletedTasks(allTypes);

        for (TaskType type : uncompletedTasks.keySet()) {

            logger.info("Searching for " + param.getKeywords());

            ArrayList<Task<?>> specificResult = new ArrayList<Task<?>>();
            for (Task<?> task : uncompletedTasks.get(type)) {

                logger.info("Searching by " + type + " in " + task);
                if (searchKeywords(task, keywords)) {
                    logger.info("Found");
                    specificResult.add(task);
                } else {
                    logger.info("Not found");
                }
            }
            logger.info("Finished with " + type);
            result.put(type, specificResult);

        }

        return result;
    }

    private boolean searchKeywords(Task<?> task, ArrayList<String> keywords) {

        String description = task.getDescription().toLowerCase();

        for (String keyword : keywords) {
            if (description.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
