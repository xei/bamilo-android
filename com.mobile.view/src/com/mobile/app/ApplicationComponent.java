/**
 * 
 */
package com.mobile.app;

import android.content.Context;

import com.mobile.framework.ErrorCode;

/**
 * @author nutzer2
 * 
 */
public abstract class ApplicationComponent {
    
    private ErrorCode result;
    
    public ErrorCode init(Context context) {
        if(result != ErrorCode.NO_ERROR) {
            result = initInternal(context);
        }
        return result;
    }

    protected abstract ErrorCode initInternal(Context context);

}
