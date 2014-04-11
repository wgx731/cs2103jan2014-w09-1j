package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B
public class PriorityCompositeKeyword extends Keyword {

    static {
        PriorityCompositeKeyword priorityComposite = new PriorityCompositeKeyword(
                CompositeKeyword.PRIORITY);
        KeywordFactory.addAliasEntry("-urgent", priorityComposite);
        KeywordFactory.addAliasEntry("-trivial", priorityComposite);
        KeywordFactory.addAliasEntry("-unimportant", priorityComposite);
    }

    private HashMap<String, ArrayList<String>> _priorityCompositeValues;
    private static final int LOOK_AHEAD_LIMIT = 1;
    private static final String CLASS_NAME = PriorityCompositeKeyword.class
            .getName();
    
    public PriorityCompositeKeyword(StandardKeyword type) {
        super(type);
    }

    public PriorityCompositeKeyword() {
        super(CompositeKeyword.PRIORITY);
    }

    @Override
    protected Keyword createKeyword() {
        return new PriorityCompositeKeyword();
    }

    @Override
    protected void initValues() {
        _priorityCompositeValues = new HashMap<String, ArrayList<String>>();
        _priorityCompositeValues.put("-urgent", getAliasValue("h"));
        _priorityCompositeValues.put("-trivial", getAliasValue("l"));
        _priorityCompositeValues.put("-unimportant", getAliasValue("l"));
    }

    private ArrayList<String> getAliasValue(String priorityValue) {
        ArrayList<String> substitute = new ArrayList<String>();
        substitute.add(SimpleKeyword.PRIORITY.getFormattedKeyword());
        substitute.add(priorityValue.toUpperCase());
        return substitute;
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex, Argument argument) throws ParseException {

        String composite = getComposite(tokens.get(currIndex));
        if (_priorityCompositeValues.containsKey(composite)) {
            return replaceSimpleKeyword(tokens, currIndex, composite);
        } else {
            throw new ParseException("No Such priority composite keyword",
                    currIndex);
        }
    }

    private String getComposite(String keyword) {
        if (!keyword.startsWith(StandardKeyword.DELIMITER)) {
            return StandardKeyword.DELIMITER + keyword.toLowerCase();
        } else {
            return keyword.toLowerCase();
        }
    }

    private ArrayList<String> replaceSimpleKeyword(ArrayList<String> tokens,
            int currIndex, String composite) {
        LogHelper.log(CLASS_NAME, Level.INFO, tokens + " " +
                _priorityCompositeValues.get(composite));
        tokens.set(currIndex, null);
        tokens.addAll(currIndex + 1, _priorityCompositeValues.get(composite));
        return tokens;
    }

    @Override
    public Map<String, String> getKeywordValues() {

        HashMap<String, String> priorityValues = new HashMap<String, String>();
        ArrayList<String> valueArr;

        for (String key : _priorityCompositeValues.keySet()) {
            valueArr = _priorityCompositeValues.get(key);
            priorityValues.put(key, toString(valueArr));
        }
        return priorityValues;
    }

    private String toString(ArrayList<String> value) {
        StringBuilder strBuild = new StringBuilder();
        for (String token : value) {
            strBuild.append(token);
            strBuild.append(" ");
        }
        return strBuild.toString().trim();
    }
}
