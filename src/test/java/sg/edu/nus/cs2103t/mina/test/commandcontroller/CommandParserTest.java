package sg.edu.nus.cs2103t.mina.test.commandcontroller;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandParser;
import sg.edu.nus.cs2103t.mina.model.TaskView;

public class CommandParserTest {
    
    private CommandProcessorStub cpStub = new CommandProcessorStub();
    private CommandParser parser = new CommandParser(cpStub);
    
    public static final String TODO_ONE = "CPT_todo1";
    public static final String TODO_TWO = "CPT_todo2";
    public static final String TODO_THREE = "CPT_todo3";
    public static final String TODO_FOUR = "CPT_todo4";
    
    private static String addTodoControlLow, 
                            addTodoControlMed,
                            addTodoControlHigh,
                            addTodoControlNone,
                            addDeadlineControl,
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
        //assertEquals(addTodoControlMed, parser.convertCommand(addTodoControlMed));
        //assertEquals(addTodoControlHigh, parser.convertCommand(addTodoControlHigh));
        //assertEquals(addTodoControlNone, parser.convertCommand(addTodoControlNone));
    }
    
    /**
     * Test for the different add command for todos.
     */
    @Test
    public void testAddTodo() throws Exception{
        StringBuilder variationBuild = new StringBuilder();
        String variation;
        
        //Various way of adding todos with low priority:
        variationBuild.append("add ");
        variationBuild.append("'" + TODO_ONE + "'");
        variationBuild.append(" low priority");
        variation = variationBuild.toString();
        logger.info(variation);
        String result = parser.convertCommand(variation);
        assertEquals(addTodoControlLow, result);
    }
    
    @Ignore
    @Test
    public void breakAddTodo() throws Exception{
        
    }
}
