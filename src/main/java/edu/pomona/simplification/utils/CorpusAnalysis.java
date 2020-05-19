package edu.pomona.simplification.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import edu.pomona.simplification.Preferences;
import edu.pomona.simplification.ToolController;
import edu.pomona.simplification.sentence.DoubleNegationChecker;
import edu.pomona.simplification.sentence.GrammarChangeSuggester;
import edu.pomona.simplification.sentence.GrammarRule;
import edu.pomona.simplification.substitution.AffixSubstitutionGenerator;
import edu.pomona.simplification.substitution.DuplicateStemFilter;
import edu.pomona.simplification.substitution.MorphNegation;
import edu.pomona.simplification.substitution.POSOnlyInputFilter;
import edu.pomona.simplification.substitution.SimplificationOption;
import edu.pomona.simplification.substitution.SubstitutionDriver;
import edu.pomona.simplification.substitution.WordFrequencyFilter;
import edu.pomona.simplification.substitution.WordFrequencyInputFilter;
import edu.pomona.simplification.substitution.WordnetSynonymGenerator;
import edu.pomona.simplification.text.Nominals;
import edu.pomona.simplification.text.POSTaggedWord;
import edu.pomona.simplification.text.PartOfSpeech;
import edu.pomona.simplification.text.PorterStemmer;
import edu.pomona.simplification.text.TextFrequencyCalculator;
import edu.pomona.simplification.text.TextProcessor;
import edu.pomona.simplification.text.UMLS;
import edu.pomona.simplification.text.Wordnet;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;

public class CorpusAnalysis {
	private PorterStemmer stemmer = new PorterStemmer();

	private StanfordCoreNLP pipeline;

	private WordFrequencyInputFilter frequencyFilter = new WordFrequencyInputFilter();

	private SubstitutionDriver synonymDriver = new SubstitutionDriver();
	private SubstitutionDriver wordNetDriver = new SubstitutionDriver();
	private SubstitutionDriver UMLSDriver = new SubstitutionDriver();
	private SubstitutionDriver nominalDriver = new SubstitutionDriver();
	private SubstitutionDriver negationDriver = new SubstitutionDriver();
	private SubstitutionDriver affixDriver = new SubstitutionDriver();

	private WordnetSynonymGenerator wordnetSynonymGenerator;
	private UMLS UMLSGenerator;
	private Nominals nominalGenerator;

	private GrammarChangeSuggester grammarChangeSuggester = new GrammarChangeSuggester();

	private DoubleNegationChecker doubleNegationChecker;	

	public CorpusAnalysis() {
		Wordnet wn = new Wordnet();

		// setup the synonym sources
		wordnetSynonymGenerator = new WordnetSynonymGenerator(wn);
		UMLSGenerator = new UMLS();
		nominalGenerator = new Nominals();

		//////////////////////////////
		// setup the wordNetDriver
		wordNetDriver.addInputFilter(frequencyFilter);
		UMLSDriver.addInputFilter(frequencyFilter);
		synonymDriver.addInputFilter(frequencyFilter);
		
		wordNetDriver.addGenerator(wordnetSynonymGenerator);
		UMLSDriver.addGenerator(UMLSGenerator);
		
		synonymDriver.addGenerator(wordnetSynonymGenerator);
		synonymDriver.addGenerator(UMLSGenerator);

		// setup postfilters for improved frequency and to avoid duplicates
		wordNetDriver.addResultFilter(new WordFrequencyFilter());
		wordNetDriver.addResultFilter(new DuplicateStemFilter());

		UMLSDriver.addResultFilter(new WordFrequencyFilter());
		UMLSDriver.addResultFilter(new DuplicateStemFilter());

		synonymDriver.addResultFilter(new WordFrequencyFilter());
		synonymDriver.addResultFilter(new DuplicateStemFilter());

		//////////////////////////////
		// setup the morph negation driver
		MorphNegation morphNegation = new MorphNegation(stemmer);
		negationDriver.addGenerator(morphNegation);

		//////////////////////////////
		// setup the affix driver
		affixDriver.addInputFilter(frequencyFilter);
		affixDriver.addGenerator(new AffixSubstitutionGenerator(wn));

		//////////////////////////////
		// setup double negation checker
		doubleNegationChecker = new DoubleNegationChecker(morphNegation);

		//////////////////////////////
		// setup the nominal checker
		nominalDriver.addInputFilter(new POSOnlyInputFilter(PartOfSpeech.NOUN));
		nominalDriver.addInputFilter(frequencyFilter);
		nominalDriver.addGenerator(nominalGenerator);

		/////////////////////////////
		// setup NLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,parse,depparse");
		props.setProperty("threads", Integer.toString(Preferences.NUM_PARSER_THREADS));
		pipeline = new StanfordCoreNLP(props);


		// setup the requested synonyms
		synonymDriver.clearGenerators();
		synonymDriver.addGenerator(wordnetSynonymGenerator);
		synonymDriver.addGenerator(UMLSGenerator);

	}

	public void printStats(File dir) {
		int totalSentences = 0;
		int totalFiles = 0;
		int totalWords = 0;

		int wordNetWords = 0;
		int UMLSWords = 0;
		int negationWords = 0;
		int affixWords = 0;
		int affixTotal = 0;
		int nominalWords = 0;
		int wordsWithSimps = 0;
		int totalOptions = 0;
		int doubleNegations = 0;
		int grammarSentences = 0;

		for( File file: dir.listFiles() ) {
			totalFiles++;
			String text = readFile(file);

			// parse, etc. the text
			Annotation annotator = new Annotation(text);
			pipeline.annotate(annotator);	    
			CoreDocument document = new CoreDocument(annotator);

			// -------- WORD-LEVEL PROCESSING ---------
			// get all of the words that could be simplified
			ArrayList<POSTaggedWord> words = TextProcessor.getCountableWords(document);
			// add count information here so that any substitution driver will have access
			TextFrequencyCalculator.addFrequencyInformation(words);

			totalWords += words.size();

			List<SimplificationOption> syns = synonymDriver.getAllSynonyms(words);
			List<SimplificationOption> wordSyns = wordNetDriver.getAllSynonyms(words);
			List<SimplificationOption> UMLSSyns = UMLSDriver.getAllSynonyms(words);
			List<SimplificationOption> negationSubs = negationDriver.getAllSynonyms(words);
			List<SimplificationOption> affixSubs = affixDriver.getAllSynonyms(words);
			List<SimplificationOption> nominalSubs = nominalDriver.getAllSynonyms(words);

			// put the "good" types of synonym replacers together
			List<List<SimplificationOption>> allSubs = new ArrayList<List<SimplificationOption>>(3);
			allSubs.add(syns);
			allSubs.add(negationSubs);
			allSubs.add(nominalSubs);
			
			// merge all the word simplifications, with priority to those in allSubs
			List<SimplificationOption> merged = ToolController.mergeOptions(allSubs, affixSubs);
			
			HashSet<String> synWordSet = getWords(merged);
			HashSet<String> wordnetWordSet = getWords(wordSyns);
			HashSet<String> umlsWordSet = getWords(UMLSSyns);
			HashSet<String> negationWordSet = getWords(negationSubs);
			HashSet<String> affixWordSet = getWords(affixSubs);
			HashSet<String> nomialWordSet = getWords(nominalSubs);
			
			for( POSTaggedWord pWord: words) {
				String word = pWord.getWord();
				boolean foundGood = false;

				if( synWordSet.contains(word) ) {
					wordsWithSimps++;
				}
				
				if( wordnetWordSet.contains(word) ) {
					wordNetWords++;
					foundGood = true;
				}
				
				if( umlsWordSet.contains(word) ) {
					UMLSWords++;
					foundGood = true;
				}
				
				if( nomialWordSet.contains(word) ) {
					nominalWords++;
					foundGood = true;
				}
				
				if( negationWordSet.contains(word) ) {
					negationWords++;
					foundGood = true;
				}
				
				if( affixWordSet.contains(word) ) {
					affixTotal++;
					
					if( !foundGood ) {
						affixWords++;
					}
				}
			}
				
			
			//for(SimplificationOption option: merged) {
			//	totalOptions =+ option.getSimplifications().size();
			//}
			
			// -------- SENTENCE-LEVEL PROCESSING ---------
			for( CoreSentence sentence: document.sentences() ) {
				totalSentences++;

				if( doubleNegationChecker.hasDoubleNegation(sentence) ) {
					doubleNegations++;
				}

				Tree parseTree = sentence.constituencyParse();
				List<GrammarRule> foundRules = grammarChangeSuggester.checkSentence(parseTree);

				if( foundRules.size() > 0 ){
					grammarSentences++;
				}
			}
			
			System.out.println(file);
			System.out.println(totalFiles);
			System.out.println("Total sentences\t" + totalSentences);
			System.out.println("Total words\t" + totalWords);
			System.out.println("Total files\t" + totalFiles);
			System.out.println("Words with simplifications\t" + wordsWithSimps + "\t" + ((double)wordsWithSimps)/totalWords);
			System.out.println("Ave. number of options\t" + ((double)totalOptions)/wordsWithSimps);
			System.out.println("WordNet words\t" + wordNetWords + "\t" + ((double)wordNetWords)/totalWords);
			System.out.println("UMLS words\t" + UMLSWords + "\t" + ((double)UMLSWords)/totalWords);
			System.out.println("negation words\t" + negationWords + "\t" + ((double)negationWords)/totalWords);
			System.out.println("Affix words\t" + affixWords + "\t" + ((double)affixWords)/totalWords);
			System.out.println("Affix words\t" + affixTotal + "\t" + ((double)affixTotal)/totalWords);
			System.out.println("Nominal words\t" + nominalWords + "\t" + ((double)nominalWords)/totalWords);
			System.out.println("Double negations\t" + doubleNegations + "\t" + ((double)doubleNegations)/totalSentences);
			System.out.println("Grammar sentences\t" + grammarSentences + "\t" + ((double)grammarSentences)/totalSentences);

		}		
	}

	private String readFile(File file) {
		StringBuffer text = new StringBuffer();
		
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			
			String line;
			
			while( (line = in.readLine()) != null ){
				if( line != "" ) {				
					text.append(line + " ");
				}
			}
			
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return text.substring(0, text.length()-1);
	}
	
	private HashSet<String> getWords(List<SimplificationOption> syns){
		HashSet<String> words = new HashSet<String>();
		
		for( SimplificationOption o: syns) {
			words.add(o.getWord());
		}
		
		return words;
	}
	
	public static void main(String[] args) {
		//File directory = new File("/Users/drk04747/temp/corpus/");
		File directory = new File("/Users/drk04747/Dropbox/Shared-NIHR01-Text-Simplification/Corpora/English/2016 - Wikipedia Illnesses (Pako)/en_Wikipedia/articles/");
		
		CorpusAnalysis analyzer = new CorpusAnalysis();
		analyzer.printStats(directory);
	}
}
