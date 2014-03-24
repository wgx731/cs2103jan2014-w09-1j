package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.model.TaskView;

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
    
    protected CommandManager _commandController;
    
    public MinaView(CommandManager commandController) {
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
    public abstract void displayOutput();

    /**
     * Update List Data in UI
     * 
     * @param eventList the event task list
     * @param deadlineList the deadline task list
     * @param todoList the todo task list
     */
    public abstract void updateLists();

    public abstract void loop();
}
