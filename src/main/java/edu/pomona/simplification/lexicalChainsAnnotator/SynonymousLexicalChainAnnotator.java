package edu.pomona.simplification.lexicalChainsAnnotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.pomona.simplification.text.UMLS;
import edu.pomona.simplification.text.UMLS_lexicalchain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;

public class SynonymousLexicalChainAnnotator extends LexicalChainAnnotator{
	//protected Wordnet wd;
	protected UMLS_lexicalchain umls;
	public SynonymousLexicalChainAnnotator(List<CoreSentence> sentences) {
		super(sentences);
		super.type = LexicalChainType.Synonymous;
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
				List<String> syns = umls.getSynonyms(token.lemma());
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
	
//	private void arrange_chains() {
//		List<LexicalChain> newChains = new ArrayList<LexicalChain>();
//		for(LexicalChain lc : super.chains) {
//			newChains.addAll(split_chain(lc));
//		}
//		super.chains = newChains;
//		removeDuplicateChains();
//	}
//	// if multiple lexically-identical words in a chain, then split the chain
//	// to multiple chains so that each sub chain can only contain distinct words
//	private List<LexicalChain> split_chain(LexicalChain lc) {
//		Map<String, Integer> checktable = new HashMap<>();
//		for(CoreLabel c : lc.getChain())
//			checktable.put(c.lemma(), 0);
//		List<List<Integer>> list = new ArrayList<List<Integer>>();
//		dfs(lc.getChain(), checktable, -1, new ArrayList<Integer>(), list);
//		List<LexicalChain> subchains = new ArrayList<LexicalChain>();
//		for(List<Integer> l : list) {
//			if(l.size() <= 1) continue;
//			subchains.add(lc.cloneLexicalChain(super.chain_assigning_ID ++, l));
//		}
//		return subchains;
//	}
//	private void dfs(List<CoreLabel> chain, Map<String, Integer> checktable, int pos, List<Integer> t, List<List<Integer>> list) {
//		if(pos >= chain.size()) {
//			for(String s : checktable.keySet()) {
//				if(checktable.get(s) == 0) return;
//			}
//			if(t.size() > checktable.size()) System.out.println("ERROR !!!");
//			list.add(new ArrayList<Integer>(t));
//			return;
//		}
//		int insert_pos = -1;
//		if(pos != -1 && checktable.get(chain.get(pos).lemma()) == 0) {
//			t.add(pos);
//			checktable.put(chain.get(pos).lemma(), 1);
//			insert_pos = t.size() - 1;
//		}
//		for(int i = pos + 1; i <= chain.size(); i ++) {
//			dfs(chain, checktable, i, t, list);
//		}
//		if(insert_pos != -1) {
//			t.remove(insert_pos);
//			checktable.put(chain.get(pos).lemma(), 0);
//		}
//	}
}
