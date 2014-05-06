package com.mathtimeexplorer.ranking;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
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
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
		   user = (User) extras.getParcelable(Constants.USER);
		   //Log.i("OptionActivity", "User id:" + user.getApp_user_id());
		}
		
		// create the TabHost that will contain the Tabs
	    TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		
	    /*
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				int currentTab  = tabHost.getCurrentTab();
				if (currentTab == 0) {
					
				} else {
					
				}
			}        	
        });
		*/
		TabSpec classTab = tabHost.newTabSpec(Constants.TAB_CLASS);
        TabSpec schoolTab = tabHost.newTabSpec(Constants.TAB_SCHOOL);
		
		// Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        classTab.setIndicator(Constants.TAB_CLASS);
        Intent classIntent = new Intent(this, RankingTab.class);
        classIntent.putExtra(Constants.TAB_CHOICE, Constants.TAB_CLASS);
        classIntent.putExtra(Constants.USER, user);
        classTab.setContent(classIntent);
        
        schoolTab.setIndicator(Constants.TAB_SCHOOL);
        Intent schoolIntent = new Intent(this, RankingTab.class);
        schoolIntent.putExtra(Constants.TAB_CHOICE, Constants.TAB_SCHOOL);
        schoolIntent.putExtra(Constants.USER, user);
        schoolTab.setContent(schoolIntent);
        
        // Add the tabs  to the TabHost to display
        tabHost.addTab(classTab);
        tabHost.addTab(schoolTab);
	}
}


