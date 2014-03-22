package sg.edu.nus.cs2103t.mina.test.commandcontroller;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.model.TaskView;

public class CommandParserTest {
    
    private CommandProcessorStub cpStub = new CommandProcessorStub();
    private CommandParser parser = new CommandParser(cpStub);
    
    private StringBuilder variationBuild;
    private String variation;
    private String result;
    
    public static final String TODO_ONE = "CPT_todo1";
    public static final String TODO_TWO = "CPT_todo2";
    public static final String TODO_THREE = "CPT_todo3";
    public static final String TODO_FOUR = "CPT_todo4";
    
    public static final String DEADLINE_DESCRIPTION = "Submit assignment";
    private static final int HAS_SECS = 0;
    private static final int HAS_NO_SECS = 1;
    
    private static String addTodoControlLow, 
                          addTodoControlMed,
                          addTodoControlHigh,
                            addTodoControlNone,
                            addDeadlineControlMonthDaySecs,
                            addDeadlineControlMonthDaySame,
                            addDeadlineControlNoDate,
                            addDeadlineControlNoDateMorning,
                            addDeadlineControlMonthTimeNoSecs,
                            addEventControlADay,
                            addEventControlDays,
                            addEventControlMonths,
                            addEventControlYears;
    
    
    private static Logger logger;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        
        CommandParserTest cpt = new CommandParserTest();
        
        logger = LogManager.getLogger(CommandParserTest.class.getName());
        //Controls for adding todos
        addTodoControlLow = "add CPT_todo1 -priority L";
        addTodoControlMed = "add CPT_todo2 -priority M";
        addTodoControlHigh = "add CPT_todo3 -priority H";
        addTodoControlNone = "add CPT_todo4";

        //Today, only time.
        addDeadlineControlNoDate = "add Submit assignment -end 235900";
        //Today, morning
        addDeadlineControlNoDateMorning = "add Submit assignment -end 090000";
        //26th of April 2013, time, no seconds.
        addDeadlineControlMonthTimeNoSecs = "add Submit assignment -end 26042013235900";
        //26th of April 2013, 11:59:23pm
        addDeadlineControlMonthDaySecs = "add Submit assignment -end 26042013235923";
        //4th of July 2013, 2359
        addDeadlineControlMonthDaySame = "add Submit assignment -end 04072013235900";
        
    }
    
    @Before
    public void setUp(){
        variationBuild = new StringBuilder();
        variation = result = "";
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
    public void breakAddTodo() throws Exception{
        
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
    }
    
    @Test
    public void testAddDeadlinesControl() throws Exception{
        //assertEquals(addDeadlineControlNoDate,
                        //parser.convertCommand(addDeadlineControlNoDate));
        //assertEquals(addDeadlineControlNoDateMorning,
                       //parser.convertCommand(addDeadlineControlNoDateMorning));
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
        assertEquals(addDeadlineControlMonthDaySecs, result);
        
        //No time
        setUp();
        variationBuild.append("add ");
        variationBuild.append(getTestTime(HAS_NO_SECS));
        variationBuild.append(DEADLINE_DESCRIPTION);
        variation = variationBuild.toString();
        result =  parser.convertCommand(variation);
        assertEquals(addDeadlineControlMonthTimeNoSecs, result);
        
        
    }
    
    @Ignore
    @Test
    public void testAddDeadlines() throws Exception{
        
    }
    
    @Ignore
    @Test
    public void testAddDeadlinesDateFormat() throws Exception{
        
    }
    
    private String getTestTime(int type){
        switch(type){
            case HAS_SECS :
                return "-end 26042013235923 ";
            case HAS_NO_SECS :
                return "-end 260420132359 ";
            default:
                return "";
        }
    }
    
    private String wrapDescription(String descript) {
        return "'" + descript + "'";
    }
    

}
