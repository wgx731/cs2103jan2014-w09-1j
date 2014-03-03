package sg.edu.nus.cs2103t.mina.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.SyncDataParameter;

/**
 * Task data manager: checks user's input determines the type of tasks breaks up
 * parameters for the tasks passes tasks to DAO to retrieve data from files
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class TaskDataManager {

    private static Logger logger = LogManager.getLogger(TaskDataManager.class
            .getName());

    // error messages
    public static final int ERROR_MISSING_TASK_DESCRIPTION = -2;

    // parameters of String after trimming
    public static final int PARAM_TASK_DESCRIPTION = 0;
    public static final int SYNC_LIST_MAX = 6;

    private SortedSet<TodoTask> _todoTasks;
    private SortedSet<DeadlineTask> _deadlineTasks;
    private SortedSet<EventTask> _eventTasks;

    private SortedSet<TodoTask> _compTodoTasks;
    private SortedSet<EventTask> _compEventTasks;
    private SortedSet<DeadlineTask> _compDeadlineTasks;

    private List<SyncDataParameter> _syncList;

    public TaskDataManager() {
        // empty constructor class
        _todoTasks = new TreeSet<TodoTask>();
        _deadlineTasks = new TreeSet<DeadlineTask>();
        _eventTasks = new TreeSet<EventTask>();
        _compTodoTasks = new TreeSet<TodoTask>();
        _compDeadlineTasks = new TreeSet<DeadlineTask>();
        _compEventTasks = new TreeSet<EventTask>();
        _syncList = new ArrayList<SyncDataParameter>(SYNC_LIST_MAX);
    }

    @SuppressWarnings("unchecked")
    public TaskDataManager(TaskDao storage) {
        SortedSet<? extends Task<?>> tempTasks = null;
        try {
            tempTasks = storage.loadTaskSet(TaskType.TODO, false);
            _todoTasks = tempTasks == null ? new TreeSet<TodoTask>()
                    : (SortedSet<TodoTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.DEADLINE, false);
            _deadlineTasks = tempTasks == null ? new TreeSet<DeadlineTask>()
                    : (SortedSet<DeadlineTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.EVENT, false);
            _eventTasks = tempTasks == null ? new TreeSet<EventTask>()
                    : (SortedSet<EventTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.TODO, true);
            _compTodoTasks = tempTasks == null ? new TreeSet<TodoTask>()
                    : (SortedSet<TodoTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.DEADLINE, true);
            _compDeadlineTasks = tempTasks == null ? new TreeSet<DeadlineTask>()
                    : (SortedSet<DeadlineTask>) tempTasks;

            tempTasks = storage.loadTaskSet(TaskType.EVENT, true);
            _compEventTasks = tempTasks == null ? new TreeSet<EventTask>()
                    : (SortedSet<EventTask>) tempTasks;

            _syncList = new ArrayList<SyncDataParameter>(SYNC_LIST_MAX);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    public SortedSet<TodoTask> getTodoTasks() {
        return _todoTasks;
    }

    public SortedSet<DeadlineTask> getDeadlineTasks() {
        return _deadlineTasks;
    }

    public SortedSet<EventTask> getEventTasks() {
        return _eventTasks;
    }

    public SortedSet<TodoTask> getCompTodoTasks() {
        return _compTodoTasks;
    }

    public SortedSet<EventTask> getCompEventTasks() {
        return _compEventTasks;
    }

    public SortedSet<DeadlineTask> getCompDeadlineTasks() {
        return _compDeadlineTasks;
    }

    public List<SyncDataParameter> getSyncList() {
        return _syncList;
    }

    public void clearSyncList() {
        _syncList.clear();
    }

    /**
     * This method creates a Task depending on its type and parameters. If
     * changes are successfully saved by DAO, it returns a Task object to the
     * method which called it.
     * 
     * @param String addParameters
     * @return
     */
    public Task<?> addTask(String addParameters) {
        switch (determineTaskType(addParameters)) {
            case TODO:
                TodoTask newTodoTask = createTodoTask(addParameters);
                if (_todoTasks.add(newTodoTask)) {
                    return newTodoTask;
                }
                break;
            case DEADLINE:
                DeadlineTask newDeadlineTask = createDeadlineTask(addParameters);
                if (_deadlineTasks.add(newDeadlineTask)) {
                    return newDeadlineTask;
                }
                break;
            case EVENT:
                EventTask newEventTask = createEventTask(addParameters);
                if (_eventTasks.add(newEventTask)) {
                    return newEventTask;
                }
                break;
            default:
                break;
        }
        return null;
    }

    // TODO: change to private afterwards
    // does not check for null parameters, this will be checked at addTask()
    public TaskType determineTaskType(String addParameters) {
        if (addParameters.equals("")) {
            return TaskType.UNKOWN;
        }

        if (addParameters.contains("-end")) {
            if (addParameters.contains("-start")) {
                return TaskType.EVENT;
            } else {
                return TaskType.DEADLINE;
            }
        } else {
            return TaskType.TODO;
        }
    }

    public TodoTask createTodoTask(String addParameters) {
        TodoTask newTodo = null;

        if (!addParameters.contains("-priority")) {
            newTodo = new TodoTask(addParameters);

        } else {
            String[] parameters = addParameters.split("-priority");
            char[] priority = parameters[1].trim().toCharArray();

            // TODO: check > 1 instances of "-priority"
            // TODO: check valid value for priority

            newTodo = new TodoTask(parameters[PARAM_TASK_DESCRIPTION].trim(),
                    priority[0]);
        }

        return newTodo;
    }

    public DeadlineTask createDeadlineTask(String addParameters) {
        String[] parameters = addParameters.split("-end|-priority");
        String description = parameters[PARAM_TASK_DESCRIPTION].trim();
        String endTimeString = parameters[1].trim();

        // TODO: use this method to accept and check for different formats
        SimpleDateFormat slashFormatter = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss");
        Date endTime;
        try {
            endTime = slashFormatter.parse(endTimeString + " 23:59:00"); // default,
                                                                         // deadline
                                                                         // submission
                                                                         // 2359
            if (parameters.length == 2) {
                return new DeadlineTask(description, endTime);
            } else {
                char[] priority = parameters[2].trim().toCharArray();

                return new DeadlineTask(description, endTime, priority[0]);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public EventTask createEventTask(String addParameters) {
        // handle parameters
        String[] parameters = addParameters.split("-start|-end|-priority");

        String description = parameters[PARAM_TASK_DESCRIPTION].trim();

        String startTimeString = parameters[1].trim();
        String endTimeString = parameters[2].trim();

        // handle date formats
        if (startTimeString.contains(" ") && !endTimeString.contains(" ")) {
            String[] startTimeParam = startTimeString.split(" ");
            endTimeString = startTimeParam[0] + " " + endTimeString;
        }

        Date startTime, endTime;

        // TODO: use this method to accept and check for different formats
        SimpleDateFormat slashFormatter = new SimpleDateFormat(
                "dd/MM/yyyy HHmm");

        try {
            startTime = slashFormatter.parse(startTimeString);
            endTime = slashFormatter.parse(endTimeString);

            // actual creation of events
            if (parameters.length == 3) {
                return new EventTask(description, startTime, endTime);
            } else {
                char[] priority = parameters[3].trim().toCharArray();

                return new EventTask(description, startTime, endTime,
                        priority[0]);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    // TODO: add a method that checks if priority is valid
    // TODO: add a method that converts any form of a date to a Date

    /**
     * This method deletes a specific task by identifying the Task with its type
     * and id number. If changes are successfully saved by DAO, it returns a
     * Task object to the method which called it.
     * 
     * @param deleteParameters
     * @return Task<?> (if successful), null otherwise
     */
    public Task<?> deleteTask(String deleteParameters) {
        String[] parameters = deleteParameters.split(" ", 2);
        String taskTypeString = parameters[0].trim();

        switch (taskTypeString) {
            case ("todo"):
                return deleteTodo(Integer.parseInt(parameters[1].trim()));
            case ("deadline"):
                return deleteDeadline(Integer.parseInt(parameters[1].trim()));
            case ("event"):
                return deleteEvent(Integer.parseInt(parameters[1].trim()));
            default:
                break;
        }
        System.out.println("Unregconised task: " + taskTypeString);
        return null;
    }

    private TodoTask deleteTodo(int todoId) {
        TreeSet<TodoTask> tempTodoTreeSet = new TreeSet<TodoTask>();

        for (int i = 0; i < (todoId - 1); i++) {
            tempTodoTreeSet.add(((TreeSet<TodoTask>) _todoTasks).pollFirst());
        }

        TodoTask removedTodo = ((TreeSet<TodoTask>) _todoTasks).pollFirst();
        _todoTasks.addAll(tempTodoTreeSet);

        return removedTodo;
    }

    private DeadlineTask deleteDeadline(int deadlineId) {
        TreeSet<DeadlineTask> tempDeadlineTreeSet = new TreeSet<DeadlineTask>();

        for (int i = 0; i < (deadlineId - 1); i++) {
            tempDeadlineTreeSet.add(((TreeSet<DeadlineTask>) _deadlineTasks)
                    .pollFirst());
        }

        DeadlineTask removeDeadline = ((TreeSet<DeadlineTask>) _deadlineTasks)
                .pollFirst();
        _deadlineTasks.addAll(tempDeadlineTreeSet);

        return removeDeadline;
    }

    private EventTask deleteEvent(int eventId) {
        TreeSet<EventTask> tempEventTreeSet = new TreeSet<EventTask>();

        for (int i = 0; i < (eventId - 1); i++) {
            tempEventTreeSet
                    .add(((TreeSet<EventTask>) _eventTasks).pollFirst());
        }

        EventTask removeEvent = ((TreeSet<EventTask>) _eventTasks).pollFirst();
        _eventTasks.addAll(tempEventTreeSet);

        return removeEvent;
    }

    /**
     * This method checks what Task the user wants to modify, calls the command
     * of DAO to make the amendments, then returns the modified task.
     * 
     * @param modifyParameters
     * @return
     */
    public Task<?> modifyTask(String modifyParameters) {
        if (modifyParameters.contains("-totype")) {
            String[] modifyTypeParameters = modifyParameters
                    .split("-totype", 2);
            return modifyTaskType(modifyTypeParameters[0].trim(),
                    modifyTypeParameters[1].trim());
        } else {
            String[] modifyTypeParameters = modifyParameters.split(" ", 3);
            return modifyTaskParameters(modifyTypeParameters[0].trim() + " "
                    + modifyTypeParameters[1].trim(),
                    modifyTypeParameters[2].trim());
        }
    }

    /**
     * Retrieves existing Task object to be modified, calls the commands to
     * modify it, then return the modified Task object.
     * 
     * @param oldParameters
     * @param newParmeters
     * @return
     */
    private Task<?> modifyTaskType(String oldParameters, String newParameters) {
        // assume that id is correct

        String[] oldTaskParametersTokens = oldParameters.split(" ", 2);
        String oldTaskTypeString = oldTaskParametersTokens[0].trim();
        int oldTaskId = Integer.parseInt(oldTaskParametersTokens[1].trim());

        String[] newTaskParametersTokens = newParameters.split(" ", 2);
        String newTaskTypeString = newTaskParametersTokens[0].trim();
        String newTaskParameters = null;
        if (newTaskParametersTokens.length == 2) {
            newTaskParameters = newTaskParametersTokens[1].trim();
        }

        if (!hasSufficientParameters(oldTaskTypeString, newTaskTypeString,
                newTaskParameters)) {
            System.out.println("Insufficient parameters.");
            return null; // or throw error ?
        }

        switch (oldTaskTypeString) {
            case ("todo"):
                return modifyTodoTaskType(oldTaskId, newTaskTypeString,
                        newTaskParameters);
            case ("deadline"):
                return modifyDeadlineTaskType(oldTaskId, newTaskTypeString,
                        newTaskParameters);
            case ("event"):
                return modifyEventTaskType(oldTaskId, newTaskTypeString,
                        newTaskParameters);
            default:
                break;
        }

        return null;
    }

    // assumes that parameter strings are correct
    private boolean hasSufficientParameters(String oldTaskTypeString,
            String newTaskTypeString, String newTaskParameters) {
        if (oldTaskTypeString.equals("todo")
                && newTaskTypeString.equals("deadline")) {
            return newTaskParameters.contains("-end");
        }
        if (oldTaskTypeString.equals("todo")
                && newTaskTypeString.equals("event")) {
            return newTaskParameters.contains("-end")
                    && newTaskParameters.contains("-start");
        }
        if (oldTaskTypeString.equals("todo")
                && newTaskTypeString.equals("event")) {
            return newTaskParameters.contains("-start");
        }

        return true;
    }

    private Task<?> modifyTodoTaskType(int oldTaskId, String newTaskTypeString,
            String newTaskParameters) {
        try {
            if (newTaskTypeString.equals("deadline")) {
                return modifyTodoTaskToDeadlineTask(oldTaskId,
                        newTaskParameters);

            }

            if (newTaskTypeString.equals("event")) {
                return modifyTodoTaskToEventTask(oldTaskId, newTaskParameters);
            }

            System.out.println("Unregconised Task: " + newTaskTypeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("something wierd happened");
        return null;
    }

    private DeadlineTask modifyTodoTaskToDeadlineTask(int oldTaskId,
            String newTaskParameters) throws ParseException {
        TodoTask oldTodo = deleteTodo(oldTaskId);

        String description = oldTodo.getDescription();
        String endTimeString = null;
        char priority = oldTodo.getPriority();
        String[] newTaskParametersTokens = newTaskParameters
                .split("-description|-end|-priority");

        if (newTaskParametersTokens.length == 2) {
            endTimeString = newTaskParametersTokens[1].trim();

        } else if (newTaskParametersTokens.length == 3) {
            if (newTaskParameters.contains("-priority")) {
                endTimeString = newTaskParametersTokens[1].trim();
                priority = newTaskParametersTokens[2].trim().charAt(0);
            }

            if (newTaskParameters.contains("-description")) {
                description = newTaskParametersTokens[1].trim();
                endTimeString = newTaskParametersTokens[2].trim();
            }

        } else if (newTaskParametersTokens.length == 4) {
            description = newTaskParametersTokens[1].trim();
            endTimeString = newTaskParametersTokens[2].trim();
            priority = newTaskParametersTokens[3].trim().charAt(0);

        } else {
            // shouldn't happen
            System.out.println("Wrong number of tokens for deadline.");
            return null;
        }

        SimpleDateFormat slashFormatter = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss");
        Date newEndTime = slashFormatter.parse(endTimeString + " 23:59:00");

        DeadlineTask newDeadline = new DeadlineTask(description, newEndTime,
                priority);

        if (_deadlineTasks.add(newDeadline)) {
            return newDeadline;
        } else {
            System.out.println("Unable to add deadline.");
            return null;
        }
    }

    private EventTask modifyTodoTaskToEventTask(int oldTaskId,
            String newTaskParameters) throws ParseException {
        TodoTask oldTodo = deleteTodo(oldTaskId);

        String description = oldTodo.getDescription();
        String startTimeString = null;
        String endTimeString = null;
        char priority = oldTodo.getPriority();

        String[] newTaskParametersTokens = newTaskParameters
                .split("-description|-start|-end|-priority");

        SimpleDateFormat slashFormatter = new SimpleDateFormat(
                "dd/MM/yyyy HHmm");

        if (newTaskParametersTokens.length == 3) {
            // description and priority not overwritten
            startTimeString = newTaskParametersTokens[1].trim();
            endTimeString = newTaskParametersTokens[2].trim();

        } else if (newTaskParametersTokens.length == 4) {
            if (newTaskParameters.contains("-description")) {
                // only description overwritten
                description = newTaskParametersTokens[1].trim();
                startTimeString = newTaskParametersTokens[2].trim();
                endTimeString = newTaskParametersTokens[3].trim();
            }

            if (newTaskParameters.contains("-priority")) {
                // only priority overwritten
                startTimeString = newTaskParametersTokens[1].trim();
                endTimeString = newTaskParametersTokens[2].trim();
                priority = newTaskParametersTokens[3].trim().charAt(0);
            }

        } else if (newTaskParametersTokens.length == 5) {
            // both description and priority overwritten
            description = newTaskParametersTokens[1].trim();
            startTimeString = newTaskParametersTokens[2].trim();
            endTimeString = newTaskParametersTokens[3].trim();
            priority = newTaskParametersTokens[4].trim().charAt(0);

        } else {
            // shouldn't happen
            System.out.println("Wrong number of tokens for event.");
            return null;
        }

        // handle date formats
        if (startTimeString.contains(" ") && !endTimeString.contains(" ")) {
            String[] startTimeParam = startTimeString.split(" ");
            endTimeString = startTimeParam[0] + " " + endTimeString;
        }

        Date newStartTime = slashFormatter.parse(startTimeString);
        Date newEndTime = slashFormatter.parse(endTimeString);

        EventTask newEvent = new EventTask(description, newStartTime,
                newEndTime, priority);

        if (_eventTasks.add(newEvent)) {
            return newEvent;
        } else {
            System.out.println("Unable to add event.");
            return null;
        }

    }

    private Task<?> modifyDeadlineTaskType(int oldTaskId,
            String newTaskTypeString, String newTaskParameters) {
        if (newTaskTypeString.equals("todo")) {
            return modifyDeadlineTaskToTodoTask(oldTaskId, newTaskParameters);

        }

        if (newTaskTypeString.equals("event")) {
            return modifyDeadlineTaskToEventTask(oldTaskId, newTaskParameters);
        }

        System.out.println("Unregconised Task: " + newTaskTypeString);

        System.out.println("something wierd happened");

        return null;
    }

    private TodoTask modifyDeadlineTaskToTodoTask(int oldTaskId,
            String newTaskParameters) {
        DeadlineTask oldDeadlineTask = deleteDeadline(oldTaskId);

        DateFormat formatSlash = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String description = oldDeadlineTask.getDescription() + " by "
                + formatSlash.format(oldDeadlineTask.getEndTime());
        char priority = oldDeadlineTask.getPriority();

        if (newTaskParameters != null) {
            String[] newTaskParametersTokens = newTaskParameters
                    .split("-description|-priority");
            if (newTaskParametersTokens.length == 3) {
                // overwrite description and priority
                description = newTaskParametersTokens[1].trim() + " by "
                        + formatSlash.format(oldDeadlineTask.getEndTime());
                priority = newTaskParametersTokens[2].trim().charAt(0);
            }

            if (newTaskParametersTokens.length == 2
                    && newTaskParameters.contains("-description")) {
                // overwrite description only
                description = newTaskParametersTokens[1].trim() + " by "
                        + formatSlash.format(oldDeadlineTask.getEndTime());
            }

            if (newTaskParametersTokens.length == 2
                    && newTaskParameters.contains("-priority")) {
                // overwrite priority only
                priority = newTaskParametersTokens[1].trim().charAt(0);
            }
        }
        TodoTask newTodo = new TodoTask(description, priority);

        if (_todoTasks.add(newTodo)) {
            return newTodo;
        } else {
            System.out.println("Unable to add todo.");
            return null;
        }
    }

    private EventTask modifyDeadlineTaskToEventTask(int oldTaskId,
            String newTaskParameters) {
        DeadlineTask oldDeadlineTask = deleteDeadline(oldTaskId);

        String description = oldDeadlineTask.getDescription();
        Date startDate = null;
        Date endDate = oldDeadlineTask.getEndTime();
        char priority = oldDeadlineTask.getPriority();

        try {
            SimpleDateFormat slashFormatter = new SimpleDateFormat(
                    "dd/MM/yyyy HHmm");

            String[] newTaskParametersTokens = newTaskParameters
                    .split("-description|-start|-end|-priority");
            if (newTaskParametersTokens.length == 5) {
                // overwrite description, end and priority

                String startTimeString = newTaskParametersTokens[2].trim();
                String endTimeString = newTaskParametersTokens[3].trim();

                // handle date formats
                if (startTimeString.contains(" ")
                        && !endTimeString.contains(" ")) {
                    String[] startTimeParam = startTimeString.split(" ");
                    endTimeString = startTimeParam[0] + " " + endTimeString;
                }

                description = newTaskParametersTokens[1].trim();
                startDate = slashFormatter.parse(startTimeString);
                endDate = slashFormatter.parse(endTimeString);
                priority = newTaskParametersTokens[4].trim().charAt(0);

            }

            if (newTaskParametersTokens.length == 4
                    && !newTaskParameters.contains("-description")) {
                // overwrite end and priority only

                String startTimeString = newTaskParametersTokens[1].trim();
                String endTimeString = newTaskParametersTokens[2].trim();

                // handle date formats
                if (startTimeString.contains(" ")
                        && !endTimeString.contains(" ")) {
                    String[] startTimeParam = startTimeString.split(" ");
                    endTimeString = startTimeParam[0] + " " + endTimeString;
                }

                startDate = slashFormatter.parse(startTimeString);
                endDate = slashFormatter.parse(endTimeString);
                priority = newTaskParametersTokens[3].trim().charAt(0);

            }

            if (newTaskParametersTokens.length == 4
                    && !newTaskParameters.contains("-priority")) {
                // overwrite description and end only

                String startTimeString = newTaskParametersTokens[2].trim();
                String endTimeString = newTaskParametersTokens[3].trim();
                // handle date formats
                if (startTimeString.contains(" ")
                        && !endTimeString.contains(" ")) {
                    String[] startTimeParam = startTimeString.split(" ");
                    endTimeString = startTimeParam[0] + " " + endTimeString;
                }
                description = newTaskParametersTokens[1].trim();
                startDate = slashFormatter.parse(startTimeString);
                endDate = slashFormatter.parse(endTimeString);
            }

            if (newTaskParametersTokens.length == 3
                    && newTaskParameters.contains("-description")) {
                // overwrite description only

                description = newTaskParametersTokens[1].trim();
                startDate = slashFormatter.parse(newTaskParametersTokens[2]
                        .trim());
            }

            if (newTaskParametersTokens.length == 3
                    && newTaskParameters.contains("-end")) {
                // overwrite end only

                String startTimeString = newTaskParametersTokens[1].trim();
                String endTimeString = newTaskParametersTokens[2].trim();

                // handle date formats
                if (startTimeString.contains(" ")
                        && !endTimeString.contains(" ")) {
                    String[] startTimeParam = startTimeString.split(" ");
                    endTimeString = startTimeParam[0] + " " + endTimeString;
                }

                startDate = slashFormatter.parse(startTimeString);
                endDate = slashFormatter.parse(endTimeString);
            }

            if (newTaskParametersTokens.length == 3
                    && newTaskParameters.contains("-priority")) {
                // overwrite priority only

                String startTimeString = newTaskParametersTokens[1].trim();
                // handle date formats
                if (!startTimeString.contains(" ")) {
                    DateFormat formatSlash = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss");

                    startTimeString = formatSlash.format(oldDeadlineTask
                            .getEndTime()) + " " + startTimeString;
                }

                startDate = slashFormatter.parse(startTimeString);
                priority = newTaskParametersTokens[2].trim().charAt(0);
            }

            if (newTaskParametersTokens.length == 2) {
                // not overwriting anything

                startDate = slashFormatter.parse(newTaskParametersTokens[1]
                        .trim());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        EventTask newEvent = new EventTask(description, startDate, endDate,
                priority);

        if (_eventTasks.add(newEvent)) {
            return newEvent;
        } else {
            System.out.println("Unable to add event.");
            return null;
        }
    }

    private Task<?> modifyEventTaskType(int oldTaskId,
            String newTaskTypeString, String newTaskParameters) {
        if (newTaskTypeString.equals("todo")) {
            return modifyEventTaskToTodoTask(oldTaskId, newTaskParameters);

        }

        if (newTaskTypeString.equals("deadline")) {
            return modifyEventTaskToDeadlineTask(oldTaskId, newTaskParameters);
        }

        System.out.println("Unregconised Task: " + newTaskTypeString);

        System.out.println("something wierd happened");

        return null;
    }

    private TodoTask modifyEventTaskToTodoTask(int oldTaskId,
            String newTaskParameters) {
        EventTask oldEventTask = deleteEvent(oldTaskId);

        DateFormat formatSlash = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String description = oldEventTask.getDescription() + " from "
                + formatSlash.format(oldEventTask.getStartTime()) + " to "
                + formatSlash.format(oldEventTask.getEndTime());
        char priority = oldEventTask.getPriority();

        if (newTaskParameters != null) {
            String[] newTaskParametersTokens = newTaskParameters
                    .split("-description|-priority");
            if (newTaskParametersTokens.length == 3) {
                // description and priority are being overwritten
                description = newTaskParametersTokens[1].trim() + " from "
                        + formatSlash.format(oldEventTask.getStartTime())
                        + " to "
                        + formatSlash.format(oldEventTask.getEndTime());
                priority = newTaskParametersTokens[2].trim().charAt(0);
            }

            if (newTaskParametersTokens.length == 2
                    && newTaskParameters.contains("-description")) {
                // only description is being overwritten
                description = newTaskParametersTokens[1].trim() + " from "
                        + formatSlash.format(oldEventTask.getStartTime())
                        + " to "
                        + formatSlash.format(oldEventTask.getEndTime());
            }

            if (newTaskParametersTokens.length == 2
                    && newTaskParameters.contains("-priority")) {
                // only priority is being overwritten
                priority = newTaskParametersTokens[1].trim().charAt(0);
            }

        } // else nothing else is modified

        TodoTask newTodo = new TodoTask(description, priority);

        if (_todoTasks.add(newTodo)) {
            return newTodo;
        } else {
            System.out.println("Unable to add event.");
            return null;
        }
    }

    // note: unlike the other methods which do not drop information unless it's
    // overwritten, modifyEventTaskToDeadlineTask will drop endTime parameter,
    // and take the startTime parameter as DeadlineTask's endTime parameter.
    private DeadlineTask modifyEventTaskToDeadlineTask(int oldTaskId,
            String newTaskParameters) {
        EventTask oldEventTask = deleteEvent(oldTaskId);

        SimpleDateFormat slashFormatter = new SimpleDateFormat(
                "dd/MM/yyyy HHmm");

        String description = oldEventTask.getDescription();

        Date endDate = oldEventTask.getStartTime();
        char priority = oldEventTask.getPriority();

        try {
            if (newTaskParameters != null) {
                String[] newTaskParametersTokens = newTaskParameters
                        .split("-description|-end|-priority");

                if (newTaskParametersTokens.length == 4) {
                    // description, end and priority are being overwritten
                    description = newTaskParametersTokens[1].trim();

                    String endTimeString = newTaskParametersTokens[2].trim();

                    // handle date formats
                    if (!endTimeString.contains(" ")) {
                        DateFormat formatSlash = new SimpleDateFormat(
                                "dd/MM/yyyy");

                        endTimeString = formatSlash.format(oldEventTask
                                .getStartTime()) + " " + endTimeString;
                    }
                    endDate = slashFormatter.parse(endTimeString);

                    priority = newTaskParametersTokens[3].trim().charAt(0);
                }

                if (newTaskParametersTokens.length == 3
                        && !newTaskParameters.contains("-description")) {
                    // end and priority are being overwritten only

                    String endTimeString = newTaskParametersTokens[1].trim();

                    // handle date formats
                    if (!endTimeString.contains(" ")) {
                        DateFormat formatSlash = new SimpleDateFormat(
                                "dd/MM/yyyy");

                        endTimeString = formatSlash.format(oldEventTask
                                .getStartTime()) + " " + endTimeString;
                    }

                    endDate = slashFormatter.parse(endTimeString);
                    priority = newTaskParametersTokens[2].trim().charAt(0);
                }

                if (newTaskParametersTokens.length == 3
                        && !newTaskParameters.contains("-end")) {
                    // description and priority are being overwritten only
                    description = newTaskParametersTokens[1].trim();
                    priority = newTaskParametersTokens[2].trim().charAt(0);
                }

                if (newTaskParametersTokens.length == 3
                        && !newTaskParameters.contains("-priority")) {
                    // description and end are being overwritten only
                    description = newTaskParametersTokens[1].trim();

                    String endTimeString = newTaskParametersTokens[2].trim();

                    // handle date formats
                    if (!endTimeString.contains(" ")) {
                        DateFormat formatSlash = new SimpleDateFormat(
                                "dd/MM/yyyy");

                        endTimeString = formatSlash.format(oldEventTask
                                .getStartTime()) + " " + endTimeString;
                    }

                    endDate = slashFormatter.parse(endTimeString);
                }

                if (newTaskParametersTokens.length == 2
                        && newTaskParameters.contains("-description")) {
                    // only description is being overwritten
                    description = newTaskParametersTokens[1].trim();
                }

                if (newTaskParametersTokens.length == 2
                        && newTaskParameters.contains("-end")) {
                    // only end is being overwritten

                    String endTimeString = newTaskParametersTokens[1].trim();

                    // handle date formats
                    if (!endTimeString.contains(" ")) {
                        DateFormat formatSlash = new SimpleDateFormat(
                                "dd/MM/yyyy");

                        endTimeString = formatSlash.format(oldEventTask
                                .getStartTime()) + " " + endTimeString;
                    }

                    endDate = slashFormatter.parse(endTimeString);
                }

                if (newTaskParametersTokens.length == 2
                        && newTaskParameters.contains("-priority")) {
                    // only priority is being overwritten
                    priority = newTaskParametersTokens[1].trim().charAt(0);
                }
            } // else nothing else is modified
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DeadlineTask newDeadline = new DeadlineTask(description, endDate,
                priority);

        if (_deadlineTasks.add(newDeadline)) {
            return newDeadline;
        } else {
            System.out.println("Unable to add deadline.");
            return null;
        }

    }

    /**
     * Retrieves existing Task object to be modified, calls the commands to
     * modify it, then return the modified Task object.
     * 
     * @param oldParameters
     * @param newParmeters
     * @return
     */
    private Task<?> modifyTaskParameters(String oldParameters,
            String newParameters) {
        String[] oldTaskParametersTokens = oldParameters.split(" ", 2);
        String taskTypeString = oldTaskParametersTokens[0].trim();
        int oldTaskId = Integer.parseInt(oldTaskParametersTokens[1].trim());

        switch (taskTypeString) {
            case ("todo"):
                TodoTask newTodo = modifyTodoParameters(oldTaskId,
                        newParameters);
                if (_todoTasks.add(newTodo)) {
                    return newTodo;
                } else {
                    System.out.println("Unable to add todo.");
                    return null;
                }
            case ("deadline"):
                return modifyDeadlineParameters(oldTaskId, newParameters);
            case ("event"):
                return modifyEventParameters(oldTaskId, newParameters);
            default:
                break;
        }

        System.out.println("Unable to determine task type: " + taskTypeString);
        return null;
    }

    private TodoTask modifyTodoParameters(int oldTaskId, String newParameters) {
        TodoTask prevTodo = deleteTodo(oldTaskId);

        String[] newParametersTokens = newParameters
                .split("-description|-priority");

        if (newParametersTokens.length == 3) {
            // both description and priority is modified
            return new TodoTask(newParametersTokens[1].trim(),
                    newParametersTokens[2].trim().charAt(0));
        }
        if (newParametersTokens.length == 2
                && newParameters.contains("-description")) {
            // only description is modified
            return new TodoTask(prevTodo.getDescription(),
                    newParametersTokens[1].trim().charAt(0));
        }
        if (newParametersTokens.length == 2
                && newParameters.contains("-priority")) {
            // only priority is modified
            return new TodoTask(newParametersTokens[1].trim(),
                    prevTodo.getPriority());
        }

        return null;
    }

    private DeadlineTask modifyDeadlineParameters(int oldTaskId,
            String newParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    private EventTask modifyEventParameters(int oldTaskId, String newParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO: mark as completed

    /**
     * only to be used for testing
     */
    void resetTrees() {
        _todoTasks.clear();
        _deadlineTasks.clear();
        _eventTasks.clear();
    }
}
