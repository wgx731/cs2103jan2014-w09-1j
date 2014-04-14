package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Actual test case for task map Dao
 */
//@author A0105853H

public class FileTaskMapDaoImpTest extends FileDaoImplTest {

    private static final String CLASS_NAME = FileTaskMapDaoImpTest.class
            .getName();

    private static final String CORRUPTED_DATA = "Corrupted file.";
    private static final String SAVE_ERROR = "Test data not saved.";

    private FileTaskMapDaoImpl _storage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        _storage = new FileTaskMapDaoImpl();
        try {
            _storage.saveTaskMap(new TaskMapDataParameter(sampleTaskMap,
                    MAX_RECUR_INT));
        } catch (IOException e) {
            Assert.fail(SAVE_ERROR);
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        _storage = null;
    }

    @Test
    public void testLoadTaskMapNormal() {
        TaskMapDataParameter loadedData;
        loadedData = _storage.loadTaskMap();
        Assert.assertNotNull(loadedData);
        Assert.assertEquals(sampleTaskMap, loadedData.getRecurringTasks());
        Assert.assertEquals(MAX_RECUR_INT, loadedData.getMaxRecurTagInt());
    }

    @Test
    public void testLoadCorruptedTaskMapFile() {
        rewriteFile(new File(_storage.getFileOperationHelper()
                .getTaskMapFileLocation()));
        TaskMapDataParameter loadedTaskMap = _storage.loadTaskMap();
        Assert.assertNull(loadedTaskMap);
    }

    private void rewriteFile(File file) {
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(CORRUPTED_DATA);
            writer.close();
        } catch (IOException e) {
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        }
    }

    @Override
    public void testLoadEmptyFile() {
        _storage.getFileOperationHelper().cleanTaskMapDao();
        TaskMapDataParameter loadedTaskMap = _storage.loadTaskMap();
        Assert.assertNull(loadedTaskMap);
    }

}
