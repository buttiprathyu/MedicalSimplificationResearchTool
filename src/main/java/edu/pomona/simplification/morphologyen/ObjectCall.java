package edu.pomona.simplification.morphologyen;

import java.io.IOException;

public class ObjectCall {
	
	public static void main (String[] args) throws IOException {
	
		//Control Center to do main call and to do main counts
		TokenFilter tfr = new TokenFilter();
		String path = "/Users/pokea/Documents/Work/UofA/"
				+ "Current/MIS/AffixSimplification";
		DirIn pmDir = new DirIn(path + "/Pilot/Corpora/PubMed/Dev",tfr,0);
		DirIn wkDir = new DirIn(path + "/Pilot/Corpora/Med_Wiki/Dev",tfr,1);
		
		wkDir.writeFile(path + "/Results/WKresults.txt");
		pmDir.writeFile(path + "/Results/PMresults.txt");
		
		System.out.println("DONE!!!");

	}
}
