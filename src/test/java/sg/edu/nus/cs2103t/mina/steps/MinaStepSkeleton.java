package sg.edu.nus.cs2103t.mina.steps;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.Level;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.wb.swt.SWTResourceManager;
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
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * Template class jbehave steps definition
 */
// @author A0105853H

public abstract class MinaStepSkeleton {

    protected static final String TAB_SEPARATOR = "\t";
    protected static final String LINE_SEPARATOR = "line.separator";
    protected static final String TAB = "\\t";
    protected static final String NEXT_LINE = "\\n";

    private static final String CLASS_NAME = MinaStepSkeleton.class.getName();

    protected static final int INPUT_TEXT_INDEX = 0;
    protected static final int FEEDBACK_LABEL_INDEX = 0;
    protected static final int UNKOWN_INDEX = -1;
    protected static final int EVENT_LIST_INDEX = 0;
    protected static final int DEADLINE_LIST_INDEX = 1;
    protected static final int TODO_LIST_INDEX = 2;
    protected static final String TODO_TYPE = "todo";
    protected static final String EVENT_TYPE = "event";
    protected static final String DEADLINE_TYPE = "deadline";

    protected static final String EMPTY_COMMAND = "";

    protected static SWTBot bot;
    protected static MinaDriver driver;

    private static FileOperationHelper fileOperationHelper = new FileOperationHelper(
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
                    LogHelper.log(CLASS_NAME, Level.ERROR, e.getStackTrace()
                            .toString());
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
        fileOperationHelper.cleanAll();
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

    @Then("the <type> list should contains <task> at line number <line>")
    public void listContains(@Named("type") String type,
            @Named("task") String task, @Named("line") int line) {
        // NOTE: replace new line and tab
        task = task.replace(NEXT_LINE, System.getProperty(LINE_SEPARATOR));
        task = task.replace(TAB, TAB_SEPARATOR);
        int lineNum = line - 1;
        SWTBotStyledText list = getList(type);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.getTextOnLine(lineNum), task);
    }

    @Then("the <type> list at line number <colorline> should be in <color> color")
    public void listContains(@Named("type") String type,
            @Named("colorline") int line, @Named("color") String color) {
        int lineNum = line - 1;
        SWTBotStyledText list = getList(type);
        Assert.assertNotNull(list);
        Color colorCode = getColorCode(color);
        Assert.assertEquals(colorCode, list.getStyle(lineNum, list
                .getTextOnLine(lineNum).length() - 1).foreground);
    }

    private Color getColorCode(String color) throws Error {
        Color colorCode;
        switch (color) {
            case "gray" :
                colorCode = SWTResourceManager.getColor(192, 192, 192);
                break;
            case "orange" :
                colorCode = SWTResourceManager.getColor(247, 150, 70);
                break;
            case "yellow" :
                colorCode = SWTResourceManager.getColor(225, 212, 113);
                break;
            case "white" :
                colorCode = SWTResourceManager.getColor(255, 255, 255);
                break;
            case "green" :
                colorCode = SWTResourceManager.getColor(155, 187, 89);
                break;
            default :
                throw new Error("Unkown color");
        }
        return colorCode;
    }

    @Then("the <type> list should not contains <task>")
    public void listNotContains(@Named("type") String type,
            @Named("task") String task) {
        // NOTE: replace new line and tab
        task = task.replace(NEXT_LINE, System.getProperty(LINE_SEPARATOR));
        task = task.replace(TAB, TAB_SEPARATOR);
        SWTBotStyledText list = getList(type);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.getText().contains(task));
    }

    protected SWTBotStyledText getList(String type) {
        int listIndex = UNKOWN_INDEX;
        switch (type) {
            case TODO_TYPE :
                listIndex = TODO_LIST_INDEX;
                break;
            case EVENT_TYPE :
                listIndex = EVENT_LIST_INDEX;
                break;
            case DEADLINE_TYPE :
                listIndex = DEADLINE_LIST_INDEX;
                break;
            default :
                listIndex = UNKOWN_INDEX;
                break;
        }
        Assert.assertNotEquals(UNKOWN_INDEX, listIndex);
        return bot.styledText(listIndex);
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
