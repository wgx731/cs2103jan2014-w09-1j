package sg.edu.nus.cs2103t.mina.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A TaskView object for the UI module to read the tasks and 
 * status of commands executed
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public class TaskView {
    
    HashMap<TaskType, ArrayList<Task<?>>> _tasksOutput;
    String _status;
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final int EVERYTHING = -1;
    private static final int PAGE_ONE = 0;
    private static final int ITEMS_PER_PAGE = 5;
    
    public TaskView(String status, 
                    HashMap<TaskType, ArrayList<Task<?>>> tasksOutput) {
        _tasksOutput = tasksOutput;
        _status = status;
    }
    
    public boolean hasTasks(TaskType type) {
        return _tasksOutput.containsKey(type);
    }
    
    /**
     * Get all todos tasks in String
     * @return String representation of all todos.
     */
    public String getTodos() {
        return getTasks(TaskType.TODO, PAGE_ONE, EVERYTHING);
    }
    
    public String getEvents() {
        return getTasks(TaskType.EVENT, PAGE_ONE, EVERYTHING);
    }
    
    public String getDeadlines() {
        return getTasks(TaskType.DEADLINE, PAGE_ONE, EVERYTHING);        
    }
    
    public String getStatus(){
        return _status;
    }
    
    public String getPage(TaskType type, int page){
        /* Pg   items
         * 1  - 0-4
         * 2  - 5-9
         * 3  - 10-14
         * 4  - 15-19
         * ...
         */
        int end = page * ITEMS_PER_PAGE - 1;
        int start = end - ITEMS_PER_PAGE;
        return getTasks(type, start, end);
    }
    
    private String getTasks(TaskType type, int start, int end) {
        
        //Guard clause
        if(!hasTasks(type)){
            return EMPTY;
        }       
        
        ArrayList<Task<?>> tasks = _tasksOutput.get(type);
        StringBuilder outputBuild = new StringBuilder();
        for(int i=start; i<end; i++) {
            int index = i+1;
            Task<?> task = tasks.get(i);
            outputBuild.append(index + SPACE + task.toString() + "\r\n");
        }
        return outputBuild.toString();
    }
    
    
    
}
