package edu.pomona.simplification;

import java.util.TimeZone;

public class Preferences {
	
	// --------- SYSTEM SPECIFIC PREFERENCES ------------------
	
	// MAC PREFERENCES
	// WHEN DEPLOYING ON MAC, URL IS:  http://localhost:8080/
	public static final String MODEL_DIR = "C:/WorkSpace/MedicalSimpliText/SimplificationTool/models/";
			//"/Users/drk04747/Documents/spring-workspaces/simplification_tool/models/";

	// database information
	public static final String DATABASE_BASE_URL = "jdbc:mysql://localhost:3306/";
	public static String UMLS_DATABASE_URL = Preferences.DATABASE_BASE_URL + "simplification?useSSL=false&allowPublicKeyRetrieval=True&serverTimezone=" + TimeZone.getDefault().getID();
	public static String GOOGLE_DATABASE_URL = Preferences.DATABASE_BASE_URL + "simplification?useSSL=false&allowPublicKeyRetrieval=True&serverTimezone=" + TimeZone.getDefault().getID();
	public static String ANALYTICS_DATABASE_URL = Preferences.DATABASE_BASE_URL + "analytics?useSSL=false&allowPublicKeyRetrieval=True&serverTimezone=" + TimeZone.getDefault().getID();
	public static final String USER = "root";
	public static final String PASSWORD = "root123";
	
	
		
	// SIMPLE.CS PREFERENCES
	// WHEN DEPLOYING ON simple, URL IS:  http://simple.cs.pomona.edu:3000/
	/*public static final String MODEL_DIR = "/home/dkauchak/text_simplification_tool/v2/models/";

	// database information
	public static final String DATABASE_BASE_URL = "jdbc:mysql://localhost:3306/";
	public static String UMLS_DATABASE_URL = Preferences.DATABASE_BASE_URL + "simplification?useSSL=false";
	public static String GOOGLE_DATABASE_URL = Preferences.DATABASE_BASE_URL + "simplification?useSSL=false";
	public static String ANALYTICS_DATABASE_URL = Preferences.DATABASE_BASE_URL + "simplification?useSSL=false";
	public static final String USER = "dkauchak";
	public static final String PASSWORD = "mysqlpassword";
	*/
	
	// --------- GENERAL PREFERENCES ------------------
	public static final String SYSTEM_VERSION = "1.0";
	
	public static final int NUM_PARSER_THREADS = 4;
	//public static final long FREQUENCY_CUTOFF = 15377914;
	public static final long MAX_MENU_OPTIONS = 5;
	
	// frequency thresholds for word cuttoffs for 500th most frequent, 1000th, ... 5000th
	//
	public static final int[] FREQUENCY_THRESHOLDS = {
			Integer.MAX_VALUE, // --0
			129569107,//500   --1
			74357980, //1,000 --2
			52130778, //1,500 --3
			39494357, //2,000 --4
			32174445, //2,500 --5
			26302573, //3,500 --6
			22431407, //3,500 --7
			19420705, //4,000 --8
			17224755, //4,500 --9
			15377914, //5,000 --10
			6394044   //10,000 --10
			};
	public static final int DEFAULT_FREQUENCY_CUTTOFF_INDEX = 10;
	
	// the number of characters to return for the frontend to use to find
	// the sentences that are flagged as difficult.
	public static final int BEGIN_CHARACTERS_TO_RETURN_FOR_SENTENCE = 15;

	public static final String WORDNET_DIR = MODEL_DIR + "wordnet";
	
	// negation files
	private static final String NEGATION_LEXICON_DIR = MODEL_DIR + "lexicons/";
	public static final String NEGATION_LEXICON_ACCEPT = NEGATION_LEXICON_DIR + "Accept.txt";
	public static final String NEGATION_LEXICON_REJECT = NEGATION_LEXICON_DIR + "Reject.txt";
	
	// morphology files
	public static String AFFIX_FILE = MODEL_DIR + "morphology/MedicalAffixes.txt";
	
	// grammar simplication rules
	public static String GRAMMAR_RULE_FILE = MODEL_DIR + "grammar_rules.txt";
	
}
