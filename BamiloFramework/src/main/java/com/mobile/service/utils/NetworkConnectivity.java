package com.mobile.service.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Network connectivity helper
 * @author sergiopereira
 *
 */
public class NetworkConnectivity {
    
    /**
     * Check the connection
     * @return true or false
     * @author sergiopereira
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
