package pt.rocket.framework.tracking;

import pt.rocket.framework.R;
import android.content.Context;
import android.util.Log;

import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.util.NetworkFailure;

/**
 * NewRelic class
 * @author sergiopereira
 *
 */
public class NewRelicTracker {
	
	private static final String TAG = NewRelicTracker.class.getSimpleName();

	/**
	 * New Relic initialization method
	 * @param context
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
	 * 
	 * @param url
	 * @param requestStatus
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @param bytesReceived
	 */
	public static void noticeSuccessTransaction(String url, int requestStatus, long startTimeMillis, long endTimeMillis, long bytesReceived){
		Log.i(TAG, "ON SUCCESS TRANSACTION");
		NewRelic.noticeHttpTransaction((url != null) ? url.toString() : "n.a.", requestStatus, startTimeMillis, endTimeMillis, 0, bytesReceived);
	}
	
	/**
	 * 
	 * @param url
	 * @param startTimeMillis
	 * @param endTimeMillis
	 */
	public static void noticeFailureTransaction(String url, long startTimeMillis, long endTimeMillis){
		Log.i(TAG, "ON FAILURE TRANSACTION");
		NewRelic.noticeNetworkFailure((url != null) ? url : "n.a.", startTimeMillis, System.currentTimeMillis(), NetworkFailure.BadServerResponse);
	}

}
