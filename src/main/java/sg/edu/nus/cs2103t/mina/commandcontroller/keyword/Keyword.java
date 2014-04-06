package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;

public abstract class Keyword {
    
    protected StandardKeyword _type;
    protected final static boolean IS_PROTOTYPE = true;
    protected final static boolean IS_NOT_PROTOTYPE = false;
    protected static final int LOOK_AHEAD_LIMIT = 5;
    
    public Keyword(StandardKeyword type, boolean isPrototype) {
        _type = type;
        if(!isPrototype) {
            initValues();
        }
    }

    //Functions that need to be implemented
    protected abstract Keyword createKeyword();
    protected abstract void initValues();
    public abstract ArrayList<String> processKeyword(ArrayList<String> tokens, int currIndex,  
                                                        Argument argument) throws ParseException;
}
 