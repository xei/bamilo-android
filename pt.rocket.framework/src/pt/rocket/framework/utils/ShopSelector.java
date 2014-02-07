package pt.rocket.framework.utils;

import java.util.Locale;

import pt.rocket.framework.R;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestContract;
import android.content.Context;
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

	private static int sShopId = -1;
	
	private static String sCountryName;

	private static String sShopName;

	/**
	 * Initializing the country selector to a certain country code. This also
	 * initializes the currency formatter to the related currency code.
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void init(Context context, int shopId, boolean isChangeShop) {
		setLocale(
				context,
				context.getResources().getStringArray(R.array.language_codes)[shopId]);
		RestContract.init(context, shopId);
		RestClientSingleton.init(context);
		String currencyCode = context.getResources().getStringArray(
				R.array.currency_codes)[shopId];
//		Log.i(TAG, "code1 currency code is :_ "+currencyCode);
		CurrencyFormatter.initialize(context, currencyCode);
		AnalyticsGoogle.startup(context, shopId);
		MixpanelTracker.startup(context, shopId);
		MixpanelTracker.launch(context);
		AdXTracker.startup(context);
		
		sShopId = shopId;
		sShopName = context.getResources().getStringArray( R.array.shop_names)[sShopId];
		sCountryName = context.getResources().getStringArray( R.array.country_names)[sShopId];
	}

	/**
	 * Sets the locale for the app by using the language code.
	 * 
	 * @param context
	 * @param language
	 */
	private static void setLocale(Context context, String language) {
		Resources res = context.getResources();
		if(language.equalsIgnoreCase("ug")){
		    language = "en";
		}
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
	
	public static int getShopId() {
		return sShopId;
	}
	
	public static String getShopName() {
		if ( sShopId == -1) {
			return null;
		}
		
		return sShopName;		
	}
	
	public static String getCountryName() {
		if ( sShopId == -1) {
			return null;
		}
		
		return sCountryName;
	}

}
