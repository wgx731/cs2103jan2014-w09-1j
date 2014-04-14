package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.assertEquals;
import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Argument;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StartKeyword;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

//@author A0099151B

public class StartKeywordTest {

    private StartKeyword startKeyword;
    private ArrayList<String> tokens;
    private String todayDate;
    private Argument argument;

    private static String timeOnlyControl, dateOnlyControl, timeDateControl,
            dateTimeControl;
    private static String timeResultContol, dateResultControl;

    private static String dateWordOnlyControl, dateWordTimeControl,
            timeDateWordControl;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String keyword = "-start ";
        String date = "12/3/2004 ";
        String time = "4am ";

        timeOnlyControl = keyword + time;
        dateOnlyControl = keyword + date;
        timeDateControl = keyword + time + date;
        dateTimeControl = keyword + date + time;

        dateWordOnlyControl = keyword + "today";
        dateWordTimeControl = keyword + "today " + time;
        timeDateWordControl = keyword + time + "today";

        timeResultContol = "040000";
        dateResultControl = "12032004";
    }

    @Before
    public void setUp() throws Exception {
        DateTime today = DateTime.today(TimeZone.getDefault());
        todayDate = today.format(DateUtil.MILITARY_DATE_FORMAT.toUpperCase());
        startKeyword = new StartKeyword();
        tokens = new ArrayList<String>();
        argument = new Argument();
    }

    @Test(expected = ParseException.class)
    public void testInvTwoArgument() throws ParseException {
        ArrayList<String> inv = new ArrayList<String>();
        inv.add("-start");
        inv.add("bah");
        inv.add("mah");
        startKeyword.processKeyword(inv, 0, argument);
    }

    @Test(expected = ParseException.class)
    public void testInvOneArgument() throws ParseException {
        ArrayList<String> inv = new ArrayList<String>();
        inv.add("-start");
        inv.add("bah");
        startKeyword.processKeyword(inv, 0, argument);
    }

    @Test(expected = ParseException.class)
    public void testInvNoArgument() throws ParseException {
        ArrayList<String> inv = new ArrayList<String>();
        inv.add("-start");
        startKeyword.processKeyword(inv, 0, argument);
    }

    @Test
    public void testTimeOnly() throws ParseException {
        tokens = tokenize(timeOnlyControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = todayDate + timeResultContol;
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testDateOnly() throws ParseException {
        tokens = tokenize(dateOnlyControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = dateResultControl + "000000";
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testTimeDateOrder() throws ParseException {
        tokens = tokenize(timeDateControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = dateResultControl + timeResultContol;
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testDateTimeOrder() throws ParseException {
        tokens = tokenize(dateTimeControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = dateResultControl + timeResultContol;
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testDateWordOnly() throws ParseException {
        tokens = tokenize(dateWordOnlyControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = todayDate + "000000";
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testDateWordTime() throws ParseException {
        tokens = tokenize(dateWordTimeControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = todayDate + timeResultContol;
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    @Test
    public void testTimeDateWord() throws ParseException {
        tokens = tokenize(timeDateWordControl);
        tokens = startKeyword.processKeyword(tokens, 0, argument);
        String expected = todayDate + timeResultContol;
        assertEquals(expected, argument.getKeywordValue(SimpleKeyword.START)
                .trim());
    }

    private ArrayList<String> tokenize(String argument) {
        ArrayList<String> newTokens = new ArrayList<String>();
        String[] rawTokens = argument.split(" ");
        Collections.addAll(newTokens, rawTokens);
        return newTokens;
    }

}
