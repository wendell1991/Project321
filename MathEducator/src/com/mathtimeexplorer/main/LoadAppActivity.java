package com.mathtimeexplorer.main;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.utils.Constants;

public class LoadAppActivity extends Activity {

	private ImageButton startBtn;
	private Context context = this;
	private MediaPlayer bkgrdMusic = null; 
	
	@Override
	public void onResume() {
		super.onResume();
		if (bkgrdMusic != null) {
			bkgrdMusic.start();
		} else {
			startBkGrdMusic();
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
				bkgrdMusic.pause();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_app);
		
		// Start background music
		startBkGrdMusic();
		
		startBtn = (ImageButton) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Checks if there is Internet connectivity
				if (isNetworkAvailable() == false) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder
						.setCancelable(false)
						.setTitle(R.string.noInternetTitle)
						.setMessage(R.string.noInternetMsg)
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								// Go to HOME screen
								Intent intent = new Intent(Intent.ACTION_MAIN);
								intent.addCategory(Intent.CATEGORY_HOME);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						});
				
						// Creates the dialog
						AlertDialog dialog = builder.create();
						dialog.show();
						
				} else {
					DBAdapter database = new DBAdapter(getApplicationContext());
					
					try {
						database.createDataBase();
					} catch (IOException e) {
						Log.i(Constants.LOG_LOADAPP, e.toString());
				    }
					database.close();
					
					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
				}
				
				// Stops the background music
				bkgrdMusic.release();
				bkgrdMusic = null;
			} 
		});
	}
	
	private void startBkGrdMusic() {
		bkgrdMusic = MediaPlayer.create(LoadAppActivity.this, 
				R.raw.jewelbeat_in_my_own_world);
				
		bkgrdMusic.setLooping(true);
		bkgrdMusic.start();
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
