package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;

//@author A0099151B
public class AgendaOfCompositeKeyword extends StartKeyword {
    
    static {
        AgendaOfCompositeKeyword agendaOf = new AgendaOfCompositeKeyword(CompositeKeyword.AGENDAOF);
        KeywordFactory.addAliasEntry("-agendaof", agendaOf);
    }
    
    private static final String START_TIME = "000000";
    private static final String END_TIME = "235959";
    private static final int LOOK_AHEAD_LIMIT = 3;
    public AgendaOfCompositeKeyword(StandardKeyword type) {
        super(type);
    }
    
    public AgendaOfCompositeKeyword() {
        super(CompositeKeyword.AGENDAOF);
    }
    
    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex, Argument argument) throws ParseException {
        
        String lookahead = getLookAhead(tokens, currIndex, LOOK_AHEAD_LIMIT);
        String rawDate = parseDateFirst(lookahead);
        checkDate(rawDate);
        
        int argLimit = getArgLimit(rawDate);
        String date = getMilitaryDate(rawDate);
        tokens = updateTokens(tokens, currIndex, argLimit, date);
        return tokens;
    }
    
    private void checkDate(String rawDate) throws ParseException {
        if(rawDate==null){
            throw new ParseException("Invalid date", 0);
        }
    }

    private ArrayList<String> updateTokens(ArrayList<String> tokens,
                                            int currIndex, int argLimit, String date) {
        tokens = nullifyTokens(tokens, currIndex, argLimit);
        tokens = replaceSimpleKeywords(tokens, currIndex,date);
        return tokens;
    }

    private ArrayList<String> replaceSimpleKeywords(ArrayList<String> tokens,
                                                    int currIndex, String date) {
        
        ArrayList<String> startEndSub = new ArrayList<String>();
        
        startEndSub.add("-start");
        startEndSub.add(date + START_TIME);
        startEndSub.add("-end");
        startEndSub.add(date + END_TIME);
        
        tokens.addAll(currIndex + 1, startEndSub);
        return tokens;
    }

    @Override
    public Keyword createKeyword() {
        return new AgendaOfCompositeKeyword();
    }
}


