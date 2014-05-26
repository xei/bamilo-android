package pt.rocket.framework.utils;

import java.util.Locale;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestContract;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
	
	private static Locale sLocale;

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
	public static void init(Context context, String shopId, boolean isChangeShop) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		setLocale(
				context,
				sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null));
		RestContract.init(context, shopId);
		RestClientSingleton.init(context);

		String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
//		Log.i(TAG, "code1 currency code is :_ "+currencyCode);
		CurrencyFormatter.initialize(context, currencyCode);
		AnalyticsGoogle.startup(context, shopId);
		MixpanelTracker.startup(context, shopId);
		MixpanelTracker.launch(context);
		AdXTracker.startup(context);
		
		sShopId = shopId;
		sShopName = context.getResources().getString( R.string.global_server_shop_name);
		sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
	}
	
	/**
	 * Initializing To General Requests
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void init(Context context) {
		
		RestContract.init(context);
		RestClientSingleton.init(context);

	}

	/**
	 * Initializing the country selector to a certain Country host.
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void init(Context context, String requestHost, String basePath) {
		
		RestContract.init(context, requestHost, basePath);
		RestClientSingleton.init(context);

	}
	
	/**
	 * Update the country selector to a certain country code. This also
	 * updates the currency formatter to the related currency code.
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void update(Context context, String shopId) {
		SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		setLocale(
				context,
				sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null));
		RestContract.init(context, shopId);
		RestClientSingleton.init(context);

		String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
		CurrencyFormatter.initialize(context, currencyCode);
		
		sShopId = shopId;
		sShopName = context.getResources().getString( R.string.global_server_shop_name);
		sCountryName = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
	}
	
	/**
	 * Sets the locale for the app by using the language code.
	 * 
	 * @param context
	 * @param language
	 */
	private static void setLocale(Context context, String language) {
		Resources res = context.getResources();
		String[] languageCountry = language.split("_");
		sLocale = languageCountry.length >= 2 ? new Locale(
				languageCountry[0], languageCountry[1]) : new Locale(language);
		res.getConfiguration().locale = sLocale;
		res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
		Locale.setDefault(sLocale);
        Log.i(TAG, "setLocale " + res.getConfiguration().toString());
	}

	/**
	 * Hidden default constructor for utility class.
	 */
	private ShopSelector() {
		// empty
	}
	
	public static void resetConfiguration( Context context ) {
		if ( sLocale == null)
			return;
		
		Resources res = context.getResources();
        Log.i(TAG, "resetConfiguration: old config = " + res.getConfiguration().toString());
		res.getConfiguration().locale = sLocale;
		res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
		Locale.setDefault(sLocale);
        Log.i(TAG, "resetConfiguration: new config = " + res.getConfiguration().toString());
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
