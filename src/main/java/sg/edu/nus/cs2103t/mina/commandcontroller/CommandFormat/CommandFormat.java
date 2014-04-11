package sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Argument;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Keyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;

/**
 * Command keyword abstract class
 * 
 * The commands/command type are considered keywords as well, except for each command keyword 
 * there can be different ways processing the rest of the string. Thus, a CommandKeyword has to be created to
 * reign in the different formats/keywords.
 * 
 * Certain CommandType only belong to certain keywords/format thus it's neccessary for developers
 * to intialise a set of accepted CommandTypes when they decided to extend this class.
 * 
 * Terminology: 
 * CommandKeyword: The CommandType used for the keyword
 * Arguments: The rest of the string minus the CommandKeyword
 * Keyword and Values: The corresponding keyword and values that made up the arguments. From
 * here on, only simple and composite keywords are used.
 *  
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

//@author A0099151B
public abstract class CommandFormat{
    
    private static final int DESCRIPT_NOT_FOUND = -1;
    protected HashMap<String, CommandType> _commandValues; 
    protected HashSet<CommandType> _acceptedCommands;
    protected CommandType _commandType;
    protected String _argumentStr;
    protected Argument _argument;

    //attributes to help with processing command arguments.
    protected boolean _isWrapped;
    private static final String DESCRIPT_END_DELIMIT = "' ";
    private static final String DESCRIPT_START_DELIMIT = " '";
    protected static final String KEYWORD_VALUE_PAIR = "%1$s %2$s";
    private static Logger logger = LogManager.getLogger(CommandFormat.class
            .getName());;
    
    
    /**
     * Attempt to create a CommandKeyword from here. If commandType supplied is not part of the
     * accepted command, an IllegalArgumentException will be thrown.
     * 
     * @param commandType CommandType enum constant
     * @throws IllegalArgumentException if the commandType is not part of the accepted CommandType
     */
    public CommandFormat(CommandType commandType, String argumentStr) throws IllegalArgumentException{
        
        setCommandType(commandType);
        initArgument(argumentStr);
    }
    
    private void initArgument(String argumentStr) {
        _argument = new Argument();
        _argumentStr = " " + argumentStr + " ";
    }
    
    protected void setCommandType(CommandType type) {
        _commandType = type;
    }
    
    
    //Shared methods that all other CommandKeyword will use
    
    protected String trimmedCommand(StringBuilder properCommand) {
        return properCommand.toString().trim();
    }

    private StringBuilder appendWord(StringBuilder properCommand, String word){
        properCommand.append(word);
        properCommand.append(" ");
        return properCommand;
    }
    
    protected StringBuilder appendValue(StringBuilder properCommand,
                                        StandardKeyword keyword) {
        String value = _argument.getKeywordValue(keyword).trim();
        return appendWord(properCommand, value);
    }

    protected StringBuilder appendKeyword(StringBuilder properCommand,
                                        StandardKeyword keyword) {
        String key = keyword.getFormattedKeyword();
        String value = _argument.getKeywordValue(keyword).trim();
        String keyValue = String.format(KEYWORD_VALUE_PAIR, key, value);
        
        return appendWord(properCommand, keyValue);
    }
    
    protected void addAcceptedCommand(CommandType type) {
        _acceptedCommands.add(type);
    }
    
    /**
     * Extract and save the description if it's wrapped within a description delimiter.
     * If there is none to be found, the argument will not be altered and returned exactly
     * 
     * @param commandArguments
     * @return The argument without the description
     */
    protected String extractWrappedDescription(String argument) throws ParseException{
        
        StringBuilder argumentStr = new StringBuilder(argument);
        
        logger.info("Checking for segment");
        if(hasSegment(argumentStr)) {
            _isWrapped = true;
            String descript = getDescriptionSegment(argumentStr);
            addSegmentToDescription(descript);
            argumentStr = removeDescriptionSegment(argumentStr);
        }
        
        return argumentStr.toString();
    }
    
    private StringBuilder removeDescriptionSegment(StringBuilder argumentStr) {
        int first = getFirstDelimiter(argumentStr);
        int last = getLastDeimiter(argumentStr);
        argumentStr.delete(first+1, last + 2);
        return argumentStr;
    }

    private void addSegmentToDescription(String descript) throws ParseException{
        ArrayList<String> descriptTokens = tokenize(descript);
        for(int i=0; i<descriptTokens.size(); i++) {
            descriptTokens = addToDescription(descriptTokens, i);
        }
    }

    private String getDescriptionSegment(StringBuilder argumentStr) {
        
        int start = getFirstDelimiter(argumentStr) + DESCRIPT_START_DELIMIT.length();
        int end = getLastDeimiter(argumentStr);
        
        return argumentStr.substring(start, end);
    }

    private boolean hasSegment(StringBuilder argumentStr) {
        int first = getFirstDelimiter(argumentStr);
        int last = getLastDeimiter(argumentStr);
        
        return first != DESCRIPT_NOT_FOUND && last > first;
    }
    
    private int getFirstDelimiter(StringBuilder argumentStr) {
        return argumentStr.indexOf(DESCRIPT_START_DELIMIT);
    }
    private int getLastDeimiter(StringBuilder argumentStr) {
        return argumentStr.lastIndexOf(DESCRIPT_END_DELIMIT);
    }
    
    protected void initValues() {
        
        _commandValues = new HashMap<String, CommandType>();
        
        _commandValues.put(CommandType.ADD.getType(), CommandType.ADD);
        _commandValues.put("make", CommandType.ADD);
        _commandValues.put("create", CommandType.ADD);
        _commandValues.put("new", CommandType.ADD);
        _commandValues.put("+", CommandType.ADD);
        
        _commandValues.put(CommandType.MODIFY.getType(), CommandType.MODIFY);
        _commandValues.put("change", CommandType.MODIFY);
        _commandValues.put("edit", CommandType.MODIFY);
        
        _commandValues.put("remove", CommandType.DELETE);
        _commandValues.put("rm", CommandType.DELETE);
        _commandValues.put("-", CommandType.DELETE);
        _commandValues.put(CommandType.DELETE.getType(), CommandType.DELETE);
        
        _commandValues.put(CommandType.SEARCH.getType(), CommandType.SEARCH);
        _commandValues.put("find", CommandType.SEARCH);
        
        _commandValues.put(CommandType.DISPLAY.getType(), CommandType.DISPLAY);
        _commandValues.put("filter", CommandType.DISPLAY);
        _commandValues.put("show", CommandType.DISPLAY);
        
        _commandValues.put(CommandType.COMPLETE.getType(), CommandType.COMPLETE);
        _commandValues.put("finish", CommandType.COMPLETE);
        
        _commandValues.put(CommandType.EXIT.getType(), CommandType.EXIT);
        _commandValues.put("quit", CommandType.EXIT);
        
        _commandValues.put(CommandType.UNDO.getType(), CommandType.UNDO);
        _commandValues.put(CommandType.REDO.getType(), CommandType.REDO);
        
    }
    
    /**
     * Template method. It's split into three phases, preProcessing(), processArgument() and postProcessing().
     * The first and the last method is implemented by the developer if need be. 
     * 
     * @return the standardised command that can be interpreted by the CommandProcessor
     * @throws ParseException if the CommandFormat is unable to parse and translate the argument
     */
    public String parseArgument() throws ParseException{
        
        preProcessArgument();
        ArrayList<String> tokens = processKeywordValuePairs();
        postProcessing(tokens);
        
        return getProperCommand();
    }
    

    private ArrayList<String> processKeywordValuePairs() throws ParseException{
        
        ArrayList<String> tokens = tokenize(_argumentStr);
        logger.info("Getting tokens: " + tokens.toString());
        
        for(int i=0; i<tokens.size(); i++) {            
            
            String currWord = tokens.get(i);
            if(currWord==null) {
                continue;
            }
            
            if(isValidKeyword(currWord)) {
                logger.info("Creating keyword: " + currWord);
                tokens = processKeyword(tokens, i, currWord);
            } else if(!_isWrapped){
                tokens = addToDescription(tokens, i);
            }
        }
        
        return tokens;
        
    }
    
    private ArrayList<String> processKeyword(ArrayList<String> tokens, int i, String currWord) throws ParseException{
        currWord = createFormatedWord(currWord);
        logger.info("Getting keyword: " + currWord);
        Keyword newKeyword = KeywordFactory.createKeyword(currWord);
        tokens = newKeyword.processKeyword(tokens, i, _argument);
        return tokens;
    }

    private boolean isValidKeyword(String currWord) {
        currWord = createFormatedWord(currWord);
        return KeywordFactory.isKeyword(currWord);
    }

    private String createFormatedWord(String currWord) {
        currWord = currWord.toLowerCase();
        if (_isWrapped && !currWord.startsWith(StandardKeyword.DELIMITER)) {
            currWord = StandardKeyword.DELIMITER + currWord;
        }
        return currWord;
    }

    private ArrayList<String> addToDescription(ArrayList<String> tokens, int i) throws ParseException{
        String descriptKeyword = SimpleKeyword.DESCRIPTION.getFormattedKeyword();
        return processKeyword(tokens, i, descriptKeyword);
    }
    
    protected ArrayList<String> tokenize(String segment) {
        logger.info("Segregating segment: " + segment);
        String[] tokens = segment.trim().split(" ");
        ArrayList<String> dTokens = new ArrayList<String>();
        
        for(int i=0; i<tokens.length; i++) {
            String token = tokens[i].trim();
            if (!token.equals("")) {
                dTokens.add(token);
            }
        }
        
        return dTokens;    
    }
    
    protected String getProperCommand() {
        String properCommand = getCommand();
        properCommand = getProperArgument(properCommand); // Sub class needs to implement this
        return formatCommand(properCommand);
    }
    //Utility functions. These functions are shared among all CommandFormat
    
    private String formatCommand(String properCommand) {
        return properCommand.toString().trim();
    }

    private String getCommand() {
        StringBuilder commandBuilder = new StringBuilder();
        String command = _commandType.getType();
        commandBuilder.append(command);
        commandBuilder.append(" ");
        return commandBuilder.toString();
    }

    //functions that need to be created
    protected abstract void preProcessArgument() throws ParseException;
    protected abstract void postProcessing(ArrayList<String> tokens);
    protected abstract String getProperArgument(String properCommand);
}
