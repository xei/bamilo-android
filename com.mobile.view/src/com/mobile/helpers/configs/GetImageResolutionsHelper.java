/**
 * @author Manuel Silva
 * 
 * @version 1.5
 * 
 * 2014/01/08
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.helpers.configs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.database.ImageResolutionTableHelper;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Manuel Silva
 *
 */
public class GetImageResolutionsHelper extends BaseHelper {

	private static final String TAG = LogTagHelper.create(GetImageResolutionsHelper.class);
	
    private static final EventType EVENT_TYPE = EventType.GET_RESOLUTIONS;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_RESOLUTIONS.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_RESOLUTIONS);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        JSONArray resolutionsArray = null;
        try {
            resolutionsArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            ImageResolutionTableHelper.replaceAllImageResolutions(resolutionsArray);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_RESOLUTIONS);
//        Log.i("REQUEST", "event EVENT_TYPE response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return null;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetResolutionsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_RESOLUTIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_RESOLUTIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
    
}
