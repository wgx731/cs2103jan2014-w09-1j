package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Action keywords and their process. Actions are also known as Command types.
 * 
 * Actions are special keywords because they are are values themselves. Each action 
 * is recognized by the CommandProcessor, however each action can have their own alias as well.
 * 
 * Thus, aliases are mapped to their respective actions which represent as values by themselves.
 * 
 * For example, 'add' is the official command that CommandProcessor understands as 'adding a task', however
 * 'create', 'new', etc are considered as add as well, thus, they are mapped to 'add' which by itself is the 
 * value
 * 
 * If an action invokes a special requirement, like 'modify' requiring task ids, we recommend you to add an enum
 * in this class and include any Actions (the enum) that require this kind of special attention. This is not only
 * for documentation's sake but also 
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 * 
 */

public class ActionKeyword extends Keyword{
    
    public enum Action{
        UNDO("undo"),
        REDO("redo"),
        EXIT("exit"),
        
        SEARCH("search"),
        DISPLAY("display"),
        DELETE("delete"),
        ADD("add"),
        MODIFY("modify"),
        COMPLETE("complete");
        
        
        private String _action;
        
        private Action(String action) {
            _action = action;
        }
        public String getAction() {
            return _action;
        }
    }
    
    private HashMap<String, Action> _actionValues; 
    private HashMap<String, Action> _singleActionValues; 
    
    public ActionKeyword() {
        super(StandardKeyword.ACTION);
    }

    @Override
    protected void initAlias() {
        return;
    }

    @Override
    protected void initValues() {
        
        _actionValues = new HashMap<String, Action>();
        _singleActionValues = new HashMap<String, Action>();
        
        _actionValues.put(Action.ADD.getAction(), Action.ADD);
        _actionValues.put("make", Action.ADD);
        _actionValues.put("create", Action.ADD);
        _actionValues.put("new", Action.ADD);
        _actionValues.put("+", Action.ADD);
        
        _actionValues.put(Action.MODIFY.getAction(), Action.MODIFY);
        _actionValues.put("change", Action.MODIFY);
        _actionValues.put("edit", Action.MODIFY);
        
        _actionValues.put("remove", Action.DELETE);
        _actionValues.put("rm", Action.DELETE);
        _actionValues.put("-", Action.DELETE);
        _actionValues.put(Action.DELETE.getAction(), Action.DELETE);
        
        _actionValues.put(Action.SEARCH.getAction(), Action.SEARCH);
        _actionValues.put("find", Action.SEARCH);
        
        _actionValues.put(Action.DISPLAY.getAction(), Action.DISPLAY);
        _actionValues.put("filter", Action.DISPLAY);
        _actionValues.put("show", Action.DISPLAY);
        
        _actionValues.put(Action.COMPLETE.getAction(), Action.COMPLETE);
        _actionValues.put("finish", Action.COMPLETE);
        
        _singleActionValues.put(Action.EXIT.getAction(), Action.EXIT);
        _singleActionValues.put("quit", Action.EXIT);
        
        _singleActionValues.put(Action.UNDO.getAction(), Action.UNDO);
        _singleActionValues.put(Action.REDO.getAction(), Action.REDO);
        
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex) throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
