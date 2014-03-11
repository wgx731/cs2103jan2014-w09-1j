package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.Date;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

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
    public DataParameter() {
        setDescription(null);
        setPriority('M');
        setStartDate(null);
        setEndDate(null);
        setOriginalTaskType(null);
        setNewTaskType(null);
        setTaskID(-1);
    }

    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);
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
		
		if(taskToLoad.getType() == TaskType.DEADLINE) {
		    DeadlineTask taskToLoadDeadline = (DeadlineTask) taskToLoad;
		    setEndDate(taskToLoadDeadline.getEndTime());
		}
		if(taskToLoad.getType() == TaskType.EVENT) {
		    EventTask taskToLoadEvent = (EventTask) taskToLoad;
		    setEndDate(taskToLoadEvent.getEndTime());
            setStartDate(taskToLoadEvent.getStartTime());
		}
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
        if (modifyParam.getStartDate() != null) {
            setStartDate(modifyParam.getStartDate());
        }
        if (modifyParam.getEndDate() != null) {
            setEndDate(modifyParam.getEndDate());
        }
        if (modifyParam.getOriginalTaskType() != null) {
            setOriginalTaskType(modifyParam.getOriginalTaskType());
        } else {
            // there is an error, do something!!
        }
        if (modifyParam.getTaskId() != -1) {
            setTaskID(modifyParam.getTaskId());
        }

        // deduce the new task type from the existing parameters
        setNewTaskType(this.determineTaskType());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataParameter other = (DataParameter) obj;
        if (_description == null) {
            if (other._description != null)
                return false;
        } else if (!_description.equals(other._description))
            return false;
        if (_end == null) {
            if (other._end != null)
                return false;
        } else if (!_end.equals(other._end))
            return false;
        if (_newTaskType != other._newTaskType)
            return false;
        if (_originalTaskType != other._originalTaskType)
            return false;
        if (_priority != other._priority)
            return false;
        if (_start == null) {
            if (other._start != null)
                return false;
        } else if (!_start.equals(other._start))
            return false;
        if (_taskID != other._taskID)
            return false;
        return true;
    }

    /** get Methods */
    public String getDescription() {
        return _description;
    }

    public char getPriority() {
        return _priority;
    }

    public Date getStartDate() {
        return _start;
    }

    public Date getEndDate() {
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

    public void setStartDate(Date start) {
        _start = start;
    }

    public void setEndDate(Date end) {
        _end = end;
    }

    public void setOriginalTaskType(TaskType originalTaskType) {
        _originalTaskType = originalTaskType;
    }

    public void setNewTaskType(TaskType newTaskType) {
        _newTaskType = newTaskType;
    }

    public void setTaskID(int taskID) {
        _taskID = taskID;
    }

}
