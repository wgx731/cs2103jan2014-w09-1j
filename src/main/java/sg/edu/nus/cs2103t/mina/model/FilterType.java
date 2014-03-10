package sg.edu.nus.cs2103t.mina.model;

/**
 * Available filter types
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public enum FilterType {
	DEADLINE("deadline"),
	TODO("todo"),
	EVENT("event"),
	COMPLETE("complete"),
	COMPLETE_PLUS("+complete"),
	PRIORITY("priority"), 
	START("start");
		
	private String _type;
		
	private FilterType(String type) { 
		_type = type;
	}
		
	public String getType() {
		return _type;
	}
}
