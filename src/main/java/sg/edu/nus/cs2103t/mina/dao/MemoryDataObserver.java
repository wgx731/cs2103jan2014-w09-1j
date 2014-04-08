package sg.edu.nus.cs2103t.mina.dao;

import sg.edu.nus.cs2103t.mina.model.parameter.TaskMapDataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.TaskSetDataParameter;

/**
 * Observe memory data changes in MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
// @author A0105853H
public interface MemoryDataObserver {

    /**
     * notify observer task set memory data change
     * 
     * @param changedTaskSetData changed taskset data
     */
    public void updateTaskSet(TaskSetDataParameter changedTaskSetData);

    /**
     * notify observer task map memory data change
     * 
     * @param changedTaskMapData changed taskmap data
     */
    public void updateTaskMap(TaskMapDataParameter changedTaskMapData);

}
