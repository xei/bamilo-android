package com.mobile.utils.ui;

import android.content.Context;
import android.support.annotation.IntDef;

import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class used to manage all toasts.
 *
 * @author sergiopereira
 */
public class ToastManager {

    /*
     * #### ERROR ####
     */
    public static final int ERROR_FB_PERMISSION             = 0;
    public static final int ERROR_NO_CONNECTION             = 1;
    public static final int ERROR_CATALOG_LOAD_MORE         = 2;
    public static final int ERROR_OCCURRED                  = 3;
    public static final int ERROR_PRODUCT_NOT_RETRIEVED     = 4;
    public static final int ERROR_PRODUCT_OUT_OF_STOCK      = 5;
    public static final int ERROR_ADDED_TO_CART             = 6;
    public static final int ERROR_UNEXPECTED_PLEASE_RETRY   = 7;

    /*
     * #### SUCCESS ####
     */
    public static final int SUCCESS_LOGIN                   = 8;
    public static final int SUCCESS_ADDED_FAVOURITE         = 9;
    public static final int SUCCESS_REMOVED_FAVOURITE       = 10;
    public static final int SUCCESS_ADDED_CART              = 11;

    @IntDef({
            // #### ERROR ####
            ERROR_FB_PERMISSION,
            ERROR_NO_CONNECTION,
            ERROR_CATALOG_LOAD_MORE,
            ERROR_OCCURRED,
            ERROR_PRODUCT_NOT_RETRIEVED,
            ERROR_PRODUCT_OUT_OF_STOCK,
            ERROR_ADDED_TO_CART,
            ERROR_UNEXPECTED_PLEASE_RETRY,
            // #### SUCCESS ####
            SUCCESS_LOGIN,
            SUCCESS_ADDED_FAVOURITE,
            SUCCESS_REMOVED_FAVOURITE,
            SUCCESS_ADDED_CART
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeMessage {}

    /**
     * Show a toast.
     */
    public static void show(Context context, @TypeMessage int type) {
        switch (type) {
            /*
             * #### ERROR ####
             */
            case ERROR_FB_PERMISSION:
                Toast.makeText(context, context.getString(R.string.error_fb_permission_not_granted), Toast.LENGTH_LONG).show();
                break;
            case ERROR_NO_CONNECTION:
                Toast.makeText(context, context.getString(R.string.no_connect_dialog_content), Toast.LENGTH_LONG).show();
                break;
            case ERROR_CATALOG_LOAD_MORE:
                Toast.makeText(context, context.getString(R.string.products_could_notloaded), Toast.LENGTH_LONG).show();
                break;
            case ERROR_OCCURRED:
                Toast.makeText(context, context.getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                break;
            case ERROR_PRODUCT_NOT_RETRIEVED:
                Toast.makeText(context, context.getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                break;
            case ERROR_PRODUCT_OUT_OF_STOCK:
                Toast.makeText(context, context.getString(R.string.product_outof_stock), Toast.LENGTH_SHORT).show();
                break;
            case ERROR_ADDED_TO_CART:
                Toast.makeText(context, context.getString(R.string.some_products_not_added), Toast.LENGTH_SHORT).show();
                break;
            case ERROR_UNEXPECTED_PLEASE_RETRY:
                Toast.makeText(context, context.getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                break;
            /*
             * #### SUCCESS ####
             */
            case SUCCESS_LOGIN:
                Toast.makeText(context, context.getString(R.string.succes_login), Toast.LENGTH_LONG).show();
                break;
            case SUCCESS_ADDED_FAVOURITE:
                Toast.makeText(context, context.getString(R.string.products_added_saved), Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS_REMOVED_FAVOURITE:
                Toast.makeText(context, context.getString(R.string.products_removed_saved), Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS_ADDED_CART:
                Toast.makeText(context, context.getString(R.string.added_to_shop_cart_dialog_text), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

}
