package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.TodoTask;

public class MinaGuiUI extends MinaView {

    private static final String EMPTY_STRING = "";
    
    private TaskView _taskView;

    private Shell _shell;
    private Display _display;
    private Text _userInputTextField;
    private Label _statusBar;
    private StyledText _eventListUI;
    private StyledText _deadlineListUI;
    private StyledText _todoListUI;
    private StyledText _backgroundBox;
    private Label _lblEvent;
    private Label _lblDeadline;
    private Label _lblTodo;
    
    private int _eventPage;
    private int _deadlinePage;
    private int _todoPage;
    
    private Label _eventPrevPage;
    private Label _eventNextPage;

    private Label _deadlinePrevPage;
    private Label _deadlineNextPage;

    private Label _todoPrevPage;
    private Label _todoNextPage;
    
    private final String RIGHT_ARROW = "\u2192";
    private final String LEFT_ARROW = "\u2190";
    
    private LinkedList<String> _commandHistory;
    private int _commandPosition;
    private boolean _historyUp;
    
    private int _currentTab;
    // private boolean _highlightOn;
    
    private static final String ERROR = "Operation failed. Please try again.";
    private static final String INVALID_COMMAND = "Invalid command. Please re-enter.";
    private static final String SUCCESS = "Operation completed";

    private static Logger logger = LogManager.getLogger(MinaGuiUI.class
            .getName());
    
    public MinaGuiUI(CommandController commandController) {
        super(commandController);
        createContents();
    }

    /**
     * Open the window.
     */
    public Shell open() {
    	logger.log(Level.INFO, "shell open");
        Monitor primary = _display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = _shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        _shell.setLocation(x, y);
        
    	logger.log(Level.INFO, "shell set position");
        _shell.open();
        _shell.layout();
        return _shell;
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
    	logger.log(Level.INFO, "shell create contents");
        _display = Display.getDefault();
        
        _commandHistory = new LinkedList<String>();
        _commandPosition = 0;
        _historyUp = true;
        
        _currentTab = 0;
        // _highlightOn = false;
        
        _eventPage = 1;
        _deadlinePage = 1;
        _todoPage = 1;

        _taskView = _commandController.getTaskView();
        
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
        
        _lblEvent = new Label(_shell, SWT.NONE);
        _lblEvent.setAlignment(SWT.CENTER);
        _lblEvent.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblEvent.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        _lblEvent.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _lblEvent.setBounds(4, 4, 354, 36);
        _lblEvent.setText("Events(e)");

        _lblDeadline = new Label(_shell, SWT.NONE);
        _lblDeadline.setAlignment(SWT.CENTER);
        _lblDeadline.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblDeadline.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        _lblDeadline.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_DARK_CYAN));
        _lblDeadline.setBounds(362, 4, 356, 36);
        _lblDeadline.setText("Deadlines(d)");

        _lblTodo = new Label(_shell, SWT.NONE);
        _lblTodo.setAlignment(SWT.CENTER);
        _lblTodo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblTodo.setFont(SWTResourceManager.getFont("Comic Sans MS", 15,
                SWT.BOLD));
        _lblTodo.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _lblTodo.setBounds(722, 4, 354, 36);
        _lblTodo.setText("To-do(td)");
        
        _todoNextPage = new Label(_shell, SWT.NONE);
        _todoNextPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoNextPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _todoNextPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _todoNextPage.setAlignment(SWT.CENTER);
        _todoNextPage.setBounds(992, 500, 84, 36);
        
        _todoPrevPage = new Label(_shell, SWT.NONE);
        _todoPrevPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoPrevPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _todoPrevPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _todoPrevPage.setAlignment(SWT.CENTER);
        _todoPrevPage.setBounds(722, 500, 84, 36);
        
        _deadlinePrevPage = new Label(_shell, SWT.NONE);
        _deadlinePrevPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _deadlinePrevPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _deadlinePrevPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _deadlinePrevPage.setAlignment(SWT.CENTER);
        _deadlinePrevPage.setBounds(362, 500, 84, 36);
        
        _deadlineNextPage = new Label(_shell, SWT.NONE);
        _deadlineNextPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _deadlineNextPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _deadlineNextPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _deadlineNextPage.setAlignment(SWT.CENTER);
        _deadlineNextPage.setBounds(634, 500, 84, 36);
        
        _eventPrevPage = new Label(_shell, SWT.NONE);
        _eventPrevPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventPrevPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _eventPrevPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _eventPrevPage.setAlignment(SWT.CENTER);
        _eventPrevPage.setBounds(4, 500, 84, 36);
        
        _eventNextPage = new Label(_shell, SWT.NONE);
        _eventNextPage.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventNextPage.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.BOLD));
        _eventNextPage.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
        _eventNextPage.setAlignment(SWT.CENTER);
        _eventNextPage.setBounds(274, 500, 84, 36);
        
        updateArrowNavigation();

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
                        _taskView = _commandController
                                .processUserInput(command, _eventPage, _deadlinePage, _todoPage);
                        _userInputTextField.setText(EMPTY_STRING);
                        displayOutput();
                        updateLists();
                        updateArrowNavigation();
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
        
        _backgroundBox = new StyledText(_shell, SWT.NONE);
        _backgroundBox.setDoubleClickEnabled(false);
        _backgroundBox.setEnabled(false);
        _backgroundBox.setEditable(false);
        _backgroundBox.setBackground(SWTResourceManager
                .getColor(255, 140, 0));
        _backgroundBox.setBounds(0, 0, 362, 540);
        
        
        _shell.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.TAB) {
                    _currentTab = (_currentTab+1)%3;
                	if (_currentTab==0){
                    	_backgroundBox.setBackground(SWTResourceManager
                                .getColor(255, 140, 0));
                        _backgroundBox.setBounds(0, 0, 362, 540);
                    } else if (_currentTab==1){
                    	_backgroundBox.setBackground(SWTResourceManager
                                .getColor(255, 140, 0));
                        _backgroundBox.setBounds(358, 0, 364, 540);
                    } else {
                    	_backgroundBox.setBackground(SWTResourceManager
                                .getColor(255, 140, 0));
                        _backgroundBox.setBounds(718, 0, 362, 540);
                    }
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.ARROW_RIGHT){
                	if (_currentTab==0){
                		int maxNumberOfEventPages = _taskView.maxEventPage();
                		if (_eventPage<maxNumberOfEventPages){
                			_eventPage++;
                        	updateLists();
                		}
                	} else if (_currentTab==1){
                		int maxNumberOfDeadlinePages = _taskView.maxDeadlinePage();
                		if (_deadlinePage<maxNumberOfDeadlinePages){
                			_deadlinePage++;
                        	updateLists();
                		}
                	} else {
                		int maxNumberOfTodoPages = _taskView.maxTodoPage();
                		if (_todoPage<maxNumberOfTodoPages){
                			_todoPage++;
                        	updateLists();
                		}
                	}
                	updateArrowNavigation();
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.ARROW_LEFT){
                	if (_currentTab==0){
                		if (_eventPage>1){
                			_eventPage--;
                        	updateLists();
                		}
                	} else if (_currentTab==1){
                		if (_deadlinePage>1){
                			_deadlinePage--;
                        	updateLists();
                		}
                	} else {
                		if (_todoPage>1){
                			_todoPage--;
                        	updateLists();
                		}
                	}
                	updateArrowNavigation();
                }
                if (event.stateMask != SWT.CTRL && ((event.keyCode>31 && event.keyCode<127)||(event.keyCode==SWT.ARROW_UP||
                		event.keyCode==SWT.ARROW_DOWN||event.keyCode==SWT.ARROW_LEFT||event.keyCode==SWT.ARROW_RIGHT))) {
                    _userInputTextField.forceFocus();
                    if (event.keyCode>31&&event.keyCode<127) {
                    	_userInputTextField.append(String.valueOf((char)event.keyCode));
                    }
                }
            }});
        
    }

    @Override
    public String getUserInput() {
    	logger.log(Level.INFO, "shell get user input");
        return _userInputTextField.getText();
    }

    @Override
    public void displayOutput() {
    	logger.log(Level.INFO, "shell diplay output");
        final String outputMessage = _taskView.getStatus();
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
    public void updateLists() {
    	logger.log(Level.INFO, "shell update lists");
    	ArrayList<Task<?>> eventList = _taskView.getPage(TaskType.EVENT, _eventPage);
    	ArrayList<Task<?>> deadlineList = _taskView.getPage(TaskType.DEADLINE, _deadlinePage);
    	ArrayList<Task<?>> todoList = _taskView.getPage(TaskType.TODO, _todoPage);

    	_eventListUI.setText(EMPTY_STRING);
    	for (int i=0; i<eventList.size(); i++) {
    		EventTask event = (EventTask) eventList.get(i);
    		_eventListUI.append((i+1)+". "+event.getDescription()
    				+ " from "+event.getStartTime()+" to "+event.getEndTime()+"\n");
    	}
    	_deadlineListUI.setText(EMPTY_STRING);
    	for (int i=0; i<deadlineList.size(); i++) {
    		DeadlineTask deadline = (DeadlineTask) deadlineList.get(i);
    		_deadlineListUI.append((i+1)+". "+deadline.getDescription()
    				+" by "+deadline.getEndTime()+"\n");
    	}
    	_todoListUI.setText(EMPTY_STRING);
    	for (int i=0; i<todoList.size(); i++) {
    		TodoTask todo = (TodoTask) todoList.get(i);
    		_todoListUI.append((i+1)+". "+todo.getDescription()+"\n");
    	}
    }

    public void loop() {
    	logger.log(Level.INFO, "shell running loop");
        while (!_shell.isDisposed()) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
        }
    }
    
    public void updateArrowNavigation(){
        if (_eventPage>=_taskView.maxEventPage()){
        	_eventNextPage.setText("");
        } else {
            _eventNextPage.setText(RIGHT_ARROW);
        }
        logger.log(Level.INFO, "max Event page: "+_taskView.maxEventPage());
        if (_eventPage==1){
        	_eventPrevPage.setText("");
        } else {
            _eventPrevPage.setText(LEFT_ARROW);
        }
        if (_deadlinePage>=_taskView.maxDeadlinePage()){
        	_deadlineNextPage.setText("");
        } else {
            _deadlineNextPage.setText(RIGHT_ARROW);
        }
        if (_deadlinePage==1){
        	_deadlinePrevPage.setText("");
        } else {
            _deadlinePrevPage.setText(LEFT_ARROW);
        }
        if (_todoPage>=_taskView.maxTodoPage()){
        	_todoNextPage.setText("");
        } else {
            _todoNextPage.setText(RIGHT_ARROW);
        }
        if (_todoPage==1){
        	_todoPrevPage.setText("");
        } else {
            _todoPrevPage.setText(LEFT_ARROW);
        }
    }
}
