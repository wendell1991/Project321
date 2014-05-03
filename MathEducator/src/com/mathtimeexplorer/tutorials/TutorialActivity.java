package com.mathtimeexplorer.tutorials;

import java.util.ArrayList;
import java.util.List;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.utils.UI;
import com.example.matheducator.R;
import com.mathtimeexplorer.utils.Constants;

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
import android.widget.TextView;

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
		    		  (inflater.getContext().getResources(), image, layout.getWidth(), layout.getHeight()));
		    
			return layout;
		}
		
		private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				final int heightRatio = Math.round((float) height / (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);

				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			return inSampleSize;
		}
		
		private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
		        int reqWidth, int reqHeight) {

		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeResource(res, resId, options);

		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeResource(res, resId, options);
		}
	}
}
