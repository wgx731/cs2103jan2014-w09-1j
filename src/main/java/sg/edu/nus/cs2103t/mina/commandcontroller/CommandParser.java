package sg.edu.nus.cs2103t.mina.commandcontroller;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandParser {

    private static final String END = "end";
    private static final int ACTION_INDEX = 0;
    private static final String SPACE = " ";
    private static final String ACTION = "action";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";
    
    private static final HashMap<String, String> PRIORITY_VALUES = new HashMap<String, String>();
    private static final HashMap<String, String> ACTIONS_KEYWORDS = new HashMap<String, String>();
    
    private static final HashMap<String, Boolean> END_KEYWORDS = new HashMap<String, Boolean>();
    
    private CommandProcessor _cmdProcess;
    private HashMap<String, String> _arguments;
    private static Logger logger = LogManager.getLogger(CommandParser.class
            .getName());

    public CommandParser(CommandProcessor cmdProcess) {
        _cmdProcess = cmdProcess;
        initArgMap();
        
        ACTIONS_KEYWORDS.put("add", "add");
        ACTIONS_KEYWORDS.put("make", "add");
        ACTIONS_KEYWORDS.put("create", "add");
        ACTIONS_KEYWORDS.put("new", "add");
        ACTIONS_KEYWORDS.put("+", "add");
        
        END_KEYWORDS.put("end", false);
        END_KEYWORDS.put("due", false);
        END_KEYWORDS.put("by", false);
        END_KEYWORDS.put("-end", true);
        END_KEYWORDS.put("-due", true);
        END_KEYWORDS.put("-by", true);
        
        PRIORITY_VALUES.put("low", "L");
        PRIORITY_VALUES.put("l", "L");
        PRIORITY_VALUES.put("medium", "M");
        PRIORITY_VALUES.put("m", "M");
        PRIORITY_VALUES.put("med", "M");
        PRIORITY_VALUES.put("h", "H");
        PRIORITY_VALUES.put("high", "H");
        PRIORITY_VALUES.put("urgent", "H");
    }

    public String convertCommand(String userInput) throws NullPointerException,
            ParseException {
        
        if (userInput == null) {
            throw new NullPointerException();
        }
        if (userInput.equals("")) {
            throw new ParseException("Empty String!", 0);
        }

        initArgMap();

        StringBuilder originalString = new StringBuilder(userInput);

        // helpers
        StringBuilder result = new StringBuilder();
        boolean isWrapped = false;
        
        int first = originalString.indexOf(" '");
        int last = originalString.lastIndexOf("' ");
        if (first != -1 && last > first) {
            String descript = originalString.substring(first + 2, last);
            _arguments.put(DESCRIPTION, descript.trim());
            originalString.delete(first+1, last + 2);
            isWrapped = true;
            logger.info("KAH" + originalString);
        }

        String[] tokens = originalString.toString().split(SPACE);
        
        String action = tokens[ACTION_INDEX].toLowerCase();
        if (ACTIONS_KEYWORDS.containsKey(action)) {
            _arguments.put(ACTION, ACTIONS_KEYWORDS.get(action));
        } else {
            throw new ParseException("No such action", 0);
        }

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < tokens.length; i++) {

            if (tokens[i].equalsIgnoreCase(PRIORITY) && !hasValue(PRIORITY) && isWrapped) {
                int prevIndex = i - 1;
                if ( tryAddPriority(tokens[prevIndex]) ) {
                    continue;
                } else if (i + 1 < tokens.length && 
                        tryAddPriority(tokens[i + 1])) {
                    i++;
                    continue;
                }
            }

            if (tokens[i].equalsIgnoreCase("-priority") && !hasValue(PRIORITY)) {
                int nextIndex = i + 1;
                if (nextIndex < tokens.length && 
                        tryAddPriority(tokens[i + 1])) {
                    i++;
                    continue;
                }
            }
            
            //If no dash, but description is wrapped
            if (END_KEYWORDS.containsKey(tokens[i]) && !hasValue(END) &&
                    (END_KEYWORDS.get(tokens[i]) || isWrapped)) {
                int nextIndex = i + 1;
                if (nextIndex < tokens.length && 
                        isValidDate(tokens[i + 1])) {
                    addEnd(tokens[i + 1]);
                    i++;
                    continue;
                }                
            }
            
            if (tokens[i].equalsIgnoreCase("urgent") && isWrapped) {
                tryAddPriority(tokens[i]);
                continue;
            }
            
            if (!hasValue(DESCRIPTION)) {
                description.append(tokens[i]);
                description.append(SPACE);
            }
        }
        
        if (!hasValue(DESCRIPTION)) {
            _arguments.put(DESCRIPTION, description.toString().trim());
        }
        logger.info("Original: " + userInput);
        result = getProperCommands();

        logger.info(result);
        return result.toString().trim();
    }
    
    private boolean isValidDate(String dateTime){
        String format = DateUtil.determineDateFormat(dateTime);
        
        if(format==null){
            return false;
        } else {
            return true;
        }
        
    }

    private void addEnd(String dateTime) throws ParseException{
        
        StringBuilder dateBuilder = new StringBuilder();
        
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
        
        Calendar endDate = DateUtil.toCalendar(rawDate);
        SimpleDateFormat standard = new SimpleDateFormat("ddMMyyyyHHmmss");
        String standardDate = standard.format(rawDate);
        logger.info("Time formatted: " + standardDate);
        _arguments.put(END, standardDate);
    }

    public boolean tryAddPriority(String key) {
        
        boolean isValid = false;
        String value = null;
        key = key.toLowerCase();
        
        if(PRIORITY_VALUES.containsKey(key)){
            value = PRIORITY_VALUES.get(key);
            isValid = true;
        }
        
        if (isValid && value!=null) {
            _arguments.put(PRIORITY, value);
        }
        return isValid;
    }

    private StringBuilder getProperCommands() {
        StringBuilder result = new StringBuilder();
        logger.info("Building proper commands\n" + 
                    "Action: " + getFormattedValue(ACTION) + "\n" + 
                    "Description: " + getFormattedValue(DESCRIPTION) + "\n" + 
                    "Priority: " + getFormattedValue(PRIORITY) + "\n" +
                    "End: " + getFormattedValue(END));
        result.append(getFormattedValue(ACTION));
        result.append(getFormattedValue(DESCRIPTION));
        if(hasValue(PRIORITY)) {
            result.append(getFormattedKey(PRIORITY));
            result.append(getFormattedValue(PRIORITY));
        }
        if(hasValue(END)){
            result.append(getFormattedKey(END));
            result.append(getFormattedValue(END));
        }
        return result;
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

    }

    private boolean hasValue(String key) {
        return _arguments.get(key) != null;
    }
}
