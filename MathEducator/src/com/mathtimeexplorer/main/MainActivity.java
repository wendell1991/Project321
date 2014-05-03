package com.mathtimeexplorer.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
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

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.misc.Constants;
import com.mathtimeexplorer.misc.CreditsActivity;
import com.mathtimeexplorer.ranking.RankingTabHost;

public class MainActivity extends Activity implements ViewFactory, OnSeekBarChangeListener {
	
	// Main Page
	private ImageView profileView;
	private ImageSwitcher topicSwitch;
	private GestureDetector gesturedetector;
	private ImageButton settingsPopUp, loginPopUp;
	private int[] imageIndex = {R.drawable.arithmetic, R.drawable.fraction, R.drawable.ranking};
	
	// Login Dialog
	private Dialog loginDialog;
	private EditText userName, pwd;
	private Button loginBtn, cancelBtn;

	// Settings Dialog 
	private Switch effSwitch, musicSwitch;
	private SeekBar effSeekBar, musicSb;
	private Button creditsBtn, doneBtn, cancelBtn1;
	private Dialog settingsDialog;
	private AudioManager am;
	private int effVol = 0;
	private int musicVol = 0;
	
	private Context context = this;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Store user information, value = null if user = guest
		User user = initMain();
		
		gesturedetector = new GestureDetector(new MyGestureListener(user));
		topicSwitch.setFactory(this);
		topicSwitch.setImageResource(imageIndex[0]);
		topicSwitch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				return true;
			}
		});
		
		// Upon clicking, calls the login dialog box
		loginPopUp.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						// Checks whether status is logged in or out
						Object tag = loginPopUp.getTag();
						int id = tag == null ? -1 : (Integer) tag;
						if (id == R.drawable.loginbutton) {
							// Calls the login dialog box and authenticates the user
							callLoginDialog();
						} else {
							// Logs out the user
							DBAdapter database = openDatabaseConnection();
							database.deleteUser();
							setLoginPopUpImage(Constants.LOGIN);
							setProfileImage(Constants.GENDER_GUEST);
						}
					}
				}	
				return true;
			}
		});
		
		// Upon clicking, calls the settings dialog box
		settingsPopUp.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						break;
					case MotionEvent.ACTION_UP : {
						callSettingsDialog();
					}
				}	
				return true;
			}
		});
	}
	
	private void callLoginDialog() {
		
		// Calls the login dialog box
	    loginDialog = new Dialog(context);
		loginDialog.setContentView(R.layout.activity_login);
		initLogin();
		loginBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = userName.getText().toString();
				String password = pwd.getText().toString();
				if (name.isEmpty() == false && password.isEmpty() == false) {
					new AuthenticateUser(name, password).execute();
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
	
	private void callSettingsDialog(){
		
		// Calls the settings dialog box
		settingsDialog = new Dialog(context);
		settingsDialog.setContentView(R.layout.activity_settings);
		initSettings();
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
		if (seekBar.getId() ==  R.id.effSeekBar) {
			// Set the appropriate current values
			effVol = progress;
		} else if (seekBar.getId() == R.id.musicSb){
			musicVol = progress;
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
	
	// Initialize Main Screen UIs
	private User initMain () {
		profileView = (ImageView) findViewById(R.id.profileView);
		topicSwitch = (ImageSwitcher) findViewById(R.id.topicSwitcher);
		loginPopUp = (ImageButton) findViewById(R.id.loginPopUp);
		settingsPopUp = (ImageButton) findViewById(R.id.setBtn);
		
		// Retrieve and check whether use exists in database
		DBAdapter database = openDatabaseConnection();
		User user = database.isUserExists();
		
		// Return value will be null if user does not exists
		if (user != null) {
	    	setProfileImage(user.getGender());
	    	setLoginPopUpImage(Constants.LOGOUT);
		} else {
			setLoginPopUpImage(Constants.LOGIN); 
		}
		return user;
	}
	
	// Initialize Login Dialog UIs
	private void initLogin() {
		userName = (EditText) loginDialog.findViewById(R.id.usrTxtField);
		pwd = (EditText) loginDialog.findViewById(R.id.pwdTxtField);
		loginBtn = (Button) loginDialog.findViewById(R.id.loginBtn);
		cancelBtn = (Button) loginDialog.findViewById(R.id.cancelBtn);
	}
	
	// Initialize Settings Dialog UIs
	private void initSettings() {
		effSwitch = (Switch) settingsDialog.findViewById(R.id.effSwitch);
		musicSwitch = (Switch) settingsDialog.findViewById(R.id.musicSwitch);
		effSeekBar = (SeekBar) settingsDialog.findViewById(R.id.effSeekBar);
		musicSb = (SeekBar) settingsDialog.findViewById(R.id.musicSb);
		creditsBtn = (Button) settingsDialog.findViewById(R.id.creditsBtn);
		doneBtn = (Button) settingsDialog.findViewById(R.id.doneBtn);
		cancelBtn1 = (Button) settingsDialog.findViewById(R.id.cancelBtn1);
	}
	
	private void setLoginPopUpImage(String loginOrLogout) {
		// Configure the image of LoginPopUp button
		int resourceId = 0;
		if (loginOrLogout.equals(Constants.LOGIN)) {
			resourceId = R.drawable.loginbutton;
		} else {
			resourceId = R.drawable.logoutbtn;
		}
		loginPopUp.setImageResource(resourceId);
		loginPopUp.setTag(resourceId);
	}
	
	private void setProfileImage(String gender) {
		// Change the image of profile to male or female
		if (gender.equals(Constants.GENDER_MALE)) {
			profileView.setImageResource(R.drawable.maleprofile);
		} else if (gender.equals(Constants.GENDER_GUEST)) {
			profileView.setImageResource(R.drawable.guestprofile);
		} else {
			profileView.setImageResource(R.drawable.femaleprofile);
		}
	}
	
	private DBAdapter openDatabaseConnection() {
		DBAdapter database = new DBAdapter(getApplicationContext());
		try {
			database.openDataBase();
		}catch(SQLException e){
			Log.i(Constants.LOG_MAIN, e.toString());
		}
		return database;
	}
	
	
	class AuthenticateUser extends AsyncTask<String, String, String> {
		
		private String name;
		private String password;
		private int success = 0;
		
		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Verifying user...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";
		
		private JSONObject json = null;
		
		public AuthenticateUser (String name, String password) {
			this.name = name;
			this.password = password;
		}
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle(DIALOG_LOGIN_TITLE);
            pDialog.setMessage(DIALOG_LOGIN_MESSAGE);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));
            
		    json = jsonParser.makeHttpRequest(Constants.URL_LOGIN, "POST", params);
		    try {
		    	success = json.getInt(Constants.JSON_SUCCESS);
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
		    return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
            // If success = 1, user is authenticated with the server successfully
			final User user = new User();
            if (success == 1) {
            	try {
            		// Retrieve the user object from JSON and save it to class user
					JSONObject obj = json.getJSONObject(Constants.LOGIN_USER);
					
					user.setApp_user_id(obj.getInt(Constants.LOGIN_USER_ID));
					user.setFirst_name(obj.getString(Constants.LOGIN_FIRST_NAME));
					user.setLast_name(obj.getString(Constants.LOGIN_LAST_NAME));
					user.setGender(obj.getString(Constants.LOGIN_GENDER));
					user.setSchool_id(obj.getInt(Constants.LOGIN_SCHOOL_ID));
					user.setClass_id(obj.getInt(Constants.LOGIN_CLASS_ID));
					user.setEduLevel(obj.getInt(Constants.LOGIN_EDULEVEL));
					
					// Save user into the local database
					DBAdapter database = openDatabaseConnection();
					database.addUser(user);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i(Constants.LOG_MAIN, e.toString());
				}
	        	MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Login successfully, Welcome "+
                        		user.getFirst_name()+user.getLast_name()+"!", Toast.LENGTH_SHORT).show();
                    }
                });
	        	// Change loginPopUp image
	        	setLoginPopUpImage(Constants.LOGOUT);
	        	
	        	// Change profile image
	        	setProfileImage(user.getGender());
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

		// Stores the value of the image resources currently being used
		private int topicImageId = 0;
		
		// Values to keep track of imageIndex
		private int currentIndex = 0;
		private int imgTracker = imageIndex.length - 1;
		
		private User user;
		
		// Load Animations
		private Animation slide_out_right = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_right);
		private Animation slide_in_right = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right);
		private Animation slide_out_left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_left);
		private Animation slide_in_left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left);
		
		public MyGestureListener(User user) {
			this.user = user;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float dX = e2.getX()-e1.getX();
			float dY = e1.getY()-e2.getY();

			if (Math.abs(dY) < Constants.SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= 
					Constants.SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >= Constants.SWIPE_MIN_DISTANCE ) {
				if (dX>0) {
					currentIndex--;
 					if (currentIndex < 0) {
 						currentIndex = imgTracker;
 					}
 					topicSwitch.setOutAnimation(slide_out_right);
 					topicSwitch.setInAnimation(slide_in_right);
 					topicImageId = imageIndex[currentIndex];
 					topicSwitch.setImageResource(topicImageId);

				} else {
					currentIndex++;
 					if (currentIndex > imgTracker) {
 						currentIndex = 0;
 					}
 					topicSwitch.setOutAnimation(slide_out_left);
 					topicSwitch.setInAnimation(slide_in_left);
 					topicImageId = imageIndex[currentIndex];
 					topicSwitch.setImageResource(topicImageId);
				}
			} 
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
			
			// Pass user to next activity
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.USER, user);
			intent.putExtras(bundle);
			
			if (imageIndex[currentIndex] == R.drawable.ranking){
				intent.setClass(context, RankingTabHost.class);
				startActivity(intent);
			} else {
				intent.setClass(context, OptionActivity.class);
				intent.putExtra(Constants.TOPIC, topicImageId);
				startActivity(intent);
			}
            return super.onSingleTapConfirmed(e);
        }
	}            	
}
