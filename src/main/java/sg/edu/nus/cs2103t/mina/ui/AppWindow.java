package sg.edu.nus.cs2103t.mina.ui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.controller.DataSyncManager;
import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;

public class AppWindow {

	protected Shell shell;
	private Display display;
	private Text userInputTextField;
	
	private static CommandController _cc;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			_cc = new CommandController();
			AppWindow window = new AppWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation (x, y);		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
		shell.setSize(1080, 540);
		shell.setText("SWT Application");
		
		userInputTextField = new Text(shell, SWT.NONE);
		userInputTextField.setForeground(SWTResourceManager.getColor(0, 51, 0));
		userInputTextField.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		userInputTextField.setBounds(4, 500, 1072, 36);
		
		Label lblEvent = new Label(shell, SWT.NONE);
		lblEvent.setAlignment(SWT.CENTER);
		lblEvent.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEvent.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.BOLD));
		lblEvent.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		lblEvent.setBounds(4, 4, 354, 36);
		lblEvent.setText("Events(e)");
		
		Label lblDeadline = new Label(shell, SWT.NONE);
		lblDeadline.setAlignment(SWT.CENTER);
		lblDeadline.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDeadline.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.BOLD));
		lblDeadline.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		lblDeadline.setBounds(362, 4, 356, 36);
		lblDeadline.setText("Deadlines(d)");
		
		Label lblTodo = new Label(shell, SWT.NONE);
		lblTodo.setAlignment(SWT.CENTER);
		lblTodo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTodo.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.BOLD));
		lblTodo.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		lblTodo.setBounds(722, 4, 354, 36);
		lblTodo.setText("To-do(td)");
		
		final List eventList = new List(shell, SWT.NONE);
		eventList.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		eventList.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		eventList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		eventList.setBounds(4, 40, 354, 456);
		
		final List deadlineList = new List(shell, SWT.NONE);
		deadlineList.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		deadlineList.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		deadlineList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		deadlineList.setBounds(362, 40, 356, 456);
		
		final List todoList = new List(shell, SWT.NONE);
		todoList.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		todoList.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		todoList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		todoList.setBounds(722, 40, 354, 456);

		ArrayList<String> todo = _cc.getTodoTask();
   	 	for (int i=0; i<todo.size(); i++){
   	 		todoList.add(todo.get(i));
   	 	}
   	 	ArrayList<String> deadline = _cc.getDeadlineTask();
   	 	for (int i=0; i<deadline.size(); i++){
   	 		deadlineList.add(deadline.get(i));
   	 	}   	 	
   	 	ArrayList<String> event = _cc.getEventTask();
   	 	for (int i=0; i<event.size(); i++){
   	 		eventList.add(event.get(i));
   	 	}
   	 
		userInputTextField.addKeyListener(new KeyAdapter(){
	         public void keyPressed(KeyEvent e) {
	             int key = e.keyCode;
	             if (key == SWT.CR) {
	            	 _cc.processUserInput(userInputTextField.getText());
	            	 if (userInputTextField.getText().equals("exit")){
	            		 shell.close();
	            	 } else {
	            	 userInputTextField.setText("");
	            	 todoList.removeAll();
	            	 ArrayList<String> todo = _cc.getTodoTask();
	            	 for (int i=0; i<todo.size(); i++){
	            		 todoList.add(todo.get(i));
	            	 }
	            	 deadlineList.removeAll();
	            	 ArrayList<String> deadline = _cc.getDeadlineTask();
	            	 for (int i=0; i<deadline.size(); i++){
	            		 deadlineList.add(deadline.get(i));
	            	 }
	            	 eventList.removeAll();
	            	 ArrayList<String> event = _cc.getEventTask();
	            	 for (int i=0; i<event.size(); i++){
	            		 eventList.add(event.get(i));
	            	 }
	            	}
	                }
	             }
	           });
	}
}
