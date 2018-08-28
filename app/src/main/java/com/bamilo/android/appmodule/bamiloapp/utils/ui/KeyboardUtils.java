package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bamilo.android.framework.service.utils.output.Print;

/**
 * Class used to interact with the keyboard.
 *
 * @author spereira
 */
public class KeyboardUtils {

    private static final String TAG = KeyboardUtils.class.getSimpleName();

    /**
     * This method was created because the method on BaseActivity not working with dynamic forms
     *
     * @param view - The token of the window that is making the request
     * @author sergio pereira
     */
    public static void hide(@Nullable View view) {
        Print.d(TAG, "HIDE KEYBOARD...");
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


}
