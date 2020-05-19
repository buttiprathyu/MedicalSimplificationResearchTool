package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.List;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.morphology.AffixMatcher;
import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.Wordnet;

public class AffixSubstitutionGenerator implements SubstitutionGenerator{
	private AffixMatcher matcher;
	
	public AffixSubstitutionGenerator(Wordnet wn) {
		matcher = new AffixMatcher(Preferences.AFFIX_FILE, wn);
	}
	
	@Override
	public List<String> getSubstitutions(POSTaggedWord word) {
		List<String> results = new ArrayList<String>(1);
		
		String summary = matcher.getSummary(word.getWord());
		
		if( summary != null ) {
			results.add(summary);
		}
		
		return results;
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
		return "affix";
	}

}
