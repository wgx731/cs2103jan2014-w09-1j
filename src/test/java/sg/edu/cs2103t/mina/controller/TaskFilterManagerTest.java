package sg.edu.cs2103t.mina.controller;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

public class TaskFilterManagerTest {

	private static final String FILTER_COMPLETE = "complete";
	private static final String FILTER_COMPLETE_PLUS = "+complete";

	private TaskDataManagerStub tdmStub = new TaskDataManagerStub();
	private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
	private static Logger logger = LogManager
			.getLogger(TaskFilterManagerTest.class.getName());

	@Test
	public void viewOutput() {
		// check for output
		TaskDataManagerStub tdm = tdmStub;

		Iterator<TodoTask> todoIterator;
		todoIterator = tdm.getAllTodoTasks().iterator();

		while (todoIterator.hasNext()) {
			TodoTask task = todoIterator.next();
			if (task != null)
				printTodo(task);
		}

		Iterator<EventTask> eventIterator;
		eventIterator = tdm.getAllEventTasks().iterator();

		while (eventIterator.hasNext()) {
			printEvent(eventIterator.next());
		}

		Iterator<DeadlineTask> deadlineIterator;
		deadlineIterator = tdm.getAllDeadlineTasks().iterator();

		while (deadlineIterator.hasNext()) {
			printDeadline(deadlineIterator.next());
		}
		assertTrue(true);
	}

	@Test(expected = NullPointerException.class)
	public void testFilterVoid() {
		tfmTest.filterTask(null);
	}

	/**
	 * Test for passing an empty filter parameter Expected: Returned all
	 * uncompleted tasks. In this case with the dummy data, everything should be
	 * there.
	 * 
	 */
	@Test
	public void testNoFilter() {

		ArrayList<Task<?>> test = tfmTest.filterTask(new FilterParameter());
		boolean isExist = true;

		for (Iterator<Task<?>> iterator = test.iterator(); iterator.hasNext();) {
			Task<?> t = iterator.next();
			if (t instanceof TodoTask) {
				isExist = tdmStub.getAllTodoTasks().contains((TodoTask) t);
			} else if (t instanceof EventTask) {
				isExist = tdmStub.getAllEventTasks().contains((EventTask) t);
			} else if (t instanceof DeadlineTask) {
				isExist = tdmStub.getAllDeadlineTasks().contains((DeadlineTask) t);
			}
			if (!isExist) {
				break;
			}
		}

		assertTrue("Must have everything!", isExist);

	}

	/**
	 * Test for displaying deadlines only. Expected: Deadlines only. In this case,
	 * all dummy data in deadlines.
	 */
	@Test
	public void testDeadlinesOnly() {

		ArrayList<Task<?>> test = getResult(TaskFilterManager.DEADLINE);

		SortedSet<DeadlineTask> deadlines = tdmStub.getAllDeadlineTasks();
		int numOfDeadlines = deadlines.size();

		assertTrue("Must be all deadlines!", numOfDeadlines == test.size()
				&& isTaskExist(test, deadlines, TaskType.DEADLINE));

	}

	/**
	 * Test for displaying Todos only. Expected: Todos only. In this case, all
	 * dummy data in Todos.
	 */
	@Test
	public void testTodosOnly() {

		ArrayList<Task<?>> test = getResult(TaskFilterManager.TODO);
		SortedSet<TodoTask> todos = tdmStub.getAllTodoTasks();
		int numOfTodos = todos.size();

		assertTrue("Must be all todos!",
				numOfTodos == test.size() && isTaskExist(test, todos, TaskType.TODO));
	}

	/**
	 * Test for displaying events only. Expected: Events only. In this case, all
	 * dummy data in Events.
	 */
	@Test
	public void testEventsOnly() {

		ArrayList<Task<?>> test = getResult(TaskFilterManager.EVENT);
		SortedSet<EventTask> events = tdmStub.getAllEventTasks();
		int numOfEvents = events.size();

		assertTrue("Must be all events!", numOfEvents == test.size()
				&& isTaskExist(test, events, TaskType.EVENT));
	}

	/**
	 * Test for completed tasks only. Expected: Completed events only.
	 */
	@Test
	public void testCompletedOnly() {

		ArrayList<Task<?>> test = getResult(FILTER_COMPLETE);
		ArrayList<Task<?>> allCompleted = getCompletedDummyTasks();

		assertTrue(
				"Tasks must be identical in both cases!",
				test.size() == allCompleted.size()
						&& hasCompletedTasks(test, allCompleted));

	}

	/**
	 * Test for completed+ tasks Expected: Everything including completed tasks
	 */
	@Test
	public void testCompletePlus() {
		ArrayList<Task<?>> test = getResult(FILTER_COMPLETE_PLUS);
		boolean hasEverything = checkEverything(test);
		assertTrue("Need everything!", hasEverything);
	}

	/**
	 * Test for priority. Expected: Tasks are ranked from highest to lowest. In
	 * the case of events, the closer the date, the more urgent it is.
	 */
	@Test
	public void testPriority() {
		ArrayList<Task<?>> test = getResult("priority");
		assertTrue("Priorities are not sorted!", checkPriority(test));
	}

	private boolean checkPriority(ArrayList<Task<?>> test) {

		int orderPriority = 0;
		int dayPassed = 0;
		Date prevCreated = new Date(0);

		for (Task<?> task : test) {
			// todo task in the order of HHHHHH...MMMMMM...LLLL
			if (task instanceof TodoTask) {
				TodoTask todo = (TodoTask) task;
				orderPriority = checkTodoContiguity(todo, orderPriority);
				if (orderPriority == -1) {
					return false;
				}
			} else if (task instanceof EventTask) {

			} else if (task instanceof DeadlineTask) {

			}
		}

		return false;
	}

	/**
	 * 
	 * @param todo
	 * @param orderPriority
	 * @return
	 */
	private int checkTodoContiguity(TodoTask todo, int orderPriority) {

		int currPriority;

		switch (todo.getPriority()) {
		case 'H':
			currPriority = 0;
			break;
		case 'M':
			currPriority = 1;
			break;
		case 'L':
			currPriority = 2;
			break;
		default:
			return -1;
		}

		if (currPriority >= orderPriority) {
			return currPriority;
		} else {
			return -1;
		}

	}

	private boolean checkEverything(ArrayList<Task<?>> test) {

		SortedSet<TodoTask> todos = tdmStub.getAllTodoTasks();
		SortedSet<TodoTask> todosComp = tdmStub.getPastTodoTasks();

		SortedSet<EventTask> events = tdmStub.getAllEventTasks();
		SortedSet<EventTask> eventsComp = tdmStub.getPastEventTasks();

		SortedSet<DeadlineTask> deadlines = tdmStub.getAllDeadlineTasks();
		SortedSet<DeadlineTask> deadlinesComp = tdmStub.getPastDeadlineTasks();

		int totalTaskSize = deadlinesComp.size() + eventsComp.size()
				+ todosComp.size() + todos.size() + events.size() + deadlines.size();

		for (Task<?> task : test) {

			if (task instanceof DeadlineTask
					&& !(deadlinesComp.contains(task) || deadlines.contains(task))) {
				return false;
			}

			if (task instanceof TodoTask
					&& !(todosComp.contains(task) || todos.contains(task))) {
				return false;
			}

			if (task instanceof EventTask
					&& !(eventsComp.contains(task) || events.contains(task))) {
				return false;
			}

		}

		return totalTaskSize == test.size();
	}

	private boolean hasCompletedTasks(ArrayList<Task<?>> test,
			ArrayList<Task<?>> allCompleted) {

		for (Task<?> t : test) {
			if (!allCompleted.contains(t)) {
				return false;
			}
		}

		return true;
	}

	private ArrayList<Task<?>> getCompletedDummyTasks() {

		SortedSet<EventTask> events = tdmStub.getPastEventTasks();
		Iterator<EventTask> eventIter = events.iterator();

		SortedSet<DeadlineTask> deadlines = tdmStub.getPastDeadlineTasks();
		Iterator<DeadlineTask> deadlineIter = deadlines.iterator();

		SortedSet<TodoTask> todos = tdmStub.getPastTodoTasks();
		Iterator<TodoTask> todosIter = todos.iterator();

		ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();

		while (todosIter.hasNext()) {
			tasks.add(todosIter.next());
		}

		while (eventIter.hasNext()) {
			tasks.add(eventIter.next());
		}

		while (deadlineIter.hasNext()) {
			tasks.add(deadlineIter.next());
		}

		return tasks;
	}

	/**
	 * Get the result based on the keywords entered
	 * 
	 * @param cmd
	 *          the string of keywords
	 * @return An arrayList of tasks.
	 */
	private ArrayList<Task<?>> getResult(String cmd) {

		String[] tokens = cmd.split(" ");
		ArrayList<String> keywords = new ArrayList<String>();

		for (int i = 0; i < tokens.length; i++) {
			keywords.add(tokens[i]);
		}

		FilterParameter filter = new FilterParameter(keywords);
		return tfmTest.filterTask(filter);
	}

	/**
	 * Compare the returned result to dummy data
	 * 
	 * @param test
	 *          the arraylist of tasks
	 * @param taskTree
	 *          the dummy data for task
	 * @param type
	 *          the TaskType specified.
	 * @return true if everything in the result is in specified tasks
	 */
	private boolean isTaskExist(ArrayList<Task<?>> test,
			SortedSet<? extends Task<?>> taskTree, TaskType type) {

		for (Iterator<Task<?>> iterator = test.iterator(); iterator.hasNext();) {
			Task<?> task = iterator.next();
			if (!(isTask(task, type) && taskTree.contains(task))) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("rawtypes")
	private boolean isTask(Task task, TaskType type) {

		boolean isTask;

		switch (type) {
		case DEADLINE:
			isTask = task instanceof DeadlineTask;
			break;
		case TODO:
			isTask = task instanceof TodoTask;
			break;
		case EVENT:
			isTask = task instanceof EventTask;
			break;
		default:
			isTask = false;
		}

		return isTask;
	}

	/**
	 * To compare with tasks for now. The current task compareTo has a bug.
	 * 
	 * @param task
	 * @param todoTasks
	 * @return true if they match. False, if they don't
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private boolean compareTodo(Task task, TreeSet<TodoTask> todoTasks) {

		Iterator<TodoTask> todoIter = todoTasks.iterator();

		while (todoIter.hasNext()) {
			TodoTask todo = todoIter.next();
			if (todo.getDescription().equals(task.getDescription())) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public String toStringForTask(Task task) {
		String format = "UUID: %1$s\n" + "Type: %2$s\n" + "Description: %3$s\n"
				+ "Tags: %4$s\n" + "Priority: %5$s\n" + "Created: %6$s\n"
				+ "Last modified: %7$s\n" + "Completed: %8$s\n\n";
		String output = String.format(format, task.getId(), task.getType()
				.getType(), task.getDescription(), task.getTags(), task.getPriority(),
				task.getCreatedTime(), task.getLastEditedTime(), task.isCompleted());
		return output;
	}

	public void printEvent(EventTask task) {
		String format = "Start: %1$s\n" + "End: %2$s\n";
		logger.info(toStringForTask(task));
		logger.info(String.format(format, task.getStartTime(), task.getEndTime()));
	}

	public void printDeadline(DeadlineTask task) {
		String format = "End: %1$s\n";
		logger.info(toStringForTask(task));
		logger.info(String.format(format, task.getEndTime()));
	}

	public void printTodo(TodoTask task) {
		logger.info(toStringForTask(task));
	}

}
