/**
 * @author Manuel Silva
 * 
 * @version 1.5
 * 
 * 2014/01/08
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.helpers;

import java.util.EnumSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.ImageResolutionTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Manuel Silva
 *
 */
public class GetImageResolutionsHelper extends BaseHelper {

	private static final String TAG = LogTagHelper.create(GetImageResolutionsHelper.class);

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_RESOLUTIONS.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_RESOLUTIONS);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
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
        
        return null;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }

}
