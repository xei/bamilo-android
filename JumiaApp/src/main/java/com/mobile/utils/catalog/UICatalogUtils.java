package com.mobile.utils.catalog;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.akquinet.android.androlog.Log;

/**
 * Class used to help catalog UI.
 * @author spereira
 */
public class UICatalogUtils {

    /**
     * Get the catalog type and save the respective values into query values.<br>
     * - Catalog types: Hash|Seller|Brand|Category|DeepLink
     */
    public static ContentValues saveCatalogType(@NonNull Bundle arguments, @NonNull ContentValues values, String contentId) {
        FragmentType type = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.TARGET_TYPE);
        if (type != null) {
            switch (type) {
                case CATALOG_BRAND:
                    values.put(RestConstants.BRAND, contentId);
                    break;
                case CATALOG_SELLER:
                    values.put(RestConstants.SELLER, contentId);
                    break;
                case CATALOG_DEEP_LINK:
                    ContentValues data = arguments.getParcelable(ConstantsIntentExtra.DATA);
                    if (data != null) {
                        values = data;
                    }
                    break;
                case CATALOG_CATEGORY:
                    values.put(RestConstants.CATEGORY, contentId);
                    break;
                default:
                    values.put(RestConstants.HASH, contentId);
                    break;
            }
        } else {
            Print.e("ERROR: RECEIVED CATALOG TYPE IS NULL");
        }
        return values;
    }


    /**
     * Get the search query and save the respective values into query values.<br>
     */
    public static void saveSearchQuery(@NonNull Bundle arguments, @NonNull ContentValues values) {
        String query = arguments.getString(ConstantsIntentExtra.SEARCH_QUERY);
        if (TextUtils.isNotEmpty(query)) {
            try {
                values.put(RestConstants.QUERY, URLEncoder.encode(query, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Print.w("WARNING: SEARCH QUERY WITH UNSUPPORTED ENCODING");
                values.put(RestConstants.QUERY, query);
            }
        }
    }

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
            Log.i("SET FILTER BUTTON STATE: " + button.isSelected());
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
