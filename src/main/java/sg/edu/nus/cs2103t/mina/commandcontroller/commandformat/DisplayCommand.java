package sg.edu.nus.cs2103t.mina.commandcontroller.commandformat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.TaskIdKeyword;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B
public class DisplayCommand extends AddCommand {

    private static final String CLASS_NAME = DisplayCommand.class.getName();

    private HashMap<String, String> _displayValueAlias;

    public DisplayCommand(CommandType type, String argumentStr)
            throws IllegalArgumentException {
        super(type, argumentStr);
        initDisplayValueAlias();
    }

    private void initDisplayValueAlias() {
        TaskIdKeyword taskIdKeyword = new TaskIdKeyword();
        _displayValueAlias = new HashMap<String, String>();

        _displayValueAlias.put("all", FilterType.COMPLETE_PLUS.getType());
        _displayValueAlias.put("completed", FilterType.COMPLETE.getType());
        _displayValueAlias.putAll(taskIdKeyword.getKeywordValues());
    }

    @Override
    protected void preProcessArgument() throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Preprocessing string to replace keywords");
        ArrayList<String> tokens;
        tokens = tokenize(_argumentStr);
        tokens = replaceValues(tokens);
        _argumentStr = rebuildString(tokens);
    }

    private String rebuildString(ArrayList<String> tokens) {
        StringBuilder rebuiltString = new StringBuilder();
        for (String token : tokens) {
            rebuiltString.append(token);
            rebuiltString.append(" ");
        }
        return rebuiltString.toString().trim();
    }

    private ArrayList<String> replaceValues(ArrayList<String> tokens) {

        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i);
            if (_displayValueAlias.containsKey(word)) {
                tokens.set(i, _displayValueAlias.get(word));
            }
        }

        return tokens;
    }
}
