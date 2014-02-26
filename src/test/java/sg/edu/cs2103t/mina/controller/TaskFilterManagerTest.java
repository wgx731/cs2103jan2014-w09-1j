package sg.edu.cs2103t.mina.controller;

import java.util.ArrayList;

import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

public class TaskFilterManagerTest {

    private TaskDataManagerStub tdmStub = new TaskDataManagerStub();
    private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);

    @Test(expected = NullPointerException.class)
    public void testFilterVoid() {
        tfmTest.filterTask(null);
    }

    @Test
    /**
     * Test for passing an empty filter parameter
     * Expected: Returned all uncompleted tasks
     * 
     */
    public void testNoFilter() {

        ArrayList<Task> test = tfmTest.filterTask(new FilterParameter());

    }

}
