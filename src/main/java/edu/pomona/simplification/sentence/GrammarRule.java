package edu.pomona.simplification.sentence;

import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.IntPair;

public class GrammarRule {	
	private String normalParse;
	private String simpleParse;
	private String description;
	private String normalExample;
	private String simpleExample;
	private String normalHighlight;
	private String simpleHighlight;
	private TregexPattern pattern;

	public GrammarRule(String ruleLine) {		
		String[] tokens = ruleLine.split("\\t");

		normalParse = tokens[0].replaceAll(" ","").replaceAll("\\d","").replaceAll("\t", "").trim();
		simpleParse = tokens[1].replaceAll(" ","").replaceAll("\\d","").replaceAll("\t", "").trim();
		description = tokens[2].trim();
		normalExample = tokens[3].trim();
		simpleExample = tokens[4].trim();
		normalHighlight = tokens[5].trim();
		simpleHighlight = tokens[6].trim();
		
		pattern = TregexPattern.compile(ruleToTregex(normalParse));
	}

	public boolean matchesSentence(Tree sentence) {
		return getNormalizedParseText(sentence).contains(normalParse);
	}
	
	// public String getSentenceLength(Tree sentence) {
	// 	if (sentence == null || sentence.isEmpty()) {
	// 	      return 0;
	// 	}
	// 	CoreLabel token = sentence.tokens();
	//     //StringTokenizer tokens = new StringTokenizer(sentence);
	// 	//System.out.println("Sentence Length"+ tokens.countTokens());
	// 	if(token.length > 25) {
	// 		//call the rule and display
	// 	}
	//     //return tokens.countTokens();	
	// }

	
	public String getTregexMatch(Tree sentence){
		TregexMatcher matcher = pattern.matcher(sentence);
		
		if( matcher.find() ) {
			Tree match = matcher.getMatch();
			
			StringBuffer buffer = new StringBuffer();
			
			for (Tree t : match.getLeaves()) { //Add all the matched leaves to a list
	    		buffer.append(t.toString() + " ");
	    	}
			
			return buffer.substring(0, buffer.length()-1);
		}else {
			return "";
		}
	}

	/**
	 * Precondition: assume that this rule matches this sentence
	 * 
	 * @param sentence
	 * @return
	 */
	public String getSentenceLengthExceededInfo(){
		return "<p class=\"instructions\"> Please split this long sentence into shorter sentences. </p>";
	}

	public String getChangeInformation() {
		return "<p class=\"instructions\">" + description + " For example:</p>" + 
				"<p class=\"example\">" + markupExample(normalExample, normalHighlight) + "</p>" + 
				"<p class=\"example\">" + markupExample(simpleExample, simpleHighlight) + "</p>";
	}

	private String markupExample(String example, String textToMark) {
		return example.replace(textToMark, "<exampleHighlight>" + textToMark + "</exampleHighlight>").trim();
	}
	
	private String getNormalizedParseText(Tree sentence) {
		String parseString = sentence.toString();
		char[] parseChars = parseString.toCharArray();
		int length = parseString.length()-1;

		//Remove words from the parse 
		// TODO: Could be written more cleanly, but will leave as is for now
		for(int i=length; i > 0;) {
			if(Character.isLetter(parseString.charAt(i-1)) && parseString.charAt(i)==')') {
				i--;
				int endWord = 2;
				while (endWord >= 1) {
					parseChars[i] = '~';
					if (parseString.charAt(i-1) == ' ') {
						i--;
						parseChars[i] = '~';
						endWord = 0;
					}
					i--;
				}

			}else{
				i--;
			}

			if (i <= 2) {
				i = 0;
			}
		}

		String modifiedParseString = new String(parseChars);

		//Simplify parsing to match the format of our original aggregated data set
		// TODO: could be done faster in one pass, but fine for now
		return new String(modifiedParseString.replaceAll("~","").replaceAll("JJR", "JJ").replaceAll("JJS", "JJ").
				replaceAll("VBD", "VB").replaceAll("VBZ", "VB").replaceAll("VBG", "VB").replaceAll("VBN", "VB").
				replaceAll("RBR", "RB").replaceAll("RBS", "RB").replaceAll("NNS", "NN").replaceAll("NNPS", "NN").
				replaceAll("NNP", "NN").replaceAll(" ", ""));
	}

	public static String ruleToTregex(String ruleText) {
		String parenStripped = ruleText.substring(1, ruleText.length()-1);

		// see if it's a simple POS tree
		if( !parenStripped.contains("(") ) {
			//return "/^" + parenStripped + "/";
			return processPOS(parenStripped);
		}else {
			// recursive case
			int firstLeftIndex = parenStripped.indexOf("(");
			String name = parenStripped.substring(0, firstLeftIndex);
			String rest = parenStripped.substring(firstLeftIndex);

			String finalText = "(" + name + " < ";			
			finalText += childProcessor(getChildren(rest));
			finalText += ")";
			
			return finalText;
		}
	}
	
	private static String processPOS(String pos) {
		if( pos.equals("JJ") ||
			pos.equals("VB") ||
			pos.equals("RB") ||
			pos.equals("NN") ) {
			return "/^" + pos + "/";
		}else if( pos.equals(",,") ||
				  pos.contains(":") ||
				pos.contains("$")){
			return "/^" + pos + "$/";
		}else if (pos.contains(".")){
			return "/^" + pos.replaceAll(".", "\\.") + "$/";
		}else {
			return pos;
		}
	}
	
	private static String childProcessor(ArrayList<String> children) {
		if( children.size() == 1 ) {
			return ruleToTregex(children.get(0));
		}else {
			String first = children.get(0);
			children.remove(0);
			return "(" + ruleToTregex(first) + " . " + childProcessor(children) + ")";
		}
	}

	private static ArrayList<String> getChildren(String childrenText) {
		int parenCount = 0;

		StringBuffer current = new StringBuffer();

		ArrayList<String> children = new ArrayList<String>();

		for( int i = 0; i < childrenText.length(); i++ ) {
			char c = childrenText.charAt(i);
			current.append(c);

			if( c == '(' ) {
				parenCount++;
			}else if( c == ')' ) {
				parenCount--;

				if( parenCount == 0 ) {
					children.add(current.toString());
					current = new StringBuffer();
				}
			}
		}

		return children;
	}

	public String getNormalParse() {
		return normalParse;
	}

	public String getSimpleParse() {
		return simpleParse;
	}

	public String getDescription() {
		return description;
	}

	public String getNormalExample() {
		return normalExample;
	}

	public String getSimpleExample() {
		return simpleExample;
	}

	public String getNormalHighlight() {
		return normalHighlight;
	}

	public String getSimpleHighlight() {
		return simpleHighlight;
	}

	public String toString() {
		return normalParse + "\t" + simpleParse + "\t" + description + "\t" + normalExample + "\t" + 
				simpleExample + "\t" + normalHighlight + "\t" + simpleHighlight;
	}
}
