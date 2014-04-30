package com.mathtimeexplorer.worksheets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.matheducator.R;

public class QuizActivity extends Activity {
	
	Fragment previousFrag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		
		Button nextBtn = (Button) findViewById(R.id.nxtQnBtn);
		Button prevBtn = (Button) findViewById(R.id.prevQnBtn);
	
		nextBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Fragment quizFrag = new QuizFragment();			
				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();	
				fragmentTransaction.replace(R.id.fragment_quiz_container, quizFrag);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
		
		prevBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    getFragmentManager().popBackStack();
			}
		});
	}
}
