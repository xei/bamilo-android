/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import android.content.Context;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsSharedPrefs;
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
 * Get Countries Configurations helper 
 * metadata: { 
 *      currency_iso: "XOF", 
 *      currency_symbol: "FCFA",
 *      no_decimals: 0, 
 *      thousands_sep: ",", 
 *      decimals_sep: ".", 
 *      languages: [ 
 *          { 
 *              lang_code: "fr_CI",
 *              lang_name: "Français", 
 *              lang_default: true 
 *             } 
 *       ], 
 *       ga_id: "UA-41452964-1", 
 *       phone_number: "20006161",
 *       cs_email: "" 
 * }
 * 
 * @author Manuel Silva
 * 
 */
public class GetCountryConfigsHelper extends BaseHelper {

    private static String TAG = GetCountryConfigsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_COUNTRY_CONFIGURATIONS;
    
    //private final String POSITION_RIGHT = "2";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_COUNTRY_CONFIGURATIONS.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
       try{
           CountryConfigs countryConfigs = new CountryConfigs(jsonObject);
           countryConfigs.writePreferences(JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE));
        } catch (JSONException e) {
            e.printStackTrace();
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetCountriesConfigsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
