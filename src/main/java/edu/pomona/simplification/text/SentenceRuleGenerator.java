package edu.pomona.simplification.text;

import java.util.ArrayList;
import java.util.HashSet;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.SimplificationResult;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.trees.Tree;

@Deprecated
public class SentenceRuleGenerator {
	private HashSet<String> endingPunctuationChars = new HashSet<String>();
	private HashSet<String> beginningPunctuationChars = new HashSet<String>();
	
	public SentenceRuleGenerator() {
		endingPunctuationChars.add(".");
		endingPunctuationChars.add(",");
		endingPunctuationChars.add(";");
		endingPunctuationChars.add(":");
		endingPunctuationChars.add("\'");
		endingPunctuationChars.add(")");
		endingPunctuationChars.add("]");
		endingPunctuationChars.add("}");
		
		beginningPunctuationChars.add("\"");
		beginningPunctuationChars.add(".");
		beginningPunctuationChars.add("(");
		beginningPunctuationChars.add("[");
		beginningPunctuationChars.add("{");

	}
	
	/*public SimplificationResult generateSimplificationResult(CoreSentence sentence, String changeInformation) {
		String sentenceText = sentence.text();
		
		// if it's too short, just return the whole sentence as the start, minus the last character.
		if( sentenceText.length() < Preferences.BEGIN_CHARACTERS_TO_RETURN_FOR_SENTENCE + Preferences.END_CHARACTERS_TO_RETURN_FOR_SENTENCE ) {
			return new SimplificationResult()
		}else {

			
			return new SimplificationResult(beginBuffer.toString(), end, changeInformation);
		}
	}*/

	/*public SimplificationResult generateSimplificationResult(Tree sentence, String changeInformation) {
		ArrayList<Word> words = sentence.yieldWords();

		if( words.size() < Preferences.BEGIN_WORDS_TO_RETURN_FOR_SENTENCE + 2 ) {
			System.err.println("Called GrammarRule.generateSimplificationResult on a sentence that is too short");
			return null;
		}else {
			StringBuffer beginBuffer = new StringBuffer();
			beginBuffer.append(renormalizeTreebankWord(words.get(0).word()));

			for( int i = 1; i < Preferences.BEGIN_WORDS_TO_RETURN_FOR_SENTENCE; i++ ) {
				String word = renormalizeTreebankWord(words.get(i).word());

				if( endingPunctuationChars.contains(word) ||
					word.startsWith("\'") ||
					beginningPunctuationChars.contains(beginBuffer.substring(beginBuffer.length()-1))) {
					// attach it without adding a space
					beginBuffer.append(word);
				}else {
					// add a space at the end
					beginBuffer.append(" " + word);
				}
			}

			int length = words.size();

			// assume it's a punctuation character
			String end = words.get(length-2).word() + words.get(length-1).word();

			return new SimplificationResult(beginBuffer.toString(), end, changeInformation);
		}
	}*/
	
	private String renormalizeTreebankWord(String word) {
		if( word.equals("-LRB-") ) {
			return "(";
		}else if( word.equals("-RRB-") ) {
			return ")";
		}else if( word.equals("-RRB-") ) {
			return ")";
		}else if( word.equals("-LSB-") ) {
			return "[";
		}else if( word.equals("-RSB-") ) {
			return "]";
		}else if( word.equals("-LCB-") ) {
			return "{";
		}else if( word.equals("-RCB-") ) {
			return "}";
		}else {
			return word;
		}
	}
}
