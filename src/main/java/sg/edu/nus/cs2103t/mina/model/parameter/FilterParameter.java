package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;

import sg.edu.nus.cs2103t.mina.model.FilterType;

public class FilterParameter {

	private ArrayList<FilterType> _filters;

	public FilterParameter(ArrayList<String> newFilters) {
		_filters = getFilters(newFilters);
		
	}

	public FilterParameter() {
		_filters = new ArrayList<FilterType>();
	}

	public ArrayList<FilterType> getFilters() {
		return _filters;
	}

	/**
	 * Convert the argument String into their appropriate types 
	 * 
	 * @param filters
	 * @return Arraylist of FilterType
	 */
	private ArrayList<FilterType> getFilters(ArrayList<String> rawFilters) {
		
		// TODO Discuss with the team about this.
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		
		for (FilterType filterType: FilterType.values()) {
			if(rawFilters.contains(filterType.getType())) {
				filters.add(filterType);
			}
		}
		return filters;
	}
	
}
