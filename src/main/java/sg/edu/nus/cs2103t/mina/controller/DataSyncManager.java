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
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.SyncDataParameter;

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

    private List<SyncDataParameter> _syncList;
    private TaskDao _storage;

    /**
     * Create a self running timed task to sync memory data to storage
     * 
     * @param storage DAO object used to perform actual saving operation
     */
    public DataSyncManager(TaskDao storage) {
        super();
        _storage = storage;
        _syncList = new ArrayList<SyncDataParameter>(6);
    }

    /**
     * Save all the data in given list to storage
     * 
     * @param changedDataList the list of data to be saved
     */
    public boolean saveAll(List<SyncDataParameter> changedDataList) {
        return saveChangedData(changedDataList);
    }

    /**
     * Timed operation to sync changed data to storage
     */
    @Override
    public void run() {
        if (_syncList.isEmpty()) {
            logger.info("no memory data change.");
            return;
        }
        saveChangedData(_syncList);
        _syncList.clear();
    }

    private boolean saveChangedData(List<SyncDataParameter> changedData) {
        for (SyncDataParameter data : changedData) {
            try {
                _storage.saveTaskSet(data.getTaskSet(), data.getTaskType(),
                        data.isCompleted());
                logger.info("saved data: " + data);
            } catch (IllegalArgumentException e) {
                logger.error("wrong saving parameter passed.");
                logger.error(e, e);
                return false;
            } catch (IOException e) {
                logger.error("save operation failed.");
                logger.error(e, e);
                return false;
            }
        }
        return true;
    }

    public void updateChange(SyncDataParameter syncData) {
        assert syncData != null : syncData;
        if (_syncList.contains(syncData)) {
            logger.info("duplicate sync data: " + syncData.toString());
            return;
        }
        _syncList.add(syncData);
    }

    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        return _storage.loadTaskSet(taskType, isCompleted);
    }

}
