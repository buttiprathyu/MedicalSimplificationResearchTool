package edu.pomona.simplification.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.morphology.Affix;
import edu.pomona.simplification.morphology.AffixMatch;
import edu.pomona.simplification.morphology.AffixMatcher;
import edu.pomona.simplification.text.Wordnet;

public class AffixTestGenerator {
	private static final int MAX_RESULTS_PER_AFFIX = 10;

	private AffixMatcher matcher = new AffixMatcher(Preferences.AFFIX_FILE, new Wordnet());
	private HashMap<AffixWrapper, ArrayList<Result>> results = new HashMap<AffixWrapper, ArrayList<Result>>();
	private HashSet<String> searched = new HashSet<String>();

	// assume that word file is a file with words, one
	// per line that has already been frequency filtered
	public void populateWordMap(String wordFile) {
		BufferedReader in;

		try {
			in = new BufferedReader(new FileReader(wordFile));

			String word;
			int count = 0;
			while((word = in.readLine()) != null ) {
				word = word.toLowerCase();

				if( !searched.contains(word) ) {
					searched.add(word);

					AffixMatch match = matcher.getSummaryDebug(word);

					if( match.hasMatch() ) {				
						Result result = new Result(word, match.getSummary());

						if( match.hasPrefixMatch() ) {
							addResult(new AffixWrapper(match.getPrefix()), result);
						}

						if( match.hasSuffixMatch() ) {
							addResult(new AffixWrapper(match.getSuffix()), result);
						}
					}

				}

				count++;

				if( count % 10000 == 0 ) {
					System.out.println(count);
				}
			}

			in.close();
		}catch(IOException e) {
			System.err.println(e);
			throw new RuntimeException();
		}
	}

	public void printWordMap(String outputFile) {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(outputFile));
			ArrayList<AffixWrapper> sortedKeys = new ArrayList<AffixWrapper>(results.keySet());

			Collections.sort(sortedKeys);

			for(AffixWrapper wrapper: sortedKeys) {

				for( Result r: results.get(wrapper) ) {
					out.println(wrapper.getAffix().toString() + "\t" + r.getWord() + "\t" + r.getSummary());
				}
			}

			out.close();
		}catch(IOException e) {
			System.err.println(e);
			throw new RuntimeException();
		}
	}

	private void addResult(AffixWrapper wrapper, Result result) {
		if( results.containsKey(wrapper) ) {
			ArrayList<Result> temp = results.get(wrapper);

			if( temp.size() < MAX_RESULTS_PER_AFFIX * 2 ) {
				temp.add(result);
			}
		}else {
			ArrayList<Result> temp = new ArrayList<Result>();
			temp.add(result);
			results.put(wrapper, temp);
		}
	}

	private class AffixWrapper implements Comparable<AffixWrapper>{
		private Affix affix;

		public AffixWrapper(Affix affix) {
			this.affix = affix;
		}

		public Affix getAffix() {
			return affix;
		}

		public boolean equals(Object o) {
			if( o instanceof AffixWrapper ) {
				return affix.getAffixString().equals(((AffixWrapper)o).affix.getAffixString());
			}else {
				return false;
			}
		}

		public int hashCode() {
			return affix.getAffixString().hashCode();
		}

		@Override
		public int compareTo(AffixWrapper o) {
			return affix.getAffixString().compareTo(o.affix.getAffixString());
		}
	}

	private class Result{
		private String word;
		private String summary;

		public Result(String word, String summary) {
			this.word = word;
			this.summary = summary;
		}

		public String getWord() {
			return word;
		}

		public String getSummary() {
			return summary;
		}
	}

	public static void main(String[] args) {	
		String workingDir = "/Users/drk04747/research/simple-health/misc/affix_test/";
		String inFile = workingDir + "words_to_search";
		String outFile = workingDir + "words_to_search.out";

		AffixTestGenerator generator = new AffixTestGenerator();
		generator.populateWordMap(inFile);
		generator.printWordMap(outFile);
		System.out.println("done");
	}
}
