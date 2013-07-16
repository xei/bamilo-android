package pt.rocket.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pt.rocket.view.R;

/**
 * A general Class with UI utils such as set the font <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Manuel Silva
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
}
