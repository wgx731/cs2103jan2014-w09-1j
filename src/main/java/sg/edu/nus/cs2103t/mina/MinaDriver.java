package sg.edu.nus.cs2103t.mina;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.eclipse.swt.widgets.Display;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.TaskMapDao;
import sg.edu.nus.cs2103t.mina.dao.impl.FileOperationHelper;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskMapDaoImpl;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.utils.FileLockHelper;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;
import sg.edu.nus.cs2103t.mina.view.MinaGuiUI;
import sg.edu.nus.cs2103t.mina.view.MinaView;
import sg.edu.nus.cs2103t.mina.view.SplashScreen;

/**
 * This is the start point of MINA which links all component and start the
 * application
 */
//@author A0105853H

public class MinaDriver {

    private static final String MINA_ALREADY_STARTED = "MINA already started.";
    private static final int ERROR_EXIT = -1;
    private static final int DEFAULT_TASK_LIST_SIZE = 4;

    private static final String CLASS_NAME = MinaDriver.class.getName();

    static MinaDriver _driver;

    private CommandManager _commandManager;
    private TaskDataManager _taskDataManager;
    private TaskFilterManager _taskFilterManager;
    private MinaView _uiView;
    private DataSyncManager _dataSyncManager;

    MinaDriver() {
    
    }

    public static MinaDriver getMinaDriver() {
        if (_driver == null) {
            _driver = new MinaDriver();
        }
        return _driver;
    }

    CommandManager getCommandManager() {
        return _commandManager;
    }

    void setCommandManager(CommandManager commandManager) {
        this._commandManager = commandManager;
    }

    TaskDataManager getTaskDataManager() {
        return _taskDataManager;
    }

    void setTaskDataManager(TaskDataManager taskDataManager) {
        this._taskDataManager = taskDataManager;
    }

    TaskFilterManager getTaskFilterManager() {
        return _taskFilterManager;
    }

    void setTaskFilterManager(TaskFilterManager taskFilterManager) {
        this._taskFilterManager = taskFilterManager;
    }

    DataSyncManager getDataSyncManager() {
        return _dataSyncManager;
    }

    void setDataSyncManager(DataSyncManager dataSyncManager) {
        this._dataSyncManager = dataSyncManager;
    }

    MinaView getUiView() {
        return _uiView;
    }

    void setUiView(MinaView uiView) {
        _uiView = uiView;
    }

    void initDao() {
        FileOperationHelper fileOperationHelper = new FileOperationHelper(
                JsonFileTaskDaoImpl.getCompletedSuffix(),
                JsonFileTaskDaoImpl.getFileExtension(),
                FileTaskMapDaoImpl.getFileExtension());
        TaskDao taskDao = new JsonFileTaskDaoImpl(fileOperationHelper);
        TaskMapDao taskMapDao = new FileTaskMapDaoImpl(fileOperationHelper);
        _dataSyncManager = new DataSyncManager(taskDao, taskMapDao);
    }

    void initTDM() {
        _taskDataManager = new TaskDataManager(_dataSyncManager);
    }

    void initTFM() {
        _taskFilterManager = new TaskFilterManager(_taskDataManager);
    }

    void initCC() {
        _commandManager = new CommandManager(_taskDataManager, _taskFilterManager);
    }

    void showMinaView() {
        MinaGuiUI gui = new MinaGuiUI(_commandManager);
        gui.open();
        _uiView = gui;
        _uiView.updateLists();
    }

    public void processLoop() {
        if (_uiView != null) {
            _uiView.displayOutput();
            _uiView.loop();
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
        // Prevent multiple instances running
        if (!fileLockHelper.isAppActive()) {
            MinaDriver driver = MinaDriver.getMinaDriver();
            try {
                driver.showSplashScreen();
                driver.showMinaView();
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
