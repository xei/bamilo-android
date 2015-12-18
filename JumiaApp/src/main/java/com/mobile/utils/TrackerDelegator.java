package com.mobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.objects.checkout.ExternalOrder;
import com.mobile.newFramework.objects.checkout.PurchaseItem;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.FacebookTracker;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.view.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TrackerDelegator {
    private final static String TAG = TrackerDelegator.class.getSimpleName();

    public static final boolean IS_AUTO_LOGIN = true;
    public static final boolean ISNT_AUTO_LOGIN = false;

    public static final String CUSTOMER_KEY = "customer";
    public static final String AUTOLOGIN_KEY = "auto_login";
    public static final String FACEBOOKLOGIN_KEY = "facebook_login";
    public static final String SEARCH_CRITERIA_KEY = "search_criteria";
    public static final String SEARCH_RESULTS_KEY = "search_results";
    public static final String SKU_KEY = "sku";
    public static final String PRICE_KEY = "price";
    public static final String LOCATION_KEY = "location";
    public static final String START_TIME_KEY = "start";
    public static final String CATEGORY_KEY = "category";
    public static final String COUPON_KEY = "coupon";
    public static final String PRODUCT_KEY = "product";
    public static final String RATINGS_KEY = "ratings";
    public static final String RATING_KEY = "rating";
    public static final String PURCHASE_KEY = "purchase";
    public static final String EMAIL_KEY = "email";
    public static final String GA_STEP_KEY = "ga_step";
    public static final String PAYMENT_METHOD_KEY = "payment_method";
    public static final String ORDER_NUMBER_KEY = "order_number";
    public static final String VALUE_KEY = "value";
    public static final String SOURCE_KEY = "source";
    public static final String PATH_KEY = "path";
    public static final String NAME_KEY = "name";
    public static final String BRAND_KEY = "brand";
    public static final String RELATED_ITEM = "related_item";
    public static final String FAVOURITES_KEY = "favourites";
    public static final String DISCOUNT_KEY = "discount";
    public static final String SUBCATEGORY_KEY = "sub_category";
    public static final String QUANTITY_KEY = "quantity";
    public static final String CARTVALUE_KEY = "cart_value";
    public static final String CATALOG_FILTER_KEY = "catalog_filter";
    public static final String FILTER_COLOR = "color_family";
    public static final String TAX_KEY = "tax";
    public static final String SHIPPING_KEY = "shipping";
    public static final String LOGIN_KEY = "source_login";

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SIGNUP_KEY_FOR_LOGIN = "signup_for_login";

    private static final String SESSION_COUNTER = "sessionCounter";
    private static final String LAST_SESSION_SAVED = "lastSessionSaved";
    private static final String EUR_CURRENCY = "EUR";

    public static final String CART_COUNT = "cartCount";
    public static final String GRAND_TOTAL = "grandTotal";

    private final static int MAX_RATE_VALUE = 5;

    public static final String APP_VERSION_KEY = "appVersion";

    private static final Context sContext = JumiaApplication.INSTANCE.getApplicationContext();


    public static void trackLoginSuccessful(Customer customer, boolean autoLogin, boolean fromFacebook) {
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
        params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, autoLogin);
        params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, fromFacebook);
        params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
        trackLoginSuccessful(params);
    }

    public static void trackLoginSuccessful(Bundle params) {
        TrackingEvent event;

        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean wasAutologin = params.getBoolean(AUTOLOGIN_KEY);
        boolean wasFacebookLogin = params.getBoolean(FACEBOOKLOGIN_KEY);
        String location = params.getString(LOCATION_KEY);

        if (wasFacebookLogin) {
            event = TrackingEvent.LOGIN_FB_SUCCESS;
        } else if (wasAutologin) {
            event = TrackingEvent.LOGIN_AUTO_SUCCESS;
        } else {
            event = TrackingEvent.LOGIN_SUCCESS;
        }

        String customerId = "";
        if (customer != null) {
            customerId = customer.getIdAsString();
        }

        AnalyticsGoogle.get().trackEvent(event, customerId, 0l);

        if (customer == null) {
            return;
        }

        boolean loginAfterRegister = checkLoginAfterSignup(customer);
        Print.d(TAG, "trackLoginSuccessul: loginAfterRegister = " + loginAfterRegister + " wasAutologin = " + wasAutologin);

        if (wasFacebookLogin) {
            Ad4PushTracker.get().trackFacebookConnect(customer.getIdAsString());
        }

        Ad4PushTracker.get().trackLogin(customer.getIdAsString(), customer.getFirstName(),customer.getLastName(), customer.getBirthday(), customer.getGender());

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(sContext, event, bundle);

        storeFirstCustomer(customer);

        //GTM
        if(event.compareTo(TrackingEvent.LOGIN_AUTO_SUCCESS) == 0){
            GTMManager.get().gtmTrackAutoLogin(customer);
        } else {
            GTMManager.get().gtmTrackLogin(customer, event, location);
        }

    }

    /**
     * Track the normal/auto login
     */
    public static void trackLoginFailed(boolean wasAutologin, String location, String method) {
        Print.i(TAG, "trackLoginFailed: autologin " + wasAutologin);
        // Case login
        TrackingEvent event = TrackingEvent.LOGIN_FAIL;
        // Case autologin
        if (wasAutologin) event = TrackingEvent.LOGIN_AUTO_FAIL;
        // Track
        AnalyticsGoogle.get().trackEvent(event, "", 0l);

        //GTM
        if(event.compareTo(TrackingEvent.LOGIN_AUTO_FAIL) == 0){
            GTMManager.get().gtmTrackAutoLoginFailed();
        }
        else {
            GTMManager.get().gtmTrackLoginFailed(location,method);
        }
    }

    public static void trackLogoutSuccessful() {
        String customerId = "";
        if (JumiaApplication.CUSTOMER != null) {
            customerId = JumiaApplication.CUSTOMER.getIdAsString();
        }

        AnalyticsGoogle.get().trackEvent(TrackingEvent.LOGOUT_SUCCESS, customerId, 0l);
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.LOGOUT_SUCCESS, bundle);

        //GTM
        GTMManager.get().gtmTrackLogout(customerId);
        JumiaApplication.CUSTOMER = null;
    }


    public static void trackSearch(Bundle params) {
        String criteria = params.getString(SEARCH_CRITERIA_KEY);
        long results = params.getLong(SEARCH_RESULTS_KEY);

        String customerId = "";
        if (JumiaApplication.CUSTOMER != null) {
            customerId = JumiaApplication.CUSTOMER.getIdAsString();
        }

        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SEARCH, criteria, results);
        // AD4P
        Ad4PushTracker.get().trackSearch(criteria);
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.SEARCH_TERM, criteria);
        if(params.containsKey(AdjustTracker.CATEGORY))
            bundle.putString(AdjustTracker.CATEGORY, params.getString(AdjustTracker.CATEGORY));

        bundle.putString(AdjustTracker.CATEGORY_ID, params.getString(AdjustTracker.CATEGORY_ID));
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.SEARCH, bundle);
        //GTM
        GTMManager.get().gtmTrackSearch(criteria, results);
        // FB
//        FacebookTracker.get(sContext).trackSearched(criteria);
    }


    public static void trackShopChanged() {
        //GTM
        GTMManager.get().gtmTrackChangeCountry(ShopSelector.getShopId());
    }


    public static void trackProductRemoveFromCart(Bundle params) {
        String sku = params.getString(SKU_KEY);

        String customer_id = "";
        if (JumiaApplication.CUSTOMER != null) {
            customer_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer_id);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, params.getDouble(PRICE_KEY));
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.REMOVE_FROM_CART, bundle);

        // GTM
        GTMManager.get().gtmTrackRemoveFromCart(sku, params.getDouble(RATING_KEY), params.getDouble(PRICE_KEY),
                params.getLong(QUANTITY_KEY), params.getString(CARTVALUE_KEY), EUR_CURRENCY);
    }

    public static void trackCheckout(List<PurchaseCartItem> items) {
        AnalyticsGoogle.get().trackCheckout(items);
    }

    /**
     *
     */
    public static void trackItemShared(Intent intent, String category) {
        String sku = intent.getExtras().getString(RestConstants.SKU);
        String userId = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            userId = JumiaApplication.CUSTOMER.getIdAsString();
        }
        // GA
        AnalyticsGoogle.get().trackShare(sContext, sku, userId, JumiaApplication.SHOP_NAME);
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.SHARE, bundle);
        //GTM
        GTMManager.get().gtmTrackShare("", sku, category);
        // Ad4Push
        Ad4PushTracker.get().trackLastShared(sku);
    }

    /**
     *
     */
    public static void trackCategoryView() {
        new Thread(new Runnable() {
            public void run() {
                try  {
                    // AD4Push
                    Ad4PushTracker.get().trackCategorySelection();
                } catch (NullPointerException | IllegalStateException e) {
                    Print.i(TAG, "WARNING: EXCEPTION ON TRACK CATEGORY ");
                }
            }
        }).start();
    }

    /**
     *
     */
    public static void trackItemReview(Bundle params, boolean isRating) {

        ProductComplete product = params.getParcelable(PRODUCT_KEY);
        HashMap<String, Long> ratingValues = (HashMap<String, Long>) params.getSerializable(RATINGS_KEY);
        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }

        if (ratingValues != null && ratingValues.size() > 0) {
            for (Entry<String, Long> pairs : ratingValues.entrySet()) {
                // GA
                AnalyticsGoogle.get().trackRateProduct(sContext, product.getSku(), pairs.getValue(), pairs.getKey());
                // FB
//                FacebookTracker.get(sContext).trackRated(product.getSku(), pairs.getKey(), pairs.getValue(), MAX_RATE_VALUE);
            }
        }
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, user_id);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, product.getSku());
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.ADD_REVIEW, bundle);
        //GTM
        GTMManager.get().gtmTrackRateProduct(product, EUR_CURRENCY);
        // Ad4Push
        if(!isRating){
            Ad4PushTracker.get().trackLastReviewed(product.getSku());
        }

    }

    /**
     *
     */
    public static void trackViewReview(ProductComplete product) {
        //GTM
        GTMManager.get().gtmTrackViewRating(product, EUR_CURRENCY);
    }

    /**
     * Track signup successful.
     */
    public static void trackSignupSuccessful(String location) {
        Customer customer = JumiaApplication.CUSTOMER;
        // Validate customer
        if (customer == null) return;
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_SUCCESS, customer.getIdAsString(), 0l);
        // AD4
        Ad4PushTracker.get().trackSignup(customer.getIdAsString(), String.valueOf(customer.getGender()), customer.getFirstName(), customer.getLastName(), customer.getBirthday());
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.SIGNUP_SUCCESS, bundle);
        //GTM
        GTMManager.get().gtmTrackRegister(customer.getIdAsString(),location);
        storeFirstCustomer(customer);
    }

    public static void trackSignupFailed(String location) {
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_FAIL, null, 0l);
        //GTM
        GTMManager.get().gtmTrackRegisterFailed(location);
    }

    /**
     * For Web Checkout
     */
    public static void trackPurchase(final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                trackPurchaseInt(params);
            }

        }).start();
    }



    /**
     * Track Payment Method
     */
    public static void trackPaymentMethod(String userId, String email, String payment) {
        try {
            // GA
            AnalyticsGoogle.get().trackPaymentMethod(TextUtils.isEmpty(userId) ? email : userId, payment);
            // GTM
            GTMManager.get().gtmTrackChoosePayment(payment);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON TRACK PAYMENT METHOD");
        }
    }

    public static void trackNativeCheckoutError(String userId, String email, String error) {
        try {
            // GA
            AnalyticsGoogle.get().trackNativeCheckoutError(TextUtils.isEmpty(userId) ? email : userId, error);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON TRACK NC ERROR");
        }
    }

    /**
     * For Native Checkout
     */
    public static void trackPurchaseNativeCheckout(final Bundle params, final ArrayList<PurchaseCartItem> mItems, final String attributeIdList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                trackNativeCheckoutPurchase(params, mItems, attributeIdList);
            }

        }).start();
    }

    // Got Web checkout response
    private static void trackPurchaseInt(Bundle params) {
        Print.i(TAG, "TRACK SALE: STARTED");
        JSONObject result = null;
        String appVersion = DeviceInfoHelper.getVersionName(sContext);
        try {
            result = new JSONObject(params.getString(PURCHASE_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Validate json
        if (result == null) {
            return;
        }
        Print.d(TAG, "TRACK SALE: JSON " + result.toString());

        // Validate customer
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean isFirstCustomer = false;
        if (customer != null && checkFirstCustomer(customer)) {
            isFirstCustomer = true;
            removeFirstCustomer(customer);
        } else {
            Print.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
        }

        // Create external order
        ExternalOrder order = new ExternalOrder(result);

        // GA
        AnalyticsGoogle.get().trackPurchase(order.number, order.valueConverted, order.items);
        // AD4
        Ad4PushTracker.get().trackCheckoutEnded(order.number, order.valueConverted, order.value, order.average, order.items.size(), order.coupon, "");
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID, customer != null ? customer.getIdAsString() : AdjustTracker.NOT_AVAILABLE);
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, order.number);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, order.skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer != null && customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, order.items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, order.value);
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.CHECKOUT_FINISHED, bundle);
        //GTM
        String paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        GTMManager.get().gtmTrackTransaction(order.items, EUR_CURRENCY, order.value, order.number, order.coupon, paymentMethod, "", "");
        // FB
        FacebookTracker.get(sContext).trackCheckoutFinished(order.skus, order.valueConverted, JumiaApplication.SHOP_ID, appVersion);
    }

    private static void trackNativeCheckoutPurchase(Bundle params, ArrayList<PurchaseCartItem> mItems, String attributeIdList) {
        String orderNr = params.getString(ORDER_NUMBER_KEY);
        double grandTotal = params.getDouble(GRAND_TOTAL);
        double cartValue = params.getDouble(VALUE_KEY);
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String coupon = params.getString(COUPON_KEY);
        String paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        String shippingAmount = params.getString(SHIPPING_KEY);
        String taxAmount = params.getString(TAX_KEY);
        String appVersion =DeviceInfoHelper.getVersionName(sContext);

        int numberOfItems = params.getInt(TrackerDelegator.CART_COUNT);

        Double averageValue;
        try {
            averageValue = cartValue / numberOfItems;
        } catch (IllegalArgumentException e) {
            averageValue = 0d;
        }

        Print.i(TAG, "TRACK SALE: STARTED");
        Print.d(TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        Print.d(TAG, "TRACK SALE: JSON " + orderNr);

        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        ArrayList<String> skus = new ArrayList<>();
        // Get number of items
        for (PurchaseItem item : items) {
            skus.add(item.sku);
        }

        boolean isFirstCustomer;
        if (customer == null) {
            Print.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // Send the track sale without customer id
            isFirstCustomer = false;
        } else {
            isFirstCustomer = checkFirstCustomer(customer);
            if(isFirstCustomer) removeFirstCustomer(customer);
        }

        // GA
        AnalyticsGoogle.get().trackPurchase(orderNr, cartValue, items);
        //GA Banner Flow
        trackBannerClick(items);
        // Ad4
        Ad4PushTracker.get().trackCheckoutEnded(orderNr, grandTotal, cartValue, averageValue, numberOfItems, coupon, attributeIdList);
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID, customer != null ? customer.getIdAsString() : AdjustTracker.NOT_AVAILABLE);
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, orderNr);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer != null && customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, (ArrayList<PurchaseItem>) items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, cartValue);
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.CHECKOUT_FINISHED, bundle);
        //GTM
        GTMManager.get().gtmTrackTransaction(items, EUR_CURRENCY, cartValue, orderNr, coupon, paymentMethod, shippingAmount, taxAmount);
        // FB
        FacebookTracker.get(sContext).trackCheckoutFinished(skus, cartValue, JumiaApplication.SHOP_ID, appVersion);
    }

//    public static void storeSignupProcess(Customer customer) {
//        Print.d(TAG, "storing signup tags");
//        SharedPreferences prefs = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
//        prefs.edit().putString(SIGNUP_KEY_FOR_LOGIN, customer.getEmail()).putString(SIGNUP_KEY_FOR_CHECKOUT, customer.getEmail()).apply();
//    }
    
    public static void removeFirstCustomer(Customer customer) {
        Print.d(TAG, "remove first customer");
        SharedPreferences prefs = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(customer.getEmail(), false).apply();
    }

    public static void storeFirstCustomer(Customer customer) {
        Print.d(TAG, "store first customer");
        SharedPreferences prefs = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        boolean isNewCustomer = prefs.getBoolean(customer.getEmail(), true);
        if (isNewCustomer) {
            Print.d(TAG, "store first customer1");
            prefs.edit().putBoolean(customer.getEmail(),true).apply();
        }

    }

    private static boolean checkLoginAfterSignup(Customer customer) {
        SharedPreferences prefs = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        if (!prefs.contains(SIGNUP_KEY_FOR_LOGIN)) {
            return false;
        }

        String email = prefs.getString(SIGNUP_KEY_FOR_LOGIN, "");
        if (!email.equals(customer.getEmail())) {
            return false;
        }

        prefs.edit().remove(SIGNUP_KEY_FOR_LOGIN).apply();
        return true;
    }

    private static boolean checkFirstCustomer(Customer customer) {
        SharedPreferences prefs = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        return !prefs.contains(customer.getEmail()) || prefs.getBoolean(customer.getEmail(), true);
    }


    /**
     * Tracking the continue shopping
     */
    public static void trackCheckoutContinueShopping(String userId) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CHECKOUT_CONTINUE, userId, 0l);
    }

    /**
     * Tracking the start of checkout
     */
    public static void trackCheckoutStart(TrackingEvent event, String userId, int cartQt, double cartValue, String attributeIdList) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
        // AD4Push
        Ad4PushTracker.get().trackCheckoutStarted(cartQt, cartValue, attributeIdList);
        // GTM
        GTMManager.get().gtmTrackStartCheckout(cartQt, cartValue, EUR_CURRENCY);
        // FB
//        FacebookTracker.get(sContext).trackCheckoutStarted(userId, cartValue, cartQt);
    }

    /**
     * Tracking a timing
     */
    public static void trackLoadTiming(Bundle params) {
        int location = params.getInt(LOCATION_KEY);
        long start = params.getLong(START_TIME_KEY);
        // GA
        AnalyticsGoogle.get().trackLoadTiming(location, start);
    }

    /**
     * Tracking a page
     *
     */
    public static void trackPage(TrackingPage screen, long loadTime, boolean justGTM) {
        // GTM
        trackScreenGTM(screen, loadTime);
        //
        if (!justGTM) {
            // GA
            AnalyticsGoogle.get().trackPage(screen);
            // AD4
            Ad4PushTracker.get().trackScreen(screen);
        }
    }

    /**
     * Tracking a page for adjust
     */
    public static void trackPageForAdjust(TrackingPage screen, Bundle bundle) {
        if (null != bundle) {
            bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
            bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
            bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
            if (JumiaApplication.CUSTOMER != null) {
                bundle.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
            }
            AdjustTracker.get().trackScreen(screen, bundle);
        }
    }

    public static void trackFavouriteAddedToCart(ProductRegular product, String simpleSku, TeaserGroupType type) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, product.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, product.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, product.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, product.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, product.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, product.getCategories());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.WISHLISTPAGE);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, type);
        trackProductAddedToCart(bundle);
    }


    public static void trackProductAddedToCart(ProductRegular product, String simpleSku, TeaserGroupType type) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, product.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, product.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, product.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, product.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, product.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, product.getCategories());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, type);
        trackProductAddedToCart(bundle);
    }

    /**
     * Tracking a product added to cart
     */
    public static void trackProductAddedToCart(Bundle bundle) {
        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        // Data
        String brand = bundle.getString(BRAND_KEY);
        String category = bundle.getString(CATEGORY_KEY);
        String subCategory = bundle.getString(SUBCATEGORY_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        double discount = bundle.getDouble(DISCOUNT_KEY);
        double rating = bundle.getDouble(RATING_KEY);
        String sku = bundle.getString(SKU_KEY);
        String name = bundle.getString(NAME_KEY);
        String location = bundle.getString(LOCATION_KEY);
        String appVersion = DeviceInfoHelper.getVersionName(sContext);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_CART, sku, (long) price);
        // AD4Push
        if(TextUtils.equals(location, GTMValues.WISHLISTPAGE)) {
            Ad4PushTracker.get().trackAddToCartFromFav(sku, price, name, category);
        } else {
            Ad4PushTracker.get().trackAddToCart(sku, price, name, category);
        }
        Ad4PushTracker.get().trackLastAddToCart(name, sku, category);
        //Adjust
        Bundle params = new Bundle();
        params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        params.putString(AdjustTracker.USER_ID, customerId);
        params.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        params.putString(AdjustTracker.PRODUCT_SKU, sku);
        params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        params.putDouble(AdjustTracker.VALUE, price);
        AdjustTracker.get().trackEvent(sContext, TrackingEvent.ADD_TO_CART, params);
        //GTM
        GTMManager.get().gtmTrackAddToCart(sku, price, brand, EUR_CURRENCY, discount, rating, category, subCategory, location);
        // FB
        FacebookTracker.get(sContext).trackAddedToCart(sku, price, JumiaApplication.SHOP_ID, appVersion);
        //GA Banner Flow
        if (bundle.getSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE) != null) {
            JumiaApplication.INSTANCE.setBannerFlowSkus(sku, (TeaserGroupType) bundle.getSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE));
        }

    }

    /**
     *
     */
    public static void trackProduct(ProductRegular mCompleteProduct, String source, String path, boolean isRelatedItem) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SOURCE_KEY, source);
        bundle.putString(TrackerDelegator.PATH_KEY, path);
        bundle.putString(TrackerDelegator.NAME_KEY, mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        bundle.putString(TrackerDelegator.SKU_KEY, mCompleteProduct.getSku());
        bundle.putDouble(TrackerDelegator.PRICE_KEY, mCompleteProduct.getPriceForTracking());
        bundle.putBoolean(TrackerDelegator.RELATED_ITEM, isRelatedItem);
        bundle.putString(TrackerDelegator.BRAND_KEY, mCompleteProduct.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, mCompleteProduct.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, mCompleteProduct.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, mCompleteProduct.getCategories());
        trackProduct(bundle);
    }


    /**
     * Tracking a complete product
     */
    public static void trackProduct(Bundle bundle) {
        // Data
        String prefix = bundle.getString(SOURCE_KEY);
        String path = bundle.getString(PATH_KEY);
        String name = bundle.getString(NAME_KEY);
        String sku = bundle.getString(SKU_KEY);
        String brand = bundle.getString(BRAND_KEY);
        String category = bundle.getString(CATEGORY_KEY);
        String subCategory = bundle.getString(SUBCATEGORY_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        double discount = bundle.getDouble(DISCOUNT_KEY);
        double rating = bundle.getDouble(RATING_KEY);
        Boolean isRelatedItem = bundle.getBoolean(RELATED_ITEM, false);
        String appVersion = DeviceInfoHelper.getVersionName(sContext);

        TrackingEvent event = !isRelatedItem ? TrackingEvent.SHOW_PRODUCT_DETAIL : TrackingEvent.SHOW_RELATED_PRODUCT_DETAIL;
        // GA
        AnalyticsGoogle.get().trackProduct(event, prefix, path, name, sku, price);
        //GTM
        GTMManager.get().gtmTrackViewProduct(sku, price, brand, EUR_CURRENCY, discount, rating, category, subCategory);
        // FB
        FacebookTracker.get(sContext).trackProduct(sku, price, JumiaApplication.SHOP_ID, appVersion);
        // Ad4push
        Ad4PushTracker.get().trackTopBrand();
    }

    /**
     * Tracking a campaign
     */
    public static void trackGACampaign(Context context, String utm) {
        Print.i(TAG, "UTM INFO ->" + utm);
        if(!TextUtils.isEmpty(utm)) {
            // GA
            AnalyticsGoogle.get().setGACampaign(utm);
            // GTM
            GTMManager.saveCampaignParams(context, GTMManager.CAMPAIGN_ID_KEY, AnalyticsGoogle.get().getUtmParameter(utm, "utm_campaign="));
        }
    }

    /**
     * Tracking a campaign
     */
    public static void trackCampaignView(TeaserCampaign teaserCampaign) {
        // GA
        AnalyticsGoogle.get().trackGenericPage(teaserCampaign != null ? teaserCampaign.getTitle() : AdjustTracker.NOT_AVAILABLE);
        // Ad4Push
        Ad4PushTracker.get().trackLastViewedCampaign(teaserCampaign != null ? teaserCampaign.getId() : AdjustTracker.NOT_AVAILABLE);
    }

    /**
     * Tracking add product to favorites
     */
    public static void trackAddToFavorites(ProductRegular completeProduct) {
        String productSku = completeProduct.getSku();
        String productBrand = completeProduct.getBrand();
        double productPrice = completeProduct.getPriceForTracking();
        double averageRating = completeProduct.getAvgRating();
        double productDiscount = completeProduct.getMaxSavingPercentage();
        boolean fromCatalog = false;
        String categories = completeProduct.getCategories();

        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";

        Ad4PushTracker.get().trackAddToFavorites(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_WISHLIST, productSku, (long) productPrice);

        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putDouble(AdjustTracker.VALUE, productPrice);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());

        AdjustTracker.get().trackEvent(sContext, TrackingEvent.ADD_TO_WISHLIST, bundle);
        String location = GTMValues.PRODUCTDETAILPAGE;
        if(fromCatalog) location = GTMValues.CATALOG;

        //GTM
        GTMManager.get().gtmTrackAddToWishList(productSku, productBrand, productPrice, averageRating,
                productDiscount, CurrencyFormatter.getCurrencyCode(),location, categories, "");
        // FB
//        FacebookTracker.get(sContext).trackAddedToWishlist(productSku, productPrice, categories);
    }

    /**
     * Tracking remove product from favorites
     * h375id
     */
    public static void trackRemoveFromFavorites(ProductRegular completeProduct) {
        String productSku = completeProduct.getSku();
        double price = completeProduct.getPriceForTracking();
        double averageRatingTotal = completeProduct.getAvgRating();

        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";

        Ad4PushTracker.get().trackRemoveFromWishList(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.REMOVE_FROM_WISHLIST, productSku, (long) price);

        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, price);

        AdjustTracker.get().trackEvent(sContext, TrackingEvent.REMOVE_FROM_WISHLIST, bundle);

        //GTM
        if(averageRatingTotal != -1d)
            GTMManager.get().gtmTrackRemoveFromWishList(productSku, price, averageRatingTotal, EUR_CURRENCY);
    }

    
//    /**
//     * Tracking a view Catalog
//     *
//     */
//    public static void trackViewCatalog(String category, String subCategory, int pageNumber) {
//        // GTM
//        GTMManager.get().gtmTrackCatalog(category, subCategory, pageNumber);
//    }
    
    
    
    /**
     * Tracking a catalog filter
     */
    public static void trackCatalogFilter(ContentValues catalogFilterValues) {
        // Validate filters
        if(catalogFilterValues != null) {
            // GA
            String filter = catalogFilterValues.toString();
            AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_FILTER, filter, 0l);
            // AD4Push
            Ad4PushTracker.get().trackCatalogFilter(catalogFilterValues);
            //GTM
            if(catalogFilterValues.containsKey(TrackerDelegator.CATALOG_FILTER_KEY)){
                String activeFilters = catalogFilterValues.getAsString(TrackerDelegator.CATALOG_FILTER_KEY);
                if (!TextUtils.isEmpty(activeFilters)) {
                    String[] filters = activeFilters.split(",");
                    for (String activefilter : filters) {
                        Print.d("GTM FILTER", ":" + activefilter);
                        GTMManager.get().gtmTrackFilterCatalog(activefilter);
                    }
                }
            }
        }
    }

    /**
     * Tracking a catalog Sort
     */
    public static void trackCatalogSorter(CatalogSort sort) {
        // GTM
        GTMManager.get().gtmTrackSortCatalog(sort.toString());
        // Ad4push
        Ad4PushTracker.get().trackLastSortedBy(sort.id);

    }

    /**
     * Tracking a cart view for GTM
     *
     */
    public static void trackViewCart(int quantityCart, double cartValue) {
        // GTM
        GTMManager.get().gtmTrackViewCart(quantityCart, cartValue, EUR_CURRENCY);
    }

    /**
     * Tracking add address for GTM
     *
     */
    public static void trackAddAddress(boolean success) {
        // GTM
        GTMManager.get().gtmTrackEnterAddress(success);
    }

    /**
     * Tracking add address for GTM
     *
     */
    public static void trackFailedPayment(String paymentMethod, PurchaseEntity order) {
        if (order != null) {
            GTMManager.get().gtmTrackFailedPayment(paymentMethod, order.getCartValueEuroConverted(), EUR_CURRENCY);
        }
    }

    /**
     * Tracking closing app for GTM
     *
     */
    public static void trackCloseApp() {
        // GTM
        GTMManager.get().gtmTrackAppClose();
    }


    private static void trackScreenGTM(TrackingPage page, long loadTime){
        //GMT
        String screenName = "";
        if(page.getName() != -1){
            screenName = sContext.getString(page.getName());
        }

        if(!"".equalsIgnoreCase(screenName))
            GTMManager.get().gtmTrackViewScreen(screenName, loadTime);
    }

    /**
     * Tracking newsletter subscription
     */
    public static void trackNewsletterSubscription(boolean subscribe, String location) {
        // User
        String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        // GA
        TrackingEvent event = (subscribe) ? TrackingEvent.SUBSCRIBE_NEWSLETTER : TrackingEvent.UNSUBSCRIBE_NEWSLETTER;
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
        if(subscribe) trackNewsletterGTM(userId, location);
    }

    public static void trackNewsletterGTM(String customerId, String location){
        // User
        if (TextUtils.isEmpty(customerId)) {
            customerId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        }
        //GTM
        GTMManager.get().gtmTrackSignUp(customerId, location);
    }

    /**
     * Tracking a search query
     */
    public static void trackSearchSuggestions(String query) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SEARCH_SUGGESTIONS, query, 0l);
    }

    /**
     * Track first open and shop country
     */
    public static void trackAppOpen(Context context, boolean isFromPush) {
        // Track app open
        boolean userNeverLoggedIn = JumiaApplication.INSTANCE.getCustomerUtils().userNeverLoggedIn();
        // Get device info
        Bundle info = DeviceInfoHelper.getInfo(context);
        // AD4Push
        Ad4PushTracker.get().trackEmptyUserId(userNeverLoggedIn);
        Ad4PushTracker.get().trackAppFirstOpen(info);
        Ad4PushTracker.get().trackShopCountry();
        // GA
        AnalyticsGoogle.get().setCustomData(info);
        // GTM
        String campaingId = GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_ID_KEY);
        GTMManager.saveUtmParameters(context, GTMManager.CAMPAIGN_ID_KEY, "");
        GTMManager.get().gtmTrackAppOpen(info, ShopSelector.getShopId(), campaingId,
                GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_SOURCE),
                GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_MEDIUM),
                isFromPush);
        countSession();
        // FB
        FacebookTracker.get(context).trackActivatedApp();
    }

    /**
     *
     */
    public static void trackAppOpenAdjust(Context context, long launchtime) {
        if(ShopSelector.getShopId() == null)
            return;
        // Get device info
        Bundle info = DeviceInfoHelper.getInfo(context);
        // Adjust
        Bundle params = new Bundle(info);
        params.putLong(AdjustTracker.BEGIN_TIME, launchtime);
        params.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, TrackingEvent.APP_OPEN, params);
        GTMManager.trackAdjustInstallSource(context);

        Ad4PushTracker.get().storeGaIdOnAccengage();
    }

    /**
     * Tracking the call button
     */
    public static void trackCall(Context context) {
        String userId = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            userId= JumiaApplication.CUSTOMER.getIdAsString();
        }
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, TrackingEvent.CALL, bundle);
    }



    /**
     * Tracking the click in overflow menu item
     */
    public static void trackOverflowMenu(TrackingEvent event) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, "", 0l);
    }

    /**
     * Tracking the switch layout in catalog
     */
    public static void trackCatalogSwitchLayout(String label) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SWITCH_LAYOUT, label, 0l);
    }

    private static void countSession() {
        SharedPreferences settings = sContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        int sessionCount = settings.getInt(SESSION_COUNTER, 0);
        long savedTimeStamp = settings.getLong(LAST_SESSION_SAVED, 0);
        long currentTimeStamp = System.currentTimeMillis();
        long diff = currentTimeStamp - savedTimeStamp;
        Print.i("TIME", " DIFF " + diff + " 30L * 60 * 1000 " + 2L * 60 * 1000 + " currentTimeStamp " + currentTimeStamp + " savedTimeStamp " + savedTimeStamp);
        if (diff >= 30 * 60 * 1000) {
            Print.i("TIME", " A MINUTE HAS PASSED");
            Editor editor = settings.edit();
            editor.putLong(LAST_SESSION_SAVED, currentTimeStamp);
            sessionCount++;
            editor.putInt(SESSION_COUNTER, sessionCount);
            editor.apply();

        } else {
            Print.i("TIME", " STILL TICKING");
        }

    }

    /**
     * Track the new cart for each user interaction, add or remove.
     * @author sergiopereira
     */
    public static void trackCart(double cartValue, int cartCount, String attributeIds) {
        // Ad4
        Ad4PushTracker.get().trackCart(cartValue, cartCount, attributeIds);
    }

    public static void trackAddBundleToCart(String productSku, double price) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_BUNDLE_TO_CART, productSku, (long) price);
    }

    public static void trackAddOfferToCart(String productSku, double price) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_OFFER_TO_CART, productSku, (long) price);
    }

    public static void clearTransactionCount() {
        SharedPreferences settings = sContext.getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.apply();
    }

    /**
     * validate if there's any product added from a banner when finished a success order
     */
    public static void trackBannerClick(final List<PurchaseItem> items) {
        final HashMap<String,String> skus = JumiaApplication.INSTANCE.getBannerFlowSkus();
        if (skus != null && skus.size() > 0 && !CollectionUtils.isEmpty(items)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (PurchaseItem item : items) {
                            if (skus.containsKey(item.sku)) {
                                Print.e(TAG, "BANNER KEY:" +item.sku + " VALUE:" + skus.get(item.sku));
                                // fires the GA event when the user finish a order, originating in one of the home teasers
                                AnalyticsGoogle.get().trackBannerFlowPurchase(skus.get(item.sku),
                                        TrackingEvent.MAIN_BANNER_CLICK.getAction(),
                                        item.sku, (long) item.getPriceForTracking());
                            }
                    }
                    JumiaApplication.INSTANCE.clearBannerFlowSkus();
                }
            }).start();
        }
    }

    /**
     * fires a GA event every time the user taps on one of the home teasers
     */
    public static void trackBannerClicked(TeaserGroupType groupType, String targetKey, int position){
        AnalyticsGoogle.get().trackEventBannerClick(getCategoryFromTeaserGroupType(groupType), targetKey, position);
    }

    /**
     * this function matchs the home page teaser type with a tracking event
     */
    public static int getCategoryFromTeaserGroupType(TeaserGroupType groupType){
        // Default value
        TrackingEvent event;
        switch (groupType){
            case MAIN_TEASERS:
                event = TrackingEvent.MAIN_BANNER_CLICK;
                break;
            case SMALL_TEASERS:
                event = TrackingEvent.SMALL_BANNER_CLICK;
                break;
            case CAMPAIGNS:
                event =  TrackingEvent.CAMPAIGNS_BANNER_CLICK;
                break;
            case SHOP_TEASERS:
                event =  TrackingEvent.SHOP_BANNER_CLICK;
                break;
            case BRAND_TEASERS:
                event =  TrackingEvent.BRAND_BANNER_CLICK;
                break;
            case SHOP_OF_WEEK:
                event =  TrackingEvent.SHOPS_WEEK_BANNER_CLICK;
                break;
            case FEATURED_STORES:
                event =  TrackingEvent.FEATURE_BANNER_CLICK;
                break;
            case TOP_SELLERS:
                event =  TrackingEvent.TOP_SELLER_BANNER_CLICK;
                break;
            default:
                event =  TrackingEvent.UNKNOWN_BANNER_CLICK;
                Print.w(TAG, "UNKNOWN TEASER GROUP");
                break;

        }
         return event.getCategory();
    }
    /**
     * DeepLink Reattribution, Adjust
     */
    public static void deeplinkReattribution(Intent intent){
        if(intent != null && intent.getData() != null){
            AdjustTracker.deepLinkReattribution(intent.getData());
        }
    }
    /**
     * Track Push Notification
     */
    public static void trackPushNotification(){
        // Track open push notification
        Ad4PushTracker.get().trackOpenPushNotification();
    }

    /**
     * Track Last viewed Category
     */
    public static void trackLastViewedCategory(String categoryId){
        Ad4PushTracker.get().trackLastViewedCategory(categoryId);
    }
//    private static void saveUtmParams(Context context, String key, String value) {
//        Log.d(TAG, "saving saveUtmParams params, key: " + key + ", value : " + value);
//        Log.d("BETA", "saving saveUtmParams params, key: " + key + ", value : " + value);
//        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }


    /**
     * Track catalog page
     * Fire the track catalog page for Adjust Tracker
     */
    public static void trackCatalogPageContent(CatalogPage catalogPage, String categoryTree) {

        if (catalogPage != null) {
            // Track Adjust screen
            Bundle bundle = new Bundle();

            if (!TextUtils.isEmpty(catalogPage.getCategoryId())) {
                bundle.putString(AdjustTracker.CATEGORY_ID, catalogPage.getCategoryId());
                // last viewed category Id
                TrackerDelegator.trackLastViewedCategory(catalogPage.getCategoryId());
            }
            if (!TextUtils.isEmpty(catalogPage.getName())) {
                bundle.putString(AdjustTracker.CATEGORY, catalogPage.getName());
            }
            if (!CollectionUtils.isEmpty(catalogPage.getProducts())) {
                bundle.putParcelableArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, catalogPage.getProducts());
            }
            if (!TextUtils.isEmpty(categoryTree)) {
                bundle.putString(AdjustTracker.TREE, categoryTree);
            }
            if (!TextUtils.isEmpty(catalogPage.getBrandId())) {
                bundle.putString(AdjustTracker.BRAND_ID, catalogPage.getBrandId());
            }
            TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_LIST_SORTED, bundle);

            // Search
            if (!TextUtils.isEmpty(catalogPage.getSearchTerm())) {
                TrackerDelegator.trackCatalogSearch(catalogPage);
            }
            //FB
            FacebookTracker.get(sContext).trackCatalogView(catalogPage.getName(),JumiaApplication.SHOP_ID, DeviceInfoHelper.getVersionName(sContext));
        }
    }

    /**
     * fires the search event for all trackers
     */
    public static void trackCatalogSearch(CatalogPage catalogPage) {
        final Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SEARCH_CRITERIA_KEY, catalogPage.getSearchTerm());
        bundle.putLong(TrackerDelegator.SEARCH_RESULTS_KEY, catalogPage.getTotal());
        if (!TextUtils.isEmpty(catalogPage.getCategoryId())) {
            bundle.putString(AdjustTracker.CATEGORY_ID, catalogPage.getCategoryId());
        }
        if (!TextUtils.isEmpty(catalogPage.getName())) {
            bundle.putString(AdjustTracker.CATEGORY, catalogPage.getName());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    TrackerDelegator.trackSearch(bundle);
                } catch (NullPointerException e) {
                    Print.i(TAG, "WARNING: EXCEPTION ON TRACK SEARCH ");
                }
            }
        }).start();

    }

    /**
     * Track Checkout Step
     */
    public static void trackCheckoutStep(TrackingEvent step) {
        try {
            String email = JumiaApplication.INSTANCE.getCustomerUtils().getEmail();
            String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
            AnalyticsGoogle.get().trackEvent(step, TextUtils.isEmpty(userId) ? email : userId, 0l);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON TRACK CHECKOUT STEP");
        }
    }
}
