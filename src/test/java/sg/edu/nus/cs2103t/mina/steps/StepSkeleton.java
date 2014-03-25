package sg.edu.nus.cs2103t.mina.steps;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;

import sg.edu.nus.cs2103t.mina.MinaDriver;
import sg.edu.nus.cs2103t.mina.dao.impl.FileOperationHelper;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.dao.impl.ObjectFileTaskDaoImpl;

public abstract class StepSkeleton {

    protected static final int INPUT_TEXT_INDEX = 0;
    protected static final int FEEDBACK_LABEL_INDEX = 0;
    protected static final int UNKOWN_INDEX = -1;
    protected static final int EVENT_LABEL_INDEX = 0;
    protected static final int DEADLINE_LABEL_INDEX = 1;
    protected static final int TODO_LABEL_INDEX = 2;

    protected static final String EMPTY_COMMAND = "";

    protected static SWTBot bot;
    protected static MinaDriver driver;

    private final static CyclicBarrier swtBarrier = new CyclicBarrier(2);
    private static Thread uiThread;
    private static Shell appShell;

    public StepSkeleton() {
        if (driver == null) {
            driver = new MinaDriver();
        }
        if (uiThread == null) {
            initializeUIThread();
            uiThread.start();
        }
    }

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
    public void setUp() throws InterruptedException, BrokenBarrierException {
        swtBarrier.await();
    }

    @AfterStories
    public void tearDown() throws InterruptedException {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                appShell.close();
            }
        });
        FileOperationHelper jsonFileOperationHelper = new FileOperationHelper(
                JsonFileTaskDaoImpl.getCompletedSuffix(),
                JsonFileTaskDaoImpl.getFileExtension());
        FileOperationHelper objectFileOperationHelper = new FileOperationHelper(
                ObjectFileTaskDaoImpl.getCompletedSuffix(),
                ObjectFileTaskDaoImpl.getFileExtension());
        jsonFileOperationHelper.cleanUp();
        objectFileOperationHelper.cleanUp();
    }

    /**
     * This method must be overridden by users. It should return the
     * {@link Shell} to be tested, after being opened and laid out. This class
     * will take care of running its event loop afterwards, until the test ends:
     * at this point, this class will close the {@link Shell} automatically.
     */
    protected abstract Shell createShell();
}
