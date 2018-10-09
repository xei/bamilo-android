package com.bamilo.android.appmodule.bamiloapp.utils.catalog;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class used to help catalog UI.
 *
 * @author spereira
 */
public class UICatalogUtils {

    /**
     * Get query param for category.
     */
    public static String getCategoryQueryParam() {
        return "?" + RestConstants.CATEGORY + "=";
    }

    /**
     * Get query param for seller.
     */
    public static String getSellerQueryParam() {
        return "?" + RestConstants.SELLER + "=";
    }

    /**
     * Get the catalog type and save the respective values into query values.<br> - Catalog types:
     * Hash|Seller|Brand|Category|DeepLink
     */
    public static ContentValues saveCatalogType(@NonNull Bundle arguments,
            @NonNull ContentValues values, String contentId) {
        FragmentType type = (FragmentType) arguments
                .getSerializable(ConstantsIntentExtra.TARGET_TYPE);
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
                case CATALOG_FILTER:
                case CATALOG_NOFILTER:
                    values.put(RestConstants.CATEGORY, contentId);
                    break;
                default:
                    if (contentId != null) {
                        values.put(RestConstants.HASH, contentId);
                    }
                    break;
            }
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
     * Set button state when catalog show no internet connection error.
     */
    public static void setFilterButtonActionState(View button, boolean selectable,
            View.OnClickListener listener) {
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
     *
     * @param context - the application context
     * @param button - the button
     */
    public static void showGotoTopButton(Context context, View button) {
        if (button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils
                    .loadAnimation(context, R.anim.abc_slide_in_bottom);
            button.startAnimation(animation);
        }
    }

    /**
     * Hide the goto top button.
     *
     * @param context - the application context
     * @param button - the button
     */
    public static void hideGotoTopButton(Context context, View button) {
        if (button.getVisibility() != View.INVISIBLE) {
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils
                    .loadAnimation(context, R.anim.abc_slide_out_bottom);
            button.startAnimation(animation);
            button.setVisibility(View.INVISIBLE);
        }
    }
}
