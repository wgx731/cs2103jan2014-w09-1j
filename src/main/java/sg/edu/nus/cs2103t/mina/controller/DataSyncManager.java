package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.dao.MemoryDataObserver;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.dao.impl.MemoryDataObserverImpl;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * DataSyncManager is a facade class responsible for providing all required API
 * in one place.
 */
// @author A0105853H
public class DataSyncManager {

    private static final String CLASS_NAME = DataSyncManager.class.getName();

    private TaskDao _taskDao;
    private TaskMapDao _taskMapDao;
    private MemoryDataObserver _dataObserver;

    /**
     * Create a new DataSyncManager instance
     * 
     * @param taskDao DAO for task tree set
     * @param taskMapDao DAO for recurring task map
     */
    public DataSyncManager(TaskDao taskDao, TaskMapDao taskMapDao) {
        _taskDao = taskDao;
        _taskMapDao = taskMapDao;
        _dataObserver = new MemoryDataObserverImpl(taskDao, taskMapDao);
    }

    /**
     * Getter for data observer
     * 
     * @return the data observer
     */
    public MemoryDataObserver getDataObserver() {
        return _dataObserver;
    }

    /**
     * Save all the changed data from the given list of tree set data to storage
     * 
     * @param changedDataList the list of tree set data to be saved
     */
    public boolean saveAll(List<TaskSetDataParameter> changedDataList) {
        for (TaskSetDataParameter changedData : changedDataList) {
            try {
                _taskDao.saveTaskSet(changedData.getTaskSet(),
                        changedData.getTaskType(), changedData.isCompleted());
            } catch (IOException e) {
                LogHelper.log(CLASS_NAME, Level.ERROR,
                        "save task set failed: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Save specific type of tree set data to storage
     * 
     * @param taskType the task type of the tree set
     * @param isCompleted whether the task in the tree set is completed
     */
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        return (SortedSet<? extends Task<?>>) _taskDao.loadTaskSet(taskType,
                isCompleted);
    }

    /**
     * Save task map data to storage
     * 
     * @param taskMapData the task map data to be saved
     */
    public void saveTaskMap(TaskMapDataParameter taskMapData)
            throws IOException {
        _taskMapDao.saveTaskMap(taskMapData);
    }

    /**
     * Load task map data from storage
     * 
     * @return the task map data to be loaded
     */
    public TaskMapDataParameter loadTaskMap() {
        return _taskMapDao.loadTaskMap();
    }

}
