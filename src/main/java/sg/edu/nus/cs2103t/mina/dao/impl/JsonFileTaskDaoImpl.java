package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.JsonHelper;

/**
 * 
 * File based implementation for TaskSetDao
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0105853H
public class JsonFileTaskDaoImpl implements TaskDao {

    private static final String COMPLETED_SUFFIX = "-compl";
    private static final String FILE_EXTENSION = ".json";
    private static final String EMPTY_FILE_ERROR = "can't load storage file content.";

    public static String getCompletedSuffix() {
        return COMPLETED_SUFFIX;
    }

    public static String getFileExtension() {
        return FILE_EXTENSION;
    }

    private FileOperationHelper _fileOperationHelper;

    /**
     * Create a new json file task dao instance and initialize required files
     */
    public JsonFileTaskDaoImpl() {
        _fileOperationHelper = new FileOperationHelper(COMPLETED_SUFFIX,
                FILE_EXTENSION);
        _fileOperationHelper.createFileStorage();
    }

    JsonFileTaskDaoImpl(Map<TaskType, String> storageMap) {
        _fileOperationHelper = new FileOperationHelper(COMPLETED_SUFFIX,
                FILE_EXTENSION, storageMap);
        _fileOperationHelper.createFileStorage();
    }

    private BufferedWriter getOutputWriter(String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        return new BufferedWriter(fileWriter);
    }

    private BufferedReader getInputReader(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        return new BufferedReader(fileReader);
    }

    @Override
    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) throws IOException {
        assert taskType != TaskType.UNKNOWN;
        assert taskSet != null;
        if (!taskSet.isEmpty()) {
            assert taskSet.first().getType() == taskType;
        }
        String json = JsonHelper.taskSetToJson(taskSet, taskType);
        BufferedWriter writer = getOutputWriter(_fileOperationHelper
                .getFileLocation(taskType, isCompleted));
        writer.write(json);
        writer.close();
    }

    @Override
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        assert taskType != TaskType.UNKNOWN;
        BufferedReader reader = getInputReader(_fileOperationHelper
                .getFileLocation(taskType, isCompleted));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        SortedSet<? extends Task<?>> taskSet = JsonHelper.jsonToTaskSet(
                sb.toString(), taskType);
        if (taskSet == null) {
            throw new IOException(EMPTY_FILE_ERROR);
        }
        return taskSet;
    }

}
