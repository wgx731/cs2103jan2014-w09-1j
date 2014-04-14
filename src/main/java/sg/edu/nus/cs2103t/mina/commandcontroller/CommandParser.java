package sg.edu.nus.cs2103t.mina.commandcontroller;

/**
 * This class is in charges of normalizing the command for CommandProcessor
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
        
        initAddCommands();
        initModifyCommands();
        initDeleteCommands();
        initSearchCommands();
        initDisplayCommands();
        initCompleteCommands();
        initSingleActionCommand();
    }

    private void initSingleActionCommand() {
        SINGLE_ACTION_KEYWORD.put(EXIT.getType(), EXIT.getType());
        SINGLE_ACTION_KEYWORD.put("quit", EXIT.getType());
        SINGLE_ACTION_KEYWORD.put(UNDO.getType(), UNDO.getType());
        SINGLE_ACTION_KEYWORD.put(REDO.getType(), REDO.getType());
    }

    private void initCompleteCommands() {
        ACTIONS_KEYWORDS.put(COMPLETE.getType(), COMPLETE);
        ACTIONS_KEYWORDS.put("finish", COMPLETE);
    }

    private void initDisplayCommands() {
        ACTIONS_KEYWORDS.put(DISPLAY.getType(), DISPLAY);
        ACTIONS_KEYWORDS.put("filter", DISPLAY);
        ACTIONS_KEYWORDS.put("show", DISPLAY);
    }

    private void initSearchCommands() {
        ACTIONS_KEYWORDS.put("search", SEARCH);
        ACTIONS_KEYWORDS.put("find", SEARCH);
    }

    private void initDeleteCommands() {
        ACTIONS_KEYWORDS.put("remove", DELETE);
        ACTIONS_KEYWORDS.put("rm", DELETE);
        ACTIONS_KEYWORDS.put("-", DELETE);
        ACTIONS_KEYWORDS.put("delete", DELETE);
    }

    private void initModifyCommands() {
        ACTIONS_KEYWORDS.put("modify", MODIFY);
        ACTIONS_KEYWORDS.put("change", MODIFY);
        ACTIONS_KEYWORDS.put("edit", MODIFY);
    }

    private void initAddCommands() {
        ACTIONS_KEYWORDS.put("add", ADD);
        ACTIONS_KEYWORDS.put("make", ADD);
        ACTIONS_KEYWORDS.put("create", ADD);
        ACTIONS_KEYWORDS.put("new", ADD);
        ACTIONS_KEYWORDS.put("+", ADD);
    }
    
    /**
     * To translate a user input into a form where CommandProcessor could understand.
     * @param userInput
     * @return
     * @throws NullPointerException
     * @throws ParseException
     */
    public String convertCommand(String userInput) throws NullPointerException,
            ParseException {

        checkValidity(userInput);

        String[] rawTokens = getRawTokens(userInput);
        String action = rawTokens[ACTION_INDEX];
        String argument = rawTokens[ARG_INDEX];

        return executeCommand(action, argument);

    }

    private String executeCommand(String action, String argument) throws ParseException {
        
        // short circuit, if the action needs no argument
        if (SINGLE_ACTION_KEYWORD.containsKey(action)) {
            return SINGLE_ACTION_KEYWORD.get(action);
        }

        // Determine action here
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
                // FALLTHROUGH
            case DELETE :
                // FALLTHROUGH
            case COMPLETE :
                currCommand = new ModifyCommand(command, argument);
                break;
            case SEARCH :
                currCommand = new SearchCommand(command, argument);
                break;
            default :
                throw new Error("No such action yet.");

        }

        // execute commandformat and return
        String translatedCommand = currCommand.parseArgument();
        return translatedCommand;
    }

    private String[] getRawTokens(String userInput) {

        String[] rawTokens = userInput.trim().split(SPACE, 2);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Distinguishing action: " + Arrays.toString(rawTokens));

        if (rawTokens.length < 2) {
            String[] newRawTokens = { rawTokens[ACTION_INDEX], "" };
            rawTokens = newRawTokens;
        }

        rawTokens[ACTION_INDEX] = rawTokens[ACTION_INDEX].toLowerCase();
        rawTokens[ARG_INDEX] = rawTokens[ARG_INDEX].trim();

        return rawTokens;
    }

    private void checkValidity(String userInput) throws ParseException,
            NullPointerException {
        if (userInput == null) {
            throw new NullPointerException();
        }
        if (userInput.equals(EMPTY)) {
            throw new ParseException("Empty String!", 0);
        }
    }
}
