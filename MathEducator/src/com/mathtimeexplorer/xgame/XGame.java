package com.mathtimeexplorer.xgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.ranking.RankingResult;
import com.mathtimeexplorer.utils.Constants;

public class XGame extends Activity {
	
	private TableLayout rankTable;
	private Dialog countDownDialog;
	private PopupWindow scoreSheetPopUp;
	
	private ImageView valueOne, valueTwo, valAnswer, optionOne, optionTwo, optionThree, 
					  optionFour, optionFive, optionSix, correctGreen, wrongRed, countDownView,
					  xgame_highscore;
	
	private ImageButton tryAgainBtn, giveUpBtn;
	private TextView timerText, numCorrect, numWrong, totalMarks;
	
	// Count-down configuration
	private long COUNTDOWN_DURATION = 6000, COUNTDOWN_INTERVAL = 1000;
	// Timer configuration
	private long TIMER_DURATION = 30000, TIMER_INTERVAL = 10;
	
	// Blink duration for correct / wrong
	private long animBlinkDuration = 1000;
	
	private int sixty = 60;
	private static int INDEX_ZERO = 0;
	private static int OPTION_CORRECT = 1; 
	private static int OPTION_WRONG = 2;
	
	private User user;
	private CountDown count = null;
	private GameStartTimer gst = null;
	private Context context = this;
	private MediaPlayer bkgrdMusic = null;
	private ArrayList<ImageView> optionArrayList;
	private ArrayList<XGameNumbers> numList = new ArrayList<XGameNumbers>();
	
	// Keep track of number of corrects & wrong
	private int correct = 0;
	private int wrong = 0;
	
	// Check the state of the game
	private boolean isGameStarted = false;
	private boolean isCountDownFinished = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xgame_main);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getParcelable(Constants.USER);
		}
		
		// Initialize UIs (Only once)
		init();
		new LoadGameData().execute();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (isGameStarted == true) {
			// Display dialog, prompts user whether he wants to resume or quit game
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			
			builder
				.setCancelable(false)
				.setTitle(R.string.resumeGameTitle)
				.setMessage(R.string.resumeGameMsg)
				.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub					
						// User quits the game, returns to previous activity
						dialog.cancel();
						finish();						
					}
				})
				
				.setNegativeButton(R.string.resume, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						long timeLeft;
						
						// If count-down is not finished, resume the count-down
						if (isCountDownFinished == true) {
							// Resumes the count-down & background music
							timeLeft = count.getTimeLeft();
							count = new CountDown(timeLeft, TIMER_INTERVAL);
							count.start();
							bkgrdMusic.start();
						
							// Populate the next question
							if (numList.size() > 0) {
								numList.remove(INDEX_ZERO);
								XGameNumbers nextNh = numList.get(INDEX_ZERO);
								genGameContent(nextNh);
							} else {
								// If exceeds the number of questions generated, reloads the game
								Intent intent = getIntent();
								finish();
								startActivity(intent);
							}
						} else {
							timeLeft = gst.getTimeLeft();
							gst = new GameStartTimer(timeLeft, COUNTDOWN_INTERVAL);
							gst.start();
						}
					}
				});
			
			// Creates the dialog
			AlertDialog dialog = builder.create();
			dialog.show();
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
			// Pause background music if started
			if (bkgrdMusic != null) {
				bkgrdMusic.pause();
			}
			// Pause count-downs if started
			if (count != null) {
				count.cancel();
			}
			if (gst != null) {
				countDownDialog.dismiss();
				gst.cancel();
			}
		}
	}
	
	// onClick Handler to check user's input
	public void checkAnswer(View view){
		Object tag = view.getTag();
		int selectedValue = tag == null ? -1 : (Integer) tag;
		
		XGameNumbers nh = numList.get(INDEX_ZERO);
		
		// Remove the current item to get nextNh
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
			correctGreen.setVisibility(View.VISIBLE);
			correctGreen.startAnimation(blink);
			delayStopAnimation(correctGreen);
			soundEff = MediaPlayer.create(XGame.this, R.raw.xgamecorrect);
		} else {
			// Wrong
			wrongRed.setVisibility(View.VISIBLE);
			wrongRed.startAnimation(blink);
			delayStopAnimation(wrongRed);
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
	
	// Wait for a duration before stopping the blink animation
	private void delayStopAnimation(final ImageView view) {
		Handler handler = new Handler(); 
		
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 view.clearAnimation();
	        	 view.setVisibility(View.INVISIBLE);
	         } 
	    }, animBlinkDuration);
	}
	
	private void callScoreSheetDialog(){
		
		RelativeLayout xgame_main = (RelativeLayout) findViewById(R.id.xgame_main);
        
		RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsImage.addRule(RelativeLayout.CENTER_IN_PARENT);
		
        final ImageView timesUp = new ImageView(this);
		timesUp.setImageResource(R.drawable.game_timesup);
		timesUp.setLayoutParams(paramsImage);
		
		xgame_main.addView(timesUp);
		setContentView(xgame_main);
		
		Animation bounceRotate = AnimationUtils.loadAnimation(context, R.anim.bounce_rotate);
		final Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		
		bounceRotate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				timesUp.startAnimation(fadeOut);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				int totalScore = 0;
				
				// Find total Score
				if (correct != 0 || correct > wrong) {
					totalScore = correct - wrong;
				}
				
				// Guest user is playing the game if user = null
				if (user == null) {
					new ManageGameResults().execute();
				} else {
					new ManageGameResults(user.getApp_user_id(),
							 user.getSchool_id(), totalScore).execute();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		timesUp.startAnimation(bounceRotate);
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
	
	class ManageGameResults extends AsyncTask<String, String, String> {

		private int userId;
		private int schoolId;
		private int score;
		private int success;
		private RankingResult rankResult;
		private ProgressDialog pDialog;
		
		public ManageGameResults() {
			// Indicate that guest user is playing the game 
			this.userId = -1;
		}
		
		public ManageGameResults(int userId, int schoolId, int score) {
			this.userId = userId;
			this.schoolId = schoolId;
			this.score = score;
		}
		
		@Override
        protected void onPreExecute() {
			super.onPreExecute();
            pDialog = new ProgressDialog(XGame.this);
            pDialog.setTitle(Constants.TITLE_GEN_SCORE);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// If its guest user, we do not send any request to server
			if (userId != -1) {
				JSONParser jsonParser = new JSONParser();
				
				// Parameters for the POST request
				List<NameValuePair> params = new ArrayList<NameValuePair>();	
				params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
				params.add(new BasicNameValuePair("type", Constants.XGAME_TYPE));
				params.add(new BasicNameValuePair("score", String.valueOf(score)));
	            params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
	            
	            JSONObject json = jsonParser.makeHttpRequest(
	            		Constants.URL_MANAGE_GAME_RESULT, Constants.HTTP_POST, params);
	            
	            rankResult = new RankingResult();
	            
	            try{
					success = json.getInt(Constants.JSON_SUCCESS);
	            } catch (JSONException e) {
					Log.i(Constants.LOG_XGAME, e.toString());
	            }
	            
	            // Retrieve XGame rank results
	            // Remove score parameter to suit the retrieve operation
	            params.remove(2);
	            
	            json = jsonParser.makeHttpRequest(Constants.URL_GAME_RESULT, 
	            		Constants.HTTP_GET, params);
	            
	            rankResult.getRankingResults(json);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			
			LayoutInflater inflater = (LayoutInflater) XGame.this.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View layout = inflater.inflate(R.layout.xgame_scoresheet, 
					(ViewGroup) findViewById(R.id.xgame_scoresheet));
			
			scoreSheetPopUp = new PopupWindow(layout, 1100, 650, true);
			scoreSheetPopUp.setAnimationStyle(R.style.Animation_Bounce);
			
			// Initialize the widgets
			numCorrect = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameQnsCorrect);
			numWrong = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameQnsWrong);
			totalMarks = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.xgameTotalScore);
			xgame_highscore = (ImageView) scoreSheetPopUp.getContentView().findViewById(R.id.xgame_highscore);
			tryAgainBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.xgameTryAgain);
			giveUpBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.xgameGiveUp);
			rankTable = (TableLayout) scoreSheetPopUp.getContentView().findViewById(R.id.xgame_rank_tablelayout);
			 
			String spacing = " ";
			Resources resource = getResources();
			
			// Display results
			numCorrect.setText(resource.getString(R.string.qnsCorrect) + spacing + correct);
			numWrong.setText(resource.getString(R.string.qnsWrong) + spacing + wrong);
			totalMarks.setText(resource.getString(R.string.totalScore) + spacing + score);
			
			// If guest user, we do not display result on rank-table or check high score
			if (userId != -1) {
				// No results
				if (rankResult.getSuccess() == 0) {
								
				} else {
					rankResult.setRankTableResults(rankTable);
				}
							
				// If success = 1 (New entry), = 2 (Better score update)
				if (success == 1 || success == 2) {
					// Display new high score
					xgame_highscore.setVisibility(View.VISIBLE);
				}
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
	}
	
	class LoadGameData extends AsyncTask<String, String, String> {
		
		private ProgressDialog pDialog;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(XGame.this);
            pDialog.setTitle(Constants.TITLE_GAME_LOADING);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
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
			
			ImageView curView;
			
			// Disables all the option buttons first until count-down finishes
			for (int i = 0; i < optionArrayList.size(); i++) {
				curView = (ImageView) optionArrayList.get(i);
				curView.setEnabled(false);
			}
		
			isGameStarted = true;
			gst = new GameStartTimer(COUNTDOWN_DURATION, COUNTDOWN_INTERVAL);
			gst.start();
		}
	}
	
	class GameStartTimer extends CountDownTimer {
		
		private long timeLeft = 0;

		public GameStartTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
			countDownDialog = new Dialog(context);	
			countDownDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			countDownDialog.setCancelable(false);
			countDownDialog.setContentView(R.layout.dialog_timer);
				
			// Makes the count-down dialog-box transparent
		    final Window window = countDownDialog.getWindow();
		    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		    
			countDownView = (ImageView) countDownDialog.findViewById(R.id.xgame_countdown_ready); 
			countDownView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
			countDownDialog.show();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) % sixty;
			if (seconds == 4) {
				countDownView.setImageResource(R.drawable.countdown_3);
			} else if (seconds == 3) {
				countDownView.setImageResource(R.drawable.countdown_2);
			} else if (seconds == 2) {
				countDownView.setImageResource(R.drawable.countdown_1);
			} else if (seconds == 1) {
				countDownView.setImageResource(R.drawable.xgame_start);
			}
			setTimeLeft(millisUntilFinished);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			countDownView.setVisibility(View.GONE);
			countDownDialog.dismiss();
			
			ImageView curView;
			
			// Enable all the buttons upon count-down finish
			for (int i = 0; i < optionArrayList.size(); i++) {
				curView = (ImageView) optionArrayList.get(i);
				curView.setEnabled(true);
			}
			
			// Start background music
			bkgrdMusic = MediaPlayer.create(XGame.this, R.raw.xgamemusic);
			bkgrdMusic.start();
			
			// Create a 30 seconds count-down timer 
			count = new CountDown(TIMER_DURATION, TIMER_INTERVAL);
			count.start();		
			isCountDownFinished = true;
		}
		
		public long getTimeLeft() {
			return timeLeft;
		}

		public void setTimeLeft(long timeLeft) {
			this.timeLeft = timeLeft;
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
			if (bkgrdMusic != null) {
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