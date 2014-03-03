package sg.edu.nus.cs2103t.mina;

import java.util.HashMap;
import java.util.Map;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.dao.TaskDao;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.view.ConsoleUI;
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

    private static CommandController commandController;
    private static TaskDataManager taskDataManager;
    private static TaskFilterManager taskFilterManager;
    private static TaskDao taskDao;
    private static MinaView uiView;

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
        commandController = new CommandController(taskDataManager,
                taskFilterManager);
        uiView = new ConsoleUI(System.in, System.out);

    }

    public static void main(String[] args) {
        initComponents();
        while (true) {
            String userInput = uiView.getUserInput();
            String feedback = commandController.processUserInput(userInput);
            uiView.displayOutput(feedback);
        }
    }
}
