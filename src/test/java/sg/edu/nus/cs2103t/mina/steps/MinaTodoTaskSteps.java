package sg.edu.nus.cs2103t.mina.steps;

import org.apache.logging.log4j.Level;
import org.eclipse.swt.widgets.Shell;
import org.jbehave.core.annotations.AfterScenario;

import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Mina todo tasks test steps definition
 */
//@author A0105853H

public class MinaTodoTaskSteps extends MinaStepSkeleton {

    private static final String CLASS_NAME = MinaTodoTaskSteps.class.getName();

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }

    @AfterScenario
    public void afterSenario() {
        try {
            driver.cleanUp();
        } catch (Exception e) {
            LogHelper
                    .log(CLASS_NAME, Level.ERROR, e.getStackTrace().toString());
        }
    }
}
