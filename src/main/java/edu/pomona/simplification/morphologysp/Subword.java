package edu.pomona.simplification.morphologysp;

import java.util.ArrayList;
import java.util.HashMap;

import edu.pomona.simplification.Preferences;

public class Subword {	
	private static Affixes As;
	private Integer startdx;
	private Integer enddx;
	private HashMap<String,String> currentMap;
	
	Subword(){
		As = new Affixes(Preferences.AFFIX_FILE);
	}
	
	public String summarize(String token){
		String theString = "";
		currentMap = new HashMap<String,String>();
		String stripped = strip(token);
		if (!stripped.equals(token)){
			String stripped2 = strip(stripped);
			if (!stripped2.equals(stripped)){
				stripped = strip(stripped2);
			} else {
				stripped = stripped2;
			}
		}
		theString += stripped + " ";
		for (String k : currentMap.keySet()){
			theString += k.toLowerCase() + " ";
			theString += currentMap.get(k) + " ";
		}
		
		return theString;
	}
	
	private String strip(String token){
		String stripped = null;
		startdx = null;
		enddx = null;
		ArrayList<String> matches = As.getMatches(token);
		for (String match : matches){
			String[] info = match.split("\t");
			String affix = info[0];
			currentMap.put(affix, info[1]);
			if (As.getPrefix(affix)){
				startdx = token.indexOf(affix.toLowerCase()) + affix.length();
			}else {
				enddx = token.lastIndexOf(affix.toLowerCase());
			}
		}
		if (startdx == null){
			startdx = 0;
		}
		if (enddx == null){
			enddx = token.length();
		}
		// Get root of word!
		try{
			stripped = token.substring(startdx,enddx);
		} catch (IndexOutOfBoundsException e) {
			if (startdx + enddx >= token.length() + 1){
				stripped = "";
			} else {
				stripped = token;
			}
		}
		return stripped;
	}
	
	public static void main(String[] args) {
		Subword sw = new Subword();
		
		System.out.println(sw.summarize("respiratory"));
	}
}
