package edu.pomona.simplification.morphologysp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Doc{
	public Doc(String nameF, 
			StanfordCoreNLP pipeline, 
			Terms Ts,
			Indexer I){
		BufferedReader inBr;
		FileInputStream inStream = null;
		String rawLine;
		try{
			// read in file
			inStream = new FileInputStream(nameF);
			inBr = new BufferedReader(new InputStreamReader(inStream));
			while ((rawLine = inBr.readLine()) != null) {
				if (!rawLine.startsWith("<")){
					// Parse sentences & run Stanford Annotators
					Annotation antdLine = new Annotation(rawLine);
					pipeline.annotate(antdLine);
					List<CoreMap> coreSents = antdLine
						.get(CoreAnnotations.SentencesAnnotation.class);
					// Declare a Sentence Object for each Sentence
					I.reset(1);
					for(CoreMap coreSent : coreSents) {
						Sentence S = new Sentence(coreSent, Ts, I);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();				
		} finally {
			try {
				if (inStream != null){
					inStream.close();
				}
		    } catch (IOException ex) {  
				ex.printStackTrace();
			}
		}
	}
}
