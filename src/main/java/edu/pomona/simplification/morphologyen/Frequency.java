package edu.pomona.simplification.morphologyen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Frequency {
	// Instance Variables
	private FileInputStream stream;
	private HashSet<String> vocab = new HashSet<String>();
	private BufferedReader inBr;
	private Pattern r = Pattern.compile("(^-..b-$|^\\W+$|^\\W*\\d*\\W*\\d*$|^...?$)");
	private String path = "/Users/pokea/Documents/Work/UofA"
			+ "/Current/MIS/AffixSimplification/Results/"
			+ "vocab_filtered15mil";
	// Constructor
	public Frequency(){
		try {
			stream = new FileInputStream(path);
			inBr = new BufferedReader(new InputStreamReader(stream));
			String rawLine;
			while ((rawLine = inBr.readLine()) != null) {

				String line = rawLine.replace("\n", "");
				vocab.add(line.toLowerCase());
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
	
	public Boolean membership (String token){
		if(!vocab.contains(token)) {
			Matcher m = r.matcher(token);
			if (!m.find()){
				return true;
			} else {
				return false;
			} 
		} else {
			return false;
		}
	}		
}
