package com.mathtimeexplorer.main;

<<<<<<< HEAD
import java.util.ArrayList;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.misc.Constants;
import com.mathtimeexplorer.worksheets.Question;
import com.mathtimeexplorer.worksheets.QuizFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PracticeSetActivity extends Activity {
	
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
=======
import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PracticeSetActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;
		setContentView(R.layout.activity_practiceset);
		LinearLayout ll = (LinearLayout) findViewById(R.id.practicesetlayout);
		FrameLayout fl = new FrameLayout(this);
		fl.setPadding(10, 10, 10, 10);
		LinearLayout questionFormat = new LinearLayout(this);
		questionFormat.setOrientation(LinearLayout.VERTICAL);
		
		
		
		DBAdapter database = new DBAdapter(getApplicationContext());
		try {
			database.openDataBase();
		}catch(SQLException e){
			Log.i("Database", e.toString());
		}
		Cursor questions = database.getQuestions();
		questions.moveToFirst();
		while(!questions.isAfterLast()){
		}
		
		
	}
	
	private void callProgressWindow(Context cx, Intent in, int evX, int evY) {

		final Intent intent = in;
		final Context context = cx;
		PopupWindow progressWindow = new PopupWindow(context);
		int stars = 3;
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setPadding(5, 5, 5, 5);
		ll.setGravity(Gravity.CENTER);
		for(int i=0;i<stars;i++){
			ImageView star = new ImageView(this);
			star.setImageResource(R.drawable.yellowstar);
			star.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			ll.addView(star);
		}
		for(int i=0;i<5-stars;i++){
			ImageView star2 = new ImageView(this);
			star2.setImageResource(R.drawable.blackstar);
			star2.setLayoutParams(new LinearLayout.LayoutParams(50,50));
			ll.addView(star2);
		}
		LinearLayout ll2 = new LinearLayout(this);
		ll2.setOrientation(LinearLayout.VERTICAL);
		ll2.setPadding(10, 10, 10, 10);
		ll2.setGravity(Gravity.CENTER);
		Button practiceBtn = new Button(this);
		practiceBtn.setText("Practice Set");
		Button tutorialBtn = new Button(this);
		tutorialBtn.setText("Tutorial");
		tutorialBtn.setTextSize(8);
		practiceBtn.setTextSize(8);
		practiceBtn.setLayoutParams(new LinearLayout.LayoutParams(200,50));
		tutorialBtn.setLayoutParams(new LinearLayout.LayoutParams(200,50));
		
		practiceBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(context, SelectPracticeSetActivity.class);
				intent2.putExtra("Subject", intent.getExtras().getString("Tutorial"));
				startActivity(intent2);
			}
		});
		
		tutorialBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
			}
		});
		
		
		ll2.addView(ll);
		ll2.addView(tutorialBtn);
		ll2.addView(practiceBtn);
		/*
		// Inflate the popup_layout.xml
		RelativeLayout viewGroup = (RelativeLayout) ((Activity) context).findViewById(R.id.progresslayout);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.activity_progress, viewGroup);

		Button practiceBtn = (Button) layout.findViewById(R.id.practiceBtn);
		Button tutorialBtn = (Button) layout.findViewById(R.id.tutorialBtn);
		 */
		progressWindow.setContentView(ll2);
		progressWindow.setWidth(400);
		progressWindow.setHeight(200);
		progressWindow.setFocusable(true);
		progressWindow.showAtLocation(ll, Gravity.NO_GRAVITY, evX+50, evY+150);
		
		/*practiceBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(context, SelectPracticeSetActivity.class);
				startActivity(intent2);
			}
		});

		tutorialBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
			}
		});
		*/
>>>>>>> branch 'master' of ssh://git@github.com/wendell1991/Project321
	}
}
