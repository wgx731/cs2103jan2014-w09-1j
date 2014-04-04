package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.KeyStroke;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.HelperView;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.utils.ConfigHelper;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

public class MinaGuiUI extends MinaView {

    private static final String MENU_EXIT = "Exit";
    private static final String MENU_OPEN = "Open";
    private static final String HIDE_SHOW_HOT_KEY = "alt F12";
    private static final String EMPTY_STRING = "";
    private static final String TRAY_TEXT = "MINA";

    private static final String BUSY = "Processing command ... ";
    private static final String ERROR = "Operation failed. Please try again.";
    private static final String INVALID_COMMAND = "Invalid command. Please re-enter.";
    private static final String SUCCESS = "Operation completed.";

    private TaskView _taskView;

    private Calendar _today;
    private Calendar _tomorrow;

    private AutoCompleteDB _autoComplete;

    private Shell _shell;
    private Display _display;
    private Image _trayImage;
    private Tray _tray;

    private StyledText _helpWindowBorder;
    private StyledText _helpWindow;
    private UICommandHelper _help;

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

    private int _eventMaxPage;
    private int _deadlineMaxPage;
    private int _todoMaxPage;

    private Label _eventPageLabel;
    private Label _deadlinePageLabel;
    private Label _todoPageLabel;

    private Label _eventPrevPage;
    private Label _eventNextPage;

    private Label _deadlinePrevPage;
    private Label _deadlineNextPage;

    private Label _todoPrevPage;
    private Label _todoNextPage;

    private final String RIGHT_ARROW = "\u2192";
    private final String LEFT_ARROW = "\u2190";

    private LinkedList<String> _commandUnHistory;
    private LinkedList<String> _commandReHistory;
    private boolean _isExpanded;

    private int _currentTab;

    private boolean _isAutoComplete;

    private int SHELL_WIDTH;
    private int SHELL_HEIGHT;

    private String UI_FONT;
    private int UI_FONT_SIZE;

    private static Logger logger = LogManager.getLogger(MinaGuiUI.class
            .getName());

    public MinaGuiUI(CommandManager commandController) {
        super(commandController);
        createContents();
        createTray();
        initGlobalKeyListener();
    }

    /**
     * Add global key listener
     */
    private void initGlobalKeyListener() {
        final Provider provider = Provider.getCurrentProvider(true);
        provider.reset();
        provider.register(KeyStroke.getKeyStroke(HIDE_SHOW_HOT_KEY),
                new HotKeyListener() {

                    @Override
                    public void onHotKey(final HotKey key) {
                        _display.syncExec(new Runnable() {
                            public void run() {
                                if (_shell.getVisible()) {
                                    _shell.setVisible(false);
                                } else {
                                    _shell.setVisible(true);
                                    _shell.setMinimized(false);
                                    _shell.forceActive();
                                }
                            }
                        });
                    }
                });

        _shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                _tray.dispose();
                provider.reset();
                provider.stop();
            }
        });
    }

    /**
     * Create system tray
     */
    private void createTray() {
        _tray = _display.getSystemTray();
        _trayImage = new Image(_display, getClass().getResourceAsStream(
                ConfigHelper.getProperty(ConfigHelper.ICONPATH_KEY)));

        TrayItem item;
        if (_tray == null) {
            logger.warn("The system tray is not available");
        } else {
            item = new TrayItem(_tray, SWT.NONE);
            item.setToolTipText(TRAY_TEXT);

            final Menu menu = new Menu(_shell, SWT.POP_UP);

            MenuItem openMenuItem = new MenuItem(menu, SWT.PUSH);
            openMenuItem.setText(MENU_OPEN);
            openMenuItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    _shell.setVisible(true);
                    _shell.setMinimized(false);
                    _shell.forceActive();
                }
            });
            MenuItem exitMenuItem = new MenuItem(menu, SWT.PUSH);
            exitMenuItem.setText(MENU_EXIT);
            exitMenuItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    System.exit(0);
                }
            });

            item.addListener(SWT.MenuDetect, new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });

            item.setImage(_trayImage);
        }

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

        if (_display.getBounds().width > 1024) {
            SHELL_WIDTH = 1096;
        } else {
            SHELL_WIDTH = 976;
        }

        SHELL_HEIGHT = 580;
        logger.log(Level.INFO, "width" + SHELL_WIDTH);

        UI_FONT = "Trebuchet MS";
        UI_FONT_SIZE = (SHELL_WIDTH == 1096) ? 15 : 14;

        _autoComplete = new AutoCompleteDB();

        _commandUnHistory = new LinkedList<String>();
        _commandReHistory = new LinkedList<String>();

        _currentTab = 0;

        _today = Calendar.getInstance();
        _tomorrow = Calendar.getInstance();
        _tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        _taskView = _commandController.getTaskView();

        _eventPage = 1;
        _deadlinePage = 1;
        _todoPage = 1;

        _isAutoComplete = false;

        _shell = new Shell(_display, SWT.NO_TRIM);
        _shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
        _shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
        _shell.setText("MINA");

        _helpWindow = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _helpWindow.setDoubleClickEnabled(false);
        _helpWindow.setEnabled(false);
        _helpWindow.setEditable(false);
        _helpWindow.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _helpWindow.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.NORMAL));
        _helpWindow.setBackground(SWTResourceManager.getColor(89, 89, 89));

        _helpWindowBorder = new StyledText(_shell, SWT.NONE);
        _helpWindowBorder.setDoubleClickEnabled(false);
        _helpWindowBorder.setEnabled(false);
        _helpWindowBorder.setEditable(false);
        _helpWindowBorder.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_BLACK));

        _helpWindowBorder.setBounds(0, 0, SHELL_WIDTH, SHELL_HEIGHT - 40);
        _helpWindow.setBounds(5, 5, SHELL_WIDTH - 10, SHELL_HEIGHT - 50);

        _helpWindowBorder.setVisible(false);
        _helpWindow.setVisible(false);
        _help = new UICommandHelper();

        _statusBar = new Label(_shell, SWT.NONE);
        _statusBar.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.NORMAL));
        _statusBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        _userInputTextField = new Text(_shell, SWT.NONE);
        _userInputTextField
                .setForeground(SWTResourceManager.getColor(0, 51, 0));
        _userInputTextField.setFont(SWTResourceManager.getFont(UI_FONT,
                UI_FONT_SIZE, SWT.NORMAL));

        _lblEvent = new Label(_shell, SWT.NONE);
        _lblEvent.setAlignment(SWT.CENTER);
        _lblEvent.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblEvent.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.BOLD));
        _lblEvent.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _lblEvent.setText("Events(e)");

        _lblDeadline = new Label(_shell, SWT.NONE);
        _lblDeadline.setAlignment(SWT.CENTER);
        _lblDeadline
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblDeadline.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.BOLD));
        _lblDeadline.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _lblDeadline.setText("Deadlines(d)");

        _lblTodo = new Label(_shell, SWT.NONE);
        _lblTodo.setAlignment(SWT.CENTER);
        _lblTodo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblTodo.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.BOLD));
        _lblTodo.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _lblTodo.setText("To-do(td)");

        _todoNextPage = new Label(_shell, SWT.NONE);
        _todoNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _todoNextPage
                .setFont(SWTResourceManager.getFont(UI_FONT, 20, SWT.BOLD));
        _todoNextPage.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _todoNextPage.setAlignment(SWT.CENTER);

        _todoPrevPage = new Label(_shell, SWT.NONE);
        _todoPrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _todoPrevPage
                .setFont(SWTResourceManager.getFont(UI_FONT, 20, SWT.BOLD));
        _todoPrevPage.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _todoPrevPage.setAlignment(SWT.CENTER);

        _deadlinePrevPage = new Label(_shell, SWT.NONE);
        _deadlinePrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlinePrevPage.setFont(SWTResourceManager.getFont(UI_FONT, 20,
                SWT.BOLD));
        _deadlinePrevPage
                .setBackground(SWTResourceManager.getColor(89, 89, 89));
        _deadlinePrevPage.setAlignment(SWT.CENTER);

        _deadlineNextPage = new Label(_shell, SWT.NONE);
        _deadlineNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineNextPage.setFont(SWTResourceManager.getFont(UI_FONT, 20,
                SWT.BOLD));
        _deadlineNextPage
                .setBackground(SWTResourceManager.getColor(89, 89, 89));
        _deadlineNextPage.setAlignment(SWT.CENTER);

        _eventPrevPage = new Label(_shell, SWT.NONE);
        _eventPrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _eventPrevPage.setFont(SWTResourceManager
                .getFont(UI_FONT, 20, SWT.BOLD));
        _eventPrevPage.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _eventPrevPage.setAlignment(SWT.CENTER);

        _eventNextPage = new Label(_shell, SWT.NONE);
        _eventNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _eventNextPage.setFont(SWTResourceManager
                .getFont(UI_FONT, 20, SWT.BOLD));
        _eventNextPage.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _eventNextPage.setAlignment(SWT.CENTER);

        _todoPageLabel = new Label(_shell, SWT.NONE);
        _todoPageLabel.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _todoPageLabel.setFont(SWTResourceManager.getFont(UI_FONT, 20,
                SWT.NORMAL));
        _todoPageLabel.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _todoPageLabel.setAlignment(SWT.CENTER);

        _deadlinePageLabel = new Label(_shell, SWT.NONE);
        _deadlinePageLabel.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlinePageLabel.setFont(SWTResourceManager.getFont(UI_FONT, 20,
                SWT.NORMAL));
        _deadlinePageLabel.setBackground(SWTResourceManager
                .getColor(89, 89, 89));
        _deadlinePageLabel.setAlignment(SWT.CENTER);

        _eventPageLabel = new Label(_shell, SWT.NONE);
        _eventPageLabel.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _eventPageLabel.setFont(SWTResourceManager.getFont(UI_FONT, 20,
                SWT.NORMAL));
        _eventPageLabel.setBackground(SWTResourceManager.getColor(89, 89, 89));
        _eventPageLabel.setAlignment(SWT.CENTER);

        updateArrowNavigation();

        _eventListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _eventListUI.setEnabled(false);
        _eventListUI.setEditable(false);
        _eventListUI
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventListUI.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.NORMAL));
        _eventListUI.setBackground(SWTResourceManager.getColor(89, 89, 89));

        _deadlineListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _deadlineListUI.setEnabled(false);
        _deadlineListUI.setEditable(false);
        _deadlineListUI.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineListUI.setFont(SWTResourceManager.getFont(UI_FONT,
                UI_FONT_SIZE, SWT.NORMAL));
        _deadlineListUI.setBackground(SWTResourceManager.getColor(89, 89, 89));

        _todoListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _todoListUI.setEnabled(false);
        _todoListUI.setEditable(false);
        _todoListUI.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoListUI.setFont(SWTResourceManager.getFont(UI_FONT, UI_FONT_SIZE,
                SWT.NORMAL));
        _todoListUI.setBackground(SWTResourceManager.getColor(89, 89, 89));

        _backgroundBox = new StyledText(_shell, SWT.NONE);
        _backgroundBox.setDoubleClickEnabled(false);
        _backgroundBox.setEnabled(false);
        _backgroundBox.setEditable(false);

        resetPanel();

        _userInputTextField.addListener(SWT.KeyUp, new Listener() {
            public void handleEvent(Event event) {
                if (event.keyCode > 31 && event.keyCode < 127) {
                    if (_isAutoComplete) {
                        autoComplete();
                    }
                }
            }
        });

        _userInputTextField.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.BS) {
                    setAutoComplete();
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.F11) {
                    if (_isExpanded) {
                        resetPanel();
                    } else {
                        expand();
                    }
                }
                if (event.keyCode == SWT.CR) {
                    showBusyStatus();
                    String command = _userInputTextField.getText();
                    addToUnHistory(command);
                    if (command.trim().toLowerCase().equals("help") || command
                            .trim().toLowerCase().equals("-h")) {
                        startHelpWindows();
                    } else if (command.length() >= 5 && command.substring(0, 5)
                            .equals("page ")) {
                        String[] commands = command.split(" ");
                        try {
                            int page = Integer.parseInt(commands[1]);
                            setPage(page);
                        } catch (Exception e) {
                            logger.log(Level.ERROR, e.getMessage());
                        }
                    } else {
                        _taskView = _commandController.processUserInput(
                                command, _eventPage, _deadlinePage, _todoPage);
                        updatePageFromTaskView();
                    }
                    _userInputTextField.setText(EMPTY_STRING);
                    updatePage();
                    displayOutput();
                    updateLists();
                    updateArrowNavigation();
                    if (_taskView.hasOnlyOneType(TaskType.EVENT)) {
                        _currentTab = 0;
                        expand();
                    } else if (_taskView.hasOnlyOneType(TaskType.DEADLINE)) {
                        _currentTab = 1;
                        expand();
                    } else if (_taskView.hasOnlyOneType(TaskType.TODO)) {
                        _currentTab = 2;
                        expand();
                    } else {
                        resetPanel();
                    }
                }
                if (event.keyCode == SWT.ARROW_UP) {
                    event.doit = false;
                    if (_commandUnHistory.size() != 0) {
                        String text = _commandUnHistory.removeFirst();
                        if (!_userInputTextField.getText().equals(EMPTY_STRING)) {
                            _commandReHistory.addFirst(_userInputTextField
                                    .getText());
                        }
                        _userInputTextField.setText(text);
                        _userInputTextField.setSelection(text.length());
                    }
                }
                if (event.keyCode == SWT.ARROW_DOWN) {
                    event.doit = false;
                    if (!_userInputTextField.getText().equals(EMPTY_STRING)) {
                        addToUnHistory(_userInputTextField.getText());
                    }
                    if (_commandReHistory.size() != 0) {
                        String text = _commandReHistory.removeFirst();
                        _userInputTextField.setText(text);
                        _userInputTextField.setSelection(text.length());
                    } else {
                        _userInputTextField.setText(EMPTY_STRING);
                    }
                }
                if (event.keyCode == SWT.ESC) {
                    _taskView = _commandController.processUserInput("display",
                            _eventPage, _deadlinePage, _todoPage);
                    resetPanel();
                    updateLists();
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.ARROW_RIGHT) {
        			event.doit = false;
    				event.detail = SWT.TRAVERSE_NONE;
        			if (_currentTab == 0) {
                        int maxNumberOfEventPages = _taskView.maxEventPage();
                        if (_eventPage < maxNumberOfEventPages) {
                            _eventPage++;
                            updateEventList();
                        }
                    } else if (_currentTab == 1) {
                        int maxNumberOfDeadlinePages = _taskView.maxDeadlinePage();
                        if (_deadlinePage < maxNumberOfDeadlinePages) {
                            _deadlinePage++;
                            updateDeadlineList();
                        }
                    } else {
                        int maxNumberOfTodoPages = _taskView.maxTodoPage();
                        if (_todoPage < maxNumberOfTodoPages) {
                            _todoPage++;
                            updateTodoList();
                        }
                    }
                    updateArrowNavigation();
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.ARROW_LEFT) {
                	event.doit = false;
                	event.detail = SWT.TRAVERSE_NONE;
                	if (_currentTab == 0) {
                        if (_eventPage > 1) {
                            _eventPage--;
                            updateEventList();
                        }
                    } else if (_currentTab == 1) {
                        if (_deadlinePage > 1) {
                            _deadlinePage--;
                            updateDeadlineList();
                        }
                    } else {
                        if (_todoPage > 1) {
                            _todoPage--;
                            updateTodoList();
                        }
                    }
                    updateArrowNavigation();
                }
            }
        });
        
        _userInputTextField.addTraverseListener(new TraverseListener(){
        	public void keyTraversed(TraverseEvent event){
        		if (event.stateMask==SWT.CTRL&&(event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS)){
        			event.doit = false;
        			_currentTab = (_currentTab + 1) % 3;
                    if (!_isExpanded) {
                        positionBackgroundBox();
                        showBackgroundBox();
                    } else {
                        if (_currentTab == 0) {
                            expandEvent();
                        } else if (_currentTab == 1) {
                            expandDeadline();
                        } else {
                            expandTodo();
                        }
                    }
        		}
                if (event.stateMask != SWT.CTRL && (event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS)) {
                    event.doit = false;
                    _userInputTextField.setSelection(_userInputTextField
                            .getText().length());
                }
            }
        });
    }

    private void updatePage() {
    	maxPages();
        if (_todoPage > _todoMaxPage) {
            _todoPage = _todoMaxPage;
        }
        if (_deadlinePage > _deadlineMaxPage) {
            _deadlinePage = _deadlineMaxPage;
        }
        if (_eventPage > _eventMaxPage) {
            _eventPage = _eventMaxPage;
        }
    }

    private void updatePageFromTaskView() {
    	if (_taskView.getTabEditedAlt()==0&&_taskView.getCurPageAlt()>0){
        	_eventPage = _taskView.getCurPageAlt();
        }
        if (_taskView.getTabEditedAlt()==1&&_taskView.getCurPageAlt()>0){
        	_deadlinePage = _taskView.getCurPageAlt();
        }
        if (_taskView.getTabEditedAlt()==2&&_taskView.getCurPageAlt()>0){
        	_todoPage = _taskView.getCurPageAlt();
        }
        if (_taskView.getTabEdited() == 0 && _taskView.getCurPage() > 0) {
            _eventPage = _taskView.getCurPage();
        }
        if (_taskView.getTabEdited() == 1 && _taskView.getCurPage() > 0) {
            _deadlinePage = _taskView.getCurPage();
        }
        if (_taskView.getTabEdited() == 2 && _taskView.getCurPage() > 0) {
            _todoPage = _taskView.getCurPage();
        }
    }

    private void setPage(int page) {
        if (page > 0) {
            if (_currentTab == 0) {
                _eventPage = page;
            } else if (_currentTab == 1) {
                _deadlinePage = page;
            } else {
                _todoPage = page;
            }
        }
    }

    private void addToUnHistory(String text) {
        if (_commandUnHistory.size() < 5) {
            _commandUnHistory.addFirst(text);
        } else {
            _commandUnHistory.removeLast();
            _commandUnHistory.addFirst(text);
        }
    }

    private void setAutoComplete() {
        _isAutoComplete ^= true;
        if (_isAutoComplete) {
            _statusBar.setBackground(SWTResourceManager.getColor(155, 187, 89));
            _statusBar.setText("\tAutoComplete Enabled");
        } else {
            _statusBar.setBackground(SWTResourceManager.getColor(247, 150, 70));
            _statusBar.setText("\tAutoComplete Disabled");
        }
    }

    private void autoComplete() {
        String curText = _userInputTextField.getText();
        String suggestText = _autoComplete.firstMatch(curText);
        if (suggestText.length() > 0) {
            _userInputTextField.setText(suggestText);
            _userInputTextField.setSelection(curText.length(),
                    suggestText.length());
        }
    }

    @Override
    public String getUserInput() {
        logger.log(Level.INFO, "shell get user input");
        return _userInputTextField.getText();
    }

    @Override
    public void displayOutput() {
        logger.log(Level.INFO, "shell diplay output");
        _display.asyncExec(new Runnable() {
            @Override
            public void run() {
                String outputMessage = _taskView.getStatus();
                _shell.setCursor(_display.getSystemCursor(SWT.CURSOR_ARROW));
                if (outputMessage.contains("Error") || outputMessage
                        .contains("invalid")) {
                    _statusBar.setBackground(SWTResourceManager.getColor(217,
                            83, 79));
                    if (outputMessage.contains("Error")) {
                        _statusBar.setText(ERROR);
                    } else {
                        _statusBar.setText(INVALID_COMMAND);
                    }
                } else {
                    _statusBar.setBackground(SWTResourceManager.getColor(92,
                            184, 92));
                    if (outputMessage.contains("welcome")) {
                        _statusBar.setText(outputMessage);
                    } else {
                        _statusBar.setText(SUCCESS);
                    }
                }
            }
        });
    }

    private void showBusyStatus() {
        _display.syncExec(new Runnable() {

            @Override
            public void run() {
                _statusBar.setText(BUSY);
                _statusBar.setBackground(SWTResourceManager.getColor(91, 192,
                        222));
                _shell.setCursor(_display.getSystemCursor(SWT.CURSOR_WAIT));
                _display.update();
            }

        });
    }

    @Override
    public void updateLists() {
        logger.log(Level.INFO, "shell update lists");
        if (_taskView.getTabEditedAlt()!=-1){
        	if (_taskView.getTabEditedAlt()==0){
            	updateEventList();
            } else if (_taskView.getTabEditedAlt()==1){
            	updateDeadlineList();
            } else if (_taskView.getTabEditedAlt()==2){
            	updateTodoList();
            }
        }
        if (_taskView.getTabEdited() == -1) {
            updateEventList();
            updateDeadlineList();
            updateTodoList();
        } else if (_taskView.getTabEdited() == 0) {
            _currentTab = 0;
        	updateEventList();
        } else if (_taskView.getTabEdited() == 1) {
            _currentTab = 1;
        	updateDeadlineList();
        } else if (_taskView.getTabEdited()==2){
            _currentTab = 2;
        	updateTodoList();
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

    private void updateEventList() {
        ArrayList<Task<?>> eventList = _taskView.getPage(TaskType.EVENT,
                _eventPage);
        _eventListUI.setText(EMPTY_STRING);
        Calendar currentDate = null;
        String currentDateString = "";
        Calendar itemStartDate;
        Calendar itemEndDate;
        int initialCursorPosition = 0;
        for (int i = 0; i < eventList.size(); i++) {
            EventTask event = (EventTask) eventList.get(i);
            itemStartDate = DateUtil.toCalendar(event.getStartTime());
            itemEndDate = DateUtil.toCalendar(event.getEndTime());
            if ((currentDate == null) || (!DateUtil.isSameDateCalendar(
                    currentDate, itemStartDate))) {
                currentDate = itemStartDate;
                StyleRange eventStyle = new StyleRange();
                if (currentDate.before(_today) && !DateUtil.isSameDateCalendar(
                        _today, currentDate)) {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = DateUtil.displayDateOnly(currentDate) + "\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager
                            .getColor(SWT.COLOR_GRAY);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_today, currentDate)) {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = "Today\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager.getColor(247,
                            150, 70);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_tomorrow, currentDate)) {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = "Tomorrow\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager.getColor(225,
                            212, 113);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = DateUtil.displayDateOnly(currentDate) + "\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager.getColor(155,
                            187, 89);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                }
            }
            String eventStringIndex = "\t" + (i + 1) + ". ";
            String eventDescription = event.getDescription() + "\n";
            String eventTime = "\t\t" + DateUtil.displayTimeOnly(itemStartDate) +
                    " - " +
                    (DateUtil.isSameDateCalendar(itemStartDate, itemEndDate) ? DateUtil
                            .displayTimeOnly(itemEndDate) : DateUtil
                            .displayDateTime(itemEndDate)) +
                    "\n";
            String eventCompleted = (event.isCompleted() ? "\t\tdone\n" : "");
            _eventListUI.append(eventStringIndex);
            initialCursorPosition += eventStringIndex.length();

            _eventListUI.append(eventDescription);
            StyleRange styleRange = new StyleRange();
            styleRange.start = initialCursorPosition;
            styleRange.length = eventDescription.length();
            styleRange.fontStyle = SWT.BOLD;
            _eventListUI.setStyleRange(styleRange);
            initialCursorPosition += eventDescription.length();

            _eventListUI.append(eventTime);
            initialCursorPosition += eventTime.length();

            _eventListUI.append(eventCompleted);
            StyleRange completedStyle = new StyleRange();
            completedStyle.start = initialCursorPosition;
            completedStyle.length = eventCompleted.length();
            completedStyle.fontStyle = SWT.ITALIC;
            _eventListUI.setStyleRange(completedStyle);
            initialCursorPosition += eventCompleted.length();
        }
    }

    private void updateDeadlineList() {
        ArrayList<Task<?>> deadlineList = _taskView.getPage(TaskType.DEADLINE,
                _deadlinePage);
        _deadlineListUI.setText(EMPTY_STRING);
        Calendar currentDate = null;
        String currentDateString = "";
        Calendar itemDate;
        int initialCursorPosition = 0;
        for (int i = 0; i < deadlineList.size(); i++) {
            DeadlineTask deadline = (DeadlineTask) deadlineList.get(i);
            itemDate = DateUtil.toCalendar(deadline.getEndTime());
            if ((currentDate == null) || (!DateUtil.isSameDateCalendar(
                    currentDate, itemDate))) {
                currentDate = itemDate;
                StyleRange deadlineStyle = new StyleRange();
                if (currentDate.before(_today) && !DateUtil.isSameDateCalendar(
                        _today, currentDate)) {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = DateUtil.displayDateOnly(currentDate) + "\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager
                            .getColor(SWT.COLOR_GRAY);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_today, currentDate)) {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = "Today\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager.getColor(247,
                            150, 70);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_tomorrow, currentDate)) {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = "Tomorrow\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager.getColor(225,
                            212, 113);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                } else {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = DateUtil.displayDateOnly(currentDate) + "\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager.getColor(155,
                            187, 89);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                }
            }
            String deadlineStringIndex = "\t" + (i + 1) + ". ";
            String deadlineDescription = deadline.getDescription();
            String deadlineTime = " by " + DateUtil.displayTimeOnly(itemDate) +
                    "\n";
            String deadlineCompleted = (deadline.isCompleted() ? "\t\tdone\n"
                    : "");
            _deadlineListUI.append(deadlineStringIndex);
            initialCursorPosition += deadlineStringIndex.length();

            _deadlineListUI.append(deadlineDescription);
            StyleRange styleRange = new StyleRange();
            styleRange.start = initialCursorPosition;
            styleRange.length = deadlineDescription.length();
            styleRange.fontStyle = SWT.BOLD;
            _deadlineListUI.setStyleRange(styleRange);
            initialCursorPosition += deadlineDescription.length();

            _deadlineListUI.append(deadlineTime);
            initialCursorPosition += deadlineTime.length();

            _deadlineListUI.append(deadlineCompleted);
            StyleRange completedStyle = new StyleRange();
            completedStyle.start = initialCursorPosition;
            completedStyle.length = deadlineCompleted.length();
            completedStyle.fontStyle = SWT.ITALIC;
            _deadlineListUI.setStyleRange(completedStyle);
            initialCursorPosition += deadlineCompleted.length();
        }
    }

    private void updateTodoList() {
        ArrayList<Task<?>> todoList = _taskView.getPage(TaskType.TODO,
                _todoPage);
        _todoListUI.setText(EMPTY_STRING);
        int initialCursorPosition = 0;
        for (int i = 0; i < todoList.size(); i++) {
            TodoTask todo = (TodoTask) todoList.get(i);
            String todoString = (i + 1) + ". " + todo.getDescription() + "\n";
            _todoListUI.append(todoString);
            StyleRange todoStyle = new StyleRange();
            todoStyle.start = initialCursorPosition;
            todoStyle.length = todoString.length();
            if (todo.getPriority() == 'H') {
                todoStyle.foreground = SWTResourceManager
                        .getColor(247, 150, 70);
            } else if (todo.getPriority() == 'M') {
                todoStyle.foreground = SWTResourceManager.getColor(225, 212,
                        113);
            } else {
                todoStyle.foreground = SWTResourceManager
                        .getColor(155, 187, 89);
            }
            _todoListUI.setStyleRange(todoStyle);
            initialCursorPosition += todoString.length();

            String todoCompleted = (todo.isCompleted() ? "\t\tdone\n" : "");

            _todoListUI.append(todoCompleted);
            StyleRange completedStyle = new StyleRange();
            completedStyle.start = initialCursorPosition;
            completedStyle.length = todoCompleted.length();
            completedStyle.fontStyle = SWT.ITALIC;
            _todoListUI.setStyleRange(completedStyle);
            initialCursorPosition += todoCompleted.length();
        }

    }

    private void expand() {
        if (_currentTab == 0) {
            expandEvent();
        } else if (_currentTab == 1) {
            expandDeadline();
        } else {
            expandTodo();
        }
    }

    private void updateArrowNavigation() {
        if (_eventPage >= _taskView.maxEventPage()) {
            _eventNextPage.setText("");
        } else {
            _eventNextPage.setText(RIGHT_ARROW);
        }
        if (_eventPage == 1) {
            _eventPrevPage.setText("");
        } else {
            _eventPrevPage.setText(LEFT_ARROW);
        }
        if (_deadlinePage >= _taskView.maxDeadlinePage()) {
            _deadlineNextPage.setText("");
        } else {
            _deadlineNextPage.setText(RIGHT_ARROW);
        }
        if (_deadlinePage == 1) {
            _deadlinePrevPage.setText("");
        } else {
            _deadlinePrevPage.setText(LEFT_ARROW);
        }
        if (_todoPage >= _taskView.maxTodoPage()) {
            _todoNextPage.setText("");
        } else {
            _todoNextPage.setText(RIGHT_ARROW);
        }
        if (_todoPage == 1) {
            _todoPrevPage.setText("");
        } else {
            _todoPrevPage.setText(LEFT_ARROW);
        }
        maxPages();
        _todoPageLabel.setText(_todoPage + "/" + _todoMaxPage);
        _deadlinePageLabel.setText(_deadlinePage + "/" + _deadlineMaxPage);
        _eventPageLabel.setText(_eventPage + "/" + _eventMaxPage);
    }

	private void maxPages() {
		_eventMaxPage = _taskView.maxEventPage();
        _deadlineMaxPage = _taskView.maxDeadlinePage();
        _todoMaxPage = _taskView.maxTodoPage();
        if (_eventMaxPage==0){
        	_eventMaxPage = 1;
        }
        if (_deadlineMaxPage==0){
        	_deadlineMaxPage = 1;
        }
        if (_todoMaxPage==0){
        	_todoMaxPage = 1;
        }
	}

    private void showEvent() {
        _lblEvent.setBounds(4, 4, (SHELL_WIDTH - 16) / 3, 36);
        _lblEvent.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setEventPanelSize(4, 40, (SHELL_WIDTH - 16) / 3, 496);
    }

    private void hideEvent() {
        _lblEvent.setBounds(4, 4, (SHELL_WIDTH - 16) / 3, 32);
        _lblEvent.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setEventPanelSize(4, 40, (SHELL_WIDTH - 16) / 3, 0);
    }

    private void showDeadline() {
        _lblDeadline.setBounds((SHELL_WIDTH - 16) / 3 + 8, 4,
                (SHELL_WIDTH - 16) / 3, 36);
        _lblDeadline.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setDeadlinePanelSize((SHELL_WIDTH - 16) / 3 + 8, 40,
                (SHELL_WIDTH - 16) / 3, 496);
    }

    private void hideDeadline() {
        _lblDeadline.setBounds((SHELL_WIDTH - 16) / 3 + 8, 4,
                (SHELL_WIDTH - 16) / 3, 32);
        _lblDeadline
                .setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setDeadlinePanelSize((SHELL_WIDTH - 16) / 3 + 8, 40,
                (SHELL_WIDTH - 16) / 3, 0);
    }

    private void showTodo() {
        _lblTodo.setBounds((SHELL_WIDTH - 16) / 3 * 2 + 12, 4,
                (SHELL_WIDTH - 16) / 3, 36);
        _lblTodo.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setTodoPanelSize((SHELL_WIDTH - 16) / 3 * 2 + 12, 40,
                (SHELL_WIDTH - 16) / 3, 496);
    }

    private void hideTodo() {
        _lblTodo.setBounds((SHELL_WIDTH - 16) / 3 * 2 + 12, 4,
                (SHELL_WIDTH - 16) / 3, 32);
        _lblTodo.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setTodoPanelSize((SHELL_WIDTH - 16) / 3 * 2 + 12, 40,
                (SHELL_WIDTH - 16) / 3, 0);
    }

    private void positionBackgroundBox() {
        if (_currentTab == 0) {
            _backgroundBox.setBounds(0, 0, (SHELL_WIDTH - 16) / 3 + 8, 540);
        } else if (_currentTab == 1) {
            _backgroundBox.setBounds((SHELL_WIDTH - 16) / 3 + 4, 0,
                    (SHELL_WIDTH - 16) / 3 + 8, 540);
        } else {
            _backgroundBox.setBounds((SHELL_WIDTH - 16) / 3 * 2 + 8, 0,
                    (SHELL_WIDTH - 16) / 3 + 8, 540);
        }
    }

    private void hideBackgroundBox() {
        _backgroundBox.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_BLACK));
    }

    private void showBackgroundBox() {
        _backgroundBox.setBackground(SWTResourceManager.getColor(153, 153, 51));
    }

    private void expandEvent() {
        hideDeadline();
        hideTodo();
        _lblEvent.setBounds(4, 4, (SHELL_WIDTH - 16) / 3, 36);
        _lblEvent.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setEventPanelSize(4, 40, SHELL_WIDTH - 8, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void expandDeadline() {
        hideEvent();
        hideTodo();
        _lblDeadline.setBounds((SHELL_WIDTH - 16) / 3 + 8, 4,
                (SHELL_WIDTH - 16) / 3, 36);
        _lblDeadline.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setDeadlinePanelSize(4, 40, SHELL_WIDTH - 8, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void expandTodo() {
        hideDeadline();
        hideEvent();
        _lblTodo.setBounds((SHELL_WIDTH - 16) / 3 * 2 + 12, 4,
                (SHELL_WIDTH - 16) / 3, 36);
        _lblTodo.setBackground(SWTResourceManager.getColor(89, 89, 89));
        setTodoPanelSize(4, 40, SHELL_WIDTH - 8, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void resetPanel() {
        showDeadline();
        showTodo();
        showEvent();
        positionBackgroundBox();
        showBackgroundBox();
        _statusBar.setBounds((SHELL_WIDTH - 16) / 3 * 2 + 12, 540,
                (SHELL_WIDTH - 16) / 3, 36);
        _userInputTextField.setBounds(4, 540, (SHELL_WIDTH - 16) / 3 * 2 + 4,
                36);
        _isExpanded = false;
    }

    private void setEventPanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _eventListUI.setBounds(x_coordinate, y_coordinate, width, height);
            _eventPrevPage.setBounds(x_coordinate, y_coordinate + height - 36,
                    84, 36);
            _eventNextPage.setBounds(x_coordinate + width - 84,
                    y_coordinate + height - 36, 84, 36);
            _eventPageLabel.setBounds(x_coordinate + width / 2 - 50,
                    y_coordinate + height - 36, 100, 36);
            _eventListUI.setVisible(true);
            _eventPrevPage.setVisible(true);
            _eventNextPage.setVisible(true);
            _eventPageLabel.setVisible(true);
        } else {
            _eventListUI.setVisible(false);
            _eventPrevPage.setVisible(false);
            _eventNextPage.setVisible(false);
            _eventPageLabel.setVisible(false);
        }
    }

    private void setDeadlinePanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _deadlineListUI
                    .setBounds(x_coordinate, y_coordinate, width, height);
            _deadlinePrevPage.setBounds(x_coordinate, y_coordinate + height -
                    36, 84, 36);
            _deadlineNextPage.setBounds(x_coordinate + width - 84,
                    y_coordinate + height - 36, 84, 36);
            _deadlinePageLabel.setBounds(x_coordinate + width / 2 - 50,
                    y_coordinate + height - 36, 100, 36);
            _deadlineListUI.setVisible(true);
            _deadlinePrevPage.setVisible(true);
            _deadlineNextPage.setVisible(true);
            _deadlinePageLabel.setVisible(true);
        } else {
            _deadlineListUI.setVisible(false);
            _deadlinePrevPage.setVisible(false);
            _deadlineNextPage.setVisible(false);
            _deadlinePageLabel.setVisible(true);
        }
    }

    private void setTodoPanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _todoListUI.setBounds(x_coordinate, y_coordinate, width, height);
            _todoPrevPage.setBounds(x_coordinate, y_coordinate + height - 36,
                    84, 36);
            _todoNextPage.setBounds(x_coordinate + width - 84,
                    y_coordinate + height - 36, 84, 36);
            _todoPageLabel.setBounds(x_coordinate + width / 2 - 50,
                    y_coordinate + height - 36, 100, 36);
            _todoListUI.setVisible(true);
            _todoPrevPage.setVisible(true);
            _todoNextPage.setVisible(true);
            _todoPageLabel.setVisible(true);
        } else {
            _todoListUI.setVisible(false);
            _todoPrevPage.setVisible(false);
            _todoNextPage.setVisible(false);
            _todoPageLabel.setVisible(false);
        }
    }

    public void startHelpWindows() {
        _helpWindowBorder.setVisible(true);
        _helpWindow.setVisible(true);
        _help.setDefaultMenu();
        HelperView defaultView = _help.getDefaultMenu();
        _helpWindow.append(defaultView.getHelperDescription());
        for (int i = 0; i < defaultView.getHelperText().size(); i++) {
            _helpWindow.append((i + 1) + ". ");
            _helpWindow.append(defaultView.getHelperText().get(i));
            _helpWindow.append("\n");
        }
        _helpWindow.append("\n[ESC:Exit help menu]");
        _userInputTextField.addListener(SWT.KeyUp, new Listener() {
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.BS) {
                    HelperView view = _help.getHelperView(0);
                    _helpWindow.setText(EMPTY_STRING);
                    _helpWindow.append(view.getHelperDescription());
                    for (int i = 0; i < view.getHelperText().size(); i++) {
                        _helpWindow.append((i + 1) + ". ");
                        _helpWindow.append(view.getHelperText().get(i));
                        _helpWindow.append("\n");
                    }
                    if (view.getHelperDescription().contains("Help Contents")) {
                        _helpWindow.append("\n[ESC:Exit help menu]");
                    } else {
                        _helpWindow
                                .append("\n[ESC:Exit help menu]\t\t\t\t[BACKSPACE:Back]");
                    }
                }
                if (event.keyCode == SWT.ESC) {
                    _helpWindowBorder.setVisible(false);
                    _helpWindow.setVisible(false);
                    _helpWindow.setText(EMPTY_STRING);
                    _userInputTextField.forceFocus();
                }
                if (event.keyCode >= '0' && event.keyCode <= '9') {
                    int menu_index = Integer.parseInt(String
                            .valueOf((char) event.keyCode));
                    _userInputTextField.setText(EMPTY_STRING);
                    HelperView view = _help.getHelperView(menu_index);
                    if (view.getHelperFunction().equals("DISPLAY")) {
                        _helpWindow.setText(EMPTY_STRING);
                        _helpWindow.append(view.getHelperDescription());
                        for (int i = 0; i < view.getHelperText().size(); i++) {
                            _helpWindow.append((i + 1) + ". ");
                            _helpWindow.append(view.getHelperText().get(i));
                            _helpWindow.append("\n");
                        }
                        if (view.getHelperDescription().contains(
                                "Help Contents")) {
                            _helpWindow.append("\n[ESC:Exit help menu]");
                        } else {
                            _helpWindow
                                    .append("\n[ESC:Exit help menu]\t\t\t\t[BACKSPACE:Back]");
                        }
                    } else if (view.getHelperFunction().equals("COPY")) {
                        String text = view.getHelperDescription();
                        _helpWindowBorder.setVisible(false);
                        _helpWindow.setVisible(false);
                        _helpWindow.setText(EMPTY_STRING);
                        _userInputTextField.forceFocus();
                        _userInputTextField.setText(text);
                        _userInputTextField.setSelection(text.length(),
                                text.length());
                    }
                } else if (_helpWindow.getVisible()) {
                    _userInputTextField.setText(EMPTY_STRING);
                }
            }
        });

    }
}
