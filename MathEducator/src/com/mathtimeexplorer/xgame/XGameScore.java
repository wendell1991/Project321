package com.mathtimeexplorer.xgame;

public class XGameScore {
	private int correct = 0;
	private int wrong = 0;
	private int qnsAttempted = 0;
	
	public int getCorrect() {
		return correct;
	}
	
	public void setCorrect(int correct) {
		this.correct = correct;
	}
	
	public int getWrong() {
		return wrong;
	}
	
	public void setWrong(int wrong) {
		this.wrong = wrong;
	}
	
	public int getQnsAttempted() {
		return qnsAttempted;
	}
	
	public void setQnsAttempted(int qnsAttempted) {
		this.qnsAttempted = qnsAttempted;
	}
}
