package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * 
 * File based implementation for TaskSetDao
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class FileTaskDaoImpl implements TaskDao {

    private static Logger logger = LogManager.getLogger(FileTaskDaoImpl.class
            .getName());

    private static final String INVALID_TASK_TYPE = "The given task type is invalid.";
    private static final String INVALID_FILE_STORAGE = "The given file path is an invalid file storage.";
    private static final String COMPLETED_SUFFIX = ".compl";

    public static String getCompletedSuffix() {
        return COMPLETED_SUFFIX;
    }

    private Map<TaskType, String> _fileLocationMap;

    /**
     * Create a new file based task set storage with all required file names
     * 
     * @param fileLocationMap map of all the supported taskType with file path
     */
    public FileTaskDaoImpl(Map<TaskType, String> fileLocationMap) {
        super();
        _fileLocationMap = fileLocationMap;
        for (String fileLocation : fileLocationMap.values()) {
            try {
                createFileIfNotExist(fileLocation);
            } catch (IOException e) {
                logger.error(e, e);
            }
        }
    }

    private String getFileLocation(TaskType taskType, boolean isCompleted) {
        String fileLocation = _fileLocationMap.get(taskType);
        if (isCompleted) {
            fileLocation += COMPLETED_SUFFIX;
        }
        return fileLocation;
    }

    private void createFileIfNotExist(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
    }

    private ObjectOutput getOutputWriter(String fileName) {
        OutputStream file;
        try {
            file = new FileOutputStream(fileName);
            OutputStream buffer = new BufferedOutputStream(file);
            return new ObjectOutputStream(buffer);
        } catch (FileNotFoundException e) {
            logger.error(e, e);
            return null;
        } catch (IOException e) {
            logger.error(e, e);
            return null;
        }
    }

    private ObjectInput getInputReader(String fileName) {
        InputStream file;
        try {
            file = new FileInputStream(fileName);
            InputStream buffer = new BufferedInputStream(file);
            return new ObjectInputStream(buffer);
        } catch (FileNotFoundException e) {
            logger.error(e, e);
            return null;
        } catch (IOException e) {
            logger.error(e, e);
            return null;
        }
    }

    @Override
    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) throws IOException,
            IllegalArgumentException {
        if (!_fileLocationMap.containsKey(taskType)) {
            throw new IllegalArgumentException(INVALID_TASK_TYPE);
        }
        String fileLocation = getFileLocation(taskType, isCompleted);
        ObjectOutput output = getOutputWriter(fileLocation);
        output.writeObject(taskSet);
        output.close();
    }

    @Override
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException, IllegalArgumentException {
        if (!_fileLocationMap.containsKey(taskType)) {
            throw new IllegalArgumentException(INVALID_TASK_TYPE);
        }
        String fileLocation = getFileLocation(taskType, isCompleted);
        File f = new File(fileLocation);
        if (!f.exists() || !f.isFile()) {
            throw new IOException(INVALID_FILE_STORAGE);
        }
        ObjectInput input = getInputReader(fileLocation);
        try {
            Object object = input.readObject();
            if (object == null) {
                return null;
            }
            @SuppressWarnings("unchecked")
            SortedSet<? extends Task<?>> content = (SortedSet<? extends Task<?>>) object;
            input.close();
            return content;
        } catch (ClassNotFoundException e) {
            logger.error(e, e);
            return null;
        } catch (NullPointerException e) {
            logger.error(e, e);
            return null;
        }
    }

}
