package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Keyword {
    
    private StandardKeyword _type;
    protected String _value;
    protected static final int LOOK_AHEAD_LIMIT = 5;
    
    public Keyword(StandardKeyword type) {
        _type = type;
    }
    
    public StandardKeyword getType() {
        return _type;
    }
    
    protected void setValue(String value) {
        _value = value;
    }
    
    //Functions that need to be implemented
    protected abstract HashSet<String> initAlias();
    public abstract boolean contains(String alias);
    public abstract ArrayList<String> processKeyword(ArrayList<String> tokens, int currIndex) throws ParseException;
    public abstract String getValue();
}
 