package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.List;

import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.Wordnet;

public class WordnetSynonymGenerator implements SubstitutionGenerator{
	private Wordnet wn;

	public WordnetSynonymGenerator(Wordnet wn){
		this.wn = wn;
	}

	@Override
	public List<String> getSubstitutions(POSTaggedWord word) {
		return wn.getSynonyms(word);
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
	public String sourceName(){
		return "wordnet";
	}
}
