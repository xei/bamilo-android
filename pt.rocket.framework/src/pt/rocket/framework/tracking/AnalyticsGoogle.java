package pt.rocket.framework.tracking;

import java.util.List;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	private static AnalyticsGoogle sInstance;
	
	private final static int PRE_INSTALL_ID = 1;
	
	private final static int SIM_OPERATOR_ID = 2;
	
	private GoogleAnalytics mAnalytics;
	
	private Tracker mTracker;
	
	private Context mContext;
	
	private String mCurrentKey;
	
	private boolean isEnabled;
	
	private String mShopId;
	
	private SharedPreferences mSharedPreferences;

	private String mGACampaign;

	private Bundle mCustomData;

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
		// Set debug mode
		validateDebugMode(context.getResources().getBoolean(R.bool.ga_debug_mode));
		// Set key
		updateTracker();
//		mTracker.enableAdvertisingIdCollection(true);
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
		mCurrentKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
		Log.d(TAG, "TRACK LOAD KEYS: mCurrentKey-> " + mCurrentKey);
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
		Log.i(TAG, "UPDATED TRACKER WITH KEY: " + mCurrentKey);
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
		Log.i(TAG, "TRACK PAGE: " + path);
		mTracker.setScreenName(path);
		mTracker.send(new HitBuilders.AppViewBuilder()
		.setCampaignParamsFromUrl(getGACampaign())
		.setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
		.build());
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
    	.setCampaignParamsFromUrl(getGACampaign())
    	.setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
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
        .setCampaignParamsFromUrl(getGACampaign())
        .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
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
        .setCampaignParamsFromUrl(getGACampaign())
        .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
        .build());
	}
	
	/**
	 * Build and send a transaction.
	 * @param order
	 * @param revenue
	 * @param currencyCode
	 * @author sergiopereira
	 */
	private void trackTransaction(String order, long revenue, String currencyCode) {
		Log.i(TAG, "TRACK TRANSACTION: id->" + order + " revenue->" + revenue + " currency->" + currencyCode);
		mTracker.send(new HitBuilders.TransactionBuilder()
		.setTransactionId(order)
		.setRevenue(revenue)
		.setCurrencyCode(currencyCode)
		.setCampaignParamsFromUrl(getGACampaign())
		.setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
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
		Log.i(TAG, "TRACK TRANSACTION ITEM: id->" + order + " nm->" + name + " sku->" + sku + " ct->" + category + " prc->" + price + " qt->" + quantity);
		mTracker.send(new HitBuilders.ItemBuilder()
		.setTransactionId(order)
	    .setName(name)
	    .setSku(sku)
	    .setCategory(category)
	    .setPrice(price)
	    .setQuantity(quantity)
	    .setCurrencyCode(currencyCode)
	    .setCampaignParamsFromUrl(getGACampaign())
	    .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
		.setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR))
	    .build());
	}
	
	/**
	 * Build and send a GA campaign.
	 * @param campaign
	 * @author sergiopereira
	 */
	protected void trackGACampaign() {
		// Track
		// String utmURI = (!mGACampaign.contains("utm_source")) ? "utm_campaign=" + mGACampaign + "&utm_source=push&utm_medium=referrer" : mGACampaign;
		// Log.i(TAG, "TRACK CAMPAIGN: campaign->" + utmURI);
		// mTracker.send(new HitBuilders.AppViewBuilder()
		// .setCampaignParamsFromUrl(utmURI)
		// .build());
		
		//mTracker.set("&cn", campaign);
		//mTracker.set("&cs", "push");
		//mTracker.set("&cm", "referrer");
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
		if (-1 != page.getName()) {
    		// Get and send page
    		String path = mContext.getString(page.getName());
    		trackPage(path);
		}
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
		TrackingEvent event;
		if (isCheckoutStarted) {
			event = TrackingEvent.CHECKOUT_CONTINUE;
		} else {
			event = TrackingEvent.CHECKOUT_STARTED;
			isCheckoutStarted = true;
		}
		// Track each item
		for (ShoppingCartItem item : items) {
			String sku = item.getConfigSimpleSKU();
			double price = item.getPriceForTracking() * item.getQuantity();
			trackEvent(event, sku, (long) price);
		}
	}
	
	/**
	 * 
	 * @param navigationPrefix
	 * @param navigationPath
	 * @param name
	 * @param sku
	 * @param price
	 */
	public void trackProduct(TrackingEvent event, String navigationPrefix, String navigationPath, String name, String sku, Double price) {
		// Validation
		if (!isEnabled) return;
		// Data
		if(navigationPrefix == null) navigationPrefix = "n.a.";
		String pageView;
		String n = !TextUtils.isEmpty(name) ? name.replace(" ", "_") : "n.a.";
		if(!TextUtils.isEmpty(navigationPath)){
			pageView = navigationPrefix + "_" + navigationPath + "/" + n;
		} else {
			pageView = navigationPrefix + "_" + n;
		}
		trackPage(pageView);
		trackEvent(event, sku, (price != null) ? price.longValue() : 0l);
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
	 * Tracking the purchase using the EURO currency.
	 * @param order number
	 * @param cart value euro converted
	 * @param list of items
	 * @author sergiopereira
	 */
	public void trackPurchase(String orderNr, double cartValue, List<PurchaseItem> items) {
		isCheckoutStarted = false;
		// Validation
		if (!isEnabled) return;
		// Validation
		if (items == null || items.size() == 0) return;
		// Get euro currency
		String currencyCode = CurrencyFormatter.EURO_CODE;
		//String currencyCode = CurrencyFormatter.getCurrencyCode();
		// Track transaction
		trackTransaction(orderNr, (long) cartValue, currencyCode);
		// Track all items
		for (PurchaseItem item : items)
			trackTransactionItem(orderNr, item.name, item.sku, item.category, (long) item.getPriceForTracking(), (long) item.quantityAsInt, currencyCode);
		// Event
		trackEvent(TrackingEvent.CHECKOUT_FINISHED, orderNr, (long) cartValue);
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
	 * @param ratingLabel
	 */
	public void trackRateProduct(Context context, String sku, Long value, String ratingLabel){
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(mContext.getString(R.string.grateproduct));
		stringBuilder.append(ratingLabel);
		String action = stringBuilder.toString();		
		trackEvent(category, action, sku, value);
	}
	
	/**
	 * Track a page from a string value.
	 * @param page
	 * @author sergiopereira
	 */
	public void trackGenericPage(String page){
		// Validate
		if (!isEnabled) return;
		// Data		
		trackPage(page);
	}

	/**
	 * Google Analytics "General Campaign Measurement"
	 * 
	 * @param UTM Campaign
	 * 
	 */
	public void setGACampaign(String campaignString) {
		// Validation
		if (!isEnabled) return;
		// Data
		if (!TextUtils.isEmpty(campaignString)) {
			// Track
			String utmURI = (!campaignString.contains("utm_campaign")) ? "utm_campaign=" + campaignString + "&utm_source=push&utm_medium=referrer" : campaignString;
			Log.i(TAG, "TRACK CAMPAIGN: campaign->" + utmURI);
			mGACampaign = utmURI;
		}
	}
	
	/**
	 * Build and send a GA campaign.
	 * @param campaign
	 * @author sergiopereira
	 */
	private String getGACampaign() {
		return !TextUtils.isEmpty(mGACampaign) ? mGACampaign : "";
	}

	/**
	 * Save the custom data.
	 * @param data
	 * @author sergiopereira
	 */
	public void setCustomData(Bundle data) {
		// Validation
		if (!isEnabled) return;
		// Set device info
		mCustomData = data;
		//mTracker.set("&cd1", ""+info.getBoolean(Constants.INFO_PRE_INSTALL, false));
		//mTracker.set("&cd2", info.getString(Constants.INFO_SIM_OPERATOR));
	}
	
	/**
	 * Get the current custom data.
	 * @return data
	 * @author sergiopereira
	 */
	private Bundle getCustomData() {
		return mCustomData != null ? mCustomData : new Bundle();
	}
}
