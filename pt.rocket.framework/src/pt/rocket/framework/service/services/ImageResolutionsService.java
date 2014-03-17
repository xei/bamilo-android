/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service.services;

import java.util.EnumSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.ImageResolutionTableHelper;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.events.GetResolutionsEvent;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class ImageResolutionsService extends DarwinService {

	private static final String TAG = LogTagHelper.create(ImageResolutionsService.class);

	public ImageResolutionsService() {
		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.GET_RESOLUTIONS));
		Log.i(TAG, "CONSTRUCTOR");
	}

	/**
	 * Gets the categories from the api.
	 * 
	 * @param event
	 * 
	 * @param url
	 * @param onGetCategoriesListener
	 */
	public static void getResolutions(final GetResolutionsEvent event) {
		Log.d(TAG, "GET RESOLUTIONS");
		String action = event.value != null ? event.value : event.eventType.action;
		Log.d(TAG, "ACTION: " + action + " URI: " + Uri.parse(action));
		// Request
		RestServiceHelper.requestGet(Uri.parse(action),
				new ResponseReceiver<Object>(event) {
					@Override
					public Object parseResponse(JSONObject metadataObject) throws JSONException {
						Log.d(TAG, "RESPONSE: " + metadataObject.toString());
						// Replace resolutions
						JSONArray resolutionsArray = metadataObject.getJSONArray(RestConstants.JSON_DATA_TAG);
						ImageResolutionTableHelper.replaceAllImageResolutions(resolutionsArray);
						return null;
					}
				}, event.metaData);
	}
	



	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		Log.d(TAG, "HANDLE EVENT: " + event.getType().toString());
		if (event.getType() == EventType.GET_RESOLUTIONS) {
			getResolutions((GetResolutionsEvent) event);
		}
	}

}
