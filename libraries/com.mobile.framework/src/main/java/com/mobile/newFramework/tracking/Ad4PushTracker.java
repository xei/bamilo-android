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
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.DateTimeUtils;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;

import java.io.IOException;

/**
 * Class used for Accengage tracking
 * @author nunocastro
 * @modified 
 */
public class Ad4PushTracker {

    private final static String TAG = Ad4PushTracker.class.getSimpleName();

    private boolean isEnabled = false;
    private static Ad4PushTracker sInstance;
    private A4S mA4S;
    private Context mContext;

    // Preferences
    private static final String AD4PUSH_PREFERENCES_PERSIST = "Ad4PushPreferencesPersist";
    private static final String AD4PUSH_PREFERENCES = "Ad4PushPreferences";
    private static final String AD4PUSH_PREFS_HAS_OPENED_APP = "app_opened";
    private static final String AD4PUSH_PREFS_IS_ENABLED = "Enabled";

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

    // Views
    private static final String VIEW_STATE = "view";
    private static final String HOME_VIEW = "HOME";
    private static final String CATEGORY_VIEW = "CATEGORY";
    private static final String PRODUCT_VIEW = "PRODUCT";
    private static final String LOGIN_SIGN_UP_VIEW = "ACCOUNT";
    private static final String FAVORITES_VIEW = "MYFAVORITES";
    private static final String CART_VIEW = "CART";
    private static final String REGISTRATION_VIEW = "REGISTER";

    // Events
    private static final int EVENT_LOGIN = 1001;
    private static final int EVENT_FACEBOOK = 1002;
    private static final int EVENT_FIRST_OPEN_APP = 1003;

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
        Print.i(TAG, "ON STARTUP");
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
            Print.i(TAG, "Ad4PSUH Startup -> INITIALIZED");
            mA4S = A4S.get(mContext);
            boolean isActive = getActiveAd4Push(mContext);
            setGCMEnabled(isActive);
            setInAppDisplayLocked(!isActive);
        }
    }

    private boolean isAvailable() {
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
        if (isAvailable()) {
            Print.i(TAG,  "LOCK PUSH NOTIFICATIONS: " + bool);
            mA4S.setPushNotificationLocked(bool);
        }
    }

    /**
     * Lock or unlock the in-app messages.
     * @author sergiopereira
     */
    public void setInAppDisplayLocked(boolean bool) {
        if (isAvailable()) {
            Print.i(TAG,  "LOCK IN APP MESSAGE: " + bool);
            mA4S.setInAppDisplayLocked(bool);
        }
    }

    private void trackView(String view) {
        if (isAvailable()) {
            Print.i(TAG,  "View state tracked -> " + VIEW_STATE + "=" + view);
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
        if (isAvailable()) {
            Print.i(TAG,  "setGCMEnabled:" + enabled);
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

    /**
     * User preferences
     */
    public static boolean getActiveAd4Push(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES_PERSIST, Context.MODE_PRIVATE);
        return settings.getBoolean(AD4PUSH_PREFS_IS_ENABLED, context.getResources().getBoolean(R.bool.ad4push_enabled));
    }

    /**
     * User preferences
     */
    public static void setActiveAd4Push(Context context, boolean isActive) {
        SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES_PERSIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(AD4PUSH_PREFS_IS_ENABLED, isActive);
        editor.apply();
        Ad4PushTracker.startup(context);
    }

    /*
     * ############## TRACKING ##############
     */

    public void trackScreen(@NonNull TrackingPage screen) {
        switch (screen) {
            case HOME:              trackView(HOME_VIEW); break;
            case PRODUCT_LIST:      trackView(CATEGORY_VIEW); break;
            case PRODUCT_DETAIL:    trackView(PRODUCT_VIEW); break;
            case LOGIN_SIGN_UP:     trackView(LOGIN_SIGN_UP_VIEW); break;
            case FAVORITES:         trackView(FAVORITES_VIEW); break;
            case CART:              trackView(CART_VIEW); break;
            case REGISTRATION:      trackView(REGISTRATION_VIEW); break;
            default:                break;
        }
    }

    /**
     * Get and set the flag for first app open.
     */
    private boolean isFirstAppOpen(@NonNull Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        boolean firstTime = preferences.getBoolean(AD4PUSH_PREFS_HAS_OPENED_APP, false);
        preferences.edit().putBoolean(AD4PUSH_PREFS_HAS_OPENED_APP, true).apply();
        return firstTime;
    }

    /**
     * Track the app open
     */
    public void trackAppOpen() {
        if (isAvailable()) {
            Bundle bundle = new Bundle();
            if (!isFirstAppOpen(mContext)) {
                mA4S.trackEvent(EVENT_FIRST_OPEN_APP, "firstOpenDate=" + DateTimeUtils.getCurrentDateTime());
                bundle.putString(SHOP_COUNTRY, ShopSelector.getCountryCodeIso());
                bundle.putString(LANGUAGE_SELECTION, ShopSelector.getCountryLanguageCode());
                storeGaIdOnAccengage();
            } else {
                bundle.putString(LAST_OPEN_DATE, DateTimeUtils.getCurrentDateTime());
            }
            Print.i(TAG, "TRACK APP OPEN: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * Track the last product seen by the user
     */
    public void trackProductView(ProductRegular product) {
        if (isAvailable()) {
            Bundle bundle = new Bundle();
            bundle.putString(LAST_PRODUCT_VIEWED, product.getName());
            bundle.putString(LAST_SKU_VIEWED, product.getSku());
            bundle.putString(LAST_BRAND_VIEWED_KEY, product.getBrandKey());
            bundle.putString(LAST_BRAND_VIEWED_NAME, product.getBrandName());
            bundle.putString(LAST_VIEWED_CATEGORY_KEY, product.getCategoryKey());
            bundle.putString(LAST_VIEWED_CATEGORY_NAME, product.getCategoryName());
            Print.i(TAG, "TRACK PRODUCT VIEW: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User adds a product to cart
     */
    public void trackAddToCart(PurchaseEntity purchase) {
        if (isAvailable()) {
            Bundle bundle = new Bundle();
            bundle.putInt(CART_STATUS, purchase.getCartCount());
            bundle.putString(CART_VALUE, String.valueOf(purchase.getPriceForTracking()));
            bundle.putString(DATE_LAST_CART_UPDATED, DateTimeUtils.getCurrentDateTime());
            bundle.putString(LAST_CART_PRODUCT_NAME, purchase.getLastItemAdded().getName());
            bundle.putString(LAST_CART_SKU, purchase.getLastItemAdded().getConfigSimpleSKU());
            bundle.putString(LAST_CATEGORY_ADDED_TO_CART_NAME, purchase.getLastItemAdded().getCategoryName());
            bundle.putString(LAST_CATEGORY_ADDED_TO_CART_KEY, purchase.getLastItemAdded().getCategoryKey());
            bundle.putString(LAST_BRAND_ADDED_TO_CART_KEY, purchase.getLastItemAdded().getBrandKey());
            bundle.putString(LAST_BRAND_ADDED_TO_CART_NAME, purchase.getLastItemAdded().getBrandName());
            Print.i(TAG, "TRACK ADD TO CART: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User removes a product from cart
     */
    public void trackRemoveFromCart(PurchaseEntity purchase) {
        if (isAvailable()) {
            Bundle bundle = new Bundle();
            bundle.putInt(CART_STATUS, purchase.getCartCount());
            bundle.putString(CART_VALUE, String.valueOf(purchase.getPriceForTracking()));
            bundle.putString(DATE_LAST_CART_UPDATED, DateTimeUtils.getCurrentDateTime());
            Print.i(TAG, "TRACK REMOVE FROM CART: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User adds a product to favorite
     */
    public void trackAddToFavorites(ProductRegular product) {
        if (isAvailable()) {
            Bundle bundle = new Bundle();
            bundle.putInt(WISH_LIST_STATUS, WishListCache.size() + 1);
            bundle.putString(LAST_WISHLIST_SKU, product.getSku());
            bundle.putString(LAST_WISHLIST_PRODUCT_NAME, product.getName());
            bundle.putString(LAST_BRAND_ADDED_TO_WISHLIST_KEY, product.getBrandKey());
            bundle.putString(LAST_BRAND_ADDED_TO_WISHLIST_NAME, product.getBrandName());
            bundle.putString(LAST_CATEGORY_ADDED_TO_WISHLIST_KEY, product.getCategoryKey());
            bundle.putString(LAST_CATEGORY_ADDED_TO_WISHLIST_NAME, product.getCategoryName());
            Print.i(TAG, "TRACK ADD TO WISH LIST: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User removes a product from favorite
     */
    public void trackRemoveFromWishList() {
        if (isEnabled) {
            Bundle bundle = new Bundle();
            bundle.putInt(WISH_LIST_STATUS, WishListCache.size() - 1);
            Print.i(TAG, "TRACK REMOVE FROM WISH LIST: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User logs in to the app
     */
    public void trackLoginRegistration(Customer customer, boolean facebook) {
        if (isEnabled) {
            // Tracking user info
            trackCustomerInfo(customer);
            // Track event
            mA4S.trackEvent(facebook ? EVENT_FACEBOOK : EVENT_LOGIN, "loginUserID=" + customer.getIdAsString());
        }
    }

    /**
     * User updates his info on the app
     */
    public void trackCustomerInfo(Customer customer) {
        if (isEnabled) {
            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putString(USER_ID, customer.getIdAsString());
            bundle.putString(FIRST_NAME, customer.getFirstName());
            bundle.putString(LAST_NAME, customer.getLastName());
            bundle.putString(USER_DOB, customer.getBirthday());
            bundle.putString(USER_GENDER, customer.getGender());
            Print.i(TAG, "TRACKING USER INFO: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
    }

    /**
     * User completes a purchase.
     */
    public void trackPurchase(CheckoutFinish checkout, PurchaseEntity cart) {
        if (isEnabled) {
            // Get the most expensive
            PurchaseCartItem item = cart.getTheMostExpansiveItem();
            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putString(LAST_ORDER_DATE, DateTimeUtils.getCurrentDateTime());
            bundle.putString(AGGREGATED_NUMBER_OF_PURCHASE, checkout.getOrdersCount());
            bundle.putString(ORDER_NUMBER, checkout.getOrderNumber());
            bundle.putInt(CART_STATUS, 0);
            bundle.putString(LAST_BRAND_PURCHASED_KEY, item.getBrandKey());
            bundle.putString(LAST_BRAND_PURCHASED_NAME, item.getBrandName());
            bundle.putString(LAST_CATEGORY_PURCHASED_KEY, item.getCategoryKey());
            bundle.putString(LAST_CATEGORY_PURCHASED_NAME, item.getCategoryName());
            bundle.putString(LAST_PRODUCT_NAME_PURCHASED, item.getName());
            bundle.putString(LAST_SKU_PURCHASED, item.getConfigSimpleSKU());
            Print.i(TAG, "TRACK PURCHASE: " + bundle.toString());
            mA4S.updateDeviceInfo(bundle);
        }
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
        new Thread(r).start();
    }
}