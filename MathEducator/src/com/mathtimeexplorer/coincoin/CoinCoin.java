package com.mathtimeexplorer.coincoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class CoinCoin extends Activity {
	
	private RelativeLayout coincoin_layout;
	private TableLayout rankTable;
	private PopupWindow storyPopUp, scoreSheetPopUp;
	private TextView question, coincoin_tapStart, timeTaken, score_timeTaken, score_noofflips;
	private ImageButton tryAgainBtn, giveUpBtn;
	
	private ImageView 
			frontViewOne, backViewOne, frontViewTwo, backViewTwo, frontViewThree, backViewThree, 
			frontViewFour, backViewFour, frontViewFive, backViewFive, frontViewSix, backViewSix, 
			frontViewSeven, backViewSeven, frontViewEight, backViewEight, frontViewNine, backViewNine,
			frontViewTen, backViewTen, frontViewEleven, backViewEleven, frontViewTwelve, backViewTwelve,
			coincoin_highscore;
	
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
	
	// Check the state of the game
	private boolean isGameStarted = false;
	
	private int questionIndex = 0;
	private static final int flipFront = 1;
	private static final int flipBack = 2;
	private static final String TAG_CENTS = " cents";
	private static final String TAG_QUESTION = "YOUR MUM WANTS YOU TO FIND: ";
	
	private int[] cardFrontList = {R.drawable.cardfront_orange, 
			R.drawable.cardfront_purple, R.drawable.cardfront_blue}; 
	
	private User user;
	private Context context = this;
	private AnimatorSet flip_left_in; 
	private AnimatorSet flip_right_in;
	private Animation fadeOut;
	private MediaPlayer bkgrdMusic = null;
	private ArrayList<Integer> questionList;
	private ArrayList<Integer> valueList;
	private Drawable coincoin_bkgrd;
	
	// Keep track of the number of cards the user flips
	private ArrayList<ImageView> flipList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coincoin_main);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getParcelable(Constants.USER);
		}
		
		initMain();
		
		coincoin_bkgrd = getResources().getDrawable(R.drawable.coincoinbkgrd);
		
		flipList = new ArrayList<ImageView>();
		
		flip_left_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
		flip_right_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
		fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		
		// Checks if time exceeds 5mins, end the game
		timeTaken.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String timeResult = timeTaken.getText().toString();
				timeResult = timeResult.substring(timeResult.indexOf(":") + 1).trim();
				String[] formatString = timeResult.split(STRING_COLON);
				
				// If over 5mins call score-sheet pop-up
				if (formatString[0].equals("05")) {
					callScoreSheetPopUp(R.drawable.game_timesup);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub			
			}
		});
		
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
						// Resumes the timer & background music
						startTime = SystemClock.uptimeMillis();
						customHandler.postDelayed(updateTimerThread, 0);
						bkgrdMusic.start();
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
			if (bkgrdMusic != null) {
				// Pause both timer and background music
				timeSwapBuff += timeInMilliseconds;
				customHandler.removeCallbacks(updateTimerThread);				
				bkgrdMusic.pause();
			}
		}
	}
	
	private void processFlip(ImageView backView) {
		flipList.add(backView);

		if (flipList.size() == 2) {
			
			flipTries++;
			
			final ImageView backViewOne = (ImageView) flipList.get(0);
			final ImageView backViewTwo = (ImageView) flipList.get(1);

			Object tagOne = backViewOne.getTag();
			Object tagTwo = backViewTwo.getTag();
			final int firstNumber = tagOne == null ? -1 : (Integer) tagOne;
			final int secondNumber = tagTwo == null ? -1 : (Integer) tagTwo;
			
			// Find and format the value of the question given 
			String[] values = question.getText().toString().split(STRING_COLON);			
			final String qnGiven = (String) values[1].replace(TAG_CENTS, "").trim();
			
			Handler handler = new Handler(); 
			
			// Sleep 2 seconds
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		        	// Checks if the values of the two cards adds up to the correct value
		        	 if (Integer.valueOf(qnGiven) == (firstNumber + secondNumber)) {
		        		 
		        		fadeOut.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub
								// Hide the two back-views upon animation ended
								backViewOne.setVisibility(View.INVISIBLE);
								backViewTwo.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
								
							}
		        		});
		        		
		 				backViewOne.startAnimation(fadeOut);
		 				backViewTwo.startAnimation(fadeOut);
		 				
		 				questionIndex++;
		 				
		 				// Call score-sheet pop-up since the last question is answered
		 				if (questionIndex > 6) {
		 					callScoreSheetPopUp(R.drawable.coincoin_congrats);
		 				} else {
		 					// Populate the next question
		 					question.setText(TAG_QUESTION + 
		 							questionList.get(questionIndex) + TAG_CENTS);
		 				}
		 			} else {
		 				onBackViewClick(backViewOne);
		 				onBackViewClick(backViewTwo);
		 				//callScoreSheetPopUp(R.drawable.coincoin_congrats);
		 			} 
		         } 
		    }, 1000);
			
			// Resets flips selected
			flipList.clear();
		}
	}
	
	
	private CoinCoinValues generateGameValues() {
		CoinCoinValues values = new CoinCoinValues();
		Random rand = new Random();
		
	    while (true) {
	    	int actualValue = 0;
			int firstNumber = 0;
			int secondNumber = 0;
	    	
	    	// Random generate a value between 1 - 200
	    	int randomVal = rand.nextInt(200) + 1;
			
	    	// Ensures that randomVal will not be 0 or less than 5
	    	if (randomVal >= 10) {
	    		while (true) {    			
	    			// Round the generated value to be a multiple of 5
	    			actualValue = 5 * (Math.round(randomVal / 5));
	    			
	    			// Ensure that actualValue will not be 0
	    			if (actualValue != 0) {				
	    				while (true) {
	    					randomVal = rand.nextInt(actualValue) + 1;
	    					firstNumber = 5 * (Math.round(randomVal / 5));
	    					if (firstNumber == 5 || firstNumber > 10) {
		    					secondNumber = actualValue - firstNumber;

		    					values.setActualValue(actualValue);
		    				    values.setFirstNumber(firstNumber);
		    				    values.setSecondNumber(secondNumber);
		    					break;
		    				}	
	    				}
	    				break;
	    			}	    			
	    		}
	    		break;
	    	}  	
	    }
	    return values;
	}
	
	// On-click handler for frontViews
	public void onFrontViewClick(View v) {
		CustomAnimationListener myListener = new CustomAnimationListener(flipFront,
				(ImageView) v, findCorrespondingView(v));
		flip_right_in.addListener(myListener);
		flip_right_in.setTarget(v);
		flip_right_in.start();
		startFlipSoundEffect();
	}
	
	private void onBackViewClick(View v) {
		CustomAnimationListener myListener = (new CustomAnimationListener(flipBack, 
				findCorrespondingView(v), (ImageView) v));
		flip_left_in.addListener(myListener);
		flip_left_in.setTarget(v);
		flip_left_in.start();
		startFlipSoundEffect();
	}
	
	private void startFlipSoundEffect() {
		final MediaPlayer soundEff = MediaPlayer.create(CoinCoin.this, R.raw.cardflip);
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
	
	private void callScoreSheetPopUp(int imageToAnimate) {
		
		// Stops the timer first
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		
		RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsImage.addRule(RelativeLayout.CENTER_IN_PARENT);
		
        final ImageView timesUp = new ImageView(this);
		timesUp.setImageResource(imageToAnimate);
		timesUp.setLayoutParams(paramsImage);
		
		coincoin_layout.addView(timesUp);
		setContentView(coincoin_layout);
		
		Animation bounceRotate = AnimationUtils.loadAnimation(context, R.anim.bounce_rotate);
		final Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		
		bounceRotate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				// Stop the background music just before times-up animation starts
				if (bkgrdMusic.isPlaying() == true) {
					bkgrdMusic.reset();
					bkgrdMusic.release();
				}
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
				 new ManageGameResults(user.getApp_user_id(),
						 user.getSchool_id(), flipTries).execute();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		timesUp.startAnimation(bounceRotate);
	}
	
	private void callStoryPopUpWindow(final int selectedCardResId, final ArrayList<ImageView> frontViewList) {
		
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
						question.setText(TAG_QUESTION + 
								questionList.get(questionIndex) + TAG_CENTS);			
						
						questionIndex++;
						
						// Set activity screen to fully opaque
						coincoin_bkgrd.setAlpha(255);
						coincoin_layout.setBackground(coincoin_bkgrd);
						setContentView(coincoin_layout);
						
						ImageView currentView = null;
						
						// Set visibility and image resource for each front view
						for (int i = 0; i < frontViewList.size(); i++) {
							currentView = (ImageView) frontViewList.get(i);
							currentView.setImageResource(selectedCardResId);
							currentView.setVisibility(View.VISIBLE);
						}						
						
						storyPopUp.dismiss();
						
						// Start background music
						bkgrdMusic = MediaPlayer.create(CoinCoin.this, R.raw.irishtavern);
						bkgrdMusic.start(); 
						
						// Start the timer
						startTime = SystemClock.uptimeMillis();
						customHandler.postDelayed(updateTimerThread, 0);						
						isGameStarted = true;
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
	
	private void initMain() {
		coincoin_layout = (RelativeLayout) findViewById(R.id.coincoin_main);
		
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
					backView.setVisibility(View.VISIBLE);
					frontView.setVisibility(View.INVISIBLE);
					flip_right_in.removeAllListeners();
					
					// Process flip only when front-View is flip
					processFlip(backView);
					break;
				}
				case 2: {
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
	
	class ManageGameResults extends AsyncTask<String, String, String> {

		private int userId;
		private int schoolId;
		private int score;
		private int success;
		private RankingResult rankResult;
		private ProgressDialog pDialog;
		
		public ManageGameResults(int userId, int schoolId, int score) {
			this.userId = userId;
			this.schoolId = schoolId;
			this.score = score;
		}
		
		@Override
        protected void onPreExecute() {
			super.onPreExecute();
            pDialog = new ProgressDialog(CoinCoin.this);
            pDialog.setTitle(Constants.TITLE_GEN_SCORE);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			JSONParser jsonParser = new JSONParser();

			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();	
			params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
			params.add(new BasicNameValuePair("type", Constants.COINCOIN_TYPE));
			params.add(new BasicNameValuePair("score", String.valueOf(score)));
            params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
            
            JSONObject json = jsonParser.makeHttpRequest(
            		Constants.URL_MANAGE_GAME_RESULT, Constants.HTTP_POST, params);
               
            try{
				success = json.getInt(Constants.JSON_SUCCESS);
				
            } catch (JSONException e) {
				Log.i(Constants.LOG_COINCOIN, e.toString());
            }
            
            // Retrieve CoinCoin rank results
            // Remove score parameter to suit the retrieve operation
            params.remove(2);

            json = jsonParser.makeHttpRequest(Constants.URL_GAME_RESULT,
            		Constants.HTTP_GET, params);
            
            rankResult = new RankingResult();
            rankResult.getRankingResults(json);
            
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			
			LayoutInflater inflater = (LayoutInflater) CoinCoin.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View layout = inflater.inflate(R.layout.coincoin_scoresheet, 
					(ViewGroup) findViewById(R.id.coincoin_scoresheet));
			
			scoreSheetPopUp = new PopupWindow(layout, 1100, 600, true);
			scoreSheetPopUp.setAnimationStyle(R.style.Animation_Bounce);
			
			// Initialize the widgets
			rankTable = (TableLayout) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_rank_tablelayout);
			score_timeTaken = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_timetaken);
			score_noofflips = (TextView) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_noofflips);
			tryAgainBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_tryagain);
			giveUpBtn = (ImageButton) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_giveup);			
						
			String spacing = " ";
			Resources resource = getResources();
			String timeResult = timeTaken.getText().toString();
			timeResult = timeResult.substring(timeResult.indexOf(":") + 1).trim();
			
			// Display results
			score_timeTaken.setText(resource.getString(R.string.coincoin_timetaken) + spacing +
					timeResult);
			score_noofflips.setText(resource.getString(R.string.coincoin_nooftries) + spacing +
					String.valueOf(flipTries));
			
			// No results
			if (rankResult.getSuccess() == 0) {
				
			} else {
				rankResult.setRankTableResults(rankTable);
			}
			
			// If success = 1 (New entry), = 2 (Better score update)
			if (success == 1 || success == 2) {
				// Display new high score image
				coincoin_highscore = (ImageView) scoreSheetPopUp.getContentView().findViewById(R.id.coincoin_highscore);
				coincoin_highscore.setVisibility(View.VISIBLE);
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
		
		private int selectedCardResId;
		private ArrayList<ImageView> frontViewList;
		private ArrayList<ImageView> backViewList;
		private ProgressDialog pDialog;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CoinCoin.this);
            pDialog.setTitle(Constants.TITLE_GAME_LOADING);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			frontViewList = new ArrayList<ImageView>();
			backViewList = new ArrayList<ImageView>();
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
			
			frontViewList.add(frontViewOne);
			frontViewList.add(frontViewTwo);
			frontViewList.add(frontViewThree);
			frontViewList.add(frontViewFour);
			frontViewList.add(frontViewFive);
			frontViewList.add(frontViewSix);
			frontViewList.add(frontViewSeven);
			frontViewList.add(frontViewEight);
			frontViewList.add(frontViewNine);
			frontViewList.add(frontViewTen);
			frontViewList.add(frontViewEleven);
			frontViewList.add(frontViewTwelve);			
			
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
			
			// Randomize and choose a card front
			Random random = new Random();
			int randomVal = random.nextInt(2) + 1;
			selectedCardResId = cardFrontList[randomVal];
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// Dim the activity screen
			coincoin_bkgrd.setAlpha(150);
			coincoin_layout.setBackground(coincoin_bkgrd);
			setContentView(coincoin_layout);
			
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
			
			pDialog.dismiss();
			callStoryPopUpWindow(selectedCardResId, frontViewList);
		}	
	}
	
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
		}
		return resourceId;
	}
}