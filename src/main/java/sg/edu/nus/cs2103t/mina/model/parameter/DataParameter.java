/**
 * 
 */
package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.Date;

import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * @author wgx731
 * 
 */
public class DataParameter {

    private String _description;
    private char _priority;
    private Date _start;
    private Date _end;
    private TaskType _original;
    private TaskType _new;
    private int _taskID;

    // Constructor
    // default constructor
    public DataParameter() {
        _description = null;
        _priority = 'M';
        _start = null;
        _end = null;
        _original = null;
        _new = null;
        _taskID = -1;
    }

    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id) {
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
        return _original;
    }

    public TaskType getNewTaskType() {
        return _new;
    }

    public int getTaskID() {
        return _taskID;
    }

    // Set methods
    public void setDescription(String des) {
        _description = des;
    }

    public void setPriority(char pri) {
        _priority = pri;
    }

    public void setStartDate(Date start) {
        _start = start;
    }

    public void setEndDate(Date end) {
        _end = end;
    }

    public void setOriginalTaskType(TaskType origTaskType) {
        _original = origTaskType;
    }

    public void setNewTaskType(TaskType newTaskType) {
        _new = newTaskType;
    }

    public void setTaskID(int id) {
        _taskID = id;
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
        if (_new != other._new)
            return false;
        if (_original != other._original)
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

}
