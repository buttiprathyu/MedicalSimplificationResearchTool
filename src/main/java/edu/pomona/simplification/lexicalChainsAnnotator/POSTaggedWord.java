package edu.pomona.simplification.lexicalChainsAnnotator;
import java.util.ArrayList;
import java.util.List;

public class POSTaggedWord {
	private String word;
	private PartOfSpeech pos;
	private long frequency = -1;
	
	public POSTaggedWord(String word, String pennPos) {
		this.word = word;
		pos = convertPennPos(pennPos);
	}
	
	public POSTaggedWord(String word, PartOfSpeech pos) {
		this.word = word;
		this.pos = pos;
	}
	
	public String getWord() {
		return word;
	}

	public PartOfSpeech getPos() {
		return pos;
	}
	
	public long getFrequency() {
		if( frequency == -1 ) {
			System.err.println("Warning: getting frequency of word without frequency");
		}
		
		return frequency;
	}
	
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	
	public boolean equals(Object other) {
		if( !(other instanceof POSTaggedWord) ) {
			return false;
		}else {
			return word.equals(((POSTaggedWord)other).word);
		}
	}
	
	public int hashCode() {
		return word.hashCode();
	}
	
	public static PartOfSpeech convertPennPos(String pennPos) {
		if( pennPos.equals("NN") || pennPos.equals("NNS") ) {
			return PartOfSpeech.NOUN;
		}else if( pennPos.startsWith("JJ") ) {
			return PartOfSpeech.ADJECTIVE;
		}else if( pennPos.startsWith("RB") ) {
			return PartOfSpeech.ADVERB;
		}else if( pennPos.startsWith("VB") ) {
			return PartOfSpeech.VERB;
		}else {
			return PartOfSpeech.OTHER;
		}
	}
	
	public static List<String> getWords(List<POSTaggedWord> tagged){
		ArrayList<String> words = new ArrayList<String>(tagged.size());
		
		for( POSTaggedWord w: tagged ) {
			words.add(w.getWord());
		}
		
		return words;
	}
}
