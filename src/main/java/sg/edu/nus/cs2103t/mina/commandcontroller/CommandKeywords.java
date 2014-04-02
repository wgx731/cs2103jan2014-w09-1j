package sg.edu.nus.cs2103t.mina.commandcontroller;

/**
 * This class is for command keywords. Keywords are special words that signal MINA that it needs special processing
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public class CommandKeywords {
    
    /**
     * This enum contains basic keywords t
     */
    public enum StandardKeyword{
        ACTION("action"),
        TASKID("taskid"),
        TO_TASK_TYPE("totype"),
        DESCRIPTION("description"),
        START("start"),
        END("end"),
        RECURRING("every"),
        UNTIL("until"),
        PRIORITY("priority");
        
        private String _keyword;
        
        private StandardKeyword(String keyword) {
            _keyword = keyword;
        }
        
        public String getKeyword() {
            return _keyword;
        }
        
        public String getFormattedKeyword(){
            return "-" + _keyword;
        }
        
    }
    
    
    
}
