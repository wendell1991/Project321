package com.mathtimeexplorer.ranking;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Quiz;

public class RankingTab extends Activity {
	
	private TableLayout rankTable;
	private Spinner topicSpinner, quizSpinner;
	
	private User user = null;
	private Context context = this;
	private JSONParser jsonParser = new JSONParser();
	private ArrayAdapter<String> quizAdapter;
	private ArrayList<String> topicNameList = new ArrayList<String>();
	private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
	private List<String> quizNames = new ArrayList<String>();
	
	private String whichTab;
	private static final String tmpTopic = "-- Select a topic --";
	private static final String tmpQuizName = "-- Select a quiz --";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_tab);
			
		Bundle extras = getIntent().getExtras();
		
		topicNameList = getIntent().getStringArrayListExtra(Constants.TOPIC);
		
		for (int i = 0; i < topicNameList.size(); i++) {
			Log.i(Constants.LOG_RANKING, "TOPIC NAME: " + (String) topicNameList.get(i));
		}
		
		if (extras != null) {
			whichTab = extras.getString(Constants.TAB_CHOICE);
	
			user = (User) extras.getParcelable(Constants.USER);
			if (user != null) {
				Log.i(Constants.LOG_RANKING, "ID:" + user.getApp_user_id());
			}
		}
		
		// Initialize the UIs
		init();

		topicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item = parent.getItemAtPosition(position).toString();
				
				Log.i(Constants.LOG_RANKING, "ITEM SELECTED: " + item);
				
				if (!item.equals(tmpTopic)) {
					if (item.equals(Constants.TOPIC_XGAME)) {
						Log.i(Constants.LOG_RANKING, "RETRIEVING RESULTS FOR XGAME");
						new RetrieveResults(user.getApp_user_id(), user.getClass_id(), 
								user.getSchool_id(), Constants.TOPIC_XGAME).execute();			
					} 
					else if (item.equals(Constants.TOPIC_COINCOIN)) {
						Log.i(Constants.LOG_RANKING, "RETRIEVING RESULTS FOR COINCOIN ");
						new RetrieveResults(user.getApp_user_id(), user.getClass_id(), 
								user.getSchool_id(), Constants.TOPIC_COINCOIN).execute();
					} 
					else {
						new RetrieveQuizNames(item, user.getSchool_id(), 
								user.getEduLevel()).execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
		
		quizSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item = parent.getItemAtPosition(position).toString();
				
				if (!item.equals(tmpQuizName)) {
					int quizId = 0;
					Quiz quiz;
					// Find the quiz index which the user had selected
					for (int i = 0; i < quizList.size(); i++) {
						quiz = (Quiz) quizList.get(i);
						if (item.equals(quiz.getQuizName())) {
							quizId = quiz.getQuizId();
						}
					}
					new RetrieveResults(user.getApp_user_id(), user.getClass_id(), 
							user.getSchool_id(), quizId).execute();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	class RetrieveResults extends AsyncTask<String, String, String> {
		
		private int userId;
		private int classId;
		private int schoolId;
		private int quizId = -1;
		private String resultType = "";
		private RankingResult rankResult;
		
		public RetrieveResults(int userId, int classId, int schoolId, String resultType) {
			this.userId = userId;
			this.classId = classId;
			this.schoolId = schoolId;
			this.resultType = resultType;
			
			quizSpinner.setVisibility(View.INVISIBLE);
		}
		
		public RetrieveResults(int userId, int classId, int schoolId, int quizId) {
			this.userId = userId;
			this.classId = classId;
			this.schoolId = schoolId;
			this.quizId = quizId;
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the GET request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
			
			if (quizId != -1) {				
				params.add(new BasicNameValuePair("quizid", String.valueOf(quizId)));
			}
            
            JSONObject json = null;
            rankResult = new RankingResult();
            
            // GET request depending on which tab is selected
            if (whichTab.equals(Constants.TAB_CLASS)) {
            	params.add(new BasicNameValuePair("classid", String.valueOf(classId)));
            	json = jsonParser.makeHttpRequest(Constants.URL_CLASS_RESULT, 
            			Constants.HTTP_GET, params);
            } else {
            	if (resultType.isEmpty() == false) {
            		if (resultType.equals(Constants.TOPIC_XGAME)) {
            			params.add(new BasicNameValuePair("type", Constants.XGAME_TYPE));
            		} else {
            			params.add(new BasicNameValuePair("type", Constants.COINCOIN_TYPE));
            		}
            		params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
            		json = jsonParser.makeHttpRequest(Constants.URL_GAME_RESULT,
                    		Constants.HTTP_GET, params);
            	} else {
            		params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
                	json = jsonParser.makeHttpRequest(Constants.URL_SCHOOL_RESULT, 
                			Constants.HTTP_GET, params);
            	}	
            }
            rankResult.getRankingResults(json);
            
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// No results
			if (rankResult.getSuccess() == 0) {
				// Display no result found alert dialog box
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				
				builder
				.setCancelable(false)
				.setTitle(R.string.noResultFoundTitle)
				.setMessage(R.string.noResultFoundMsg)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub					
						// User quits the game, returns to previous activity
						dialog.cancel();					
					}
				});
			
			// Creates the dialog
			AlertDialog dialog = builder.create();
			dialog.show();
			
			} else {
				rankResult.setRankTableResults(rankTable);
			}		
		}
	}
	
	class RetrieveQuizNames extends AsyncTask<String, String, String> {
		
		private String topicSelected;
		private int school_id;
		private int eduLevel;
		
		public RetrieveQuizNames (String topicSelected, int school_id, int eduLevel) {
			this.topicSelected = topicSelected;
			this.school_id = school_id;
			this.eduLevel = eduLevel;
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the GET request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("topicname", topicSelected));
			params.add(new BasicNameValuePair("schoolid", String.valueOf(school_id)));
            params.add(new BasicNameValuePair("edulevel", String.valueOf(eduLevel)));  
            
			JSONObject json = jsonParser.makeHttpRequest(
					Constants.URL_QUIZ_NAMES, Constants.HTTP_GET, params);
			
			try{
				// Create a new set of Quiz names				
				quizNames = new ArrayList<String>(); 
				quizNames.add(tmpQuizName);
				
				// Get success tag and checks whether it is 1
				int success = json.getInt(Constants.JSON_SUCCESS);
				if (success == 1) {			
					Quiz quiz;
					JSONObject obj;
					String quizName = "";
					
					quizList = new ArrayList<Quiz>();
					JSONArray resultList = json.getJSONArray(Constants.TAG_QUIZ);
					
					// Loops the results and saves the result into Quiz class 
					for (int i = 0; i < resultList.length(); i++) {
						obj = resultList.getJSONObject(i);
						quiz = new Quiz();
						quizName = obj.getString(Constants.QUIZ_NAME);
						quiz.setQuizId(obj.getInt(Constants.QUIZ_ID));
						quiz.setQuizName(quizName);
						quizNames.add(quizName);
						quizList.add(quiz);
					}
				}
			} catch (JSONException e) {
				Log.i(Constants.LOG_RANKING, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// Updates list of items in the quiz spinner
			runOnUiThread(new Runnable() {
                public void run() {
                	addItemsToQuizSpinner();
                }
			});
		}
	}
	
	
	private void addItemsToQuizSpinner() {
		quizAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quizNames);
		quizAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	quizSpinner.setAdapter(quizAdapter);
    	quizSpinner.setVisibility(View.VISIBLE);
	}
	
	private void init() { 
		topicSpinner = (Spinner) findViewById(R.id.topicSpinner);
		quizSpinner = (Spinner) findViewById(R.id.quizSpinner);
		rankTable = (TableLayout) findViewById(R.id.rankTable);
		
		ArrayAdapter<String> topicAdapter = null;
		
		if (whichTab.equals(Constants.TAB_SCHOOL)) {
			topicNameList.add(Constants.TOPIC_XGAME);
			topicNameList.add(Constants.TOPIC_COINCOIN);
		} 
		
	    topicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, topicNameList);
		
		topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		topicSpinner.setAdapter(topicAdapter);
	}
}
