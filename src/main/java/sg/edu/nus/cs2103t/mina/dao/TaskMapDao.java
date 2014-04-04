package sg.edu.nus.cs2103t.mina.dao;

import java.io.IOException;

import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;

/**
 * @author wgx731
 * 
 */
public interface TaskMapDao {

    /**
     * Save task map data into storage
     * 
     * @param recurringTaskMap given task to be saved
     * @throws IOException
     */
    public void saveTaskMapData(TaskMapDataParameter taskMapData)
            throws IOException;

    /**
     * Load task map from storage
     * 
     * @throws IOException
     */
    public TaskMapDataParameter loadTaskMapData();

}
