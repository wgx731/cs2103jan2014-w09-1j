package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeywordFactory {

    private static KeywordFactory _keywordFactory;
    private static LinkedHashMap<String, Keyword>  _keywordAliasMap = new LinkedHashMap<String, Keyword>();
    
    private static final Logger logger = LogManager.getLogger(KeywordFactory.class.getName());
    
    private static final String CLASS_NAME = "sg.edu.nus.cs2103t.mina.commandcontroller.keyword.%1$sKeyword";
    private static final String ERR_CLASS_NOT_FOUND = "Cannot find class: %1$s";

    private KeywordFactory()  {
        
        initKeywordClasses(SimpleKeyword.values());
        initKeywordClasses(CompositeKeyword.values());
                
    }
    
    private void initKeywordClasses(StandardKeyword[] values) {
        for (StandardKeyword type : values) {
            String className = String.format(CLASS_NAME, type.getFilePrefix());
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                String classNotFoundErr = String.format(ERR_CLASS_NOT_FOUND, className);
                logger.error(classNotFoundErr);
            }
        }
        
    }

    public static KeywordFactory getInstance() {
        if (_keywordFactory == null) {
            _keywordFactory = new KeywordFactory();
            return _keywordFactory;
        } else {
            return _keywordFactory;
        }
    }

    /**
     * Add a standard keyword entry into the KeywordFactory. Note: this method
     * should be invoked only under static initializer of keyword
     * 
     * @param type StandardKeyword enum. It must be found there.
     * @param keyword The Keyword object created. This should be a prototype.
     */
    protected static void addAliasEntry(String alias, Keyword keyword) {
        assert (alias != null && keyword != null);
        logger.info("Adding " + alias + " and " + keyword);
        _keywordAliasMap.put(alias, keyword);
        logger.info("Done");
    }
    
    /**
     * Create the keyword object based on the alias given
     * 
     * @param keyword the alias found e
     * @return The specific keyword object
     */
    public Keyword createKeyword(String alias) throws ParseException{
        logger.info(alias + " " + _keywordAliasMap.toString());
        Keyword prototype = _keywordAliasMap.get(alias);
        if(prototype==null) {
            throw new ParseException("No such alias/keyword", 0);
        }
        return prototype.createKeyword();
    }
    
    public boolean isKeyword(String token) {
        return _keywordAliasMap.containsKey(token);
    }
    
}
