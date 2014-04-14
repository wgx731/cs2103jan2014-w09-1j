package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

//@author A0099151B
public class AllKeyword extends Keyword {
    
    static {
        KeywordFactory.addAliasEntry("-all", new AllKeyword());
    }
    
    public static final String EMPTY_DEFAULT = "";
    
    public AllKeyword (StandardKeyword type) {
        super(type);
    }
    public AllKeyword() {
        this(SimpleKeyword.ALL);
    }
    
    @Override
    protected Keyword createKeyword() {
        return new AllKeyword();
    }

    @Override
    protected void initValues() {
        return;
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex, Argument argument) throws ParseException {
        updateArgument(argument);
        tokens = nullifyToken(tokens, currIndex);
        return tokens;
    }

    private ArrayList<String> nullifyToken(ArrayList<String> tokens,
            int currIndex) {
        tokens.set(currIndex, null);
        return tokens;
    }
    
    private void updateArgument(Argument argument) {
        argument.setKeywordValue(SimpleKeyword.ALL, EMPTY_DEFAULT);
    }
    
    @Override
    public Map<String, String> getKeywordValues() {
        return null;
    }

}
