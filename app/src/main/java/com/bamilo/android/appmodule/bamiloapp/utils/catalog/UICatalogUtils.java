package com.bamilo.android.appmodule.bamiloapp.utils.catalog;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.objects.catalog.CatalogPage;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class used to help catalog UI.
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

    // TODO: 8/14/2017 no usage for this method, remove it asap
    /**
     * Set the filter button state, to show as selected or not.<br>
     * The Android M (API 23) has an issue to refresh the drawable, so is used a post runnable.
     */
    /*public static void setFilterButtonState(@Nullable final View button, final ContentValues filterValues, @Nullable final TextView descriptionLabel, final CatalogPage catalogPage) {
        if (button != null) {
            Print.i("SET FILTER BUTTON STATE: " + button.isSelected());
            button.post(new Runnable() {
                @Override
                public void run() {
                    boolean hasFilter = filterValues.size() > 0;
                    button.setSelected(hasFilter);
                    if (hasFilter) {
                        String desc = "";
                        int count = 0;
                        for (String entry : filterValues.keySet()) {
                            count++;
                            if (count > 2) break;
                            for (int index = 0; index < catalogPage.getFilters().size(); index++) {
                                if (catalogPage.getFilters().get(index).getId().equals(entry)) {
                                    desc = desc + catalogPage.getFilters().get(index).getName() + ",";
                                }
                            }
                        }
                        if (filterValues.size()<3 && desc.endsWith(",")) {
                            desc = desc.substring(0, desc.length()-1);
                        } else if (desc.trim().length()>0) {
                            desc = desc + "...";
                        }
                        descriptionLabel.setText(desc);
                        if (catalogPage.getFilters() == null || catalogPage.getFilters().size() == 0) {
                            button.findViewById(R.id.catalog_bar_filter).setEnabled(false);
                            button.findViewById(R.id.catalog_bar_button_filter).setEnabled(false);
                            button.findViewById(R.id.catalog_bar_description_filter).setEnabled(false);
                            ((TextView)button.findViewById(R.id.catalog_bar_description_filter)).setText("برند، قیمت، ...");
                            button.setSelected(false);
                            button.setBackgroundResource(R.color.black_300);

                        }
                    }
                    else {
                        String filterNames = "";

                        if (catalogPage.hasFilters()) {
                            filterNames = catalogPage.getFilters().get(0).getName();
                            if (catalogPage.getFilters().size() > 1)
                                filterNames = filterNames + ", " + catalogPage.getFilters().get(1).getName();
                            if (catalogPage.getFilters().size() > 2)
                                filterNames = filterNames + ", ...";
                        }
                        else {
                            button.setEnabled(false);
                        }
                        descriptionLabel.setText(filterNames);
                    }
                }
            });
        }
    }*/

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
