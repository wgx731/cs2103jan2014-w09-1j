package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.nus.cs2103t.mina.model.FilterType;

public class FilterParameter {

	private ArrayList<FilterType> _filters;
	private Date _start;
	private Date _end;
	private boolean hasStart;
	private boolean hasEnd;
	
	public FilterParameter(ArrayList<String> newFilters) {
		this(newFilters, null, null);
	}

	public FilterParameter() {
		_filters = new ArrayList<FilterType>();
		_start = null;
		_end = null;
	}
	
	public Date getStart() {
		return _start;
	}

	public void setStart(Date start) {
		hasStart = (start==null);
		_start = start;
	}

	public Date getEnd() {
		return _end;
	}

	public void setEnd(Date end) {
		hasEnd = (end==null);
		_end = end;
	}

	public boolean hasStart() {
		return hasStart;
	}

	public boolean hasEnd() {
		return hasEnd;
	}

	public FilterParameter(ArrayList<String> newFilters, Date start,
												Date end) {
		_filters = getFilters(newFilters);
		_start = start;
		_end = end;
		
		if(start==null) {
			hasStart = false;
		} else {
			hasStart = true;
		}
		
		if(end==null) {
			hasEnd = false;
		} else {
			hasEnd = true;
		}
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
