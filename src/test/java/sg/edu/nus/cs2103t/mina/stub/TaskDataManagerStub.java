package sg.edu.nus.cs2103t.mina.stub;

import hirondelle.date4j.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TimeZone;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

public class TaskDataManagerStub extends TaskDataManager {

    //Thu Jan 08 11:43:28 SGT 1970
    public static final int START_TIME = 620008200; 

    public static final int ONE_HOUR = 1000 * 60 * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;
    public static final int ONE_WEEK = ONE_DAY * 7;
    
    public static final int PUNCTUATION_SEARCH = 0;
    public static final int DATE_RANGE_SEARCH = 1;
    public static final int SANITISED_DATE_RANGE_SEARCH = 2;
    
		private static SortedSet<TodoTask> _todoTasks;
    private static SortedSet<EventTask> _eventTasks;
    private static SortedSet<DeadlineTask> _deadlineTasks;

    private static SortedSet<TodoTask> _compTodoTasks;
    private static SortedSet<EventTask> _compEventTasks;
    private static SortedSet<DeadlineTask> _compDeadlineTasks;

    public TaskDataManagerStub() {

        super();

        _todoTasks = getUncompletedTodoTasks();
        _eventTasks = getUncompletedEventTasks();
        _deadlineTasks = getUncompletedDeadlineTasks();

        _compTodoTasks = getCompletedTodoTasks();
        _compEventTasks = getCompletedEventTasks();
        _compDeadlineTasks = getCompletedDeadlineTasks();
        
        _todoTasks.clear();
        _eventTasks.clear();
        _deadlineTasks.clear();
        
        _compTodoTasks.clear();
        _compEventTasks.clear();
        _compDeadlineTasks.clear();
        
        char[] roulette = { 'L', 'M', 'H' };
        
        for (int i = 0; i < 20; i++) {

            TodoTask newTodoTask = new TodoTask(
                    "Do item " + i + " on the list", roulette[i % 3]);
            if (i % 5 == 0) {
                newTodoTask.setCompleted(true);
                _compTodoTasks.add(newTodoTask);
            } else {
                _todoTasks.add(newTodoTask);
            }

            int time = ONE_DAY * (i+1);
            Date startTime = new Date();
            startTime.setTime(startTime.getTime() + time);

            Date endDate = new Date(startTime.getTime());
            endDate.setTime(startTime.getTime() + ONE_HOUR * 2);

            EventTask newEventTask = new EventTask("Event " + i, startTime,
                    endDate);
            if (i % 10 == 0) {
                // Make it into the past
                Date pastStart = new Date((new Date()).getTime() - time);
                newEventTask.setStartTime(pastStart);

                Date pastEnd = new Date(pastStart.getTime() + ONE_HOUR * 2);
                newEventTask.setEndTime(pastEnd);

                newEventTask.setCompleted(true);
                _compEventTasks.add(newEventTask);

            } else {
                _eventTasks.add(newEventTask);
            }

            time = ONE_DAY * (i+1) ;
            startTime = new Date();
            startTime.setTime(startTime.getTime() + time);

            DeadlineTask newDeadline = new DeadlineTask("Deadline " + i,
                    startTime);
            if (i % 6 == 0) {
                // Make it into the past
                Date pastEnd = new Date((new Date()).getTime() - time);
                newDeadline.setEndTime(pastEnd);
                newDeadline.setCompleted(true);
                _compDeadlineTasks.add(newDeadline);
            } else {
                _deadlineTasks.add(newDeadline);
            }

        }

    }
    
    public TaskDataManagerStub(int cases) {
    	
    	super();
    	
      _todoTasks = getUncompletedTodoTasks();
      _eventTasks = getUncompletedEventTasks();
      _deadlineTasks = getUncompletedDeadlineTasks();

      _compTodoTasks = getCompletedTodoTasks();
      _compEventTasks = getCompletedEventTasks();
      _compDeadlineTasks = getCompletedDeadlineTasks();
    	
    	switch(cases) {
    		case PUNCTUATION_SEARCH :
    			_todoTasks.clear();
    			_todoTasks.add(new TodoTask("1. Do laundry, grocery and walk the dog"));
    			_todoTasks.add(new TodoTask("2. Buy the new Zack Hemsey's album"));
    			_todoTasks.add(new TodoTask("3. Watch GITS 2nd gIg. Watch SAC as well."));
    			_todoTasks.add(new TodoTask("4. we , might . have ! a ; puntuation o problem } I {} think. "));
    			break;
    		case DATE_RANGE_SEARCH :
    		    
    		    _eventTasks.clear();
    		    _deadlineTasks.clear();
    		    
    		    Calendar baseDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    		    baseDate.setTime(new Date(START_TIME));
    		   
    		    baseDate.set(baseDate.get(Calendar.YEAR), 
    		            baseDate.get(Calendar.MONTH), 
    		            baseDate.get(Calendar.DATE), 
    		            3, 45, 00);
    		    baseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
    		    EventTask newEvent;
    		    DeadlineTask newDeadline;
    		    Date startDate;
    		    Date endDate;
    		    
    		    for(int i=0; i<5; i++) {
    		        
                    baseDate.set(baseDate.get(Calendar.YEAR), 
                            baseDate.get(Calendar.MONTH), 
                            baseDate.get(Calendar.DATE) + 1, 
                            3, 45, 00);
    		        
        		    startDate = baseDate.getTime();
        		    startDate.setTime(startDate.getTime());
        		    endDate = new Date(startDate.getTime() + 2 * ONE_HOUR);
        		    
        		    newDeadline = new DeadlineTask("Daily Deadline " + (i+1), startDate);
        		    newEvent = new EventTask("Dailies " + (i+1), startDate, endDate);
        		    _eventTasks.add(newEvent);
        		    _deadlineTasks.add(newDeadline);
    		    }
    		    
    		    baseDate.setTime(new Date(START_TIME));
    		    
    		    for (int i=0; i<3; i++) {
    		        
                    baseDate.set(baseDate.get(Calendar.YEAR), 
                            baseDate.get(Calendar.MONTH), 
                            baseDate.get(Calendar.DATE) + 7, 
                            3, 45, 00);
    		        
                    startDate = baseDate.getTime();
                    startDate.setTime(startDate.getTime() + (i+1)*ONE_WEEK);
                    endDate = new Date(startDate.getTime() + 2 * ONE_HOUR);
                    
                    newDeadline = new DeadlineTask("Weekly Deadline " + (i+1), startDate);
                    newEvent = new EventTask("Weekly " + (i+1), startDate, endDate);
                    _eventTasks.add(newEvent);
                    _deadlineTasks.add(newDeadline);
    		    }
    		    
    		    break;
    		    
          case SANITISED_DATE_RANGE_SEARCH :
                _deadlineTasks.clear();
                baseDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                baseDate.setTime(new Date(START_TIME));            
                
                baseDate.set(baseDate.get(Calendar.YEAR), 
                        baseDate.get(Calendar.MONTH), 
                        baseDate.get(Calendar.DATE), 
                        23, 59, 59);
                
                Date end = baseDate.getTime();
                newDeadline = new DeadlineTask("Deadline 1",end);
                _deadlineTasks.add(newDeadline);
                break;
                
    		default:
    			throw new Error("Woah, no such test case yet!");
    	}
    	
    }
    
    public SortedSet<TodoTask> getCompTodoTasks() {
        return _compTodoTasks;
    }

    public SortedSet<EventTask> getCompEventTasks() {
        return _compEventTasks;
    }

    public SortedSet<DeadlineTask> getCompDeadlineTasks() {
        return _compDeadlineTasks;
    }

}
