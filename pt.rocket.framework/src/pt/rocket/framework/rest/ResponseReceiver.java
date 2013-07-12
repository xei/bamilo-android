/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.rest;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.utils.ErrorMonitoring;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Ralph Holland-Moritz
 * 
 *         Interface used by any component when the rest client when the request if finished execute 443
 */
public abstract class ResponseReceiver<T> extends ResultReceiver {

	private static final String TAG = ResponseReceiver.class.getSimpleName();

	public static final String RESPONSE = "response";
	
	public static final String WARNING = "warning";

	private static final String JSON_SUCCESS_TAG = "success";
	private static final String JSON_MESSAGES_TAG = "messages";
	private static final String JSON_METADATA_TAG = "metadata";
	
	protected RequestEvent requestEvent;

	/**
	 * @param handler
	 */
	public ResponseReceiver(RequestEvent requestEvent) {
		super(new Handler());
		this.requestEvent = requestEvent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.ResultReceiver#onReceiveResult(int, android.os.Bundle)
	 */
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		ErrorCode errorCode = ErrorCode.byId(resultCode);
		switch (errorCode) {
		case HTTP_STATUS: // TODO
			break;
		case NO_ERROR:
			String response = resultData.getString(RESPONSE);
			try{
			for (String key : resultData.keySet()) {
						Log.i("RESPONSE"," key = "+key+" value =  "+resultData.get(key).toString()+" ");
			}
			}
			catch(NullPointerException e){
				
			}
			try {
				parseResponse(response, resultData.getString(WARNING), resultData);
			} catch (JSONException e) {
				if (Darwin.logDebugEnabled) {
					Log.d(TAG, "Could not parse JSON:\n" + response);
				}
				Log.w(TAG, e);
				RestClientSingleton.getSingleton().removeEntryFromCache(resultData.getString(IMetaData.URI));
				onError(ErrorCode.ERROR_PARSING_SERVER_DATA, null, resultData);
				ErrorMonitoring.sendException(e, resultData.getString(IMetaData.URI),
						ErrorCode.ERROR_PARSING_SERVER_DATA, "Could not parse JSON", response, false);
			}
			break;
		default:
			onError(errorCode, null, resultData);
			break;
		}
	}

	private void parseResponse(String result, String warning, Bundle resultData) throws JSONException {
		JSONObject jsonObject = new JSONObject(result);

		if ( Darwin.logDebugEnabled ) {
			Log.d(TAG, "Got JSON result (length = " + result.length() + ")\n" + jsonObject.toString(2));
		}
		if (jsonObject.getBoolean(JSON_SUCCESS_TAG)) {
			JSONObject metadataObject = jsonObject
					.optJSONObject(JSON_METADATA_TAG);
			T responseData = parseResponse(metadataObject);
			Log.d( TAG, "resultData location = " + resultData.getString(IMetaData.LOCATION));
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseResultEvent<T>(requestEvent, responseData,
							warning, resultData));
		} else {
			onRequestError(jsonObject, resultData);
		}
	}

	protected void onRequestError(JSONObject jsonObject, Bundle resultData) {
		JSONObject messagesObject = jsonObject.optJSONObject(JSON_MESSAGES_TAG);
		HashMap<String, List<String>> errors = Errors
				.createErrorMessageMap(messagesObject);
		// TODO: add request error handling
		EventManager.getSingleton().triggerResponseEvent(
				new ResponseErrorEvent(requestEvent, errors));
	}

	/**
	 * Event triggers when the rest request succeeds.
	 * 
	 * @param response
	 *            String containing the data from the request.
	 * @param warning TODO
	 * @return TODO
	 */
	public abstract T parseResponse(JSONObject metadata) throws JSONException;

	protected void onError(ErrorCode errorCode, JSONObject result, Bundle metaData) {
		EventManager.getSingleton().triggerResponseEvent(
				new ResponseErrorEvent(requestEvent, errorCode));
	}

	public static void sendResult(ResultReceiver receiver, String result, String warning, Bundle metaData) {
		metaData.putString(RESPONSE, result);
		metaData.putString(WARNING, warning);
		receiver.send(ErrorCode.NO_ERROR.id, metaData);
	}

	public static void sendError(ResultReceiver receiver, ErrorCode errorCode, Bundle metaData) {
		receiver.send(errorCode.id, metaData);
	}

	public static void sendHttpError(ResultReceiver receiver, int errorCode, Bundle metaData) {
		receiver.send(errorCode, metaData);
	}
}
