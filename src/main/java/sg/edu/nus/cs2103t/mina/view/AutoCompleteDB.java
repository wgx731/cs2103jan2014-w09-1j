package sg.edu.nus.cs2103t.mina.view;

import java.util.TreeSet;

/**
 * Helper class uses for auto complete function
 */
//@author A0099324X

public class AutoCompleteDB {
    private static final String EMPTY_STRING = "";
	TreeSet<String> proposals;

    public AutoCompleteDB() {
    	proposals = new TreeSet<String>();
    	proposals.add("add");
    	proposals.add("create");
    	proposals.add("new");
    	proposals.add("make");
    	proposals.add("+");
		proposals.add("delete");
		proposals.add("remove");
		proposals.add("rm");
		proposals.add("-");
		proposals.add("modify");
		proposals.add("change");
		proposals.add("edit");
		proposals.add("complete");
		proposals.add("finish");
		proposals.add("undo");
		proposals.add("redo");
		proposals.add("search");
		proposals.add("find");
		proposals.add("display");
		proposals.add("filter");
		proposals.add("show");
		proposals.add("exit");
		proposals.add("quit");
		proposals.add("modify deadline");
		proposals.add("modify event");
		proposals.add("modify todo");
		proposals.add("edit deadline");
		proposals.add("edit event");
		proposals.add("edit todo");
		proposals.add("change deadline");
		proposals.add("change event");
		proposals.add("change todo");
		proposals.add("delete deadline");
		proposals.add("delete event");
		proposals.add("delete todo");
		proposals.add("remove deadline");
		proposals.add("remove event");
		proposals.add("remove todo");
		proposals.add("- deadline");
		proposals.add("- event");
		proposals.add("- todo");
		proposals.add("rm deadline");
		proposals.add("rm event");
		proposals.add("rm todo");
		proposals.add("complete deadline");
		proposals.add("complete event");
		proposals.add("complete todo");
		proposals.add("finish deadline");
		proposals.add("finish event");
		proposals.add("finish todo");
		proposals.add("display deadline");
		proposals.add("display event");
		proposals.add("display todo");
		proposals.add("filter deadline");
		proposals.add("filter event");
		proposals.add("filter todo");
		proposals.add("show deadline");
		proposals.add("show event");
		proposals.add("show todo");
    }

    public String firstMatch(String input) {
        String str = proposals.ceiling(input);
        if (str.equals(input)||str.contains(input)){
        	return str;
        } else {
        	return EMPTY_STRING;
        }
    }
}
