package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.ArrayList;

//@author A0099151B

public class SearchParameter {

	private ArrayList<String> _keywords;

	public SearchParameter(ArrayList<String> newKeywords) {
		_keywords = newKeywords;
	}

	public SearchParameter() {
		_keywords = new ArrayList<String>();
	}

	public ArrayList<String> getKeywords() {
		return _keywords;
	}
}
