package pt.rocket.framework.tracking;

import java.util.List;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Transaction;

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

//	public void setDefaultTracker() {
//		if (!isEnabled) {
//			return;
//		}
//
//		String trackerKey = mContext.getString(R.string.ga_trackingId);
//		if (TextUtils.isEmpty(trackerKey)) {
//			Log.w("No trackingId with resource ga_trackingId found. Cant enable tracking. Sorry.");
//			return;
//		}
//		mCurrentKey = trackerKey;
//		updateTracker();
//	}

	public void trackPage(int pageRes) {
		if (!isEnabled) {
			return;
		}

		String pageName = mContext.getString(pageRes);
		Log.d(TAG, "trackPage: " + pageName);
		mTracker.sendView(pageName);
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

	public void trackProduct(int navigationPrefix, String navigationPath, String name, String sku, String url) {
		if (!isEnabled) {
			return;
		}

		if (navigationPrefix == -1 || navigationPath == null || name == null) {
			return;
		}

		// Log.d( TAG, "trackProduct: navigationPath = " + navigationPath +
		// " name = " + name );
		String pageView;
		if(navigationPath != null && !navigationPath.equalsIgnoreCase("")){
			pageView = mContext.getString(navigationPrefix) + "_" + navigationPath + "/" + name.replace(" ", "_");	
		} else {
			pageView = mContext.getString(navigationPrefix) + "_" + name.replace(" ", "_");
		}
		

		Log.d(TAG, "trackProduct pageView = " + pageView);
		mTracker.sendView(pageView);

		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gpdv);

		Log.d(TAG, "trackProduct: category = " + category + " action = " + action + " sku = " + sku);
		mTracker.sendEvent(category, action, sku, 0l);
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
		if (!isEnabled) {
			return;
		}

		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gaddtocart);

		Log.d(TAG, "trackProduct: category = " + category + " action = " + action + " sku = " + sku + " price = "
				+ price);
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
		if (!isEnabled) {
			return;
		}

		if (items == null || items.size() == 0) {
			return;
		}
		
		
		Log.i(TAG, "code1track value "+value);
		Double valueDouble = CurrencyFormatter.getValueDouble(value.trim());
		long valueAsLongMicro = (long) (valueDouble * MICRO_MULTI);
		String currencyCode = CurrencyFormatter.getCurrencyCode();

		Transaction transaction = new Transaction.Builder(orderNr, valueAsLongMicro).setCurrencyCode(currencyCode)
				.build();

		for (PurchaseItem item : items) {
			long itemValueAsLongMicro = (long) (item.paidpriceAsDouble * MICRO_MULTI);
			long quantity = item.quantityAsInt;
			transaction.addItem(new Transaction.Item.Builder(item.sku, item.name, itemValueAsLongMicro, quantity)
					.setProductCategory(item.category).build());
		}

		mTracker.sendTransaction(transaction);

		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gfinished);

		isCheckoutStarted = false;
		Log.d(TAG, "trackSales event: category = " + category + " action = " + action + " orderNr = " + orderNr
				+ " value = " + valueDouble.longValue());
		mTracker.sendEvent(category, action, orderNr, valueDouble.longValue());
	}

	public void trackShare(Context context, String sku, String user_id, String shop_country ){
		if (!isEnabled) {
			return;
		}
		
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.gsocialshare);
		mTracker.sendSocial(category, action, sku);
	}
	
	public void trackCheckoutStart(Context context, String user_id){
		if (!isEnabled) {
			return;
		}
		
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutstart);
		mTracker.sendSocial(category, action, user_id);
	}
	
	public void trackCheckoutContinueShopping(Context context, String user_id){
		if (!isEnabled) {
			return;
		}
		
		String category = mContext.getString(R.string.gcheckout);
		String action = mContext.getString(R.string.gcheckoutcontinueshopping);
		mTracker.sendSocial(category, action, user_id);
	}
	
	public void trackRateProduct(Context context, String sku, Long value, String rating_type){
		if (!isEnabled) {
			return;
		}
		
		String category = mContext.getString(R.string.gcatalog);
		String action = mContext.getString(R.string.grateproduct)+rating_type;
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
	
}
