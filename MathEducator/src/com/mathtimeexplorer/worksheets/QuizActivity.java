package com.mathtimeexplorer.worksheets;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.misc.Constants;
import com.mathtimeexplorer.main.OptionActivity;
import com.mathtimeexplorer.main.User;

public class QuizActivity extends Activity {

	Fragment previousFrag;
	int questionNo = 0;
	ArrayList<QuizFragment> fragmentList;
	ArrayList<Question> questionList;
	int[] answers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Log.e("Topic",Integer.toString(extras.getInt(Constants.TOPIC)));
		}



		questionList = GetQuestions();
		for(int i=0;i<questionList.size();i++){
			Log.e("Question",(questionList.get(i).getQuestion()));
		}
		fragmentList = new ArrayList<QuizFragment>();

		for(int i=0;i<questionList.size();i++){
			QuizFragment fragment = QuizFragment.newInstance(questionList.get(i));
			fragmentList.add(fragment);
		}

		answers = new int[questionList.size()];
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, fragmentList.get(0));
		ft.commit();
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
						answers[questionNo-1] = 1;
					else if(optionB.isChecked())
						answers[questionNo-1] = 2;
					else if(optionC.isChecked())
						answers[questionNo-1] = 3;
					else if(optionD.isChecked())
						answers[questionNo-1] = 4;


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
				questionNo--;
				if(questionNo==-1){
					questionNo=0;
				}
				else{
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();	
					fragmentTransaction.replace(R.id.fragment_container,fragmentList.get(questionNo));
					fragmentTransaction.commit();
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
					answers[questionNo] = 1;
				else if(optionB.isChecked())
					answers[questionNo] = 2;
				else if(optionC.isChecked())
					answers[questionNo] = 3;
				else if(optionD.isChecked())
					answers[questionNo] = 4;

				String empty = "";
				for(int i=0;i<answers.length;i++){
					Log.e("Answers",Integer.toString(answers[i]));
					if(answers[i]==0)
						empty+=(i+1)+",";
				}
				if(empty.length()>1){
					empty = empty.subSequence(0, empty.length()-1).toString();
					Toast.makeText(getApplicationContext(), "You have not completed questions "+empty+".",Toast.LENGTH_LONG).show();
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
			qn.setMark(questions.getString(8));
			qn.setQuizId(questions.getInt(10));
			qn.setSubtopicId(questions.getInt(11));

			questionList.add(qn);
			questions.moveToNext();
		}

		database.close();
		return questionList;
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Back is disabled.",Toast.LENGTH_SHORT).show();
	}
}
