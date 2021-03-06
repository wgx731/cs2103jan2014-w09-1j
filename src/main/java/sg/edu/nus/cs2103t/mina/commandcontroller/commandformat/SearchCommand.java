package sg.edu.nus.cs2103t.mina.commandcontroller.commandformat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B
public class SearchCommand extends CommandFormat {

    private static final String CLASS_NAME = SearchCommand.class.getName();
    private static final String SEARCH_DELIMIT = "//";

    public SearchCommand(CommandType commandType, String argumentStr)
            throws IllegalArgumentException {
        super(commandType, argumentStr);
    }

    @Override
    protected void preProcessArgument() throws ParseException {
        StringBuilder argBuilder;
        argBuilder = prepArgument(_argumentStr);
        argBuilder = extractSearchTokens(argBuilder);
        _argumentStr = processSearchToken(argBuilder);
    }

    private String processSearchToken(StringBuilder argBuilder) {
        //remove the last '//' 
        int lastDelimiter = argBuilder.lastIndexOf(SEARCH_DELIMIT);
        String result = argBuilder.toString().substring(0, lastDelimiter);
        LogHelper.log(CLASS_NAME, Level.INFO, result);
        return result;
    }

    private StringBuilder extractSearchTokens(StringBuilder argBuilder) {

        StringBuilder searchArg = new StringBuilder();        
        searchArg = extractPhrases(searchArg, argBuilder); 
        searchArg = extractWords(searchArg, argBuilder);
        
        return searchArg;
    }

    private StringBuilder extractWords(StringBuilder searchArg,
                                        StringBuilder argBuilder) {
        
        String words[] = tokenizeString(argBuilder);
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            if (!word.equals("")) {
                searchArg.append(word);
                searchArg.append(SEARCH_DELIMIT);
            }
        }
        return searchArg;
    }

    private StringBuilder extractPhrases(StringBuilder searchArg, StringBuilder argBuilder) {
                
        ArrayList<String> phrases = getPhrases(argBuilder);
        searchArg = processPhrases(phrases, searchArg);

        return searchArg;
    }

    private StringBuilder processPhrases(ArrayList<String> phrases, StringBuilder searchArg) {
        
        for (String phrase : phrases) {
            phrase = phrase.trim();
            int first = phrase.indexOf("'");
            int last = phrase.lastIndexOf("'");
            phrase = phrase.substring(first + 1, last);
            searchArg.append(phrase.trim());
            searchArg.append(SEARCH_DELIMIT);
        }
        
        return searchArg;
    }

    private ArrayList<String> getPhrases(StringBuilder argBuilder) {
        
        Pattern phrasePattern = Pattern.compile("\\s'.*?'\\s");
        Matcher phraseMatcher = phrasePattern.matcher(argBuilder.toString());
        ArrayList<String> phrases = new ArrayList<String>();
        
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Match count: " + phraseMatcher.groupCount());
        
        //Extract phrases and remove them from the original argument.
        while (phraseMatcher.find()) {
            String match = phraseMatcher.group();
            phrases.add(match);
            LogHelper.log(CLASS_NAME, Level.INFO, match);
            int matchIndex = argBuilder.indexOf(match);
            
            argBuilder.delete(matchIndex + 1, matchIndex + match.length());
            argBuilder.insert(0, " ");
            
            phraseMatcher.reset(argBuilder);
        }
        
        return phrases;
        
    }

    private String[] tokenizeString(StringBuilder rawString) {
        return rawString.toString().trim().split(" ");
    }

    private StringBuilder prepArgument(String argumentStr) {
        String argument = " " + argumentStr + " ";
        argument = argument.replace(StandardKeyword.DELIMITER, StandardKeyword.DELIMITER_ESCAPE);
        return new StringBuilder(argument);

    }

    @Override
    protected String getProperArgument(String properCommand) {
        String value = _argument.getKeywordValue(SimpleKeyword.DESCRIPTION);
        return properCommand + value;
    }

}
