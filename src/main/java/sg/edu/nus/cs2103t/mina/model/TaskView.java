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
    
    private static final int MIN_PAGE = 1;
    HashMap<TaskType, ArrayList<Task<?>>> _tasksOutput;
    String _status;

    private static final int EVERYTHING = Integer.MAX_VALUE;
    private static final int PAGE_ONE = 0;
    public static int ITEMS_PER_PAGE = 1;
    public static final int ITEMS_PER_PAGE_EVENT = 3;
    public static final int ITEMS_PER_PAGE_DEADLINE = 4;
    public static final int ITEMS_PER_PAGE_TODO = 7; 
    
    private boolean IS_EVENT_CHANGE = false;
    private boolean IS_DEADLINE_CHANGE = false;
    private boolean IS_TODO_CHANGE = false;
    
    private int TAB_SELECTED = -1;
    
    private int EVENT_PAGE = 0;
    private int DEADLINE_PAGE = 0;
    private int TODO_PAGE = 0;
    
    public TaskView(String status, 
                    HashMap<TaskType, ArrayList<Task<?>>> tasksOutput) {
        _tasksOutput = tasksOutput;
        _status = status;
    }
    
    public TaskView(String status) {
        _tasksOutput = new HashMap<TaskType, ArrayList<Task<?>>>();
        _status = status;        
    }
    
    public boolean hasOnlyOneType(TaskType type){
    	return _tasksOutput.containsKey(type)
    		&&_tasksOutput.size()==1;
    }
    
    public boolean hasTasks(TaskType type) {
        return _tasksOutput.containsKey(type);
    }
    
    /**
     * Get all todos tasks in String
     * @return String representation of all todos.
     */
    public ArrayList<Task<?>> getTodos() {
        return getTasks(TaskType.TODO, PAGE_ONE, EVERYTHING);
    }
    
    public ArrayList<Task<?>> getEvents() {
        return getTasks(TaskType.EVENT, PAGE_ONE, EVERYTHING);
    }
    
    public ArrayList<Task<?>> getDeadlines() {
        return getTasks(TaskType.DEADLINE, PAGE_ONE, EVERYTHING);        
    }
    
    public String getStatus(){
        return _status;
    }
    
    public int eventPageSize(){
    	return ITEMS_PER_PAGE_EVENT;
    }
    
    public int deadlinePageSize(){
    	return ITEMS_PER_PAGE_DEADLINE;
    }
    
    public int todoPageSize(){
    	return ITEMS_PER_PAGE_TODO;
    }
    
    public int maxTodoPage(){
    	return (getTodos().size()+todoPageSize()-1)/todoPageSize();
    }
    
    public int maxDeadlinePage(){
    	return (getDeadlines().size()+deadlinePageSize()-1)/deadlinePageSize();
    }
    
    public int maxEventPage(){
    	return (getEvents().size()+eventPageSize()-1)/eventPageSize();
    }
    
    public void setEventPage(int page){
    	EVENT_PAGE = page;
    }
    
    public void setDeadlinePage(int page){
    	DEADLINE_PAGE = page;
    }
    
    public void setTodoPage(int page){
    	TODO_PAGE = page;
    }
    
    public int getEventPage(){
    	return EVENT_PAGE;
    }
    
    public int getDeadlinePage(){
    	return DEADLINE_PAGE;
    }
    
    public int getTodoPage(){
    	return TODO_PAGE;
    }
    
    public void setTabSelected(int tabNum){
    	TAB_SELECTED = tabNum;
    }
    
    public int getTabSelected(){
    	return TAB_SELECTED;
    }
    
    public void setEventChange(boolean isChanged){
    	IS_EVENT_CHANGE = isChanged;
    }
    
    public void setDeadlineChange(boolean isChanged){
    	IS_DEADLINE_CHANGE = isChanged;
    }
    
    public void setTodoChange(boolean isChanged){
    	IS_TODO_CHANGE = isChanged;
    }
    
    public boolean isEventChange(){
    	return IS_EVENT_CHANGE;
    }
    
    public boolean isDeadlineChange(){
    	return IS_DEADLINE_CHANGE;
    }
    
    public boolean isTodoChange(){
    	return IS_TODO_CHANGE;
    }
    
    public ArrayList<Task<?>> getPage(TaskType type, int page) throws NumberFormatException, IndexOutOfBoundsException{
        /* Pg   items
         * 1  - 0-4
         * 2  - 5-9
         * 3  - 10-14
         * 4  - 15-19
         * ...
         */
        //Guard clause
        if (page<MIN_PAGE) {
           throw new NumberFormatException(); 
        }
        if (type == TaskType.TODO){
        	ITEMS_PER_PAGE = ITEMS_PER_PAGE_TODO;
        } else if (type == TaskType.DEADLINE){
        	ITEMS_PER_PAGE = ITEMS_PER_PAGE_DEADLINE;
        } else if (type == TaskType.EVENT){
        	ITEMS_PER_PAGE = ITEMS_PER_PAGE_EVENT;
        }
        int end = page * ITEMS_PER_PAGE - 1;
        int start = end - ITEMS_PER_PAGE + 1;
        return getTasks(type, start, end);
    }
    
    private ArrayList<Task<?>> getTasks(TaskType type, int start, int end) throws IndexOutOfBoundsException {
        
        //Guard clause and sanity check
        if(!hasTasks(type)){
            return new ArrayList<Task<?>>();
        }       
        assert(start<end);
        
        
        ArrayList<Task<?>> tasks = _tasksOutput.get(type);
        ArrayList<Task<?>> output = new ArrayList<Task<?>>();
        
        if (tasks.size()==0){
        	return output;
        }
        
        if (start >= tasks.size()) {
           throw new IndexOutOfBoundsException();
        }
        
        for(int i=start; i<=end && i<tasks.size(); i++) {
            output.add(tasks.get(i));
        }
        return output;
    }
    
    
    
}
