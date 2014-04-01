package sg.edu.nus.cs2103t.mina.dao;

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
     * update state based on memory data change signal
     * 
     * @param syncData changed data signal
     */
    public void updateChange(TaskSetDataParameter syncData);

}
