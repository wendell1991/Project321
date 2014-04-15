package com.mathtimeexplorer.ranking;

import com.example.matheducator.R;
import com.mathtimeexplorer.misc.Constants;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class RankingTabHost extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_tabhost);
		
		// create the TabHost that will contain the Tabs
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		TabSpec classTab = tabHost.newTabSpec(Constants.TAB_CLASS);
        TabSpec schoolTab = tabHost.newTabSpec(Constants.TAB_SCHOOL);
		
		// Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
		
        classTab.setIndicator(Constants.TAB_CLASS);
        classTab.setContent(new Intent(this, RankingTab.class));
        
        schoolTab.setIndicator(Constants.TAB_SCHOOL);
        schoolTab.setContent(new Intent(this, RankingTab.class));
        
        // Add the tabs  to the TabHost to display
        tabHost.addTab(classTab);
        tabHost.addTab(schoolTab);
	}
}


