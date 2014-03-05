package sg.edu.cs2103t.mina.stub;

import java.util.Date;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.*;

public class TaskDataManagerStub extends TaskDataManager{
	
	/*private TreeMap<String, TodoTask> _todoTasks;
	private TreeMap<String, EventTask> _eventTasks;
	private TreeMap<String, DeadlineTask> _deadlineTasks;
	
	private TreeMap<String, TodoTask> _compTodoTasks;
	private TreeMap<String, EventTask> _compEventTasks;
	private TreeMap<String, DeadlineTask> _compDeadlineTasks;*/

	private TreeSet<TodoTask> _todoTasks;
	private TreeSet<EventTask> _eventTasks;
	private TreeSet<DeadlineTask> _deadlineTasks;
	
	private TreeSet<TodoTask> _compTodoTasks;
	private TreeSet<EventTask> _compEventTasks;
	private TreeSet<DeadlineTask> _compDeadlineTasks;
	
	public TaskDataManagerStub(){
		
		super();
		
		/*_todoTasks = new TreeMap<String, TodoTask>();
		_eventTasks = new TreeMap<String, EventTask>();
		_deadlineTasks = new TreeMap<String, DeadlineTask>();
		
		_compTodoTasks= new TreeMap<String, TodoTask>();
		_compEventTasks = new TreeMap<String, EventTask>();
		_compDeadlineTasks = new TreeMap<String, DeadlineTask>();*/

		_todoTasks = getAllTodoTasks();
		_eventTasks = getAllEventTasks();
		_deadlineTasks = getAllDeadlineTasks();
		
		_compTodoTasks= getPastTodoTasks();
		_compEventTasks = getPastEventTasks();
		_compDeadlineTasks = getPastDeadlineTasks();
		
		char[] roulette = {'L', 'M', 'H'};
		
		Random rand = new Random();
		int oneDay = 1000*60*60*24;
		int oneHour = 1000*60*60;
		
		for (int i=0; i<20; i++) {
			
			TodoTask newTodoTask = new TodoTask("Do item " + i + " on the list", 
																					roulette[i%3]);
			if(i%5==0){
				newTodoTask.setCompleted(true);
				//_compTodoTasks.put(newTodoTask.getId(), newTodoTask);
				_compTodoTasks.add(newTodoTask);
			} else {
				//_todoTasks.put(newTodoTask.getId(), newTodoTask);
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
				//_compEventTasks.put(newEventTask.getId(), newEventTask);
				_compEventTasks.add(newEventTask);
				
			} else {
				//_eventTasks.put(newEventTask.getId(), newEventTask);
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
				//_compDeadlineTasks.put(newDeadline.getId(), newDeadline);
				_compDeadlineTasks.add(newDeadline);
			} else {
				//_deadlineTasks.put(newDeadline.getId(), newDeadline);
				_deadlineTasks.add(newDeadline);
			}
			
		}
		
	}

	/*public TreeMap<String, TodoTask> getTodoTasksTemp() {
		return _todoTasks;
	}

	public TreeMap<String, EventTask> getEventTasksTemp() {
		return _eventTasks;
	}

	public TreeMap<String, DeadlineTask> getDeadlineTasksTemp() {
		return _deadlineTasks;
	}

	public TreeMap<String, TodoTask> getCompTodoTasks() {
		return _compTodoTasks;
	}

	public TreeMap<String, EventTask> getCompEventTasks() {
		return _compEventTasks;
	}

	public TreeMap<String, DeadlineTask> getCompDeadlineTasks() {
		return _compDeadlineTasks;
	}*/
	
}
