package pt.rocket.framework;

import java.util.Set;

import pt.rocket.framework.database.DarwinDatabaseHelper;
import pt.rocket.framework.tracking.NewRelicTracker;
import pt.rocket.framework.utils.PreInstallController;
import pt.rocket.framework.utils.ShopSelector;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import de.akquinet.android.androlog.Log;

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
 * - create the event (see pt.rocket.framewrok.event.events for example events)
 * - add it to eventTypes
 * - create the service (see pt.rocket.framework.service.services)
 * - make the service listen to the event (add the event type to the arra in the constructor)
 * - handle to that event (create function HandleEvent)
 * 
 * - in the activity, make it implement the response listener interface and handle event method
 * When you receive the response event it will be handled bythat function
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
	private static DarwinMode mode = DarwinMode.DEBUG;
	private static String SHOP_ID = null;

	public static Context context = null;
	
	public final static boolean logDebugEnabled = true;
	private static int sVersionCode;
	
	//private static final String FRAMEWORK_PREFS = "framework";
	
	//private static final String KEY_INITSUCCESSFUL = "init_successful";
	
	public final static String SHARED_PREFERENCES = "whitelabel_prefs";
	
	public final static String INSTALL_TIME_PREFERENCE = "install_time";
	
	/**
	 * Countries Configs
	 */
	public static final String KEY_COUNTRY_CHANGED= "country_changed";
	public static final String KEY_COUNTRIES_CONFIGS_LOADED = "countries_configs_loaded";
	public static final String KEY_SELECTED_COUNTRY_ID = "selected_country_id";
	public static final String KEY_SELECTED_COUNTRY_NAME = "selected_country_name";
	public static final String KEY_SELECTED_COUNTRY_URL = "selected_country_url";
	public static final String KEY_SELECTED_COUNTRY_FLAG = "selected_country_flag";
	public static final String KEY_SELECTED_COUNTRY_MAP_FLAG = "selected_country_map_flag";
	public static final String KEY_SELECTED_COUNTRY_ISO = "selected_country_iso";
	public static final String KEY_SELECTED_COUNTRY_FORCE_HTTP = "selected_country_force_http";
	public static final String KEY_SELECTED_COUNTRY_IS_LIVE = "selected_country_is_live";
	public static final String KEY_SELECTED_COUNTRY_REST_BASE = "selected_country_rest_base";
	
	public static final String KEY_SELECTED_COUNTRY_CURRENCY_ISO = "selected_country_currency_iso";
	public static final String KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL = "selected_country_currency_symbol";
	public static final String KEY_SELECTED_COUNTRY_CURRENCY_POSITION = "selected_country_currency_position";
	public static final String KEY_SELECTED_COUNTRY_NO_DECIMALS = "selected_country_no_decimals";
	public static final String KEY_SELECTED_COUNTRY_THOUSANDS_SEP = "selected_country_thousands_sep";
	public static final String KEY_SELECTED_COUNTRY_DECIMALS_SEP = "selected_country_decimals_sep";
	public static final String KEY_SELECTED_COUNTRY_LANG_CODE = "selected_country_lang_code";
	public static final String KEY_SELECTED_COUNTRY_LANG_NAME = "selected_country_lang_name";
	public static final String KEY_SELECTED_COUNTRY_GA_ID = "selected_country_ga_id";
	public static final String KEY_SELECTED_COUNTRY_GA_TEST_ID = "selected_country_ga_test_id";
	public static final String KEY_SELECTED_COUNTRY_PHONE_NUMBER = "selected_country_phone_number";
	public static final String KEY_SELECTED_COUNTRY_CS_EMAIL = "selected_country_cs_email";
	/**
	 * Prevent this class from being instantiated. Make this class into a
	 * singleton
	 */
	protected Darwin() {
	}

	/**
	 * Initializes the Darwin framework
	 * 
	 * @param mode
	 *            execution mode used to set the service and activity behavior
	 * @param context
	 *            context of the activity
	 * @param keystore
	 *            certificate of the server
	 * @param keystorePassword
	 *            password of the certificate
	 * @return return true is Darwin is initializes and false if it is already
	 *         intialized
	 */
	public static boolean initialize(DarwinMode mode, Context ctx, String shopId, boolean isChangeShop) {
		Log.d(TAG, "Initializing Darwin with id " + shopId);
		context = ctx.getApplicationContext();
		if (SHOP_ID != null && SHOP_ID.equalsIgnoreCase(shopId)) {
			Log.d(TAG, "Allready initialized for id " + shopId);
			ShopSelector.update(ctx, shopId);
			return true;
		}
		
		// Set pre install tracking
//		boolean isPreInstallApp = PreInstallController.init(context);
		PreInstallController.init(context);
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		
		retrieveVersionCode();
		ShopSelector.init(context, shopId, isChangeShop);
		
		NewRelicTracker.init(context);

		Log.d(TAG, "Darwin is initialized with id " + shopId);
		SHOP_ID = shopId;
		
//		setUAPushTags(context, isPreInstallApp);
		
		return true;
	}
	
	public static boolean initialize(DarwinMode mode, Context ctx) {
		Log.d(TAG, "Initializing Darwin to get global ");
		context = ctx.getApplicationContext();
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		
		ShopSelector.init(context);
		
		return true;
	}
	
	public static boolean initialize(DarwinMode mode, Context ctx, String requestHost, String basePath) {
		Log.d(TAG, "Initializing Darwin to get global ");
		context = ctx.getApplicationContext();
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		
		ShopSelector.init(ctx, requestHost, basePath);
		
		return true;
	}
	
	public static String getShopId(){
		return SHOP_ID;
	}
	

	/**
	 * Gets the mode on which the framework is running
	 * 
	 * @return DEBUG or RELEASE depending on the mode that was set vy the
	 *         application
	 */
	public static DarwinMode getMode() {
		return mode;
	}

	/**
	 * Checks if the class was already initialized
	 * 
	 * @return true, if the class was already initialized, otherwise false
	 */
	public synchronized static boolean isInitialized() {
		return SHOP_ID != null;
	}

	public synchronized static Context getContext() {
		return context;
	}

//	private static void setUAPushTags(Context context, boolean isPreInstallApp) {
//		Set<String> tags = new HashSet<String>();
//		tags.add(TimeZone.getDefault().getID());
//		tags.add(Locale.getDefault().getLanguage());
//		tags.add(Locale.getDefault().getCountry());
//		tags.add(Build.MANUFACTURER.replaceAll(" ", "-"));
//		tags.add(Build.MODEL.replaceAll(" ", "-"));
//		tags.add(Build.VERSION.RELEASE.replaceAll(" ", "-"));
//		tags.add(context.getString(R.string.ua_store));
//		try {
//			tags.add("app_version_"+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		// Check pre-install flag
//		if(isPreInstallApp) {
//			preInstallTag(context, tags, Build.MANUFACTURER);
//		}
//		// Set tags
//		PushManager.shared().setTags(tags);
//	}

	
    /**
     * Method that adds the pre install tag for UA
     * @param value
     * @author sergiopereira
     */
    public static void preInstallTag(Context context, Set<String> tags, String value) {
        String tag = context.getString(R.string.ua_preisntall) + value;
        Log.i(TAG, "PRE INSTALL UA TRACKING: " + tag);
        //Set<String> tags = new HashSet<String>();
        tags.add(tag);
        //PushManager.shared().setTags(tags);
    }
	
	
    private static void retrieveVersionCode() {
        PackageInfo pinfo;
        int versionCode = -1;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pinfo.versionCode;
        } catch (NameNotFoundException e) {
            // ignore
        }
        
        sVersionCode = versionCode;        
    }
    
    public static int getVersionCode() {
    	return sVersionCode;
    }
	
}
