package edu.pomona.simplification.substitution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class NegationLexicon {
	// Read in the Accept/Reject Lexicon (Derived and Underived Forms)
	public static HashMap<String, String> read(String path, Boolean accept){
		HashMap<String, String> lexicon = new HashMap<String,String>();
		String rawLine = null;
		InputStream stream = null;
		BufferedReader inBr;

		try{
			inBr = new BufferedReader(new FileReader(path));

			// Parse lines and add to Lexicon Hashmap
			while ((rawLine = inBr.readLine()) != null) {
				// Split the Line by , to get the Derived, Underived Pair
				String[] line = rawLine.split(",");

				if (accept){
					if (line.length == 2){
						// Add them to Hashmap <derived, underived>
						lexicon.put(line[0], line[1]);
					} else {
						// There are some typos, and this will tell us about them
						continue;
					}
				} else {
					// If it's the reject list, don't include the rest
					lexicon.put(line[0].substring(1), "null");		
				}
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

		return lexicon;
	}
}

