package pt.rocket.framework.rest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.utils.ErrorMonitoring;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Ralph Holland-Moritz
 * 
 */
public class RestService extends Service {

	public static final String EXTRA_RESULT_RECEIVER = "resultReceiver";
	static final String EXTRA_REST_URL = "restUrl";
	static final String EXTRA_CONTENT_VALUE = "contentValues";
	static final String EXTRA_REST_METHOD = "method";

	private static final String TAG = RestService.class.getSimpleName();
	
	private ConnectivityManager connManager;

	private ThreadPoolExecutor tpe;

	@Override
	public void onCreate() {
		super.onCreate();
		tpe = new TestThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
 	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
 	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
		if (resultReceiver == null) {
			Log.e(TAG, "ResultReceiver cannot be null");
			return Service.START_NOT_STICKY;
		}

		// dont start a new request if there is already a request for the same
		// uri running!
		Uri uri = intent.getParcelableExtra(EXTRA_REST_URL);
		Log.d(TAG, "Got uri: " + uri);
		if (uri == null) {
			Log.e(TAG, "Uri cannot be null");
			resultReceiver.send(ErrorCode.INTERNAL_ERROR.id, Bundle.EMPTY);
			ErrorMonitoring.sendException(null, "null", ErrorCode.INTERNAL_ERROR, 
					"onStartCommand: Uri cannot be null", null, false);
			return Service.START_NOT_STICKY;
		}
		
		if (uri.getScheme() == null) {
			Log.e(TAG, "Scheme has to be specified for " + uri + " Skipping request");
			resultReceiver.send(ErrorCode.INTERNAL_ERROR.id, Bundle.EMPTY);
			return Service.START_NOT_STICKY;
		}
		
		uri = completeUri(uri);
		int method = intent.getIntExtra(EXTRA_REST_METHOD, RestContract.METHOD_GET);

		// convert ContentValues to HashMap
		ContentValues formData = intent.getParcelableExtra(EXTRA_CONTENT_VALUE);
		
		Log.d(TAG, "Adding a new request to the working queue: " + uri);
		// creating a new Thread for networkoperations
		tpe.execute(new RestMethodWorkerThread(uri, formData, method, resultReceiver, intent.getExtras()));
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	
	public static Uri completeUri( Uri uri ) {
		if ( Darwin.logDebugEnabled) {
			Log.d( TAG, "completeUri: uri = " + uri);
		}
		Builder builder = uri.buildUpon();
		
		if (uri.getAuthority() == null) {
			builder.authority(RestContract.REQUEST_HOST).path(RestContract.REST_BASE_PATH + uri.getPath());
			Log.w(TAG, "Url " + uri + " should include authority, authority and base path added");
		}
		uri = builder.build();
		if ( Darwin.logDebugEnabled) {
			Log.d(TAG, "Rebuilded uri: " + uri);
		}
		return uri;
	}

	public class TestThreadPoolExecutor extends ThreadPoolExecutor {
		Set<Runnable> executions = Collections.synchronizedSet(new HashSet<Runnable>());

		public TestThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		public synchronized void execute(Runnable command) {
			if (executions.contains(command)) {
				Log.w(TAG, "Duplicate requests for: " + command);
				return;
			}
			super.execute(command);
			executions.add(command);
		}

		protected synchronized void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			executions.remove(r);
		}
	}

}
