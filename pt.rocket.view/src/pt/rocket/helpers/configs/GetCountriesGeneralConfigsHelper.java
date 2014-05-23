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
        ArrayList<CountryObject> mCountries = new ArrayList<CountryObject>();
        JSONArray sessionJSONArray = jsonObject
                .optJSONArray(RestConstants.JSON_DATA_TAG);
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();

        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        
        if(sessionJSONArray != null){
            for (int i = 0; i < sessionJSONArray.length(); i++) {
                CountryObject mCountryObject = new CountryObject();
                try {
                    mCountryObject.initialize(sessionJSONArray.getJSONObject(i));
                    mCountries.add(mCountryObject);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                    mCountries.addAll(stagingServers);
                    
                }
                JumiaApplication.INSTANCE.countriesAvailable = mCountries;
                CountriesConfigsTableHelper.insertCountriesConfigs(mCountries);
            } else if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
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
     
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_GLOBAL_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}