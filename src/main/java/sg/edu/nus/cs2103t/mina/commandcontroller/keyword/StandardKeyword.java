package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

/**
 * This class is for command keywords. Keywords are special words that signal
 * MINA that it needs special processing
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

/**
 * This enum contains standard keywords that CommandProcessor recognized
 */
public enum StandardKeyword {
    
    //Add your standard keywords here
    ACTION("action"), 
    TASKID("taskid"), 
    TO_TASK_TYPE("totype"), 
    DESCRIPTION("description"), 
    START("start"), 
    END("end"), 
    RECURRING("every"), 
    UNTIL("until"), 
    PRIORITY("priority");
    
    public static final String DELIMITER = "-";
    private String _keyword;

    private StandardKeyword(String keyword) {
        _keyword = keyword;
    }

    public String getKeyword() {
        return _keyword;
    }

    public String getFormattedKeyword() {
        return DELIMITER + _keyword;
    }
    
    public static String getFormattedKeyword(String keyword) {
        return DELIMITER + keyword;
    }
}
