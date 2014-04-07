package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.Matcher;

import sg.edu.nus.cs2103t.mina.model.TaskType;

public class TaskId extends Keyword {
    

    
    private HashMap<String, String> _taskTypeValues;
    private String taskRegex;
    private String idRegex = "\\d*$";
    private static final int TASK_ID_LOOKAHEAD = 2;
    private static Logger logger = LogManager.getLogger(TaskId.class
            .getName());
    
    static {
        TaskId newTaskId = new TaskId(SimpleKeyword.TASKID);
        KeywordFactory.addAliasEntry("-taskid", newTaskId);
        KeywordFactory.addAliasEntry("-block", newTaskId);
    }
    
    public TaskId(StandardKeyword type) {
        super(type);
    }
    
    public TaskId() {
        this(SimpleKeyword.TASKID);
        initValues();
    }
    
    @Override
    protected Keyword createKeyword() {
        return new TaskId();
    }

    @Override
    protected void initValues() {
        
        _taskTypeValues = new HashMap<String, String>();
        
        _taskTypeValues.put(TaskType.EVENT.getType(), TaskType.EVENT.getType());
        _taskTypeValues.put("e", TaskType.EVENT.getType());
        _taskTypeValues.put("events", TaskType.EVENT.getType());
        _taskTypeValues.put("appointment", TaskType.EVENT.getType());
        _taskTypeValues.put("appt", TaskType.EVENT.getType());
        _taskTypeValues.put("appts", TaskType.EVENT.getType());

        _taskTypeValues.put(TaskType.TODO.getType(), TaskType.TODO.getType());
        _taskTypeValues.put("td", TaskType.TODO.getType());
        _taskTypeValues.put("to-do", TaskType.TODO.getType());
        _taskTypeValues.put("todos", TaskType.TODO.getType());
        _taskTypeValues.put("task", TaskType.TODO.getType());
        _taskTypeValues.put("tasks", TaskType.TODO.getType());

        _taskTypeValues.put(TaskType.DEADLINE.getType(),
                TaskType.DEADLINE.getType());
        _taskTypeValues.put("d", TaskType.DEADLINE.getType());
        _taskTypeValues.put("deadlines", TaskType.DEADLINE.getType());
        _taskTypeValues.put("cutoff", TaskType.DEADLINE.getType());
        _taskTypeValues.put("cutoffs", TaskType.DEADLINE.getType());

        StringBuilder taskTypeAlias = new StringBuilder();

        for (String key: _taskTypeValues.keySet()) {
            taskTypeAlias.append(key);
            taskTypeAlias.append("|");
        }
        String regex = "^(%1$s)(\\s)?";
        taskTypeAlias = taskTypeAlias.deleteCharAt(taskTypeAlias.length()-1);
        taskRegex = String.format(regex, taskTypeAlias.toString());
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex, Argument argument) throws ParseException {
        String rawTaskId;
        String taskId;
        String lookahead = getLookAhead(tokens, currIndex, TASK_ID_LOOKAHEAD);
        
        rawTaskId = extractTaskId(lookahead);
        tokens = nullifyTokens(rawTaskId);
        taskId = processTaskId(rawTaskId);
        updateArgument(taskId, argument);
        return tokens;
    }

    private String extractTaskId(String lookahead) {
        String allRegex = taskRegex + idRegex;
        Pattern rawTaskIdPattern = Pattern.compile(allRegex, Pattern.CASE_INSENSITIVE);
        Matcher rawTaskIdMatcher = rawTaskIdPattern.matcher(lookahead);
        
        if(rawTaskIdMatcher.find()) {
            return rawTaskIdMatcher.group();
        }
        
        return null;
    }

    private ArrayList<String> nullifyTokens(String rawTaskId) {
        
        
        
        return null;
    }

    private String processTaskId(String taskId) {
        // TODO Auto-generated method stub
        return null;
    }

    private void updateArgument(String taskId, Argument argument) {
        // TODO Auto-generated method stub
        
    }
    
//
//private ArrayList<String> updateTaskID(ArrayList<String> dTokens) throws
//  ParseException{
// 
//  assert(dTokens!=null);
// 
//  if( dTokens.isEmpty() ){
//  throw new ParseException("String is too small for modify!", 0);
//  }
// 
//  for (int tokenCount=1; tokenCount<3; tokenCount++) {
//  String taskId = extractTaskID(dTokens, tokenCount);
//  taskId = getProperTaskId(taskId);
//  if(taskId!=null) {
//  dTokens = removeTaskIdFromTokens(dTokens, tokenCount);
//  _arguments.put(TASKID, taskId);
//  logger.info(dTokens);
//  return dTokens;
//  }
//  }
//  
//private String extractTaskID(ArrayList<String> dTokens, int numOfToken) {
//
//String taskExtract = "";
//
//for (int i=0; i<numOfToken && i < dTokens.size(); i++) {
//taskExtract += dTokens.get(i);
//}
//
//return taskExtract;
//}
//
//private ArrayList<String> removeTaskIdFromTokens(ArrayList<String> dTokens,
//int numOfToken) {
//
//assert(numOfToken<=2);
//
//for (int i=0; i<numOfToken; i++) {
//dTokens.remove(FIRST);
//}
//return dTokens;
//}
//
///**
//* Extract out the proper task id
//*
//* @param taskId
//* @return the task id. If it's invalid, return null.
//*/
//private String getProperTaskId(String taskId) {
//
//logger.info("FINDING TASK ID FOR: " + taskId);
//
//Pattern patternId = Pattern.compile("\\d+$");
//Matcher matcherId = patternId.matcher(taskId);
//
//String regex = "^(%1$s)(\\s)?";
//StringBuilder taskTypeAlias = new StringBuilder();
//
//for (String key: TASKTYPE_ALIAS.keySet()) {
//taskTypeAlias.append(key);
//taskTypeAlias.append("|");
//}
//taskTypeAlias = taskTypeAlias.deleteCharAt(taskTypeAlias.length()-1);
//regex = String.format(regex, taskTypeAlias.toString());
//
//logger.info("Task id pattern: " + regex);
//
//Pattern patternTask = Pattern.compile(regex);
//Matcher matcherTaskType = patternTask.matcher(taskId);
//
//boolean hasMatchId = matcherId.find();
//boolean hasMatchTask = matcherTaskType.find();
//
//if (hasMatchId && hasMatchTask) {
//
//String id = matcherId.group();
//String taskType = matcherTaskType.group();
//
//logger.info("Found match for " + taskId + "\n" +
//"Task type: " + taskType + "\n" +
//"ID: " + id);
//
//taskType = getTaskType(taskType);
//return taskType + SPACE + id;
//
//} else {
//logger.info("Returning null for: " + taskId + "\n" +
//"Mismatch type: " + hasMatchId + "\n" +
//"Mismatch id: " + hasMatchTask);
//return null;
//}
//}
}
