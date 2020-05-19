package edu.pomona.simplification.lexicalChainsAnnotator;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;

public class LexicalChainsAnnotator {
	
	/**
	 * For each sentence in sentences, creat a lexical chain annotation.  
	 * The List returned should have one entry for each sentence in sentences.
	 * 
	 * @param sentences
	 * @return
	 */
	String format = "%-15s %-10s %-15s %-10s %-10s %s%n";
	public List<LexicalChainAnnotation> sentenceDifficulty(List<CoreSentence> sentences){
		// I think the list of sentences should have all the information that 
		// you need, but if need be, you can pass in the CoreDocument instead
		for (CoreSentence s : sentences) {
			
			CoreMap c = s.coreMap();
			System.out.println("Sentence text:");
			System.out.println(c.get(CoreAnnotations.TextAnnotation.class));
			// Here are all info we need to construct lexical chains: Word, Lemma, Begin char offset, End char offset, Index
			System.out.printf(format, "TOKEN", "TAG", "LEMMA", "BEGIN", "END", "INDEX");
			// results of rules action
			for (CoreLabel token : c.get(CoreAnnotations.TokensAnnotation.class)) {
				System.out.printf(format, token.word(), token.tag(), token.lemma(), token.beginPosition(), token.endPosition(), token.index());
			}
		}
		
//		ExactLexicalChainAnnotator lco = new ExactLexicalChainAnnotator(sentences);
//		lco.printout();
//		System.out.println(lco.getLCAnnotation().generateJSON());
		LexicalChainAnnotator lca = new ExactLexicalChainAnnotator(sentences);
		lca.printout();
		System.out.println(lca.getLCAnnotation().generateJSON());
		return null;
	}
	public String getChainsByJSON(Enum type, List<CoreSentence> sentences){
		LexicalChainAnnotator lca = null;
		if(type == LexicalChainType.Exact) {
			System.out.println("Exact Lexical Chain: ");
			lca = new ExactLexicalChainAnnotator(sentences);
		}else if(type == LexicalChainType.Synonymous) {
			System.out.println("Synonymous Lexical Chain: ");
			lca = new SynonymousLexicalChainAnnotator(sentences);
		}else if(type == LexicalChainType.Semantic) {
			System.out.println("Semantical Lexical Chain: ");
			lca = new SemanticLexicalChainAnnotator(sentences);
		}
		lca.printout();
		return lca != null ? lca.getLCAnnotation().generateJSON() : "";
	}
}
