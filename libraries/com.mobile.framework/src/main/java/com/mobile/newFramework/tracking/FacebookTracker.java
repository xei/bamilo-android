package com.mobile.newFramework.tracking;

import android.content.Context;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;


/**
 * Class used for tracking via Facebook.
 * @author sergiopereira
 * @modified Paulo Carvalho
 */
public class FacebookTracker {

    private static final String TAG = FacebookTracker.class.getSimpleName();

    private static final String PRODUCT_CONTENT_TYPE = "product";

    private static final String EVENT_PARAM_VALUE_TO_SUM = "_valueToSum";

    private static final String EVENT_PARAM_CONTENT_CATEGORY= "content_category";

    private static final String EVENT_PARAM_SHOP_COUNTRY = "shop_country";

    private static final String EVENT_PARAM_APP_VERSION = "app_version";

    private static final String EVENT_VIEW_CONTENT_PRODUCT = "fb_mobile_content_view_productDetailPage";

    private static final String EVENT_VIEW_CONTENT_CATEGORY = "fb_mobile_content_view_categoryPage";

    private static FacebookTracker sFacebookTracker;

    private AppEventsLogger mFacebookLogger;

    /**
     * Constructor.
     * @param context
     * @author sergiopereira
     */
    public FacebookTracker(Context context) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(context);
        }
        mFacebookLogger = AppEventsLogger.newLogger(context);


    }

    /**
     * Get singleton instance
     * @param context
     * @return FacebookTracker
     * @author sergiopereira
     */
    public static FacebookTracker get(Context context) {
        return sFacebookTracker == null ? sFacebookTracker = new FacebookTracker(context) : sFacebookTracker;
    }

    /**
     * Startup Tracker.
     * @param context
     * @author sergiopereira
     */
    public static void startup(Context context) {
        sFacebookTracker = new FacebookTracker(context);
    }

    /*
     * ######### BASE #########
     */

    /**
     * Log a simple event.
     * @param event
     * @author sergiopereira
     */
    private void logEvent(String event) {
        mFacebookLogger.logEvent(event);
    }

    /**
     * Log an event with parameters.
     * @param event
     * @author sergiopereira
     */
    private void logEvent(String event, Bundle parameters) {
        mFacebookLogger.logEvent(event, parameters);
    }

    /**
     * Log an event with associated value.
     * @param event
     * @param valueToSum
     * @param parameters
     * @author sergiopereira
     */
    private void logEvent(String event, double valueToSum, Bundle parameters) {
        mFacebookLogger.logEvent(event, valueToSum, parameters);
    }

    /*
     * ######### PRIVATE #########
     */

    private void trackItem(String event, String sku, double price, String shopCountry, String appVersion) {
        Bundle params = new Bundle();
        params.putDouble(EVENT_PARAM_VALUE_TO_SUM, price);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, sku);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
        params.putString(EVENT_PARAM_SHOP_COUNTRY, shopCountry);
        params.putString(EVENT_PARAM_APP_VERSION, appVersion);
        logEvent(event, price, params);
    }

    /*
     * ######### PUBLIC #########
     */

    /**
     * Track the activated app.
     * @author sergiopereira
     */
    public void trackActivatedApp() {
        Log.i(TAG, "TRACK: ACTIVATED APP");
        logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);
    }

    /**
     * Track added item to cart.
     * @param sku - the product sku
     * @param price - the product price
     * @param shopCountry
     * @param appVersion
     * @author sergiopereira
     */
    public void trackAddedToCart(String sku, double price, String shopCountry, String appVersion) {
        Log.i(TAG, "TRACK ADD TO CART: " + sku + " " + price);
        trackItem(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, sku, price, shopCountry, appVersion);
    }

    /**
     * Track Catalog view.
     * @param shopCountry
     * @param appVersion
     * @author sergiopereira
     */
    public void trackCatalogView(String category, String shopCountry, String appVersion) {
        Log.i(TAG, "TRACK CATALOG VIEW: " + category);
        Bundle params = new Bundle();
        params.putString(EVENT_PARAM_CONTENT_CATEGORY, category);
        params.putString(EVENT_PARAM_SHOP_COUNTRY, shopCountry);
        params.putString(EVENT_PARAM_APP_VERSION, appVersion);
        logEvent(EVENT_VIEW_CONTENT_CATEGORY, params);
    }

    /**
     * Track a product detail.
     * @param sku
     * @param price
     * @param shopCountry
     * @param appVersion
     * @author sergiopereira
     */
    public void trackProduct(String sku, double price, String shopCountry, String appVersion) {
        Log.i(TAG, "TRACK PRODUCT DETAIL: " + sku + " " + price);
//      trackItem(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, sku, price, shopCountry, appVersion);
        trackItem(EVENT_VIEW_CONTENT_PRODUCT, sku, price, shopCountry, appVersion);
    }


    /**
     * Track checkout finished.
     * @param skus
     * @param total
     * @param shopCountry
     * @param appVersion
     * @author sergiopereira
     */
    public void trackCheckoutFinished(ArrayList<String> skus, double total, String shopCountry, String appVersion) {
        Log.i(TAG, "TRACK CHECKOUT FINISHED: " + total);
        Bundle params = new Bundle();

        params.putDouble(EVENT_PARAM_VALUE_TO_SUM, total);
        params.putStringArrayList(AppEventsConstants.EVENT_PARAM_CONTENT_ID, skus);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
        params.putString(EVENT_PARAM_SHOP_COUNTRY, shopCountry);
        params.putString(EVENT_PARAM_APP_VERSION, appVersion);
        logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, total, params);
//        mFacebookLogger.logPurchase(BigDecimal.valueOf(total), Currency.getInstance(CurrencyFormatter.EURO_CODE), params);
    }


    //    /**
//     * Track added item to wishlist.
//     * @param sku
//     * @param price
//     * @param shopCountry
//     * @param appVersion
//     * @author sergiopereira
//     */
//    public void trackAddedToWishlist(String sku, double price, String shopCountry, String appVersion) {
//        Log.i(TAG, "TRACK ADD TO WISHLIST: " + sku + " " + price);
//        trackItem(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, sku, price, shopCountry, appVersion);
//    }


//    /**
//     * Track checkout started.
//     * @param userId
//     * @param total
//     * @param numItems
//     * @author sergiopereira
//     */
//    public void trackCheckoutStarted(String userId, double total, int numItems) {
//        Log.i(TAG, "TRACK CHECKOUT STARTED: " + total + " " + numItems);
//        Bundle params = new Bundle();
//        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, userId);
//        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
//        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
//        logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, total, params);
//    }



//    /**
//     * Track rating.
//     * @param sku
//     * @param category
//     * @param rateValue
//     * @param maxRate
//     * @author sergiopereira
//     */
//    public void trackRated(String sku, String category, double rateValue, int maxRate) {
//        Log.i(TAG, "TRACK RATE: " + sku + " " + rateValue);
//        Bundle params = new Bundle();
//        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, sku);
//        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, category);
//        params.putInt(AppEventsConstants.EVENT_PARAM_MAX_RATING_VALUE, maxRate);
//        logEvent(AppEventsConstants.EVENT_NAME_RATED, rateValue, params);
//    }
//
//    /**
//     * Track search.
//     * @param query
//     * @author sergiopereira
//     */
//    public void trackSearched(String query) {
//        Log.i(TAG, "TRACK SEARCH: " + query);
//        Bundle params = new Bundle();
//        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, query);
//        logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
//    }

}
