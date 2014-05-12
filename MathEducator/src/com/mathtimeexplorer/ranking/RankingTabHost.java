package com.mathtimeexplorer.ranking;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.matheducator.R;
import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;

@SuppressWarnings("deprecation")
public class RankingTabHost extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_tabhost);
		
		User user = null;
		
	    ArrayList<String> topicNameList = getIntent()
	    		.getStringArrayListExtra(Constants.TOPIC);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
		   user = (User) extras.getParcelable(Constants.USER);
		   if (user != null) {
			   Log.i("RankingTabHost", "User id:" + user.getApp_user_id());
		   }
		}
		
		// create the TabHost that will contain the Tabs
	    TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		TabSpec classTab = tabHost.newTabSpec(Constants.TAB_CLASS);
        TabSpec schoolTab = tabHost.newTabSpec(Constants.TAB_SCHOOL);
		
		// Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        classTab.setIndicator(Constants.TAB_CLASS);
        Intent classIntent = new Intent(this, RankingTab.class);
        classIntent.putExtra(Constants.TAB_CHOICE, Constants.TAB_CLASS);
        classIntent.putStringArrayListExtra(Constants.TOPIC, topicNameList);
        classIntent.putExtra(Constants.USER, user);
        classTab.setContent(classIntent);
        
        schoolTab.setIndicator(Constants.TAB_SCHOOL);
        Intent schoolIntent = new Intent(this, RankingTab.class);
        schoolIntent.putExtra(Constants.TAB_CHOICE, Constants.TAB_SCHOOL);
        schoolIntent.putStringArrayListExtra(Constants.TOPIC, topicNameList);
        schoolIntent.putExtra(Constants.USER, user);
        schoolTab.setContent(schoolIntent);
        
        // Add the tabs  to the TabHost to display
        tabHost.addTab(classTab);
        tabHost.addTab(schoolTab);
	}
}


