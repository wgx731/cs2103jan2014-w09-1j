package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;

//@author A0099151B
public class MinaDeadlineTaskSteps extends MinaStepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }
   
}
