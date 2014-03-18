package sg.edu.cs2103t.mina.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

public class TaskViewTest {
    
    private static final String TESTING = "TESTING";
    TaskDataManagerStub tdmStub = new TaskDataManagerStub();
    TaskFilterManager tfm = new TaskFilterManager(tdmStub);
    TaskView taskView;
    
    private static Logger logger = LogManager
            .getLogger(TaskViewTest.class.getName());
    
    public void intiTaskView(){
        ArrayList<String> filters = new ArrayList<String>();
        filters.add("todo");
        filters.add("deadline");
        FilterParameter param = new FilterParameter(filters);
        taskView = new TaskView(TESTING, tfm.filterTask(param));
    }
    
    @Test
    public void testStatus() {
        intiTaskView();
        assertEquals(taskView.getStatus(), TESTING);
    }
    
    @Test
    public void testPagination(){
        intiTaskView();
        int pageNum = TaskView.ITEMS_PER_PAGE;
        int startPage = pageNum;
        int endPage = startPage + pageNum;
        //Just right
        ArrayList<Task<?>> currPage = taskView.getPage(TaskType.DEADLINE, 2);
        ArrayList<Task<?>> expected = getTasks(tdmStub.getUncompletedDeadlineTasks());
        boolean isSame = true;
        
        logger.info(expected);
        
        for (int i=startPage, j=0; i<endPage && j<currPage.size(); i++, j++) {
            logger.info(expected.get(i));
            logger.info(currPage.get(j));
            if(!expected.get(i).equals(currPage.get(j))){
                isSame = false;
                break;
            }
        }
        
        assertTrue("Should start from page 2, 5-9", isSame);
        
        //Last page
        
        
        //No pages in events 
        
        //Negative pages or zero page 
        
        //Exceed the number of pages
        
        //
    }
    
    public ArrayList<Task<?>> getTasks(SortedSet<? extends Task<?>> taskSet){
        
        Iterator<? extends Task<?>> taskIter = taskSet.iterator();
        ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();
        
        while (taskIter.hasNext()) {
            tasks.add(taskIter.next());
        }
        return tasks;
    }
    
}
