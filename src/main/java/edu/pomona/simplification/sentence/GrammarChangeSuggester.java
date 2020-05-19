package edu.pomona.simplification.sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.SimplificationResult;
import edu.stanford.nlp.pipeline.CoreSentence;
//Stanford core NLP version 3.7.0 was used -- to install it simply download the historic version (https://stanfordnlp.github.io/CoreNLP/history.html), and then add the .jar files within the reference libraries
import edu.stanford.nlp.trees.Tree;

public class GrammarChangeSuggester{
	private List<GrammarRule> rules = new ArrayList<GrammarRule>();

	public GrammarChangeSuggester(){
		try {
			// read in the rule file
			BufferedReader br = new BufferedReader(new FileReader(Preferences.GRAMMAR_RULE_FILE));  
			String line;
			
			while ((line = br.readLine()) != null) {
				rules.add(new GrammarRule(line));				
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<GrammarRule> checkSentence(Tree sentence){
		List<GrammarRule> matchedRules = new ArrayList<GrammarRule>();
		
		//For each rule, run an if contains.
		for(GrammarRule rule: rules){
			if(rule.matchesSentence(sentence)){
				matchedRules.add(rule);
			}
		}
		
		return matchedRules;
	}
}
