package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.nus.cs2103t.mina.model.FilterType;

public class FilterParameter {

	private ArrayList<FilterType> _filters;
	private Date _start;
	private Date _end;
	private boolean _hasTime;

    public FilterParameter(ArrayList<String> newFilters) {
		this(newFilters, null, null, false);
	}

	public FilterParameter() {
		_filters = new ArrayList<FilterType>();
		_start = null;
		_end = null;
	}

	/**
	 * Create a new filer parameter with 
	 * @param newFilters
	 * @param start
	 * @param end
	 */
	public FilterParameter(ArrayList<String> newFilters, 
	                        Date start,
							Date end,
							boolean hasTime) {
		
		_filters = getFilterTypes(newFilters);
		setStart(start);
		setEnd(end);
		_hasTime = hasTime;
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
	private ArrayList<FilterType> getFilterTypes(ArrayList<String> rawFilters) {
		
		// TODO Discuss with the team about this.
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		
		for (FilterType filterType: FilterType.values()) {
			if(rawFilters.contains(filterType.getType())) {
				filters.add(filterType);
			}
		}
		return filters;
	}
	
	public Date getStart() {
		return _start;
	}

	public void setStart(Date start) {
		
		boolean hasStartFilter =  _filters.contains(FilterType.START);
		_start = start;
		
		if(start!=null && !hasStartFilter) {
			_filters.add(FilterType.START);
		} else if (hasStartFilter){
			_filters.remove(FilterType.START);
		}
	}

	public Date getEnd() {
		return _end;
	}

	public void setEnd(Date end) {
		
		boolean hasEndFilter =  _filters.contains(FilterType.END);
		_end = end;
		
		if(end!=null && !hasEndFilter) {
			_filters.add(FilterType.END);
		} else if (hasEndFilter){
			_filters.remove(FilterType.END);
		}
	}
	
   public boolean hasTime() {
        return _hasTime;
    }
	
	public boolean contains(FilterType type) {
	    return _filters.contains(type);
	}
	
	public boolean remove(FilterType type) {
	    return _filters.remove(type);
	}
	
	public boolean hasNoTaskTypes() {
	    return !(_filters.contains(FilterType.EVENT) ||
	            _filters.contains(FilterType.DEADLINE) ||
	            _filters.contains(FilterType.TODO));
	}
}
