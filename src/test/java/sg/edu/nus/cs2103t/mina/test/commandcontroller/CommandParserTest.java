package sg.edu.nus.cs2103t.mina.test.commandcontroller;

import static org.junit.Assert.*;
import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.stub.CommandProcessorStub;

public class CommandParserTest {
    
    private static final boolean HAS_DASH = true;
    private static final boolean IS_REORDER = true;
    private static final boolean IS_WRAPPED = true;
    private CommandProcessorStub cpStub = new CommandProcessorStub();
    private CommandParser parser = new CommandParser(cpStub);
    
    private StringBuilder variationBuild;
    private String variation;
    private String result;
    private String testTime;
    
    public static final String TODO_ONE = "CPT_todo1";
    public static final String TODO_TWO = "CPT_todo2";
    public static final String TODO_THREE = "CPT_todo3";
    public static final String TODO_FOUR = "CPT_todo4";
    
    public static final String DEADLINE_DESCRIPTION = "Submit assignment ";
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
    
    private static final int DEAFULT_END = 0;
    private static final int DUE_END = 1;
    private static final int BY_END = 2;
    private static final int BEFORE_END = 3;

    
    private static String addTodoControlLow, 
                          addTodoControlMed,
                          addTodoControlHigh,
                            addTodoControlNone,
                            addDeadlineControlMonthDaySecs,
                            addDeadlineControlMonthDaySame,
                            addDeadlineControlNoDate,
                            addDeadlineControlNoDateMorning,
                            addDeadlineControlTodayNoTime,
                            addDeadlineControlMonthTimeNoSecs,
                            addEventControlADay,
                            addEventControlDays,
                            addEventControlMonths,
                            addEventControlYears;
    
    private static DateTime today;
    private static Logger logger;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        
        CommandParserTest cpt = new CommandParserTest();
        
        today = DateTime.today(TimeZone.getDefault());
        
        logger = LogManager.getLogger(CommandParserTest.class.getName());
        //Controls for adding todos
        addTodoControlLow = "add CPT_todo1 -priority L";
        addTodoControlMed = "add CPT_todo2 -priority M";
        addTodoControlHigh = "add CPT_todo3 -priority H";
        addTodoControlNone = "add CPT_todo4";
        
        String todayDateString = today.format("DDMMYYYY");
        
        //Today, time specified.
        addDeadlineControlNoDate = "add Submit assignment -end " + todayDateString + "200000";
        //Today, morning
        addDeadlineControlNoDateMorning = "add Submit assignment -end " + todayDateString + "090000";
        //Today no time specified
        addDeadlineControlTodayNoTime = "add Submit assignment -end " + todayDateString + "235959";
        //26th of April 2013, time, no seconds.
        addDeadlineControlMonthTimeNoSecs = "add Submit assignment -end 26042013235900";
        //26th of April 2013, 11:59:23pm
        addDeadlineControlMonthDaySecs = "add Submit assignment -end 26042013235923";
        //4th of July 2013, 2359
        addDeadlineControlMonthDaySame = "add Submit assignment -end 04072013235900";
        
        //start: 24th of August 2014 0900 - 25th of August 2014 1200
        addEventControlADay = "add meet friends -start 240820140900 -end 250820141200";
        //start: 24th of August 2014 0900 - 31th of August 2014 1200
        addEventControlDays = "add meet friends -start 240820140900 -end 310820141200";
        //start: 24th of August 2014 0900 - 24th of September 2014 1200
        addEventControlMonths = "add meet friends -start 240820140900 -end 240920141200";
        //start: 24th of August 2014 0900 - 24th of September 2017 1200
        addEventControlYears = "add meet friends -start 240820140900 -end 240920171200";;        
    }
    
    @Before
    public void setUp(){
        variationBuild = new StringBuilder();
        variation = result = testTime = "";
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testNull() throws Exception{
        parser.convertCommand(null);
    }
    
    @Test(expected=ParseException.class)
    public void testEmptyString() throws Exception{
        parser.convertCommand("");
    }
    
    /**
     * Test to ensure that the control statements returned as it is.
     * @throws Exception
     */
    @Test
    public void testAddTodoControl() throws Exception{
        assertEquals(addTodoControlLow, parser.convertCommand(addTodoControlLow));
        assertEquals(addTodoControlMed, parser.convertCommand(addTodoControlMed));
        assertEquals(addTodoControlHigh, parser.convertCommand(addTodoControlHigh));
        assertEquals(addTodoControlNone, parser.convertCommand(addTodoControlNone));
    }
    
    /**
     * Testing for the different add command for todos.
     */
    @Test
    public void testReorderTodo() throws Exception{
        
        //Reordering of flags
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
    public void testDelimitTodo() throws Exception{
        //With delimiter of adding todos with low priority:
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
    public void testAddKeywords() throws Exception{
        
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
    public void testPriorityKeywords() throws Exception{
        
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
        variationBuild.append(" urgent priority");
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
    
    @Test
    public void exploratoryTest() throws Exception{
        
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
        variationBuild.append("submit homework -due 12/3/2013 so that I could do something ");
        variation = variationBuild.toString();
        logger.info(variation);
        result = parser.convertCommand(variation);
        assertEquals("add submit homework so that I could do something -end 12032013235959", result);
        
        variation = "add today -end 0800 today tomorrow yesterday";
        result = parser.convertCommand(variation);
        String resultDate = today.format("DDMMYYYY");
        assertEquals("add today tomorrow yesterday -end " + resultDate + "080000", result);
        
        variation = "add before tomorrow 9.30am 'push harder'";
        result = parser.convertCommand(variation);
        DateTime tmr = today.plusDays(1);
        resultDate = tmr.format("DDMMYYYY");
        assertEquals("add push harder -end " + resultDate + "093000", result);
    }
    
    @Test
    public void testAddDeadlinesControl() throws Exception{
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
     * @throws Exception
     */
    @Test
    public void testAddDeadlineReorder() throws Exception{
        
        variationBuild.append("add ");
        variationBuild.append(getTestTime(HAS_SECS));
        variationBuild.append(DEADLINE_DESCRIPTION);
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthDaySecs, result);
        
        setUp();
        variationBuild.append("add ");
        variationBuild.append(getTestTime(HAS_NO_SECS));
        variationBuild.append(DEADLINE_DESCRIPTION);
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);
        
    }
    
    /**
     * Testing for different -end words. Check for reorder, quotes and no quotes
     * @throws Exception
     */
    @Test
    public void testAddDeadlinesEndWords() throws Exception{
        
        //-end as control
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, DEAFULT_END, false));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);   
        
        //due no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, DUE_END, false));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);
        
        //-due
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, DUE_END, true));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);
        
        //by no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, BY_END, false));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result); 
        
        //-by
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, BY_END, true));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);   
        
        //before no dash
        setUp();
        variationBuild.append("add ");
        variationBuild.append(wrapDescription(DEADLINE_DESCRIPTION));
        variationBuild.append(getTestTime(HAS_NO_SECS, BEFORE_END, !HAS_DASH));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);
        
        //-before
        setUp();
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_NO_SECS, BEFORE_END, true));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);   
        
    }
    
    /**
     * Test wrap and no wrap
     * @throws Exception
     */
    @Test
    public void testAddDeadlinesDateFormat() throws Exception{
        
        logger.info("Today's date is: " + today.toString());
        
        //testing today no time
        //no wrap with DD/MM/YYYY format
        variationBuild.append("add ");
        variationBuild.append(DEADLINE_DESCRIPTION);
        variationBuild.append(getTestTime(HAS_TODAY_NO_TIME_SLASH, BEFORE_END, HAS_DASH));
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);   
        
        //With wrap
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, 
                                    getTestTime(HAS_TODAY_NO_TIME_SLASH, BEFORE_END, HAS_DASH),
                                    !IS_REORDER, IS_WRAPPED);
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);    
        
        //DD.MM.YYYY
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, 
                                    getTestTime(HAS_TODAY_NO_TIME_DOT, 
                                                BEFORE_END, HAS_DASH),
                                    !IS_REORDER, IS_WRAPPED);
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result);  
        
        //'Today' keyword
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, 
                                        getTestTime(HAS_TODAY_NO_TIME_KEYWORD, 
                                                    BEFORE_END, HAS_DASH),
                                         !IS_REORDER, IS_WRAPPED);
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlTodayNoTime, result); 
        
        //'Today' with time, 2000
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, 
                                        getTestTime(HAS_TODAY_TIME, 
                                                    BEFORE_END, HAS_DASH),
                                         !IS_REORDER, IS_WRAPPED);
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);
        
        //Today with time, 9am
        testTime = getTestTime(HAS_TODAY_TIME_AM, BEFORE_END, HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                                        !IS_REORDER, IS_WRAPPED);      
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDateMorning, result);
        
        //Today with no 'today' stated. Military
        testTime = getTestTime(HAS_NO_DATE_BUT_MILITARY_TIME, BEFORE_END, HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                                        !IS_REORDER, IS_WRAPPED);      
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);
        
        //Today with no 'today' stated. am
        //Today with no 'today' stated. Military
        testTime = getTestTime(HAS_NO_DATE_BUT_TIME_PM, BEFORE_END, HAS_DASH);
        variation = getDeadlineAddCmd(DEADLINE_DESCRIPTION, testTime,
                                        !IS_REORDER, IS_WRAPPED);      
        result =  parser.convertCommand(variation);
        logger.info(variation);
        assertEquals(addDeadlineControlNoDate, result);        
    }
    
    /**
     * Get the add deadline command
     * @param description
     * @param end
     * @param isReorder
     * @param isWrapped
     * @return
     */
    private String getDeadlineAddCmd(String description, String end, 
                                    boolean isReorder, boolean isWrapped){
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
    
    private String getTestTime(int type, int keywordType, boolean hasDash){
        
        String endType = getEndType(keywordType, hasDash);
        
        switch(type){
            case HAS_SECS :
                return  endType + " 26042013235923 ";
            case HAS_NO_SECS :
                return endType + " 260420132359 ";
            case HAS_TODAY_NO_TIME_SLASH :
                return endType + " " +today.format("DD/MM/YYYY");
            case HAS_TODAY_NO_TIME_DOT :
                return endType + " " +today.format("DD.MM.YYYY");
            case HAS_TODAY_NO_TIME_DASH :
                return endType + " " +today.format("DD-MM-YYYY");
            case HAS_TODAY_NO_TIME_KEYWORD :
                return endType + " today";
            case HAS_TODAY_TIME :
                return endType + " today 2000";
            case HAS_TODAY_TIME_AM:
                return endType + " today 9am";
            case HAS_NO_DATE_BUT_TIME_PM:
                return endType + " 8pm";
            case HAS_NO_DATE_BUT_MILITARY_TIME:
                return endType + " 2000";
            default:
                return "";
        }        
    }
    
    private String getEndType(int keywordType, boolean hasDash) {
        
        String endType = "";
        
        if(hasDash){
            endType+="-";
        }
        
        switch (keywordType) {
            case DEAFULT_END :
                return endType + "end";
            case DUE_END:
                return endType + "due";
            case BY_END :
                return endType + "by";
            case BEFORE_END :
                return endType + "before";
            default :
                return "";
        }
    }

    private String getTestTime(int type){
        return getTestTime(type, DEAFULT_END, true);
    }
    
    private String wrapDescription(String descript) {
        return "'" + descript + "' ";
    }
    

}
