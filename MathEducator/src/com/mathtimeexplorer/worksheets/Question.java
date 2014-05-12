package com.mathtimeexplorer.worksheets;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {

	private int questionId;
	private String question;
	private String a;
	private String b;
	private String c;
	private String d;
	private String answer;
	private int quizId;
	private int subtopicId;
	private String explanation;
	private int mark;
	private int difficulty;

	public Question(){
		
	}
	
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getQuizId() {
		return quizId;
	}
	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}
	public int getSubtopicId() {
		return subtopicId;
	}
	public void setSubtopicId(int subtopicId) {
		this.subtopicId = subtopicId;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(questionId);
		parcel.writeString(question);
		parcel.writeString(a);
		parcel.writeString(b);
		parcel.writeString(c);
		parcel.writeString(d);
		parcel.writeString(answer);
		parcel.writeInt(quizId);
		parcel.writeInt(subtopicId);
		parcel.writeString(explanation);
		parcel.writeInt(mark);
		parcel.writeInt(difficulty);
	}

	public static final Parcelable.Creator<Question> CREATOR = new
			Parcelable.Creator<Question>() {
		public Question createFromParcel(Parcel in) {
			return new Question(in);
		}

		public Question[] newArray(int size) {
			return new Question[size];
		}
	};

	private Question(Parcel parcel) {
		questionId = parcel.readInt();
		question = parcel.readString();
		a = parcel.readString();
		b = parcel.readString();
		c = parcel.readString();
		d = parcel.readString();
		answer = parcel.readString();
		quizId = parcel.readInt();
		subtopicId = parcel.readInt();
		explanation = parcel.readString();
		mark = parcel.readInt();
		difficulty = parcel.readInt();
	}
}
