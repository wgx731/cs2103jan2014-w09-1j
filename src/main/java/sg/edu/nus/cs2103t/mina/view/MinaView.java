package sg.edu.nus.cs2103t.mina.view;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;

/** 
 * Skeleton class for MINA view
 */
//@author A0105853H

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
     */
    public abstract void displayOutput();

    /**
     * Update List Data in UI
     */
    public abstract void updateLists();

    /**
     * wait user input in loop
     */
    public abstract void loop();
}
