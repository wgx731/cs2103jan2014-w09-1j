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
public class SyncDataParameter {

    private SortedSet<? extends Task<?>> _taskSet;
    private TaskType _taskType;
    private boolean _isCompleted;

    public SyncDataParameter(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {
        super();
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

}
