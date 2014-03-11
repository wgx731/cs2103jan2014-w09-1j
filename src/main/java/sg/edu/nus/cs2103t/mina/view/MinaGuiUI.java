package sg.edu.nus.cs2103t.mina.view;

import java.util.SortedSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

public class MinaGuiUI implements MinaView {

    private static final String EMPTY_STRING = "";

    private Shell _shell;
    private Display _display;
    private Text _userInputTextField;
    private Label _statusBar;
    private StyledText _eventListUI;
    private StyledText _deadlineListUI;
    private StyledText _todoListUI;

    private String _userCommand;
    
    private static final String ERROR = "Operation failed. Please try again.";
    private static final String INVALID_COMMAND = "Invalid command. Please re-enter.";
    private static final String SUCCESS = "Operation completed";

    public MinaGuiUI() {
        super();
        createContents();
    }

    /**
     * Open the window.
     */
    public void open() {
        Monitor primary = _display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = _shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        _shell.setLocation(x, y);
        _shell.open();
        _shell.layout();
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        _display = Display.getDefault();

        _shell = new Shell(_display, SWT.NO_TRIM);
        _shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
        _shell.setSize(1080, 580);
        _shell.setText("MINA");

        _statusBar = new Label(_shell, SWT.NONE);
        _statusBar.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
        _statusBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _statusBar.setBounds(722, 540, 354, 36);

        _userInputTextField = new Text(_shell, SWT.NONE);
        _userInputTextField
                .setForeground(SWTResourceManager.getColor(0, 51, 0));
        _userInputTextField.setFont(SWTResourceManager.getFont("Comic Sans MS",
                15, SWT.NORMAL));
        _userInputTextField.setBounds(4, 540, 714, 36);

        Label lblEvent = new Label(_shell, SWT.NONE);
        lblEvent.setAlignment(SWT.CENTER);
        lblEvent.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblEvent.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        lblEvent.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        lblEvent.setBounds(4, 4, 354, 36);
        lblEvent.setText("Events(e)");

        Label lblDeadline = new Label(_shell, SWT.NONE);
        lblDeadline.setAlignment(SWT.CENTER);
        lblDeadline.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblDeadline.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        lblDeadline.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        lblDeadline.setBounds(362, 4, 356, 36);
        lblDeadline.setText("Deadlines(d)");

        Label lblTodo = new Label(_shell, SWT.NONE);
        lblTodo.setAlignment(SWT.CENTER);
        lblTodo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblTodo.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        lblTodo.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        lblTodo.setBounds(722, 4, 354, 36);
        lblTodo.setText("To-do(td)");

        _eventListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _eventListUI
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _eventListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _eventListUI.setBounds(4, 40, 354, 496);
        _eventListUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        _deadlineListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _deadlineListUI.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _deadlineListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _deadlineListUI.setBounds(362, 40, 356, 496);
        _deadlineListUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        _todoListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _todoListUI.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _todoListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _todoListUI.setBounds(722, 40, 354, 496);
        _todoListUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        _userInputTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.keyCode;
                switch (key) {
                    case SWT.CR :
                        _userCommand = _userInputTextField.getText();
                        _userInputTextField.setText(EMPTY_STRING);
                        break;
                    default :
                        break;
                }
            }
        });
    }

    @Override
    public String getUserInput() {
        if (!_display.readAndDispatch()) {
            _display.sleep();
            return _userCommand;
        } else {
            String command = _userCommand;
            _userCommand = null;
            return command;
        }
    }

    @Override
    public void displayOutput(String message) {
        final String outputMessage = message;
        _display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (outputMessage.contains("Error")||outputMessage.contains("invalid")){
                	_statusBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                	if (outputMessage.contains("Error")){
                        _statusBar.setText(ERROR);
                	} else {
                		_statusBar.setText(INVALID_COMMAND);
                	}
                } else {
                	_statusBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                	if (outputMessage.contains("welcome")){
                		_statusBar.setText(outputMessage);
                	} else {
                		_statusBar.setText(SUCCESS);
                	}
                }
            }
        });
    }

    @Override
    public void updateLists(SortedSet<EventTask> allEventTasks,
            SortedSet<DeadlineTask> allDeadlineTasks,
            SortedSet<TodoTask> allTodoTasks) {
    	int startEvent = 1;
    	int startTodo = 1;
    	int startDeadline = 1;
        _eventListUI.setText(EMPTY_STRING);
        for (EventTask event : allEventTasks) {
            _eventListUI.append(startEvent+". "+event.getDescription()
            		+" from "+event.getStartTime()+" to "+event.getEndTime()+"\n");
            startEvent++;
        }
        _deadlineListUI.setText(EMPTY_STRING);
        for (DeadlineTask deadline : allDeadlineTasks) {
            _deadlineListUI.append(startDeadline+". "+deadline.getDescription()
            		+" by "+deadline.getEndTime()+"\n");
            startDeadline++;
        }
        _todoListUI.setText(EMPTY_STRING);
        for (TodoTask todo : allTodoTasks) {
            _todoListUI.append(startTodo+". "+todo.getDescription()+"\n");
            startTodo++;
        }

    }

}
