package edu.pomona.simplification.morphologysp;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class Sentence {
	private String original;
	
	
	public Sentence(CoreMap coreSent, Terms Ts, Indexer I){
		original = coreSent.toString();
		I.add(1);
		I.reset(2);
		for (CoreLabel c : coreSent.get(TokensAnnotation.class)){
			I.add(2);
			String token = c.get(TextAnnotation.class);
			String pos = c.get(PartOfSpeechAnnotation.class);
			String idx = I.getAll();
			Ts.put(idx, token.toLowerCase(),"1");
			Ts.put(idx, pos, "2");
		}
	}
}
