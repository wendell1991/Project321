package com.mathtimeexplorer.xgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

public class XGame extends Activity {
	
	private Dialog countDownDialog;
	private PopupWindow scoreSheetPopUp;
	
	private ImageView valueOne, valueTwo, valAnswer, optionOne, optionTwo, optionThree, 
					  optionFour, optionFive, optionSix, correctGreen, wrongRed, countDownView;
	
	private ImageButton tryAgainBtn, giveUpBtn;
	private TextView timerText, totalQns, numCorrect, numWrong, totalMarks;
	
	// Count-down configuration
	private long COUNTDOWN_DURATION = 6000, COUNTDOWN_INTERVAL = 1000;
	// Timer configuration
	private long TIMER_DURATION = 30000, TIMER_INTERVAL = 10;
	
	private int sixty = 60;
	private static int INDEX_ZERO = 0;
	private static int OPTION_CORRECT = 1; 
	private static int OPTION_WRONG = 2;
	
	private Context context = this;
	private CountDown count;
	private MediaPlayer bkgrdMusic = null;
	private ArrayList<ImageView> optionArrayList;
	private ArrayList<XGameNumbers> numList = new ArrayList<XGameNumbers>();
	
	// Keep track of number of corrects & wrong
	private int correct = 0;
	private int wrong = 0;
	
	// Check the state of the game
	private boolean isGameStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xgame_main);
		
		// Initialize UIs (Only once)
		init();
		new LoadGameData().execute();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (isGameStarted == true) {
			// Resumes the count-down & background music
			long timeLeft = count.getTimeLeft();
			count = new CountDown(timeLeft, TIMER_INTERVAL);
			count.start();
			bkgrdMusic.start();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Back button is pressed
		if (this.isFinishing()) {
			if (bkgrdMusic != null) {
				//bkgrdMusic.reset();
				bkgrdMusic.release();
			}
			count.cancel();
			count = null;
		}
		// User exits the app
		else {
			if (bkgrdMusic != null) {
				bkgrdMusic.pause();
			}
			count.cancel();
		}
	}
	
	// onClick Handler to check user's input
	public void checkAnswer(View view){
		Object tag = view.getTag();
		int selectedValue = tag == null ? -1 : (Integer) tag;
		
		XGameNumbers nh = numList.get(INDEX_ZERO);
		
		// Remove the current nh to get nextNh
		numList.remove(INDEX_ZERO);
		XGameNumbers nextNh = numList.get(INDEX_ZERO);
		
		if (nh.getAnswer() == selectedValue) {
			animateResult(OPTION_CORRECT);
			genGameContent(nextNh);
			correct++;
		} else {
			animateResult(OPTION_WRONG);
			genGameContent(nextNh);
			wrong++;
		}
	}
	
	private void animateResult(int option) {
		final MediaPlayer soundEff;
		Animation blink = AnimationUtils.loadAnimation(context, R.anim.blink);
		
		if (option == 1) {
			// Correct
			wrongRed.clearAnimation();
			wrongRed.setVisibility(View.INVISIBLE);
			correctGreen.setVisibility(View.VISIBLE);
			correctGreen.startAnimation(blink);
			soundEff = MediaPlayer.create(XGame.this, R.raw.xgamecorrect);
		} else {
			// Wrong
			correctGreen.clearAnimation();
			correctGreen.setVisibility(View.INVISIBLE);
			wrongRed.setVisibility(View.VISIBLE);
			wrongRed.startAnimation(blink);
			soundEff = MediaPlayer.create(XGame.this, R.raw.xgamewrong);
		}
		
		soundEff.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				soundEff.reset();
				soundEff.release();
			}
		});
		soundEff.start();
	}
	
	private void callScoreSheetDialog(){
		LayoutInflater inflater = (LayoutInflater) this.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.xgame_scoresheet, 
				(ViewGroup) findViewById(R.id.xgame_scoresheet));
		
		scoreSheetPopUp = new PopupWindow(layout, 1200, 650, true);
		scoreSheetPopUp.setAnimationStyle(R.style.Animation_Bounce);
		
		// Initialize the widgets
		totalQns = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameQnsAttempted);
		numCorrect = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameQnsCorrect);
		numWrong = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameQnsWrong);
		totalMarks = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameTotalScore);
		tryAgainBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.xgameTryAgain);
		giveUpBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.xgameGiveUp);
		 
		// Display results
		totalQns.setText(String.valueOf(correct + wrong));
		numCorrect.setText(String.valueOf(correct));
		numWrong.setText(String.valueOf(wrong));
	
		if (correct == 0 || correct < wrong) {
			totalMarks.setText(String.valueOf(0));
		} else {
			totalMarks.setText(String.valueOf(correct - wrong));
		}
		
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
						// User gives up, so returns to the main topic selection screen
						Intent intent = new Intent(context, MainActivity.class);
						scoreSheetPopUp.dismiss();
						startActivity(intent);
					}
				}
				return true;
			}
		});
		scoreSheetPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
	}

	private void genGameContent(XGameNumbers nh){
		int whichToHide = nh.getWhichToHide();
		int firstNo = nh.getFirstNo();
		int secondNo = nh.getSecondNo();
		
		// Hide either firstNumber, secondNumber or result randomly and paint them
		if (whichToHide == firstNo) {
			valueOne.setImageResource(R.drawable.xgame_qnmark);
			valueTwo.setImageResource(nh.getSecondNoResId());
			valAnswer.setImageResource(nh.getResultResId());
		}
		else if (whichToHide == secondNo) {
			valueTwo.setImageResource(R.drawable.xgame_qnmark);
			valueOne.setImageResource(nh.getFirstNoResId());
			valAnswer.setImageResource(nh.getResultResId());
		}
		else {
			valAnswer.setImageResource(R.drawable.xgame_qnmark);
			valueOne.setImageResource(nh.getFirstNoResId());
			valueTwo.setImageResource(nh.getSecondNoResId());
		}

		HashMap<Integer, Integer> optionsMap = nh.getOptionsMap();
		
		// Randomize positions of all the options
		List keys = new ArrayList(optionsMap.keySet());
	    Collections.shuffle(keys);
	
		int curVal = 0;
		int curValResId = 0;
		int currentIndex = 0;
		ImageView curBtn;
		
	    for (Object obj: keys) {
	    	curVal = (Integer) obj;
	    	curValResId = optionsMap.get(obj);
	    	curBtn = (ImageView) optionArrayList.get(currentIndex);
	    	curBtn.setTag(curVal);
	    	curBtn.setImageResource(curValResId);
	    	currentIndex++;
	    }
	}

	private XGameNumbers generateGameValues() {
		XGameNumbers nh = new XGameNumbers();
		Random rand = new Random();
		ArrayList<Integer> itemToHide = new ArrayList<Integer>();

		int firstNo = 0;
		int secondNo = 0;
		while (true) {
			// Ensure that firstNo and secondNo cannot be both be 0
			if (firstNo == 0 && secondNo == 0){
				// Generate values ranging 0 - 12
				firstNo = rand.nextInt(12) + 1;
				secondNo = rand.nextInt(12) + 1;
			} else {
				break;
			}
		}
		
		// Compute result of firstNo * secondNo
		int result = firstNo * secondNo;
		
		nh.setFirstNo(firstNo);
		nh.setSecondNo(secondNo);
		nh.setResult(result);
		nh.setFirstNoResId(findImageResourceId(firstNo));
		nh.setSecondNoResId(findImageResourceId(secondNo));
		nh.setResultResId(findImageResourceId(result));
		
		// Determines which to hide. 
		int whichToHide = rand.nextInt(3);
		itemToHide.add(firstNo);
		itemToHide.add(secondNo);
		itemToHide.add(result);
		whichToHide = itemToHide.get(whichToHide);
		
		/* Game Logic:  
		 * - We do not want both displayed value to be 0, e.g what multiplies by 0 = 0
		 * - If the above example is generated, all 6 buttons will be the correct answer
		 * - We only want 1 out of 6 of the buttons to be the correct answer
		 * - Therefore, we need to ensure maximum only one displayed value is 0
		 */
		if (whichToHide != 0 && result == 0) {
			// Hide the result since result is 0
			nh.setWhichToHide(result);
		} else {
			nh.setWhichToHide(whichToHide);
		}
		
		HashMap<Integer, Integer> optionsMap = new HashMap<Integer, Integer>();
		
		/*
		 * 0 Timetable is special, e.g x divide 0 will cause arithmetic exception
		 * If result is 0, either firstNumber or secondNumber is 0 
		 * Generation scheme: -Produce 2 options close to the answer
		 * 					  -Produce 3 randomly generated options
		*/
		if (result == 0) {
			// Anything multiplies by 0 will be 0
			nh.setAnswer(result);
			// Statically put 0, (0 + 1), (0 + 2) as the options
			optionsMap.put(result, findImageResourceId(result));
			optionsMap.put(result + 1, findImageResourceId(result + 1));
			optionsMap.put(result + 2, findImageResourceId(result + 2));
		} else {
			// Determines the answer, depending on which value to hide
			int tempResult = 0;
			if (whichToHide == firstNo) {
				tempResult = result / secondNo;
			} else if (whichToHide == secondNo) {
				tempResult = result / firstNo;
			} else {
				tempResult = result;
			}
			nh.setAnswer(tempResult);
			optionsMap.put(tempResult, findImageResourceId(tempResult));
			// Produce two values close to the answer
			optionsMap.put(tempResult + 1, findImageResourceId(tempResult + 1));
			optionsMap.put(tempResult - 1, findImageResourceId(tempResult - 1));
		}
		
		/* 
		 * Makes sure value of the random options are not repeated
		 * Generate options ranging from 0-144, exit loop when we have 3 options
		 */
		int prevVal = 145;
		int curVal = 0;
		while (true) {
			curVal = rand.nextInt(144) + 1;
			if (curVal != prevVal) {
				optionsMap.put(curVal, findImageResourceId(curVal));
				prevVal = curVal;
			}
			// Already should contain 3 items, 6 - 3 = 3
			if (optionsMap.size() == 6) {
				break;
			}
		}
		nh.setOptionsMap(optionsMap);
		return nh;
	}
	
	private void init() {
		timerText = (TextView) findViewById(R.id.timerText);
		correctGreen = (ImageView) findViewById(R.id.correctGreen);
		wrongRed = (ImageView) findViewById(R.id.wrongRed);
		valueOne = (ImageView) findViewById(R.id.valueOne);
		valueTwo = (ImageView) findViewById(R.id.valueTwo);
		valAnswer = (ImageView) findViewById(R.id.valAnswer);
		optionOne = (ImageView) findViewById(R.id.optionOne);
	    optionTwo = (ImageView) findViewById(R.id.optionTwo);
	    optionThree = (ImageView) findViewById(R.id.optionThree);
		optionFour = (ImageView) findViewById(R.id.optionFour);
		optionFive = (ImageView) findViewById(R.id.optionFive);
		optionSix = (ImageView) findViewById(R.id.optionSix);
		
		// Add the options into the array
		optionArrayList = new ArrayList<ImageView>();
		optionArrayList.add(optionOne);
		optionArrayList.add(optionTwo);
		optionArrayList.add(optionThree);
		optionArrayList.add(optionFour);
		optionArrayList.add(optionFive);
		optionArrayList.add(optionSix);
	}
	
	class LoadGameData extends AsyncTask<String, String, String> {
		
		private ProgressDialog pDialog;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(XGame.this);
            pDialog.setTitle(Constants.DIALOG_TITLE);
            pDialog.setMessage(Constants.DIALOG_MESSAGE);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// we generate 150 questions assuming user cannot answer them in 30 seconds
			for (int i = 0; i < 150; i++){
				XGameNumbers nh = generateGameValues();
				numList.add(nh);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// Pull the first question out create the contents
			genGameContent(numList.get(INDEX_ZERO));
			pDialog.dismiss();
			GameStartTimer gst = new GameStartTimer(COUNTDOWN_DURATION, COUNTDOWN_INTERVAL);
			gst.start();
		}
	}
	
	class GameStartTimer extends CountDownTimer {

		public GameStartTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
			countDownDialog = new Dialog(context);	
			countDownDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			countDownDialog.setContentView(R.layout.dialog_timer);
			
			// Makes the count-down dialog-box transparent
		    final Window window = countDownDialog.getWindow();
		    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		    
			countDownView = (ImageView) countDownDialog.findViewById(R.id.optionsBkGrd);
			countDownDialog.show();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) % sixty;
			countDownView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
			if (seconds == 4) {
				countDownView.setImageResource(R.drawable.countdown_3);
			} else if (seconds == 3) {
				countDownView.setImageResource(R.drawable.countdown_2);
			} else if (seconds == 2) {
				countDownView.setImageResource(R.drawable.countdown_1);
			} else if (seconds == 1) {
				countDownView.setImageResource(R.drawable.xgame_start);
			}
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			countDownView.setVisibility(View.GONE);
			countDownDialog.dismiss();
			
			// Start background music
			bkgrdMusic = MediaPlayer.create(XGame.this, R.raw.xgamemusic);
			bkgrdMusic.start();
			
			// Create a 30 seconds count-down timer 
			count = new CountDown(TIMER_DURATION, TIMER_INTERVAL);
			count.start();
			isGameStarted = true;
		}
	}
	
	class CountDown extends CountDownTimer {
		
		private static final String TIME_FINISH = "00:00";
		private static final String TIME_LEFT = "Time left: ";
		private static final String STRING_COLON = ":";
		private long timeLeft = 0;

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}
		
		@Override
        public void onFinish() {
			timerText.setText(TIME_LEFT+TIME_FINISH);
			if (bkgrdMusic.isPlaying() == true) {
				bkgrdMusic.reset();
				bkgrdMusic.release();
			}
			callScoreSheetDialog();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	long milliseconds = (TimeUnit.MILLISECONDS.toMillis((millisUntilFinished)) % sixty);
        	long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) % sixty;
        	
        	// If time left is less than 10 seconds
        	if (seconds < 10) {
        		timerText.setTextColor(Color.RED);
        	}
        	
        	// Format seconds and milliseconds to 2 digits
        	timerText.setText(TIME_LEFT
        			+ String.format("%02d", seconds) + STRING_COLON 
					+ String.format("%02d", milliseconds));;
					
        	setTimeLeft(millisUntilFinished);
        }

		public long getTimeLeft() {
			return timeLeft;
		}

		public void setTimeLeft(long timeLeft) {
			this.timeLeft = timeLeft;
		}
	}
	
	private int findImageResourceId(int value) {
		int resourceId = 0;
		switch(value){
		case 1: 
			resourceId = R.drawable.xgame1;
			break;
		case 2: 
			resourceId = R.drawable.xgame2;
			break;
		case 3: 
			resourceId = R.drawable.xgame3;
			break;
		case 4: 
			resourceId = R.drawable.xgame4;
			break;
		case 5: 
			resourceId = R.drawable.xgame5;
			break;
		case 6: 
			resourceId = R.drawable.xgame6;
			break;
		case 7: 
			resourceId = R.drawable.xgame7;
			break;
		case 8:
			resourceId = R.drawable.xgame8;
			break;
		case 9: 
			resourceId = R.drawable.xgame9;
			break;
		case 10: 
			resourceId = R.drawable.xgame10;
			break;
		case 11: 
			resourceId = R.drawable.xgame11;
			break;
		case 12: 
			resourceId = R.drawable.xgame12;
			break;
		case 13: 
			resourceId = R.drawable.xgame13;
			break;
		case 14: 
			resourceId = R.drawable.xgame14;
			break;
		case 15: 
			resourceId = R.drawable.xgame15;
			break;
		case 16: 
			resourceId = R.drawable.xgame16;
			break;
		case 17:
			resourceId = R.drawable.xgame17;
			break;
		case 18: 
			resourceId = R.drawable.xgame18;
			break;	
		case 19: 
			resourceId = R.drawable.xgame19;
			break;
		case 20: 
			resourceId = R.drawable.xgame20;
			break;
		case 21: 
			resourceId = R.drawable.xgame21;
			break;
		case 22: 
			resourceId = R.drawable.xgame22;
			break;
		case 23: 
			resourceId = R.drawable.xgame23;
			break;
		case 24: 
			resourceId = R.drawable.xgame24;
			break;
		case 25: 
			resourceId = R.drawable.xgame25;
			break;
		case 26:
			resourceId = R.drawable.xgame26;
			break;
		case 27: 
			resourceId = R.drawable.xgame27;
			break;
		case 28: 
			resourceId = R.drawable.xgame28;
			break;
		case 29: 
			resourceId = R.drawable.xgame29;
			break;
		case 30: 
			resourceId = R.drawable.xgame30;
			break;
		case 31: 
			resourceId = R.drawable.xgame31;
			break;
		case 32: 
			resourceId = R.drawable.xgame32;
			break;
		case 33: 
			resourceId = R.drawable.xgame33;
			break;
		case 34: 
			resourceId = R.drawable.xgame34;
			break;
		case 35:
			resourceId = R.drawable.xgame35;
			break;
		case 36: 
			resourceId = R.drawable.xgame36;
			break;
		case 37: 
			resourceId = R.drawable.xgame37;
			break;
		case 38: 
			resourceId = R.drawable.xgame38;
			break;
		case 39: 
			resourceId = R.drawable.xgame39;
			break;
		case 40: 
			resourceId = R.drawable.xgame40;
			break;
		case 41: 
			resourceId = R.drawable.xgame41;
			break;
		case 42: 
			resourceId = R.drawable.xgame42;
			break;
		case 43: 
			resourceId = R.drawable.xgame43;
			break;
		case 44:
			resourceId = R.drawable.xgame44;
			break;
		case 45: 
			resourceId = R.drawable.xgame45;
			break;
		case 46: 
			resourceId = R.drawable.xgame46;
			break;
		case 47: 
			resourceId = R.drawable.xgame47;
			break;
		case 48: 
			resourceId = R.drawable.xgame48;
			break;
		case 49: 
			resourceId = R.drawable.xgame49;
			break;
		case 50: 
			resourceId = R.drawable.xgame50;
			break;
		case 51: 
			resourceId = R.drawable.xgame51;
			break;
		case 52: 
			resourceId = R.drawable.xgame52;
			break;
		case 53:
			resourceId = R.drawable.xgame53;
			break;
		case 54: 
			resourceId = R.drawable.xgame54;
			break;	
		case 55: 
			resourceId = R.drawable.xgame55;
			break;
		case 56: 
			resourceId = R.drawable.xgame56;
			break;
		case 57: 
			resourceId = R.drawable.xgame57;
			break;
		case 58: 
			resourceId = R.drawable.xgame58;
			break;
		case 59: 
			resourceId = R.drawable.xgame59;
			break;
		case 60: 
			resourceId = R.drawable.xgame60;
			break;
		case 61: 
			resourceId = R.drawable.xgame61;
			break;
		case 62:
			resourceId = R.drawable.xgame62;
			break;
		case 63: 
			resourceId = R.drawable.xgame63;
			break;
		case 64: 
			resourceId = R.drawable.xgame64;
			break;
		case 65: 
			resourceId = R.drawable.xgame65;
			break;
		case 66: 
			resourceId = R.drawable.xgame66;
			break;
		case 67: 
			resourceId = R.drawable.xgame67;
			break;
		case 68: 
			resourceId = R.drawable.xgame68;
			break;
		case 69: 
			resourceId = R.drawable.xgame69;
			break;
		case 70: 
			resourceId = R.drawable.xgame70;
			break;
		case 71:
			resourceId = R.drawable.xgame71;
			break;
		case 72: 
			resourceId = R.drawable.xgame72;
			break;
		case 73: 
			resourceId = R.drawable.xgame73;
			break;
		case 74: 
			resourceId = R.drawable.xgame74;
			break;
		case 75: 
			resourceId = R.drawable.xgame75;
			break;
		case 76: 
			resourceId = R.drawable.xgame76;
			break;
		case 77: 
			resourceId = R.drawable.xgame77;
			break;
		case 78: 
			resourceId = R.drawable.xgame78;
			break;
		case 79: 
			resourceId = R.drawable.xgame79;
			break;
		case 80:
			resourceId = R.drawable.xgame80;
			break;
		case 81: 
			resourceId = R.drawable.xgame81;
			break;
		case 82: 
			resourceId = R.drawable.xgame82;
			break;
		case 83: 
			resourceId = R.drawable.xgame83;
			break;
		case 84: 
			resourceId = R.drawable.xgame84;
			break;
		case 85: 
			resourceId = R.drawable.xgame85;
			break;
		case 86: 
			resourceId = R.drawable.xgame86;
			break;
		case 87: 
			resourceId = R.drawable.xgame87;
			break;
		case 88: 
			resourceId = R.drawable.xgame88;
			break;
		case 89:
			resourceId = R.drawable.xgame89;
			break;
		case 90: 
			resourceId = R.drawable.xgame90;
			break;	
		case 91: 
			resourceId = R.drawable.xgame91;
			break;
		case 92: 
			resourceId = R.drawable.xgame92;
			break;
		case 93: 
			resourceId = R.drawable.xgame93;
			break;
		case 94: 
			resourceId = R.drawable.xgame94;
			break;
		case 95: 
			resourceId = R.drawable.xgame95;
			break;
		case 96: 
			resourceId = R.drawable.xgame96;
			break;
		case 97: 
			resourceId = R.drawable.xgame97;
			break;
		case 98:
			resourceId = R.drawable.xgame98;
			break;
		case 99: 
			resourceId = R.drawable.xgame99;
			break;
		case 100: 
			resourceId = R.drawable.xgame100;
			break;
		case 101: 
			resourceId = R.drawable.xgame101;
			break;
		case 102: 
			resourceId = R.drawable.xgame102;
			break;
		case 103: 
			resourceId = R.drawable.xgame103;
			break;
		case 104: 
			resourceId = R.drawable.xgame104;
			break;
		case 105: 
			resourceId = R.drawable.xgame105;
			break;
		case 106: 
			resourceId = R.drawable.xgame106;
			break;
		case 107:
			resourceId = R.drawable.xgame107;
			break;
		case 108: 
			resourceId = R.drawable.xgame108;
			break;
		case 109: 
			resourceId = R.drawable.xgame109;
			break;
		case 110: 
			resourceId = R.drawable.xgame110;
			break;
		case 111: 
			resourceId = R.drawable.xgame111;
			break;
		case 112: 
			resourceId = R.drawable.xgame112;
			break;
		case 113: 
			resourceId = R.drawable.xgame113;
			break;
		case 114: 
			resourceId = R.drawable.xgame114;
			break;
		case 115: 
			resourceId = R.drawable.xgame115;
			break;
		case 116:
			resourceId = R.drawable.xgame116;
			break;
		case 117: 
			resourceId = R.drawable.xgame117;
			break;
		case 118: 
			resourceId = R.drawable.xgame118;
			break;
		case 119: 
			resourceId = R.drawable.xgame119;
			break;
		case 120: 
			resourceId = R.drawable.xgame120;
			break;
		case 121: 
			resourceId = R.drawable.xgame121;
			break;
		case 122: 
			resourceId = R.drawable.xgame122;
			break;
		case 123: 
			resourceId = R.drawable.xgame123;
			break;
		case 124: 
			resourceId = R.drawable.xgame124;
			break;
		case 125:
			resourceId = R.drawable.xgame125;
			break;
		case 126: 
			resourceId = R.drawable.xgame126;
			break;	
		case 127: 
			resourceId = R.drawable.xgame127;
			break;
		case 128: 
			resourceId = R.drawable.xgame128;
			break;
		case 129: 
			resourceId = R.drawable.xgame129;
			break;
		case 130: 
			resourceId = R.drawable.xgame130;
			break;
		case 131: 
			resourceId = R.drawable.xgame131;
			break;
		case 132: 
			resourceId = R.drawable.xgame132;
			break;
		case 133: 
			resourceId = R.drawable.xgame133;
			break;
		case 134:
			resourceId = R.drawable.xgame134;
			break;
		case 135: 
			resourceId = R.drawable.xgame135;
			break;
		case 136: 
			resourceId = R.drawable.xgame136;
			break;
		case 137: 
			resourceId = R.drawable.xgame137;
			break;
		case 138: 
			resourceId = R.drawable.xgame138;
			break;
		case 139: 
			resourceId = R.drawable.xgame139;
			break;
		case 140: 
			resourceId = R.drawable.xgame140;
			break;
		case 141: 
			resourceId = R.drawable.xgame141;
			break;
		case 142: 
			resourceId = R.drawable.xgame142;
			break;
		case 143:
			resourceId = R.drawable.xgame143;
			break;
		case 144: 
			resourceId = R.drawable.xgame144;
			break;
		default:
			resourceId = R.drawable.xgame0;
			break;
		}
		return resourceId;
	}
}
