package com.bamilo.android.appmodule.bamiloapp.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.controllers.ChooseLanguageController;
import com.bamilo.android.appmodule.bamiloapp.controllers.CountrySettingsAdapter;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.objects.configs.AuthInfo;
import com.bamilo.android.framework.service.objects.configs.CountryConfigs;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.objects.configs.Language;
import com.bamilo.android.framework.service.objects.configs.Languages;
import com.bamilo.android.framework.service.objects.configs.RedirectPage;
import com.bamilo.android.framework.service.objects.statics.MobileAbout;
import com.bamilo.android.framework.service.objects.statics.TargetHelper;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to manage country configs.
 * @author spereira
 */
public class CountryPersistentConfigs {

    public static String toString(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return
                "#### LANGUAGE ####"+
                        "\nCode: "              + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, null) +
                        "\nName: "              + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME, null) +
                        "\n\n#### CURRENCY ####"+
                        "\nISO: "               + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null) +
                        "\nSymbol: "            + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, null) +
                        "\nDecimals: "          + sharedPrefs.getInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, 0) +
                        "\nThousands: "         + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_STEP, null) +
                        "\nDecimals: "          + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_STEP, null) +
                        "\n\n#### TACKING ####" +
                        "\nGTM Id: "            + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_GTM_ID, null) +
                        "\nGA Id: "             + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null) +
                        "\n\n#### ALGOLIA ####" +
                        "\nEnabled: "           + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_USE, false) +
                        "\nApp Id: "            + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_APP_ID, null) +
                        "\nApi Key: "           + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_API_KEY, null) +
                        "\nPrefix: "            + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_PREFIX, null) +
                        "\n\n#### GENERAL ####" +
                        "\nPhone: "             + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null) +
                        "\nEmail: "             + sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, null) +
                        "\nFacebook: "          + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, false) +
                        "\nRating: "            + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, false) +
                        "\nRating Login: "      + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, false) +
                        "\nReview: "            + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, false) +
                        "\nReview Login: "      + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, false) +
                        "\nCart Popup: "        + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_HAS_CART_POPUP, false) +
                        "\nRich Relevance: "    + sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_HAS_RICH_RELEVANCE, false)
                ;
    }

    /**
     * Validate and save new country configurations from request.
     */
    public static void newConfigurations(@NonNull CountryConfigs configs) {
        // Case available/supported country save configs
        Context context = BamiloApplication.INSTANCE.getApplicationContext();
        // Validate country languages
        if (!CountryPersistentConfigs.hasLanguages(context)) {
            ChooseLanguageController.setLanguageBasedOnDevice(configs.getLanguages(), configs.getCurrencyIso());
        }
        // Save country configs
        CountryPersistentConfigs.write(context, configs);
    }

    /**
     * Write object variables to preferences
     *
     * @param context The application context
     * @author ricardosoares
     */
    private static void write(Context context, CountryConfigs countryConfigs) {
        // Get shared prefs
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        // Currency info
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, countryConfigs.getCurrencyIso());
        if (!TextUtils.isEmpty(countryConfigs.getCurrencyPosition()) && countryConfigs.getCurrencyPosition().equals(CountryConfigs.CURRENCY_LEFT_POSITION)) {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, CountryConfigs.STRING_START_PLACEHOLDER + countryConfigs.getCurrencySymbol());
        } else {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, countryConfigs.getCurrencySymbol() + CountryConfigs.STRING_END_PLACEHOLDER);
        }
        // Price info
        mEditor.putInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, countryConfigs.getNoDecimals());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_STEP, countryConfigs.getThousandsSep());
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_STEP, countryConfigs.getDecimalsSep());
        // Save languages only if there isn't any yet saved
        if(!hasLanguages(sharedPrefs)){
            saveLanguages(mEditor,countryConfigs.getLanguages());
        }
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
        // More info
        saveMoreInfo(mEditor, countryConfigs.getMobileAbout());
        // Algolia info for search
        saveAlgoliaInfo(mEditor, countryConfigs.getApplicationId(), countryConfigs.getSuggesterApiKey(), countryConfigs.getNamespacePrefix(), countryConfigs.isAlgoliaSearchEngine());
        // Authentication info
        saveAuthInfo(mEditor, countryConfigs.getAuthInfo());
        // Cart popup
        mEditor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_HAS_CART_POPUP, countryConfigs.hasCartPopup());
        // Rich Relevance
        mEditor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_HAS_RICH_RELEVANCE, countryConfigs.isRichRelevanceEnabled());
        // Save redirect page
        mEditor.putString(Darwin.KEY_SELECTED_REDIRECT, new Gson().toJson(countryConfigs.getRedirectPage()));
        // Write
        mEditor.apply();
    }

    /**
     * Function used to get the shop country code.
     * @param context The application context
     */
    public static String getCountryPhoneNumber(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);
    }

    /**
     * Function used to get the shop country email.
     * @param context The application context
     */
    public static String getCountryEmail(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, null);
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

    public static void save(SharedPreferences.Editor editor, CountryObject countryObject) {
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, countryObject.getCountryIso().toLowerCase());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, countryObject.getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, countryObject.getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, countryObject.getCountryFlag());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, countryObject.getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, countryObject.isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, countryObject.isCountryIsLive());
        editor.putString(Darwin.KEY_COUNTRY_USER_AGENT_AUTH_KEY, countryObject.getUserAgentToAccessDevServers());
    }

    public static void eraseCountryPreferences(@NonNull Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = settings.edit();
        eraseCountryPreferences(mEditor);
        mEditor.apply();
    }

    public static void eraseCountryPreferences(@NonNull SharedPreferences.Editor editor){
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

    public static CountrySettingsAdapter.CountryLanguageInformation getCountryInformation(@NonNull Context context){
        CountrySettingsAdapter.CountryLanguageInformation countryLanguageInformation = new CountrySettingsAdapter.CountryLanguageInformation();
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        countryLanguageInformation.countryName = settings.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        countryLanguageInformation.countryFlag = settings.getString(Darwin.KEY_SELECTED_COUNTRY_FLAG, null);
        // countryLanguageInformation.languages = getLanguages(settings);
        return countryLanguageInformation;
    }

    private static void saveLanguages(@NonNull SharedPreferences.Editor mEditor, @Nullable Languages languages){
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

    public static void saveLanguages(@NonNull Context context, Languages languages){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        saveLanguages(mEditor, languages);
        mEditor.apply();
    }

    public static void saveMoreInfo(@NonNull Context context, @Nullable List<TargetHelper> moreInfo){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        saveMoreInfo(mEditor, moreInfo);
        mEditor.apply();
    }

    public static void saveMoreInfo(@NonNull SharedPreferences.Editor mEditor, @Nullable List<TargetHelper> moreInfo){
        mEditor.putString(Darwin.KEY_SELECTED_MORE_INFO, new Gson().toJson(moreInfo));
    }

    public static void saveAlgoliaInfo(@NonNull SharedPreferences.Editor mEditor, @Nullable final String appId, final String suggesterAPIKey, final String namespacePrefix, final boolean useAlgolia){
        mEditor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_USE, useAlgolia);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_APP_ID, appId);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_API_KEY, suggesterAPIKey);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_PREFIX, namespacePrefix);
    }

    public static void saveAuthInfo(@NonNull SharedPreferences.Editor mEditor, @Nullable AuthInfo authInfo){
        mEditor.putString(Darwin.KEY_SELECTED_AUTH_INFO, new Gson().toJson(authInfo));
    }

    public static boolean isUseAlgolia(@NonNull Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_USE, false);
    }

    public static String getAlgoliaInfoByKey(@NonNull Context context, @NonNull final String key){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }

    @Nullable
    public static ArrayList<TargetHelper> getMoreInfo(@NonNull Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String json = settings.getString(Darwin.KEY_SELECTED_MORE_INFO, null);
        return TextUtils.isEmpty(json) ? null : new Gson().fromJson(json, MobileAbout.class);
    }

    @NonNull
    public static AuthInfo getAuthInfo(@NonNull Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String json = settings.getString(Darwin.KEY_SELECTED_AUTH_INFO, null);
        return TextUtils.isEmpty(json) ? new AuthInfo() : new Gson().fromJson(json, AuthInfo.class);
    }

    @Nullable
    public static Languages getLanguages(@NonNull SharedPreferences settings){
        String json = settings.getString(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES, null);
        return TextUtils.isEmpty(json) ? null : new Gson().fromJson(json, Languages.class);
    }

    public static boolean hasLanguages(@NonNull SharedPreferences sharedPrefs){
        return TextUtils.isNotEmpty(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANGUAGES, null));
    }

    private static boolean hasLanguages(@NonNull Context context){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return hasLanguages(sharedPrefs);
    }

    public static boolean hasCartPopup(@NonNull Context context){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_HAS_CART_POPUP, false);
    }

    @Nullable
    public static RedirectPage getRedirectPage(@NonNull Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String json = sharedPrefs.getString(Darwin.KEY_SELECTED_REDIRECT, null);
        return new Gson().fromJson(json, RedirectPage.class);
    }

    /**
     * Method used to set a custom host, only for debug
     * Case necessary add UA, Darwin.KEY_COUNTRY_USER_AGENT_AUTH_KEY with "NGAMZ EGAMZ CMAMZ MAAMZ"
     */
    public static void saveDebugHost(Context context, String host){
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, host);
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, host);
        editor.apply();
    }

}