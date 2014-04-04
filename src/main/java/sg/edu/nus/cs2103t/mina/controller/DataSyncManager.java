package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.dao.impl.MemoryDataObserverImp;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;

/**
 * Sync memory data to storage on predefined interval
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0105853H
public class DataSyncManager {

    private static Logger logger = LogManager.getLogger(DataSyncManager.class
            .getName());

    private TaskDao _taskDao;
    private TaskMapDao _taskMapDao;
    private MemoryDataObserver _dataOberserver;

    /**
     * Create a self running timed task to sync memory data to storage
     * 
     * @param storage DAO object used to perform actual saving operation
     */
    public DataSyncManager(TaskDao taskDao, TaskMapDao taskMapDao) {
        super();
        _taskDao = taskDao;
        _taskMapDao = taskMapDao;
        _dataOberserver = new MemoryDataObserverImp(taskDao, taskMapDao);
    }
    
   
    public MemoryDataObserver getDataOberserver() {
        return _dataOberserver;
    }

    /**
     * Save all the data in given list to storage
     * 
     * @param changedDataList the list of data to be saved
     */
    public boolean saveAll(List<TaskSetDataParameter> changedDataList) {
        for (TaskSetDataParameter changedData : changedDataList) {
            try {
                _taskDao.saveTaskSet(changedData.getTaskSet(),
                        changedData.getTaskType(), changedData.isCompleted());
            } catch (IOException e) {
                logger.error("can't save task set data");
                logger.error(e, e);
                return false;
            }
        }
        return true;
    }

    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        return (SortedSet<? extends Task<?>>) _taskDao.loadTaskSet(taskType,
                isCompleted);
    }

    public void saveTaskMap(TaskMapDataParameter taskMapData)
            throws IOException {
        _taskMapDao.saveTaskMapData(taskMapData);
    }

    public TaskMapDataParameter loadTaskMap() {
        return _taskMapDao.loadTaskMapData();
    }

}
