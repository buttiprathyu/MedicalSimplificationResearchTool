package edu.pomona.simplification.morphologysp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import edu.pomona.simplification.Preferences;

public class Affixes{
	private HashMap<String,Affix> affixMap = new HashMap<String,Affix>();
	private ArrayList<String> orderedAffixStrings;

	public Affixes(String affixFile){
		BufferedReader inBr;
		FileInputStream inStream = null;
		String rawLine;
		try{
			// read in file
			inStream = new FileInputStream(affixFile);
			inBr = new BufferedReader(new InputStreamReader(inStream));
			while ((rawLine = inBr.readLine()) != null) {
				if(!rawLine.isEmpty()){
					if (!rawLine.startsWith("<")){
						String[] lineInfo = rawLine.split("\t");
						if (lineInfo.length == 3){
							Affix A = new Affix();
							A.setSummary(lineInfo[1]);
							A.setPrefix(lineInfo[2]);
							affixMap.put(lineInfo[0], A);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();				
		} finally {
			try {
				if (inStream != null){
					inStream.close();
				}
			} catch (IOException ex) {  
				ex.printStackTrace();
			}
		}

		// Sort Affix Array by length
		orderedAffixStrings = new ArrayList<>(affixMap.keySet());
		orderedAffixStrings.sort(Comparator.comparing(String::length).reversed());
	}

	public ArrayList<String> getMatches(String token){

		ArrayList<String> extractedSummary = new ArrayList<String>();
		String pMatch = null;
		String sMatch = null;
		for (String AffixString : orderedAffixStrings){
			Affix CurrentA = affixMap.get(AffixString);
			if(CurrentA.getPrefix() == true){
				if (token.toLowerCase().startsWith(AffixString.toLowerCase())){
					if (pMatch == null){
						pMatch = AffixString;
					}
				}
			}
			if(CurrentA.getPrefix() == false){
				if (token.toLowerCase().endsWith(AffixString.toLowerCase())){
					if (sMatch == null){
						sMatch = AffixString;
					}
				}
			}
		}
		if (pMatch != null){
			extractedSummary.add(pMatch + "\t" + affixMap.get(pMatch).getSummary());
		}
		if (sMatch != null){
			extractedSummary.add(sMatch + "\t" + affixMap.get(sMatch).getSummary());
		}
		return extractedSummary;
	}

	public boolean getPrefix(String affixString){
		return affixMap.get(affixString).getPrefix();
	}
	
	public static void main(String[] args) {
		new Affixes(Preferences.AFFIX_FILE);
		System.out.println("Affixes read");
	}
}
