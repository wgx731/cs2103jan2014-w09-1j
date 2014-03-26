package sg.edu.nus.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandProcessor;
import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandProcessorTest {
	
    @Test
    public void testProcessAddParameter() throws ParseException {
        CommandProcessor commandProcessorTest = new CommandProcessor();
 
        /*This is the non-null return partition*/
        assertEquals(commandProcessorTest.processAddParameter("do laundry"), new DataParameter(
                "do laundry", 'M', null, null, null, TaskType.TODO, -1));
        assertEquals(commandProcessorTest.processAddParameter("do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null, null,
                        TaskType.TODO, -1));
        assertEquals(
                commandProcessorTest.processAddParameter("do homework -end 12/12/1212"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/1212"), null, TaskType.DEADLINE, -1));
        assertEquals(
                commandProcessorTest.processAddParameter("do homework -end 12/12/1212 -priority L"),
                new DataParameter("do homework", 'L', null, DateUtil
                        .parse("12/12/1212"), null, TaskType.DEADLINE, -1));
        assertEquals(
                commandProcessorTest.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/1111"), DateUtil.parse("12/12/1212"),
                        null, TaskType.EVENT, -1));        
        assertEquals(
                commandProcessorTest.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212 -priority L"),
                new DataParameter("meet friends", 'L', DateUtil
                        .parse("11/11/1111"), DateUtil.parse("12/12/1212"),
                        null, TaskType.EVENT, -1));
        
        /* This is the null return partition*/
        /* This is the empty description partition*/
        assertEquals(commandProcessorTest.processAddParameter(""), null);
        assertEquals(commandProcessorTest.processAddParameter("-end 12/12/1212"),
        				null);        
        
        /* This is the wrong start/end date partition*/
        assertEquals(
        		commandProcessorTest.processAddParameter("meet friends -start 12/12/1212"),
        		null);
        assertEquals(
        		commandProcessorTest.processAddParameter("meet friends -start 12/12/1212 -end 11/11/1111 -priority L"),
        		null);
    }

    @SuppressWarnings("serial")
    @Test
    public void testProcessSearchParameter() {
        CommandProcessor commandProcessorTest = new CommandProcessor();

        assertEquals(commandProcessorTest.processSearchParameter("yesterday//today//tomorrow")
                .getKeywords(), new ArrayList<String>() {
            {
                add("yesterday");
                add("today");
                add("tomorrow");
            }
        });
    }

    @SuppressWarnings("serial")
    @Test
    public void testProcessFilterParameter() {
        CommandProcessor commandProcessorTest = new CommandProcessor();
        
        ArrayList<FilterType> targetFilter = new ArrayList<FilterType>() {
            {
                add(FilterType.TODO);
                add(FilterType.EVENT);
                add(FilterType.COMPLETE);
            }
        };
        assertEquals(targetFilter,
                commandProcessorTest.processFilterParameter("complete todo event").getFilters());
    }
    
    @Test
    public void testProcessModifyParameter() throws ParseException {
        CommandProcessor commandProcessorTest = new CommandProcessor();

    	commandProcessorTest.processUserInput("add todo1", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo2", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo3", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo4", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo5", 1, 1, 1);
    	
    	commandProcessorTest.processUserInput("add deadline1 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline2 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline3 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline4 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline5 -end 25032014", 1, 1, 1);
    	
    	commandProcessorTest.processUserInput("add event1 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event2 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event3 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event4 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event5 -start 25032014 -end 26032014", 1, 1, 1);
    	
    	/*This is test for modify todo partition*/
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -description do laundry"),
                new DataParameter("do laundry", 'M', null, null, TaskType.TODO,
                        TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null, TaskType.TODO,
                        TaskType.TODO, 1));
        assertEquals(commandProcessorTest.processModifyParameter("todo 1 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.TODO,
                        TaskType.TODO, 1));
        /* This is an invalid case that return null*/
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -end 26/03/2014"),
                	null);
        
    	/*This is test for modify deadline partition*/
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -description do homework"),
                new DataParameter("do homework", 'M', null, null,
                        TaskType.DEADLINE, TaskType.DEADLINE, 1));
        assertEquals(commandProcessorTest.processModifyParameter("deadline 1 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.DEADLINE,
                        TaskType.DEADLINE, 1));
        assertEquals(commandProcessorTest.processModifyParameter("deadline 1 -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -description do homework -priority H"),
                new DataParameter("do homework", 'H', null, null,
                        TaskType.DEADLINE, TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -description do homework -end 12/12/2012"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -description do homework -end 12/12/2012 -priority H"),
                new DataParameter("do homework", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1));
        /* This is an invalid case that return null*/
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -start 26/03/2014"),
                	null);

    	/*This is test for modify event partition*/
        assertEquals(commandProcessorTest
                .processModifyParameter("event 1 -description meet friends"),
                new DataParameter("meet friends", 'M', null, null,
                        TaskType.EVENT, TaskType.EVENT, 1));
        assertEquals(commandProcessorTest.processModifyParameter("event 1 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.EVENT, 1));
        assertEquals(commandProcessorTest.processModifyParameter("event 1 -start 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("12/12/2012"),
                        null, TaskType.EVENT, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -priority L"),
                new DataParameter("meet friends", 'L', null, null,
                        TaskType.EVENT, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -start 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("12/12/2012"), null, TaskType.EVENT,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -priority L -start 12/12/2012"),
                new DataParameter(null, 'L', DateUtil.parse("12/12/2012"),
                        null, TaskType.EVENT, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.EVENT, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -start 11/11/2011 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), null, TaskType.EVENT,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.EVENT, TaskType.EVENT, 1));
        /*These are test cases that return null*/
        assertEquals(commandProcessorTest.processModifyParameter("event 1 -end 12/12/2012"),
            	null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -end 12/12/2012"),
                null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -priority L -end 12/12/2012"),
                null);
        assertEquals(
        		commandProcessorTest.processModifyParameter("event 1 -start 12/12/2012 -end 11/11/2011 -priority H"),
        		null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -description meet friends -end 12/12/2012 -priority H"),
                null);
        
    	/*This is test for change type event to todo partition*/
        assertEquals(commandProcessorTest.processModifyParameter("event 1 -totype todo"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT,
                        TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype todo -description do laundry"),
                new DataParameter("do laundry", 'M', null, null,
                        TaskType.EVENT, TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype todo -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null,
                        TaskType.EVENT, TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype todo -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.TODO, 1));

        /*This is test for change type deadline to todo partition*/
        assertEquals(commandProcessorTest.processModifyParameter("deadline 1 -totype todo"),
                new DataParameter(null, 'M', null, null, TaskType.DEADLINE,
                        TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype todo -description do laundry"),
                new DataParameter("do laundry", 'M', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype todo -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype todo -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.DEADLINE,
                        TaskType.TODO, 1));
        
        /*This is test for change type event to deadline partition*/
        assertEquals(commandProcessorTest.processModifyParameter("event 1 -totype deadline"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -description do homework"),
                new DataParameter("do homework", 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -description do homework -priority H"),
                new DataParameter("do homework", 'H', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1));
        /*These are test cases that return null*/
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -end 12/12/2012"),
                	null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -description do homework -end 12/12/2012"),
                	null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -priority L -end 12/12/2012"),
                	null);
        assertEquals(
                commandProcessorTest.processModifyParameter("event 1 -totype deadline -description do homework -end 12/12/2012 -priority H"),
                	null);
        
        /*This is test for change type todo to deadline partition*/
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype deadline -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype deadline -description do homework -end 12/12/2012"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.TODO, TaskType.DEADLINE,
                        1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype deadline -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.DEADLINE, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype deadline -description do homework -end 12/12/2012 -priority H"),
                new DataParameter("do homework", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.TODO, TaskType.DEADLINE,
                        1));
        
        /*This is test for change type deadline to event partition*/
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -start 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("12/12/2012"),
                        null, TaskType.DEADLINE, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -description meet friends -start 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("12/12/2012"), null, TaskType.DEADLINE,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -priority L -start 12/12/2012"),
                new DataParameter(null, 'L', DateUtil.parse("12/12/2012"),
                        null, TaskType.DEADLINE, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.DEADLINE, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -description meet friends -start 11/11/2011 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), null, TaskType.DEADLINE,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("deadline 1 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.DEADLINE, TaskType.EVENT, 1));

        /*This is test for change type todo to event partition*/
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype event -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.TODO, TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype event -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.EVENT, 1));
        assertEquals(
                commandProcessorTest.processModifyParameter("todo 1 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.TODO, TaskType.EVENT, 1));
    }
    
    @Test
    public void testProcessMarkDeleteParameter() {
        CommandProcessor commandProcessorTest = new CommandProcessor();

    	commandProcessorTest.processUserInput("add todo1", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo2", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo3", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo4", 1, 1, 1);
    	commandProcessorTest.processUserInput("add todo5", 1, 1, 1);
    	
    	commandProcessorTest.processUserInput("add deadline1 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline2 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline3 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline4 -end 25032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add deadline5 -end 25032014", 1, 1, 1);
    	
    	commandProcessorTest.processUserInput("add event1 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event2 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event3 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event4 -start 25032014 -end 26032014", 1, 1, 1);
    	commandProcessorTest.processUserInput("add event5 -start 25032014 -end 26032014", 1, 1, 1);
    	
        assertEquals(commandProcessorTest.processMarkDeleteParameter("todo 5"),
                new DataParameter(null, 'M', null, null, TaskType.TODO, null,
                        5));
        assertEquals(
                commandProcessorTest.processMarkDeleteParameter("todo 2"),
                new DataParameter(null, 'M', null, null, TaskType.TODO, null, 2));
        assertEquals(commandProcessorTest.processMarkDeleteParameter("deadline 2"),
                new DataParameter(null, 'M', null, null, TaskType.DEADLINE,
                        null, 2));
        assertEquals(commandProcessorTest.processMarkDeleteParameter("event 1"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT, null,
                        1));
                        
    }
    
    @Test
    public void testProcessTaskTypeFromString() {
        CommandProcessor commandProcessorTest = new CommandProcessor();

        assertEquals(commandProcessorTest.processTaskTypeFromString("todo"), TaskType.TODO);
        assertEquals(commandProcessorTest.processTaskTypeFromString("deadline"),
                TaskType.DEADLINE);
        assertEquals(commandProcessorTest.processTaskTypeFromString("event"), TaskType.EVENT);
    }
}
