package sg.edu.nus.cs2103t.mina;

import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.view.MinaGuiUI;
import sg.edu.nus.cs2103t.mina.view.MinaView;


/**
 * MINA test driver
 */
//@author A0105853H

public class MinaTestDriver extends MinaDriver {

    private static MinaTestDriver _driver;

    public static MinaTestDriver getMinaTestDriver() {
        _driver = new MinaTestDriver();
        return _driver;
    }

    public Shell guiTestSetUp() {
        _driver.initDao();
        _driver.initTDM();
        _driver.initTFM();
        _driver.initCC();
        MinaGuiUI gui = new MinaGuiUI(_driver.getCommandManager());
        gui.updateLists();
        return gui.open();
    }

    public void cleanUp() {
        TaskDataManager taskDataManager = _driver.getTaskDataManager();
        if (taskDataManager != null) {
            taskDataManager.resetTrees();
        }
    }

    public void updateLists() {
        MinaView view = _driver.getUiView();
        if (view != null) {
            view.updateLists();
        }
    }

}
