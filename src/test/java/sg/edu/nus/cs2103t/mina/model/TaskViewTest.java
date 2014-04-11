package sg.edu.nus.cs2103t.mina.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

public class TaskViewTest {

    private static final String TESTING = "TESTING";
    TaskDataManagerStub tdmStub = new TaskDataManagerStub();
    TaskFilterManager tfm = new TaskFilterManager(tdmStub);
    TaskView taskView;

    private static final String CLASS_NAME = TaskViewTest.class.getName();

    public void intiTaskView() {
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

    @Ignore
    @Test
    public void testPagination() {
        intiTaskView();
        int pageNum = TaskView.ITEMS_PER_PAGE;
        int startPage = pageNum;
        int endPage = startPage + pageNum;
        // Just right
        ArrayList<Task<?>> currPage = taskView.getPage(TaskType.DEADLINE, 2);
        ArrayList<Task<?>> expected = getTasks(tdmStub
                .getUncompletedDeadlineTasks());
        boolean isSame = true;

        LogHelper.log(CLASS_NAME, Level.INFO, expected.toString());
        LogHelper.log(CLASS_NAME, Level.INFO, currPage.toString());

        for (int i = startPage, j = 0; i < endPage && j < currPage.size(); i++, j++) {
            if (!expected.get(i).equals(currPage.get(j))) {
                isSame = false;
                break;
            }
        }

        assertTrue("Should start from page 2, 5-9", isSame);

        // Last page
        currPage = taskView.getPage(TaskType.DEADLINE, 4);
        assertTrue(currPage.size() == 1);
        assertEquals(currPage.get(0), expected.get(expected.size() - 1));

        // No pages in events
        currPage = taskView.getPage(TaskType.EVENT, 2);
        assertTrue(currPage.isEmpty());

        // Negative pages or zero page
        boolean hasNegative = false;
        try {
            currPage = taskView.getPage(TaskType.TODO, -1);
        } catch (NumberFormatException e) {
            hasNegative = true;
        } finally {
            assertTrue(hasNegative);
        }
        // Exceed the number of pages
        boolean hasExceed = false;
        try {
            currPage = taskView.getPage(TaskType.TODO, 1000);
        } catch (IndexOutOfBoundsException e) {
            hasExceed = true;
        } finally {
            assertTrue(hasExceed);
        }
        //
    }

    public ArrayList<Task<?>> getTasks(SortedSet<? extends Task<?>> taskSet) {

        Iterator<? extends Task<?>> taskIter = taskSet.iterator();
        ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();

        while (taskIter.hasNext()) {
            tasks.add(taskIter.next());
        }
        return tasks;
    }

}
