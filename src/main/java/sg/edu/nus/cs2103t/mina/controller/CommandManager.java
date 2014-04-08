package sg.edu.nus.cs2103t.mina.controller;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandProcessor;
import sg.edu.nus.cs2103t.mina.model.TaskView;

/**
 * Facade class uses for process user commands
 * 
 * @author viettrung9012
 * 
 */

public class CommandManager {

    private TaskDataManager _taskDataManager;
    private TaskFilterManager _taskFilterManager;
    private CommandProcessor _commandProcessor;
    private CommandParser _commandParser;
    
    private static Logger logger = LogManager.getLogger(CommandManager.class
            .getName());
    
    // Constructor
    public CommandManager() {
        _taskDataManager = new TaskDataManager();
        _taskFilterManager = new TaskFilterManager(_taskDataManager);
        _commandProcessor = new CommandProcessor(_taskDataManager,
                _taskFilterManager);
        _commandParser = new CommandParser();
    }

    public CommandManager(TaskDataManager taskDataManager,
            TaskFilterManager taskFilterManager) {
        _taskDataManager = taskDataManager;
        _taskFilterManager = taskFilterManager;
        _commandProcessor = new CommandProcessor(_taskDataManager,
                _taskFilterManager);
        _commandParser = new CommandParser();
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

    public TaskView getTaskView() {
        return _commandProcessor.getTaskView();
    }

    public TaskView processUserInput(String userInput, int eventPage,
            int deadlinePage, int todoPage) {
        
        String standardInput = null;
        try {
            standardInput = _commandParser.convertCommand(userInput);
        } catch (NullPointerException e) {
            logger.error(e,e);
            standardInput = "INVALID";
        } catch (ParseException e) {
            logger.error(e,e);
            standardInput = "INVALID";
        }
        return _commandProcessor.processUserInput(standardInput, eventPage,
                deadlinePage, todoPage);
    }
}
