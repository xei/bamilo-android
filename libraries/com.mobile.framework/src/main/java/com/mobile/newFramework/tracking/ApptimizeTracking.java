package com.mobile.newFramework.tracking;

import android.content.Context;

import com.mobile.framework.R;
import com.mobile.newFramework.utils.output.Print;

/**
 * @author Andre Lopes
 */
public class ApptimizeTracking {

    public static void startup(Context context) {
        Print.i("APPTIMIZE Startup");
        boolean isEnabled = context.getResources().getBoolean(R.bool.apptimize_enabled);
        if (isEnabled) {
            Print.i("Apptimize -> INITITALIZED");
            //String apptimize_apikey = context.getString(R.string.apptimize_apikey);
            //Apptimize.setup(context, apptimize_apikey);
        } else {
            Print.i("Apptimize is not enabled");
        }
    }

}
