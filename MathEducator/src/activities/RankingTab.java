package activities;

import java.util.ArrayList;
import java.util.List;

import objects.Quiz;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.matheducator.R;

import database.JSONParser;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;

public class RankingTab extends Activity {
	
	private Spinner topicSpinner, quizSpinner;
	private TableLayout rankTable;

	private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
	private List<String> quizNames = new ArrayList<String>();
	private ArrayAdapter<String> quizAdapter;
	
	// JSON Response node names
	private static String TAG_QUIZ = "quiz";
	private static String TAG_QUIZ_ID = "quiz_id";
	private static String TAG_QUIZ_NAME = "quiz_name";
	private static String TAG_SUCCESS = "success";
	private static String URL_RETRIEVE_QUIZ = "http://10.0.2.2/TimeExplorer/ranking_quiz.php";
	
	// Node names for retrieving results
	private static String TAG_RESULTS = "results";
	private static String TAG_RESULT = "result";
	private static String TAG_FIRST_NAME = "first_name";
	private static String TAG_LAST_NAME = "last_name";
	private static String URL_RETRIEVE_RESULT = "http://10.0.2.2/TimeExplorer/ranking_result.php";
	
	private JSONParser jsonParser = new JSONParser();
	private JSONArray resultList = null;
	
	private static final String TAG_LOG = "RankingTab";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_tab);
		
		// Initialize the UIs
		init();
		
		topicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item = parent.getItemAtPosition(position).toString();
				new RetrieveQuizNames(item).execute();
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
						Integer.valueOf(2), quizId).execute();
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
            params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
            params.add(new BasicNameValuePair("classid", String.valueOf(classId)));
            
            JSONObject json = jsonParser.makeHttpRequest(URL_RETRIEVE_RESULT, "POST", params);
            
            try{
				// Get success tag and checks whether it is 1
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					resultList = json.getJSONArray(TAG_RESULTS);
					for (int i = 0; i < resultList.length(); i++) {
						
					}
				}
            } catch (JSONException e) {
				Log.i(TAG_LOG, e.toString());
            }
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// Updates list of items in the quiz spinner
			runOnUiThread(new Runnable() {
                public void run() {
                	
                }
			});
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
            
			JSONObject json = jsonParser.makeHttpRequest(URL_RETRIEVE_QUIZ, "POST", params);
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
				Log.i(TAG_LOG, e.toString());
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
