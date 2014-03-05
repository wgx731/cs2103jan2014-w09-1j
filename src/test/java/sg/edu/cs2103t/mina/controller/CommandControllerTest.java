package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandControllerTest {

    CommandController cc = new CommandController();

    @Test
    public void testProcessAddParameter() throws ParseException {
        assertEquals(cc.processAddParameter("do laundry"), new DataParameter(
                "do laundry", 'M', null, null, null, TaskType.TODO, -1));
        assertEquals(cc.processAddParameter("do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null, null,
                        TaskType.TODO, -1));
        assertEquals(
                cc.processAddParameter("do homework -end 12/12/1212"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/1212"), null, TaskType.DEADLINE, -1));
        assertEquals(
                cc.processAddParameter("do homework -end 12/12/1212 -priority L"),
                new DataParameter("do homework", 'L', null, DateUtil
                        .parse("12/12/1212"), null, TaskType.DEADLINE, -1));
        assertEquals(
                cc.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/1111"), DateUtil.parse("12/12/1212"),
                        null, TaskType.EVENT, -1));
        assertEquals(
                cc.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212 -priority L"),
                new DataParameter("meet friends", 'L', DateUtil
                        .parse("11/11/1111"), DateUtil.parse("12/12/1212"),
                        null, TaskType.EVENT, -1));
    }

    @SuppressWarnings("serial")
    @Test
    public void testProcessSearchParameter() {
        assertEquals(cc.processSearchParameter("yesterday today tomorrow")
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
        assertEquals(cc.processFilterParameter("complete todo event")
                .getFilters(), new ArrayList<String>() {
            {
                add("complete");
                add("todo");
                add("event");
            }
        });
    }

    @Test
    public void testProcessModifyParameter() throws ParseException {
        assertEquals(
                cc.processModifyParameter("todo 12 -description do laundry"),
                new DataParameter("do laundry", 'M', null, null, TaskType.TODO,
                        TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null, TaskType.TODO,
                        TaskType.TODO, 12));
        assertEquals(cc.processModifyParameter("todo 7 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.TODO,
                        TaskType.TODO, 7));

        assertEquals(
                cc.processModifyParameter("deadline 12 -description do homework"),
                new DataParameter("do homework", 'M', null, null,
                        TaskType.DEADLINE, TaskType.DEADLINE, 12));
        assertEquals(cc.processModifyParameter("deadline 12 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.DEADLINE,
                        TaskType.DEADLINE, 12));
        assertEquals(cc.processModifyParameter("deadline 12 -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -description do homework -priority H"),
                new DataParameter("do homework", 'H', null, null,
                        TaskType.DEADLINE, TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -description do homework -end 12/12/2012"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -description do homework -end 12/12/2012 -priority H"),
                new DataParameter("do homework", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.DEADLINE, 12));

        assertEquals(cc
                .processModifyParameter("event 12 -description meet friends"),
                new DataParameter("meet friends", 'M', null, null,
                        TaskType.EVENT, TaskType.EVENT, 12));
        assertEquals(cc.processModifyParameter("event 12 -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(cc.processModifyParameter("event 12 -start 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("12/12/2012"),
                        null, TaskType.EVENT, TaskType.EVENT, 12));
        assertEquals(cc.processModifyParameter("event 12 -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -priority L"),
                new DataParameter("meet friends", 'L', null, null,
                        TaskType.EVENT, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -start 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("12/12/2012"), null, TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -end 12/12/2012"),
                new DataParameter("meet friends", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.EVENT, TaskType.EVENT,
                        12));
        assertEquals(
                cc.processModifyParameter("event 12 -priority L -start 12/12/2012"),
                new DataParameter(null, 'L', DateUtil.parse("12/12/2012"),
                        null, TaskType.EVENT, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.EVENT, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.EVENT, TaskType.EVENT,
                        12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -start 11/11/2011 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), null, TaskType.EVENT,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.EVENT, TaskType.EVENT, 12));

        assertEquals(cc.processModifyParameter("event 12 -totype todo"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT,
                        TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype todo -description do laundry"),
                new DataParameter("do laundry", 'M', null, null,
                        TaskType.EVENT, TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype todo -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null,
                        TaskType.EVENT, TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype todo -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.TODO, 12));

        assertEquals(cc.processModifyParameter("deadline 12 -totype todo"),
                new DataParameter(null, 'M', null, null, TaskType.DEADLINE,
                        TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype todo -description do laundry"),
                new DataParameter("do laundry", 'M', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype todo -description do laundry -priority H"),
                new DataParameter("do laundry", 'H', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype todo -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.DEADLINE,
                        TaskType.TODO, 12));

        assertEquals(cc.processModifyParameter("event 12 -totype deadline"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -description do homework"),
                new DataParameter("do homework", 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -priority L"),
                new DataParameter(null, 'L', null, null, TaskType.EVENT,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -description do homework -priority H"),
                new DataParameter("do homework", 'H', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -description do homework -end 12/12/2012"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.EVENT,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.EVENT,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("event 12 -totype deadline -description do homework -end 12/12/2012 -priority H"),
                new DataParameter("do homework", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.EVENT,
                        TaskType.DEADLINE, 12));

        assertEquals(
                cc.processModifyParameter("todo 12 -totype deadline -end 12/12/2012"),
                new DataParameter(null, 'M', null,
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype deadline -description do homework -end 12/12/2012"),
                new DataParameter("do homework", 'M', null, DateUtil
                        .parse("12/12/2012"), TaskType.TODO, TaskType.DEADLINE,
                        12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype deadline -priority L -end 12/12/2012"),
                new DataParameter(null, 'L', null,
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.DEADLINE, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype deadline -description do homework -end 12/12/2012 -priority H"),
                new DataParameter("do homework", 'H', null, DateUtil
                        .parse("12/12/2012"), TaskType.TODO, TaskType.DEADLINE,
                        12));

        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -start 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("12/12/2012"),
                        null, TaskType.DEADLINE, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -description meet friends -start 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("12/12/2012"), null, TaskType.DEADLINE,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -priority L -start 12/12/2012"),
                new DataParameter(null, 'L', DateUtil.parse("12/12/2012"),
                        null, TaskType.DEADLINE, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.DEADLINE, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.DEADLINE,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -description meet friends -start 11/11/2011 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), null, TaskType.DEADLINE,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("deadline 12 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.DEADLINE, TaskType.EVENT, 12));

        assertEquals(
                cc.processModifyParameter("todo 12 -totype event -start 11/11/2011 -end 12/12/2012"),
                new DataParameter(null, 'M', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012"),
                new DataParameter("meet friends", 'M', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.TODO, TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype event -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter(null, 'H', DateUtil.parse("11/11/2011"),
                        DateUtil.parse("12/12/2012"), TaskType.TODO,
                        TaskType.EVENT, 12));
        assertEquals(
                cc.processModifyParameter("todo 12 -totype event -description meet friends -start 11/11/2011 -end 12/12/2012 -priority H"),
                new DataParameter("meet friends", 'H', DateUtil
                        .parse("11/11/2011"), DateUtil.parse("12/12/2012"),
                        TaskType.TODO, TaskType.EVENT, 12));
    }

    @Test
    public void testProcessMarkDeleteParameter() {
        assertEquals(cc.processMarkDeleteParameter("todo 12"),
                new DataParameter(null, 'M', null, null, TaskType.TODO, null,
                        12));
        assertEquals(
                cc.processMarkDeleteParameter("todo 7"),
                new DataParameter(null, 'M', null, null, TaskType.TODO, null, 7));
        assertEquals(cc.processMarkDeleteParameter("deadline 7"),
                new DataParameter(null, 'M', null, null, TaskType.DEADLINE,
                        null, 7));
        assertEquals(cc.processMarkDeleteParameter("event 7"),
                new DataParameter(null, 'M', null, null, TaskType.EVENT, null,
                        7));
    }

    @Test
    public void testProcessTaskTypeFromString() {
        assertEquals(cc.processTaskTypeFromString("todo"), TaskType.TODO);
        assertEquals(cc.processTaskTypeFromString("deadline"),
                TaskType.DEADLINE);
        assertEquals(cc.processTaskTypeFromString("event"), TaskType.EVENT);
    }
}
