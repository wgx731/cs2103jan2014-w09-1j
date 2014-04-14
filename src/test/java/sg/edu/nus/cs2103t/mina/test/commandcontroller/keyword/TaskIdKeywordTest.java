package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.TaskIdKeyword;

//@author A0099151B
public class TaskIdKeywordTest {
    
    private TaskIdKeyword taskId;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        taskId = new TaskIdKeyword();
    }
    
    @Test
    public void testExtractTaskId() throws ParseException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Method extractTaskId = TaskIdKeyword.class.getDeclaredMethod("extractTaskId", String.class);
        extractTaskId.setAccessible(true);
        
        String result = (String)extractTaskId.invoke(taskId, "todo 1");
        assertEquals("todo 1", result);
        
        result = (String)extractTaskId.invoke(taskId, "td 1");
        assertEquals("td 1", result);
        
        result = (String)extractTaskId.invoke(taskId, "td1");
        assertEquals("td1", result);
        
        result = (String)extractTaskId.invoke(taskId, "td1 ccvcv");
        assertEquals("td1", result);
        
        result = (String)extractTaskId.invoke(taskId, "xcxc td1");
        assertNull(result);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testNullifyTokens() throws ParseException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method nullifyTokens = TaskIdKeyword.class.getDeclaredMethod("nullifyTokens", new Class[]{String.class, ArrayList.class, Integer.class});
        nullifyTokens.setAccessible(true);
        
        ArrayList<String> testTokens, result, expected;

        testTokens = tokenize("sss -block todo 1 what the ehll?");
        result = (ArrayList<String>)nullifyTokens.invoke(taskId, "todo 1", testTokens, 1);
        expected = tokenize("sss @@!! @@!! @@!! what the ehll?");
        assertEquals(expected, result);
        
        testTokens = tokenize("sss -block td1 what the ehll?");
        result = (ArrayList<String>)nullifyTokens.invoke(taskId, "td1", testTokens, 1);
        expected = tokenize("sss @@!! @@!! what the ehll?");
        assertEquals(expected, result);
    }
    
    @Test
    public void testProcessTaskId() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method processTaskId = TaskIdKeyword.class.getDeclaredMethod("processTaskId",String.class);
        processTaskId.setAccessible(true);
        
        String rawTaskId, expected, result;
        
        rawTaskId = "td1";
        result = (String)processTaskId.invoke(taskId, rawTaskId);
        expected = "todo 1";
        assertEquals(expected, result);
        
        rawTaskId = "todo 1";
        result = (String)processTaskId.invoke(taskId, rawTaskId);
        expected = "todo 1";
        assertEquals(expected, result);
        
        rawTaskId = "td 11";
        result = (String)processTaskId.invoke(taskId, rawTaskId);
        expected = "todo 11";
        assertEquals(expected, result);
    }
    
    private ArrayList<String> tokenize(String cmd) {
        String[] tokens = cmd.split(" ");
        ArrayList<String> testTokens = new ArrayList<String>();
        
        for(int i=0; i<tokens.length; i++) {
            String token;
            if(!tokens[i].equals("@@!!")) {
                token = tokens[i]; 
            } else {
                token = null;
            }
            testTokens.add(token);
        }

        return testTokens;
    }
}
