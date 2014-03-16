package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;

import sg.edu.nus.cs2103t.mina.controller.CommandController;

/**
 * 
 * Method of possible view components for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public abstract class MinaView {

    protected CommandController _commandController;

    public MinaView(CommandController commandController) {
        _commandController = commandController;
    }

    /**
     * Get user input
     * 
     * @return user input string
     */
    public abstract String getUserInput();

    /**
     * Display output to user
     * 
     * @param message output message to be displayed
     */
    public abstract void displayOutput(String message);

    /**
     * Update List Data in UI
     * 
     * @param eventList the event task list
     * @param deadlineList the deadline task list
     * @param todoList the todo task list
     */
    public abstract void updateLists(ArrayList<String> allEventTasks,
            ArrayList<String> allDeadlineTasks, ArrayList<String> allTodoTasks);

    public abstract void loop();

}
