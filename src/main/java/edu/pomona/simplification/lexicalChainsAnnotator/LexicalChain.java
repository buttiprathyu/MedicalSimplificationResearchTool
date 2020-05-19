package edu.pomona.simplification.lexicalChainsAnnotator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;

public class LexicalChain {

	private List<CoreLabel> chain;
	private List<Integer> sentence_nums;
	//private int sentence_num;
	private int[] preceding_index_offsets;
	private int ID;
	private String keyword;

	public LexicalChain(int ID, String keyword, int[] preceding_index_offsets) {
		this.ID = ID;
		this.keyword = keyword;
		//this.sentence_num = sentence_num;
		this.preceding_index_offsets = preceding_index_offsets;
		chain = new ArrayList<CoreLabel>();
		sentence_nums = new ArrayList<Integer>();
	}
	public LexicalChain(CoreLabel c, int sentence_num, int ID, String keyword, int[] preceding_index_offsets) {
		this.ID = ID;
		this.keyword = keyword;
		//this.sentence_num = sentence_num;
		this.preceding_index_offsets = preceding_index_offsets;
		chain = new ArrayList<CoreLabel>();
		chain.add(c);
		sentence_nums = new ArrayList<Integer>();
		sentence_nums.add(sentence_num);
	}
	public LexicalChain(int ID, String keyword, List<CoreLabel> chain, List<Integer> sentence_nums, int[] preceding_index_offsets) {
		this.ID = ID;
		this.keyword =  keyword;
		this.chain = chain;
		this.sentence_nums = sentence_nums;
		this.preceding_index_offsets = preceding_index_offsets;
	}
	public void insert(CoreLabel c, int sentenceNum) {
		for(int i = 0; i < chain.size(); i ++) {
			if(c.endPosition() < chain.get(i).beginPosition()) {
				chain.add(i, c);
				sentence_nums.add(i, sentenceNum);
				return;
			}
		}
		chain.add(c);
		sentence_nums.add(sentenceNum);
	}
	//	get LexicalChain ID
	public int getID() {
		return this.ID;
	}
	//	get LexicalChain Keyword
	public String getKeyword() {
		return this.keyword;
	}
	// get tokens
	public List<String> getWords() {
		List<String> list = new ArrayList<String>();
		for(CoreLabel c : chain) {
			list.add(c.word());
		}
		return list;
	}
	//	get the char offset of the beginning of the first noun in the text
	public int getStartOffset() {
		return chain.get(0).beginPosition();
	}
	//	get the char offset of the ending of the last noun in the text
	public int getEndOffset() {
		return chain.get(chain.size()-1).endPosition();
	}
	//	get word index in the text
	public int getIndex(int word_num) {
		return chain.get(word_num).index() + this.preceding_index_offsets[sentence_nums.get(word_num)];
	}
	//	get sentence number of input token - UNTESTED
	public int getSentenceNum(CoreLabel c) {
		for(int i = 0; i < chain.size(); i ++) {
			if(c.equals(chain.get(i))) {
				return sentence_nums.get(i);
			}
		}
		return -1;
	}
	//	get the number of items in this chain
	public int getLength() {
		return chain.size();
	}
	// 	Span is number of words between first noun and last noun in the chain
	public int getSpan() {
		if(chain.size() <= 1) return 0;
		int first_index = getIndex(0);
		int last_index = getIndex(chain.size()-1);
		return last_index - first_index + 1;
	}
	//	check if this chain has a cross with another chain
	//	return sum of total cross
	public int getNumberOfCross(List<LexicalChain> LCs) {
		int counter = 0;
		for(LexicalChain lc : LCs) {
			if(lc.getID() == this.ID || lc.getLength() <= 1) continue;
			int s = lc.getIndex(0);
			int e = lc.getIndex(lc.getLength()-1);
			int first_index = getIndex(0);
			int last_index = getIndex(chain.size()-1);
			if(first_index > e || s > last_index) {}
			else
				counter ++;
		}
		return counter;
	}
	public List<Integer> getCharsOffets(){
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < chain.size(); i ++) {
			list.add(chain.get(i).beginPosition());
		}
		return list;
	}
	//	print out all items with their index in the chain
	public void print_chain() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chain [" + this.keyword + "]:\t");
		for(CoreLabel c : chain) {
			sb.append("{\"" + c.word() + "\"(\"" +c.lemma() + "\")  @ (" + c.beginPosition() + "," + (c.endPosition() - 1) + ")}");
			sb.append(" -> ");
		}
		sb.append(" END ");
		System.out.println(sb.toString());
	}
	public List<CoreLabel> getChain() {
		return this.chain;
	}
	public boolean isEqualTo(LexicalChain lc) {
		List<CoreLabel> other_chain = lc.getChain();
		if(this.chain.size() != other_chain.size()) return false;
		for(int i = 0; i < this.chain.size(); i ++) {
			if(!this.chain.get(i).equals(other_chain.get(i)))
					return false;
		}
		return true;
	}
	public LexicalChain cloneLexicalChain(int newID, List<Integer> list) {
		List<CoreLabel> chainlist = new ArrayList<CoreLabel>();
		List<Integer> sentences = new ArrayList<Integer>();
		for(int i : list) {
			chainlist.add(this.chain.get(i));
			sentences.add(this.sentence_nums.get(i));
		}
		return new LexicalChain(newID, this.keyword, chainlist, sentences, Arrays.copyOf(this.preceding_index_offsets, this.preceding_index_offsets.length));
	}
}
