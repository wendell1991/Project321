package com.example.matheducator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		final Context context = this;
		android.util.Log.e("test", "test");
		TextView tv = (TextView) findViewById (R.id.textView1);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String tutorial = extras.getString("Tutorial");
		    tv.setText("Tutorial "+tutorial);
		}
	}
}
