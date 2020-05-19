package edu.pomona.simplification.morphology;

public class AffixMatch{
	private String summary;
	private Affix prefix;
	private Affix suffix;
	
	public AffixMatch(String summary, Affix prefix, Affix suffix){
		this.summary = summary;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	public boolean hasMatch() {
		return summary != null;
	}
	
	public String getSummary() {
		if( summary == null ) {
			throw new RuntimeException("Tried to get summary string from null match");
		}else {
			return summary;
		}
	}
	
	public boolean hasPrefixMatch() {
		return prefix != null;
	}
	
	public boolean hasSuffixMatch() {
		return suffix != null;
	}
	
	public Affix getPrefix() {
		return prefix;
	}
	
	public Affix getSuffix() {
		return suffix;
	}
	
	public String getPrefixString() {
		if( prefix == null ) {
			throw new RuntimeException("Tried to get prefix string from null match");
		}else {
			return prefix.getAffixString();
		}
	}
	
	public String getSufixString() {
		if( suffix == null ) {
			throw new RuntimeException("Tried to get suffix string from null match");
		}else {
			return suffix.getAffixString();
		}
	}
}
