package com.mathtimeexplorer.ranking;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Quiz;

public class RankingTab extends Activity {
	
	private TableLayout rankTable;
	private Spinner topicSpinner, quizSpinner;
		
	// Node names for retrieving quizzes
	private static String TAG_QUIZ = "quiz";
	private static String TAG_QUIZ_ID = "quiz_id";
	private static String TAG_QUIZ_NAME = "quiz_name";
	
	private JSONParser jsonParser = new JSONParser();
	private ArrayAdapter<String> quizAdapter;
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
		if (extras != null) {
			whichTab = extras.getString(Constants.TAB_CHOICE);
		}
		
		// Initialize the UIs
		init();
		
		Log.i(Constants.LOG_RANKING, "TAB SELECTED: " + whichTab);
		
		topicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item = parent.getItemAtPosition(position).toString();
				if (!item.equals(tmpTopic)) {
					new RetrieveQuizNames(item).execute();
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
					new RetrieveResults(Integer.valueOf(1), Integer.valueOf(1), 
							Integer.valueOf(2), Integer.valueOf(38)).execute();
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
		private int quizId;
		private RankingResult rankResult;
		
		public RetrieveResults(int userId, int classId, int schoolId, int quizId) {
			this.userId = userId;
			this.classId = classId;
			this.schoolId = schoolId;
			this.quizId = quizId;
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
            params.add(new BasicNameValuePair("quizid", String.valueOf(quizId)));
            
            JSONObject json = null;
            rankResult = new RankingResult();
            
            // POST request depending on which tab is selected
            if (whichTab.equals(Constants.TAB_CLASS)) {
            	params.add(new BasicNameValuePair("classid", String.valueOf(classId)));
            	json = jsonParser.makeHttpRequest(Constants.URL_CLASS_RESULT, 
            			Constants.HTTP_GET, params);
            } else {
            	params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
            	json = jsonParser.makeHttpRequest(Constants.URL_SCHOOL_RESULT, 
            			Constants.HTTP_GET, params);
            }
            rankResult.getRankingResults(json);
            
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// No results
			if (rankResult.getSuccess() == 0) {
				TextView resultView = (TextView) findViewById(R.id.rankThreeName);
				resultView.setText(R.string.noResult);
			} else {
				rankResult.setRankTableResults(rankTable);
			}		
		}
	}
	
	class RetrieveQuizNames extends AsyncTask<String, String, String> {
		
		private String topicSelected;
		
		public RetrieveQuizNames (String topicSelected) {
			this.topicSelected = topicSelected;
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("topicname", topicSelected));
            params.add(new BasicNameValuePair("edulevel", String.valueOf("1")));  
            
			JSONObject json = jsonParser.makeHttpRequest(
					Constants.URL_QUIZ_NAMES, Constants.HTTP_GET, params);
			
			JSONArray resultList = null;
			
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
					resultList = json.getJSONArray(TAG_QUIZ);
					
					// Loops the results and saves the result into Quiz class 
					for (int i = 0; i < resultList.length(); i++) {
						obj = resultList.getJSONObject(i);
						quiz = new Quiz();
						quizName = obj.getString(TAG_QUIZ_NAME);
						quiz.setQuizId(obj.getInt(TAG_QUIZ_ID));
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
	}
	
	private void init() { 
		topicSpinner = (Spinner) findViewById(R.id.topicSpinner);
		quizSpinner = (Spinner) findViewById(R.id.quizSpinner);
		rankTable = (TableLayout) findViewById(R.id.rankTable);
		
		ArrayAdapter<CharSequence> topicAdapter = null;
		
		if (whichTab.equals(Constants.TAB_CLASS)) {
			topicAdapter = ArrayAdapter.createFromResource(this, R.array.class_topic_arrays, 
					android.R.layout.simple_spinner_dropdown_item);
		} else {
			topicAdapter = ArrayAdapter.createFromResource(this, R.array.school_topic_arrays, 
					android.R.layout.simple_spinner_dropdown_item);
		}
		topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		topicSpinner.setAdapter(topicAdapter);
	}
}
