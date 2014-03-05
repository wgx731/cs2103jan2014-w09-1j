package sg.edu.cs2103t.mina.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

public class FileTaskSetDaoImplTest {

    private static Logger logger = LogManager
            .getLogger(FileTaskSetDaoImplTest.class.getName());

    private static final String TODO_DESCRIPTION = "This is a todo task.";
    private static final String TODO_FILE_NAME = "todo";
    private static final String EVENT_DESCRIPTION = "This is an event task";
    private static final String EVENT_FILE_NAME = "event";
    private static final String DEADLINE_DESCRIPTION = "This is an deadline task";
    private static final String DEADLINE_FILE_NAME = "deadline";

    private static final String UNEXPECTED_ERROR = "unexpected error occured.";

    private static final Date startDate = new Date(1393243200);
    private static final Date endDate = new Date(1393333200);

    private static SortedSet<TodoTask> sampleTodoTaskSet;
    private static SortedSet<EventTask> sampleEventTaskSet;
    private static SortedSet<DeadlineTask> sampleDeadlineTaskSet;
    private static Map<TaskType, String> storageMap;

    private FileTaskDaoImpl storage;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private static String getAbsoluteName(File file) {
        return file.getAbsolutePath();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        sampleTodoTaskSet = new TreeSet<TodoTask>();
        sampleTodoTaskSet.add(new TodoTask(TODO_DESCRIPTION));
        sampleEventTaskSet = new TreeSet<EventTask>();
        sampleEventTaskSet.add(new EventTask(EVENT_DESCRIPTION, startDate,
                endDate));
        sampleDeadlineTaskSet = new TreeSet<DeadlineTask>();
        sampleDeadlineTaskSet.add(new DeadlineTask(DEADLINE_DESCRIPTION,
                endDate));
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
        storage = new FileTaskDaoImpl(storageMap);
    }

    @After
    public void tearDown() throws Exception {
        storageMap = null;
        storage = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownTaskTypeCompleted() {
        try {
            storage.loadTaskSet(TaskType.UNKOWN, true);
        } catch (IOException e) {
            logger.error(e, e);
            fail(UNEXPECTED_ERROR);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownTaskTypeUnCompleted() {
        try {
            storage.loadTaskSet(TaskType.UNKOWN, false);
        } catch (IOException e) {
            logger.error(e, e);
            fail(UNEXPECTED_ERROR);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCompleted() {
        try {
            storage.loadTaskSet(null, true);
        } catch (IOException e) {
            logger.error(e, e);
            fail(UNEXPECTED_ERROR);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUnCompleted() {
        try {
            storage.loadTaskSet(null, false);
        } catch (IOException e) {
            logger.error(e, e);
            fail(UNEXPECTED_ERROR);
        }
    }

    private File saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {
        try {
            storage.saveTaskSet(taskSet, taskType, isCompleted);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        String fileName = null;
        switch (taskType) {
            case TODO:
                fileName = TODO_FILE_NAME;
                break;
            case EVENT:
                fileName = EVENT_FILE_NAME;
                break;
            case DEADLINE:
                fileName = DEADLINE_FILE_NAME;
                break;
            default:
                fileName = null;
                break;
        }
        if (isCompleted) {
            return new File(testFolder.getRoot() + "/" + fileName
                    + FileTaskDaoImpl.getCompletedSuffix());
        }
        return new File(testFolder.getRoot() + "/" + fileName);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnCompletedTodo() {
        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO, false);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<TodoTask> loadedTodoTasks = null;
        try {
            loadedTodoTasks = (Set<TodoTask>) storage.loadTaskSet(
                    TaskType.TODO, false);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedTodoTasks);
        assertEquals(sampleTodoTaskSet, loadedTodoTasks);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompletedTodo() {
        Iterator<TodoTask> iterator = sampleTodoTaskSet.iterator();
        TodoTask task = iterator.next();
        sampleTodoTaskSet.remove(task);
        task.setCompleted(true);
        sampleTodoTaskSet.add(task);

        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO, true);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<TodoTask> loadedTodoTasks = null;
        try {
            loadedTodoTasks = (Set<TodoTask>) storage.loadTaskSet(
                    TaskType.TODO, true);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedTodoTasks);
        assertEquals(sampleTodoTaskSet, loadedTodoTasks);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnCompletedEvent() {
        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT, false);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<EventTask> loadedEventTask = null;
        try {
            loadedEventTask = (Set<EventTask>) storage.loadTaskSet(
                    TaskType.EVENT, false);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedEventTask);
        assertEquals(sampleEventTaskSet, loadedEventTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompletedEvent() {
        Iterator<EventTask> iterator = sampleEventTaskSet.iterator();
        EventTask task = iterator.next();
        sampleEventTaskSet.remove(task);
        task.setCompleted(true);
        sampleEventTaskSet.add(task);
        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT, true);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<EventTask> loadedEventTask = null;
        try {
            loadedEventTask = (Set<EventTask>) storage.loadTaskSet(
                    TaskType.EVENT, true);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedEventTask);
        assertEquals(sampleEventTaskSet, loadedEventTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnCompletedDeadline() {
        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                false);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<EventTask> loadedDeadlineTask = null;
        try {
            loadedDeadlineTask = (Set<EventTask>) storage.loadTaskSet(
                    TaskType.DEADLINE, false);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedDeadlineTask);
        assertEquals(sampleDeadlineTaskSet, loadedDeadlineTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompletedDeadline() {
        Iterator<DeadlineTask> iterator = sampleDeadlineTaskSet.iterator();
        DeadlineTask task = iterator.next();
        sampleDeadlineTaskSet.remove(task);
        task.setCompleted(true);
        sampleDeadlineTaskSet.add(task);

        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                true);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        Set<EventTask> loadedDeadlineTask = null;
        try {
            loadedDeadlineTask = (Set<EventTask>) storage.loadTaskSet(
                    TaskType.DEADLINE, true);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        assertNotNull(loadedDeadlineTask);
        assertEquals(sampleDeadlineTaskSet, loadedDeadlineTask);
    }

}
