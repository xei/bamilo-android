package com.mobile.framework.tracking;

import android.content.Context;

import com.apptimize.Apptimize;
import com.mobile.framework.R;
import com.mobile.framework.utils.LogTagHelper;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class ApptimizeTracking {
    private static final String TAG = LogTagHelper.create(ApptimizeTracking.class);

    public static void startup(Context context) {
        Log.d(TAG, "APPTIMIZE Startup");

        boolean isEnabled = context.getResources().getBoolean(R.bool.apptimize_enabled);

        if (isEnabled) {
            Log.d(TAG, "Apptimize -> INITITALIZED");
            String apptimize_apikey = context.getString(R.string.apptimize_apikey);
            Apptimize.setup(context, apptimize_apikey);
        } else {
            Log.d(TAG, "Apptimize is not enabled");
        }
    }
    
}
