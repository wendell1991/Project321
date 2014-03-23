package com.example.matheducator;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class OptionActivity extends Activity implements OnTouchListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		final Context context = this;
		android.util.Log.e("test", "test");
		ImageView img = (ImageView) findViewById (R.id.imageView1);
		img.setOnTouchListener(this);

	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		final Context context = this;
		final int action = ev.getAction();
		// (1) 
		final int evX = (int) ev.getX();
		final int evY = (int) ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN :
			break;
		case MotionEvent.ACTION_UP :
			int touchColor = getHotspotColor (R.id.imageView2, evX, evY);

			//int rgb1 = Color.red(touchColor);
			//int rgb2 = Color.green(touchColor);
			//int rgb3 = Color.blue(touchColor);
			//String rgb = rgb1+","+rgb2+","+rgb3;
			android.util.Log.e("test", Integer.toString(touchColor));
			// (3)
			int tutorial = Color.rgb(0, 162, 232);
			android.util.Log.e("test", Integer.toString(touchColor));
			android.util.Log.e("test", Integer.toString(tutorial));

			int tutorial1 = -16735512;
			int tutorial2 = -3947581;
			int tutorial3 = -20791;
			int tutorial4 = -14066;
			int quiz = -5684139;
			int game =-8239959;

			if(touchColor==tutorial1){
				Intent intent = new Intent(context, TutorialActivity.class);
				intent.putExtra("Tutorial", "1");
				callProgressWindow(context, intent, evX, evY);
			}
			if(touchColor==tutorial2){
				Intent intent = new Intent(context, TutorialActivity.class);
				intent.putExtra("Tutorial", "2");
				callProgressWindow(context, intent, evX, evY);
			}
			if(touchColor==tutorial3){
				Intent intent = new Intent(context, TutorialActivity.class);
				intent.putExtra("Tutorial", "3");
				callProgressWindow(context, intent, evX, evY);
			}
			if(touchColor==tutorial4){
				Intent intent = new Intent(context, TutorialActivity.class);
				intent.putExtra("Tutorial", "4");
				callProgressWindow(context, intent, evX, evY);
			}
			if(touchColor==quiz){
				Intent intent = new Intent(context, QuizActivity.class);
				startActivity(intent);
			}
			if(touchColor==game){
				Intent intent = new Intent(context, GameActivity.class);
				startActivity(intent);
			}
		}
		return true;
	}

	public int getHotspotColor (int hotspotId, int x, int y) {
		ImageView img = (ImageView) findViewById (hotspotId);
		img.setDrawingCacheEnabled(true); 
		Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache()); 
		img.setDrawingCacheEnabled(false);
		return hotspots.getPixel(x, y);
	}

	private void callProgressDialog(Context cx, Intent in) {
		
		final Context context = cx;
		final Intent intent = in;
		final Dialog progressDialog = new Dialog(context);
		progressDialog.setContentView(R.layout.activity_progress);

		Button practiceBtn = (Button) progressDialog.findViewById(R.id.practiceBtn);
		Button tutorialBtn = (Button) progressDialog.findViewById(R.id.tutorialBtn);


		practiceBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(context, QuizActivity.class);
				startActivity(intent2);
			}
		});

		tutorialBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
			}
		});
		// returns to main-page

		progressDialog.show();
	}

	private void callProgressWindow(Context cx, Intent in, int evX, int evY) {

		final Intent intent = in;
		final Context context = cx;
		PopupWindow progressWindow = new PopupWindow(context);
		
		// Inflate the popup_layout.xml
		RelativeLayout viewGroup = (RelativeLayout) ((Activity) context).findViewById(R.id.progresslayout);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.activity_progress, viewGroup);
		Button practiceBtn = (Button) layout.findViewById(R.id.practiceBtn);
		Button tutorialBtn = (Button) layout.findViewById(R.id.tutorialBtn);

		progressWindow.setContentView(layout);
		progressWindow.setWidth(300);
		progressWindow.setHeight(300);
		progressWindow.setFocusable(true);
		progressWindow.showAtLocation(layout, Gravity.NO_GRAVITY, evX+20, evY+100);
		
		practiceBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(context, QuizActivity.class);
				startActivity(intent2);
			}
		});

		tutorialBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
			}
		});
	}
}
