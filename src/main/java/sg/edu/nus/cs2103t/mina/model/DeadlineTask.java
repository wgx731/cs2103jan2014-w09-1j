package sg.edu.nus.cs2103t.mina.model;

import java.util.Date;

/**
 * Task with deadline
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class DeadlineTask extends Task<DeadlineTask> implements
        Comparable<DeadlineTask> {

    private static final long serialVersionUID = -8101963187338761520L;

    protected Date _endTime;

    public DeadlineTask(String description, Date end) {
        super(TaskType.DEADLINE, description);
        _endTime = end;
    }

    public DeadlineTask(String description, Date end, char priority) {
        super(TaskType.DEADLINE, description);
        _endTime = end;
        _priority = priority;
    }

    public int compareTo(DeadlineTask otherTask) {
        Date currDeadlineEnd = this._endTime;
        Date otherDeadlineEnd = otherTask.getEndTime();

        if (currDeadlineEnd.before(otherDeadlineEnd)) {
            return -1;
        } else if (currDeadlineEnd.after(otherDeadlineEnd)) {
            return 1;
        } else {
            return super.compareTo((Task<?>) otherTask);
        }
    }

    public Date getEndTime() {
        return _endTime;
    }

    public void setEndTime(Date endTime) {
        _endTime = endTime;
    }

}
