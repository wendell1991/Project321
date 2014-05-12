package com.mathtimeexplorer.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
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
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.ranking.RankingTabHost;
import com.mathtimeexplorer.utils.Constants;

public class MainActivity extends Activity implements ViewFactory, OnSeekBarChangeListener {
	
	// Main Page
	private ImageView profileView;
	private TextSwitcher newsSwitch;
	private ImageSwitcher topicSwitch;
	private GestureDetector gesturedetector;
	private ImageButton settingsPopUp, loginPopUp;
	
	// Login Dialog
	private Dialog loginDialog;
	private EditText userName, pwd;
	private Button loginBtn, cancelBtn;

	// Settings Dialog 
	private Switch musicSwitch;
	private SeekBar musicSb;
	private Button creditsBtn, doneBtn, cancelBtn1;
	private Dialog settingsDialog;
	private AudioManager am;
	private int musicVol = 0;
	
	private User user;
	private Context context = this;
	private MediaPlayer bkgrdMusic = null;
	private ArrayList<News> newsList = null;
	private ArrayList<Integer> topicList;
	private ArrayList<String> topicNameList;
	
	private Animation slide_out_right;
	private Animation slide_in_right;
	private Animation slide_out_left; 
	private Animation slide_in_left; 
	
	// Tracks news index
	private int curNewsIndex = 0;
	private int newsTracker;
	
	private static final String NEWS_UPDATE = "News Update: ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		topicList = new ArrayList<Integer>();
		topicNameList = new ArrayList<String>();
		
		// Load Animations
		slide_out_right = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_right);
		slide_in_right = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right);
		slide_out_left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_left);
		slide_in_left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left);
		
		initMain();
		
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
							
							// CLear all Image resource in topic list and current views
							topicList.clear();
							topicSwitch.removeAllViews();
							
							// If guest account, only entitled to access arithmetic
							topicList.add(R.drawable.arithmetic);						
							initTopicList();

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
	
	private void startBkGrdMusic() {
		bkgrdMusic = MediaPlayer.create(MainActivity.this, 
				R.raw.jewelbeat_journey_through_time);
		
	    bkgrdMusic.setLooping(true);
		bkgrdMusic.start();
	}
	
	private void callLoginDialog() {
		
		// Calls the login dialog box
	    loginDialog = new Dialog(context);
	    loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loginDialog.setContentView(R.layout.activity_login);
		loginDialog.setCancelable(false);
		
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
		settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settingsDialog.setContentView(R.layout.activity_settings);
		settingsDialog.setCancelable(false);
		
		initSettings();
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxMusicVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curMusicVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        musicSwitch.setChecked(true);
        musicSb.setMax(maxMusicVol);
        musicSb.setProgress(curMusicVol);
        
        musicSb.setOnSeekBarChangeListener(this);
		
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
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
	    musicVol = progress;
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
	private void initMain() {
		profileView = (ImageView) findViewById(R.id.profileView);
		topicSwitch = (ImageSwitcher) findViewById(R.id.topicSwitcher);
		loginPopUp = (ImageButton) findViewById(R.id.loginPopUp);
		settingsPopUp = (ImageButton) findViewById(R.id.setBtn);
		newsSwitch = (TextSwitcher) findViewById(R.id.news_textswitcher);
			
		initNewsSwitcher();
		
		// Retrieve and check whether use exists in database
		DBAdapter database = openDatabaseConnection();
	    user = database.isUserExists();
	    
		if (user != null) {
			new RetrieveTopics().execute();
		} else {				
			// If guest account, only entitled to access arithmetic
			topicList.add(R.drawable.arithmetic);
			topicSwitch.removeAllViews();
			initTopicList();
			setLoginPopUpImage(Constants.LOGIN);
		}
		
		// Start background music and retrieve list of news
		startBkGrdMusic();
		new RetrieveNews().execute();
	}
	
	@SuppressWarnings("deprecation")
	private void initTopicList() {
		gesturedetector = new GestureDetector(new MyGestureListener());
		topicSwitch.setFactory(MainActivity.this);
		topicSwitch.setImageResource(topicList.get(0));
		topicSwitch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				return true;
			}
		});
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
		musicSwitch = (Switch) settingsDialog.findViewById(R.id.musicSwitch);
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
	
	private void initNewsSwitcher() {
		// Set the ViewFactory of the Switcher that will create TextView
		newsSwitch.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				// TODO Auto-generated method stub
				TextView view = new TextView(MainActivity.this);
				view.setMovementMethod(LinkMovementMethod.getInstance());
				view.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
				view.setPadding(0, 20, 0, 0);
				view.setTypeface(null, Typeface.BOLD);
				view.setTextSize(15);
				return view;
			}
		});
	}
	
	// On-click handler for news-switch next button
	public void onNewsNextBtnClick(View view) {
		if (newsList != null) {		
			newsSwitch.setOutAnimation(slide_out_left);
			newsSwitch.setInAnimation(slide_in_left);
			newsSwitch.setText(Html.fromHtml(formatNewsSwitcherText(1)));
		}	
	}
	
	// On-click handler for news-switch previous button
	public void onNewsPrevBtnClick(View view) {
		if (newsList != null) {
			newsSwitch.setOutAnimation(slide_out_right);
			newsSwitch.setInAnimation(slide_in_right);
			newsSwitch.setText(Html.fromHtml(formatNewsSwitcherText(-1)));
		}	
	}
	
	private String formatNewsSwitcherText(Integer indexToMove) {
		curNewsIndex += indexToMove;
		Log.i(Constants.LOG_MAIN, "indexToMove: " + indexToMove 
				+ " curNewsIndex: " + curNewsIndex);
		if (curNewsIndex != 0) {
			if (curNewsIndex < 0) {
				curNewsIndex = newsTracker;
			} else if (curNewsIndex > newsTracker) {
				curNewsIndex = 0;
			}
		}
		News news = (News) newsList.get(curNewsIndex);
		
		String newsText = NEWS_UPDATE + (curNewsIndex + 1) + ". <a href='" + Constants.URL_VIEW_NEWS +
				news.getNew_id() + "'>" + news.getNews_title() + "</a> " + news.getNews_postdate();
		
		Log.i(Constants.LOG_MAIN, "newsText: " + newsText);
		
		return newsText;
	}
	
	class RetrieveNews extends AsyncTask<String, String, String> {

		private int success;
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			JSONParser jsonParser = new JSONParser();
			
			// Parameters for the GET request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			// If user = null, set education level as 1. static flag for guest users
			if (user == null) {
				params.add(new BasicNameValuePair("schoolid", String.valueOf(1)));
			} else {
				params.add(new BasicNameValuePair("schoolid", String.valueOf(user.getEduLevel())));
			}
			
			JSONObject json = jsonParser.makeHttpRequest(Constants.URL_RETRIEVE_NEWS, 
					Constants.HTTP_GET, params);
			
			try {				
				success = json.getInt(Constants.JSON_SUCCESS);				
		    	if (success == 1) {		
		    		newsList = new ArrayList<News>();
		    		JSONArray resultList = json.getJSONArray(Constants.TAG_NEWS);
		    		
		    		Log.i(Constants.LOG_MAIN, "resultList size:" + resultList.length());
		    		
		    		JSONObject obj;
		    		News news;
		    		
		    		for (int i = 0; i < resultList.length(); i++) {
		    			news = new News();
		    			obj = resultList.getJSONObject(i);
		    			news.setNew_id(obj.getInt(Constants.NEWS_ID));
		    			news.setNews_title(obj.getString(Constants.NEWS_TITLE));
		    			news.setNews_postdate(obj.getString(Constants.NEWS_POST_DATE));
		    			
		    			Log.i(Constants.LOG_MAIN, "NEWS_ID: " + news.getNew_id());
		    			Log.i(Constants.LOG_MAIN, "NEWS_TITLE: " + news.getNews_title());
		    			Log.i(Constants.LOG_MAIN, "NEWS_POST_DATE: " + news.getNews_postdate());
		    			
		    			newsList.add(news);
		    		}
		    	}
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}	
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			if (success == 1) {				
				if (newsList.size() > 0) {				
					newsTracker = newsList.size() - 1;					
					newsSwitch.removeAllViews();
					initNewsSwitcher();
					
					newsSwitch.setOutAnimation(slide_out_right);
					newsSwitch.setInAnimation(slide_in_left);					
					newsSwitch.setText(Html.fromHtml(formatNewsSwitcherText(0)));		
				}
			}
			
		}
	}
	
	class RetrieveTopics extends AsyncTask<String, String, String> {
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			JSONParser jsonParser = new JSONParser();
			
			// Parameters for the GET request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("edulevel", String.valueOf(user.getEduLevel())));
			params.add(new BasicNameValuePair("schoolid", String.valueOf(user.getSchool_id())));

			JSONObject json = jsonParser.makeHttpRequest(Constants.URL_RETRIEVE_TOPICS, 
					Constants.HTTP_GET, params);
			
			// Clear topic List
			topicList.clear();
			topicNameList.clear();
			
			try {
				int success = json.getInt(Constants.JSON_SUCCESS);
		    	if (success == 1) {
		    		
		    		JSONObject obj;
		    		String topicName;
		    		boolean hasOtherTopics = false;
		    		
		    		JSONArray resultList = json.getJSONArray(Constants.TAG_TOPIC);
		    		
		    		Log.i(Constants.LOG_MAIN, "resultList size:" + resultList.length());
		    		
		    		for (int i = 0; i < resultList.length(); i++) {
		    			obj = resultList.getJSONObject(i);
		    			topicName = obj.getString(Constants.TOPIC_NAME);
		    			topicNameList.add(topicName);
		    			Log.i(Constants.LOG_MAIN, "topicName:" + topicName);
		    			
		    			// Check if contain arithmetic, fraction, measurement or other topics
		    			if (topicName.equals(Constants.TOPIC_ARITH)) {
		    				topicList.add(R.drawable.arithmetic);
		    			} else if (topicName.equals(Constants.TOPIC_FRACTION)) {
		    				topicList.add(R.drawable.fraction);
		    			} else if (topicName.equals(Constants.TOPIC_MEASURE)) {
		    				topicList.add(R.drawable.measurement);
		    			} else {				
		    				if (hasOtherTopics == false) { 					
		    					topicList.add(R.drawable.othertopic);
		    					hasOtherTopics = true;
		    				}
		    			}
		    		}
		    	}
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// Any valid users should have access to ranking
			topicList.add(R.drawable.ranking);
			topicSwitch.removeAllViews();
			initTopicList();

			setProfileImage(user.getGender());
	    	setLoginPopUpImage(Constants.LOGOUT);
		}
	}
	
	
	class AuthenticateUser extends AsyncTask<String, String, String> {
		
		private String name;
		private String password;
		private int success = 0;
		private ProgressDialog pDialog;
		private JSONObject json = null;
		
		public AuthenticateUser (String name, String password) {
			this.name = name;
			this.password = password;
		}
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle(Constants.TITLE_VERIFY_USER);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			JSONParser jsonParser = new JSONParser();
			
			// Parameters for the POST request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));
            
		    json = jsonParser.makeHttpRequest(Constants.URL_LOGIN,
		    		Constants.HTTP_POST, params);
		    
		    try {
		    	success = json.getInt(Constants.JSON_SUCCESS);
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}   
		    return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {      
			if (success == 1) {
				try {
					// If success = 1, user is authenticated with the server successfully
        		    user = new User();
        		    
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
	        	
	        	new RetrieveTopics().execute();
	        	new RetrieveNews().execute();
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
		private int imgTracker = topicList.size() - 1;
		
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
 					topicImageId = topicList.get(currentIndex);
 					topicSwitch.setImageResource(topicImageId);

				} else {
					currentIndex++;
 					if (currentIndex > imgTracker) {
 						currentIndex = 0;
 					}
 					topicSwitch.setOutAnimation(slide_out_left);
 					topicSwitch.setInAnimation(slide_in_left);
 					topicImageId = topicList.get(currentIndex);
 					topicSwitch.setImageResource(topicImageId);
				}
			} 
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
			
			topicImageId = topicList.get(currentIndex);
			
			// Pass user to next activity
			Intent intent = new Intent();
			intent.putExtra(Constants.USER, user);
			
			// Stops the background music
			bkgrdMusic.release();
			bkgrdMusic = null;
			
			if (topicImageId == R.drawable.ranking){
				intent.setClass(context, RankingTabHost.class);
				intent.putStringArrayListExtra(Constants.TOPIC, topicNameList);
				startActivity(intent);
			} else if (topicImageId == R.drawable.othertopic){
				intent.setClass(context,OtherTopicActivity.class);
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
