package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.List;

import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.TextFrequencyCalculator;

public class WordFrequencyFilter implements SubstitutionResultFilter{
	
	/* Assumes that word already has frequency information annotated */
	@Override
	public List<Substitution> filter(POSTaggedWord word, List<Substitution> substitutions) {
		List<String> wordsOnly = Substitution.getAllSubstitutes(substitutions);
		List<Long> freqs = TextFrequencyCalculator.wordFreqLookupHandleMultiword(wordsOnly);
		
		ArrayList<Substitution> finalList = new ArrayList<Substitution>(substitutions.size());
		
		for( int i = 0; i < substitutions.size(); i++ ) {
			// make sure the frequency is higher AND that we haven't already added it
			if( freqs.get(i) > word.getFrequency() ) {
				finalList.add(substitutions.get(i));
			}
		}
		
		return finalList;
	}	
}