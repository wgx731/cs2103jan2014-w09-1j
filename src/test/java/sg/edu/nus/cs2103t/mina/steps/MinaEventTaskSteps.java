package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.junit.Assert;

public class MinaEventTaskSteps extends MinaStepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }
    
    @Then("the <type> list at line number <daterangeline> should be a time range of <daterange>")
    public void checkDateRange(@Named("type") String type, @Named("daterangeline") int dateRangeLine, @Named("daterange") String date) {
        date = date.replace(NEXT_LINE, System.getProperty(LINE_SEPARATOR));
        date = date.replace(TAB, TAB_SEPARATOR);
        SWTBotStyledText list = getList(type);
        dateRangeLine-=1;
        Assert.assertNotNull(list);
        Assert.assertEquals(list.getTextOnLine(dateRangeLine), date);
    }
}
