package edu.pomona.simplification.morphologyen;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class Wordnet {
	
	HashMap<String,String> summaries;
	IDictionary dict;
		
	public Wordnet() throws MalformedURLException{
		// Open the Wordnet API & Construct the URL to Wordnet Dictionary Director
		String wnhome = "/Users/pokea/Documents/Work/Programming/Java/WordNet-3.0";
		String path = wnhome + File.separator + "dict";
		URL url = new URL("file", null, path);
		dict = new Dictionary(url);
	}
		
	public HashMap<String,String> getSummaries(Document D) throws IOException {
		dict.open();
		summaries = new HashMap<String,String>();
		POS p;
		WordnetStemmer W = new WordnetStemmer(dict);
			
		for (Map.Entry<String, String> entry : D.getAccept().entrySet()) {
			String token = entry.getValue();
			String stanPOS = D.getPOS(entry.getKey());
			if (stanPOS.startsWith("V")){
				p = POS.VERB;
			} else if (stanPOS.startsWith("J")){
				p = POS.ADJECTIVE;					
			} else if (stanPOS.startsWith("RB")){
				p = POS.ADVERB;				
			} else {
				p = POS.NOUN;
			}
			summaries.put(entry.getKey(),lookupStems(W, token.toLowerCase(), p));
		}
		dict.close();
		return summaries;
	}
	
	private String wnLookup(String token,POS p){
		String synonym = "";
		IIndexWord idxWord = dict.getIndexWord(token, p);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		for (IWord w : word.getSynset().getWords()) {
			if (w.getLemma().equals(token)){
				continue;
			} else {
				synonym += w.getLemma() + ",";
			}
		}
		if (synonym.equals("")){
			synonym = null;
		}
		return synonym.trim() + "\t" + word.getSynset().getGloss();
	}
	
	private String wnPosShoot(String token,POS p){
		String summary;
		try{
			summary = wnLookup(token,p);
		}catch(NullPointerException ex){
			try {
				summary = wnLookup(token,POS.NOUN);
			}catch(NullPointerException ex1){
				try {
					summary = wnLookup(token,POS.VERB);
				}catch(NullPointerException ex2){
					try {
						summary = wnLookup(token,POS.ADJECTIVE);
					}catch(NullPointerException ex3){
						try {
							summary = wnLookup(token,POS.ADVERB);
						}catch(NullPointerException ex4){
							summary = null + "\t" + null;
						}
					}		
				}				
			}
		}
		return summary;
	}
		
	private String lookupStems(WordnetStemmer W, String token,POS p){
		String summary; 
		ArrayList<String> stems = new ArrayList<String>(W.findStems(token, p));
		if (!stems.isEmpty()) {
			if (stems.size() == 1) {
				summary = wnPosShoot(stems.get(0).toString(),p);
			} else { 
				summary = wnPosShoot(getLongest(stems),p);
			}
		} else {
			summary = null;
		}
		if (summary == null){
			summary = wnPosShoot(token,p);
		}
		return summary;
	}
		
	private String getLongest(ArrayList<String> aList){
		return Collections.max(aList, Comparator.comparing(s -> s.length()));
	}
	
	public String stemLookup(String stem) throws IOException{ 
		dict.open();
		WordnetStemmer W = new WordnetStemmer(dict);
		try {
			return lookupStems(W,stem,POS.NOUN).replaceAll("\t", ",");
		} catch(IllegalArgumentException ex){
			return null;
		}
	}
}