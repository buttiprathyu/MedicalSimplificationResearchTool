package edu.pomona.simplification.morphologyen;

public class Counter {
	
	Integer corpdx;
	Integer docdx;
	Integer sentdx;
	Integer tokdx;
	
	public Counter(){
		corpdx = 0;
		docdx = 0;
		sentdx = 0;
		tokdx = 0;
	}
	
	public Integer getCorpdx(){
		return corpdx;
	}

	public void setCorpdx(Integer val){
		corpdx = val;
	}
	
	public Integer getDocdx(){
		return docdx;
	}

	public void setDocdx(Integer val){
		docdx = val;
	}
	
	public Integer getSentpdx(){
		return sentdx;
	}

	public void setSentdx(Integer val){
		sentdx = val;
	}
	
	public Integer getTokdx(){
		return tokdx;
	}

	public void setTokdx(Integer val){
		tokdx = val;
	}
}
