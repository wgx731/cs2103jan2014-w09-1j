package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.IOException;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Actual test cases for memory data observer
 */
// @author A0105853H

public class MemoryDataObserverImplTest extends FileDaoImplTest {

    private static final String CLASS_NAME = MemoryDataObserverImplTest.class
            .getName();

    private MemoryDataObserverImpl _observer;
    private FileOperationHelper _fileOperationHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        _fileOperationHelper = new FileOperationHelper(storageMap);
        _observer = new MemoryDataObserverImpl(_fileOperationHelper);
        _fileOperationHelper.cleanAll();
        _fileOperationHelper.setUpAll();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        _fileOperationHelper.cleanAll();
        _observer = null;
    }

    @Test
    public void testUpdateTaskMap() {
        _observer.updateTaskMap(new TaskMapDataParameter(sampleTaskMap,
                MAX_RECUR_INT));
        TaskMapDataParameter loadedData = _observer.getTaskMapDao()
                .loadTaskMap();
        Assert.assertNotNull(loadedData);
        Assert.assertEquals(sampleTaskMap, loadedData.getRecurringTasks());
        Assert.assertEquals(MAX_RECUR_INT, loadedData.getMaxRecurTagInt());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateTaskSet() {
        _observer.updateTaskSet(new TaskSetDataParameter(sampleTodoTaskSet,
                TaskType.TODO, UNCOMPLETED));
        SortedSet<TodoTask> loadedTaskSet = null;
        try {
            loadedTaskSet = (SortedSet<TodoTask>) _observer.getTaskDao()
                    .loadTaskSet(TaskType.TODO, UNCOMPLETED);
        } catch (IOException e) {
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        }
        Assert.assertNotNull(loadedTaskSet);
        Assert.assertEquals(sampleTodoTaskSet, loadedTaskSet);
    }

    @Override
    @Ignore
    public void testLoadEmptyFile() {
    }

}
