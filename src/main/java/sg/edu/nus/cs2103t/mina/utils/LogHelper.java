package sg.edu.nus.cs2103t.mina.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for Logger
 * 
 * It helps simply the logging process such that only class name, message and
 * level are needed.
 */
public class LogHelper {

    private static final boolean isLoggerEnabled = Boolean.valueOf(ConfigHelper
            .getProperty(ConfigHelper.LOGGER_KEY));

    private static Map<String, Logger> loggers = new HashMap<String, Logger>();

    public static void log(String className, Level level, String msg) {
        if (isLoggerEnabled) {
            if (loggers.containsKey(className)) {
                loggers.get(className).log(level, msg);
            } else {
                Logger logger = LogManager.getLogger(className);
                logger.log(level, msg);
                loggers.put(className, logger);
            }
        }
    }

}
