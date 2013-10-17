/**
 * @author Michael Kroez
 * 
 * 
 * 2013/05/14
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service.services;

import java.util.EnumSet;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.net.Uri;

/**
 * Service responsible for requesting the server api version.
 * 
 * @author Michael Kroez
 */
public class ApiService extends DarwinService {

	private static final String TAG = LogTagHelper.create(ApiService.class);

	private VersionInfo mVersionInfo;

	public ApiService() {
		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.INIT_SHOP,
				EventType.GET_API_INFO));
		mVersionInfo = new VersionInfo();
	}
	
	@Override
	public void onInit(Context context) {
		super.onInit(context);
	}

	/**
	 * Gets the categories from the api.
	 * 
	 * @param event
	 * 
	 * @param url
	 * @param onGetCategoriesListener
	 */
	private void getVersionFromApi(final RequestEvent event) {
		/*
		 * Version mVersion = new Version(10068, 10070); mVersionInfo.addEntry(
		 * Darwin.getContext().getPackageName(), mVersion );
		 * EventManager.getSingleton().triggerResponseEvent(new
		 * ResponseResultEvent<VersionInfo>(event, mVersionInfo, ""));
		 */
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<VersionInfo>(event) {

					@Override
					public VersionInfo parseResponse(JSONObject metadataObject)
							throws JSONException {

						VersionInfo info = new VersionInfo();
						info.initialize(metadataObject);
						mVersionInfo = info;
						return mVersionInfo;
					}
				}, event.metaData);
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
		if (event.getType() == EventType.GET_API_INFO) {
			getVersionFromApi(event);
		} else if(event.getType() == EventType.INIT_SHOP) {
			mVersionInfo = new VersionInfo();
		}
	}

	public VersionInfo getVersionInfo() {
		return mVersionInfo;
	}

}
