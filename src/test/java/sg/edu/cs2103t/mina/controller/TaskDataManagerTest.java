package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
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
        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        DataParameter deleteParametersTest1 = new DataParameter("Lie down",
                'H', null, null, TaskType.TODO, null, 2);

        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(deleteParametersTest1));

        Long currDateMilliSec = System.currentTimeMillis();

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 1));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 3));

        DataParameter deleteParametersTest2 = new DataParameter("Lie down",
                'H', null, new Date(currDateMilliSec), TaskType.DEADLINE,
                TaskType.DEADLINE, 2);

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'),
                tdmTest.deleteTask(deleteParametersTest2));
    }

    /*
     * @Test public void testModifyTask() { fail("Not yet implemented"); }
     */

}
