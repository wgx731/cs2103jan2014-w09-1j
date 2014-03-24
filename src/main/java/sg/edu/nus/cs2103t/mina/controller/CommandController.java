package sg.edu.nus.cs2103t.mina.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandProcessor;
import sg.edu.nus.cs2103t.mina.model.TaskView;


public class CommandController {

	private TaskDataManager _taskDataManager;
	private TaskFilterManager _taskFilterManager;
	private CommandProcessor _commandProcessor;
	
    private static Logger logger = LogManager.getLogger(CommandController.class
            .getName());
    
    // Constructor
    public CommandController() {
        _taskDataManager = new TaskDataManager();
        _taskFilterManager = new TaskFilterManager(_taskDataManager);
        _commandProcessor = new CommandProcessor(_taskDataManager, _taskFilterManager);
    }

    public CommandController(TaskDataManager taskDataManager,
            TaskFilterManager taskFilterManager) {
        _taskDataManager = taskDataManager;
        _taskFilterManager = taskFilterManager;
        _commandProcessor = new CommandProcessor(_taskDataManager, _taskFilterManager);
    }

    public TaskDataManager getTaskDataManager() {
        return _taskDataManager;
    }

    public TaskFilterManager getTaskFilterManager() {
        return _taskFilterManager;
    }

    public CommandProcessor getCommandProcessor() {
        return _commandProcessor;
    }
    
    public TaskView getTaskView(){
    	return _commandProcessor.getTaskView();
    }

    public TaskView processUserInput(String userInput, int eventPage,
            int deadlinePage, int todoPage) {
        return _commandProcessor.processUserInput(userInput, eventPage, deadlinePage, todoPage);
    }
}
