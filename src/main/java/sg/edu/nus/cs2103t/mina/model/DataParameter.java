package sg.edu.nus.cs2103t.mina.model;

import java.util.Date;

import sg.edu.cs2103t.mina.stub.DataParameterStub;

public class DataParameter {
	private String _description;
	private char _priority;
	private Date _start;
	private Date _end;
	private TaskType _originalTaskType;
	private TaskType _newTaskType;
	private int _taskID;
	
	/**
	 * Constructors for DataParameter
	 */
	// default constructor
	public DataParameter(){
		setDescription(null);
		setPriority('M');
		setStart(null);
		setEnd(null);
		setOriginalTaskType(null);
		setNewTaskType(null);
		setTaskId(-1);
	}
	
	public DataParameter(String des, char pri, Date start, Date end, TaskType origType, TaskType newType, int id){
		setDescription(des);
		setPriority(pri);
		setStart(start);
		setEnd(end);
		setOriginalTaskType(origType);
		setNewTaskType(newType);
		setTaskId(id);
	}
	
	/**
	 * This method checks the existing parameters within DataParameter and
	 * infers the possible TaskType that it can model after. If there are not
	 * enough parameters, it returns and UNKNOWN type.
	 * 
	 * @return TaskType inferredTaskType.
	 */
	public TaskType determineTaskType() {
		
		if (_description != null) {
			if (_end != null) {
				if (_start != null) {
					return TaskType.EVENT;
				}
				return TaskType.DEADLINE;
			}
			return TaskType.TODO;
		}
		
		return TaskType.UNKOWN;
	}

	/**
	 * This method takes in an existing task, and loads all of its old
	 * parameters. Caution: overrides any existing data if they existed.
	 */
	public void loadOldTask(Task<?> taskToLoad) {
		setDescription(taskToLoad.getDescription());
		setPriority(taskToLoad.getPriority());
		//setTaskID(taskToLoad.getId());
		//TODO find a way to map the UUID for _id in task to a human readable ID value
		setOriginalTaskType(taskToLoad.getType());
		
		setStart(null);
		setEnd(null);
		
	}

	/**
	 * For the modify function. Call this function after you call
	 * loadOldTask(Task taskToModify).Takes in the values that user wants to
	 * modify, and adds it onto existing values.
	 */
	public void loadNewParameters(DataParameter modifyParam) {
		if (modifyParam.getDescription() != null) {
			setDescription(modifyParam.getDescription());
		}
		if (modifyParam.getPriority() != 'M') {
			setPriority(modifyParam.getPriority());
		}
		if (modifyParam.getStart() != null) {
			setStart(modifyParam.getStart());
		}
		if (modifyParam.getEnd() != null) {
			setEnd(modifyParam.getEnd());
		}
		if (modifyParam.getOriginalTaskType() != null) {
			setOriginalTaskType(modifyParam.getOriginalTaskType());
		} else {
			// there is an error, do something!!
		}
		if (modifyParam.getTaskId() != -1) {
			setTaskId(modifyParam.getTaskId());
		}
		
		// deduce the new task type from the existing parameters
		setNewTaskType(this.determineTaskType());
	}
	
	/**
	 * Overriding the comparator.
	 */
	@Override
	public boolean equals(final Object o) {
	    if (!(o instanceof DataParameterStub))
	        return false;
	    final DataParameterStub om = (DataParameterStub)o;
	    	return (((om.getDescription()==null&&_description==null)||om.getDescription().equals(_description))
	    			&& om.getOriginalTaskType()==_originalTaskType
	    			&& om.getNewTaskType()==_newTaskType
	    			&& ((om.getStartDate()==null&&_start==null)||om.getStartDate().equals(_start))
	    			&& ((om.getEndDate()==null&&_end==null)||om.getEndDate().equals(_end))
	    			&& om.getPriority()==_priority
	    			&& om.getTaskID()==_taskID);
	}
	
	/** get Methods */
	public String getDescription() {
		return _description;
	}

	public char getPriority() {
		return _priority;
	}
	
	public Date getStart() {
		return _start;
	}
	
	public Date getEnd() {
		return _end;
	}
	
	public TaskType getOriginalTaskType() {
		return _originalTaskType;
	}
	
	public TaskType getNewTaskType() {
		return _newTaskType;
	}
	
	public int getTaskId() {
		return _taskID;
	}
	
	/** set Methods */
	public void setDescription(String description) {
		_description = description;
	}

	public void setPriority(char priority) {
		_priority = priority;
	}

	public void setStart(Date start) {
		_start = start;
	}

	public void setEnd(Date end) {
		_end = end;
	}

	public void setOriginalTaskType(TaskType originalTaskType) {
		_originalTaskType = originalTaskType;
	}

	public void setNewTaskType(TaskType newTaskType) {
		_newTaskType = newTaskType;
	}

	public void setTaskId(int taskID) {
		_taskID = taskID;
	}
	
}
