package edu.pomona.simplification.morphologysp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FrequencyFilter {
	
	private static HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
	
	public FrequencyFilter(Terms Ts, int iThreshold){}
	
	/*public FrequencyFilter(Terms Ts, int iThreshold){
		BufferedReader inBr;
		FileInputStream inStream = null;
		String rawLine;
		String[] fineLine;
		try{
			// read in file
			inStream = new FileInputStream("/Users/pokea/Documents/"
					+ "Work/UofA/Current/MIS/AffixSimplification/"
					+ "Code/subwordSpanish/Resources/spa_vocab");
			inBr = new BufferedReader(new InputStreamReader(inStream));
			while ((rawLine = inBr.readLine()) != null) {
				fineLine = rawLine.split("\t");
				String token = fineLine[0].toLowerCase();
				if (Ts.containsV(token,"1")){
					String freq = fineLine[1].replace("\n", "");
					try{
						int i = Integer.parseInt(freq);
						freqMap.put(token,i);
					}catch(NumberFormatException ex){ // handle your exception
						System.out.println("Couldnt Parse:\t" + freq);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();				
		} finally {
			try {
				if (inStream != null){
					inStream.close();
				}
		    } catch (IOException ex) {  
				ex.printStackTrace();
			}
		}
		
		for (String token : Ts.getMap("1").values()){
			Boolean accept;
			if (freqMap.containsKey(token)){
				if (freqMap.get(token) >= iThreshold){
					accept = false;
				} else {
					accept = true;
				}
			} else {
				accept = true;
			}
			if (accept){
				Ts.put(token, "", "3");
			}
		}
	}*/

	/*public boolean contains(String s){
		if (freqMap.containsKey(s)){
			return true;
		} else {
			return false;
		}
	}*/
	
	public boolean contains(String s){
		return true;
	}
	
	/*public Integer getVal(String s){
		return freqMap.get(s);
	}*/
	
	
	//private void getVals(){
		
	//	String fileName = "/Users/pokea/Documents/Work/UofA/Current/"
	//			+ "MIS/AffixSimplification/Code/subwordSpanish/"
	//			+ "Resources/PilotCounts"; 
	//	TreeMap<String, String> sortedTokMap = new TreeMap<String, String>(tokMapMaster);

	//	BufferedWriter bw = null;
	//	FileWriter fw = null;

	//	try {
	//		fw = new FileWriter(fileName);
	//		bw = new BufferedWriter(fw);
	//		String outString;
	//		HashSet<String> valSet = new HashSet<String>(sortedTokMap.values());
				
	//		for (String token : valSet){
	//			if (freqMap.containsKey(token)){
	//				String freq = Integer.toString(freqMap.get(token));
	//				outString = token + "\t" + freq + "\n";
	//			} else {
	//				outString = token + "\t" + "0" + "\n";
	//			}
	//			bw.write(outString);
	//		}
	//	} catch (IOException e) {
	//		e.printStackTrace();
	//	} finally {
	//		try {
	//			if (bw != null)
	//				bw.close();
	//			if (fw != null)
	//				fw.close();
	//		} catch (IOException ex) {
	//			ex.printStackTrace();
	//		}
	//	}
	//}
}
