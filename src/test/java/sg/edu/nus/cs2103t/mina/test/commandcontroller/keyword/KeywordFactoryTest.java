package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CompositeKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.Keyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

public class KeywordFactoryTest {

    private static final String CLASS_NAME = KeywordFactoryTest.class.getName();
    private static final String WRONG_CLASS = "Expceted $1%s. Got $2%s instead";
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        KeywordFactory.initialiseKeywordFactory();
    }

    
    @Test
    public void testKeywordsRegisteration() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        log("Testing to see if classes can be loaded");
        
        Method initKeywordClassesMethod = KeywordFactory.class.getDeclaredMethod("initKeywordClasses", StandardKeyword[].class); 
        initKeywordClassesMethod.setAccessible(true);
        
        try {
            invokeInitClasses(initKeywordClassesMethod);
        } catch (ClassNotFoundException e) {
            log("Not all classs are loaded");
            fail(e.getMessage());
        }
        
        log("All classes loaded properly!");
    }
    
    @Test
    public void testAliasRegistration() throws Exception {
        
        log("Testing to see if aliases are registered correctly");
        
        LinkedHashMap<String, Keyword> aliasMap = getAliasMap();
        
        for(String alias: aliasMap.keySet()) {
            Keyword keyword = aliasMap.get(alias);
            String fullClassName = keyword.getClass().getName();
            String keywordClass = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            if(!isRegisteredInStandardKeyword(keywordClass)) {
                fail("No such class in enum: " + keywordClass);
            }
        }
        
    }
    
    @Test
    public void testCreateKeyword() throws Exception {
        
        log("Testing to see if keywords are created correctly.");
        
        Method createKeyword = Keyword.class.getDeclaredMethod("createKeyword", new Class[]{}); 
        createKeyword.setAccessible(true);
        
        LinkedHashMap<String, Keyword> aliasMap = getAliasMap();   
        for(String alias: aliasMap.keySet()) {
            Keyword protoype = aliasMap.get(alias);
            String prototypeClass = protoype.getClass().getName();
            
            Keyword keyword = (Keyword)createKeyword.invoke(protoype);
            String keywordClass="NoClass";
            if(keyword!=null) {
                keywordClass = keyword.getClass().getName();
            } else {
                fail(prototypeClass + " createKeyword() returns null!");
            }
            
            if(!keywordClass.equals(prototypeClass)) {
                String err = String.format(WRONG_CLASS, prototypeClass, keywordClass);
                fail(err);
            }
        }
    }
    
    private boolean isRegisteredInStandardKeyword(String keywordClass) {
        //For standardKeyword first
        if(!isRegisteredInEnum(keywordClass, SimpleKeyword.values())){
            return isRegisteredInEnum(keywordClass, CompositeKeyword.values());
        }
        return true;
    }

    private boolean isRegisteredInEnum(String keywordClass,
            StandardKeyword[] values) {
        for(StandardKeyword type: values) {
            String className = type.getFilePrefix() + "Keyword";
            log("Testing " + keywordClass + " against " + className);
            if(className.equals(keywordClass)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, Keyword> getAliasMap() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getAliasMapMethod = KeywordFactory.class.getDeclaredMethod("getAliasMap", new Class[]{}); 
        getAliasMapMethod.setAccessible(true);
        
        
        return (LinkedHashMap<String, Keyword>) getAliasMapMethod.invoke(KeywordFactory.class);
    }
    
    private void invokeInitClasses(Method initKeywordClassesMethod) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        initKeywordClassesMethod.invoke(KeywordFactory.class, (Object)SimpleKeyword.values());
        initKeywordClassesMethod.invoke(KeywordFactory.class, (Object)CompositeKeyword.values()); 
    }
    
    private void log(String message) {
        LogHelper.log(CLASS_NAME, Level.INFO, message);
    }
    
}
