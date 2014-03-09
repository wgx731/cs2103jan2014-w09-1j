package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManagerSearchTest {
	
	private TaskDataManagerStub tdmStub = new TaskDataManagerStub();
	private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
	private static Logger logger = LogManager
			.getLogger(TaskFilterManagerSearchTest.class.getName());
	
	@Test
	public void testEmptySearch() {
		//TODO Splitting by " " may have unintended effects, 
		//e.g "        s           " is going to give us alot of false positive
		ArrayList<Task<?>> test = getResult("");
		assertEquals(new ArrayList<Task<?>>(), test);
	}
	
	@Test
	public void testOneKeyword() {
		
		ArrayList<Task<?>> test = getResult("deadline");
		
		Iterator<DeadlineTask> expectedIter = tdmStub.getAllDeadlineTasks().iterator();
		ArrayList<DeadlineTask> expected = new ArrayList<DeadlineTask>();
		
		while (expectedIter.hasNext()) {
			expected.add(expectedIter.next());
		}
		
		assertEquals(expected, test);
		
		test = getResult("1");
		assertTrue("Only three tasks", 
								test.size()==3 &&
								compareToActualTasks(test));
	}
	
	@Test
	public void testTwoWordsAndMore() {
		
		ArrayList<Task<?>> test = getResult("deadline event");
		int expectedSize = tdmStub.getAllDeadlineTasks().size() + 
											 tdmStub.getAllEventTasks().size();
		assertTrue("Must contain deadline and event", 
								test.size()==expectedSize && 
										compareToActualTasks(test));
		
	}
	
	@Test
	public void testPunctuations() {
		
	}
	
	private boolean compareToActualTasks(ArrayList<Task<?>> test) {
		
		//guard clause
		if (test.isEmpty()) {
			return false;
		}
		
		Iterator<DeadlineTask> deadlineIter = tdmStub.getAllDeadlineTasks().iterator();
		ArrayList<DeadlineTask> deadlines = new ArrayList<DeadlineTask>();
		
		while (deadlineIter.hasNext()) {
			deadlines.add(deadlineIter.next());
		}
		
		Iterator<EventTask> eventIter = tdmStub.getAllEventTasks().iterator();
		ArrayList<EventTask> events = new ArrayList<EventTask>();
		
		while (eventIter.hasNext()) {
			events.add(eventIter.next());
		}
		
		Iterator<TodoTask> todoIter = tdmStub.getAllTodoTasks().iterator();
		ArrayList<TodoTask> todos = new ArrayList<TodoTask>();
		
		while (todoIter.hasNext()) {
			todos.add(todoIter.next());
		}
		
		for (Task<?> task: test) {
			if (task instanceof TodoTask && 
					!todos.contains((TodoTask)task)) {
				logger.info((TodoTask)task);
				return false;
			}
			if (task instanceof EventTask &&
					!events.contains((EventTask) task)) {
				logger.info((EventTask)task);
				return false;
			}
			if (task instanceof DeadlineTask &&
					!deadlines.contains((DeadlineTask) task)) {
				logger.info((DeadlineTask)task);
				return false;
			}
					
		}
		
		return true;
	}

	public ArrayList<Task<?>> getResult(String rawKeywords) {
		
		String[] tokens = rawKeywords.split(" ");
		ArrayList<String> keywords = new ArrayList<String>();
		SearchParameter searchParam;
		
		for (int i=0; i<tokens.length; i++) {
			if (!tokens[i].trim().equals("")) {
				keywords.add(tokens[i]);
			}
		}
		
		searchParam = new SearchParameter(keywords);
		return tfmTest.searchTasks(searchParam);
		
	}
	
}
