package sg.edu.nus.cs2103t.mina.ui;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

public class AppWindow {

	protected Shell shell;
	private Display display;
	private Text userInputTextField;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
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
		shell.setSize(1080, 720);
		shell.setText("SWT Application");
		
		userInputTextField = new Text(shell, SWT.NONE);
		userInputTextField.setForeground(SWTResourceManager.getColor(0, 51, 0));
		userInputTextField.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		userInputTextField.setBounds(4, 680, 1076, 36);
		
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
		
		List eventList = new List(shell, SWT.NONE);
		eventList.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		eventList.setFont(SWTResourceManager.getFont("Comic Sans MS", 15, SWT.NORMAL));
		eventList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		eventList.setBounds(4, 40, 354, 636);
		
		List deadlineList = new List(shell, SWT.NONE);
		deadlineList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		deadlineList.setEnabled(false);
		deadlineList.setBounds(362, 40, 356, 636);
		
		List todoList = new List(shell, SWT.NONE);
		todoList.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN));
		todoList.setEnabled(false);
		todoList.setBounds(722, 40, 354, 636);
		
		for (int loopIndex = 0; loopIndex < 10; loopIndex++) {
		      eventList.add("Event " + loopIndex);
		}
	}
}
