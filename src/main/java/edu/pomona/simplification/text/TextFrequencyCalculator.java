package edu.pomona.simplification.text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.pomona.simplification.Preferences;

public class TextFrequencyCalculator {
	public static List<Long> wordFreqLookup(List<String> words){
		return simpleWordFreqLookup(words, false);
	}

	public static List<Long> wordFreqLookupHandleMultiword(List<String> words){
		return simpleWordFreqLookup(words, true);
	}

	public static List<Long> taggedWordFreqLookup(List<POSTaggedWord> taggedWords){
		return simpleWordFreqLookup(POSTaggedWord.getWords(taggedWords), false);
	}

	public static void addFrequencyInformation(List<POSTaggedWord> taggedWords){
		List<Long> freqs = simpleWordFreqLookup(POSTaggedWord.getWords(taggedWords), false);

		for( int i = 0; i < taggedWords.size(); i++ ) {
			taggedWords.get(i).setFrequency(freqs.get(i));
		}
	}

	/**
	 * 
	 * @param words
	 * @param handleMultiword If true, frequency of multiword phrase is the minimum of all words. If false, just 
	 * do normal lookup of multiword phrase if it were a single word.
	 * @return
	 */

	private static List<Long> simpleWordFreqLookup(List<String> words, boolean handleMultiword){
		ArrayList<Long> freqs = new ArrayList<Long>();
		HashMap<String, Long> cache = new HashMap<String, Long>();

		try {
			Connection conn = DriverManager.getConnection(Preferences.GOOGLE_DATABASE_URL, Preferences.USER, 
					Preferences.PASSWORD);
			PreparedStatement statement = conn.prepareStatement("SELECT sum(termcount) FROM 1Gram WHERE term = ?");

			for( String word: words ) {
				if( cache.containsKey(word.toLowerCase()) ) {
					// we've already looked it up in the DB, so just grab the value from the cache
					freqs.add(cache.get(word.toLowerCase()));
				}else {
					String[] splitWords = word.split("\\s+");
					long freq;

					if( !handleMultiword || splitWords.length == 1) {
						// could turn this into a method, but complicates error handling, so we'll duplicate below
						statement.setString(1, word);
						ResultSet result = statement.executeQuery();
						result.first();
						freq = result.getLong(1);
					}else {
						// multiword phrase and we want to handle it
						// iterate through all the words and take the minimum frequency
						long minFreq = Long.MAX_VALUE;

						for( String subWord: splitWords ) {
							statement.setString(1, subWord);
							ResultSet result = statement.executeQuery();
							result.first();
							long tempFreq = result.getLong(1);
							
							if( tempFreq < minFreq ) {
								minFreq = tempFreq;
							}
						}
						
						freq = minFreq;
					}

					freqs.add(freq);
					cache.put(word.toLowerCase(), freq);
				}
			}	
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return freqs;
	}

	public static void main(String[] args) {
		ArrayList<String> testWords = new ArrayList<String>();
		String test = "DNA nanotechnology is the design and manufacture of artificial nucleic acid structures for technological uses. In this field, nucleic acids are used as non-biological engineering materials for nanotechnology rather than as the carriers of genetic information in living cells. Researchers in the field have created static structures such as two- and three-dimensional crystal lattices, nanotubes, polyhedra, and arbitrary shapes, and functional devices such as molecular machines and DNA computers. The field is beginning to be used as a tool to solve basic science problems in structural biology and biophysics, including applications in X-ray crystallography and nuclear magnetic resonance spectroscopy of proteins to determine structures. Potential applications in molecular scale electronics and nanomedicine are also being investigated.\n" + 
				"The conceptual foundation for DNA nanotechnology was first laid out by Nadrian Seeman in the early 1980s, and the field began to attract widespread interest in the mid-2000s. This use of nucleic acids is enabled by their strict base pairing rules, which cause only portions of strands with complementary base sequences to bind together to form strong, rigid double helix structures. This allows for the rational design of base sequences that will selectively assemble to form complex target structures with precisely controlled nanoscale features. Several assembly methods are used to make these structures, including tile-based structures that assemble from smaller structures, folding structures using the DNA origami method, and dynamically reconfigurable structures using strand displacement methods. The field's name specifically references DNA, but the same principles have been used with other types of nucleic acids as well, leading to the occasional use of the alternative name nucleic acid nanotechnology.";
		test = test + test + test + test + test + test + test;
		test = test + test + test + test + test;
		testWords.addAll(Arrays.asList(test.split(" ")));

		System.out.println(wordFreqLookup(testWords));
	}
}