package edu.pomona.simplification.substitution;

import edu.pomona.simplification.text.POSTaggedWord;

public interface SubstitutionInputFilter {
	public boolean filter(POSTaggedWord word);
}
