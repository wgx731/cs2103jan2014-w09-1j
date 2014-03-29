package sg.edu.nus.cs2103t.mina.view;

import java.util.Iterator;
import java.util.TreeSet;

public class AutoCompleteDB {
	TreeSet<String> proposals = new TreeSet<String>();
	public AutoCompleteDB(){
		proposals.add("add");
		proposals.add("delete");
		proposals.add("modify");
		proposals.add("complete");
		proposals.add("undo");
		proposals.add("redo");
		proposals.add("search");
		proposals.add("display");
		proposals.add("modify deadline");
		proposals.add("modify event");
		proposals.add("modify todo");
		proposals.add("delete deadline");
		proposals.add("delete event");
		proposals.add("delete todo");
		proposals.add("complete deadline");
		proposals.add("complete event");
		proposals.add("complete todo");
		proposals.add("display deadline");
		proposals.add("display event");
		proposals.add("display todo");
	}
	
	public String firstMatch(String input){
		Iterator<String> iter = proposals.iterator();
		while (iter.hasNext()){
			String str = iter.next();
			if (str.length()>input.length()&&str.substring(0, input.length()).equals(input)){
				return str;
			}
		}
		return "";
	}
}
