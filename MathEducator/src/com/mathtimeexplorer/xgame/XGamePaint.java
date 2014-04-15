package com.mathtimeexplorer.xgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.ImageView;

import com.example.matheducator.R;

public class XGamePaint {
	
	private Context context;
	
	public XGamePaint(Context context) {
		this.context = context;
	}
	
	public void configureImage(int value, ImageView view){
		int firstDigit = 0;
		int firstImg = 0;
		int secondDigit = 0;
		int secondImg = 0;
		int thirdDigit = 0;
		int thirdImg = 0;
		
		// Checks whether the value is 1, 2 or 3 digits long
		switch(String.valueOf(value).length()){
			case 1: {
				firstDigit = Integer.parseInt(String.valueOf(value).substring(0, 1));
				firstImg = findCorrespondingImg(firstDigit);
				view.setImageResource(firstImg);
				break;
			}
			case 2: {
				firstDigit = Integer.parseInt(String.valueOf(value).substring(0, 1));
				secondDigit = Integer.parseInt(String.valueOf(value).substring(1, 2));
				firstImg = findCorrespondingImg(firstDigit);
				secondImg = findCorrespondingImg(secondDigit);
				paintImage(firstImg, secondImg, view);
				break;
			}
			case 3: {
				firstDigit = Integer.parseInt(String.valueOf(value).substring(0, 1));
				secondDigit = Integer.parseInt(String.valueOf(value).substring(1, 2));
				thirdDigit = Integer.parseInt(String.valueOf(value).substring(2, 3));
				firstImg = findCorrespondingImg(firstDigit);
				secondImg = findCorrespondingImg(secondDigit);
				thirdImg = findCorrespondingImg(thirdDigit);
				paintImage(firstImg, secondImg, thirdImg, view);
				break;
			}
		}
	}
	
	// Method to merge two images together for a image-view
	private void paintImage(int firstImg, int secondImg, ImageView view) {
		Bitmap firstBit = BitmapFactory.decodeResource(context.getResources(), firstImg);
		Bitmap secondBit = BitmapFactory.decodeResource(context.getResources(), secondImg);
		int oneImgWidth = firstBit.getWidth();
		int twoImgWidth = secondBit.getWidth();
		// Create the combined image
		Bitmap result = Bitmap.createBitmap(oneImgWidth + twoImgWidth,  firstBit.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas combinedImage = new Canvas(result);
		combinedImage.drawBitmap(firstBit, 0f, 0f, null);
		combinedImage.drawBitmap(secondBit, oneImgWidth, 0f, null);
		view.setImageBitmap(result);
	}
	
	// Method to merge three images together for a image-view
	private void paintImage(int firstImg, int secondImg, int thirdImg, ImageView view) {
		Bitmap firstBit = BitmapFactory.decodeResource(context.getResources(), firstImg);
		Bitmap secondBit = BitmapFactory.decodeResource(context.getResources(), secondImg);
		Bitmap thirdBit = BitmapFactory.decodeResource(context.getResources(), thirdImg);
		int oneImgWidth = firstBit.getWidth();
		int twoImgWidth = oneImgWidth * 2;
		int threeImgWidth = oneImgWidth * 3;
		// Create the combined image
		Bitmap result = Bitmap.createBitmap(threeImgWidth, firstBit.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas combinedImage = new Canvas(result);
		combinedImage.drawBitmap(firstBit, 0f, 0f, null);
		combinedImage.drawBitmap(secondBit, oneImgWidth, 0f, null);
		combinedImage.drawBitmap(thirdBit, twoImgWidth, 0f, null);
		view.setImageBitmap(result);
	}
	
	private int findCorrespondingImg(int input) {
		int result = 0;
		switch(input){
		case 1: 
			result = R.drawable.xgameone;
			break;
		case 2: 
			result = R.drawable.xgametwo;
			break;
		case 3: 
			result = R.drawable.xgamethree;
			break;
		case 4: 
			result = R.drawable.xgamefour;
			break;
		case 5: 
			result = R.drawable.xgamefive;
			break;
		case 6: 
			result = R.drawable.xgamesix;
			break;
		case 7: 
			result = R.drawable.xgameseven;
			break;
		case 8:
			result = R.drawable.xgameeight;
			break;
		case 9: 
			result = R.drawable.xgamenine;
			break;
		default:
			result = R.drawable.xgamezero;
		}
		return result;
	}
}
