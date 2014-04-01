package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.IOException;

import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;

public class FileTaskMapDaoImpl implements TaskMapDao {

    private static final String FILE_EXTENSION = ".ser";
    private static final String TASK_MAP_KEY = "taskmap";
    private static final String FILE_ERROR = "storage file is corrupted.";

    private String taskMapFile;

    public FileTaskMapDaoImpl() {
        taskMapFile = ConfigHelper.getProperty(TASK_MAP_KEY) + FILE_EXTENSION;
    }

    @Override
    public void saveTaskMapData(TaskMapDataParameter taskMapData)
            throws IOException {
    }

    @Override
    public TaskMapDataParameter loadRecurringTaskMap() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
