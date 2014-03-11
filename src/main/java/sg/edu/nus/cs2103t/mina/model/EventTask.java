package sg.edu.nus.cs2103t.mina.model;

import java.util.Date;

/**
 * Appointment task
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class EventTask extends Task<EventTask> implements Comparable<EventTask> {

    private static final long serialVersionUID = 5531321810458646971L;
    protected Date _startTime;
    protected Date _endTime;

    public EventTask(String description, Date start, Date end) {
        super(TaskType.EVENT, description);
        _startTime = start;
        _endTime = end;
    }

    public EventTask(String description, Date start, Date end, char priority) {
        super(TaskType.EVENT, description);
        _startTime = start;
        _endTime = end;
        _priority = priority;
    }

    public int compareTo(EventTask otherTask) {
        Date currEventStart = _startTime;
        Date otherEventStart = otherTask.getStartTime();

        if (currEventStart.before(otherEventStart)) {
            return -1;
        } else if (currEventStart.after(otherEventStart)) {
            return 1;
        } else {
            return super.compareTo((Task<?>) otherTask);
        }
    }

    public Date getStartTime() {
        return _startTime;
    }

    public void setStartTime(Date startTime) {
        _startTime = startTime;
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
        sb.append(" start time(");
        sb.append(_startTime.toString());
        sb.append(") end time(");
        sb.append(_endTime.toString());
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object other) {
    	if(other instanceof EventTask) {
    		EventTask otherEvent = (EventTask) other;
    		return this.compareTo(otherEvent)==0;
    	} else {
    		return false;
    	}
    }
    
}
