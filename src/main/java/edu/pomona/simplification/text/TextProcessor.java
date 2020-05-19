package edu.pomona.simplification.text;

import java.util.ArrayList;
import java.util.HashSet;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

public class TextProcessor {		
	public static ArrayList<POSTaggedWord> getCountableWords(CoreDocument document){
		ArrayList<POSTaggedWord> words = new ArrayList<POSTaggedWord>();

		for(CoreLabel token: document.tokens()) {
			PartOfSpeech pos = POSTaggedWord.convertPennPos(token.tag());

			if( pos != PartOfSpeech.OTHER ) {
				words.add(new POSTaggedWord(token.originalText().toLowerCase(), pos));
			}
		}
		
		return words;
	}

	public static ArrayList<POSTaggedWord> getSentenceCountableWords(CoreSentence sentence){
		
		ArrayList<POSTaggedWord> sentenceWords = new ArrayList<POSTaggedWord>();
		for(CoreLabel token: sentence.tokens()) {
			PartOfSpeech pos = POSTaggedWord.convertPennPos(token.tag());
			if( pos != PartOfSpeech.OTHER ) {
				sentenceWords.add(new POSTaggedWord(token.originalText().toLowerCase(), pos));
			}
		}
		return sentenceWords;
	}
	
	public static ArrayList<POSTaggedWord> dedup(ArrayList<POSTaggedWord> words){
		return new ArrayList<POSTaggedWord>(new HashSet<POSTaggedWord>(words));
	}

	private static boolean isPosTagOfInterest(String posTag) {
		return posTag.equals("NN") || posTag.equals("NNS") || // nouns
				posTag.startsWith("JJ") || // adjectives
				posTag.startsWith("RB") || // adverbs
				posTag.startsWith("VB");   // verbs
	}
}