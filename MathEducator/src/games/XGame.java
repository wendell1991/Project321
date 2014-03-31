package games;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matheducator.R;

public class XGame extends Activity {
	
	private TextView timerText;
	private ImageView valueOne, valueTwo, valAnswer;
	private ImageButton optionOne, optionTwo, optionThree, optionFour ,optionFive, optionSix;
	
	private Random rand;
	private Context context = this;
	private long millisInFuture = 30000, countDownInterval = 1000;
	private ArrayList<ImageButton> optionArrayList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xgame);
		
		// Initialize UIs (Only once)
		init();
		
		// Create a count-down timer, starting number: 30000 milliseconds
		CountDown count = new CountDown(millisInFuture, countDownInterval);
		count.start();
		
		NumberHandler nh = generateGameValues();
		initGameContent(nh);
		
		optionOne.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		optionTwo.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		optionThree.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		optionFour.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		optionFive.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		optionSix.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	private void initGameContent(NumberHandler nh){
		int whichToHide = nh.getWhichToHide();
		int firstNumber = nh.getFirstNumber();
		int secondNumber = nh.getSecondNumber();
		int answer = nh.getAnswer();
		
		// Hide either firstNumber, secondNumber or result randomly and paint them
		if (whichToHide == firstNumber) {
			valueOne.setImageResource(R.drawable.qnmark);
			paintImageView(valueTwo, secondNumber);
			paintImageView(valAnswer, answer);
		}
		else if (whichToHide == secondNumber) {
			valueTwo.setImageResource(R.drawable.qnmark);
			paintImageView(valueOne, firstNumber);
			paintImageView(valAnswer, answer);
		}
		else {
			valAnswer.setImageResource(R.drawable.qnmark);
			paintImageView(valueOne, firstNumber);
			paintImageView(valueTwo, secondNumber);
		}
		
		// Paint the option image buttons
		ArrayList<Integer> optionList = nh.getOptions();
		
		ImageButton curBtn;
		rand = new Random();
		int randomVal = 0;
		int index = 6;
		for (int i = 0; i < optionArrayList.size(); i++) {
			curBtn = (ImageButton) optionArrayList.get(i);
			randomVal = rand.nextInt(index);
			paintImageButton(curBtn, (Integer) optionList.get(randomVal));
			optionList.remove(randomVal);
			index--;
		}
	}
	
	private void paintImageView(ImageView view, int value){
		int firstImg = 0;
		int secondImg = 0;
		boolean isTwoDigits = false;
		int firstDigit = Integer.parseInt(String.valueOf(value).substring(0, 1));
		Log.i("XGame", "Button to be paint: " + view);
		Log.i("XGame", "Value: " + value);
		// Check value of first digit and set image resource 
		switch(firstDigit){
		case 1: 
			firstImg = R.drawable.testone;
			break;
		case 2: 
			firstImg = R.drawable.testtwo;
			break;
		case 3: 
			firstImg = R.drawable.testthree;
			break;
		case 4: 
			firstImg = R.drawable.testfour;
			break;
		case 5: 
			firstImg = R.drawable.testfive;
			break;
		case 6: 
			firstImg = R.drawable.testsix;
			break;
		case 7: 
			firstImg = R.drawable.testseven;
			break;
		case 8:
			firstImg = R.drawable.testeight;
			break;
		case 9: 
			firstImg = R.drawable.testnine;
			break;
		default:
			firstImg = R.drawable.testzero;
		}
		
		// Checks if value is two digits
		if (String.valueOf(value).length() == 2) {
			int secondDigit = Integer.parseInt(String.valueOf(value).substring(1, 2));
			isTwoDigits = true;
			// Check value of second digit and set image resource 
			switch(secondDigit){
			case 1: 
				secondImg = R.drawable.testone;
				break;
			case 2: 
				secondImg = R.drawable.testtwo;
				break;
			case 3: 
				secondImg = R.drawable.testthree;
				break;
			case 4: 
				secondImg = R.drawable.testfour;
				break;
			case 5:
				secondImg = R.drawable.testfive;
				break;
			case 6: 
				secondImg = R.drawable.testsix;
				break;
			case 7: 
				secondImg = R.drawable.testseven;
				break;
			case 8: 
				secondImg = R.drawable.testeight;
				break;
			case 9: 
				secondImg = R.drawable.testnine;
				break;
			default:
				secondImg = R.drawable.testzero;
			}
		}
		
		Log.i("XGame", "firstImg: " + firstImg + " SecondImg: " +secondImg);
		if (isTwoDigits == true) {
			Bitmap firstBit = BitmapFactory.decodeResource(context.getResources(), firstImg);
			Bitmap secondBit = BitmapFactory.decodeResource(context.getResources(), secondImg);
			// Create the combined image
			Bitmap result =  Bitmap.createBitmap(firstBit.getWidth() + secondBit.getWidth(), 
					firstBit.getHeight(), Bitmap.Config.ARGB_8888);
		    Canvas combinedImage = new Canvas(result); 
			combinedImage.drawBitmap(firstBit, 0f, 0f, null); 
			combinedImage.drawBitmap(secondBit, firstBit.getWidth(), 0f, null); 
			view.setImageBitmap(result);
		} else {
			view.setImageResource(firstImg);
		}
	}
	
	private void paintImageButton(ImageButton btn, int value){
		int firstImg = 0;
		int secondImg = 0;
		boolean isTwoDigits = false;
		int firstDigit = Integer.parseInt(String.valueOf(value).substring(0, 1));
		Log.i("XGame", "Button to be paint: " + btn);
		Log.i("XGame", "Value: " + value);
		// Check value of first digit and set image resource 
		switch(firstDigit){
		case 1: 
			firstImg = R.drawable.testone;
			break;
		case 2: 
			firstImg = R.drawable.testtwo;
			break;
		case 3: 
			firstImg = R.drawable.testthree;
			break;
		case 4: 
			firstImg = R.drawable.testfour;
			break;
		case 5: 
			firstImg = R.drawable.testfive;
			break;
		case 6: 
			firstImg = R.drawable.testsix;
			break;
		case 7: 
			firstImg = R.drawable.testseven;
			break;
		case 8:
			firstImg = R.drawable.testeight;
			break;
		case 9: 
			firstImg = R.drawable.testnine;
			break;
		default:
			firstImg = R.drawable.testzero;
		}
		
		// Checks if value is two digits
		if (String.valueOf(value).length() == 2) {
			int secondDigit = Integer.parseInt(String.valueOf(value).substring(1, 2));
			isTwoDigits = true;
			// Check value of second digit and set image resource 
			switch(secondDigit){
			case 1: 
				secondImg = R.drawable.testone;
				break;
			case 2: 
				secondImg = R.drawable.testtwo;
				break;
			case 3: 
				secondImg = R.drawable.testthree;
				break;
			case 4: 
				secondImg = R.drawable.testfour;
				break;
			case 5:
				secondImg = R.drawable.testfive;
				break;
			case 6: 
				secondImg = R.drawable.testsix;
				break;
			case 7: 
				secondImg = R.drawable.testseven;
				break;
			case 8: 
				secondImg = R.drawable.testeight;
				break;
			case 9: 
				secondImg = R.drawable.testnine;
				break;
			default:
				secondImg = R.drawable.testzero;
			}
		}
		
		Log.i("XGame", "firstImg: " + firstImg + " SecondImg: " +secondImg);
		if (isTwoDigits == true) {
			Bitmap firstBit = BitmapFactory.decodeResource(context.getResources(), firstImg);
			Bitmap secondBit = BitmapFactory.decodeResource(context.getResources(), secondImg);
			// Create the combined image
			Bitmap result =  Bitmap.createBitmap(firstBit.getWidth() + secondBit.getWidth(), 
					firstBit.getHeight(), Bitmap.Config.ARGB_8888);
		    Canvas combinedImage = new Canvas(result); 
			combinedImage.drawBitmap(firstBit, 0f, 0f, null); 
			combinedImage.drawBitmap(secondBit, firstBit.getWidth(), 0f, null); 
			btn.setImageBitmap(result);
		} else {
			btn.setImageResource(firstImg);
		}
	}
	
	
	private NumberHandler generateGameValues() {
		NumberHandler nh = new NumberHandler();
		ArrayList<Integer> itemToHide = new ArrayList<Integer>();
		rand = new Random();
		
		// Generate values ranging 0 - 12
		int firstNumber = rand.nextInt(13);
		int secondNumber = rand.nextInt(13);
		int result = firstNumber * secondNumber;
		
		// Determines which to hide. 
		int whichToHide = rand.nextInt(3);
		itemToHide.add(firstNumber);
		itemToHide.add(secondNumber);
		itemToHide.add(result);
		nh.setWhichToHide(itemToHide.get(whichToHide));
		nh.setFirstNumber(firstNumber);
		nh.setSecondNumber(secondNumber);
		nh.setAnswer(result);
		
		/* Generation scheme:
		 * Generate 2 options close to the answer 
		 * Generate 3 random options ranging from 0 - 100
		*/
		ArrayList<Integer> optionList = new ArrayList<Integer>();
		optionList.add(result);
		optionList.add(result + 1);
		optionList.add(result - 1);
		
		// Makes sure value of the random options are not repeated
		int prevVal = -1;
		int curVal = 0;
		for (int i = 0; i < 3; i++) {
			curVal = rand.nextInt(101);
			if (curVal != prevVal) {
				optionList.add(curVal);
			}
			prevVal = curVal;
		}
		
		nh.setOptions(optionList);
		return nh;
	}
	
	private void init(){
		timerText = (TextView) findViewById(R.id.timerText);
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
	
	class CountDown extends CountDownTimer {

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}
		
		@Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	timerText.setText(String.valueOf(millisUntilFinished / 1000));
        }
	}
}
