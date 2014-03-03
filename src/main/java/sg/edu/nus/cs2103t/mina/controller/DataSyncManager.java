package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.parameter.SyncDataParameter;

/**
 * Sync memory data with storage on predefined intervals
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class DataSyncManager extends TimerTask {

    private static Logger logger = LogManager.getLogger(DataSyncManager.class
            .getName());

    private TaskDataManager _memoryDataStore;
    private TaskDao _storage;

    public DataSyncManager(TaskDataManager memoryDataStore, TaskDao storage) {
        super();
        _memoryDataStore = memoryDataStore;
        _storage = storage;
    }

    @Override
    public void run() {
        List<SyncDataParameter> dataToSync = _memoryDataStore.getSyncList();
        for (SyncDataParameter parameter : dataToSync) {
            try {
                _storage.saveTaskSet(parameter.getTaskSet(),
                        parameter.getTaskType(), parameter.isCompleted());
            } catch (IllegalArgumentException e) {
                logger.error(e, e);
            } catch (IOException e) {
                logger.error(e, e);
            }
        }
        _memoryDataStore.clearSyncList();
    }

}
