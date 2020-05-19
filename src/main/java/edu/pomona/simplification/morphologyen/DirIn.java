package edu.pomona.simplification.morphologyen;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class DirIn {
	
	private File folder = null;
	private File[] listOfFiles = null;
	private Integer kdx;
	private String data;
	
	public DirIn(String dirPath, TokenFilter tfr, Integer cdx) throws IOException {
		folder = new File(dirPath);
		listOfFiles = folder.listFiles();
		kdx = 0;
		data = "";
		
		// creates a StanfordCoreNLP object, with POS tagging, parsing
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				
				// Get File Directory
				String docName = listOfFiles[i].getName();
				
				// Create Doc Object
				Document D = new Document(
						dirPath + "/" + docName, 
						pipeline,
						kdx,
						cdx
						);
				// Filter out using CHVI
				tfr.cFilter(D);
				// Filter out using Wordnet
				tfr.wFilter(D);
				// Filter out using Affixes
				tfr.aFilter(D);
				data += D.writeOut();
			kdx += 1;
			} else if (listOfFiles[i].isDirectory()) {
				continue;
			}
		}
	}

	public void writeFile(String outPath) throws IOException{
		@SuppressWarnings("unused")
		WriteOut WO = new WriteOut(outPath, data);
	}
}
