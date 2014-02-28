package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import sg.edu.cs2103t.mina.stub.DataParameterStub;
import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class CommandControllerTest {
	CommandController cc = new CommandController();
	@Test
	public void testProcessAddParameter() throws ParseException{
		assertEquals(cc.processAddParameter("do laundry"), 
				new DataParameterStub("do laundry", 'M', null, null, null, TaskType.TODO, -1));
		assertEquals(cc.processAddParameter("do laundry -priority H"),
				new DataParameterStub("do laundry", 'H', null, null, null, TaskType.TODO, -1));
		assertEquals(cc.processAddParameter("do homework -end 12/12/1212"),
				new DataParameterStub("do homework", 'M', null, DateUtil.parse("12/12/1212"), null, TaskType.DEADLINE, -1));
		assertEquals(cc.processAddParameter("do homework -end 12/12/1212 -priority L"),
				new DataParameterStub("do homework", 'L', null, DateUtil.parse("12/12/1212"), null, TaskType.DEADLINE, -1));
		assertEquals(cc.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212"),
				new DataParameterStub("meet friends", 'M', DateUtil.parse("11/11/1111"), DateUtil.parse("12/12/1212"), null, TaskType.EVENT, -1));
		assertEquals(cc.processAddParameter("meet friends -start 11/11/1111 -end 12/12/1212 -priority L"),
				new DataParameterStub("meet friends", 'L', DateUtil.parse("11/11/1111"), DateUtil.parse("12/12/1212"), null, TaskType.EVENT, -1));
	}
}
