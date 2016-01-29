package com.mobile.newFramework;

import android.content.Context;

import com.mobile.newFramework.database.DarwinDatabaseHelper;
import com.mobile.newFramework.tracking.NewRelicTracker;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;

/**
 * This Singleton class defines the entry point for the framework. Every
 * application that uses the framework should call the initialize method before
 * using anything from the framework.
 * 
 * Darwin is an event driven Framework. 
 * Activity <-> EventManager <-> Service
 * The activities communicate to the underlying services using events
 * 
 * Two types of events
 *	- Request event -> Events sent from the activity to the service
 *	- Response Event -> Events sent from the service to the activity
 *
 * How to implement a new service:
 * 
 * - create the event (see com.mobile.framework.event.events for example events)
 * - add it to eventTypes
 * - create the service (see com.mobile.framework.service.services)
 * - make the service listen to the event (add the event type to the arra in the constructor)
 * - handle to that event (create function HandleEvent)
 * 
 * - in the activity, make it implement the response listener interface and handle event method
 * When you receive the response event it will be handled by that function
 * - implement the methods that use that event
 *
 * 
 * @author GuilhermeSilva
 * @version 1.00
 * 
 *          2012
 * 
 *          COPYRIGHT (C) Rocket Internet All Rights Reserved.
 */
public class Darwin {

	private static final String TAG = Darwin.class.getSimpleName();
	
	private static String SHOP_ID = null;

	public static Context context = null;
	
	public final static boolean logDebugEnabled = true;
	
	public final static String SHARED_PREFERENCES = "whitelabel_prefs";
	
	/**
	 * Countries Configs
	 */
	public static final String KEY_COUNTRY_CHANGED= "country_changed";
	public static final String KEY_COUNTRIES_CONFIGS_LOADED = "countries_configs_loaded";
	public static final String KEY_SELECTED_COUNTRY_ID = "selected_country_id";
	public static final String KEY_SELECTED_COUNTRY_NAME = "selected_country_name";
	public static final String KEY_SELECTED_COUNTRY_URL = "selected_country_url";
	public static final String KEY_SELECTED_COUNTRY_FLAG = "selected_country_flag";
	public static final String KEY_SELECTED_COUNTRY_ISO = "selected_country_iso";
	public static final String KEY_SELECTED_COUNTRY_FORCE_HTTP = "selected_country_force_http";
	public static final String KEY_SELECTED_COUNTRY_IS_LIVE = "selected_country_is_live";
	
	public static final String KEY_SELECTED_COUNTRY_CURRENCY_ISO = "selected_country_currency_iso";
	public static final String KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL = "selected_country_currency_symbol";
	public static final String KEY_SELECTED_COUNTRY_NO_DECIMALS = "selected_country_no_decimals";
	public static final String KEY_SELECTED_COUNTRY_THOUSANDS_STEP = "selected_country_thousands_sep";
	public static final String KEY_SELECTED_COUNTRY_DECIMALS_STEP = "selected_country_decimals_sep";
	public static final String KEY_SELECTED_COUNTRY_LANG_CODE = "selected_country_lang_code";
	public static final String KEY_SELECTED_COUNTRY_LANG_NAME = "selected_country_lang_name";
	public static final String KEY_SELECTED_COUNTRY_LANGUAGES = "selected_country_languages";
	public static final String KEY_SELECTED_COUNTRY_GA_ID = "selected_country_ga_id";
	public static final String KEY_SELECTED_COUNTRY_GTM_ID = "selected_country_gtm_id";
	public static final String KEY_SELECTED_COUNTRY_PHONE_NUMBER = "selected_country_phone_number";
	public static final String KEY_SELECTED_COUNTRY_CS_EMAIL = "selected_country_cs_email";
	public static final String KEY_SELECTED_COUNTRY_HAS_CART_POPUP = "selected_country_has_cart_popup";
	public static final String KEY_SELECTED_FACEBOOK_IS_AVAILABLE = "selected_facebook_is_available";
	public static final String KEY_SELECTED_MORE_INFO = "selected_more_info";
	public static final String KEY_SELECTED_COUNTRY_HAS_RICH_RELEVANCE = "selected_country_has_rich_relevance";
	
	public static final String KEY_SELECTED_RATING_ENABLE = "selected_rating_enable";
	public static final String KEY_SELECTED_RATING_REQUIRED_LOGIN = "selected_rating_required_login";
	public static final String KEY_SELECTED_REVIEW_ENABLE = "selected_review_enable";
	public static final String KEY_SELECTED_REVIEW_REQUIRED_LOGIN = "selected_review_required_login";

	public static final String KEY_COUNTRY_CONFIGS_AVAILABLE = "country_configs_available";

	public static final String KEY_COUNTRY_USER_AGENT_AUTH_KEY = "user_agent_authentication";
	public static final String KEY_CHANGE_PASSWORD = "change_password";

	/**
	 * Prevent this class from being instantiated. Make this class into a
	 * singleton
	 */
	protected Darwin() {
	}

	/**
	 * Initializes the Darwin framework
	 *
	 * @return return true is Darwin is initializes and false if it is already
	 *         intialized
	 */
	public static boolean initialize(Context ctx, String shopId) {
		Print.d(TAG, "Initializing Darwin with id " + shopId);
		context = ctx.getApplicationContext();
		if (SHOP_ID != null && SHOP_ID.equalsIgnoreCase(shopId)) {
			Print.d(TAG, "Already initialized for id " + shopId);
			ShopSelector.updateLocale(ctx, shopId);
			return true;
		}
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		// Shop
		ShopSelector.init(context, shopId);
		// New relic
		NewRelicTracker.init(context);
		Print.i(TAG, "Darwin is initialized with id " + shopId);
		SHOP_ID = shopId;
		return true;
	}
	
	// FOR NO_COUNTRIES_CONFIGS
	public static boolean initialize(Context ctx) {
		Print.d(TAG, "Initializing Darwin to get global ");
		context = ctx.getApplicationContext();
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		// Shop
		ShopSelector.init(context);
		return true;
	}
	
	// FOR NO_COUNTRY_CONFIGS_AVAILABLE
	public static boolean initialize(Context ctx, String requestHost, String basePath) {
		Print.d(TAG, "Initializing Darwin to get global ");
		context = ctx.getApplicationContext();
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		// Shop
		ShopSelector.init(ctx, requestHost, basePath);
		return true;
	}
	
}
