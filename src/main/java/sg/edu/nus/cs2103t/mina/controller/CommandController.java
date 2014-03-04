package sg.edu.nus.cs2103t.mina.controller;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandController {

    private static String[] _inputString;
    private static final int MAX_INPUT_ARRAY_SIZE = 2;
    private static final int COMMAND_POSITION = 0;
    private static final int PARAMETER_POSITION = 1;
    private static final int FISRT_ARRAY_INDEX = 0;

    private static final String INVALID_COMMAND = "command given is invalid.\r\n";
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String ADDED_MESSAGE = "%1$s task %2$s has been added.\r\n";
    private static final String ADD_ERROR_MESSAGE = "Error occured whe system try to add new task.\r\n";
    private static final String TO_BE_DONE = "to be done.\r\n";
    private TaskDataManager _taskDataManager;
    private TaskFilterManager _taskFilterManager;

    enum CommandType {
        ADD, DELETE, MODIFY, COMPLETE, DISPLAY, SEARCH, UNDO, EXIT, INVALID
    };

    // Constructor
    public CommandController() {
        _taskDataManager = new TaskDataManager();
        _taskFilterManager = new TaskFilterManager(_taskDataManager);
    }

    public CommandController(TaskDataManager taskDataManager,
            TaskFilterManager taskFilterManager) {
        super();
        this._taskDataManager = taskDataManager;
        this._taskFilterManager = taskFilterManager;
    }

    public TaskDataManager getTaskDataManager() {
        return _taskDataManager;
    }

    public TaskFilterManager getTaskFilterManager() {
        return _taskFilterManager;
    }

    // This operation is used to get input from the user and execute it till
    // exit
    public String processUserInput(String userInput) {
        if (userInput == null || userInput.trim().equals(EMPTY_STRING)) {
            return INVALID_COMMAND;
        }
        //TODO: fix this bad design
        _inputString = userInput.split(SPACE, MAX_INPUT_ARRAY_SIZE);
        if (_inputString.length == 1) {
            _inputString = (userInput + " ").split(SPACE, MAX_INPUT_ARRAY_SIZE);
        }
        CommandType command;
        command = determineCommand();
        return processUserCommand(command);
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
    private String processUserCommand(CommandType command) {
        switch (command) {
            case ADD: {
                DataParameter addParameter = processAddParameter(_inputString[PARAMETER_POSITION]);
                Task<?> task = _taskDataManager.addTask(addParameter);
                if (task == null) {
                    return ADD_ERROR_MESSAGE;
                }
                return String.format(ADDED_MESSAGE, task.getType(), task.getDescription());
            }
            case DELETE: {
                // GUI: ask user confirmation
                // isConfirmedDelete()
                DataParameter deleteParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
                // TaskDataManager.deleteTask(deleteparams);
                // deleteparams: (int id)
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                return TO_BE_DONE;
            }
            case MODIFY: {
                DataParameter modifyParameter = processModifyParameter(_inputString[PARAMETER_POSITION]);
                // TaskDataManager.editTask(addparams);
                // editparams: (int id, String des) (int id, String des, Char
                // prir) (int id, Char prir)
                // (int id, String des, Date dead) (int id, Date dead)
                // (int id, String des, Date start, Date end) (int id, Date
                // start, Date end) // what happen if user want to change start
                // or end only?
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                return TO_BE_DONE;
            }
            case DISPLAY: {
                String filterParameterString = _inputString[PARAMETER_POSITION];
                FilterParameter filterParam;
                if (!filterParameterString.isEmpty()) {
                    filterParam = processFilterParameter(filterParameterString);
                } else {
                    filterParam = new FilterParameter();
                }
                ArrayList<Task<?>> filterResult = _taskFilterManager
                        .filterTask(filterParam);
                return getTaskListString(filterResult);
            }
            case SEARCH: {
                SearchParameter searchParameter = processSearchParameter(_inputString[PARAMETER_POSITION]);
                ArrayList<Task<?>> searchResult = _taskFilterManager
                        .searchTasks(searchParameter);
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.searchTask(searchparams);
                // searchparams: (String word), (String word1, String word2,
                // String word1 + word2), ...
                // (algorithm: combination of words)
                // call GUI display here
                return TO_BE_DONE;
            }
            case COMPLETE: {
                DataParameter completeParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
                return TO_BE_DONE;
            }
            case UNDO: {
                return TO_BE_DONE;
            }
            case EXIT: {
                System.exit(0);
                break;
            }
            case INVALID: {
                return INVALID_COMMAND;
            }
            default: {
                return INVALID_COMMAND;
            }
        }
        return INVALID_COMMAND;
    }

    private String getTaskListString(ArrayList<Task<?>> taskList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            Task<?> task = taskList.get(i);
            switch (task.getType()) {
                case TODO:
                    task = (TodoTask) task;
                    break;
                case EVENT:
                    task = (EventTask) task;
                    break;
                case DEADLINE:
                    task = (DeadlineTask) task;
                    break;
                case UNKOWN:
                    break;
                default:
                    break;
            }
            sb.append(i + SPACE + task.toString() + "\r\n");
        }
        return sb.toString();
    }

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
            try {
                Date startDate = DateUtil.parse(parameters
                        .get(indexOfStartDate));
                Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                addParam.setStartDate(startDate);
                addParam.setEndDate(endDate);
            } catch (Exception e) {

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
            } catch (Exception e) {

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
        return addParam;
    }

    // This method process search parameter into a SearchParameter instance
    // @param parameterString
    // string contains parameter data
    // @return searchParam
    // SearchParameter instance contains parameter for search method

    public SearchParameter processSearchParameter(String parameterString) {
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(" ")) {
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
        FilterParameter filterParam = new FilterParameter(parameters);
        return filterParam;
    }

    // This method process modify parameter into a DataParameter instance
    // @param parameterString
    // string contains parameter data
    // @return modifyParam
    // DataParameter instance contains parameter for modify method

    public DataParameter processModifyParameter(String parameterString) {
        DataParameter modifyParam = new DataParameter();
        ArrayList<String> parameters = new ArrayList<String>();
        for (String word : parameterString.split(" ")) {
            parameters.add(word);
        }
        TaskType original = processTaskTypeFromString(parameters
                .get(FISRT_ARRAY_INDEX));
        modifyParam.setOriginalTaskType(original);
        modifyParam.setTaskID(Integer.parseInt(parameters
                .get(FISRT_ARRAY_INDEX + 1)));
        if (parameters.contains("-totype")) {
            int indexOfNewTaskType = parameters.indexOf("-totype") + 1;
            TaskType newType = processTaskTypeFromString(parameters
                    .get(indexOfNewTaskType));
            modifyParam.setNewTaskType(newType);
        } else {
            modifyParam.setNewTaskType(original);
        }
        if (parameters.contains("-priority")) {
            int indexOfPriority = parameters.indexOf("-priority") + 1;
            char priority = parameters.get(indexOfPriority).toCharArray()[FISRT_ARRAY_INDEX];
            modifyParam.setPriority(priority);
        }
        if (parameters.contains("-start")) {
            int indexOfStartDate = parameters.indexOf("-start") + 1;
            try {
                Date startDate = DateUtil.parse(parameters
                        .get(indexOfStartDate));
                modifyParam.setStartDate(startDate);
            } catch (Exception e) {

            }
        }
        if (parameters.contains("-end")) {
            int indexOfEndDate = parameters.indexOf("-end") + 1;
            try {
                Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
                modifyParam.setEndDate(endDate);
            } catch (Exception e) {

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
        markDeleteParam.setTaskID(Integer.parseInt(parameters
                .get(FISRT_ARRAY_INDEX + 1)));
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
            return TaskType.UNKOWN;
        }
    }
}
