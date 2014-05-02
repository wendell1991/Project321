package com.mathtimeexplorer.coincoin;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.matheducator.R;
import com.mathtimeexplorer.main.MainActivity;
import com.mathtimeexplorer.utils.Constants;

public class CoinCoin extends Activity {
	
	private PopupWindow storyPopUp, scoreSheetPopUp;
	private TextView question, coincoin_tapStart, timeTaken, score_timetaken, score_nooftries;
	private Button tryAgainBtn, giveUpBtn;
	
	private ImageView 
			frontViewOne, backViewOne, frontViewTwo, backViewTwo, frontViewThree, backViewThree, 
			frontViewFour, backViewFour, frontViewFive, backViewFive, frontViewSix, backViewSix, 
			frontViewSeven, backViewSeven, frontViewEight, backViewEight, frontViewNine, backViewNine,
			frontViewTen, backViewTen, frontViewEleven, backViewEleven, frontViewTwelve, backViewTwelve;
	
	// TIMER
	private int sixty = 60;
	private long startTime = 0L;
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;	
	private static final String STRING_COLON = ":";
	private static final String TIME_TAKEN = "TIME TAKEN: ";
	private Handler customHandler = new Handler();
	
	// Keep tracks of the number of flips
	private int flipTries = 0;
	
	private int questionIndex = 0;
	private static final int flipFront = 1;
	private static final int flipBack = 2;
	private static final String TAG_QUESTION = "YOUR MUM WANTS YOU TO FIND:";
	
	private Context context = this;
	private AnimatorSet flip_left_in; 
	private AnimatorSet flip_right_in;
	private Animation fade;
	private ArrayList<Integer> questionList;
	private ArrayList<Integer> valueList;
	private ArrayList<ImageView> backViewList;
	
	// Keep track of the number of cards the user flips
	private ArrayList<ImageView> flipList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coincoin_main);
		
		initMain();
		
		flipList = new ArrayList<ImageView>();
		
		flip_left_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
		flip_right_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
		fade = AnimationUtils.loadAnimation(context, R.anim.fade);
		
		new LoadGameData().execute();
	}
	
	private void processFlip(ImageView backView) {
		Log.i("CoinCoin", "---------------Processing flip------------------");
		flipList.add(backView);
		Log.i("CoinCoin", "FlipList Size: "+flipList.size());
		if (flipList.size() == 2) {
			
			flipTries++;
			
			final ImageView backViewOne = (ImageView) flipList.get(0);
			final ImageView backViewTwo = (ImageView) flipList.get(1);
			
			Log.i("CoinCoin", "backViewOne: "+backViewOne);
			Log.i("CoinCoin", "backViewTwo: "+backViewTwo);
			
			Object tagOne = backViewOne.getTag();
			Object tagTwo = backViewTwo.getTag();
			final int firstNumber = tagOne == null ? -1 : (Integer) tagOne;
			final int secondNumber = tagTwo == null ? -1 : (Integer) tagTwo;
			
			// Checks if the values of the two cards adds up to the correct value
			final String[] values = question.getText().toString().split(":");
			Log.i("CoinCoin", "string value: "+(String) values[1]);
			
			Handler handler = new Handler(); 
			
			// Sleep 2 seconds
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		        	 if (Integer.valueOf((String) values[1]) == (firstNumber + secondNumber)) {
		 				backViewOne.startAnimation(fade);
		 				backViewTwo.startAnimation(fade);
		 				
		 				questionIndex++;
		 				
		 				// Call score-sheet pop-up since the last question is answered
		 				if (questionIndex > 6) {
		 					callScoreSheetPopUp();
		 				} else {
		 					// Populate the next question
		 					question.setText(TAG_QUESTION+questionList.get(questionIndex));
		 				}
		 			} else {
		 				onBackViewClick(backViewOne);
		 				onBackViewClick(backViewTwo);
		 				callScoreSheetPopUp();
		 			} 
		         } 
		    }, 1500);
			
			// Resets flips selected
			flipList.clear();
		}
	}
	
	
	private CoinCoinValues generateGameValues() {
		CoinCoinValues values = new CoinCoinValues();
		Random rand = new Random();
		
		int randomVal = 0;
	    while (true) {
	    	// Ensures that randomVal will not be 0 or less than 5
	    	if (randomVal == 0 || randomVal < 5) {
	    		// Random generate a value between 1 - 200
	    		randomVal = rand.nextInt(200) + 1;
	    	} else {
	    		break;
	    	}
	    }
	    Log.i("CoinCoin", "randomVal: " +randomVal);
	    
		// Round the generated value to be a multiple of 5	    
		int actualValue = 5 * (Math.round(randomVal / 5));
		randomVal = rand.nextInt(actualValue) + 1;
		
		// Random generate 2 values that adds up to actualValue
		int firstNumber = 5 * (Math.round(randomVal / 5));
		int secondNumber = actualValue - firstNumber;
		
		Log.i("CoinCoin", "actualValue: "+actualValue+" firstNumber: "+firstNumber+
				" secondNumber: "+secondNumber);
		
		values.setActualValue(actualValue);
		values.setFirstNumber(firstNumber);
		values.setSecondNumber(secondNumber);
		return values;
	}
	
	// On-click handler for frontViews
	public void onFrontViewClick(View v) {
		CustomAnimationListener myListener = new CustomAnimationListener(flipFront,
				(ImageView) v, findCorrespondingView(v));
		flip_right_in.addListener(myListener);
		flip_right_in.setTarget(v);
		flip_right_in.start();
	}
	
	private void onBackViewClick(View v) {
		CustomAnimationListener myListener = (new CustomAnimationListener(flipBack, 
				findCorrespondingView(v), (ImageView) v));
		flip_left_in.addListener(myListener);
		flip_left_in.setTarget(v);
		flip_left_in.start();
	}
	
	private void callScoreSheetPopUp() {
		
		// Stops the timer first
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.coincoin_scoresheet, 
				(ViewGroup) findViewById(R.id.coincoin_scoresheet));
		
		scoreSheetPopUp = new PopupWindow(layout, 600, 450, true);
		scoreSheetPopUp.setAnimationStyle(R.style.Animation_Bounce);
		
		// Initialize the widgets
		score_timetaken = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_timetaken);
		score_nooftries = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_nooftries);
		tryAgainBtn = (Button) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_tryagain);
		giveUpBtn = (Button) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_giveup);
		
		// Display results
		String timeResult = timeTaken.getText().toString();
		score_timetaken.setText(timeResult.substring(timeResult.indexOf(":") + 1).trim());
		score_nooftries.setText(String.valueOf(flipTries));

		tryAgainBtn.setOnTouchListener(new View.OnTouchListener() {
				
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						// User selected try again, reloads this activity
						Intent intent = getIntent();
						scoreSheetPopUp.dismiss();
						finish();
						startActivity(intent);
					}
				}
				return true;
			}
		});
				
		giveUpBtn.setOnTouchListener(new View.OnTouchListener() {
					
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						// User gives up, returns to previous activity
						scoreSheetPopUp.dismiss();
						finish();
					}
				}
				return true;
			}
		});
		scoreSheetPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
	}
	
	private void callStoryPopUpWindow() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
	
		View layout = inflater.inflate(R.layout.coincoin_storyboard, 
				(ViewGroup) findViewById(R.id.coincoin_popup));
	
		storyPopUp = new PopupWindow(layout, 1000, 600, true);
		
	    coincoin_tapStart = (TextView) storyPopUp.getContentView().findViewById(R.id.coincoin_tapStart);
	    coincoin_tapStart.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						// Set the first question
						question.setText(TAG_QUESTION+questionList.get(questionIndex));			
						questionIndex++;
						storyPopUp.dismiss();
				
						// Start the timer
						startTime = SystemClock.uptimeMillis();
						customHandler.postDelayed(updateTimerThread, 0);
					}
				}
				return true;
			}
		});
	    
	    // Show the story pop-up window at the center of the screen
	    storyPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
	}

	private Runnable updateTimerThread = new Runnable() {
		
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			
			long minutes = (TimeUnit.MILLISECONDS.toMinutes((updatedTime)) % sixty);
			long seconds = (TimeUnit.MILLISECONDS.toSeconds(updatedTime)) % sixty;
			long milliseconds = (TimeUnit.MILLISECONDS.toMillis((updatedTime)) % sixty);

			// Format minutes, seconds and milliseconds to 2 digits
			timeTaken.setText(TIME_TAKEN 
					+ String.format("%02d", minutes) + STRING_COLON 
					+ String.format("%02d", seconds) + STRING_COLON 
					+ String.format("%02d", milliseconds));
			
			customHandler.postDelayed(this, 0);
		}
	};

	// Find the backView or frontView
	private ImageView findCorrespondingView(View input) {
		ImageView view = null;
		switch (input.getId()) {
			case R.id.frontViewOne:
				view = backViewOne;
				break;
			case R.id.backViewOne:
				view = frontViewOne;
				break;
			case R.id.frontViewTwo:
				view = backViewTwo;
				break;
			case R.id.backViewTwo:
				view = frontViewTwo;
				break;
			case R.id.frontViewThree:
				view = backViewThree;
				break;
			case R.id.backViewThree:
				view = frontViewThree;
				break;
			case R.id.frontViewFour:
				view = backViewFour;
				break;
			case R.id.backViewFour:
				view = frontViewFour;
				break;
			case R.id.frontViewFive:
				view = backViewFive;
				break;
			case R.id.backViewFive:
				view = frontViewFive;
				break;
			case R.id.frontViewSix:
				view = backViewSix;
				break;
			case R.id.backViewSix:
				view = frontViewSix;
				break;
			case R.id.frontViewSeven:
				view = backViewSeven;
				break;
			case R.id.backViewSeven:
				view = frontViewSeven;
				break;
			case R.id.frontViewEight:
				view = backViewEight;
				break;
			case R.id.backViewEight:
				view = frontViewEight;
				break;
			case R.id.frontViewNine:
				view = backViewNine;
				break;
			case R.id.backViewNine:
				view = frontViewNine;
				break;
			case R.id.frontViewTen:
				view = backViewTen;
				break;
			case R.id.backViewTen:
				view = frontViewTen;
				break;
			case R.id.frontViewEleven:
				view = backViewEleven;
				break;
			case R.id.backViewEleven:
				view = frontViewEleven;
				break;
			case R.id.frontViewTwelve:
				view = backViewTwelve;
				break;
			case R.id.backViewTwelve:
				view = frontViewTwelve;
				break;
		}
		return view;
	}
	
	private void initMain() {
		backViewList = new ArrayList<ImageView>();
		// Front and back views of each cards
		frontViewOne = (ImageView) findViewById(R.id.frontViewOne);
		backViewOne = (ImageView) findViewById(R.id.backViewOne);
		frontViewTwo = (ImageView) findViewById(R.id.frontViewTwo);
		backViewTwo = (ImageView) findViewById(R.id.backViewTwo);		
		frontViewThree = (ImageView) findViewById(R.id.frontViewThree);
		backViewThree = (ImageView) findViewById(R.id.backViewThree);
		frontViewFour = (ImageView) findViewById(R.id.frontViewFour);
		backViewFour = (ImageView) findViewById(R.id.backViewFour);		
		frontViewFive = (ImageView) findViewById(R.id.frontViewFive);
		backViewFive = (ImageView) findViewById(R.id.backViewFive);		
		frontViewSix = (ImageView) findViewById(R.id.frontViewSix);
		backViewSix = (ImageView) findViewById(R.id.backViewSix);		
		frontViewSeven = (ImageView) findViewById(R.id.frontViewSeven);
		backViewSeven = (ImageView) findViewById(R.id.backViewSeven);	
		frontViewEight = (ImageView) findViewById(R.id.frontViewEight);
		backViewEight = (ImageView) findViewById(R.id.backViewEight);	
		frontViewNine = (ImageView) findViewById(R.id.frontViewNine);
		backViewNine = (ImageView) findViewById(R.id.backViewNine);	
		frontViewTen = (ImageView) findViewById(R.id.frontViewTen);
		backViewTen = (ImageView) findViewById(R.id.backViewTen);		
		frontViewEleven = (ImageView) findViewById(R.id.frontViewEleven);
		backViewEleven = (ImageView) findViewById(R.id.backViewEleven);
		frontViewTwelve = (ImageView) findViewById(R.id.frontViewTwelve);
		backViewTwelve = (ImageView) findViewById(R.id.backViewTwelve);
		
		backViewList.add(backViewOne);
		backViewList.add(backViewTwo);
		backViewList.add(backViewThree);
		backViewList.add(backViewFour);
		backViewList.add(backViewFive);
		backViewList.add(backViewSix);
		backViewList.add(backViewSeven);
		backViewList.add(backViewEight);
		backViewList.add(backViewNine);
		backViewList.add(backViewTen);
		backViewList.add(backViewEleven);
		backViewList.add(backViewTwelve);
		
		question = (TextView) findViewById(R.id.coincoin_qn);
		timeTaken = (TextView) findViewById(R.id.coincoin_timer);
	}
	
	class CustomAnimationListener implements AnimatorListener {
		
		private int flipDirection;
		private ImageView frontView;
		private ImageView backView;
		
		public CustomAnimationListener(int flipDirection, ImageView frontView, ImageView backView) {
			this.flipDirection = flipDirection;
			this.frontView = frontView;
			this.backView = backView;
		}

		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// TODO Auto-generated method stub
			// Base on the flip direction, decide whether to hide front or back card
			switch (flipDirection) {
				case 1: {
					Log.i("CoinCoin", "Flipdirection case 1!");
					backView.setVisibility(View.VISIBLE);
					frontView.setVisibility(View.INVISIBLE);
					flip_right_in.removeAllListeners();
					
					// Process flip only when front-View is flip
					processFlip(backView);
					break;
				}
				case 2: {
					Log.i("CoinCoin", "Flipdirection case 2!");
					frontView.setVisibility(View.VISIBLE);
					backView.setVisibility(View.INVISIBLE);		
					flip_left_in.removeAllListeners();
					break;
				}
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub		
		}
	}
	
	class LoadGameData extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CoinCoin.this);
            pDialog.setTitle(Constants.DIALOG_TITLE);
            pDialog.setMessage(Constants.DIALOG_MESSAGE);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			valueList = new ArrayList<Integer>();
			questionList = new ArrayList<Integer>();
			CoinCoinValues values = null;
			
			// Generate 6 set of values needed by 12 cards
			for (int i = 0; i < 6; i++) {
				values = generateGameValues();
				questionList.add(values.getActualValue());
				valueList.add(values.getFirstNumber());
				valueList.add(values.getSecondNumber());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			int index = 12;
			int randomValue = 0;
			int selectedValue = 0;
			Random rand = new Random();
			ImageView currentView = null;
			
			// Randomize the positions of the values among the 12 cards
			for (int i = 0; i < backViewList.size(); i++) {
				currentView = (ImageView) backViewList.get(i);
				randomValue = rand.nextInt(index);
				selectedValue = (Integer) valueList.get(randomValue);
				currentView.setImageResource(findResourceId(selectedValue));
				currentView.setTag(selectedValue);
				
				// Remove the value after assigning it to the view
				valueList.remove(randomValue);
				index--;
			}
			
			// Populate the first value on question view
			pDialog.dismiss();
			callStoryPopUpWindow();
		}	
	}
	
	// Method to find image resource id of each value
	private int findResourceId(int selectedValue) {
		int resourceId = 0;
		switch (selectedValue) {
		case 5:
			resourceId = R.drawable.cents_5;
			break;
		case 10:
			resourceId = R.drawable.cents_10;
			break;
		case 15:
			resourceId = R.drawable.cents_15;
			break;
		case 20:
			resourceId = R.drawable.cents_20;
			break;
		case 25:
			resourceId = R.drawable.cents_25;
			break;
		case 30:
			resourceId = R.drawable.cents_30;
			break;
		case 35:
			resourceId = R.drawable.cents_35;
			break;
		case 40:
			resourceId = R.drawable.cents_40;
			break;
		case 45:
			resourceId = R.drawable.cents_45;
			break;
		case 50:
			resourceId = R.drawable.cents_50;
			break;
		case 55:
			resourceId = R.drawable.cents_55;
			break;
		case 60:
			resourceId = R.drawable.cents_60;
			break;
		case 65:
			resourceId = R.drawable.cents_65;
			break;
		case 70:
			resourceId = R.drawable.cents_70;
			break;
		case 75:
			resourceId = R.drawable.cents_75;
			break;
		case 80:
			resourceId = R.drawable.cents_80;
			break;
		case 85:
			resourceId = R.drawable.cents_85;
			break;
		case 90:
			resourceId = R.drawable.cents_90;
			break;
		case 95:
			resourceId = R.drawable.cents_95;
			break;
		case 100:
			resourceId = R.drawable.cents_100;
			break;
		case 105:
			resourceId = R.drawable.cents_105;
			break;
		case 110:
			resourceId = R.drawable.cents_110;
			break;
		case 115:
			resourceId = R.drawable.cents_115;
			break;
		case 120:
			resourceId = R.drawable.cents_120;
			break;
		case 125:
			resourceId = R.drawable.cents_125;
			break;
		case 130:
			resourceId = R.drawable.cents_130;
			break;
		case 135:
			resourceId = R.drawable.cents_135;
			break;
		case 140:
			resourceId = R.drawable.cents_140;
			break;
		case 145:
			resourceId = R.drawable.cents_145;
			break;
		case 150:
			resourceId = R.drawable.cents_150;
			break;
		case 155:
			resourceId = R.drawable.cents_155;
			break;
		case 160:
			resourceId = R.drawable.cents_160;
			break;
		case 165:
			resourceId = R.drawable.cents_165;
			break;
		case 170:
			resourceId = R.drawable.cents_170;
			break;
		case 175:
			resourceId = R.drawable.cents_175;
			break;
		case 180:
			resourceId = R.drawable.cents_180;
			break;
		case 185:
			resourceId = R.drawable.cents_185;
			break;
		case 190:
			resourceId = R.drawable.cents_190;
			break;
		case 195:
			resourceId = R.drawable.cents_195;
			break;
		case 200:
			resourceId = R.drawable.cents_200;
			break;
		default:
			Log.i("CoinCoin", "Image not found! value:" + selectedValue);
			resourceId = R.drawable.cardfront;
			break;
		}
		return resourceId;
	}
}