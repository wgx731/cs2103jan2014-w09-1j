package sg.edu.nus.cs2103t.mina.steps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

/**
 * GUI test steps translation for MINA
 * 
 * @author wgx731
 */
public class MinaAddTaskSteps extends StepSkeleton {

    @Override
    protected Shell createShell() {
        return driver.guiTestSetUp();
    }

    @Given("command input field is empty")
    public void givenEmptyCommand() {
        bot.text(INPUT_TEXT_INDEX).typeText(EMPTY_COMMAND);
    }

    @When("I enter <command>")
    public void enterCommand(@Named("command") String command) {
        bot.text(INPUT_TEXT_INDEX).typeText(command + SWT.CR);
    }

    @Then("the status bar should show <feedback>")
    public void statusBarShow(@Named("feedback") String feedback) {
        Assert.assertEquals(feedback, bot.label(FEEDBACK_LABEL_INDEX).getText());
    }

    @Then("the <type> list should contains <task>")
    public void listContains(@Named("type") String type,
            @Named("task") String task) {
        int listIndex = UNKOWN_INDEX;
        switch (type) {
            case "todo" :
                listIndex = TODO_LIST_INDEX;
                break;
            case "event" :
                listIndex = EVENT_LIST_INDEX;
                break;
            case "deadline" :
                listIndex = DEADLINE_LIST_INDEX;
                break;
            default :
                listIndex = UNKOWN_INDEX;
                break;
        }
        Assert.assertNotEquals(UNKOWN_INDEX, listIndex);
        Assert.assertNotEquals(null, bot.styledText(listIndex));
        Assert.assertTrue(bot.styledText(listIndex).getText().contains(task));
    }
}
