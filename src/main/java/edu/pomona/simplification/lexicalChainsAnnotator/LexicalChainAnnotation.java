package edu.pomona.simplification.lexicalChainsAnnotator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class LexicalChainAnnotation {
	Enum lc_type;
	List<LexicalChain> list;
	List<List<Integer>> charsoffsets; 
	private int num_chains, numberofcross, numberofhalfdoclength;
	private double average_chain_length, average_chain_span;
	public LexicalChainAnnotation(Enum type, List<LexicalChain> list, List<List<Integer>> charsoffsets,
			int num_chains, double average_chain_length, double average_chain_span, int numberofcross, int numberofhalfdoclength) {
		this.lc_type = type;
		this.list = list;
		this.charsoffsets = charsoffsets;
		this.num_chains = num_chains;
		this.average_chain_length = average_chain_length;
		this.average_chain_span = average_chain_span;
		this.numberofcross = numberofcross;
		this.numberofhalfdoclength = numberofhalfdoclength;
	}
	// Types
	public String getType() {
		return this.lc_type.name();
	}
	
	// Number of Total Chains
	public int getNumChains() {
		return this.num_chains;
	}
	public void setChains(List<LexicalChain> list) {
		this.list = list;
	}
	
	// Average Chain Length
	public double getAverageLength() {
		return this.average_chain_length;
	}
	public void setAverageLength(double average_chain_length) {
		this.average_chain_length = average_chain_length;
	}
	
	// Average Chain Span
	public double getAverageSpan() {
		return this.average_chain_span;
	}
	
	// Number of Crossing Chains
	public int getNumCrossingChains() {
		return numberofcross;
	}

	public void setNumCrossingChains(int numCrossingChains) {
		this.numberofcross = numCrossingChains;
	}
	
	// Number of Half Doc Length
	public int getNumHalfDocLengthChains() {
		return numberofhalfdoclength;
	}

	public void setNumHalfDocLengthChains(int numHalfDocLength) {
		this.numberofhalfdoclength = numHalfDocLength;
	}
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	public String generateJSON() {
		JSONArray chains_array = new JSONArray();
		// add Chains
		for(int i = 0; i < list.size(); i ++) {
			LexicalChain lc = list.get(i);
			List<String> words = lc.getWords();
			List<Integer> offsets = charsoffsets.get(i);
			JSONArray array = new JSONArray();
			for(int j = 0; j < words.size(); j ++) {
				JSONArray offset = new JSONArray().put(offsets.get(j)).put(offsets.get(j) + words.get(j).length() - 1);
				Map t = new LinkedHashMap(2);
				t.put("word", words.get(j));
				t.put("location", offset);
				array.put(t);
			}
			JSONObject jo = new JSONObject().put("Chain", array);
			chains_array.put(jo);
		}
		chains_array.put(new JSONObject().put("Number of Lexical Chains", this.num_chains));
		chains_array.put(new JSONObject().put("Average Chain Length", df2.format(this.average_chain_length)));
		chains_array.put(new JSONObject().put("Average Chain Span", df2.format(this.average_chain_span)));
		chains_array.put(new JSONObject().put("Number of Cross Chains", this.numberofcross));
		chains_array.put(new JSONObject().put("Number of Half-Document-Length Chains", this.numberofhalfdoclength));
		return chains_array.toString();
	}
}
