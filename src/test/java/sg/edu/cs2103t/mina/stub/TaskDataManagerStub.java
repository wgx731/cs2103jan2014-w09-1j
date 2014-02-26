package sg.edu.cs2103t.mina.stub;

import java.util.Date;
import java.util.Random;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.*;

public class TaskDataManagerStub extends TaskDataManager{
	
	TreeSet<TodoTask> _todoTasks;
	TreeSet<EventTask> _eventTasks;
	TreeSet<DeadlineTask> _deadlineTasks;
	
	TreeSet<TodoTask> _compTodoTasks;
	TreeSet<EventTask> _compEventTasks;
	TreeSet<DeadlineTask> _compDeadlineTasks;
	
	public TaskDataManagerStub(){
		_todoTasks = new TreeSet<TodoTask>();
		_eventTasks = new TreeSet<EventTask>();
		_deadlineTasks = new TreeSet<DeadlineTask>();
		
		char[] roulette = {'L', 'M', 'H'};
		
		Random rand = new Random();
		int oneDay = 1000*60*60*24;
		int oneHour = 1000*60*60;
		
		for(int i=0; i<20; i++){
			
			TodoTask newTodoTask = new TodoTask("Do item " + i + " on the list", roulette[i%3]);
			_todoTasks.add(newTodoTask);
			
			int time = oneDay + rand.nextInt(oneDay*15);
			Date startTime = new Date();
			startTime.setTime(startTime.getTime() + time);
			
			Date endDate = new Date(startTime.getTime());
			endDate.setTime(startTime.getTime() + oneHour*2);
			
			EventTask newEventTask = new EventTask("Event " + i, startTime, endDate);
			_eventTasks.add(newEventTask);
			
			time = oneDay + rand.nextInt(oneDay*15);
			startTime = new Date();
			startTime.setTime(startTime.getTime() + time);
			DeadlineTask newDeadline = new DeadlineTask("Deadline " + i, startTime);
			_deadlineTasks.add(newDeadline);
			
		}
		
	}
	
	public TreeSet<TodoTask> getTodoTasks() {
		return _todoTasks;
	}

	public TreeSet<EventTask> getEventTasks() {
		return _eventTasks;
	}

	public TreeSet<DeadlineTask> getDeadlineTasks() {
		return _deadlineTasks;
	}

	public TreeSet<TodoTask> getCompTodoTasks() {
		return _compTodoTasks;
	}

	public TreeSet<EventTask> getCompEventTasks() {
		return _compEventTasks;
	}

	public TreeSet<DeadlineTask> getCompDeadlineTasks() {
		return _compDeadlineTasks;
	}
	
}
