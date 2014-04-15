package com.mathtimeexplorer.coincoin;

import java.text.DecimalFormat;
import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matheducator.R;

public class CoinCoin extends Activity {
	
	private Context context = this;
	private AnimatorSet flip_left_in; 
	private AnimatorSet flip_right_in; 
	private ImageView cardFaceOne, cardBackOne, cardFaceTwo, cardBackTwo;
	
	private static int flipFront = 1;
	private static int flipBack = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coincoin_main);
		
		initMain();
		
		final TextView question = (TextView) findViewById(R.id.coinCoinQn);
		Button nextQn = (Button) findViewById(R.id.button1);
		nextQn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CoinCoinValues result = generateGameValues();
				question.setText("$"+String.valueOf(result.getQn()));
			}
		});
		
		flip_left_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
		flip_right_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
		
		cardFaceOne.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						flip_right_in.addListener(new CustomAnimationListener(flipFront, cardFaceOne, cardBackOne));
						flip_right_in.setTarget(cardFaceOne);
						flip_right_in.start();
					}
				}	
				return true;
			}
		});
		
		cardBackOne.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						flip_left_in.addListener(new CustomAnimationListener(flipBack, cardFaceOne, cardBackOne));
						flip_left_in.setTarget(cardBackOne);
						flip_left_in.start();
					}
				}	
				return true;
			}
		});
		
		cardFaceTwo.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						flip_left_in.addListener(new CustomAnimationListener(flipFront, cardFaceTwo, cardBackTwo));
						flip_left_in.setTarget(cardFaceTwo);
						flip_left_in.start();
					}
				}	
				return true;
			}
		});
		
		cardBackTwo.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						flip_right_in.addListener(new CustomAnimationListener(flipBack, cardFaceTwo, cardBackTwo));
						flip_right_in.setTarget(cardBackTwo);
						flip_right_in.start();
					}
				}	
				return true;
			}
		});
	}
	
	// Keep track of the number of cards the user flips
	private int flipCount = 0;
	
	private void processFlipInput() {
		flipCount++;
		if (flipCount == 2) {
			Log.i("CoinCoin", "FlipCount equals to 2");
		}
		Log.i("CoinCoin", "FlipCount:" + flipCount);
	}
	
	private void initMain() {
		cardFaceOne = (ImageView) findViewById(R.id.cardFaceOne);
		//cardBackOne = (ImageView) findViewById(R.id.cardBackOne);
	    cardFaceTwo = (ImageView) findViewById(R.id.cardFaceTwo);
	    cardBackTwo = (ImageView) findViewById(R.id.cardBackTwo);
	    
	    
		TextView story = (TextView) findViewById(R.id.coinCoinStory);
		story.setText("Your Mum hides the pocket money of different" + "\n" +
					  "values under these cards. she wants you" + "\n" +
					  "to be familiar with handling money. Please" + "\n" +
					  "match the value below with two cards");
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

		@SuppressWarnings("deprecation")
		@Override
		public void onAnimationEnd(Animator animation) {
			// TODO Auto-generated method stub
			// Base on the flip direction, decide whether to hide front or back card
			switch (flipDirection) {
			case 1: {

			}
			case 2: {

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
	
	String[] randLastDigits = {".05", ".00"};
	
	private CoinCoinValues generateGameValues() {
		CoinCoinValues cardGuess = new CoinCoinValues();
		Random rand = new Random();
		int randomVal = rand.nextInt(2) + 1;
		int lastDigits = rand.nextInt(2);
		String qnVal = randomVal + randLastDigits[lastDigits];
		cardGuess.setQn(qnVal);
		return cardGuess;
	}
	
	class LoadGameData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
