package sg.edu.nus.cs2103t.mina;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.UIType;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.view.ConsoleUI;
import sg.edu.nus.cs2103t.mina.view.MinaGuiUI;
import sg.edu.nus.cs2103t.mina.view.MinaView;

/**
 * Start driver for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class MinaDriver {

    private static Logger logger = LogManager.getLogger(MinaDriver.class
            .getName());

    private static final String SYNC_INTERVAL_KEY = "synctime";
    private static final String VIEW_TYPE_KEY = "viewtype";
    private static final String WELCOME_MESSAGE = "welcome to MINA.\r\n";
    private static final String UNKOWN_TYPE_ERROR = "unkown type";

    private static CommandController commandController;
    private static TaskDataManager taskDataManager;
    private static TaskFilterManager taskFilterManager;
    private static TaskDao taskDao;
    private static MinaView uiView;
    private static DataSyncManager dataSyncManager;

    static void initDao() {
        Map<TaskType, String> fileMap = new HashMap<TaskType, String>();
        fileMap.put(TaskType.TODO,
                ConfigHelper.getProperty(TaskType.TODO.getType()));
        fileMap.put(TaskType.EVENT,
                ConfigHelper.getProperty(TaskType.EVENT.getType()));
        fileMap.put(TaskType.DEADLINE,
                ConfigHelper.getProperty(TaskType.DEADLINE.getType()));
        taskDao = new FileTaskDaoImpl(fileMap);
    }

    static void initTDM() {
        taskDataManager = new TaskDataManager(taskDao);
    }

    static void initTFM() {
        taskFilterManager = new TaskFilterManager(taskDataManager);
    }

    static void initSync() {
        dataSyncManager = new DataSyncManager(taskDataManager, taskDao);
        new Timer().schedule(dataSyncManager, 0,
                Long.valueOf(ConfigHelper.getProperty(SYNC_INTERVAL_KEY)));
    }

    static void initCC() {
        commandController = new CommandController(dataSyncManager,
                taskDataManager, taskFilterManager);
    }

    static void initView() {
        UIType viewType;
        try {
            viewType = UIType.valueOf(ConfigHelper.getProperty(VIEW_TYPE_KEY)
                    .toUpperCase());
        } catch (IllegalArgumentException e) {
            viewType = UIType.CONSOLE;
        }
        switch (viewType) {
            case GUI:
                MinaGuiUI gui = new MinaGuiUI(commandController);
                gui.open();
                uiView = gui;
                break;
            case CONSOLE:
                uiView = new ConsoleUI(System.in, System.out, commandController);
                break;
            default:
                throw new Error(UNKOWN_TYPE_ERROR);
        }
        uiView.updateLists(commandController.getEventTask(),
                commandController.getDeadlineTask(),
                commandController.getTodoTask());
    }

    private void initComponents() {
        initDao();
        initTDM();
        initTFM();
        initSync();
        initCC();
        initView();
    }

    public Shell guiTestSetUp() {
        initDao();
        initTDM();
        initTFM();
        initSync();
        initCC();
        MinaGuiUI gui = new MinaGuiUI(commandController);
        gui.updateLists(commandController.getEventTask(),
                commandController.getDeadlineTask(),
                commandController.getTodoTask());
        return gui.open();
    }

    public void processLoop() {
        uiView.displayOutput(WELCOME_MESSAGE);
        uiView.loop();
    }

    public static void main(String[] args) {
        try {
            MinaDriver driver = new MinaDriver();
            driver.initComponents();
            driver.processLoop();
        } catch (Exception e) {
            logger.error(e, e);
            dataSyncManager.saveAll();
        }
    }
}
