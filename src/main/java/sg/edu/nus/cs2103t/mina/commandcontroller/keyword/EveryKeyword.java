package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//@author A0099151B
public class EveryKeyword extends Keyword {
    
    private static final int EVERY_LOOKAHEAD_LIMIT = 1;

    private HashMap<String, String> _everyValues;
    
    private static final String EVERY_DAY = "day";
    private static final String EVERY_WEEK = "week";
    private static final String EVERY_MONTH = "month";
    private static final String EVERY_YEAR = "year";
    private static final String EVERY_HOUR = "hour";
    
    static {
        EveryKeyword everyKeyword = new EveryKeyword(SimpleKeyword.RECURRING);
        KeywordFactory.addAliasEntry("-every", everyKeyword);
    }
    
    public EveryKeyword(StandardKeyword type) {
        super(type);
    }
    
    public EveryKeyword() {
        this(SimpleKeyword.RECURRING);
    }
    
    @Override
    protected Keyword createKeyword() {
        return new EveryKeyword();
    }

    @Override
    protected void initValues() {
        
        _everyValues = new HashMap<String, String>();
        
        _everyValues.put(EVERY_DAY, EVERY_DAY);
        _everyValues.put(EVERY_WEEK, EVERY_WEEK);
        _everyValues.put(EVERY_MONTH, EVERY_MONTH);
        _everyValues.put(EVERY_YEAR, EVERY_YEAR);
        _everyValues.put("yr", EVERY_YEAR);
        _everyValues.put(EVERY_HOUR, EVERY_HOUR);
        _everyValues.put("hr", EVERY_HOUR);
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex, Argument argument) throws ParseException {
        
        String everyKeyword;
        everyKeyword = getLookAhead(tokens, currIndex, EVERY_LOOKAHEAD_LIMIT);
        everyKeyword = getEveryValue(everyKeyword);
        tokens = nullifyTokens(tokens, currIndex); 
        
        updateArgument(everyKeyword, argument);
        
        return tokens;
    }

    private void updateArgument(String everyKeyword, Argument argument) {
        if(!hasExistingKeywordValue(argument)){
            argument.setKeywordValue(_type, everyKeyword);
        }
    }

    private ArrayList<String> nullifyTokens(ArrayList<String> tokens, int currIndex) {
        
        int next = currIndex + 1;
        
        tokens.set(currIndex, null);
        if(next<tokens.size()) {
            tokens.set(currIndex+1, null);
        }
        
        return tokens;
    }

    private String getEveryValue(String everyKeyword) throws ParseException{
        String value = _everyValues.get(everyKeyword);
        if(value==null){
            throw new ParseException("No such values", 0);
        } else{
            return value;
        }
    }

    @Override
    public Map<String, String> getKeywordValues() {
        return _everyValues;
    }

}
