package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;
import org.jbehave.core.annotations.AfterScenario;

public class MinaAddTaskSteps extends MinaStepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }

    @AfterScenario
    public void afterSenario() {
        driver.cleanUp();
    }

}
