/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers.configs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.database.CountriesConfigsTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.objects.CountryObject;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

/**
 * Get Countries Configurations helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetCountriesGeneralConfigsHelper extends BaseHelper {
    
    private static String TAG = GetCountriesGeneralConfigsHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_GLOBAL_CONFIGURATIONS.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE");
        
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        JSONArray sessionJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);

//		// TODO : Validate if it is necessary (Test with version code !=)
//        if(!DarwinDatabaseHelper.getInstance().exists(CountriesConfigsTableHelper.TABLE)){
//            Log.i(TAG, "FORCE UPDATE");
//            DarwinDatabaseHelper.getInstance().forceDatabaseUpdate();
//        }
        
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
                
                if(JumiaApplication.INSTANCE.generateStagingServers){
                    ArrayList<CountryObject> stagingServers = new ArrayList<CountryObject>();
                    for (CountryObject countryObject : mCountries) {
                        CountryObject stagingCountryObject = new CountryObject();
                        try {
                            stagingCountryObject.setCountryName(URLDecoder.decode(countryObject.getCountryName(), "utf-8")+" Staging");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        stagingCountryObject.setCountryUrl(countryObject.getCountryUrl().replace("www", "alice-staging"));
                        stagingCountryObject.setCountryFlag(countryObject.getCountryFlag());
                        stagingCountryObject.setCountryMapMdpi(countryObject.getCountryMapMdpi());
                        stagingCountryObject.setCountryMapHdpi(countryObject.getCountryMapHdpi());
                        stagingCountryObject.setCountryMapXhdpi(countryObject.getCountryMapXhdpi());
                        stagingCountryObject.setCountryIso(countryObject.getCountryIso());
                        stagingCountryObject.setCountryForceHttps(countryObject.isCountryForceHttps());
                        stagingCountryObject.setCountryIsLive(countryObject.isCountryIsLive());
                        stagingServers.add(stagingCountryObject);
                    }
                    
                    // Add the MA SERVER CORE 4.6 (INTEGRATION)
                    CountryObject integrationCountryObject = new CountryObject();
                    integrationCountryObject.setCountryName("Maroc Core 4.6");
                    integrationCountryObject.setCountryUrl("integration-www.jumia.ma");
                    integrationCountryObject.setCountryFlag(stagingServers.get(0).getCountryFlag());
                    integrationCountryObject.setCountryMapMdpi(stagingServers.get(0).getCountryMapMdpi());
                    integrationCountryObject.setCountryMapHdpi(stagingServers.get(0).getCountryMapHdpi());
                    integrationCountryObject.setCountryMapXhdpi(stagingServers.get(0).getCountryMapXhdpi());
                    integrationCountryObject.setCountryIso(stagingServers.get(0).getCountryIso());
                    integrationCountryObject.setCountryForceHttps(stagingServers.get(0).isCountryForceHttps());
                    integrationCountryObject.setCountryIsLive(stagingServers.get(0).isCountryIsLive());
                    stagingServers.add(integrationCountryObject);
                    
                    mCountries.addAll(stagingServers);
                    
                }
                JumiaApplication.INSTANCE.countriesAvailable = mCountries;
                CountriesConfigsTableHelper.insertCountriesConfigs(mCountries);
                Log.i(TAG, "INSERT INTO DB FROM JSON");
            } else if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                Log.i(TAG, "INSERT INTO DB FROM MEM");
                CountriesConfigsTableHelper.insertCountriesConfigs(JumiaApplication.INSTANCE.countriesAvailable);
            }
        }
        
        Log.i(TAG, "code1configs "+ sessionJSONArray.toString());
        
        SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
        mEditor.commit();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, mCountries);

        return bundle;
    }

 
 
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetCountriesGeneralConfigsHelper");
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(mCountries != null && mCountries.size() > 0){
            
            if(JumiaApplication.INSTANCE.generateStagingServers){
                ArrayList<CountryObject> stagingServers = new ArrayList<CountryObject>();
                for (CountryObject countryObject : mCountries) {
                    CountryObject stagingCountryObject = new CountryObject();
                    try {
                        stagingCountryObject.setCountryName(URLDecoder.decode(countryObject.getCountryName(), "utf-8")+" Staging");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    stagingCountryObject.setCountryUrl(countryObject.getCountryUrl().replace("www", "alice-staging"));
                    stagingCountryObject.setCountryFlag(countryObject.getCountryFlag());
                    stagingCountryObject.setCountryMapMdpi(countryObject.getCountryMapMdpi());
                    stagingCountryObject.setCountryMapHdpi(countryObject.getCountryMapHdpi());
                    stagingCountryObject.setCountryMapXhdpi(countryObject.getCountryMapXhdpi());
                    stagingCountryObject.setCountryIso(countryObject.getCountryIso());
                    stagingCountryObject.setCountryForceHttps(countryObject.isCountryForceHttps());
                    stagingCountryObject.setCountryIsLive(countryObject.isCountryIsLive());
                    stagingServers.add(stagingCountryObject);
                }
                mCountries.addAll(stagingServers);
                
            }
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

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(mCountries != null && mCountries.size() > 0){
            
            if(JumiaApplication.INSTANCE.generateStagingServers){
                ArrayList<CountryObject> stagingServers = new ArrayList<CountryObject>();
                for (CountryObject countryObject : mCountries) {
                    CountryObject stagingCountryObject = new CountryObject();
                    try {
                        stagingCountryObject.setCountryName(URLDecoder.decode(countryObject.getCountryName(), "utf-8")+" Staging");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    stagingCountryObject.setCountryUrl(countryObject.getCountryUrl().replace("www", "alice-staging"));
                    stagingCountryObject.setCountryFlag(countryObject.getCountryFlag());
                    stagingCountryObject.setCountryMapMdpi(countryObject.getCountryMapMdpi());
                    stagingCountryObject.setCountryMapHdpi(countryObject.getCountryMapHdpi());
                    stagingCountryObject.setCountryMapXhdpi(countryObject.getCountryMapXhdpi());
                    stagingCountryObject.setCountryIso(countryObject.getCountryIso());
                    stagingCountryObject.setCountryForceHttps(countryObject.isCountryForceHttps());
                    stagingCountryObject.setCountryIsLive(countryObject.isCountryIsLive());
                    stagingServers.add(stagingCountryObject);
                }
                mCountries.addAll(stagingServers);
                
            }
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
}
