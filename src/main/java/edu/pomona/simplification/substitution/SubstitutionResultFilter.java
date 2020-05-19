package edu.pomona.simplification.substitution;

import java.util.List;

import edu.pomona.simplification.text.POSTaggedWord;

public interface SubstitutionResultFilter {
	public List<Substitution> filter(POSTaggedWord word, List<Substitution> substitutions);
}
