package pt.rocket.framework;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import pt.rocket.framework.database.DarwinDatabaseHelper;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.utils.PreInstallController;
import pt.rocket.framework.utils.ShopSelector;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.urbanairship.push.PushManager;

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
	private static int SHOP_ID = -1;

	public static Context context = null;
	
	public final static boolean logDebugEnabled = true;
	private static int sVersionCode;
	
	private static final String FRAMEWORK_PREFS = "framework";
	
	private static final String KEY_INITSUCCESSFUL = "init_successful";

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
	public static boolean initialize(DarwinMode mode, Context ctx, int shopId) {
		Log.d(TAG, "Initializing Darwin with id " + shopId);
		context = ctx.getApplicationContext();
		if (SHOP_ID == shopId) {
			Log.d(TAG, "Allready initialized for id " + shopId);
			return true;
		}
		
		// Set pre install tracking
		boolean isPreInstallApp = PreInstallController.init(context);
		// Init darwin database
		DarwinDatabaseHelper.init(context);
		
		retrieveVersionCode();
		ShopSelector.init(context, shopId);
		RestServiceHelper.init(context);
		RestClientSingleton.init(context);
		// Clear Login Data
		ServiceManager.init(context, shopId);
		Log.d(TAG, "Darwin is initialized with id " + shopId);
		SHOP_ID = shopId;
		
		setUAPushTags(context, isPreInstallApp);
		
		return true;
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
		return SHOP_ID > -1;
	}

	public synchronized static Context getContext() {
		return context;
	}

	private static void setUAPushTags(Context context, boolean isPreInstallApp) {
		Set<String> tags = new HashSet<String>();
		tags.add(TimeZone.getDefault().getID());
		tags.add(Locale.getDefault().getLanguage());
		tags.add(Locale.getDefault().getCountry());
		tags.add(Build.MANUFACTURER);
		tags.add(Build.MODEL);
		tags.add(Build.VERSION.RELEASE);
		// Check pre-install flag
		if(isPreInstallApp) {
			preInstallTag(context, tags, Build.MANUFACTURER);
		}
		// Set tags
		PushManager.shared().setTags(tags);
	}

	
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
