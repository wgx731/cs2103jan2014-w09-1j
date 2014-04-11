package sg.edu.nus.cs2103t.mina.view;

import java.util.ArrayList;

import sg.edu.nus.cs2103t.mina.model.HelperView;

/**
 * Helper class uses for display help
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
//@author A0099324X

public class UICommandHelper {	
	private final String MAIN_HELP_MENU_DESCRIPTION = "Help Contents\n\n\n";
	
	private final String ABOUT_MINA_DESCRIPTION = "MINA\nTask Manager Software\n\nVersion: 0.5\n\n"
			+ "Copyright 2000, 2013 (c).  All rights reserved.\n\n"
			+ "This product is developed by a group of CS2103T students from NUS\n"
			+ "Please visit https://code.google.com/p/cs2103jan2014-w09-1j/ for more information\n";
	
	private final String OPEN_HELP_MENU_DESCRIPTION = "\n\n\nTo open MINA, run MINA.jar\n"
			+ "To maximize/minimize the program, hold Alt and press F12\n\n";
	
	private final String HELP_MENU_NAVIGATION_DESCRIPTION = "\n\n\nTo exit help menu, press ESC\n"
			+ "To navigate back to previous menu, press BACKSPACE\n"
			+ "To navigate to a menu, select it by pressing 1-9 accordingly\n";
	
	private final String LIST_COMMANDS_HELP_MENU_DESCRIPTION = "This menu provides you all commands supported by MINA\n\n\n";
	
	private final String GUI_INTERACTION_HELP_DESCRIPTION = "\n\n\nTo select task panel, hold CTRL and press TAB\n"
			+ "To change page, hold CTRL and press \u2190 or \u2192\n OR type \"page [number]\" and press Enter"
			+ "To enable auto complete, hold CTRL and press BACKSPACE\n"
			+ "To expand a panel, press F11 OR hold CTRL and press E\n";
	
	private final String ADD_HELP_MENU_DESCRIPTION = "This menu provides you information about adding a new task\n"
			+ "Please select which type of task you want to add\n\n";
	
	private final String ADD_EVENT_HELP_MENU_DESCRIPTION = "This menu provides you commands to add a new event\n"
			+ "Events are tasks with a start and an end date, e.g.: meet clients 9pm - 10pm\n\n";
	
	private final String ADD_DEADLINE_HELP_MENU_DESCRIPTION = "This menu provides you commands to add a new deadline\n"
			+ "Deadlines are tasks with an end date, e.g.: submit report by 11:59pm\n\n";
	
	private final String ADD_TODO_HELP_MENU_DESCRIPTION = "This menu provides you commands to add a new todo\n"
			+ "Default priority for todo task is M(medium)\n\n";
	
	private final String ADD_RECURRING_HELP_MENU_DESCRIPTION = "This menu provides you commands to add a new recurring task\n"
			+ "If no end date of recurring stated, the recurring tasks will recur until the end of the current year\n\n";
		
	private final String DELETE_HELP_MENU_DESCRIPTION = "This menu provides you commands to delete a task\n\n\n";
	
	private final String MODIFY_HELP_MENU_DESCRIPTION = "This menu provides you information about modifying a task\n\n\n";
	
	private final String MODIFY_NORMAL_HELP_MENU_DESCRIPTION = "This menu provides you commands to modify normal information of a task without changing task type\n\n\n";
	
	private final String MODIFY_CHANGE_TYPE_HELP_MENU_DESCRIPTION = "This menu provides you commands to modify and change type of a task\n"
			+ "Take note that a task may need additional information when it changes to another type\n\n";
	
	private final String COMPLETE_HELP_MENU_DESCRIPTION = "This menu provides you commands to complete a task\n\n\n";
	
	private final String UNDO_REDO_HELP_MENU_DESCRIPTION = "This menu provides you commands to undo/redo\n"
			+ "This software supports up to 5 undos/redos\n\n";
	
	private final String DISPLAY_HELP_MENU_DESCRIPTION = "This menu provides you commands to display tasks\n\n\n";
	
	private final String SEARCH_HELP_MENU_DESCRIPTION = "This menu provides you commands to search\n\n\n";
	
	private final String EXIT_HELP_MENU_DESCRIPTION = "This menu provides you command to exit\n\n\n";	
	
	private final String DATE_FORMAT_HELP_MENU_DESCRIPTION = "This menu provides you date formats that the program supports\n"
			+ "examples: last Thursday 6pm, tmr 9am, 23/8/2015 10:21:32\n\n";
	
	@SuppressWarnings("serial")
	private final ArrayList<String> MAIN_HELP_MENU = new ArrayList<String>(){
		{
		add("open program");
		add("GUI interaction");
		add("help menu navigation");
		add("list of commands");
		add("supported date&time formats");
		add("about MINA");
		}
	};
	
	private final ArrayList<String> OPEN_HELP_MENU = new ArrayList<String>();
	
	private final ArrayList<String> GUI_INTERACTION_HELP = new ArrayList<String>();
	
	private final ArrayList<String> HELP_MENU_NAVIGATION =  new ArrayList<String>();
	
	@SuppressWarnings("serial")
	private final ArrayList<String> LIST_COMMANDS_HELP_MENU = new ArrayList<String>(){
		{
			add("add");
			add("delete");
			add("modify");
			add("complete");
			add("undo, redo");
			add("display");
			add("search");
			add("exit");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> DATE_FORMAT_HELP_MENU = new ArrayList<String>(){
		{
			add("dd/MM/yyyy\t\tdd-MM-yyyy\t\tdd.MM.yyyy\t\tdd MM yyyy");
			add("hh:mm\t\thh.mm\t\thh:mm:ss\t\thh.mm.ss");
			add("today\t\ttomorrow(tmr)\t\tyesterday\t\tnext\t\tlast");
		}
	};
	
	private final ArrayList<String> ABOUT_MINA = new ArrayList<String>();
	
	@SuppressWarnings("serial")
	private final ArrayList<String> ADD_HELP_MENU = new ArrayList<String>(){
		{
			add("event");
			add("deadline");
			add("todo");
			add("recurring");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> ADD_EVENT_HELP_MENU = new ArrayList<String>(){
		{
			add("add [description] -from [date time] -to [date time]");
			add("new [description] -start [date time] -end [date time]");
			add("create [description] -start [date time] -end [date time] [more description]");
			add("+ [description] -from [date time] -to [date time]");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> ADD_DEADLINE_HELP_MENU = new ArrayList<String>(){
		{
			add("add [description] -due [date time]");
			add("new [description] -end [date time]");
			add("create [description] -end [date time] [more description]");
			add("+ [description] -by [date time]");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> ADD_TODO_HELP_MENU = new ArrayList<String>(){
		{
			add("add [description]");
			add("new [description]");
			add("create [description] -priority [H/M/L]");
			add("+ [description]");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> ADD_RECURRING_HELP_MENU = new ArrayList<String>(){
		{
			add("add [description] -from [date time] -to [date time] -every [hour/day/week/month/year] -until [date time]");
			add("new [description] -by [date time] -every [hour/day/week/month/year] -until [date time]");
			add ("create [description -by [date time] -every [hour/day/week/month/year]");
		}
	};
		
	@SuppressWarnings("serial")
	private final ArrayList<String> DELETE_HELP_MENU = new ArrayList<String>(){
		{
			add("delete [todo/deadline/event] [index]");
			add("remove [td/d/e][index]");
			add("rm [td/d/e][index]");
			add("delete [recurring tasktype] [index] -all");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> MODIFY_HELP_MENU = new ArrayList<String>(){
		{
			add("modify task information");
			add("change task type");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> MODIFY_NORMAL_HELP_MENU = new ArrayList<String>(){
		{
			add("modify [task type][index] [new description]");
			add("edit [task type][index] -priority [new priority H/M/L]");
			add("edit [task type][index] -end [date time]");
			add("change [task type][index] -start [date/time] -end [date/time]");
			add("modify [recurring tasktype] -every [hour/day/week/month/year] -until [date time]");
			add("change [recurring tasktype] [description] -start [date time] -end [date time] -all");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> MODIFY_CHANGE_TYPE_HELP_MENU = new ArrayList<String>(){
		{
			add("change [task type][index] -totype [new type] [additional information:-start/-end]");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> COMPLETE_HELP_MENU = new ArrayList<String>(){
		{
			add("complete [task type][index]");
		}		
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> UNDO_REDO_HELP_MENU = new ArrayList<String>(){
		{
			add("undo");
			add("redo");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> DISPLAY_HELP_MENU = new ArrayList<String>(){
		{
			add("display [task type]");
			add("filter -from [date time] -to [date time]");
			add("show -agendaof [date]");
		}
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> SEARCH_HELP_MENU = new ArrayList<String>(){
		{
			add("search keyword");
			add("find 'phrase 1' 'phrase 2'");
		}		
	};
	
	@SuppressWarnings("serial")
	private final ArrayList<String> EXIT_HELP_MENU = new ArrayList<String>(){
		{
			add("exit");
			add("quit");
		}
	};
	
	private final String DISPLAY = "DISPLAY";
	private final String COPY = "COPY";
	
	public enum MenuType {
		MAIN,
		OPEN,
		GUI_INTERACTION,
		HELP_MENU_NAVIGATION,
		LIST_COMMANDS,
		DATE_FORMAT,
		ABOUT_MINA,
		ADD, ADD_EVENT, ADD_DEADLINE, ADD_TODO, ADD_RECURRING,
		DELETE,
		MODIFY, MODIFY_NORMAL, MODIFY_CHANGE_TYPE,
		COMPLETE,
		UNDO_REDO,
		DISPLAY,
		SEARCH,
		EXIT
	};
	
	private MenuType _currentMenu;
	
	public UICommandHelper(){
		_currentMenu = MenuType.MAIN;
	}
	
	public void setDefaultMenu(){
		_currentMenu = MenuType.MAIN;
	}
	
	public HelperView getDefaultMenu(){
		HelperView defaultView = new HelperView(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
		return defaultView;
	}
	
	public HelperView getHelperView(int menu_index){
		HelperView helperView = new HelperView();
		switch (_currentMenu){
		case MAIN:{
			if (menu_index>0 && menu_index<=MAIN_HELP_MENU.size()){
				switch(menu_index){
				case 1:{
					helperView.set(DISPLAY, OPEN_HELP_MENU_DESCRIPTION, OPEN_HELP_MENU);
					_currentMenu = MenuType.OPEN;
					break;
				} 
				case 2:{
					helperView.set(DISPLAY, GUI_INTERACTION_HELP_DESCRIPTION, GUI_INTERACTION_HELP);
					_currentMenu = MenuType.GUI_INTERACTION;
					break;
				}
				case 3:{
					helperView.set(DISPLAY, HELP_MENU_NAVIGATION_DESCRIPTION, HELP_MENU_NAVIGATION);
					_currentMenu = MenuType.HELP_MENU_NAVIGATION;
					break;
				}
				case 4:{
					helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
					_currentMenu = MenuType.LIST_COMMANDS;
					break;
				}
				case 5:{
					helperView.set(DISPLAY, DATE_FORMAT_HELP_MENU_DESCRIPTION, DATE_FORMAT_HELP_MENU);
					_currentMenu = MenuType.DATE_FORMAT;
					break;
				}
				case 6:{
					helperView.set(DISPLAY, ABOUT_MINA_DESCRIPTION, ABOUT_MINA);
					_currentMenu = MenuType.ABOUT_MINA;
					break;
				}
				default: break;
			}
			} else {
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case OPEN:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case GUI_INTERACTION:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case HELP_MENU_NAVIGATION:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case DATE_FORMAT:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case ABOUT_MINA:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			}
			return helperView;
		}
		case LIST_COMMANDS:{
			if (menu_index==0){
				helperView.set(DISPLAY, MAIN_HELP_MENU_DESCRIPTION, MAIN_HELP_MENU);
				_currentMenu = MenuType.MAIN;
			} else if (menu_index>0 && menu_index<=LIST_COMMANDS_HELP_MENU.size()){
				switch(menu_index){
				case 1:{
					helperView.set(DISPLAY, ADD_HELP_MENU_DESCRIPTION, ADD_HELP_MENU);
					_currentMenu = MenuType.ADD;
					break;
				}
				case 2:{
					helperView.set(DISPLAY, DELETE_HELP_MENU_DESCRIPTION, DELETE_HELP_MENU);
					_currentMenu = MenuType.DELETE;
					break;
				}
				case 3:{
					helperView.set(DISPLAY, MODIFY_HELP_MENU_DESCRIPTION, MODIFY_HELP_MENU);
					_currentMenu = MenuType.MODIFY;
					break;
				}
				case 4:{
					helperView.set(DISPLAY, COMPLETE_HELP_MENU_DESCRIPTION, COMPLETE_HELP_MENU);
					_currentMenu = MenuType.COMPLETE;
					break;
				}
				case 5:{
					helperView.set(DISPLAY, UNDO_REDO_HELP_MENU_DESCRIPTION, UNDO_REDO_HELP_MENU);
					_currentMenu = MenuType.UNDO_REDO;
					break;
				}
				case 6:{
					helperView.set(DISPLAY, DISPLAY_HELP_MENU_DESCRIPTION, DISPLAY_HELP_MENU);
					_currentMenu = MenuType.DISPLAY;
					break;
				}
				case 7:{
					helperView.set(DISPLAY, SEARCH_HELP_MENU_DESCRIPTION, SEARCH_HELP_MENU);
					_currentMenu = MenuType.SEARCH;
					break;
				}
				case 8:{
					helperView.set(DISPLAY, EXIT_HELP_MENU_DESCRIPTION, EXIT_HELP_MENU);
					_currentMenu = MenuType.EXIT;
					break;
				}
				default: break;	
				}
			}
			return helperView;
		}
		case ADD:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=ADD_HELP_MENU.size()){
				switch(menu_index){
				case 1:{
					helperView.set(DISPLAY, ADD_EVENT_HELP_MENU_DESCRIPTION, ADD_EVENT_HELP_MENU);
					_currentMenu = MenuType.ADD_EVENT;
					break;
				}
				case 2:{
					helperView.set(DISPLAY, ADD_DEADLINE_HELP_MENU_DESCRIPTION, ADD_DEADLINE_HELP_MENU);
					_currentMenu = MenuType.ADD_DEADLINE;
					break;
				}
				case 3:{
					helperView.set(DISPLAY, ADD_TODO_HELP_MENU_DESCRIPTION, ADD_TODO_HELP_MENU);
					_currentMenu = MenuType.ADD_TODO;
					break;
				}
				case 4:{
					helperView.set(DISPLAY, ADD_RECURRING_HELP_MENU_DESCRIPTION, ADD_RECURRING_HELP_MENU);
					_currentMenu = MenuType.ADD_RECURRING;
					break;
				}
				default: break;	
				}
			}
			return helperView;
		}
		case MODIFY:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=MODIFY_HELP_MENU.size()){
				switch(menu_index){
				case 1:{
					helperView.set(DISPLAY, MODIFY_NORMAL_HELP_MENU_DESCRIPTION, MODIFY_NORMAL_HELP_MENU);
					_currentMenu = MenuType.MODIFY_NORMAL;
					break;
				}
				case 2:{
					helperView.set(DISPLAY, MODIFY_CHANGE_TYPE_HELP_MENU_DESCRIPTION, MODIFY_CHANGE_TYPE_HELP_MENU);
					_currentMenu = MenuType.MODIFY_CHANGE_TYPE;
					break;
				}
				default: break;	
				}
			}
			return helperView;
		}
		case ADD_EVENT:{
			if (menu_index==0){
				helperView.set(DISPLAY, ADD_HELP_MENU_DESCRIPTION, ADD_HELP_MENU);
				_currentMenu = MenuType.ADD;
			} else if (menu_index>0 && menu_index<=ADD_EVENT_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(ADD_EVENT_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case ADD_DEADLINE:{
			if (menu_index==0){
				helperView.set(DISPLAY, ADD_HELP_MENU_DESCRIPTION, ADD_HELP_MENU);
				_currentMenu = MenuType.ADD;
			} else if (menu_index>0 && menu_index<=ADD_DEADLINE_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(ADD_DEADLINE_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case ADD_TODO:{
			if (menu_index==0){
				helperView.set(DISPLAY, ADD_HELP_MENU_DESCRIPTION, ADD_HELP_MENU);
				_currentMenu = MenuType.ADD;
			} else if (menu_index>0 && menu_index<=ADD_TODO_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(ADD_TODO_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case DELETE:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=DELETE_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(DELETE_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case MODIFY_NORMAL:{
			if (menu_index==0){
				helperView.set(DISPLAY, MODIFY_HELP_MENU_DESCRIPTION, MODIFY_HELP_MENU);
				_currentMenu = MenuType.MODIFY;
			} else if (menu_index>0 && menu_index<=MODIFY_NORMAL_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(MODIFY_NORMAL_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case MODIFY_CHANGE_TYPE:{
			if (menu_index==0){
				helperView.set(DISPLAY, MODIFY_HELP_MENU_DESCRIPTION, MODIFY_HELP_MENU);
				_currentMenu = MenuType.MODIFY;
			} else if (menu_index>0 && menu_index<=MODIFY_CHANGE_TYPE_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(MODIFY_CHANGE_TYPE_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case COMPLETE:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=COMPLETE_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(COMPLETE_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case UNDO_REDO:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=UNDO_REDO_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(UNDO_REDO_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case DISPLAY:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=DISPLAY_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(DISPLAY_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case SEARCH:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=SEARCH_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(SEARCH_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		case EXIT:{
			if (menu_index==0){
				helperView.set(DISPLAY, LIST_COMMANDS_HELP_MENU_DESCRIPTION, LIST_COMMANDS_HELP_MENU);
				_currentMenu = MenuType.LIST_COMMANDS;
			} else if (menu_index>0 && menu_index<=EXIT_HELP_MENU.size()){
				helperView.setHelperFunction(COPY);
				helperView.setHelperDescription(EXIT_HELP_MENU.get(menu_index-1));
			}
			return helperView;
		}
		default: return helperView;
		}
	}
	
}
