package com.mobile.utils.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

    public static void showViewFadeIn(@NonNull View view) {
        if (view.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.abc_fade_in);
            view.startAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setDrawableByString(@NonNull ImageView imageView, String name) {
        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        imageView.setImageResource(id);

    }



    /**
     * Animate a view sliding from down to top
     * @param animatedView -  the animated view
     * */
    public static void animateSlideUp(@NonNull View animatedView) {
        animatedView.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.slide_up);
        animatedView.startAnimation(animation);
        animatedView.setVisibility(View.GONE);
    }


    /**
     * Animate a view sliding from top to down
     * @param animatedView -  the animated view
     * */
    public static void animateSlideDown(@NonNull View animatedView) {
        animatedView.clearAnimation();
        animatedView.setVisibility(View.VISIBLE);
        Animation downAnimation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.slide_down);
        Animation upAnimation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.slide_up);

        upAnimation.setStartOffset(downAnimation.getDuration() + WarningFactory._3_SECONDS);
        // Create a set with animations
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(downAnimation);
        animation.addAnimation(upAnimation);
        animatedView.startAnimation(animation);
    }

    /**
     * method responsible for scrolling a scrollview for 60dp
     * This is used for editexts that show in a layout inside a toolbar
     * @param scrollView
     * @param viewToDetectTouch
     */
    public static void scrollToViewByClick(final View scrollView, final View viewToDetectTouch){
        viewToDetectTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollBy(0, dpToPx(60, scrollView.getContext().getResources().getDisplayMetrics().scaledDensity));
                        }
                    }, 500);
                }
                return false;
            }
        });
    }
}
