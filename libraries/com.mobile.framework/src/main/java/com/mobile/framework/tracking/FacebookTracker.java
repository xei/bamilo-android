package com.mobile.framework.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AppEventsConstants;
import com.facebook.AppEventsLogger;
import com.mobile.framework.utils.CurrencyFormatter;


/**
 * Class used for tracking via Facebook.
 * @author sergiopereira
 */
public class FacebookTracker {

    private static final String TAG = FacebookTracker.class.getSimpleName();

    private static FacebookTracker sFacebookTracker;
    
    private AppEventsLogger mFacebookLogger;

    /**
     * Constructor.
     * @param context
     * @author sergiopereira
     */
    public FacebookTracker(Context context) {
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
    
    private void trackItem(String event, String sku, double price, String category) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, sku);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, category);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
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
     * @param category - the product category
     * @author sergiopereira
     */
    public void trackAddedToCart(String sku, double price, String category) {
        Log.i(TAG, "TRACK ADD TO CART: " + sku + " " + price);
        trackItem(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, sku, price, category);
    }
    
    /**
     * Track added item to wishlist.
     * @param sku
     * @param price
     * @param category
     * @author sergiopereira
     */
    public void trackAddedToWishlist(String sku, double price, String category) {
        Log.i(TAG, "TRACK ADD TO WISHLIST: " + sku + " " + price);
        trackItem(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, sku, price, category);
    }
    
    /**
     * Track a product detail.
     * @param sku
     * @param price
     * @param category
     * @author sergiopereira
     */
    public void trackProduct(String sku, double price, String category) {
        Log.i(TAG, "TRACK PRODUCT DETAIL: " + sku + " " + price);
        trackItem(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, sku, price, category);
    }
    
    /**
     * Track checkout started.
     * @param userId
     * @param total
     * @param numItems
     * @author sergiopereira
     */
    public void trackCheckoutStarted(String userId, double total, int numItems) {
        Log.i(TAG, "TRACK CHECKOUT STARTED: " + total + " " + numItems);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, userId);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
        logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, total, params);
    }
    
    /**
     * Track checkout finished.
     * @param order
     * @param total
     * @param numItems
     * @author sergiopereira
     */
    public void trackCheckoutFinished(String order, double total, int numItems) {
        Log.i(TAG, "TRACK CHECKOUT FINISHED: " + total + " " + numItems);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, order);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyFormatter.EURO_CODE);
        logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, total, params);
    }
    
    /**
     * Track rating.
     * @param sku
     * @param category
     * @param rateValue
     * @param maxRate
     * @author sergiopereira
     */
    public void trackRated(String sku, String category, double rateValue, int maxRate) {
        Log.i(TAG, "TRACK RATE: " + sku + " " + rateValue);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, sku);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, category);
        params.putInt(AppEventsConstants.EVENT_PARAM_MAX_RATING_VALUE, maxRate);
        logEvent(AppEventsConstants.EVENT_NAME_RATED, rateValue, params);
    }
    
    /**
     * Track search.
     * @param query
     * @author sergiopereira
     */
    public void trackSearched(String query) {
        Log.i(TAG, "TRACK SEARCH: " + query);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, query);
        logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);        
    }
    
}
