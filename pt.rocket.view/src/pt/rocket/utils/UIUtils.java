package pt.rocket.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

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
    private static Typeface robotoTypeFace;

    public static void setRobotoFont(Context context, View view) {
        if (robotoTypeFace == null) {
            robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto/Roboto-Regular.ttf");
        }
        setFont(view, robotoTypeFace);
    }

    private static void setFont(View view, Typeface robotoTypeFace) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setFont(((ViewGroup) view).getChildAt(i), robotoTypeFace);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(robotoTypeFace);
        }
    }
    
    public static int dpToPx(int dp, float density)
    {
        return Math.round((float)dp * density);
    } 

    public static int spToPx(float dp, Context context)
    {
        return Math.round(dp * context.getResources().getDisplayMetrics().scaledDensity );
    }

    /**
     * Set transparency to view
     * 
     * @param view
     * @param alpha
     */
    public static void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT < 11) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
        else {
            view.setAlpha(alpha);
        }
    }
}
