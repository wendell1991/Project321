package com.mathtimeexplorer.main;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	
	private int app_user_id;
	private String first_name;
	private String last_name;
	private String gender;
	private int school_id;
	private int class_id;
	private int eduLevel;
	
	public int getApp_user_id() {
		return app_user_id;
	}
	
	public void setApp_user_id(int app_user_id) {
		this.app_user_id = app_user_id;
	}
	
	public String getFirst_name() {
		return first_name;
	}
	
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public String getLast_name() {
		return last_name;
	}
	
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public int getSchool_id() {
		return school_id;
	}
	
	public void setSchool_id(int school_id) {
		this.school_id = school_id;
	}
	
	public int getClass_id() {
		return class_id;
	}
	
	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}
	
	public int getEduLevel() {
		return eduLevel;
	}
	
	public void setEduLevel(int eduLevel) {
		this.eduLevel = eduLevel;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	}
}
