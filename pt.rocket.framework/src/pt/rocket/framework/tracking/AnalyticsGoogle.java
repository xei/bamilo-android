package pt.rocket.framework.tracking;

import java.util.List;
import java.util.Locale;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;

import de.akquinet.android.androlog.Log;

/**
 * Helper singleton class for the Google Analytics tracking library.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Michael Kroez
 * @modified sergiopereira
 * @version 0.1
 * 
 */
public class AnalyticsGoogle {

	private static final String TAG = LogTagHelper.create(AnalyticsGoogle.class);
	
	private static final double MICRO_MULTI = 1000000;

	private static AnalyticsGoogle sInstance;
	
	private GoogleAnalytics mAnalytics;
	
	private Tracker mTracker;
	
	private Context mContext;
	
	private String mTestKey;
	
	private String mLiveKey;
	
	private String mCurrentKey;
	
	private boolean isEnabled;
	
	private String mShopId;
	
	private SharedPreferences mSharedPreferences;

	private static boolean isCheckoutStarted = false;

	/**
	 * Startup GA
	 * @param context
	 * @param shopId
	 */
	public static void startup(Context context, String shopId) {
		sInstance = new AnalyticsGoogle(context, shopId);
	}

	/**
	 * Gets the current instance of the analytics object to use.
	 * @return the global {@link AnalyticsUtils} singleton object, creating one
	 *         if necessary.
	 */
	public static AnalyticsGoogle get() {
		return (sInstance == null) ? sInstance = new AnalyticsGoogle() : sInstance;
	}
	
	/**
	 * Empty constructor
	 */
	private AnalyticsGoogle() {
		isEnabled = false;
	}

	/**
	 * The private constructor for the Analytics preventing the instantiation of
	 * this object
	 * 
	 * @param context the base context for the analytics to run
	 * @param shopId the current shop id
	 */
	private AnalyticsGoogle(Context context, String shopId) {
		// Save data
		mContext = context;
		mShopId = shopId;
		// Validation
		isEnabled = mContext.getResources().getBoolean(R.bool.ga_enable);
		if (!isEnabled) return;
		// Get instance
		mAnalytics = GoogleAnalytics.getInstance(mContext);
		// Load live and test key
		loadKeys();
		// Set test mode to set key
		validateTestMode(context.getResources().getBoolean(R.bool.ga_testmode));
		// Set debug mode
		validateDebugMode(context.getResources().getBoolean(R.bool.ga_debug_mode));
		// Set key
		updateTracker();
		Log.i(TAG, "TRACKING SUCCESSFULLY STEUP");
	}
	
	/**
	 * Manual Dispatch
	 * @author sergiopereira
	 */
	public void dispatchHits() {
		// Validation
		if (!isEnabled) return;
        // Manually start a dispatch (Unnecessary if the tracker has a dispatch interval)
        GoogleAnalytics.getInstance(mContext).dispatchLocalHits();
        Log.i(TAG, "TRACK DISPATCH LOCAL HITS MANUALLY");
	}
	
	/**
	 * ################## CONFIGS ################## 
	 */
	
	/**
	 * Validate the current envirment to load the respective key
	 * @param testMode
	 * @author sergiopereira
	 */
	private void validateTestMode(boolean testMode) {
		// Case debug mode
		mCurrentKey = (testMode) ? mTestKey : mLiveKey;
		Log.d(TAG, "TRACK TEST MODE: " + testMode + " KEY: " + mCurrentKey);
	}
	
	/**
	 * When dry run is set, hits will not be dispatched, but will still be logged as though they were dispatched.
	 * @param testMode
	 * @author sergiopereira
	 */
	private void validateDebugMode(boolean debugMode) {
		// Case debug mode
		if(debugMode) {
			Log.w(TAG, "WARNING: DEBUG IS ENABLE SO HITS WILL NOT BE DISPATCHED");
			mAnalytics.setDryRun(true);
			mAnalytics.getLogger().setLogLevel(LogLevel.VERBOSE);
		}
	}

	/**
	 * Load keys from saved preferences
	 * @author sergiopereira
	 */
	private void loadKeys() {
		// Load keys
		mSharedPreferences = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		mLiveKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
		mTestKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_TEST_ID, null);
		Log.d(TAG, "TRACK LOAD KEYS: test-> " + mTestKey + " live->" + mLiveKey);
	}

	/**
	 * Switch mode
	 * @param testing
	 * @author sergiopereira
	 */
	public void switchMode(boolean testing) {
		// Validation
		if (!isEnabled) return;
		// Mode
		validateTestMode(testing);
		// Update
		updateTracker();
	}

	/**
	 * Update the tracker using the current key
	 * @author sergiopereira
	 */
	private void updateTracker() {
		if (TextUtils.isEmpty(mCurrentKey)) {
			isEnabled = false;
			Log.e("WARNING: NO TRACKING ID FOR SHOP ID " + mShopId + " KEY " + mCurrentKey);
			return;
		}
		mTracker = mAnalytics.newTracker(mCurrentKey);
		mTracker.setAnonymizeIp(true);
		Log.d(TAG, "tracking switched");
	}
	
	/**
	 * ################## BASE GA TRACKING (v4) ################## 
	 */
	
	/**
	 * Build and send a page
	 * @param path
	 * @author sergiopereira
	 */
	private void trackPage(String path) {
		Log.d(TAG, "TRACK PAGE: " + path);
		mTracker.setScreenName(path);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * Build and send an event.
	 * @param category
	 * @param action
	 * @param label
	 * @param value
	 * @author sergiopereira
	 */
	private void trackEvent(String category, String action, String label, long value) {
		Log.i(TAG, "TRACK EVENT: category->" + category + " action->" + action + " label->" + label + " value->" + value);
		mTracker.send(new HitBuilders.EventBuilder()
    	.setCategory(category)
    	.setAction(action)
    	.setLabel(label)
    	.setValue(value)
		.build());
	}
	
	/**
	 * Build and send a social action.
	 * @param category
	 * @param action
	 * @param target
	 * @author sergiopereira
	 */
	private void trackShare(String category, String action, String target) {
		Log.i(TAG, "TRACK SHARE: category->" + category + " action->" + action + " target->" + target);
		mTracker.send(new HitBuilders.SocialBuilder()
		.setNetwork(category)
        .setAction(action)
        .setTarget(target)
        .build());
	}
	
	/**
	 * Build and send a timing.
	 * @param categoryId
	 * @param nameId
	 * @param milliSeconds
	 * @param label
	 * @author sergiopereira
	 */
	private void trackTiming(String category, String name, long milliSeconds, String label) {
		Log.i(TAG, "TRACK TIMING: category->" + category + " name->" + name + " ms->" + milliSeconds + " label->" + label);
		mTracker.send(new HitBuilders.TimingBuilder()
		.setCategory(category)
        .setValue(milliSeconds)
        .setVariable(name)
        .setLabel(label)
        .build());
	}
	
	/**
	 * Build and send a transaction.
	 * @param order
	 * @param valueAsLongMicro
	 * @param currencyCode
	 * @author sergiopereira
	 */
	private void trackTransaction(String order, long valueAsLongMicro, String currencyCode) {
		Log.i(TAG, "TRACK TRANSACTION: id->" + order + " revenue->" + valueAsLongMicro + " currency->" + currencyCode);
		mTracker.send(new HitBuilders.TransactionBuilder()
		.setTransactionId(order)
		.setRevenue(valueAsLongMicro)
		.setCurrencyCode(currencyCode)
		.build());
	}
	
	/**
	 * Build and send a transaction item.
	 * @param order
	 * @param name
	 * @param sku
	 * @param category
	 * @param price
	 * @param quantity
	 * @param currencyCode
	 * @author sergiopereira
	 */
	private void trackTransactionItem(String order, String name, String sku, String category, long price, long quantity, String currencyCode) {
		Log.i(TAG, "TRACK TRANSACTION: id->" + order + " nm->" + name + " sku->" + sku + " ct->" + category + " prc->" + price + " qt->" + quantity);
		mTracker.send(new HitBuilders.ItemBuilder()
		.setTransactionId(order)
	    .setName(name)
	    .setSku(sku)
	    .setCategory(category)
	    .setPrice(price)
	    .setQuantity(quantity)
	    .setCurrencyCode(currencyCode)
	    .build());
	}
	
	/**
	 * Build and send a GA campaign.
	 * @param campaign
	 * @author sergiopereira
	 */
	private void trackCampaign(String campaign) {
		Log.d(TAG, "TRACK CAMPAIGN: campaign->" + campaign);
		mTracker.send(new HitBuilders.AppViewBuilder()
	    .setCampaignParamsFromUrl(campaign)
	    .build());
	}
		
	/**
	 * ################## SPECIFIC TRACKING ################## 
	 */
	
	/**
	 * Track a page via {@link TrackingPage}.
	 * @param page
	 * @author sergiopereira
	 */
	public void trackPage(TrackingPage page) {
		// Validate
		if (!isEnabled) return;
		// Get and send page
		String path = mContext.getString(page.getName());
		trackPage(path);
	}
	
	/**
	 * Track an event via {@link TrackingEvent}.
	 * @param event
	 * @param label
	 * @param value
	 * @author sergiopereira
	 */
	public void trackEvent(TrackingEvent event, String label, long value) {
		// Validation
		if (!isEnabled) return;
		// Get and send page
		String category = mContext.getString(event.getCategory());
		String action = mContext.getString(event.getAction());
		// Tracking
		trackEvent(category, action, label, value);
	}

	/**
	 * 
	 * @param navigationPrefix
	 * @param navigationPath
	 * @param name
	 * @param sku
	 * @param url
	 * @param price
	 */
	public void trackProduct(int navigationPrefix, String navigationPath, String name, String sku, String url, Double price) {
		// Validation
		if (!isEnabled) return;
		// Data
		if (navigationPrefix != -1) {
			String pageView;
			String n = !TextUtils.isEmpty(name) ? name.replace(" ", "_") : "n.a.";
			if(navigationPath != null && !navigationPath.equalsIgnoreCase("")){
				pageView = mContext.getString(navigationPrefix) + "_" + navigationPath + "/" + n;	
			} else {
				pageView = mContext.getString(navigationPrefix) + "_" + n;
			}
			trackPage(pageView);
		}
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gpdv);
		trackEvent(category, action, sku, (price != null) ? price.longValue() : 0l);
	}

	/**
	 * 
	 * @param resAction
	 * @param userId
	 */
	public void trackAccount(int resAction, String userId) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gaccount);
		String action = mContext.getString(resAction);
		String label = !TextUtils.isEmpty(userId) ? userId : "";
		trackEvent(category, action, label, 0l);
	}

	/**
	 * 
	 * @param categoryId
	 * @param beginMillis
	 */
	public void trackLoadTiming(int categoryId, long beginMillis) {				
		// Validation
		if (!isEnabled) return;
		// Data
		long milliseconds = System.currentTimeMillis();
		if ( milliseconds < beginMillis || beginMillis <= 0 ) {
			Log.d( TAG, "trackTiming ERROR : start -> " + beginMillis );
			return;
		}
		milliseconds = milliseconds - beginMillis;
		int nameId = R.string.gload;
		// Data
		String category = mContext.getString(categoryId);
		String name = mContext.getString(nameId);
		// Track
		trackTiming(category, name, milliseconds, "duration for event");
	}
	


	/**
	 * 
	 */
	public static void clearCheckoutStarted() {
		isCheckoutStarted = false;
	}

	/**
	 * 
	 * @param items
	 */
	public void trackCheckout(List<ShoppingCartItem> items) {
		// Validation
		if (!isEnabled) return;
		// Validate items
		if (items == null || items.size() == 0) return;
		// Data
		String category = mContext.getString(R.string.gcheckout);
		String action;
		if (isCheckoutStarted) {
			action = mContext.getString(R.string.gcontinueshopping);
		} else {
			action = mContext.getString(R.string.gstarted);
			isCheckoutStarted = true;
		}

		for (ShoppingCartItem item : items) {
			String sku = item.getConfigSimpleSKU();
			long price = item.getPriceVal().longValue() * item.getQuantity();
			trackEvent(category, action, sku, price);
		}
	}
	
	/**
	 * 
	 * @param email
	 * @param step
	 */
	public void trackCheckoutStep(String email, int step) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gNativeCheckout);
		String action = mContext.getString(step);
		trackEvent(category, action, email, 0l);
	}
	
	/**
	 * 
	 * @param email
	 * @param payment
	 */
	public void trackPaymentMethod(String email, String action) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gPaymentMethod);
		trackEvent(category, action, email, 0l);
	}
	
	/**
	 * 
	 * @param email
	 * @param error
	 */
	public void trackNativeCheckoutError(String email, String action) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gNativeCheckoutError);
		trackEvent(category, action, email, 0l);
	}
	/**
	 * 
	 * @param orderNr
	 * @param value
	 * @param items
	 */
	public void trackSales(String orderNr, String value, List<PurchaseItem> items) {
		isCheckoutStarted = false;
		// Validation
		if (!isEnabled) return;
		// Validation
		if (items == null || items.size() == 0) return;
		
		Double valueDouble = CurrencyFormatter.getValueDouble(value.trim());
		long valueAsLongMicro = (long) (valueDouble * MICRO_MULTI);
		String currencyCode = CurrencyFormatter.getCurrencyCode();

		// Track transaction
		trackTransaction(orderNr, valueAsLongMicro, currencyCode);
		
		for (PurchaseItem item : items) {
			// Track transaction item
			long itemValueAsLongMicro = (long) (item.paidpriceAsDouble * MICRO_MULTI);
			long quantity = item.quantityAsInt;
			trackTransactionItem(orderNr, item.name, item.sku, item.category, itemValueAsLongMicro, quantity, currencyCode);
		}
		
		// Event
		trackEvent(TrackingEvent.CHECKOUT_FINISHED, orderNr, valueDouble.longValue());
	}

	/**
	 * 
	 * @param context
	 * @param sku
	 * @param user_id
	 * @param shop_country
	 */
	public void trackShare(Context context, String sku, String user_id, String shop_country ){
		// Validate
		if (!isEnabled) return;
		// Get data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gsocialshare);
		Log.d(TAG, "TRACK SHARE EVENT: Cat " + category + ", Action " + action + ", Sku " + sku);
		trackShare(category, action, sku);
	}
	
	/**
	 * 
	 * @param context
	 * @param sku
	 * @param value
	 * @param ratingType
	 */
	public void trackRateProduct(Context context, String sku, Long value, String ratingType){
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(mContext.getString(R.string.grateproduct));
		stringBuilder.append(ratingType.toUpperCase(Locale.getDefault()));
		String action = stringBuilder.toString();		
		trackEvent(category, action, sku, value);
	}

	/**
	 * Google Analytics "General Campaign Measurement"
	 * 
	 * @param UTM Campaign
	 * 
	 */
	public void setCampaign(String campaignString) {
		// Validation
		if (!isEnabled) return;
		// Data
		if (campaignString != null) trackCampaign(campaignString);
	}
	
	/**
	 * 
	 * @param location
	 * @param title
	 */
	public void trackCategory(String action, String title) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		trackEvent(category, action, title, 0l);
	}

}
