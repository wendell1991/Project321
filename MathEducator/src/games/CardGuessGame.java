package games;

import com.example.matheducator.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class CardGuessGame extends Activity {
	
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cardguess_main);
		
		View rootLayout = (View) findViewById(R.id.main_activity_root);
		final ImageView cardFace = (ImageView) findViewById(R.id.main_activity_card_face);
		final ImageView cardBack = (ImageView) findViewById(R.id.main_activity_card_back);
		
		final AnimatorSet anim1 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in); 
		final AnimatorSet anim2 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out); 
		final AnimatorSet anim3 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in); 
		final AnimatorSet anim4 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out); 
		
		anim1.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				cardFace.setVisibility(View.GONE);
				cardBack.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}			
		});
		anim2.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				cardBack.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}			
		});
		anim3.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				cardFace.setVisibility(View.VISIBLE);
				cardBack.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		anim4.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		cardFace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				anim1.setTarget(cardFace);
				anim1.start();
			}
		});
		
		cardBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				anim3.setTarget(cardBack);
				anim3.start();
			}
		});
	}
}
