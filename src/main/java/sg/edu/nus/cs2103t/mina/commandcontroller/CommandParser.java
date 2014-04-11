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

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.AddCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.CommandFormat;
import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.DisplayCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.ModifyCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.SearchCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

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

    private static final int ACTION_INDEX = 0;
    private static final int ARG_INDEX = 1;
    private static final String SPACE = " ";

    private static final HashMap<String, CommandType> ACTIONS_KEYWORDS = new HashMap<String, CommandType>();
    private static final HashMap<String, String> SINGLE_ACTION_KEYWORD = new HashMap<String, String>();
    private static final String EMPTY = "";
    private static final String CLASS_NAME = CommandParser.class.getName();

    public CommandParser() {

        KeywordFactory.initialiseKeywordFactory();

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
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Distinguishing action: " + Arrays.toString(rawTokens));

        if (rawTokens.length < 2) {
            String[] newRawTokens = { rawTokens[ACTION_INDEX], "" };
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

        switch (command) {

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
            default :
                throw new Error("No such action, yet.");

        }

        // execute commandformat and return
        String translatedCommand = currCommand.parseArgument();
        return translatedCommand;

    }
}
