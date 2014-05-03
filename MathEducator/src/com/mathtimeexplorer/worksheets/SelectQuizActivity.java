package com.mathtimeexplorer.worksheets;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.main.OptionActivity;
import com.mathtimeexplorer.main.SelectPracticeSetActivity;
import com.mathtimeexplorer.misc.Constants;
import com.mathtimeexplorer.misc.Quiz;

public class SelectQuizActivity extends ListActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectquiz);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Log.e("Topic",Integer.toString(extras.getInt(Constants.TOPIC)));
		}
		
		ArrayList<Quiz> quizzes= getQuiz();
		ArrayList<String> quizNames = new ArrayList<String>();
		
		for(int i=0;i<quizzes.size();i++){
			quizNames.add(quizzes.get(i).getQuizName());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, quizNames);
		setListAdapter(adapter);
		
		Button backBtn = (Button)findViewById(R.id.selectquizback);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), OptionActivity.class);
				Bundle extras = getIntent().getExtras();
				intent.putExtras(extras);
				startActivity(intent);
			}
		});

	}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Intent intent = new Intent(getBaseContext(), QuizActivity.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtras(extras);
		startActivity(intent);
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
}