package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.DescriptionKeyword;

public class DescriptionKeywordTest {
    
    private static DescriptionKeyword descript;
    private static String control;
    private ArrayList<String> tokens;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        descript = new DescriptionKeyword();
        control = "hello bye world what";
    }
    
    @Before
    public void setUp() {
        tokens = new ArrayList<String>();
        String[] tokensArr = control.split(" ");
        tokens = new ArrayList<String>();
        for(int i=0; i<tokensArr.length; i++) {
            tokens.add(tokensArr[i]);
        }
    }
    
    @Test
    public void testAddingDescription() throws ParseException{
        tokens = descript.processKeyword(tokens, 0);
        assertNull(tokens.get(0));
    }
    
    @Test
    public void testDescription() throws ParseException {
        
        //check to see whether we get the description
        for (int i=0; i<tokens.size(); i++) {
            tokens = descript.processKeyword(tokens, i);
        }
        assertEquals(control, descript.getValue());
        
        //check to see whether we
    }
}
