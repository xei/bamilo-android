package com.mobile.newFramework.tracking;

import android.content.Context;

import com.mobile.framework.R;
import com.mobile.newFramework.utils.output.Print;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class ApptimizeTracking {
    private static final String TAG = ApptimizeTracking.class.getSimpleName();

    public static void startup(Context context) {
        Print.d(TAG, "APPTIMIZE Startup");

        boolean isEnabled = context.getResources().getBoolean(R.bool.apptimize_enabled);

        if (isEnabled) {
            Print.d(TAG, "Apptimize -> INITITALIZED");
            //String apptimize_apikey = context.getString(R.string.apptimize_apikey);
            //Apptimize.setup(context, apptimize_apikey);
        } else {
            Print.d(TAG, "Apptimize is not enabled");
        }
    }
    
}
