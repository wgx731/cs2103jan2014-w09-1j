package sg.edu.nus.cs2103t.mina.utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Helper class to translate between json and java objects
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class JsonHelper {

    private static final String KEY_ID = "_id";
    private static final String KEY_DESCRIPTION = "_description";
    private static final String KEY_PRIORITY = "_priority";
    private static final String KEY_COMPLETED = "_isCompleted";
    private static final String KEY_CREATED_TIME = "_createdTime";
    private static final String KEY_LAST_EDITED_TIME = "_lastEditedTime";
    private static final String KEY_START_TIME = "_startTime";
    private static final String KEY_END_TIME = "_endTime";

    private static final String DATE_FORMAT = "EEEE, dd/MM/yyyy/HH:mm:ss.SSS";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            DATE_FORMAT);

    private static final String EMPTY_STRING = "";

    private static class EventTaskDeserializer implements
            JsonDeserializer<EventTask> {

        @Override
        public EventTask deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            boolean isValidJson = true;
            String id = jsonObject.get(KEY_ID).getAsString();
            TaskType taskType = TaskType.EVENT;
            String description = jsonObject.get(KEY_DESCRIPTION).getAsString();
            char priority = jsonObject.get(KEY_PRIORITY).getAsCharacter();
            if (!Task.isValidPriority(priority)) {
                isValidJson = false;
            }
            boolean isCompleted = jsonObject.get(KEY_COMPLETED).getAsBoolean();
            Date createdTime = null, lastEditedTime = null, start = null, end = null;
            try {
                createdTime = formatter.parse(jsonObject.get(KEY_CREATED_TIME)
                        .getAsString());
                lastEditedTime = formatter.parse(jsonObject.get(
                        KEY_LAST_EDITED_TIME).getAsString());
                start = formatter.parse(jsonObject.get(KEY_START_TIME)
                        .getAsString());
                end = formatter.parse(jsonObject.get(KEY_END_TIME)
                        .getAsString());
            } catch (ParseException e) {
                isValidJson = false;
            }
            if (isValidJson) {
                return new EventTask(taskType, description, id, priority,
                        createdTime, lastEditedTime, isCompleted, start, end);
            } else {
                return null;
            }
        }
    }

    private static class DeadlineTaskDeserializer implements
            JsonDeserializer<DeadlineTask> {

        @Override
        public DeadlineTask deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            boolean isValidJson = true;
            String id = jsonObject.get(KEY_ID).getAsString();
            TaskType taskType = TaskType.DEADLINE;
            String description = jsonObject.get(KEY_DESCRIPTION).getAsString();
            char priority = jsonObject.get(KEY_PRIORITY).getAsCharacter();
            if (!Task.isValidPriority(priority)) {
                isValidJson = false;
            }
            boolean isCompleted = jsonObject.get(KEY_COMPLETED).getAsBoolean();
            Date createdTime = null, lastEditedTime = null, end = null;
            try {
                createdTime = formatter.parse(jsonObject.get(KEY_CREATED_TIME)
                        .getAsString());
                lastEditedTime = formatter.parse(jsonObject.get(
                        KEY_LAST_EDITED_TIME).getAsString());
                end = formatter.parse(jsonObject.get(KEY_END_TIME)
                        .getAsString());
            } catch (ParseException e) {
                isValidJson = false;
            }
            if (isValidJson) {
                return new DeadlineTask(taskType, description, id, priority,
                        createdTime, lastEditedTime, isCompleted, end);
            } else {
                return null;
            }
        }
    }

    private static class TodoTaskDeserializer implements
            JsonDeserializer<TodoTask> {

        @Override
        public TodoTask deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            boolean isValidJson = true;
            String id = jsonObject.get(KEY_ID).getAsString();
            TaskType taskType = TaskType.TODO;
            String description = jsonObject.get(KEY_DESCRIPTION).getAsString();
            char priority = jsonObject.get(KEY_PRIORITY).getAsCharacter();
            if (!Task.isValidPriority(priority)) {
                isValidJson = false;
            }
            boolean isCompleted = jsonObject.get(KEY_COMPLETED).getAsBoolean();
            Date createdTime = null, lastEditedTime = null;
            try {
                createdTime = formatter.parse(jsonObject.get(KEY_CREATED_TIME)
                        .getAsString());
                lastEditedTime = formatter.parse(jsonObject.get(
                        KEY_LAST_EDITED_TIME).getAsString());
            } catch (ParseException e) {
                isValidJson = false;
            }
            if (isValidJson) {
                return new TodoTask(taskType, description, id, priority,
                        createdTime, lastEditedTime, isCompleted);
            } else {
                return null;
            }
        }
    }

    private static Gson gson = null;

    private static final Type TODO_TREESET_TYPE = new TypeToken<TreeSet<TodoTask>>() {
    }.getType();
    private static final Type EVENT_TREESET_TYPE = new TypeToken<TreeSet<EventTask>>() {
    }.getType();
    private static final Type DEADLINE_TREESET_TYPE = new TypeToken<TreeSet<DeadlineTask>>() {
    }.getType();
    private static final Type RECURRING_TASK_MAP_TYPE = new TypeToken<HashMap<String, ArrayList<Task<?>>>>() {
    }.getType();
    private static final Type BLOCK_TASK_MAP_TYPE = new TypeToken<HashMap<String, ArrayList<EventTask>>>() {
    }.getType();

    private static Gson setUp() {
        return new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .registerTypeAdapter(EventTask.class,
                        new EventTaskDeserializer())
                .registerTypeAdapter(DeadlineTask.class,
                        new DeadlineTaskDeserializer())
                .registerTypeAdapter(TodoTask.class, new TodoTaskDeserializer())
                .setDateFormat(DATE_FORMAT)
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .setPrettyPrinting().create();
    }

    @SuppressWarnings("unchecked")
    public static String taskSetToJson(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType) {
        if (gson == null) {
            gson = setUp();
        }
        switch (taskType) {
            case TODO :
                return gson.toJson((SortedSet<TodoTask>) taskSet,
                        TODO_TREESET_TYPE);
            case DEADLINE :
                return gson.toJson((SortedSet<DeadlineTask>) taskSet,
                        DEADLINE_TREESET_TYPE);
            case EVENT :
                return gson.toJson((SortedSet<EventTask>) taskSet,
                        EVENT_TREESET_TYPE);
            default :
                return null;
        }
    }

    public static SortedSet<? extends Task<?>> jsonToTaskSet(String json,
            TaskType taskType) {
        if (gson == null) {
            gson = setUp();
        }
        switch (taskType) {
            case TODO :
                if (json.trim().equals(EMPTY_STRING)) {
                    return new TreeSet<TodoTask>();
                }
                return gson.fromJson(json, TODO_TREESET_TYPE);
            case DEADLINE :
                if (json.trim().equals(EMPTY_STRING)) {
                    return new TreeSet<DeadlineTask>();
                }
                return gson.fromJson(json, DEADLINE_TREESET_TYPE);
            case EVENT :
                if (json.trim().equals(EMPTY_STRING)) {
                    return new TreeSet<EventTask>();
                }
                return gson.fromJson(json, EVENT_TREESET_TYPE);
            default :
                return null;
        }
    }

    public static String recurringTaskMapToJson(
            HashMap<String, ArrayList<Task<?>>> recurringTaskMap) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.toJson(recurringTaskMap, RECURRING_TASK_MAP_TYPE);
    }

    public static HashMap<String, ArrayList<Task<?>>> jsonToRecurringTaskMap(
            String json) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.fromJson(json, RECURRING_TASK_MAP_TYPE);
    }

    public static String blockTaskMapToJson(
            HashMap<String, ArrayList<EventTask>> blockTaskMap) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.toJson(blockTaskMap, BLOCK_TASK_MAP_TYPE);
    }

    public static HashMap<String, ArrayList<Task<?>>> jsonToBlockTaskMap(
            String json) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.fromJson(json, BLOCK_TASK_MAP_TYPE);
    }
}
