/**
 * 
 */
package com.bamilo.android.appmodule.bamiloapp.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.preferences.ShopPreferences;
import com.bamilo.android.R;

/**
 * @author nutzer2
 * @modified Manuel Silva
 */
public class DarwinComponent extends ApplicationComponent {
    
    private static final String TAG = DarwinComponent.class.getName();

    /* (non-Javadoc)
     * @see com.mobile.app.ApplicationComponent#init(android.app.Application)
     */
    @Override
    @ErrorCode.Code
    public int init(Context context) {
        return initInternal(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.app.ApplicationComponent#initInternal(android.app.Application)
     */
    @Override
    @ErrorCode.Code
    protected int initInternal(Context context) {
        /**
         * Single shop country validation.
         * ErrorCode values: ErrorCode.UNKNOWN_ERROR / ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE / ErrorCode.NO_ERROR
         */
        // Get single shop
        boolean isSingleShop = context.getResources().getBoolean(R.bool.is_single_shop_country);
        // Validate if is single shop
        if (isSingleShop) return isSingleShopCountry(context);

        /**
         * Multi shop countries validation
         */
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        boolean countriesConfigs = sharedPrefs.getBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, false);
        boolean countryConfigsAvailable = sharedPrefs.getBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        boolean isChangeShop = sharedPrefs.getBoolean(Darwin.KEY_COUNTRY_CHANGED, false);
        boolean hasLanguages = CountryPersistentConfigs.hasLanguages(sharedPrefs);
        
        Print.i(TAG, "DarwinComponent shopId :  " + shopId + " countriesConfigs : " + countriesConfigs + " isChangeShop : " + isChangeShop + " countryConfigsAvailable : " + countryConfigsAvailable);
        
        if (shopId == null && countriesConfigs && !isChangeShop) {
            Print.i(TAG, "DarwinComponent AUTO_COUNTRY_SELECTION");
            return ErrorCode.AUTO_COUNTRY_SELECTION;
        }
        
        if(!countriesConfigs && !countryConfigsAvailable) {
            Print.i(TAG, "DarwinComponent NO_COUNTRIES_CONFIGS");
            if(Darwin.initialize(context)){
                return ErrorCode.NO_COUNTRIES_CONFIGS;    
            }
            Print.i(TAG, "DarwinComponent NO_COUNTRIES_CONFIGS UNKNOWN_ERROR");
            return ErrorCode.UNKNOWN_ERROR;
        }
        
        if(!countryConfigsAvailable || !hasLanguages){
            Print.i(TAG, "DarwinComponent NO_COUNTRY_CONFIGS_AVAILABLE");
            if(Darwin.initialize(context, sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null), null)) {
                return ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE;   
            }
            Print.i(TAG, "DarwinComponent NO_COUNTRY_CONFIGS_AVAILABLE UNKNOWN_ERROR");
            return ErrorCode.UNKNOWN_ERROR;
            
        }
        Print.i(TAG, "DarwinComponent shop id is : " + shopId);
        if (Darwin.initialize(context, shopId)) {
            Print.i(TAG, "DarwinComponent NO_ERROR");
            Editor editor = sharedPrefs.edit();
            editor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, false);
            editor.apply();
            return ErrorCode.NO_ERROR;
        }
        Print.i(TAG, "DarwinComponent NO_ERROR UNKNOWN_ERROR");
        return ErrorCode.UNKNOWN_ERROR;
    }

    /**
     * Method used to validate if this is a application for a single shop country.
     * Set country data from XML, initialize the framework and set the error code for SplashScreen.
     * @param context
     * @return true or false
     * @author sergiopereira
     */
    @ErrorCode.Code
    private int isSingleShopCountry(Context context) {
        // Get data from shared preferences
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        boolean hasCountryConfigs = sharedPrefs.getBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        Print.i(TAG, "SINGLE SHOP ID: " + shopId + " HAS COUNTRY CONFIGS: " + hasCountryConfigs);
        
        // Case first time
        if (TextUtils.isEmpty(shopId) || !hasCountryConfigs) {
            Print.i(TAG, "SINGLE SHOP: IS FIRST TIME SO NO COUNTRY CONFIGS AVAILABLE");
            // Set shop from configs
            ShopPreferences.setShopFromConfigs(context);
            // Partial framework initialization 
            Darwin.initialize(context, sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null), null);
            // Set the error code for Splash screen
            return ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE;
        // Case default
        } else {
            Print.i(TAG, "SINGLE SHOP: NO_ERROR");
            // Full framework initialization 
            Darwin.initialize(context, shopId);
            // Set the error code for Splash screen
            return ErrorCode.NO_ERROR;
        }
    }
    
}
