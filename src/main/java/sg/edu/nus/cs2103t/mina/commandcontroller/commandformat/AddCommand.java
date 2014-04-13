package sg.edu.nus.cs2103t.mina.commandcontroller.commandformat;

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Command with keyword values
 * 
 * A command format that is like this: [CommandType] [mix of keyword and values
 * in any order]
 * 
 * e.g add Camping trip -from today 7pm -to 2/4/2014 8pm with family This format
 * will probably be mostly used when keywords (with their corresponding values)
 * and values can be reordered at any position.
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

// @author A0099151B
public class AddCommand extends CommandFormat {

    private static final String CLASS_NAME = AddCommand.class.getName();

    public AddCommand(CommandType type, String argumentStr)
            throws IllegalArgumentException {
        super(type, argumentStr);
    }

    @Override
    protected String getProperArgument(String properCommand) {

        String log = _argument.getKeywordValueView();
        StringBuilder commandBuilder = new StringBuilder(properCommand);

        LogHelper.log(CLASS_NAME, Level.INFO, log);

        for (StandardKeyword keyword : _argument.getArgumentKeys()) {

            if (_argument.getKeywordValue(keyword) == null) {
                continue;
            }

            if (keyword != SimpleKeyword.DESCRIPTION) {
                commandBuilder = appendKeyword(commandBuilder, keyword);
            } else {
                commandBuilder = appendValue(commandBuilder, keyword);
            }

        }

        return trimmedCommand(commandBuilder);
    }

    @Override
    protected void preProcessArgument() throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Preprocessing string for description");
        _argumentStr = extractWrappedDescription(_argumentStr);
    }

}
