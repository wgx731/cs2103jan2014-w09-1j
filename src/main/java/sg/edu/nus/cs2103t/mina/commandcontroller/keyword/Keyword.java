package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Keyword {
    
    private StandardKeyword _type;
    private HashMap<String, StandardKeyword> _aliasMap;
    
    protected HashSet<String> _alias;
    protected String _value;
    protected static final int LOOK_AHEAD_LIMIT = 5;
    
    public Keyword(StandardKeyword type) {
        _type = type;
        _alias = new HashSet<String>();
        _value = "";
        initAlias();
        initValues();
    }
    
    public StandardKeyword getType() {
        return _type;
    }
    
    public String getValue() {
        return _value;
    }
    
    protected void setValue(String value) {
        _value = value;
    }
    
    public void clearValue() {
        setValue("");
    }
    
    public HashMap<String, StandardKeyword> getAlias() {
        
        //If the alias map has already been generated, don't bother
        if(_aliasMap!=null) {
            return _aliasMap;
        }
        
        _aliasMap = generateAlias();
        return _aliasMap;
    }

    private HashMap<String, StandardKeyword> generateAlias() {
        
        Iterator<String> aliasIterator = _alias.iterator();
        HashMap<String, StandardKeyword> aliasMap = new HashMap<String, StandardKeyword>();
        
        while (aliasIterator.hasNext()) {
            aliasMap.put(aliasIterator.next(), _type);
        }
        
        return aliasMap;
    }
    
    protected void setAlias(HashSet<String> alias) {
        _alias = alias;
    }
    
    public boolean containsAlias(String alias) {
        if(_alias==null) {
            return false;
        }
        return _alias.contains(alias);
    }
    
    /**
     * Add new aliases. This method would add both delimiter and non-delimiter as well
     * @param keyword the new alias
     */
    protected void addAlias(String keyword) {
        
        assert(_alias!=null);
        
        String formatedKeyword = StandardKeyword.getFormattedKeyword(keyword);
        _alias.add(keyword);
        _alias.add(formatedKeyword);
    }
    
    //Functions that need to be implemented
    protected abstract void initAlias();
    protected abstract void initValues();
    public abstract ArrayList<String> processKeyword(ArrayList<String> tokens, int currIndex) throws ParseException;
}
 