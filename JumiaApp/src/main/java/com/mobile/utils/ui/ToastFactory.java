package com.mobile.utils.ui;

import android.content.Context;

import com.mobile.utils.Toast;
import com.mobile.view.R;

/**
 * Class used to manage all toasts.
 *
 * @author sergiopereira
 */
public enum ToastFactory {

    ERROR_FB_PERMISSION(R.string.error_fb_permission_not_granted, Toast.LENGTH_LONG),
    ERROR_NO_CONNECTION(R.string.no_connect_dialog_content, Toast.LENGTH_LONG),
    ERROR_CATALOG_LOAD_MORE(R.string.products_could_notloaded, Toast.LENGTH_LONG),
    ERROR_OCCURRED(R.string.error_occured, Toast.LENGTH_SHORT),
    ERROR_PRODUCT_NOT_RETRIEVED(R.string.product_could_not_retrieved, Toast.LENGTH_LONG),

    SUCCESS_LOGIN(R.string.succes_login, Toast.LENGTH_LONG),
    ADDED_FAVOURITE(R.string.products_added_favourite, Toast.LENGTH_SHORT),
    REMOVED_FAVOURITE(R.string.products_removed_favourite, Toast.LENGTH_SHORT);


    private int string;
    private int duration;

    /**
     * Constructor
     *
     * @param string
     * @param duration
     */
    ToastFactory(int string, int duration) {
        this.string = string;
        this.duration = duration;
    }

    /**
     * Show a toast.
     *
     * @param context
     * @param type
     * @author sergiopereira
     */
    public void show(Context context) {
        Toast.makeText(context, context.getString(string), duration).show();
    }
}
