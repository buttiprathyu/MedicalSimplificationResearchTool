package edu.pomona.simplification.text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.substitution.SubstitutionGenerator;

public class UMLS_lexicalchain implements SubstitutionGenerator{
	// lookup statements
	private static String CUI_LOOKUP = "SELECT DISTINCT CUI FROM MRCONSO WHERE STR = ?";
	private static String SYN_LOOKUP = "SELECT DISTINCT STR FROM MRCONSO WHERE CUI = ?";
	private static String AUI_LOOKUP = "SELECT DISTINCT AUI FROM MRCONSO WHERE CUI = ?";
	private static String SEM_LOOKUP = "SELECT DISTINCT STR FROM MRCONSO WHERE AUI = ?";

	@Override
	public List<String> getSubstitutions(POSTaggedWord word) {
		return getSynonyms(word.getWord());
	}

	public List<List<String>> getSubstitutions(List<POSTaggedWord> posWords){
		List<String> words = new ArrayList<String>(posWords.size());

		for( POSTaggedWord w: posWords ) {
			words.add(w.getWord());
		}

		return getSynonyms(words);
	}

	public List<String> getSynonyms(String word){
		ArrayList<String> synonyms = new ArrayList<String>();

		try {
			Connection conn = DriverManager.getConnection(Preferences.UMLS_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement cuiStatement = conn.prepareStatement(CUI_LOOKUP);
			PreparedStatement synStatement = conn.prepareStatement(SYN_LOOKUP);

			synonyms = synonymLookup(cuiStatement, synStatement, word.toLowerCase());
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return synonyms;
	}
	public List<String> getSemanticAUIs(String word){
		ArrayList<String> semantic_AUIs = new ArrayList<String>();

		try {
			Connection conn = DriverManager.getConnection(Preferences.UMLS_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement cuiStatement = conn.prepareStatement(CUI_LOOKUP);
			PreparedStatement auiStatement = conn.prepareStatement(AUI_LOOKUP);
			PreparedStatement semStatement = conn.prepareStatement(SEM_LOOKUP);

			semantic_AUIs = semanticAUILookup(cuiStatement, auiStatement, semStatement, word.toLowerCase());
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return semantic_AUIs;
	}

	public List<List<String>> getSynonyms(List<String> words){
		ArrayList<List<String>> results = new ArrayList<List<String>>();
		HashMap<String, List<String>> cache = new HashMap<String, List<String>>();

		try {
			Connection conn = DriverManager.getConnection(Preferences.UMLS_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement cuiStatement = conn.prepareStatement(CUI_LOOKUP);
			PreparedStatement synStatement = conn.prepareStatement(SYN_LOOKUP);

			for( String word: words ) {
				String wordString = word.toLowerCase();

				if( cache.containsKey(wordString) ) {
					// we've already looked it up in the DB, so just grab the value from the cache
					results.add(cache.get(wordString));
				}else {
					ArrayList<String> synonyms = synonymLookup(cuiStatement, synStatement, wordString);
					results.add(synonyms);
					cache.put(wordString, synonyms);
				}
			}

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;

	}


	private ArrayList<String> synonymLookup(PreparedStatement cuiStatement, PreparedStatement synStatement,  String word){
		ArrayList<String> synonyms = new ArrayList<String>();

		try {
			// get the CUIs
			cuiStatement.setString(1, word);
			ResultSet result = cuiStatement.executeQuery();

			// lookup all variants of the CUI
			while( result.next() ) {
				String cui = result.getString(1);

				synStatement.setString(1, result.getString(1));
				ResultSet synResult = synStatement.executeQuery();

				while( synResult.next() ) {
					synonyms.add(synResult.getString(1).toLowerCase());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return synonyms;
	}
	
	private ArrayList<String> semanticAUILookup(PreparedStatement cuiStatement, PreparedStatement auiStatement, PreparedStatement semStatement,  String word){
		ArrayList<String> AUIs = new ArrayList<String>();

		try {
			// get the CUIs
			cuiStatement.setString(1, word);
			ResultSet cuiresult = cuiStatement.executeQuery();
			
			// lookup all variants of the CUI
				while( cuiresult.next() ) {
					String cui = cuiresult.getString(1);
	
					// get the AUIs
					auiStatement.setString(1, cui);
					ResultSet result = auiStatement.executeQuery();
		
					// lookup all variants of the CUI
					while( result.next() ) {
						AUIs.add(result.getString(1));
					}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return AUIs;
	}

	private static void test1() {
		UMLS_lexicalchain umls = new UMLS_lexicalchain();
		POSTaggedWord w = new POSTaggedWord("dermatitis", PartOfSpeech.NOUN);
		System.out.println(umls.getSubstitutions(w));
		System.out.println(umls.getSemanticAUIs("cancer"));
		System.out.println(umls.getSemanticAUIs("diseases"));
	}

	public static void main(String[] args) {
		test1();
	}

	@Override
	public String sourceName() {
		return "umls";
	}
}
