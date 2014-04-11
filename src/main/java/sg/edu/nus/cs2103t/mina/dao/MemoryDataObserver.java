package sg.edu.nus.cs2103t.mina.dao;

import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;

/**
 * Observe memory data (task set and task map) changes in MINA
 * 
 * @author wgx731
 */
//@author A0105853H
public interface MemoryDataObserver {

    /**
     * notify observer task set change
     * 
     * @param changedTaskSetData changed task set data
     */
    public void updateTaskSet(TaskSetDataParameter changedTaskSetData);

    /**
     * notify observer task map change
     * 
     * @param changedTaskMapData changed task map data
     */
    public void updateTaskMap(TaskMapDataParameter changedTaskMapData);

}
