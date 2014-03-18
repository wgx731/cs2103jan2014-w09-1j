package sg.edu.nus.cs2103t.mina.commandcontroller;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;

public class CommandProcessor {
    
    private static final String DISPLAYING_SEARCHES = "Displaying searches!";
    private static final String RESULTS_DISPLAYED = "Results displayed";
    private static String[] _inputString;
    private static final int MAX_INPUT_ARRAY_SIZE = 2;
    private static final int COMMAND_POSITION = 0;
    private static final int PARAMETER_POSITION = 1;
    private static final int FISRT_ARRAY_INDEX = 0;

    private static final String INVALID_COMMAND = "command given is invalid.";
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String ADDED_MESSAGE = "%1$s task %2$s has been added.";
    private static final String ADD_ERROR_MESSAGE = "Error occured whe system try to add new task.";
    private static final String DELETED_MESSAGE = "%1$s task %2$s has been deleted.";
    private static final String DELETE_ERROR_MESSAGE = "Error occured whe system try to delete task.";
    private static final String MODIFIED_MESSAGE = "Modified. %1$s task %2$s.";
    private static final String MODIFY_ERROR_MESSAGE = "Error occured whe system try to modify task.";
    private static final String COMPLETED_MESSAGE = "%1$s task %2$s has been makred as completed.";
    private static final String COMPLETE_ERROR_MESSAGE = "Error occured whe system try to mark task as completed.";
    private static final String SEARCH_NOT_FOUND = "Search cannot find any result.";
    private static final String TO_BE_DONE = "to be done.";
    
    private TaskDataManager _taskDataManager;
    private TaskFilterManager _taskFilterManager;
    
    enum CommandType {
        ADD, DELETE, MODIFY, COMPLETE, DISPLAY, SEARCH, UNDO, EXIT, INVALID
    };
    
    public CommandProcessor(TaskDataManager taskDataManager, 
                            TaskFilterManager taskFilterManager){
        _taskDataManager = taskDataManager;
        _taskFilterManager = taskFilterManager;
    }
    
    
}
