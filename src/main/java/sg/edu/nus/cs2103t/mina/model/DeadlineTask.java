package sg.edu.nus.cs2103t.mina.model;

import java.util.Date;

import sg.edu.nus.cs2103t.mina.utils.DateUtil;

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

    public DeadlineTask(TaskType type, String description, String id,
            char priority, Date createdTime, Date lastEditedTime,
            boolean isCompleted, Date end, String tag) {
        super(type, description, id, priority, createdTime, lastEditedTime,
                isCompleted, tag);
        _endTime = end;
    }

    public int compareTo(DeadlineTask otherTask) {
        Date currDeadlineEnd = _endTime;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" by ");
        sb.append(DateUtil.displayDateTime(DateUtil.toCalendar(_endTime)));
        sb.append(" ");
        sb.append(super.isCompleted()?"done":"");
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            if (!(other instanceof DeadlineTask)) {
                return false;
            }
            return compareTo((DeadlineTask) other) == 0;
        }
        return false;
    }
}
