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
        
        private String _value;
        
        private StandardKeyword(String value) {
            _value = value;
        }
        
        public String getValue() {
            return _value;
        }
        
        public String getFormattedValue(){
            return "-" + _value;
        }
        
    }
    
    
    
}
