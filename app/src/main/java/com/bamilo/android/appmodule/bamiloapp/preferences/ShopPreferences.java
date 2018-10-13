package com.bamilo.android.appmodule.bamiloapp.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.database.BrandsTableHelper;
import com.bamilo.android.framework.service.database.CountriesConfigsTableHelper;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Class used to save the shared preferences for a shop
 * @author sergiopereira
 */
public class ShopPreferences {
    
    private static final String TAG = ShopPreferences.class.getSimpleName();
    
    public static final String SHOP_NOT_SELECTED = null;
    
    /**
     * Function used to get the shop id
     * 
     * @author sergiopereira
     */
    public static String getShopId(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, SHOP_NOT_SELECTED);
    }

    /**
     * Function used to get the shop name
     *
     * @author sergiopereira
     */
    public static String getShopName(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
    }

    /**
     * Function used to get the shop country currency code
     *
     * @author sergiopereira
     */
    public static String getShopCountryCurrencyIso(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
    }

    /**
     * Function used to get the shop country code
     */
    public static String getShopCountryISO(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, SHOP_NOT_SELECTED);
    }
    
    /**
     * Method used to set the shop with the position
     * @author sergiopereira
     */
    public static void setShopId(Context context, int shopPosition) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, true);
        /**
         * Save the Selected Country Configs 
         * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
         */
        CountryObject countryObject = BamiloApplication.INSTANCE.countriesAvailable.get(shopPosition);
        //Print.i(TAG, "code1DarwinComponent : selected : " + countryObject.getCountryName());
        editor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        CountryPersistentConfigs.save(editor, countryObject);
        editor.apply();

        // Clean other
        BamiloApplication.INSTANCE.cleanAllPreviousCountryValues();
        LastViewedTableHelper.deleteAllLastViewed();
        BrandsTableHelper.clearBrands();
        
    }
    
    /**
     * Method used to set a shop country from xml
     * @author sergiopereira
     */
    public static void setShopFromConfigs(Context context) {
        // Create country
        CountryObject countryObject = new CountryObject();
        countryObject.setCountryName(context.getString(R.string.single_shop_country_name));
        countryObject.setCountryUrl(context.getString(R.string.single_shop_country_url));
        countryObject.setCountryIso(context.getString(R.string.single_shop_country_iso));
        countryObject.setCountryFlag(context.getString(R.string.single_shop_country_flag));
        countryObject.setCountryForceHttps(context.getResources().getBoolean(R.bool.single_shop_country_force_https));
        countryObject.setCountryIsLive(context.getResources().getBoolean(R.bool.single_shop_country_is_live));
        // Save in shared preferences
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, countryObject.getCountryIso().toLowerCase());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, countryObject.getCountryIso().toLowerCase());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, countryObject.getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, countryObject.getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, countryObject.getCountryFlag());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, countryObject.isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, countryObject.isCountryIsLive());
        editor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, true);
        editor.apply();
        // Delete old data
        CountriesConfigsTableHelper.deleteAllCountriesConfigs();
        // Save in database
        ArrayList<CountryObject> mCountries = new ArrayList<>();
        mCountries.add(countryObject);
        CountriesConfigsTableHelper.insertCountriesConfigs(mCountries);
    }

}