package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.List;

public class SimplificationOption {
	private String word;
	private List<Substitution> simplifications;
	
	public SimplificationOption(SimplificationOption copy) {
		this.word = copy.word;
		this.simplifications = new ArrayList<Substitution>(copy.simplifications);
	}
	
	public SimplificationOption(String word, List<Substitution> simplifications) {
		this.word = word;
		this.simplifications = simplifications;
	}
		
	public String getWord() {
		return word;
	}
		
	public List<Substitution> getSimplifications() {
		return simplifications;
	}
	
	public void addOptions(SimplificationOption other) {
		if( !other.word.equals(word) ) {
			System.err.println("Warning: attempted to add options for different words");
			System.err.println(word + " vs. " + other.word);
		}else {
			simplifications.addAll(other.simplifications);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(word + " -> ");
		
		for( Substitution s: simplifications ) {
			buffer.append( s.getSubstitute() + "(" + s.getSource() + "), ");
		}
		
		return buffer.toString();
	}
}
