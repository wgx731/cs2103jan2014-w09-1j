package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Description keyword and its associated methods.
 * 
 * Like its parent, Keyword, processKeyword only process tokens after the keyword. Developers would 
 * have to manually extract out the value of the keyword with getValue(). However, the CommandParser
 * should have done it.
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

public class DescriptionKeyword extends Keyword{
    
    private static final Logger logger = LogManager.getLogger(DescriptionKeyword.class.getName());
    
    public DescriptionKeyword() {
        super(StandardKeyword.DESCRIPTION);
    }

    @Override
    protected void initAlias() {
        
        addAlias(getType().getKeyword());
        addAlias("descript");

    }
    
    @Override
    protected void initValues() {
       return; 
    }
    
    @Override
    /**
     * Process keyword by using the current tokens and current index
     * For description, tokens are processed one-by-one.
     * 
     * 
     * @param tokens The arraylist in question
     * @param currindex the index where the the keyword should start
     * @return the tokens of which all of its
     */
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex) throws ParseException {
        
        logger.info("Adding description");
        String description = extractWord(tokens, currIndex);
        setValue(description);
        tokens = updateTokens(tokens, currIndex);
        
        return tokens;
    }
    
    @Override
    public String getValue() {
        logger.info("returning " + _value);
        return _value.trim();
    }
    
    private ArrayList<String> updateTokens(ArrayList<String> tokens, int currIndex) {
        tokens.set(currIndex, null);
        return tokens;
    }

    private String extractWord(ArrayList<String> tokens, int currIndex) {
        
        StringBuilder descriptBuilder = new StringBuilder(_value);
        String word = tokens.get(currIndex);
        
        logger.info("Appending " + word + " to " + _value);
        
        descriptBuilder.append(word);
        descriptBuilder.append(" ");
       
        return descriptBuilder.toString();
    }


    

}
