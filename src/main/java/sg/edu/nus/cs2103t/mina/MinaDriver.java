package sg.edu.nus.cs2103t.mina;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.dao.impl.FileOperationHelper;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskMapDaoImpl;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.utils.FileLockHelper;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;
import sg.edu.nus.cs2103t.mina.view.ConsoleUI;
import sg.edu.nus.cs2103t.mina.view.MinaGuiUI;
import sg.edu.nus.cs2103t.mina.view.MinaView;
import sg.edu.nus.cs2103t.mina.view.SplashScreen;

/**
 * Driver class for MINA
 * 
 * This is start point of MINA, it contains main method.
 * 
 */
public class MinaDriver {

    private static final String MINA_ALREADY_STARTED = "MINA already started.";
    private static final int ERROR_EXIT = -1;
    private static final int DEFAULT_TASK_LIST_SIZE = 4;

    private static final String CLASS_NAME = MinaDriver.class.getName();
    private static final String GUI = "gui";
    private static final String CONSOLE = "console";
    private static final String UNKOWN_TYPE_ERROR = "unkown";

    private static MinaDriver driver;

    private CommandManager commandManager;
    private TaskDataManager taskDataManager;
    private TaskFilterManager taskFilterManager;
    private MinaView uiView;
    private DataSyncManager dataSyncManager;

    private MinaDriver() {
        super();
    }

    public static MinaDriver getMinaDriver() {
        if (driver == null) {
            driver = new MinaDriver();
            driver.showSplashScreen();
            driver.showMinaView();
        }
        return driver;
    }

    CommandManager getCommandManager() {
        return commandManager;
    }

    void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    TaskDataManager getTaskDataManager() {
        return taskDataManager;
    }

    void setTaskDataManager(TaskDataManager taskDataManager) {
        this.taskDataManager = taskDataManager;
    }

    TaskFilterManager getTaskFilterManager() {
        return taskFilterManager;
    }

    void setTaskFilterManager(TaskFilterManager taskFilterManager) {
        this.taskFilterManager = taskFilterManager;
    }

    DataSyncManager getDataSyncManager() {
        return dataSyncManager;
    }

    void setDataSyncManager(DataSyncManager dataSyncManager) {
        this.dataSyncManager = dataSyncManager;
    }

    void initDao() {
        FileOperationHelper fileOperationHelper = new FileOperationHelper(
                JsonFileTaskDaoImpl.getCompletedSuffix(),
                JsonFileTaskDaoImpl.getFileExtension(),
                FileTaskMapDaoImpl.getFileExtension());
        TaskDao taskDao = new JsonFileTaskDaoImpl(fileOperationHelper);
        TaskMapDao taskMapDao = new FileTaskMapDaoImpl(fileOperationHelper);
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
        if (taskDataManager != null) {
            taskDataManager.resetTrees();
        }
    }

    public void processLoop() {
        if (uiView != null) {
            uiView.displayOutput();
            uiView.loop();
        }
    }

    private void showSplashScreen() {
        List<Runnable> tasks = createTaskList();
        SplashScreen screen = SplashScreen.getInstance(Display.getDefault(),
                tasks);
        screen.open();
    }

    private List<Runnable> createTaskList() {
        List<Runnable> tasks = new ArrayList<Runnable>(DEFAULT_TASK_LIST_SIZE);
        tasks.add(new Runnable() {
            @Override
            public void run() {
                initDao();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                initTDM();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                initTFM();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                initCC();
            }
        });
        return tasks;
    }

    public static void main(String[] args) {
        FileLockHelper fileLockHelper = FileLockHelper
                .getFileLockHelperInstance();
        if (!fileLockHelper.isAppActive()) {
            MinaDriver driver = MinaDriver.getMinaDriver();
            try {
                driver.processLoop();
            } catch (Exception e) {
                LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
                driver.getTaskDataManager().saveAllTasks();
                System.exit(ERROR_EXIT);
            }
        } else {
            LogHelper.log(CLASS_NAME, Level.WARN, MINA_ALREADY_STARTED);
            System.out.println(MINA_ALREADY_STARTED);
        }
    }

}
