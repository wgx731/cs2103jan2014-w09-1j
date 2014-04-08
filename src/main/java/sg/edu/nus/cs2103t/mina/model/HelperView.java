package sg.edu.nus.cs2103t.mina.model;

import java.util.ArrayList;

/**
 * View class uses for help display
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
//@author A0099324X

public class HelperView {
	private String _helperFunction;
	private ArrayList<String> _helperText;
	private String _helperDescription;
	
	private final String EMPTY_STRING = "";
	
	public HelperView(){
		setHelperFunction(EMPTY_STRING);
		setHelperDescription(EMPTY_STRING);
		setHelperText(new ArrayList<String>());
	}
	
	public HelperView(String func, String desc, ArrayList<String> text){
		setHelperFunction(func);
		setHelperDescription(desc);
		setHelperText(text);
	}
	
	public String getHelperFunction(){
		return _helperFunction;
	}
	
	public String getHelperDescription(){
		return _helperDescription;
	}
	
	public ArrayList<String> getHelperText(){
		return _helperText;
	}
	
	public void setHelperFunction(String func){
		_helperFunction = func;
	}
	
	public void setHelperDescription(String desc){
		_helperDescription = desc;
	}
	
	public void setHelperText(ArrayList<String> text){
		_helperText = text;
	}
	
	public void set(String func, String desc, ArrayList<String> text){
		setHelperFunction(func);
		setHelperDescription(desc);
		setHelperText(text);
	}
}
