package com.bamilo.android.appmodule.bamiloapp.app;

import android.content.Context;

import com.bamilo.android.framework.service.rest.errors.ErrorCode;

/**
 * @author nutzer2
 */
public abstract class ApplicationComponent {

    @ErrorCode.Code
    private int result;

    @ErrorCode.Code
    public int init(Context context) {
        if (result != ErrorCode.NO_ERROR) {
            result = initInternal(context);
        }
        return result;
    }

    @ErrorCode.Code
    protected abstract int initInternal(Context context);

}
