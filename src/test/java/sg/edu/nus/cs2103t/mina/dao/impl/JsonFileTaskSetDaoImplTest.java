package sg.edu.nus.cs2103t.mina.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.utils.JsonHelper;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Actual test cases for task set Dao
 */
//@author A0105853H

public class JsonFileTaskSetDaoImplTest extends FileDaoImplTest {

    private static final String EMPTY_STRING = "";
    private static final String WHITE_SPACES = "\\s+";
    private static final String CLASS_NAME = JsonFileTaskSetDaoImplTest.class
            .getName();

    private JsonFileTaskDaoImpl _storage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        _storage = new JsonFileTaskDaoImpl(storageMap);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        _storage.getFileOperationHelper().cleanTaskSetDao();
        _storage = null;
    }

    private File saveTaskSet(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType, boolean isCompleted) {
        try {
            _storage.saveTaskSet(taskSet, taskType, isCompleted);
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
            return _storage.loadTaskSet(taskType, isCompleted);
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("resource")
    private String readFileContent(File file) {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        } catch (IOException e) {
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        }
        return sb.toString();
    }

    private void checkSavedFile(File savedFile, String json) {
        Assert.assertTrue(savedFile.exists());
        Assert.assertTrue(savedFile.isFile());
        Assert.assertTrue(savedFile.length() > 0);
        String fileContent = readFileContent(savedFile);
        Assert.assertTrue(fileContent.replaceAll(WHITE_SPACES, EMPTY_STRING)
                .equals(json.replaceAll(WHITE_SPACES, EMPTY_STRING)));
    }

    @SuppressWarnings("unchecked")
    private void checkLoadedSet(SortedSet<? extends Task<?>> loadedSet,
            TaskType taskType) {
        switch (taskType) {
            case TODO :
                SortedSet<TodoTask> todoTaskSet = (SortedSet<TodoTask>) loadedSet;
                Assert.assertNotNull(todoTaskSet);
                Assert.assertEquals(sampleTodoTaskSet, todoTaskSet);
                break;
            case EVENT :
                SortedSet<EventTask> eventTaskSet = (SortedSet<EventTask>) loadedSet;
                Assert.assertNotNull(eventTaskSet);
                Assert.assertEquals(sampleEventTaskSet, eventTaskSet);
                break;
            case DEADLINE :
                SortedSet<DeadlineTask> deadlineTaskSet = (SortedSet<DeadlineTask>) loadedSet;
                Assert.assertNotNull(deadlineTaskSet);
                Assert.assertEquals(sampleDeadlineTaskSet, deadlineTaskSet);
                break;
            default :
                break;
        }
    }

    @Test
    public void testUnCompletedTodo() {
        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO,
                UNCOMPLETED);
        checkSavedFile(savedFile,
                JsonHelper.taskSetToJson(sampleTodoTaskSet, TaskType.TODO));
        checkLoadedSet(loadTaskSet(TaskType.TODO, UNCOMPLETED), TaskType.TODO);
    }

    @Test
    public void testCompletedTodo() {
        Iterator<TodoTask> iterator = sampleTodoTaskSet.iterator();
        TodoTask task = iterator.next();
        sampleTodoTaskSet.remove(task);
        task.setCompleted(COMPLETED);
        sampleTodoTaskSet.add(task);

        File savedFile = saveTaskSet(sampleTodoTaskSet, TaskType.TODO,
                COMPLETED);
        checkSavedFile(savedFile,
                JsonHelper.taskSetToJson(sampleTodoTaskSet, TaskType.TODO));
        checkLoadedSet(loadTaskSet(TaskType.TODO, COMPLETED), TaskType.TODO);
    }

    @Test
    public void testUnCompletedEvent() {
        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT,
                UNCOMPLETED);
        checkSavedFile(savedFile,
                JsonHelper.taskSetToJson(sampleEventTaskSet, TaskType.EVENT));
        checkLoadedSet(loadTaskSet(TaskType.EVENT, UNCOMPLETED), TaskType.EVENT);
    }

    @Test
    public void testCompletedEvent() {
        Iterator<EventTask> iterator = sampleEventTaskSet.iterator();
        EventTask task = iterator.next();
        sampleEventTaskSet.remove(task);
        task.setCompleted(COMPLETED);
        sampleEventTaskSet.add(task);

        File savedFile = saveTaskSet(sampleEventTaskSet, TaskType.EVENT,
                COMPLETED);
        checkSavedFile(savedFile,
                JsonHelper.taskSetToJson(sampleEventTaskSet, TaskType.EVENT));
        checkLoadedSet(loadTaskSet(TaskType.EVENT, COMPLETED), TaskType.EVENT);
    }

    @Test
    public void testUnCompletedDeadline() {
        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                UNCOMPLETED);
        checkSavedFile(savedFile, JsonHelper.taskSetToJson(
                sampleDeadlineTaskSet, TaskType.DEADLINE));
        checkLoadedSet(loadTaskSet(TaskType.DEADLINE, UNCOMPLETED),
                TaskType.DEADLINE);
    }

    @Test
    public void testCompletedDeadline() {
        Iterator<DeadlineTask> iterator = sampleDeadlineTaskSet.iterator();
        DeadlineTask task = iterator.next();
        sampleDeadlineTaskSet.remove(task);
        task.setCompleted(COMPLETED);
        sampleDeadlineTaskSet.add(task);

        File savedFile = saveTaskSet(sampleDeadlineTaskSet, TaskType.DEADLINE,
                COMPLETED);
        checkSavedFile(savedFile, JsonHelper.taskSetToJson(
                sampleDeadlineTaskSet, TaskType.DEADLINE));
        checkLoadedSet(loadTaskSet(TaskType.DEADLINE, COMPLETED),
                TaskType.DEADLINE);
    }

    /* This is a boundary case for the ‘empty file’ partition */
    @SuppressWarnings("unchecked")
    @Override
    public void testLoadEmptyFile() {
        SortedSet<DeadlineTask> deadlineSet = (SortedSet<DeadlineTask>) loadTaskSet(
                TaskType.DEADLINE, UNCOMPLETED);
        Assert.assertTrue(deadlineSet.isEmpty());
        deadlineSet = (SortedSet<DeadlineTask>) loadTaskSet(TaskType.DEADLINE,
                COMPLETED);
        Assert.assertTrue(deadlineSet.isEmpty());
        SortedSet<DeadlineTask> eventSet = (SortedSet<DeadlineTask>) loadTaskSet(
                TaskType.EVENT, UNCOMPLETED);
        Assert.assertTrue(eventSet.isEmpty());
        eventSet = (SortedSet<DeadlineTask>) loadTaskSet(TaskType.EVENT,
                COMPLETED);
        Assert.assertTrue(eventSet.isEmpty());
        SortedSet<TodoTask> todoSet = (SortedSet<TodoTask>) loadTaskSet(
                TaskType.TODO, UNCOMPLETED);
        Assert.assertTrue(todoSet.isEmpty());
        todoSet = (SortedSet<TodoTask>) loadTaskSet(TaskType.TODO, COMPLETED);
        Assert.assertTrue(todoSet.isEmpty());
    }

}
