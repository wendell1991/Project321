package com.mathtimeexplorer.misc;

import com.example.matheducator.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CreditsActivity extends Activity {

	private int[] imageIndex = {R.drawable.creditschai, R.drawable.creditswoo, 
			R.drawable.creditsmark, R.drawable.creditsow};
	private ScrollView creditsView;
	private TextView tapView;
	private LinearLayout creditsLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);

	    creditsView = (ScrollView) findViewById(R.id.creditsView);
	    tapView = (TextView) findViewById(R.id.tapMsg);
	    creditsLayout = (LinearLayout) findViewById(R.id.creditsLayout);
	    
	    ImageView nextImg;
		for (int i = 0; i < imageIndex.length; i++) {
			nextImg = new ImageView(this);
			nextImg.setImageResource(imageIndex[i]);
			creditsLayout.addView(nextImg);
		}
		
		Animation scrollAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_down);
	    creditsView.startAnimation(scrollAnim);
	    tapView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
	    });
	}
}
