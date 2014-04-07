package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.util.LinkedHashMap;
import java.util.Set;

public class Argument {
    
    private LinkedHashMap<StandardKeyword, String> _arguments;
    //private static final Logger logger = LogManager.getLogger(Argument.class.getName());
    
    public Argument(){
        initKeywordsMap();
    }
    
    private void initKeywordsMap() {
        _arguments = new LinkedHashMap<StandardKeyword, String>();
        for (SimpleKeyword type: SimpleKeyword.values()) {
            _arguments.put(type, null);
        }
    }
    
    public boolean hasValue(StandardKeyword key) {
        return _arguments.get(key) != null;
    }
    
    public void setKeywordValue(StandardKeyword key, String value) {
        value = value.trim() + " ";
        _arguments.put(key, value);
    }
    
    public String getKeywordValue(StandardKeyword key) {
        return _arguments.get(key);
    }
    
    public Set<StandardKeyword> getArgumentKeys() {
        return _arguments.keySet();
    }
     
    public String getKeywordValueView() {
        
        StringBuilder commandLog = new StringBuilder();
        
        for (StandardKeyword keyword: _arguments.keySet()) {
            String entry = keyword + ": " + getKeywordValue(keyword) + " \n";
            commandLog.append(entry);
        }
        
        return commandLog.toString();
    }
    
}
