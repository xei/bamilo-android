package com.mobile.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ad4screen.sdk.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.GWearManager;

/**
 * Class used to receive the push notification from Accengage for GoogleWear.
 */
public class PushNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = PushNotificationReceiver.class.getSimpleName();

    /**
     * Method to receive
     * @see "http://wiki.accengage.com/android/javadoc/reference/com/ad4screen/sdk/Constants.html"
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Print.i(TAG, "ON RECEIVE");
        // Get action
        String action = intent.getAction();
        Print.i(TAG, "INTENT ACTION: " + action);
        Print.i(TAG, "INTENT EXTRAS: " + intent.getExtras());
        if(!TextUtils.isEmpty(action) && !action.equals(Constants.ACTION_CLICKED)) {
            GWearManager mGWearManager = new GWearManager(context);
            mGWearManager.parseLocalNotification(intent);
        }
    }

}