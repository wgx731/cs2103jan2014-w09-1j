package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;

public class FilterParameter {

    private ArrayList<String> filters;

    public FilterParameter(ArrayList<String> newFilters) {
        filters = newFilters;
    }

    public FilterParameter() {
        filters = new ArrayList<String>();
    }

    public ArrayList<String> getFilters() {
        return filters;
    }

}
