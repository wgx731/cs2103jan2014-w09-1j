package sg.edu.nus.cs2103t.mina.controller;

import java.util.Map;
import java.util.SortedSet;

import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

public class FileTaskDaoImplStub extends FileTaskDaoImpl {
    

    public FileTaskDaoImplStub(Map<TaskType, String> fileLocationMap) {
        super(fileLocationMap);
        // TODO Auto-generated constructor stub
    }

    public void saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {

    }

    public SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) {

        return null;
    }

}
