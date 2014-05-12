package com.mathtimeexplorer.ranking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mathtimeexplorer.utils.Constants;

public class RankingResult {
	
	// Node names for retrieving results
	private static String TAG_RANK = "rank";
	private static String TAG_FIRST_NAME = "first_name";
	private static String TAG_LAST_NAME = "last_name";
	private static String TAG_RESULT = "result";
	
	private int success = 0;
	private ArrayList<Ranking> rankingList;
	
	public int getSuccess() {
		return success;
	}
	
	public void getRankingResults(JSONObject json) {
		
		JSONArray resultList = null;
		rankingList = new ArrayList<Ranking>();
		
		try{
			// Get success tag and checks whether it is 1
			success = json.getInt(Constants.JSON_SUCCESS);
			
			if (success == 1) {
				resultList = json.getJSONArray(Constants.JSON_RESULTS);
				// Log.i(Constants.LOG_RANKING, "RESULT LIST SIZE: " + resultList.length());
		
				Ranking rank;
				JSONObject obj;
		
				// Obtains the ranking results and save in the ranking-list
				for (int i = 0; i < resultList.length(); i++) {
					obj = resultList.getJSONObject(i);
					rank = new Ranking();
					rank.setRank(obj.getInt(TAG_RANK));
					rank.setFirst_name(obj.getString(TAG_FIRST_NAME));
					rank.setLast_name(obj.getString(TAG_LAST_NAME));
					rank.setResult(obj.getInt(TAG_RESULT));
					rankingList.add(rank);
				}
			}
        } catch (JSONException e) {
			Log.i(Constants.LOG_RANKING, e.toString());
        }
	}
	
	public void setRankTableResults(TableLayout rankTable) {
		TableRow row;
		Ranking rank;
		String spacing = "";
		int rankIndex = 0;
			
		// Static positions of each Views at each rows
		int rankViewIndex = 0;
		int nameViewIndex = 1;
		int resultViewIndex = 2;
		
		//Log.i("RankingResult", "Ranking List Size: " + rankingList.size());
		
        // Populates the ranking result on the table
        for (int i = 1; i <= 5; i++) {
        	row = (TableRow) rankTable.getChildAt(i);              		
        	if (rankingList.size() > rankIndex) {
        		rank = (Ranking) rankingList.get(rankIndex);
        		
        		//Log.i(Constants.LOG_RANKING, "RANK: " + rank.getRank());
        		//Log.i(Constants.LOG_RANKING, "FIRST_NAME: " + rank.getFirst_name());
        		//Log.i(Constants.LOG_RANKING, "LAST_NAME: " + rank.getLast_name());
        		//Log.i(Constants.LOG_RANKING, "RESULT: " + rank.getResult());
        		
        		configureResultView(String.valueOf(rank.getRank()), 
        				(TextView) row.getChildAt(rankViewIndex));
        		
        		configureResultView(String.valueOf(rank.getFirst_name() + spacing
        				+ rank.getLast_name()), (TextView) row.getChildAt(nameViewIndex));
        		
        		configureResultView(String.valueOf(rank.getResult()), 
        				(TextView) row.getChildAt(resultViewIndex));
        		
        		rankIndex++;
        	} else {
        		break;
        	}
       }
	}
	
	private void configureResultView(String value, TextView view) {
		view.setText(value);
		if (view.getVisibility() == View.INVISIBLE) {
			view.setVisibility(View.VISIBLE);
		}			
	}
}
