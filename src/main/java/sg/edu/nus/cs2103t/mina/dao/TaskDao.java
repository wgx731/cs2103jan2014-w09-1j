package sg.edu.nus.cs2103t.mina.dao;

import java.io.IOException;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * Task set Data Access Object (DAO) for MINA
 */
//@author A0105853H
public interface TaskDao {

    /**
     * Save task set into storage
     * 
     * @param taskSet given task to be saved
     * @param taskType the task type to be saved
     * @param isCompleted whether the task in the set is completed
     * @throws IOException if save operation failed
     */
    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) throws IOException;

    /**
     * load task set from storage
     * 
     * @param taskType the task type to be loaded
     * @param isCompleted whether the task in the set is completed
     * @return loaded task set
     * @throws IOException if load operation failed
     */
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException;

}
