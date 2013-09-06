/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */

package pt.rocket.framework.service.services;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import android.content.Context;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Service that manages the Teaser objects.
 * 
 * @author GuilhermeSilva
 * 
 */
public class TeasersService extends DarwinService {

	private static final String TAG = TeasersService.class.getSimpleName();

	// private ArrayList<TeaserSpecification<?>> teaserSpecifications;

	public TeasersService() {
		super(EnumSet.noneOf(EventType.class), EnumSet
				.of(EventType.GET_TEASERS_EVENT));
	}

	/**
	 * Gets the teasers form the api.
	 * 
	 * @param event
	 */
	// PREVIOUS IMPLEMENTATION
	// public void getTeasers(final RequestEvent event) {
	// // if (teaserSpecifications == null) {
	// Log.d(TAG, "getTeasers");
	// RestServiceHelper.requestGet(event.eventType.action, new
	// ResponseReceiver<List<TeaserSpecification<?>>>(event) {
	//
	// @Override
	// public List<TeaserSpecification<?>> parseResponse(JSONObject response)
	// throws JSONException {
	// return updateTeaserSpecification(response);
	// }
	// }, event.metaData);
	// // } else {
	// // triggerTeasersResult(event);
	// // }
	// }

	// NEW IMPLEMENTATION
	public void getTeasers(final RequestEvent event) {
		// if (teaserSpecifications == null) {
		Log.d(TAG, "getTeasers");
		RestServiceHelper.requestGet(event.eventType.action,
				new ResponseReceiver<List<Homepage>>(event) {

					@Override
					public List<Homepage> parseResponse(JSONObject response)
							throws JSONException {
						return updateTeaserSpecification(response);
					}
				}, event.metaData);
		// } else {
		// triggerTeasersResult(event);
		// }
	}

	// PREVIOUS IMPLEMENTATION
	// private ArrayList<TeaserSpecification<?>>
	// updateTeaserSpecification(JSONObject metadataObject) throws JSONException
	// {
	// JSONArray dataArray = metadataObject.getJSONArray(JSON_DATA_TAG);
	// int dataArrayLenght = dataArray.length();
	// ArrayList<TeaserSpecification<?>> teaserSpecifications = new
	// ArrayList<TeaserSpecification<?>>();
	// for (int i = 0; i < dataArrayLenght; ++i) {
	// teaserSpecifications.add(TeaserSpecification.parse(dataArray.getJSONObject(i)));
	// }
	// return teaserSpecifications;
	// }

	// NEW IMPLEMENTATION
	private ArrayList<Homepage> updateTeaserSpecification(
			JSONObject metadataObject) throws JSONException {
		JSONArray dataArray = metadataObject.getJSONArray(RestConstants.JSON_DATA_TAG);
		int dataArrayLenght = dataArray.length();
		ArrayList<Homepage> homepageSpecifications = new ArrayList<Homepage>();
		for (int i = 0; i < dataArrayLenght; ++i) {
			Homepage homepage = new Homepage();
			homepage.initialize(dataArray.getJSONObject(i));
			homepageSpecifications.add(homepage);
		}
		return homepageSpecifications;
	}

	// PREVIOUS IMPLEMENTATION
	// private void triggerTeasersResult(RequestEvent event,
	// ArrayList<TeaserSpecification<?>> teaserSpecifications, String warning) {
	// EventManager.getSingleton().triggerResponseEvent(
	// new ResponseResultEvent<List<TeaserSpecification<?>>>(event,
	// teaserSpecifications, warning, new Bundle()));
	// }

	// NEW IMPLEMENTATION
	private void triggerTeasersResult(RequestEvent event,
			ArrayList<Homepage> homepageSpecifications, String warning) {
		EventManager.getSingleton().triggerResponseEvent(
				new ResponseResultEvent<List<Homepage>>(event,
						homepageSpecifications, warning, new Bundle()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
	 * .event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		getTeasers(event);
	}
}
