package sg.edu.nus.cs2103t.mina.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

public class JsonFileTaskSetDaoImplTest extends FileTaskSetDaoImplTest {

    private static final String CLASS_NAME = JsonFileTaskSetDaoImplTest.class
            .getName();

    private JsonFileTaskDaoImpl storage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        storage = new JsonFileTaskDaoImpl(new FileOperationHelper(
                JsonFileTaskDaoImpl.getCompletedSuffix(),
                JsonFileTaskDaoImpl.getFileExtension(),
                FileTaskMapDaoImpl.getFileExtension()), storageMap);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        storage = null;
    }

    private File saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {
        try {
            storage.saveTaskSet(taskSet, taskType, isCompleted);
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }
        String fileName = null;
        switch (taskType) {
            case TODO :
                fileName = TODO_FILE_NAME;
                break;
            case EVENT :
                fileName = EVENT_FILE_NAME;
                break;
            case DEADLINE :
                fileName = DEADLINE_FILE_NAME;
                break;
            default :
                fileName = null;
                break;
        }
        String fileFullName = testFolder.getRoot() + "/";
        if (isCompleted) {
            fileFullName += fileName + JsonFileTaskDaoImpl.getCompletedSuffix() +
                    JsonFileTaskDaoImpl.getFileExtension();
        } else {
            fileFullName += fileName + JsonFileTaskDaoImpl.getFileExtension();
        }
        return new File(fileFullName);
    }

    private SortedSet<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) {
        try {
            return storage.loadTaskSet(taskType, isCompleted);
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
            return null;
        }
    }

    private void checkSavedFile(File savedFile) {
        assertTrue(savedFile.exists());
        assertTrue(savedFile.isFile());
        assertTrue(savedFile.length() > 0);
    }

    @SuppressWarnings("unchecked")
    private void checkLoadedSet(SortedSet<? extends Task<?>> loadedSet,
            TaskType taskType) {
        switch (taskType) {
            case TODO :
                SortedSet<TodoTask> todoTaskSet = (SortedSet<TodoTask>) loadedSet;
                assertNotNull(todoTaskSet);
                assertEquals(sampleTodoTaskSet, todoTaskSet);
                break;
            case EVENT :
                SortedSet<EventTask> eventTaskSet = (SortedSet<EventTask>) loadedSet;
                assertNotNull(eventTaskSet);
                assertEquals(sampleEventTaskSet, eventTaskSet);
                break;
            case DEADLINE :
                SortedSet<DeadlineTask> deadlineTaskSet = (SortedSet<DeadlineTask>) loadedSet;
                assertNotNull(deadlineTaskSet);
                assertEquals(sampleDeadlineTaskSet, deadlineTaskSet);
                break;
            default :
                break;
        }
    }

    @Test
    public void testUnCompletedTodo() {
        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO, false);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.TODO, false), TaskType.TODO);
    }

    @Test
    public void testCompletedTodo() {
        Iterator<TodoTask> iterator = sampleTodoTaskSet.iterator();
        TodoTask task = iterator.next();
        sampleTodoTaskSet.remove(task);
        task.setCompleted(true);
        sampleTodoTaskSet.add(task);

        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO, true);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.TODO, true), TaskType.TODO);
    }

    @Test
    public void testUnCompletedEvent() {
        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT, false);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.EVENT, false), TaskType.EVENT);
    }

    @Test
    public void testCompletedEvent() {
        Iterator<EventTask> iterator = sampleEventTaskSet.iterator();
        EventTask task = iterator.next();
        sampleEventTaskSet.remove(task);
        task.setCompleted(true);
        sampleEventTaskSet.add(task);

        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT, true);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.EVENT, true), TaskType.EVENT);
    }

    @Test
    public void testUnCompletedDeadline() {
        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                false);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.DEADLINE, false), TaskType.DEADLINE);
    }

    @Test
    public void testCompletedDeadline() {
        Iterator<DeadlineTask> iterator = sampleDeadlineTaskSet.iterator();
        DeadlineTask task = iterator.next();
        sampleDeadlineTaskSet.remove(task);
        task.setCompleted(true);
        sampleDeadlineTaskSet.add(task);

        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                true);
        checkSavedFile(savedFile);
        checkLoadedSet(loadTaskSet(TaskType.DEADLINE, true), TaskType.DEADLINE);
    }

    /* This is a boundary case for the ‘empty file’ partition */
    @SuppressWarnings("unchecked")
    @Override
    public void testLoadEmptyFile() {
        SortedSet<DeadlineTask> deadlineSet = (SortedSet<DeadlineTask>) loadTaskSet(
                TaskType.DEADLINE, false);
        assertTrue(deadlineSet.isEmpty());
        deadlineSet = (SortedSet<DeadlineTask>) loadTaskSet(TaskType.DEADLINE,
                true);
        assertTrue(deadlineSet.isEmpty());
        SortedSet<DeadlineTask> eventSet = (SortedSet<DeadlineTask>) loadTaskSet(
                TaskType.EVENT, false);
        assertTrue(eventSet.isEmpty());
        eventSet = (SortedSet<DeadlineTask>) loadTaskSet(TaskType.EVENT, true);
        assertTrue(eventSet.isEmpty());
        SortedSet<TodoTask> todoSet = (SortedSet<TodoTask>) loadTaskSet(
                TaskType.TODO, false);
        assertTrue(todoSet.isEmpty());
        todoSet = (SortedSet<TodoTask>) loadTaskSet(TaskType.TODO, true);
        assertTrue(todoSet.isEmpty());

    }

}
