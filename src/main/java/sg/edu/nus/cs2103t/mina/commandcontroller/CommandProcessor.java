package sg.edu.nus.cs2103t.mina.commandcontroller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedSet;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.FilterType;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;
import sg.edu.nus.cs2103t.mina.model.parameter.SearchParameter;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

import com.rits.cloning.Cloner;

/**
 * Processor class to process user input command
 */
//@author A0099324X

public class CommandProcessor {

	private static final String DISPLAYING_SEARCHES = "Displaying searches!";
	private static final String RESULTS_DISPLAYED = "Results displayed";
	private static final int MAX_INPUT_ARRAY_SIZE = 2;
	private static final int COMMAND_POSITION = 0;
	private static final int PARAMETER_POSITION = 1;
	private static final int FISRT_ARRAY_INDEX = 0;

	private static final String WELCOME_MESSAGE = "Welcome to MINA!";
	private static final String INVALID_COMMAND = "Invalid command.";
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String ADDED_MESSAGE = "%1$s task %2$s has been added.";
	private static final String ADD_ERROR_MESSAGE = "Error occured when system try to add new task.";
	private static final String DELETED_MESSAGE = "%1$s task %2$s has been deleted.";
	private static final String DELETE_ERROR_MESSAGE = "Error occured when system try to delete task.";
	private static final String MODIFIED_MESSAGE = "Modified. %1$s task %2$s.";
	private static final String MODIFY_ERROR_MESSAGE = "Error occured when system try to modify task.";
	private static final String COMPLETED_MESSAGE = "%1$s task %2$s has been makred as completed.";
	private static final String COMPLETE_ERROR_MESSAGE = "Error occured when system try to mark task as completed.";
	private static final String SEARCH_NOT_FOUND = "Search cannot find any result.";
	private static final String UNDO_MESSAGE = "undo completed.";
	private static final String UNDO_ERROR_MESSAGE = "Error occured when system try to undo.";
	private static final String REDO_MESSAGE = "redo completed.";
	private static final String REDO_ERROR_MESSAGE = "Error occured when system try to redo.";
	private static final String CLASS_NAME = CommandManager.class.getName();

	private String[] _inputString;
	
	private int _currentEventPage;
	private int _currentDeadlinePage;
	private int _currentTodoPage;

	private TaskView _taskView;
	private TaskDataManager _taskDataManager;
	private TaskFilterManager _taskFilterManager;
	private CommandHistory _commandHistory;

	// Constructor
	public CommandProcessor() {
		_taskDataManager = new TaskDataManager();
		_taskFilterManager = new TaskFilterManager(_taskDataManager);
		_commandHistory = new CommandHistory();
	      initializeTaskView();
	}

	public CommandProcessor(TaskDataManager taskDataManager,
			TaskFilterManager taskFilterManager) {
		_taskDataManager = taskDataManager;
		_taskFilterManager = taskFilterManager;
		_commandHistory = new CommandHistory();
		initializeTaskView();
	}

	public TaskView getTaskView() {
		return _taskView;
	}

	public void initializeTaskView() {
		FilterParameter defaultFilter = new FilterParameter();
		_commandHistory.updateLatestFilter(defaultFilter);
		_taskView = new TaskView(WELCOME_MESSAGE,
				_taskFilterManager.filterTask(defaultFilter));
	}

	// This operation is used to get input from the user and execute it till
	// exit
	public TaskView processUserInput(String userInput, int eventPage,
			int deadlinePage, int todoPage) {
		LogHelper.log(CLASS_NAME, Level.INFO, "Process Input: \"" + userInput
				+ "\"");
		if (userInput == null || userInput.trim().equals(EMPTY_STRING)) {
			return new TaskView(INVALID_COMMAND);
		}
		_currentEventPage = eventPage;
		_currentDeadlinePage = deadlinePage;
		_currentTodoPage = todoPage;
		_inputString = userInput.split(SPACE, MAX_INPUT_ARRAY_SIZE);
		if (_inputString.length == 1) {
			_inputString = (userInput + " ").split(SPACE, MAX_INPUT_ARRAY_SIZE);
		}
		CommandType command;
		command = determineCommand();
		try {
			processUserCommand(command);
			return _taskView;
		} catch (Exception e) {
			processUserCommand(CommandType.INVALID);
			LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			return _taskView;
		}
	}

	// This operation is used to get the user input and extract the command from
	// inputString
	private CommandType determineCommand() {
		String userCommand = _inputString[COMMAND_POSITION];
		LogHelper.log(CLASS_NAME, Level.INFO, "command is: " + userCommand);
		try {
			return CommandType.valueOf(userCommand.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return CommandType.INVALID;
		}
	}

	// This operation is used to process the extracted command and call the
	// respective functions
	private void processUserCommand(CommandType command) {
		switch (command) {
		case ADD: {
			performAdd();
			break;
		}
		case DELETE: {
			performDelete();
			break;
		}
		case MODIFY: {
			performModify();
			break;
		}
		case DISPLAY: {
			performDisplay();
			break;
		}
		case SEARCH: {
			performSearch();
			break;
		}
		case COMPLETE: {
			performComplete();
			break;
		}
		case UNDO: {
			performUndo();
			break;
		}
		case REDO: {
			performRedo();
			break;
		}
		case EXIT: {
			_taskDataManager.saveAllTasks();
			System.exit(0);
			break;
		}
		case INVALID: {
			_taskView = errorCommandReturn(CommandType.INVALID);
			break;
		}
		default: {
			_taskView = errorCommandReturn(CommandType.INVALID);
			break;
		}
		}
	}

	private void performAdd() {
		updateUndo();
		DataParameter addParameter = processAddParameter(_inputString[PARAMETER_POSITION]);
		if (addParameter == null) {
			_taskView = errorCommandReturn(CommandType.INVALID);
			_commandHistory.removeLatestUndo();
		}
		Task<?> task = _taskDataManager.addTask(addParameter);
		if (task == null) {
			_taskView = errorCommandReturn(CommandType.ADD);
			_commandHistory.removeLatestUndo();
		} else {
			String output = String.format(ADDED_MESSAGE, task.getType(),
					task.getDescription());
			_taskView = updatedTaskView(output, task);
			postUpdateTaskView(task);
			updateUndoAfter();
		}
	}

	private void performDelete() {
		updateUndo();
		DataParameter deleteParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
		Task<?> task = _taskDataManager.deleteTask(deleteParameter);
		if (task == null) {
			_taskView = errorCommandReturn(CommandType.DELETE);
			_commandHistory.removeLatestUndo();
		} else {
			String output = String.format(DELETED_MESSAGE, task.getType(),
					task.getDescription());
			_taskView = updatedTaskView(output, task);
			postUpdateTaskView(task);
			updateUndoAfter();
		}
	}

	private void performModify() {
		updateUndo();
		DataParameter modifyParameter = processModifyParameter(_inputString[PARAMETER_POSITION]);
		if (modifyParameter == null) {
			_taskView = errorCommandReturn(CommandType.INVALID);
			_commandHistory.removeLatestUndo();
		}
		Task<?> task = _taskDataManager.modifyTask(modifyParameter);
		if (task == null) {
			_taskView = errorCommandReturn(CommandType.MODIFY);
			_commandHistory.removeLatestUndo();
		} else {
			String output = String.format(MODIFIED_MESSAGE, task.getType(),
					task.getDescription());
			_taskView = updatedTaskView(output, task);
			postUpdateTaskView(modifyParameter.getTaskObject());
			postUpdateTaskView(task);
			updateUndoAfter();
		}
	}

	private void performDisplay() {
		String filterParameterString = _inputString[PARAMETER_POSITION];
		FilterParameter filterParam;
		if (!filterParameterString.isEmpty()) {
			filterParam = processFilterParameter(filterParameterString);
		} else {
			filterParam = new FilterParameter();
		}
		HashMap<TaskType, ArrayList<Task<?>>> filterResult;
		filterResult = _taskFilterManager.filterTask(filterParam);
		_taskView = new TaskView(RESULTS_DISPLAYED, filterResult);
		_commandHistory
				.updateLatestFilter(processFilterParameter(filterParameterString));
	}

	private void performSearch() {
		SearchParameter searchParameter = processSearchParameter(_inputString[PARAMETER_POSITION]);

		HashMap<TaskType, ArrayList<Task<?>>> searchResult;
		searchResult = _taskFilterManager.searchTasks(searchParameter);

		String output;

		if (searchResult.size() == 0) {
			output = SEARCH_NOT_FOUND;
		} else {
			output = DISPLAYING_SEARCHES;
		}
		_taskView = new TaskView(output, searchResult);
	}

	private void performComplete() {
		updateUndo();
		DataParameter completeParameter = processMarkDeleteParameter(_inputString[PARAMETER_POSITION]);
		Task<?> task = _taskDataManager.markCompleted(completeParameter);
		if (task == null) {
			_taskView = errorCommandReturn(CommandType.COMPLETE);
			_commandHistory.removeLatestUndo();
		} else {
			String output = String.format(COMPLETED_MESSAGE, task.getType(),
					task.getDescription());
			_taskView = updatedTaskView(output, task);
			postUpdateTaskView(task);
			updateUndoAfter();
		}
	}

	private void performUndo() {
		if (_commandHistory.isEmptyUndo()) {
			_taskView = errorCommandReturn(CommandType.UNDO);
		} else {
			int[] pageChangedAfter = _commandHistory.getUndoPageChangedAfter();
			_commandHistory.addRedo(_taskDataManager.getUncompletedTodoTasks(),
					_taskDataManager.getUncompletedDeadlineTasks(),
					_taskDataManager.getUncompletedEventTasks(),
					_taskDataManager.getCompletedTodoTasks(),
					_taskDataManager.getCompletedDeadlineTasks(),
					_taskDataManager.getCompletedEventTasks(),
					_commandHistory.getUndoFilterParameterAfter(),
					_commandHistory.getUndoTabSelectedAfter(),
					pageChangedAfter[0], pageChangedAfter[1],
					pageChangedAfter[2]);

			SortedSet<TodoTask> uncompletedTodoTasks = _commandHistory
					.getUndoTodoUncompleted();
			SortedSet<DeadlineTask> uncompletedDeadlineTasks = _commandHistory
					.getUndoDeadlineUncompleted();
			SortedSet<EventTask> uncompletedEventTasks = _commandHistory
					.getUndoEventUncompleted();
			SortedSet<TodoTask> completedTodoTasks = _commandHistory
					.getUndoTodoCompleted();
			SortedSet<DeadlineTask> completedDeadlineTasks = _commandHistory
					.getUndoDeadlineCompleted();
			SortedSet<EventTask> completedEventTasks = _commandHistory
					.getUndoEventCompleted();
			FilterParameter filterParam = _commandHistory
					.getUndoFilterParameter();
			int tabSelected = _commandHistory.getUndoTabSelected();
			int[] pageChanged = _commandHistory.getUndoPageChanged();
			Cloner cloner = new Cloner();
			FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
			_commandHistory.updateLatestFilter(cloneFilterParam);
			_taskDataManager.updateTrees(uncompletedTodoTasks,
					uncompletedDeadlineTasks, uncompletedEventTasks,
					completedTodoTasks, completedDeadlineTasks,
					completedEventTasks);
			String output = UNDO_MESSAGE;
			_taskView = updatedTaskView(output);
			_taskView.setTabSelected(tabSelected);
			_taskView.setEventPage(pageChanged[0]);
			_taskView.setDeadlinePage(pageChanged[1]);
			_taskView.setTodoPage(pageChanged[2]);
			_commandHistory.addRedoAfter(filterParam,
					_taskView.getTabSelected(), _taskView.getEventPage(),
					_taskView.getDeadlinePage(), _taskView.getTodoPage());
		}
	}

	private void performRedo() {
		if (_commandHistory.isEmptyRedo()) {
			_taskView = errorCommandReturn(CommandType.REDO);
		} else {
			int[] pageChangedAfter = _commandHistory.getRedoPageChangedAfter();
			_commandHistory.addUndo(_taskDataManager.getUncompletedTodoTasks(),
					_taskDataManager.getUncompletedDeadlineTasks(),
					_taskDataManager.getUncompletedEventTasks(),
					_taskDataManager.getCompletedTodoTasks(),
					_taskDataManager.getCompletedDeadlineTasks(),
					_taskDataManager.getCompletedEventTasks(),
					_commandHistory.getRedoFilterParameterAfter(),
					_commandHistory.getRedoTabSelectedAfter(),
					pageChangedAfter[0], pageChangedAfter[1],
					pageChangedAfter[2]);

			SortedSet<TodoTask> uncompletedTodoTasks = _commandHistory
					.getRedoTodoUncompleted();
			SortedSet<DeadlineTask> uncompletedDeadlineTasks = _commandHistory
					.getRedoDeadlineUncompleted();
			SortedSet<EventTask> uncompletedEventTasks = _commandHistory
					.getRedoEventUncompleted();
			SortedSet<TodoTask> completedTodoTasks = _commandHistory
					.getRedoTodoCompleted();
			SortedSet<DeadlineTask> completedDeadlineTasks = _commandHistory
					.getRedoDeadlineCompleted();
			SortedSet<EventTask> completedEventTasks = _commandHistory
					.getRedoEventCompleted();
			FilterParameter filterParam = _commandHistory
					.getRedoFilterParameter();
			int tabSelected = _commandHistory.getRedoTabSelected();
			int[] pageChanged = _commandHistory.getRedoPageChanged();
			Cloner cloner = new Cloner();
			FilterParameter cloneFilterParam = cloner.deepClone(filterParam);
			_commandHistory.updateLatestFilter(cloneFilterParam);
			_taskDataManager.updateTrees(uncompletedTodoTasks,
					uncompletedDeadlineTasks, uncompletedEventTasks,
					completedTodoTasks, completedDeadlineTasks,
					completedEventTasks);
			String output = REDO_MESSAGE;
			_taskView = updatedTaskView(output);
			_taskView.setTabSelected(tabSelected);
			_taskView.setEventPage(pageChanged[0]);
			_taskView.setDeadlinePage(pageChanged[1]);
			_taskView.setTodoPage(pageChanged[2]);
			_commandHistory.addUndoAfter(filterParam,
					_taskView.getTabSelected(), _taskView.getEventPage(),
					_taskView.getDeadlinePage(), _taskView.getTodoPage());
		}
	}

	private void updateUndoAfter() {
		_commandHistory.clearRedo();
		_commandHistory.addUndoAfter(_commandHistory.getLatestFilter(),
				_taskView.getTabSelected(), _taskView.getEventPage(),
				_taskView.getDeadlinePage(), _taskView.getTodoPage());
	}

	private void updateUndo() {
		_commandHistory.addUndo(_taskDataManager.getUncompletedTodoTasks(),
				_taskDataManager.getUncompletedDeadlineTasks(),
				_taskDataManager.getUncompletedEventTasks(),
				_taskDataManager.getCompletedTodoTasks(),
				_taskDataManager.getCompletedDeadlineTasks(),
				_taskDataManager.getCompletedEventTasks(),
				_commandHistory.getLatestFilter(), _taskView.getTabSelected(),
				_taskView.getEventPage(), _taskView.getDeadlinePage(),
				_taskView.getTodoPage());
	}

	private void postUpdateTaskView(Task<?> task) {
		if (task.getType() == TaskType.EVENT) {
			_taskView.setTabSelected(0);
			if (_taskView.getEvents().contains(task)) {
				int page = _taskView.getEvents().indexOf(task)
						/ _taskView.eventPageSize() + 1;
				_taskView.setEventPage(page);
			}
		} else if (task.getType() == TaskType.DEADLINE) {
			_taskView.setTabSelected(1);
			if (_taskView.getDeadlines().contains(task)) {
				int page = _taskView.getDeadlines().indexOf(task)
						/ _taskView.deadlinePageSize() + 1;
				_taskView.setDeadlinePage(page);
			}
		} else {
			_taskView.setTabSelected(2);
			if (_taskView.getTodos().contains(task)) {
				int page = _taskView.getTodos().indexOf(task)
						/ _taskView.todoPageSize() + 1;
				_taskView.setTodoPage(page);
			}
		}
	}

	private TaskView updatedTaskView(String statusMessage) {
		return new TaskView(
				statusMessage,
				_taskFilterManager.filterTask(_commandHistory.getLatestFilter()));
	}

	private TaskView updatedTaskView(String statusMessage, Task<?> task) {
		FilterParameter taskViewFilter = _commandHistory.getLatestFilter();
		boolean filterContainsOnlyTodo = taskViewFilter
				.contains(FilterType.TODO)
				&& !taskViewFilter.contains(FilterType.EVENT)
				&& !taskViewFilter.contains(FilterType.DEADLINE);
		boolean filterContainsOnlyDeadline = taskViewFilter
				.contains(FilterType.DEADLINE)
				&& !taskViewFilter.contains(FilterType.EVENT)
				&& !taskViewFilter.contains(FilterType.TODO);
		boolean filterContainsOnlyEvent = taskViewFilter
				.contains(FilterType.EVENT)
				&& !taskViewFilter.contains(FilterType.DEADLINE)
				&& !taskViewFilter.contains(FilterType.TODO);
		if (filterContainsOnlyTodo && task.getType() != TaskType.TODO) {
			taskViewFilter.remove(FilterType.TODO);
			if (task.getType() == TaskType.EVENT) {
				taskViewFilter.addFilter(FilterType.EVENT);
			} else if (task.getType() == TaskType.DEADLINE) {
				taskViewFilter.addFilter(FilterType.DEADLINE);
			}
		} else if (filterContainsOnlyDeadline
				&& task.getType() != TaskType.DEADLINE) {
			taskViewFilter.remove(FilterType.DEADLINE);
			if (task.getType() == TaskType.EVENT) {
				taskViewFilter.addFilter(FilterType.EVENT);
			} else if (task.getType() == TaskType.TODO) {
				taskViewFilter.addFilter(FilterType.TODO);
			}
		} else if (filterContainsOnlyEvent && task.getType() != TaskType.EVENT) {
			taskViewFilter.remove(FilterType.EVENT);
			if (task.getType() == TaskType.DEADLINE) {
				taskViewFilter.addFilter(FilterType.DEADLINE);
			} else if (task.getType() == TaskType.TODO) {
				taskViewFilter.addFilter(FilterType.TODO);
			}
		}
		_commandHistory.updateLatestFilter(taskViewFilter);
		return new TaskView(statusMessage,
				_taskFilterManager.filterTask(taskViewFilter));
	}

	private TaskView errorCommandReturn(CommandType type) {
		switch (type) {
		case ADD:
			return new TaskView(ADD_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case DELETE:
			return new TaskView(DELETE_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case MODIFY:
			return new TaskView(MODIFY_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case COMPLETE:
			return new TaskView(COMPLETE_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case UNDO:
			return new TaskView(UNDO_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case REDO:
			return new TaskView(REDO_ERROR_MESSAGE,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		case INVALID:
			return new TaskView(INVALID_COMMAND,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		default:
			return new TaskView(INVALID_COMMAND,
					_taskFilterManager.filterTask(_commandHistory
							.getLatestFilter()));
		}
	}

	// This method process add parameter into a DataParameter instance
	// @param parameterString
	// string contains parameter data
	// @return addParam
	// DataParameter instance contains parameter for add method

	public DataParameter processAddParameter(String parameterString) {
		DataParameter addParam = new DataParameter();
		ArrayList<String> parameters = new ArrayList<String>();
		for (String word : parameterString.split(SPACE)) {
			parameters.add(word);
		}
		if (parameters.contains("-start")) {
			addParam.setNewTaskType(TaskType.EVENT);
			int endIndexOfDescription = parameterString.indexOf("-");
			String description = parameterString.substring(0,
					endIndexOfDescription).trim();
			addParam.setDescription(description);
			int indexOfStartDate = parameters.indexOf("-start") + 1;
			int indexOfEndDate = parameters.indexOf("-end") + 1;
			if (indexOfEndDate == 0) {
				addParam = null;
				return addParam;
			}
			try {
				Date startDate = DateUtil.parse(parameters
						.get(indexOfStartDate));
				Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
				if (startDate.before(endDate)) {
					addParam.setStartDate(startDate);
					addParam.setEndDate(endDate);
				} else {
					addParam = null;
					return addParam;
				}
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		} else if (parameters.contains("-end")) {
			addParam.setNewTaskType(TaskType.DEADLINE);
			int endIndexOfDescription = parameterString.indexOf("-");
			String description = parameterString.substring(0,
					endIndexOfDescription).trim();
			addParam.setDescription(description);
			int indexOfEndDate = parameters.indexOf("-end") + 1;
			try {
				Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
				addParam.setEndDate(endDate);
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		} else {
			addParam.setNewTaskType(TaskType.TODO);
			int endIndexOfDescription = parameterString.indexOf("-");
			String description;
			if (endIndexOfDescription != -1) {
				description = parameterString.substring(0,
						endIndexOfDescription).trim();
			} else {
				description = parameterString;
			}
			addParam.setDescription(description);
		}
		if (parameters.contains("-priority")) {
			int indexOfPriority = parameters.indexOf("-priority") + 1;
			char priority = parameters.get(indexOfPriority).toCharArray()[FISRT_ARRAY_INDEX];
			addParam.setPriority(priority);
		}
		if (parameters.contains("-every")) {
			String timeType = parameters.get(parameters.indexOf("-every") + 1)
					.toUpperCase();
			addParam.setTimeType(timeType);
			addParam.setFreqOfTimeType(1);
			addParam.setTag("RECUR");
		}
		if (parameters.contains("-until")) {
			try {
				Date recurEndDate = DateUtil.parse(parameters.get(parameters
						.indexOf("-until") + 1));
				addParam.setEndRecurOn(recurEndDate);
			} catch (Exception e) {
				addParam = null;
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}
		if (addParam.getDescription().equals("")) {
			addParam = null;
		}
		return addParam;
	}

	// This method process search parameter into a SearchParameter instance
	// @param parameterString
	// string contains parameter data
	// @return searchParam
	// SearchParameter instance contains parameter for search method

	public SearchParameter processSearchParameter(String parameterString) {
		ArrayList<String> parameters = new ArrayList<String>();
		for (String word : parameterString.split("//")) {
			parameters.add(word);
		}
		SearchParameter searchParam = new SearchParameter(parameters);
		return searchParam;
	}

	// This method process filter parameter into a FilterParameter instance
	// @param parameterString
	// string contains parameter data
	// @return filterParam
	// FilterParameter instance contains parameter for filter method

	public FilterParameter processFilterParameter(String parameterString) {
		ArrayList<String> parameters = new ArrayList<String>();
		for (String word : parameterString.split(SPACE)) {
			parameters.add(word);
		}
		Date startDate = null;
		Date endDate = null;
		boolean hasStartTime = false;
		boolean hasEndTime = false;

		if (parameters.contains("-end")) {
			int indexOfEndDate = parameters.indexOf("-end") + 1;
			try {
				endDate = DateUtil.parse(parameters.get(indexOfEndDate));
				hasEndTime = hasTime(parameters.get(indexOfEndDate));
				parameters.remove(indexOfEndDate);
				parameters.remove(indexOfEndDate - 1);
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}
		if (parameters.contains("-start")) {
			int indexOfStartDate = parameters.indexOf("-start") + 1;
			try {
				startDate = DateUtil.parse(parameters.get(indexOfStartDate));
				hasStartTime = hasTime(parameters.get(indexOfStartDate));
				parameters.remove(indexOfStartDate);
				parameters.remove(indexOfStartDate - 1);
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}
		FilterParameter filterParam = new FilterParameter(parameters);
		filterParam.setStart(startDate);
		filterParam.setEnd(endDate);
		filterParam.setStartTime(hasStartTime);
		filterParam.setEndTime(hasEndTime);
		if (filterParam.getStart() != null && filterParam.getEnd() != null) {
			if (filterParam.getStart().after(filterParam.getEnd())) {
				filterParam = null;
				return filterParam;
			}
		}
		return filterParam;
	}

	// This method process modify parameter into a DataParameter instance
	// @param parameterString
	// string contains parameter data
	// @return modifyParam
	// DataParameter instance contains parameter for modify method

	private boolean hasTime(String date) {
		String format = DateUtil.determineDateFormat(date);
		return format.contains("HH");
	}

	public DataParameter processModifyParameter(String parameterString) {
		DataParameter modifyParam = new DataParameter();
		ArrayList<String> parameters = new ArrayList<String>();
		for (String word : parameterString.split(" ")) {
			parameters.add(word);
		}
		if (parameters.size()==2){
			modifyParam = null;
			return modifyParam;
		}
		boolean isContainModifyTags = parameters.contains("-description")||
				parameters.contains("-totype")||parameters.contains("-end")||
				parameters.contains("-start")||parameters.contains("-priority");
		if (!isContainModifyTags){
			modifyParam = null;
			return modifyParam;
		}
		TaskType original = processTaskTypeFromString(parameters
				.get(FISRT_ARRAY_INDEX));
		modifyParam.setOriginalTaskType(original);
		int userfriendlyTaskID = Integer.parseInt(parameters
				.get(FISRT_ARRAY_INDEX + 1));
		int pageNum;
		if (original == TaskType.EVENT) {
			pageNum = _currentEventPage;
		} else if (original == TaskType.DEADLINE) {
			pageNum = _currentDeadlinePage;
		} else if (original == TaskType.TODO) {
			pageNum = _currentTodoPage;
		} else {
			pageNum = 0;
		}

		if (modifyParam.getOriginalTaskType() == null
				|| modifyParam.getOriginalTaskType() == TaskType.UNKNOWN) {
			modifyParam = null;
			return modifyParam;
		}

		ArrayList<Task<?>> pageOfModifyObject = _taskView.getPage(original,
				pageNum);
		Task<?> modifyTask = pageOfModifyObject.get(userfriendlyTaskID - 1);
		modifyParam.setTaskObject(modifyTask);
		modifyParam.setTaskID(userfriendlyTaskID);
		if (modifyTask.getTag().contains("RECUR")) {
			modifyParam.setTag("RECUR");
		}
		
		boolean isContainOnlyDescription = parameters.contains("-description")&&
				!parameters.contains("-totype")&&!parameters.contains("-end")&&
				!parameters.contains("-start")&&!parameters.contains("-priority");
		if (parameters.contains("-all")) {
			if (isContainOnlyDescription){
				modifyParam.setModifyAll(true);
			} else {
				modifyParam = null;
				return modifyParam;
			}
		}

		if (parameters.contains("-every")) {
			String timeType = parameters.get(parameters.indexOf("-every") + 1)
					.toUpperCase();
			modifyParam.setTimeType(timeType);
			modifyParam.setFreqOfTimeType(1);
		}

		if (parameters.contains("-until")) {
			try {
				Date recurEndDate = DateUtil.parse(parameters.get(parameters
						.indexOf("-until") + 1));
				modifyParam.setEndRecurOn(recurEndDate);
			} catch (Exception e) {
				modifyParam = null;
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}

		if (parameters.contains("-totype")) {
			int indexOfNewTaskType = parameters.indexOf("-totype") + 1;
			TaskType newType = processTaskTypeFromString(parameters
					.get(indexOfNewTaskType));
			modifyParam.setNewTaskType(newType);
		} else {
			modifyParam.setNewTaskType(original);
		}
		if (parameters.contains("-end")) {
			int indexOfEndDate = parameters.indexOf("-end") + 1;
			try {
				Date endDate = DateUtil.parse(parameters.get(indexOfEndDate));
				modifyParam.setEndDate(endDate);
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}
		// NOTE: this can only detect error when user want to modify both start
		// and end date, but not only one.
		if (parameters.contains("-start")) {
			int indexOfStartDate = parameters.indexOf("-start") + 1;
			try {
				Date startDate = DateUtil.parse(parameters
						.get(indexOfStartDate));
				modifyParam.setStartDate(startDate);
				if (modifyParam.getEndDate() != null) {
					if (modifyParam.getStartDate().after(
							modifyParam.getEndDate())) {
						modifyParam = null;
						return modifyParam;
					}
				}
			} catch (ParseException e) {
				LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
			}
		}
		if (parameters.contains("-description")) {
			String newDescription;
			int indexOfDescription = parameterString.indexOf("-description");
			newDescription = parameterString.substring(indexOfDescription + 13);
			int indexOfEndOfDescription = newDescription.indexOf("-");
			if (indexOfEndOfDescription != -1) {
				newDescription = newDescription.substring(0,
						indexOfEndOfDescription - 1);
			}
			assert(newDescription!=EMPTY_STRING);
			modifyParam.setDescription(newDescription);
		}
		if (modifyParam.getOriginalTaskType() == TaskType.TODO
				&& modifyParam.getNewTaskType() == TaskType.DEADLINE
				&& modifyParam.getEndDate() == null) {
			modifyParam = null;
			return modifyParam;
		}
		if (modifyParam.getOriginalTaskType() == TaskType.TODO
				&& modifyParam.getNewTaskType() == TaskType.EVENT
				&& (modifyParam.getEndDate() == null || modifyParam
						.getStartDate() == null)) {
			modifyParam = null;
			return modifyParam;
		}
		if (modifyParam.getOriginalTaskType() == TaskType.DEADLINE
				&& modifyParam.getNewTaskType() == TaskType.EVENT
				&& modifyParam.getStartDate() == null) {
			modifyParam = null;
			return modifyParam;
		}
		if (modifyParam.getOriginalTaskType() == TaskType.TODO
				&& modifyParam.getNewTaskType() == TaskType.TODO
				&& (modifyParam.getStartDate() != null || modifyParam
						.getEndDate() != null)) {
			modifyParam = null;
			return modifyParam;
		}
		if (modifyParam.getOriginalTaskType() == TaskType.DEADLINE
				&& modifyParam.getNewTaskType() == TaskType.DEADLINE
				&& modifyParam.getStartDate() != null) {
			modifyParam = null;
			return modifyParam;
		}
		if (modifyParam.getStartDate() != null
				|| modifyParam.getEndDate() != null) {
			if (modifyParam.getStartDate() != null
					&& modifyParam.getEndDate() != null) {
				if (modifyParam.getStartDate().after(modifyParam.getEndDate())) {
					modifyParam = null;
					return modifyParam;
				}
			} else if (modifyParam.getStartDate() != null) {
				Task<?> toModifyTask = modifyParam.getTaskObject();
				if (toModifyTask.getType() == TaskType.EVENT) {
					if (modifyParam.getStartDate().after(
							((EventTask) toModifyTask).getEndTime())) {
						modifyParam = null;
						return modifyParam;
					}
				} else if (toModifyTask.getType() == TaskType.DEADLINE) {
					if (modifyParam.getStartDate().after(
							((DeadlineTask) toModifyTask).getEndTime())) {
						modifyParam = null;
						return modifyParam;
					}
				}
			} else if (modifyParam.getEndDate() != null) {
				Task<?> toModifyTask = modifyParam.getTaskObject();
				if (toModifyTask.getType() == TaskType.EVENT) {
					if (modifyParam.getEndDate().before(
							((EventTask) toModifyTask).getStartTime())) {
						modifyParam = null;
						return modifyParam;
					}
				}
			}
		}
		if (parameters.contains("-priority")) {
			int indexOfPriority = parameters.indexOf("-priority") + 1;
			char priority = parameters.get(indexOfPriority).toCharArray()[FISRT_ARRAY_INDEX];
			modifyParam.setPriority(priority);
		} else {
			modifyParam.setPriority(modifyParam.getTaskObject().getPriority());
		}
		return modifyParam;
	}

	// This method process delete/complete parameter into a DataParameter
	// instance
	// @param parameterString
	// string contains parameter data
	// @return markDeleteParam
	// DataParameter instance contains parameter for delete/complete method

	public DataParameter processMarkDeleteParameter(String parameterString) {
		DataParameter markDeleteParam = new DataParameter();
		ArrayList<String> parameters = new ArrayList<String>();
		for (String word : parameterString.split(" ")) {
			parameters.add(word);
		}
		TaskType original = processTaskTypeFromString(parameters
				.get(FISRT_ARRAY_INDEX));
		markDeleteParam.setOriginalTaskType(original);
		int userfriendlyTaskID = Integer.parseInt(parameters
				.get(FISRT_ARRAY_INDEX + 1));
		int pageNum;
		if (original == TaskType.EVENT) {
			pageNum = _currentEventPage;
		} else if (original == TaskType.DEADLINE) {
			pageNum = _currentDeadlinePage;
		} else if (original == TaskType.TODO) {
			pageNum = _currentTodoPage;
		} else {
			pageNum = 0;
		}

		if (markDeleteParam.getOriginalTaskType() == null
				|| markDeleteParam.getOriginalTaskType() == TaskType.UNKNOWN) {
			markDeleteParam = null;
			return markDeleteParam;
		}

		ArrayList<Task<?>> pageOfMarkDeleteObject = _taskView.getPage(original,
				pageNum);
		if (parameters.contains("-all")) {
			markDeleteParam.setModifyAll(true);
		}
		Task<?> markDeleteTask = pageOfMarkDeleteObject
				.get(userfriendlyTaskID - 1);
		markDeleteParam.setTaskObject(markDeleteTask);
		markDeleteParam.setTaskID(userfriendlyTaskID);

		return markDeleteParam;
	}

	// This method process TaskType from String
	// @param taskTypeString
	// string contains task type
	// @return taskType
	// TaskType indicated by taskTypeString

	public TaskType processTaskTypeFromString(String taskTypeString) {
		if (taskTypeString.equals(TaskType.TODO.getType())) {
			return TaskType.TODO;
		} else if (taskTypeString.equals(TaskType.DEADLINE.getType())) {
			return TaskType.DEADLINE;
		} else if (taskTypeString.equals(TaskType.EVENT.getType())) {
			return TaskType.EVENT;
		} else {
			return TaskType.UNKNOWN;
		}
	}
}
