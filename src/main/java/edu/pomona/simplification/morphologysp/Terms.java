package edu.pomona.simplification.morphologysp;

import java.util.HashMap;

public class Terms {
	private HashMap<String,String> tokMapMaster = new HashMap<String,String>();;
	private HashMap<String,String> posMapMaster = new HashMap<String,String>();;
	private HashMap<String,String> acceptMapMaster = new HashMap<String,String>();;
	
	public void put(String k, String v, String mdx){
		HashMap<String,String> currentMap = null;
		if (mdx.equals("1")){
			currentMap = tokMapMaster;
		} else {
			if (mdx.equals("2")){
				currentMap = posMapMaster;
			} else {
				if (mdx.equals("3")){
					currentMap = acceptMapMaster;
				}
			}
		}
		currentMap.put(k, v);
	}
	
	public String get(String k, String mdx){
		HashMap<String,String> currentMap = null;
		if (mdx.equals("1")){
			currentMap = tokMapMaster;
		} else {
			if (mdx.equals("2")){
				currentMap = posMapMaster;
			} else {
				if (mdx.equals("3")){
					currentMap = acceptMapMaster;
				}
			}
		}
		return currentMap.get(k);
	}
	
	public Boolean containsV(String v, String mdx){
		HashMap<String,String> currentMap = null;
		if (mdx.equals("1")){
			currentMap = tokMapMaster;
		} else {
			if (mdx.equals("2")){
				currentMap = posMapMaster;
			} else {
				if (mdx.equals("3")){
					currentMap = acceptMapMaster;
				}
			}
		}
		if (currentMap.containsValue(v)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean containsK(String k, String mdx){
		HashMap<String,String> currentMap = null;
		if (mdx.equals("1")){
			currentMap = tokMapMaster;
		} else {
			if (mdx.equals("2")){
				currentMap = posMapMaster;
			} else {
				if (mdx.equals("3")){
					currentMap = acceptMapMaster;
				}
			}
		}
		if (currentMap.containsKey(k)) {
			return true;
		} else {
			return false;
		}
	}
	
	public HashMap<String,String> getMap(String mdx){
		if (mdx.equals("1")){
			return tokMapMaster;
		} else {
			if (mdx.equals("2")){
				return posMapMaster;
			} else {
				if (mdx.equals("3")){
					return acceptMapMaster;
				} else {
					return null;
				}
			}
		}
	}
}
