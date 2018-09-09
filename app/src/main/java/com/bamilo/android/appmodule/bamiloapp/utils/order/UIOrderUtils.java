package com.bamilo.android.appmodule.bamiloapp.utils.order;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.OrderReturnStepsMain;

/**
 * UI Order helper
 * @author spereira
 */
public class UIOrderUtils {

    /**
     * Set return section
     */
    @SuppressLint("SwitchIntDef")
    public static void setReturnSections(@OrderReturnStepsMain.ReturnStepType int step, @NonNull View view, @IdRes int group, @Nullable String sub, @NonNull View.OnClickListener listener) {
        // Find section
        View section = view.findViewById(group);
        // Validate step
        @StringRes int title = R.string.return_label;
        switch (step) {
            case OrderReturnStepsMain.REASON:
                title = R.string.return_reason;
                break;
            case OrderReturnStepsMain.METHOD:
                title = R.string.return_method;
                break;
            case OrderReturnStepsMain.REFUND:
                title = R.string.return_payment;
                break;
            default:
                break;
        }
        // Title
        ((TextView) section.findViewById(R.id.section_item_title)).setText(view.getContext().getString(title));
        // Sub
        ((TextView) section.findViewById(R.id.section_item_sub_title)).setText(sub);
        // Button
        View button = section.findViewById(R.id.section_item_button);
        button.setTag(R.id.target_type, step);
        button.setOnClickListener(listener);
    }

}
