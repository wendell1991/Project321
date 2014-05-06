package com.mathtimeexplorer.tutorials;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.utils.UI;
import com.example.matheducator.R;
import com.mathtimeexplorer.utils.Constants;

public class TutorialActivity extends Activity {

	private FlipViewController flipView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		int topic = 0;
		if (extras != null) {
		   topic = extras.getInt(Constants.TOPIC);
		}
		
		flipView = new FlipViewController(this);
		
		//Use RGB_565 to reduce peak memory usage on large screen device
	    flipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
	    flipView.setAdapter(new TutorialBaseAdapter(this));
		setContentView(flipView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		flipView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		flipView.onPause();
	}

	private static class TutorialBaseAdapter extends BaseAdapter {

		private int repeatCount = 1;
	    private LayoutInflater inflater;
	    private List<Integer> imageList;
	    
		private TutorialBaseAdapter(Context context) {
		      inflater = LayoutInflater.from(context);
		      imageList = new ArrayList<Integer>(Arithmetic.additionList);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageList.size() * repeatCount;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View layout = convertView;
		    if (convertView == null) {
		    	layout = inflater.inflate(R.layout.activity_tutorial, null);
		    }
		    
		    final int image = imageList.get(position % imageList.size());
		    Log.i("TutorialActivity", "Image: " + image);
		    
		    UI
		      .<ImageView>findViewById(layout, R.id.lessonView)
		      .setImageBitmap(decodeSampledBitmapFromResource
		    		  (inflater.getContext().getResources(), image, 600, 600));
		    
			return layout;
		}
		
		public static int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			
			// Raw height and width of image
			final int height = options.outHeight;
	    	final int width = options.outWidth;
	    	int inSampleSize = 1;

	    	if (height > reqHeight || width > reqWidth) {
	        	
	    		final int halfHeight = height / 2;
	        	final int halfWidth = width / 2;

	        	// Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        	// height and width larger than the requested height and width.
	        	while ((halfHeight / inSampleSize) > reqHeight
	        			&& (halfWidth / inSampleSize) > reqWidth) {
	            	inSampleSize *= 2;
	        	}
	    	}
	    	return inSampleSize;
		}
		
		public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
		        int reqWidth, int reqHeight) {

		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeResource(res, resId, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeResource(res, resId, options);
		}
	}
}
