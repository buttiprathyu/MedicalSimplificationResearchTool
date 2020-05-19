package edu.pomona.simplification.morphologyen;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class TokenFilter {
	private Wordnet wordnet;
	private CHVI consumerHealth;
	private Affixes affixes;
	
	public TokenFilter() throws MalformedURLException {
		wordnet = new Wordnet();
		consumerHealth = new CHVI("/Users/pokea/Documents/Work/"
				+ "Programming/Java/UMLS/"
				+ "CHV_concepts_terms_flatfile_20110204.tsv");
		affixes = new Affixes("/Users/pokea/Documents"
				+ "/Work/UofA/Current/MIS/"
				+ "AffixSimplification/Pilot/Code"
				+ "/MorphComplexity/MedicalAffixes");
	}
	
	public void cFilter(Document D) {
		D.updateSummaries(consumerHealth.getSummaries(D));
	}
	
	public void wFilter(Document D) throws IOException{
		D.updateSummaries(wordnet.getSummaries(D));
	}
	
	public void aFilter(Document D) throws IOException{
		D.updateSummaries(affixes.getSummaries(D.getAccept(),wordnet));
	}
}
