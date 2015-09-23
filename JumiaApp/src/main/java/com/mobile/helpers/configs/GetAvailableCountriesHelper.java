package com.mobile.helpers.configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.database.CountriesConfigsTableHelper;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Get Countries Configurations helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetAvailableCountriesHelper extends SuperBaseHelper {
    
    private static String TAG = GetAvailableCountriesHelper.class.getSimpleName();

    @Override
    protected String getRequestUrl(Bundle args) {
        return JumiaApplication.INSTANCE.getApplicationContext().getString(R.string.countries_url);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest<Exception>(requestBundle, this).execute(AigApiInterface.getAvailableCountries);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        AvailableCountries availableCountries = (AvailableCountries) baseResponse.getMetadata().getData();

        //TODO move to observable
        // Gets the previous Countries list
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        Print.i(TAG, "COUNTRIES SIZE IN MEM: " + JumiaApplication.INSTANCE.countriesAvailable.size());
        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        // Validate available countries
        if (CollectionUtils.isNotEmpty(availableCountries)) {
            JumiaApplication.INSTANCE.countriesAvailable = availableCountries;
            CountriesConfigsTableHelper.insertCountriesConfigs(availableCountries);
            Print.i(TAG, "INSERT INTO DB FROM JSON");
        } else if (CollectionUtils.isNotEmpty(JumiaApplication.INSTANCE.countriesAvailable)) {
            Print.i(TAG, "INSERT INTO DB FROM MEM");
            CountriesConfigsTableHelper.insertCountriesConfigs(JumiaApplication.INSTANCE.countriesAvailable);
        }

        SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
        mEditor.apply();


    }

    @Override
    public void postError(BaseResponse baseResponse) {
        super.postError(baseResponse);

        //TODO move to observable
        JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        ArrayList<CountryObject> mCountries = JumiaApplication.INSTANCE.countriesAvailable;
        if(CollectionUtils.isNotEmpty(mCountries)){
            SharedPreferences sharedPrefs =  JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Editor mEditor = sharedPrefs.edit();
            mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
            mEditor.apply();
        }
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_GLOBAL_CONFIGURATIONS;
    }

}
