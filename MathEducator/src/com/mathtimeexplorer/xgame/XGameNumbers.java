package com.mathtimeexplorer.xgame;

import java.util.HashMap;

public class XGameNumbers {

	private int firstNo;
	private int firstNoResId;
	private int secondNo;
	private int secondNoResId;
	private int result;
	private int resultResId;
	private int answer;
	private int whichToHide;
	private HashMap<Integer, Integer> optionsMap;

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

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getFirstNo() {
		return firstNo;
	}

	public void setFirstNo(int firstNo) {
		this.firstNo = firstNo;
	}

	public int getFirstNoResId() {
		return firstNoResId;
	}

	public void setFirstNoResId(int firstNoResId) {
		this.firstNoResId = firstNoResId;
	}

	public int getSecondNoResId() {
		return secondNoResId;
	}

	public void setSecondNoResId(int secondNoResId) {
		this.secondNoResId = secondNoResId;
	}

	public int getSecondNo() {
		return secondNo;
	}

	public void setSecondNo(int secondNo) {
		this.secondNo = secondNo;
	}

	public int getResultResId() {
		return resultResId;
	}

	public void setResultResId(int resultResId) {
		this.resultResId = resultResId;
	}

	public HashMap<Integer, Integer> getOptionsMap() {
		return optionsMap;
	}

	public void setOptionsMap(HashMap<Integer, Integer> optionsMap) {
		this.optionsMap = optionsMap;
	}
	
	public String toString() {
		return "FirstNo: "+firstNo+" FirstNoResId: "+firstNoResId+" SecondNo: "+secondNo+" SecondNoResId: "+secondNoResId
				+" Result: "+result+" ResultResId: "+resultResId+" Answer: "+answer+" WhichToHide: "+whichToHide;
	}
}
