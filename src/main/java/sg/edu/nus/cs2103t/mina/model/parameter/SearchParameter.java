package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;

public class SearchParameter {

    private ArrayList<String> keywords;

    public SearchParameter(ArrayList<String> newKeywords) {
        keywords = newKeywords;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }
}
