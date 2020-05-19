package edu.pomona.simplification;

public class SessionInformation {
	private String sessionID;
	private int textID;
	
	public SessionInformation(String sessionId, int textID) {
		this.sessionID = sessionId;
		this.textID = textID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public int getTextID() {
		return textID;
	}
}
