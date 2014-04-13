package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@author A0099151B
public class ToTypeKeyword extends TaskIdKeyword {
    
    static {
        ToTypeKeyword toType = new ToTypeKeyword(SimpleKeyword.TO_TASK_TYPE);
        KeywordFactory.addAliasEntry("-totype", toType);
        KeywordFactory.addAliasEntry("-changeto", toType);
        KeywordFactory.addAliasEntry("-totask", toType);
    }
    
    private static final int TO_TYPE_LOOKAHEAD = 1;
    
    public ToTypeKeyword(StandardKeyword type) {
        super(type);
    }
    
    public ToTypeKeyword() {
        this(SimpleKeyword.TO_TASK_TYPE);
        initValues();
    }
    
    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
                                            int currIndex, Argument argument) throws ParseException {
        
        String lookahead = getLookAhead(tokens, currIndex, TO_TYPE_LOOKAHEAD);
        String taskType = processLookahead(lookahead);
        updateArgument(taskType, argument);
        return nullifyTokens(lookahead, tokens, currIndex);
    }
    
    @Override
    protected Keyword createKeyword() {
        return new ToTypeKeyword();
    }
    
    
    private String processLookahead(String lookahead) throws ParseException{
        
        if (lookahead==null) {
            throw new ParseException("Invalid task type", 0);
        }
        
        Pattern taskTypePattern = Pattern.compile(_taskRegex, Pattern.CASE_INSENSITIVE);
        Matcher taskTypeMatcher = taskTypePattern.matcher(lookahead);
        String taskType;
        if (taskTypeMatcher.find()) {
            taskType = taskTypeMatcher.group();
            taskType = _taskTypeValues.get(taskType);
        } else {
            throw new ParseException("Invalid task type", 0);
        }
        
        return taskType;
    }
    
}
