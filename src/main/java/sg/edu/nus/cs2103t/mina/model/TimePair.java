package sg.edu.nus.cs2103t.mina.model;

import java.util.Date;

public class TimePair {

	private final Date _startDate;
	private final Date _endDate;
	
	public TimePair (Date startDate, Date endDate) {
		_startDate = startDate;
		_endDate = endDate;
	}
	
	/* getter methods */
	public Date getStartDate() {
		return _startDate;
	}

	public Date getEndDate() {
		return _endDate;
	}
	
}
