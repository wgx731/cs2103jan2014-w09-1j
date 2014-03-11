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

    /*
     * @Test public void testGetAllTodoTasks() { fail("Not yet implemented"); }
     * 
     * @Test public void testGetAllDeadlineTasks() {
     * fail("Not yet implemented"); }
     * 
     * @Test public void testGetAllEventTasks() { fail("Not yet implemented"); }
     * 
     * @Test public void testGetPastTodoTasks() { fail("Not yet implemented"); }
     * 
     * @Test public void testGetPastDeadlineTasks() {
     * fail("Not yet implemented"); }
     * 
     * @Test public void testGetPastEventTasks() { fail("Not yet implemented");
     * }
     * 
     * @Test public void testGetSyncList() { fail("Not yet implemented"); }
     * 
     * @Test public void testClearSyncList() { fail("Not yet implemented"); }
     * 
     * @Test public void testAddTask() { fail("Not yet implemented"); }
     */

    @Test
    public void testDeleteTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();
        
        /* To-do Tasks*/
        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 6)));

        /* Deadline Tasks */
        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'), tdmTest.deleteTask(new DataParameter(
                null, 'M', null, null, TaskType.DEADLINE,
                null, 2)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 6)));

        /* Event Tasks */
        tdmTest.addTask(new DataParameter("Sleep", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));

        assertEquals("Event tasks.", new EventTask("Lie down", new Date(
                currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 6)));
    }

    /*
     * @Test public void testModifyTask() { fail("Not yet implemented"); }
     */

}
