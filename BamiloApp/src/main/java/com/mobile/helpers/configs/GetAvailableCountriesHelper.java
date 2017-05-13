package com.mobile.helpers.configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.Darwin;
import com.mobile.service.database.CountriesConfigsTableHelper;
import com.mobile.service.objects.configs.AvailableCountries;
import com.mobile.service.objects.configs.CountryObject;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
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

        //TODO move to observable
        // Gets the previous Countries list
        BamiloApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        Print.i(TAG, "COUNTRIES SIZE IN MEM: " + BamiloApplication.INSTANCE.countriesAvailable.size());
        // deletes the old entries
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        // Validate available countries
        if (CollectionUtils.isNotEmpty(availableCountries)) {
            BamiloApplication.INSTANCE.countriesAvailable = availableCountries;
            CountriesConfigsTableHelper.insertCountriesConfigs(availableCountries);
            Print.i(TAG, "INSERT INTO DB FROM JSON");
        } else if (CollectionUtils.isNotEmpty(BamiloApplication.INSTANCE.countriesAvailable)) {
            Print.i(TAG, "INSERT INTO DB FROM MEM");
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

        //TODO move to observable
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
