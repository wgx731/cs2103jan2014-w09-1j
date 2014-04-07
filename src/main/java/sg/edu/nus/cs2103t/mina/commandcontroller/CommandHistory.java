package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.util.LinkedList;
import java.util.SortedSet;

import com.rits.cloning.Cloner;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

public class CommandHistory {
	
	private LinkedList<SortedSet<TodoTask>> _undoTodoUncompletedSet;
	private LinkedList<SortedSet<DeadlineTask>> _undoDeadlineUncompletedSet;
	private LinkedList<SortedSet<EventTask>> _undoEventUncompletedSet;
	
	private LinkedList<SortedSet<TodoTask>> _undoTodoCompletedSet;
	private LinkedList<SortedSet<DeadlineTask>> _undoDeadlineCompletedSet;
	private LinkedList<SortedSet<EventTask>> _undoEventCompletedSet;
	
	private LinkedList<SortedSet<TodoTask>> _redoTodoUncompletedSet;
	private LinkedList<SortedSet<DeadlineTask>> _redoDeadlineUncompletedSet;
	private LinkedList<SortedSet<EventTask>> _redoEventUncompletedSet;
	
	private LinkedList<SortedSet<TodoTask>> _redoTodoCompletedSet;
	private LinkedList<SortedSet<DeadlineTask>> _redoDeadlineCompletedSet;
	private LinkedList<SortedSet<EventTask>> _redoEventCompletedSet;
	
	private FilterParameter _latestFilter;
		
	public CommandHistory(){
		
		_undoTodoUncompletedSet = new LinkedList<SortedSet<TodoTask>>();
		_undoDeadlineUncompletedSet = new LinkedList<SortedSet<DeadlineTask>>();
		_undoEventUncompletedSet = new LinkedList<SortedSet<EventTask>>();
		
		_undoTodoCompletedSet = new LinkedList<SortedSet<TodoTask>>();
		_undoDeadlineCompletedSet = new LinkedList<SortedSet<DeadlineTask>>();
		_undoEventCompletedSet = new LinkedList<SortedSet<EventTask>>();
		
		_redoTodoUncompletedSet = new LinkedList<SortedSet<TodoTask>>();
		_redoDeadlineUncompletedSet = new LinkedList<SortedSet<DeadlineTask>>();
		_redoEventUncompletedSet = new LinkedList<SortedSet<EventTask>>();
		
		_redoTodoCompletedSet = new LinkedList<SortedSet<TodoTask>>();
		_redoDeadlineCompletedSet = new LinkedList<SortedSet<DeadlineTask>>();
		_redoEventCompletedSet = new LinkedList<SortedSet<EventTask>>();
		
		_latestFilter = new FilterParameter();
	}
    
	public void updateLatestFilter(FilterParameter filterParam){
		_latestFilter = filterParam;
	}
	
	public FilterParameter getLatestFilter(){
		return _latestFilter;
	}
	
    public void addUndo(SortedSet<TodoTask> uncompletedTodoTasks, 
    		SortedSet<DeadlineTask> uncompletedDeadlineTasks,
    		SortedSet<EventTask> uncompletedEventTasks,
    		SortedSet<TodoTask> completedTodoTasks,
    		SortedSet<DeadlineTask> completedDeadlineTasks,
    		SortedSet<EventTask> completedEventTasks){
    	
    	Cloner cloner = new Cloner();
    	SortedSet<TodoTask> cloneUncompletedTodoTasks = cloner.deepClone(uncompletedTodoTasks);
    	SortedSet<DeadlineTask> cloneUncompletedDeadlineTasks = cloner.deepClone(uncompletedDeadlineTasks);
    	SortedSet<EventTask> cloneUncompletedEventTasks = cloner.deepClone(uncompletedEventTasks);

    	SortedSet<TodoTask> cloneCompletedTodoTasks = cloner.deepClone(completedTodoTasks);
    	SortedSet<DeadlineTask> cloneCompletedDeadlineTasks = cloner.deepClone(completedDeadlineTasks);
    	SortedSet<EventTask> cloneCompletedEventTasks = cloner.deepClone(completedEventTasks);
    	
    	_undoTodoUncompletedSet.addFirst(cloneUncompletedTodoTasks);
    	_undoDeadlineUncompletedSet.addFirst(cloneUncompletedDeadlineTasks);
    	_undoEventUncompletedSet.addFirst(cloneUncompletedEventTasks);
    	
    	_undoTodoCompletedSet.addFirst(cloneCompletedTodoTasks);
    	_undoDeadlineCompletedSet.addFirst(cloneCompletedDeadlineTasks);
    	_undoEventCompletedSet.addFirst(cloneCompletedEventTasks);    
    	
    	if (_undoTodoUncompletedSet.size()==6&&_undoDeadlineUncompletedSet.size()==6&&
    			_undoEventUncompletedSet.size()==6&&_undoTodoCompletedSet.size()==6&&
    			_undoDeadlineCompletedSet.size()==6&&_undoEventCompletedSet.size()==6){
    		
    		_undoTodoUncompletedSet.removeLast();
        	_undoDeadlineUncompletedSet.removeLast();
        	_undoEventUncompletedSet.removeLast();
        	
        	_undoTodoCompletedSet.removeLast();
        	_undoDeadlineCompletedSet.removeLast();
        	_undoEventCompletedSet.removeLast();
    	}
    }
    
    public void removeLatestUndo(){
    	_undoTodoUncompletedSet.removeFirst();
    	_undoDeadlineUncompletedSet.removeFirst();
    	_undoEventUncompletedSet.removeFirst();
    	
    	_undoTodoCompletedSet.removeFirst();
    	_undoDeadlineCompletedSet.removeFirst();
    	_undoEventCompletedSet.removeFirst();
    }
    
    public void removeLatestRedo(){
    	_redoTodoUncompletedSet.removeFirst();
    	_redoDeadlineUncompletedSet.removeFirst();
    	_redoEventUncompletedSet.removeFirst();
    	
    	_redoTodoCompletedSet.removeFirst();
    	_redoDeadlineCompletedSet.removeFirst();
    	_redoEventCompletedSet.removeFirst();
    }
    
    public SortedSet<TodoTask> getUndoTodoUncompleted(){
    	return _undoTodoUncompletedSet.removeFirst();
    }
    
    public SortedSet<DeadlineTask> getUndoDeadlineUncompleted(){
    	return _undoDeadlineUncompletedSet.removeFirst();
    }
    
    public SortedSet<EventTask> getUndoEventUncompleted(){
    	return _undoEventUncompletedSet.removeFirst();
    }
    
    public SortedSet<TodoTask> getUndoTodoCompleted(){
    	return _undoTodoCompletedSet.removeFirst();
    }
    
    public SortedSet<DeadlineTask> getUndoDeadlineCompleted(){
    	return _undoDeadlineCompletedSet.removeFirst();
    }
    
    public SortedSet<EventTask> getUndoEventCompleted(){
    	return _undoEventCompletedSet.removeFirst();
    }
    
    public void addRedo(SortedSet<TodoTask> uncompletedTodoTasks, 
    		SortedSet<DeadlineTask> uncompletedDeadlineTasks,
    		SortedSet<EventTask> uncompletedEventTasks,
    		SortedSet<TodoTask> completedTodoTasks,
    		SortedSet<DeadlineTask> completedDeadlineTasks,
    		SortedSet<EventTask> completedEventTasks){
    	
    	Cloner cloner = new Cloner();
    	SortedSet<TodoTask> cloneUncompletedTodoTasks = cloner.deepClone(uncompletedTodoTasks);
    	SortedSet<DeadlineTask> cloneUncompletedDeadlineTasks = cloner.deepClone(uncompletedDeadlineTasks);
    	SortedSet<EventTask> cloneUncompletedEventTasks = cloner.deepClone(uncompletedEventTasks);

    	SortedSet<TodoTask> cloneCompletedTodoTasks = cloner.deepClone(completedTodoTasks);
    	SortedSet<DeadlineTask> cloneCompletedDeadlineTasks = cloner.deepClone(completedDeadlineTasks);
    	SortedSet<EventTask> cloneCompletedEventTasks = cloner.deepClone(completedEventTasks);
    	
    	_redoTodoUncompletedSet.addFirst(cloneUncompletedTodoTasks);
    	_redoDeadlineUncompletedSet.addFirst(cloneUncompletedDeadlineTasks);
    	_redoEventUncompletedSet.addFirst(cloneUncompletedEventTasks);
    	
    	_redoTodoCompletedSet.addFirst(cloneCompletedTodoTasks);
    	_redoDeadlineCompletedSet.addFirst(cloneCompletedDeadlineTasks);
    	_redoEventCompletedSet.addFirst(cloneCompletedEventTasks); 
    }
    
    public SortedSet<TodoTask> getRedoTodoUncompleted(){
    	return _redoTodoUncompletedSet.removeFirst();
    }
    
    public SortedSet<DeadlineTask> getRedoDeadlineUncompleted(){
    	return _redoDeadlineUncompletedSet.removeFirst();
    }
    
    public SortedSet<EventTask> getRedoEventUncompleted(){
    	return _redoEventUncompletedSet.removeFirst();
    }
    
    public SortedSet<TodoTask> getRedoTodoCompleted(){
    	return _redoTodoCompletedSet.removeFirst();
    }
    
    public SortedSet<DeadlineTask> getRedoDeadlineCompleted(){
    	return _redoDeadlineCompletedSet.removeFirst();
    }
    
    public SortedSet<EventTask> getRedoEventCompleted(){
    	return _redoEventCompletedSet.removeFirst();
    }
    
    public void clearRedo(){
    	_redoTodoUncompletedSet.clear();
    	_redoDeadlineUncompletedSet.clear();
    	_redoEventUncompletedSet.clear();
    	
    	_redoTodoCompletedSet.clear();
    	_redoDeadlineCompletedSet.clear();
    	_redoEventCompletedSet.clear(); 
    }

    public boolean isEmptyUndo(){
    	return _undoTodoUncompletedSet.isEmpty()&&
    			_undoDeadlineUncompletedSet.isEmpty()&&
    			_undoEventUncompletedSet.isEmpty()&&    	
    			_undoTodoCompletedSet.isEmpty()&&
    			_undoDeadlineCompletedSet.isEmpty()&&
    			_undoEventCompletedSet.isEmpty(); 
    }

    public boolean isEmptyRedo(){
    	return _redoTodoUncompletedSet.isEmpty()&&
    			_redoDeadlineUncompletedSet.isEmpty()&&
    			_redoEventUncompletedSet.isEmpty()&&    	
    			_redoTodoCompletedSet.isEmpty()&&
    			_redoDeadlineCompletedSet.isEmpty()&&
    			_redoEventCompletedSet.isEmpty();
    }
}
