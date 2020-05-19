package edu.pomona.simplification.substitution;

import java.util.ArrayList;
import java.util.List;

public class Substitution {
	private static final String SOURCE_SEPARATOR = "-D-";
	
	private String substitute;
	private String source;
	
	public Substitution(String substitute, String source) {
		this.substitute = substitute;
		this.source = source;
	}

	public String getSubstitute() {
		return substitute;
	}

	public String getSource() {
		return source;
	}
	
	public String toString() {
		return substitute + SOURCE_SEPARATOR + source;
	}
	
	private static String stripOffSource(String s){
		return s.split(SOURCE_SEPARATOR)[0];
	}
	
	public static List<String> getAllSubstitutes(List<Substitution> substitutes){
		List<String> result = new ArrayList<String>(substitutes.size());
		
		for( Substitution sub: substitutes ) {
			result.add(sub.getSubstitute());
		}
		
		return result;
	}
}
