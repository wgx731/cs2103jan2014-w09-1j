package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;

public class TaskDataManagerTest {

	/*@Test
	public void testGetAllTodoTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllDeadlineTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllEventTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPastTodoTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPastDeadlineTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPastEventTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSyncList() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearSyncList() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddTask() {
		fail("Not yet implemented");
	}*/

	@Test
	public void testDeleteTask() {
		TaskDataManager tdmTest = new TaskDataManager();
		tdmTest.addTask(new DataParameter("Sleep", 'H', null, null, TaskType.TODO, TaskType.TODO, 1));
		tdmTest.addTask(new DataParameter("Lie down", 'H', null, null, TaskType.TODO, TaskType.TODO, 2));
		tdmTest.addTask(new DataParameter("Bed...", 'H', null, null, TaskType.TODO, TaskType.TODO, 3));
		
		DataParameter deleteParametersTest =  new DataParameter("Lie down", 'H', null, null, TaskType.TODO, null, 2);
		
		assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'), tdmTest.deleteTask(deleteParametersTest));
	}

	/*@Test
	public void testModifyTask() {
		fail("Not yet implemented");
	}*/

}
