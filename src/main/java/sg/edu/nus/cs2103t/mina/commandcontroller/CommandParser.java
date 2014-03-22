package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandParser {

    private static final int ACTION_INDEX = 0;
    private static final String SPACE = " ";
    private static final String ACTION = "action";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";
    
    private static final HashMap<String, String> PRIORITY_VALUES = new HashMap<String, String>();
    private static final HashMap<String, String> ACTIONS_KEYWORDS = new HashMap<String, String>();
    
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

            if (tokens[i].equalsIgnoreCase(PRIORITY) && !hasValue(PRIORITY)) {
                int prevIndex = i - 1;
                if ( tryAddPriority(tokens[prevIndex]) ) {
                    continue;
                } else if (i + 1 < tokens.length && 
                        tryAddPriority(tokens[i + 1])) {
                    i++;
                    continue;
                }
            }

            if (tokens[i].equalsIgnoreCase("-priority")) {
                int nextIndex = i + 1;
                if (nextIndex < tokens.length && 
                        tryAddPriority(tokens[i + 1])) {
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
                    "Priority: " + getFormattedValue(PRIORITY));
        result.append(getFormattedValue(ACTION));
        result.append(getFormattedValue(DESCRIPTION));
        if(hasValue(PRIORITY)) {
            result.append("-priority ");
            result.append(getFormattedValue(PRIORITY));
        }
        return result;
    }

    public String getFormattedValue(String key) {
        return _arguments.get(key) + SPACE;
    }

    private void initArgMap() {

        _arguments = new HashMap<String, String>();

        _arguments.put(ACTION, null);
        _arguments.put(PRIORITY, null);
        _arguments.put(DESCRIPTION, null);

    }

    private boolean hasValue(String key) {
        return _arguments.get(key) != null;
    }
}
