package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTION = "action";
    private static final String PRIORITY = "priority";
    private static final String DESCRIPTION = "description";

    private CommandProcessor _cmdProcess;
    private HashMap<String, String> _arguments;
    private static Logger logger = LogManager.getLogger(CommandParser.class
            .getName());

    public CommandParser(CommandProcessor cmdProcess) {
        _cmdProcess = cmdProcess;
        initArgMap();
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

        int first = originalString.indexOf(" '");
        int last = originalString.lastIndexOf("' ");

        if (first != -1 && last > first) {
            String descript = originalString.substring(first + 2, last);
            _arguments.put(DESCRIPTION, descript.trim());
            originalString.delete(first+1, last + 2);
            logger.info("KAH" + originalString);
        }

        String[] tokens = originalString.toString().split(SPACE);
        _arguments.put(ACTION, tokens[0]);

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < tokens.length; i++) {

            if (tokens[i].equalsIgnoreCase(PRIORITY) && !hasValue(PRIORITY)) {
                int prevIndex = i - 1;
                if (tokens[prevIndex].equalsIgnoreCase("low")) {
                    addPriority(tokens[prevIndex]);
                    continue;
                } else if (i + 1 < tokens.length && 
                            tokens[i + 1].equalsIgnoreCase("low")) {
                    addPriority(tokens[i + 1]);
                    i++;
                    continue;
                }
            }

            if (tokens[i].equalsIgnoreCase("-priority")) {
                int nextIndex = i + 1;
                if (nextIndex < tokens.length && 
                        tokens[nextIndex].equalsIgnoreCase("L")) {
                    addPriority(tokens[nextIndex]);
                    i++;
                    continue;
                }
                throw new ParseException("Priority keyword is invalid",
                        originalString.indexOf("-priority"));
            }
            if (!hasValue(DESCRIPTION)) {
                description.append(tokens[i]);
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
    
    public void addPriority(String value) {
        if(value.equalsIgnoreCase("low")) {
            value = "L";
        }
        _arguments.put(PRIORITY, value);
    }

    private StringBuilder getProperCommands() {
        StringBuilder result = new StringBuilder();
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
