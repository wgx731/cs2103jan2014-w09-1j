package sg.edu.nus.cs2103t.mina.model.parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.nus.cs2103t.mina.model.Task;

public class TaskMapDataParameter implements Serializable {

    private static final long serialVersionUID = -1665500740615897037L;

    private HashMap<String, ArrayList<Task<?>>> _recurringTasks;
    private int _maxRecurTagInt;

    public TaskMapDataParameter(
            HashMap<String, ArrayList<Task<?>>> recurringTasks,
            int maxRecurTagInt) {
        super();
        _recurringTasks = recurringTasks;
        _maxRecurTagInt = maxRecurTagInt;
    }

    public HashMap<String, ArrayList<Task<?>>> getRecurringTasks() {
        return _recurringTasks;
    }

    public void setRecurringTasks(
            HashMap<String, ArrayList<Task<?>>> recurringTasks) {
        _recurringTasks = recurringTasks;
    }

    public int getMaxRecurTagInt() {
        return _maxRecurTagInt;
    }

    public void setMaxRecurTagInt(int maxRecurTagInt) {
        _maxRecurTagInt = maxRecurTagInt;
    }

}
