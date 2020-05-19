package edu.pomona.simplification.morphology;

import java.util.ArrayList;

public class Affix {
	private boolean isPrefix;
	private String affixString;
	private String definition;
	
	public Affix(boolean isPrefix, String affixString, String definition) {
		this.isPrefix = isPrefix;
		this.affixString = affixString;
		this.definition = definition;
	}
	
	public boolean isPrefix() {
		return isPrefix;
	}

	public String getAffixString() {
		return affixString;
	}

	public String getDefinition() {
		return definition;
	}
	
	public static ArrayList<Affix> parseAffixFileLine(String affixLine){
		// Split the Line by tabs
		String[] line = affixLine.split("\t");		
		String definition = line[1];
		
		ArrayList<Affix> affixes = new ArrayList<Affix>(5);
		
		for (String affix : line[0].split("\\s+")){
			affix = affix.replace(",", "");
			
			// figure out if it's a prefix or suffix
			boolean isPrefix = affix.endsWith("-");

			// remove the '-' to indicate prefix or suffix
			if( isPrefix ) {
				affix = affix.substring(0, affix.length()-1);
			}else {
				affix = affix.substring(1);
			}
			
			// check to see if it has an optional portion
			if (affix.indexOf("(") > 0){
				int start = affix.indexOf("(");
				int stop = affix.indexOf(")");
				String original = affix.substring(0, start);
				String variantString = original + affix.substring(start + 1, stop);
				affixes.add(new Affix(isPrefix, original, definition));
				affixes.add(new Affix(isPrefix, variantString, definition));
			} else {
				affixes.add(new Affix(isPrefix, affix, definition));
			}
		}
		
		return affixes;
	}
	
	public String toString() {
		return (isPrefix ? affixString + "-" : "-" + affixString) + "\t" + definition;
	}
}
