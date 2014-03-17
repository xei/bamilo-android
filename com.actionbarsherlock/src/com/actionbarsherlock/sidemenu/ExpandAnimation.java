package com.actionbarsherlock.sidemenu;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class ExpandAnimation extends Animation implements Animation.AnimationListener {
	private LinearLayout appView;
	private LinearLayout navView;
	private LinearLayout navCView;
	private int ANIMATION_DURATION;
	private int LastWidth;
	private int FromWidth;
	private int ToWidth;
	private int LastNavWidth;
	private int FromNavWidth;
	private int ToNavWidth;
	private int contentHeight;
	private int screenWidth;
	float lastAlpha = 1;
	int animRepeatCount;
	int interpolatorWidth;
	boolean isLandscape;
	private static int REPEAT_SIZE = 10;

	public ExpandAnimation(LinearLayout nView, LinearLayout nCView, int sWidth, int fromNavWidth, int toNavWith, int FromWidth, int ToWidth, int height,
			int duration, LinearLayout appV, boolean isLand) {

		this.navView = nView;
		this.navCView = nCView;
		this.appView = appV;
		this.contentHeight = height;
		ANIMATION_DURATION = duration;
		this.FromWidth = FromWidth;
		this.ToWidth = ToWidth;
		this.FromNavWidth = fromNavWidth;
		this.ToNavWidth = toNavWith;
		this.screenWidth = sWidth;
		this.isLandscape = isLand;
		animRepeatCount = 1;
		LastNavWidth = ToNavWidth;
		setDuration(0);
		setRepeatCount(REPEAT_SIZE);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);

		if (ToWidth / 5 >= FromNavWidth / REPEAT_SIZE) {
			interpolatorWidth = ToWidth / REPEAT_SIZE;
		} else {
			interpolatorWidth = FromNavWidth / REPEAT_SIZE;
		}
		// Log.i("interpolator: ", " "+interpolatorWidth);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		navView.scrollTo(0, 0);
		navCView.scrollTo(0, 0);
		appView.scrollTo(ToWidth, 0);
		AlphaAnimation fadeOut = new AlphaAnimation(1, 0.7f);
		fadeOut.setDuration(200);
		fadeOut.setFillAfter(true);
		appView.startAnimation(fadeOut);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

		// if(animRepeatCount<REPEAT_SIZE){
		int margin = LastWidth += ToWidth / REPEAT_SIZE;
		LastNavWidth += FromNavWidth / REPEAT_SIZE;
		// Log.i("ExpandAnimation", "LastNavWidth: "+LastNavWidth);
		int marginNav;
		int marginCNav;
		if (animRepeatCount == 1) {
			navCView.setVisibility(View.VISIBLE);
		}
		if (isLandscape) {
			marginNav = FromNavWidth - (LastNavWidth + 55);
		} else {
			marginNav = FromNavWidth - (LastNavWidth + 50);
		}

		if (animRepeatCount < REPEAT_SIZE - 1) {
			navView.scrollTo(marginNav, 0);
			marginCNav = FromNavWidth - LastNavWidth;
			navCView.scrollTo(marginCNav, 0);

			appView.scrollTo(margin, 0);
		} else {
			navView.scrollTo(0, 0);
			navCView.scrollTo(0, 0);
			appView.scrollTo(ToWidth, 0);
		}

		animRepeatCount += 1;
		// float alpha = 1-animRepeatCount/REPEAT_SIZE;
		// if(alpha<0.7f){
		// alpha=0.7f;
		// }

	}

	@Override
	public void onAnimationStart(Animation animation) {
		navView.scrollTo(FromNavWidth, 0);
		navCView.scrollTo(FromNavWidth, 0);
	}
}
