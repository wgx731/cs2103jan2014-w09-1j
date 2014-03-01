package sg.edu.nus.cs2103t.mina.controller;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.cs2103t.mina.stub.DataParameterStub;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandController {

    private static String[] _inputString;
	private static final int _maxInputStringArraySize = 2;
	private static final int _modifyInputStringSplitSize = 3;
	private static final int _userCommandPosition = 0;
	private static final int _parameterPosition = 1;
	private static final int _firstArrayIndex = 0;
	private TaskDataManager TDM;
	private TaskFilterManager TFM;
	
    enum CommandType {
        ADD, DELETE, MODIFY, COMPLETE, DISPLAY, SEARCH, UNDO, EXIT, INVALID
    };
    
    
    // Constructor
    public CommandController(){
    	
    }
    
    // This operation is used to get input from the user and execute it till
    // exit
    public void processUserInput(String userInput) {
        CommandType command;
        _inputString = userInput.split(" ", _maxInputStringArraySize);
        TDM = new TaskDataManager();
        TFM = new TaskFilterManager(TDM);
        command = determineCommand();
        processUserCommand(command);
    }

    // This operation is used to get the user input and extract the command from
    // inputString
    private CommandType determineCommand() {
    	String userCommand = _inputString[_userCommandPosition];
        CommandType command = CommandType.valueOf(userCommand.trim().toUpperCase());
        return command;
    }

    // This operation is used to process the extracted command and call the
    // respective functions
    private void processUserCommand(CommandType command) {
        switch (command) {
            case ADD: {
                DataParameterStub addParameter = processAddParameter(_inputString[_parameterPosition]);
                // TaskDataManager.addTask(addParameter);
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                break;
            }
            case DELETE: {
                // GUI: ask user confirmation
                // isConfirmedDelete()
                // process inputString here
                // TaskDataManager.deleteTask(deleteparams);
                // deleteparams: (int id)
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                break;
            }
            case MODIFY: {
                DataParameterStub modifyParameter = processModifyParameter(_inputString[_parameterPosition]);
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
                break;
            }
            case DISPLAY: {
                String filterParameterString = _inputString[_parameterPosition];
                if (!filterParameterString.isEmpty()){
                	FilterParameter filterParam = processFilterParameter(filterParameterString);
                	ArrayList<Task<?>> filterResult = TFM.filterTask(filterParam);
                } else {
                // to be continue..
                }
                // ArrayList<Task> display =
                // TaskFilterManager.filterTask(filterparams);
                // filterparams: (Filter object)
                // call GUI display here
                break;
            }
            case SEARCH: {
                SearchParameter searchParameter = processSearchParameter(_inputString[_parameterPosition]);
                ArrayList<Task<?>> searchResult = TFM.searchTasks(searchParameter);
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.searchTask(searchparams);
                // searchparams: (String word), (String word1, String word2,
                // String word1 + word2), ...
                // (algorithm: combination of words)
                // call GUI display here
                break;
            }            
            case UNDO: {
                // Hmm?
                break;
            }
            case EXIT: {
                System.exit(0);
                break;
            }
            default: {
                break;
            }
        }
    }
    
    // This method process add parameter into a DataParameter instance
    // @param parameterString
    //			string contains parameter data
    // @return addParam
    //			DataParameter instance contains parameter for add method
    
    public DataParameterStub processAddParameter(String parameterString){
    	DataParameterStub addParam = new DataParameterStub();
    	ArrayList<String> parameters = new ArrayList<String>();
    	for(String word : parameterString.split(" ")) {
    	    parameters.add(word);
    	}
    	if (parameters.contains("-start")){
    		addParam.setNewTaskType(TaskType.EVENT);
    		int endIndexOfDescription = parameterString.indexOf("-");
    		String description = parameterString.substring(0, endIndexOfDescription).trim();
    		addParam.setDescription(description);
    		int indexOfStartDate = parameters.indexOf("-start")+1;
    		int indexOfEndDate = parameters.indexOf("-end")+1;
    		try{
    			Date startDate = DateUtil.parse(parameters.get(indexOfStartDate));
    			Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
    			addParam.setStartDate(startDate);
    			addParam.setEndDate(endDate);
    		} catch (Exception e){
    			
    		}    		
    	} else if (parameters.contains("-end")){
    		addParam.setNewTaskType(TaskType.DEADLINE);
    		int endIndexOfDescription = parameterString.indexOf("-");
    		String description = parameterString.substring(0, endIndexOfDescription).trim();
    		addParam.setDescription(description);
    		int indexOfEndDate = parameters.indexOf("-end")+1;
    		try{
    			Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
    			addParam.setEndDate(endDate);
    		} catch (Exception e){
    			
    		} 
    	} else {
    		addParam.setNewTaskType(TaskType.TODO);
    		int endIndexOfDescription = parameterString.indexOf("-");
    		String description;
    		if (endIndexOfDescription!=-1){
    			description = parameterString.substring(0, endIndexOfDescription).trim();
    		} else {
    			description = parameterString;
    		}
    		addParam.setDescription(description);
    	}
    	if (parameters.contains("-priority")){
    		int indexOfPriority = parameters.indexOf("-priority")+1;
    		char priority = parameters.get(indexOfPriority).toCharArray()[_firstArrayIndex];
    		addParam.setPriority(priority);
    	}
    	return addParam;
    }
    
    // This method process search parameter into a SearchParameter instance
    // @param parameterString
    //			string contains parameter data
    // @return searchParam
    //			SearchParameter instance contains parameter for search method
    
    public SearchParameter processSearchParameter(String parameterString){
    	ArrayList<String> parameters = new ArrayList<String>();
    	for(String word : parameterString.split(" ")) {
    	    parameters.add(word);
    	}
    	SearchParameter searchParam = new SearchParameter(parameters);
    	return searchParam;
    }
    
 // This method process filter parameter into a FilterParameter instance
    // @param parameterString
    //			string contains parameter data
    // @return filterParam
    //			FilterParameter instance contains parameter for filter method
    
    public FilterParameter processFilterParameter(String parameterString){
    	ArrayList<String> parameters = new ArrayList<String>();
    	for(String word : parameterString.split(" ")) {
    	    parameters.add(word);
    	}
    	FilterParameter filterParam = new FilterParameter(parameters);
    	return filterParam;
    }
    
    // This method process modify parameter into a DataParameter instance
    // @param parameterString
    //			string contains parameter data
    // @return modifyParam
    //			DataParameter instance contains parameter for modify method
    
    public DataParameterStub processModifyParameter(String parameterString){
    	DataParameterStub modifyParam = new DataParameterStub();
    	ArrayList<String> parameters = new ArrayList<String>();
    	for(String word : parameterString.split(" ")) {
    	    parameters.add(word);
    	}
    	TaskType original = processTaskTypeFromString(parameters.get(_firstArrayIndex));
    	modifyParam.setOriginalTaskType(original);
    	modifyParam.setTaskID(Integer.parseInt(parameters.get(_firstArrayIndex+1)));
    	if (parameters.contains("-totype")){
    		int indexOfNewTaskType = parameters.indexOf("-totype")+1;
    		TaskType newType = processTaskTypeFromString(parameters.get(indexOfNewTaskType));
    		modifyParam.setNewTaskType(newType);
    	} else {
    		modifyParam.setNewTaskType(original);
    	}
    	if (parameters.contains("-priority")){
    		int indexOfPriority = parameters.indexOf("-priority")+1;
    		char priority = parameters.get(indexOfPriority).toCharArray()[_firstArrayIndex];
    		modifyParam.setPriority(priority);
    	}
    	if (parameters.contains("-start")){
    		int indexOfStartDate = parameters.indexOf("-start")+1;
    		try{
    			Date startDate = DateUtil.parse(parameters.get(indexOfStartDate));
    			modifyParam.setStartDate(startDate);
    		} catch (Exception e){
    			
    		}
    	}
    	if (parameters.contains("-end")){
    		int indexOfEndDate = parameters.indexOf("-end")+1;
    		try{
    			Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
    			modifyParam.setEndDate(endDate);
    		} catch (Exception e){
    			
    		}
    	}
    	if (parameters.contains("-description")){
    		String newDescription;
    		int indexOfDescription = parameterString.indexOf("-description");
    		newDescription = parameterString.substring(indexOfDescription+13);
    		int indexOfEndOfDescription = newDescription.indexOf("-");
    		if (indexOfEndOfDescription!=-1){
    			newDescription = newDescription.substring(0, indexOfEndOfDescription-1);
    		}
    		modifyParam.setDescription(newDescription);
    	}
    	return modifyParam;
    }
    
    // This method process TaskType from String
    // @param taskTypeString
    //			string contains task type
    // @return taskType
    //			TaskType indicated by taskTypeString
    
    public TaskType processTaskTypeFromString(String taskTypeString){
    	if (taskTypeString.equals("todo")){
    		return TaskType.TODO;
    	} else if (taskTypeString.equals("deadline")){
    		return TaskType.DEADLINE;
    	} else if (taskTypeString.equals("event")){
    		return TaskType.EVENT;
    	} else {
    		return TaskType.UNKOWN;
    	}
    }
}
