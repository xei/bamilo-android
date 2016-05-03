package com.mobile.utils.order;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

/**
 * UI Order helper
 * @author spereira
 */
public class UIOrderUtils {

    /**
     * Set return section
     */
    public static void setReturnSections(@NonNull View view, @IdRes int group, @StringRes int title, @Nullable String sub) {
        // Find section
        View section = view.findViewById(group);
        // Title
        ((TextView) section.findViewById(R.id.section_item_title)).setText(view.getContext().getString(title));
        // Sub
        ((TextView) section.findViewById(R.id.section_item_sub_title)).setText(sub);
        // Button
        section.findViewById(R.id.section_item_button);
    }

}
