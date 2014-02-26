package sg.edu.nus.cs2103t.mina.controller;

/**
 * This class is in charged to filtering tasks based on certain
 * criteria
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

import java.util.ArrayList;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;

public class TaskFilterManager {

    private TaskDataManagerStub _taskStore;

    public TaskFilterManager(TaskDataManagerStub taskStore) {
        _taskStore = taskStore;
    }

    /**
     * Filter the tasks based on its critieria
     * 
     * @param param a FilterParameter object that represents the criteria
     * @return An arraylist of tasks that satisfied the task. Empty if there's
     * none
     */
    public ArrayList<Task> filterTask(FilterParameter param)
            throws NullPointerException {
        // GuardClause
        if (param == null) {
            throw new NullPointerException();
        }

        // TODO no filter

        return null;
    }

    /**
     * Search for tasks based on its keywords.
     * 
     * @param param a SearchParameter object that represents the keywords used
     * @return An arraylist of task that satisfied the keywords. Empty if
     * there's none.
     */
    public ArrayList<Task> searchTasks(SearchParameter param) {
        return null;
    }

}
