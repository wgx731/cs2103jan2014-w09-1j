package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Task map Dao implementation
 */
//@author A0105853H

public class FileTaskMapDaoImpl implements TaskMapDao {

    private static final String CLASS_NAME = FileTaskMapDaoImpl.class.getName();

    private static final String FILE_EXTENSION = ".ser";

    public static String getFileExtension() {
        return FILE_EXTENSION;
    }

    private FileOperationHelper _fileOperationHelper;

    public FileTaskMapDaoImpl(FileOperationHelper fileOperationHelper) {
        _fileOperationHelper = fileOperationHelper;
        _fileOperationHelper.createTaskMapDaoFiles();
    }

    FileTaskMapDaoImpl() {
        _fileOperationHelper = new FileOperationHelper();
        _fileOperationHelper.createTaskMapDaoFiles();
    }
    
    FileOperationHelper getFileOperationHelper() {
        return _fileOperationHelper;
    }

    private ObjectOutput getOutputWriter() throws IOException {
        OutputStream file;
        file = new FileOutputStream(
                _fileOperationHelper.getTaskMapFileLocation());
        OutputStream buffer = new BufferedOutputStream(file);
        return new ObjectOutputStream(buffer);
    }

    private ObjectInput getInputReader() throws IOException {
        InputStream file;
        file = new FileInputStream(
                _fileOperationHelper.getTaskMapFileLocation());
        InputStream buffer = new BufferedInputStream(file);
        return new ObjectInputStream(buffer);
    }

    @Override
    public void saveTaskMap(TaskMapDataParameter taskMapData)
            throws IOException {
        ObjectOutput output = getOutputWriter();
        output.writeObject(taskMapData);
        output.close();
    }

    @Override
    public TaskMapDataParameter loadTaskMap() {
        try {
            ObjectInput input = getInputReader();
            Object object = input.readObject();
            TaskMapDataParameter taskMapData = (TaskMapDataParameter) object;
            input.close();
            return taskMapData;
        } catch (ClassNotFoundException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "task class not found: " + e.getMessage());
            return null;
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR,
                    "empty file: " + e.getMessage());
            return null;
        }
    }

}
