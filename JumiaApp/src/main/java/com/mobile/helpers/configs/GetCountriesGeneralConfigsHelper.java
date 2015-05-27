/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.Darwin;
import com.mobile.framework.database.CountriesConfigsTableHelper;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.configs.GetAvailableCountries;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Get Countries Configurations helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetCountriesGeneralConfigsHelper extends SuperBaseHelper {
    
    private static String TAG = GetCountriesGeneralConfigsHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
        new GetAvailableCountries(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);

        AvailableCountries availableCountries = (AvailableCountries) baseResponse.metadata.getData();

        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        Log.i(TAG, "COUNTRIES SIZE IN MEM: " + JumiaApplication.INSTANCE.countriesAvailable.size());
        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        // Validate available countries
        if (CollectionUtils.isNotEmpty(availableCountries)) {
            JumiaApplication.INSTANCE.countriesAvailable = availableCountries;
            CountriesConfigsTableHelper.insertCountriesConfigs(availableCountries);
            Log.i(TAG, "INSERT INTO DB FROM JSON");
        } else if (CollectionUtils.isNotEmpty(JumiaApplication.INSTANCE.countriesAvailable)) {
            Log.i(TAG, "INSERT INTO DB FROM MEM");
            CountriesConfigsTableHelper.insertCountriesConfigs(JumiaApplication.INSTANCE.countriesAvailable);
        }

        SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
        mEditor.apply();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, availableCountries);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.message);

        Bundle bundle = new Bundle();

        // TODO: VALIDATE THIS METHOD ????
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        ArrayList<CountryObject> mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(CollectionUtils.isNotEmpty(mCountries)){
            SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Editor mEditor = sharedPrefs.edit();
            mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
            mEditor.apply();
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);
        }

        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.error.getErrorCode());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        mRequester.onRequestError(bundle);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_GLOBAL_CONFIGURATIONS;
    }


//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        Context context = JumiaApplication.INSTANCE.getApplicationContext();
//        bundle.putString(Constants.BUNDLE_URL_KEY, context.getString(R.string.countries_url));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
//        return bundle;
//    }
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//
//        ArrayList<CountryObject> mCountries = new ArrayList<>();
//        JSONObject metadataJsonObject = jsonObject.optJSONObject(RestConstants.JSON_METADATA_TAG);
//        JSONArray sessionJSONArray = null;
//        if (null != metadataJsonObject) {
//            sessionJSONArray = metadataJsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
//        }
//
//        // Gets the previous Countries list
//        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
//        Log.i(TAG, "COUNTRIES SIZE IN MEM: " + JumiaApplication.INSTANCE.countriesAvailable.size());
//
//        // deletes the old entries
//        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
//        Log.i(TAG, "DELETE FROM DB");
//
//        if(sessionJSONArray != null){
//            for (int i = 0; i < sessionJSONArray.length(); i++) {
//                CountryObject mCountryObject = new CountryObject();
//                try {
//                    mCountryObject.initialize(sessionJSONArray.getJSONObject(i));
//                    mCountries.add(mCountryObject);
//                } catch (JSONException e) {
//                    Log.w(TAG, "WARNING JSON EXCEPTION ON PARSE COUNTRIES", e);
//                }
//            }
//            if (CollectionUtils.isNotEmpty(mCountries)) {
//                JumiaApplication.INSTANCE.countriesAvailable = mCountries;
//                CountriesConfigsTableHelper.insertCountriesConfigs(mCountries);
//                Log.i(TAG, "INSERT INTO DB FROM JSON");
//            } else if (CollectionUtils.isNotEmpty(JumiaApplication.INSTANCE.countriesAvailable)) {
//                Log.i(TAG, "INSERT INTO DB FROM MEM");
//                CountriesConfigsTableHelper.insertCountriesConfigs(JumiaApplication.INSTANCE.countriesAvailable);
//            }
//        }
//
//        SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        Editor mEditor = sharedPrefs.edit();
//        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
//        mEditor.apply();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
//        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetCountriesGeneralConfigsHelper");
//        // Gets the previous Countries list
//        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
//        ArrayList<CountryObject> mCountries = JumiaApplication.INSTANCE.countriesAvailable;
//        if(CollectionUtils.isNotEmpty(mCountries)){
//            JumiaApplication.INSTANCE.countriesAvailable = mCountries;
//            SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//            Editor mEditor = sharedPrefs.edit();
//            mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
//            mEditor.apply();
//            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        // Gets the previous Countries list
//        return parseErrorBundle(bundle);
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
