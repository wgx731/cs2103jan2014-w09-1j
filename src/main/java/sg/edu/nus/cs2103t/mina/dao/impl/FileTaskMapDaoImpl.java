package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;

public class FileTaskMapDaoImpl implements TaskMapDao {

    private static Logger logger = LogManager
            .getLogger(FileTaskMapDaoImpl.class.getName());

    private static final String FILE_EXTENSION = ".ser";
    private static final String TASK_MAP_KEY = "taskmap";;

    private String taskMapFile;

    public FileTaskMapDaoImpl() {
        taskMapFile = ConfigHelper.getProperty(TASK_MAP_KEY) + FILE_EXTENSION;
    }

    private void createFileIfNotExist(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                logger.error("storage file is not created.");
                logger.error(e, e);
            }
        }
    }

    private ObjectOutput getOutputWriter() throws IOException {
        OutputStream file;
        createFileIfNotExist(taskMapFile);
        file = new FileOutputStream(taskMapFile);
        OutputStream buffer = new BufferedOutputStream(file);
        return new ObjectOutputStream(buffer);
    }

    private ObjectInput getInputReader() throws IOException {
        InputStream file;
        createFileIfNotExist(taskMapFile);
        file = new FileInputStream(taskMapFile);
        InputStream buffer = new BufferedInputStream(file);
        return new ObjectInputStream(buffer);
    }

    @Override
    public void saveTaskMapData(TaskMapDataParameter taskMapData)
            throws IOException {
        ObjectOutput output = getOutputWriter();
        output.writeObject(taskMapData);
        output.close();
    }

    @Override
    public TaskMapDataParameter loadTaskMapData() {
        try {
            ObjectInput input = getInputReader();
            Object object = input.readObject();
            TaskMapDataParameter taskMapData = (TaskMapDataParameter) object;
            input.close();
            return taskMapData;
        } catch (ClassNotFoundException e) {
            logger.error("task class not found.");
            logger.error(e, e);
            return null;
        } catch (IOException e) {
            logger.error("empty file.");
            logger.error(e, e);
            return null;
        }
    }

}
