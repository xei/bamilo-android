package com.mobile.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.mobile.controllers.CountrySettingsAdapter;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.objects.configs.Language;
import com.mobile.newFramework.objects.configs.Languages;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;

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
            if (ShopSelector.isRtl() && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, countryConfigs.getCurrencySymbol() + CountryConfigs.STRING_END_PLACEHOLDER);
            }
        } else {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, countryConfigs.getCurrencySymbol() + CountryConfigs.STRING_END_PLACEHOLDER);
        }
        // Price info
        mEditor.putInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, countryConfigs.getNoDecimals());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_STEP, countryConfigs.getThousandsSep());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_STEP, countryConfigs.getDecimalsSep());

        //Save languages only if there isn't any yet saved
        if(!hasLanguages(sharedPrefs)){
            saveLanguages(mEditor,countryConfigs.getLanguages());
        }
//        saveLanguages(mEditor, countryConfigs.getLanguages());
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

    public static CountryConfigs getCountryConfigsFromPreferences(Context context){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        CountryConfigs countryConfigs = new CountryConfigs();
//        countryConfigs.setCountryName(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, ""));
        countryConfigs.setCurrencyIso(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null));
        countryConfigs.setCurrencySymbol(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, null));
        countryConfigs.setNoDecimals(sharedPrefs.getInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, -1));
        countryConfigs.setThousandsSep(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_STEP, null));
        countryConfigs.setDecimalsSep(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_STEP, null));
        countryConfigs.setGaId(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null));
        countryConfigs.setGTMId(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_GTM_ID, null));
        countryConfigs.setPhoneNumber(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null));
        countryConfigs.setCsEmail(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, null));
        countryConfigs.setIsFacebookAvailable(sharedPrefs.getBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, false));
        countryConfigs.setIsRatingEnable(sharedPrefs.getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, false));
        countryConfigs.setIsRatingLoginRequired(sharedPrefs.getBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, false));
        countryConfigs.setIsReviewEnable(sharedPrefs.getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, false));
        countryConfigs.setIsReviewLoginRequired(sharedPrefs.getBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, false));
//        countryConfigs.setLanguages(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, ""));
        return countryConfigs;
    }

    /**
     * Function used to get the shop country code.
     * @param context The application context
     */
    public static String getCountryPhoneNumber(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);
        Print.i(TAG, "SHOP COUNTRY PHONE NUMBER: " + mPhone2Call);
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

    public static void writePreferences(SharedPreferences.Editor editor, CountryObject countryObject){

        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, countryObject.getCountryIso().toLowerCase());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, countryObject.getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, countryObject.getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, countryObject.getCountryFlag());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, countryObject.getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, countryObject.isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, countryObject.isCountryIsLive());
        editor.putString(Darwin.KEY_COUNTRY_USER_AGENT_AUTH_KEY, countryObject.getUserAgentToAccessDevServers());
    }

    public static void eraseCountryPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = settings.edit();
        eraseCountryPreferences(mEditor);
        mEditor.apply();
    }

    public static void eraseCountryPreferences(SharedPreferences.Editor editor){
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_ID);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_NAME);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_URL);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_FLAG);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_ISO);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE);
        editor.remove(Darwin.KEY_COUNTRY_USER_AGENT_AUTH_KEY);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE);
        editor.remove(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME);
    }

    public static CountryObject getCountryFromPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        CountryObject countryObject = new CountryObject();
        countryObject.setCountryName(settings.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null));
        countryObject.setCountryUrl(settings.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null));
        countryObject.setCountryFlag(settings.getString(Darwin.KEY_SELECTED_COUNTRY_FLAG, null));
        countryObject.setCountryIso(settings.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, null));
        countryObject.setCountryForceHttps(settings.getBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, false));
        countryObject.setCountryIsLive(settings.getBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, false));
//        countryObject.set(settings.getString(Darwin.KEY_SELECTED_COUNTRY_ID,""));
        return countryObject;
    }

    public static CountrySettingsAdapter.CountryLanguageInformation getCountryInformation(Context context){
        CountrySettingsAdapter.CountryLanguageInformation countryLanguageInformation = new CountrySettingsAdapter.CountryLanguageInformation();
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        countryLanguageInformation.countryName = settings.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        countryLanguageInformation.countryFlag = settings.getString(Darwin.KEY_SELECTED_COUNTRY_FLAG, null);
        countryLanguageInformation.languages = getLanguages(settings);
        return countryLanguageInformation;
    }

    private static void saveLanguages(SharedPreferences.Editor mEditor, Languages languages){
        // Languages
        if(CollectionUtils.isNotEmpty(languages)) {
            Language language = languages.getSelectedLanguage();
            if (language != null) {
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, language.getLangCode());
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME, language.getLangName());
            }

            String json = new Gson().toJson(languages);
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES, json);
        }
    }

    public static void saveLanguages(Context context, Languages languages){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        saveLanguages(mEditor,languages);
        mEditor.apply();
    }

    public static Languages getLanguages(SharedPreferences settings){
        String json = settings.getString(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES, null);
        return TextUtils.isEmpty(json) ? null : new Gson().fromJson(json, Languages.class);
    }

    public static boolean hasLanguages(SharedPreferences sharedPrefs){
        return TextUtils.isNotEmpty(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES, null));
    }

    public static boolean hasLanguages(Context context){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return hasLanguages(sharedPrefs);
    }
}
