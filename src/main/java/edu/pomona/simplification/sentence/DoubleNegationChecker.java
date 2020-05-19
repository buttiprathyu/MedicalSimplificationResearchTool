package edu.pomona.simplification.sentence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.pomona.simplification.substitution.MorphNegation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class DoubleNegationChecker{	
	private static int NEGATION_WINDOW = 6;
	
	// short accept list for all sentence negations
	private static final HashSet<String> negArray = new HashSet<String>(Arrays.asList("no", "neither", "stop","stops","none","not"));
	
	private MorphNegation morphNegation;
	
	// variables for storing the words that are part of the double negation for generating information
	private String firstNegation;
	private String secondNegation;
	
	public DoubleNegationChecker(MorphNegation morphNegation) {
		this.morphNegation = morphNegation;
	}

	public boolean hasDoubleNegation(CoreSentence sentence) {
		HashSet<String> sententialNegations = getSententialNegations(sentence.dependencyParse());
		List<CoreLabel> tokens = sentence.tokens();

		int prevNegation = -(2*NEGATION_WINDOW);
		boolean prevNegationIsMorph = true;
		
		for( int i = 0; i < tokens.size(); i++ ) {
			String token = tokens.get(i).originalText().toLowerCase();
			
			if( sententialNegations.contains(token) ) {				
				// if there's a previous negation within the window then we've found a double negation
				if( i - prevNegation <= NEGATION_WINDOW ) {
					firstNegation = tokens.get(prevNegation).originalText();
					secondNegation = tokens.get(i).originalText();
					return true;
				}
				
				prevNegation = i;
				prevNegationIsMorph = false;
			}else if( morphNegation.isMorphNegation(token) ) {
				// check if there's a previous negation within the window AND make sure
				// it's sentential
				if( i - prevNegation <= NEGATION_WINDOW && !prevNegationIsMorph ) {
					firstNegation = tokens.get(prevNegation).originalText();
					secondNegation = tokens.get(i).originalText();
					return true;
				}
				
				prevNegation = i;
				prevNegationIsMorph = true;
			}
		}
		
		// if we made it through and didn't find one, then the isn't a double negation
		return false;
	}
	
	private static HashSet<String> getSententialNegations(SemanticGraph graph){
		HashSet<String> sententialNegations = new HashSet<String>();
		
		List<SemanticGraphEdge> edges = graph.edgeListSorted();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			String token = edge.getDependent().word().toLowerCase();

			if(relation.getShortName() == "neg" || negArray.contains(token.toLowerCase())){
				sententialNegations.add(token);
			}
		}
		
		return sententialNegations;
	}
	
	// NOTE: Should only be called after a call to hasDoubleNegation
	public String getChangeInformation() {
		return "<p class=\"instructions\">Avoid double negatives in sentences: " +
				"<negationHighlight>" + firstNegation + "</negationHighlight>" +
				" and <negationHighlight>" + secondNegation + "</negationHighlight></p>";
	}
}
