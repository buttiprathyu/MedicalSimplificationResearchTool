package edu.pomona.simplification.lexicalChainsAnnotator;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;

public class ExactLexicalChainAnnotator extends LexicalChainAnnotator{

	
	public ExactLexicalChainAnnotator(List<CoreSentence> sentences) {
		super(sentences);
		super.type = LexicalChainType.Exact;
		for(int i = 0; i < sentences.size(); i ++) {
			CoreSentence s = sentences.get(i);
			this.preceding_index_offsets[i] = (i == 0 ? 0 : this.preceding_index_offsets[i-1]) + 
					s.coreMap().get(CoreAnnotations.TokensAnnotation.class).size();
			processSentence(s, i);
		}
		super.trimChainList();
	}

	protected void processSentence(CoreSentence sentence, int i) {
		CoreMap c = sentence.coreMap();
		for(CoreLabel token : c.get(CoreAnnotations.TokensAnnotation.class)) {
			// Exact lexical chain consists of Nouns
			// There are four Nouns tagged by POS: NN, NNS, NNP, NNPS
			// list of POS tags
			// https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
			if(token.tag().matches("NN|NNS|NNP|NNPS")){
				pushWord(token, i);
			}
		}
	}
}