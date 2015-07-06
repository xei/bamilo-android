package com.mobile.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import com.mobile.app.JumiaApplication;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.database.CountriesConfigsTableHelper;
import com.mobile.newFramework.database.FavouriteTableHelper;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

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
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, SHOP_NOT_SELECTED);
        Print.d(TAG, "SHOP ID: " + shopId);
        return shopId;
    }

    /**
     * Function used to get the shop name
     *
     * @author sergiopereira
     */
    public static String getShopName(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String name = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        Print.i(TAG, "SHOP NAME: " + name);
        return name;
    }

    /**
     * Function used to get the shop country currency code
     *
     * @author sergiopereira
     */
    public static String getShopCountryCurrencyIso(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currency = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
        Print.i(TAG, "SHOP COUNTRY CURRENCY ISO: " + currency);
        return currency;
    }

    /**
     * Function used to get the shop country code
     */
    public static String getShopCountryISO(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopCountryISO = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, SHOP_NOT_SELECTED);
        Print.d(TAG, "SHOP COUNTRY: " + shopCountryISO);
        return shopCountryISO;
    }
    
    /**
     * Method used to set the shop with the position
     * @author sergiopereira
     */
    public static void setShopId(Context context, int shopPosition) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, true);
        /**
         * Save the Selected Country Configs 
         * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
         */
        Print.i(TAG, "code1DarwinComponent : selected : " + JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryFlag());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).isCountryIsLive());
        editor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        editor.apply();

        // Clean other
        JumiaApplication.INSTANCE.cleanAllPreviousCountryValues();
        LastViewedTableHelper.deleteAllLastViewed();
        FavouriteTableHelper.deleteAllFavourite();
        
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