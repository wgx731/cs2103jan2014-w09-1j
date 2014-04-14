package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.IOException;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Memory Data Observer Implementation
 */
//@author A0105853H

public class MemoryDataObserverImpl implements MemoryDataObserver {

    private static final String CLASS_NAME = MemoryDataObserverImpl.class
            .getName();

    private TaskDao _taskDao;
    private TaskMapDao _taskMapDao;

    public MemoryDataObserverImpl(TaskDao taskDao, TaskMapDao taskMapDao) {
        _taskDao = taskDao;
        _taskMapDao = taskMapDao;
    }

    MemoryDataObserverImpl(FileOperationHelper fileOperationHelper) {
        _taskDao = new JsonFileTaskDaoImpl(fileOperationHelper);
        _taskMapDao = new FileTaskMapDaoImpl(fileOperationHelper);
    }

    TaskDao getTaskDao() {
        return _taskDao;
    }

    TaskMapDao getTaskMapDao() {
        return _taskMapDao;
    }

    @Override
    public void updateTaskMap(TaskMapDataParameter changedTaskMapData) {
        try {
            _taskMapDao.saveTaskMap(changedTaskMapData);
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "can't save task map data: " + e.getMessage());
        }
    }

    @Override
    public void updateTaskSet(TaskSetDataParameter changedData) {
        assert changedData != null : changedData;
        saveChangedData(changedData);
    }

    private void saveChangedData(TaskSetDataParameter changedData) {
        try {
            _taskDao.saveTaskSet(changedData.getTaskSet(),
                    changedData.getTaskType(), changedData.isCompleted());
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "can't save task set data: " + e.getMessage());
        }
    }

}
