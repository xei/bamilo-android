package com.mobile.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.mobile.framework.Darwin;
import com.mobile.framework.utils.Constants;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Created by spereira on 5/27/15.
 */
public class CountryPersistentConfigs {

    private static final String TAG = CountryPersistentConfigs.class.getSimpleName();

    /**
     * Write object variables to preferences
     *
     * @param context The application context
     * @author ricardosoares
     */
    public static void writePreferences(Context context, CountryConfigs countryConfigs) {
        // Get shared prefs
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        // Currency info
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, countryConfigs.getCurrencyIso());
        if (!TextUtils.isEmpty(countryConfigs.getCurrencyPosition()) && countryConfigs.getCurrencyPosition().equals(CountryConfigs.CURRENCY_LEFT_POSITION)) {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, CountryConfigs.STRING_START_PLACEHOLDER + countryConfigs.getCurrencySymbol());
            // #RTL
            if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, countryConfigs.getCurrencySymbol() + CountryConfigs.STRING_END_PLACEHOLDER);
            }
        } else {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, countryConfigs.getCurrencySymbol() + CountryConfigs.STRING_END_PLACEHOLDER);
        }
        // Price info
        mEditor.putInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, countryConfigs.getNoDecimals());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_SEP, countryConfigs.getThousandsSep());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_SEP, countryConfigs.getDecimalsSep());
        // Languages
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, countryConfigs.getLangCode());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME, countryConfigs.getLangName());
        // GA
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, countryConfigs.getGaId());
        // GTM
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_GTM_ID, countryConfigs.getGTMId());
        // Phone number
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, countryConfigs.getPhoneNumber());
        // Email
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, countryConfigs.getCsEmail());
        // Facebook
        mEditor.putBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, countryConfigs.isFacebookAvailable());
        // Rating
        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, countryConfigs.isRatingEnable());
        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, countryConfigs.isRatingLoginRequired());
        // Review
        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, countryConfigs.isReviewEnable());
        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, countryConfigs.isReviewLoginRequired());
        // Flag
        mEditor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, true);
        mEditor.apply();
    }

    /**
     * Function used to get the shop country code.
     * @param context The application context
     */
    public static String getCountryPhoneNumber(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);
        Log.i(TAG, "SHOP COUNTRY PHONE NUMBER: " + mPhone2Call);
        return mPhone2Call;
    }

    /**
     * Get the value for Facebook.
     * @param context The application context
     * @return true or false
     */
    public static boolean checkCountryRequirements(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.contains(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE);
    }
}
