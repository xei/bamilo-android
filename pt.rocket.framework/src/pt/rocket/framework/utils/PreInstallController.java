package pt.rocket.framework.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.TelephonyManager;
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
    public static void init(Context context) {
        ApplicationInfo applicationInfo =  context.getApplicationInfo();        
        if ((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) != 0) {
            Log.i(TAG, "PRE INSTALLED APPLICATION");
            TelephonyManager telephonyManager =(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Get operator name
            String operatorName = telephonyManager.getNetworkOperatorName();
            // Set AdX
//            AdXTracker.preInstall(context, Build.MANUFACTURER + "-" + operatorName);
            // Set UA
//            Darwin.preInstall(context, Build.MANUFACTURER + "-" + operatorName);
        }
    }
    
    public boolean isPreInstalled(Context context) {
        ApplicationInfo applicationInfo =  context.getApplicationInfo();
        if ((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) != 0) {
            return true;
        } else {
        	return false;
        }
    }
}
