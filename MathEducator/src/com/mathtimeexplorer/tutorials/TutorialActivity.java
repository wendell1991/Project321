package com.mathtimeexplorer.tutorials;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.utils.UI;
import com.example.matheducator.R;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.utils.ImageLoader;

public class TutorialActivity extends Activity {

	private String topic = "";
	private String sub_topic = "";
	private int userid;
	private int schoolId;
	private int eduLevel;
	
	private User user = null;
	private Context context = this;
	private static ArrayList<String> urlList;
	private FlipViewController flipView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			topic = extras.getString("topicname");
			sub_topic = extras.getString(Constants.SUB_TOPIC);
			user = extras.getParcelable(Constants.USER);
		}
		
		if (user == null) {
			// Guest user sees the same tutorials as those in system school
			schoolId = Constants.GUEST_SCH_ID;
			eduLevel = Constants.GUEST_SCH_ID;
		} else {
			userid = user.getApp_user_id();
			schoolId = user.getSchool_id();
			eduLevel = user.getEduLevel();
		}
		
		Log.i(Constants.LOG_TUTORIAL, "Sub Topic: " +sub_topic);
		
		new RetrieveImageUrls().execute();
		
		flipView = new FlipViewController(TutorialActivity.this, FlipViewController.HORIZONTAL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		flipView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Back button is pressed
		if (this.isFinishing()) {
			finish();
		} else {
			flipView.onPause();
		}
	}

	class RetrieveImageUrls extends AsyncTask<String, String, Integer> {
		
		private JSONArray resultList;
		private ProgressDialog pDialog;

		@Override
        protected void onPreExecute() {
			super.onPreExecute();
            pDialog = new ProgressDialog(TutorialActivity.this);
            pDialog.setTitle(Constants.TITLE_LOADING_TUT);
            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected Integer doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Parameters for the GET request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("topic", topic));
			params.add(new BasicNameValuePair("subtopic", sub_topic));
			params.add(new BasicNameValuePair("schoolid", String.valueOf(schoolId)));
			params.add(new BasicNameValuePair("edulevel", String.valueOf(eduLevel)));
			
			int success;
			int progress = 1;
			
			JSONParser jsonParser = new JSONParser();
			resultList = new JSONArray();
					
			JSONObject json = jsonParser.makeHttpRequest(Constants.URL_RETRIEVE_TUT,
					Constants.HTTP_GET, params);
			
			// Retrieve the list of URLs containing the tutorial images
			try{
				success = json.getInt(Constants.JSON_SUCCESS);
				
				Log.i(Constants.LOG_TUTORIAL, "Retrieve tutorial urls success: " + success);
				
				if (success == 1) {
					String url = "";
					JSONObject obj;
					urlList = new ArrayList<String>();
					
					resultList = json.getJSONArray(Constants.TAG_TUT);
					Log.i(Constants.LOG_TUTORIAL, "resultList length: " + resultList.length());
					
					for (int i = 0; i < resultList.length(); i++) {
						obj = resultList.getJSONObject(i);
						url = obj.getString(Constants.TUT_FILE_LOC);					
						urlList.add(url);
						Log.i(Constants.LOG_TUTORIAL, "URL: " + url);
					}
				}
				
				// If user is not guest, retrieve progress
				if (user != null && resultList.length() > 0) {
					// Clear the parameter list and re-use it
					params.clear();
					params.add(new BasicNameValuePair("userid", String.valueOf(userid)));
					params.add(new BasicNameValuePair("topic", topic));
					params.add(new BasicNameValuePair("subtopic", sub_topic));
					
					// Retrieve tutorial progress from where the user previously left off
					// If no progress found, create a new one
					json = jsonParser.makeHttpRequest(Constants.URL_MANAGE_PROGRESS,
							Constants.HTTP_POST, params);
					
					success = json.getInt(Constants.JSON_SUCCESS);
					
					Log.i(Constants.LOG_TUTORIAL, "Retrieve tutorial progress success: " + success);
					    
					if (success == 2) {
						progress = json.getInt(Constants.TUT_PROGRESS);				
					}
					
					Log.i(Constants.LOG_TUTORIAL, "Progress: " + progress);
				}
			} catch (JSONException e) {
				Log.i(Constants.LOG_TUTORIAL, e.toString());
			}
			return progress;
		}
		
		@Override
		protected void onPostExecute(Integer progress) {
			pDialog.dismiss();
			
			// No tutorials found, return to previous screen
			if (resultList.length() == 0) {		
				// Display dialog box, inform user that no tutorials has been found
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				
				builder
				.setCancelable(false)
				.setTitle(R.string.noTutFoundTitle)
				.setMessage(R.string.noTutFoundMsg)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub					
						// Returns to sub-topic selection screen
						dialog.cancel();
						finish();						
					}
				});
				
				// Creates the dialog
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				//Use RGB_565 to reduce peak memory usage on large screen device
			    flipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
			    TutorialBaseAdapter adapter = new TutorialBaseAdapter(TutorialActivity.this);
			    flipView.setAdapter(adapter, (progress - 1));
				setContentView(flipView);
			}		
		}
	}
	
	private class TutorialBaseAdapter extends BaseAdapter {
	
		private int totalCount = 0;
		private int repeatCount = 1;
		
		private View layout;
	    private LayoutInflater inflater;    
	    private ImageLoader imgLoader = new ImageLoader(context);
	    
		private TutorialBaseAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return urlList.size() * repeatCount;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			layout = convertView;			
		    if (convertView == null) {
		    	layout = inflater.inflate(R.layout.activity_tutorial, null);
		    }	
		    
		    Log.i(Constants.LOG_TUTORIAL, "position: " + position); 
		    
		    totalCount = getCount();
		    imgLoader.DisplayImage(urlList.get(position % urlList.size()), R.drawable.ic_launcher, 
		    		UI.<ImageView>findViewById(layout, R.id.lessonView));
		    
		    ProgressBar tutBar = UI.<ProgressBar>findViewById(layout, R.id.tut_progbar);
		    tutBar.setMax(totalCount);
		    tutBar.setProgress(position + 1);
		    
		    Log.i(Constants.LOG_TUTORIAL, "ProgressBar Bug output: " + tutBar.getProgress());
	        
		    UI.<TextView>findViewById(layout, R.id.tut_progtext).setText(position + 1
		    		+ "/" + totalCount);
		    
		    UI.<Button>findViewById(layout, R.id.tut_endbtn)
	          .setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// If user is not guest, update progress 
					if (user == null) {
						finish();
					} else {
						Log.i(Constants.LOG_TUTORIAL, "EXECUTING UPDATE...");
						new UpdateProgress(position + 1).execute();
					}
				}
			});
		    
			return layout;
		}
		
		private class UpdateProgress extends AsyncTask<String, String, Integer> {
			
			private int curProgress;
			private ProgressDialog pDialog;
			
			public UpdateProgress(int curProgress) {
				this.curProgress = curProgress;
			}
			
			@Override
	        protected void onPreExecute() {
				super.onPreExecute();
	            pDialog = new ProgressDialog(TutorialActivity.this);
	            pDialog.setTitle(Constants.TITLE_SAVING_PROG);
	            pDialog.setMessage(Constants.MESSAGE_PLEASE_WAIT);
	            pDialog.setIndeterminate(true);
	            pDialog.setCancelable(false);
	            pDialog.show();
			}
			
			@Override
			protected Integer doInBackground(String... args) {
				// TODO Auto-generated method stub
				// Parameters for the POST request
				List<NameValuePair> params = new ArrayList<NameValuePair>();	
				params.add(new BasicNameValuePair("userid", String.valueOf(userid)));
				params.add(new BasicNameValuePair("topic", topic));
				params.add(new BasicNameValuePair("subtopic", sub_topic));		
				params.add(new BasicNameValuePair("progress", String.valueOf(curProgress)));
				
				JSONParser jsonParser = new JSONParser();
				
				JSONObject json = jsonParser.makeHttpRequest(Constants.URL_UPDATE_PROGRESS, 
						Constants.HTTP_POST, params);
				
				int success = 0;
				try {
					success = json.getInt(Constants.JSON_SUCCESS);
				} catch (JSONException e) {
					Log.i(Constants.LOG_TUTORIAL, e.toString());
				}
				
				Log.i(Constants.LOG_TUTORIAL, "Update progress success: " + success);
				
				return success;
			}
			
			@Override
			protected void onPostExecute(Integer success) {
				pDialog.dismiss();
				
				// Display dialog box, inform user update status
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(false);
				
				if (success == 1) {
					builder.setTitle(R.string.saveProgSucTitle);
				} else {
					builder.setTitle(R.string.saveProgFailTitle);
					builder.setMessage(R.string.saveProgFailMsg);
				}
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub					
						// Returns to sub-topic selection screen
						dialog.cancel();
						finish();						
					}
				});
				
				// Creates the dialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	}
}
