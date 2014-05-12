package com.mathtimeexplorer.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.matheducator.R;
import com.mathtimeexplorer.coincoin.CoinCoin;
import com.mathtimeexplorer.tutorials.TutorialActivity;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.SelectQuizActivity;
import com.mathtimeexplorer.xgame.XGame;

public class OptionActivity extends Activity implements OnTouchListener {

	int topic = 0;
	private MediaPlayer bkgrdMusic = null;
	private ImageView img, img2;
	User user;
	int eduLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			Log.e("topic",Integer.toString(topic));
			user = extras.getParcelable(Constants.USER);
			eduLevel = user.getEduLevel();
		}	
		
		img = (ImageView) findViewById (R.id.optionsBkGrd);
		img2 = (ImageView) findViewById (R.id.optionsHotSpot);
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
		else if(topic == R.drawable.measurement){
			if(user.getEduLevel()==1){
				img.setImageResource(R.drawable.measurementp1);
				img2.setImageResource(R.drawable.measurementp1mask);
				img.setTag(R.drawable.measurementp1);
				img2.setTag(R.drawable.measurementp1mask);
			}else if(user.getEduLevel()==2){
				img.setImageResource(R.drawable.measurementp2);
				img2.setImageResource(R.drawable.measurementp2mask);
				img.setTag(R.drawable.measurementp2);
				img2.setTag(R.drawable.measurementp2mask);
			}else if(user.getEduLevel()==3){
				img.setImageResource(R.drawable.measurementp3);
				img2.setImageResource(R.drawable.measurementp3mask);
				img.setTag(R.drawable.measurementp3);
				img2.setTag(R.drawable.measurementp3mask);
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		final Context context = this;
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topic = extras.getInt(Constants.TOPIC);
			Log.e("topic",Integer.toString(topic));
		}
		if(topic == R.drawable.arithmetic){
			extras.putString("topicname", "Arithmetic");
		}
		else if(topic == R.drawable.fraction){
			extras.putString("topicname", "Fraction");
		} 
		else if (topic == R.drawable.measurement) {
			extras.putString("topicname", "Measurement");
		}
		
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
				arithmeticLayout(touchColor,evX,evY,context,extras);
			else if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.fractiontopic).getConstantState()))
				fractionLayout(touchColor,evX,evY,context,extras);
			else if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.measurementp1).getConstantState()) || img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.measurementp2).getConstantState()) || img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.measurementp3).getConstantState()))
				measurementLayout(touchColor,evX,evY,context,extras);
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

	private void arithmeticLayout(int touchColor, int evX, int evY, Context context,Bundle extras){
		int tutorial1 = -16711681;
		int tutorial2 = -65281;
		int tutorial3 = -256;
		int tutorial4 = -10092544;
		int tutorial5 = -26368;
		int quiz = -16711936;
		int game = -52480;

		if(touchColor==tutorial1){
			extras.putString("subtopic", "Numbers");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial2){
			extras.putString("subtopic", "Subtraction");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial3){
			extras.putString("subtopic", "Addition");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial4){
			extras.putString("subtopic", "Multiplication");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial5){
			extras.putString("subtopic", "Division");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==quiz){
			Intent intent = new Intent(context, SelectQuizActivity.class);
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

	private void fractionLayout(int touchColor, int evX, int evY, Context context,Bundle extras){
		int tutorial1 = -16711681;
		int tutorial2 = -65281;
		int tutorial3 = -256;
		int tutorial4 = -26368;
		int quiz = -10092544;
		int game = -52480;

		if(touchColor==tutorial1){
			extras.putString("subtopic", "Understanding");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial2){
			extras.putString("subtopic", "Addition");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial3){
			extras.putString("subtopic", "Comparing&Ordering");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial4){
			extras.putString("subtopic", "Subtraction");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==quiz){
			Intent intent = new Intent(context, SelectQuizActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
		}
		if(touchColor==game){
			Intent intent = new Intent(context, CoinCoin.class);
			startActivity(intent);
		}
	}
	
	private void measurementLayout(int touchColor, int evX, int evY, Context context,Bundle extras){
		int tutorial1 = -10092544;
		int tutorial2 = -256;
		int tutorial3 = -16711681;
		int tutorial4 = -65281;
		int tutorial5 = -16777216;
		int tutorial6 = -1;
		int tutorial7 = -13369600;
		int quiz = -26368;
		int game = -52480;

		if(touchColor==tutorial1){
			extras.putString("subtopic", "Mass");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial2){
			extras.putString("subtopic", "Length");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial3){
			extras.putString("subtopic", "Time");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial4){
			extras.putString("subtopic", "Money");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial5){
			extras.putString("subtopic", "Volume");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial6){
			extras.putString("subtopic", "Area");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==tutorial7){
			extras.putString("subtopic", "Perimeter");
			callProgressWindow(context, evX, evY, extras);
		}
		if(touchColor==quiz){
			Intent intent = new Intent(context, SelectQuizActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
		}
		if(touchColor==game){
			Intent intent = new Intent(context, CoinCoin.class);
			startActivity(intent);
		}
	}

	private void callProgressWindow(Context cx, int evX, int evY, Bundle bundle) {

		final Context context = cx;
		final Bundle extras = bundle;
		PopupWindow progressWindow = new PopupWindow(context);
		int stars = 0;
		
		
		
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
		for(int i=0;i<3-stars;i++){
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
				Intent intent = new Intent(context, SelectPracticeSetActivity.class);
				intent.putExtras(extras);
				startActivity(intent);
			}
		});

		tutorialBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TutorialActivity.class);
				intent.putExtra(Constants.USER, user);
				intent.putExtras(extras);
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
	@Override
	public void onResume() {
		super.onResume();
		if (bkgrdMusic != null) {
			bkgrdMusic.start();
		} else{
			checkTopic();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Back button is pressed
		if (this.isFinishing()) {
			if (bkgrdMusic != null) {
				bkgrdMusic.release();
				bkgrdMusic = null;
			}
		} 
		// User exits the application
		else {
			if (bkgrdMusic != null) {
				bkgrdMusic.pause();
			}
		}
	}
	
	private void checkTopic() {
		if(topic == R.drawable.arithmetic){
			startBkGrdMusic(R.raw.jewelbeat_jungle);
		}
		else if(topic == R.drawable.fraction){
			startBkGrdMusic(R.raw.jewelbeat_getting_the_right_groove);
		} 
		else if (topic == R.drawable.measurement) {
			startBkGrdMusic(R.raw.jewelbeat_working_at_the_countryside);
		}
	}
	
	private void startBkGrdMusic(int musicResId) {
		bkgrdMusic = MediaPlayer.create(OptionActivity.this, musicResId);
		bkgrdMusic.setLooping(true);
		bkgrdMusic.start();
	}
	
	public void onBackPressed() {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtras(extras);
		startActivity(intent);
	}
}
