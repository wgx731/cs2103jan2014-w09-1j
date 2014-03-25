package sg.edu.nus.cs2103t.mina.commandcontroller;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandParser {

    
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
    private static final String SPACE = " ";
    private static final String ACTION = "action";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";
    private static final String END = "end";
    private static final String START = "start";
    
    private static final HashMap<String, String> ACTIONS_KEYWORDS = new HashMap<String, String>();
    private static final HashMap<String, Boolean> END_KEYWORDS = new HashMap<String, Boolean>();
    private static final HashMap<String, Boolean> START_KEYWORDS = new HashMap<String, Boolean>();
    
    private static final HashMap<String, DateTime> END_VALUES = new HashMap<String, DateTime>();
    private static final HashMap<String, String> PRIORITY_VALUES = new HashMap<String, String>();
    
    private HashMap<Integer, Boolean> keyFlags = new HashMap<Integer, Boolean>();
    
    private static final LinkedHashSet<String> TIME = new LinkedHashSet<String>();
    private static final int DATETIME_VALUE_KEYWORD = 0;
    private static final int DATE_VALUE = 1;
    private static final int TIME_VALUE = 2;
    private static final int INVALID_VALUE = -1;
    
    private static final int PRIORITY_FLAG = 0;
    private static final int END_FLAG = 1;
    private static final int END_CONTINUE_FLAG = 2;
    private static final int DESCRIPTION_FLAG = 3;
    private static final int START_FLAG = 4;
    private static final Integer START_CONTINUE_FLAG = 5;
    
    private HashMap<String, String> _arguments;
    private static Logger logger = LogManager.getLogger(CommandParser.class
            .getName());

    public CommandParser() {
        initArgMap();
        
        ACTIONS_KEYWORDS.put("add", "add");
        ACTIONS_KEYWORDS.put("make", "add");
        ACTIONS_KEYWORDS.put("create", "add");
        ACTIONS_KEYWORDS.put("new", "add");
        ACTIONS_KEYWORDS.put("+", "add");
        
        ACTIONS_KEYWORDS.put(MODIFY, MODIFY);
        ACTIONS_KEYWORDS.put("change", MODIFY);
        ACTIONS_KEYWORDS.put("edit", MODIFY);
        
        ACTIONS_KEYWORDS.put("remove", "delete");
        ACTIONS_KEYWORDS.put("delete", "delete");
        
        ACTIONS_KEYWORDS.put("search", "search");
        ACTIONS_KEYWORDS.put("find", "search");
        
        ACTIONS_KEYWORDS.put("display", "display");
        ACTIONS_KEYWORDS.put("filter", "display");
        ACTIONS_KEYWORDS.put("show", "display");
        
        ACTIONS_KEYWORDS.put("complete", "complete");
        ACTIONS_KEYWORDS.put("finish", "complete");
        
        ACTIONS_KEYWORDS.put("exit", "exit");
        
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
        END_KEYWORDS.put("-to", false);
        
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
        
        userInput+=" ";
        
        initArgMap();

        StringBuilder originalString = new StringBuilder(userInput);

        // helpers
        StringBuilder result = new StringBuilder();
        boolean isWrapped = false;
       //Map<String, String> startDateMap;
        String endTime = EMPTY;
        String endDate = EMPTY;
        
        int first = originalString.indexOf(" '");
        int last = originalString.lastIndexOf("' ");
        if (first != -1 && last > first) {
            String descript = originalString.substring(first + 2, last);
            _arguments.put(DESCRIPTION, descript.trim());
            originalString.delete(first+1, last + 2);
            isWrapped = true;
            logger.info("Original Parse: " + originalString);
        }

        String[] tokens = originalString.toString().trim().split(SPACE);
        
        ArrayList<String> dTokens = new ArrayList<String>();
        Collections.addAll(dTokens, tokens);
        
        String action = tokens[ACTION_INDEX].toLowerCase();
        if (ACTIONS_KEYWORDS.containsKey(action)) {
            
//            if(!ACTIONS_KEYWORDS.get(action).equalsIgnoreCase("add"))
//                return userInput;
            
            _arguments.put(ACTION, ACTIONS_KEYWORDS.get(action));
            if(isModify()){
                
            }
            
            
            dTokens.remove(ACTION_INDEX);
        } else {
            throw new ParseException("No such action", 0);
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
            
            //Checking for flags
            if (keyword.equals(PRIORITY) && !hasValue(PRIORITY) && isWrapped) {
                int prevIndex = i - 1;
                if ( isValidPriorityValue(dTokens.get(prevIndex)) ) {
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
            
            if ((keyword.equalsIgnoreCase("urgent") && isWrapped) ||
                   (keyword.equalsIgnoreCase("-urgent") && !isWrapped) ) {
                addPriority(keyword);
                continue;
            }
            
            
            if (!hasValue(DESCRIPTION)) {
                description.append(dTokens.get(i));
                description.append(SPACE);
            }
        }
        
        if (!hasValue(DESCRIPTION)) {
            _arguments.put(DESCRIPTION, description.toString().trim());
        }
        
        result = getProperCommands();

        logger.info(result);
        return result.toString().trim();
    }
    
    private void initKeyFlags() {
        keyFlags.put(PRIORITY_FLAG, false);
        keyFlags.put(END_FLAG, false);
        keyFlags.put(END_CONTINUE_FLAG, false);
        keyFlags.put(START_FLAG, false);
        keyFlags.put(START_CONTINUE_FLAG, false);
        keyFlags.put(DESCRIPTION_FLAG, false);
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
                endDate = (endDate.equals(EMPTY)) ? keyword : endDate;
                break;
            case TIME_VALUE:
                logger.info("Getting time: " + keyword);
                if (hasAmPm(keyword)) {
                    logger.info("Convering endTime");
                    endTime = convertToMilitaryTime(keyword);
                }
                endTime = (endTime.equals(EMPTY)) ? keyword : endTime;
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
    
    private int getDateValueType(String dateTime) throws ParseException{
        
        //Check keyword
        if(END_VALUES.containsKey(dateTime)) {
            return DATETIME_VALUE_KEYWORD;
        }

        //check actual date
        String format = DateUtil.determineDateFormat(dateTime);
        
        if(format!=null){
            return DATE_VALUE;
        } 
        
        //check time
        if(isTimeFormat(dateTime)) {
            return TIME_VALUE;
        }
        
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
        
        LinkedHashMap<String, String> timeMap = new LinkedHashMap<String, String>();
        
        //HH AM/PM
        timeMap.put("^\\d{1}(am|pm)?$", "ha");
        timeMap.put("^\\d{1,2}(am|pm)?$", "hha");
        
        //HH:MM:SS AM/PM
        timeMap.put("^\\d{1,2}:\\d{2}(am|pm)?", "hh:mma");
        timeMap.put("^\\d{1,2}:\\d{2}:\\{d2}(am|pm)?", "hh:mm:ssa");
        
        //HH.MM.SS AM/PM
        timeMap.put("^\\d{1,2}.\\d{2}(am|pm)?", "hh.mma");
        timeMap.put("^\\d{1,2}.\\d{2}.\\{d2}(am|pm)?", "hh.mm.ssa");
        
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
        
        if(!format.contains("HH")){
            logger.info("No time");
            int sec = 1000;
            int min = sec*60;
            int hour = min*60;
            rawDate.setTime(rawDate.getTime() + hour*23 + min*59 + sec*59);
        }
        
        SimpleDateFormat standard = new SimpleDateFormat("ddMMyyyyHHmmss");
        String standardDate = standard.format(rawDate);
        logger.info("Time formatted: " + standardDate);
        _arguments.put(timeArg, standardDate);        
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
        
        key = key.toLowerCase();
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
        
        if(isModify()) {
            result.append(getFormattedKey(DESCRIPTION));
            result.append(getFormattedValue(DESCRIPTION));
        } else {
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
    
    private boolean isModify() {
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

    }
    
    private boolean hasValue(String key) {
        return _arguments.get(key) != null;
    }
}
