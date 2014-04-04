package sg.edu.nus.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TimePair;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;

public class TaskDataManagerTest {
    // TODO: check if date modify changes.
    // TODO: add test cases for delete completed

    @Test
    public void testAddTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* Basic add */
        // Partition: All parameters except _priority specified
        assertEquals("Adding To-do task.", new TodoTask("TodoTask1"),
                tdmTest.addTask(new DataParameter("TodoTask1", 'M', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask1",
                new Date(currDateMilliSec)), tdmTest.addTask(new DataParameter(
                "DeadlineTask1", 'M', null, new Date(currDateMilliSec), null,
                TaskType.DEADLINE, 123)));
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask1",
                new Date(currDateMilliSec), new Date(currDateMilliSec)),
                tdmTest.addTask(new DataParameter("EventTask1", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        // Partition: All parameters specified
        assertEquals("Adding To-do task.", new TodoTask("TodoTask2", 'H'),
                tdmTest.addTask(new DataParameter("TodoTask2", 'H', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(2, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask2",
                new Date(currDateMilliSec), 'M'),
                tdmTest.addTask(new DataParameter("DeadlineTask2", 'M', null,
                        new Date(currDateMilliSec), null, TaskType.DEADLINE,
                        123)));
        assertEquals(2, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask2",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.addTask(new DataParameter("EventTask2", 'H', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(2, tdmTest.getUncompletedEventTasks().size());

        /* Task added is exactly the same as a task that already exists. */
        // Partition: Task does not exist
        assertNull(tdmTest.addTask(new DataParameter("TodoTask2", 'H', null,
                null, null, TaskType.TODO, 123)));

        tdmTest.resetTrees();

        /* Adding a recurring DeadlineTask */
        // Partition: all parameters added correctly, recur every 2 weeks for 4
        // months
        Calendar calendar_1 = Calendar.getInstance();
        calendar_1.set(2014, 1, 1);
        Date firstDeadline_1 = calendar_1.getTime();

        DeadlineTask expectedDeadlineTask_1 = new DeadlineTask(
                "RecurDeadlineTask every 2 weeks, for 4 months.",
                firstDeadline_1, 'M');
        expectedDeadlineTask_1.setTag("RECUR_0");

        int currMth_1 = calendar_1.get(Calendar.MONTH);
        calendar_1.set(Calendar.MONTH, currMth_1 + 4);

        try {
            assertEquals(
                    "Adding Deadline Task that reccurs every fortnight for 4 months.",
                    expectedDeadlineTask_1, tdmTest.addTask(new DataParameter(
                            "RecurDeadlineTask every 2 weeks, for 4 months.",
                            'M', null, firstDeadline_1, null,
                            TaskType.DEADLINE, 123, "RECUR", calendar_1
                                    .getTime(), "WEEK", 2, null)));
            assertEquals(9, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(9, tdmTest.getRecurringTasks().get("RECUR_0").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: all parameters added correctly, recur every month forever
        Calendar calendar_2 = Calendar.getInstance();
        calendar_2.set(2014, 9, 16);
        Date firstDeadline_2 = calendar_2.getTime();

        DeadlineTask expectedDeadlineTask_2 = new DeadlineTask(
                "RecurDeadlineTask every month, forever.", firstDeadline_2, 'M');
        expectedDeadlineTask_2.setTag("RECUR_1");

        try {

            assertEquals(
                    "Adding Deadline Task that reccurs every month forever.",
                    expectedDeadlineTask_2, tdmTest.addTask(new DataParameter(
                            "RecurDeadlineTask every month, forever.", 'M',
                            null, firstDeadline_2, null, TaskType.DEADLINE,
                            123, "RECUR", null, "MONTH", 1, null)));
            assertEquals(12, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(3, tdmTest.getRecurringTasks().get("RECUR_1").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: some wrong parameters added for Recurring Deadlines
        try {
            assertNull("Adding a recurring DeadlineTask wrongly.",
                    tdmTest.addTask(new DataParameter(
                            "RecurDeadlineTask every month, forever.", 'M',
                            null, null, null, TaskType.DEADLINE, 123, "RECUR",
                            null, "MONTH", 1, null)));
            assertNull("Adding a recurring DeadlineTask wrongly.",
                    tdmTest.addTask(new DataParameter(
                            "RecurDeadlineTask every month, forever.", 'M',
                            null, firstDeadline_2, null, null, 123, "RECUR",
                            null, "MONTH", 1, null)));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        /* Adding a recurring EventTask */
        // Partition: all parameters added correctly, recur every 8 hours for 2
        // weeks
        Calendar calendar_e_1a = Calendar.getInstance();
        calendar_e_1a.set(2014, 9, 16);
        calendar_e_1a.set(Calendar.HOUR_OF_DAY, 14);
        Date firstStartTime_1 = calendar_e_1a.getTime();

        Calendar calendar_e_1b = Calendar.getInstance();
        calendar_e_1b.set(2014, 9, 16);
        calendar_e_1b.set(Calendar.HOUR_OF_DAY, 16);
        Date firstEndTime_1 = calendar_e_1b.getTime();

        calendar_e_1a.add(Calendar.WEEK_OF_YEAR, 2);
        Date recurUntil = calendar_e_1a.getTime();

        EventTask expectedEventTask_1 = new EventTask(
                "RecurEventTask every 8 hours, for 2 weeks.", firstStartTime_1,
                firstEndTime_1, 'H');
        expectedEventTask_1.setTag("RECUR_3");

        try {
            assertEquals(
                    "Adding Event Task that reccurs every 8 hours, for 2 weeks.",
                    expectedEventTask_1, tdmTest.addTask(new DataParameter(
                            "RecurEventTask every 8 hours, for 2 weeks.", 'H',
                            firstStartTime_1, firstEndTime_1, null,
                            TaskType.EVENT, 123, "RECUR", recurUntil, "HOUR",
                            8, null)));
            assertEquals(43, tdmTest.getUncompletedEventTasks().size());
            assertEquals(43, tdmTest.getRecurringTasks().get("RECUR_3").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: all parameters added correctly, recur every month forever
        Calendar calendar_e_2a = Calendar.getInstance();
        calendar_e_2a.set(2014, 9, 16);
        calendar_e_2a.set(Calendar.HOUR_OF_DAY, 14);
        Date firstStartTime_2 = calendar_e_2a.getTime();

        Calendar calendar_e_2b = Calendar.getInstance();
        calendar_e_2b.set(2014, 9, 16);
        calendar_e_2b.set(Calendar.HOUR_OF_DAY, 16);
        Date firstEndTime_2 = calendar_e_2b.getTime();

        EventTask expectedEventTask_2 = new EventTask(
                "RecurEventTask every month, forever.", firstStartTime_2,
                firstEndTime_2, 'H');
        expectedEventTask_2.setTag("RECUR_4");

        try {
            assertEquals(
                    "Adding Event Task that reccurs every month, forever.",
                    expectedEventTask_2, tdmTest.addTask(new DataParameter(
                            "RecurEventTask every month, forever.", 'H',
                            firstStartTime_2, firstEndTime_2, null,
                            TaskType.EVENT, 123, "RECUR", null, "MONTH", 1,
                            null)));
            assertEquals(46, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getRecurringTasks().get("RECUR_4").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: some wrong parameters added for Recurring Events

        // Partition: Events overlap
        // Partition: adding a block to-do/deadline task

        tdmTest.resetTrees();

        /* Adding a block task */
        // TODO: set a limit to how many block tasks can be added at one go

        // Partition: all parameters specified correctly, no overlapping blocks
        Calendar calendarBlockTest1 = Calendar.getInstance();
        calendarBlockTest1.set(2014, 4, 3, 10, 00);
        Date startDateBlockTest1 = calendarBlockTest1.getTime();
        calendarBlockTest1.set(2014, 4, 3, 12, 00);
        Date endDateBlockTest1 = calendarBlockTest1.getTime();
        List<TimePair> timeSlots1 = new ArrayList<TimePair>();

        try {
            DataParameter addParameters1 = new DataParameter(
                    "Blocked Events 1", 'H', startDateBlockTest1,
                    endDateBlockTest1, null, TaskType.EVENT, 9, "BLOCK", null,
                    null, 0, timeSlots1);
            List<Task<?>> expectedTaskList1 = new ArrayList<Task<?>>();

            EventTask expectedBlockEvent1a = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1a.setTag("BLOCK_0");

            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            expectedTaskList1.add(expectedBlockEvent1a);

            calendarBlockTest1.set(2014, 4, 5, 12, 00);
            startDateBlockTest1 = calendarBlockTest1.getTime();
            calendarBlockTest1.set(2014, 4, 5, 14, 00);
            endDateBlockTest1 = calendarBlockTest1.getTime();
            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            EventTask expectedBlockEvent1b = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1b.setTag("BLOCK_0");
            expectedTaskList1.add(expectedBlockEvent1b);

            calendarBlockTest1.set(2014, 4, 6, 12, 00);
            startDateBlockTest1 = calendarBlockTest1.getTime();
            calendarBlockTest1.set(2014, 4, 6, 14, 00);
            endDateBlockTest1 = calendarBlockTest1.getTime();
            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            EventTask expectedBlockEvent1c = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1c.setTag("BLOCK_0");
            expectedTaskList1.add(expectedBlockEvent1c);

            assertEquals(
                    "Adding block task that blocks out 3 days within the week.",
                    expectedBlockEvent1a, tdmTest.addTask(addParameters1));
            assertEquals(3, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getBlockTasks().get("BLOCK_0").size());
            assertEquals(expectedTaskList1,
                    tdmTest.getBlockTasks().get("BLOCK_0"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: overlapping time slots
        // Partition: some wrong parameters
        // Partition: adding a block to-do/deadline task

    }

    @Test
    public void testDeleteTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        Task<?> testDeleteTodo1 = tdmTest.addTask(new DataParameter("Lie down",
                'H', null, null, TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 1));
        Task<?> testDeleteDeadeline1 = tdmTest.addTask(new DataParameter(
                "Lie down", 'H', null, new Date(currDateMilliSec),
                TaskType.DEADLINE, TaskType.DEADLINE, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 1));
        Task<?> testDeleteEvent1 = tdmTest.addTask(new DataParameter(
                "Lie down", 'H', new Date(currDateMilliSec), new Date(
                        currDateMilliSec), TaskType.EVENT, TaskType.EVENT, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 3));

        /* Basic Delete */
        // Partition: Task exist in the file
        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2, testDeleteTodo1)));
        assertEquals(2, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'), tdmTest.deleteTask(new DataParameter(
                null, 'M', null, null, TaskType.DEADLINE, null, 2,
                testDeleteDeadeline1)));
        assertEquals(2, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Event tasks.", new EventTask("Lie down", new Date(
                currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2, testDeleteEvent1)));
        assertEquals(2, tdmTest.getUncompletedEventTasks().size());

        /* deleting a task that does not exist */
        // Partition: Delete Task non-existent
        assertNull("Deleting todo task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2, testDeleteTodo1)));
        assertNull("Deleting deadline task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 2, testDeleteDeadeline1)));
        assertNull("Deleting event task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2, testDeleteEvent1)));

        tdmTest.resetTrees();

        /* deleting a single recurring DeadlineTask */
        Calendar deadlineRecurTest1 = Calendar.getInstance();
        deadlineRecurTest1.set(2014, 3, 1, 18, 00);
        Calendar untilDeadline1 = Calendar.getInstance();
        untilDeadline1.set(2014, 4, 6, 18, 00);

        try {
            DeadlineTask expectedDeadlineRecurTask1 = (DeadlineTask) tdmTest
                    .addTask(new DataParameter("Another assignment..", 'H',
                            null, deadlineRecurTest1.getTime(), null,
                            TaskType.DEADLINE, 21, "RECUR", untilDeadline1
                                    .getTime(), "WEEK", 1, null));

            // deleting the 1st recurring task
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(0), null, null, 0,
                            null, null, false)));
            assertEquals(5, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(5, tdmTest.getRecurringTasks().get("RECUR_0").size());

            // deleting the 3rd recurring task
            deadlineRecurTest1.add(Calendar.WEEK_OF_YEAR, 2);
            expectedDeadlineRecurTask1.setEndTime(deadlineRecurTest1.getTime());
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(1), null, null, 0,
                            null, null, false)));
            assertEquals(4, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(4, tdmTest.getRecurringTasks().get("RECUR_0").size());

            // deleting the 5th recurring task
            deadlineRecurTest1.add(Calendar.WEEK_OF_YEAR, 2);
            expectedDeadlineRecurTask1.setEndTime(deadlineRecurTest1.getTime());
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(2), null, null, 0,
                            null, null, false)));

            // deleting the 2nd recurring task
            deadlineRecurTest1.add(Calendar.WEEK_OF_YEAR, -3);
            expectedDeadlineRecurTask1.setEndTime(deadlineRecurTest1.getTime());
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(0), null, null, 0,
                            null, null, false)));
            assertEquals(2, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(2, tdmTest.getRecurringTasks().get("RECUR_0").size());

            // deleting the 4th recurring task
            deadlineRecurTest1.add(Calendar.WEEK_OF_YEAR, 2);
            expectedDeadlineRecurTask1.setEndTime(deadlineRecurTest1.getTime());
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(0), null, null, 0,
                            null, null, false)));

            // deleting the 5th recurring task
            deadlineRecurTest1.add(Calendar.WEEK_OF_YEAR, 2);
            expectedDeadlineRecurTask1.setEndTime(deadlineRecurTest1.getTime());
            assertEquals("Deleting a single recurring deadlineTask.",
                    expectedDeadlineRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_0").get(0), null, null, 0,
                            null, null, false)));

            // deleting RECUR_0 again (e.g. repeated command)

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* deleting all recurring DeadlineTask */
        Calendar deadlineRecurTest2 = Calendar.getInstance();
        deadlineRecurTest2.set(2014, 3, 1, 18, 00);
        Calendar untilDeadline2 = Calendar.getInstance();
        untilDeadline1.set(2014, 8, 6, 18, 00);

        try {
            DeadlineTask expectedDeadlineRecurTask2 = (DeadlineTask) tdmTest
                    .addTask(new DataParameter("Another homework..", 'H', null,
                            deadlineRecurTest2.getTime(), null,
                            TaskType.DEADLINE, 21, "RECUR", untilDeadline2
                                    .getTime(), "MONTH", 1, null));

            // deleting all the recurring tasks
            assertEquals("Deleting all recurring deadlineTask.",
                    expectedDeadlineRecurTask2,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_1").get(0), null, null, 0,
                            null, null, true)));
            assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(0, tdmTest.getRecurringTasks().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* deleting a single recurring EventTask */
        Calendar startDateRecurTest1 = Calendar.getInstance();
        startDateRecurTest1.set(2014, 3, 1, 18, 00);
        Calendar endDateRecurTest1 = Calendar.getInstance();
        endDateRecurTest1.set(2014, 3, 1, 23, 00);
        Calendar untilEvent1 = Calendar.getInstance();
        untilEvent1.set(2014, 8, 6, 18, 00);

        try {
            EventTask expectedEventRecurTask1 = (EventTask) tdmTest
                    .addTask(new DataParameter("Another tutorial..", 'H',
                            startDateRecurTest1.getTime(), endDateRecurTest1
                                    .getTime(), null, TaskType.EVENT, 21,
                            "RECUR", untilEvent1.getTime(), "WEEK", 2, null));

            // deleting the 3rd recurring event
            startDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 4);
            endDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 4);
            expectedEventRecurTask1.setStartTime(startDateRecurTest1.getTime());
            expectedEventRecurTask1.setEndTime(endDateRecurTest1.getTime());

            assertEquals("Deleting a single recurring EventTask",
                    expectedEventRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_2").get(2), null, null, 0,
                            null, null, false)));
            assertEquals(11, tdmTest.getUncompletedEventTasks().size());
            assertEquals(11, tdmTest.getRecurringTasks().get("RECUR_2").size());

            // deleting the 6th recurring event
            startDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 6);
            endDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 6);
            expectedEventRecurTask1.setStartTime(startDateRecurTest1.getTime());
            expectedEventRecurTask1.setEndTime(endDateRecurTest1.getTime());

            assertEquals("Deleting a single recurring EventTask",
                    expectedEventRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_2").get(4), null, null, 0,
                            null, null, false)));
            assertEquals(10, tdmTest.getUncompletedEventTasks().size());
            assertEquals(10, tdmTest.getRecurringTasks().get("RECUR_2").size());

            // deleting the 12th recurring event
            startDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 12);
            endDateRecurTest1.add(Calendar.WEEK_OF_MONTH, 12);
            expectedEventRecurTask1.setStartTime(startDateRecurTest1.getTime());
            expectedEventRecurTask1.setEndTime(endDateRecurTest1.getTime());

            assertEquals("Deleting a single recurring EventTask",
                    expectedEventRecurTask1,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_2").get(9), null, null, 0,
                            null, null, false)));
            assertEquals(9, tdmTest.getUncompletedEventTasks().size());
            assertEquals(9, tdmTest.getRecurringTasks().get("RECUR_2").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* deleting all recurring EventTask */
        Calendar startDateRecurTest2 = Calendar.getInstance();
        startDateRecurTest2.set(2014, 3, 1, 18, 00);
        Calendar endDateRecurTest2 = Calendar.getInstance();
        endDateRecurTest2.set(2014, 3, 1, 23, 00);
        Calendar untilEvent2 = Calendar.getInstance();
        untilEvent2.set(2014, 8, 6, 18, 00);

        try {
            EventTask expectedEventRecurTask2 = (EventTask) tdmTest
                    .addTask(new DataParameter("Another tutorial..", 'H',
                            startDateRecurTest2.getTime(), endDateRecurTest2
                                    .getTime(), null, TaskType.EVENT, 21,
                            "RECUR", untilEvent2.getTime(), "MONTH", 1, null));

            // deleting all the recurring tasks
            assertEquals("Deleting all recurring EventTask.",
                    expectedEventRecurTask2,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getRecurringTasks()
                                    .get("RECUR_3").get(0), null, null, 0,
                            null, null, true)));
            assertEquals(9, tdmTest.getUncompletedEventTasks().size());
            assertEquals(1, tdmTest.getRecurringTasks().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* deleting a single block Task */
        Calendar calendarBlockTest1 = Calendar.getInstance();
        calendarBlockTest1.set(2014, 4, 3, 10, 00);
        Date startDateBlockTest1 = calendarBlockTest1.getTime();
        calendarBlockTest1.set(2014, 4, 3, 12, 00);
        Date endDateBlockTest1 = calendarBlockTest1.getTime();
        List<TimePair> timeSlots1 = new ArrayList<TimePair>();

        try {
            DataParameter addParameters1 = new DataParameter(
                    "Blocked Events 1", 'H', startDateBlockTest1,
                    endDateBlockTest1, null, TaskType.EVENT, 9, "BLOCK", null,
                    null, 0, timeSlots1);
            List<Task<?>> expectedTaskList1 = new ArrayList<Task<?>>();

            EventTask expectedBlockEvent1a = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1a.setTag("BLOCK_0");

            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            expectedTaskList1.add(expectedBlockEvent1a);

            calendarBlockTest1.set(2014, 4, 5, 12, 00);
            startDateBlockTest1 = calendarBlockTest1.getTime();
            calendarBlockTest1.set(2014, 4, 5, 14, 00);
            endDateBlockTest1 = calendarBlockTest1.getTime();
            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            EventTask expectedBlockEvent1b = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1b.setTag("BLOCK_0");
            expectedTaskList1.add(expectedBlockEvent1b);

            calendarBlockTest1.set(2014, 4, 6, 12, 00);
            startDateBlockTest1 = calendarBlockTest1.getTime();
            calendarBlockTest1.set(2014, 4, 6, 14, 00);
            endDateBlockTest1 = calendarBlockTest1.getTime();
            timeSlots1
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));
            EventTask expectedBlockEvent1c = new EventTask("Blocked Events 1",
                    startDateBlockTest1, endDateBlockTest1, 'H');
            expectedBlockEvent1c.setTag("BLOCK_0");
            expectedTaskList1.add(expectedBlockEvent1c);

            tdmTest.addTask(addParameters1);

            // delete the 1st blocked event
            assertEquals("Deleting a single block event", expectedBlockEvent1a,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getBlockTasks()
                                    .get("BLOCK_0").get(0), null, null, 0,
                            null, null, false)));
            assertEquals(11, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getBlockTasks().get("BLOCK_0").size());
            assertEquals(1, tdmTest.getBlockTasks().size());

            // delete the 3rd blocked event
            assertEquals("Deleting a single block event", expectedBlockEvent1c,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getBlockTasks()
                                    .get("BLOCK_0").get(1), null, null, 0,
                            null, null, false)));
            assertEquals(10, tdmTest.getUncompletedEventTasks().size());
            assertEquals(1, tdmTest.getBlockTasks().get("BLOCK_0").size());
            assertEquals(1, tdmTest.getBlockTasks().size());

            // delete the 2nd blocked event
            assertEquals("Deleting a single block event", expectedBlockEvent1b,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getBlockTasks()
                                    .get("BLOCK_0").get(0), null, null, 0,
                            null, null, false)));
            assertEquals(9, tdmTest.getUncompletedEventTasks().size());
            assertEquals(0, tdmTest.getBlockTasks().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* deleting all block Tasks */
        Calendar calendarBlockTest2 = Calendar.getInstance();
        calendarBlockTest1.set(2014, 4, 16, 10, 00);
        Date startDateBlockTest2 = calendarBlockTest1.getTime();
        calendarBlockTest1.set(2014, 4, 16, 12, 00);
        Date endDateBlockTest2 = calendarBlockTest1.getTime();
        List<TimePair> timeSlots2 = new ArrayList<TimePair>();

        try {
            DataParameter addParameters2 = new DataParameter(
                    "Blocked Events 2", 'H', startDateBlockTest2,
                    endDateBlockTest2, null, TaskType.EVENT, 9, "BLOCK", null,
                    null, 0, timeSlots2);

            EventTask expectedBlockEvent2 = new EventTask("Blocked Events 2",
                    startDateBlockTest2, endDateBlockTest2, 'H');
            expectedBlockEvent2.setTag("BLOCK_1");

            timeSlots2
                    .add(new TimePair(startDateBlockTest2, endDateBlockTest2));

            calendarBlockTest2.set(2014, 4, 20, 12, 00);
            startDateBlockTest2 = calendarBlockTest2.getTime();
            calendarBlockTest2.set(2014, 4, 520, 14, 00);
            endDateBlockTest2 = calendarBlockTest2.getTime();
            timeSlots2
                    .add(new TimePair(startDateBlockTest1, endDateBlockTest1));

            calendarBlockTest2.set(2014, 4, 6, 12, 00);
            startDateBlockTest2 = calendarBlockTest1.getTime();
            calendarBlockTest2.set(2014, 4, 6, 14, 00);
            endDateBlockTest2 = calendarBlockTest2.getTime();
            timeSlots2
                    .add(new TimePair(startDateBlockTest2, endDateBlockTest2));

            expectedBlockEvent2.setStartTime(startDateBlockTest2);
            expectedBlockEvent2.setEndTime(endDateBlockTest2);

            tdmTest.addTask(addParameters2);

            // delete all blocked events
            assertEquals("Deleting all block events", expectedBlockEvent2,
                    tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                            null, null, 0, tdmTest.getBlockTasks()
                                    .get("BLOCK_1").get(2), null, null, 0,
                            null, null, true)));
            assertEquals(9, tdmTest.getUncompletedEventTasks().size());
            assertEquals(0, tdmTest.getBlockTasks().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testModifyTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* Partition: modify all task parameters but don't change the task type */
        Task<?> testModifyTodo1 = tdmTest
                .addTask(new DataParameter("TodoTask I am slack.", 'L', null,
                        null, null, TaskType.TODO, 1));
        assertEquals("Modify todo description and priority.", new TodoTask(
                "TodoTask I am not slack anymore.", 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "TodoTask I am not slack anymore.", 'H', null, null,
                        TaskType.TODO, TaskType.TODO, 1, testModifyTodo1)));

        Task<?> testModifyDeadline1 = tdmTest.addTask(new DataParameter(
                "Deadline I am slack.", 'L', null, new Date(currDateMilliSec),
                null, TaskType.DEADLINE, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new DeadlineTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', null,
                        new Date(currDateMilliSec - 100000), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1, testModifyDeadline1)));

        Task<?> testModifyEvent1 = tdmTest.addTask(new DataParameter(
                "Event I am slack.", 'L', new Date(currDateMilliSec), new Date(
                        currDateMilliSec), null, TaskType.EVENT, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new EventTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', new Date(
                                currDateMilliSec - 100000), new Date(
                                currDateMilliSec - 100000), TaskType.EVENT,
                        TaskType.EVENT, 1, testModifyEvent1)));

        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        /* Partition: modify task type and all its parameters */
        Task<?> testModifyTaskType1 = tdmTest.addTask(new DataParameter(
                "TodoTask becomes a DeadlineTask.", 'M', null, null, null,
                TaskType.TODO, 123));

        assertEquals("Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask.", new Date(
                        currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 1,
                        testModifyTaskType1)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        Task<?> testModifyTaskType2 = tdmTest.addTask(new DataParameter(
                "TodoTask becomes an EventTask.", 'M', null, null, null,
                TaskType.TODO, 123));
        assertEquals("Modify to-do to event.", new EventTask(
                "TodoTask becomes an EventTask.", new Date(currDateMilliSec),
                new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec),
                        TaskType.TODO, TaskType.EVENT, 1, testModifyTaskType2)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        Task<?> testModifyTaskType3 = tdmTest.addTask(new DataParameter(
                "Deadline task becomes a TodoTask.", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 123));
        assertEquals("Modify deadline to to-do.", new TodoTask(
                "Deadline task becomes a TodoTask."),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 1,
                        testModifyTaskType3)));
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());

        Task<?> testModifyTaskType4 = tdmTest.addTask(new DataParameter(
                "Deadline task becomes an EventTask.", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 1));
        assertEquals("Modify deadline to event", new EventTask(
                "Deadline task becomes an EventTask.", new Date(
                        currDateMilliSec), new Date(currDateMilliSec), 'M'),
                tdmTest.modifyTask(new DataParameter(null, 'M', new Date(
                        currDateMilliSec), null, TaskType.DEADLINE,
                        TaskType.EVENT, 1, testModifyTaskType4)));
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        Task<?> testModifyTaskType5 = tdmTest.addTask(new DataParameter(
                "Event task becomes TodoTask.", 'M',
                new Date(currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 1));
        assertEquals("Modify deadline to event", new TodoTask(
                "Event task becomes TodoTask.", 'M'),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.TODO, 1, testModifyTaskType5)));

        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());

        Task<?> testModifyTaskType6 = tdmTest.addTask(new DataParameter(
                "Event task becomes DeadlineTask.", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 2));
        assertEquals("Modify deadline to event", new TodoTask(
                "Event task becomes DeadlineTask."),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1,
                        testModifyTaskType6)));

        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        tdmTest.resetTrees();

        /* modify a task that does not exist */
        Task<?> testModifyTaskType7 = new TodoTask(
                "Event task becomes TodoTask.", 'M');
        assertNull("Modifying a task that does not exist.",
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1,
                        testModifyTaskType7)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());

    }

    @Test
    public void markCompleted() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        Task<?> testMarkTodo1 = tdmTest.addTask(new DataParameter(
                "Mark this TodoTask complete", 'M', null, null, null,
                TaskType.TODO, 1));

        TodoTask expectedTodo = new TodoTask("Mark this TodoTask complete", 'M');
        expectedTodo.setCompleted(true);
        assertEquals("Mark Todo completed", expectedTodo,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 1, testMarkTodo1)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getCompletedTodoTasks().size());

        Task<?> testMarkDeadline1 = tdmTest.addTask(new DataParameter(
                "Mark this DeadlineTask complete", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 123));

        DeadlineTask expectedDeadline = new DeadlineTask(
                "Mark this DeadlineTask complete", new Date(currDateMilliSec),
                'M');
        expectedDeadline.setCompleted(true);
        assertEquals("Mark Deadline completed", expectedDeadline,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 1, testMarkDeadline1)));
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getCompletedDeadlineTasks().size());

        Task<?> testMarkEvent1 = tdmTest.addTask(new DataParameter(
                "Mark this EventTask complete", 'M',
                new Date(currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 123));

        EventTask expectedEvent = new EventTask("Mark this EventTask complete",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'M');
        expectedEvent.setCompleted(true);
        assertEquals("Mark Event completed", expectedEvent,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 1, testMarkEvent1)));
        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getCompletedEventTasks().size());
    }

}
