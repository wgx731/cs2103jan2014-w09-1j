package sg.edu.nus.cs2103t.mina.model;

/**
 * Task without restriction
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class TodoTask extends Task<TodoTask> implements Comparable<TodoTask> {

    private static final long serialVersionUID = -6501529436053212805L;

    public TodoTask(String description) {
        super(TaskType.TODO, description);
    }

    public TodoTask(String description, char priority) {
        super(TaskType.TODO, description);
        _priority = priority;
    }

    public int compareTo(TodoTask otherTask) {
        return super.compareTo((Task<?>) otherTask);
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
    
}
