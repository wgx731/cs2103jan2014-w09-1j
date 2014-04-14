package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.commandformat.AddCommand;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;

//@author A0099151B

public class AddCommandTest {
    
    
    private AddCommand commandFormat; 
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        KeywordFactory.initialiseKeywordFactory();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNormalNoKeyword() throws ParseException {
        String result = "add testing one two three";
        commandFormat = getCommandFormat(CommandType.ADD, "testing one two three");
        assertEquals(result, commandFormat.parseArgument());
    }
    
    @Test
    public void testWrapping() throws ParseException {
        String result = "add testing one two three with wrap";
        commandFormat = getCommandFormat(CommandType.ADD, "'testing one two three with wrap'");
        assertEquals(result, commandFormat.parseArgument());        
    }
    
    @Test
    public void testRecognisingKeyword() throws ParseException {
        String result = "add testing one two three -priority H";
        commandFormat = getCommandFormat(CommandType.ADD, "-priority H testing one two three");
        assertEquals(result, commandFormat.parseArgument());             
    }
    
    @Test
    public void testRecognisingKeywordWithWrapping() throws ParseException {
        String result = "add testing one two three -priority H";
        commandFormat = getCommandFormat(CommandType.ADD, "'testing one two three' high priority");
        assertEquals(result, commandFormat.parseArgument());           
        
        commandFormat = getCommandFormat(CommandType.ADD, "high priority 'testing one two three'");
        assertEquals(result, commandFormat.parseArgument());   
    }
    
    private AddCommand getCommandFormat(CommandType type, String argumentStr) {
        return new AddCommand(type, argumentStr);
    }
}
