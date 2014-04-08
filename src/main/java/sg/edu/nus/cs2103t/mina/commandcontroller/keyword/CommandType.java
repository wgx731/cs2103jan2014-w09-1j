package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

public enum CommandType {
    
    UNDO("undo"),
    REDO("redo"),
    EXIT("exit"),
    
    SEARCH("search"),
    DISPLAY("display"),
    DELETE("delete"),
    ADD("add"),
    MODIFY("modify"),
    COMPLETE("complete"),
    UNCOMPLETE("uncomplete"),
    INVALID("invalid");
    
    private String _cmd;
    
    private CommandType(String cmd) {
        _cmd = cmd;
    }
    public String getType() {
        return _cmd;
    }
}
