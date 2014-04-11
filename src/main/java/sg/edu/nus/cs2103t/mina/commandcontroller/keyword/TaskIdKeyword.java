package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

// @author A0099151B
public class TaskIdKeyword extends Keyword {

    protected HashMap<String, String> _taskTypeValues;
    protected String _taskRegex;
    protected String _idRegex = "\\d+";

    private static final int TASK_ID_LOOKAHEAD = 2;
    private static final String CLASS_NAME = TaskIdKeyword.class.getName();

    static {
        TaskIdKeyword newTaskId = new TaskIdKeyword(SimpleKeyword.TASKID);
        KeywordFactory.addAliasEntry("-taskid", newTaskId);
        KeywordFactory.addAliasEntry("-block", newTaskId);
    }

    public TaskIdKeyword(StandardKeyword type) {
        super(type);
    }

    public TaskIdKeyword() {
        this(SimpleKeyword.TASKID);
        initValues();
    }

    @Override
    protected Keyword createKeyword() {
        return new TaskIdKeyword();
    }

    @Override
    protected void initValues() {

        _taskTypeValues = new HashMap<String, String>();

        _taskTypeValues.put(TaskType.EVENT.getType(), TaskType.EVENT.getType());
        _taskTypeValues.put("e", TaskType.EVENT.getType());
        _taskTypeValues.put("events", TaskType.EVENT.getType());
        _taskTypeValues.put("appointment", TaskType.EVENT.getType());
        _taskTypeValues.put("appt", TaskType.EVENT.getType());
        _taskTypeValues.put("appts", TaskType.EVENT.getType());

        _taskTypeValues.put(TaskType.TODO.getType(), TaskType.TODO.getType());
        _taskTypeValues.put("td", TaskType.TODO.getType());
        _taskTypeValues.put("to-do", TaskType.TODO.getType());
        _taskTypeValues.put("todos", TaskType.TODO.getType());
        _taskTypeValues.put("task", TaskType.TODO.getType());
        _taskTypeValues.put("tasks", TaskType.TODO.getType());

        _taskTypeValues.put(TaskType.DEADLINE.getType(),
                TaskType.DEADLINE.getType());
        _taskTypeValues.put("d", TaskType.DEADLINE.getType());
        _taskTypeValues.put("deadlines", TaskType.DEADLINE.getType());
        _taskTypeValues.put("cutoff", TaskType.DEADLINE.getType());
        _taskTypeValues.put("cutoffs", TaskType.DEADLINE.getType());

        StringBuilder taskTypeAlias = new StringBuilder();

        for (String key : _taskTypeValues.keySet()) {
            taskTypeAlias.append(key);
            taskTypeAlias.append("|");
        }
        String regex = "^(%1$s)(\\s)?";
        taskTypeAlias = taskTypeAlias.deleteCharAt(taskTypeAlias.length() - 1);
        _taskRegex = String.format(regex, taskTypeAlias.toString());
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex, Argument argument) throws ParseException {

        String rawTaskId, taskId, lookahead;

        lookahead = getLookAhead(tokens, currIndex, TASK_ID_LOOKAHEAD);
        rawTaskId = extractTaskId(lookahead);
        tokens = nullifyTokens(rawTaskId, tokens, currIndex);
        taskId = processTaskId(rawTaskId);
        updateArgument(taskId, argument);

        return tokens;
    }

    private String extractTaskId(String lookahead) {
        String allRegex = _taskRegex + _idRegex;
        Pattern rawTaskIdPattern = Pattern.compile(allRegex,
                Pattern.CASE_INSENSITIVE);
        Matcher rawTaskIdMatcher = rawTaskIdPattern.matcher(lookahead);

        if (rawTaskIdMatcher.find()) {
            return rawTaskIdMatcher.group();
        }

        return null;
    }

    protected ArrayList<String> nullifyTokens(String rawTaskId,
            ArrayList<String> tokens, Integer currIndex) {

        if (rawTaskId == null) {
            return tokens;
        }

        int upperLimit = rawTaskId.split(" ").length + currIndex;
        for (int i = currIndex; i < tokens.size() && i <= upperLimit; i++) {
            tokens.set(i, null);
        }

        return tokens;
    }

    private String processTaskId(String rawTaskId) {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Processing raw task id: " + rawTaskId);

        if (rawTaskId == null) {
            return null;
        }

        rawTaskId = rawTaskId.trim();
        Pattern taskPattern = Pattern.compile(_taskRegex,
                Pattern.CASE_INSENSITIVE);
        Matcher taskMatcher = taskPattern.matcher(rawTaskId);

        Pattern idPattern = Pattern.compile(_idRegex, Pattern.CASE_INSENSITIVE);
        Matcher idMatcher = idPattern.matcher(rawTaskId);

        String taskType;
        String id;

        if (taskMatcher.find()) {
            taskType = taskMatcher.group().trim();
            taskType = _taskTypeValues.get(taskType);
        } else {
            return null;
        }

        if (idMatcher.find()) {
            id = idMatcher.group().trim();
        } else {
            return null;
        }

        return taskType + " " + id;

    }

    protected void updateArgument(String taskId, Argument argument)
            throws ParseException {

        if (taskId == null) {
            throw new ParseException("Not a valid task id", 0);
        }

        argument.setKeywordValue(_type, taskId);
    }

    @Override
    public Map<String, String> getKeywordValues() {
        return _taskTypeValues;
    }

}
