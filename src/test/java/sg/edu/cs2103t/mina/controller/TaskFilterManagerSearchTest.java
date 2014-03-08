package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManagerSearchTest {
	
	private TaskDataManagerStub tdmStub = new TaskDataManagerStub();
	private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
	private static Logger logger = LogManager
			.getLogger(TaskFilterManagerFilterTest.class.getName());
	
	@Test
	public void testEmptySearch() {
		ArrayList<Task<?>> test = tfmTest.searchTasks(new SearchParameter());
		assertEquals(test, new ArrayList<Task<?>>());
	}
	
}
