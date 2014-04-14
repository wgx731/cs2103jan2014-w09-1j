package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

/**
 * Template class for File Dao test
 */
//@author A0105853H

public abstract class FileDaoImplTest {

    private static final int TASK_MAP_LIST_DEFAULT_SIZE = 4;
    protected static final String TODO_DESCRIPTION = "This is a todo task.";
    protected static final String TODO_FILE_NAME = "todo";
    protected static final String EVENT_DESCRIPTION = "This is an event task";
    protected static final String EVENT_FILE_NAME = "event";
    protected static final String DEADLINE_DESCRIPTION = "This is an deadline task";
    protected static final String DEADLINE_FILE_NAME = "deadline";
    protected static final String RECUR_DEADLINE_DESCRIPTION = "This is an recurring deadline task";
    protected static final String TEST_RECUR_TAG = "RECUR_0";
    protected static final boolean COMPLETED = true;
    protected static final boolean UNCOMPLETED = false;

    protected static final int MAX_RECUR_INT = 0;

    protected static final Date startDate = new Date(1393243200);
    protected static final Date endDate = new Date(1393333200);

    protected static final Date recurDeadlineDateOne = new Date(1393333300);
    protected static final Date recurDeadlineDateTwo = new Date(1393333400);

    protected SortedSet<TodoTask> sampleTodoTaskSet;
    protected SortedSet<EventTask> sampleEventTaskSet;
    protected SortedSet<DeadlineTask> sampleDeadlineTaskSet;
    protected HashMap<String, ArrayList<Task<?>>> sampleTaskMap;
    protected ArrayList<Task<?>> sampleTaskMapList;
    protected Map<TaskType, String> storageMap;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private static String getAbsoluteName(File file) {
        return file.getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        storageMap = new HashMap<TaskType, String>();
        storageMap.put(TaskType.TODO,
                getAbsoluteName(testFolder.newFile(TODO_FILE_NAME)));
        storageMap.put(TaskType.EVENT,
                getAbsoluteName(testFolder.newFile(EVENT_FILE_NAME)));
        storageMap.put(TaskType.DEADLINE,
                getAbsoluteName(testFolder.newFile(DEADLINE_FILE_NAME)));

        sampleTodoTaskSet = new TreeSet<TodoTask>();
        sampleTodoTaskSet.add(new TodoTask(TODO_DESCRIPTION));
        sampleEventTaskSet = new TreeSet<EventTask>();
        sampleEventTaskSet.add(new EventTask(EVENT_DESCRIPTION, startDate,
                endDate));
        sampleDeadlineTaskSet = new TreeSet<DeadlineTask>();
        sampleDeadlineTaskSet.add(new DeadlineTask(DEADLINE_DESCRIPTION,
                endDate));
        sampleTaskMap = new HashMap<String, ArrayList<Task<?>>>();
        sampleTaskMapList = new ArrayList<Task<?>>(TASK_MAP_LIST_DEFAULT_SIZE);
        sampleTaskMapList.add(new DeadlineTask(DEADLINE_DESCRIPTION,
                recurDeadlineDateOne));
        sampleTaskMapList.add(new DeadlineTask(DEADLINE_DESCRIPTION,
                recurDeadlineDateTwo));
        sampleTaskMap.put(TEST_RECUR_TAG, sampleTaskMapList);
    }

    @After
    public void tearDown() throws Exception {
        storageMap = null;
        sampleTodoTaskSet = null;
        sampleEventTaskSet = null;
        sampleDeadlineTaskSet = null; 
    }

    @Test
    public abstract void testLoadEmptyFile();
}
