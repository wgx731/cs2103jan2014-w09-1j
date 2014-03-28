package sg.edu.nus.cs2103t.mina.stub;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

/**
 * File Task Dao stub
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class FileTaskSetDaoStub extends JsonFileTaskDaoImpl {

    public FileTaskSetDaoStub() {

    }

    @Override
    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) throws IOException {
        return;
    }

    @Override
    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException {
        switch (taskType) {
            case TODO :
                return new TreeSet<TodoTask>();
            case DEADLINE :
                return new TreeSet<DeadlineTask>();
            case EVENT :
                return new TreeSet<EventTask>();
            default :
                return null;
        }
    }

}
