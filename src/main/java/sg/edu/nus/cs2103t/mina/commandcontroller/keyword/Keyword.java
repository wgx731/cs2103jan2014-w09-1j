package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@author A0099151B
public abstract class Keyword {
    
    protected StandardKeyword _type;
    protected final static boolean IS_PROTOTYPE = true;
    protected final static boolean IS_NOT_PROTOTYPE = false;
    protected static final int LOOK_AHEAD_LIMIT = 4;
    private static Logger logger = LogManager.getLogger(Keyword.class
            .getName());
    
    public Keyword(StandardKeyword type) {
        _type = type;
        initValues();
    }
    
    protected String getLookAhead(ArrayList<String> tokens, int currIndex, int lookaheadLimit) {
        
        StringBuilder lookahead = new StringBuilder();
        
        for(int i=0, index=currIndex+1; i<tokens.size() && i<lookaheadLimit && index<tokens.size(); i++, index++) {
            logger.info("Getting word");
            String word = tokens.get(index);
            lookahead.append(word);
            lookahead.append(" ");
        }
        
        return lookahead.toString().trim();
    }
    
    protected boolean hasExistingKeywordValue(Argument arguments) {
        return arguments.hasValue(_type);
    }
    
    //Functions that need to be implemented
    protected abstract Keyword createKeyword();
    protected abstract void initValues();
    public abstract ArrayList<String> processKeyword(ArrayList<String> tokens, int currIndex,  
                                                        Argument argument) throws ParseException;
    public abstract Map<String, String> getKeywordValues();
}
 