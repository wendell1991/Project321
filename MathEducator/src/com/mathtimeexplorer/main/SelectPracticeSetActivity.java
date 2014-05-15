package com.mathtimeexplorer.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Question;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPracticeSetActivity extends Activity{


	int topic = 0;
	User user;
	String subtopic;
	int difficulty;
	int stars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;
		setContentView(R.layout.activity_selectpracticeset);
		final TextView tv = (TextView) findViewById (R.id.practicesetsubject);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			user = extras.getParcelable(Constants.USER);
			subtopic = extras.getString("subtopic");
			stars = extras.getInt("stars");
			Log.e("topic",Integer.toString(topic));
		}
		tv.setText(subtopic);

		RelativeLayout layout = (RelativeLayout) findViewById (R.id.selectpracticesetbackground);
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

		}
		LinearLayout currentstarlayout = (LinearLayout) findViewById(R.id.currentstarslayout);
		LinearLayout obtainablestarlayout = (LinearLayout) findViewById(R.id.obtainablestarslayout);

		for(int i=0;i<stars;i++){
			ImageView star = new ImageView(this);
			star.setImageResource(R.drawable.yellowstar);
			star.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			currentstarlayout.addView(star);
		}
		for(int i=0;i<3-stars;i++){
			ImageView star2 = new ImageView(this);
			star2.setImageResource(R.drawable.blackstar);
			star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			currentstarlayout.addView(star2);
		}

		for(int i=0;i<1;i++){
			ImageView star2 = new ImageView(this);
			star2.setImageResource(R.drawable.yellowstar);
			star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			obtainablestarlayout.addView(star2);
		}
		for(int i=0;i<2;i++){
			ImageView star2 = new ImageView(this);
			star2.setImageResource(R.drawable.blackstar);
			star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			obtainablestarlayout.addView(star2);
		}

		RadioButton easyBtn = (RadioButton) findViewById(R.id.radioeasy);
		RadioButton normalBtn = (RadioButton) findViewById(R.id.radionormal);
		RadioButton hardBtn = (RadioButton) findViewById(R.id.radiohard);

		easyBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout obtainablestarlayout = (LinearLayout) findViewById(R.id.obtainablestarslayout);
				obtainablestarlayout.removeAllViews();
				for(int i=0;i<1;i++){
					ImageView star2 = new ImageView(getApplicationContext());
					star2.setImageResource(R.drawable.yellowstar);
					star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
					obtainablestarlayout.addView(star2);
				}
				for(int i=0;i<2;i++){
					ImageView star2 = new ImageView(getApplicationContext());
					star2.setImageResource(R.drawable.blackstar);
					star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
					obtainablestarlayout.addView(star2);
				}
			}
		});

		normalBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout obtainablestarlayout = (LinearLayout) findViewById(R.id.obtainablestarslayout);
				obtainablestarlayout.removeAllViews();
				for(int i=0;i<2;i++){
					ImageView star2 = new ImageView(getApplicationContext());
					star2.setImageResource(R.drawable.yellowstar);
					star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
					obtainablestarlayout.addView(star2);
				}
				for(int i=0;i<1;i++){
					ImageView star2 = new ImageView(getApplicationContext());
					star2.setImageResource(R.drawable.blackstar);
					star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
					obtainablestarlayout.addView(star2);
				}
			}
		});

		hardBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout obtainablestarlayout = (LinearLayout) findViewById(R.id.obtainablestarslayout);
				obtainablestarlayout.removeAllViews();
				for(int i=0;i<3;i++){
					ImageView star2 = new ImageView(getApplicationContext());
					star2.setImageResource(R.drawable.yellowstar);
					star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
					obtainablestarlayout.addView(star2);
				}
			}
		});

		Button practiceback = (Button) findViewById(R.id.practiceback);
		Button practicestart = (Button) findViewById(R.id.practicestart);


		practiceback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(context, OptionActivity.class);
				Bundle extras = getIntent().getExtras();
				in.putExtras(extras);
				startActivity(in);
			}
		});

		practicestart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extras = getIntent().getExtras();
				RadioGroup rbg = (RadioGroup)findViewById(R.id.difficulty);
				int radioID = rbg.getCheckedRadioButtonId();
				if(radioID == R.id.radioeasy){
					difficulty = 1;
					extras.putInt("Difficulty", 1);
				}
				else if(radioID == R.id.radionormal){
					difficulty = 2;
					extras.putInt("Difficulty", 2);
				}
				else if(radioID == R.id.radiohard){
					difficulty = 3;
					extras.putInt("Difficulty", 3);
				}
				if(user!=null)
					new GetQuestions(subtopic,user.getEduLevel(),difficulty).execute();
				else
					new GetQuestions(subtopic,1,difficulty).execute();
			}
		});
	}


	class GetQuestions extends AsyncTask<String, String, String> {

		private String subTopicName;
		private int eduLevel;
		private int difficulty;
		private int success = 0;

		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Retrieving Questions...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";

		private JSONObject json = null;

		public GetQuestions (String subTopicName, int eduLevel, int difficulty) {
			this.subTopicName = subTopicName;
			this.eduLevel = eduLevel;
			this.difficulty = difficulty;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SelectPracticeSetActivity.this);
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
			params.add(new BasicNameValuePair("subtopicname", subTopicName));
			params.add(new BasicNameValuePair("edulevel", Integer.toString(eduLevel)));
			params.add(new BasicNameValuePair("difficulty", Integer.toString(difficulty)));


			json = jsonParser.makeHttpRequest(Constants.URL_GETPRACTICESETQUESTION, "POST", params);
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
				if(success!=1 || questionsList.size()==0){
					Toast.makeText(getApplicationContext(), "Unable to retrieve Practice Set.",
							Toast.LENGTH_SHORT).show();
				}
				else{
					Intent intent = new Intent(getBaseContext(),PracticeSetActivity.class);
					Bundle extras = getIntent().getExtras();
					extras.putInt("time_limit", timeLimit);
					extras.putParcelableArrayList("questions", questionsList);
					extras.putInt("difficulty", difficulty);
					Log.e("after parcelable","test");
					intent.putExtras(extras);
					startActivity(intent);
				}

			} else {
				SelectPracticeSetActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),"Couldn't retrieve questions.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			// dismiss the dialog once done
			pDialog.dismiss();
		}


	}
}

