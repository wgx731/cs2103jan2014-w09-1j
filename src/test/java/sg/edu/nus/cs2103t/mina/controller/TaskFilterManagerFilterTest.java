package sg.edu.nus.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B

public class TaskFilterManagerFilterTest {

    private static final int SEC = 2;
    private static final int MIN = 1;
    private static final int HOUR = 0;
    private static final int WEEK = 7;
    private static final int TODAY = 0;
    private static final boolean NO_TIME = false;
    private static final int END = 1;
    private static final int START = 0;
    private static final int DATE_RANGE_EVENT_EXPECTED_SIZE = 5;
    private static final int EVERYTHING = 0;
    private static final int COMPLETE = 1;
    private static final int UNCOMPLETE = 2;
    private static final boolean HAS_TIME = true;

    private TaskDataManagerStub tdmStub = new TaskDataManagerStub();
    private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
    private static final String CLASS_NAME = TaskFilterManagerFilterTest.class
            .getName();

    /**
     * Test for passing an empty filter parameter Expected: Returned all
     * uncompleted tasks. In this case with the dummy data, everything should be
     * there.
     * 
     */
    @Test
    public void testNoFilter() {
        ArrayList<Task<?>> test = getResult(new FilterType[] {});
        assertTrue("Must have everything!", checkAllUncompletedTasks(test));

    }

    /**
     * Test for displaying deadlines only. Expected: Deadlines only. In this
     * case, all dummy data in deadlines.
     */
    @Test
    public void testDeadlinesOnly() {

        ArrayList<Task<?>> test = getResult(FilterType.DEADLINE);

        SortedSet<DeadlineTask> deadlines = tdmStub
                .getUncompletedDeadlineTasks();
        int numOfDeadlines = deadlines.size();

        assertTrue(
                "Must be all deadlines!",
                numOfDeadlines == test.size() && isTaskExist(test, deadlines,
                        TaskType.DEADLINE));

    }

    /**
     * Test for displaying Todos only. Expected: Todos only. In this case, all
     * dummy data in Todos.
     */
    @Test
    public void testTodosOnly() {

        ArrayList<Task<?>> test = getResult(FilterType.TODO);
        SortedSet<TodoTask> todos = tdmStub.getUncompletedTodoTasks();
        int numOfTodos = todos.size();

        assertTrue(
                "Must be all todos!",
                numOfTodos == test.size() && isTaskExist(test, todos,
                        TaskType.TODO));
    }

    /**
     * Test for displaying events only. Expected: Events only. In this case, all
     * dummy data in Events.
     */
    @Test
    public void testEventsOnly() {

        /* XXX EP: Either uncompleted event only or not at all */
        ArrayList<Task<?>> test = getResult(FilterType.EVENT);
        SortedSet<EventTask> events = tdmStub.getUncompletedEventTasks();
        int numOfEvents = events.size();

        assertTrue(
                "Must be all events!",
                numOfEvents == test.size() && isTaskExist(test, events,
                        TaskType.EVENT));
    }

    /**
     * Test for completed tasks only. Expected: Completed events only.
     */
    @Test
    public void testCompletedOnly() {

        ArrayList<Task<?>> test = getResult(FilterType.COMPLETE);
        ArrayList<Task<?>> allCompleted = getCompletedDummyTasks();

        assertTrue(
                "Tasks must be identical in both cases!",
                test.size() == allCompleted.size() && hasCompletedTasks(test,
                        allCompleted));

    }

    /**
     * Test for completed+ tasks Expected: Everything including completed tasks
     */
    @Test
    public void testCompletePlus() {
        ArrayList<Task<?>> test = getResult(FilterType.COMPLETE_PLUS);
        boolean hasEverything = checkEverything(test);
        assertTrue("Need everything!", hasEverything);
    }

    /**
     * Test for priority. Expected: Tasks are ranked from highest to lowest. In
     * the case of events, the closer the date, the more urgent it is.
     */
    @Test
    public void testPriority() {
        ArrayList<Task<?>> test = getResult(FilterType.PRIORITY);
        assertTrue("Priorities are not sorted!", checkPriority(test));
    }

    /**
     * Test for different combination of filter.
     */
    @Test
    public void testFilterCombination() {

        /* XXX Testing for EP: UNCOMPLETE DEADLINE UNCOMPLETE EVENT */
        /*
         * XXX Testing for EP: in fact, this test tries to cover all possible EP
         * P({UNCOMPLETE, COMPLETE} X {EVENT, DEADLINE, TODO})
         */

        // With deadlines and events
        FilterType[] filters = { FilterType.DEADLINE, FilterType.EVENT };

        ArrayList<Task<?>> test = getResult(filters);
        int actualSize = tdmStub.getUncompletedDeadlineTasks().size() + tdmStub
                .getUncompletedEventTasks().size();

        assertTrue(
                "Contains only uncompleted deadline and events!",
                actualSize == test.size() && checkTwoTaskTypes(test,
                        TaskType.DEADLINE, TaskType.EVENT, UNCOMPLETE));

        // With deadlines and todos
        filters = new FilterType[] { FilterType.DEADLINE, FilterType.TODO };
        test = getResult(filters);
        actualSize = tdmStub.getUncompletedDeadlineTasks().size() + tdmStub
                .getUncompletedTodoTasks().size();
        assertTrue(
                "Contains only uncompleted deadline and todos!",
                actualSize == test.size() && checkTwoTaskTypes(test,
                        TaskType.DEADLINE, TaskType.TODO, UNCOMPLETE));

        // With events and todos
        filters = new FilterType[] { FilterType.EVENT, FilterType.TODO };
        test = getResult(filters);
        actualSize = tdmStub.getUncompletedEventTasks().size() + tdmStub
                .getUncompletedTodoTasks().size();
        assertTrue(
                "Contains only uncompleted todos and events!",
                actualSize == test.size() && checkTwoTaskTypes(test,
                        TaskType.EVENT, TaskType.TODO, UNCOMPLETE));

        // With all events, todos and deadlines
        filters = new FilterType[] { FilterType.DEADLINE, FilterType.TODO,
                FilterType.EVENT };
        test = getResult(filters);
        actualSize = tdmStub.getUncompletedEventTasks().size() + tdmStub
                .getUncompletedTodoTasks().size() +
                tdmStub.getUncompletedDeadlineTasks().size();
        assertTrue("Contains all task!", checkAllUncompletedTasks(test));

        // Completed deadlines and events.
        filters = new FilterType[] { FilterType.COMPLETE, FilterType.EVENT,
                FilterType.DEADLINE };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedEventTasks().size() + tdmStub
                .getCompletedDeadlineTasks().size();
        assertTrue(
                "Contains completed deadlines and events!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.DEADLINE, TaskType.EVENT, COMPLETE));

        // Completed deadlines and todos.
        filters = new FilterType[] { FilterType.COMPLETE, FilterType.TODO,
                FilterType.DEADLINE };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedTodoTasks().size() + tdmStub
                .getCompletedDeadlineTasks().size();
        assertTrue(
                "Contains completed deadlines and todos!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.DEADLINE, TaskType.TODO, COMPLETE));

        // Completed event and todos.
        filters = new FilterType[] { FilterType.COMPLETE, FilterType.TODO,
                FilterType.EVENT };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedTodoTasks().size() + tdmStub
                .getCompletedEventTasks().size();
        assertTrue(
                "Contains completed events and todos!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.EVENT, TaskType.TODO, COMPLETE));

        // Get all completed
        filters = new FilterType[] { FilterType.COMPLETE };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedTodoTasks().size() + tdmStub
                .getCompletedEventTasks().size() +
                tdmStub.getCompletedDeadlineTasks().size();
        assertTrue(
                "Contains all completed tasks",
                test.size() == actualSize && hasCompletedTasks(test,
                        getCompletedDummyTasks()));

        // Both completed and uncompleted event and todos.
        filters = new FilterType[] { FilterType.COMPLETE_PLUS, FilterType.TODO,
                FilterType.EVENT };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedTodoTasks().size() + tdmStub
                .getCompletedEventTasks().size() +
                tdmStub.getUncompletedEventTasks().size() +
                tdmStub.getUncompletedTodoTasks().size();
        assertTrue(
                "Contains all event and todos!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.EVENT, TaskType.TODO, EVERYTHING));

        // Both completed and uncompleted event and deadline.
        filters = new FilterType[] { FilterType.COMPLETE_PLUS,
                FilterType.DEADLINE, FilterType.EVENT };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedDeadlineTasks().size() + tdmStub
                .getCompletedEventTasks().size() +
                tdmStub.getUncompletedEventTasks().size() +
                tdmStub.getUncompletedDeadlineTasks().size();
        assertTrue(
                "Contains all event and deadlines!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.EVENT, TaskType.DEADLINE, EVERYTHING));

        // Both completed and uncompleted todo and deadline.
        filters = new FilterType[] { FilterType.COMPLETE_PLUS,
                FilterType.DEADLINE, FilterType.TODO };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedDeadlineTasks().size() + tdmStub
                .getCompletedTodoTasks().size() +
                tdmStub.getUncompletedTodoTasks().size() +
                tdmStub.getUncompletedDeadlineTasks().size();
        assertTrue(
                "Contains all deadlines and todos!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.TODO, TaskType.DEADLINE, EVERYTHING));

        // No task type filters, only +complete
        filters = new FilterType[] { FilterType.COMPLETE_PLUS };
        test = getResult(filters);
        actualSize = tdmStub.getCompletedDeadlineTasks().size() + tdmStub
                .getCompletedTodoTasks().size() +
                tdmStub.getUncompletedTodoTasks().size() +
                tdmStub.getUncompletedDeadlineTasks().size() +
                tdmStub.getCompletedEventTasks().size() +
                tdmStub.getUncompletedEventTasks().size();

        assertTrue("Contains everything!",
                test.size() == actualSize && checkEverything(test));

        // Testing with priority keyword for single, two and
        // all task type

        // Single task
        filters = new FilterType[] { FilterType.PRIORITY, FilterType.DEADLINE };
        test = getResult(filters);
        Iterator<DeadlineTask> deadlineSet = tdmStub
                .getUncompletedDeadlineTasks().iterator();
        ArrayList<DeadlineTask> deadlines = new ArrayList<DeadlineTask>();

        while (deadlineSet.hasNext()) {
            deadlines.add(deadlineSet.next());
        }

        assertEquals(test, deadlines);

        // Double task
        filters = new FilterType[] { FilterType.PRIORITY, FilterType.DEADLINE,
                FilterType.TODO };
        test = getResult(filters);
        actualSize = tdmStub.getUncompletedTodoTasks().size() + tdmStub
                .getUncompletedDeadlineTasks().size();
        assertTrue(
                "Contains deadlines and todos!",
                test.size() == actualSize && checkTwoTaskTypes(test,
                        TaskType.DEADLINE, TaskType.TODO, UNCOMPLETE));

        // All tasks
        filters = new FilterType[] { FilterType.PRIORITY };
        test = getResult(filters);
        actualSize = tdmStub.getUncompletedEventTasks().size() + tdmStub
                .getUncompletedTodoTasks().size() +
                tdmStub.getUncompletedDeadlineTasks().size();
        assertTrue("Contains all task!", checkAllUncompletedTasks(test));
    }

    /**
     * Test the date filter by range, start to end
     */
    @Test
    public void testDateFilterByRange() {

        tdmStub = new TaskDataManagerStub(TaskDataManagerStub.DATE_RANGE_SEARCH);
        tfmTest = new TaskFilterManager(tdmStub);

        FilterType[] dateFilters = new FilterType[] { FilterType.EVENT };
        Iterator<EventTask> eventsIter = tdmStub.getUncompletedEventTasks()
                .iterator();
        ArrayList<EventTask> events = new ArrayList<EventTask>();

        while (eventsIter.hasNext()) {
            events.add(eventsIter.next());
        }

        Iterator<DeadlineTask> deadlinesIter = tdmStub
                .getUncompletedDeadlineTasks().iterator();
        ArrayList<DeadlineTask> deadlines = new ArrayList<DeadlineTask>();

        while (deadlinesIter.hasNext()) {
            deadlines.add(deadlinesIter.next());
        }
        LogHelper.log(CLASS_NAME, Level.INFO, "Events: " + events +
                "Deadlines: " +
                deadlines);

        // Test dates only. No time (00:00)
        // A week from now.
        LogHelper.log(CLASS_NAME, Level.INFO, "Testing dates with only");
        Date[] dateRange = getRange(TODAY, new int[0], WEEK, new int[0],
                NO_TIME);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Date Range: " + Arrays.toString(dateRange));
        ArrayList<Task<?>> test = getResult(dateFilters, dateRange[START],
                dateRange[END], NO_TIME);

        ArrayList<Task<?>> expected = new ArrayList<Task<?>>();

        for (int i = 0; i < DATE_RANGE_EVENT_EXPECTED_SIZE; i++) {
            expected.add(events.get(i));
        }
        assertEquals(expected, test);

        // Test date with time.
        // A week from now.
        LogHelper.log(CLASS_NAME, Level.INFO, "Testing date with time");
        dateRange = getRange(2, new int[] { 12, 0, 0 }, 5,
                new int[] { 9, 0, 0 }, HAS_TIME);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Date Range: " + Arrays.toString(dateRange));
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);

        expected = new ArrayList<Task<?>>();
        for (int i = 2; i < 5; i++) {
            expected.add(events.get(i));
        }
        assertEquals(expected, test);

        // Test date with start only
        LogHelper.log(CLASS_NAME, Level.INFO, "Testing date with start only");
        dateRange = getRange(TODAY, new int[0], 0, new int[0], NO_TIME);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Date Range: " + Arrays.toString(dateRange));
        test = getResult(dateFilters, dateRange[START], null, HAS_TIME);
        assertEquals(events, test);

        // Test date with end only
        LogHelper.log(CLASS_NAME, Level.INFO, "Testing date with end only");
        dateRange = getRange(2, new int[] { 12, 00, 0 }, 5,
                new int[] { 9, 0, 0 }, HAS_TIME);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Date Range: " + Arrays.toString(dateRange));
        test = getResult(dateFilters, null, dateRange[END], HAS_TIME);

        expected = new ArrayList<Task<?>>();
        for (int i = 0; i < 5; i++) {
            expected.add(events.get(i));
        }
        assertEquals(expected, test);

        LogHelper.log(CLASS_NAME, Level.INFO, "Testing for middle");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting Date range");
        dateRange = getRange(2, new int[] { 12, 00, 0 }, 5,
                new int[] { 9, 0, 0 }, HAS_TIME);
        test = getResult(dateFilters, null, dateRange[END], HAS_TIME);

        expected = new ArrayList<Task<?>>();
        for (int i = 0; i < 5; i++) {
            expected.add(events.get(i));
        }
        assertEquals(expected, test);

        // Checking for overlaps
        // Events only
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Testing overlaps: START < event.start but END > event.start ");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting date range");
        dateRange = getRange(TODAY, new int[] { 0, 0, 0 }, 1, new int[] { 12,
                0, 0 }, HAS_TIME);
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);
        expected = new ArrayList<Task<?>>();
        expected.add(events.get(0));
        assertEquals(expected, test);

        LogHelper.log(CLASS_NAME, Level.INFO,
                "Testing overlaps: START < event.end but END > event.end ");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting date range");
        dateRange = getRange(1, new int[] { 4, 45, 0 }, 0,
                new int[] { 6, 0, 0 }, HAS_TIME);
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);
        expected = new ArrayList<Task<?>>();
        expected.add(events.get(0));
        assertEquals(expected, test);

        LogHelper
                .log(CLASS_NAME, Level.INFO,
                        "Testing strict subset: START > event.start and END < event.end ");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting date range");
        dateRange = getRange(1, new int[] { 4, 45, 0 }, 0,
                new int[] { 5, 0, 0 }, HAS_TIME);
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);
        expected = new ArrayList<Task<?>>();
        expected.add(events.get(0));
        assertEquals(expected, test);

        LogHelper.log(CLASS_NAME, Level.INFO,
                "Testing equality: START = event.start and END > event.end ");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting date range");
        dateRange = getRange(1, new int[] { 3, 45, 0 }, 0, new int[] { 20, 30,
                0 }, HAS_TIME);
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);
        expected = new ArrayList<Task<?>>();
        expected.add(events.get(0));
        assertEquals(expected, test);

        LogHelper.log(CLASS_NAME, Level.INFO,
                "Testing equality: START < event.start and END = event.end ");
        LogHelper.log(CLASS_NAME, Level.INFO, "Getting date range");
        dateRange = getRange(1, new int[] { 2, 15, 0 }, 0,
                new int[] { 5, 45, 0 }, HAS_TIME);
        test = getResult(dateFilters, dateRange[START], dateRange[END],
                HAS_TIME);
        expected = new ArrayList<Task<?>>();
        expected.add(events.get(0));
        assertEquals(expected, test);

        resetTdmTfm();

    }

    @Test
    /**
     * The ArrayList has already been tested.
     * Now, it's just to test whether the keys only
     * cover what the users want.
     * Note we need not do this on Search 
     */
    public void testHashMap() {
        ArrayList<String> newKeywords = new ArrayList<String>();
        FilterParameter param;
        HashMap<TaskType, ArrayList<Task<?>>> result;

        // Test one type
        newKeywords.add("deadline");
        param = new FilterParameter(newKeywords);
        result = tfmTest.filterTask(param);
        assertTrue(
                "Must contain only DEADLINE",
                result.containsKey(TaskType.DEADLINE) && result.keySet().size() == 1);
        newKeywords.clear();

        // Test two type
        newKeywords.add("deadline");
        newKeywords.add("event");
        param = new FilterParameter(newKeywords);
        result = tfmTest.filterTask(param);
        assertTrue(
                "Must contain only DEADLINE and Event",
                result.containsKey(TaskType.DEADLINE) && result
                        .containsKey(TaskType.EVENT) &&
                        result.keySet().size() == 2);
        newKeywords.clear();

        // Test three types
        param = new FilterParameter(newKeywords);
        result = tfmTest.filterTask(param);
        assertTrue(
                "Must contain only DEADLINE and TODO",
                result.containsKey(TaskType.DEADLINE) && result
                        .containsKey(TaskType.EVENT) &&
                        result.containsKey(TaskType.TODO) &&
                        result.keySet().size() == 3);
        newKeywords.clear();
    }

    /*
     * Test the date range
     */
    /**
     * Get the range of date.
     * 
     * @param startNumDay How many days from 'today'
     * @param startMs Start time, an int array of size 3, hour/min/sec in that
     * order
     * @param range How many days from 'start' parameter
     * @param endMs End time, an int array of size 3, hour/min/sec in that order
     * @param hasTime Does it have time?
     * @return Date array of size 2. START and END in that order
     */
    private Date[] getRange(int startNumDay, int[] startMs, int range,
            int[] endMs, boolean hasTime) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date(TaskDataManagerStub.START_TIME));
        startDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        int year = startDate.get(Calendar.YEAR);
        int month = startDate.get(Calendar.MONTH);
        int startDay = startDate.get(Calendar.DAY_OF_MONTH) + startNumDay;
        if (hasTime) {
            startDate.set(year, month, startDay, startMs[HOUR], startMs[MIN],
                    startMs[SEC]);
        } else {
            startDate.set(year, month, startDay, 0, 0, 0);
        }

        LogHelper.log(CLASS_NAME, Level.INFO, "START: " + startDate.getTime());

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        endDate.setTime(startDate.getTime());

        if (hasTime) {
            endDate.set(startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH) + range, endMs[HOUR],
                    endMs[MIN], endMs[SEC]);
        } else {
            endDate.set(startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH) + range, 0, 0, 0);
        }

        LogHelper.log(CLASS_NAME, Level.INFO, "END: " + endDate.getTime());
        return new Date[] { startDate.getTime(), endDate.getTime() };
    }

    private boolean checkTwoTaskTypes(ArrayList<Task<?>> test,
            TaskType firstType, TaskType secondType, int taskStatus) {

        if (test.isEmpty()) {
            return false;
        }

        for (Task<?> task : test) {
            if (!isValidInTaskCombi(task, firstType, secondType, taskStatus)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidInTaskCombi(Task<?> task, TaskType firstType,
            TaskType secondType, int taskStatus) {

        TaskType currType = task.getType();
        boolean isCorrectType = currType == firstType || currType == secondType;

        if (taskStatus == EVERYTHING) {
            return isCorrectType;
        } else {
            return isCorrectStatus(task.isCompleted(), taskStatus) && isCorrectType;
        }
    }

    private boolean isCorrectStatus(boolean isCompleted, int taskStatus) {
        return (isCompleted && taskStatus == COMPLETE) || (!isCompleted && taskStatus == UNCOMPLETE);
    }

    private boolean checkPriority(ArrayList<Task<?>> test) {

        int orderPriority = 0;
        Date prevCreatedEvent = new Date(0);
        Date prevCreatedDeadline = new Date(0);

        if (test.isEmpty()) {
            return false;
        }

        for (Task<?> task : test) {
            // todo task in the order of HHHHHH...MMMMMM...LLLL
            if (task instanceof TodoTask) {
                TodoTask todo = (TodoTask) task;
                orderPriority = checkTodoContiguity(todo, orderPriority);
                if (orderPriority != -1) {
                    continue;
                }
            } else if (task instanceof EventTask) {
                EventTask event = (EventTask) task;
                Date currDate = event.getStartTime();
                if (currDate.after(prevCreatedEvent)) {
                    prevCreatedEvent = currDate;
                    continue;
                }
            } else if (task instanceof DeadlineTask) {
                DeadlineTask deadline = (DeadlineTask) task;
                Date currDate = deadline.getEndTime();
                if (currDate.after(prevCreatedDeadline)) {
                    prevCreatedDeadline = currDate;
                    continue;
                }
            }
            return false;
        }

        return true;
    }

    /**
     * 
     * @param todo
     * @param orderPriority
     * @return
     */
    private int checkTodoContiguity(TodoTask todo, int orderPriority) {

        int currPriority;

        switch (todo.getPriority()) {
            case 'H' :
                currPriority = 0;
                break;
            case 'M' :
                currPriority = 1;
                break;
            case 'L' :
                currPriority = 2;
                break;
            default :
                return -1;
        }

        if (currPriority >= orderPriority) {
            return currPriority;
        } else {
            return -1;
        }

    }

    private boolean checkEverything(ArrayList<Task<?>> test) {

        SortedSet<TodoTask> todos = tdmStub.getUncompletedTodoTasks();
        SortedSet<TodoTask> todosComp = tdmStub.getCompletedTodoTasks();

        SortedSet<EventTask> events = tdmStub.getUncompletedEventTasks();
        SortedSet<EventTask> eventsComp = tdmStub.getCompletedEventTasks();

        SortedSet<DeadlineTask> deadlines = tdmStub
                .getUncompletedDeadlineTasks();
        SortedSet<DeadlineTask> deadlinesComp = tdmStub
                .getCompletedDeadlineTasks();

        int totalTaskSize = deadlinesComp.size() + eventsComp.size() +
                todosComp.size() +
                todos.size() +
                events.size() +
                deadlines.size();

        for (Task<?> task : test) {

            if (task instanceof DeadlineTask && !(deadlinesComp.contains(task) || deadlines
                    .contains(task))) {
                return false;
            }

            if (task instanceof TodoTask && !(todosComp.contains(task) || todos
                    .contains(task))) {
                return false;
            }

            if (task instanceof EventTask && !(eventsComp.contains(task) || events
                    .contains(task))) {
                return false;
            }

        }

        return totalTaskSize == test.size();
    }

    private boolean hasCompletedTasks(ArrayList<Task<?>> test,
            ArrayList<Task<?>> allCompleted) {

        if (test.isEmpty()) {
            return false;
        }

        for (Task<?> t : test) {
            if (!allCompleted.contains(t)) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<Task<?>> getCompletedDummyTasks() {

        SortedSet<EventTask> events = tdmStub.getCompletedEventTasks();
        Iterator<EventTask> eventIter = events.iterator();

        SortedSet<DeadlineTask> deadlines = tdmStub.getCompletedDeadlineTasks();
        Iterator<DeadlineTask> deadlineIter = deadlines.iterator();

        SortedSet<TodoTask> todos = tdmStub.getCompletedTodoTasks();
        Iterator<TodoTask> todosIter = todos.iterator();

        ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();

        while (todosIter.hasNext()) {
            tasks.add(todosIter.next());
        }

        while (eventIter.hasNext()) {
            tasks.add(eventIter.next());
        }

        while (deadlineIter.hasNext()) {
            tasks.add(deadlineIter.next());
        }

        return tasks;
    }

    /**
     * Get the result based on the keywords entered
     * 
     * @param cmd the string of keywords
     * @return An arrayList of tasks.
     */
    private ArrayList<Task<?>> getResult(FilterType[] filters) {
        return getResult(filters, null, null, false);
    }

    private ArrayList<Task<?>> getResult(FilterType cmd) {

        FilterType[] tokens = { cmd };
        return getResult(tokens);

    }

    private ArrayList<Task<?>> getResult(FilterType[] filters, Date start,
            Date end, boolean hasTime) {

        ArrayList<String> keywords = new ArrayList<String>();

        for (int i = 0; i < filters.length; i++) {
            keywords.add(filters[i].getType());
        }

        FilterParameter filter = new FilterParameter(keywords, start, end,
                hasTime);
        HashMap<TaskType, ArrayList<Task<?>>> resultMap = tfmTest
                .filterTask(filter);

        ArrayList<Task<?>> result = new ArrayList<Task<?>>();

        for (TaskType type : resultMap.keySet()) {
            result.addAll(resultMap.get(type));
        }

        return result;
    }

    /**
     * Compare the returned result to dummy data
     * 
     * @param test the arraylist of tasks
     * @param taskTree the dummy data for task
     * @param type the TaskType specified.
     * @return true if everything in the result is in specified tasks
     */
    private boolean isTaskExist(ArrayList<Task<?>> test,
            SortedSet<? extends Task<?>> taskTree, TaskType type) {

        for (Iterator<Task<?>> iterator = test.iterator(); iterator.hasNext();) {
            Task<?> task = iterator.next();
            if (!(isTask(task, type) && taskTree.contains(task))) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    private boolean isTask(Task task, TaskType type) {

        boolean isTask;

        switch (type) {
            case DEADLINE :
                isTask = task instanceof DeadlineTask;
                break;
            case TODO :
                isTask = task instanceof TodoTask;
                break;
            case EVENT :
                isTask = task instanceof EventTask;
                break;
            default :
                isTask = false;
        }

        return isTask;
    }

    /**
     * To compare with tasks for now. The current task compareTo has a bug.
     * 
     * @param task
     * @param todoTasks
     * @return true if they match. False, if they don't
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    private boolean compareTodo(Task task, TreeSet<TodoTask> todoTasks) {

        Iterator<TodoTask> todoIter = todoTasks.iterator();

        while (todoIter.hasNext()) {
            TodoTask todo = todoIter.next();
            if (todo.getDescription().equals(task.getDescription())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkAllUncompletedTasks(ArrayList<Task<?>> test) {

        boolean isExist = true;

        for (Iterator<Task<?>> iterator = test.iterator(); iterator.hasNext();) {
            Task<?> t = iterator.next();
            if (t instanceof TodoTask) {
                isExist = tdmStub.getUncompletedTodoTasks().contains(
                        (TodoTask) t);
            } else if (t instanceof EventTask) {
                isExist = tdmStub.getUncompletedEventTasks().contains(
                        (EventTask) t);
            } else if (t instanceof DeadlineTask) {
                isExist = tdmStub.getUncompletedDeadlineTasks().contains(
                        (DeadlineTask) t);
            }
            if (!isExist) {
                break;
            }
        }
        return isExist;
    }

    @SuppressWarnings("rawtypes")
    public String toStringForTask(Task task) {
        String format = "UUID: %1$s\n" + "Type: %2$s\n"
                + "Description: %3$s\n"
                + "Tags: %4$s\n"
                + "Priority: %5$s\n"
                + "Created: %6$s\n"
                + "Last modified: %7$s\n"
                + "Completed: %8$s\n\n";
        String output = String.format(format, task.getId(), task.getType()
                .getType(), task.getDescription(), task.getTag(), task
                .getPriority(), task.getCreatedTime(),
                task.getLastEditedTime(), task.isCompleted());
        return output;
    }

    public void printEvent(EventTask task) {
        String format = "Start: %1$s\n" + "End: %2$s\n";
        LogHelper.log(CLASS_NAME, Level.INFO, toStringForTask(task));
        LogHelper.log(CLASS_NAME, Level.INFO,
                String.format(format, task.getStartTime(), task.getEndTime()));
    }

    public void printDeadline(DeadlineTask task) {
        String format = "End: %1$s\n";
        LogHelper.log(CLASS_NAME, Level.INFO, toStringForTask(task));
        LogHelper.log(CLASS_NAME, Level.INFO,
                String.format(format, task.getEndTime()));
    }

    public void printTodo(TodoTask task) {
        LogHelper.log(CLASS_NAME, Level.INFO, toStringForTask(task));
    }

    public void resetTdmTfm() {
        tdmStub = new TaskDataManagerStub();
        tfmTest = new TaskFilterManager(tdmStub);
    }

}
