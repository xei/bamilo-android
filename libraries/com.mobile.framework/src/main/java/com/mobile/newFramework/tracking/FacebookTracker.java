package com.mobile.newFramework.tracking;

import android.content.Context;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import de.akquinet.android.androlog.Log;


/**
 * Class used for tracking via Facebook.
 * @author sergiopereira
 * @modified Paulo Carvalho
 */
public class FacebookTracker {

    private static final String TAG = FacebookTracker.class.getSimpleName();

    private static FacebookTracker sFacebookTracker;

    private AppEventsLogger mFacebookLogger;

    /**
     * Constructor.
     * @author sergiopereira
     */
    public FacebookTracker(Context context) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(context);
        }
        mFacebookLogger = AppEventsLogger.newLogger(context);
    }

    /**
     * Get singleton instance
     * @return FacebookTracker
     * @author sergiopereira
     */
    public static FacebookTracker get(Context context) {
        return sFacebookTracker == null ? sFacebookTracker = new FacebookTracker(context) : sFacebookTracker;
    }

    /**
     * Startup Tracker.
     * @author sergiopereira
     */
    public static void startup(Context context) {
        sFacebookTracker = new FacebookTracker(context);
    }

    /*
     * ######### BASE #########
     */

    /**
     * Log a simple event.
     * @author sergiopereira
     */
    @SuppressWarnings("unused")
    private void logEvent(String event) {
        mFacebookLogger.logEvent(event);
    }

    /**
     * Log an event with parameters.
     * @author sergiopereira
     */
    @SuppressWarnings("unused")
    private void logEvent(String event, Bundle parameters) {
        mFacebookLogger.logEvent(event, parameters);
    }

    /**
     * Log an event with associated value.
     * @author sergiopereira
     */
    @SuppressWarnings("unused")
    private void logEvent(String event, double valueToSum, Bundle parameters) {
        mFacebookLogger.logEvent(event, valueToSum, parameters);
    }

    /*
     * ######### PUBLIC #########
     */

    /**
     * Track the activated app.
     * @author sergiopereira
     */
    public void trackActivatedApp() {
        Log.i(TAG, "TRACK: ACTIVATED APP");
        logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);
    }


}
