package com.mathtimeexplorer.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Question;
import com.mathtimeexplorer.worksheets.QuizFragment;

public class PracticeSetActivity extends Activity {
	
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
	
	int quizId=0;
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
			user = extras.getParcelable(Constants.USER);
			Log.e("Topic",Integer.toString(extras.getInt(Constants.TOPIC)));
			Log.e("quiz_id",Integer.toString(extras.getInt("quiz_id")));
		}

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
		}else if(topic == 0){
			layout.setBackgroundResource(R.drawable.arithmeticbackground);
			layout.setTag(R.drawable.arithmeticbackground);
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



	private ArrayList<Question> GetQuestions(){

		ArrayList<Question> questionList = new ArrayList<Question>();
		DBAdapter database = new DBAdapter(getApplicationContext());
		try {
			database.openDataBase();
		}catch(SQLException e){
			Log.i("Database", e.toString());
		}
		Cursor questions = database.getQuestions();
		questions.moveToFirst();
		while(!questions.isAfterLast()){

			Question qn = new Question();
			qn.setQuestionId(questions.getInt(0));
			qn.setQuestion(questions.getString(1));
			qn.setA(questions.getString(2));
			qn.setB(questions.getString(3));
			qn.setC(questions.getString(4));
			qn.setD(questions.getString(5));
			qn.setAnswer(questions.getString(6));
			qn.setMark(questions.getInt(8));
			qn.setQuizId(questions.getInt(10));
			qn.setSubtopicId(questions.getInt(11));

			questionList.add(qn);
			questions.moveToNext();
		}

		database.close();
		return questionList;
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
				score++;
		}
		Log.e("Score", Integer.toString(score));
		
		
		Intent intent = new Intent(getBaseContext(), OptionActivity.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtras(extras);
		startActivity(intent);

	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		counter.cancel();
	}


	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getBaseContext(), SelectPracticeSetActivity.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtras(extras);
		startActivity(intent);
	}
}