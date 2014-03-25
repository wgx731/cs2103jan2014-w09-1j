package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
 * Implementation of TaskDao which saves data as Serialized Object File
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0105853H
public class ObjectFileTaskDaoImpl implements TaskDao {

    private static Logger logger = LogManager
            .getLogger(ObjectFileTaskDaoImpl.class.getName());

    private static final String FILE_NOT_FOUND_ERROR = "The file %1$s is not found.";
    private static final String CLASS_NOT_FOUND_ERROR = "Required task class is missing.";
    private static final String NULL_TASK_SET_ERROR = "File content is null.";
    private static final String COMPLETED_SUFFIX = "-compl";
    private static final String FILE_EXTENSION = ".ser";

    public static String getCompletedSuffix() {
        return COMPLETED_SUFFIX;
    }

    public static String getFileExtension() {
        return FILE_EXTENSION;
    }

    private FileOperationHelper _fileOperationHelper;

    /**
     * Create a new object file task dao instance and initialize required files
     */
    public ObjectFileTaskDaoImpl() {
        _fileOperationHelper = new FileOperationHelper(COMPLETED_SUFFIX,
                FILE_EXTENSION);
        _fileOperationHelper.createFileStorage();
    }

    ObjectFileTaskDaoImpl(Map<TaskType, String> fileLocationMap) {
        _fileOperationHelper = new FileOperationHelper(COMPLETED_SUFFIX,
                FILE_EXTENSION, fileLocationMap);
        _fileOperationHelper.createFileStorage();
    }

    private ObjectOutput getOutputWriter(String fileName) throws IOException {
        OutputStream file;
        try {
            file = new FileOutputStream(fileName);
            OutputStream buffer = new BufferedOutputStream(file);
            return new ObjectOutputStream(buffer);
        } catch (FileNotFoundException e) {
            logger.error("storage file is not created.");
            logger.error(e, e);
            throw new IOException(String.format(FILE_NOT_FOUND_ERROR, fileName));
        }
    }

    private ObjectInput getInputReader(String fileName) throws IOException {
        InputStream file;
        try {
            file = new FileInputStream(fileName);

            InputStream buffer = new BufferedInputStream(file);
            return new ObjectInputStream(buffer);
        } catch (FileNotFoundException e) {
            logger.error("storage file is not created.");
            logger.error(e, e);
            throw new IOException(String.format(FILE_NOT_FOUND_ERROR, fileName));
        }
    }

    @Override
    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) throws IOException {
        assert taskType != TaskType.UNKOWN;
        assert taskSet != null;
        if (!taskSet.isEmpty()) {
            assert taskSet.first().getType() == taskType;
        }
        String fileLocation = _fileOperationHelper.getFileLocation(taskType,
                isCompleted);
        ObjectOutput output = getOutputWriter(fileLocation);
        output.writeObject(taskSet);
        output.close();
    }

    @Override
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        assert taskType != TaskType.UNKOWN;
        String fileLocation = _fileOperationHelper.getFileLocation(taskType,
                isCompleted);
        ObjectInput input = getInputReader(fileLocation);
        try {
            Object object = input.readObject();
            if (object == null) {
                throw new IOException(NULL_TASK_SET_ERROR);
            }
            @SuppressWarnings("unchecked")
            SortedSet<? extends Task<?>> content = (SortedSet<? extends Task<?>>) object;
            input.close();
            return content;
        } catch (ClassNotFoundException e) {
            logger.error("task class not found.");
            logger.error(e, e);
            throw new IOException(CLASS_NOT_FOUND_ERROR);
        }
    }

}
