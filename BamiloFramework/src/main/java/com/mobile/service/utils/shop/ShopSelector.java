package com.mobile.service.utils.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.mobile.framework.R;
import com.mobile.service.Darwin;
import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.rest.configs.AigRestContract;
import com.mobile.service.tracking.AdjustTracker;
import com.mobile.service.tracking.AnalyticsGoogle;
import com.mobile.service.tracking.gtm.GTMManager;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;

import java.util.Locale;

/**
 * <p>
 * Utility class that encapsulates the initialization of shop dependent
 * framework components.
 * </p>
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * </p>
 *
 * @author Ralph Holland-Moritz
 */
public final class ShopSelector {

    private final static String TAG = ShopSelector.class.getSimpleName();

    private static String sShopId = null;

    private static String sCountryName;

    private static String sShopName;

    private static boolean isRtlShop;

    private static String countryCode;

    private static boolean isLayoutRtl;

    private static boolean isSingleShopCountry;

    /**
     * Hidden default constructor for utility class.
     */
    private ShopSelector() {
        // empty
    }

    /**
     * Initializing the country selector to a certain country code. This also
     * initializes the currency formatter to the related currency code.
     */
    public static void init(Context context, String shopId) {
        // Init
        SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Set locale
        setLocale(context, sharedPrefs);
        sShopId = shopId;
        sShopName = context.getResources().getString(R.string.global_server_shop_name);
        sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        // Rest client
        AigRestContract.init(context, shopId);
        AigHttpClient.getInstance(context);
        // Currency formatter
        String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
        CurrencyFormatter.initialize(context, currencyCode);
        // RTL flags
        initShopDynamics(context.getResources());
        // Trackers
        AdjustTracker.startup(context);
        AdjustTracker.initializeAdjust(context);
        AnalyticsGoogle.startup(context);
        GTMManager.init(context);
    }

    /**
     * Initializing To General Requests
     */
    // FROM GET AVAILABLE COUNTRIES
    public static void init(Context context) {
        // Rest client
        AigRestContract.init(context);
        AigHttpClient.getInstance(context);
        // RTL flags
        initShopDynamics(context.getResources());
    }

    /**
     * Initializing the country selector to a certain Country host.
     */
    // NO_COUNTRY_CONFIGS_AVAILABLE
    public static void init(Context context, String requestHost, String basePath) {
        // Set locale
        setLocale(context, context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE));
        // Rest client
        AigRestContract.init(context, requestHost, basePath);
        AigHttpClient.getInstance(context);
        // RTL flags
        initShopDynamics(context.getResources());
    }

    /**
     * Update the country selector to a certain country code. This also
     * updates the currency formatter to the related currency code.
     */
    public static void updateLocale(Context context, String shopId) {
        //Print.i(TAG, "UPDATE LOCALE");
        SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        setLocale(context, sharedPrefs);
        sShopId = shopId;
        sShopName = context.getResources().getString(R.string.global_server_shop_name);
        sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        // Rest client
        AigRestContract.init(context, shopId);
        AigHttpClient.getInstance(context);
        // Currency formatter
        String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
        CurrencyFormatter.initialize(context, currencyCode);
        // RTL flags
        initShopDynamics(context.getResources());
    }

    /**
     * These method forces the saved locale used before the rotation.
     */
    public static void setLocaleOnOrientationChanged(Context context) {
        //Print.i(TAG, "SET LOCALE ON ORIENTATION CHANGED");
        setLocale(context, context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE));
    }

    /**
     * Sets the locale for the app by using the language code.
     */
    private static void setLocale(Context context, SharedPreferences preferences) {
        String language = preferences.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, null);
        if (TextUtils.isNotEmpty(language)) {
            Print.i(TAG, "ON SET LOCALE: language " + language);
            // Get language and country code
            countryCode = language;
            String[] languageCountry = language.split("_");
            // Create new locale
            Locale locale = languageCountry.length >= 2 ? new Locale(languageCountry[0], languageCountry[1]) : new Locale(language);
            // Set as default
            Locale.setDefault(locale);
            // Create and update configuration
            Configuration config = new Configuration();
            config.locale = locale;
            Resources res = context.getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
            Print.i(TAG, "> SET LOCALE " + res.getConfiguration().toString() + " " + Locale.getDefault().toString());
        }
    }

    private static void initShopDynamics(Resources resources) {
        isRtlShop = resources.getBoolean(R.bool.is_bamilo_specific);
        isLayoutRtl = resources.getBoolean(R.bool.is_layout_rtl);
        isSingleShopCountry = resources.getBoolean(R.bool.is_single_shop_country);
    }

    public static boolean isRtl() {
        return isLayoutRtl;
    }

    public static boolean isRtlShop() {
        return isRtlShop;
    }

    public static String getShopId() {
        return sShopId;
    }

    public static String getShopName() {
        return sShopName;
    }

    public static String getCountryName() {
        return sCountryName;
    }

    public static String getCountryCode() {
        return countryCode;
    }

    public static String getCountryCodeIso() {
        return Locale.getDefault().getCountry().toUpperCase();
    }

    public static String getCountryLanguageCode() {
        return Locale.getDefault().getLanguage().toUpperCase();
    }

    public static boolean isSingleShopCountry() {
        return isSingleShopCountry;
    }
}
