package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.Date;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Stores variables of a new task that a user will want to add or delete, or
 * modified variables of an existing task that a user will want to modify.
 */
// @author A0080412W

public class DataParameter {

    private static final String CLASS_NAME = TaskDataManager.class.getName();

    /* for all tasks */
    private String _description;
    private char _priority;
    private Date _start;
    private Date _end;
    private TaskType _originalTaskType;
    private TaskType _newTaskType;
    private int _taskID;
    private Task<?> _taskObject;

    /* for RECURRING Tasks */
    private String _tag; // either "RECUR" or ""
    private boolean _modifyAll;

    private String _timeType; // refer to field values of CALENDAR
    private int _freqOfTimeType;

    private Date _endRecurOn;

    /**
     * Creates a DataParameter with all values set to their default.
     */
    public DataParameter() {
        setDescription(null);
        setPriority('M');
        setStartDate(null);
        setEndDate(null);
        setOriginalTaskType(null);
        setNewTaskType(null);
        setTaskID(-1);

        setTag("");
        setModifyAll(false);

        setTimeType(null);
        setFreqOfTimeType(0);

        setEndRecurOn(null);

        setTaskObject(null);

    }

    /**
     * Creates a DataParameter to hold variables for a new non-recurring task.
     * 
     * @param des
     * @param pri
     * @param start
     * @param end
     * @param origType
     * @param newType
     * @param id
     */
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTag("");
        setModifyAll(false);

        setTimeType(null);
        setFreqOfTimeType(0);

        setEndRecurOn(null);

        setTaskObject(null);
    }

    /**
     * Creates a DataParameter to hold variables for modifying or deleting an
     * existing non-recurring task.
     * <p>
     * Returns null if the taskObj does not exist.
     * 
     * @param des
     * @param pri
     * @param start
     * @param end
     * @param origType
     * @param newType
     * @param id
     * @param taskObj
     */
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, Task<?> taskObj) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTaskObject(taskObj);

        if (taskObj.getType() == TaskType.DEADLINE) {
            DeadlineTask deadlineTaskObj = (DeadlineTask) taskObj;

            setEndDate(end == null ? deadlineTaskObj.getEndTime() : end);
        }

        if (taskObj.getType() == TaskType.EVENT) {
            EventTask eventTaskObj = (EventTask) taskObj;

            setEndDate(end == null ? eventTaskObj.getEndTime() : end);
            setStartDate(start == null ? eventTaskObj.getStartTime() : start);
        }

        setTag("");
        setModifyAll(false);

        setTimeType(null);
        setFreqOfTimeType(0);
        setEndRecurOn(null);

    }

    /**
     * Creates a DataParameter to hold variables for a new recurring task.
     * <p>
     * A recurring task can only be of TaskType DEADLINE or EVENT.
     * 
     * @param des
     * @param pri
     * @param start
     * @param end
     * @param origType
     * @param newType
     * @param id
     * @param tag
     * @param endRecurOn
     * @param timeType
     * @param freqOfTimeType
     * @throws Exception
     */
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, String tag,
            Date endRecurOn, String timeType, int freqOfTimeType)
            throws Exception {
        assert (!tag.equals(null));

        if (!tag.equals(null) && tag.equals("RECUR")) {
            assert (!newType.equals(TaskType.TODO));
            createAddRecurParameters(des, pri, start, end, origType, newType,
                    id, tag, timeType, freqOfTimeType, endRecurOn);

        } else {
            LogHelper.log(CLASS_NAME, Level.ERROR, "tag: " + tag);

            throw new Exception("invalid tag used");
        }

    }

    /**
     * Creates a DataParameter to hold variables for modifying or deleting an
     * existing recurring task.
     * <p>
     * A recurring task can only be of TaskType DEADLINE or EVENT.
     * <p>
     * Returns null if the taskObj does not exist.
     * 
     * @param des
     * @param pri
     * @param start
     * @param end
     * @param origType
     * @param newType
     * @param id
     * @param tag
     * @param endRecurOn
     * @param timeType
     * @param freqOfTimeType
     * @throws Exception
     */
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, Task<?> taskObj,
            String tag, String timeType, int freqOfTimeType, Date endRecurOn,
            boolean isModifyAll) throws Exception {

        if (taskObj.getTag().contains("RECUR")) {
            assert (!taskObj.getType().equals(null) && !taskObj.getType()
                    .equals(TaskType.TODO) || !newType.equals(null) && !newType
                    .equals(TaskType.TODO));
            createRecurParameters(des, pri, start, end, origType, newType, id,
                    taskObj, tag, timeType, freqOfTimeType, endRecurOn,
                    isModifyAll);

        } else {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "invalid tag: " + taskObj.getTag());

            throw new Exception("invalid tag used");

        }

    }

    // only for event and deadline TaskTypes
    private void createAddRecurParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id, String tag,
            String timeType, int freqOfTimeType, Date endRecurOn) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTag(tag);
        setTimeType(timeType);
        setFreqOfTimeType(freqOfTimeType);
        setEndRecurOn(endRecurOn);

        setModifyAll(false);

    }

    private void createRecurParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id,
            Task<?> taskObj, String tag, String timeType, int freqOfTimeType,
            Date endRecurOn, boolean isModifyAll) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTaskObject(taskObj);

        if (taskObj.getType() == TaskType.DEADLINE) {
            DeadlineTask deadlineTaskObj = (DeadlineTask) taskObj;

            setEndDate(end == null ? deadlineTaskObj.getEndTime() : end);
        }

        if (taskObj.getType() == TaskType.EVENT) {
            EventTask eventTaskObj = (EventTask) taskObj;

            setEndDate(end == null ? eventTaskObj.getEndTime() : end);
            setStartDate(start == null ? eventTaskObj.getStartTime() : start);
        }

        setTag(tag);
        setTimeType(timeType);
        setFreqOfTimeType(freqOfTimeType);
        setEndRecurOn(endRecurOn);

        setTag(tag);
        setModifyAll(isModifyAll);

        setEndRecurOn(endRecurOn);

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

        return TaskType.UNKNOWN;
    }

    /**
     * This method takes in an existing task, and loads all of its old
     * parameters. Caution: overrides any existing data if they existed.
     */
    public void loadOldTask(Task<?> taskToLoad) {
        if (taskToLoad == null) {
            return;
        }

        setDescription(taskToLoad.getDescription());
        setPriority(taskToLoad.getPriority());
        // setTaskID(taskToLoad.getId());
        setOriginalTaskType(taskToLoad.getType());

        setTag(taskToLoad.getTag());

        if (taskToLoad.getType() == TaskType.DEADLINE) {
            DeadlineTask taskToLoadDeadline = (DeadlineTask) taskToLoad;
            setEndDate(taskToLoadDeadline.getEndTime());

        }

        if (taskToLoad.getType() == TaskType.EVENT) {
            EventTask taskToLoadEvent = (EventTask) taskToLoad;
            setEndDate(taskToLoadEvent.getEndTime());
            setStartDate(taskToLoadEvent.getStartTime());

        }
    }

    /**
     * Takes in the values that user wants to modify from another DataParameter
     * variable, and adds it onto existing values.
     */
    public void loadNewParameters(DataParameter modifyParam) {
        if (modifyParam.getDescription() != null) {
            setDescription(modifyParam.getDescription());
        }
        if (modifyParam.getPriority() != _priority) {
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
        }
        if (modifyParam.getNewTaskType() != null) {
            setNewTaskType(modifyParam.getNewTaskType());
        } else {
            setNewTaskType(this._originalTaskType);
        }
        if (modifyParam.getTaskId() != -1) {
            setTaskID(modifyParam.getTaskId());
        }

        if (modifyParam.getTag() != "") {
            setTag(modifyParam.getTag());
        }
        if (modifyParam.getTimeType() != null) {
            setTimeType(modifyParam.getTimeType());
        }
        if (modifyParam.getFreqOfTimeType() != 0) {
            setFreqOfTimeType(modifyParam.getFreqOfTimeType());
        }
        if (modifyParam.getEndRecurOn() != null) {
            setEndRecurOn(modifyParam.getEndRecurOn());
        }

        if (modifyParam.isModifyAll()) {
            setModifyAll(true);
        }
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

    public Task<?> getTaskObject() {
        return _taskObject;
    }

    public String getTag() {
        return _tag;
    }

    public boolean isModifyAll() {
        return _modifyAll;
    }

    public Date getEndRecurOn() {
        return _endRecurOn;
    }

    public int getFreqOfTimeType() {
        return _freqOfTimeType;
    }

    public String getTimeType() {
        return _timeType;
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

    public void setTaskObject(Task<?> taskObject) {
        _taskObject = taskObject;
    }

    public void setTag(String tag) {
        _tag = tag;
    }

    public void setModifyAll(boolean modifyAll) {
        _modifyAll = modifyAll;
    }

    public void setEndRecurOn(Date endRecurOn) {
        _endRecurOn = endRecurOn;
    }

    public void setFreqOfTimeType(int freqOfTimeType) {
        _freqOfTimeType = freqOfTimeType;
    }

    public void setTimeType(String timeType) {
        _timeType = timeType;
    }

}
