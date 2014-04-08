package sg.edu.nus.cs2103t.mina;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskMapDaoImpl;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.view.ConsoleUI;
import sg.edu.nus.cs2103t.mina.view.MinaGuiUI;
import sg.edu.nus.cs2103t.mina.view.MinaView;
import sg.edu.nus.cs2103t.mina.view.SplashScreen;

/**
 * Start driver for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class MinaDriver {

    private static final int DEFAULT_TASK_LIST_SIZE = 4;

    private static Logger logger = LogManager.getLogger(MinaDriver.class
            .getName());

    private static final String GUI = "gui";
    private static final String CONSOLE = "console";
    private static final String UNKOWN_TYPE_ERROR = "unkown type";

    private static CommandManager commandManager;
    private static TaskDataManager taskDataManager;
    private static TaskFilterManager taskFilterManager;
    private static MinaView uiView;
    private static DataSyncManager dataSyncManager;

    void initDao() {
        TaskDao taskDao = new JsonFileTaskDaoImpl();
        TaskMapDao taskMapDao = new FileTaskMapDaoImpl();
        dataSyncManager = new DataSyncManager(taskDao, taskMapDao);
    }

    void initTDM() {
        taskDataManager = new TaskDataManager(dataSyncManager);
    }

    void initTFM() {
        taskFilterManager = new TaskFilterManager(taskDataManager);
    }

    void initCC() {
        commandManager = new CommandManager(taskDataManager, taskFilterManager);
    }

    void showMinaView() {
        switch (ConfigHelper.getProperty(ConfigHelper.VIEW_TYPE_KEY)) {
            case GUI :
                MinaGuiUI gui = new MinaGuiUI(commandManager);
                gui.open();
                uiView = gui;
                break;
            case CONSOLE :
                uiView = new ConsoleUI(System.in, System.out, commandManager);
                break;
            default :
                throw new Error(UNKOWN_TYPE_ERROR);
        }
        uiView.updateLists();
    }

    public Shell guiTestSetUp() {
        initDao();
        initTDM();
        initTFM();
        initCC();
        MinaGuiUI gui = new MinaGuiUI(commandManager);
        gui.updateLists();
        return gui.open();
    }

    public void cleanUp() {
        taskDataManager.resetTrees();
    }

    public void processLoop() {
        uiView.displayOutput();
        uiView.loop();
    }

    private static void showSplashScreen(final MinaDriver driver) {
        List<Runnable> tasks = createTaskList(driver);
        SplashScreen screen = SplashScreen.getInstance(Display.getDefault(),
                tasks);
        screen.open();
    }

    private static List<Runnable> createTaskList(final MinaDriver driver) {
        List<Runnable> tasks = new ArrayList<Runnable>(DEFAULT_TASK_LIST_SIZE);
        tasks.add(new Runnable() {
            @Override
            public void run() {
                driver.initDao();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                driver.initTDM();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                driver.initTFM();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                driver.initCC();
            }
        });
        return tasks;
    }

    public static void main(String[] args) {
        try {
            MinaDriver driver = new MinaDriver();
            showSplashScreen(driver);
            driver.showMinaView();
            driver.processLoop();
        } catch (Exception e) {
            logger.error(e, e);
            taskDataManager.saveAllTasks();
            System.exit(-1);
        }
    }
}
