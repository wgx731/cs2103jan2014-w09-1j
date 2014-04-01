package sg.edu.nus.cs2103t.mina;

import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
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
    private static final String GUI = "gui";
    private static final String CONSOLE = "console";
    private static final String UNKOWN_TYPE_ERROR = "unkown type";

    private static CommandManager commandController;
    private static TaskDataManager taskDataManager;
    private static TaskFilterManager taskFilterManager;
    private static MinaView uiView;
    private static DataSyncManager dataSyncManager;
    private static Timer dataSyncTimer;

    static void initDao() {
        TaskDao taskDao = new JsonFileTaskDaoImpl();
        dataSyncManager = new DataSyncManager(taskDao);
        dataSyncTimer = new Timer();
        dataSyncTimer.schedule(dataSyncManager, 0,
                Long.valueOf(ConfigHelper.getProperty(SYNC_INTERVAL_KEY)));
    }

    static void initTDM() {
        taskDataManager = new TaskDataManager(dataSyncManager);
    }

    static void initTFM() {
        taskFilterManager = new TaskFilterManager(taskDataManager);
    }

    static void initCC() {
        commandController = new CommandManager(taskDataManager,
                taskFilterManager);
    }

    static void initView() {
        switch (ConfigHelper.getProperty(VIEW_TYPE_KEY)) {
            case GUI :
                MinaGuiUI gui = new MinaGuiUI(commandController);
                gui.open();
                uiView = gui;
                break;
            case CONSOLE :
                uiView = new ConsoleUI(System.in, System.out, commandController);
                break;
            default :
                throw new Error(UNKOWN_TYPE_ERROR);
        }
        uiView.updateLists();
    }

    private void initComponents() {
        initDao();
        initTDM();
        initTFM();
        initCC();
        initView();
    }

    public Shell guiTestSetUp() {
        initDao();
        initTDM();
        initTFM();
        initCC();
        MinaGuiUI gui = new MinaGuiUI(commandController);
        gui.updateLists();
        return gui.open();
    }

    public void cleanUp() {
        taskDataManager.resetTrees();
    }

    public void stopSync() {
        dataSyncTimer.cancel();
    }

    public void processLoop() {
        uiView.displayOutput();
        uiView.loop();
    }

    public static void main(String[] args) {
        try {
            MinaDriver driver = new MinaDriver();
            driver.initComponents();
            driver.processLoop();
        } catch (Exception e) {
            logger.error(e, e);
            taskDataManager.saveAllTasks();
            System.exit(-1);
        }
    }
}
