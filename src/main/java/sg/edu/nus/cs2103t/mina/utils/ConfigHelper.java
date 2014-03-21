package sg.edu.nus.cs2103t.mina.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Helper class to read properties from config file for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class ConfigHelper {

    private static Logger logger = LogManager.getLogger(ConfigHelper.class
            .getName());

    private static final String CONFIG_FILE_PATH = "/config.properties";

    private static Properties prop = null;

    private static Properties setUp() {
        prop = new Properties();
        try {
            prop.load(ConfigHelper.class
                    .getResourceAsStream(CONFIG_FILE_PATH));
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
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
