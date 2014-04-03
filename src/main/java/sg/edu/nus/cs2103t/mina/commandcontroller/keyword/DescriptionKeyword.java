package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Description keyword
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

public class DescriptionKeyword extends Keyword{
    
    HashSet<String> descriptAlias = initAlias();
    
    public DescriptionKeyword() {
        super(StandardKeyword.DESCRIPTION);
        setValue("");
    }

    @Override
    protected HashSet<String> initAlias() {
        
        HashSet<String> newAlias = new HashSet<String>();
        
        newAlias.add(getType().getFormattedKeyword());
        newAlias.add(getType().getKeyword());
        newAlias.add("descript");
        newAlias.add(StandardKeyword.getFormattedKeyword("descript"));
        
        return newAlias;
    }
    
    @Override
    public boolean contains(String alias) {
        return descriptAlias.contains(alias);
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
        
        String description = extractWord(tokens, currIndex);
        updateValue(tokens, currIndex, description);
        
        return tokens;
    }
    
    @Override
    public String getValue() {
        return _value.trim();
    }
    
    private void updateValue(ArrayList<String> tokens, int currIndex,
                             String description) {
        tokens.set(currIndex, null);
        setValue(description);
    }

    private String extractWord(ArrayList<String> tokens, int currIndex) {
        
        StringBuilder descriptBuilder = new StringBuilder(_value);
        String word = tokens.get(currIndex);
        
        descriptBuilder.append(word);
        descriptBuilder.append(" ");
       
        return descriptBuilder.toString();
    }
    

}
