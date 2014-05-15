package com.mathtimeexplorer.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheducator.R;
import com.mathtimeexplorer.database.DBAdapter;
import com.mathtimeexplorer.database.JSONParser;
import com.mathtimeexplorer.utils.Constants;
import com.mathtimeexplorer.worksheets.Question;
import com.mathtimeexplorer.worksheets.Quiz;
import com.mathtimeexplorer.worksheets.QuizActivity;
import com.mathtimeexplorer.worksheets.SelectQuizActivity;

public class OtherTopicActivity extends ListActivity{

	final ArrayList<String> topicsList = new ArrayList<String>();
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_othertopic);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			user = extras.getParcelable(Constants.USER);
		}
		ListView lv = getListView();
		lv.setBackgroundColor(Color.TRANSPARENT);

		//Get Topics
		String edulevel = Integer.toString(user.getEduLevel());
		String schoolId = Integer.toString(user.getSchool_id());
		if (edulevel.isEmpty() == false && schoolId.isEmpty() == false) {
			new GetTopics(edulevel,schoolId).execute();
		} 
		else {
			Toast.makeText(getApplicationContext(), "SchoolId and EduLevel must not be empty!",
					Toast.LENGTH_SHORT).show();
		}	
		Button backBtn = (Button)findViewById(R.id.selectothertopicback);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				Bundle extras = getIntent().getExtras();
				intent.putExtras(extras);
				startActivity(intent);
			}
		});
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.e("position",Integer.toString(position));
		Intent intent = new Intent(getBaseContext(), SelectQuizActivity.class);
		Bundle extras = getIntent().getExtras();
		extras.putString("topicname", topicsList.get(position));
		intent.putExtras(extras);
		startActivity(intent);
	}


	class GetTopics extends AsyncTask<String, String, String> {

		private String edulevel;
		private String schoolId;
		private int success = 0;

		private ProgressDialog pDialog;
		private static final String DIALOG_LOGIN_TITLE = "Getting Other Topics...";
		private static final String DIALOG_LOGIN_MESSAGE = "Please wait.";

		private JSONObject json = null;

		public GetTopics (String eduLevel, String schoolId) {
			this.edulevel = eduLevel;
			this.schoolId = schoolId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(OtherTopicActivity.this);
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
			params.add(new BasicNameValuePair("edulevel", edulevel));
			params.add(new BasicNameValuePair("schoolId", schoolId));

			json = jsonParser.makeHttpRequest(Constants.URL_GETOTHERTOPIC, "POST", params);
			try {
				success = json.getInt(Constants.JSON_SUCCESS);
				Log.e("Success", Integer.toString(success));
			} catch (JSONException e) {
				Log.i(Constants.LOG_MAIN, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// If success = 1, user is authenticated with the server successfully

			if (success == 1) {
				try {
					// Retrieve the user object from JSON and save it to class user
					JSONArray obj = json.getJSONArray("topic");

					for(int i=0;i<obj.length();i++){
						JSONObject topic = obj.getJSONObject(i);
						topicsList.add(topic.getString("topic_name"));
					}

					// Save user into the local database


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i(Constants.LOG_MAIN, e.toString());
				}
				OtherTopicActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						String[] arr = topicsList.toArray(new String[topicsList.size()]);
						for(int i=0;i<arr.length;i++){
							Log.e("test",arr[i]);
						}
						MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), arr);
						setListAdapter(adapter);


					}
				});

			} else {
				OtherTopicActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),"Unable to retrieve Topics.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			// dismiss the dialog once done
			pDialog.dismiss();
		}
	}


	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;

		public MySimpleArrayAdapter(Context context, String[] values) {
			super(context, R.layout.rowlayout, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			textView.setText(values[position]);
			LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.rowbackground);
			layout.setBackgroundColor(Color.TRANSPARENT);
			return rowView;
		}
	} 
}
