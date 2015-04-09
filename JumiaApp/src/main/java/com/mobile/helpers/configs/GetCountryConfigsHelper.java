/**
 * @author Manuel Silva
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.interfaces.IMetaData;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.preferences.CountryConfigs;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Get Countries Configurations helper from {@link EventType#GET_COUNTRY_CONFIGURATIONS}
 *
 * @author Manuel Silva
 * @modified sergiopereira
 */
public class GetCountryConfigsHelper extends BaseHelper {

    private static final String TAG = GetCountryConfigsHelper.class.getSimpleName();

    private static final EventType TYPE = EventType.GET_COUNTRY_CONFIGURATIONS;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.i(TAG, "ON REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, TYPE.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(TYPE.name()));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE");
        try {
            CountryConfigs countryConfigs = new CountryConfigs(jsonObject);
            countryConfigs.writePreferences(JumiaApplication.INSTANCE.getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.i(TAG, "ON PARSE ERROR RESPONSE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.i(TAG, "ON ERROR RESPONSE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
