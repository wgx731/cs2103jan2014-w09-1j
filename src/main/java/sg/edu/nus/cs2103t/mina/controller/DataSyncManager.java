package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
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
public class DataSyncManager extends TimerTask implements MemoryDataObserver {

    private static Logger logger = LogManager.getLogger(DataSyncManager.class
            .getName());

    private List<TaskSetDataParameter> _syncList;
    private TaskDao _taskDao;
    private TaskMapDao _taskMapDao;

    /**
     * Create a self running timed task to sync memory data to storage
     * 
     * @param storage DAO object used to perform actual saving operation
     */
    public DataSyncManager(TaskDao taskDao, TaskMapDao taskMapDao) {
        super();
        _taskDao = taskDao;
        _taskMapDao = taskMapDao;
        _syncList = new ArrayList<TaskSetDataParameter>(6);
    }

    /**
     * Save all the data in given list to storage
     * 
     * @param changedDataList the list of data to be saved
     */
    public boolean saveAll(List<TaskSetDataParameter> changedDataList) {
        return saveChangedData(changedDataList);
    }

    /**
     * Timed operation to sync changed data to storage
     */
    @Override
    public void run() {
        if (_syncList.isEmpty()) {
            return;
        }
        saveChangedData(_syncList);
        _syncList.clear();
    }

    private boolean saveChangedData(List<TaskSetDataParameter> changedData) {
        for (TaskSetDataParameter data : changedData) {
            try {
                _taskDao.saveTaskSet(data.getTaskSet(), data.getTaskType(),
                        data.isCompleted());
            } catch (IOException e) {
                logger.error("save operation failed.");
                logger.error(e, e);
                return false;
            }
        }
        return true;
    }

    public void updateChange(TaskSetDataParameter syncData) {
        assert syncData != null : syncData;
        if (_syncList.contains(syncData)) {
            return;
        }
        _syncList.add(syncData);
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
