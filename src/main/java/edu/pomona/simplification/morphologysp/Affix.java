package edu.pomona.simplification.morphologysp;

public class Affix {
	private String summary;
	private boolean isPrefix;

	public void setSummary(String inSummary){
		summary = inSummary;
	}

	public void setPrefix(String inPrefix){
		if (inPrefix.equals("True")){
			isPrefix = true;
		}else {
			isPrefix = false;
		}
	}

	public String getSummary(){
		return summary;
	}

	public boolean getPrefix(){
		return isPrefix;
	}
}
