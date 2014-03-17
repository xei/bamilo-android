package pt.rocket.framework.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

/**
 * @author sergiopereira
 */
public class PreInstallController {
    
    private final static String TAG = PreInstallController.class.getSimpleName();

    /**
     * Add the pre install tracking
     * @param context
     */
    public static boolean init(Context context) {
        ApplicationInfo applicationInfo =  context.getApplicationInfo();        
        if ((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) != 0) {
            Log.i(TAG, "PRE INSTALLED APPLICATION");
            
            //// Method with operator name
            //TelephonyManager telephonyManager =(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //// Get operator name
            //String operatorName = telephonyManager.getNetworkOperatorName();
            //// Set AdX
            //AdXTracker.preInstall(context, Build.MANUFACTURER + "-" + operatorName);
            //// Set UA
            //Darwin.preInstall(context, Build.MANUFACTURER + "-" + operatorName);
            
            // Method without operator name
            // Set AdX
            AdXTracker.preInstall(context, Build.MANUFACTURER);
            // Set UA
            // Darwin.preInstall(context, Build.MANUFACTURER);

            return true;
        }
        return false;
    }
}
