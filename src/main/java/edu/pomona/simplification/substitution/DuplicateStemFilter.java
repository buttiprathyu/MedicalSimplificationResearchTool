package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.PorterStemmer;

/**
 * Removes substitutions that have the same stem
 * 
 * @author drk04747
 *
 */
public class DuplicateStemFilter implements SubstitutionResultFilter{
	private PorterStemmer stemmer = new PorterStemmer();

	@Override
	public List<Substitution> filter(POSTaggedWord word, List<Substitution> substitutions) {
		// map from stem to "best" substitution found for that stem
		HashMap<String, Substitution> stems = new HashMap<String, Substitution>();
		
		String wordStem = stemmer.stem(word.getWord().toLowerCase());
		String wordEnd = getLastTwoChars(word.getWord());
		
		// Go through the list and find the best substitution for each stem
		for( Substitution sub: substitutions ) {
			String subStem = stemmer.stem(sub.getSubstitute());
			
			// first, make sure it's not a stemmed variant of the original word
			if( !wordStem.equals(subStem) ) {
				// now, check to see if a stemmed version of this substitution already exists
				if( stems.containsKey(subStem) ) {
					String subEnd = getLastTwoChars(sub.getSubstitute());
					
					if( wordEnd.equals(subEnd) ) {
						stems.put(subStem, sub);
					}
				}else {
					stems.put(subStem, sub);
				}
			}
		}
		
		// to maintain the original order, go back through and add the first occurrence to the output for each stem
		HashSet<String> addedAlready = new HashSet<String>();
		List<Substitution> returnMe = new ArrayList<Substitution>(substitutions.size());
		
		for( Substitution sub: substitutions ) {
			String subStem = stemmer.stem(sub.getSubstitute());
			
			if( stems.containsKey(subStem) && !addedAlready.contains(subStem)) {
				returnMe.add(stems.get(subStem));
				addedAlready.add(subStem);
			}
		}
				
		return returnMe;
	}
	
	private String getLastTwoChars(String s) {
		return s.substring(Integer.max(0, s.length()-2));
	}
}
