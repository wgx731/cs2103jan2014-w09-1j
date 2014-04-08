package sg.edu.nus.cs2103t.mina.commandcontroller;

/**
 * This class is in charges of normalizing the command for CommandProcessor
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */



import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat.AddCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat.CommandFormat;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat.DisplayCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat.ModifyCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat.SearchCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;

//@author A0099151B
public class CommandParser {

    private static final CommandType REDO = CommandType.REDO;
    private static final CommandType SEARCH = CommandType.SEARCH;
    private static final CommandType UNDO = CommandType.UNDO;
    private static final CommandType EXIT = CommandType.EXIT;
    private static final CommandType DISPLAY = CommandType.DISPLAY;
    private static final CommandType DELETE = CommandType.DELETE;
    private static final CommandType ADD = CommandType.ADD;
    private static final CommandType MODIFY = CommandType.MODIFY;
    private static final CommandType COMPLETE = CommandType.COMPLETE;
    
    private static final String EVERY_DAY = "day";
    private static final String EVERY_WEEK = "week";
    private static final String EVERY_MONTH = "month";
    private static final String EVERY_YEAR = "year";
    private static final String EVERY_HOUR = "hour";

    private static final int ACTION_INDEX = 0;
    private static final int ARG_INDEX = 1;
    private static final String SPACE = " ";

    private static final HashMap<String, CommandType> ACTIONS_KEYWORDS = new HashMap<String, CommandType>();
    private static final HashMap<String, String> SINGLE_ACTION_KEYWORD = new HashMap<String, String>();
    private static final HashMap<String, Boolean> RECURRING_KEYWORDS = new HashMap<String, Boolean>();
    private static final HashMap<String, String> RECURRING_VALUES = new HashMap<String, String>();
    private static final String EMPTY = "";

    private static Logger logger = LogManager.getLogger(CommandParser.class
            .getName());

    public CommandParser() {

        KeywordFactory keywordFactory = KeywordFactory.getInstance();
        
        ACTIONS_KEYWORDS.put("add", ADD);
        ACTIONS_KEYWORDS.put("make", ADD);
        ACTIONS_KEYWORDS.put("create", ADD);
        ACTIONS_KEYWORDS.put("new", ADD);
        ACTIONS_KEYWORDS.put("+", ADD);

        ACTIONS_KEYWORDS.put("modify", MODIFY);
        ACTIONS_KEYWORDS.put("change", MODIFY);
        ACTIONS_KEYWORDS.put("edit", MODIFY);

        ACTIONS_KEYWORDS.put("remove", DELETE);
        ACTIONS_KEYWORDS.put("rm", DELETE);
        ACTIONS_KEYWORDS.put("-", DELETE);
        ACTIONS_KEYWORDS.put("delete", DELETE);

        ACTIONS_KEYWORDS.put("search", SEARCH);
        ACTIONS_KEYWORDS.put("find", SEARCH);

        ACTIONS_KEYWORDS.put(DISPLAY.getType(), DISPLAY);
        ACTIONS_KEYWORDS.put("filter", DISPLAY);
        ACTIONS_KEYWORDS.put("show", DISPLAY);

        ACTIONS_KEYWORDS.put(COMPLETE.getType(), COMPLETE);
        ACTIONS_KEYWORDS.put("finish", COMPLETE);

        SINGLE_ACTION_KEYWORD.put(EXIT.getType(), EXIT.getType());
        SINGLE_ACTION_KEYWORD.put("quit", EXIT.getType());
        SINGLE_ACTION_KEYWORD.put(UNDO.getType(), UNDO.getType());
        SINGLE_ACTION_KEYWORD.put(REDO.getType(), REDO.getType());

        RECURRING_KEYWORDS.put("daily", false);
        RECURRING_KEYWORDS.put("weekly", false);
        RECURRING_KEYWORDS.put("monthly", false);
        RECURRING_KEYWORDS.put("yearly", false);
        RECURRING_KEYWORDS.put("every", false);

        RECURRING_KEYWORDS.put("-every", true);
        RECURRING_KEYWORDS.put("-daily", true);
        RECURRING_KEYWORDS.put("-weekly", true);
        RECURRING_KEYWORDS.put("-monthly", true);
        RECURRING_KEYWORDS.put("-yearly", true);

        RECURRING_VALUES.put(EVERY_DAY, EVERY_DAY);
        RECURRING_VALUES.put(EVERY_WEEK, EVERY_WEEK);
        RECURRING_VALUES.put(EVERY_MONTH, EVERY_MONTH);
        RECURRING_VALUES.put(EVERY_YEAR, EVERY_YEAR);
        RECURRING_VALUES.put(EVERY_HOUR, EVERY_HOUR);

        RECURRING_VALUES.put("daily", EVERY_DAY);
        RECURRING_VALUES.put("weekly", EVERY_WEEK);
        RECURRING_VALUES.put("monthly", EVERY_MONTH);
        RECURRING_VALUES.put("hourly", EVERY_HOUR);

        RECURRING_VALUES.put("yearly", EVERY_YEAR);
        RECURRING_VALUES.put("-daily", EVERY_DAY);
        RECURRING_VALUES.put("-weekly", EVERY_WEEK);
        RECURRING_VALUES.put("-monthly", EVERY_MONTH);
        RECURRING_VALUES.put("-yearly", EVERY_YEAR);
        RECURRING_VALUES.put("-hourly", EVERY_HOUR);
    }

    public String convertCommand(String userInput) throws NullPointerException,
            ParseException {
        if (userInput == null) {
            throw new NullPointerException();
        }
        if (userInput.equals(EMPTY)) {
            throw new ParseException("Empty String!", 0);
        }

        String[] rawTokens = userInput.trim().split(SPACE, 2);
        logger.info("Distinguishing action: " + Arrays.toString(rawTokens));
        
        if(rawTokens.length<2) {
            String[] newRawTokens = {rawTokens[ACTION_INDEX], ""};
            rawTokens = newRawTokens;
        }
        
        // get action
        String action = rawTokens[ACTION_INDEX].toLowerCase();
        String argument = rawTokens[ARG_INDEX].trim();
        // short circuit, if the action needs no argument
        if (SINGLE_ACTION_KEYWORD.containsKey(action)) {
            return SINGLE_ACTION_KEYWORD.get(action);
        }
        
        // Determine action here
        // Need another factory object?
        CommandType command = ACTIONS_KEYWORDS.get(action);
        CommandFormat currCommand;
        
        switch(command){
            
            case DISPLAY :
                currCommand = new DisplayCommand(command, argument);
                break;
            case ADD :
                currCommand = new AddCommand(command, argument);
                break;
                
            case MODIFY :
            case DELETE :
            case COMPLETE :
                currCommand = new ModifyCommand(command, argument);
                break;
            case SEARCH :
                currCommand = new SearchCommand(command, argument);
                break;                
            default:
                throw new Error("No such action, yet.");
            
        }
        
        // execute commandformat and return
        String translatedCommand = currCommand.parseArgument();
        return translatedCommand;

    }
}

