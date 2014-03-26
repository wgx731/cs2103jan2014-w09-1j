package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.Date;
import java.util.List;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TimePair;

public class DataParameter {
	/* for all tasks */
	private String _description;
	private char _priority;
	private Date _start;
	private Date _end;
	private TaskType _originalTaskType;
	private TaskType _newTaskType;
	private int _taskID;

	/* for recurring and block tasks */
	private String _tag; //either 'RECUR' or 'BLOCK'
	private long _freqInMilliSec; // lower bound: every hour (i.e. 60*60*1000ms)
	private List<TimePair> _timeSlots;
	private boolean _modifyAll;

	private Task<?> _taskObject;

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
		setTaskObject(null);

		setTag(null);
		setFreqInMilliSec(7 * 24 * 60 * 60 * 1000); // default: every week
		setTimeSlots(null);
		setModifyAll(false);

	}

	// if task is not recurring or block, for adding task
	public DataParameter(String des, char pri, Date start, Date end,
			TaskType origType, TaskType newType, int id) {
		setDescription(des);
		setPriority(pri);
		setStartDate(start);
		setEndDate(end);
		setOriginalTaskType(origType);
		setNewTaskType(newType);
		setTaskID(id);

		setTag(null);
		setFreqInMilliSec(7 * 24 * 60 * 60 * 1000); // default: every week
		setTimeSlots(null);
		setModifyAll(false);
	}

	// if task is not recurring or block, for deleting, modifying, marking tasks
	// 1st alternative
	public DataParameter(Task<?> taskObj) {
		setDescription(taskObj.getDescription());
		setPriority(taskObj.getPriority());
		setOriginalTaskType(taskObj.getType());
		setNewTaskType(taskObj.getType());
		// setTaskID(taskObj.getId());
		setTaskObject(taskObj);

		if (taskObj.getType() == TaskType.DEADLINE) {
			DeadlineTask deadlineTaskObj = (DeadlineTask) taskObj;

			setEndDate(deadlineTaskObj.getEndTime());
		}

		if (taskObj.getType() == TaskType.EVENT) {
			EventTask eventTaskObj = (EventTask) taskObj;

			setEndDate(eventTaskObj.getEndTime());
			setStartDate(eventTaskObj.getStartTime());
		}

		setTag(null);
		setFreqInMilliSec(7 * 24 * 60 * 60 * 1000); // default: every week
		setTimeSlots(null);
		setModifyAll(false);
	}

	// if task is not recurring or block, for deleting, modifying, marking tasks
	// 2nd alternative
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

		setTag(null);
		setFreqInMilliSec(7 * 24 * 60 * 60 * 1000); // default: every week
		setTimeSlots(null);
		setModifyAll(false);
	}

	// for adding or modifying recurring or block tasks
	public DataParameter(String des, char pri, Date start, Date end,
			TaskType origType, TaskType newType, int id, Task<?> taskObj,
			String tag, long freqInMilliSec, List<TimePair> timeSlots,
			boolean isModifyAll) throws Exception {
		if (tag.equals("BLOCK")) {
			assert(origType.equals(TaskType.EVENT) || newType.equals(TaskType.EVENT));
			createBlockParameters(des, pri, start, end, origType, newType, id,
					taskObj, tag, freqInMilliSec, timeSlots, isModifyAll);

		} else if (tag.equals("RECUR")) {
			assert(!origType.equals(TaskType.TODO) || !newType.equals(TaskType.TODO));
			createRecurParameters(des, pri, start, end, origType, newType, id,
					taskObj, tag, freqInMilliSec, timeSlots, isModifyAll);

		} else {
			throw new Exception("invalid tag used by CC");
		}

	}

	// only for event TaskTypes
	private void createBlockParameters(String des, char pri, Date start,
			Date end, TaskType origType, TaskType newType, int id,
			Task<?> taskObj, String tag, long freqInMilliSec,
			List<TimePair> timeSlots, boolean isModifyAll) {
		setDescription(des);
		setPriority(pri);
		setStartDate(start);
		setEndDate(end);
		setOriginalTaskType(origType);
		setNewTaskType(newType);
		setTaskID(id);
		
		// parameters specific to EventTask
		EventTask eventTaskObj = (EventTask) taskObj;
		setEndDate(end == null ? eventTaskObj.getEndTime() : end);
		setStartDate(start == null ? eventTaskObj.getStartTime() : start);

		setTag(tag);
		setFreqInMilliSec(freqInMilliSec);
		setTimeSlots(timeSlots);
		setModifyAll(isModifyAll);

	}

	// only for event and deadline TaskTypes
	private void createRecurParameters(String des, char pri, Date start,
			Date end, TaskType origType, TaskType newType, int id,
			Task<?> taskObj, String tag, long freqInMilliSec,
			List<TimePair> timeSlots, boolean isModifyAll) {
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
		setFreqInMilliSec(freqInMilliSec);
		setTimeSlots(timeSlots);
		setModifyAll(isModifyAll);

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
		// TODO find a way to map the UUID for _id in task to a human readable
		// ID value
		setOriginalTaskType(taskToLoad.getType());

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
			assert(false); // shouldn't be null
		}
		if (modifyParam.getNewTaskType() != null) {
			setNewTaskType(modifyParam.getNewTaskType());
		} else {
			assert(false); // shouldn't be null
		}
		if (modifyParam.getTaskId() != -1) {
			setTaskID(modifyParam.getTaskId());
		}

		if (_originalTaskType != _newTaskType) {
			if (_originalTaskType == TaskType.DEADLINE
					&& _newTaskType == TaskType.TODO) {
				//_description += (" by " + _end);
				//_end = null;
			} else if (_originalTaskType == TaskType.EVENT
					&& _newTaskType == TaskType.TODO) {
				//_description += (" from " + _start + " to " + _end);
				//_start = null;
				//_end = null;
			} else if (_originalTaskType == TaskType.EVENT
					&& _newTaskType == TaskType.DEADLINE) {
				//_end = _start;
				//_start = null;
			}

			_originalTaskType = _newTaskType;
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

	public long getFreqInMilliSec() {
		return _freqInMilliSec;
	}

	public boolean isModifyAll() {
		return _modifyAll;
	}

	public List<TimePair> getTimeSlots() {
		return _timeSlots;
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

	public void setFreqInMilliSec(long freqInMilliSec) {
		_freqInMilliSec = freqInMilliSec;
	}

	public void setModifyAll(boolean modifyAll) {
		_modifyAll = modifyAll;
	}

	public void setTimeSlots(List<TimePair> timeSlots) {
		_timeSlots = timeSlots;
	}

}
