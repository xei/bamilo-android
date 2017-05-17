package com.mobile.utils.dialogfragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

/**
 * Class used to create a custom toast
 * @author sergiopereira
 *
 */
public class CatalogPageToastView extends Toast {

    /**
     * View constructor
     * @author sergiopereira
     */
    public CatalogPageToastView(Context context) {
        super(context);
    }
    
    /**
     * Create the custom toast view
     * @author sergiopereira
     * @return CustomToastView
     */
    public static Toast makeText(BaseActivity activity, CharSequence message, int duration) {
        // Get layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_catalog_page, (ViewGroup) activity.findViewById(R.id.toast_exit_layout_root));
        // Set message
        TextView text = (TextView) layout.findViewById(R.id.toast_exit_message);
        text.setText(message);
        // Create and show the toast
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(duration);
        toast.setView(layout);
        return toast;
    }

}
