package com.example.matheducator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import database.JSONParser;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends Activity implements ViewFactory, OnSeekBarChangeListener {
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
	// Topic selection widgets & attributes
	private Context context = this;
	private ImageSwitcher topicSwitch;
	private GestureDetector gesturedetector;
	private int[] imageIndex = {R.drawable.arithmetic, R.drawable.fraction, R.drawable.money, R.drawable.time, R.drawable.ranking};
	private int currentIndex = 0;
	private int imgTracker = imageIndex.length - 1;
	
	// Login form widgets & attributes
	private String name = "";
	private String password = "";
	private EditText userName;
	private EditText pwd;
	private Button loginPopUp;
	private Button loginBtn;
	private Button cancelBtn;
	private Dialog loginDialog;
	private static String LOGIN_URL = "http://10.0.2.2/TimeExplorer/login.php";

	// Settings page widgets & attributes
	private Switch effSwitch;
	private Switch musicSwitch;
	private SeekBar effSeekBar;
	private SeekBar musicSb;
	private ImageButton settingsPopUp;
	private Button creditsBtn;
	private Button doneBtn;
	private Button cancelBtn1;
	private AudioManager am;
	private int effVol = 0;
	private int musicVol = 0;
	
	// JSON Response node names
    private static String TAG_SUCCESS = "success";
    
	// Log tag
	private static final String LOG_TAG = "MainActivity";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Context context = this;

		gesturedetector = new GestureDetector(new MyGestureListener());
		topicSwitch = (ImageSwitcher) findViewById(R.id.topicSwitcher);
		topicSwitch.setFactory(this);
		topicSwitch.setImageResource(imageIndex[0]);
		topicSwitch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				return true;
			}
		});
		
		loginPopUp = (Button) findViewById(R.id.loginPopUp);
		// Upon clicking, calls the login dialog box
		loginPopUp.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  callLoginDialog(context);
			  }
		});
		
		settingsPopUp = (ImageButton) findViewById(R.id.setBtn);
		// Upon clicking, calls the settings dialog box
		settingsPopUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callSettingsDialog(context);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void callLoginDialog(Context context) {
		
		// Calls the login dialog box
	    loginDialog = new Dialog(context);
		loginDialog.setContentView(R.layout.activity_login);
		
		// Initialize the widgets
		userName = (EditText) loginDialog.findViewById(R.id.usrTxtField);
		pwd = (EditText) loginDialog.findViewById(R.id.pwdTxtField);
		loginBtn = (Button) loginDialog.findViewById(R.id.loginBtn);
		cancelBtn = (Button) loginDialog.findViewById(R.id.cancelBtn);
		
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = userName.getText().toString();
				password = pwd.getText().toString();
				if (name.isEmpty() == false && password.isEmpty() == false) {
					new authenticateUser().execute();
				} 
				else {
					Toast.makeText(getApplicationContext(), "Username & Password must not be empty!",
							Toast.LENGTH_SHORT).show();
				}	
			}
		});
		
		// returns to main-page
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginDialog.dismiss();
			}
		});
		
		loginDialog.show();
	}
	
	private void callSettingsDialog(final Context context){
		
		// Calls the settings dialog box
		final Dialog settingsDialog = new Dialog(context);
		settingsDialog.setContentView(R.layout.activity_settings);
				
		// Initialize the widgets
		effSwitch = (Switch) settingsDialog.findViewById(R.id.effSwitch);
		musicSwitch = (Switch) settingsDialog.findViewById(R.id.musicSwitch);
		effSeekBar = (SeekBar) settingsDialog.findViewById(R.id.effSeekBar);
		musicSb = (SeekBar) settingsDialog.findViewById(R.id.musicSb);
		creditsBtn = (Button) settingsDialog.findViewById(R.id.creditsBtn);
		doneBtn = (Button) settingsDialog.findViewById(R.id.doneBtn);
		cancelBtn1 = (Button) settingsDialog.findViewById(R.id.cancelBtn1);

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxEffVol = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int curEffVol = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
		int maxMusicVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curMusicVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        effSwitch.setChecked(true);
        musicSwitch.setChecked(true);
        effSeekBar.setMax(maxEffVol);
        effSeekBar.setProgress(curEffVol);
        musicSb.setMax(maxMusicVol);
        musicSb.setProgress(curMusicVol);
        
        // Set event listeners
        
        effSeekBar.setOnSeekBarChangeListener(this);
        musicSb.setOnSeekBarChangeListener(this);
        
        effSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked == true) {					
					// Switch is on, get the value of seek-bar and set it as the current value
					effVol = effSeekBar.getProgress();
					effSeekBar.setEnabled(true);
				} else {
					// Since switch is Off, set volume as 0 and disable the seek-bar
					effVol = 0;
					effSeekBar.setEnabled(false);
				}
			}
		});
		
		musicSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked == true) {
					// Switch is on, get the value of seek-bar and set it as the current value
					musicVol = musicSb.getProgress();
					musicSb.setEnabled(true);
				} else {
					// Since switch is Off, set volume as 0 and disable the seek-bar
					musicVol = 0;
					musicSb.setEnabled(false);
				}
			}
		});
		
		creditsBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, CreditsActivity.class);
				startActivity(intent);
			}
		});
		
        doneBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am.setStreamVolume(AudioManager.STREAM_SYSTEM, effVol, 0);
				am.setStreamVolume(AudioManager.STREAM_MUSIC, musicVol, 0);
				settingsDialog.dismiss();
			}
		});
        
        // returns to main page
        cancelBtn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingsDialog.dismiss();
			}
		});
        
        settingsDialog.show();
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		ImageView iv = new ImageView(this);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return iv;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		// Checks which seek-bar is adjusted by user.
		switch (seekBar.getId()) {
		
		// Set the appropriate current values
		case R.id.effSeekBar:
			effVol = progress;
			break;
		case R.id.musicSb:
			musicVol = progress;
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	class authenticateUser extends AsyncTask<String, String, String> {
		
		JSONParser jsonParser = new JSONParser();
		int success = 0;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Verifying user...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));
            
			JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
			Log.i(LOG_TAG, "json: "+json.toString());
		    try {
		    	success = json.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		    return null;
		}
		
		protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
	        	MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Login successfully, Welcome "+name+"!", 
                        		Toast.LENGTH_SHORT).show();
                    }
                });
	        	loginDialog.dismiss();
			} else {
				MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Login denied, either username or password is wrong!",
                        		Toast.LENGTH_SHORT).show();
                    }
                });
			}
        }
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

		private static final int SWIPE_MIN_DISTANCE = 150;
		private static final int SWIPE_MAX_OFF_PATH = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float dX = e2.getX()-e1.getX();
			float dY = e1.getY()-e2.getY();

			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY && Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
				if (dX>0) {
					currentIndex--;
 					if (currentIndex < 0) {
 						currentIndex = imgTracker;
 					}
 					topicSwitch.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_right));
 					topicSwitch.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right));
 					topicSwitch.setImageResource(imageIndex[currentIndex]);

				} else {
					currentIndex++;
 					if (currentIndex > imgTracker) {
 						currentIndex = 0;
 					}
 					topicSwitch.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_left));
 					topicSwitch.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left));
 					topicSwitch.setImageResource(imageIndex[currentIndex]);
				}
			} 
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
			if (imageIndex[currentIndex] == R.drawable.ranking){
				Intent intent = new Intent(context, RankingActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(context, OptionActivity.class);
				startActivity(intent);
			}
            return super.onSingleTapConfirmed(e);
        }
		
	}            	
}
