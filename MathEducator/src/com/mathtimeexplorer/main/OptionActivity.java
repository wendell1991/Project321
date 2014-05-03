package com.mathtimeexplorer.main;

import com.example.matheducator.R;
import com.mathtimeexplorer.coincoin.CoinCoin;
import com.mathtimeexplorer.misc.Constants;
import com.mathtimeexplorer.tutorials.TutorialActivity;
import com.mathtimeexplorer.worksheets.QuizActivity;
import com.mathtimeexplorer.worksheets.SelectQuizActivity;
import com.mathtimeexplorer.xgame.XGame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class OptionActivity extends Activity implements OnTouchListener {

	int topic = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			Log.e("topic",Integer.toString(topic));
		}
		ImageView img = (ImageView) findViewById (R.id.optionsBkGrd);
		ImageView img2 = (ImageView) findViewById (R.id.optionsHotSpot);
		img.setOnTouchListener(this);
		if(topic == R.drawable.arithmetic){
			img.setImageResource(R.drawable.arithmetictopic);
			img2.setImageResource(R.drawable.arithmetictopicmask);
			img.setTag(R.drawable.arithmetictopic);
			img2.setTag(R.drawable.arithmetictopicmask);
		}
		else if(topic == R.drawable.fraction){
			img.setImageResource(R.drawable.fractiontopic);
			img2.setImageResource(R.drawable.fractiontopicmask);
			img.setTag(R.drawable.fractiontopic);
			img2.setTag(R.drawable.fractiontopicmask);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		final Context context = this;
		final int action = ev.getAction();
		// (1) 
		final int evX = (int) ev.getX();
		final int evY = (int) ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN :
			break;
		case MotionEvent.ACTION_UP :
			int touchColor = getHotspotColor (R.id.optionsHotSpot, evX, evY);
			ImageView img = (ImageView) findViewById (R.id.optionsBkGrd);
			if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.arithmetictopic).getConstantState()))
				arithmeticLayout(touchColor,evX,evY,context);
			else if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.fractiontopic).getConstantState()))
				fractionLayout(touchColor,evX,evY,context);
		}
		return true;
	}

	public int getHotspotColor (int hotspotId, int x, int y) {
		ImageView img = (ImageView) findViewById (hotspotId);
		img.setDrawingCacheEnabled(true); 
		Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache()); 
		img.setDrawingCacheEnabled(false);
		Log.e("test",Integer.toString(hotspots.getPixel(x, y)));
		return hotspots.getPixel(x, y);
	}

	private void arithmeticLayout(int touchColor, int evX, int evY, Context context){
		int tutorial1 = -16711681;
		int tutorial2 = -65281;
		int tutorial3 = -256;
		int tutorial4 = -10092544;
		int tutorial5 = -26368;
		int quiz = -16711936;
		int game = -52480;

		if(touchColor==tutorial1){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial2){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial3){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial4){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial5){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==quiz){
			Intent intent = new Intent(context, SelectQuizActivity.class);
			Bundle extras = getIntent().getExtras();
			//User user  = extras.getParcelable(Constants.USER);
			//Log.e("Edulevel",Integer.toString(user.getEduLevel()));
			intent.putExtras(extras);
			startActivity(intent);
		}
		if(touchColor==game){
			Intent intent = new Intent(context, XGame.class);
			startActivity(intent);
		}
	}

	private void fractionLayout(int touchColor, int evX, int evY, Context context){
		int tutorial1 = -16711681;
		int tutorial2 = -65281;
		int tutorial3 = -256;
		int tutorial4 = -26368;
		int quiz = -10092544;
		int game = -52480;

		if(touchColor==tutorial1){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial2){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial3){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==tutorial4){
			Intent intent = new Intent(context, TutorialActivity.class);
			intent.putExtra(Constants.TOPIC, topic);
			callProgressWindow(context, intent, evX, evY);
		}
		if(touchColor==quiz){
			Intent intent = new Intent(context, QuizActivity.class);
			startActivity(intent);
		}
		if(touchColor==game){
			Intent intent = new Intent(context, CoinCoin.class);
			startActivity(intent);
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
		ll2.setBackgroundResource(R.drawable.cardbackground);
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
		progressWindow.showAtLocation(ll, Gravity.NO_GRAVITY, evX+40, evY+100);

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
	}

}
