package sg.edu.nus.cs2103t.mina.utils;

import java.lang.reflect.Type;
import java.text.DateFormat;
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

    private static class TaskDeserializer implements JsonDeserializer<Task<?>> {
        @Override
        public Task<?> deserialize(JsonElement arg0, Type arg1,
                JsonDeserializationContext arg2) throws JsonParseException {
            // TODO: force to use full constructor
            return null;
        }

    }

    private static Gson gson = null;

    private static final Type TODO_TREESET_TYPE = new TypeToken<TreeSet<TodoTask>>() {
    }.getType();
    private static final Type EVENT_TREESET_TYPE = new TypeToken<TreeSet<EventTask>>() {
    }.getType();
    private static final Type DEADLINE_TREESET_TYPE = new TypeToken<TreeSet<DeadlineTask>>() {
    }.getType();

    private static Gson setUp() {
        return new GsonBuilder().enableComplexMapKeySerialization()
                .serializeNulls()
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting().create();
    }

    public static String taskSetToJson(SortedSet<? extends Task<?>> taskSet,
            TaskType taskType) {
        if (gson == null) {
            gson = setUp();
        }
        switch (taskType) {
            case TODO :
                return gson.toJson(taskSet, TODO_TREESET_TYPE);
            case DEADLINE :
                return gson.toJson(taskSet, DEADLINE_TREESET_TYPE);
            case EVENT :
                return gson.toJson(taskSet, EVENT_TREESET_TYPE);
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
                return gson.fromJson(json, TODO_TREESET_TYPE);
            case DEADLINE :
                return gson.fromJson(json, DEADLINE_TREESET_TYPE);
            case EVENT :
                return gson.fromJson(json, EVENT_TREESET_TYPE);
            default :
                return null;
        }
    }
}
