package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
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
public class JsonFileTaskDaoImpl implements TaskDao {

    private static final String COMPLETED_SUFFIX = "-compl";
    private static final String FILE_EXTENSION = ".json";

    static String getCompletedSuffix() {
        return COMPLETED_SUFFIX;
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
        String json = JsonHelper.taskSetToJson(taskSet);
        BufferedWriter writer = getOutputWriter(_fileOperationHelper
                .getFileLocation(taskType, isCompleted));
        writer.write(json);
        writer.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        BufferedReader reader = getInputReader(_fileOperationHelper
                .getFileLocation(taskType, isCompleted));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        SortedSet<? extends Task<?>> loadObject = JsonHelper.jsonToTaskSet(sb
                .toString());
        if (loadObject == null) {
            throw new IOException();
        }
        // SortedSet<? extends Task<?>> taskSet = null;
        // switch (taskType) {
        // case TODO:
        // taskSet = (SortedSet<TodoTask>) loadObject;
        // break;
        // case DEADLINE:
        // taskSet = (SortedSet<DeadlineTask>) loadObject;
        // break;
        // case EVENT:
        // taskSet = (SortedSet<EventTask>) loadObject;
        // break;
        // default:
        // break;
        // }
        return loadObject;
    }

}
