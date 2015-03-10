package com.mobile.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobile.utils.GWearManager;

import de.akquinet.android.androlog.Log;

public class PushNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = PushNotificationReceiver.class.getSimpleName();
    private Context mContext;
    public GWearManager mGWearManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.i(TAG, "ON RECEIVE");
        // Get action
        String action = intent.getAction();
        Log.i(TAG, "INTENT ACTION: " + action);
        Log.i(TAG, "INTENT EXTRAS: " + intent.getExtras());

        mGWearManager = new GWearManager(mContext);
        mGWearManager.parseLocalNotification(intent);

    }

}