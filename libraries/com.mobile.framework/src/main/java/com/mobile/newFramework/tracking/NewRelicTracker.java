package com.mobile.newFramework.tracking;

import android.content.Context;
import android.text.TextUtils;

import com.mobile.framework.R;
import com.mobile.newFramework.utils.output.Print;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.util.NetworkFailure;

/**
 * NewRelic class.<br>
 * Current version: 5.0.3
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
			Print.i(TAG, "ON INIT NEW RELIC");
	        NewRelic.withApplicationToken(context.getString(R.string.newrelic_token)).withLoggingEnabled(context.getResources().getBoolean(R.bool.ga_debug_mode));
		} else {
			Print.w(TAG, "WARNING NPE ON INIT NEW RELIC");
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
	public static void noticeSuccessTransaction(String url, String httpMethod, int requestStatus, long startTimeMillis, long endTimeMillis, long bytesReceived){
		Print.i(TAG, "ON SUCCESS TRANSACTION: " + url);
		NewRelic.noticeHttpTransaction(!TextUtils.isEmpty(url) ? url : "n.a.", httpMethod, requestStatus, startTimeMillis, endTimeMillis, 0, bytesReceived);
	}

	/**
	 * Notice a failure transaction
	 * @param url
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @author sergiopereira
	 */
	public static void noticeFailureTransaction(String url, String httpMethod, long startTimeMillis, long endTimeMillis){
		noticeFailureTransaction(url, httpMethod, startTimeMillis, endTimeMillis, NetworkFailure.BadServerResponse);
	}

	/**
	 * Notice a failure transaction
	 * @param url
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @param networkFailure
	 * @author ricardosoares
	 */
	public static void noticeFailureTransaction(String url, String httpMethod, long startTimeMillis, long endTimeMillis, NetworkFailure networkFailure){
		Print.i(TAG, "ON FAILURE TRANSACTION: " + url);
		NewRelic.noticeNetworkFailure(!TextUtils.isEmpty(url) ? url : "n.a.", httpMethod, startTimeMillis, System.currentTimeMillis(), networkFailure);
	}

}
