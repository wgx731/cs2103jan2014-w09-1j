package sg.edu.nus.cs2103t.mina.dao;

import java.io.IOException;

import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;

/**
 * Task map Data Access Object (DAO) for MINA
 */
//@author A0105853H
public interface TaskMapDao {

    /**
     * Save task map data into storage
     * 
     * @param recurringTaskMap given task to be saved
     * @throws IOException if save operation failed
     */
    public void saveTaskMap(TaskMapDataParameter taskMapData)
            throws IOException;

    /**
     * Load task map from storage
     * 
     * @return loaded task map, if the file has been modified or changed, return
     * null
     */
    public TaskMapDataParameter loadTaskMap();

}
