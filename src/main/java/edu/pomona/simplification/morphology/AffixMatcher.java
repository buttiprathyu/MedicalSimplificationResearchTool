package edu.pomona.simplification.morphology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.text.Wordnet;

public class AffixMatcher {
	private HashMap<String,Affix> prefixes = new HashMap<String,Affix>();
	private HashMap<String,Affix> suffixes = new HashMap<String,Affix>();
	private Wordnet wn;

	public AffixMatcher(String affixFile, Wordnet wn) {
		this.wn = wn;

		try {
			BufferedReader in = new BufferedReader(new FileReader(affixFile));

			String line;

			while((line = in.readLine()) != null) {
				for( Affix affix: Affix.parseAffixFileLine(line) ){
					if( affix.isPrefix() ) {
						prefixes.put(affix.getAffixString(), affix);
					}else {
						suffixes.put(affix.getAffixString(), affix);
					}
				}
			}

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param word word to lookup
	 * @return the affix-based summary for a word if one exists.  If one does 
	 * not exist, returns null.
	 */
	public String getSummary(String word) {
		// see if a prefix matches, working from longest to shortest
		Affix prefixFound = null;
		word = word.toLowerCase();
		String stem = word;
		String affixStem = word;

		for( int end = word.length(); end > 0 && prefixFound == null; end-- ) {
			String prefix = word.substring(0, end);

			if( prefixes.containsKey(prefix) ) {
				prefixFound = prefixes.get(prefix);
				stem = word.substring(prefix.length());
				affixStem = word.substring(prefix.length()-1);
			}
		}

		// see if a prefix matches, working from longest to shortest
		Affix suffixFound = null;

		for( int start = 0; start < affixStem.length() && suffixFound == null; start++ ) {
			String suffix = affixStem.substring(start);

			if( suffixes.containsKey(suffix) ) {
				suffixFound = suffixes.get(suffix);
				
				if( start == 0 ) {
					// the affix is the entire affixStem, so grab it all
					stem = affixStem.substring(0, affixStem.length()-suffix.length());
				}else {
					// just use the normal stem
					stem = stem.substring(0, stem.length()-suffix.length());					
				}
				
			}
		}

		String summary;

		if(prefixFound != null && suffixFound != null ) {
			if( stem.equals("") ){
				// a bit weird, but sometimes there is no "stem"
				summary = "["+prefixFound.getAffixString() + "] [" + suffixFound.getAffixString() + "] :\t";			
				summary += "["+ prefixFound.getDefinition() + "] [" + suffixFound.getDefinition()+"]";
			}else {
				summary = "["+prefixFound.getAffixString() + "] [" + stem + "] [" + suffixFound.getAffixString() + "] :\t";			
				summary += "["+prefixFound.getDefinition() + "] [" + getStemReplacement(stem) + "] [" + suffixFound.getDefinition() +"]";
			}	
		}else if( prefixFound != null ) {
			summary = "[" + prefixFound.getAffixString() + "] [" + stem + "] :\t";
			summary += "["+ prefixFound.getDefinition() + "] [" + getStemReplacement(stem)+"]";
		}else if( suffixFound != null ) {
			summary = "["+stem + "] [" + suffixFound.getAffixString() + "] :\t";
			summary += "[" + getStemReplacement(stem) + "] [" + suffixFound.getDefinition()+"]";
		}else {
			summary = null;
		}

		return summary;
	}
	
	
	/**
	 * @param word word to lookup
	 * @return the affix-based summary for a word if one exists.  If one does 
	 * not exist, returns null.
	 */
	public AffixMatch getSummaryDebug(String word) {
		// see if a prefix matches, working from longest to shortest
		Affix prefixFound = null;
		word = word.toLowerCase();
		String stem = word;
		String affixStem = word;

		for( int end = word.length(); end > 0 && prefixFound == null; end-- ) {
			String prefix = word.substring(0, end);

			if( prefixes.containsKey(prefix) ) {
				prefixFound = prefixes.get(prefix);
				stem = word.substring(prefix.length());
				affixStem = word.substring(prefix.length()-1);
			}
		}

		// see if a prefix matches, working from longest to shortest
		Affix suffixFound = null;

		for( int start = 0; start < affixStem.length() && suffixFound == null; start++ ) {
			String suffix = affixStem.substring(start);

			if( suffixes.containsKey(suffix) ) {
				suffixFound = suffixes.get(suffix);
				
				if( start == 0 ) {
					// the affix is the entire affixStem, so grab it all
					stem = affixStem.substring(0, affixStem.length()-suffix.length());
				}else {
					// just use the normal stem
					stem = stem.substring(0, stem.length()-suffix.length());					
				}
				
			}
		}

		String summary;

		if(prefixFound != null && suffixFound != null ) {
			if( stem.equals("") ){
				// a bit weird, but sometimes there is no "stem"
				summary = "["+prefixFound.getAffixString() + "] [" + suffixFound.getAffixString() + "] :\t";			
				summary += "["+ prefixFound.getDefinition() + "] [" + suffixFound.getDefinition()+"]";
			}else {
				summary = "["+prefixFound.getAffixString() + "] [" + stem + "] [" + suffixFound.getAffixString() + "] :\t";			
				summary += "["+prefixFound.getDefinition() + "] [" + getStemReplacement(stem) + "] [" + suffixFound.getDefinition() +"]";
			}	
		}else if( prefixFound != null && !stem.equals("")) {
			summary = "[" + prefixFound.getAffixString() + "] [" + stem + "] :\t";
			summary += "["+ prefixFound.getDefinition() + "] [" + getStemReplacement(stem)+"]";
		}else if( suffixFound != null && !stem.equals("") ) {
			summary = "["+stem + "] [" + suffixFound.getAffixString() + "] :\t";
			summary += "[" + getStemReplacement(stem) + "] [" + suffixFound.getDefinition()+"]";
		}else {
			summary = null;
		}
	
		return new AffixMatch(summary, prefixFound, suffixFound);
	}

	private String getStemReplacement(String stem) {
		if(stem == null || stem.equals("") ) {
			return null;
		}
		String definition = wn.getFirstSynonym(stem);
		
		/*try{
			definition = wn.getFirstSynonym(stem);
		}catch(java.lang.IllegalArgumentException e) {
			System.err.println("'" + stem + "'");
		}*/
		return definition == null ? stem : definition;
	}
	
	/*private String getStemReplacement(String stem) {
		String definition = wn.definitionLookup(stem);
		return definition == null ? stem : definition;
	}*/

	public static void main(String[] args) {
		AffixMatcher matcher = new AffixMatcher(Preferences.AFFIX_FILE, new Wordnet());

		String[] test = {"respiratory",
				"disorders",
				"clinic",
				"e.g.",
				"asthma",
				"tract",
				"URTI",
				"median",
				"duration",
				"follow-up",
				"survival",
				"papillary",
				"thyroid",
				"infancy",
				"sporadic",
				"carcinoma"};
		
		for( String word: test) {
			System.out.println(word + "\t" + matcher.getSummary(word));
		}
	}
}
