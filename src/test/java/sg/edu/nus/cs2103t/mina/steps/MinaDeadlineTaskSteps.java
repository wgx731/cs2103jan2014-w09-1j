package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.junit.Assert;

public class MinaDeadlineTaskSteps extends MinaStepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }
    
    @Then("the <type> list at line number <dateline> should be <date>")
    public void checkDate(@Named("type") String type, @Named("dateline") int dateLine, @Named("date") String date) {
        date = date.replace(NEXT_LINE, System.getProperty(LINE_SEPARATOR));
        date = date.replace(TAB, TAB_SEPARATOR);
        SWTBotStyledText list = getList(type);
        dateLine-=1;
        Assert.assertNotNull(list);
        Assert.assertEquals(list.getTextOnLine(dateLine), date);
    }
}
