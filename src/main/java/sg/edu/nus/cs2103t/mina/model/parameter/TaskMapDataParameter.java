package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;

public class TaskMapDataParameter {

    private HashMap<String, ArrayList<Task<?>>> _recurringTasks;
    private HashMap<String, ArrayList<EventTask>> _blockTasks;
    private int _maxRecurTagInt;
    private int _maxBlockTagInt;

    public TaskMapDataParameter(
            HashMap<String, ArrayList<Task<?>>> recurringTasks,
            HashMap<String, ArrayList<EventTask>> blockTasks,
            int maxRecurTagInt, int maxBlockTagInt) {
        super();
        _recurringTasks = recurringTasks;
        _blockTasks = blockTasks;
        _maxRecurTagInt = maxRecurTagInt;
        _maxBlockTagInt = maxBlockTagInt;
    }

    public HashMap<String, ArrayList<Task<?>>> getRecurringTasks() {
        return _recurringTasks;
    }

    public void setRecurringTasks(
            HashMap<String, ArrayList<Task<?>>> recurringTasks) {
        _recurringTasks = recurringTasks;
    }

    public HashMap<String, ArrayList<EventTask>> getBlockTasks() {
        return _blockTasks;
    }

    public void setBlockTasks(HashMap<String, ArrayList<EventTask>> blockTasks) {
        _blockTasks = blockTasks;
    }

    public int getMaxRecurTagInt() {
        return _maxRecurTagInt;
    }

    public void setMaxRecurTagInt(int maxRecurTagInt) {
        _maxRecurTagInt = maxRecurTagInt;
    }

    public int getMaxBlockTagInt() {
        return _maxBlockTagInt;
    }

    public void setMaxBlockTagInt(int maxBlockTagInt) {
        _maxBlockTagInt = maxBlockTagInt;
    }

}
