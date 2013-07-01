package pt.rocket.framework.rest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * This class will help to start the RestService and to keep track if it is
 * required to send the request (e.g. the same GET is already in Progress).
 * 
 * @author Jacob Zschunke
 * 
 */
public class RestServiceHelper {

	private static Context CONTEXT;

	public static void init(Context context) {
		RestServiceHelper.CONTEXT = context;
	}

	/**
	 * 
	 * @param ctx
	 * @param intent
	 */
	public static void requestGet(String uri, ResponseReceiver resultReceiver, Bundle metaData) {
		requestGet(Uri.parse(uri), resultReceiver, metaData);
	}

	/**
	 * 
	 * @param ctx
	 * @param intent
	 */
	public static void requestGet(Uri uri, ResponseReceiver resultReceiver, Bundle metaData) {
		CONTEXT.startService(createGetIntent(uri, resultReceiver, metaData));
	}

	public static void requestPost(String uriString, ContentValues postValues,
			ResponseReceiver resultReceiver, Bundle metaData) {
		CONTEXT.startService(createPostIntent(uriString, postValues,
				resultReceiver, metaData));
	}

	public static void requestPost(Uri uri, ContentValues postValues,
			ResponseReceiver resultReceiver, Bundle metaData) {
		CONTEXT.startService(createPostIntent(uri, postValues, resultReceiver, metaData));
	}

	private static Intent createGetIntent(Uri uri,
			ResponseReceiver resultReceiver, Bundle metaData) {
		Intent intent;
		intent = new Intent(CONTEXT, RestService.class);
		intent.putExtra(RestService.EXTRA_REST_URL, uri);
		intent.putExtra(RestService.EXTRA_REST_METHOD, RestContract.METHOD_GET);
		intent.putExtra(RestService.EXTRA_RESULT_RECEIVER, resultReceiver);
		intent.putExtras( metaData );
		return intent;
	}

	private static Intent createPostIntent(String uriString,
			ContentValues postValues, ResponseReceiver resultReceiver, Bundle metaData) {
		return createPostIntent(Uri.parse(uriString), postValues, resultReceiver, metaData);
	}

	private static Intent createPostIntent(Uri uri, ContentValues postValues,
			ResponseReceiver resultReceiver, Bundle metaData) {
		Intent intent = createGetIntent(uri, resultReceiver, metaData);
		intent.putExtra(RestService.EXTRA_REST_METHOD, RestContract.METHOD_POST);
		intent.putExtra(RestService.EXTRA_CONTENT_VALUE, postValues);
		intent.putExtras( metaData );
		return intent;
	}
}
