package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;

/**
 * 
 * Common file operations to be used by file based Dao implementation
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0105853H
public class FileOperationHelper {

    private static Logger logger = LogManager
            .getLogger(FileOperationHelper.class.getName());

    private Map<TaskType, String> _fileLocationMap;
    private String _completedSuffix;
    private String _fileExtension;

    FileOperationHelper(String completedSuffix, String fileExtension) {
        _fileLocationMap = new HashMap<TaskType, String>();
        _fileLocationMap.put(TaskType.TODO,
                ConfigHelper.getProperty(TaskType.TODO.getType()));
        _fileLocationMap.put(TaskType.EVENT,
                ConfigHelper.getProperty(TaskType.EVENT.getType()));
        _fileLocationMap.put(TaskType.DEADLINE,
                ConfigHelper.getProperty(TaskType.DEADLINE.getType()));
        _completedSuffix = completedSuffix;
        _fileExtension = fileExtension;
    }

    FileOperationHelper(String completedSuffix, String fileExtension,
            Map<TaskType, String> fileLocationMap) {
        _fileLocationMap = fileLocationMap;
        _completedSuffix = completedSuffix;
        _fileExtension = fileExtension;
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
            logger.error("failed to create file.");
            logger.error(e, e);
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
