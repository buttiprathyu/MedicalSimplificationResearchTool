package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.PorterStemmer;

public class MorphNegation implements SubstitutionGenerator{
	// Declare Maps themselves
	private HashMap<String, String> acceptMap = new HashMap<String, String>();
	private HashMap<String, String> rejectMap = new HashMap<String, String>();
	private PorterStemmer stemmer;

	public MorphNegation(PorterStemmer stemmer) {
		this.stemmer = stemmer;

		//Read in Accept and Reject maps using Lexicon
		acceptMap = NegationLexicon.read(Preferences.NEGATION_LEXICON_ACCEPT, true);
		rejectMap = NegationLexicon.read(Preferences.NEGATION_LEXICON_REJECT, false);		
	}

	@Override
	public List<String> getSubstitutions(POSTaggedWord word) {
		String token = word.getWord();
		String stem = stemmer.stem(token).toLowerCase();
		List<String> results = new ArrayList<String>();

		if( !rejectMap.containsKey(token.toLowerCase()) ) {
			if (acceptMap.containsKey(stem)){
				results.add(getNegatedVersion(token, stem));
			}
			else if(stem.startsWith("non")) {
				results.add("not " + token.substring(3));
			}
		}

		return results;
	}
	
	/**
	 * Assumes it's already been checked and passes the accept/reject stages
	 * 
	 * @param word
	 * @param stem
	 * @return
	 */
	private String getNegatedVersion(String word, String stem) {
		String lexiconVersion = acceptMap.get(stem);
		
		// if the lexicon variant is a substring of the original
		// word, then try and extract the ending
		if( stem.contains(lexiconVersion) ) {
			if( word.startsWith("ab") || word.startsWith("il") ||
					word.startsWith("im") || word.startsWith("in") ||
					word.startsWith("ir") || word.startsWith("un")) {
				return "not " + word.substring(2);
			}else if( word.startsWith("dis") || word.startsWith("dys")) {
				return "not " + word.substring(3);
			}
		}
		
		// otherwise, just return whatever is in the Lexicon.  It will be stemmed
		// but it should be better than nothing
		return "not " + acceptMap.get(stem);
	}
	
	@Override
	public List<List<String>> getSubstitutions(List<POSTaggedWord> words) {
		ArrayList<List<String>> results = new ArrayList<List<String>>();

		for( POSTaggedWord w: words ) {
			results.add(getSubstitutions(w));
		}

		return results;
	}

	@Override
	public String sourceName() {
		return "negation";
	}

	public boolean isMorphNegation(String token) {
		String stem = stemmer.stem(token).toLowerCase();

		if( !rejectMap.containsKey(token.toLowerCase()) ) {
			if (acceptMap.containsKey(stem)){
				return true;
			}
			else if(stem.startsWith("non")) {
				return true;
			}
		}
		
		return false;
	}
}
