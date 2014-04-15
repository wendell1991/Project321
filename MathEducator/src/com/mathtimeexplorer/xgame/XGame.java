package com.mathtimeexplorer.xgame;

import java.util.ArrayList;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matheducator.R;
import com.mathtimeexplorer.main.MainActivity;

public class XGame extends Activity {
	
	private Dialog scoreSheetDialog, countDownDialog;
	private ImageView valueOne, valueTwo, valAnswer, notifyView, countDownView;
	private ImageButton optionOne, optionTwo, optionThree, optionFour ,optionFive, optionSix, tryAgainBtn, giveUpBtn;
	private TextView timerText, totalQns, numCorrect, numWrong, totalMarks;
	
	// Count-down configuration
	private long COUNTDOWN_DURATION = 10000, COUNTDOWN_INTERVAL = 1000;
	// Timer configuration
	private long TIMER_DURATION = 30000, TIMER_INTERVAL = 10;
	
	private Context context = this;
	private MediaPlayer bkgrdMusic;
	private ArrayList<ImageButton> optionArrayList;
	private Random rand = new Random();
	private XGameScore scoreSheet = new XGameScore();
	private XGamePaint paintHandler = new XGamePaint(context);
	private ArrayList<XGameNumbers> numList = new ArrayList<XGameNumbers>();
	
	// Tags to help determine user's choice
	// Option 1 & 2 also help to display correct or wrong image
	private static int OPTION_1 = 1; 
	private static int OPTION_2 = 2;
	private static int OPTION_3 = 3;
	private static int OPTION_4 = 4;
	private static int OPTION_5 = 5;
	private static int OPTION_6 = 6;
	
	private int sixty = 60;
	private static int VALUE_ZERO = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xgame_main);
		
		// Initialize UIs (Only once)
		init();
		new LoadGameData().execute();
		
		optionOne.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_1);
					}
				}	
				return true;
			}
		});
		optionTwo.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_2);
					}
				}	
				return true;
			}
		});
		optionThree.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_3);
					}
				}	
				return true;
			}
		});
		optionFour.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_4);
					}
				}	
				return true;
			}
		});
		optionFive.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_5);
					}
				}	
				return true;
			}
		});
		optionSix.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						checkAnswer(OPTION_6);
					}
				}	
				return true;
			}
		});
	}

	private void checkAnswer(int optionSelected){
		int correct = scoreSheet.getCorrect();
		int wrong = scoreSheet.getWrong();	
		int qnsAttempted = scoreSheet.getQnsAttempted();
		XGameNumbers nh = numList.get(VALUE_ZERO);
		
		// Remove the current numberHandler from the list to retrieve the next object
		numList.remove(VALUE_ZERO);
		XGameNumbers nextNh = numList.get(VALUE_ZERO);
		
		switch(optionSelected) {
			case 1: {
				if (optionOne.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);
					genGameContent(nextNh);
					correct++;
				} else {	
					animateResult(OPTION_2);
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
			case 2: {
				if (optionTwo.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);
					genGameContent(nextNh);
					correct++;
				} else {
					animateResult(OPTION_2);
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
			case 3: {
				if (optionThree.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);
					genGameContent(nextNh);
					correct++;
				} else {
					animateResult(OPTION_2);
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
			case 4: {
				if (optionFour.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);
					genGameContent(nextNh);
					correct++;
				} else {
					animateResult(OPTION_2);
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
			case 5: {
				if (optionFive.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);
					genGameContent(nextNh);
					correct++;
				} else {
					animateResult(OPTION_2);					
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
			case 6: {
				if (optionSix.getId() == nh.getAnsPosition()) {
					animateResult(OPTION_1);					
					genGameContent(nextNh);
					correct++;
				} else {
					animateResult(OPTION_2);
					genGameContent(nextNh);
					wrong++;
				}
				break;
			}
		}
		qnsAttempted++;
		
		// Updates the score-sheet
		scoreSheet.setCorrect(correct);
		scoreSheet.setWrong(wrong);
		scoreSheet.setQnsAttempted(qnsAttempted);
	}
	
	private void animateResult(int rightOrWrong) {
		final MediaPlayer soundEff;
		if (rightOrWrong == 1) {
			// Option 1 = Correct
			notifyView.setImageResource(R.drawable.correct);
			soundEff = MediaPlayer.create(XGame.this, R.raw.xgamecorrect);
		} else {
			// Option 2 = Wrong
			notifyView.setImageResource(R.drawable.wrong);
			soundEff = MediaPlayer.create(XGame.this, R.raw.xgamewrong);
		}
		
		soundEff.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				soundEff.release();
			}
		});
		
		soundEff.start();
		
		// Check if view is visible, set to visible if it is not
		if (notifyView.getVisibility() == View.GONE) {
			notifyView.setVisibility(View.VISIBLE);
		}
		notifyView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink));
	}
	
	private void callScoreSheetDialog(){
		
		// Calls the score sheet dialog box
		scoreSheetDialog = new Dialog(context);
		scoreSheetDialog.setContentView(R.layout.xgame_scoresheet);
		scoreSheetDialog.getWindow().setWindowAnimations(R.style.XGameScoreDialogAnimation);
		
		// Initialize the widgets
		totalQns = (TextView) scoreSheetDialog.findViewById(R.id.xgameQnsAttempted);
		numCorrect = (TextView) scoreSheetDialog.findViewById(R.id.xgameQnsCorrect);
		numWrong = (TextView) scoreSheetDialog.findViewById(R.id.xgameQnsWrong);
		totalMarks = (TextView) scoreSheetDialog.findViewById(R.id.xgameTotalScore);
		tryAgainBtn = (ImageButton) scoreSheetDialog.findViewById(R.id.xgameTryAgain);
		giveUpBtn = (ImageButton) scoreSheetDialog.findViewById(R.id.xgameGiveUp);
		 
		// Display results
		int qnsAttempted = scoreSheet.getQnsAttempted();
		int correct= scoreSheet.getCorrect();
		int wrong = scoreSheet.getWrong();
		totalQns.setText(String.valueOf(qnsAttempted));
		numCorrect.setText(String.valueOf(correct));
		numWrong.setText(String.valueOf(wrong));
		
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
						scoreSheetDialog.dismiss();
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
						scoreSheetDialog.dismiss();
						startActivity(intent);
					}
				}
				return true;
			}
		});
		scoreSheetDialog.show();
	}

	private void genGameContent(XGameNumbers nh){

		int whichToHide = nh.getWhichToHide();
		int firstNumber = nh.getFirstNumber();
		int secondNumber = nh.getSecondNumber();
		int result = nh.getResult();
		
		// Hide either firstNumber, secondNumber or result randomly and paint them
		if (whichToHide == firstNumber) {
			valueOne.setImageResource(R.drawable.qnmark);
			paintHandler.configureImage(secondNumber, valueTwo);
			paintHandler.configureImage(result, valAnswer);
		}
		else if (whichToHide == secondNumber) {
			valueTwo.setImageResource(R.drawable.qnmark);
			paintHandler.configureImage(firstNumber, valueOne);
			paintHandler.configureImage(result, valAnswer);
		}
		else {
			valAnswer.setImageResource(R.drawable.qnmark);
			paintHandler.configureImage(firstNumber, valueOne);
			paintHandler.configureImage(secondNumber, valueTwo);
		}
		
		// Paint the option image buttons
		ArrayList<Integer> optionList = nh.getOptions();
		
		ImageButton curBtn;
		rand = new Random();
		int randomVal = 0;
		int index = 6;
		int curOptionVal;
		
		// Randomize positions of all the options
		for (int i = 0; i < optionArrayList.size(); i++) {
			curBtn = (ImageButton) optionArrayList.get(i);
			randomVal = rand.nextInt(index);
			curOptionVal = (Integer) optionList.get(randomVal);
			// Saves the position which the answer is located
			if (curOptionVal == nh.getAnswer()) {
				nh.setAnsPosition(curBtn.getId());
				// Save the changes
				numList.set(VALUE_ZERO, nh);
			}
			paintHandler.configureImage(curOptionVal, curBtn);
			optionList.remove(randomVal);
			index--;
		}
	}

	private XGameNumbers generateGameValues() {
		XGameNumbers nh = new XGameNumbers();
		ArrayList<Integer> itemToHide = new ArrayList<Integer>();

		int firstNumber = 0;
		int secondNumber = 0;
		while (true) {
			// Ensure that firstNumber and SecondNumber cannot be both be 0
			if (firstNumber == 0 && secondNumber == 0){
				// Generate values ranging 0 - 12
				firstNumber = rand.nextInt(13);
				secondNumber = rand.nextInt(13);
			} else {
				break;
			}
		}
		
		// Compute result of firstNumber * secondNumber
		int result = firstNumber * secondNumber;
		nh.setFirstNumber(firstNumber);
		nh.setSecondNumber(secondNumber);
		nh.setResult(result);
		
		// Determines which to hide. 
		int whichToHide = rand.nextInt(3);
		itemToHide.add(firstNumber);
		itemToHide.add(secondNumber);
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
		
		ArrayList<Integer> optionList = new ArrayList<Integer>();
		
		/*
		 * 0 Timetable is special, e.g x divide 0 will cause arithmetic exception
		 * If result is 0, either firstNumber or secondNumber is 0 
		 * Generation scheme: -Produce 2 options close to the answer
		 * 					  -Produce 3 randomly generated options
		*/
		if (result == 0) {
			// Anything multiplies by 0 will be 0
			nh.setAnswer(VALUE_ZERO);
			// Statically put 0, (0 + 1), (0 + 2) as the options
			optionList.add(VALUE_ZERO);
			optionList.add(result + 1);
			optionList.add(result + 2);
		} else {
			// Determines the answer, depending on which value to hide
			int tempResult = 0;
			if (whichToHide == firstNumber) {
				tempResult = result / secondNumber;
			} else if (whichToHide == secondNumber) {
				tempResult = result / firstNumber;
			} else {
				tempResult = result;
			}
			nh.setAnswer(tempResult);
			optionList.add(tempResult);
			// Produce two values close to the answer
			optionList.add(tempResult + 1);
			optionList.add(tempResult - 1);
		}
		
		/* 
		 * Makes sure value of the random options are not repeated
		 * Generate options ranging from 0-100, exit loop when we have 3 options
		 */
		int prevVal = 101;
		int curVal = 0;
		while (true) {
			curVal = rand.nextInt(101);
			if (curVal != prevVal) {
				optionList.add(curVal);
				prevVal = curVal;
			}
			// Already should contain 3 items, 6 - 3 = 3
			if (optionList.size() == 6) {
				break;
			}
		}
		nh.setOptions(optionList);
		return nh;
	}
	
	private void init() {
		timerText = (TextView) findViewById(R.id.timerText);
		notifyView = (ImageView) findViewById(R.id.notifyView);
		valueOne = (ImageView) findViewById(R.id.valueOne);
		valueTwo = (ImageView) findViewById(R.id.valueTwo);
		valAnswer = (ImageView) findViewById(R.id.valAnswer);
		optionOne = (ImageButton) findViewById(R.id.optionOne);
	    optionTwo = (ImageButton) findViewById(R.id.optionTwo);
	    optionThree = (ImageButton) findViewById(R.id.optionThree);
		optionFour = (ImageButton) findViewById(R.id.optionFour);
		optionFive = (ImageButton) findViewById(R.id.optionFive);
		optionSix = (ImageButton) findViewById(R.id.optionSix);
		
		// Add the options into the array
		optionArrayList = new ArrayList<ImageButton>();
		optionArrayList.add(optionOne);
		optionArrayList.add(optionTwo);
		optionArrayList.add(optionThree);
		optionArrayList.add(optionFour);
		optionArrayList.add(optionFive);
		optionArrayList.add(optionSix);
	}
	
	class LoadGameData extends AsyncTask<String, String, String> {
		
		private ProgressDialog pDialog;
		private static final String DIALOG_TITLE = "Game Loading...";
		private static final String DIALOG_MESSAGE = "Please wait.";
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(XGame.this);
            pDialog.setTitle(DIALOG_TITLE);
            pDialog.setMessage(DIALOG_MESSAGE);
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
			genGameContent(numList.get(VALUE_ZERO));
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
			
		    final Window window = countDownDialog.getWindow();
		    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		    
			countDownView = (ImageView) countDownDialog.findViewById(R.id.xgameTimesUp);
			countDownDialog.show();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) % sixty;
			/*
			 * When seconds = 5, display ready?
			 * When seconds = 4, display 3
			 * When seconds = 3, display 2
			 * when seconds = 2, display 1
			 * When seconds = 1, display start
			*/
			if (seconds == 8) {
				countDownView.setImageResource(R.drawable.timethree);
				countDownView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
			} else if (seconds == 6) {
				countDownView.setImageResource(R.drawable.timetwo);
				countDownView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
			} else if (seconds == 4) {
				countDownView.setImageResource(R.drawable.timeone);
				countDownView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
			} else if (seconds == 2) {
				countDownView.setImageResource(R.drawable.xgame_start);
				countDownView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
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
			
			// Create a count-down timer, starting number: 30 seconds
			CountDown count = new CountDown(TIMER_DURATION, TIMER_INTERVAL);
			count.start();
		}
	}
	
	class CountDown extends CountDownTimer {
		private String TAG_ZERO = "0";
		private String TIME_FINISH = "00:00";
		private String TIME_LEFT = "Time left: ";
		private String STRING_COLON = ":";

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}
		
		@Override
        public void onFinish() {
			timerText.setText(TIME_LEFT+TIME_FINISH);
			if (bkgrdMusic.isPlaying()) {
				bkgrdMusic.release();
			}
			callScoreSheetDialog();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	String currentSeconds = "";
        	long milliseconds = (TimeUnit.MILLISECONDS.toMillis((millisUntilFinished)) % sixty);
        	long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) % sixty;
        	
        	// If time left is less than 10 seconds
        	if (seconds < 10) {
        		timerText.setTextColor(Color.RED);
        	}
        	
        	// Format seconds and milliseconds, ensure both is two digits
        	if (String.valueOf(seconds).length() == 2) {
        		currentSeconds = String.valueOf(seconds);
        	} else {
        		currentSeconds = TAG_ZERO + seconds;
        	}
        	if (String.valueOf(milliseconds).length() == 2) {
        		timerText.setText(TIME_LEFT+currentSeconds+STRING_COLON+milliseconds);
        	} else {
        		timerText.setText(TIME_LEFT+currentSeconds+STRING_COLON+TAG_ZERO+milliseconds);
        	}
        }
	}
}
