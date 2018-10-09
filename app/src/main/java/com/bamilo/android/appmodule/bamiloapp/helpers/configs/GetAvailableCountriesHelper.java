package com.bamilo.android.appmodule.bamiloapp.helpers.configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.database.CountriesConfigsTableHelper;
import com.bamilo.android.framework.service.objects.configs.AvailableCountries;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.R;

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
    protected String getEndPoint(Bundle args) {
        return BamiloApplication.INSTANCE.getApplicationContext().getString(R.string.countries_url);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest<Exception>(requestBundle, this).execute(AigApiInterface.getAvailableCountries);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        AvailableCountries availableCountries = (AvailableCountries) baseResponse.getContentData();

        // Gets the previous Countries list
        BamiloApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        // Validate available countries
        if (CollectionUtils.isNotEmpty(availableCountries)) {
            BamiloApplication.INSTANCE.countriesAvailable = availableCountries;
            CountriesConfigsTableHelper.insertCountriesConfigs(availableCountries);
        } else if (CollectionUtils.isNotEmpty(BamiloApplication.INSTANCE.countriesAvailable)) {
            CountriesConfigsTableHelper.insertCountriesConfigs(BamiloApplication.INSTANCE.countriesAvailable);
        }

        SharedPreferences sharedPrefs =  BamiloApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, true);
        mEditor.apply();


    }

    @Override
    public void postError(BaseResponse baseResponse) {
        super.postError(baseResponse);

        BamiloApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        ArrayList<CountryObject> mCountries = BamiloApplication.INSTANCE.countriesAvailable;
        if(CollectionUtils.isNotEmpty(mCountries)){
            SharedPreferences sharedPrefs =  BamiloApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
