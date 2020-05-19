package edu.pomona.simplification.substitution;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.text.POSTaggedWord;

public class WordFrequencyInputFilter implements SubstitutionInputFilter{
	private int frequencyThreshold = Preferences.FREQUENCY_THRESHOLDS[Preferences.DEFAULT_FREQUENCY_CUTTOFF_INDEX];
	
	public void setFrequencyThreshold(int newFrequencyThreshold) {
		frequencyThreshold = newFrequencyThreshold;
	}
	
	@Override
	public boolean filter(POSTaggedWord word) {
		return word.getFrequency() >= frequencyThreshold;
	}
}