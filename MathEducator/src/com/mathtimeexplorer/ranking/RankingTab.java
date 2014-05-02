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
import android.widget.TableRow;
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
	private static String TAG_SUCCESS = "success";
	private static String URL_QUIZZES = "http://10.0.2.2/TimeExplorer/ranking_quiz.php";
	
	// Node names for retrieving results
	private static String TAG_RESULTS = "results";
	private static String TAG_RANK = "rank";
	private static String TAG_FIRST_NAME = "first_name";
	private static String TAG_LAST_NAME = "last_name";
	private static String TAG_RESULT = "result";
	
	private static String URL_CLASS_RESULT = "http://10.0.2.2/TimeExplorer/ranking_class_result.php";
	private static String URL_SCHOOL_RESULT = "http://10.0.2.2/TimeExplorer/ranking_school_result.php";
	
	private JSONParser jsonParser = new JSONParser();
	private JSONArray resultList = null;
	
	private ArrayAdapter<String> quizAdapter;
	private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
	private List<String> quizNames = new ArrayList<String>();
	
	private String whichTab;
	private static final String tmpTopic = "-- Select a topic --";
	private static final String tmpQuizName = "<-- Select a quiz -->";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_tab);
		
		// Initialize the UIs
		init();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			whichTab = extras.getString(Constants.TAB_CHOICE);
		}
		
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
							Integer.valueOf(2), Integer.valueOf(1)).execute();
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
		private ArrayList<Ranking> rankingList;
		
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
            
            // POST request depending on which tab is selected
            if (whichTab.equals(Constants.TAB_CLASS)) {
            	params.add(new BasicNameValuePair("classid", String.valueOf(classId)));
            	json = jsonParser.makeHttpRequest(URL_CLASS_RESULT, "POST", params);
            } else {
            	params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
            	json = jsonParser.makeHttpRequest(URL_SCHOOL_RESULT, "POST", params);
            }
            
            try{
				// Get success tag and checks whether it is 1
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					rankingList = new ArrayList<Ranking>();
					resultList = json.getJSONArray(TAG_RESULTS);
					
					// Log.i(Constants.LOG_RANKING, "RESULT LIST SIZE: " + resultList.length());
					
					Ranking rank;
					JSONObject obj;
					
					// Obtains the ranking results and save in the ranking-list
					for (int i = 0; i < resultList.length(); i++) {
						obj = resultList.getJSONObject(i);
						rank = new Ranking();
						rank.setRank(obj.getInt(TAG_RANK));
						rank.setFirst_name(obj.getString(TAG_FIRST_NAME));
						rank.setLast_name(obj.getString(TAG_LAST_NAME));
						rank.setResult(obj.getInt(TAG_RESULT));
						rankingList.add(rank);
					}
				}
            } catch (JSONException e) {
				Log.i(Constants.LOG_RANKING, e.toString());
            }
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			TableRow row;
			TextView view;
			Ranking rank;
			String spacing = "";
			int rankIndex = 0;
				
			// Static positions of each Views at each rows
			int rankViewIndex = 0;
			int nameViewIndex = 1;
			int resultViewIndex = 2;
			
            // Populates the ranking result on the table
            for (int i = 1; i <= 5; i++) {
            	row = (TableRow) rankTable.getChildAt(i);              		
            	if (rankingList.size() >= rankIndex) {
            		rank = (Ranking) rankingList.get(rankIndex);
            		
            		//Log.i(Constants.LOG_RANKING, "RANK: " + rank.getRank());
            		//Log.i(Constants.LOG_RANKING, "FIRST_NAME: " + rank.getFirst_name());
            		//Log.i(Constants.LOG_RANKING, "LAST_NAME: " + rank.getLast_name());
            		//Log.i(Constants.LOG_RANKING, "RESULT: " + rank.getResult());
            		
            		view = (TextView) row.getChildAt(rankViewIndex);
            		view.setText(String.valueOf(rank.getRank()));
                		
            		view = (TextView) row.getChildAt(nameViewIndex);
            		view.setText(String.valueOf(rank.getFirst_name() 
            				+ spacing + rank.getLast_name()));
                		
            		view = (TextView) row.getChildAt(resultViewIndex);
            		view.setText(String.valueOf(rank.getResult()));
            	} else {
            		break;
            	}
                rankIndex++;
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
            
			JSONObject json = jsonParser.makeHttpRequest(URL_QUIZZES, "POST", params);
			try{
				// Get success tag and checks whether it is 1
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {			
					Quiz quiz;
					JSONObject obj;
					String quizName = "";
					
					// Clear previous & creates new quizList, quizNameList
					quizList = new ArrayList<Quiz>();
					quizNames = new ArrayList<String>();
					resultList = json.getJSONArray(TAG_QUIZ);
					
					// Add the option <-- Select a quiz --> first 
					quizNames.add(tmpQuizName);
					
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
		
		ArrayAdapter<CharSequence> topicAdapter = ArrayAdapter.createFromResource(this, R.array.topic_arrays,
				android.R.layout.simple_spinner_dropdown_item);
		topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		topicSpinner.setPromptId(R.string.topic_prompt);
		topicSpinner.setAdapter(topicAdapter);
	}
}
