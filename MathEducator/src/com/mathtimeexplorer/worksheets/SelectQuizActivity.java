package com.mathtimeexplorer.worksheets;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.main.MainActivity;
import com.mathtimeexplorer.main.OptionActivity;
import com.mathtimeexplorer.main.SelectPracticeSetActivity;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Quiz;

public class SelectQuizActivity extends ListActivity {

	final ArrayList<Quiz> quizzes = new ArrayList<Quiz>();
	int topic = 0;
	User user;
	String topicname="Arithmetic";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectquiz);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			user = extras.getParcelable(Constants.USER);
			topicname = extras.getString("topicname");
			Log.e("topicname",topicname);
		}
		RelativeLayout layout = (RelativeLayout) findViewById (R.id.selectquizbackground);
		if(topic == R.drawable.arithmetic){
			layout.setBackgroundResource(R.drawable.arithmeticbackground);
			layout.setTag(R.drawable.arithmeticbackground);

		}
		else if(topic == R.drawable.fraction){
			layout.setBackgroundResource(R.drawable.fractionbackground);
			layout.setTag(R.drawable.fractionbackground);

		}else if(topic == R.drawable.measurement){
			layout.setBackgroundResource(R.drawable.measurementbackground);
			layout.setTag(R.drawable.measurementbackground);
		}else if(topic == R.drawable.othertopic){
			layout.setBackgroundResource(R.drawable.othertopicbackground);
			layout.setTag(R.drawable.othertopicbackground);
		}

		ListView lv = getListView();
		lv.setBackgroundColor(Color.TRANSPARENT);
		//Get Quizzes
		String edulevel = Integer.toString(user.getEduLevel());
		String schoolId = Integer.toString(user.getSchool_id());
		if (topicname.isEmpty() == false && edulevel.isEmpty() == false) {
			new GetQuizzes(topicname, edulevel,schoolId).execute();
		} 
		else {
			Toast.makeText(getApplicationContext(), "Topic Name and EduLevel must not be empty!",
					Toast.LENGTH_SHORT).show();
		}	
		Button backBtn = (Button)findViewById(R.id.selectquizback);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.e("position",Integer.toString(position));
		new GetQuestions(quizzes.get(position).getQuizId()).execute();
	}

	private ArrayList<Quiz> getQuiz(){

		ArrayList<Quiz> quizList = new ArrayList<Quiz>();
		DBAdapter database = new DBAdapter(getApplicationContext());
		try {
			database.openDataBase();
		}catch(SQLException e){
			Log.i("Database", e.toString());
		}
		Cursor quizzes = database.getQuiz();
		quizzes.moveToFirst();
		while(!quizzes.isAfterLast()){

			Quiz quiz = new Quiz();
			quiz.setQuizId(quizzes.getInt(0));
			quiz.setQuizName(quizzes.getString(1));

			quizList.add(quiz);
			quizzes.moveToNext();
		}

		database.close();
		return quizList;
	}

	class GetQuizzes extends AsyncTask<String, String, String> {

		private String topicname;
		private String edulevel;
		private String schoolId;
		private int success = 0;

		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Getting Quizzes...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";

		private JSONObject json = null;

		public GetQuizzes (String topicname, String edulevel, String schoolId) {
			this.topicname = topicname;
			this.edulevel = edulevel;
			this.schoolId = schoolId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SelectQuizActivity.this);
			pDialog.setTitle(DIALOG_LOGIN_TITLE);
			pDialog.setMessage(DIALOG_LOGIN_MESSAGE);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			params.add(new BasicNameValuePair("topicname", topicname));
			params.add(new BasicNameValuePair("edulevel", edulevel));
			params.add(new BasicNameValuePair("schoolId", schoolId));

			json = jsonParser.makeHttpRequest(Constants.URL_GETQUIZ, "POST", params);
			try {
				success = json.getInt(Constants.JSON_SUCCESS);
				Log.e("Success", Integer.toString(success));
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// If success = 1, user is authenticated with the server successfully

			if (success == 1) {
				try {
					// Retrieve the user object from JSON and save it to class user
					JSONArray obj = json.getJSONArray("quiz");
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					DateFormat dateFormat = formatter;
					Date date = new Date();
					Log.e("Date",dateFormat.format(date));
					Log.e("Obj length", Integer.toString(obj.length()));
					
					for(int i=0;i<obj.length();i++){
						Quiz quiz = new Quiz();
						JSONObject quiz2 = obj.getJSONObject(i);
						quiz.setQuizId(quiz2.getInt("quiz_id"));
						quiz.setQuizName(quiz2.getString("quiz_name"));
						quiz.setPassMark(quiz2.getInt("pass_mark"));
						quiz.setTotalScore(quiz2.getInt("total_score"));
						quiz.setTimeLimit(quiz2.getInt("time_limit"));
						quiz.setDeadline(quiz2.getString("deadline"));
						quiz.setQuizType(quiz2.getInt("quiz_type"));
						quiz.setSubTopicId(quiz2.getInt("sub_topic_id"));
						quiz.setSchoolId(quiz2.getInt("school_id"));
						quiz.setQuestionList(quiz2.getString("question_list"));
						quiz.setNoOfQuestions(quiz2.getInt("no_of_qns"));
						quiz.setStatus(quiz2.getInt("status"));
						Log.e("quiz",quiz.getQuizName()+quiz.getQuizId());
					 
						try {
					 
							Date quizdate = formatter.parse(quiz.getDeadline());
							Log.e("Date",Integer.toString(date.compareTo(quizdate)));
							if(date.compareTo(quizdate)>0){
								Log.e("Date is After",Integer.toString(date.compareTo(quizdate)));
							}
							else if(quiz.getQuestionList().equals("")){
								
							}
								else
								quizzes.add(quiz);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}

					// Save user into the local database


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i(Constants.LOG_MAIN, e.toString());
				}
				SelectQuizActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.e("quizzes", Integer.toString(quizzes.size()));
						ArrayList<String> quizNames = new ArrayList<String>();

						for(int i=0;i<quizzes.size();i++){
							quizNames.add(quizzes.get(i).getQuizName());
						}
						
					    View footerView = getLayoutInflater().inflate(R.layout.rowlayout, null);
						LinearLayout layout = (LinearLayout) footerView.findViewById (R.id.rowbackground);
						layout.setBackgroundResource(R.drawable.fractionbackgroundcolor);
						layout.setTag(R.drawable.fractionbackgroundcolor);
						
						String[] arr = quizNames.toArray(new String[quizNames.size()]);
						for(int i=0;i<arr.length;i++){
							Log.e("test",arr[i]);
						}
						MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), arr, topic);
						setListAdapter(adapter);


					}
				});

			} else {
				SelectQuizActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),"Unable to retrieve quizzes.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			// dismiss the dialog once done
			pDialog.dismiss();
		}
	}

	class GetQuestions extends AsyncTask<String, String, String> {

		private int quizId;
		private int success = 0;

		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Retrieving Questions...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";

		private JSONObject json = null;

		public GetQuestions (int quizId) {
			this.quizId = quizId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SelectQuizActivity.this);
			pDialog.setTitle(DIALOG_LOGIN_TITLE);
			pDialog.setMessage(DIALOG_LOGIN_MESSAGE);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			params.add(new BasicNameValuePair("quiz_id", Integer.toString(quizId)));

			json = jsonParser.makeHttpRequest(Constants.URL_GETQUESTION, "POST", params);
			try {
				success = json.getInt(Constants.JSON_SUCCESS);
				Log.e("Success for questions", Integer.toString(success));
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// If success = 1, user is authenticated with the server successfully
			ArrayList<Question> questionsList = new ArrayList<Question>();
			int timeLimit = 0;
			Log.e("Success2", Integer.toString(success));
			if (success == 1) {
				try {
					// Retrieve the user object from JSON and save it to class user
					Log.e("Before Array","test");
					
					JSONArray quiz = json.getJSONArray("quiz");
					JSONObject quiz2 = quiz.getJSONObject(0);
					timeLimit = quiz2.getInt("time_limit");
					Log.e("Time",Integer.toString(timeLimit));
					
					JSONArray obj = json.getJSONArray("questions");
					Log.e("test","questions array");
					for(int i=0;i<obj.length();i++){
						Log.e("test", Integer.toString(i));
						Question question = new Question();
						JSONObject question2 = obj.getJSONObject(i);
						question.setQuestionId((question2.getInt("question_id")));
						question.setQuestion((question2.getString("question")));
						question.setA((question2.getString("a")));
						question.setB((question2.getString("b")));
						question.setC((question2.getString("c")));
						question.setD((question2.getString("d")));
						question.setAnswer((question2.getString("answer")));
						question.setExplanation((question2.getString("explanation")));
						question.setMark((question2.getInt("mark")));
						question.setSubtopicId((question2.getInt("sub_topic_id")));
						question.setDifficulty((question2.getInt("difficulty")));


						Log.e("question",question.getQuestion()+question.getQuestionId());
						questionsList.add(question);
					}

					// Save user into the local database


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i(Constants.LOG_MAIN, e.toString());
				}
				
				Intent intent = new Intent(getBaseContext(), QuizActivity.class);
				Bundle extras = getIntent().getExtras();
				Log.e("Quiz_ID",Integer.toString(quizId));
				extras.putInt("quiz_id", quizId);
				extras.putInt("time_limit", timeLimit);
				extras.putParcelableArrayList("questions", questionsList);
				Log.e("after parcelable","test");
				intent.putExtras(extras);
				startActivity(intent);
				
			} else {
				SelectQuizActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),"Unable to retrieve questions.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			// dismiss the dialog once done
			pDialog.dismiss();
		}
		
		
	}
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		  private final Context context;
		  private final String[] values;
		  private final int topic;

		  public MySimpleArrayAdapter(Context context, String[] values, int topic) {
		    super(context, R.layout.rowlayout, values);
		    this.context = context;
		    this.values = values;
		    this.topic = topic;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.label);
		    textView.setText(values[position]);
		    LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.rowbackground);
		    
		    if(topic == R.drawable.arithmetic){
				layout.setBackgroundResource(R.drawable.arithmeticbackgroundcolor);
				layout.setTag(R.drawable.arithmeticbackgroundcolor);

			}
			else if(topic == R.drawable.fraction){
				layout.setBackgroundResource(R.drawable.cardbackground);
				layout.setTag(R.drawable.cardbackground);

			}else if(topic == 0){
				layout.setBackgroundResource(R.drawable.arithmeticbackgroundcolor);
				layout.setTag(R.drawable.arithmeticbackgroundcolor);

			}
		    return rowView;
		  }
		} 
}