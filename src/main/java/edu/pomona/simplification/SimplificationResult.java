package edu.pomona.simplification;

import java.util.ArrayList;
import java.util.List;

import edu.pomona.simplification.substitution.SimplificationOption;
import edu.pomona.simplification.substitution.Substitution;

public class SimplificationResult {
	private static final String SENTENCE_BEGIN_END_SEPARATOR = "-D-";
	
	// TODO: Should rename these variables to reflect the fact that they're
	// two separate things (lexical or word changes).
	private String word;
	private List<String> simplifications;
	
	private boolean isLexicalChange;
	// hacky:
	// - simplifications are extended by their source
	// - sentence changes are split by a delimiter
	
	/*public SimplificationOption(String word, List<String> simplifications) {
		this.word = word;
		this.simplifications = simplifications;
	}*/
	
	// for lexical changes
	public SimplificationResult(SimplificationOption option) {
		this.word = option.getWord();
		List<Substitution> substitutions = option.getSimplifications();
		this.simplifications = new ArrayList<String>(substitutions.size());
		
		for( Substitution sub: substitutions ) {
			simplifications.add(sub.toString());
		}
		
		isLexicalChange = true;
	}
	
	// for sentence changes
	public SimplificationResult(String sentenceChangeBegin, String coveredText, String changeInformation) {
		isLexicalChange = false;
		word = sentenceChangeBegin + SENTENCE_BEGIN_END_SEPARATOR + coveredText;
		
		// a bit hacky, but we'll put the change here in the first entry
		simplifications = new ArrayList<String>(1);
		simplifications.add(changeInformation);
	}
	
	public boolean getIsLexicalChange() {
		return isLexicalChange;
	}
	
	public String getWord() {
		return word;
	}
		
	public List<String> getSimplifications() {
		return simplifications;
	}
	
	public void addOptions(SimplificationResult other) {
		if( !other.word.equals(word) ) {
			System.err.println("Warning: attempted to add options for different words");
			System.err.println(word + " vs. " + other.word);
		}else {
			simplifications.addAll(other.simplifications);
		}
	}	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if( isLexicalChange ) {
			buffer.append(word + " -> ");
			
			for( String option: simplifications ) {
				buffer.append(option + ", ");
			}
		}
		
		return buffer.toString();
	}
}
