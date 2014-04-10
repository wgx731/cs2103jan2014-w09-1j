package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * 
 * Parameters for TaskDAO
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public class TaskSetDataParameter {

    private SortedSet<? extends Task<?>> _taskSet;
    private TaskType _taskType;
    private boolean _isCompleted;

    public TaskSetDataParameter(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {
        _taskSet = taskSet;
        _taskType = taskType;
        _isCompleted = isCompleted;
    }

    public SortedSet<? extends Task<?>> getTaskSet() {
        return _taskSet;
    }

    public TaskType getTaskType() {
        return _taskType;
    }

    public boolean isCompleted() {
        return _isCompleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskSetDataParameter other = (TaskSetDataParameter) obj;
        if (_isCompleted != other._isCompleted)
            return false;
        if (_taskType != other._taskType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SyncDataParameter [_taskSet=" + _taskSet + ", _taskType="
                + _taskType + ", _isCompleted=" + _isCompleted + "]";
    }

}
