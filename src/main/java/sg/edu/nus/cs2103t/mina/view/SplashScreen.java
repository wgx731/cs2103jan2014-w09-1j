package sg.edu.nus.cs2103t.mina.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;

public class SplashScreen {

    private final int SPLASH_MAX = 100;
    private final int SPLASH_MIN = 0;

    private int _progress;
    private Display _display;
    private Image _image;
    private Shell _splashShell;
    private ProgressBar _progressBar;
    private List<Runnable> _tasks;

    private SplashScreen(Display display, List<Runnable> tasks) {
        _tasks = tasks;
        _progress = SPLASH_MIN;
        _display = display;
        _image = new Image(display, getClass().getResourceAsStream(
                ConfigHelper.getProperty(ConfigHelper.SPLASHPATH_KEY)));

        _splashShell = new Shell(SWT.ON_TOP);
        _progressBar = new ProgressBar(_splashShell, SWT.NONE);
        _progressBar.setMaximum(SPLASH_MAX);

        Label label = new Label(_splashShell, SWT.NONE);
        label.setImage(_image);

        FormLayout layout = new FormLayout();
        _splashShell.setLayout(layout);

        FormData labelData = new FormData();
        labelData.right = new FormAttachment(100, 0);
        labelData.bottom = new FormAttachment(100, 0);
        label.setLayoutData(labelData);

        FormData progressData = new FormData();
        progressData.left = new FormAttachment(0, -5);
        progressData.right = new FormAttachment(100, 0);
        progressData.bottom = new FormAttachment(100, 0);
        _progressBar.setLayoutData(progressData);
        _splashShell.pack();

        Rectangle splashRect = _splashShell.getBounds();
        Rectangle displayRect = display.getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.height) / 2;
        _splashShell.setLocation(x, y);

    }

    public static SplashScreen getInstance(Display display, List<Runnable> tasks) {
        return new SplashScreen(display, tasks);
    }

    public void open() {
        _splashShell.setCursor(new Cursor(_display, SWT.CURSOR_APPSTARTING));
        _splashShell.open();
        _display.asyncExec(new Runnable() {
            public void run() {
                int totalTaskSize = _tasks.size();
                int taskProgress = SPLASH_MAX / totalTaskSize;
                for (int i = 0; i < totalTaskSize; i++) {
                    updateProgress(_progress + taskProgress / 2);
                    _tasks.get(i).run();
                    updateProgress(_progress + taskProgress / 2);
                }
                updateProgress(SPLASH_MAX);
                close();
            }
        });
        while (_progress != SPLASH_MAX) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
        }
    }

    private void updateProgress(int progress) {
        _progress = progress;
        _progressBar.setSelection(_progress);
    }

    private void close() {
        _splashShell.setCursor(new Cursor(_display, SWT.CURSOR_ARROW));
        _splashShell.close();
        _image.dispose();
    }

}