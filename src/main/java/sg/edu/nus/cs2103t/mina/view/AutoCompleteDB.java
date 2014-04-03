package sg.edu.nus.cs2103t.mina.view;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Helper class uses for auto complete function
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
//@author A0099324X

public class AutoCompleteDB {
    TreeSet<String> proposals;

    public AutoCompleteDB() {
    	proposals = new TreeSet<String>();
    	proposals.add("add");
    	proposals.add("create");
    	proposals.add("new");
		proposals.add("delete");
		proposals.add("remove");
		proposals.add("modify");
		proposals.add("change");
		proposals.add("edit");
		proposals.add("complete");
		proposals.add("undo");
		proposals.add("redo");
		proposals.add("search");
		proposals.add("display");
		proposals.add("exit");
		proposals.add("quit");
		proposals.add("modify deadline");
		proposals.add("modify event");
		proposals.add("modify todo");
		proposals.add("change deadline");
		proposals.add("change event");
		proposals.add("change todo");
		proposals.add("edit deadline");
		proposals.add("edit event");
		proposals.add("edit todo");
		proposals.add("delete deadline");
		proposals.add("delete event");
		proposals.add("delete todo");
		proposals.add("remove deadline");
		proposals.add("remove event");
		proposals.add("remove todo");
		proposals.add("complete deadline");
		proposals.add("complete event");
		proposals.add("complete todo");
		proposals.add("display deadline");
		proposals.add("display event");
		proposals.add("display todo");
		proposals.add("filter deadline");
		proposals.add("filter event");
		proposals.add("filter todo");
    }

    public String firstMatch(String input) {
        Iterator<String> iter = proposals.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str.length() > input.length() && str.substring(0,
                    input.length()).equals(input)) {
                return str;
            }
        }
        return "";
    }
}
