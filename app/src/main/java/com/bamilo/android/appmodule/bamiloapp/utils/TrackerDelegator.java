package com.bamilo.android.appmodule.bamiloapp.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.CatalogSort;
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ProductDetail;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.catalog.CatalogPage;
import com.bamilo.android.framework.service.objects.checkout.CheckoutFinish;
import com.bamilo.android.framework.service.objects.checkout.ExternalOrder;
import com.bamilo.android.framework.service.objects.checkout.PurchaseItem;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.objects.product.pojo.ProductComplete;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.AdjustTracker;
import com.bamilo.android.framework.service.tracking.AnalyticsGoogle;
import com.bamilo.android.framework.service.tracking.TrackingEvent;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.tracking.gtm.GTMManager;
import com.bamilo.android.framework.service.tracking.gtm.GTMValues;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrackerDelegator {

    private final static String TAG = TrackerDelegator.class.getSimpleName();

    public static final boolean IS_AUTO_LOGIN = true;
    public static final boolean ISNT_AUTO_LOGIN = false;

    public static final String CUSTOMER_KEY = RestConstants.CUSTOMER;
    public static final String AUTOLOGIN_KEY = "auto_login";
    public static final String SEARCH_CRITERIA_KEY = "search_criteria";
    public static final String SEARCH_RESULTS_KEY = "search_results";
    static final String CATEGORY_SEARCH_RESULTS = "SearchResults";
    static final String CATEGORY_SEARCH_SUGGESTIONS = "SearchSuggestions";
    static final String ACTION_SEARCH = "Search";
    static final String ACTION_TAPPED = "tapped";
    public static final String SKU_KEY = RestConstants.SKU;
    public static final String PRICE_KEY = RestConstants.PRICE;
    public static final String LOCATION_KEY = "location";
    public static final String START_TIME_KEY = "start";
    public static final String CATEGORY_KEY = RestConstants.CATEGORY;
    public static final String COUPON_KEY = RestConstants.COUPON;
    public static final String PRODUCT_KEY = RestConstants.PRODUCT;
    public static final String RATINGS_KEY = RestConstants.RATINGS;
    public static final String RATING_KEY = RestConstants.RATING;
    public static final String PURCHASE_KEY = "purchase";
    public static final String EMAIL_KEY = RestConstants.EMAIL;
    public static final String PAYMENT_METHOD_KEY = "payment_method";
    public static final String ORDER_NUMBER_KEY = "order_number";
    public static final String VALUE_KEY = RestConstants.VALUE;
    public static final String NAME_KEY = RestConstants.NAME;
    public static final String BRAND_KEY = RestConstants.BRAND;
    public static final String DISCOUNT_KEY = "discount";
    public static final String SUBCATEGORY_KEY = "sub_category";
    public static final String QUANTITY_KEY = RestConstants.QUANTITY;
    public static final String CARTVALUE_KEY = "cart_value";
    public static final String CATALOG_FILTER_KEY = "catalog_filter";
    public static final String TAX_KEY = "tax";
    public static final String SHIPPING_KEY = RestConstants.SHIPPING;

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SIGNUP_KEY_FOR_LOGIN = "signup_for_login";
    private static final String EUR_CURRENCY = "EUR";
    public static final String CART_COUNT = "cartCount";
    public static final String GRAND_TOTAL = "grandTotal";

    @SuppressLint("StaticFieldLeak")
    private static final Context sContext = BamiloApplication.INSTANCE.getApplicationContext();

    /**
     * Called only on resume activity
     */
    public static void onResumeActivity(long time) {
        AdjustTracker.onResume();
        trackAppOpenAdjust(sContext, time);
    }

    /**
     * Called only on pause activity
     */
    public static void onPauseActivity() {
        AdjustTracker.onPause();
    }

    /**
     * Called only on destroy activity
     */
    public static void onDestroyActivity() {
        GTMManager.get().gtmTrackAppClose();
    }


    public static void trackLoginSuccessful(Customer customer, boolean autoLogin,
            boolean fromFacebook) {
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
        params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, autoLogin);
        params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
        trackLoginSuccessful(params);
    }

    public static void trackLoginSuccessful(Bundle params) {
        TrackingEvent event;

        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean wasAutologin = params.getBoolean(AUTOLOGIN_KEY);
        String location = params.getString(LOCATION_KEY);

        if (wasAutologin) {
            event = TrackingEvent.LOGIN_AUTO_SUCCESS;
        } else {
            event = TrackingEvent.LOGIN_SUCCESS;
        }

        String customerId = "";
        if (customer != null) {
            customerId = customer.getIdAsString();
        }

        if (customer == null) {
            return;
        }

        boolean loginAfterRegister = checkLoginAfterSignup(customer);

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(event, bundle);

        storeFirstCustomer(customer);

        //GTM
        if (event.compareTo(TrackingEvent.LOGIN_AUTO_SUCCESS) == 0) {
            GTMManager.get().gtmTrackAutoLogin(customer);
        } else {
            GTMManager.get().gtmTrackLogin(customer, event, location);
        }
    }

    /**
     * Track the normal/auto login
     */
    public static void trackLoginFailed(boolean wasAutologin, String location, String method) {
        // Case login
        TrackingEvent event = TrackingEvent.LOGIN_FAIL;
        // Case autologin
        if (wasAutologin) {
            event = TrackingEvent.LOGIN_AUTO_FAIL;
        }
        // Track
        AnalyticsGoogle.get().trackEvent(event, "", 0L);

        //GTM
        if (event.compareTo(TrackingEvent.LOGIN_AUTO_FAIL) == 0) {
            GTMManager.get().gtmTrackAutoLoginFailed();
        } else {
            GTMManager.get().gtmTrackLoginFailed(location, method);
        }
    }

    public static void trackLogoutSuccessful() {
        String customerId = "";
        if (BamiloApplication.CUSTOMER != null) {
            customerId = BamiloApplication.CUSTOMER.getIdAsString();
        }

//        AnalyticsGoogle.get().trackEvent(TrackingEvent.LOGOUT_SUCCESS, customerId, 0L);
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(TrackingEvent.LOGOUT_SUCCESS, bundle);

        //GTM
        GTMManager.get().gtmTrackLogout(customerId);
        BamiloApplication.CUSTOMER = null;
    }


    public static void trackSearch(Bundle params) {
        String criteria = params.getString(SEARCH_CRITERIA_KEY);
        long results = params.getLong(SEARCH_RESULTS_KEY);

        String customerId = "";
        if (BamiloApplication.CUSTOMER != null) {
            customerId = BamiloApplication.CUSTOMER.getIdAsString();
        }
        // GA
//        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SEARCH, criteria, results);
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putParcelable(AdjustTracker.CUSTOMER, BamiloApplication.CUSTOMER);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.SEARCH_TERM, criteria);
        if (params.containsKey(AdjustTracker.CATEGORY)) {
            bundle.putString(AdjustTracker.CATEGORY, params.getString(AdjustTracker.CATEGORY));
        }

        bundle.putString(AdjustTracker.CATEGORY_ID, params.getString(AdjustTracker.CATEGORY_ID));
        AdjustTracker.get().trackEvent(TrackingEvent.SEARCH, bundle);
        //GTM
        GTMManager.get().gtmTrackSearch(criteria, results);
    }

    public static void trackShopChanged() {
        //GTM
        GTMManager.get().gtmTrackChangeCountry(ShopSelector.getShopId());
    }

    public static void trackProductRemoveFromCart(Bundle params) {
        String sku = params.getString(SKU_KEY);

        String customer_id = "";
        if (BamiloApplication.CUSTOMER != null) {
            customer_id = BamiloApplication.CUSTOMER.getIdAsString();
        }
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer_id);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, params.getDouble(PRICE_KEY));
        AdjustTracker.get().trackEvent(TrackingEvent.REMOVE_FROM_CART, bundle);

        // GTM
        GTMManager.get().gtmTrackRemoveFromCart(sku, params.getDouble(RATING_KEY),
                params.getDouble(PRICE_KEY),
                params.getLong(QUANTITY_KEY), params.getString(CARTVALUE_KEY), EUR_CURRENCY);
    }

    public static void trackCheckout(List<PurchaseCartItem> items) {
        AnalyticsGoogle.get().trackCheckout(items);
    }

    public static void trackItemShared(Intent intent, String category) {
        String sku = intent.getExtras().getString(RestConstants.SKU);
        String userId = "";
        if (BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
            userId = BamiloApplication.CUSTOMER.getIdAsString();
        }

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);
        AdjustTracker.get().trackEvent(TrackingEvent.SHARE, bundle);
        //GTM
        GTMManager.get().gtmTrackShare("", sku, category);
    }

    public static void trackAppShared() {
        String userId = "";
        if (BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
            userId = BamiloApplication.CUSTOMER.getIdAsString();
        }

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(TrackingEvent.SHARE, bundle);
        //GTM
    }

    public static void trackItemReview(Bundle params) {
        ProductComplete product = params.getParcelable(PRODUCT_KEY);
        HashMap<String, Long> ratingValues = (HashMap<String, Long>) params
                .getSerializable(RATINGS_KEY);
        String user_id = "";
        if (BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
            user_id = BamiloApplication.CUSTOMER.getIdAsString();
        }

        /*if (ratingValues != null && ratingValues.size() > 0) {
            for (Entry<String, Long> pairs : ratingValues.entrySet()) {

                // GA
                AnalyticsGoogle.get().trackRateProduct(product.getSku(), pairs.getValue(), pairs.getKey());
            }
        }*/
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, user_id);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, product.getSku());
        AdjustTracker.get().trackEvent(TrackingEvent.ADD_REVIEW, bundle);
        //GTM
        GTMManager.get().gtmTrackRateProduct(product, EUR_CURRENCY);
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
        Customer customer = BamiloApplication.CUSTOMER;
        // Validate customer
        if (customer == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(TrackingEvent.SIGNUP_SUCCESS, bundle);
        //GTM
        GTMManager.get().gtmTrackRegister(customer.getIdAsString(), location);
        storeFirstCustomer(customer);
    }

    public static void trackSignupFailed(String location) {
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_FAIL, null, 0L);
        //GTM
        GTMManager.get().gtmTrackRegisterFailed(location);
    }

    public static void trackSessionFailed(EventType eventType) {
        if (eventType == EventType.AUTO_LOGIN_EVENT) {
            TrackerDelegator.trackLoginFailed(true, GTMValues.LOGIN, GTMValues.EMAILAUTH);
        }
        if (eventType == EventType.GUEST_LOGIN_EVENT) {
            TrackerDelegator.trackSignupFailed(GTMValues.CHECKOUT);
        }
    }

    /**
     * For Web Checkout
     */
    public static void trackPurchase(final Bundle params) {
        new Thread(() -> trackPurchaseInt(params)).start();
    }

    /**
     * Track Payment Method
     */
    public static void trackPaymentMethod(String userId, String email, String payment) {
        try {
            // GA
            AnalyticsGoogle.get()
                    .trackPaymentMethod(TextUtils.isEmpty(userId) ? email : userId, payment);
            // GTM
            GTMManager.get().gtmTrackChoosePayment(payment);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * For Native Checkout
     */
    public static void trackPurchaseNativeCheckout(final Bundle params,
            final ArrayList<PurchaseCartItem> mItems) {
        new Thread(() -> trackNativeCheckoutPurchase(params, mItems)).start();
    }

    // Got Web checkout response
    private static void trackPurchaseInt(Bundle params) {
        JSONObject result = null;
        try {
            result = new JSONObject(params.getString(PURCHASE_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Validate json
        if (result == null) {
            return;
        }
        // Validate customer
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean isFirstCustomer = false;
        if (customer != null && checkFirstCustomer(customer)) {
            isFirstCustomer = true;
            removeFirstCustomer(customer);
        }
        // Create external order
        ExternalOrder order = new ExternalOrder(result);

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID,
                customer != null ? customer.getIdAsString() : AdjustTracker.NOT_AVAILABLE);
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, order.number);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, order.skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer != null && customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, order.items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, order.value);
        AdjustTracker.get().trackEvent(TrackingEvent.CHECKOUT_FINISHED, bundle);
        //GTM
        String paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        GTMManager.get().gtmTrackTransaction(order.items, EUR_CURRENCY, order.value, order.number,
                order.coupon, paymentMethod, "", "");
    }

    private static void trackNativeCheckoutPurchase(Bundle params,
            ArrayList<PurchaseCartItem> mItems) {
        String orderNr = params.getString(ORDER_NUMBER_KEY);
        double cartValue = params.getDouble(VALUE_KEY);
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String coupon = params.getString(COUPON_KEY);
        String paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        String shippingAmount = params.getString(SHIPPING_KEY);
        String taxAmount = params.getString(TAX_KEY);

        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        ArrayList<String> skus = new ArrayList<>();
        // Get number of items
        for (PurchaseItem item : items) {
            skus.add(item.sku);
        }

        boolean isFirstCustomer;
        if (customer == null) {
            isFirstCustomer = false;
        } else {
            isFirstCustomer = checkFirstCustomer(customer);
            if (isFirstCustomer) {
                removeFirstCustomer(customer);
            }
        }

        AnalyticsGoogle.get().trackPurchase(orderNr, cartValue, items);
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID,
                customer != null ? customer.getIdAsString() : AdjustTracker.NOT_AVAILABLE);
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, orderNr);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer != null && customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, (ArrayList<PurchaseItem>) items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, cartValue);
        AdjustTracker.get().trackEvent(TrackingEvent.CHECKOUT_FINISHED, bundle);
        // GTM
        GTMManager.get()
                .gtmTrackTransaction(items, EUR_CURRENCY, cartValue, orderNr, coupon, paymentMethod,
                        shippingAmount, taxAmount);
    }

    public static void removeFirstCustomer(Customer customer) {
        SharedPreferences prefs = sContext
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(customer.getEmail(), false).apply();
    }

    public static void storeFirstCustomer(Customer customer) {
        SharedPreferences prefs = sContext
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        boolean isNewCustomer = prefs.getBoolean(customer.getEmail(), true);
        if (isNewCustomer) {
            prefs.edit().putBoolean(customer.getEmail(), true).apply();
        }
    }

    private static boolean checkLoginAfterSignup(Customer customer) {
        SharedPreferences prefs = sContext
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

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
        SharedPreferences prefs = sContext
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        return !prefs.contains(customer.getEmail()) || prefs.getBoolean(customer.getEmail(), true);
    }

    /**
     * Tracking the continue shopping
     */
    public static void trackCheckoutContinueShopping(String userId) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CHECKOUT_CONTINUE, userId, 0L);
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
     * Tracking timing of screen loading
     */
    public static void trackScreenLoadTiming(int screenNameId, long start, String label) {
        // int location = params.getInt(LOCATION_KEY);
        // long start = params.getLong(START_TIME_KEY);
        // GA
        AnalyticsGoogle.get().trackLoadTimingNew(R.string.gaScreen, start, screenNameId, label);
    }

    /**
     * Tracking a page
     */
    public static void trackPage(@NonNull TrackingPage screen, long loadTime, boolean justGTM) {
        // GTM
        trackScreenGTM(screen, loadTime);
        //
        if (!justGTM) {
            // GA
            AnalyticsGoogle.get().trackPage(screen);
        }
    }

    /**
     * Tracking a page for adjust
     */
    public static void trackPageForAdjust(TrackingPage screen, Bundle bundle) {
        if (null != bundle) {
            bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
            bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
            bundle.putBoolean(AdjustTracker.DEVICE,
                    sContext.getResources().getBoolean(R.bool.isTablet));
            if (BamiloApplication.CUSTOMER != null) {
                bundle.putParcelable(AdjustTracker.CUSTOMER, BamiloApplication.CUSTOMER);
            }
            AdjustTracker.get().trackScreen(screen, bundle);
        }
    }

    public static void trackFavouriteAddedToCart(ProductRegular product, String simpleSku,
            TeaserGroupType type) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
        bundle.putDouble(TrackerDelegator.PRICE_KEY, product.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, product.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, product.getBrandName());
        bundle.putDouble(TrackerDelegator.RATING_KEY, product.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, product.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, product.getCategories());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.WISHLISTPAGE);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, type);
        trackProductAddedToCart(bundle);
    }


    public static void trackProductAddedToCart(ProductRegular product, TeaserGroupType type) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, product.getSku());
        bundle.putDouble(TrackerDelegator.PRICE_KEY, product.getPriceForTracking());
        bundle.putString(TrackerDelegator.NAME_KEY, product.getName());
        bundle.putString(TrackerDelegator.BRAND_KEY, product.getBrandName());
        bundle.putDouble(TrackerDelegator.RATING_KEY, product.getAvgRating());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, product.getMaxSavingPercentage());
        bundle.putString(TrackerDelegator.CATEGORY_KEY, product.getCategories());
        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, type);
        trackProductAddedToCart(bundle);
    }

    public static void trackProductAddedToCart(ProductDetail product) {
        Bundle bundle = new Bundle();
        bundle.putString(TrackerDelegator.SKU_KEY, product.getSku());
        bundle.putDouble(TrackerDelegator.PRICE_KEY,
                Double.parseDouble(product.getPrice().getPrice()));
        bundle.putString(TrackerDelegator.NAME_KEY, product.getTitle());
        bundle.putString(TrackerDelegator.BRAND_KEY, product.getBrand());
        bundle.putDouble(TrackerDelegator.RATING_KEY, product.getRating().getAverage());
        bundle.putDouble(TrackerDelegator.DISCOUNT_KEY,
                Double.parseDouble(product.getPrice().getDiscount_percentage()));
        try {
            bundle.putString(TrackerDelegator.CATEGORY_KEY,
                    product.getBreadcrumbs().get(0).getTarget().split("::")[1]);
        } catch (Exception e) {
            bundle.putString(TrackerDelegator.CATEGORY_KEY,
                    " ");
        }

        bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
        trackProductAddedToCart(bundle);
    }


    /**
     * Tracking a product added to cart
     */
    public static void trackProductAddedToCart(Bundle bundle) {
        // User
        String customerId =
                (BamiloApplication.CUSTOMER != null) ? BamiloApplication.CUSTOMER.getIdAsString()
                        : "";
        // Data
        String brand = bundle.getString(BRAND_KEY);
        String category = bundle.getString(CATEGORY_KEY);
        String subCategory = bundle.getString(SUBCATEGORY_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        double discount = bundle.getDouble(DISCOUNT_KEY);
        double rating = bundle.getDouble(RATING_KEY);
        String sku = bundle.getString(SKU_KEY);
        String location = bundle.getString(LOCATION_KEY);
        // GA
//        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_CART, sku, (long) price);
        //Adjust
        Bundle params = new Bundle();
        params.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        params.putString(AdjustTracker.USER_ID, customerId);
        params.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        params.putString(AdjustTracker.PRODUCT_SKU, sku);
        params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        params.putDouble(AdjustTracker.VALUE, price);
        AdjustTracker.get().trackEvent(TrackingEvent.ADD_TO_CART, params);
        //GTM
        GTMManager.get()
                .gtmTrackAddToCart(sku, price, brand, EUR_CURRENCY, discount, rating, category,
                        subCategory, location);
        //GA Banner Flow
//        if (bundle.getSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE) != null) {
//            BamiloApplication.INSTANCE.setBannerFlowSkus(sku, (TeaserGroupType) bundle
//                    .getSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE));
//        }
    }

    /**
     * Tracking a complete product
     */
    public static void trackProduct(ProductRegular product, String source, String path) {
        // Data
        String name = product.getBrandName() + " " + product.getName();
        String sku = product.getSku();
        String brand = product.getBrandName();
        String category = "";
        String subCategory = "";
        double price = product.getPriceForTracking();
        double discount = product.getMaxSavingPercentage();
        double rating = product.getAvgRating();
        // GA
        AnalyticsGoogle.get()
                .trackProduct(TrackingEvent.SHOW_PRODUCT_DETAIL, source, path, name, sku, price);
        // GTM
        GTMManager.get()
                .gtmTrackViewProduct(sku, price, brand, EUR_CURRENCY, discount, rating, category,
                        subCategory);
    }

    /**
     * Tracking a campaign
     */
    public static void trackGACampaign(Context context, String utm) {
        if (!TextUtils.isEmpty(utm)) {
            // GA
            AnalyticsGoogle.get().setGACampaign(utm);
            // GTM
            GTMManager.saveCampaignParams(context, GTMManager.CAMPAIGN_ID_KEY,
                    AnalyticsGoogle.get().getUtmParameter(utm, "utm_campaign="));
        }
    }

    /**
     * Tracking a campaign
     */
    public static void trackCampaignView(TeaserCampaign teaserCampaign) {
        // GA
        AnalyticsGoogle.get().trackGenericPage(
                teaserCampaign != null ? teaserCampaign.getTitle() : AdjustTracker.NOT_AVAILABLE);
    }

    /**
     * Tracking add product to favorites
     */
    public static void trackAddToFavorites(@NonNull ProductRegular completeProduct) {
        String productSku = completeProduct.getSku();
        String productBrand = completeProduct.getBrandName();
        double productPrice = completeProduct.getPriceForTracking();
        double averageRating = completeProduct.getAvgRating();
        double productDiscount = completeProduct.getMaxSavingPercentage();
        String categories = completeProduct.getCategories();

        String customerId = (BamiloApplication.CUSTOMER != null) ?
                BamiloApplication.CUSTOMER.getIdAsString() : "";

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putDouble(AdjustTracker.VALUE, productPrice);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        AdjustTracker.get().trackEvent(TrackingEvent.ADD_TO_WISHLIST, bundle);
        String location = GTMValues.PRODUCTDETAILPAGE;
        //GTM
        GTMManager.get()
                .gtmTrackAddToWishList(productSku, productBrand, productPrice, averageRating,
                        productDiscount, CurrencyFormatter.getCurrencyCode(), location, categories,
                        "");
    }

    public static void trackAddToFavorites(@NotNull ProductDetail completeProduct) {
        String productSku = completeProduct.getSku();
        String productBrand = completeProduct.getBrand();
        double productPrice = Double.parseDouble(completeProduct.getPrice().getPrice());
        double averageRating = completeProduct.getRating().getAverage();
        double productDiscount =
                Double.parseDouble(completeProduct.getPrice().getDiscount_percentage());

        String categories;
        try {
            categories = completeProduct.getBreadcrumbs().get(0).getTarget().split("::")[1];
        } catch (Exception e) {
            categories = " ";
        }

        String customerId = (BamiloApplication.CUSTOMER != null) ?
                BamiloApplication.CUSTOMER.getIdAsString() : "";

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putDouble(AdjustTracker.VALUE, productPrice);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        AdjustTracker.get().trackEvent(TrackingEvent.ADD_TO_WISHLIST, bundle);
        String location = GTMValues.PRODUCTDETAILPAGE;

        GTMManager.get()
                .gtmTrackAddToWishList(productSku, productBrand, productPrice, averageRating,
                        productDiscount, CurrencyFormatter.getCurrencyCode(), location, categories,
                        "");
    }

    public static void trackRemoveFromFavorites(
            @NotNull ProductDetail product) {

        String productSku = product.getSku();
        double price = Double.parseDouble(product.getPrice().getPrice());
        double averageRatingTotal = product.getRating().getAverage();

        String customerId = (BamiloApplication.CUSTOMER != null) ?
                BamiloApplication.CUSTOMER.getIdAsString() : "";

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, price);

        AdjustTracker.get().trackEvent(TrackingEvent.REMOVE_FROM_WISHLIST, bundle);

        if (averageRatingTotal != -1d) {
            GTMManager.get().gtmTrackRemoveFromWishList(productSku, price, averageRatingTotal,
                    EUR_CURRENCY);
        }
    }

    /**
     * Tracking remove product from favorites h375id
     */
    public static void trackRemoveFromFavorites(@NonNull ProductRegular product) {
        String productSku = product.getSku();
        double price = product.getPriceForTracking();
        double averageRatingTotal = product.getAvgRating();
        String customerId = (BamiloApplication.CUSTOMER != null) ?
                BamiloApplication.CUSTOMER.getIdAsString() : "";

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE,
                sContext.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, price);
        AdjustTracker.get().trackEvent(TrackingEvent.REMOVE_FROM_WISHLIST, bundle);
        //GTM
        if (averageRatingTotal != -1d) {
            GTMManager.get().gtmTrackRemoveFromWishList(productSku, price, averageRatingTotal,
                    EUR_CURRENCY);
        }
    }

    /**
     * Tracking a catalog filter
     */
    public static void trackCatalogFilter(ContentValues catalogFilterValues) {
        // Validate filters
        if (catalogFilterValues != null) {
            // GA
            String filter = catalogFilterValues.toString();
            AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_FILTER, filter, 0L);
            //GTM
            if (catalogFilterValues.containsKey(TrackerDelegator.CATALOG_FILTER_KEY)) {
                String activeFilters = catalogFilterValues
                        .getAsString(TrackerDelegator.CATALOG_FILTER_KEY);
                if (!TextUtils.isEmpty(activeFilters)) {
                    String[] filters = activeFilters.split(",");
                    for (String activefilter : filters) {
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
    }

    /**
     * Tracking a cart view for GTM
     */
    public static void trackViewCart(int quantityCart, double cartValue) {
        // GTM
        GTMManager.get().gtmTrackViewCart(quantityCart, cartValue, EUR_CURRENCY);
    }

    /**
     * Tracking add address for GTM
     */
    public static void trackAddAddress(boolean success) {
        // GTM
        GTMManager.get().gtmTrackEnterAddress(success);
    }

    /**
     * Tracking add address for GTM
     */
    public static void trackFailedPayment(String paymentMethod, PurchaseEntity order) {
        if (order != null) {
            GTMManager.get().gtmTrackFailedPayment(paymentMethod, order.getPriceForTracking(),
                    EUR_CURRENCY);
        }
    }


    private static void trackScreenGTM(TrackingPage page, long loadTime) {
        //GMT
        String screenName = "";
        if (page.getName() != -1) {
            screenName = sContext.getString(page.getName());
        }

        if (TextUtils.isNotEmpty(screenName)) {
            GTMManager.get().gtmTrackViewScreen(screenName, loadTime);
        }
    }

    public static void trackNewsletterGTM(String customerId, String location) {
        // User
        if (TextUtils.isEmpty(customerId)) {
            customerId =
                    BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString()
                            : "";
        }
        //GTM
        GTMManager.get().gtmTrackSignUp(customerId, location);
    }

    /**
     * Tracking a search query
     */
    public static void trackSearchSuggestions(String query) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SEARCH_SUGGESTIONS, query, 0L);
    }

    /**
     * Track first open and shop country
     */
    public static void trackAppOpen(Context context, boolean isFromPush) {
        // Get device info
        Bundle info = DeviceInfoHelper.getInfo(context);
        // GA
//        AnalyticsGoogle.get().setCustomData(info);
        // GTM
        String campaingId = GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_ID_KEY);
        GTMManager.saveUtmParameters(context, GTMManager.CAMPAIGN_ID_KEY, "");
        GTMManager.get().gtmTrackAppOpen(info, ShopSelector.getShopId(), campaingId,
                GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_SOURCE),
                GTMManager.getUtmParams(context, GTMManager.CAMPAIGN_MEDIUM),
                isFromPush);
    }

    /**
     *
     */
    public static void trackAppOpenAdjust(Context context, long launchtime) {
        if (ShopSelector.getShopId() == null) {
            return;
        }
        // Get device info
        Bundle info = DeviceInfoHelper.getInfo(context);
        // Adjust
        Bundle params = new Bundle(info);
        params.putLong(AdjustTracker.BEGIN_TIME, launchtime);
        params.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(TrackingEvent.APP_OPEN, params);
        GTMManager.trackAdjustInstallSource(context);
    }

    /**
     * Tracking the call button
     */
    public static void trackCall(Context context) {
        String userId = "";
        if (BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
            userId = BamiloApplication.CUSTOMER.getIdAsString();
        }
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(TrackingEvent.CALL, bundle);
    }


    /**
     * Tracking the click in overflow menu item
     */
    public static void trackOverflowMenu(TrackingEvent event) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, "", 0L);
    }

    /**
     * Tracking the switch layout in catalog
     */
    public static void trackCatalogSwitchLayout(String label) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SWITCH_LAYOUT, label, 0L);
    }

    /**
     * Track the new cart for each user interaction, add or remove.
     *
     * @author sergiopereira
     */
    public static void trackAddToCart(PurchaseEntity purchase) {
    }

    /**
     * Track the new cart for each user interaction, add or remove.
     *
     * @author sergiopereira
     */
    public static void trackRemoveFromCart(PurchaseEntity purchase) {
    }

    public static void trackAddOfferToCart(String productSku, double price) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_OFFER_TO_CART, productSku, (long) price);
    }

    public static void clearTransactionCount() {
        SharedPreferences settings = sContext
                .getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.apply();
    }

    /**
     * validate if there's any product added from a banner when finished a success order
     */

    /* farshid
    public static void trackBannerClick(final List<PurchaseItem> items) {
        final HashMap<String, String> skus = BamiloApplication.INSTANCE.getBannerFlowSkus();
        if (skus != null && skus.size() > 0 && !CollectionUtils.isEmpty(items)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (PurchaseItem item : items) {
                        if (skus.containsKey(item.sku)) {
                            Print.e(TAG, "BANNER KEY:" + item.sku + " VALUE:" + skus.get(item.sku));
                            // fires the GA event when the user finish a order, originating in one of the home teasers
                            AnalyticsGoogle.get().trackBannerFlowPurchase(skus.get(item.sku),
                                    TrackingEvent.MAIN_BANNER_CLICK.getAction(),
                                    item.sku, (long) item.getPriceForTracking());
                        }
                    }
                    BamiloApplication.INSTANCE.clearBannerFlowSkus();
                }
            }).start();
        }
    }*/

    /**
     * fires a GA event every time the user taps on one of the home teasers
     */
    public static void trackBannerClicked(TeaserGroupType groupType, String targetKey,
            int position) {
        AnalyticsGoogle.get()
                .trackEventBannerClick(getCategoryFromTeaserGroupType(groupType), targetKey,
                        position);
    }

    /**
     * this function matchs the home page teaser type with a tracking event
     */
    public static int getCategoryFromTeaserGroupType(TeaserGroupType groupType) {
        // Default value
        TrackingEvent event;
        switch (groupType) {
            case MAIN_TEASERS:
                event = TrackingEvent.MAIN_BANNER_CLICK;
                break;
            case SMALL_TEASERS:
                event = TrackingEvent.SMALL_BANNER_CLICK;
                break;
            case CAMPAIGNS:
                event = TrackingEvent.CAMPAIGNS_BANNER_CLICK;
                break;
            case SHOP_TEASERS:
                event = TrackingEvent.SHOP_BANNER_CLICK;
                break;
            case BRAND_TEASERS:
                event = TrackingEvent.BRAND_BANNER_CLICK;
                break;
            case SHOP_OF_WEEK:
                event = TrackingEvent.SHOPS_WEEK_BANNER_CLICK;
                break;
            case FEATURED_STORES:
                event = TrackingEvent.FEATURE_BANNER_CLICK;
                break;
            case TOP_SELLERS:
                event = TrackingEvent.TOP_SELLER_BANNER_CLICK;
                break;
            default:
                event = TrackingEvent.UNKNOWN_BANNER_CLICK;
                break;

        }
        return event.getCategory();
    }

    /**
     * DeepLink ReAttribution
     */
    public static void deepLinkReAttribution(@NonNull Uri uri) {
        AdjustTracker.deepLinkReAttribution(uri);
    }

    /**
     * Track catalog page Fire the track catalog page for Adjust Tracker
     */
    public static void trackCatalogPageContent(CatalogPage catalogPage, String categoryTree,
            String mainCategory) {
        if (catalogPage != null) {
            // Track Adjust screen
            Bundle bundle = new Bundle();

            if (!TextUtils.isEmpty(catalogPage.getName())) {
                bundle.putString(AdjustTracker.CATEGORY, catalogPage.getName());
            }
            if (!CollectionUtils.isEmpty(catalogPage.getProducts())) {
                bundle.putParcelableArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS,
                        catalogPage.getProducts());
            }
            if (!TextUtils.isEmpty(categoryTree)) {
                bundle.putString(AdjustTracker.TREE, categoryTree);
            }
            if (!TextUtils.isEmpty(catalogPage.getBrandId())) {
                bundle.putString(AdjustTracker.BRAND_ID, catalogPage.getBrandId());
            }
            //send content category even empty
            if (mainCategory != null) {
                bundle.putString(AdjustTracker.MAIN_CATEGORY, mainCategory);
            }

            TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_LIST_SORTED, bundle);

            // Search
            if (!TextUtils.isEmpty(catalogPage.getSearchTerm())) {
                TrackerDelegator.trackCatalogSearch(catalogPage);
            }
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

        new Thread(() -> {
            try {
                TrackerDelegator.trackSearch(bundle);
            } catch (NullPointerException ignored) {
            }
        }).start();
    }

    /**
     * Track Checkout Step
     */
    public static void trackCheckoutStep(TrackingEvent step) {
        try {
            String email = BamiloApplication.INSTANCE.getCustomerUtils().getEmail();
            String userId =
                    BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString()
                            : "";
            AnalyticsGoogle.get().trackEvent(step, TextUtils.isEmpty(userId) ? email : userId, 0L);
        } catch (NullPointerException ignored) {
        }
    }

    public static void trackAddToCartGTM(PurchaseCartItem item, int quantity,
            String mItemRemovedCartValue) {
        try {
            double prods = item.getQuantity();
            Bundle params = new Bundle();

            params.putString(TrackerDelegator.SKU_KEY, item.getConfigSimpleSKU());

            params.putLong(TrackerDelegator.START_TIME_KEY, System.currentTimeMillis());
            params.putDouble(TrackerDelegator.PRICE_KEY, item.getPriceForTracking());
            params.putLong(TrackerDelegator.QUANTITY_KEY, 1);
            params.putDouble(TrackerDelegator.RATING_KEY, -1d);
            params.putString(TrackerDelegator.NAME_KEY, item.getName());
            params.putString(TrackerDelegator.CATEGORY_KEY, item.getCategories());
            params.putString(TrackerDelegator.CARTVALUE_KEY, mItemRemovedCartValue);

            if (quantity > prods) {
                prods = quantity - prods;
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.SHOPPINGCART);
                for (int i = 0; i < prods; i++) {
                    TrackerDelegator.trackProductAddedToCart(params);
                }
            } else {
                prods = prods - quantity;
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                for (int i = 0; i < prods; i++) {
                    TrackerDelegator.trackProductRemoveFromCart(params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void trackPurchase(CheckoutFinish checkoutFinish, PurchaseEntity cart) {
    }

    public static void trackCustomerInfo(@NonNull Customer customer) {
    }

    /**
     * Tracking cart in checkout thanks
     */
    public static void trackPurchaseInCheckoutThanks(PurchaseEntity cart, String order,
            double total, String shipping, String tax, String payment) {
        if (cart != null && CollectionUtils.isNotEmpty(cart.getCartItems())) {
            Bundle params = new Bundle();
            params.putString(TrackerDelegator.ORDER_NUMBER_KEY, order);
            params.putDouble(TrackerDelegator.VALUE_KEY, cart.getPriceForTracking());
            params.putString(TrackerDelegator.EMAIL_KEY,
                    BamiloApplication.INSTANCE.getCustomerUtils().getEmail());
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, BamiloApplication.CUSTOMER);
            params.putString(TrackerDelegator.COUPON_KEY, String.valueOf(cart.getCouponDiscount()));
            params.putInt(TrackerDelegator.CART_COUNT, cart.getCartCount());
            params.putDouble(TrackerDelegator.GRAND_TOTAL, total);
            if (!TextUtils.isEmpty(shipping) && !TextUtils.isEmpty(tax) && !TextUtils
                    .isEmpty(payment)) {
                params.putString(TrackerDelegator.SHIPPING_KEY, shipping);
                params.putString(TrackerDelegator.TAX_KEY, tax);
                params.putString(TrackerDelegator.PAYMENT_METHOD_KEY, payment);
            }
            TrackerDelegator.trackPurchaseNativeCheckout(params, cart.getCartItems());
        }
    }

    /**
     * Tracking event for the External Link Click
     */
    public static void trackClickOnExternalLink(@NonNull String label) {
        AnalyticsGoogle.get()
                .trackEventClickOnExternalLink(TrackingEvent.EXTERNAL_LINK_CLICK, label);
    }

    public static void trackOpenPushNotification() {
    }

    public static void trackSearch(String searchTerm) {
        AnalyticsGoogle.get().sendEvent(CATEGORY_SEARCH_RESULTS, ACTION_SEARCH, searchTerm, -1);
    }

    public static void trackSearchSuggestion(String categoryName, String productName) {
        AnalyticsGoogle.get().sendEvent(CATEGORY_SEARCH_SUGGESTIONS, ACTION_TAPPED,
                String.format(Locale.US, "%s/%s", productName, categoryName), -1);
    }

    public static void trackVoiceSearch(String searchTerm) {
        AnalyticsGoogle.get().sendEvent(CATEGORY_SEARCH_RESULTS, ACTION_TAPPED, searchTerm, -1);
    }

    public static void trackComponentViewTap(String page, String componentName, String target) {
        AnalyticsGoogle.get()
                .sendEvent(String.format(Locale.US, "%s+%s", page, componentName), ACTION_TAPPED,
                        target, -1);
    }

    public static void trackEmarsysRecommendation(String screenName, String logic) {
        String CATEGORY_EMARSYS = "Emarsys";
        AnalyticsGoogle.get().sendEvent(CATEGORY_EMARSYS, ACTION_TAPPED,
                String.format(Locale.US, "%s-%s", screenName, logic), -1);
    }
}
