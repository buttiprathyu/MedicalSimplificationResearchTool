package edu.pomona.simplification.negate;

import java.util.HashMap;

import edu.pomona.simplification.Preferences;

public class MorphNegationAnnotator implements Annotator {
	// Declare Stemmer and Lexicon Objects
	private	Stemmer porterStemmer = new Stemmer();

	// Declare Relative Paths for Reject/Accept Maps
	//private String acceptPath = "negate/lexicons/Accept.txt";
	//private String rejectPath = "negate/lexicons/Reject.txt";

	// Declare Maps themselves
	private HashMap<String, String> acceptMap = new HashMap<String, String>();
	private HashMap<String, String> rejectMap = new HashMap<String, String>();

	public MorphNegationAnnotator() {
		//Read in Accept and Reject maps using Lexicon
		acceptMap = Lexicon.read(Preferences.NEGATION_LEXICON_ACCEPT, true);
		rejectMap = Lexicon.read(Preferences.NEGATION_LEXICON_REJECT, false);		
	}

	public void annotate(Sentence s) {
		int idx = 0;

		for (String token : s.toList()){
			// Get Stem and lower
			String stem = porterStemmer.stem(token).toLowerCase();

			if (acceptMap.containsKey(stem) || stem.startsWith("non")) {
				if (!rejectMap.containsKey(token.toLowerCase())){
					// set the Negation Map
					s.addNegation(idx, Negation.MORPHOLOGICAL);
				}
			}

			idx += 1;
		}
	}
}
