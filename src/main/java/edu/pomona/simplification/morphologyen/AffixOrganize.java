package edu.pomona.simplification.morphologyen;

import java.util.Arrays;
import java.util.HashMap;
public class AffixOrganize {
	private String[] SortedAffixes;
	public AffixOrganize(HashMap<String,String> affixMap){
		//convert to array and sort
		SortedAffixes = affixMap.keySet().toArray(new String[affixMap.size()]);
		Arrays.sort(SortedAffixes, (b, a)->Integer.compare(a.length(), b.length()));
	}
		
	public String[] getSorted(){
		return SortedAffixes;
	}
}
