package edu.pomona.simplification.lexicalChainsAnnotator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;

public class LexicalChainAnnotator {
	protected List<LexicalChain> chains;
	protected int chain_assigning_ID; // An self-ascending number to be assigned to each Lexical Chain as an ID
	protected Map<String, Integer> dictionary;
	protected int[] preceding_index_offsets;
	protected int total_length;
	protected Enum type;
	
	public LexicalChainAnnotator(List<CoreSentence> sentences) {
		this.total_length = 0;
		for(CoreSentence s : sentences) {
			this.total_length += s.coreMap().get(CoreAnnotations.TokensAnnotation.class).size();
		}
		this.chain_assigning_ID = 0;
		this.dictionary = new HashMap<>();
		this.chains = new ArrayList<>();
		this.preceding_index_offsets = new int[sentences.size()];
	}
	protected int pushWord(CoreLabel c, int sentence_num) {
		int ID = checkRepetition(c.lemma());
		LexicalChain lc = null;
		// if the Noun is pushed for its first time, create an atomic chain for it
		if(ID == -1) {
			lc = new LexicalChain(c, sentence_num, chain_assigning_ID, c.lemma(), this.preceding_index_offsets);
			chains.add(lc);
			dictionary.put(c.lemma(), chain_assigning_ID);
			ID = chain_assigning_ID;
			chain_assigning_ID ++;
			return 1;
		}else {
			lc = locateLexicalChain(ID);
			lc.insert(c,sentence_num);
			return 0;
		}
	}
	// Building exact lexical chain
	// return 1 - an atomic lexical chain (length = 1) is created
	// return 0 - the pushed word is inserted into an existing chain
	protected int pushWord(String synonyms, CoreLabel c, int sentence_num) {
		int ID = checkRepetition(synonyms);
		LexicalChain lc = null;
		// if the synonyms is pushed for its first time, create an atomic chain for it
		if(ID == -1) {
			lc = new LexicalChain(c, sentence_num, chain_assigning_ID, synonyms, this.preceding_index_offsets);
			chains.add(lc);
			dictionary.put(synonyms, chain_assigning_ID);
			ID = chain_assigning_ID;
			chain_assigning_ID ++;
			return 1;
		}else {
			lc = locateLexicalChain(ID);
			lc.insert(c,sentence_num);
			return 0;
		}
	}
	private int checkRepetition(String s) {
		return dictionary.getOrDefault(s, -1);
	}
	protected void trimChainList() {
		List<LexicalChain> t = new ArrayList<LexicalChain>();
		for(LexicalChain lc : chains) {
			if(lc.getLength() <= 1) continue;
			t.add(lc);
		}
		this.chains = t;
	}
	protected void removeDuplicateChains() {
		int i = 0;
		while(i < chains.size()) {
			boolean duplicate_exist = false;
			Inner:
				for(int j = 0; j < chains.size(); j ++) {
					LexicalChain lc = chains.get(j);
					if(i != j && lc.isEqualTo(chains.get(i))) {
							duplicate_exist = true;
							break Inner;
					}
				}
			if(duplicate_exist) chains.remove(i);
			else i ++;
		}
	}
	private LexicalChain locateLexicalChain(int ID) {
		for(LexicalChain lc : chains) {
			if(lc.getID() == ID)
				return lc;
		}
		return null;
	}
	
	// Exact Chain Properties
	public void computeNumberOfExactChains() {
		int counter = 0;
		for(LexicalChain lc : chains) {
			if(lc.getLength() > 1)
				counter ++;
		}
		num_exact = counter;
	}
	public void computeAverageChainLength() {
		double counter = 0.0;
		for(LexicalChain lc : chains) {
			if(lc.getLength() <= 1 ) continue;
			counter += lc.getLength();
		}
		average_chain_length = counter / num_exact;
	}
	public void computeAverageChainSpan() {
		double counter = 0.0;
		for(LexicalChain lc : chains) {
			if(lc.getLength() > 1) {
				counter += lc.getSpan();
			}
		}
		average_chain_span = (double)counter / (double)num_exact;
	}
	public void computeNumberOfCross() {
		int counter = 0;
		for(LexicalChain lc : chains) {
			if(lc.getLength() > 1) {
				counter += lc.getNumberOfCross(chains) > 0 ? 1 : 0;
			}
		}
		numberofcross = counter;
	}
	public void computeNumberOfMoreThanHalfDoc() {
		int counter = 0;
		for(LexicalChain lc : chains) {
			if(lc.getLength() > 1 && lc.getSpan() > total_length / 2) {
				counter ++;
			}
		}
		numberofhalfdoclength = counter;
	}
	private List<List<Integer>> getCharsOffsets(){
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		for(LexicalChain lc : chains)
			list.add(lc.getCharsOffets());
		return list;
	}
	public LexicalChainAnnotation generateLexicalChainAnnotation() {
		computeProperties();
		LexicalChainAnnotation lca = new LexicalChainAnnotation(LexicalChainType.Exact, chains, getCharsOffsets(), 
								num_exact, average_chain_length, average_chain_span, numberofcross, numberofhalfdoclength);
		return lca;
	}
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private int num_exact, numberofcross, numberofhalfdoclength;
	private double average_chain_length, average_chain_span;
	private void computeProperties() {
		computeNumberOfExactChains();
		computeAverageChainLength();
		computeAverageChainSpan();
		computeNumberOfCross();
		computeNumberOfMoreThanHalfDoc();
	}
	public void printout() {
		computeProperties();
		for(LexicalChain lc : chains) {
			lc.print_chain();
		}
		System.out.println("-----------------");
		System.out.println("Five Properties: ");
		System.out.println("Number of Exact Lexical Chains: " + num_exact);
		System.out.println("Average Chain Length: " + df2.format(average_chain_length));
		System.out.println("Average Chain Span: " + df2.format(average_chain_span));
		System.out.println("Number of Cross: " + numberofcross);
		System.out.println("Number of Half Document Length Chain: " + numberofhalfdoclength);
	}
	public LexicalChainAnnotation getLCAnnotation() {
		computeProperties();
		return new LexicalChainAnnotation(type, this.chains, this.getCharsOffsets(), 
				this.num_exact, this.average_chain_length, this.average_chain_span, 
				this.numberofcross, this.numberofhalfdoclength);
	}

	
}
