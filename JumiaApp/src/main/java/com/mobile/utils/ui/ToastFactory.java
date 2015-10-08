package com.mobile.utils.ui;

import android.content.Context;

import com.mobile.utils.Toast;
import com.mobile.view.R;

/**
 * Class used to manage all toasts.
 *
 * @author sergiopereira
 */
@Deprecated
public enum ToastFactory {

    ERROR_FB_PERMISSION(R.string.error_fb_permission_not_granted, Toast.LENGTH_LONG),
    ERROR_NO_CONNECTION(R.string.no_connect_dialog_content, Toast.LENGTH_LONG),
    ERROR_CATALOG_LOAD_MORE(R.string.products_could_notloaded, Toast.LENGTH_LONG),
    ERROR_OCCURRED(R.string.error_occured, Toast.LENGTH_SHORT),
    ERROR_PRODUCT_NOT_RETRIEVED(R.string.product_could_not_retrieved, Toast.LENGTH_LONG),
    ERROR_PRODUCT_OUT_OF_STOCK(R.string.product_outof_stock, Toast.LENGTH_SHORT),
    ERROR_ADDED_TO_CART(R.string.some_products_not_added, Toast.LENGTH_SHORT),
    ERROR_UNEXPECTED_PLEASE_RETRY(R.string.error_please_try_again, Toast.LENGTH_SHORT),

    SUCCESS_LOGIN(R.string.succes_login, Toast.LENGTH_LONG),
    SUCCESS_ADDED_FAVOURITE(R.string.products_added_saved, Toast.LENGTH_SHORT),
    SUCCESS_REMOVED_FAVOURITE(R.string.products_removed_saved, Toast.LENGTH_SHORT),
    SUCCESS_ADDED_CART(R.string.added_to_shop_cart_dialog_text, Toast.LENGTH_SHORT);


    private int string;
    private int duration;

    /**
     * Constructor
     */
    ToastFactory(int string, int duration) {
        this.string = string;
        this.duration = duration;
    }

    /**
     * Show a toast.
     */
    public void show(Context context) {
        Toast.makeText(context, context.getString(string), duration).show();
    }
}
