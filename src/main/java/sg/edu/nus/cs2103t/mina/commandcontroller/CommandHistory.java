package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.util.LinkedList;
import java.util.SortedSet;

import com.rits.cloning.Cloner;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

/**
 * History class uses for Undo/Redo
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
//@author A0099324X

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
	
	private LinkedList<FilterParameter> _undoFilterParameterBefore;
	private LinkedList<FilterParameter> _undoFilterParameterAfter;
	private LinkedList<FilterParameter> _redoFilterParameterBefore;
	private LinkedList<FilterParameter> _redoFilterParameterAfter;
	
	private LinkedList<Integer> _undoTabSelectedBefore;
	private LinkedList<Integer> _undoTabSelectedAfter;
	private LinkedList<Integer> _redoTabSelectedBefore;
	private LinkedList<Integer> _redoTabSelectedAfter;
	
	private LinkedList<int[]> _undoPageChangedBefore;
	private LinkedList<int[]> _undoPageChangedAfter;
	private LinkedList<int[]> _redoPageChangedBefore;
	private LinkedList<int[]> _redoPageChangedAfter;
	
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
		
		_undoFilterParameterBefore = new LinkedList<FilterParameter>();
		_undoFilterParameterAfter = new LinkedList<FilterParameter>();
		_redoFilterParameterBefore = new LinkedList<FilterParameter>();
		_redoFilterParameterAfter = new LinkedList<FilterParameter>();
		
		_undoTabSelectedBefore = new LinkedList<Integer>();
		_undoTabSelectedAfter = new LinkedList<Integer>();
		_redoTabSelectedBefore = new LinkedList<Integer>();
		_redoTabSelectedAfter = new LinkedList<Integer>();
		
		_undoPageChangedBefore = new LinkedList<int[]>();
		_undoPageChangedAfter = new LinkedList<int[]>();
		_redoPageChangedBefore = new LinkedList<int[]>();
		_redoPageChangedAfter = new LinkedList<int[]>();
	}
    
	public void updateLatestFilter(FilterParameter filterParam){
		Cloner cloner = new Cloner();
		_latestFilter = cloner.deepClone(filterParam);
	}
	
	public FilterParameter getLatestFilter(){
		Cloner cloner = new Cloner();
		FilterParameter cloneFilter = cloner.deepClone(_latestFilter);
		return cloneFilter;
	}
	
    public void addUndo(SortedSet<TodoTask> uncompletedTodoTasks, 
    		SortedSet<DeadlineTask> uncompletedDeadlineTasks,
    		SortedSet<EventTask> uncompletedEventTasks,
    		SortedSet<TodoTask> completedTodoTasks,
    		SortedSet<DeadlineTask> completedDeadlineTasks,
    		SortedSet<EventTask> completedEventTasks,
    		FilterParameter filterParam,
    		int tabSelected,
    		int eventPage, int deadlinePage, int todoPage){
    	
    	Cloner cloner = new Cloner();
    	SortedSet<TodoTask> cloneUncompletedTodoTasks = cloner.deepClone(uncompletedTodoTasks);
    	SortedSet<DeadlineTask> cloneUncompletedDeadlineTasks = cloner.deepClone(uncompletedDeadlineTasks);
    	SortedSet<EventTask> cloneUncompletedEventTasks = cloner.deepClone(uncompletedEventTasks);

    	SortedSet<TodoTask> cloneCompletedTodoTasks = cloner.deepClone(completedTodoTasks);
    	SortedSet<DeadlineTask> cloneCompletedDeadlineTasks = cloner.deepClone(completedDeadlineTasks);
    	SortedSet<EventTask> cloneCompletedEventTasks = cloner.deepClone(completedEventTasks);
    	
    	FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
    	
    	int[] pageNumbers = new int[]{eventPage, deadlinePage, todoPage};
    	
    	_undoTodoUncompletedSet.addFirst(cloneUncompletedTodoTasks);
    	_undoDeadlineUncompletedSet.addFirst(cloneUncompletedDeadlineTasks);
    	_undoEventUncompletedSet.addFirst(cloneUncompletedEventTasks);
    	
    	_undoTodoCompletedSet.addFirst(cloneCompletedTodoTasks);
    	_undoDeadlineCompletedSet.addFirst(cloneCompletedDeadlineTasks);
    	_undoEventCompletedSet.addFirst(cloneCompletedEventTasks);
    	
    	_undoFilterParameterBefore.addFirst(cloneFilterParam);
    	_undoTabSelectedBefore.addFirst(tabSelected);
    	_undoPageChangedBefore.addFirst(pageNumbers);
    	
    	if (_undoTodoUncompletedSet.size()==6&&_undoDeadlineUncompletedSet.size()==6&&
    			_undoEventUncompletedSet.size()==6&&_undoTodoCompletedSet.size()==6&&
    			_undoDeadlineCompletedSet.size()==6&&_undoEventCompletedSet.size()==6&&
    			_undoFilterParameterBefore.size()==6&&_undoTabSelectedBefore.size()==6&&
    			_undoPageChangedBefore.size()==6){
    		
    		_undoTodoUncompletedSet.removeLast();
        	_undoDeadlineUncompletedSet.removeLast();
        	_undoEventUncompletedSet.removeLast();
        	
        	_undoTodoCompletedSet.removeLast();
        	_undoDeadlineCompletedSet.removeLast();
        	_undoEventCompletedSet.removeLast();
        	
        	_undoFilterParameterBefore.removeLast();
        	_undoTabSelectedBefore.removeLast();
        	_undoPageChangedBefore.removeLast();
    	}
    }
    
    public void addUndoAfter(FilterParameter filterParam, int tabSelected,
    		int eventPage, int deadlinePage, int todoPage){
    	int[] pageNumbers = new int[]{eventPage, deadlinePage, todoPage};
    	Cloner cloner = new Cloner();
    	FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
    	
    	_undoFilterParameterAfter.addFirst(cloneFilterParam);
    	_undoTabSelectedAfter.addFirst(tabSelected);
    	_undoPageChangedAfter.addFirst(pageNumbers);
    	if (_undoFilterParameterAfter.size()==6&&
    		_undoTabSelectedAfter.size()==6&&
    		_undoPageChangedAfter.size()==6){
    		_undoFilterParameterAfter.removeLast();
    		_undoTabSelectedAfter.removeLast();
        	_undoPageChangedAfter.removeLast();
    	}
    }
    
    public void removeLatestUndo(){
    	_undoTodoUncompletedSet.removeFirst();
    	_undoDeadlineUncompletedSet.removeFirst();
    	_undoEventUncompletedSet.removeFirst();
    	
    	_undoTodoCompletedSet.removeFirst();
    	_undoDeadlineCompletedSet.removeFirst();
    	_undoEventCompletedSet.removeFirst();
    	
    	_undoFilterParameterBefore.removeFirst();
    	_undoTabSelectedBefore.removeFirst();
    	_undoPageChangedBefore.removeFirst();
    }
    
    public void removeLatestRedo(){
    	_redoTodoUncompletedSet.removeFirst();
    	_redoDeadlineUncompletedSet.removeFirst();
    	_redoEventUncompletedSet.removeFirst();
    	
    	_redoTodoCompletedSet.removeFirst();
    	_redoDeadlineCompletedSet.removeFirst();
    	_redoEventCompletedSet.removeFirst();
    	
    	_redoFilterParameterBefore.removeFirst();
    	_redoTabSelectedBefore.removeFirst();
    	_redoPageChangedBefore.removeFirst();
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
    
    public FilterParameter getUndoFilterParameter(){
    	return _undoFilterParameterBefore.removeFirst();
    }
    
    public int getUndoTabSelected(){
    	return _undoTabSelectedBefore.removeFirst();
    }
    
    public int[] getUndoPageChanged(){
    	return _undoPageChangedBefore.removeFirst();
    }
    
    public FilterParameter getUndoFilterParameterAfter(){
    	return _undoFilterParameterAfter.removeFirst();
    }
    
    public int getUndoTabSelectedAfter(){
    	return _undoTabSelectedAfter.removeFirst();
    }
    
    public int[] getUndoPageChangedAfter(){
    	return _undoPageChangedAfter.removeFirst();
    }
    
    public void addRedo(SortedSet<TodoTask> uncompletedTodoTasks, 
    		SortedSet<DeadlineTask> uncompletedDeadlineTasks,
    		SortedSet<EventTask> uncompletedEventTasks,
    		SortedSet<TodoTask> completedTodoTasks,
    		SortedSet<DeadlineTask> completedDeadlineTasks,
    		SortedSet<EventTask> completedEventTasks,
    		FilterParameter filterParam,
    		int tabSelected,
    		int eventPage, int deadlinePage, int todoPage){
    	
    	Cloner cloner = new Cloner();
    	SortedSet<TodoTask> cloneUncompletedTodoTasks = cloner.deepClone(uncompletedTodoTasks);
    	SortedSet<DeadlineTask> cloneUncompletedDeadlineTasks = cloner.deepClone(uncompletedDeadlineTasks);
    	SortedSet<EventTask> cloneUncompletedEventTasks = cloner.deepClone(uncompletedEventTasks);

    	SortedSet<TodoTask> cloneCompletedTodoTasks = cloner.deepClone(completedTodoTasks);
    	SortedSet<DeadlineTask> cloneCompletedDeadlineTasks = cloner.deepClone(completedDeadlineTasks);
    	SortedSet<EventTask> cloneCompletedEventTasks = cloner.deepClone(completedEventTasks);
    	
    	FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
    	
    	int[] pageNumbers = new int[]{eventPage, deadlinePage, todoPage};
    	
    	_redoTodoUncompletedSet.addFirst(cloneUncompletedTodoTasks);
    	_redoDeadlineUncompletedSet.addFirst(cloneUncompletedDeadlineTasks);
    	_redoEventUncompletedSet.addFirst(cloneUncompletedEventTasks);
    	
    	_redoTodoCompletedSet.addFirst(cloneCompletedTodoTasks);
    	_redoDeadlineCompletedSet.addFirst(cloneCompletedDeadlineTasks);
    	_redoEventCompletedSet.addFirst(cloneCompletedEventTasks); 
    	
    	_redoFilterParameterBefore.addFirst(cloneFilterParam);
    	_redoTabSelectedBefore.addFirst(tabSelected);
    	_redoPageChangedBefore.addFirst(pageNumbers);
    }
    
    public void addRedoAfter(FilterParameter filterParam, int tabSelected,
    		int eventPage, int deadlinePage, int todoPage){
    	int[] pageNumbers = new int[]{eventPage, deadlinePage, todoPage};
    	Cloner cloner = new Cloner();
    	FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
    	
    	_redoFilterParameterAfter.addFirst(cloneFilterParam);
    	_redoTabSelectedAfter.addFirst(tabSelected);
    	_redoPageChangedAfter.addFirst(pageNumbers);
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
    
    public FilterParameter getRedoFilterParameter(){
    	return _redoFilterParameterBefore.removeFirst();
    }
    
    public int getRedoTabSelected(){
    	return _redoTabSelectedBefore.removeFirst();
    }
    
    public int[] getRedoPageChanged(){
    	return _redoPageChangedBefore.removeFirst();
    }
    
    public FilterParameter getRedoFilterParameterAfter(){
    	return _redoFilterParameterAfter.removeFirst();
    }
    
    public int getRedoTabSelectedAfter(){
    	return _redoTabSelectedAfter.removeFirst();
    }
    
    public int[] getRedoPageChangedAfter(){
    	return _redoPageChangedAfter.removeFirst();
    }
    
    public void clearRedo(){
    	_redoTodoUncompletedSet.clear();
    	_redoDeadlineUncompletedSet.clear();
    	_redoEventUncompletedSet.clear();
    	
    	_redoTodoCompletedSet.clear();
    	_redoDeadlineCompletedSet.clear();
    	_redoEventCompletedSet.clear(); 
    	
    	_redoFilterParameterBefore.clear();
    	_redoTabSelectedBefore.clear();
    	_redoPageChangedBefore.clear();
    	
    	_redoFilterParameterAfter.clear();
    	_redoTabSelectedAfter.clear();
    	_redoPageChangedAfter.clear();
    }

    public boolean isEmptyUndo(){
    	return _undoTodoUncompletedSet.isEmpty()&&
    			_undoDeadlineUncompletedSet.isEmpty()&&
    			_undoEventUncompletedSet.isEmpty()&&    	
    			_undoTodoCompletedSet.isEmpty()&&
    			_undoDeadlineCompletedSet.isEmpty()&&
    			_undoEventCompletedSet.isEmpty()&&
    			_undoFilterParameterBefore.isEmpty()&&
    			_undoTabSelectedBefore.isEmpty()&&
    	    	_undoPageChangedBefore.isEmpty()&&
    	    	_undoFilterParameterAfter.isEmpty()&&
    			_undoTabSelectedAfter.isEmpty()&&
    	    	_undoPageChangedAfter.isEmpty();
    }

    public boolean isEmptyRedo(){
    	return _redoTodoUncompletedSet.isEmpty()&&
    			_redoDeadlineUncompletedSet.isEmpty()&&
    			_redoEventUncompletedSet.isEmpty()&&    	
    			_redoTodoCompletedSet.isEmpty()&&
    			_redoDeadlineCompletedSet.isEmpty()&&
    			_redoEventCompletedSet.isEmpty()&&
    			_redoFilterParameterBefore.isEmpty()&&
    			_redoTabSelectedBefore.isEmpty()&&
    	    	_redoPageChangedBefore.isEmpty()&&
    			_redoFilterParameterAfter.isEmpty()&&
    			_redoTabSelectedAfter.isEmpty()&&
    	    	_redoPageChangedAfter.isEmpty();
    }
}
