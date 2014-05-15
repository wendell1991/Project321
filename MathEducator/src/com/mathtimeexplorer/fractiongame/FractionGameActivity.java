package com.mathtimeexplorer.fractiongame;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import com.example.matheducator.R;
import com.mathtimeexplorer.main.SelectPracticeSetActivity;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;

public class FractionGameActivity extends Activity implements OnTouchListener, OnDragListener {

	User user;

	LinearLayout penguinContainer;
	LinearLayout eskimoContainer;
	LinearLayout bearContainer;
	HorizontalScrollView penguinSlider;
	HorizontalScrollView eskimoSlider;
	HorizontalScrollView bearSlider;

	ImageView cake;
	ImageView fish;
	ImageView meat;
	ImageView bin;

	int total;

	Table table1;
	Table table2;
	Table table3;
	
	TextView cakeNumerator;
	TextView cakeDenominator;
	TextView fishNumerator;
	TextView fishDenominator;
	TextView meatNumerator;
	TextView meatDenominator;
	TextView totalText;
	
	int fault;
	int score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fractiongame_main);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getParcelable(Constants.USER);
		}

		// Initialize UIs (Only once)

		penguinContainer = (LinearLayout)findViewById(R.id.penguin_container);
		eskimoContainer = (LinearLayout)findViewById(R.id.eskimo_container);
		bearContainer = (LinearLayout)findViewById(R.id.bear_container);

		cake = (ImageView)findViewById(R.id.fractiongame_cake);
		cake.setOnTouchListener(this);

		fish = (ImageView)findViewById(R.id.fractiongame_fish);
		fish.setOnTouchListener(this);

		meat = (ImageView)findViewById(R.id.fractiongame_meat);
		meat.setOnTouchListener(this);
		
		bin = (ImageView) findViewById(R.id.fractiongame_bin);
		bin.setOnDragListener(new View.OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent e) {
				if (e.getAction()==DragEvent.ACTION_DROP) {
					ImageView view = (ImageView) e.getLocalState();
					ViewGroup from = (ViewGroup) view.getParent();
					if(from.getId()==R.id.penguin_container || from.getId()==R.id.eskimo_container || from.getId()==R.id.bear_container){
						from.removeView(view);
					}
				}
				return true;
			}
		});

		cakeNumerator = (TextView)findViewById(R.id.cakeNumerator);
		cakeDenominator = (TextView)findViewById(R.id.cakeDenominator);
		fishNumerator = (TextView)findViewById(R.id.fishNumerator);
		fishDenominator = (TextView)findViewById(R.id.fishDenominator);
		meatNumerator = (TextView)findViewById(R.id.meatNumerator);
		meatDenominator = (TextView)findViewById(R.id.meatDenominator);
		totalText = (TextView) findViewById(R.id.fractiongame_total);

		penguinSlider = (HorizontalScrollView)findViewById(R.id.penguin_slider);
		eskimoSlider = (HorizontalScrollView)findViewById(R.id.eskimo_slider);
		bearSlider = (HorizontalScrollView)findViewById(R.id.bear_slider);

		penguinSlider.setOnDragListener(this);
		eskimoSlider.setOnDragListener(this);
		bearSlider.setOnDragListener(this);


		initValues();
		Log.i("table1",Integer.toString(table1.getAmount()));
		Log.i("table2",Integer.toString(table2.getAmount()));
		Log.i("table3",Integer.toString(table3.getAmount()));
		Log.i("table1",Integer.toString(table1.getNumerator()));
		Log.i("table1",Integer.toString(table1.getDenominator()));
		Log.i("table2",Integer.toString(table2.getNumerator()));
		Log.i("table2",Integer.toString(table2.getDenominator()));
		Log.i("table3",Integer.toString(table3.getNumerator()));
		Log.i("table3",Integer.toString(table3.getDenominator()));
		initTexts();
		

		Button checkAnswerBtn = (Button) findViewById(R.id.fractiongame_checkanswer);
		checkAnswerBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				checkAnswer();
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(v);
			v.startDrag(null, shadowBuilder, v, 0);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onDrag(View v, DragEvent e) {
		if (e.getAction()==DragEvent.ACTION_DROP) {
			ImageView view = (ImageView) e.getLocalState();
			HorizontalScrollView hsv = (HorizontalScrollView) v;
			Log.i("Count",Integer.toString(hsv.getChildCount()));
			LinearLayout to = (LinearLayout)hsv.getChildAt(0);
			ViewGroup from = (ViewGroup) view.getParent();
			if(from.getId()==R.id.penguin_container || from.getId()==R.id.eskimo_container || from.getId()==R.id.bear_container){
				from.removeView(view);
				to.addView(view);
			}
			else{
			ImageView img = new ImageView(getApplicationContext());			
			img.setImageDrawable(view.getDrawable());
			LayoutParams param = new LayoutParams(100,100);
			param.setMargins(10, 0, 0, 0);
			img.setLayoutParams(param);
			img.setOnTouchListener(this);

			to.addView(img);
			}
		}
		return true;
	}

	public class MyDragShadowBuilder extends View.DragShadowBuilder {

		public MyDragShadowBuilder(View v) {
			super(v);

		}
		@Override
		public void onDrawShadow(Canvas canvas) {
			super.onDrawShadow(canvas);



		}
		@Override
		public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
			super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
		}
	}

	private void initValues(){
		table1 = new Table();
		table2 = new Table();
		table3 = new Table();


		ArrayList<String> types = new ArrayList<String>();
		types.add("eskimo");
		types.add("penguin");
		types.add("bear");


		Random r = new Random();

		int no = r.nextInt(3-0)+0;

		table1.setType(types.get(no));
		no = r.nextInt(2-0)+0;
		table2.setType(types.get(no));
		table3.setType(types.get(0));

		int[] numbers = new int[]{6,8,9,10,12,14,15,16,18,20,21,22,24};

		no = r.nextInt(numbers.length-0)+0;
		total = numbers[no];

		int count = total;
		no = r.nextInt(total-1-1)+1;

		table1.setAmount(no);
		count -=no;

		no = r.nextInt(count-1)+1;
		table2.setAmount(no);
		table3.setAmount(count-no);



		/*no = r.nextInt(factors.size()-0)+0;
		table1.setDenominator(no);
		no = r.nextInt(factors.size()-0)+0;
		table2.setDenominator(no);
		no = r.nextInt(factors.size()-0)+0;
		table3.setDenominator(no);

		int count = total;

		no = r.nextInt(table1.getDenominator()+1-1)+1;
		table1.setNumerator(no);

		count-=(total/table1.getNumerator()*no);
		 */

		
		Fraction first = new Fraction(table1.getAmount(),total);
		Fraction second = new Fraction(table2.getAmount(),total);
		Fraction third = new Fraction(table3.getAmount(),total);
		
		first.reduce();
		second.reduce();
		third.reduce();
		table1.setNumerator(first.getNumerator());
		table1.setDenominator(first.getDenominator());
		table2.setNumerator(second.getNumerator());
		table2.setDenominator(second.getDenominator());
		table3.setNumerator(third.getNumerator());
		table3.setDenominator(third.getDenominator());
		
		Log.i("table1",Integer.toString(table1.getAmount()));
		Log.i("table2",Integer.toString(table2.getAmount()));
		Log.i("table3",Integer.toString(table3.getAmount()));
		Log.i("table1",Integer.toString(table1.getNumerator()));
		Log.i("table1",Integer.toString(table1.getDenominator()));
		Log.i("table2",Integer.toString(table2.getNumerator()));
		Log.i("table2",Integer.toString(table2.getDenominator()));
		Log.i("table3",Integer.toString(table3.getNumerator()));
		Log.i("table3",Integer.toString(table3.getDenominator()));
	}
	
	private void initTexts(){
		cakeNumerator.setText(Integer.toString(table2.getNumerator()));
		cakeDenominator.setText(Integer.toString(table2.getDenominator()));
		fishNumerator.setText(Integer.toString(table1.getNumerator()));
		fishDenominator.setText(Integer.toString(table1.getDenominator()));
		meatNumerator.setText(Integer.toString(table3.getNumerator()));
		meatDenominator.setText(Integer.toString(table3.getDenominator()));
		totalText.setText(total+" Dishes");
		
		penguinContainer.removeAllViews();
		eskimoContainer.removeAllViews();
		bearContainer.removeAllViews();
	}

	private void checkAnswer(){

		Table table1Ans = new Table();
		int ans =0;

		for(int i=0;i<penguinContainer.getChildCount();i++){
			ImageView img = (ImageView)penguinContainer.getChildAt(i);
			if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.fish).getConstantState())){
				Log.i("fish = fish", "fish");
				table1Ans.setAmount(table1Ans.getAmount()+1);
			}
		}
		if(table1.getAmount()==table1Ans.getAmount()){
			Log.i("Answer table 1", "Correct");
			ans++;
		}
		
		Table table2Ans = new Table();

		for(int i=0;i<eskimoContainer.getChildCount();i++){
			ImageView img = (ImageView)eskimoContainer.getChildAt(i);
			if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.cake).getConstantState())){
				Log.i("cake = cake", "cake");
				table2Ans.setAmount(table2Ans.getAmount()+1);
			}
		}
		if(table2.getAmount()==table2Ans.getAmount()){
			Log.i("Answer table 2", "Correct");
			ans++;
		}
		
		Table table3Ans = new Table();

		for(int i=0;i<bearContainer.getChildCount();i++){
			ImageView img = (ImageView)bearContainer.getChildAt(i);
			if(img.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.meat).getConstantState())){
				Log.i("meat = meat", "meat");
				table3Ans.setAmount(table3Ans.getAmount()+1);
			}
		}
		if(table3.getAmount()==table3Ans.getAmount()){
			Log.i("Answer table 3", "Correct");
			ans++;
		}
		Log.i("ans",Integer.toString(ans));
		if(ans==3){
			score++;
			initValues();
			initTexts();
		}
		else{
			fault++;
			Log.i("fault",Integer.toString(fault));
			if(fault==3){
				Toast.makeText(getApplicationContext(), "Your total score is: "+score,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class Fraction {
		int numerator;
		int denominator;
		
		Fraction(int num,int denom){
			numerator = num;
			denominator = denom;
		}

		public void reduce() {
			int n = numerator;
			int d = denominator;

			while (d != 0) {
				int t = d;
				d = n % d;
				n = t;
			}

			int gcd = n;

			numerator /= gcd;
			denominator /= gcd;
		}

		public int getNumerator() {
			return numerator;
		}

		public void setNumerator(int numerator) {
			this.numerator = numerator;
		}

		public int getDenominator() {
			return denominator;
		}

		public void setDenominator(int denominator) {
			this.denominator = denominator;
		} 
	}
}
