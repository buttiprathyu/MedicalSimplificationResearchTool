package edu.pomona.simplification.substitution;

import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.PartOfSpeech;

public class POSOnlyInputFilter implements SubstitutionInputFilter{
	private PartOfSpeech acceptPOS;
	
	public POSOnlyInputFilter(PartOfSpeech acceptPOS) {
		this.acceptPOS = acceptPOS;
	}
	
	@Override
	public boolean filter(POSTaggedWord word) {
		// TODO Auto-generated method stub
		return !word.getPos().equals(acceptPOS);
	}	
}