package com.bamilo.android.framework.service.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.bamilo.android.R;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.checkout.PurchaseItem;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;

import java.util.List;
import java.util.regex.PatternSyntaxException;

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
public class AnalyticsGoogle extends AbcBaseTracker {

    private static final String TAG = AnalyticsGoogle.class.getSimpleName();

    private static AnalyticsGoogle sInstance;

    private final static int PRE_INSTALL_ID = 1;

    private final static int SIM_OPERATOR_ID = 2;

    private GoogleAnalytics mAnalytics;

    private Tracker mTracker;

    private Context mContext;

    private String mCurrentKey;

    private boolean isEnabled;

    private String mUtmCampaign = DONT_SEND;

    private String mUtmMedium = DONT_SEND;

    private String mUtmSource = DONT_SEND;

    private String mUtmContent = DONT_SEND;

    private String mUtmTerm = DONT_SEND;

    private Bundle mCustomData;

    private static boolean isCheckoutStarted = false;

    private static final long NO_VALUE = -1;

    private static final String DONT_SEND = "dontSendParameter";

    /**
     * Startup GA
     */
    public static void startup(Context context) {
        sInstance = new AnalyticsGoogle(context);
    }

    /**
     * Gets the current instance of the analytics object to use.
     * @return the global {@link GoogleAnalytics} singleton object, creating one
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
     */
    private AnalyticsGoogle(Context context) {
        // Save data
        mContext = context;
        // Validation
        isEnabled = mContext.getResources().getBoolean(R.bool.ga_enable);
        if (!isEnabled) return;
        // Get instance
        mAnalytics = GoogleAnalytics.getInstance(mContext);
        // Load live and test key
        loadKeys();
        // Set key
        updateTracker();
        // Enable Display Advertising features
        enableAdvertisingCollection(context);

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
     * Load keys from saved preferences
     * @author sergiopereira
     */
    private void loadKeys() {
        // Load keys
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mCurrentKey = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
    }

    /**
     * Update the tracker using the current key
     * @author sergiopereira
     */
    private void updateTracker() {
        if (TextUtils.isEmpty(mCurrentKey)) {
            isEnabled = false;
            return;
        }
        mTracker = mAnalytics.newTracker(mCurrentKey);
        mTracker.setAnonymizeIp(true);
    }

    /*
     * ######### BASE TRACKER #########
     */

    @Override
    public String getId() {
        return mTracker != null ? mTracker.get("&tid") : NOT_AVAILABLE;
    }

    @Override
    public void debugMode(@NonNull Context context, boolean enable) {
        if (enable) {
            mAnalytics.setDryRun(true);
            mAnalytics.getLogger().setLogLevel(LogLevel.VERBOSE);
        } else {
            mAnalytics.setDryRun(false);
            mAnalytics.getLogger().setLogLevel(LogLevel.INFO);
        }
    }

    /*
     * ################## BASE GA TRACKING (v4) ##################
     */

    /**
     * Build and send a page
     * @author sergiopereira
     */
    private void trackPage(String path) {
        mTracker.setScreenName(path);

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));

        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send an event.
     * @author sergiopereira
     */
    private void trackEvent(String category, String action, String label, long value) {

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));
        // Only set Value if is a valid Value
        if(value != NO_VALUE){
            builder.setValue(value);
        }
        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send an event.
     * @author sergiopereira
     */
    public void sendEvent(String category, String action, String label, long value) {

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));
        // Only set Value if is a valid Value
        if(value != NO_VALUE){
            builder.setValue(value);
        }

        mTracker.send(builder.build());
    }

    /**
     * Build and send a social action.
     * @author sergiopereira
     */
    private void trackShare(String category, String action, String target) {

        HitBuilders.SocialBuilder builder = new HitBuilders.SocialBuilder()
                .setNetwork(category)
                .setAction(action)
                .setTarget(target)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));

        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send a timing.
     * @author sergiopereira
     */
    private void trackTiming(String category, String name, long milliSeconds, String label) {

        HitBuilders.TimingBuilder builder = new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setValue(milliSeconds)
                .setVariable(name)
                .setLabel(label)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));
        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send a transaction.
     * @author sergiopereira
     */
    private void trackTransaction(String order, long revenue, String currencyCode) {

        HitBuilders.TransactionBuilder builder = new HitBuilders.TransactionBuilder()
                .setTransactionId(order)
                .setRevenue(revenue)
                .setCurrencyCode(currencyCode)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));

        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send a transaction item.
     * @author sergiopereira
     */
    private void trackTransactionItem(String order, String name, String sku, String category, long price, long quantity, String currencyCode) {

        HitBuilders.ItemBuilder builder = new HitBuilders.ItemBuilder()
                .setTransactionId(order)
                .setName(name)
                .setSku(sku)
                .setCategory(category)
                .setPrice(price)
                .setQuantity(quantity)
                .setCurrencyCode(currencyCode)
                .setCustomDimension(PRE_INSTALL_ID, String.valueOf(getCustomData().getBoolean(Constants.INFO_PRE_INSTALL)))
                .setCustomDimension(SIM_OPERATOR_ID, getCustomData().getString(Constants.INFO_SIM_OPERATOR));

        trackGACampaign();

        mTracker.send(builder.build());
    }

    /**
     * Build and send a GA campaign.
     * @author sergiopereira
     */
    protected void trackGACampaign() {
        //setting as empty string or a null object, will show on GA has "not set"
        if(!mUtmCampaign.equals(DONT_SEND)){
            mTracker.set("&cn", mUtmCampaign);
        }
        if(!mUtmSource.equals(DONT_SEND)){
            mTracker.set("&cs", mUtmSource);
        }
        if(!mUtmMedium.equals(DONT_SEND)){
            mTracker.set("&cm", mUtmMedium);
        }
        if(!mUtmContent.equals(DONT_SEND)){
            mTracker.set("&cc", mUtmContent);
        }
        if(!mUtmTerm.equals(DONT_SEND)){
            mTracker.set("&ck", mUtmTerm);
        }
    }

    /**
     * Enable Display Advertising features.
     * @author ricardo
     * @modified sergiopereira
     */
    private void enableAdvertisingCollection(Context context) {
        // Null if theres no ga_id from API
        if(mTracker!= null) mTracker.enableAdvertisingIdCollection(context.getResources().getBoolean(R.bool.ga_advertisingIDCollection));
    }

    /**
     * ################## SPECIFIC TRACKING ##################
     */

    /**
     * Track a page via {@link TrackingPage}.
     * @author sergiopereira
     */
    public void trackPage(TrackingPage page) {
        // Validate
        if (!isEnabled) return;
        if (page.getName() != IntConstants.INVALID_POSITION) {
            // Get and send page
            String path = mContext.getString(page.getName());
            trackPage(path);
        }
    }

    /**
     * Track an event via {@link TrackingEvent}.
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
     * Specific function to track purchase flow from home page teasers
     */
    public void trackBannerFlowPurchase(String category, int actionInt, String label, long value) {
        // Validation
        if (!isEnabled) return;
        // Get action
        String action = mContext.getString(actionInt);
        // Tracking
        trackEvent(category, action, label, value);
    }

    /**
     * Event to track the specific teaser and position the user clicked
     */
    public void trackEventBannerClick(int cat, String label, int position) {
        // Validation
        if (!isEnabled) return;
        // Get and send page
        String category = mContext.getString(cat);
        String action = mContext.getString(TrackingEvent.HOME_BANNER_CLICK.getAction());
        if (position != IntConstants.INVALID_POSITION) {
            category = category + "_" + position;
        }
        // Tracking
        trackEvent(category, action, label, NO_VALUE);
    }

    /**
     * Track time
     */
    public void trackLoadTiming(int categoryId, long beginMillis) {
        // Validation
        if (!isEnabled) return;
        // Data
        long milliseconds = System.currentTimeMillis();
        if ( milliseconds < beginMillis || beginMillis <= 0 ) {
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

    public void trackLoadTimingNew(int categoryId, long beginMillis, int nameId, String label) {
        // Validation
        if (!isEnabled) return;
        // Data
        long milliseconds = System.currentTimeMillis();
        if ( milliseconds < beginMillis || beginMillis <= 0 ) {
            return;
        }
        milliseconds = milliseconds - beginMillis;
        //int nameId = R.string.gload;
        // Data
        String category = mContext.getString(categoryId);
        String name = mContext.getString(nameId);
        if (label.trim().compareTo("") == 0) label = "Duration";
        // Track
        trackTiming(category, name, milliseconds, label);
    }

    /**
     *
     */
    public static void clearCheckoutStarted() {
        isCheckoutStarted = false;
    }

    /**
     * Track checkout
     */
    public void trackCheckout(List<PurchaseCartItem> items) {
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
        for (PurchaseCartItem item : items) {
            String sku = item.getConfigSimpleSKU();
            double price = item.getPriceForTracking() * item.getQuantity();
            trackEvent(event, sku, (long) price);
        }
    }

    /**
     * Track product
     */
    public void trackProduct(TrackingEvent event, String navigationPrefix, String navigationPath, String name, String sku, Double price) {
        // Validation
        if (!isEnabled) return;
        // Data
        if(navigationPrefix == null) navigationPrefix = NOT_AVAILABLE;
        String pageView;
        String n = !TextUtils.isEmpty(name) ? name.replace(" ", "_") : NOT_AVAILABLE;
        if(!TextUtils.isEmpty(navigationPath)){
            pageView = navigationPrefix + "_" + navigationPath + "/" + n;
        } else {
            pageView = navigationPrefix + "_" + n;
        }
        trackPage(pageView);
        trackEvent(event, sku, (price != null) ? price.longValue() : 0L);
    }


    /**
     * Track payment
     */
    public void trackPaymentMethod(String email, String action) {
        // Validation
        if (!isEnabled) return;
        // Data
        String category = mContext.getString(R.string.gPaymentMethod);
        trackEvent(category, action, email, 0L);
    }

    /**
     * Tracking the purchase using the EURO currency.
     * @param orderNr number
     * @param cartValue value euro converted
     * @param items list of items
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
        // Track transaction
        trackTransaction(orderNr, (long) cartValue, currencyCode);
        // Track all items
        for (PurchaseItem item : items)
            trackTransactionItem(orderNr, item.name, item.sku, item.category, (long) item.getPriceForTracking(), (long) item.quantity, currencyCode);
        // Event
        trackEvent(TrackingEvent.CHECKOUT_FINISHED, orderNr, (long) cartValue);
    }


    /**
     * Track share
     */
    public void trackShare(String sku){
        // Validate
        if (!isEnabled) return;
        // Get data
        String category = mContext.getString(R.string.gcatalog);
        String action = mContext.getString(R.string.gsocialshare);
        trackShare(category, action, sku);
    }

    /**
     * Track rate
     */
    public void trackRateProduct(String sku, Long value, String ratingLabel){
        // Validate
        if (!isEnabled) return;
        // Data
        String category = mContext.getString(R.string.gcatalog);
        String action = mContext.getString(R.string.grateproduct) + ratingLabel;
        trackEvent(category, action, sku, value);
    }

    /**
     * Track a page from a string value.
     */
    public void trackGenericPage(String page){
        // Validate
        if (!isEnabled) return;
        // Data
        trackPage(page);
    }

    /**
     * Share the app
     */
    public void trackShareApp(TrackingEvent event, String label){
        // Validate
        if (!isEnabled) return;
        // Data
        trackEvent(event, label, 0L);
    }

    /**
     * Address creation
     */
    public void trackAddressCreation(TrackingEvent event, String label){
        // Validate
        if (!isEnabled) return;
        // Data
        trackEvent(event, label, 0L);
    }

    /**
     * Track External Link Click
     */
    public void trackEventClickOnExternalLink(TrackingEvent event, String action) {
        // Validation
        if (!isEnabled) return;
        // Get label
        String label = mContext.getString(event.getAction());
        // Get category
        String category = mContext.getString(event.getCategory());
        // Tracking
        trackEvent(category, action, label, NO_VALUE);
    }

    /**
     * Google Analytics "General Campaign Measurement"
     *
     * Method used to create a UTM string with all the info and their constrains.
     *
     * specifications: https://jira.rocket-internet.de/browse/NAFAMZ-13827
     * @param campaignString string sent in the UTM parameter of a push notification
     *
     */
    public void setGACampaign(String campaignString) {
        // Validation
        if (!isEnabled) return;
        // Clean data before every campaign tracking
        mUtmCampaign = "";
        mUtmMedium = "";
        mUtmSource = "";
        mUtmContent = "";
        mUtmTerm = "";
        //campaignString = campaignString.substring(campaignString.indexOf("?"), campaignString.length());

        if (!TextUtils.isEmpty(campaignString)) {
            // Track

            String[] items = campaignString.split("&");
            for(String item :items) {
                String[] terms = item.split("=");
                if (terms.length != 2) continue;

                if (terms[0].toLowerCase().endsWith("utm_campaign")) {
                    mUtmCampaign = terms[1]; //getUtmParameter(campaignString, "utm_campaign=");
                } else if (terms[0].toLowerCase().endsWith("utm_source")) {
                    mUtmSource = terms[1];// getUtmParameter(campaignString, "utm_source=");
                } else if (terms[0].toLowerCase().endsWith("utm_medium")) {
                    mUtmMedium = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                } else if (terms[0].toLowerCase().endsWith("utm_content")) {
                    mUtmContent = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                } else if (terms[0].toLowerCase().endsWith("utm_term")) {
                    mUtmTerm = terms[1]; // getUtmParameter(campaignString, "utm_medium=");
                }
            }
            if (TextUtils.isEmpty(mUtmSource) && !TextUtils.isEmpty(mUtmCampaign)) {
                mUtmSource = "push";
            }
            if (TextUtils.isEmpty(mUtmMedium) && !TextUtils.isEmpty(mUtmCampaign)) {
                mUtmMedium = "referrer";
            }
        }
    }


    /**
     * Functions that receives s string and looks for the value of a specific parameter
     * @return utm parameter
     */
    public String getUtmParameter(String campaignString, String parameter) {
        try{
            String[] separated = campaignString.toLowerCase().split(parameter);
            String afterParameter =separated[1];

            if(afterParameter.contains("&")){
                String[] separatedPost = afterParameter.split("&");
                return separatedPost[0];
            } else {
                return afterParameter;
            }
        } catch (PatternSyntaxException | NullPointerException | IndexOutOfBoundsException e){
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Save the custom data.
     * @author sergiopereira
     */
    public void setCustomData(Bundle data) {
        // Validation
        if (!isEnabled) return;
        // Set device info
        mCustomData = data;
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
