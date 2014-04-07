package sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat;

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;


/**
 * Command with keyword values 
 * 
 * A command format that is like this:
 * [CommandType] [mix of keyword and values in any order]
 * 
 * e.g
 * add Camping trip -from today 7pm -to 2/4/2014 8pm with family
 * This format will probably be mostly used when keywords (with their corresponding values) 
 * and values can be reordered at any position.
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

public class CommandOnlyKeywordValues extends CommandFormat{
    
    private static Logger logger = LogManager.getLogger(CommandOnlyKeywordValues.class
            .getName());
    
    
    public CommandOnlyKeywordValues(CommandType type, String argumentStr) throws IllegalArgumentException{
        super(type, argumentStr);
    }

    @Override
    protected String getProperArgument(String properCommand) {
        
        String log = _argument.getKeywordValueView();
        StringBuilder commandBuilder = new StringBuilder(properCommand);
        
        logger.info(log);

        for (StandardKeyword keyword: _argument.getArgumentKeys()) {
            
            if(_argument.getKeywordValue(keyword)==null){
                continue;
            }
           
            if (keyword!=SimpleKeyword.DESCRIPTION) {
                commandBuilder = appendKeyword(commandBuilder, keyword);
            } else {
                commandBuilder = appendValue(commandBuilder, keyword);
            }

        }
        
        return trimmedCommand(commandBuilder);
    }
    
    private String trimmedCommand(StringBuilder properCommand) {
        return properCommand.toString().trim();
    }

    private StringBuilder appendWord(StringBuilder properCommand, String word){
        properCommand.append(word);
        properCommand.append(" ");
        return properCommand;
    }
    
    private StringBuilder appendValue(StringBuilder properCommand,
                                        StandardKeyword keyword) {
        String value = _argument.getKeywordValue(keyword).trim();
        return appendWord(properCommand, value);
    }

    private StringBuilder appendKeyword(StringBuilder properCommand,
                                        StandardKeyword keyword) {
        String key = keyword.getFormattedKeyword();
        String value = _argument.getKeywordValue(keyword).trim();
        String keyValue = String.format(KEYWORD_VALUE_PAIR, key, value);
        
        return appendWord(properCommand, keyValue);
    }

    @Override
    protected void initAcceptedCommands() {
        addAcceptedCommand(CommandType.ADD);
        addAcceptedCommand(CommandType.DISPLAY);
    }

    @Override
    protected void preProcessArgument() throws ParseException {
        logger.info("Preprocessing string for description");
        _argumentStr = extractWrappedDescription(_argumentStr);
    }

    @Override
    protected void postProcessing(ArrayList<String> tokens) {
        return;
    }
}
