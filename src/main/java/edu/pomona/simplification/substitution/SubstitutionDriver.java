package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.TextFrequencyCalculator;

public class SubstitutionDriver {
	protected ArrayList<SubstitutionGenerator> generators = new ArrayList<SubstitutionGenerator>();
	protected ArrayList<SubstitutionInputFilter> inputFilters = new ArrayList<SubstitutionInputFilter>();
	protected ArrayList<SubstitutionResultFilter> resultFilters = new ArrayList<SubstitutionResultFilter>();

	public void addGenerator(SubstitutionGenerator g) {
		generators.add(g);
	}
	
	public void clearGenerators() {
		generators = new ArrayList<SubstitutionGenerator>();
	}

	public void addInputFilter(SubstitutionInputFilter filter) {
		inputFilters.add(filter);
	}

	public void addResultFilter(SubstitutionResultFilter filter) {
		resultFilters.add(filter);
	}

	public List<SimplificationOption> getAllSynonyms(List<POSTaggedWord> words){
		List<SimplificationOption> allOptions = new ArrayList<SimplificationOption>();

		for( POSTaggedWord word: words ) {
			if( passesInputFilters(word) ) {
				List<Substitution> synonyms = new ArrayList<Substitution>();
				
				for( SubstitutionGenerator generator: generators ) {
					List<Substitution> syns = toSubstitutions(generator.getSubstitutions(word), generator);

					if( syns != null ) {
						synonyms.addAll(syns);
					}
				}

				List<Substitution> filteredSynonyms = synonyms;

				for( SubstitutionResultFilter filter: resultFilters ) {
					filteredSynonyms = filter.filter(word, filteredSynonyms);
				}

				if( filteredSynonyms.size() > 0 ) {
					allOptions.add(new SimplificationOption(word.getWord(), filteredSynonyms));
				}
			}
		}

		return allOptions;
	}

	protected boolean passesInputFilters(POSTaggedWord word) {
		for(SubstitutionInputFilter filter: inputFilters) {
			if(filter.filter(word)) {
				return false;
			}
		}

		return true;
	}

	protected List<Substitution> toSubstitutions(List<String> synonyms, SubstitutionGenerator generator){
		List<Substitution> subs = new ArrayList<Substitution>(synonyms.size());

		for( String syn: synonyms ) {
			subs.add(new Substitution(syn, generator.sourceName()));
		}

		return subs;
	}

	/*protected static List<String> stripOffSource(List<String> synonyms){
		List<String> words = new ArrayList<String>(synonyms.size());

		for( String s: synonyms ) {
			words.add(stripOffSource(s));
		}

		return words;
	}*/

	/*private static String addSource(String synonym, SynonymGenerator generator){
	    return synonym + SOURCE_SEPARATOR + generator.sourceName();
    }*/
}
