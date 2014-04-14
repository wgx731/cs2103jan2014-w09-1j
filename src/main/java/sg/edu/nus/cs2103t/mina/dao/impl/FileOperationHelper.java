package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Common file operations to be used by file based Dao implementation
 */
//@author A0105853H

public class FileOperationHelper {

    private static final String CLASS_NAME = FileOperationHelper.class
            .getName();
    private static final boolean COMPLETED = true;
    private static final boolean UNCOMPLETED = false;

    private Map<TaskType, String> _fileLocationMap;
    private String _taskSetCompletedSuffix;
    private String _taskSetfileExtension;
    private String _taskMapfileExtension;

    public FileOperationHelper(String completedSuffix,
            String taskSetFileExtension, String taskMapFileExtension) {
        _fileLocationMap = new HashMap<TaskType, String>();
        _fileLocationMap.put(TaskType.TODO,
                ConfigHelper.getProperty(ConfigHelper.TODO_KEY));
        _fileLocationMap.put(TaskType.EVENT,
                ConfigHelper.getProperty(ConfigHelper.EVENT_KEY));
        _fileLocationMap.put(TaskType.DEADLINE,
                ConfigHelper.getProperty(ConfigHelper.DEADLINE_KEY));
        _taskSetCompletedSuffix = completedSuffix;
        _taskSetfileExtension = taskSetFileExtension;
        _taskMapfileExtension = taskMapFileExtension;
    }

    FileOperationHelper(Map<TaskType, String> storageMap) {
        _fileLocationMap = storageMap;
        _taskSetCompletedSuffix = JsonFileTaskDaoImpl.getCompletedSuffix();
        _taskSetfileExtension = JsonFileTaskDaoImpl.getFileExtension();
        _taskMapfileExtension = FileTaskMapDaoImpl.getFileExtension();
    }
    
    FileOperationHelper() {
        _taskSetCompletedSuffix = JsonFileTaskDaoImpl.getCompletedSuffix();
        _taskSetfileExtension = JsonFileTaskDaoImpl.getFileExtension();
        _taskMapfileExtension = FileTaskMapDaoImpl.getFileExtension();
    }

    public synchronized void cleanAll() {
        cleanTaskSetDao();
        cleanTaskMapDao();
    }

    public synchronized boolean setUpAll() {
        boolean isTaskSetCreated = createTaskSetDaoFiles();
        boolean isTaskMapCreated = createTaskMapDaoFiles();
        return isTaskMapCreated && isTaskSetCreated;
    }

    synchronized void cleanTaskSetDao() {
        new File(getTaskSetFileLocation(TaskType.TODO, UNCOMPLETED)).delete();
        new File(getTaskSetFileLocation(TaskType.TODO, COMPLETED)).delete();
        new File(getTaskSetFileLocation(TaskType.EVENT, UNCOMPLETED)).delete();
        new File(getTaskSetFileLocation(TaskType.EVENT, COMPLETED)).delete();
        new File(getTaskSetFileLocation(TaskType.DEADLINE, UNCOMPLETED))
                .delete();
        new File(getTaskSetFileLocation(TaskType.DEADLINE, COMPLETED)).delete();
    }

    synchronized void cleanTaskMapDao() {
        new File(getTaskMapFileLocation()).delete();
    }

    synchronized boolean createTaskSetDaoFiles() {
        try {
            createFileIfNotExist(getTaskSetFileLocation(TaskType.TODO,
                    UNCOMPLETED));
            createFileIfNotExist(getTaskSetFileLocation(TaskType.TODO,
                    COMPLETED));
            createFileIfNotExist(getTaskSetFileLocation(TaskType.EVENT,
                    UNCOMPLETED));
            createFileIfNotExist(getTaskSetFileLocation(TaskType.EVENT,
                    COMPLETED));
            createFileIfNotExist(getTaskSetFileLocation(TaskType.DEADLINE,
                    UNCOMPLETED));
            createFileIfNotExist(getTaskSetFileLocation(TaskType.DEADLINE,
                    COMPLETED));
        } catch (IOException e) {
            LogHelper.log(
                    CLASS_NAME,
                    Level.ERROR,
                    "failed to create task set storage files: " + e
                            .getMessage());
            return false;
        }
        return true;
    }

    synchronized boolean createTaskMapDaoFiles() {
        try {
            createFileIfNotExist(getTaskMapFileLocation());
        } catch (IOException e) {
            LogHelper
                    .log(CLASS_NAME,
                            Level.ERROR,
                            "failed to create task map storage file: " + e
                                    .getMessage());
            return false;
        }
        return true;
    }

    String getTaskSetFileLocation(TaskType taskType, boolean isCompleted) {
        String fileLocation = _fileLocationMap.get(taskType);
        if (isCompleted) {
            fileLocation += _taskSetCompletedSuffix;
        }
        fileLocation += _taskSetfileExtension;
        return fileLocation;
    }

    String getTaskMapFileLocation() {
        return ConfigHelper.getProperty(ConfigHelper.TASK_MAP_KEY) + _taskMapfileExtension;
    }

    private void createFileIfNotExist(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
    }
}
