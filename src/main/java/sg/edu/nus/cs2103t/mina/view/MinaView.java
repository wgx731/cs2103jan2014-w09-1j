package sg.edu.nus.cs2103t.mina.view;

import java.util.List;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

/**
 * 
 * Method of possible view components for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public interface MinaView {

    /**
     * Get user input
     * 
     * @return user input string
     */
    public String getUserInput();

    /**
     * Display output to user
     * 
     * @param message output message to be displayed
     */
    public void displayOutput(String message);

    /**
     * Update List Data in UI
     * 
     * @param eventList the event task list
     * @param deadlineList the deadline task list
     * @param todoList the todo task list
     */
    public void updateLists(SortedSet<EventTask> allEventTasks,
            SortedSet<DeadlineTask> allDeadlineTasks,
            SortedSet<TodoTask> allTodoTasks);

}
