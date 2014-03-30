package sg.edu.nus.cs2103t.mina.commandcontroller;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandParser {

    
    private static final String SEARCH_DELIMIT = "//";
    private static final String SEGMENT_END_DELIMIT = "' ";
    private static final String SEGMENT_START_DELIMIT = " '";
    private static final String REDO = "redo";
    private static final String SEARCH = "search";
    private static final String UNDO = "undo";
    private static final String EXIT = "exit";
    private static final String COMPLETE = "complete";
    private static final String DISPLAY = "display";
    private static final String DELETE = "delete";
    private static final String ADD = "add";
    private static final String TASKID = "taskid";
    private static final String MODIFY = "modify";
    private static final String VALIDITY = "v";
    private static final String IS_VALID = "valid";
    private static final String PM = "pm";
    private static final String AM = "am";
    private static final String EMPTY = "";
    private static final String TIME_KEY = "time";
    private static final String DATE_KEY = "date";
    private static final int NEXT = 1;
    private static final int ONE_DAY = 1;
    
    private static final int ACTION_INDEX = 0;
    
    //Specifying argument keys
    private static final String SPACE = " ";
    private static final String ACTION = "action";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";
    private static final String END = "end";
    private static final String START = "start";
    private static final String TO_TASK_TYPE = "totype";
    
    private static final HashMap<String, String> ACTIONS_KEYWORDS = new HashMap<String, String>();
    private static final HashMap<String, String> SINGLE_ACTION_KEYWORD = new HashMap<String, String>();
    private static final HashMap<String, Boolean> END_KEYWORDS = new HashMap<String, Boolean>();
    private static final HashMap<String, Boolean> START_KEYWORDS = new HashMap<String, Boolean>();
    private static final HashMap<String, Boolean> TO_TASK_TYPE_KEYWORDS = new HashMap<String, Boolean>();
    
    private static final HashMap<String, DateTime> END_VALUES = new HashMap<String, DateTime>();
    private static final HashMap<String, String> PRIORITY_VALUES = new HashMap<String, String>();
    private static final HashMap<String, String> DISPLAY_VALUES = new HashMap<String, String>();
    
    private static final HashMap<String, String> TASKTYPE_ALIAS = new HashMap<String, String>();
    
    private HashMap<Integer, Boolean> keyFlags = new HashMap<Integer, Boolean>();
    
    private static final LinkedHashSet<String> TIME = new LinkedHashSet<String>();
    private static final int DATETIME_VALUE_KEYWORD = 0;
    private static final int DATE_VALUE = 1;
    private static final int TIME_VALUE = 2;
    private static final int PROPER_DATE_TIME = 3;
    private static final int INVALID_VALUE = -1;
    
    private static final int PRIORITY_FLAG = 0;
    private static final int END_FLAG = 1;
    private static final int END_CONTINUE_FLAG = 2;
    private static final int DESCRIPTION_FLAG = 3;
    private static final int START_FLAG = 4;
    private static final int START_CONTINUE_FLAG = 5;
    private static final int TO_TASK_TYPE_FLAG = 6;
    private static final int AGENDAOF_FLAG = 7;
    
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int ONE_TOKEN = 1;
    private static final int TWO_TOKEN = 2;
    private static final int ARG_INDEX = 1;
    private static final int LAST = 1;
    private static final boolean IS_WHOLE_SEGMENT = true;
    
    
    
    
    private HashMap<String, String> _arguments;
    
    private enum ActionsTaskID{
        MODIFY(CommandParser.MODIFY),
        DELETE(CommandParser.DELETE),
        COMPLETE(CommandParser.COMPLETE);
        
        private String _action;
        
        private ActionsTaskID(String value){
            _action = value;
        }
        private String getValue(){
            return _action;
        }
    }
    
    private static Logger logger = LogManager.getLogger(CommandParser.class
            .getName());

    public CommandParser() {
        initArgMap();
        
        ACTIONS_KEYWORDS.put(ADD, ADD);
        ACTIONS_KEYWORDS.put("make", ADD);
        ACTIONS_KEYWORDS.put("create", ADD);
        ACTIONS_KEYWORDS.put("new", ADD);
        ACTIONS_KEYWORDS.put("+", ADD);
        
        ACTIONS_KEYWORDS.put(MODIFY, MODIFY);
        ACTIONS_KEYWORDS.put("change", MODIFY);
        ACTIONS_KEYWORDS.put("edit", MODIFY);
        
        ACTIONS_KEYWORDS.put("remove", DELETE);
        ACTIONS_KEYWORDS.put(DELETE, DELETE);
        
        ACTIONS_KEYWORDS.put(SEARCH, SEARCH);
        ACTIONS_KEYWORDS.put("find", SEARCH);
        
        ACTIONS_KEYWORDS.put(DISPLAY, DISPLAY);
        ACTIONS_KEYWORDS.put("filter", DISPLAY);
        ACTIONS_KEYWORDS.put("show", DISPLAY);
        
        ACTIONS_KEYWORDS.put(COMPLETE, COMPLETE);
        ACTIONS_KEYWORDS.put("finish", COMPLETE);
        
        SINGLE_ACTION_KEYWORD.put(EXIT, EXIT);
        SINGLE_ACTION_KEYWORD.put("quit", EXIT);
        SINGLE_ACTION_KEYWORD.put(UNDO, UNDO);
        SINGLE_ACTION_KEYWORD.put(REDO, REDO);
        
        TO_TASK_TYPE_KEYWORDS.put("totype", false);
        TO_TASK_TYPE_KEYWORDS.put("-totype", true);
        TO_TASK_TYPE_KEYWORDS.put("changeto", false);
        TO_TASK_TYPE_KEYWORDS.put("-changeto", true);
        TO_TASK_TYPE_KEYWORDS.put("totask", false);
        TO_TASK_TYPE_KEYWORDS.put("-totask", true);
        
        END_KEYWORDS.put("end", false);
        END_KEYWORDS.put("due", false);
        END_KEYWORDS.put("by", false);
        END_KEYWORDS.put("before", false);
        END_KEYWORDS.put("on", false);
        END_KEYWORDS.put("to", false);
        
        END_KEYWORDS.put("-end", true);
        END_KEYWORDS.put("-due", true);
        END_KEYWORDS.put("-by", true);
        END_KEYWORDS.put("-before", true);
        END_KEYWORDS.put("-on", true);
        END_KEYWORDS.put("-to", true);
        
        START_KEYWORDS.put("start", false);
        START_KEYWORDS.put("-start", true);
        START_KEYWORDS.put("starting", false);
        START_KEYWORDS.put("-starting", true);
        START_KEYWORDS.put("from", false);
        START_KEYWORDS.put("-from", true);
        
        PRIORITY_VALUES.put("low", "L");
        PRIORITY_VALUES.put("l", "L");
        PRIORITY_VALUES.put("medium", "M");
        PRIORITY_VALUES.put("m", "M");
        PRIORITY_VALUES.put("med", "M");
        PRIORITY_VALUES.put("h", "H");
        PRIORITY_VALUES.put("high", "H");
        PRIORITY_VALUES.put("urgent", "H");
        PRIORITY_VALUES.put("-urgent", "H");
        
        
        initEndValues();
        
        TASKTYPE_ALIAS.put(TaskType.EVENT.getType(), TaskType.EVENT.getType());
        TASKTYPE_ALIAS.put("e", TaskType.EVENT.getType());
        TASKTYPE_ALIAS.put("events", TaskType.EVENT.getType());
        TASKTYPE_ALIAS.put("appointment", TaskType.EVENT.getType());
        TASKTYPE_ALIAS.put("appt", TaskType.EVENT.getType());
        TASKTYPE_ALIAS.put("appts", TaskType.EVENT.getType());
        
        TASKTYPE_ALIAS.put(TaskType.TODO.getType(), TaskType.TODO.getType());
        TASKTYPE_ALIAS.put("td", TaskType.TODO.getType());
        TASKTYPE_ALIAS.put("to-do", TaskType.TODO.getType());
        TASKTYPE_ALIAS.put("todos", TaskType.TODO.getType());
        TASKTYPE_ALIAS.put("task", TaskType.TODO.getType());
        TASKTYPE_ALIAS.put("tasks", TaskType.TODO.getType());
        
        TASKTYPE_ALIAS.put(TaskType.DEADLINE.getType(), TaskType.DEADLINE.getType());
        TASKTYPE_ALIAS.put("d", TaskType.DEADLINE.getType());
        TASKTYPE_ALIAS.put("deadlines", TaskType.DEADLINE.getType());
        TASKTYPE_ALIAS.put("cutoff", TaskType.DEADLINE.getType());
        TASKTYPE_ALIAS.put("cutoffs", TaskType.DEADLINE.getType());
        
        DISPLAY_VALUES.put("completed", FilterType.COMPLETE.getType());
        DISPLAY_VALUES.put("all", FilterType.COMPLETE_PLUS.getType());
        
    }

    private void initEndValues() {
        DateTime today =  DateTime.today(TimeZone.getDefault());
        END_VALUES.put("today", today);
        END_VALUES.put("tomorrow", today.plusDays(ONE_DAY));
        END_VALUES.put("tmr", today.plusDays(ONE_DAY));
        END_VALUES.put("tommorrow", today.plusDays(ONE_DAY)); 
        END_VALUES.put("yesterday", today.minusDays(ONE_DAY));
    }

    public String convertCommand(String userInput) throws NullPointerException,
            ParseException {
        
        if (userInput == null) {
            throw new NullPointerException();
        }
        if (userInput.equals(EMPTY)) {
            throw new ParseException("Empty String!", 0);
        }
        
        initArgMap();

        StringBuilder originalString = new StringBuilder(userInput);

        // helpers
        StringBuilder result = new StringBuilder();
        boolean isWrapped = false;
       //Map<String, String> startDateMap;
        String endTime = EMPTY;
        String endDate = EMPTY;
        
        
        String[] rawTokens = userInput.trim().split(SPACE,2);
        logger.info("Distinguishing action: " + Arrays.toString(rawTokens));
        
        //get action
        String action = rawTokens[ACTION_INDEX].toLowerCase();
        
        //short circuit, if the action needs no argument
        if(SINGLE_ACTION_KEYWORD.containsKey(action)) {
            return userInput;
        }
        
        if (ACTIONS_KEYWORDS.containsKey(action)) {
            _arguments.put(ACTION, ACTIONS_KEYWORDS.get(action));            
        } else {
            throw new ParseException("No such action", 0);
        }
        
        //process the rest of arguments
        //guard clause
        if(rawTokens.length<2) {
           return userInput;
        }
        
        //Grant exceptions to certain actions that require special
        //processing.
        if(_arguments.get(ACTION).equalsIgnoreCase(SEARCH)) {
            return convertToSearch(rawTokens[ARG_INDEX]);
        }
        
        //TODO get description
        originalString = new StringBuilder(rawTokens[ARG_INDEX]);
        originalString = originalString.insert(0, SPACE);
        originalString = originalString.append(SPACE);
        
        int first = originalString.indexOf(SEGMENT_START_DELIMIT);
        int last = originalString.lastIndexOf(SEGMENT_END_DELIMIT);
        if (first != -1 && last > first) {
            String descript = originalString.substring(first + 2, last);
            _arguments.put(DESCRIPTION, descript.trim());
            originalString.delete(first+1, last + 2);
            isWrapped = true;
            logger.info("Original Parse: " + originalString);
        }
        
        //tokenize the whole thing
        String[] tokens = tokenizeString(originalString);
        ArrayList<String> dTokens = new ArrayList<String>();
        Collections.addAll(dTokens, tokens);
        
        //logger.info("Type of action: " + _arguments.get(ACTION));
        if(isRequiredTaskId()) {
            logger.info("Updating task id: " + dTokens);
            dTokens = updateTaskID(dTokens);
        }
        
        initKeyFlags();
        
        logger.info("Original: " + userInput);
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < dTokens.size(); i++) {
           
            String keyword = dTokens.get(i).toLowerCase();
            
            //Flagging for priority
            if (keyFlags.get(PRIORITY_FLAG) && isValidPriorityValue(keyword)) {
                keyFlags.put(PRIORITY_FLAG, false);
                addPriority(keyword);
                continue;
            } else if (keyFlags.get(PRIORITY_FLAG)){
                throw new ParseException("Invalid priority", 0);
            }
            
            //Flagging for End || start
            if (keyFlags.get(END_FLAG) || keyFlags.get(END_CONTINUE_FLAG)) {
                
                logger.info("Extracting END: ");
                logger.info("Setting end flag to false ");  
                logger.info("End continue flag: " + keyFlags.get(END_CONTINUE_FLAG));
                
                keyFlags.put(END_FLAG, false);
                boolean isValid = true;
                boolean isReset = false;
                
                HashMap<String, String> dateAndTime = new HashMap<String, String>();
                dateAndTime.put(DATE_KEY, endDate);
                dateAndTime.put(TIME_KEY, endTime);
                dateAndTime.put(IS_VALID, VALIDITY);
                
                dateAndTime = updateDateTimeArg(dateAndTime, keyword);
                
                endDate = dateAndTime.get(DATE_KEY);
                endTime = dateAndTime.get(TIME_KEY);
                if(dateAndTime.get(IS_VALID)==null) {
                    isValid = false;
                }
                
                if(!isFirstDateInvalid(isValid, keyFlags.get(END_CONTINUE_FLAG))){
                    throw new ParseException("Invalid end date",0);
                } else if(isNeedSecondDate(keyFlags.get(END_CONTINUE_FLAG))) {
                    logger.info("Found first valid datetime");
                    keyFlags.put(END_CONTINUE_FLAG, true);                   
                } else {
                    logger.info("Ending second datetime");
                    keyFlags.put(END_CONTINUE_FLAG, false);
                    isReset = true;
                }
                
                addEnd(endDate + " " + endTime);
                
                if(isReset) {
                    endDate = EMPTY;
                    endTime = EMPTY;
                }
                
                if(isValid){
                    continue;
                }
            }

            //Flagging for End || start
            if (keyFlags.get(START_FLAG) || keyFlags.get(START_CONTINUE_FLAG)) {
                
                logger.info("Extracting start: ");
                logger.info("Setting start flag to false ");
                logger.info("Start continue flag: " + keyFlags.get(START_CONTINUE_FLAG));
                
                keyFlags.put(START_FLAG, false);
                boolean isValid = true;
                boolean isReset = false;
                
                HashMap<String, String> dateAndTime = new HashMap<String, String>();
                dateAndTime.put(DATE_KEY, endDate);
                dateAndTime.put(TIME_KEY, endTime);
                dateAndTime.put(IS_VALID, VALIDITY);
                
                dateAndTime = updateDateTimeArg(dateAndTime, keyword);
                
                endDate = dateAndTime.get(DATE_KEY);
                endTime = dateAndTime.get(TIME_KEY);
                if(dateAndTime.get(IS_VALID)==null) {
                    isValid = false;
                }
                
                if(!isFirstDateInvalid(isValid, keyFlags.get(START_CONTINUE_FLAG))){
                    throw new ParseException("Invalid end date",0);
                } else if(isNeedSecondDate(keyFlags.get(START_CONTINUE_FLAG))) {
                    logger.info("Found first valid datetime");
                    keyFlags.put(START_CONTINUE_FLAG, true);                   
                } else {
                    logger.info("Ending second datetime");
                    keyFlags.put(START_CONTINUE_FLAG, false); 
                    isReset = true;
                }
                
                addStart(endDate + " " + endTime);
                
                if(isReset) {
                    endDate = EMPTY;
                    endTime = EMPTY;
                }
                
                if(isValid){
                    continue;
                }
            }
            
            if(keyFlags.get(TO_TASK_TYPE_FLAG)){
                keyFlags.put(TO_TASK_TYPE_FLAG, false);
                addToType(keyword);
                continue;
            }
            
            if (keyFlags.get(AGENDAOF_FLAG)) {
                keyFlags.put(AGENDAOF_FLAG, false);
                String currDate = converToMilitaryDate(keyword);
                String newStart = currDate + "000000";
                String newEnd = currDate + "235959";
                List<String> newArguments = Arrays.asList("-start", newStart, "-end", newEnd);
                dTokens.remove(i);
                dTokens.addAll(i, newArguments);
            }
            
            //Checking for flags
            if (keyword.equals(PRIORITY) && !hasValue(PRIORITY) && isWrapped) {
                int prevIndex = i - 1;
                if (prevIndex>=0 && isValidPriorityValue(dTokens.get(prevIndex)) ) {
                    addPriority(dTokens.get(prevIndex));
                    continue;
                } else {
                    keyFlags.put(PRIORITY_FLAG, true);
                    continue;
                }
            }

            if (keyword.equals("-priority") && !hasValue(PRIORITY)) {
                keyFlags.put(PRIORITY_FLAG, true);
                continue;
            }
            
            //If no dash, but description is wrapped or has dash
            if (END_KEYWORDS.containsKey(keyword) && !hasValue(END) &&
                    (END_KEYWORDS.get(keyword) || isWrapped)) {
                logger.info("Setting End flag as true");
                keyFlags.put(END_FLAG, true);
                continue;
                
            }
            
            //If no dash, but description is wrapped or has dash
            if (START_KEYWORDS.containsKey(keyword) && !hasValue(START) &&
                    (START_KEYWORDS.get(keyword) || isWrapped)) {
                logger.info("Setting Start flag as true");
                keyFlags.put(START_FLAG, true);
                continue;
                
            }
            
            if(isModifyAction() && 
                    TO_TASK_TYPE_KEYWORDS.containsKey(keyword) &&
                    !hasValue(TO_TASK_TYPE) && 
                    (TO_TASK_TYPE_KEYWORDS.get(keyword) || isWrapped)) {
                logger.info("Setting totype flag as true");
                keyFlags.put(TO_TASK_TYPE_FLAG, true);
                continue;                
            }
            
            if ((keyword.equalsIgnoreCase("urgent") && isWrapped) ||
                   (keyword.equalsIgnoreCase("-urgent") && !isWrapped) ) {
                addPriority(keyword);
                continue;
            }
            
            
            if(isFilterAction() && TASKTYPE_ALIAS.containsKey(keyword)){
                dTokens.set(i, getTaskType(keyword));
            }
            
            if(isFilterAction() && DISPLAY_VALUES.containsKey(keyword)) {
                dTokens.set(i, DISPLAY_VALUES.get(keyword));
            }
            
            if(isFilterAction() && keyword.equalsIgnoreCase("-agendaof")) {
                keyFlags.put(AGENDAOF_FLAG, true);
                continue;
            }
            
            if (!hasValue(DESCRIPTION) && 
                    !(isModifyAction() && (keyword.equals("-description")))) {
                String word = dTokens.get(i).trim();
                if(!word.equals(EMPTY)){
                    description.append(dTokens.get(i));
                    description.append(SPACE);
                }
            }
        }
        
        if (!hasValue(DESCRIPTION)) {
            String descript =  description.toString().trim();
            if(!descript.equals(EMPTY)){
                _arguments.put(DESCRIPTION, descript);
            }
        }
        
        result = getProperCommands();

        logger.info(result);
        return result.toString().trim();
    }

    private String convertToSearch(String rawTokens) {
        
        //extract all ' ' out first
        rawTokens = SPACE + rawTokens + SPACE;
        StringBuilder rawSearchArg = new StringBuilder(rawTokens);
        StringBuilder searchArg = new StringBuilder();
        
        
        Pattern phrasePattern = Pattern.compile("\\s'.*?'\\s");

        Matcher phraseMatcher = phrasePattern.matcher(rawTokens);
//        if(phraseMatcher.find()) {
//            int matchIndex;
//            do {
//                String match = phraseMatcher.group();
//                searchArg.append(match.trim());
//                searchArg.append(SEARCH_DELIMIT);
//                matchIndex = rawSearchArg.indexOf(match);
//                rawSearchArg.delete(matchIndex + 1, matchIndex + match.length());
//            } while(phraseMatcher.find(phraseMatcher.start(1)));
//        }
        
        ArrayList<String> phrases = new ArrayList<String>();
        logger.info("Match count: " + phraseMatcher.groupCount());
        while(phraseMatcher.find()){
            String match = phraseMatcher.group();
            phrases.add(match);
            logger.info(match);
            int matchIndex = rawSearchArg.indexOf(match);
            rawSearchArg.delete(matchIndex + 1, matchIndex + match.length());
            rawSearchArg.insert(0, SPACE);
            phraseMatcher.reset(rawSearchArg);
        }
        
        for(String phrase: phrases){
            phrase = phrase.trim();
            int first = phrase.indexOf("'");
            int last = phrase.lastIndexOf("'");
            phrase = phrase.substring(first+1, last);
            searchArg.append(phrase);
            searchArg.append(SEARCH_DELIMIT);   
        }
        
        String words[] = tokenizeString(rawSearchArg);
        for (int i=0; i<words.length; i++) {
            String word = words[i].trim();
            if(!word.equals(EMPTY)){
                searchArg.append(word);
                searchArg.append(SEARCH_DELIMIT);                
            }
        }
        
        int lastDelimiter = searchArg.lastIndexOf(SEARCH_DELIMIT);
        String result = SEARCH + SPACE + searchArg.toString().substring(0, lastDelimiter);
        logger.info(result);
        return result;
        
    }

    private String[] tokenizeString(StringBuilder rawString) {
        return rawString.toString().trim().split(SPACE);
    }

    private int[] getSegmentIndex(StringBuilder argument, boolean isWholeSegment) {
        
        int first = argument.indexOf(SEGMENT_START_DELIMIT);
        int last;
        if (isWholeSegment) {
            last = argument.lastIndexOf(SEGMENT_END_DELIMIT);
        } else {
            last = argument.indexOf(SEGMENT_END_DELIMIT, first + 1);
        }
        
        return new int[]{first, last};
    }

    private String extractSegment(StringBuilder argument, int[] substringIndexes, boolean isWholeSegment) {
        
        int[] indexes = getSegmentIndex(argument, isWholeSegment);
        int first = indexes[FIRST];
        int last = indexes[LAST];
        
        if (hasStringSegments(argument)) {
            return argument.substring(first + 2, last);
        }
        
        return null;
    }

    private boolean hasStringSegments(StringBuilder argument) {
        int[] indexes = getSegmentIndex(argument, IS_WHOLE_SEGMENT);
        return indexes[FIRST] != -1 && indexes[LAST] > indexes[FIRST];
    }

    private void addToType(String keyword) {
        keyword = getTaskType(keyword);
        _arguments.put(TO_TASK_TYPE, keyword);
        
    }
    
    /**
     * 
     * @param keyword
     * @return
     */
    private String getTaskType(String keyword){
        
        keyword = keyword.trim().toLowerCase();
        
        if(TASKTYPE_ALIAS.containsKey(keyword)) {
            return TASKTYPE_ALIAS.get(keyword);
        } else {
            return keyword;
        }
        
    }
    
    private ArrayList<String> updateTaskID(ArrayList<String> dTokens) throws ParseException{
        
        assert(dTokens!=null);
        
        if( dTokens.isEmpty() ){
            throw new ParseException("String is too small for modify!", 0);
        }
        
        for (int tokenCount=1; tokenCount<3; tokenCount++) {
            String taskId = extractTaskID(dTokens, tokenCount);
            taskId = getProperTaskId(taskId);
            if(taskId!=null) {
                dTokens = removeTaskIdFromTokens(dTokens, tokenCount);
                _arguments.put(TASKID, taskId);
                logger.info(dTokens);
                return dTokens;  
            }
        }
        
        throw new ParseException("Malformed task id", 0); 
    }

    private String extractTaskID(ArrayList<String> dTokens, int numOfToken) {
        
        String taskExtract = "";
 
        for (int i=0; i<numOfToken && i < dTokens.size(); i++) {
            taskExtract += dTokens.get(i);
        }

        return taskExtract;
    }
    
    private ArrayList<String> removeTaskIdFromTokens(ArrayList<String> dTokens, int numOfToken) {
        
        assert(numOfToken<=2);
        
        for (int i=0; i<numOfToken; i++) {
            dTokens.remove(FIRST);
        }
        return dTokens;
    }

    /**
     * Extract out the proper task id
     * 
     * @param taskId
     * @return the task id. If it's invalid, return null.
     */
    private String getProperTaskId(String taskId) {
        
        logger.info("FINDING TASK ID FOR: " + taskId);
        
        Pattern patternId = Pattern.compile("\\d+$");
        Matcher matcherId = patternId.matcher(taskId);
        
        String regex = "^(%1$s)(\\s)?";
        StringBuilder taskTypeAlias = new StringBuilder();
        
        for (String key: TASKTYPE_ALIAS.keySet()) {
            taskTypeAlias.append(key);
            taskTypeAlias.append("|");
        }
        taskTypeAlias = taskTypeAlias.deleteCharAt(taskTypeAlias.length()-1);
        regex = String.format(regex, taskTypeAlias.toString());
        
        logger.info("Task id pattern: " + regex);
        
        Pattern patternTask = Pattern.compile(regex);
        Matcher matcherTaskType = patternTask.matcher(taskId);
        
        boolean hasMatchId = matcherId.find();
        boolean hasMatchTask = matcherTaskType.find();
        
        if (hasMatchId && hasMatchTask) {
           
           String id = matcherId.group();
           String taskType = matcherTaskType.group();
           
           logger.info("Found match for " + taskId + "\n" +
                       "Task type: " + taskType + "\n" + 
                       "ID: " +  id);
           
           taskType = getTaskType(taskType);
           return taskType + SPACE + id;
           
        } else {
           logger.info("Returning null for: " + taskId + "\n" +
                       "Mismatch type: " + hasMatchId + "\n" + 
                       "Mismatch id: " + hasMatchTask);
           return null;
        }
    }
    
    private void initKeyFlags() {
        keyFlags.put(PRIORITY_FLAG, false);
        keyFlags.put(END_FLAG, false);
        keyFlags.put(END_CONTINUE_FLAG, false);
        keyFlags.put(START_FLAG, false);
        keyFlags.put(START_CONTINUE_FLAG, false);
        keyFlags.put(DESCRIPTION_FLAG, false);
        keyFlags.put(TO_TASK_TYPE_FLAG, false);
        keyFlags.put(AGENDAOF_FLAG, false);
    }

    private boolean isNeedSecondDate(boolean continueFlag) {
        return !continueFlag;
    }

    private boolean isFirstDateInvalid(boolean isValid, Boolean continueFlag) {
        return isValid || continueFlag;
    }

    private HashMap<String, String> updateDateTimeArg(HashMap<String, String> dateAndTime, 
                                                        String keyword) throws ParseException{
        String endDate = dateAndTime.get(DATE_KEY);
        String endTime = dateAndTime.get(TIME_KEY);
        
        switch(getDateValueType(keyword)) {
            case DATE_VALUE:
                //FALLTHROUGH
            case DATETIME_VALUE_KEYWORD :
                logger.info("Getting date: " + keyword);
                endDate = (endDate.equals(EMPTY)) ? converToMilitaryDate(keyword) : endDate;
                break;
            case PROPER_DATE_TIME :
                endDate = (endDate.equals(EMPTY)) ? keyword : endDate;
                break;                
            case TIME_VALUE:
                logger.info("Getting time: " + keyword);
                endTime = (endTime.equals(EMPTY)) ? convertToMilitaryTime(keyword) : endTime;
                break;
            default:
                logger.info("Invalid datetime");
                dateAndTime.put(IS_VALID, null);
                return dateAndTime;
        }        
        
        dateAndTime.put(DATE_KEY, endDate);
        dateAndTime.put(TIME_KEY, endTime);
        return dateAndTime;
        
    }
    
    private String converToMilitaryDate(String date) throws ParseException{
        
        initEndValues();
        
        String converted;
        
        if(END_VALUES.containsKey(date)) {
            DateTime currDate = END_VALUES.get(date);
            converted = currDate.format("DDMMYYYY");
        } else {
            SimpleDateFormat milDateFormat = new SimpleDateFormat("ddMMyyyy");
            Date convertedDate = DateUtil.parse(date);
            converted = milDateFormat.format(convertedDate);
        }
        
        return converted;
    }

    private int getDateValueType(String dateTime) throws ParseException{
        
        //Check keyword
        if(END_VALUES.containsKey(dateTime)) {
            logger.info(dateTime + " is special time keyword");
            return DATETIME_VALUE_KEYWORD;
        }

        //check actual date
        String format = DateUtil.determineDateFormat(dateTime);
        
        if(format!=null && (format.equals("ddMMyyyyHHmm") || 
                            format.equals("ddMMyyyyHHmmss"))){
            logger.info(dateTime + "is military format date");
            return PROPER_DATE_TIME;
        } else if(format!=null) {
            logger.info(dateTime + " is date but not in mil-format");
            return DATE_VALUE;
        }
        
        //check time
        if(isTimeFormat(dateTime)) {
            logger.info(dateTime + " is time");
            return TIME_VALUE;
        }
        
        logger.info(dateTime + " is invalid");
        return INVALID_VALUE;
    }

    private boolean isTimeFormat(String dateTime) throws ParseException{
        
        if (hasAmPm(dateTime)) {
            return true;
        }
        
        DateTime today = DateTime.today(TimeZone.getDefault());
        String military = today.format("DDMMYYYY") + " " + dateTime;
        String properDate = today.format("DD/MM/YYYY") + " " + dateTime;
        
        return DateUtil.determineDateFormat(military)!=null ||
                DateUtil.determineDateFormat(properDate)!=null;
    }

    private String convertToMilitaryTime(String dateTime) throws ParseException{
        
        dateTime = dateTime.toLowerCase().trim();
        //Guard clause
        logger.info("Converting " + dateTime + " to miltary date");
        if(isMilitaryDate(dateTime)){
            return dateTime;
        }
        LinkedHashMap<String, String> timeMap = new LinkedHashMap<String, String>();
        
        //HH AM/PM
        timeMap.put("^\\d{1}(am|pm){1}$", "ha");
        timeMap.put("^\\d{1,2}(am|pm){1}$", "hha");
        //HH no AM/PM
        timeMap.put("^\\d{1}$", "h");
        timeMap.put("^\\d{1,2}$", "hh");
        
        //HH:MM:SS AM/PM
        timeMap.put("^\\d{1,2}:\\d{2}(am|pm){1}", "hh:mma");
        timeMap.put("^\\d{1,2}:\\d{2}:\\{d2}(am|pm){1}", "hh:mm:ssa");
        //HH:MM:SS no AM/PM
        timeMap.put("^\\d{1,2}:\\d{2}", "hh:mm");
        timeMap.put("^\\d{1,2}:\\d{2}:\\{d2}", "hh:mm:ss");
        
        //HH.MM.SS AM/PM
        timeMap.put("^\\d{1,2}.\\d{2}(am|pm){1}", "hh.mma");
        timeMap.put("^\\d{1,2}.\\d{2}.\\{d2}(am|pm){1}", "hh.mm.ssa");
        //HH.MM.SS no AM/PM
        timeMap.put("^\\d{1,2}.\\d{2}", "hh.mm");
        timeMap.put("^\\d{1,2}.\\d{2}.\\{d2}", "hh.mm.ss");
        
        String pattern = "";
        boolean hasMatch = false;
        for(String regex: timeMap.keySet()){
            Pattern currPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher currMatch = currPattern.matcher(dateTime);
            if(currMatch.find()){
                pattern = timeMap.get(regex);
                hasMatch = true;
                break;
            } else {
                pattern = null;
            }
        }
        
        logger.info("DATETIME: |" + dateTime + "|\n" + 
                    "MATCH: " + pattern);
        
        if(!hasMatch) {
            throw new ParseException("Invalid time!", 0);
        }
        
        SimpleDateFormat rawDate = new SimpleDateFormat("dd/MM/yyyy" + SPACE + pattern);
        
        DateTime today = DateTime.today(TimeZone.getDefault());
        dateTime = today.format("DD/MM/YYYY") + SPACE + dateTime;
        try {
            Date time = rawDate.parse(dateTime);
            logger.info("DATETIME: " + time);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
            return timeFormat.format(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return dateTime;
    }

    private boolean isMilitaryDate(String dateTime) throws ParseException{
        DateTime today = DateTime.today(TimeZone.getDefault());
        String format = today.format("DDMMYYYY");
        int dateType = getDateValueType(format+dateTime);
        
        return dateType==PROPER_DATE_TIME;
    }

    private boolean hasAmPm(String dateTime) {
        return dateTime.toLowerCase().contains(AM) || dateTime.toLowerCase().contains(PM);
    }
    
    private void addTime(String timeArg, String dateTime)  throws ParseException{
        dateTime = dateTime.trim();
        dateTime = translateDateTime(dateTime);
        
        logger.info("Date Received: " + dateTime);
        String format = DateUtil.determineDateFormat(dateTime);
        Date rawDate = DateUtil.parse(dateTime);
        
        logger.info("Adding end date: " + dateTime + "\n" +
                    "Fomat: " + format + "\n" +
                    "Date: " + rawDate.toString());
        
        if(!format.contains("HH") && !isFilterAction()){
            logger.info("No time");
            int sec = 1000;
            int min = sec*60;
            int hour = min*60;
            rawDate.setTime(rawDate.getTime() + hour*23 + min*59 + sec*59);
        }
        
        SimpleDateFormat standard;
        if(!format.contains("HH") && isFilterAction()) {
            standard = new SimpleDateFormat("ddMMyyyy");
        } else {
            standard = new SimpleDateFormat("ddMMyyyyHHmmss");
        }
        String standardDate = standard.format(rawDate);
        logger.info("Time formatted: " + standardDate);
        _arguments.put(timeArg, standardDate);        
    }
    
    private boolean isFilterAction() {
        return _arguments.get(ACTION).equalsIgnoreCase(DISPLAY);
    }

    private void addEnd(String dateTime) throws ParseException{
        addTime(END, dateTime);
    }
    
    private void addStart(String dateTime) throws ParseException{
        addTime(START, dateTime);
    }

    private String translateDateTime(String dateTime) throws ParseException{
        
        if(DateUtil.isValidDate(dateTime)) {
            return dateTime;
        } else if(isTimeFormat(dateTime)){
            DateTime today = DateTime.today(TimeZone.getDefault());
            if(dateTime.contains(":")) {
                return today.format("DD/MM/YYYY") + SPACE  + dateTime;
            } else {
                return today.format("DDMMYYYY") + SPACE  + dateTime;
            }
        }
        
        logger.info("TRANSLATING DATETIME: " + dateTime);
        
        String[] dateTimeTokens = dateTime.split(SPACE);
        
        //refresh the time
        initEndValues();
        
        if(!END_VALUES.containsKey(dateTimeTokens[0])) {
            throw new ParseException("No such date: " + dateTimeTokens[0],0);
        }
        
        DateTime currDate = END_VALUES.get(dateTimeTokens[0]);
        String converted = currDate.format("DDMMYYYY");
        if (dateTimeTokens.length>1) {
            return converted + dateTimeTokens[1];
        } else {
            return converted;
        }

    }

    public boolean isValidPriorityValue(String key) {
        
        key = key.toLowerCase().trim();
        return PRIORITY_VALUES.containsKey(key);
    }
    
    public void addPriority(String key){
        _arguments.put(PRIORITY, PRIORITY_VALUES.get(key));
    }
    
    private StringBuilder getProperCommands() {
        StringBuilder result = new StringBuilder();
        logger.info("Building proper commands\n" + 
                    "Action: " + getFormattedValue(ACTION) + "\n" + 
                    "Description: " + getFormattedValue(DESCRIPTION) + "\n" + 
                    "Priority: " + getFormattedValue(PRIORITY) + "\n" +
                    "Start: " + getFormattedValue(START)+ "\n" +
                    "End: " + getFormattedValue(END));
        
        result.append(getFormattedValue(ACTION));
        
        if(isRequiredTaskId()) {
            result.append(getFormattedValue(TASKID));
        }
        
        if(isModifyAction() && hasValue(TO_TASK_TYPE)) {
            result.append(getFormattedKey(TO_TASK_TYPE));
            result.append(getFormattedValue(TO_TASK_TYPE));            
        }
        
        if(isModifyAction() && hasValue(DESCRIPTION)) {
            result.append(getFormattedKey(DESCRIPTION));
            result.append(getFormattedValue(DESCRIPTION));
        } else if(hasValue(DESCRIPTION)){
            result.append(getFormattedValue(DESCRIPTION));
        }
        
        if(hasValue(PRIORITY)) {
            result.append(getFormattedKey(PRIORITY));
            result.append(getFormattedValue(PRIORITY));
        }
        if(hasValue(START)){
            result.append(getFormattedKey(START));
            result.append(getFormattedValue(START));
        }
        if(hasValue(END)){
            result.append(getFormattedKey(END));
            result.append(getFormattedValue(END));
        }
        return result;
    }
    
    private boolean isRequiredTaskId() {
        String action = _arguments.get(ACTION);
        for(ActionsTaskID actionType: ActionsTaskID.values()) {
            if(actionType.getValue().equals(action)) {
                return true;
            }
        }
        return false;
    }

    private boolean isModifyAction() {
        return _arguments.get(ACTION).equalsIgnoreCase(MODIFY);
    }

    public String getFormattedKey(String key){
        return "-" + key + " ";
    }
    public String getFormattedValue(String key) {
        return _arguments.get(key) + SPACE;
    }

    private void initArgMap() {

        _arguments = new HashMap<String, String>();

        _arguments.put(ACTION, null);
        _arguments.put(PRIORITY, null);
        _arguments.put(DESCRIPTION, null);
        _arguments.put(END, null);
        _arguments.put(START, null);
        _arguments.put(TASKID, null);
        _arguments.put(TO_TASK_TYPE, null);

    }
    
    private boolean hasValue(String key) {
        return _arguments.get(key) != null;
    }
}
