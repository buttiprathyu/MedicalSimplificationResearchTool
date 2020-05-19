package edu.pomona.simplification;

public class Stats {
	private final long numWords;
	private final double averageFrequency;
	private final int nouns;
	private final int verbs;
	//int adjectives = 0;
	//int pronouns = 0;
	//int prepositions = 0;
	
	public Stats(long numWords, double averageFrequency, int nouns, int verbs) {
		this.numWords = numWords;
		this.averageFrequency = averageFrequency;
		this.nouns = nouns;
		this.verbs = verbs;
	}

	public long getNumWords() {
		return numWords;
	}

	public double getAverageFrequency() {
		return averageFrequency;
	}
	
	public int getNouns() {
		return nouns;
	}
	
	public int getVerbs() {
		return verbs;
	}
}
