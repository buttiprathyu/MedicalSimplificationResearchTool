package edu.pomona.simplification.morphologyen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Document {
	private HashMap<String,String> accept = new HashMap<String,String>();
	private HashMap<String,String> POS = new HashMap<String,String>();
	private HashMap<String,String> Summaries = new HashMap<String,String>();
	private HashMap<String,String> Reject = new HashMap<String,String>();
	private Frequency F = new Frequency();

	public Document(String fName, StanfordCoreNLP pipeline, int idx, int cdx){
		BufferedReader inBr;
		FileInputStream inStream = null;
		String rawLine;
		int docdx = idx;
		int sentdx = 0;
		int tokdx;
		String index;
		String ltoken;
		
		try{
			// read in file
			inStream = new FileInputStream(fName);
			inBr = new BufferedReader(new InputStreamReader(inStream));
			
			while ((rawLine = inBr.readLine()) != null) {
				if ( !rawLine.startsWith("<")){
					// Parse out the sentences
					Annotation antdLine = new Annotation(rawLine);
					// run the selected Stanford Annotators on the text
					pipeline.annotate(antdLine);					
					List<CoreMap> coreSents = antdLine
							.get(CoreAnnotations.SentencesAnnotation.class);
					// Declare a Sentence Object for each Sentence
					for(CoreMap coreSent : coreSents) {
						Sentence S = new Sentence(coreSent);
						ArrayList<String> tlist = S.toList();
						tokdx = 0;
						for(String token : tlist){
							index = strInt(cdx) + "\t";
							index += strInt(docdx) + "\t";
							index += strInt(sentdx) + "\t";
							index += strInt(tokdx);
							tokdx += 1;
							ltoken = token.toLowerCase();
							if (F.membership(ltoken)){
								accept.put(index, token);
								POS.put(index, S.getPOS(token));
							} else{
								Reject.put(index, token);
							}
						}
						sentdx += 1;
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
		Summaries.putAll(accept);
	}

	public String writeOut(){
		String docResults = "";
		Map<String, String> treeMap = new TreeMap<String, String>(Summaries);
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			docResults += entry.getKey() + "\t" + entry.getValue() + "\n";
		}
		return docResults;
	}
	
	public HashMap<String,String> getAccept(){
		return accept;
	}
	
	public String getPOS(String token){
		return POS.get(token);
	}
	
	public void updateSummaries(HashMap<String,String> newMap){
		for (Map.Entry<String, String> entry : Summaries.entrySet()) {
			Summaries.replace(entry.getKey(), entry.getValue() + "\t" + newMap.get(entry.getKey()));
		}
	}
	
	private String strInt(Integer k){
		if (k < 10){
			return "0" + Integer.toString(k);
		} else {
			return Integer.toString(k);
		}
	}
}

