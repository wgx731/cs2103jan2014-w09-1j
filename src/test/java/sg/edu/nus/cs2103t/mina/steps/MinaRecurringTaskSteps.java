package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.junit.Assert;

//@author A0099151B

public class MinaRecurringTaskSteps extends MinaStepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }
    @Then("the <type> list at line number <recurline> should be <recur>")
    public void checkRecur(@Named("type") String type, @Named("recurline") int recurLine, @Named("recur") String recur) {
        recur = recur.replace(NEXT_LINE, System.getProperty(LINE_SEPARATOR));
        recur = recur.replace(TAB, TAB_SEPARATOR);
        SWTBotStyledText list = getList(type);
        recurLine-=1;
        Assert.assertNotNull(list);
        System.out.println("We have " + list.getTextOnLine(recurLine));
        Assert.assertEquals(list.getTextOnLine(recurLine), recur);
    }
}   
