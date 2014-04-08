package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.util.LinkedList;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandProcessor.CommandType;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

public class CommandHistory {
	
	private LinkedList<CommandType> _undoCommand;
	private LinkedList<DataParameter> _undoParameter;
	private LinkedList<CommandType> _redoCommand;
	private LinkedList<DataParameter> _redoParameter;
	private FilterParameter _latestFilter;
		
	public CommandHistory(){
		_undoCommand = new LinkedList<CommandType>();
		_undoParameter = new LinkedList<DataParameter>();
		_redoCommand = new LinkedList<CommandType>();
		_redoParameter = new LinkedList<DataParameter>();
		_latestFilter = new FilterParameter();
	}
    
	public void updateLatestFilter(FilterParameter filterParam){
		_latestFilter = filterParam;
	}
	
	public FilterParameter getLatestFilter(){
		return _latestFilter;
	}
	
    public void addUndo(CommandType cmdType, DataParameter undoParam){
    	_undoCommand.push(cmdType);
    	_undoParameter.push(undoParam);
    	if (_undoCommand.size()==6&&_undoParameter.size()==6){
    		_undoCommand.removeLast();
    		_undoParameter.removeLast();
    	}
    }
    
    public CommandType getUndoCommand(){
    	return _undoCommand.pop();
    }
    
    public DataParameter getUndoParam(){
    	return _undoParameter.pop();
    }
    
    public void addRedo(CommandType cmdType, DataParameter redoParam){
    	_redoCommand.push(cmdType);
    	_redoParameter.push(redoParam);
    }
    
    public CommandType getRedoCommand(){
    	return _redoCommand.pop();
    }
    
    public DataParameter getRedoParam(){
    	return _redoParameter.pop();
    }
    
    public void clearRedo(){
    	_redoCommand.clear();
    	_redoParameter.clear();
    }
    
    public boolean isEmptyUndo(){
    	return _undoCommand.isEmpty();
    }
    
    public boolean isEmptyRedo(){
    	return _redoCommand.isEmpty();
    }
}
