package sg.edu.nus.cs2103t.mina.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.Level;

/**
 * Helper class to read properties from config file for MINA
 */
//@author A0105853H

public class ConfigHelper {

    // KEYS
    public static final String ICONPATH_KEY = "iconpath";
    public static final String SPLASHPATH_KEY = "splashscreenpath";
    public static final String TODO_KEY = "todo";
    public static final String EVENT_KEY = "event";
    public static final String DEADLINE_KEY = "deadline";
    public static final String TASK_MAP_KEY = "taskmap";
    public static final String LOGGER_KEY = "logger";
    public static final String LOCK_KEY = "lockfilename";
    public static final String TIMER_KEY = "timer";

    private static final String CLASS_NAME = ConfigHelper.class.getName();

    private static final String CONFIG_FILE_PATH = "/config.properties";

    private static Properties prop = null;

    private static Properties setUp() {
        prop = new Properties();
        try {
            prop.load(ConfigHelper.class.getResourceAsStream(CONFIG_FILE_PATH));
        } catch (FileNotFoundException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }
        return prop;
    }

    public static String getProperty(String key) {
        if (prop == null) {
            prop = setUp();
        }
        return prop.getProperty(key);
    }
}
