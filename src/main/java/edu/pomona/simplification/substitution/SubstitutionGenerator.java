package edu.pomona.simplification.substitution;

import java.util.List;

import edu.pomona.simplification.text.POSTaggedWord;

public interface SubstitutionGenerator {	
	public List<String> getSubstitutions(POSTaggedWord word);
	
	// returns an empty list if none exits
	public List<List<String>> getSubstitutions(List<POSTaggedWord> words);
	public String sourceName();
}
