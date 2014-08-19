package pt.rocket.framework.tracking;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.ContentValues;
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

	private static boolean isCheckoutStarted;

	/**
	 * 
	 * @param context
	 * @param shopId
	 */
	public static void startup(Context context, String shopId) {
		sInstance = new AnalyticsGoogle(context, shopId);
	}

	/**
	 * Gets the current instance of the analytics object to use
	 * 
	 * 
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
		mSharedPreferences = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		mContext = context;
		mShopId = shopId;
		isCheckoutStarted = false;
		
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
		Log.d(TAG, "tracking successfully setup");
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
	}
	
	/**
	 * ################## CONFIGS ################## 
	 */
	
	/**
	 * 
	 * @param testMode
	 */
	private void validateTestMode(boolean testMode) {
		// Case debug mode
		mCurrentKey = (testMode) ? mTestKey : mLiveKey;
		Log.d(TAG, "VALIDATE TEST MODE: " + testMode + " KEY: " + mCurrentKey);
	}
	
	/**
	 * When dry run is set, hits will not be dispatched, but will still be logged as though they were dispatched.
	 * @param testMode
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
	 * 
	 */
	private void loadKeys() {
		// Load keys
		mSharedPreferences = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		mLiveKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
		mTestKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_TEST_ID, null);
		Log.d(TAG, "code1keys : mTestKey : "+mTestKey+" mLiveKey : "+mLiveKey);
	}

	/**
	 * 
	 * @param testing
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
	 * 
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
	 * ################## BASE TRACKING ################## 
	 */
	
	/**
	 * 
	 * @param path
	 */
	private void trackPage(String path) {
		Log.d(TAG, "TRACK PAGE: " + path);
		mTracker.setScreenName(path);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	

	/**
	 * 
	 * @param category
	 * @param action
	 * @param label
	 * @param value
	 */
	private void trackEvent(String category, String action, String label, long value) {
		// Validation
		if (!isEnabled) return;
		// Tracking
		Log.i(TAG, "TRACK EVENT: category->" + category + " action->" + action + " label->" + label + " value->" + value);
		HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
    		.setCategory(category)
    		.setAction(action)
    		.setLabel(label)
    		.setValue(value);
		mTracker.send(builder.build());
	}
	
	/**
	 * ################## SPECIFIC TRACKING ################## 
	 */
	
	/**
	 * 
	 * @param page
	 */
	public void trackPage(TrackingPages page) {
		// Validate
		if (!isEnabled) return;
		// Data
		int stringId = -1;
		switch (page) {
		case NAVIGATION: 		stringId = R.string.gnavigation; 		break;
		case PRODUCT_LIST: 		stringId = R.string.gproductlist; 		break;
		case CHECKOUT_THANKS:	stringId = R.string.gcheckoutfinal; 	break;
		case HOME:				stringId = R.string.ghomepage;			break;
		case PRODUCT_DETAIL:	stringId = R.string.gproductdetail;		break;
		case FILLED_CART:		stringId = R.string.gcartwithitems;		break;
		case EMPTY_CART:		stringId = R.string.gcartempty;			break;
		case CART:				stringId = R.string.gshoppingcart;		break;
		case CAMPAIGNS:			stringId = R.string.gcampaignpage;		break;
		case RECENTLY_VIEWED:	stringId = R.string.grecentlyviewed;	break;
		case NEWSLETTER_SUBS:	stringId = R.string.gnewslettersubs;	break;
		case RECENT_SEARCHES:	stringId = R.string.grecentsearches;	break;
		default:														break;
		}
		// Get and send page
		String path = (stringId != -1) ? mContext.getString(stringId) : "n.a.";
		trackPage(path);
	}
	
	/**
	 * TODO: Add more events
	 * @param event
	 * @param label
	 * @param value
	 */
	public void trackEvent(TrackingEvents event, String label, long value) {
		// Validation
		if (!isEnabled) return;
		// Data
		int categoryId = event.getCategory();
		int actionId = event.getAction();
		// Get and send page
		String category = (categoryId != -1) ? mContext.getString(categoryId) : "n.a.";
		String action = (actionId != -1) ? mContext.getString(actionId)	: "n.a.";
		// Tracking
		trackEvent(category, action, label, value);
	}

	/**
	 * 
	 * @param searchTerm
	 * @param numberOfItems
	 */
	public void trackSearch(String searchTerm, long numberOfItems) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gsearch);
		Log.d(TAG, "trackSearch: category = " + category + " searchTerm = " + searchTerm);
		trackEvent(category, action, searchTerm, numberOfItems);
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
	 * @param sku
	 * @param price
	 */
	public void trackAddToCart(String sku, Long price) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gaddtocart);
		trackEvent(category, action, sku, price);
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
		trackTiming(categoryId, nameId, milliseconds, "duration for event");
	}
	
	/**
	 * 
	 * @param categoryId
	 * @param nameId
	 * @param milliSeconds
	 * @param label
	 */
	private void trackTiming(int categoryId, int nameId, long milliSeconds, String label) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(categoryId);
		String name = mContext.getString(nameId);
		Log.d( TAG, "trackTiming category = " + category + " millis = " + milliSeconds );
		//mTracker.sendTiming(category, milliSeconds, name, label);
		
		mTracker.send(new HitBuilders.TimingBuilder()
			.setCategory(category)
            .setValue(milliSeconds)
            .setVariable(name)
            .setLabel(label)
            .build());
		
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
	 */
	public void trackSignUp(String email) {
		// Validation
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gsignup);
		String action = mContext.getString(R.string.gsignup);
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
		
		Log.d(TAG, "code1track value "+value);
		Double valueDouble = CurrencyFormatter.getValueDouble(value.trim());
		long valueAsLongMicro = (long) (valueDouble * MICRO_MULTI);
		String currencyCode = CurrencyFormatter.getCurrencyCode();

		// Transaction
		// TransactionBuilder transaction = new Transaction.Builder(orderNr, valueAsLongMicro).setCurrencyCode(currencyCode).build();
//		new HitBuilders.TransactionBuilder()
//        .setTransactionId(getOrderId())
//        .setAffiliation(getStoreName())
//        .setRevenue(getTotalOrder())
//        .setTax(getTotalTax())
//        .setShipping(getShippingCost())
//        .setCurrencyCode("USD")
//        .build();
		
		Map<String, String> transaction = new HitBuilders.TransactionBuilder()
		.setTransactionId(orderNr)
		.setRevenue(valueAsLongMicro)
		.setCurrencyCode(currencyCode)
		.build();
		mTracker.send(transaction);
		
		//Log.d(TAG, "TRANSACTION TOTAL COST: " + transaction.getTotalCostInMicros());
		for (PurchaseItem item : items) {
			//Log.d(TAG, "transaction item event: " + item.name + " " + item.paidprice + " " + item.paidpriceAsDouble);
			long itemValueAsLongMicro = (long) (item.paidpriceAsDouble * MICRO_MULTI);
			long quantity = item.quantityAsInt;
			
			Map<String, String> transactionItem = new HitBuilders.ItemBuilder()
			.setTransactionId(orderNr)
            .setName(item.name)
            .setSku(item.sku)
            .setCategory(item.category)
            .setPrice(itemValueAsLongMicro)
            .setQuantity(quantity)
            .setCurrencyCode(currencyCode)
            .build();
			mTracker.send(transactionItem);
			
			//Item transactionItem = new Transaction.Item.Builder(item.sku, item.name, itemValueAsLongMicro, quantity).setProductCategory(item.category).build();
			//Log.d(TAG, "TRANSACTION ITEM PRICE: " + transactionItem.getPriceInMicros());
			//transaction.addItem(transactionItem);
		}
		//mTracker.sendTransaction(transaction);
		
		// Event
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gfinished);
		Log.d(TAG, "trackCheckout event: category = " + category + " action = " + action + " orderNr = " + orderNr + " value = " + valueDouble.longValue());
		//mTracker.sendEvent(category, action, orderNr, valueDouble.longValue());
		
		HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
    	.setCategory(category)
    	.setAction(action)
    	.setLabel(orderNr)
    	.setValue(valueDouble.longValue());
		mTracker.send(builder.build());
		
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
		
		//mTracker.sendSocial(category, action, sku);
		
		// XXX
		mTracker.send(new HitBuilders.SocialBuilder()
		.setNetwork(category)
        .setAction(action)
        .setTarget(sku)
        .build());
	}

	/**
	 * 
	 * @param context
	 * @param userId
	 */
	public void trackCheckoutStart(Context context, String userId){
		// Validate
		if (!isEnabled) return;
		// Get data
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutstart);
		trackEvent(category, action, userId, 0l);
	}
	
	/**
	 * 
	 * @param context
	 * @param userId
	 */
	public void trackCheckoutContinueShopping(Context context, String userId){
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutcontinueshopping);
		trackEvent(category, action, userId, 0l);
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
		if (campaignString != null) {
            Log.d(TAG, "Google Analytics, Campaign: " + campaignString);
			//mTracker.setCampaign(campaignString);
			
			mTracker.send(new HitBuilders.AppViewBuilder()
            .setCampaignParamsFromUrl(campaignString)
            .build());

			
		} 
	}
	
	/**
	 * 
	 * @param mCatalogFilterValues
	 */
	public void trackCatalogFilter(ContentValues mCatalogFilterValues) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gfilters);
		// Validate content
		String filter = (mCatalogFilterValues != null) ? mCatalogFilterValues.toString() : "";
		trackEvent(category, action, filter, 0l);		
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
