package edu.pomona.simplification.morphologyen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CHVI {
	
	HashMap<String,String> summaries;
	HashMap<String,String> chviMap;
	private FileInputStream stream;
	private BufferedReader inBr;
	
	public CHVI(String filePath){
		
		// Open the ConsumerHealthVocab
		String rawLine;
		chviMap = new HashMap<String,String>();
		
		try{
			stream = new FileInputStream(filePath);
			inBr = new BufferedReader(new InputStreamReader(stream));

			// Parse lines and add to Lexicon Hashmap
			while ((rawLine = inBr.readLine()) != null) {
				
				// Split the Line by tabs
				String[] line = rawLine.split("\t");
				chviMap.put(line[1],line[2]);

			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try{
				if (stream != null){
					stream.close();
				}	
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	// Get the Accept map and find summaries for all the words you can in UMLS
	public HashMap<String,String> getSummaries(Document D){
		String summary = null;
		summaries = new HashMap<String,String>();
		for (Map.Entry<String, String> Aentry : D.getAccept().entrySet()) {
			String token = Aentry.getValue().toLowerCase();
			for (Map.Entry<String, String> Centry : chviMap.entrySet()){
				if (Centry.getKey().contains(token)){
					summary = Centry.getValue();
				}
			}
			summaries.put(Aentry.getKey(), summary);
		}
		return summaries;
	}
}
