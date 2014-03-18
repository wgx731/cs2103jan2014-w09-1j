package sg.edu.nus.cs2103t.mina.utils;

import java.util.SortedSet;
import java.util.TreeSet;

import sg.edu.nus.cs2103t.mina.model.Task;

import com.google.gson.Gson;

/**
 * Helper class to translate between json and java objects
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class JsonHelper {

    private static Gson gson = null;

    private static Gson setUp() {
        return new Gson();
    }

    public static String taskSetToJson(SortedSet<? extends Task<?>> taskSet) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.toJson(taskSet);
    }

    public static Object jsonToTaskSet(String json) {
        if (gson == null) {
            gson = setUp();
        }
        return gson.fromJson(json, null);
    }
}
