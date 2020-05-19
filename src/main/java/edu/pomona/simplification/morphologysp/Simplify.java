package edu.pomona.simplification.morphologysp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Simplify {
	
	public static void main (String[] args) throws IOException {
		
		// creates a StanfordCoreNLP object, with POS tagging, parsing
		Properties props = new Properties();
		//props.load(IOUtils.readerFromString("StanfordCoreNLP-spanish.properties"));
		props.setProperty("annotators", "tokenize, ssplit, pos");
		//StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		String dirName = "PM";

		/*FromDir wkDir = new FromDir("/Users/pokea/Documents/"
					+ "Work/UofA/Current/MIS/AffixSimplification/"
					+ "Code/subwordSpanish/Resources/"
					+ dirName,
					new StanfordCoreNLP(props),
					new Indexer());*/
		
		FromDir wkDir = new FromDir("/Users/drk04747/temp/morphtest",
				new StanfordCoreNLP(props),
				new Indexer());
		
		
		
		wkDir.filter();
		wkDir.writeSummaries("/Users/drk04747/temp/morphtest/out.txt");
		
		/*wkDir.writeSummaries("/Users/pokea/Documents/"
					+ "Work/UofA/Current/MIS/AffixSimplification/"
					+ "Code/subwordSpanish/debajoPalabra/Output"
					+ dirName + ".txt");*/
		
		 System.out.println("Done with " + dirName);
	}
}
