package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sg.edu.nus.cs2103t.mina.controller.CommandController;

public class MinaGuiUI extends MinaView {

    private static final String EMPTY_STRING = "";

    private Shell _shell;
    private Display _display;
    private Text _userInputTextField;
    private Label _statusBar;
    private StyledText _eventListUI;
    private StyledText _deadlineListUI;
    private StyledText _todoListUI;
    
    private LinkedList<String> _commandHistory;
    private int _commandPosition;
    private boolean _historyUp;
    
    private static final String ERROR = "Operation failed. Please try again.";
    private static final String INVALID_COMMAND = "Invalid command. Please re-enter.";
    private static final String SUCCESS = "Operation completed";

    public MinaGuiUI(CommandController commandController) {
        super(commandController);
        createContents();
    }

    /**
     * Open the window.
     */
    public Shell open() {
        Monitor primary = _display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = _shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        _shell.setLocation(x, y);
        _shell.open();
        _shell.layout();
        return _shell;
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        _display = Display.getDefault();
        
        _commandHistory = new LinkedList<String>();
        _commandPosition = 0;
        _historyUp = true;

        _shell = new Shell(_display, SWT.NO_TRIM);
        _shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
        _shell.setSize(1080, 580);
        _shell.setText("MINA");

        _statusBar = new Label(_shell, SWT.NONE);
        _statusBar.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
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
        _eventListUI.setEnabled(false);
        _eventListUI.setEditable(false);
        _eventListUI
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _eventListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _eventListUI.setBounds(4, 40, 354, 496);

        _deadlineListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _deadlineListUI.setEnabled(false);
        _deadlineListUI.setEditable(false);
        _deadlineListUI.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _deadlineListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _deadlineListUI.setBounds(362, 40, 356, 496);

        _todoListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _todoListUI.setEnabled(false);
        _todoListUI.setEditable(false);
        _todoListUI.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoListUI.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.NORMAL));
        _todoListUI.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _todoListUI.setBounds(722, 40, 354, 496);

        _userInputTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.keyCode;
                switch (key) {
                    case SWT.CR:
                        String command = _userInputTextField.getText();
                        if (_commandHistory.size()<5){
                        	_commandHistory.offer(command);
                        } else {
                        	_commandHistory.poll();
                        	_commandHistory.offer(command);
                        }
                        String feedback = _commandController
                                .processUserInput(command);
                        _userInputTextField.setText(EMPTY_STRING);
                        displayOutput(feedback);
                        updateLists(_commandController.getEventTask(),
                                _commandController.getDeadlineTask(),
                                _commandController.getTodoTask());
                        break;
                    case SWT.ARROW_UP:
                    	if (_commandHistory.size()!=0){
                    		if (_historyUp){
                    			if (_commandPosition<_commandHistory.size()-1){
                    				_userInputTextField.setText(_commandHistory.get(_commandPosition)+" ");
                    				_userInputTextField.setSelection(_userInputTextField.getCharCount());
                    				_commandPosition++;
                    			} else {
                    				_userInputTextField.setText(_commandHistory.get(_commandPosition)+" ");
                    				_userInputTextField.setSelection(_userInputTextField.getCharCount());
                    			}
                    		} else {
                    				_commandPosition++;
                    				_historyUp = true;
                    				_userInputTextField.setText(_commandHistory.get(_commandPosition)+" ");
                    				_userInputTextField.setSelection(_userInputTextField.getCharCount());
                    				if (_commandPosition<_commandHistory.size()-1){
                    					_commandPosition++;
                    				}
                    				
                    		}
                    	}
                    	break;
                    case SWT.ARROW_DOWN:
                    	if (_commandHistory.size()!=0){
                    		if (_commandPosition>0){
                    			_commandPosition--;
                    			_userInputTextField.setText(_commandHistory.get(_commandPosition));
                    			_userInputTextField.setSelection(_userInputTextField.getCharCount());
                    		} else {
                    			_commandPosition = -1;
                    			_userInputTextField.setText(EMPTY_STRING);
                    		}
                    		_historyUp = false;
                    	}
                    	break;
                    default:
                        break;
                }
            }
            public void keyReleased(KeyEvent e) {
                int key = e.keyCode;
                switch (key) {
                    case SWT.CR:
                        break;
                    case SWT.ARROW_UP:
            			_userInputTextField.setText(_userInputTextField.getText().trim());
            			_userInputTextField.setSelection(_userInputTextField.getCharCount());
                    	break;
                    case SWT.ARROW_DOWN:
                    	break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public String getUserInput() {
        return _userInputTextField.getText();
    }

    @Override
    public void displayOutput(String message) {
        final String outputMessage = message;
        _display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (outputMessage.contains("Error")
                        || outputMessage.contains("invalid")) {
                    _statusBar.setBackground(SWTResourceManager
                            .getColor(SWT.COLOR_RED));
                    if (outputMessage.contains("Error")) {
                        _statusBar.setText(ERROR);
                    } else {
                        _statusBar.setText(INVALID_COMMAND);
                    }
                } else {
                    _statusBar.setBackground(SWTResourceManager
                            .getColor(SWT.COLOR_GREEN));
                    if (outputMessage.contains("welcome")) {
                        _statusBar.setText(outputMessage);
                    } else {
                        _statusBar.setText(SUCCESS);
                    }
                }
            }
        });
    }

    @Override
    public void updateLists(ArrayList<String> allEventTasks,
            ArrayList<String> allDeadlineTasks, ArrayList<String> allTodoTasks) {
        StringBuilder sb = new StringBuilder();
        for (String event : allEventTasks) {
            sb.append(event + "\n");
        }
        _eventListUI.setText(sb.toString());
        sb = new StringBuilder();
        for (String deadline : allDeadlineTasks) {
            sb.append(deadline + "\n");
        }
        _deadlineListUI.setText(sb.toString());
        sb = new StringBuilder();
        for (String todo : allTodoTasks) {
        	sb.append(todo + "\n");
        }
        _todoListUI.setText(sb.toString());
    }

    public void loop() {
        while (!_shell.isDisposed()) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
        }
    }

}
