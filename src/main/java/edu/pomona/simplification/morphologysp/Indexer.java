package edu.pomona.simplification.morphologysp;

import java.util.HashMap;

public class Indexer {
	HashMap<Integer,Integer> indeces;
	
	public Indexer(){
		indeces = new HashMap<Integer,Integer>(); 
	}
	
	public void add(Integer k){
		Integer count = 1;
		if (indeces.containsKey(k))
			count += indeces.get(k);
		indeces.put(k, count);
	}
	
	public void reset(Integer k){
		indeces.put(k, 0);
	}
	
	public String getAll(){
		String index = "";
		for (int i = 0; i < 3; i++) {
			if (i == 2) {
				index += indeces.get(i);
			} else {
				index += indeces.get(i) + "\t";
			}
		}
		return index;
	}
}
