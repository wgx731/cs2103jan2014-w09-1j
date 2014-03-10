package sg.edu.nus.cs2103t.mina.model;

/**
 * UI types for MINA
 * 
 * @author wgx731
 */
public enum UIType {

    GUI("deadline"), CONSOLE("todo");

    private String _type;

    private UIType(String type) {
        _type = type;
    }

    public String getType() {
        return _type;
    }
}
