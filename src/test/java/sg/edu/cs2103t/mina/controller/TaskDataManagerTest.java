package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;

public class TaskDataManagerTest {

    @Test
    public void testAddTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* Basic add */
        assertEquals("Adding To-do task.", new TodoTask("TodoTask1"),
                tdmTest.addTask(new DataParameter("TodoTask1", 'M', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals("Adding To-do task.", new TodoTask("TodoTask2", 'H'),
                tdmTest.addTask(new DataParameter("TodoTask2", 'H', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(2, tdmTest.getAllTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask1",
                new Date(currDateMilliSec)), tdmTest.addTask(new DataParameter(
                "DeadlineTask1", 'M', null, new Date(currDateMilliSec), null,
                TaskType.DEADLINE, 123)));
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());
        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask2",
                new Date(currDateMilliSec), 'M'),
                tdmTest.addTask(new DataParameter("DeadlineTask2", 'M', null,
                        new Date(currDateMilliSec), null, TaskType.DEADLINE,
                        123)));
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask1",
                new Date(currDateMilliSec), new Date(currDateMilliSec)),
                tdmTest.addTask(new DataParameter("EventTask1", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(1, tdmTest.getAllEventTasks().size());
        assertEquals("Adding Event task.", new EventTask("EventTask2",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.addTask(new DataParameter("EventTask2", 'H', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(2, tdmTest.getAllEventTasks().size());

        /* Task added is exactly the same as a task that already exists. */
        assertNull(tdmTest.addTask(new DataParameter("TodoTask2", 'H', null,
                null, null, TaskType.TODO, 123)));
    }

    @Test
    public void testDeleteTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 123));

        tdmTest.addTask(new DataParameter("Sleep", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Lie down", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));
        tdmTest.addTask(new DataParameter("Bed...", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 123));

        /* Basic Delete */
        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2)));
        assertEquals(2, tdmTest.getAllTodoTasks().size());

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'), tdmTest.deleteTask(new DataParameter(
                null, 'M', null, null, TaskType.DEADLINE, null, 2)));
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());

        assertEquals("Event tasks.", new EventTask("Lie down", new Date(
                currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2)));
        assertEquals(2, tdmTest.getAllEventTasks().size());

        /* Invalid Task Id */
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 6)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 0)));
        assertNull("Wrong task id will send back null",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 3)));

        assertEquals(2, tdmTest.getAllTodoTasks().size());
        assertEquals(2, tdmTest.getAllDeadlineTasks().size());
        assertEquals(2, tdmTest.getAllEventTasks().size());
    }

    @Test
    public void testModifyTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* modify task parameters */
        tdmTest.addTask(new DataParameter("TodoTask I am slack.", 'L', null,
                null, null, TaskType.TODO, 1));
        assertEquals("Modify todo description and priority.", new TodoTask(
                "TodoTask I am not slack anymore.", 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "TodoTask I am not slack anymore.", 'H', null, null,
                        TaskType.TODO, TaskType.TODO, 1)));
       
        tdmTest.addTask(new DataParameter("Deadline I am slack.", 'L', null,
                new Date(currDateMilliSec), null, TaskType.DEADLINE, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new DeadlineTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', null,
                        new Date(currDateMilliSec - 100000), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1)));

        tdmTest.addTask(new DataParameter("Event I am slack.", 'L', new Date(
                currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new EventTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', new Date(
                                currDateMilliSec - 100000), new Date(
                                currDateMilliSec - 100000), TaskType.EVENT,
                        TaskType.EVENT, 1)));
        
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());
        assertEquals(1, tdmTest.getAllEventTasks().size());
        
        tdmTest.resetTrees();
        
        /* modify task type */

        // basic modify
        tdmTest.addTask(new DataParameter("TodoTask becomes a DeadlineTask.",
                'M', null, null, null, TaskType.TODO, 123));

        assertEquals(
                "Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask.", new Date(
                        currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 1)));
        assertEquals(0, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());
        
        tdmTest.addTask(new DataParameter("TodoTask becomes an EventTask.",
                'M', null, null, null, TaskType.TODO, 123));
        assertEquals(
                "Modify to-do to event.",
                new EventTask("TodoTask becomes an EventTask.", new Date(
                        currDateMilliSec), new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', new Date(
                        currDateMilliSec), new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.EVENT, 1)));
        assertEquals(0, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllEventTasks().size());
        
        tdmTest.resetTrees();
        tdmTest.addTask(new DataParameter("Deadline task becomes a TodoTask.",
                'M', null, new Date(currDateMilliSec), null, TaskType.DEADLINE,
                123));
        assertEquals("Modify deadline to to-do.", new TodoTask(
                "Deadline task becomes a TodoTask. by " + new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 1)));
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals(0, tdmTest.getAllDeadlineTasks().size());
        
        //TODO: deadline->event, event->To-do, event->deadline 
        
        tdmTest.resetTrees();
        
        // modify task in between SortedSet
        tdmTest.addTask(new DataParameter("TodoTask remains a TodoTask 1.",
                'H', null, null, null, TaskType.TODO, 123));
        tdmTest.addTask(new DataParameter("TodoTask becomes a DeadlineTask 2.",
                'M', null, null, null, TaskType.TODO, 123));

        assertEquals(
                "Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask 2.",
                        new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 2)));
        assertEquals(new TodoTask("TodoTask remains a TodoTask 1.", 'H'), tdmTest.getAllTodoTasks().first());
        
        assertEquals(1, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getAllDeadlineTasks().size());

    }

    @Test
    public void markCompleted() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();
        
        tdmTest.addTask(new DataParameter("Mark this TodoTask complete", 'M',
                null, null, null, TaskType.TODO, 123));

        TodoTask expectedTodo = new TodoTask("Mark this TodoTask complete", 'M');
        expectedTodo.setCompleted(true);
        assertEquals("Mark Todo completed", expectedTodo,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 1)));
        assertEquals(0, tdmTest.getAllTodoTasks().size());
        assertEquals(1, tdmTest.getPastTodoTasks().size());

        tdmTest.addTask(new DataParameter("Mark this DeadlineTask complete", 'M',
                null, new Date(currDateMilliSec), null, TaskType.DEADLINE, 123));

        DeadlineTask expectedDeadline = new DeadlineTask(
                "Mark this DeadlineTask complete", new Date(currDateMilliSec),
                'M');
        expectedDeadline.setCompleted(true);
        assertEquals("Mark Deadline completed", expectedDeadline,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 1)));
        assertEquals(0, tdmTest.getAllDeadlineTasks().size());
        assertEquals(1, tdmTest.getPastDeadlineTasks().size());

        tdmTest.addTask(new DataParameter("Mark this EventTask complete", 'M',
                new Date(currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 123));

        EventTask expectedEvent = new EventTask("Mark this EventTask complete",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'M');
        expectedEvent.setCompleted(true);
        assertEquals("Mark Event completed", expectedEvent,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 1)));
        assertEquals(0, tdmTest.getAllEventTasks().size());
        assertEquals(1, tdmTest.getPastEventTasks().size());
    }

}
