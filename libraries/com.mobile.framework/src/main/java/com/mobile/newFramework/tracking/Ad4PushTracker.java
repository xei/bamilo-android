/**
 * 
 */
package com.mobile.newFramework.tracking;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Cart;
import com.ad4screen.sdk.analytics.Item;
import com.ad4screen.sdk.analytics.Lead;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobile.framework.R;
import com.mobile.newFramework.database.CategoriesTableHelper;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DateTimeUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @author nunocastro
 * @modified sergiopereira
 */
public class Ad4PushTracker {

    private final static String TAG = Ad4PushTracker.class.getSimpleName();

    private boolean isEnabled = false;

    private static Ad4PushTracker sInstance;

    private A4S mA4S;

    private Context mContext;

    private static final String AD4PUSH_PREFERENCES = "Ad4PushPreferences";

    private static final String VIEW_STATE = "view";

    private static final String PURCHASES_COUNTER = "aggregatedNumberOfPurchases";
    private static final String PURCHASES_SUM_VALUE = "aggregatedValueOfPurchases";
    private static final String WISHLIST_NUMBER = "aggregatedNumberOfWishlistItems";

    private static final String STATUS_IN_APP = "statusInApp";
    private static final String SHARED_PRODUCT_COUNT = "shareCount";
    private static final String WISHLIST_STATUS = "wishlistStatus";
    private static final String WISHLIST_DATE = "lastFavouritesProductDate";
    private static final String WISHLIST_PRODUCT = "lastFavouritesProduct";
    private static final String SHOP_COUNTRY = "shopCountry";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String ORDER_STATUS = "orderStatus";
    private static final String CHECKOUT_STARTED = "started";
    private static final String CHECKOUT_FINISHED = "done";
    private static final String USER_ID = "userID";
    private static final String USER_FIRST_NAME = "firstName";
    private static final String USER_GENDER = "userGender";
    private static final String USER_DOB = "userDOB";
    private static final String REGISTRATION = "registrationStatus";
    private static final String REGISTRATION_STARTED = "started";
    private static final String REGISTRATION_DONE = "done";
    private static final String LAST_SEARCH = "lastSearch";
    private static final String LAST_SEARCH_DATE = "lastSearchDate";
    private static final String PURCHASE_GRAND_TOTAL = "purchaseGrandTotal";
    private static final String CART_COUNTER = "cartStatus";
    private static final String CART_VALUE = "cartValue";
    private static final String CART_AVERAGE_VALUE = "avgCartValue";
    private static final String PURCHASE_COUPON_STATUS = "couponStatus";
    private static final String PURCHASE_LAST_DATE = "lastOrderDate";
    private static final String FAVORITES_CART_COUNT = "lastMovedFromFavtoCart";
    private static final String MOST_VISITED_CATEGORY = "mostVisitedCategory";
    private static final String CAMPAIGN_PAGEVIEW_COUNT = "campaignPageViewCount";
    private static final String REVIEW_COUNT = "reviewCount";
    private static final String PRE_INSTALL = Constants.INFO_PRE_INSTALL;
    private static final String BRAND = Constants.INFO_BRAND;
    private static final String SIM_OPERATOR = Constants.INFO_SIM_OPERATOR;
    // private static final String VERSION = Constants.INFO_BUNDLE_VERSION;

    private static final String FILTER_BRAND = "filterBrand";
    private static final String FILTER_COLOR = "filterColor";
    private static final String FILTER_CATEGORY = "filterCategory";
    private static final String FILTER_PRICE = "filterPrice";
    private static final String FILTER_SIZE = "filterSize";

    private static final String FILTER_BRAND_KEY = "searchQuery";
    private static final String FILTER_COLOR_KEY = "color_family";
    private static final String FILTER_PRICE_KEY = "price";
    private static final String FILTER_CATEGORY_KEY = "productUrl";
    private static final String FILTER_SIZE_KEY = "size";

    private static final String STATUS_PROSPECT = "Prospect";
    private static final String STATUS_CUSTOMER = "Customer";

    private static final int EVENT_LOGIN = 1001;
    private static final int EVENT_FACEBOOK_CONNECT = 1002;
    private static final int EVENT_FIRST_OPEN_APP = 1003;
    // private static final int EVENT_ADD_TO_WHISHLIST = 1005;

    private static final String HAS_OPENED_APP = "app_opened";

    // View States for In-App Messages
    private static final String HOME_VIEW = "HOME";
    private static final String CATEGORY_VIEW = "CATEGORY";
    private static final String PRODUCT_VIEW = "PRODUCT";
    private static final String LOGIN_SIGNUP_VIEW = "ACCOUNT";
    private static final String FAVORITES_VIEW = "MYFAVORITES";
    private static final String CART_VIEW = "CART";

    private static final String IS_ENABLED = "Enabled";
    private static final String AD4PUSH_PREFERENCES_PERSIST = "Ad4PushPreferencesPersist";

    HashMap<TrackingPage, String> screens;

    private Handler handler;

    /**
     * Get singleton instance of Ad4PushTracker.
     * 
     * @return Ad4PushTracker
     * @author sergiopereira
     */
    public static Ad4PushTracker get() {
        return sInstance == null ? sInstance = new Ad4PushTracker() : sInstance;
    }

    /**
     * Startup the Ad4PushTracker.
     * 
     * @param context
     * @author sergiopereira
     */
    public static void startup(Context context) {
        Print.d(TAG, "ON STARTUP");
        sInstance = new Ad4PushTracker(context);
    }

    /**
     * Empty constructor.
     * 
     * @author sergiopereira
     */
    private Ad4PushTracker() {
        isEnabled = false;
    }

    /**
     * Constructor.
     * 
     * @param context The aplication context
     * @author sergiopereira
     */
    private Ad4PushTracker(Context context) {
        // Get enable flag
        isEnabled = context.getResources().getBoolean(R.bool.ad4push_enabled);
        // Save context
        mContext = context;
        // Create screen map
        screens = new HashMap<>();
        screens.put(TrackingPage.HOME, HOME_VIEW);
        screens.put(TrackingPage.PRODUCT_LIST, CATEGORY_VIEW);
        screens.put(TrackingPage.PRODUCT_DETAIL, PRODUCT_VIEW);
        screens.put(TrackingPage.LOGIN_SIGNUP, LOGIN_SIGNUP_VIEW);
        screens.put(TrackingPage.FAVORITES, FAVORITES_VIEW);
        screens.put(TrackingPage.CART, CART_VIEW);
        screens.put(TrackingPage.REGISTRATION, REGISTRATION);
        // Get A4S
        init();
    }

    /**
     * Initialize Ad4S.
     * 
     * @author sergiopereira
     */
    private void init() {
        // Create screen map
        if (isEnabled) {
            Print.i(TAG, "Ad4PSUH Startup -> INITITALIZED");
            mA4S = A4S.get(mContext);
            boolean isActive = getActiveAd4Push(mContext);
            // setPushNotificationLocked(!isActive);
            // setInAppDisplayLocked(!isActive);
            // stopingSDK(mContext,!isActive);
            setGCMEnabled(isActive);
            setInAppDisplayLocked(!isActive);
            
        }
    }

    /**
     * ####### BASE #######
     */

    /**
     * 
     * @param activity
     */
    public void startActivity(Activity activity) {
        if (null != mA4S && isEnabled) {
            Print.i(TAG, "Started Activity -> " + activity.getLocalClassName());
            mA4S.startActivity(activity);
        }
    }

    /**
     * 
     * @param activity
     */
    public void stopActivity(Activity activity) {
        if (null != mA4S && isEnabled) {
            Print.i(TAG, "Stopped Activity -> " + activity.getLocalClassName());
            mA4S.stopActivity(activity);
        }
    }

    /**
     * Mark this activity to receive in app messages from Ad4Push service.
     * 
     * @param activity
     * @author sergiopereira
     */
    public void startActivityForInAppMessages(Activity activity) {
        if (null != mA4S && isEnabled) {
            Print.d(TAG, "ON START ACTIVITY ONLY FOR IN-APP MSG: " + activity.getLocalClassName());
            startActivity(activity);
            setPushNotificationLocked(true);
        } else
            Print.w(TAG, "WARNING: A4S IS NULL OR IS DISABLED");
    }

    /**
     * Lock or unlock the push notifications.
     * 
     * @param bool
     * @author sergiopereira
     */
    public void setPushNotificationLocked(boolean bool) {
        if (null != mA4S && isEnabled) {
            Print.d(TAG, "LOCK PUSH NOTIFICATIONS: " + bool);
            mA4S.setPushNotificationLocked(bool);
        }
    }

    /**
     * Lock or unlock the in-app messages.
     * 
     * @param bool
     * @author sergiopereira
     */
    public void setInAppDisplayLocked(boolean bool) {
        if (null != mA4S && isEnabled) {
            Print.d(TAG, "LOCK IN APP MESSAGE: " + bool);
            mA4S.setInAppDisplayLocked(bool);
        }
    }

    private void trackView(String view) {
        if (null != mA4S && isEnabled) {
            Print.d(TAG, "View state tracked -> " + VIEW_STATE + "=" + view);
            mA4S.putState(VIEW_STATE, view);
        }
    }

    /**
     * Stop all services from ad4push SDK
     * 
     * @param context
     * @param isToStop
     */
    private void stopingSDK(Context context, boolean isToStop) {
        if (null != mA4S) {
            Print.d(TAG, "Stop SDK:" + isToStop);
            mA4S.setDoNotTrackEnabled(context, isToStop);
        }
    }

    /**
     * Enables or disables GCM Push notifications for this device.
     * 
     * @param enabled
     *            - True to enable push and try to register to GCM. False to
     *            unregister from GCM
     */
    private void setGCMEnabled(boolean enabled) {
        if (null != mA4S) {
            Print.d(TAG, "setGCMEnabled:" + enabled);
            mA4S.setGCMEnabled(enabled);
        }
    }

    /**
     * Clear the shared prefs.
     * 
     * @param context
     * @author sergiopereira
     */
    public static void clearAllSavedData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    /*
     * ############## TRACKING ##############
     */

    /**
     * Track a empty user id if not has credentials for auto login.
     * 
     * @param userNeverLoggedIn
     * @author sergiopereira
     */
    public void trackEmptyUserId(boolean userNeverLoggedIn) {
        if (isEnabled && userNeverLoggedIn) {
            Print.i(TAG, "USER NEVER LOGGED: TRACK EMPTY USER ID");
            Bundle prefs = new Bundle();
            prefs.putString(USER_ID, "0");
            mA4S.updateDeviceInfo(prefs);
        }
    }

    /**
     * Method used to set some info about device.
     * 
     * @see {@link Constants} used for device info.
     * @author sergiopereira
     */
    private void setDeviceInfo(Bundle info) {
        if (null != mA4S && isEnabled) {
            // Get data from bundle
            Bundle bundle = new Bundle();
            bundle.putBoolean(PRE_INSTALL, info.getBoolean(Constants.INFO_PRE_INSTALL));
            bundle.putString(BRAND, info.getString(Constants.INFO_BRAND));
            bundle.putString(SIM_OPERATOR, info.getString(Constants.INFO_SIM_OPERATOR));
            mA4S.updateDeviceInfo(bundle);
            Print.i(TAG, "SET DEVICE INFO: " + bundle.toString());
        }
    }

    /**
     * First open
     * 
     * @param info
     */
    public void trackAppFirstOpen(Bundle info) {
        if (isEnabled) {
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            boolean alreadyOpened = settings.getBoolean(HAS_OPENED_APP, false);

            if (!alreadyOpened) {
                String currentDateAndTime = DateTimeUtils.getCurrentDateTime();
                mA4S.trackEvent(EVENT_FIRST_OPEN_APP, "firstOpenDate=" + currentDateAndTime);
                alreadyOpened = true;
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(HAS_OPENED_APP, alreadyOpened);
                editor.apply();
            }
        }

        // Set some info
        setDeviceInfo(info);
    }

    public void trackFacebookConnect(String customerId) {
        if (isEnabled) {
            mA4S.trackEvent(EVENT_FACEBOOK_CONNECT, "loginUserID=" + customerId);
        }
    }

    /**
     * Track login.
     * 
     * @param customerId
     * @param customerName
     */
    public void trackLogin(String customerId, String customerName, String customerDob, String gender) {
        if (isEnabled) {
            // Get status in app
            String userStatus = statusInApp();
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putString(STATUS_IN_APP, userStatus);
            prefs.putString(USER_ID, customerId);
            prefs.putString(USER_FIRST_NAME, customerName);
            prefs.putString(USER_DOB, customerDob);
            prefs.putString(USER_GENDER, gender);
            
            mA4S.updateDeviceInfo(prefs);
            // Track event
            mA4S.trackEvent(EVENT_LOGIN, "loginUserID=" + customerId);
            Print.d(TAG, "TRACK LOGIN: " + prefs.toString());
        }
    }

    /**
     * Get the current status in application.
     * 
     * @return String
     */
    private String statusInApp() {
        SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        String userStatus = settings.getString(STATUS_IN_APP, null);
        if (TextUtils.isEmpty(userStatus)) {
            userStatus = STATUS_PROSPECT;
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(STATUS_IN_APP, userStatus);
            editor.apply();
        }
        return userStatus;
    }

    /**
     * Track signup started.
     */
    public void trackSignupStarted() {
        if (isEnabled) {
            Print.i(TAG, "TRACK SIGNUP: STARTED");
            Bundle prefs = new Bundle();
            prefs.putString(REGISTRATION, REGISTRATION_STARTED);
            mA4S.updateDeviceInfo(prefs);
        }
    }

    /**
     * Track register and guest signup.
     * 
     * @param customerId
     * @param customerGender
     * @param customerName
     */
    public void trackSignup(String customerId, String customerGender, String customerName, String customerDob) {
        if (isEnabled) {
            Lead lead = new Lead("Registration done with customer ID", customerId);
            mA4S.trackLead(lead);
            // Get status in application
            String userStatus = statusInApp();
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putString(REGISTRATION, REGISTRATION_DONE);
            prefs.putString(USER_ID, customerId);
            prefs.putString(USER_GENDER, customerGender);
            prefs.putString(USER_FIRST_NAME, customerName);
            prefs.putString(USER_DOB, customerDob);
            prefs.putString(STATUS_IN_APP, userStatus);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK SIGNUP: " + prefs.toString());
        }
    }

    /**
     * Track checkout started.
     * 
     * @param cartQt
     * @param cartValue
     */
    public void trackCheckoutStarted(int cartQt, double cartValue) {
        if (isEnabled) {
            Bundle prefs = new Bundle();
            prefs.putString(ORDER_STATUS, CHECKOUT_STARTED);
            prefs.putDouble(CART_VALUE, cartValue);
            prefs.putInt(CART_COUNTER, cartQt);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK CHECKOUT STARTED: " + prefs.toString());
        }
    }

    public void trackCheckoutEnded(String transactionId, Double grandTotal, Double cartValue, Double average, int orderCount, String coupon) {

        // String currency = CurrencyFormatter.getCurrencyCode();
        String currency = CurrencyFormatter.EURO_CODE;

        if (isEnabled) {
            Print.d(TAG, "trackBuyNowPurchase: grandTotal = " + grandTotal + " cartValue = " + cartValue + " currency = " + currency);
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);

            // Get purchases data
            int purchasesNumber = settings.getInt(PURCHASES_COUNTER, 0);
            double ordersSum = settings.getFloat(PURCHASES_SUM_VALUE, 0);
            // Add the new purchase value
            ordersSum += grandTotal;
            // Save purchases data
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(STATUS_IN_APP, STATUS_CUSTOMER);
            editor.putInt(PURCHASES_COUNTER, ++purchasesNumber);
            editor.putFloat(PURCHASES_SUM_VALUE, (float) ordersSum);
            editor.apply();

            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putString(ORDER_STATUS, CHECKOUT_FINISHED);
            prefs.putString(STATUS_IN_APP, STATUS_CUSTOMER);
            // Cart
            prefs.putInt(CART_COUNTER, orderCount);
            prefs.putDouble(CART_VALUE, cartValue);
            prefs.putDouble(CART_AVERAGE_VALUE, average);
            // Order
            prefs.putDouble(PURCHASE_GRAND_TOTAL, grandTotal);
            prefs.putString(PURCHASE_LAST_DATE, DateTimeUtils.getCurrentDateTime());
            prefs.putString(PURCHASE_COUPON_STATUS, coupon);
            prefs.putDouble(PURCHASES_SUM_VALUE, ordersSum);
            prefs.putInt(PURCHASES_COUNTER, purchasesNumber);
            // Clean other values
            prefs.putInt(FAVORITES_CART_COUNT, 0);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK CHECKOUT ENDED: " + prefs.toString());
            // Purchase purchase = new Purchase(transactionId,
            // CurrencyFormatter.getCurrencyCode(), cartValue);
            // mA4S.trackPurchase(purchase);
        }
    }

    /**
     * Track the add item to favorites.
     * 
     * @param productSKU
     */
    public void trackAddToFavorites(String productSKU) {
        if (isEnabled) {
            // Get count
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int wishlistNumber = settings.getInt(WISHLIST_NUMBER, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(WISHLIST_NUMBER, ++wishlistNumber);
            editor.apply();
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putInt(WISHLIST_STATUS, wishlistNumber);
            prefs.putString(WISHLIST_DATE, DateTimeUtils.getCurrentDateTime());
            prefs.putString(WISHLIST_PRODUCT, productSKU);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK ADD TO FAV: " + prefs.toString());
        }
    }

    /**
     * Track the remove item from favorites.
     * 
     * @param productSKU
     */
    public void trackRemoveFromWishlist(String productSKU) {
        if (isEnabled) {
            // Get count
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int wishlistNumber = settings.getInt(WISHLIST_NUMBER, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(WISHLIST_NUMBER, --wishlistNumber);
            editor.apply();
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putInt(WISHLIST_STATUS, wishlistNumber);
            prefs.putString(WISHLIST_DATE, DateTimeUtils.getCurrentDateTime());
            prefs.putString(WISHLIST_PRODUCT, productSKU);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK REMOVE FROM FAV: " + prefs.toString());
        }
    }

    /**
     * Track the add item to cart from favorites.
     * 
     * @param sku
     * @param price
     * @param name
     * @param category
     */
    public void trackAddToCartFromFav(String sku, double price, String name, String category) {
        if (isEnabled) {
            // Get the number of favorites
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int wishlistNumber = settings.getInt(WISHLIST_NUMBER, 0);
            // Track add to cart from fav
            Bundle prefs = new Bundle();
            prefs.putInt(FAVORITES_CART_COUNT, wishlistNumber);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK ADD TO CART FROM FAV: " + prefs.toString());
            // Track add to cart
            trackAddToCart(sku, price, name, category);
        }
    }

    /**
     * Track the add item to cart.
     * 
     * @param sku
     * @param price
     * @param name
     * @param category
     */
    public void trackAddToCart(String sku, double price, String name, String category) {
        if (isEnabled) {
            Print.i(TAG, "trackAddToCart: productSKU = " + sku);
            String currency = CurrencyFormatter.getCurrencyCode();
            Item productAdded = new Item(sku, name, category, currency, price, 1);
            Cart cart = new Cart("1", productAdded);
            mA4S.trackAddToCart(cart);
        }
    }

    /**
     * Track share counter.
     */
    public void trackSocialShare() {
        if (isEnabled) {
            // Get from prefs
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int shareNumber = settings.getInt(SHARED_PRODUCT_COUNT, 0);
            // Increment and save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(SHARED_PRODUCT_COUNT, ++shareNumber);
            editor.apply();
            // Track
            Bundle prefs = new Bundle();
            prefs.putInt(SHARED_PRODUCT_COUNT, shareNumber);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK SHARE COUNTER: " + prefs.toString());
        }
    }

    /**
     * Track review counter.
     */
    public void trackReviewCounter() {
        if (isEnabled) {
            // Get from prefs
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int shareNumber = settings.getInt(REVIEW_COUNT, 0);
            // Increment and save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(REVIEW_COUNT, ++shareNumber);
            editor.apply();
            // Track
            Bundle prefs = new Bundle();
            prefs.putInt(REVIEW_COUNT, shareNumber);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK REVIEW COUNTER: " + prefs.toString());
        }
    }

    /**
     * Track shop country.
     */
    public void trackShopCountry() {
        if (isEnabled) {
            // Get shop country code
            String shopCountryCode = ShopSelector.getShopId();
            // Get country code
            String countryCode = DeviceInfoHelper.getNetworkCountryIso(mContext);
            if (TextUtils.isEmpty(countryCode))
                countryCode = DeviceInfoHelper.getSimCountryIso(mContext);
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putString(SHOP_COUNTRY, shopCountryCode);
            if (!TextUtils.isEmpty(countryCode))
                prefs.putString(COUNTRY_CODE, countryCode);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK SHOP COUNTRY: " + prefs.toString());
        }
    }

    public void trackSearch(String searchTerm) {
        if (isEnabled) {
            String currentDateAndTime = DateTimeUtils.getCurrentDateTime();
            Bundle prefs = new Bundle();
            prefs.putString(LAST_SEARCH, searchTerm);
            prefs.putString(LAST_SEARCH_DATE, currentDateAndTime);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK SEARCH: " + prefs.toString());
        }
    }

    /**
     * Track the top selected category
     */
    public void trackCategorySelection() {
        if (isEnabled) {
            try {
                Bundle prefs = new Bundle();
                String category = new String(CategoriesTableHelper.getTopCategory().getBytes(), "UTF-8");
                prefs.putString(MOST_VISITED_CATEGORY, category);
                mA4S.updateDeviceInfo(prefs);
                Print.i(TAG, "TRACK TOP CATEGORY: " + prefs.toString());
            } catch (InterruptedException | UnsupportedEncodingException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Track catalog filters.
     * 
     * @param filters
     * @author sergiopereira
     */
    public void trackCatalogFilter(ContentValues filters) {
        if (isEnabled) {
            String brand = filters.getAsString(FILTER_BRAND_KEY);
            String color = filters.getAsString(FILTER_COLOR_KEY);
            String categ = filters.getAsString(FILTER_CATEGORY_KEY);
            String price = filters.getAsString(FILTER_PRICE_KEY);
            String size = filters.getAsString(FILTER_SIZE_KEY);
            Bundle prefs = new Bundle();
            prefs.putString(FILTER_BRAND, TextUtils.isEmpty(brand) ? "" : brand);
            prefs.putString(FILTER_COLOR, TextUtils.isEmpty(color) ? "" : color);
            prefs.putString(FILTER_CATEGORY, TextUtils.isEmpty(categ) ? "" : categ);
            prefs.putString(FILTER_PRICE, TextUtils.isEmpty(price) ? "" : price);
            prefs.putString(FILTER_SIZE, TextUtils.isEmpty(size) ? "" : size);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK CATALOG FILTER: " + prefs.toString());
        }
    }

    public void trackCampaignsView() {
        if (isEnabled) {
            SharedPreferences settings = mContext.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
            int campaignNumber = settings.getInt(CAMPAIGN_PAGEVIEW_COUNT, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(CAMPAIGN_PAGEVIEW_COUNT, ++campaignNumber);
            editor.apply();

            Bundle prefs = new Bundle();
            prefs.putInt(CAMPAIGN_PAGEVIEW_COUNT, campaignNumber);

            mA4S.updateDeviceInfo(prefs);

        }
    }

    public void trackScreen(TrackingPage screen) {
        if (null != screens && screens.containsKey(screen)) {
            if (screen == TrackingPage.REGISTRATION)
                trackSignupStarted();
            else
                trackView(screens.get(screen));
        }
    }

    /**
     * Track the new cart.
     * 
     * @param cartValue
     * @author sergiopereira
     */
    public void trackCart(double cartValue, int cartCount) {
        if (isEnabled) {
            Bundle prefs = new Bundle();
            prefs.putDouble(CART_VALUE, cartValue);
            prefs.putInt(CART_COUNTER, cartCount);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK CART VALUE: " + prefs.toString());
        }
    }



    /**
     *
     * @param context
     * @return
     */
    public static boolean getActiveAd4Push(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES_PERSIST, Context.MODE_PRIVATE);
        return settings.getBoolean(IS_ENABLED, context.getResources().getBoolean(R.bool.ad4push_enabled));
    }

    /**
     *
     * @param context
     * @param isActive
     */
    public static void setActiveAd4Push(Context context, boolean isActive) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES_PERSIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_ENABLED, isActive);
        editor.apply();
        Ad4PushTracker.startup(context);
    }

    /**
     * function responsible of storing GPS id in the Accengage database
     */
    public void storeGaIdOnAccengage() {
        // Case not enabled
        if (!isEnabled) return;
        // 
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                    String id = adInfo.getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("gps_adid", id);
                    mA4S.updateDeviceInfo(bundle);
                } catch (IllegalStateException | GooglePlayServicesRepairableException | IOException | GooglePlayServicesNotAvailableException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }
}
