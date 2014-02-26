package sg.edu.cs2103t.mina.controller;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.*;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;


public class TaskFilterManagerTest {

	private TaskDataManagerStub tdmStub= new TaskDataManagerStub();
	private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
	
	@Test
	public void viewOutput(){
		//check for output
		TaskDataManagerStub tdm = tdmStub;
		
		Iterator<TodoTask> todoIterator; 
		todoIterator = tdm.getTodoTasks().iterator();
		
		while(todoIterator.hasNext()){
			TodoTask task = todoIterator.next();
			if(task!=null)
				printTodo(task);
		}
		
		Iterator<EventTask> eventIterator;
		eventIterator = tdm.getEventTasks().iterator();
		
		while(eventIterator.hasNext()){
			printEvent(eventIterator.next());
		}
		
		Iterator<DeadlineTask> deadlineIterator;
		deadlineIterator = tdm.getDeadlineTasks().iterator();
		
		while(deadlineIterator.hasNext()){
			printDeadline(deadlineIterator.next());
		}
		assertTrue(true);
	}
	
	@Test (expected=NullPointerException.class)
	public void testFilterVoid() {
		tfmTest.filterTask(null);
	}
	
	@Test
	/**
	 * Test for passing an empty filter parameter
	 * Expected: Returned all uncompleted tasks
	 * 
	 */
	public void testNoFilter() {
		
		ArrayList<Task> test = tfmTest.filterTask(new FilterParameter());
		boolean isExist = true;
		
		for (Task t: test) {
			
			if (t instanceof TodoTask) {
				isExist = compareTodo(t, tdmStub.getTodoTasks());
			} else if(t instanceof EventTask) {
				isExist = tdmStub.getEventTasks().contains((EventTask)t);
			} else if(t instanceof DeadlineTask) {
				isExist = tdmStub.getDeadlineTasks().contains((DeadlineTask)t);
			}
			if(!isExist) {
				break;
			}
		}
		
		assertTrue("Must have everything!", isExist);
		
	}
	
	/**
	 * To compare with tasks for now. The current task compareTo
	 * has a bug.
	 * 
	 * @param task
	 * @param todoTasks
	 * @return true if they match. False, if they don't
	 */
	private boolean compareTodo(Task task, 
															TreeSet<TodoTask> todoTasks) {
		
		Iterator<TodoTask> todoIter = todoTasks.iterator();
		
		while (todoIter.hasNext()) {
			TodoTask todo = todoIter.next();
			if (todo.getDescription().equals(task.getDescription())) {
				return true;
			}
		}
		
		return false;
	}
	
	public String toStringForTask(Task task){
		String format = "UUID: %1$s\n" +
										"Type: %2$s\n" +
										"Description: %3$s\n" +
										"Tags: %4$s\n" +
										"Priority: %5$s\n" +
										"Created: %6$s\n" +
										"Last modified: %7$s\n" +
										"Completed: %8$s\n\n";
		String output = String.format(format, task.getId(),
																					task.getType().getType(),
																					task.getDescription(),
																					task.getTags(),
																					task.getPriority(),
																					task.getCreatedTime(),
																					task.getLastEditedTime(),
																					task.isCompleted());
		return output;
	}
	
	public void printEvent(EventTask task){
		String format = "Start: %1$s\n" + 
										"End: %2$s\n";
		System.out.println(toStringForTask(task));
		System.out.println(String.format(format, task.getStartTime(),
																						 task.getEndTime()));
		System.out.println("------------------");
	}
	
	public void printDeadline(DeadlineTask task){
		String format = "End: %1$s\n";
		System.out.println(toStringForTask(task));
		System.out.println(String.format(format, task.getEndTime()));		
		System.out.println("------------------");
	}	
	
	public void printTodo(TodoTask task){
		System.out.println(toStringForTask(task));	
		System.out.println("------------------");		
	}
	
}
