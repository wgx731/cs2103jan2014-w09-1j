package sg.edu.nus.cs2103t.mina.view;

import java.util.Iterator;
import java.util.TreeSet;

public class AutoCompleteDB {
    TreeSet<String> proposals = new TreeSet<String>();

    public AutoCompleteDB() {
        proposals.add("add");
        proposals.add("add stuff");
        proposals.add("delete stuff");
        proposals.add("modify stuff");
        proposals.add("undo stuff");
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
