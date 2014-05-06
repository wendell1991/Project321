package com.mathtimeexplorer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context) {
		super(context);
		Typeface face = Typeface.createFromAsset(context.getAssets(), "Roboto-BoldItalic.ttf");
		this.setTypeface(face);
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface face = Typeface.createFromAsset(context.getAssets(), "Roboto-BoldItalic.ttf");
		this.setTypeface(face);
	}

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface face = Typeface.createFromAsset(context.getAssets(), "Roboto-BoldItalic.ttf");
		this.setTypeface(face);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
