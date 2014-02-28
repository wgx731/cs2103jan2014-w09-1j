package sg.edu.cs2103t.mina.stub;

import java.util.Date;
import java.util.Random;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.*;

public class TaskDataManagerStub extends TaskDataManager{
	
	private static TreeSet<TodoTask> _todoTasks;
	private static TreeSet<EventTask> _eventTasks;
	private static TreeSet<DeadlineTask> _deadlineTasks;
	
	private static TreeSet<TodoTask> _compTodoTasks;
	private static TreeSet<EventTask> _compEventTasks;
	private static TreeSet<DeadlineTask> _compDeadlineTasks;
	
	public TaskDataManagerStub(){
		
		_todoTasks = TaskDataManager.getTodoTasks();
		_eventTasks = TaskDataManager.getEventTasks();
		_deadlineTasks = TaskDataManager.getDeadlineTasks();
		
		_compTodoTasks= new TreeSet<TodoTask>();
		_compEventTasks = new TreeSet<EventTask>();
		_compDeadlineTasks = new TreeSet<DeadlineTask>();
		
		char[] roulette = {'L', 'M', 'H'};
		
		Random rand = new Random();
		int oneDay = 1000*60*60*24;
		int oneHour = 1000*60*60;
		
		for (int i=0; i<20; i++) {
			
			TodoTask newTodoTask = new TodoTask("Do item " + i + " on the list", 
																					roulette[i%3]);
			if(i%5==0){
				newTodoTask.setCompleted(true);
				_compTodoTasks.add(newTodoTask);
			} else {
				_todoTasks.add(newTodoTask);
			}
			
			int time = oneDay + rand.nextInt(oneDay*15);
			Date startTime = new Date();
			startTime.setTime(startTime.getTime() + time);
			
			Date endDate = new Date(startTime.getTime());
			endDate.setTime(startTime.getTime() + oneHour*2);
			
			EventTask newEventTask = new EventTask("Event " + i, startTime, endDate);
			if(i%10==0){
				//Make it into the past
				Date pastStart = new Date((new Date()).getTime() - time);
				newEventTask.setStartTime(pastStart);
				
				Date pastEnd = new Date(pastStart.getTime() + oneHour*2);
				newEventTask.setEndTime(pastEnd);
				
				newEventTask.setCompleted(true);
				_compEventTasks.add(newEventTask);
				
			} else {
				_eventTasks.add(newEventTask);
			}			
			
			
			time = oneDay + rand.nextInt(oneDay*15);
			startTime = new Date();
			startTime.setTime(startTime.getTime() + time);
			
			DeadlineTask newDeadline = new DeadlineTask("Deadline " + i, startTime);
			if (i%6==0) { 
				//Make it into the past
				Date pastEnd = new Date((new Date()).getTime() - time);
				newDeadline.setEndTime(pastEnd);
				newDeadline.setCompleted(true);
				_compDeadlineTasks.add(newDeadline);
			} else {
				_deadlineTasks.add(newDeadline);
			}
			
		}
		
	}
	
	public static TreeSet<TodoTask> getTodoTasks() {
		return _todoTasks;
	}

	public static TreeSet<EventTask> getEventTasks() {
		return _eventTasks;
	}

	public static TreeSet<DeadlineTask> getDeadlineTasks() {
		return _deadlineTasks;
	}

	public static TreeSet<TodoTask> getCompTodoTasks() {
		return _compTodoTasks;
	}

	public static TreeSet<EventTask> getCompEventTasks() {
		return _compEventTasks;
	}

	public static TreeSet<DeadlineTask> getCompDeadlineTasks() {
		return _compDeadlineTasks;
	}
	
}
