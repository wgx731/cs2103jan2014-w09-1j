/**
 * 
 */
package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;

/**
 * @author wgx731
 * 
 */
public class MemoryDataObserverImp implements MemoryDataObserver {

    private static Logger logger = LogManager
            .getLogger(MemoryDataObserverImp.class.getName());

    private TaskDao _taskDao;
    private TaskMapDao _taskMapDao;

    public MemoryDataObserverImp(TaskDao taskDao, TaskMapDao taskMapDao) {
        _taskDao = taskDao;
        _taskMapDao = taskMapDao;
    }

    @Override
    public void updateTaskMap(TaskMapDataParameter changedTaskMapData) {
        try {
            _taskMapDao.saveTaskMapData(changedTaskMapData);
        } catch (IOException e) {
            logger.error(e, e);
            logger.error("can't save task map data");
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
            logger.error("can't save task set data");
            logger.error(e, e);
        }
    }

}
