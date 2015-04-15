package com.mobile.utils.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.mobile.framework.utils.DeviceInfoHelper;
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
     * Set transparency to view.
     * @see https://source.android.com/source/build-numbers.html
     * @param view The view, not null
     * @param alpha The alpha view
     */
    public static void setAlpha(final View view, final float alpha) {
        DeviceInfoHelper.executeCodeBasedOnHoneyCombVersion(new DeviceInfoHelper.IDeviceVersionBasedCode() {

            @Override
            public void highVersionCallback() {
                view.setAlpha(alpha);
            }

            @Override
            public void lowerVersionCallback() {
                final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
                animation.setDuration(0);
                animation.setFillAfter(true);
                view.startAnimation(animation);
            }
        });
    }
    
    /**
     * Show or hide a set of views.
     * @param visibility The visibility parameter for all.
     * @param views The views that some can be null.
     * @author sergiopereira
     */
    public static void showOrHideViews(int visibility, View... views) {
        for (View view : views) if (view != null) view.setVisibility(visibility);
    }
    
    /**
     * Set the visibility
     * @param view The view that can be null
     * @param show The visibility parameter
     * @author sergiopereira
     */
    public static void setVisibility(View view, boolean show) {
        if (view != null) view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Animate the view with a fade in, after an offset a fade out.
     * TODO - Create this animation using XML, http://developer.android.com/guide/topics/graphics/view-animation.html
     * @param context The application context
     * @param animatedView The view
     * @param visibleOffset The visible offset
     */
    public static void animateFadeInAndOut(Context context, View animatedView, int visibleOffset){
//        if (!isAnimating(animatedView)) {
        // Set view as invisible for old Android versions
        animatedView.setVisibility(View.INVISIBLE);
        // Create the fade in and fade out animation
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        fadeOut.setStartOffset(fadeIn.getDuration() + visibleOffset);
        // Create a set with animations
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        // Remove the old and start the new animation
        animatedView.clearAnimation();
        animatedView.startAnimation(animation);
//        }
    }

    /**
     * Validate if the current view is being animated
     * @param view The animated view
     * @return true or false
     * @author sergio pereira
     */
    public static boolean isAnimating(View view) {
        return view != null && view.getAnimation() != null && (view.getAnimation().hasStarted() || !view.getAnimation().hasEnded());
    }
    
}
