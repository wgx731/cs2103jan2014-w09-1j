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

        calendar_1.add(Calendar.MONTH, 4);

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
        assertEquals(3, tdmTest.getUncompletedTodoTasks().size());
        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2, testDeleteTodo1)));
        assertEquals(2, tdmTest.getUncompletedTodoTasks().size());

        assertEquals(3, tdmTest.getUncompletedDeadlineTasks().size());
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

        /*
         * Partition: modify all task parameters but don't change the task type
         */
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

        tdmTest.resetTrees();

        /* Modify a single recurring DeadlineTask */
        Calendar deadlineRecurTest1 = Calendar.getInstance();
        deadlineRecurTest1.set(2014, 3, 1, 21, 59);
        Calendar untilDeadlineRecur1 = Calendar.getInstance();
        untilDeadlineRecur1.set(2014, 4, 6, 21, 59);

        try {
            tdmTest.addTask(new DataParameter("After lecture quiz.", 'H', null,
                    deadlineRecurTest1.getTime(), null, TaskType.DEADLINE, 21,
                    "RECUR", untilDeadlineRecur1.getTime(), "WEEK", 1, null));
            assertEquals(6, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(6, tdmTest.getRecurringTasks().get("RECUR_0").size());
            assertEquals(1, tdmTest.getRecurringTasks().size());

            // TODO: something wrong here
            // modify parameters
            // Calendar expectedDeadlineCal1 = Calendar.getInstance();
            //
            // expectedDeadlineCal1.set(2014, 3, 8, 21, 59);
            // Date expectedDeadlineDate1 = expectedDeadlineCal1.getTime();
            //
            // DeadlineTask expectedRecurDeadline1 = new DeadlineTask(
            // "After lecture quiz (half done).", expectedDeadlineDate1,
            // 'M');
            //
            // assertEquals("Modifying one deadline recurring task.",
            // expectedRecurDeadline1,
            // tdmTest.modifyTask(new DataParameter(
            // "After lecture quiz (half done).", 'M', null, null,
            // TaskType.DEADLINE, TaskType.DEADLINE, 1, tdmTest
            // .getRecurringTasks().get("RECUR_0").get(1),
            // null, null, 0, null, null, false)));
            // assertEquals(6, tdmTest.getUncompletedDeadlineTasks().size());
            // assertEquals(5,
            // tdmTest.getRecurringTasks().get("RECUR_0").size());
            tdmTest.modifyTask(new DataParameter(
                    "After lecture quiz (half done).", 'M', null, null,
                    TaskType.DEADLINE, TaskType.DEADLINE, 1, tdmTest
                            .getRecurringTasks().get("RECUR_0").get(1), null,
                    null, 0, null, null, false));

            // modify deadline task type to event
            Calendar expectedStartCal2 = Calendar.getInstance();
            Calendar expectedEndCal2 = Calendar.getInstance();

            expectedStartCal2.set(2014, 3, 8, 21, 59);
            Date expectedStartDate2 = expectedStartCal2.getTime();
            expectedEndCal2.set(2014, 3, 8, 23, 59);
            Date expectedEndDate2 = expectedEndCal2.getTime();

            EventTask expectedRecurEvent2 = new EventTask(
                    "After lecture quiz (half done).", expectedStartDate2,
                    expectedEndDate2, 'L');

            assertEquals("Modifying one deadline recurring task.",
                    expectedRecurEvent2, tdmTest.modifyTask(new DataParameter(
                            "After lecture quiz (half done).", 'L',
                            expectedStartDate2, expectedEndDate2,
                            TaskType.DEADLINE, TaskType.EVENT, 1, tdmTest
                                    .getRecurringTasks().get("RECUR_0").get(3),
                            null, null, 0, null, null, false)));
            assertEquals(5, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(1, tdmTest.getUncompletedEventTasks().size());
            assertEquals(4, tdmTest.getRecurringTasks().get("RECUR_0").size());

            expectedStartCal2.add(Calendar.MONTH, 2);
            expectedStartDate2 = expectedStartCal2.getTime();
            expectedEndCal2.add(Calendar.MONTH, 2);
            expectedEndDate2 = expectedEndCal2.getTime();

            // modify to illegal task type

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* Modify all recurring DeadlineTasks */
        // modify start deadline and frequency, description and priority
        deadlineRecurTest1.set(2014, 2, 25, 21, 59);
        untilDeadlineRecur1.set(2014, 3, 6, 21, 59);
        DeadlineTask expectedDeadlineRecur1 = new DeadlineTask(
                "After lecture assignment", deadlineRecurTest1.getTime());
        expectedDeadlineRecur1.setTag("RECUR_0");

        try {
            tdmTest.modifyTask(new DataParameter("After lecture assignment.",
                    'M', null, deadlineRecurTest1.getTime(), null, null, 89,
                    tdmTest.getRecurringTasks().get("RECUR_0").get(1), null,
                    "DAY", 1, untilDeadlineRecur1.getTime(), null, true));

            // TODO: something wrong here
            // assertEquals(expectedRecur1,
            // tdmTest.getRecurringTasks().get("RECUR_0").get(0));
            assertEquals(13, tdmTest.getRecurringTasks().get("RECUR_0").size());

        } catch (Exception e1) {
            e1.printStackTrace();

        }

        /* Modify a single recurring EventTask */
        Calendar startEventRecur1 = Calendar.getInstance();
        startEventRecur1.set(2014, 3, 1, 19, 59);
        Calendar endEventRecur1 = Calendar.getInstance();
        endEventRecur1.set(2014, 3, 1, 21, 59);
        Calendar untilEventRecur1 = Calendar.getInstance();
        untilEventRecur1.set(2014, 6, 1, 21, 59);

        try {
            tdmTest.addTask(new DataParameter("Lab", 'H', startEventRecur1
                    .getTime(), endEventRecur1.getTime(), null, TaskType.EVENT,
                    21, "RECUR", untilEventRecur1.getTime(), "MONTH", 1, null));
            assertEquals(5, tdmTest.getUncompletedEventTasks().size());

            startEventRecur1.add(Calendar.MONTH, 2);
            endEventRecur1.add(Calendar.MONTH, 2);

            EventTask expectedEventRecurTest1 = new EventTask("Lab2",
                    endEventRecur1.getTime(), startEventRecur1.getTime(), 'M');

            assertEquals(expectedEventRecurTest1,
                    tdmTest.modifyTask(new DataParameter("Lab2", 'M',
                            endEventRecur1.getTime(), startEventRecur1
                                    .getTime(), TaskType.EVENT, null, 90,
                            tdmTest.getRecurringTasks().get("RECUR_1").get(2),
                            null, null, 0, null, null, false)));
            assertEquals(5, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getRecurringTasks().get("RECUR_1").size());

        } catch (Exception e) {
            e.printStackTrace();

        }

        /* Modify all recurring EventTasks */
        // modify start deadline and frequency, description and priority
        startEventRecur1.set(2014, 3, 4, 9, 59);
        endEventRecur1.set(2014, 3, 4, 11, 59);
        untilEventRecur1.set(2014, 6, 25, 21, 59);

        EventTask expectedEventRecur1 = new EventTask("Lab poke",
                startEventRecur1.getTime(), endEventRecur1.getTime());
        expectedEventRecur1.setTag("RECUR_1");

        try {
            tdmTest.modifyTask(new DataParameter("Lab poke", 'M',
                    startEventRecur1.getTime(), endEventRecur1.getTime(), null,
                    null, 89,
                    tdmTest.getRecurringTasks().get("RECUR_1").get(1), null,
                    "MONTH", 2, untilEventRecur1.getTime(), null, true));

            assertEquals(expectedEventRecur1,
                    tdmTest.getRecurringTasks().get("RECUR_1").get(0));
            // note: 01 Jun old Lab not deleted, cos it was previously modified
            // above
            assertEquals(4, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getRecurringTasks().get("RECUR_1").size());

        } catch (Exception e1) {
            e1.printStackTrace();

        }

        /* Modify a single block EventTask */
        List<TimePair> blockTimePairList1 = new ArrayList<TimePair>();
        Calendar startEventBlock1 = Calendar.getInstance();
        Calendar endEventBlock1 = Calendar.getInstance();
        try {
            startEventBlock1.set(2014, 3, 1, 19, 59);
            endEventBlock1.set(2014, 3, 1, 21, 59);
            blockTimePairList1.add(new TimePair(startEventBlock1.getTime(),
                    endEventBlock1.getTime()));

            startEventBlock1.set(2014, 4, 3, 17, 59);
            endEventBlock1.set(2014, 4, 3, 20, 59);
            blockTimePairList1.add(new TimePair(startEventBlock1.getTime(),
                    endEventBlock1.getTime()));

            startEventBlock1.set(2014, 4, 5, 17, 59);
            endEventBlock1.set(2014, 4, 5, 20, 59);
            blockTimePairList1.add(new TimePair(startEventBlock1.getTime(),
                    endEventBlock1.getTime()));

            DataParameter initiateEvent = new DataParameter("Chope Event", 'M',
                    startEventBlock1.getTime(), endEventBlock1.getTime(), null,
                    TaskType.EVENT, 78, "BLOCK", null, null, 0,
                    blockTimePairList1);
            tdmTest.addTask(initiateEvent);

            assertEquals(7, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getBlockTasks().get("BLOCK_0").size());

            // modify start and end date only
            startEventBlock1.set(2014, 4, 3, 10, 59);
            endEventBlock1.set(2014, 4, 3, 13, 59);
            Date expectedStartBlock1 = startEventBlock1.getTime();
            Date expectedEndBlock1 = endEventBlock1.getTime();

            EventTask expectedBlockEvent1 = new EventTask("Chope Event",
                    expectedStartBlock1, expectedEndBlock1, 'M');
            expectedBlockEvent1.setTag("BLOCK_0");

            assertEquals(expectedBlockEvent1,
                    tdmTest.modifyTask(new DataParameter(null, 'M',
                            expectedStartBlock1, expectedEndBlock1, null, null,
                            90, tdmTest.getBlockTasks().get("BLOCK_0").get(2),
                            null, null, 0, null, null, false)));
            assertEquals(7, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getBlockTasks().get("BLOCK_0").size());

            // modify priority, description
            EventTask expectedBlockEvent2 = new EventTask("Chope chope event",
                    expectedStartBlock1, expectedEndBlock1, 'L');

            assertEquals(expectedBlockEvent2,
                    tdmTest.modifyTask(new DataParameter("Chope chope event",
                            'L', expectedStartBlock1, expectedEndBlock1, null,
                            null, 35, tdmTest.getBlockTasks().get("BLOCK_0")
                                    .get(1), null, null, 0, null, null, false)));

            assertEquals(7, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getBlockTasks().get("BLOCK_0").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Modify all block EventTasks */
        try {
            Calendar startEventBlock3 = Calendar.getInstance();
            Calendar endEventBlock3 = Calendar.getInstance();

            startEventBlock3.set(2015, 2, 23, 14, 00);
            endEventBlock3.set(2015, 2, 23, 16, 00);

            // modify other parameters (DO NOT allow start and end date to be
            // modified, because all will be modified)
            // still have tag
            Date expectedStartTime3 = tdmTest.getBlockTasks().get("BLOCK_0")
                    .get(0).getStartTime();
            Date expectedEndTime3 = tdmTest.getBlockTasks().get("BLOCK_0")
                    .get(0).getEndTime();

            EventTask expectedBlockEvent3 = new EventTask("All modified...",
                    expectedStartTime3, expectedEndTime3, 'L');
            expectedBlockEvent3.setTag("BLOCK_0");

            // start and end DO NOT get modified
            assertEquals(expectedBlockEvent3,
                    tdmTest.modifyTask(new DataParameter("All modified...",
                            'L', startEventBlock3.getTime(), endEventBlock3
                                    .getTime(), null, null, 35, tdmTest
                                    .getBlockTasks().get("BLOCK_0").get(1),
                            null, null, 0, null, null, true)));

            assertEquals(7, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getBlockTasks().get("BLOCK_0").size());

            // modify task type and other parameters
            // no more tag
            TodoTask expectedTodo4 = new TodoTask("Changed block to todo", 'L');

            assertEquals(expectedTodo4, tdmTest.modifyTask(new DataParameter(
                    "Changed block to todo", 'L', null, null, null,
                    TaskType.TODO, 35, tdmTest.getBlockTasks().get("BLOCK_0")
                            .get(1), null, null, 0, null, null, true)));
            assertEquals(5, tdmTest.getUncompletedEventTasks().size());
            assertEquals(1, tdmTest.getUncompletedTodoTasks().size());
            assertNull(tdmTest.getBlockTasks().get("BLOCK_0"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void markCompleted() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* regular mark complete */
        // Partition: to-do tasks
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

        // Partition: deadline tasks
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

        // Partition: event tasks
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

        tdmTest.resetTrees();

        /* Mark recurring tasks as completed */
        // Partition: recurring deadline tasks, until a certain date

        Calendar recurDeadlineCal1 = Calendar.getInstance();
        Calendar endRecurDeadlineCal1 = Calendar.getInstance();
        recurDeadlineCal1.set(2014, 2, 23, 23, 59);
        endRecurDeadlineCal1.set(2014, 3, 13, 23, 59);

        try {
            tdmTest.addTask(new DataParameter(
                    "Completed recurring assignment.", 'L', null,
                    recurDeadlineCal1.getTime(), null, TaskType.DEADLINE, 23,
                    "RECUR", endRecurDeadlineCal1.getTime(), "WEEK", 1, null));
            assertEquals(4, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(4, tdmTest.getRecurringTasks().get("RECUR_0").size());

            Calendar expectedCompletedDeadline1 = Calendar.getInstance();
            expectedCompletedDeadline1.set(2014, 3, 6, 23, 59);
            DeadlineTask expectedCompleteDeadlineRecur1 = new DeadlineTask(
                    "Completed recurring assignment",
                    expectedCompletedDeadline1.getTime(), 'L');
            expectedCompleteDeadlineRecur1.setTag("RECUR_0");
            expectedCompleteDeadlineRecur1.setCompleted(true);

            // TODO: something wrong with comparing deadlineTasks
            // assertEquals(expectedCompleteDeadlineRecur1,
            // tdmTest.markCompleted(new DataParameter(null, 'M', null,
            // null, null, null, 231, tdmTest.getRecurringTasks()
            // .get("RECUR_0").get(2), null, null, 0,
            // null, null, false)));
            tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                    null, null, 231, tdmTest.getRecurringTasks().get("RECUR_0")
                            .get(2), null, null, 0, null, null, false));

            assertEquals(3, tdmTest.getUncompletedDeadlineTasks().size());
            assertEquals(3, tdmTest.getRecurringTasks().get("RECUR_0").size());
            assertEquals(1, tdmTest.getCompletedDeadlineTasks().size());

            DeadlineTask expectedCompleteDeadlineRecur2 = new DeadlineTask(
                    "Completed recurring assignment",
                    recurDeadlineCal1.getTime(), 'L');
            expectedCompleteDeadlineRecur2.setTag("RECUR_0");
            expectedCompleteDeadlineRecur2.setCompleted(true);

            // assertEquals(expectedCompleteDeadlineRecur2,
            // tdmTest.markCompleted(new DataParameter(null, 'M', null,
            // null, null, null, 231, tdmTest.getRecurringTasks()
            // .get("RECUR_0").get(2), null, null, 0,
            // null, null, true)));

            tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                    null, null, 231, tdmTest.getRecurringTasks().get("RECUR_0")
                            .get(2), null, null, 0, null, null, true));

            assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
            assertNull(tdmTest.getRecurringTasks().get("RECUR_0"));
            assertEquals(4, tdmTest.getCompletedDeadlineTasks().size());

            // Partition: recurring deadline tasks, forever

        } catch (Exception e) {
            e.printStackTrace();

        }

        /* Mark recurring event tasks as completed */
        // Partition: recurring event tasks, until a certain date
        // Partition: recurring event tasks, forever

        /* Mark block tasks as completed */
        // Partition: recurring deadline tasks, until a certain date

        List<TimePair> blockEvent1TimePairs = new ArrayList<TimePair>();

        Calendar blockStartEventCal1a = Calendar.getInstance();
        Calendar blockEndEventCal1a = Calendar.getInstance();
        blockStartEventCal1a.set(2014, 2, 23, 7, 00);
        blockEndEventCal1a.set(2014, 2, 23, 10, 34);
        blockEvent1TimePairs.add(new TimePair(blockStartEventCal1a.getTime(),
                blockEndEventCal1a.getTime()));

        Calendar blockStartEventCal1b = Calendar.getInstance();
        Calendar blockEndEventCal1b = Calendar.getInstance();
        blockStartEventCal1b.set(2014, 2, 24, 7, 00);
        blockEndEventCal1b.set(2014, 2, 24, 9, 34);
        blockEvent1TimePairs.add(new TimePair(blockStartEventCal1b.getTime(),
                blockEndEventCal1b.getTime()));

        Calendar blockStartEventCal1c = Calendar.getInstance();
        Calendar blockEndEventCal1c = Calendar.getInstance();
        blockStartEventCal1c.set(2014, 2, 24, 12, 00);
        blockEndEventCal1c.set(2014, 2, 24, 14, 34);
        blockEvent1TimePairs.add(new TimePair(blockStartEventCal1c.getTime(),
                blockEndEventCal1c.getTime()));

        try {
            tdmTest.addTask(new DataParameter("completed block", 'L',
                    blockStartEventCal1a.getTime(), blockEndEventCal1a
                            .getTime(), null, TaskType.EVENT, 41, "BLOCK",
                    null, null, 0, blockEvent1TimePairs));
            assertEquals(3, tdmTest.getUncompletedEventTasks().size());
            assertEquals(3, tdmTest.getBlockTasks().get("BLOCK_0").size());

            // Partition: mark one of the task as completed
            EventTask expectedBlockEvent1 = new EventTask("completed block",
                    blockStartEventCal1b.getTime(),
                    blockEndEventCal1b.getTime(), 'L');
            expectedBlockEvent1.setTag("BLOCK_0");
            expectedBlockEvent1.setCompleted(true);

            assertEquals(expectedBlockEvent1,
                    tdmTest.markCompleted(new DataParameter(null, 'M', null,
                            null, null, null, 123, tdmTest.getBlockTasks()
                                    .get("BLOCK_0").get(1), null, null, 0,
                            null, null, false)));
            assertEquals(2, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getBlockTasks().get("BLOCK_0").size());
            assertEquals(1, tdmTest.getCompletedEventTasks().size());

            assertNull(tdmTest.markCompleted(new DataParameter(null, 'M', null,
                    null, null, null, 123, tdmTest.getBlockTasks()
                            .get("BLOCK_0").get(1), null, null, 0, null, null,
                    true)));
            assertEquals(2, tdmTest.getUncompletedEventTasks().size());
            assertEquals(2, tdmTest.getBlockTasks().get("BLOCK_0").size());
            assertEquals(1, tdmTest.getCompletedEventTasks().size());

            // Partition: mark all as completed (not allowed)

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
