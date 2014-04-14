package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

/**
 * This enum contains standard keywords that CommandProcessor recognized 
 * (with the exception of COMPOSITE (that's only for CommandParser) )
 */
//@author A0099151B

public enum SimpleKeyword implements StandardKeyword{
    
    //Add your standard keywords here
    TASKID("TaskId"), 
    TO_TASK_TYPE("ToType"), 
    DESCRIPTION("Description"), 
    START("Start"), 
    END("End"), 
    RECURRING("Every"), 
    UNTIL("Until"), 
    PRIORITY("Priority"),
    ALL("All");
    
    private String _keyword;

    private SimpleKeyword(String keyword) {
        _keyword = keyword;
    }

    public String getKeyword() {
        return _keyword.toLowerCase();
    }

    public String getFilePrefix() {
        return _keyword;
    }
    
    public String getFormattedKeyword() {
        return DELIMITER + _keyword.toLowerCase();
    }
    
}
