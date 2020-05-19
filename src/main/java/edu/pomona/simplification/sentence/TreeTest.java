package edu.pomona.simplification.sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;

public class TreeTest {
	public static void main(String args[]) throws IOException {
		String file = "/Users/drk04747/Documents/spring-workspaces/simplification_tool/src/main/java/edu/pomona/simplification/sentence/tregex.txt"; //Hardcoded the file name for now.
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,parse"); //Do till the parse.
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		TregexPattern pattern;
		Annotation ann;

		BufferedReader in = new BufferedReader(new FileReader(file));

		String line;
		
		while( (line = in.readLine()) != null ) {
			String[] parts = line.split("\t");
			
			String rule = parts[0];
			String sent = parts[1];
			String tregex = GrammarRule.ruleToTregex(rule);
			//String tregex = "(NP < (/^DT/ . (/^JJ/ . (/^NN/ . (/^NN/ . (/^NN/ . /^NN/))))))";
			System.out.println(tregex);
		
			ann = new Annotation(sent); //Text to be annotated - Third column, read from the row content
			pipeline.annotate(ann);
	    	//pattern = TregexPattern.compile(row.getCell(1).getStringCellValue()); //Tregex pattern to use - Second column, read from the row content
			pattern = TregexPattern.compile(tregex);
					
	    	for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
	    		System.out.println(sentence.get(TreeCoreAnnotations.TreeAnnotation.class));
		    	TregexMatcher matcher = pattern.matcher(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)); //Get the tree annotation and match with the pattern as set above.
			    while (matcher.find()) {
			    	Tree match = matcher.getMatch();
			    	System.out.println(matcher.getMatch()); //Printing stuff to debug
			    	for (Tree t : match.getLeaves()) { //Add all the matched leaves to a list
			    		System.out.println(t.toString());
			    	}
			    	break; //Else I will get the same result for the number of nodes in the pattern.
			    	//Can add to a Set here to hold multiple matches. Right now, used a break for the sample text.
			    }
		    }
	    }
	}
}
