package sg.edu.nus.cs2103t.mina.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Abstract model for Task
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * @param <T>
 */
public abstract class Task<T> implements Comparable<T>, Serializable {

    private static final long serialVersionUID = 7038530852821069011L;

    protected static final char L = 'L';
    protected static final char M = 'M';
    protected static final char H = 'H';

    private static final char[] PRIORITIES = { H, M, L };

    protected String _id;
    protected TaskType _type;
    protected String _description;
    protected List<String> _tags;
    protected char _priority;
    protected Date _createdTime;
    protected Date _lastEditedTime;
    protected boolean _isCompleted;

    public Task(TaskType type, String description) {
        _type = (TaskType) type;
        _description = description;
        _id = UUID.randomUUID().toString();
        _priority = M;
        _createdTime = new Date();
        _lastEditedTime = new Date();
        _isCompleted = false;
    }

    protected int compareTo(Task<?> otherTask) {
        int priorityComparedResult = comparePriority(_priority,
                otherTask._priority);
        if (priorityComparedResult == 0) {
            return _description.compareTo(otherTask._description);
        }
        return priorityComparedResult;
    }

    private int comparePriority(char p1, char p2) {
        int indexOne = 0, indexTwo = 0;
        for (int i = 0; i < PRIORITIES.length; i++) {
            if (p1 == PRIORITIES[i]) {
                indexOne = i;
            }
            if (p2 == PRIORITIES[i]) {
                indexTwo = i;
            }
        }
        return indexOne - indexTwo;
    }

    public TaskType getType() {
        return _type;
    }

    public void setType(TaskType type) {
        _type = type;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public List<String> getTags() {
        return _tags;
    }

    public void setTags(List<String> tags) {
        _tags = tags;
    }

    public char getPriority() {
        return _priority;
    }

    public void setPriority(char priority) {
        _priority = priority;
    }

    public Date getLastEditedTime() {
        return _lastEditedTime;
    }

    public void setLastEditedTime(Date lastEditedTime) {
        _lastEditedTime = lastEditedTime;
    }

    public boolean isCompleted() {
        return _isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        _isCompleted = isCompleted;
    }

    public String getId() {
        return _id;
    }

    public Date getCreatedTime() {
        return _createdTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_type);
        sb.append(" task: ");
        sb.append(_description);
        sb.append(" priority (");
        sb.append(_priority);
        sb.append(") done? (");
        sb.append(_isCompleted ? "yes)" : "no)");
        return sb.toString();
    }

}
