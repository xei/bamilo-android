package pt.rocket.utils.ui;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

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
    public final static void showOrHideViews(int visibility, View... views) {
        for (View view : views) if (view != null) view.setVisibility(visibility);
    }
    
    /**
     * Set the visibility
     * @param view
     * @param show
     */
    public final static void setVisibility(View view, boolean show) {
        if (view != null) view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
}
