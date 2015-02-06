/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.framework.Darwin;
import com.mobile.framework.database.CountriesConfigsTableHelper;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.interfaces.IMetaData;
import com.mobile.framework.objects.CountryObject;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Get Countries Configurations helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetCountriesGeneralConfigsHelper extends BaseHelper {
    
    private static String TAG = GetCountriesGeneralConfigsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_GLOBAL_CONFIGURATIONS;

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        Context context = JumiaApplication.INSTANCE.getApplicationContext();
        bundle.putString(Constants.BUNDLE_URL_KEY, context.getString(R.string.countries_url));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE");
        
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        JSONObject metadataJsonObject = jsonObject.optJSONObject(RestConstants.JSON_METADATA_TAG);        
        JSONArray sessionJSONArray = null;
        if (null != metadataJsonObject) {
            sessionJSONArray = metadataJsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
        }
        
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        Log.i(TAG, "COUNTRIES SIZE IN MEM: " + JumiaApplication.INSTANCE.countriesAvailable.size());

        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        Log.i(TAG, "DELETE FROM DB");
        
        if(sessionJSONArray != null){
            for (int i = 0; i < sessionJSONArray.length(); i++) {
                CountryObject mCountryObject = new CountryObject();
                try {
                    mCountryObject.initialize(sessionJSONArray.getJSONObject(i));
                    mCountries.add(mCountryObject);
                } catch (JSONException e) {
                    Log.w(TAG, "WARNING JSON EXCEPTION ON PARSE COUNTRIES", e);
                }
            }
            if(mCountries != null && mCountries.size() > 0){
                JumiaApplication.INSTANCE.countriesAvailable = mCountries;
                CountriesConfigsTableHelper.insertCountriesConfigs(mCountries);
                Log.i(TAG, "INSERT INTO DB FROM JSON");
            } else if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                Log.i(TAG, "INSERT INTO DB FROM MEM");
                CountriesConfigsTableHelper.insertCountriesConfigs(JumiaApplication.INSTANCE.countriesAvailable);
            }
        }
        
        Log.i(TAG, "code1configs " + sessionJSONArray == null ? "null" : sessionJSONArray.toString());
        
        SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
        mEditor.commit();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);        
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetCountriesGeneralConfigsHelper");
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(mCountries != null && mCountries.size() > 0){
            JumiaApplication.INSTANCE.countriesAvailable = mCountries;
            SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Editor mEditor = sharedPrefs.edit();
            mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
            mEditor.commit();
            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);
        }
        
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(mCountries != null && mCountries.size() > 0){
            JumiaApplication.INSTANCE.countriesAvailable = mCountries;
            SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Editor mEditor = sharedPrefs.edit();
            mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
            mEditor.commit();
            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);
        }
        
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    
    /**
     * Show dev env only for DEV project
     * @param mCountries
     * @author manuel
     * @modified spereira
     */
    @SuppressWarnings("unused")
    private void addDevServers(ArrayList<CountryObject> mCountries) {
        try {
            Context context = JumiaApplication.INSTANCE.getApplicationContext();
            if(context.getResources().getBoolean(R.bool.is_to_show_dev_env)){
                ArrayList<CountryObject> stagingServers = new ArrayList<CountryObject>();
                for (CountryObject countryObject : mCountries) {
                    
                    // Add Integration
                    CountryObject integrationCountryObject = new CountryObject();
                    integrationCountryObject.setCountryName(URLDecoder.decode(countryObject.getCountryName(), "utf-8")+" Integration");
                    integrationCountryObject.setCountryUrl(countryObject.getCountryUrl().replace("alice-staging", "integration-www"));
                    integrationCountryObject.setCountryFlag(countryObject.getCountryFlag());
//                    integrationCountryObject.setCountryMapMdpi(countryObject.getCountryMapMdpi());
//                    integrationCountryObject.setCountryMapHdpi(countryObject.getCountryMapHdpi());
//                    integrationCountryObject.setCountryMapXhdpi(countryObject.getCountryMapXhdpi());
                    integrationCountryObject.setCountryIso(countryObject.getCountryIso());
                    integrationCountryObject.setCountryForceHttps(countryObject.isCountryForceHttps());
                    integrationCountryObject.setCountryIsLive(countryObject.isCountryIsLive());
                    stagingServers.add(integrationCountryObject);
                    
                    // Add Live
                    CountryObject liveCountryObject = new CountryObject();
                    liveCountryObject.setCountryName(URLDecoder.decode(countryObject.getCountryName(), "utf-8")+" Live");
                    liveCountryObject.setCountryUrl(countryObject.getCountryUrl().replace("alice-staging", "www"));
                    liveCountryObject.setCountryFlag(countryObject.getCountryFlag());
//                    liveCountryObject.setCountryMapMdpi(countryObject.getCountryMapMdpi());
//                    liveCountryObject.setCountryMapHdpi(countryObject.getCountryMapHdpi());
//                    liveCountryObject.setCountryMapXhdpi(countryObject.getCountryMapXhdpi());
                    liveCountryObject.setCountryIso(countryObject.getCountryIso());
                    liveCountryObject.setCountryForceHttps(countryObject.isCountryForceHttps());
                    liveCountryObject.setCountryIsLive(countryObject.isCountryIsLive());
                    stagingServers.add(liveCountryObject);
                    
                }
                
                CountryObject liveCountryObject = new CountryObject();
                liveCountryObject.setCountryName("Bangladesh Live");
                liveCountryObject.setCountryUrl("www.daraz.com.bd");
                liveCountryObject.setCountryFlag("");
                liveCountryObject.setCountryIso("BDT");
                liveCountryObject.setCountryForceHttps(false);
                liveCountryObject.setCountryIsLive(true);
                stagingServers.add(liveCountryObject);
                
                CountryObject liveCountryObject3 = new CountryObject();
                liveCountryObject3.setCountryName("Bangladesh Staging");
                liveCountryObject3.setCountryUrl("alice-staging.daraz.com.bd");
                liveCountryObject3.setCountryFlag("");
                liveCountryObject3.setCountryIso("BDT");
                liveCountryObject3.setCountryForceHttps(false);
                liveCountryObject3.setCountryIsLive(true);
                stagingServers.add(liveCountryObject3);
                
                CountryObject liveCountryObject7 = new CountryObject();
                liveCountryObject7.setCountryName("Bangladesh Integration");
                liveCountryObject7.setCountryUrl("integration-www.daraz.com.bd");
                liveCountryObject7.setCountryFlag("");
                liveCountryObject7.setCountryIso("BDT");
                liveCountryObject7.setCountryForceHttps(false);
                liveCountryObject7.setCountryIsLive(true);
                stagingServers.add(liveCountryObject7);
                
//              CountryObject liveCountryObject1 = new CountryObject();
//              liveCountryObject1.setCountryName("Daraz PK Live");
//              liveCountryObject1.setCountryUrl("www.daraz.pk");
//              liveCountryObject1.setCountryFlag("");
//              liveCountryObject1.setCountryIso("PKT");
//              liveCountryObject1.setCountryForceHttps(false);
//              liveCountryObject1.setCountryIsLive(true);
//              stagingServers.add(liveCountryObject1);
                
//                CountryObject liveCountryObject4 = new CountryObject();
//                liveCountryObject4.setCountryName("Daraz PK Staging");
//                liveCountryObject4.setCountryUrl("alice-staging.daraz.pk");
//                liveCountryObject4.setCountryFlag("");
//                liveCountryObject4.setCountryIso("PKT");
//                liveCountryObject4.setCountryForceHttps(false);
//                liveCountryObject4.setCountryIsLive(true);
//                stagingServers.add(liveCountryObject4);
                
                CountryObject liveCountryObject2 = new CountryObject();
                liveCountryObject2.setCountryName("Shop.com.mm Live");
                liveCountryObject2.setCountryUrl("www.shop.com.mm");
                liveCountryObject2.setCountryFlag("");
                liveCountryObject2.setCountryIso("MMK");
                liveCountryObject2.setCountryForceHttps(false);
                liveCountryObject2.setCountryIsLive(true);
                stagingServers.add(liveCountryObject2);
                
                CountryObject liveCountryObject5 = new CountryObject();
                liveCountryObject5.setCountryName("Shop.com.mm Staging");
                liveCountryObject5.setCountryUrl("alice-staging.shop.com.mm");
                liveCountryObject5.setCountryFlag("");
                liveCountryObject5.setCountryIso("MMK");
                liveCountryObject5.setCountryForceHttps(false);
                liveCountryObject5.setCountryIsLive(true);
                stagingServers.add(liveCountryObject5);
                
                CountryObject liveCountryObject6 = new CountryObject();
                liveCountryObject6.setCountryName("Shop.com.mm Integration");
                liveCountryObject6.setCountryUrl("integration-www.shop.com.mm");
                liveCountryObject6.setCountryFlag("");
                liveCountryObject6.setCountryIso("MMK");
                liveCountryObject6.setCountryForceHttps(false);
                liveCountryObject6.setCountryIsLive(true);
                stagingServers.add(liveCountryObject6);
                
                CountryObject liveCountryObject8 = new CountryObject();
                liveCountryObject8.setCountryName("Bamilo Live");
                liveCountryObject8.setCountryUrl("www.bamilo.com");
                liveCountryObject8.setCountryFlag("");
                liveCountryObject8.setCountryIso("IRR");
                liveCountryObject8.setCountryForceHttps(false);
                liveCountryObject8.setCountryIsLive(true);
                stagingServers.add(liveCountryObject8);
                
                mCountries.addAll(stagingServers);
            }
        } catch (Exception e) {
            Log.w(TAG, "WARNING: ON ADD DEV SERVERS" ,e);
        }
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
