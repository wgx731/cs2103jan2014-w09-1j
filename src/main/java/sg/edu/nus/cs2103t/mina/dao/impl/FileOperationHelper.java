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
 * 
 * Common file operations to be used by file based Dao implementation
 * 
 * @author wgx731
 */
// @author A0105853H
public class FileOperationHelper {

    private static final String CLASS_NAME = FileOperationHelper.class
            .getName();

    private Map<TaskType, String> _fileLocationMap;
    private String _completedSuffix;
    private String _fileExtension;

    public FileOperationHelper(String completedSuffix, String fileExtension) {
        _fileLocationMap = new HashMap<TaskType, String>();
        _fileLocationMap.put(TaskType.TODO,
                ConfigHelper.getProperty(ConfigHelper.TODO_KEY));
        _fileLocationMap.put(TaskType.EVENT,
                ConfigHelper.getProperty(ConfigHelper.EVENT_KEY));
        _fileLocationMap.put(TaskType.DEADLINE,
                ConfigHelper.getProperty(ConfigHelper.DEADLINE_KEY));
        _completedSuffix = completedSuffix;
        _fileExtension = fileExtension;
    }

    FileOperationHelper(String completedSuffix, String fileExtension,
            Map<TaskType, String> fileLocationMap) {
        _fileLocationMap = fileLocationMap;
        _completedSuffix = completedSuffix;
        _fileExtension = fileExtension;
    }

    public void cleanUp() {
        new File(getFileLocation(TaskType.TODO, false)).delete();
        new File(getFileLocation(TaskType.TODO, true)).delete();
        new File(getFileLocation(TaskType.EVENT, false)).delete();
        new File(getFileLocation(TaskType.EVENT, true)).delete();
        new File(getFileLocation(TaskType.DEADLINE, false)).delete();
        new File(getFileLocation(TaskType.DEADLINE, true)).delete();
    }

    boolean createFileStorage() {
        try {
            createFileIfNotExist(getFileLocation(TaskType.TODO, false));
            createFileIfNotExist(getFileLocation(TaskType.TODO, true));
            createFileIfNotExist(getFileLocation(TaskType.EVENT, false));
            createFileIfNotExist(getFileLocation(TaskType.EVENT, true));
            createFileIfNotExist(getFileLocation(TaskType.DEADLINE, false));
            createFileIfNotExist(getFileLocation(TaskType.DEADLINE, true));
        } catch (IOException e) {

            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "failed to create storage files: " + e.getMessage());
            return false;
        }
        return true;
    }

    String getFileLocation(TaskType taskType, boolean isCompleted) {
        String fileLocation = _fileLocationMap.get(taskType);
        if (isCompleted) {
            fileLocation += _completedSuffix;
        }
        fileLocation += _fileExtension;
        return fileLocation;
    }

    private void createFileIfNotExist(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
    }
}
