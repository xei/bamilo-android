package com.mobile.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.mobile.framework.Darwin;
import com.mobile.framework.R;
import com.mobile.framework.rest.RestClientSingleton;
import com.mobile.framework.rest.RestContract;
import com.mobile.framework.tracking.Ad4PushTracker;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.FacebookTracker;
import com.mobile.framework.tracking.gtm.GTMManager;

import java.util.Locale;

import de.akquinet.android.androlog.Log;

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

	/**
	 * Initializing the country selector to a certain country code. This also
	 * initializes the currency formatter to the related currency code.
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void init(Context context, String shopId) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		setLocale(context, sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, null));
		RestContract.init(context, shopId);
		RestClientSingleton.getSingleton(context).init();

		String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
		//ADJUST
		AdjustTracker.startup(context);
		initializeAdjust(context);

		CurrencyFormatter.initialize(context, currencyCode);
		AnalyticsGoogle.startup(context);
		Ad4PushTracker.startup(context);
        GTMManager.init(context);
        FacebookTracker.startup(context);
        
        sShopId = shopId;
        sShopName = context.getResources().getString( R.string.global_server_shop_name);
        sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
    }
    
    /**
     * Initializing To General Requests
     * 
     * @param context
     */
	// NO_COUNTRIES_CONFIGS
    public static void init(Context context) {
        RestContract.init(context);
        RestClientSingleton.getSingleton(context).init();
	}

	/**
	 * initialized Adjust tracker
 	 * @param context
	 */
	private static void initializeAdjust(final Context context) {
		String appToken = context.getString(R.string.adjust_app_token);
		String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
		if (context.getResources().getBoolean(R.bool.adjust_is_production_env)) {
			environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
		}
		AdjustConfig config = new AdjustConfig(context, appToken, environment);
		config.setLogLevel(LogLevel.VERBOSE); // if not configured, INFO is used by default
		//PRE_INSTALL DEFAULT TRACKER
		if (!TextUtils.isEmpty(context.getString(R.string.adjust_default_tracker))) {
			config.setDefaultTracker(context.getString(R.string.adjust_default_tracker));
		}

		config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
			@Override
			public void onAttributionChanged(AdjustAttribution attribution) {
				AdjustTracker.saveResponseDataInfo(context, attribution.adgroup, attribution.network, attribution.campaign, attribution.creative);
			}
		});
		Adjust.onCreate(config);

	}

	/**
	 * Initializing the country selector to a certain Country host.
	 * 
	 * @param context
	 */
    // NO_COUNTRY_CONFIGS_AVAILABLE
	public static void init(Context context, String requestHost, String basePath) {
		RestContract.init(context, requestHost, basePath);
		RestClientSingleton.getSingleton(context).init();
	}

	/**
	 * Update the country selector to a certain country code. This also
	 * updates the currency formatter to the related currency code.
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void updateLocale(Context context, String shopId) {
	    //Log.i(TAG, "UPDATE LOCALE");
		SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		setLocale(context, sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, null));
		RestContract.init(context, shopId);
		RestClientSingleton.getSingleton(context).init();

		String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
		CurrencyFormatter.initialize(context, currencyCode);
		
		sShopId = shopId;
		sShopName = context.getResources().getString( R.string.global_server_shop_name);
		sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
	}
	
	/**
	 * These method forces the saved locale used before the rotation.
	 * @param context
	 * @author spereira
	 */
	public static void setLocaleOnOrientationChanged(Context context) {
	    //Log.i(TAG, "SET LOCALE ON ORIENTATION CHANGED");
	    SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	    String language = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, null);
	    if(!TextUtils.isEmpty(language)) setLocale(context, language);
	}
	
	/**
	 * Sets the locale for the app by using the language code.
	 * 
	 * @param context
	 * @param language
	 * @modified sergiopereira
	 */
	private static void setLocale(Context context, String language) {
		Log.i(TAG, "ON SET LOCALE: language " + language);
		// Get language and country code
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
        Log.i(TAG, "setLocale " + res.getConfiguration().toString() + " " + Locale.getDefault().toString());
	}

	/**
	 * Hidden default constructor for utility class.
	 */
	private ShopSelector() {
		// empty
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

}
