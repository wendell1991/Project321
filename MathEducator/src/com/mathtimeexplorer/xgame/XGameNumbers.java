package com.mathtimeexplorer.xgame;

import java.util.ArrayList;

public class XGameNumbers {

	private int firstNumber;
	private int secondNumber;
	private int result;
	private int answer;
	private int whichToHide;
	private int ansPosition;
	private ArrayList<Integer> options;

	public int getFirstNumber() {
		return firstNumber;
	}

	public void setFirstNumber(int firstNumber) {
		this.firstNumber = firstNumber;
	}

	public int getSecondNumber() {
		return secondNumber;
	}

	public void setSecondNumber(int secondNumber) {
		this.secondNumber = secondNumber;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public int getWhichToHide() {
		return whichToHide;
	}

	public void setWhichToHide(int whichToHide) {
		this.whichToHide = whichToHide;
	}

	public ArrayList<Integer> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<Integer> options) {
		this.options = options;
	}

	public int getAnsPosition() {
		return ansPosition;
	}

	public void setAnsPosition(int ansPosition) {
		this.ansPosition = ansPosition;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
