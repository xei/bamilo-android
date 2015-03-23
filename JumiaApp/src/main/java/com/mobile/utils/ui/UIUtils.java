package com.mobile.utils.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mobile.view.R;

/**
 * A general Class with UI utils such as set the font <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Manuel Silva
 * @modified Andre Lopes
 *
 */
public class UIUtils {

    public static final String TAG = UIUtils.class.getSimpleName();

    public static int dpToPx(int dp, float density) {
        return Math.round((float)dp * density);
    } 
    
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static int spToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().scaledDensity );
    }

    /**
     * Set transparency to view
     * @see https://source.android.com/source/build-numbers.html
     * @param view
     * @param alpha
     */
    public static void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        } else {
            view.setAlpha(alpha);
        }
    }
    
    /**
     * Show or hide a set of views.
     * @param visibility
     * @param views
     * @author sergiopereira
     */
    public static void showOrHideViews(int visibility, View... views) {
        for (View view : views) if (view != null) view.setVisibility(visibility);
    }
    
    /**
     * Set the visibility
     * @param view
     * @param show
     */
    public static void setVisibility(View view, boolean show) {
        if (view != null) view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * TODO
     * @param context
     * @param warningView
     * @param warningLength
     */
    public static void animateWarning(Context context, final View warningView, final int warningLength){
        if (warningView != null) {
            warningView.setVisibility(View.INVISIBLE);

            final Animation mAnimFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            final Animation mAnimFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

            warningView.clearAnimation();
            warningView.startAnimation(mAnimFadeIn);

            mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(final Animation animation) {
                    warningView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    warningView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            warningView.startAnimation(mAnimFadeOut);
                        }
                    }, warningLength);
                }
            });

            mAnimFadeOut.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(final Animation animation) {
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    warningView.setVisibility(View.GONE);
                }
            });
        }

    }
    
}
