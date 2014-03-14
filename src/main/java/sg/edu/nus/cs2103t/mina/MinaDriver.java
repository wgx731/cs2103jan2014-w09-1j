package sg.edu.nus.cs2103t.mina;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static void initComponents() {
        Map<TaskType, String> fileMap = new HashMap<TaskType, String>();
        fileMap.put(TaskType.TODO,
                ConfigHelper.getProperty(TaskType.TODO.getType()));
        fileMap.put(TaskType.EVENT,
                ConfigHelper.getProperty(TaskType.EVENT.getType()));
        fileMap.put(TaskType.DEADLINE,
                ConfigHelper.getProperty(TaskType.DEADLINE.getType()));
        taskDao = new FileTaskDaoImpl(fileMap);
        taskDataManager = new TaskDataManager(taskDao);
        taskFilterManager = new TaskFilterManager(taskDataManager);
        dataSyncManager = new DataSyncManager(taskDataManager, taskDao);
        commandController = new CommandController(dataSyncManager,
                taskDataManager, taskFilterManager);
        UIType viewType;
        try {
            viewType = UIType.valueOf(ConfigHelper.getProperty(VIEW_TYPE_KEY)
                    .toUpperCase());
        } catch (IllegalArgumentException e) {
            viewType = UIType.CONSOLE;
        }
        switch (viewType) {
            case GUI :
                MinaGuiUI gui = new MinaGuiUI();
                gui.open();
                uiView = gui;
                break;
            case CONSOLE :
                uiView = new ConsoleUI(System.in, System.out);
                break;
            default :
                throw new Error(UNKOWN_TYPE_ERROR);
        }
        uiView.updateLists(taskDataManager.getUncompletedEventTasks(),
                taskDataManager.getUncompletedDeadlineTasks(),
                taskDataManager.getUncompletedTodoTasks());
        new Timer().schedule(dataSyncManager, 0,
                Long.valueOf(ConfigHelper.getProperty(SYNC_INTERVAL_KEY)));
    }

    private static void processLoop() {
        uiView.displayOutput(WELCOME_MESSAGE);
        while (true) {
            String userInput = uiView.getUserInput();
            //TODO: try to split UI and while true loop
            if (userInput == null) {
                continue;
            }
            String feedback = commandController.processUserInput(userInput);            
            uiView.updateLists(taskDataManager.getUncompletedEventTasks(),
                    taskDataManager.getUncompletedDeadlineTasks(),
                    taskDataManager.getUncompletedTodoTasks());
            uiView.displayOutput(feedback);
        }
    }

    public static void main(String[] args) {
        try {
            initComponents();
            processLoop();
        } catch (Exception e) {
            logger.error(e, e);
            dataSyncManager.saveAll();
        }
    }
}
