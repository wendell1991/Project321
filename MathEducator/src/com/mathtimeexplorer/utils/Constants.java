package com.mathtimeexplorer.utils;

// Stores all the constant values in the application
public final class Constants {
	
	// Static data for guest user
	public static final int GUEST_SCH_ID = 1;
	public static final int GUEST_EDU_LEVEL = 1;
	
	// Extras to pass and retrieve from previous or next activities 
	public static final String USER = "User";
	public static final String SUB_TOPIC = "subtopic";
	
	// Tag to pass which topic was selected to next activity
	public static final String TOPIC = "Topic";
	public static final String GENDER_MALE = "M";
	public static final String GENDER_GUEST = "G";
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	
	// JSON Response node for common tags
	public static final String JSON_SUCCESS = "success";
	public static final String JSON_RESULTS = "results";
	
	// URLS
	public static final String URL_LOGIN = "http://10.0.2.2/TimeExplorer/login.php";
	public static final String URL_RETRIEVE_TOPICS = "http://10.0.2.2/TimeExplorer/retrieve_topics.php";
	public static final String URL_RETRIEVE_NEWS = "http://10.0.2.2/TimeExplorer/retrieve_news.php";
	public static final String URL_VIEW_NEWS = "http://10.0.2.2/MathEducator/login/new_detail.php?new=";
	
	public static final String URL_CLASS_RESULT = "http://10.0.2.2/TimeExplorer/ranking_class_result.php";
	public static final String URL_SCHOOL_RESULT = "http://10.0.2.2/TimeExplorer/ranking_school_result.php";
	public static final String URL_QUIZ_NAMES = "http://10.0.2.2/TimeExplorer/ranking_quiz.php";
	
	public static final String URL_GETQUIZ = "http://10.0.2.2/TimeExplorer/getquizzes.php";
	public static final String URL_GETQUESTION = "http://10.0.2.2/TimeExplorer/getquizquestions.php";
	public static final String URL_INSERTQUIZRESULT = "http://10.0.2.2/TimeExplorer/insertquizresults.php";
	public static final String URL_GETPRACTICESETQUESTION = "http://10.0.2.2/TimeExplorer/getpracticesetquestions.php";
	public static final String URL_GETOTHERTOPIC = "http://10.0.2.2/TimeExplorer/getothertopics.php";
	
	public static final String URL_MANAGE_GAME_RESULT = "http://10.0.2.2/TimeExplorer/games_manage_result.php";
	public static final String URL_GAME_RESULT = "http://10.0.2.2/TimeExplorer/ranking_game_result.php";
	
	public static final String URL_RETRIEVE_TUT = "http://10.0.2.2/TimeExplorer/retrieve_tutorial_urls.php";
	public static final String URL_MANAGE_PROGRESS = "http://10.0.2.2/TimeExplorer/manage_tutorial.php";
	public static final String URL_UPDATE_PROGRESS = "http://10.0.2.2/TimeExplorer/update_tut_prog.php";
	
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	
	// JSON Response node names for login
	public static final String LOGIN_USER = "user";
	public static final String LOGIN_USER_ID = "user_id";
	public static final String LOGIN_FIRST_NAME = "first_name";
	public static final String LOGIN_LAST_NAME = "last_name";
	public static final String LOGIN_GENDER = "gender";
	public static final String LOGIN_SCHOOL_ID = "school_id";
	public static final String LOGIN_CLASS_ID = "class_id";
	public static final String LOGIN_EDULEVEL = "eduLevel";
	
	// JSON Response node names for retrieving quiz names
	public static final String TAG_QUIZ = "quiz";
	public static final String QUIZ_ID = "quiz_id";
	public static final String QUIZ_NAME = "quiz_name";
	
	// JSON Response node names for retrieving topic names
	public static final String TAG_TOPIC = "topic";
	public static final String TOPIC_NAME = "topic_name";
	
	// JSON Response node names for retrieving news
	public static final String TAG_NEWS = "news";
	public static final String NEWS_ID = "news_id";
	public static final String NEWS_TITLE = "news_title";
	public static final String NEWS_POST_DATE = "news_post_date";
	
	// JSON Response node names for retrieving tutorial & progress
	public static final String TAG_TUT = "tutorial";
	public static final String TUT_FILE_LOC = "file_location";
	public static final String TUT_PROGRESS = "progress";
	
	// Variables for swipe gesture-detector
	public static final int SWIPE_MIN_DISTANCE = 150;
	public static final int SWIPE_MAX_OFF_PATH = 100;
	public static final int SWIPE_THRESHOLD_VELOCITY = 100;
	
	// Usage for ranking activity
	public static final String TAB_CHOICE = "TAP";
	public static final String TAB_CLASS = "Class";
	public static final String TAB_SCHOOL = "School";
	public static final String TOPIC_COINCOIN = "Coin Coin";
	public static final String TOPIC_XGAME = "XGame";
	public static final String COINCOIN_TYPE = "3";
	public static final String XGAME_TYPE = "2";
	
	// Progress dialog values
	public static final String TITLE_GAME_LOADING = "Game Loading...";
	public static final String TITLE_GEN_SCORE = "Generating Score...";
	public static final String TITLE_VERIFY_USER = "Verifying user...";
	public static final String TITLE_LOADING_TUT = "Loading Tutorial...";
	public static final String TITLE_SAVING_PROG = "Saving Progress...";

	public static final String MESSAGE_PLEASE_WAIT = "Please wait.";
	
	// List of topics by default (P1 - P3)
	public static final String TOPIC_ARITH = "Arithmetic";
	public static final String TOPIC_FRACTION = "Fraction";
	public static final String TOPIC_MEASURE = "Measurement";
	
	// All the Logging tags
	public static final String LOG_LOADAPP = "LoadAppActivity";
	public static final String LOG_MAIN = "MainActivity";
	public static final String LOG_JSONPARSER = "JSONParser";
	public static final String LOG_DBADAPTER = "DBAdapter";
	public static final String LOG_RANKING = "RankingTab";
	public static final String LOG_COINCOIN = "CoinCoin";
	public static final String LOG_XGAME = "XGame";
	public static final String LOG_TUTORIAL = "TutorialActivity";
		
	private Constants(){
		//this prevents even the native class from calling
	    throw new AssertionError();
	}
}
