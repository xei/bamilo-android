/**
 *
 */
package com.mobile.helpers.teasers;

import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.home.HomePageObject;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Helper used to get the home page
 * @author sergiopereira
 */
public class GetHomeHelper extends BaseHelper {

    public static String TAG = GetHomeHelper.class.getSimpleName();

    private EventType type = EventType.GET_HOME_EVENT;

    /**
     * Create request
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.i(TAG, "ON REQUEST HOME");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(type.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        return bundle;
    }

    /**
     * Parse the response
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE BUNDLE");
        try {
            // Get home
            HomePageObject newHomePageObject = new HomePageObject();
            newHomePageObject.initialize(jsonObject);
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, newHomePageObject);
        } catch (JSONException e) {
            Log.w(TAG, "WARNING: JE ON PARSE RESPONSE", e);
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }

    /**
     * Error on parsing json
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.i(TAG, "ON PARSE ERROR");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /**
     * Error on response
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.i(TAG, "ON ERROR RESPONSE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /**
     * Parent error on parsing json
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
