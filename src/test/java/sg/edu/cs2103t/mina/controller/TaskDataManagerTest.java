package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;

public class TaskDataManagerTest {

    @Test
    public void testAddTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* Basic add */
        assertEquals("Adding To-do task.", new TodoTask("TodoTask1"),
                tdmTest.addTask(new DataParameter("TodoTask1", 'M', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals("Adding To-do task.", new TodoTask("TodoTask2", 'H'),
                tdmTest.addTask(new DataParameter("TodoTask2", 'H', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(2, tdmTest.getAllTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask1",
                new Date(currDateMilliSec)), tdmTest.addTask(new DataParameter(
                "DeadlineTask1", 'M', null, new Date(currDateMilliSec), null,
                TaskType.DEADLINE, 123)));
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());
        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask2",
                new Date(currDateMilliSec), 'M'),
                tdmTest.addTask(new DataParameter("DeadlineTask2", 'M', null,
                        new Date(currDateMilliSec), null, TaskType.DEADLINE,
                        123)));
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask1",
                new Date(currDateMilliSec), new Date(currDateMilliSec)),
                tdmTest.addTask(new DataParameter("EventTask1", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(1, tdmTest.getAllEventTasks().size());
        assertEquals("Adding Event task.", new EventTask("EventTask2",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.addTask(new DataParameter("EventTask2", 'H', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(2, tdmTest.getAllEventTasks().size());

        /* Task added is exactly the same as a task that already exists. */
        assertNull(tdmTest.addTask(new DataParameter("TodoTask2", 'H', null,
                null, null, TaskType.TODO, 123)));
    }

    @Test
    public void testDeleteTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));

        tdmTest.addTask(new DataParameter("Sleep", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));

        /* Basic Delete */
        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2)));
        assertEquals(2, tdmTest.getAllTodoTasks().size());

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'), tdmTest.deleteTask(new DataParameter(
                null, 'M', null, null, TaskType.DEADLINE, null, 2)));
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());

        assertEquals("Event tasks.", new EventTask("Lie down", new Date(
                currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2)));
        assertEquals(2, tdmTest.getAllEventTasks().size());

        /* Invalid Task Id */
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 0)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 3)));

        assertEquals(2, tdmTest.getAllTodoTasks().size());
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());
        assertEquals(2, tdmTest.getAllEventTasks().size());
    }

    @Test
    public void testModifyTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* modify task parameters */

        /* modify task type */

        // basic modify
        tdmTest.addTask(new DataParameter("TodoTask becomes a DeadlineTask.",
                'M', null, null, null, TaskType.TODO, 123));

        assertEquals(
                "Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask.", new Date(
                        currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 1)));
        assertEquals(0, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());
        
        tdmTest.resetTrees();
        
        // modify task in between SortedSet
        tdmTest.addTask(new DataParameter("TodoTask remains a TodoTask 1.",
                'H', null, null, null, TaskType.TODO, 123));
        tdmTest.addTask(new DataParameter("TodoTask becomes a DeadlineTask 2.",
                'M', null, null, null, TaskType.TODO, 123));

        assertEquals(
                "Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask 2.",
                        new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 2)));
        assertEquals(new TodoTask("TodoTask remains a TodoTask 1.", 'H'), tdmTest.getAllTodoTasks().first());
        
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());

    }

    /*
     * @Test public void markCompleted() { fail("Not yet implemented"); }
     */

}
