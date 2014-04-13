package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Description keyword and its associated methods.
 * 
 * Like its parent, Keyword, processKeyword only process tokens after the
 * keyword. Developers would have to manually extract out the value of the
 * keyword with getValue(). However, the CommandParser should have done it.
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

// @author A0099151B
public class DescriptionKeyword extends Keyword {

    private static final StandardKeyword DESCRIPTION = SimpleKeyword.DESCRIPTION;
    private static final String CLASS_NAME = DescriptionKeyword.class.getName();
    private static final String DELIMITER_ESCAPE =  "\u2010";
    // Static initalizer to add entry to KeywordFactory. This is only for
    // standard keyword, alias will be added
    // else where.
    static {
        DescriptionKeyword newDescript = new DescriptionKeyword();
        KeywordFactory.addAliasEntry(DESCRIPTION.getFormattedKeyword(),
                newDescript);
    }

    public DescriptionKeyword(StandardKeyword type) {
        super(type);
    }

    public DescriptionKeyword() {
        this(DESCRIPTION);
    }

    @Override
    protected void initValues() {
        return;
    }

    /**
     * Process keyword by using the current tokens and current index For
     * description, tokens are processed one-by-one.
     * 
     * @param tokens The arraylist in question
     * @param currindex the index where the the keyword should start
     * @return the tokens of which all of its
     */
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex, Argument argument) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO, "Adding description");

        String description = extractWord(tokens, currIndex, argument);
        tokens = updateTokens(tokens, currIndex);

        argument.setKeywordValue(_type, description);

        return tokens;
    }

    private ArrayList<String> updateTokens(ArrayList<String> tokens,
            int currIndex) {
        tokens.set(currIndex, null);
        return tokens;
    }

    private String extractWord(ArrayList<String> tokens, int currIndex,
            Argument argument) {

        String oldDescript = argument.getKeywordValue(_type);
        StringBuilder descriptBuilder;

        if (oldDescript == null) {
            descriptBuilder = new StringBuilder();
        } else {
            descriptBuilder = new StringBuilder(oldDescript);
        }

        String word = tokens.get(currIndex);
        
        if(word.startsWith(StandardKeyword.DELIMITER)) {
            word = word.replace(StandardKeyword.DELIMITER, DELIMITER_ESCAPE);
        }
        
        LogHelper.log(CLASS_NAME, Level.INFO, "Appending " + word +
                " to " +
                oldDescript);

        descriptBuilder.append(word);

        return descriptBuilder.toString();
    }

    @Override
    protected Keyword createKeyword() {
        return new DescriptionKeyword();
    }

    @Override
    public Map<String, String> getKeywordValues() {
        return null;
    }

}
