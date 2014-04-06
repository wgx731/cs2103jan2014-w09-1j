package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Argument;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.DescriptionKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;

public class DescriptionKeywordTest {
    
    private DescriptionKeyword descript;
    private ArrayList<String> tokens;
    private Argument argument;
    
    private static String control;
    private static Logger logger;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger = LogManager.getLogger(DescriptionKeywordTest.class.getName());
        
        control = "hello bye world what ";
    }
    
    @Before
    public void setUp() {
        argument = new Argument();
        tokens = new ArrayList<String>();
        String[] tokensArr = control.split(" ");
        descript = new DescriptionKeyword();
        
        for(int i=0; i<tokensArr.length; i++) {
            tokens.add(tokensArr[i]);
        }
    }
    
    @Test
    public void testUpdatingTokens() throws ParseException{
        
        logger.info("Testing to see if tokens are updated with null value in place");
        
        tokens = descript.processKeyword(tokens, 0, argument);
        int size = tokens.size();
        
        assertNull(tokens.get(0));
        assertEquals(size, tokens.size());
    }
    
    @Test
    public void testDescription() throws ParseException {
        
        logger.info("Testing to see if descriptions are added correctly.");
        
        for (int i=0; i<tokens.size(); i++) {
            tokens = descript.processKeyword(tokens, i, argument);
            logger.info("tokens are: " + tokens.toString());
        }
        assertEquals(control, argument.getKeywordValue(SimpleKeyword.DESCRIPTION));
        
        //check to see whether we have all nulls
        logger.info("Testing to see if all of entires are nullified");
        for(int i=0; i<tokens.size(); i++) {
            assertNull(tokens.get(i));
        }
    }
}
