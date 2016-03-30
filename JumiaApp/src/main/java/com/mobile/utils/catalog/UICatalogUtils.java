package com.mobile.utils.catalog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Class used to help catalog UI.
 * @author spereira
 */
public class UICatalogUtils {

    private static final String TAG = UICatalogUtils.class.getSimpleName();


    /**
     * Set the catalog title
     */
    public static void setCatalogTitle(BaseActivity baseActivity, String name) {
        baseActivity.setActionBarTitle(name);
    }

    /**
     * Set the filter button state, to show as selected or not.<br>
     * The Android M (API 23) has an issue to refresh the drawable, so is used a post runnable.
     */
    public static void setFilterButtonState(@Nullable final View button, final boolean hasFilterValues) {
        if (button != null) {
            Log.i(TAG, "SET FILTER BUTTON STATE: " + button.isSelected());
            button.post(new Runnable() {
                @Override
                public void run() {
                    button.setSelected(hasFilterValues);
                }
            });
        }
    }

    /**
     * Set button state when catalog show no internet connection error.
     */
    public static void setFilterButtonActionState(View button, boolean selectable, View.OnClickListener listener){
        if (button != null) {
            if (!selectable) {
                button.setOnClickListener(null);
                button.setEnabled(false);
            } else {
                button.setOnClickListener(listener);
                button.setEnabled(true);
            }
        }
    }

    /**
     * Show the goto top button
     * @param context - the application context
     * @param button - the button
     */
    public static void showGotoTopButton(Context context, View button) {
        if(button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom);
            button.startAnimation(animation);
        }
    }

    /**
     * Hide the goto top button.
     * @param context - the application context
     * @param button - the button
     */
    public static void hideGotoTopButton(Context context, View button) {
        if(button.getVisibility() != View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom);
            button.startAnimation(animation);
            button.setVisibility(View.INVISIBLE);
        }
    }

}
