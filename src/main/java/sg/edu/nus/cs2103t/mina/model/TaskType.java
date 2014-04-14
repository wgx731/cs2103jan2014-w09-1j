package sg.edu.nus.cs2103t.mina.model;

/**
 * Available task types
 */
//@author A0080412W

public enum TaskType {

    TODO("todo"), EVENT("event"), DEADLINE("deadline"), UNKNOWN("unknown");

    private String _type;

    private TaskType(String type) {
        _type = type;
    }

    public String getType() {
        return _type;
    }

}
