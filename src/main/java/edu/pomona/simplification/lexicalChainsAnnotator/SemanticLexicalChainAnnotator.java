package edu.pomona.simplification.lexicalChainsAnnotator;

import java.util.HashSet;
import java.util.List;

import edu.pomona.simplification.text.UMLS;
import edu.pomona.simplification.text.UMLS_lexicalchain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;

public class SemanticLexicalChainAnnotator extends LexicalChainAnnotator {

	protected UMLS_lexicalchain umls;
	public SemanticLexicalChainAnnotator(List<CoreSentence> sentences) {
		super(sentences);
		super.type = LexicalChainType.Semantic;
//		this.wd = new Wordnet();
		this.umls = new UMLS_lexicalchain();
		for(int i = 0; i < sentences.size(); i ++) {
			CoreSentence s = sentences.get(i);
			this.preceding_index_offsets[i] = (i == 0 ? 0 : this.preceding_index_offsets[i-1]) + 
					s.coreMap().get(CoreAnnotations.TokensAnnotation.class).size();
			processSentence(s, i);
		}
		super.trimChainList();
		super.removeDuplicateChains();
//		arrange_chains();
	}
	protected void processSentence(CoreSentence sentence, int i) {
		CoreMap c = sentence.coreMap();
		for(CoreLabel token : c.get(CoreAnnotations.TokensAnnotation.class)) {
			// Synonymous lexical chain consists of Nouns
			// There are four Nouns tagged by POS: NN, NNS, NNP, NNPS
			// list of POS tags
			// https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
			if(token.tag().matches("NN|NNS|NNP|NNPS")){
				List<String> syns = umls.getSemanticAUIs(token.lemma());
				// remove duplicates
				HashSet<String> set = new HashSet<String>();
				for(String s : syns) {
					if(!set.contains(s)) {
						pushWord(s, token, i);
						set.add(s);
					}
				}
			}
		}
	}
}
