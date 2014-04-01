package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandProcessor {

    private static final String DISPLAYING_SEARCHES = "Displaying searches!";
    private static final String RESULTS_DISPLAYED = "Results displayed";
    private static String[] _inputString;
    private static final int MAX_INPUT_ARRAY_SIZE = 2;
    private static final int COMMAND_POSITION = 0;
    private static final int PARAMETER_POSITION = 1;
    private static final int FISRT_ARRAY_INDEX = 0;

    private static final String WELCOME_MESSAGE = "welcome to MINA!";
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
    private static final String UNDO_MESSAGE = "undo completed.";
    private static final String UNDO_ERROR_MESSAGE = "Error occured whe system try to undo.";
    private static final String REDO_MESSAGE = "redo completed.";
    private static final String REDO_ERROR_MESSAGE = "Error occured whe system try to redo.";
//    private static final String TO_BE_DONE = "to be done.";

    private int _currentEventPage;
    private int _currentDeadlinePage;
    private int _currentTodoPage;

    private TaskView _taskView;
    private TaskDataManager _taskDataManager;
    private TaskFilterManager _taskFilterManager;
    private CommandHistory _commandHistory;

    private boolean _isUndoNow = true;
    private boolean _isRedoNow = false;

    private static Logger logger = LogManager.getLogger(CommandManager.class
            .getName());

    enum CommandType {
        ADD, DELETE, MODIFY, COMPLETE, UNCOMPLETE, DISPLAY, SEARCH, UNDO, REDO, EXIT, INVALID
    };

    // Constructor
    public CommandProcessor() {
        _taskDataManager = new TaskDataManager();
        _taskFilterManager = new TaskFilterManager(_taskDataManager);
        initializeTaskView();
        _commandHistory = new CommandHistory();
    }

    public CommandProcessor(TaskDataManager taskDataManager,
            TaskFilterManager taskFilterManager) {
        _taskDataManager = taskDataManager;
        _taskFilterManager = taskFilterManager;
        initializeTaskView();
        _commandHistory = new CommandHistory();
    }

    public TaskDataManager getTaskDataManager() {
        return _taskDataManager;
    }

    public TaskFilterManager getTaskFilterManager() {
        return _taskFilterManager;
    }

    public TaskView getTaskView() {
        return _taskView;
    }

    public CommandHistory getCommandHistory() {
        return _commandHistory;
    }

    public void initializeTaskView() {
        _taskView = new TaskView(WELCOME_MESSAGE,
                _taskFilterManager.filterTask(new FilterParameter()));
    }

    // This operation is used to get input from the user and execute it till
    // exit
    public TaskView processUserInput(String userInput, int eventPage,
            int deadlinePage, int todoPage) {
        if (userInput == null || userInput.trim().equals(EMPTY_STRING)) {
            return new TaskView(INVALID_COMMAND);
        }
        _currentEventPage = eventPage;
        _currentDeadlinePage = deadlinePage;
        _currentTodoPage = todoPage;
        // TODO: fix this bad design
        _inputString = userInput.split(SPACE, MAX_INPUT_ARRAY_SIZE);
        if (_inputString.length == 1) {
            _inputString = (userInput + " ").split(SPACE, MAX_INPUT_ARRAY_SIZE);
        }
        CommandType command;
        command = determineCommand();
        try {
            processUserCommand(command);
            return _taskView;
        } catch (Exception e) {
            processUserCommand(CommandType.INVALID);
            logger.error(e, e);
            return _taskView;
        }
    }

    // This operation is used to get the user input and extract the command from
    // inputString
    private CommandType determineCommand() {
        String userCommand = _inputString[COMMAND_POSITION];
        try {
            return CommandType.valueOf(userCommand.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.INVALID;
        }
    }

    // This operation is used to process the extracted command and call the
    // respective functions
    private void processUserCommand(CommandType command) {
        switch (command) {
            case ADD : {
                DataParameter addParameter = processAddParameter(_inputString[PARAMETER_POSITION]);
                if (addParameter == null) {
                    _taskView = errorCommandReturn(CommandType.INVALID);
                }
                Task<?> task = _taskDataManager.addTask(addParameter);
                if (task == null) {
                    _taskView = errorCommandReturn(CommandType.ADD);
                } else {
                    // UIprocess();
                    String output = String.format(ADDED_MESSAGE,
                            task.getType(), task.getDescription());
                    _taskView = updatedTaskView(output);
                    DataParameter undoParam = processUndoAddParameter(task);
                    _commandHistory.addUndo(CommandType.DELETE, undoParam);
                    _commandHistory.clearRedo();
                }
                break;
            }
            case DELETE : {
                DataParameter deleteParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
                Task<?> task = _taskDataManager.deleteTask(deleteParameter);
                if (task == null) {
                    _taskView = errorCommandReturn(CommandType.DELETE);
                } else {
                    String output = String.format(DELETED_MESSAGE,
                            task.getType(), task.getDescription());
                    _taskView = updatedTaskView(output);
                    DataParameter undoParam = processUndoDeleteParameter(deleteParameter);
                    _commandHistory.addUndo(CommandType.ADD, undoParam);
                    _commandHistory.clearRedo();
                }
                break;
            }
            case MODIFY : {
                DataParameter modifyParameter = processModifyParameter(_inputString[PARAMETER_POSITION]);
                if (modifyParameter == null) {
                    _taskView = errorCommandReturn(CommandType.INVALID);
                }
                Task<?> task = _taskDataManager.modifyTask(modifyParameter);
                if (task == null) {
                    _taskView = errorCommandReturn(CommandType.MODIFY);
                } else {
                    String output = String.format(MODIFIED_MESSAGE,
                            task.getType(), task.getDescription());
                    _taskView = updatedTaskView(output);
                    DataParameter undoParam = processUndoModifyParameter(task,
                            modifyParameter);
                    _commandHistory.addUndo(CommandType.MODIFY, undoParam);
                    _commandHistory.clearRedo();
                }
                break;
            }
            case DISPLAY : {
                String filterParameterString = _inputString[PARAMETER_POSITION];
                FilterParameter filterParam;
                if (!filterParameterString.isEmpty()) {
                    filterParam = processFilterParameter(filterParameterString);
                } else {
                    filterParam = new FilterParameter();
                }
                HashMap<TaskType, ArrayList<Task<?>>> filterResult;
                filterResult = _taskFilterManager.filterTask(filterParam);
                _taskView = new TaskView(RESULTS_DISPLAYED, filterResult);
                break;
            }
            case SEARCH : {

                SearchParameter searchParameter = processSearchParameter(_inputString[PARAMETER_POSITION]);

                HashMap<TaskType, ArrayList<Task<?>>> searchResult;
                searchResult = _taskFilterManager.searchTasks(searchParameter);

                String output;

                if (searchResult.size() == 0) {
                    output = SEARCH_NOT_FOUND;
                } else {
                    output = DISPLAYING_SEARCHES;
                }
                _taskView = new TaskView(output, searchResult);
                break;
            }
            case COMPLETE : {
                DataParameter completeParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
                Task<?> task = _taskDataManager
                        .markCompleted(completeParameter);
                if (task == null) {
                    _taskView = errorCommandReturn(CommandType.COMPLETE);
                } else {
                    String output = String.format(COMPLETED_MESSAGE,
                            task.getType(), task.getDescription());
                    _taskView = updatedTaskView(output);
                    DataParameter undoParam = processUndoCompleteParameter(task,
                            completeParameter);
                    _commandHistory.addUndo(CommandType.UNCOMPLETE, undoParam);
                    _commandHistory.clearRedo();
                }
                break;
            }
            case UNDO : {
                if (_commandHistory.isEmptyUndo()) {
                    _taskView = errorCommandReturn(CommandType.UNDO);
                } else {
                    CommandType undoCmd = _commandHistory.getUndoCommand();
                    DataParameter undoParam = _commandHistory.getUndoParam();
                    if (_isRedoNow) {
                        _isRedoNow = false;
                        _isUndoNow = true;
                    }
                    performUndoRedo(undoCmd, undoParam);
                }
                break;
            }
            case REDO : {
                if (_commandHistory.isEmptyRedo()) {
                    _taskView = errorCommandReturn(CommandType.REDO);
                } else {
                    CommandType redoCmd = _commandHistory.getRedoCommand();
                    DataParameter redoParam = _commandHistory.getRedoParam();
                    if (_isUndoNow) {
                        _isUndoNow = false;
                        _isRedoNow = true;
                    }
                    performUndoRedo(redoCmd, redoParam);
                }
                break;
            }
            case EXIT : {
                _taskDataManager.saveAllTasks();
                System.exit(0);
                break;
            }
            case INVALID : {
                _taskView = errorCommandReturn(CommandType.INVALID);
                break;
            }
            default : {
                _taskView = errorCommandReturn(CommandType.INVALID);
                break;
            }
        }
    }

    private void performUndoRedo(CommandType cmd, DataParameter param) {
        switch (cmd) {
            case ADD :
                Task<?> addedTask = _taskDataManager.addTask(param);
                if (addedTask == null) {
                    processUserCommand(CommandType.INVALID);
                } else {
                    DataParameter undoParam = processUndoAddParameter(addedTask);
                    updateHistory(CommandType.DELETE, undoParam);
                    if (_isUndoNow) {
                        String output = UNDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    } else {
                        String output = REDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    }
                }
                break;
            case DELETE :
                Task<?> deletedTask = _taskDataManager.deleteTask(param);
                if (deletedTask == null) {
                    processUserCommand(CommandType.INVALID);
                } else {
                    DataParameter undoParam = processUndoDeleteParameter(param);
                    updateHistory(CommandType.ADD, undoParam);
                    if (_isUndoNow) {
                        String output = UNDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    } else {
                        String output = REDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    }
                }
                break;
            case MODIFY :
                Task<?> modifiedTask = _taskDataManager.modifyTask(param);
                if (modifiedTask == null) {
                    processUserCommand(CommandType.INVALID);
                } else {
                    DataParameter undoParam = processUndoModifyParameter(
                            modifiedTask, param);
                    updateHistory(CommandType.MODIFY, undoParam);
                    if (_isUndoNow) {
                        String output = UNDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    } else {
                        String output = REDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    }
                }
                break;
            case COMPLETE:
            	Task<?> completedTask = _taskDataManager.markCompleted(param);
            	if (completedTask == null){
                    processUserCommand(CommandType.INVALID);
            	} else {
            		DataParameter undoParam = processUndoCompleteParameter(
                            completedTask, param);
                    updateHistory(CommandType.UNCOMPLETE, undoParam);
                    if (_isUndoNow) {
                        String output = UNDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    } else {
                        String output = REDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    }
            	}
            	break;
            case UNCOMPLETE:
            	Task<?> uncompletedTask = _taskDataManager.markUncompleted(param);
            	if (uncompletedTask == null){
                    processUserCommand(CommandType.INVALID);
            	} else {
            		DataParameter undoParam = processUndoCompleteParameter(
                            uncompletedTask, param);
                    updateHistory(CommandType.COMPLETE, undoParam);
                    if (_isUndoNow) {
                        String output = UNDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    } else {
                        String output = REDO_MESSAGE;
                        _taskView = updatedTaskView(output);
                    }
            	}
            	break;
            default :
                break;
        }
    }

    private void updateHistory(CommandType cmd, DataParameter param) {
        if (_isUndoNow) {
            _commandHistory.addRedo(cmd, param);
        } else {
            _commandHistory.addUndo(cmd, param);
        }
    }

    private TaskView updatedTaskView(String statusMessage) {
        return new TaskView(statusMessage,
                _taskFilterManager.filterTask(new FilterParameter()));
    }

    private TaskView errorCommandReturn(CommandType type) {
        switch (type) {
            case ADD :
                return new TaskView(ADD_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case DELETE :
                return new TaskView(DELETE_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case MODIFY :
                return new TaskView(MODIFY_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case COMPLETE :
                return new TaskView(COMPLETE_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case UNDO :
                return new TaskView(UNDO_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case REDO :
                return new TaskView(REDO_ERROR_MESSAGE,
                        _taskFilterManager.filterTask(new FilterParameter()));
            case INVALID :
                return new TaskView(INVALID_COMMAND,
                        _taskFilterManager.filterTask(new FilterParameter()));
            default :
                return new TaskView(INVALID_COMMAND,
                        _taskFilterManager.filterTask(new FilterParameter()));
        }
    }

    // Legacy method, retainign it just in case
    // private String getTaskListString(ArrayList<Task<?>> taskList) {
    // StringBuilder sb = new StringBuilder();
    // for (int i = 0; i < taskList.size(); i++) {
    // Task<?> task = taskList.get(i);
    // switch (task.getType()) {
    // case TODO:
    // task = (TodoTask) task;
    // break;
    // case EVENT:    @Test
    // task = (EventTask) task;
    // break;
    // case DEADLINE:
    // task = (DeadlineTask) task;
    // break;
    // case UNKOWN:
    // break;
    // default:
    // break;
    // }
    // sb.append(i + SPACE + task.toString() + "\r\n");
    // }
    // return sb.toString();
    // }

    // This method process add parameter into a DataParameter instance
    // @param parameterString
    // string contains parameter data
    // @return addParam
    // DataParameter instance contains parameter for add method

    public DataParameter processAddParameter(String parameterString) {
        DataParameter addParam = new DataParameter();
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(SPACE)) {
            parameters.add(word);
        }
        if (parameters.contains("-start")) {
            addParam.setNewTaskType(TaskType.EVENT);
            int endIndexOfDescription = parameterString.indexOf("-");
            String description = parameterString.substring(0,
                    endIndexOfDescription).trim();
            addParam.setDescription(description);
            int indexOfStartDate = parameters.indexOf("-start") + 1;
            int indexOfEndDate = parameters.indexOf("-end") + 1;
            if (indexOfEndDate == 0){
            	addParam = null;
            	return addParam;
            }
            try {
                Date startDate = DateUtil.parse(parameters
                        .get(indexOfStartDate));
                Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                if (startDate.before(endDate)) {
                    addParam.setStartDate(startDate);
                    addParam.setEndDate(endDate);
                } else {
                    addParam = null;
                    return addParam;
                }
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (parameters.contains("-end")) {
            addParam.setNewTaskType(TaskType.DEADLINE);
            int endIndexOfDescription = parameterString.indexOf("-");
            String description = parameterString.substring(0,
                    endIndexOfDescription).trim();
            addParam.setDescription(description);
            int indexOfEndDate = parameters.indexOf("-end") + 1;
            try {
                Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                addParam.setEndDate(endDate);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            addParam.setNewTaskType(TaskType.TODO);
            int endIndexOfDescription = parameterString.indexOf("-");
            String description;
            if (endIndexOfDescription != -1) {
                description = parameterString.substring(0,
                        endIndexOfDescription).trim();
            } else {
                description = parameterString;
            }
            addParam.setDescription(description);
        }
        if (parameters.contains("-priority")) {
            int indexOfPriority = parameters.indexOf("-priority") + 1;
            char priority = parameters.get(indexOfPriority).toCharArray()[FISRT_ARRAY_INDEX];
            addParam.setPriority(priority);
        }
        if (parameters.contains("-every")){
        	String timeType = parameters.get(parameters.indexOf("-every")+1).toUpperCase();
        	addParam.setTimeType(timeType);
        	addParam.setFreqOfTimeType(1);
        	addParam.setTag("RECUR");
        }
        if (parameters.contains("-until")){
        	try{
        		Date recurEndDate = DateUtil.parse(parameters.get(parameters.indexOf("-until")+1));
        		addParam.setEndRecurOn(recurEndDate);
        	} catch (Exception e){
        		addParam = null;
        		logger.error(e.getMessage(), e);
        	}
        }
        if (addParam.getDescription().equals("")){
        	addParam = null;
        }
        return addParam;
    }

    // This method process search parameter into a SearchParameter instance
    // @param parameterString
    // string contains parameter data
    // @return searchParam
    // SearchParameter instance contains parameter for search method

    public SearchParameter processSearchParameter(String parameterString) {
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split("//")) {
            parameters.add(word);
        }
        SearchParameter searchParam = new SearchParameter(parameters);
        return searchParam;
    }

    // This method process filter parameter into a FilterParameter instance
    // @param parameterString
    // string contains parameter data
    // @return filterParam
    // FilterParameter instance contains parameter for filter method

    public FilterParameter processFilterParameter(String parameterString) {
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(SPACE)) {
            parameters.add(word);
        }
        Date startDate = null;
        Date endDate = null;
        boolean hasStartTime = false; 
        boolean hasEndTime = false;
        
        if (parameters.contains("-end")) {
            int indexOfEndDate = parameters.indexOf("-end") + 1;
            try {
                endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                hasEndTime = hasTime(parameters.get(indexOfEndDate));
                parameters.remove(indexOfEndDate);
                parameters.remove(indexOfEndDate - 1);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (parameters.contains("-start")) {
            int indexOfStartDate = parameters.indexOf("-start") + 1;
            try {
                startDate = DateUtil.parse(parameters.get(indexOfStartDate));
                hasStartTime = hasTime(parameters.get(indexOfStartDate));
                parameters.remove(indexOfStartDate);
                parameters.remove(indexOfStartDate - 1);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        FilterParameter filterParam = new FilterParameter(parameters);
        filterParam.setStart(startDate);
        filterParam.setEnd(endDate);
        filterParam.setStartTime(hasStartTime);
        filterParam.setEndTime(hasEndTime);
        if (filterParam.getStart()!=null&&filterParam.getEnd()!=null){
    		if (filterParam.getStart().after(filterParam.getEnd())){
    			filterParam = null;
    			return filterParam;
    		}
    	}
        return filterParam;
    }

    // This method process modify parameter into a DataParameter instance
    // @param parameterString
    // string contains parameter data
    // @return modifyParam
    // DataParameter instance contains parameter for modify method

    private boolean hasTime(String date) {
        String format = DateUtil.determineDateFormat(date);
        return format.contains("HH");
    }

    public DataParameter processModifyParameter(String parameterString) {
        DataParameter modifyParam = new DataParameter();
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(" ")) {
            parameters.add(word);
        }
        TaskType original = processTaskTypeFromString(parameters
                .get(FISRT_ARRAY_INDEX));
        modifyParam.setOriginalTaskType(original);
        int userfriendlyTaskID = Integer.parseInt(parameters
                .get(FISRT_ARRAY_INDEX + 1));
        int pageNum;
        if (original == TaskType.EVENT) {
            pageNum = _currentEventPage;
        } else if (original == TaskType.DEADLINE) {
            pageNum = _currentDeadlinePage;
        } else if (original == TaskType.TODO) {
            pageNum = _currentTodoPage;
        } else {
            pageNum = 0;
        }
        ArrayList<Task<?>> pageOfModifyObject = _taskView.getPage(original,
                pageNum);
        Task<?> modifyTask = pageOfModifyObject.get(userfriendlyTaskID - 1);
        modifyParam.setTaskObject(modifyTask);
        modifyParam.setTaskID(userfriendlyTaskID);
        if (parameters.contains("-totype")) {
            int indexOfNewTaskType = parameters.indexOf("-totype") + 1;
            TaskType newType = processTaskTypeFromString(parameters
                    .get(indexOfNewTaskType));
            modifyParam.setNewTaskType(newType);
        } else {
            modifyParam.setNewTaskType(original);
        }
        if (parameters.contains("-end")) {
            int indexOfEndDate = parameters.indexOf("-end") + 1;
            try {
                Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                modifyParam.setEndDate(endDate);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // NOTE: this can only detect error when user want to modify both start
        // and end date, but not only one.
        if (parameters.contains("-start")) {
            int indexOfStartDate = parameters.indexOf("-start") + 1;
            try {
                Date startDate = DateUtil.parse(parameters
                        .get(indexOfStartDate));
                modifyParam.setStartDate(startDate);
                if (modifyParam.getEndDate() != null) {
                    if (modifyParam.getStartDate().after(
                            modifyParam.getEndDate())) {
                        modifyParam = null;
                        return modifyParam;
                    }
                }
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (parameters.contains("-description")) {
            String newDescription;
            int indexOfDescription = parameterString.indexOf("-description");
            newDescription = parameterString.substring(indexOfDescription + 13);
            int indexOfEndOfDescription = newDescription.indexOf("-");
            if (indexOfEndOfDescription != -1) {
                newDescription = newDescription.substring(0,
                        indexOfEndOfDescription - 1);
            }
            modifyParam.setDescription(newDescription);
        }
        if (modifyParam.getOriginalTaskType()==TaskType.TODO
        		&&modifyParam.getNewTaskType()==TaskType.DEADLINE
        		&&modifyParam.getEndDate()==null){
        	modifyParam = null;
        	return modifyParam;
        }
        if (modifyParam.getOriginalTaskType()==TaskType.TODO
        		&&modifyParam.getNewTaskType()==TaskType.EVENT
        		&&(modifyParam.getEndDate()==null||modifyParam.getStartDate()==null)){
        	modifyParam = null;
        	return modifyParam;
        }
        if (modifyParam.getOriginalTaskType()==TaskType.DEADLINE
        		&&modifyParam.getNewTaskType()==TaskType.EVENT
        		&&modifyParam.getStartDate()==null){
        	modifyParam = null;
        	return modifyParam;
        }
        if (modifyParam.getOriginalTaskType()==TaskType.TODO
        		&&modifyParam.getNewTaskType()==TaskType.TODO
        		&&(modifyParam.getStartDate()!=null
        		||modifyParam.getEndDate()!=null)){
        	modifyParam = null;
        	return modifyParam;
        }
        if (modifyParam.getOriginalTaskType()==TaskType.DEADLINE
        		&&modifyParam.getNewTaskType()==TaskType.DEADLINE
        		&&modifyParam.getStartDate()!=null){
        	modifyParam = null;
        	return modifyParam;
        }
        if (modifyParam.getStartDate()!=null||modifyParam.getEndDate()!=null){
        	if (modifyParam.getStartDate()!=null&&modifyParam.getEndDate()!=null){
        		if (modifyParam.getStartDate().after(modifyParam.getEndDate())){
        			modifyParam = null;
        			return modifyParam;
        		}
        	} else if (modifyParam.getStartDate()!=null){
        		Task<?> toModifyTask = modifyParam.getTaskObject();
        		if (toModifyTask.getType()==TaskType.EVENT){
        			if (modifyParam.getStartDate().after(((EventTask)toModifyTask).getEndTime())){
        				modifyParam = null;
        				return modifyParam;
        			}
        		} else if (toModifyTask.getType()==TaskType.DEADLINE){
        			if (modifyParam.getStartDate().after(((DeadlineTask)toModifyTask).getEndTime())){
        				modifyParam = null;
        				return modifyParam;
        			}
        		}
        	} else if (modifyParam.getEndDate()!=null){
        		Task<?> toModifyTask = modifyParam.getTaskObject();
        		if (toModifyTask.getType()==TaskType.EVENT){
        			if (modifyParam.getEndDate().before(((EventTask)toModifyTask).getStartTime())){
        				modifyParam = null;
        				return modifyParam;
        			}
        		}
        	}
        }
        if (parameters.contains("-priority")) {
            int indexOfPriority = parameters.indexOf("-priority") + 1;
            char priority = parameters.get(indexOfPriority).toCharArray()[FISRT_ARRAY_INDEX];
            modifyParam.setPriority(priority);
        } else {
        	modifyParam.setPriority(modifyParam.getTaskObject().getPriority());
        }
        return modifyParam;
    }

    // This method process delete/complete parameter into a DataParameter
    // instance
    // @param parameterString
    // string contains parameter data
    // @return markDeleteParam
    // DataParameter instance contains parameter for delete/complete method

    public DataParameter processMarkDeleteParameter(String parameterString) {
        DataParameter markDeleteParam = new DataParameter();
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(" ")) {
            parameters.add(word);
        }
        TaskType original = processTaskTypeFromString(parameters
                .get(FISRT_ARRAY_INDEX));
        markDeleteParam.setOriginalTaskType(original);
        int userfriendlyTaskID = Integer.parseInt(parameters
                .get(FISRT_ARRAY_INDEX + 1));
        int pageNum;
        if (original == TaskType.EVENT) {
            pageNum = _currentEventPage;
        } else if (original == TaskType.DEADLINE) {
            pageNum = _currentDeadlinePage;
        } else if (original == TaskType.TODO) {
            pageNum = _currentTodoPage;
        } else {
            pageNum = 0;
        }
        /*
        if (markDeleteParam.getOriginalTaskType()==null||markDeleteParam.getOriginalTaskType()==TaskType.UNKOWN){
        	markDeleteParam = null;
        	return markDeleteParam;
        }
        if (markDeleteParam.getTaskId() == -1){
        	markDeleteParam = null;
        	return markDeleteParam;
        }
        */
        ArrayList<Task<?>> pageOfMarkDeleteObject = _taskView.getPage(original,
                pageNum);
        /*
        if (markDeleteParam.getTaskId()>=pageOfMarkDeleteObject.size()){
            markDeleteParam = null;
            return markDeleteParam;
        }
        */
        if (parameters.contains("-all")){
        	markDeleteParam.setModifyAll(true);
        }
        Task<?> markDeleteTask = pageOfMarkDeleteObject
                .get(userfriendlyTaskID - 1);
        markDeleteParam.setTaskObject(markDeleteTask);
        markDeleteParam.setTaskID(userfriendlyTaskID);
        
        return markDeleteParam;
    }

    // This method process TaskType from String
    // @param taskTypeString
    // string contains task type
    // @return taskType
    // TaskType indicated by taskTypeString

    public TaskType processTaskTypeFromString(String taskTypeString) {
        if (taskTypeString.equals("todo")) {
            return TaskType.TODO;
        } else if (taskTypeString.equals("deadline")) {
            return TaskType.DEADLINE;
        } else if (taskTypeString.equals("event")) {
            return TaskType.EVENT;
        } else {
            return TaskType.UNKNOWN;
        }
    }

    public DataParameter processUndoAddParameter(Task<?> task) {
        DataParameter undoAddParameter = new DataParameter();
        undoAddParameter.setOriginalTaskType(task.getType());
        undoAddParameter.setTaskObject(task);
        return undoAddParameter;
    }

    public DataParameter processUndoDeleteParameter(DataParameter deleteParam) {
        Task<?> deletedTask = deleteParam.getTaskObject();
        DataParameter undoDeleteParameter = new DataParameter();
        undoDeleteParameter.setDescription(deletedTask.getDescription());
        undoDeleteParameter.setNewTaskType(deletedTask.getType());
        undoDeleteParameter.setPriority(deletedTask.getPriority());
        if (deletedTask.getType() == TaskType.EVENT) {
            EventTask deletedEventTask = (EventTask) deletedTask;
            undoDeleteParameter.setStartDate(deletedEventTask.getStartTime());
            undoDeleteParameter.setEndDate(deletedEventTask.getEndTime());
        } else if (deletedTask.getType() == TaskType.DEADLINE) {
            DeadlineTask deletedDeadlineTask = (DeadlineTask) deletedTask;
            undoDeleteParameter.setEndDate(deletedDeadlineTask.getEndTime());
        }
        return undoDeleteParameter;
    }

    public DataParameter processUndoModifyParameter(Task<?> newTask,
            DataParameter modifyParam) {
        Task<?> oldTask = modifyParam.getTaskObject();
        DataParameter undoModifyParameter = new DataParameter();
        undoModifyParameter.setDescription(oldTask.getDescription());
        undoModifyParameter.setOriginalTaskType(newTask.getType());
        undoModifyParameter.setNewTaskType(oldTask.getType());
        undoModifyParameter.setPriority(oldTask.getPriority());
        undoModifyParameter.setTaskObject(newTask);
        if (oldTask.getType() == TaskType.EVENT) {
            EventTask oldEventTask = (EventTask) oldTask;
            undoModifyParameter.setStartDate(oldEventTask.getStartTime());
            undoModifyParameter.setEndDate(oldEventTask.getEndTime());
        } else if (oldTask.getType() == TaskType.DEADLINE) {
            DeadlineTask oldDeadlineTask = (DeadlineTask) oldTask;
            undoModifyParameter.setEndDate(oldDeadlineTask.getEndTime());
        }
        return undoModifyParameter;
    }
    
    public DataParameter processUndoCompleteParameter(Task<?> newTask, 
    		DataParameter completeParameter){
    	DataParameter undoCompleteParameter = new DataParameter();
    	undoCompleteParameter.setTaskObject(newTask);
    	return undoCompleteParameter;
    }
}
