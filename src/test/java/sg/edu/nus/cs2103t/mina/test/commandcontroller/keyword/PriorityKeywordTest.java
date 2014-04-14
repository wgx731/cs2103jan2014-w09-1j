package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Argument;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.PriorityKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B
public class PriorityKeywordTest {

    private static final String CLASS_NAME = PriorityKeywordTest.class
            .getName();
    private static String suffixControl;
    private static String suffixNoDelimitControl;
    private static String prefixControl;
    private static ArrayList<String> allNull;

    private ArrayList<String> prefixTokens;
    private ArrayList<String> suffixTokens;
    private ArrayList<String> suffixTokensNoDelimitTokens;

    private Argument argument;

    private PriorityKeyword priority;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        prefixControl = "low priority";
        suffixControl = "-priority high";
        suffixNoDelimitControl = "priority med";

        allNull = new ArrayList<String>();
        allNull.add(null);
        allNull.add(null);
    }

    @Before
    public void setUp() {
        priority = new PriorityKeyword();
        prefixTokens = new ArrayList<String>();
        suffixTokens = new ArrayList<String>();
        argument = new Argument();

        suffixTokensNoDelimitTokens = new ArrayList<String>();

        String[] prefixTokensArr = prefixControl.split(" ");
        String[] suffixTokensArr = suffixControl.split(" ");
        String[] suffixTokensNoDelimitTokensArr = suffixNoDelimitControl
                .split(" ");

        for (int i = 0; i < prefixTokensArr.length; i++) {
            prefixTokens.add(prefixTokensArr[i]);
        }

        for (int i = 0; i < suffixTokensArr.length; i++) {
            suffixTokens.add(suffixTokensArr[i]);
        }

        for (int i = 0; i < suffixTokensNoDelimitTokensArr.length; i++) {
            suffixTokensNoDelimitTokens.add(suffixTokensNoDelimitTokensArr[i]);
        }
    }

    @Test(expected = AssertionError.class)
    public void testNull() throws ParseException {
        priority.processKeyword(null, 0, argument);
    }

    @Test(expected = ParseException.class)
    public void testInvalidSuffixValue() throws ParseException {
        String invSuffixControl = "priority blah";
        ArrayList<String> invTokens = getInvalidTokens(invSuffixControl);
        invTokens = priority.processKeyword(invTokens, 0, argument);
    }

    @Test(expected = ParseException.class)
    public void testInvalidSuffixDelimitValue() throws ParseException {
        String invSuffixControl = "-priority blah";
        ArrayList<String> invTokens = getInvalidTokens(invSuffixControl);
        invTokens = priority.processKeyword(invTokens, 0, argument);
    }

    @Test(expected = ParseException.class)
    public void testPrefixValue() throws ParseException {
        String invSuffixControl = "blah priority";
        ArrayList<String> invTokens = getInvalidTokens(invSuffixControl);
        invTokens = priority.processKeyword(invTokens, 1, argument);
    }

    @Test(expected = ParseException.class)
    public void testPrefixWithDelimitValue() throws ParseException {
        String invSuffixControl = "H -priority";
        ArrayList<String> invTokens = getInvalidTokens(invSuffixControl);
        invTokens = priority.processKeyword(invTokens, 1, argument);
    }

    @Test(expected = ParseException.class)
    public void testNoValue() throws ParseException {
        String invSuffixControl = "priority";
        ArrayList<String> invTokens = getInvalidTokens(invSuffixControl);
        invTokens = priority.processKeyword(invTokens, 0, argument);
    }

    @Test
    public void testPrefix() throws ParseException {
        LogHelper.log(CLASS_NAME,Level.INFO,"Testing priority prefix");
        prefixTokens = priority.processKeyword(prefixTokens, 1, argument);

        assertEquals("L", argument.getKeywordValue(SimpleKeyword.PRIORITY)
                .trim());

        LogHelper.log(CLASS_NAME,Level.INFO,"Testing to ensure that all entries are null");
        assertEquals(allNull, prefixTokens);
    }

    @Test
    public void testSuffix() throws ParseException {
        LogHelper.log(CLASS_NAME,Level.INFO,"Testing priority suffix");
        suffixTokens = priority.processKeyword(suffixTokens, 0, argument);

        assertEquals("H", argument.getKeywordValue(SimpleKeyword.PRIORITY)
                .trim());

        LogHelper.log(CLASS_NAME,Level.INFO,"Testing to ensure that all entries are null");
        assertEquals(allNull, suffixTokens);
    }

    @Test
    public void testSuffixNoDelmit() throws ParseException {
        LogHelper.log(CLASS_NAME,Level.INFO,"Testing priority suffix but with no delimiter");
        suffixTokensNoDelimitTokens = priority.processKeyword(
                suffixTokensNoDelimitTokens, 0, argument);

        assertEquals("M", argument.getKeywordValue(SimpleKeyword.PRIORITY)
                .trim());

        LogHelper.log(CLASS_NAME, Level.INFO,"Testing to ensure that all entries are null");
        assertEquals(allNull, suffixTokensNoDelimitTokens);
    }

    private ArrayList<String> getInvalidTokens(String invString) {
        ArrayList<String> invTokens = new ArrayList<String>();
        Collections.addAll(invTokens, invString.split(" "));
        return invTokens;
    }
}
