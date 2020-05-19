package edu.pomona.simplification.text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.print.DocFlavor.STRING;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.substitution.SubstitutionGenerator;

public class Nominals implements SubstitutionGenerator{
	// lookup statements
	private static final String NOUN_LOOKUP = "SELECT DISTINCT OtherWord FROM LRNOM WHERE Noun = ? AND OtherPOS='verb'";
	private static final String LANGUAGE = "Use verb form: ";

	@Override
	public List<String> getSubstitutions(POSTaggedWord word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		
		try {
			Connection conn = DriverManager.getConnection(Preferences.UMLS_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement statement = conn.prepareStatement(NOUN_LOOKUP);

			String wordString = word.getWord().toLowerCase();
			
			String result = verbLookup(statement, wordString);
						
			if( result != null ) {
				synonyms.add(result);
			}
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return synonyms;
	}

	public List<List<String>> getSubstitutions(List<POSTaggedWord> words){
		ArrayList<List<String>> results = new ArrayList<List<String>>();
		HashMap<String, List<String>> cache = new HashMap<String, List<String>>();

		try {
			Connection conn = DriverManager.getConnection(Preferences.UMLS_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement statement = conn.prepareStatement(NOUN_LOOKUP);

			for( POSTaggedWord word: words ) {
				String wordString = word.getWord().toLowerCase();

				if( cache.containsKey(wordString) ) {
					// we've already looked it up in the DB, so just grab the value from the cache
					results.add(cache.get(wordString));
				}else {
					String result = verbLookup(statement, wordString);
					
					if( result != null ) {
						ArrayList<String> temp = new ArrayList<String>(1);
						temp.add(result);
						results.add(temp);
						cache.put(wordString, temp);
					}
				}
			}

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}
	
	private String verbLookup(PreparedStatement statement, String word){
		String verb = null;
		
		try {
			// get the CUIs
			statement.setString(1, word);
			ResultSet result = statement.executeQuery();
			
			if( result.next() ) {
				verb = LANGUAGE + result.getString(1).toLowerCase();				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return verb;
	}
	
	private static void test1() {
		Nominals umls = new Nominals();
		POSTaggedWord w = new POSTaggedWord("abasement", PartOfSpeech.NOUN);
		System.out.println(umls.getSubstitutions(w));
	}
	
	public static void main(String[] args) {
		test1();
	}

	@Override
	public String sourceName() {
		return "nominal";
	}
}
