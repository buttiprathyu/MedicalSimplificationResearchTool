package edu.pomona.simplification.morphologyen;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class Sentence {
	private CoreMap coreSent;
	private HashMap<String,String> PosMap;
	
	public Sentence(CoreMap cm){
		coreSent = cm;
		PosMap = new HashMap<String,String>();
	}
	
	// Get Original Sentence String
	public String toString(){
		return coreSent.toString();
	}
	
	// Get Token List
	public ArrayList<String> toList(){
		ArrayList<String> tList = new ArrayList<String>();

		for (CoreLabel c : coreSent.get(TokensAnnotation.class)){
			String token = c.get(TextAnnotation.class);
			tList.add(token);
			String pos = c.get(PartOfSpeechAnnotation.class);
			PosMap.put(token, pos);
		}
		return tList;
	}
	
	public String getPOS(String token){
		return PosMap.get(token);
	}
}
