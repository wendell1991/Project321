package com.mathtimeexplorer.utils;

// Stores all the constant values in the application
public final class Constants {

	public static final String USER = "User";
	
	// Tag to pass which topic was selected to next activity
	public static final String TOPIC = "Topic";
	public static final String GENDER_MALE = "M";
	public static final String GENDER_GUEST = "G";
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	
	// JSON Response node for success tag
	public static final String JSON_SUCCESS = "success";
	
	// URLS
	public static final String URL_LOGIN = "http://10.0.2.2/TimeExplorer/login.php";
	
	// JSON Response node names for login
	public static final String LOGIN_USER = "user";
	public static final String LOGIN_USER_ID = "user_id";
	public static final String LOGIN_FIRST_NAME = "first_name";
	public static final String LOGIN_LAST_NAME = "last_name";
	public static final String LOGIN_GENDER = "gender";
	public static final String LOGIN_SCHOOL_ID = "school_id";
	public static final String LOGIN_CLASS_ID = "class_id";
	public static final String LOGIN_EDULEVEL = "eduLevel";
	
	// Variables for swipe gesture-detector
	public static final int SWIPE_MIN_DISTANCE = 150;
	public static final int SWIPE_MAX_OFF_PATH = 100;
	public static final int SWIPE_THRESHOLD_VELOCITY = 100;
	
	// Tab usage for ranking activity
	public static final String TAB_CHOICE = "TAP";
	public static final String TAB_CLASS = "Class";
	public static final String TAB_SCHOOL = "School";
	
	// Progress dialog values
	public static final String DIALOG_TITLE = "Game Loading...";
	public static final String DIALOG_MESSAGE = "Please wait.";
	
	// All the Logging tags
	public static final String LOG_LOADAPP = "LoadAppActivity";
	public static final String LOG_MAIN = "MainActivity";
	public static final String LOG_JSONPARSER = "JSONParser";
	public static final String LOG_DBADAPTER = "DBAdapter";
	public static final String LOG_RANKING = "RankingTab";
		
	private Constants(){
		//this prevents even the native class from calling
	    throw new AssertionError();
	}
}
