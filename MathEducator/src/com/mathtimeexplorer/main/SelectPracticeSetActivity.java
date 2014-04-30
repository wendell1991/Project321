package com.mathtimeexplorer.main;

import com.example.matheducator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SelectPracticeSetActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;
		setContentView(R.layout.activity_selectpracticeset);
		final TextView tv = (TextView) findViewById (R.id.practicesetsubject);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String subject = extras.getString("Subject");
		    tv.setText(subject);
		}
		
		Button practiceback = (Button) findViewById(R.id.practiceback);
		Button practicestart = (Button) findViewById(R.id.practicestart);


		practiceback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(context, OptionActivity.class);
				startActivity(in);
			}
		});

		practicestart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(context, PracticeSetActivity.class);
				RadioGroup rbg = (RadioGroup)findViewById(R.id.difficulty);
				int radioID = rbg.getCheckedRadioButtonId();
				if(radioID == R.id.radioeasy){
					in.putExtra("Difficulty", "Easy");
				}
				else if(radioID == R.id.radionormal){
					in.putExtra("Difficulty", "Normal");
				}
				else if(radioID == R.id.radiohard){
					in.putExtra("Difficulty", "Hard");
				}
				in.putExtra("Subject", tv.getText().toString());
				startActivity(in);
			}
		});
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
	}
}

