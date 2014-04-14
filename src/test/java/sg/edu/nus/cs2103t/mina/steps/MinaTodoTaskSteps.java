package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;

/**
 * Mina todo tasks test steps definition
 */
//@author A0105853H

public class MinaTodoTaskSteps extends MinaStepSkeleton {
    
    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }

}
