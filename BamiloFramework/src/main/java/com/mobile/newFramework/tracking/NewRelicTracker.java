package com.mobile.newFramework.tracking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mobile.framework.BuildConfig;
import com.mobile.framework.R;
import com.mobile.newFramework.utils.output.Print;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.util.NetworkFailure;

/**
 * NewRelic class.<br>
 * Current version: 5.0.3
 *
 * @author sergiopereira
 */
public class NewRelicTracker extends AbcBaseTracker {

    private static final String TAG = NewRelicTracker.class.getSimpleName();

    private static NewRelicTracker sInstance;

    public static NewRelicTracker get() {
        return sInstance == null ? sInstance = new NewRelicTracker() : sInstance;
    }

    /**
     * New Relic initialization method
     *
     * @author sergiopereira
     */
    public static void init(Context context) {
        // Validate context
        if (context != null) {
            Print.i(TAG, "ON INIT NEW RELIC");

            //NewRelic.withApplicationToken(context.getString(R.string.newrelic_token)).start(context);
        }
    }

    /*
     * ######### BASE TRACKER #########
     */

    @Override
    public String getId(@NonNull Context context) {
        return context.getString(R.string.newrelic_token);
    }

    @Override
    public void debugMode(@NonNull Context context, boolean enable) {
        if (enable) {
            Print.w(TAG, "WARNING: DEBUG MODE IS ENABLE");
            NewRelic.withApplicationToken(getId(context)).withLoggingEnabled(true).start(context);
        } else {
            Print.w(TAG, "WARNING: DEBUG MODE IS DISABLE");
            NewRelic.withApplicationToken(getId(context)).withLoggingEnabled(false).start(context);
        }
    }

    /*
     * ######### TRACKER #########
     */

    /**
     * Notice a success transaction
     *
     * @author sergiopereira
     */
    @SuppressWarnings("unused")
    public static void noticeSuccessTransaction(String url, String httpMethod, int requestStatus, long startTimeMillis, long endTimeMillis, long bytesReceived) {
        Print.i(TAG, "ON SUCCESS TRANSACTION: " + url);
        NewRelic.noticeHttpTransaction(!TextUtils.isEmpty(url) ? url : NOT_AVAILABLE, httpMethod, requestStatus, startTimeMillis, endTimeMillis, 0, bytesReceived);
    }

    /**
     * Notice a failure transaction
     *
     * @author ricardosoares
     */
    @SuppressWarnings("unused")
    public static void noticeFailureTransaction(String url, String httpMethod, long startTimeMillis, NetworkFailure networkFailure) {
        Print.i(TAG, "ON FAILURE TRANSACTION: " + url);
        NewRelic.noticeNetworkFailure(!TextUtils.isEmpty(url) ? url : NOT_AVAILABLE, httpMethod, startTimeMillis, System.currentTimeMillis(), networkFailure);
    }

}
