package edu.pomona.simplification.text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.pomona.simplification.Preferences;

public class Wordnet {
	private Dictionary dictionary;
	private WordnetStemmer wnStemmer;
	private POS[] orderedPOS = {POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB};

	public Wordnet() {
		try {
			dictionary = new Dictionary(new URL("file", null, Preferences.WORDNET_DIR));
			dictionary.open();
			wnStemmer = new WordnetStemmer(dictionary);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Problems opening WordNet!");
			e.printStackTrace();
		}
	}

	public List<String> getSynonyms(POSTaggedWord word) {
		POS pos = getJWIPos(word);

		if( pos != null ) {
			return synonymLookup(word.getWord(), pos);
		}else {
			return new ArrayList<String>();
		}
	}
	
	/**
	 * @param word
	 * @return Returns the first synonym found based on the ordered set of POS
	 */
	public String getFirstSynonym(String word){
		String synonym = null;
		
		for( int i = 0; i < orderedPOS.length && synonym == null; i++ ) {
			List<String> synonyms = synonymLookup(word, orderedPOS[i]);
			
			if( synonyms.size() > 0 ) {
				synonym = synonyms.get(0);
			}
		}

		return synonym;
	}
	
	private List<String> synonymLookup(String word, POS pos) {
		List<String> stems = null;
		
		try {
			stems = wnStemmer.findStems(word, pos);
		}catch(java.lang.IllegalArgumentException e){
			System.out.println(word);
			System.out.println(pos);
			System.exit(0);
		}
		
		
		HashSet<String> synonymSet = new HashSet<String>();
		List<String> synonyms = new ArrayList<String>();
		
		for( String stem: stems ) {
			IIndexWord indexWord = dictionary.getIndexWord(stem, pos);

			if( indexWord != null ) {
				// for each of the senses, get all of the synonyms
				for( IWordID wordID: indexWord.getWordIDs() ) {
					IWord senseWord = dictionary.getWord(wordID);

					if( senseWord != null ) {
						// add all of the synonyms
						for( IWord syn: senseWord.getSynset().getWords() ) {
							String synonym = (syn.getLemma().replaceAll("_", " ")).toLowerCase();
							
							if( !synonym.equals(word) && !synonymSet.contains(synonym) ) {
								synonyms.add(synonym);
								synonymSet.add(synonym);
							}
						}
					}
				}
			}
		}
				
		return synonyms;
	}

	private POS getJWIPos(POSTaggedWord word) {
		if( word.getPos() == PartOfSpeech.NOUN ) {
			return POS.NOUN;
		}else if( word.getPos() == PartOfSpeech.VERB ) {
			return POS.VERB;
		}else if( word.getPos() == PartOfSpeech.ADJECTIVE ) {
			return POS.ADJECTIVE;
		}else if( word.getPos() == PartOfSpeech.ADVERB ) {
			return POS.ADVERB;
		}else {
			return null;
		}
	}

	private String definitionLookup(String token, POS pos) {
		List<String> stems = wnStemmer.findStems(token, pos);
		
		for( String stem: stems) {
			IIndexWord indexWord = dictionary.getIndexWord(stem, pos);

			if( indexWord != null ) {
				// for each of the senses, lookup the definitions
				// return with the definition for the first sense that we find
				for( IWordID wordID: indexWord.getWordIDs() ) {
					IWord senseWord = dictionary.getWord(wordID);

					if( senseWord != null ) {
						return senseWord.getSynset().getGloss();
					}
				}
			}
		}

		// if we either can't find the word or we can't find any sense,
		// return null
		return null;
	}

	public String definitionLookup(String token) {
		String summary = null;

		for( int i = 0; i < orderedPOS.length && summary == null; i++ ) {
			summary = definitionLookup(token, orderedPOS[i]);			
		}

		return summary;
	}
	
	/**
	 * Return first synonym found for this word searching by POS in
	 * order of orderedPOS
	 * 
	 * @param token
	 * @return
	 */
	public String synonymLookup(String token) {
		String summary = null;

		for( int i = 0; i < orderedPOS.length && summary == null; i++ ) {
			summary = definitionLookup(token, orderedPOS[i]);			
		}

		return summary;
	}

	// ---------------------------
	// COPIED FROM NICK'S CODE

	/*private String wnLookup(String token,POS p){
		String synonym = "";
		IIndexWord idxWord = dictionary.getIndexWord(token, p);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dictionary.getWord(wordID);
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
							//summary = null + "\t" + null;
							summary = null;
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

	public String stemLookup(String stem){
		try {
			return lookupStems(wnStemmer,stem,POS.NOUN).replaceAll("\t", ",");
		} catch(IllegalArgumentException ex){
			return null;
		}
	}*/
}
