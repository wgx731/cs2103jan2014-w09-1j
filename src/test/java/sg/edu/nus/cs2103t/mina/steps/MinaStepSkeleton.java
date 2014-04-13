package sg.edu.nus.cs2103t.mina.steps;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

import sg.edu.nus.cs2103t.mina.MinaDriver;
import sg.edu.nus.cs2103t.mina.dao.impl.FileOperationHelper;
import sg.edu.nus.cs2103t.mina.dao.impl.FileTaskMapDaoImpl;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;

public abstract class MinaStepSkeleton {

    protected static final int INPUT_TEXT_INDEX = 0;
    protected static final int FEEDBACK_LABEL_INDEX = 0;
    protected static final int UNKOWN_INDEX = -1;
    protected static final int EVENT_LIST_INDEX = 0;
    protected static final int DEADLINE_LIST_INDEX = 1;
    protected static final int TODO_LIST_INDEX = 2;

    protected static final String EMPTY_COMMAND = "";

    protected static SWTBot bot;
    protected static MinaDriver driver;

    private static FileOperationHelper jsonFileOperationHelper = new FileOperationHelper(
            JsonFileTaskDaoImpl.getCompletedSuffix(),
            JsonFileTaskDaoImpl.getFileExtension(),
            FileTaskMapDaoImpl.getFileExtension());

    private final static CyclicBarrier swtBarrier = new CyclicBarrier(2);
    private static Thread uiThread;
    private static Shell appShell;

    private void initializeUIThread() {
        uiThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        final Display display = Display.getDefault();
                        appShell = createShell();
                        bot = new SWTBot(appShell);
                        swtBarrier.await();
                        while (!appShell.isDisposed()) {
                            if (!display.readAndDispatch()) {
                                display.sleep();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        uiThread.setDaemon(true);
    }

    @BeforeStories
    public void beforeStories() throws InterruptedException,
            BrokenBarrierException {
        if (driver == null) {
            driver = MinaDriver.getMinaDriver();
        }
        if (uiThread == null) {
            initializeUIThread();
            uiThread.start();
        }
        swtBarrier.await();
    }

    @AfterStories
    public void afterStories() throws InterruptedException {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                appShell.close();
            }
        });
        driver.cleanUp();
        jsonFileOperationHelper.cleanAll();
    }

    /**
     * This method must be overridden by users. It should return the
     * {@link Shell} to be tested, after being opened and laid out. This class
     * will take care of running its event loop afterwards, until the test ends:
     * at this point, this class will close the {@link Shell} automatically.
     */
    protected abstract Shell createShell();

    @Given("empty command input field")
    public void givenEmptyCommand() {
        Assert.assertEquals(EMPTY_COMMAND, bot.text(INPUT_TEXT_INDEX).getText());
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
        // NOTE: replace new line and tab
        task = task.replace("\\n", System.getProperty("line.separator"));
        task = task.replace("\\t", "\t");
        SWTBotStyledText list = getList(type);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.getText().contains(task));
    }

    @Then("the <type> list should not contains <task>")
    public void listNotContains(@Named("type") String type,
            @Named("task") String task) {
        // NOTE: replace new line and tab
        task = task.replace("\\n", System.getProperty("line.separator"));
        task = task.replace("\\t", "\t");
        SWTBotStyledText list = getList(type);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.getText().contains(task));
    }

    private SWTBotStyledText getList(String type) {
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
        return bot.styledText(listIndex);
    }

}
