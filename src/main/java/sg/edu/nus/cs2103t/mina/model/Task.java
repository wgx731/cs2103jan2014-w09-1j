package sg.edu.nus.cs2103t.mina.model;

import java.io.Serializable;
import java.util.Date;
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

    protected static final String DEFAULT_TAG = "";
    protected static final char L = 'L';
    protected static final char M = 'M';
    protected static final char H = 'H';

    private static final char[] PRIORITIES = { H, M, L };

    public static boolean isValidPriority(char priority) {
        return priority == L || priority == M || priority == H;
    }

    protected String _id;
    protected TaskType _type;
    protected String _description;
    protected String _tag;
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
        _tag = DEFAULT_TAG;
    }

    public Task(TaskType type, String description, String id, char priority,
            Date createdTime, boolean isCompleted) {
        _type = (TaskType) type;
        _description = description;
        _id = id;
        _priority = priority;
        _createdTime = createdTime;
        _lastEditedTime = new Date();
        _isCompleted = isCompleted;
        _tag = DEFAULT_TAG;
    }

    public Task(TaskType type, String description, String id, char priority,
            Date createdTime, Date lastEditedTime, boolean isCompleted,
            String tag) {
        _type = (TaskType) type;
        _description = description;
        _id = id;
        _priority = priority;
        _createdTime = createdTime;
        _lastEditedTime = lastEditedTime;
        _isCompleted = isCompleted;
        _tag = tag;
    }

    protected int compareTo(Task<?> otherTask) {
        int priorityComparedResult = comparePriority(_priority,
                otherTask._priority);
        if (priorityComparedResult == 0) {
            if (_tag.compareTo(otherTask._tag) == 0) {
                return _description.compareTo(otherTask._description);
            }
            
            return _tag.compareTo(otherTask._tag);
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

    public String getTag() {
        return _tag;
    }

    public void setTag(String tag) {
        _tag = tag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_type);
        sb.append(" (tag: ");
        sb.append(_tag);
        sb.append(", description: ");
        sb.append(_description);
        sb.append(", priority: ");
        sb.append(_priority);
        sb.append(", completed: ");
        sb.append(_isCompleted ? "yes)" : "no)");
        // sb.append(" last modified: ");
        // sb.append(_lastEditedTime);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Task<?>) {
            return this.compareTo((Task<?>) other) == 0;
        } else {
            return false;
        }
    }

}
