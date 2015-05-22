package com.mobile.framework.tracking;

import android.content.Context;
import android.text.TextUtils;

import com.mobile.framework.R;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.util.NetworkFailure;

import de.akquinet.android.androlog.Log;

/**
 * NewRelic class.<br>
 * Current version: 4.98
 * @author sergiopereira
 *
 */
public class NewRelicTracker {

	private static final String TAG = NewRelicTracker.class.getSimpleName();

	/**
	 * New Relic initialization method
	 * @param context
	 * @author sergiopereira
	 */
	public static void init(Context context){
		// Validate context
		if(context != null) {
			Log.i(TAG, "ON INIT NEW RELIC");
	        NewRelic.withApplicationToken(context.getString(R.string.newrelic_token)).withLoggingEnabled(true);
	        NewRelic.withApplicationToken(context.getString(R.string.newrelic_token)).start(context);
		} else {
			Log.w(TAG, "WARNING NPE ON INIT NEW RELIC");
		}
	}

	/**
	 * Notice a success transaction
	 * @param url
	 * @param requestStatus
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @param bytesReceived
	 * @author sergiopereira
	 */
	public static void noticeSuccessTransaction(String url, int requestStatus, long startTimeMillis, long endTimeMillis, long bytesReceived){
		Log.i(TAG, "ON SUCCESS TRANSACTION: " + url);
		NewRelic.noticeHttpTransaction(!TextUtils.isEmpty(url) ? url.toString() : "n.a.", requestStatus, startTimeMillis, endTimeMillis, 0, bytesReceived);
	}

	/**
	 * Notice a failure transaction
	 * @param url
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @author sergiopereira
	 */
	public static void noticeFailureTransaction(String url, long startTimeMillis, long endTimeMillis){
		Log.i(TAG, "ON FAILURE TRANSACTION: " + url);
		NewRelic.noticeNetworkFailure(!TextUtils.isEmpty(url) ? url : "n.a.", startTimeMillis, System.currentTimeMillis(), NetworkFailure.BadServerResponse);
	}

}
