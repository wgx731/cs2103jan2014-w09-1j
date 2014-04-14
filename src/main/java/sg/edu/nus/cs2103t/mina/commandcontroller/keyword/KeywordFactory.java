package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.utils.LogHelper;

//@author A0099151B
public class KeywordFactory {

    private static LinkedHashMap<String, Keyword> _keywordAliasMap = new LinkedHashMap<String, Keyword>();

    private static final String CLASS_NAME = KeywordFactory.class.getName();

    private static final String KEYWORD_CLASS_NAME_TEMPLATE = "sg.edu.nus.cs2103t.mina.commandcontroller.keyword.%1$sKeyword";
    private static final String ERR_CLASS_NOT_FOUND = "Cannot find class: %1$s";

    /**
     * Initialise the KeywordFactory. This method will create the
     * alias-to-Keyword mapping.
     */
    public static void initialiseKeywordFactory() {
        // Update KeywordFactoryTest's invokeInitClasses() method accordingly if
        // you're going to add new entries here
        try {
            initKeywordClasses(SimpleKeyword.values());
            initKeywordClasses(CompositeKeyword.values());
        } catch (ClassNotFoundException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }
    }

    private static void initKeywordClasses(StandardKeyword[] values)
            throws ClassNotFoundException {
        for (StandardKeyword type : values) {
            String className = String.format(KEYWORD_CLASS_NAME_TEMPLATE,
                    type.getFilePrefix());

            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                String classNotFoundErr = String.format(ERR_CLASS_NOT_FOUND,
                        className);
                throw new ClassNotFoundException(classNotFoundErr);
            }
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

        LogHelper.log(CLASS_NAME, Level.INFO, "Adding " + alias +
                " and " +
                keyword);
        _keywordAliasMap.put(alias, keyword);
        LogHelper.log(CLASS_NAME, Level.INFO, "Done");
    }

    /**
     * Create the keyword object based on the alias given
     * 
     * @param keyword the alias found e
     * @return The specific keyword object
     */
    public static Keyword createKeyword(String alias) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                alias + " " + _keywordAliasMap.toString());
        Keyword prototype = _keywordAliasMap.get(alias);
        if (prototype == null) {
            throw new ParseException("No such alias/keyword", 0);
        }
        return prototype.createKeyword();
    }

    public static boolean isKeyword(String token) {
        return _keywordAliasMap.containsKey(token);
    }

    /**
     * For testing purpose only. Would need reflection to make it public
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private static LinkedHashMap<String, Keyword> getAliasMap() {
        return _keywordAliasMap;
    }

}