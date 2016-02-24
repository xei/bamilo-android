package com.mobile.newFramework.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ad4screen.sdk.A4S;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobile.framework.R;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.DateTimeUtils;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;

import java.io.IOException;
import java.util.HashMap;

/**
 * Accengage
 * @author nunocastro
 * @modified sergiopereira
 */
public class Ad4PushTracker {

    private final static String TAG = Ad4PushTracker.class.getSimpleName();

    private boolean isEnabled = false;

    private static Ad4PushTracker sInstance;

    private A4S mA4S;

    private Context mContext;

    // First_Open :: User Opens the app for the first time
    private static final String SHOP_COUNTRY = "shopCountry";
    private static final String LANGUAGE_SELECTION = "LanguageSelection";
    private static final String GA_AD_ID = "GaAdId";

    // Last_Open :: Last time the user has opened the app
    private static final String LAST_OPEN_DATE = "Last_Open_Date";

    // Product_view :: Last product seen by the user
    private static final String LAST_VIEWED_CATEGORY_NAME = "lastViewedCategoryName";
    private static final String LAST_VIEWED_CATEGORY_KEY = "lastViewedCategoryKey";
    private static final String LAST_PRODUCT_VIEWED = "LastProductViewed";
    private static final String LAST_SKU_VIEWED = "LastSKUViewed";
    private static final String LAST_BRAND_VIEWED_KEY = "LastBrandViewedKey";
    private static final String LAST_BRAND_VIEWED_NAME = "LastBrandViewedName";

    // Add_to_cart :: User adds a product to cart
    // Remove_from_cart :: User removes a product from Cart
    private static final String CART_STATUS = "cartStatus";
    private static final String CART_VALUE = "cartValue";
    private static final String DATE_LAST_CART_UPDATED = "DateLastCartUpdated";
    private static final String LAST_CART_PRODUCT_NAME = "lastCartProductName";
    private static final String LAST_CART_SKU = "lastCartSKU";
    private static final String LAST_CATEGORY_ADDED_TO_CART_NAME = "lastCategoryAddedToCartName";
    private static final String LAST_CATEGORY_ADDED_TO_CART_KEY = "lastCategoryAddedToCartKey";
    private static final String LAST_BRAND_ADDED_TO_CART_KEY = "LastBrandAddedToCartKey";
    private static final String LAST_BRAND_ADDED_TO_CART_NAME = "LastBrandAddedToCartName";

    // Purchase :: User completes a purchase
    private static final String LAST_ORDER_DATE = "lastOrderDate";
    private static final String AGGREGATED_NUMBER_OF_PURCHASE = "aggregatedNumberOfPurchase";
    private static final String ORDER_NUMBER = "OrderNumber";
    private static final String LAST_BRAND_PURCHASED_KEY = "LastBrandPurchasedKey";
    private static final String LAST_BRAND_PURCHASED_NAME = "LastBrandPurchasedName";
    private static final String LAST_CATEGORY_PURCHASED_NAME = "lastCategoryPurchasedName";
    private static final String LAST_CATEGORY_PURCHASED_KEY = "lastCategoryPurchasedKey";
    private static final String LAST_PRODUCT_NAME_PURCHASED = "lastProductNamePurchased";
    private static final String LAST_SKU_PURCHASED = "lastSKUPurchased";

    // Add_to_favorite :: User adds a product to favorite
    // Remove_from_Favorite :: User removes a product from favorite
    private static final String WISH_LIST_STATUS = "wishlistStatus";
    private static final String LAST_WISHLIST_PRODUCT_NAME = "Last_Wishlist_Product_Name";
    private static final String LAST_BRAND_ADDED_TO_WISHLIST_KEY = "LastBrandAddedToWishlistKey";
    private static final String LAST_BRAND_ADDED_TO_WISHLIST_NAME = "LastBrandAddedToWishlistName";
    private static final String LAST_CATEGORY_ADDED_TO_WISHLIST_NAME = "lastCategoryAddedToWishlistName";
    private static final String LAST_CATEGORY_ADDED_TO_WISHLIST_KEY = "lastCategoryAddedToWishlistKey";
    private static final String LAST_WISHLIST_SKU = "Last_Wishlist_SKU";

    // Login_Registration :: User logs in to the app
    // User_Info_Edit :: User updates his info on the app
    private static final String USER_ID = "userID";
    private static final String FIRST_NAME = "firstName";
    private static final String USER_DOB = "userDOB";
    private static final String USER_GENDER = "userGender";
    private static final String LAST_NAME = "lastName";









    private static final String AD4PUSH_PREFERENCES = "Ad4PushPreferences";
    private static final String VIEW_STATE = "view";
    private static final String REGISTRATION = "registrationStatus";
    private static final String REGISTRATION_STARTED = "started";
    private static final String HAS_OPENED_APP = "app_opened";
    private static final String HOME_VIEW = "HOME";
    private static final String CATEGORY_VIEW = "CATEGORY";
    private static final String PRODUCT_VIEW = "PRODUCT";
    private static final String LOGIN_SIGN_UP_VIEW = "ACCOUNT";
    private static final String FAVORITES_VIEW = "MYFAVORITES";
    private static final String CART_VIEW = "CART";
    private static final String REGISTRATION_VIEW = "REGISTER";
    private static final String IS_ENABLED = "Enabled";
    private static final String AD4PUSH_PREFERENCES_PERSIST = "Ad4PushPreferencesPersist";
    private static final String PUSH_NOTIFICATION_OPENED = "lastPNOpened";

    private static final int EVENT_LOGIN = 1001;
    private static final int EVENT_FACEBOOK = 1002;
    private static final int EVENT_FIRST_OPEN_APP = 1003;


    HashMap<TrackingPage, String> screens;

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
     * @param context The application context
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
        screens.put(TrackingPage.LOGIN_SIGN_UP, LOGIN_SIGN_UP_VIEW);
        screens.put(TrackingPage.FAVORITES, FAVORITES_VIEW);
        screens.put(TrackingPage.CART, CART_VIEW);
        screens.put(TrackingPage.REGISTRATION, REGISTRATION_VIEW);
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
            setGCMEnabled(isActive);
            setInAppDisplayLocked(!isActive);
        }
    }

    private boolean isAvalaible() {
        return isEnabled && mA4S != null;
    }

    /*
     * ####### BASE #######
     */

    /**
     * Lock or unlock the push notifications.
     * @author sergiopereira
     */
    public void setPushNotificationLocked(boolean bool) {
        if (isAvalaible()) {
            Print.d(TAG, "LOCK PUSH NOTIFICATIONS: " + bool);
            mA4S.setPushNotificationLocked(bool);
        }
    }

    /**
     * Lock or unlock the in-app messages.
     * @author sergiopereira
     */
    public void setInAppDisplayLocked(boolean bool) {
        if (isAvalaible()) {
            Print.d(TAG, "LOCK IN APP MESSAGE: " + bool);
            mA4S.setInAppDisplayLocked(bool);
        }
    }

    private void trackView(String view) {
        if (isAvalaible()) {
            Print.d(TAG, "View state tracked -> " + VIEW_STATE + "=" + view);
            mA4S.putState(VIEW_STATE, view);
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
        if (isAvalaible()) {
            Print.d(TAG, "setGCMEnabled:" + enabled);
            mA4S.setGCMEnabled(enabled);
        }
    }

    /**
     * Clear the shared prefs.
     * @author sergiopereira
     */
    public static void clearAllSavedData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    /*
     * ############## TRACKING ##############
     */

    private boolean isFirstAppOpen(@NonNull Context context) {
        // Shared preferences
        SharedPreferences preferences = context.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        // Get
        boolean firstTime = preferences.getBoolean(HAS_OPENED_APP, false);
        // Set
        preferences.edit().putBoolean(HAS_OPENED_APP, true).apply();
        // Return
        return firstTime;
    }

    /**
     * Tracking app open
     */
    public void trackAppOpen() {
        if (isAvalaible()) {
            Bundle bundle = new Bundle();
            if (!isFirstAppOpen(mContext)) {
                bundle.putString(SHOP_COUNTRY, ShopSelector.getCountryCode()); // TODO
                bundle.putString(LANGUAGE_SELECTION, ShopSelector.getCountryCode()); // TODO
                storeGaIdOnAccengage();
                Print.i(TAG, "TRACK FIRST APP OPEN: " + bundle.toString());
            } else {
                bundle.putString(LAST_OPEN_DATE, DateTimeUtils.getCurrentDateTime());
                Print.i(TAG, "TRACK APP OPEN: " + bundle.toString());
            }
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * Last product seen by the user
     */
    public void trackProductView(ProductRegular product, String source, String path) {
        if (isAvalaible()) {
            Bundle bundle = new Bundle();
            bundle.putString(LAST_VIEWED_CATEGORY_NAME, source); // TODO
            bundle.putString(LAST_VIEWED_CATEGORY_KEY, path); // TODO
            bundle.putString(LAST_PRODUCT_VIEWED, product.getName());
            bundle.putString(LAST_SKU_VIEWED, product.getSku());
            bundle.putString(LAST_BRAND_VIEWED_KEY, product.getBrandKey());
            bundle.putString(LAST_BRAND_VIEWED_NAME, product.getBrandName());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    public void trackAddToCart(PurchaseEntity purchase) {
        if (isAvalaible()) {
            Bundle bundle = new Bundle();
            bundle.putInt(CART_STATUS, purchase.getCartCount());
            bundle.putDouble(CART_VALUE, purchase.getPriceForTracking());
            bundle.putString(DATE_LAST_CART_UPDATED, DateTimeUtils.getCurrentDateTime());
            bundle.putString(LAST_CART_PRODUCT_NAME, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_CART_SKU, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_CATEGORY_ADDED_TO_CART_NAME, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_CATEGORY_ADDED_TO_CART_KEY, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_BRAND_ADDED_TO_CART_KEY, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_BRAND_ADDED_TO_CART_NAME, ShopSelector.getCountryCode()); // TODO
            mA4S.updateDeviceInfo(bundle);
        }
    }

    public void trackRemoveFromCart(PurchaseEntity purchase) {
        if (isAvalaible()) {
            Bundle bundle = new Bundle();
            bundle.putInt(CART_STATUS, purchase.getCartCount());
            bundle.putDouble(CART_VALUE, purchase.getPriceForTracking());
            bundle.putString(DATE_LAST_CART_UPDATED, DateTimeUtils.getCurrentDateTime());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * Track the add item to favorites.
     */
    public void trackAddToFavorites(ProductRegular product) {
        if (isAvalaible()) {
            Bundle bundle = new Bundle();
            bundle.putInt(WISH_LIST_STATUS, WishListCache.size());
            bundle.putString(LAST_WISHLIST_SKU, product.getSku());
            bundle.putString(LAST_WISHLIST_PRODUCT_NAME, product.getName());
            bundle.putString(LAST_BRAND_ADDED_TO_WISHLIST_KEY, product.getBrandKey());
            bundle.putString(LAST_BRAND_ADDED_TO_WISHLIST_NAME, product.getBrandName());
            bundle.putString(LAST_CATEGORY_ADDED_TO_WISHLIST_KEY, ShopSelector.getCountryCode()); // TODO
            bundle.putString(LAST_CATEGORY_ADDED_TO_WISHLIST_NAME, ShopSelector.getCountryCode()); // TODO
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * Track the remove item from favorites.
     */
    public void trackRemoveFromWishList() {
        if (isEnabled) {
            Bundle bundle = new Bundle();
            bundle.putString(WISH_LIST_STATUS, ShopSelector.getCountryCode());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * Track login registration.
     */
    public void trackLoginRegistration(Customer customer, boolean facebook) {
        if (isEnabled) {
            // Create bundle
            Bundle prefs = new Bundle();
            prefs.putString(USER_ID, customer.getIdAsString());
            prefs.putString(FIRST_NAME, customer.getFirstName());
            prefs.putString(LAST_NAME, customer.getLastName());
            prefs.putString(USER_DOB, customer.getBirthday());
            prefs.putString(USER_GENDER, customer.getGender());
            mA4S.updateDeviceInfo(prefs);
            // Track event
            mA4S.trackEvent(facebook ? EVENT_FACEBOOK : EVENT_LOGIN, "loginUserID=" + customer.getIdAsString());
            Print.d(TAG, "TRACK LOGIN: " + prefs.toString());
        }
    }

    public void trackPurchase(Double grandTotal, Double cartValue, Double average, int orderCount, String coupon, String attributeIds) {
        String currency = CurrencyFormatter.EURO_CODE;
        if (isEnabled) {
            Print.d(TAG, "trackBuyNowPurchase: grandTotal = " + grandTotal + " cartValue = " + cartValue + " currency = " + currency);
            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putString(LAST_ORDER_DATE, DateTimeUtils.getCurrentDateTime());
            bundle.putString(AGGREGATED_NUMBER_OF_PURCHASE, coupon);
            bundle.putString(ORDER_NUMBER, coupon);
            bundle.putInt(CART_STATUS, 0);
            bundle.putString(LAST_BRAND_PURCHASED_KEY, coupon);
            bundle.putString(LAST_BRAND_PURCHASED_NAME, coupon);
            bundle.putString(LAST_CATEGORY_PURCHASED_KEY, coupon);
            bundle.putString(LAST_CATEGORY_PURCHASED_NAME, coupon);
            bundle.putString(LAST_PRODUCT_NAME_PURCHASED, coupon);
            bundle.putString(LAST_SKU_PURCHASED, coupon);
            mA4S.updateDeviceInfo(bundle);
            Print.i(TAG, "TRACK CHECKOUT ENDED: " + bundle.toString());
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
     * Track the last date an push notification was opened
     */
    public void trackOpenPushNotification() {
        if (isEnabled) {
            String currentDateAndTime = DateTimeUtils.getCurrentDateTime();
            Bundle prefs = new Bundle();
            prefs.putString(PUSH_NOTIFICATION_OPENED, currentDateAndTime);
            mA4S.updateDeviceInfo(prefs);
            Print.i(TAG, "TRACK PUSH NOTIFICATION: " + prefs.toString());
        }
    }

    /**
     *
     */
    public static boolean getActiveAd4Push(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES_PERSIST, Context.MODE_PRIVATE);
        return settings.getBoolean(IS_ENABLED, context.getResources().getBoolean(R.bool.ad4push_enabled));
    }

    /**
     *
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
    private void storeGaIdOnAccengage() {
        // Case not enabled
        if (!isEnabled) return;
        //
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                    String id = adInfo.getId();
                    Bundle bundle = new Bundle();
                    bundle.putString(GA_AD_ID, id);
                    A4S.get(mContext).updateDeviceInfo(bundle);
                } catch (IllegalStateException | GooglePlayServicesRepairableException | IOException | GooglePlayServicesNotAvailableException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }
}