package sg.edu.nus.cs2103t.mina.test.commandcontroller;

import static org.junit.Assert.*;
import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.model.FilterType;

//@author A0099151B
public class CommandParserTest {

    private static final int ORDER_EVENT_EDS = 4;
    private static final int ORDER_EVENT_ESD = 3;
    private static final int ORDER_EVENT_SED = 2;
    private static final int ORDER_EVENT_SDE = 1;
    private static final int ORDER_EVENT_DES = 0;
    private static final boolean HAS_DASH = true;
    private static final boolean IS_REORDER = true;
    private static final boolean IS_WRAPPED = true;

    private CommandParser parser = new CommandParser();

    private StringBuilder variationBuild;
    private String variation;
    private String result;
    private String testTime;
    private String start;
    private String end;

    public static final String TODO_ONE = "CPT_todo1";
    public static final String TODO_TWO = "CPT_todo2";
    public static final String TODO_THREE = "CPT_todo3";
    public static final String TODO_FOUR = "CPT_todo4";

    public static final String DEADLINE_DESCRIPTION = "Submit assignment ";
    public static final String EVENT_DESCRIPTION = "meet friends";
    public static final String RECUR_DESCRIPTION = "CS2103 tutorial";
    private static final int HAS_SECS = 0;
    private static final int HAS_NO_SECS = 1;
    private static final int HAS_TODAY_NO_TIME_SLASH = 2;
    private static final int HAS_TODAY_NO_TIME_DOT = 3;
    private static final int HAS_TODAY_NO_TIME_DASH = 4;
    private static final int HAS_TODAY_NO_TIME_KEYWORD = 5;
    private static final int HAS_TODAY_TIME = 6;
    private static final int HAS_TODAY_TIME_AM = 7;
    private static final int HAS_NO_DATE_BUT_TIME_PM = 8;
    private static final int HAS_NO_DATE_BUT_MILITARY_TIME = 9;
    private static final int HAS_DATE_24_AUG_2014_0900 = 10;
    private static final int HAS_DATE_25_AUG_2014_1200 = 11;
    private static final int HAS_DATE_24_AUG_2014_2133 = 12;
    
    private static final int DEAFULT_END = 0;
    private static final int DUE_END = 1;
    private static final int BY_END = 2;
    private static final int BEFORE_END = 3;
    private static final int DEFAULT_START = 4;
    private static final int FROM_START = 5;
    private static final int STARTING_START = 6;
   
    
    private static String addTodoControlLow, addTodoControlMed,
            addTodoControlHigh, addTodoControlNone,
            addDeadlineControlMonthDaySecs, addDeadlineControlMonthDaySame,
            addDeadlineControlNoDate, addDeadlineControlNoDateMorning,
            addDeadlineControlTodayNoTime, addDeadlineControlMonthTimeNoSecs,
            addEventControlADay, addEventControlDays, addEventControlMonths,
            addEventControlYears, addEventControlToday, addEventControlTomorrow,
            addRecurDayControl, addRecurWeekControl, addRecurMonthControl,
            addRecurYearControl;
    
    private static String displayControlType;
    
    private static DateTime thisFri, thisWed, thisSunday, 
                            nextFri, nextWed, nextSunday,
                            threeDays, fourWeeks, fiveMonths, sixYears;
    
    private static DateTime today;
    private static Logger logger;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        CommandParserTest cpt = new CommandParserTest();

        today = DateTime.today(TimeZone.getDefault());

        logger = LogManager.getLogger(CommandParserTest.class.getName());
        // Controls for adding todos
        addTodoControlLow = "add CPT_todo1 -priority L";
        addTodoControlMed = "add CPT_todo2 -priority M";
        addTodoControlHigh = "add CPT_todo3 -priority H";
        addTodoControlNone = "add CPT_todo4";

        String todayDateString = today.format("DDMMYYYY");

        // Today, time specified.
        addDeadlineControlNoDate = "add Submit assignment -end " + todayDateString +
                "200000";
        // Today, morning
        addDeadlineControlNoDateMorning = "add Submit assignment -end " + todayDateString +
                "090000";
        // Today no time specified
        addDeadlineControlTodayNoTime = "add Submit assignment -end " + todayDateString +
                "235959";
        // 26th of April 2013, time, no seconds.
        addDeadlineControlMonthTimeNoSecs = "add Submit assignment -end 26042013235900";
        // 26th of April 2013, 11:59:23pm
        addDeadlineControlMonthDaySecs = "add Submit assignment -end 26042013235923";
        // 4th of July 2013, 2359
        addDeadlineControlMonthDaySame = "add Submit assignment -end 04072013235900";

        // start: 24th of August 2014 0900 - 25th of August 2014 1200
        addEventControlADay = "add meet friends -start 24082014090000 -end 25082014120000";
        // start: 24th of August 2014 0900 - 31th of August 2014 1200
        addEventControlDays = "add meet friends -start 24082014090000 -end 31082014120000";
        // start: 24th of August 2014 0900 - 24th of September 2014 1200
        addEventControlMonths = "add meet friends -start 24082014090000 -end 24092014120000";
        // start: 24th of August 2014 0900 - 24th of September 2017 1200
        addEventControlYears = "add meet friends -start 24082014090000 -end 24092017120000";
        
        //start: today 9am - today 9.33pm
        addEventControlToday = "add meet friends -start " + todayDateString + "090000 -end " + todayDateString + "213300";
        
        // start: tomorrow 0900 - tomorrow 2133
        DateTime tomorrow = today.plusDays(1);
        String tmrFormat = tomorrow.format("DDMMYYYY");
        addEventControlTomorrow = "add meet friends -start " + tmrFormat + "090000 -end " + tmrFormat + "213300";
        
        // recurring task 24th August 2014 0900 - 24th August 2014 1100. Recur until 23th November 2014
        addRecurDayControl = "add CS2103 tutorial -start 24082014090000 -end 24082014110000 -every day -until 23112014235959";
        addRecurWeekControl = "add CS2103 tutorial -start 24082014090000 -end 24082014110000 -every week -until 23112014235959";
        addRecurMonthControl = "add CS2103 tutorial -start 24082014090000 -end 24082014110000 -every month -until 23112014235959";
        // recurring task 24th August 2014 0900 - 24th August 2014 1100. Recur until 23th November 2016
        addRecurYearControl = "add CS2103 tutorial -start 24082014090000 -end 24082014110000 -every year -until 23112016235959"; 
        
        //Basic type
        displayControlType = "display";
        
        for (FilterType filter: FilterType.values()){
            if(!(filter.equals(FilterType.START) || 
                    filter.equals(FilterType.END) || 
                    filter.equals(FilterType.PRIORITY))) {
                displayControlType+= " " + filter.getType();
            }
        }
        
        // Date phrases
        //wed //sun //fri
        int sun = 7;
        int wed = 3;
        int fri = 5;
        int weekday = (today.getWeekDay() + 6) % 7;
        thisSunday = today.plusDays(sun-weekday);
        thisWed = today.plusDays(wed-weekday);
        thisFri = today.plusDays(fri-weekday);
        nextSunday = thisSunday.plusDays(7);
        nextWed = thisWed.plusDays(7);
        nextFri = thisFri.plusDays(7);
        
        // Dynamic Date phrases
        //3 days
        threeDays = today.plus(0, 0, 3, 0, 0, 0, 0, DateTime.DayOverflow.Spillover);
        //4 weeks
        fourWeeks = today.plus(0, 0, 4*7, 0, 0, 0, 0, DateTime.DayOverflow.Spillover);
        //5 months
        fiveMonths = today.plus(0, 5, 0, 0, 0, 0, 0, DateTime.DayOverflow.Spillover);
        //6 years
        sixYears = today.plus(6, 0, 0, 0, 0, 0, 0, DateTime.DayOverflow.Spillover);
    }

    @Before
    public void setUp() {
        variationBuild = new StringBuilder();
        variation = result = testTime = start = end = "";

    }

    @Test(expected = NullPointerException.class)
    public void testNull() throws Exception {
        parser.convertCommand(null);
    }

    @Test(expected = ParseException.class)
    public void testEmptyString() throws Exception {
        parser.convertCommand("");
    }

    /**
     * Test to ensure that the control statements returned as it is.
     * 
     * @throws Exception
     */
    @Test
    public void testAddTodoControl() throws Exception {
        assertEquals(addTodoControlLow,
                parser.convertCommand(addTodoControlLow));
        assertEquals(addTodoControlMed,
                parser.convertCommand(addTodoControlMed));
        assertEquals(addTodoControlHigh,
                parser.convertCommand(addTodoControlHigh));
        assertEquals(addTodoControlNone,
                parser.convertCommand(addTodoControlNone));
    }

    /**
     * Testing for the different add command for todos.
     */
    @Test
    public void testReorderTodo() throws Exception {

        // Reordering of flags
        variationBuild.append("add ");
        variationBuild.append("-priority L ");
        variationBuild.append(TODO_ONE);
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlLow, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append("-priority M ");
        variationBuild.append(TODO_TWO);
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlMed, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append("-priority H ");
        variationBuild.append(TODO_THREE);
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlHigh, result);
    }

    @Test
    public void testDelimitTodo() throws Exception {
        // With delimiter of adding todos with low priority:
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_ONE));
        variationBuild.append(" priority l");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlLow, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_TWO));
        variationBuild.append(" priority m");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlMed, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_THREE));
        variationBuild.append(" priority h");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlHigh, result);

    }

    @Test
    public void testAddKeywords() throws Exception {

        variationBuild.append("make ");
        variationBuild.append(TODO_FOUR);
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlNone, result);

        setUp();
        variationBuild.append("create ");
        variationBuild.append(TODO_FOUR);
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlNone, result);

        setUp();
        variationBuild.append("new ");
        variationBuild.append(TODO_FOUR);
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlNone, result);

        setUp();
        variationBuild.append("+ ");
        variationBuild.append(TODO_FOUR);
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlNone, result);
    }

    @Test
    public void testPriorityKeywords() throws Exception {

        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_ONE));
        variationBuild.append(" low priority");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlLow, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_ONE));
        variationBuild.append(" priority low");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlLow, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_TWO));
        variationBuild.append(" med priority");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlMed, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_TWO));
        variationBuild.append(" medium priority");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlMed, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_THREE));
        variationBuild.append(" high priority");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlHigh, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(TODO_THREE));
        variationBuild.append(" urgent");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(addTodoControlHigh, result);
    }
    
    //XXX Exploratory testing 
    @Test
    public void exploratoryTest() throws Exception {

        variationBuild.append("add ");
        variationBuild.append(" urgent request from D");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals("add urgent request from D", result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription("urgent request from D"));
        variationBuild.append(" URGENT");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals("add urgent request from D -priority H", result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append("urgent request from D ");
        variationBuild.append(" -URGENT");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals("add urgent request from D -priority H", result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append("do priority low queue assignment ");
        variationBuild.append(" -priority high");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals("add do priority low queue assignment -priority H", result);

        setUp();
        variationBuild.append("add ");
        variationBuild
                .append("submit homework -due 12/3/2013 so that I could do something ");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals(
                "add submit homework so that I could do something -end 12032013235959",
                result);

        variation = "add today -end 0800 today tomorrow yesterday";
        result = parser.convertCommand(variation);
        String resultDate = today.format("DDMMYYYY");
        assertEquals("add today tomorrow yesterday -end " + resultDate +
                "080000", result);

        variation = "add before tomorrow 9.30am 'push harder'";
        result = parser.convertCommand(variation);
        DateTime tmr = today.plusDays(1);
        resultDate = tmr.format("DDMMYYYY");
        assertEquals("add push harder -end " + resultDate + "093000", result);
    
        variation = "add -description what";
        result = parser.convertCommand(variation);
        assertEquals("add -description what", result);

        //filter no special
        variation = "filter deadline complete";
        result = parser.convertCommand(variation);  
        assertEquals("display deadline complete", result);
        
        //filter start date
        variation = "filter -start 12/3/2007 deadline complete";
        result = parser.convertCommand(variation);  
        assertEquals("display deadline complete -start 12032007000000", result);
        
        /* XXX Boundary Value analysis, intersecting date format,
         * dd/MM/yyyy (another EP) with informal time (one EP) */
        //filter start date with time
        variation = "filter -start 12/3/2007 9am deadline complete";
        result = parser.convertCommand(variation);  
        assertEquals("display deadline complete -start 12032007090000", result);
        
        //filter end date with time
        variation = "filter deadline -by today 2000 complete";
        result = parser.convertCommand(variation);  
        resultDate = today.format("DDMMYYYY");
        end = resultDate + "200000";
        assertEquals("display deadline complete -end " + end, result);

        //filter start date and end date with time
        variation = "filter -from 12/5/2007 deadline -by today 2000 complete";
        result = parser.convertCommand(variation);  
        resultDate = today.format("DDMMYYYY");
        end = resultDate + "200000";
        assertEquals("display deadline complete -start 12052007000000 -end " + end, result);       
        
    }

    @Test
    public void testAddDeadlinesControl() throws Exception {
        assertEquals(addDeadlineControlNoDate,
                parser.convertCommand(addDeadlineControlNoDate));
        assertEquals(addDeadlineControlNoDateMorning,
                parser.convertCommand(addDeadlineControlNoDateMorning));
        assertEquals(addDeadlineControlTodayNoTime,
                parser.convertCommand(addDeadlineControlTodayNoTime));
        assertEquals(addDeadlineControlMonthDaySecs,
                parser.convertCommand(addDeadlineControlMonthDaySecs));
        assertEquals(addDeadlineControlMonthTimeNoSecs,
                parser.convertCommand(addDeadlineControlMonthTimeNoSecs));
        assertEquals(addDeadlineControlMonthDaySame,
                parser.convertCommand(addDeadlineControlMonthDaySame));

    }

    /**
     * Test reorder of keywords
     * 
     * @throws Exception
     */
    @Test
    public void testAddDeadlineReorder() throws Exception {

        variationBuild.append("add ");
        variationBuild.append(getTestTime(HAS_SECS));
        variationBuild.append(DEADLINE_DESCRIPTION);
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthDaySecs, result);

        setUp();
        variationBuild.append("add ");
        variationBuild.append(getTestTime(HAS_NO_SECS));
        variationBuild.append(DEADLINE_DESCRIPTION);
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

    }

    /**
     * Testing for different -end words. Check for reorder, quotes and no quotes
     * 
     * @throws Exception
     */
    @Test
    public void testAddDeadlinesEndWords() throws Exception {

        // -end as control
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, DEAFULT_END, false));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // due no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, DUE_END, false));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // -due
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, DUE_END, true));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // by no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, BY_END, false));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // -by
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, BY_END, true));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // before no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, BEFORE_END, !HAS_DASH));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

        // -before
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, BEFORE_END, true));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);

    }

    /**
     * Test wrap and no wrap
     * 
     * @throws Exception
     */
    @Test
    public void testDateFormat() throws Exception {

        logger.info("Today's date is: " + today.toString());

        // testing today no time
        // no wrap with DD/MM/YYYY format
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_TODAY_NO_TIME_SLASH, BEFORE_END,
                HAS_DASH));
        variation = variationBuild.toString();
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);

        // With wrap
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION,
                getTestTime(HAS_TODAY_NO_TIME_SLASH, BEFORE_END, HAS_DASH),
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);

        // DD.MM.YYYY
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION,
                getTestTime(HAS_TODAY_NO_TIME_DOT, BEFORE_END, HAS_DASH),
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);

        // 'Today' keyword
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION,
                getTestTime(HAS_TODAY_NO_TIME_KEYWORD, BEFORE_END, HAS_DASH),
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);

        // 'Today' with time, 2000
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION,
                getTestTime(HAS_TODAY_TIME, BEFORE_END, HAS_DASH), !IS_REORDER,
                IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);

        // Today with time, 9am
        testTime = getTestTime(HAS_TODAY_TIME_AM, BEFORE_END, HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDateMorning, result);

        // Today with no 'today' stated. Military
        testTime = getTestTime(HAS_NO_DATE_BUT_MILITARY_TIME, BEFORE_END,
                HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);

        // Today with no 'today' stated. am
        // Today with no 'today' stated. Military
        testTime = getTestTime(HAS_NO_DATE_BUT_TIME_PM, BEFORE_END, HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);
        
        //Testing implicit today
        start = "-start today 9am";
        end = "-end 9.33pm";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_DES, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addEventControlToday, result);        
        
        //Testing for partial format
        String partialFormat = today.format("DD/MM");
        //String fullFormat = today.format("DDMMYYYY");
        start = "-start " + partialFormat + " 9:00";
        end =  "-end " + partialFormat + " 21.33";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_DES, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addEventControlToday, result);   
        
        //date phrase this wednesday ORDER_EVENT_SDE
        String thisWedStr = thisWed.format("DDMMYYYY");
        String thisFriStr = thisFri.format("DDMMYYYY");
        start = "-from this wednesday 3am";
        end = "-end friday 4am";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SDE, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        String expected = "add " + EVENT_DESCRIPTION + " -start " + thisWedStr + "030000 " + "-end " + thisFriStr + "040000";
        logger.info(variation);
        assertEquals(expected, result); 
        
        //date phrase next wednesday
        String nextWedStr = nextWed.format("DDMMYYYY");
        String nextSunStr = nextSunday.format("DDMMYYYY");
        start = "-from next wednesday 11:00am";
        end = "-end next sunday";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SDE, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        expected = "add " + EVENT_DESCRIPTION + " -start " + nextWedStr + "110000 " + "-end " + nextSunStr + "235959";
        logger.info(variation);
        assertEquals(expected, result); 
        
        //date phrase 3 days
        String threeDaysStr = threeDays.format("DDMMYYYY");
        start = "-from next 3 days 11:00pm";
        end = "-end next sunday";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SDE, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        expected = "add " + EVENT_DESCRIPTION + " -start " + threeDaysStr + "230000 " + "-end " + nextSunStr + "235959";
        logger.info(variation);
        assertEquals(expected, result);        
        
        //date phrase 4 weeks
        String fourWeeksStr = fourWeeks.format("DDMMYYYY");
        String nextFriStr = nextFri.format("DDMMYYYY");
        start = "-from next fri 22:39";
        end = "-end next 4 week 0900";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SED, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        expected = "add " + EVENT_DESCRIPTION + " -start " + nextFriStr + "223900 " + "-end " + fourWeeksStr + "090000";
        logger.info(variation);
        assertEquals(expected, result);       
        
        //date phrase 5 months
        //date phrase 6 years
        String fiveMonthsStr = fiveMonths.format("DDMMYYYY");
        String sixYearsStr = sixYears.format("DDMMYYYY");
        start = "-from next 5 months 10.46am";
        end = "-end next 6 years 9am";
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_EDS, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        expected = "add " + EVENT_DESCRIPTION + " -start " + fiveMonthsStr + "104600 " + "-end " + sixYearsStr + "090000";
        logger.info(variation);
        assertEquals(expected, result);   
        
        DateTime nextMonth = today.plus(0, 1, 0, 0, 0, 0, 0, DateTime.DayOverflow.Spillover);
        end = "-end next month 9am";
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, end,
                                        !IS_REORDER, IS_WRAPPED);
        result = parser.convertCommand(variation);
        expected = "add " + DEADLINE_DESCRIPTION + "-end " + nextMonth.format("DDMMYYYY") + "090000";
        assertEquals(expected, result);   
    }

    @Test
    public void testEventControl() throws ParseException {
        
        assertEquals(addEventControlADay,
                parser.convertCommand(addEventControlADay));
        assertEquals(addEventControlDays,
                parser.convertCommand(addEventControlDays));
        assertEquals(addEventControlMonths,
                parser.convertCommand(addEventControlMonths));
        assertEquals(addEventControlYears,
                parser.convertCommand(addEventControlYears));
        
    }
    
    @Test
    public void testEventKeywords() throws ParseException {
        
        //Wrap and unwrapped
        
        //-from
        start = getTestTime(HAS_DATE_24_AUG_2014_0900, FROM_START, HAS_DASH);
        end = getTestTime(HAS_DATE_25_AUG_2014_1200, DEAFULT_END, HAS_DASH);
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_DES, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);  
        
        //from no dash
        start = getTestTime(HAS_DATE_24_AUG_2014_0900, FROM_START, !HAS_DASH);
        end = getTestTime(HAS_DATE_25_AUG_2014_1200, DEAFULT_END, !HAS_DASH);
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_DES, IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);     
        
    }
    
    @Test
    public void testEventKeywordsReorder() throws ParseException {
        
        //descript end start
        start = getTestTime(HAS_DATE_24_AUG_2014_0900, DEFAULT_START, HAS_DASH);
        end = getTestTime(HAS_DATE_25_AUG_2014_1200, DEAFULT_END, HAS_DASH);
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_DES, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);
        
        //start descript end
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SDE, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);
        
        //start end descript
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_SED, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);
        
        //end descript start
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_EDS, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);
        
        //end start descript
        variation = getEventAddCmd(EVENT_DESCRIPTION, start, end, ORDER_EVENT_ESD, !IS_WRAPPED); 
        result = parser.convertCommand(variation);
        assertEquals(addEventControlADay,
                    result);
        
    }
    
    @Test
    public void testModifyControl() throws ParseException{
        //checking individual flags
        variation = "modify todo 2 -description change me!";
        result = parser.convertCommand(variation);
        assertEquals("modify todo 2 -description -description change me!",
                result);        
    }
    
    @Test
    public void testModify() throws ParseException{
        variation = "modify todo 1 'whahaha' priority high";
        result = parser.convertCommand(variation);
        assertEquals("modify todo 1 -description whahaha -priority H", result);
        
        variation = "modify event 100 'whahaha' from 7am to 1pm";
        result = parser.convertCommand(variation);
        String resultDate = today.format("DDMMYYYY");
        start = resultDate + "070000";
        end = resultDate + "130000";
        assertEquals("modify event 100 -description whahaha -start " + start +
                    " -end " + end, result);
        
        //For changing task type
        variation = "modify deadline 5 changeto event 'whahaha' from tmr 3am";
        result = parser.convertCommand(variation);
        DateTime tmr = today.plusDays(1);
        resultDate = tmr.format("DDMMYYYY");
        start = resultDate + "030000";
        assertEquals("modify deadline 5 -totype event -description whahaha -start " + start, result);
    
        //For shorten id
        variation = "modify d5 changeto event 'whahaha' from tmr 3am";
        result = parser.convertCommand(variation);     
        resultDate = tmr.format("DDMMYYYY");
        start = resultDate + "030000";
        assertEquals("modify deadline 5 -totype event -description whahaha -start " + start, result);

        //For shorten id
        variation = "modify d5 -changeto event -from tmr 3am";
        result = parser.convertCommand(variation);     
        resultDate = tmr.format("DDMMYYYY");
        start = resultDate + "030000";
        assertEquals("modify deadline 5 -totype event -start " + start, result);
        
        //For shorten id
        variation = "modify d5 -changeto event get the -description tag from mina -from tmr 3am";
        result = parser.convertCommand(variation);     
        resultDate = tmr.format("DDMMYYYY");
        start = resultDate + "030000";
        assertEquals("modify deadline 5 -totype event -description get the -description tag from mina -start " + start, result);
        
        //delete with shorten id
        variation = "delete td1";
        result = parser.convertCommand(variation);  
        assertEquals("delete todo 1", result);
        
        //remove with shorten id
        variation = "remove td1";
        result = parser.convertCommand(variation);  
        assertEquals("delete todo 1", result);
        
        //complete with shorten id
        variation = "complete td1";
        result = parser.convertCommand(variation);  
        assertEquals("complete todo 1", result);
        
        //complete with id
        variation = "complete event 9999";
        result = parser.convertCommand(variation);  
        assertEquals("complete event 9999", result);   
        
    }
    
    @Test
    public void testSearch() throws ParseException {
        
        variation = "search doors wide open";
        result = parser.convertCommand(variation);
        assertEquals("search doors//wide//open",
                result);           
        
        variation = "find doors 'wide open' help me please";
        result = parser.convertCommand(variation);
        assertEquals("search wide open//doors//help//me//please",
                result);
        
        variation = "find 'wide open' help 'me please'";
        result = parser.convertCommand(variation);
        assertEquals("search wide open//me please//help",
                result);       
        
        variation = "search haha hehe";
        result = parser.convertCommand(variation);  
        assertEquals("search haha//hehe", result);
        
        variation = "search 'haha hohoh' hehe";
        result = parser.convertCommand(variation);  
        assertEquals("search haha hohoh//hehe", result); 
 
        variation = "search don't don't 'blah don't blah don't'";
        result = parser.convertCommand(variation);
        assertEquals("search blah don't blah don't//don't//don't", result);  
        
        variation = "search 'hohoho hohoho ' 'sasads dfdf' ' vvvvv '";
        result = parser.convertCommand(variation);  
        assertEquals("search hohoho hohoho//sasads dfdf//vvvvv", result);        
        
        
    }
    
    @Test
    public void testDisplayControl() throws ParseException {
        
        result = parser.convertCommand("display");  
        assertEquals("display", result);  
        
        result = parser.convertCommand(displayControlType);  
        assertEquals(displayControlType, result);          
    }
    
    @Test
    public void testDisplayBasicKeywords() throws ParseException{
        
        variation = "show deadlines todos events complete +complete";
        result = parser.convertCommand(variation);  
        assertEquals(displayControlType, result);
        
        variation = "filter d td e completed +complete";
        result = parser.convertCommand(variation);  
        assertEquals(displayControlType, result);
        
        variation = "filter d td e completed all";
        result = parser.convertCommand(variation);  
        assertEquals(displayControlType, result);
    }
    
    @Test
    public void testDisplayCompositeKeywords() throws ParseException{
        
        String todayDate = today.format("DDMMYYYY");
        
        variation = "display deadlines -agendaof today";
        result = parser.convertCommand(variation);  
        assertEquals("display deadline -start " + todayDate + "000000" + " -end " + todayDate + "235959",
                     result);
        
        variation = "display deadlines -agendaof tmr";
        result = parser.convertCommand(variation);
        DateTime tmr = today.plusDays(1);
        String tmrDate = tmr.format("DDMMYYYY");
        
        assertEquals("display deadline -start " + tmrDate + "000000" + " -end " + tmrDate + "235959",
                     result);     
        
        variation = "display deadlines -agendaof next 2 weeks";
        result = parser.convertCommand(variation);
        DateTime twoWeeks = today.plusDays(14);
        String twoWeeksDate = twoWeeks.format("DDMMYYYY");
        
        assertEquals("display deadline -start " + twoWeeksDate + "000000" + " -end " + twoWeeksDate + "235959",
                     result);   
        
        variation = "display deadlines -agendaof next week";
        result = parser.convertCommand(variation);
        DateTime oneWeeks = today.plusDays(7);
        String oneWeeksDate = oneWeeks.format("DDMMYYYY");
        
        assertEquals("display deadline -start " + oneWeeksDate + "000000" + " -end " + oneWeeksDate + "235959",
                     result);
        
    }
    
    @Test
    public void testRecurringKeywordsControl() throws ParseException {
        
        result = parser.convertCommand(addRecurDayControl);
        assertEquals(addRecurDayControl, result);
        
        result = parser.convertCommand(addRecurWeekControl);
        assertEquals(addRecurWeekControl, result);
        
        result = parser.convertCommand(addRecurMonthControl);
        assertEquals(addRecurMonthControl, result);
        
        result = parser.convertCommand(addRecurYearControl);
        assertEquals(addRecurYearControl, result);
        
    }
    
    @Test
    public void testRecurringReorder() throws ParseException {
        
        variation = "add every day until 23/11/2014 'CS2103 tutorial' from 24082014090000 to 24082014110000";
        result = parser.convertCommand(variation);
        assertEquals(addRecurDayControl, result);        
        
    }
    
    //XXX test for functions
    
    /*EP: updateTaskId()
     *     1) [action] size = 0 /invalid
     *     2) [action] [invalid] size = 1 /invalid
     *     3) [action] [task id] size = 1 /parse with a regex
     *     4) [action] [task] [id] size = 2 /parse with another regex
     *          4.1) [action] [invalid] [id] 
     *          4.2) [action] [task] [invalid]
     *     5) [action] [task] [id] [the rest] size > 2
     *     4,5 can be combined.
     */
    
    @Test(expected=ParseException.class)
    public void testUpdateTaskIdSizeOneInv() throws ParseException{
        variation = "delete 1";
        result =  parser.convertCommand(variation);      
    }
    
    @Test(expected=ParseException.class)
    public void testUpdateTaskIdInvalidId() throws ParseException{
        variation = "delete event opop";
        result =  parser.convertCommand(variation); 
    }
    
    @Test(expected=ParseException.class)
    public void testUpdateTaskIdInvalidTask() throws ParseException{
        variation = "delete blahs 1";
        result =  parser.convertCommand(variation); 
    }
 
    @Test(expected=ParseException.class)
    public void testUpdateTaskIdInvalidTaskAndId() throws ParseException{
        variation = "delete blahs lll";
        result =  parser.convertCommand(variation); 
    }
    
    @Test(expected=ParseException.class)
    public void testUpdateTaskIdInvalidTaskAndIdTogether() throws ParseException{
        variation = "delete sdsd3";
        result =  parser.convertCommand(variation); 
    }
    
    @Test
    public void testUpdateTaskId() throws ParseException{

        //(2)
        variation = "delete todo1";
        result = parser.convertCommand(variation);  
        assertEquals("delete todo 1", result);
        
        variation = "delete td1";
        result = parser.convertCommand(variation);  
        assertEquals("delete todo 1", result);    
        
        //(3)
        variation = "delete todo 3";
        result = parser.convertCommand(variation);  
        assertEquals("delete todo 3", result);
    }

    
    private String getEventAddCmd(String description, String start, String end,
            int reorderType, boolean isWrapped) {
        
        StringBuilder variationBuild = new StringBuilder();
        variationBuild.append("add ");
        
        description = (isWrapped) ? wrapDescription(description) : description;
        
        switch(reorderType){
            case ORDER_EVENT_DES :
                variationBuild.append(description + " ");
                variationBuild.append(end + " ");
                variationBuild.append(start);
                break;
            case ORDER_EVENT_SDE :
                variationBuild.append(start + " ");
                variationBuild.append(description + " ");
                variationBuild.append(end);
                break;                
            case ORDER_EVENT_SED : 
                variationBuild.append(start + " ");
                variationBuild.append(end + " ");
                variationBuild.append(description);
                break;     
            case ORDER_EVENT_ESD :
                variationBuild.append(end + " ");
                variationBuild.append(start + " ");
                variationBuild.append(description);
                break;
            case ORDER_EVENT_EDS :
                variationBuild.append(end + " ");
                variationBuild.append(description + " ");
                variationBuild.append(start);
                break;
            default:
                variationBuild.append(description + " ");
                variationBuild.append(start + " ");
                variationBuild.append(end);
        }

        return variationBuild.toString();        
    }
    
    /**
     * Get the add deadline command
     * 
     * @param description
     * @param end
     * @param isReorder
     * @param isWrapped
     * @return
     */
    private String getDeadlineAddCmd(String description, String end,
            boolean isReorder, boolean isWrapped) {
        StringBuilder variationBuild = new StringBuilder();
        variationBuild.append("add ");

        if (isReorder) {
            variationBuild.append(end);
        }

        if (isWrapped) {
            variationBuild.append(wrapDescription(description));
        } else {
            variationBuild.append(description + " ");
        }

        if (!isReorder) {
            variationBuild.append(end);
        }
        return variationBuild.toString();
    }
    
    /**
     * Get the test time
     * 
     * @param type Type of date
     * @param keywordType what's the keyword type
     * @param hasDash
     * @return
     */
    private String getTestTime(int type, int keywordType, boolean hasDash) {

        String endType = getTimeType(keywordType, hasDash);

        switch (type) {
            case HAS_SECS :
                return endType + " 26042013235923 ";
            case HAS_NO_SECS :
                return endType + " 260420132359 ";
            case HAS_TODAY_NO_TIME_SLASH :
                return endType + " " + today.format("DD/MM/YYYY");
            case HAS_TODAY_NO_TIME_DOT :
                return endType + " " + today.format("DD.MM.YYYY");
            case HAS_TODAY_NO_TIME_DASH :
                return endType + " " + today.format("DD-MM-YYYY");
            case HAS_TODAY_NO_TIME_KEYWORD :
                return endType + " today";
            case HAS_TODAY_TIME :
                return endType + " today 2000";
            case HAS_TODAY_TIME_AM :
                return endType + " today 9am";
            case HAS_NO_DATE_BUT_TIME_PM :
                return endType + " 8pm";
            case HAS_NO_DATE_BUT_MILITARY_TIME :
                return endType + " 2000";
            case HAS_DATE_24_AUG_2014_0900:
                return endType + " 24082014090000";
            case HAS_DATE_25_AUG_2014_1200:
                return endType + " 25082014120000";
            case HAS_DATE_24_AUG_2014_2133:
                return endType + " 24082014121330";
            
//            // start: 24th of August 2014 0900 - 25th of August 2014 1200
//            addEventControlADay = "add meet friends -start 24082014090000 -end 25082014120000";
//            // start: 24th of August 2014 0900 - 31th of August 2014 1200
//            addEventControlDays = "add meet friends -start 24082014090000 -end 31082014120000";
//            // start: 24th of August 2014 0900 - 24th of September 2014 1200
//            addEventControlMonths = "add meet friends -start 24082014090000 -end 24092014120000";
//            // start: 24th of August 2014 0900 - 24th of September 2017 1200
//            addEventControlYears = "add meet friends -start 24082014090000 -end 24092017120000";
            default :
                return "";
        }
    }

    private String getTimeType(int keywordType, boolean hasDash) {

        String timeType = "";

        if (hasDash) {
            timeType += "-";
        }

        switch (keywordType) {
            case DEAFULT_END :
                return timeType + "end";
            case DUE_END :
                return timeType + "due";
            case BY_END :
                return timeType + "by";
            case BEFORE_END :
                return timeType + "before";
            case DEFAULT_START :
                return timeType + "start";
            case FROM_START :
                return timeType + "from";
            case STARTING_START :
                return timeType + "starting";
            default :
                return "";
        }
    }

    private String getTestTime(int type) {
        return getTestTime(type, DEAFULT_END, true);
    }

    private String wrapDescription(String descript) {
        return "'" + descript + "' ";
    }

}
