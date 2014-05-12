package com.mathtimeexplorer.worksheets;

import java.util.ArrayList;

public class Quiz {
	
	private int quizId;
	private String quizName;
	private int passMark;
	private int totalScore;
	private int timeLimit;
	private String deadline;
	private int quizType;
	private int subTopicId;
	private int schoolId;
	private String questionsList;
	private int noOfQuestions;
	private int status;
	
	public int getQuizId() {
		return quizId;
	}
	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}
	public String getQuizName() {
		return quizName;
	}
	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}
	public int getPassMark() {
		return passMark;
	}
	public void setPassMark(int passMark) {
		this.passMark = passMark;
	}
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	public int getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public int getQuizType() {
		return quizType;
	}
	public void setQuizType(int quizType) {
		this.quizType = quizType;
	}
	public int getSubTopicId() {
		return subTopicId;
	}
	public void setSubTopicId(int subTopicId) {
		this.subTopicId = subTopicId;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public String getQuestionList() {
		return questionsList;
	}
	public void setQuestionList(String questionList) {
		this.questionsList = questionList;
	}
	public int getNoOfQuestions() {
		return noOfQuestions;
	}
	public void setNoOfQuestions(int noOfQuestions) {
		this.noOfQuestions = noOfQuestions;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
