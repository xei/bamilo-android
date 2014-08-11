package pt.rocket.framework.tracking;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;
import com.google.analytics.tracking.android.Transaction.Item;

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
	SharedPreferences mSharedPreferences;
	
	private HashMap<TrackingPages, Integer> screens;

	private static boolean isCheckoutStarted;

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
		if (sInstance == null) {
			sInstance = new AnalyticsGoogle();
		}
		return sInstance;
	}

	/**
	 * The private constructor for the Analytics preventing the instantiation of
	 * this object
	 * 
	 * @param context
	 *            the base context for the analytics to run
	 */
	private AnalyticsGoogle(Context context, String shopId) {
		mSharedPreferences = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		mContext = context;
		mShopId = shopId;
		isCheckoutStarted = false;
		isEnabled = mContext.getResources().getBoolean(R.bool.ga_enable);
		if (!isEnabled) {
			return;
		}

        screens = new HashMap<TrackingPages, Integer>();
        screens.put(TrackingPages.NAVIGATION, R.string.gnavigation);
        screens.put(TrackingPages.PRODUCT_LIST, R.string.gproductlist);
        screens.put(TrackingPages.CHECKOUT_THANKS, R.string.gcheckoutfinal);
        screens.put(TrackingPages.HOME, R.string.ghomepage);
        screens.put(TrackingPages.PRODUCT_DETAIL, R.string.gproductdetail);
        screens.put(TrackingPages.FILLED_CART, R.string.gcartwithitems);
        screens.put(TrackingPages.EMPTY_CART, R.string.gcartempty);
        screens.put(TrackingPages.CART, R.string.gshoppingcart);
        screens.put(TrackingPages.CAMPAIGNS, R.string.gcampaignpage);
        screens.put(TrackingPages.RECENTLY_VIEWED, R.string.grecentlyviewed);
        screens.put(TrackingPages.NEWSLETTER_SUBSCRIPTION, R.string.gnewslettersubscription);
        screens.put(TrackingPages.RECENT_SEARCHES, R.string.grecentsearches);
		
		loadKeys();
		mAnalytics = GoogleAnalytics.getInstance(mContext);
		
		boolean testMode = context.getResources().getBoolean(R.bool.ga_testmode);
		if (testMode) {
			mCurrentKey = mTestKey;
		} else {
			mCurrentKey = mLiveKey;
		}
		
		updateTracker();
		Log.i(TAG, "tracking successfully setup");
	}

	private AnalyticsGoogle() {
		isEnabled = false;
	}

	private void loadKeys() {
		
		mSharedPreferences = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		
		 mLiveKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
		 mTestKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_TEST_ID, null);
		Log.i(TAG, "code1keys : mTestKey : "+mTestKey+" mLiveKey : "+mLiveKey);
	}

	public void switchMode(boolean testing) {
		if (!isEnabled) {
			return;
		}

		if (testing) {
			mCurrentKey = mTestKey;
		} else {
			mCurrentKey = mLiveKey;
		}
		
		updateTracker();
	}

	private void updateTracker() {

		if (TextUtils.isEmpty(mCurrentKey)) {
			isEnabled = false;
			Log.e("WARNING: NO TRACKING ID FOR SHOP ID " + mShopId + " KEY " + mCurrentKey);
			return;
		}
		mTracker = mAnalytics.getTracker(mCurrentKey);
		mTracker.setAnonymizeIp(true);
		Log.i(TAG, "tracking switched");
	}

	public void trackPage(TrackingPages page) {
		// Validate
		if (!isEnabled) return;
		// Data
		if (null != page && screens.containsKey(page)) {
	        Integer pageRes = screens.get(page);
	        String pageName = mContext.getString(pageRes);
	        Log.d(TAG, "trackPage: " + pageName);
	        mTracker.sendView(pageName);
		}
	}

	public void trackSourceResWithPath(int sourcePrefixRes, String path) {
		if (!isEnabled) {
			return;
		}

		String pageName = mContext.getString(sourcePrefixRes) + "_" + path;
		Log.d(TAG, "trackSourceWithPath: pageName = " + pageName);
		mTracker.sendView(pageName);
	}

	public void trackBrand(String brandCode) {
		if (!isEnabled) {
			return;
		}

		String brand = mContext.getString(R.string.gbrand_prefix) + "_/" + brandCode;
		mTracker.sendView(brand);
	}

	public void trackSearch(String searchTerm, long numberOfItems) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gsearch);

		// Log.d(TAG, "trackSearch: category = " + category + " searchTerm = " +
		// searchTerm);
		mTracker.sendEvent(category, action, searchTerm, numberOfItems);
	}

	public void trackProduct(int navigationPrefix, String navigationPath, String name, String sku, String url, Double price) {
		if (!isEnabled) return;

		if (navigationPrefix != -1) {
			// Log.d( TAG, "trackProduct: navigationPath = " + navigationPath + " name = " + name );
			String pageView;
			String n = !TextUtils.isEmpty(name) ? name.replace(" ", "_") : "n.a.";
			if(navigationPath != null && !navigationPath.equalsIgnoreCase("")){
				pageView = mContext.getString(navigationPrefix) + "_" + navigationPath + "/" + n;	
			} else {
				pageView = mContext.getString(navigationPrefix) + "_" + n;
			}
			//Log.d(TAG, "trackProduct pageView = " + pageView);
			mTracker.sendView(pageView);
		}

		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gpdv);

		//Log.d(TAG, "trackProduct: category = " + category + " action = " + action + " sku = " + sku + " ptrice = " + price);
		mTracker.sendEvent(category, action, sku, (price != null) ? price.longValue() : 0l);
	}

	public void trackAccount(int resAction, String user_id) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gaccount);
		String action = mContext.getString(resAction);
		String label;
		if (user_id == null) {
			label = "";
		} else {
			label = user_id;
		}

		Log.d(TAG, "trackAccount: category = " + category + " action = " + action + " label = " + label);
		mTracker.sendEvent(category, action, label, 0l);
	}

	public void trackAddToCart(String sku, Long price) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gaddtocart);
		Log.d(TAG, "trackProduct: category = " + category + " action = " + action + " sku = " + sku + " price = " + price);
		mTracker.sendEvent(category, action, sku, price);
	}

	public void trackLoadTiming(int categoryId, long beginMillis) {				
		if ( !isEnabled ) {
			return;
		}
		
		long milliseconds = System.currentTimeMillis();
		if ( milliseconds < beginMillis || beginMillis <= 0 ) {
			Log.d( TAG, "trackTiming ERROR : start -> " + beginMillis );
			return;
		}
		
		milliseconds = milliseconds - beginMillis;
		int nameId = R.string.gload;

		trackTiming(categoryId, nameId, milliseconds, "duration for event");
	}

	public static void clearCheckoutStarted() {
		isCheckoutStarted = false;
	}

	public void trackCheckout(List<ShoppingCartItem> items) {
		if (!isEnabled) {
			return;
		}

		if (items == null || items.size() == 0) {
			return;
		}

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

			Log.d(TAG, "trackCheckout: category = " + category + " action = " + action + " label(simple-sku) = " + sku
					+ " price = " + price);
			mTracker.sendEvent(category, action, sku, price);
		}
	}
	
	public void trackCheckoutStep(String email, int step) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gNativeCheckout);
		String action = mContext.getString(step);
		Log.d(TAG, "trackCheckoutStep: category = " + category + " action = " + action + " email " + email);
		mTracker.sendEvent(category, action, email, (long) 0);
		
	}
	
	public void trackSignUp(String email) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gSignUp);
		String action = mContext.getString(R.string.gSignUp);
		Log.d(TAG, "trackSignUp: category = " + category + " action = " + action + " email " + email);
		mTracker.sendEvent(category, action, email, (long) 0);
		
	}
	
	public void trackPaymentMethod(String email, String payment) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gPaymentMethod);

		Log.d(TAG, "trackCheckoutStep: category = " + category + " payment = " + payment + " email " + email);
		mTracker.sendEvent(category, payment, email, (long) 0);
		
	}
	
	public void trackNativeCheckoutError(String email, String error) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gNativeCheckoutError);

		Log.d(TAG, "trackNativeCheckoutError: category = " + category + " error = " + error + " email " + email);
		mTracker.sendEvent(category, error, email, (long) 0);
		
	}

	private void trackTiming(int categoryId, int nameId, long milliSeconds, String label) {
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(categoryId);
		String name = mContext.getString(nameId);

		// Log.d( TAG, "trackTiming category = " + category + " millis = " +
		// milliSeconds );
		mTracker.sendTiming(category, milliSeconds, name, label);
	}

	public void trackSales(String orderNr, String value, List<PurchaseItem> items) {
		isCheckoutStarted = false;
		// Validation
		if (!isEnabled) return;
		// Validation
		if (items == null || items.size() == 0) return;
		
		Log.i(TAG, "code1track value "+value);
		Double valueDouble = CurrencyFormatter.getValueDouble(value.trim());
		long valueAsLongMicro = (long) (valueDouble * MICRO_MULTI);
		String currencyCode = CurrencyFormatter.getCurrencyCode();

		// Transaction
		Transaction transaction = new Transaction.Builder(orderNr, valueAsLongMicro).setCurrencyCode(currencyCode).build();
		Log.i(TAG, "TRANSACTION TOTAL COST: " + transaction.getTotalCostInMicros());
		for (PurchaseItem item : items) {
			//Log.i(TAG, "transaction item event: " + item.name + " " + item.paidprice + " " + item.paidpriceAsDouble);
			long itemValueAsLongMicro = (long) (item.paidpriceAsDouble * MICRO_MULTI);
			long quantity = item.quantityAsInt;
			Item transactionItem = new Transaction.Item.Builder(item.sku, item.name, itemValueAsLongMicro, quantity).setProductCategory(item.category).build();
			Log.i(TAG, "TRANSACTION ITEM PRICE: " + transactionItem.getPriceInMicros());
			transaction.addItem(transactionItem);
		}
		mTracker.sendTransaction(transaction);
		
		// Event
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gfinished);
		Log.d(TAG, "trackSales event: category = " + category + " action = " + action + " orderNr = " + orderNr + " value = " + valueDouble.longValue());
		mTracker.sendEvent(category, action, orderNr, valueDouble.longValue());
	}

	public void trackShare(Context context, String sku, String user_id, String shop_country ){
		// Validate
		if (!isEnabled) return;
		// Get data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gsocialshare);
		//Log.i(TAG, "TRACK SHARE EVENT: Cat " + category + ", Action " + action + ", Sku " + sku);
		mTracker.sendSocial(category, action, sku);
	}
	
	public void trackCheckoutStart(Context context, String userId){
		// Validate
		if (!isEnabled) return;
		// Get data
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutstart);
		//Log.i(TAG, "TRACK START: Cat " + category + ", Action " + action + ", UserId " + userId);
		mTracker.sendEvent(category, action, userId, (long) 0);
	}
	
	public void trackCheckoutContinueShopping(Context context, String userId){
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutcontinueshopping);
		//Log.i(TAG, "TRACK CONT SHOP EVENT: Cat " + category + ", Action " + action + ", UserId " + userId);
		mTracker.sendEvent(category, action, userId, (long) 0);
	}
	
	public void trackRateProduct(Context context, String sku, Long value, String ratingType){
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(mContext.getString(R.string.grateproduct));
		stringBuilder.append(ratingType.toUpperCase(Locale.getDefault()));
		String action = stringBuilder.toString();
		//Log.i(TAG, "TRACK RATING EVENT: Cat " + category + ", Action " + action + ", Sku " + sku + ", Value " + value);
		mTracker.sendEvent(category, action, sku, value);
	}
	
	public void sendException(String msg, Exception e, boolean nonFatal) {
		if (!isEnabled) return;
		mTracker.sendException(msg, e, nonFatal);
	}

	private static String calcPathSegment(List<String> list, int begin) {
		if (list == null || begin >= list.size()) {
			return null;
		}

		StringBuilder out = new StringBuilder();
		int idx;
		for (idx = begin; idx < list.size(); idx++) {
			out.append("/");
			out.append(list.get(idx));
		}
		return out.toString();
	}

	public static String prepareNavigationPath(String url) {
		Log.d(TAG, "url = " + url);
		Uri uri = Uri.parse(url);
		return calcPathSegment(uri.getPathSegments(), 1);
	}

	/**
	 * Google Analytics "General Campaign Measurement"
	 * 
	 * @param UTM Campaign
	 * 
	 */
	public void setCampaign(String campaignString) {
		if (!isEnabled) return;
		
		if (campaignString != null) {
            Log.d(TAG, "Google Analytics, Campaign: " + campaignString);
			mTracker.setCampaign(campaignString);
		} 
	}
	
	public void trackCatalogFilter(ContentValues mCatalogFilterValues) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gfilters);
		// Validate content
		String filter = (mCatalogFilterValues != null) ? mCatalogFilterValues.toString() : "";  
		Log.d(TAG, "trackFilter: category = " + category + " action = " + action + " filters = " + filter);
		mTracker.sendEvent(category, action, filter, 0l);
	}
	
	public void trackNewsletterSubscription(String userId, boolean subscribe) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gaccount);
		String action = mContext.getString(subscribe ? R.string.gsubscribenewsletter : R.string.gunsubscribenewsletter);
		Log.d(TAG, "trackNewsletter: category = " + category + " action = " + action + " userId = " + userId + " subscribe = " + subscribe);
		mTracker.sendEvent(category, action, userId, 0l);
	}
	
	public void trackSearchSuggestions(String query) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gaccount);
		String action = mContext.getString(R.string.gsearchsuggestions);
		Log.d(TAG, "trackNewsletter: category = " + category + " action = " + action + " search = " + query);
		mTracker.sendEvent(category, action, query, 0l);
	}
	
	public void trackCategory(String location, String title) {
		// Validate
		if (!isEnabled) return;
		// Data
		String category = mContext.getString(R.string.gcatalog);
		Log.d(TAG, "trackCategory: category = " + category + " action = " + location + " category = " + title);
		mTracker.sendEvent(category, location, title, 0l);
	}
}
