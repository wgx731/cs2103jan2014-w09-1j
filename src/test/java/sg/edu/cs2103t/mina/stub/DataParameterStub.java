package sg.edu.cs2103t.mina.stub;

import sg.edu.nus.cs2103t.mina.model.TaskType;

import java.util.Date;

public class DataParameterStub {
	private String _description;
	private char _priority;
	private Date _start;
	private Date _end;
	private TaskType _original;
	private TaskType _new;
	private int _taskID;
	
	// Constructor
	// default constructor
	public DataParameterStub(){
		_description = null;
		_priority = 'M';
		_start = null;
		_end = null;
		_original = null;
		_new = null;
		_taskID = -1;
	}
	
	public DataParameterStub(String des, char pri, Date start, Date end, TaskType origType, TaskType newType, int id){
		_description = des;
		_priority = pri;
		_start = start;
		_end = end;
		_original = origType;
		_new = newType;
		_taskID = id;
	}
	
	// Methods
	// Get methods
	public String getDescription(){
		return _description;
	}
	
	public char getPriority(){
		return _priority;
	}

	public Date getStartDate(){
		return _start;
	}
	
	public Date getEndDate(){
		return _end;
	}
	
	public TaskType getOriginalTaskType(){
		return _original;
	}
	
	public TaskType getNewTaskType(){
		return _new;
	}
	
	public int getTaskID(){
		return _taskID;
	}
	
	// Set methods
	public void setDescription(String des){
		_description = des;
	}
	
	public void setPriority(char pri){
		_priority = pri;
	}
	
	public void setStartDate(Date start){
		_start = start;
	}
	
	public void setEndDate(Date end){
		_end = end;
	}
	
	public void setOriginalTaskType(TaskType origTaskType){
		_original = origTaskType;
	}
	
	public void setNewTaskType(TaskType newTaskType){
		_new = newTaskType;
	}
	
	public void setTaskID(int id){
		_taskID = id;
	}
	
	@Override
	public boolean equals(final Object o) {
	    if (!(o instanceof DataParameterStub))
	        return false;
	    final DataParameterStub om = (DataParameterStub)o;
	    	return (((om.getDescription()==null&&_description==null)||om.getDescription().equals(_description))
	    			&& om.getOriginalTaskType()==_original
	    			&& om.getNewTaskType()==_new
	    			&& ((om.getStartDate()==null&&_start==null)||om.getStartDate().equals(_start))
	    			&& ((om.getEndDate()==null&&_end==null)||om.getEndDate().equals(_end))
	    			&& om.getPriority()==_priority
	    			&& om.getTaskID()==_taskID);
	}
}
