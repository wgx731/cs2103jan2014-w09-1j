package sg.edu.nus.cs2103t.mina.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

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
        assertEquals(EMPTY_COMMAND, bot.text(INPUT_TEXT_INDEX).getText());
    }

    @When("I enter <command>")
    public void enterCommand(@Named("command") String command) {
        bot.text(INPUT_TEXT_INDEX).typeText(command + SWT.CR);
    }

    @Then("the status bar should show <feedback>")
    public void thenTheOutcomeShould(@Named("feedback") String feedback) {
        assertEquals(feedback, bot.label(FEEDBACK_LABEL_INDEX).getText());
    }

    @Then("the <type> list should contains <task>")
    public void thenTheOutcomeShould(@Named("type") String type,
            @Named("task") String task) {
        int listIndex = UNKOWN_INDEX;
        switch (type) {
            case "todo":
                listIndex = TODO_LABEL_INDEX;
                break;
            case "event":
                listIndex = EVENT_LABEL_INDEX;
                break;
            case "deadline":
                listIndex = DEADLINE_LABEL_INDEX;
                break;
            default:
                listIndex = UNKOWN_INDEX;
                break;
        }
        assertNotEquals(UNKOWN_INDEX, listIndex);
        assertNotEquals(null, bot.styledText(listIndex));
        assertTrue(bot.styledText(listIndex).getText().contains(task));
    }
}
