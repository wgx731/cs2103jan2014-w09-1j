package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Priority keyword.
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public class PriorityKeyword extends Keyword {

    private static final int PREV = -1;
    private static final int NEXT = 1;
    private static final String INVALID_PRIORITY = "Invalid priority";
    private static final StandardKeyword PRIORITY = SimpleKeyword.PRIORITY;
    
    private HashMap<String, String> _priorityValues;
    
    static {
        PriorityKeyword newPriority = new PriorityKeyword(IS_PROTOTYPE);
        KeywordFactory.addAliasEntry(PRIORITY.getFormattedKeyword(), newPriority);
    }
    
    public PriorityKeyword(boolean isPrototype) {
        super(PRIORITY, isPrototype);
    }
    
    public PriorityKeyword() {
        this(IS_NOT_PROTOTYPE);
    }

    @Override
    protected void initValues() {
        _priorityValues = new HashMap<String, String>();
        _priorityValues.put("low", "L");
        _priorityValues.put("l", "L");
        _priorityValues.put("medium", "M");
        _priorityValues.put("m", "M");
        _priorityValues.put("med", "M");
        _priorityValues.put("h", "H");
        _priorityValues.put("high", "H");
    }
    
    @Override
    /**
     * Process priority keyword. In this case, priority keyword will only process one word after/before
     * its keyword.
     */
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex, Argument argument) throws ParseException {
        
        assert(tokens!=null);
        
        String keyword = tokens.get(currIndex);
        
        if (hasDelimiter(keyword)) {
            tokens = processPriorityValue(tokens, currIndex, NEXT, argument);
        } else if (isValidPriorityPrefix(tokens, currIndex)) {
            tokens = processPriorityValue(tokens, currIndex, PREV, argument);
        } else {
            tokens = processPriorityValue(tokens, currIndex, NEXT, argument);
        }
        
        return tokens;
    }

    private ArrayList<String> processPriorityValue(ArrayList<String> tokens,
                                                    int currIndex, int nextIndex, Argument arguments) throws ParseException{
        int valueIndex = currIndex + nextIndex;
        String value = getValidValue(tokens, valueIndex); 
        if(value!=null) {
            arguments.setKeywordValue(_type, value);
            tokens = updateTokens(tokens, currIndex, valueIndex);
        } else {
            throw new ParseException(INVALID_PRIORITY, 0);
        }
        return tokens;
    }
    
    /**
     * Update the priority value and 
     * @param tokens
     * @param keyIndex
     * @param valueIndex
     * @param key
     * @return
     */
    private ArrayList<String> updateTokens(ArrayList<String> tokens,
                                                    int keyIndex, int valueIndex) {
        tokens.set(keyIndex, null);
        tokens.set(valueIndex, null);
        return tokens;
    }

    /**
     * Attempt to get the priority value from the tokens. If the value is invalid, return null 
     * @param tokens
     * @param prevIndex
     * @return the valid priority value or null if it's invalid
     */
    private String getValidValue(ArrayList<String> tokens, int valueIndex) {
        
        String key;
        
        try{
            key = tokens.get(valueIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        
        if(_priorityValues.containsKey(key)) {
            return _priorityValues.get(key);
        } else {
            return null;
        }
    }

    private boolean isValidPriorityPrefix(ArrayList<String> tokens,
                                            int currIndex) {
        int prevIndex = currIndex + PREV;
        if( prevIndex < 0 ) {
            return false;
        }
        
        return getValidValue(tokens, prevIndex) != null;
    }
    
    private boolean hasDelimiter(String keyword) {
        return keyword.startsWith(StandardKeyword.DELIMITER);
    }

    @Override
    protected Keyword createKeyword() {
        return new PriorityKeyword();
    }

}
