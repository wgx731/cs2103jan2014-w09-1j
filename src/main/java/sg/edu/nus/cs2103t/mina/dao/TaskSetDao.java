package sg.edu.nus.cs2103t.mina.dao;

import java.io.IOException;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * TaskSetDAO for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public interface TaskSetDao {

    /**
     * Save task set into storage
     * 
     * @param taskSet given task to be saved
     * @param taskType the task type to be saved
     * @throws IOException
     */
    public void saveTaskSet(TreeSet<? extends Task<?>> taskSet,
            TaskType taskType) throws IOException, IllegalArgumentException;

    /**
     * load task set into memory from storage
     * 
     * @param taskType the task type to be loaded
     * @return loaded task set
     * @throws IOException
     */
    public TreeSet<? extends Task<?>> loadTaskSet(TaskType taskType)
            throws IOException, IllegalArgumentException;
}
