package com.example.matheducator;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.TextView;

public class QuizActivity extends Activity {

	private String staticQn = "What is the value of ";
	private String qnMark = "?";
	private TextView qnView;
	private RadioButton optionA;
	private RadioButton optionB;
	private RadioButton optionC;
	private RadioButton optionD;
	
	private String[] randArith = {" subtracted by ", " added by "};
	private static String SUBTRACTION = " subtracted by ";
	private static String ADDITION = " added by ";
	private static int RADIO_BUTTON_1 = 1;
	private static int RADIO_BUTTON_2 = 2;
	private static int RADIO_BUTTON_3 = 3;
	private static int RADIO_BUTTON_4 = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		init();
		initQuestions();
	}
	
	private void initQuestions() {
		
		Random rand = new Random();
		int threshold = rand.nextInt(100) + 1;
		int firstNumber = rand.nextInt(threshold);
		int secondNumber = rand.nextInt(100 - threshold);
		
		int addOrSub = rand.nextInt(2);
		int ansPosition = rand.nextInt(4) + 1;
		if (randArith[addOrSub].equals(SUBTRACTION)) {
			
			 int answer = 0;
			 
		     if (firstNumber > secondNumber) {
		    	 qnView.setText(staticQn+firstNumber+SUBTRACTION+secondNumber+qnMark);
		    	 answer = firstNumber - secondNumber;
		     } else {
		    	 qnView.setText(staticQn+secondNumber+SUBTRACTION+firstNumber+qnMark);
		    	 answer = secondNumber - firstNumber;
		     }
		     initRadioButtons(ansPosition, answer);
		} else {
		    qnView.setText(staticQn+firstNumber+ADDITION+secondNumber+qnMark);
		    int answer = firstNumber + secondNumber;
		    initRadioButtons(ansPosition, answer);
		}
	}
	
	private void initRadioButtons(int ansPosition, int answer) {
		String ansVal = String.valueOf(answer);
		if (ansPosition == RADIO_BUTTON_1) {
	    	optionA.setText(ansVal);
	     } else {
	    	optionA.setText(String.valueOf(answer+=1)); 
	     }
	     
	     if (ansPosition == RADIO_BUTTON_2) {
	    	 optionB.setText(ansVal);
	     } else {
	    	 optionB.setText(String.valueOf(answer+=2)); 
	     }
	     
	     if (ansPosition == RADIO_BUTTON_3) {
	    	 optionC.setText(ansVal);
	     } else {
	    	 optionC.setText(String.valueOf(answer+=3)); 
	     }
	     
	     if (ansPosition == RADIO_BUTTON_4) {
	    	 optionD.setText(ansVal);
	     } else {
	    	 optionD.setText(String.valueOf(answer+=4)); 
	     }
	}
	
	private void init(){
		qnView = (TextView) findViewById(R.id.qnView);
		optionA = (RadioButton) findViewById(R.id.optionA);
		optionB = (RadioButton) findViewById(R.id.optionB);
		optionC = (RadioButton) findViewById(R.id.optionC);
		optionD = (RadioButton) findViewById(R.id.optionD);
	}
}
