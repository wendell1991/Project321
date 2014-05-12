package com.mathtimeexplorer.worksheets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.main.MainActivity;
import com.mathtimeexplorer.main.OptionActivity;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;

public class QuizActivity extends Activity {

	Fragment previousFrag;
	int questionNo = 0;
	ArrayList<QuizFragment> fragmentList;
	ArrayList<Question> questionList;
	String[] answers;
	int topic = 0;

	String output;
	MyCount counter;
	int length;
	long seconds;
	TextView timerView;
	int timeLimit;
	
	int quizId;
	int classId;
	int schoolId;
	int userId;
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	String date = dateFormat.format(new Date());
	int score;
	
	User user;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			timeLimit = extras.getInt("time_limit");
			user = extras.getParcelable(Constants.USER);
			Log.e("Topic",Integer.toString(extras.getInt(Constants.TOPIC)));
			Log.e("quiz_id",Integer.toString(extras.getInt("quiz_id")));
			Log.e("time_limit",Integer.toString(extras.getInt("time_limit")));
		}

		quizId = extras.getInt("quiz_id");
		classId = user.getClass_id();
		schoolId = user.getSchool_id();
		userId = user.getApp_user_id();
		date = "06/06/2014";
		
		
		RelativeLayout layout = (RelativeLayout) findViewById (R.id.quizbackground);
		if(topic == R.drawable.arithmetic){
			layout.setBackgroundResource(R.drawable.arithmeticbackground);
			layout.setTag(R.drawable.arithmeticbackground);
		}
		else if(topic == R.drawable.fraction){
			layout.setBackgroundResource(R.drawable.fractionbackground);
			layout.setTag(R.drawable.fractionbackground);
		}else if(topic == R.drawable.othertopic){
			layout.setBackgroundResource(R.drawable.othertopicbackground);
			layout.setTag(R.drawable.othertopicbackground);
		}else if(topic == R.drawable.measurement){
			layout.setBackgroundResource(R.drawable.measurementbackground);
			layout.setTag(R.drawable.measurementbackground);
		}

		questionList = extras.getParcelableArrayList("questions");
		for(int i=0;i<questionList.size();i++){
			Log.e("Question",(questionList.get(i).getQuestion()));
		}
		fragmentList = new ArrayList<QuizFragment>();

		for(int i=0;i<questionList.size();i++){
			QuizFragment fragment = QuizFragment.newInstance(questionList.get(i));
			fragmentList.add(fragment);
		}

		answers = new String[questionList.size()];

		for(int i=0;i<answers.length;i++)
			answers[i] = "";

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, fragmentList.get(0));
		ft.commit();

		timerView=(TextView)findViewById(R.id.quizTimer);

		if(timeLimit>0){
			counter = new MyCount (60000*timeLimit,1000);
			counter.start();
		}

		Button nextBtn = (Button) findViewById(R.id.nxtQnBtn);
		Button prevBtn = (Button) findViewById(R.id.prevQnBtn);
		Button finishBtn = (Button) findViewById(R.id.finishQnBtn);

		nextBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				questionNo++;
				if(questionNo>=questionList.size()){
					questionNo=questionList.size()-1;
				}
				else{
					RadioButton optionA = (RadioButton)fragmentList.get(questionNo-1).getView().findViewById(R.id.optionA);
					RadioButton optionB = (RadioButton)fragmentList.get(questionNo-1).getView().findViewById(R.id.optionB);
					RadioButton optionC = (RadioButton)fragmentList.get(questionNo-1).getView().findViewById(R.id.optionC);
					RadioButton optionD = (RadioButton)fragmentList.get(questionNo-1).getView().findViewById(R.id.optionD);

					if(optionA.isChecked())
						answers[questionNo-1] = optionA.getText().toString();
					else if(optionB.isChecked())
						answers[questionNo-1] = optionB.getText().toString();
					else if(optionC.isChecked())
						answers[questionNo-1] = optionC.getText().toString();
					else if(optionD.isChecked())
						answers[questionNo-1] = optionD.getText().toString();


					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();	
					fragmentTransaction.replace(R.id.fragment_container,fragmentList.get(questionNo));
					fragmentTransaction.commit();
				}
			}
		});

		prevBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				RadioButton optionA = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionA);
				RadioButton optionB = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionB);
				RadioButton optionC = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionC);
				RadioButton optionD = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionD);

				if(optionA.isChecked())
					answers[questionNo] = optionA.getText().toString();
				else if(optionB.isChecked())
					answers[questionNo] = optionB.getText().toString();
				else if(optionC.isChecked())
					answers[questionNo] = optionC.getText().toString();
				else if(optionD.isChecked())
					answers[questionNo] = optionD.getText().toString();

				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();	
				if(questionNo==0)
					fragmentTransaction.replace(R.id.fragment_container,fragmentList.get(questionNo));
				else
					fragmentTransaction.replace(R.id.fragment_container,fragmentList.get(questionNo-1));
				fragmentTransaction.commit();

				if(questionNo==0){

				}
				else{
					questionNo--;
				}
			}
		});

		finishBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RadioButton optionA = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionA);
				RadioButton optionB = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionB);
				RadioButton optionC = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionC);
				RadioButton optionD = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionD);

				if(optionA.isChecked())
					answers[questionNo] = optionA.getText().toString();
				else if(optionB.isChecked())
					answers[questionNo] = optionB.getText().toString();
				else if(optionC.isChecked())
					answers[questionNo] = optionC.getText().toString();
				else if(optionD.isChecked())
					answers[questionNo] = optionD.getText().toString();

				String empty = "";
				for(int i=0;i<answers.length;i++){
					Log.e("Answers",answers[i]);
					if(answers[i]=="")
						empty+=(i+1)+",";
				}
				if(empty.length()>1){
					empty = empty.subSequence(0, empty.length()-1).toString();
					Toast.makeText(getApplicationContext(), "You have not completed questions "+empty+".",Toast.LENGTH_LONG).show();
				}
				else{
					submitQuiz();
				}
			}
		});
	}



	public String formatTime(long millis) 
	{
		output = "";
		seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours=minutes/ 60;

		seconds = seconds % 60;
		minutes = minutes % 60;
		hours=hours%60;

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);
		String hoursD=String.valueOf(hours);

		if (seconds < 10)
			secondsD = "0" + seconds;
		if (minutes < 10)
			minutesD = "0" + minutes;

		if (hours < 10)
			hoursD = "0" + hours;

		output = hoursD+" : "+minutesD + " : " + secondsD;

		return output;
	}

	public class MyCount extends CountDownTimer 
	{
		Context mContext;

		public MyCount(long millisInFuture, long countDownInterval) 
		{
			super(millisInFuture, countDownInterval);
		}


		public void onTick (long millisUntilFinished) 
		{
			timerView.setText ( formatTime(millisUntilFinished));

		}

		public void onFinish() {

			RadioButton optionA = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionA);
			RadioButton optionB = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionB);
			RadioButton optionC = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionC);
			RadioButton optionD = (RadioButton)fragmentList.get(questionNo).getView().findViewById(R.id.optionD);

			if(optionA.isChecked())
				answers[questionNo] = optionA.getText().toString();
			else if(optionB.isChecked())
				answers[questionNo] = optionB.getText().toString();
			else if(optionC.isChecked())
				answers[questionNo] = optionC.getText().toString();
			else if(optionD.isChecked())
				answers[questionNo] = optionD.getText().toString();


			submitQuiz();

		}

	}
	
	public void submitQuiz(){
		score = 0;
		for(int i=0;i<questionList.size();i++){
			String answer = questionList.get(i).getAnswer();
			String hisanswer = answers[i];
			if(answer.equals(hisanswer))
				score+=questionList.get(i).getMark();
		}
		Log.e("Score", Integer.toString(score));
		
		
		new SubmitResults(quizId,userId,score,date,schoolId,classId).execute();

	}

	
	class SubmitResults extends AsyncTask<String, String, String> {

		private int quizId;
		private int score;
		private int schoolId;
		private int classId;
		private int userId;
		private String date;


		private int success = 0;

		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Submitting Results...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";

		private JSONObject json = null;

		public SubmitResults (int quizId,int userId, int score, String date, int schoolId, int classId) {
			this.quizId = quizId;
			this.score = score;
			this.userId = userId;
			this.date = date;
			this.schoolId = schoolId;
			this.classId = classId;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(QuizActivity.this);
			pDialog.setTitle(DIALOG_LOGIN_TITLE);
			pDialog.setMessage(DIALOG_LOGIN_MESSAGE);
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
			
			PopupWindow progressWindow = new PopupWindow(getApplicationContext());
			LinearLayout ll2 = new LinearLayout(getApplicationContext());
			ll2.setOrientation(LinearLayout.VERTICAL);
			ll2.setPadding(10, 10, 10, 10);
			ll2.setGravity(Gravity.CENTER);
			TextView tv = new TextView(getApplicationContext());
			tv.setText("You scored: "+score);
			Button ok = new Button(getApplicationContext());
			ok.setText("OK");
			ok.setTextSize(8);
			tv.setTextSize(8);
			tv.setTextColor(Color.BLACK);
			ok.setLayoutParams(new LinearLayout.LayoutParams(200,50));
			tv.setLayoutParams(new LinearLayout.LayoutParams(200,50));

			ok.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(topic == R.drawable.othertopic){
						Intent intent = new Intent(getBaseContext(), MainActivity.class);
						Bundle extras = getIntent().getExtras();
						intent.putExtras(extras);
						startActivity(intent);
						}
						else{
							Intent intent = new Intent(getBaseContext(), OptionActivity.class);
							Bundle extras = getIntent().getExtras();
							intent.putExtras(extras);
							startActivity(intent);
						}
				}
			});

			ll2.addView(tv);
			ll2.addView(ok);
			ll2.setBackgroundResource(R.drawable.cardbackground);

			progressWindow.setContentView(ll2);
			progressWindow.setWidth(400);
			progressWindow.setHeight(200);
			progressWindow.setFocusable(true);
			progressWindow.showAtLocation(findViewById(R.id.quizbackground), Gravity.CENTER, 0, 0);
			progressWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			    @Override
			    public void onDismiss() {
			    	if(topic == R.drawable.othertopic){
						Intent intent = new Intent(getBaseContext(), MainActivity.class);
						Bundle extras = getIntent().getExtras();
						intent.putExtras(extras);
						startActivity(intent);
						}
						else{
							Intent intent = new Intent(getBaseContext(), OptionActivity.class);
							Bundle extras = getIntent().getExtras();
							intent.putExtras(extras);
							startActivity(intent);
						}               
			    }
			});
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			params.add(new BasicNameValuePair("result", Integer.toString(score)));
			params.add(new BasicNameValuePair("app_user_id", Integer.toString(userId)));
			params.add(new BasicNameValuePair("quiz_id", Integer.toString(quizId)));
			params.add(new BasicNameValuePair("attempted_date", date));
			params.add(new BasicNameValuePair("school_id", Integer.toString(schoolId)));
			params.add(new BasicNameValuePair("class_id", Integer.toString(classId)));

			json = jsonParser.makeHttpRequest(Constants.URL_INSERTQUIZRESULT, "POST", params);
			try {
				success = json.getInt(Constants.JSON_SUCCESS);
				Log.e("Success for results", Integer.toString(success));
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// If success = 1, user is authenticated with the server successfully
			if (success == 1) {
				Log.e("Insert Success", "test");
			} else {
				QuizActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),"Couldn't submit results.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			// dismiss the dialog once done
			pDialog.dismiss();
		}
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		counter.cancel();
	}


	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Back is disabled.",Toast.LENGTH_SHORT).show();
	}
}
