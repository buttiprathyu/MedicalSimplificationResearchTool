package edu.pomona.simplification.text;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pomona.simplification.Preferences;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;

public class TextProcessorOld {	
	private static final String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	private LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
	
	public static ArrayList<String> getAllWords(String text){
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(new StringReader(text),
				new CoreLabelTokenFactory(), "");
		
		ArrayList<String> words = new ArrayList<String>();
		
		while(tokenizer.hasNext()) {
			words.add(tokenizer.next().toString());
		}
		
		return words;
	}
	
	public static ArrayList<String> getCountableWordsSimple(String text){
		ArrayList<String> tokens = getAllWords(text);
		ArrayList<String> filtered = new ArrayList<String>();
		
		Pattern p = Pattern.compile(".*[a-zA-Z0-9].*");
		Matcher m = p.matcher("");
		
		for(String token: tokens) {
			if( m.reset(token).matches() ) {
				filtered.add(token);
			}
		}
		
		return filtered;
	}
	
	public List<Tree> getSentences(String text){
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(text));
				
		ArrayList<List<HasWord>> sents = new ArrayList<List<HasWord>>();
		
		for (List<HasWord> sentence : dp) {
			sents.add(sentence);
		}
		
		return parser.parseMultiple(sents, Preferences.NUM_PARSER_THREADS);
	}
	
	public static ArrayList<POSTaggedWord> getCountableWords(List<Tree> trees){
		ArrayList<POSTaggedWord> words = new ArrayList<POSTaggedWord>();
		
		for( Tree tree: trees ) {
			List<TaggedWord> tagged = tree.taggedYield();
			
			for(TaggedWord w: tagged) {
				PartOfSpeech pos = POSTaggedWord.convertPennPos(w.tag());
				
				if( pos != PartOfSpeech.OTHER ) {
					words.add(new POSTaggedWord(w.word().toLowerCase(), pos));
				}
			}
	    }
		
		return words;
	}
	
	public ArrayList<POSTaggedWord> getCountableWords(String text){
		return getCountableWords(getSentences(text));
	}
	
	public static ArrayList<POSTaggedWord> dedup(ArrayList<POSTaggedWord> words){
		return new ArrayList<POSTaggedWord>(new HashSet<POSTaggedWord>(words));
	}
	
	private static boolean isPosTagOfInterest(String posTag) {
		return posTag.equals("NN") || posTag.equals("NNS") || // nouns
			   posTag.startsWith("JJ") || // adjectives
			   posTag.startsWith("RB") || // adverbs
			   posTag.startsWith("VB");   // verbs
	}
	
	private static void test() {
		String testText = "This is some text. And this is some more text.";
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(testText));
		LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
		
		for (List<HasWord> sentence : dp) {
			Tree tree = parser.parse(sentence);
			//List<Tree> leaves = tree.getLeaves();
			List<TaggedWord> tagged = tree.taggedYield();
			
			for(TaggedWord w: tagged) {
				System.out.println(w.word() + "\t" + w.tag());
			}
	    }
	}
	
	public static void main(String[] args) {
		test();
	}
}