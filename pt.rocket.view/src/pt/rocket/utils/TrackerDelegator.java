package pt.rocket.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.Ad4PushTracker;
import pt.rocket.framework.tracking.AdXTracker;
import pt.rocket.framework.tracking.AnalyticsGoogle;
import pt.rocket.framework.tracking.MixpanelTracker;
import pt.rocket.framework.tracking.TrackingEvents;
import pt.rocket.framework.tracking.TrackingPages;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.Utils;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

public class TrackerDelegator {
    private final static String TAG = TrackerDelegator.class.getSimpleName();

    public static final String CUSTOMER_KEY = "customer";
    public static final String AUTOLOGIN_KEY = "auto_login";
    public static final String ORIGIN_KEY = "origin";
    public static final String FACEBOOKLOGIN_KEY = "facebook_login";
    public static final String SEARCH_CRITERIA_KEY = "search_criteria";
    public static final String SEARCH_RESULTS_KEY = "search_results";
    public static final String SORT_KEY = "sort";
    public static final String SKU_KEY = "sku";
    public static final String PRICE_KEY = "price";
    public static final String LOCATION_KEY = "location";
    public static final String START_TIME_KEY = "start";
    public static final String CATEGORY_KEY = "category";
    public static final String COUPON_KEY = "coupon";
    public static final String PAGE_NUMBER_KEY = "page_number";
    public static final String PRODUCT_KEY = "product";
    public static final String REVIEW_KEY = "review";
    public static final String RATINGS_KEY = "ratings";
    public static final String RATING_KEY = "rating";
    public static final String PURCHASE_KEY = "purchase";
    public static final String EMAIL_KEY = "email";
    public static final String GA_STEP_KEY = "ga_step";
    public static final String ADX_STEP_KEY = "adx_step";
    public static final String MIXPANEL_STEP_KEY = "mixpanel_step";
    public static final String PAYMENT_METHOD_KEY = "payment_method";
    public static final String ERROR_KEY = "error";
    public static final String ORDER_NUMBER_KEY = "order_number";
    public static final String VALUE_KEY = "value";
    public static final String ITEMS_KEY = "items";
    public static final String SOURCE_KEY = "source";
    public static final String PATH_KEY = "path";
    public static final String NAME_KEY = "name";
    public static final String URL_KEY = "url";
    public static final String SIMPLE_KEY = "simple";
    public static final String BRAND_KEY = "brand";

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SIGNUP_KEY_FOR_LOGIN = "signup_for_login";
    private static final String SIGNUP_KEY_FOR_CHECKOUT = "signup_for_checkout";

    private static final String JSON_TAG_ORDER_NR = "orderNr";
    private static final String JSON_TAG_GRAND_TOTAL = "grandTotal";
    private static final String JSON_TAG_ITEMS_JSON = "itemsJson";

    private static final Context context = JumiaApplication.INSTANCE.getApplicationContext();

    public final static void trackLoginSuccessful(Bundle params) {
        String mOrigin;
        int resLogin;

        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean wasAutologin = params.getBoolean(AUTOLOGIN_KEY);
        String origin = params.getString(ORIGIN_KEY);
        boolean wasFacebookLogin = params.getBoolean(FACEBOOKLOGIN_KEY);

        if (wasFacebookLogin) {
            resLogin = R.string.gfacebookloginsuccess;
            mOrigin = origin;
        } else if (wasAutologin) {
            resLogin = R.string.gautologinsuccess;
            mOrigin = context.getString(R.string.mixprop_loginlocationautologin);
        } else {
            resLogin = R.string.gloginsuccess;
            mOrigin = origin;
        }

        if (mOrigin == null || mOrigin.length() == 0) {
            mOrigin = context.getString(R.string.mixprop_loginlocationsidemenu);
        }
        String customer_id = "";
        if (customer != null) {
            customer_id = customer.getIdAsString();
        }
        AnalyticsGoogle.get().trackAccount(resLogin, customer_id);

        if (customer == null) {
            return;
        }

        boolean loginAfterRegister = checkLoginAfterSignup(customer);
        Log.d(TAG, "trackLoginSuccessul: loginAfterRegister = " + loginAfterRegister + " wasAutologin = " + wasAutologin);

        // PushManager.shared().setAlias(customer.getIdAsString());
        if (wasFacebookLogin) {
            MixpanelTracker.loginWithFacebook(context, customer.getIdAsString(), mOrigin, customer.getCreatedAt());
            AdXTracker.facebookLogin(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
            Ad4PushTracker.get().trackFacebookConnect(customer.getIdAsString());
        } else {
            MixpanelTracker.login(context, customer.getIdAsString(), mOrigin, customer.getCreatedAt());
            AdXTracker.login(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
        }
        Ad4PushTracker.get().trackLogin(customer.getIdAsString(), customer.getFirstName());
    }

    /**
     * 
     * @param wasAutologin
     */
    public final static void trackLoginFailed(boolean wasAutologin) {
        Log.i(TAG, "trackAccount: autologin " + wasAutologin);
        // Case login
        int resLogin = R.string.gloginfailed;
        // Case autologin
        if (wasAutologin) resLogin = R.string.gautologinfailed;
        // Track
        AnalyticsGoogle.get().trackAccount(resLogin, null);
    }

    public final static void trackLogoutSuccessful() {
        MixpanelTracker.logout(context);
        String customer_id = "";
        if (JumiaApplication.CUSTOMER != null) {
            customer_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.logout(context, JumiaApplication.SHOP_NAME, customer_id);
        int trackRes = R.string.glogoutsuccess;
        AnalyticsGoogle.get().trackAccount(trackRes, customer_id);

        JumiaApplication.CUSTOMER = null;
    }

    public final static void trackSearch(Bundle params) {
        String criteria = params.getString(SEARCH_CRITERIA_KEY);
        long results = params.getLong(SEARCH_RESULTS_KEY);

        MixpanelTracker.search(context, criteria, results);
        AnalyticsGoogle.get().trackSearch(criteria, results);
        Ad4PushTracker.get().trackSearch(criteria);
    }

    public final static void trackSearchViewSortMade(Bundle params) {
        String criteria = params.getString(SEARCH_CRITERIA_KEY);
        long results = params.getLong(SEARCH_RESULTS_KEY);
        String sort = params.getString(SORT_KEY);

        MixpanelTracker.searchViewSorted(context, criteria, results, sort);
    }

    public final static void trackShopchanged() {
        MixpanelTracker.changeShop(context, ShopSelector.getShopId());
        Ad4PushTracker.get().trackCountryChange(ShopSelector.getShopId());
    }

    public final static void trackPushNotificationsEnabled(boolean enabled) {
        MixpanelTracker.setPushNotification(context, enabled);
    }

    // public final static void trackProductAddedToCart(Context context,
    // CompleteProduct product, ProductSimple simple, Double price, String
    // priceAsString, String location) {
    // MixpanelTracker.productAddedToCart(context, product, simple, price,
    // location);
    // String customer_id = "";
    // if(JumiaApplication.CUSTOMER != null){
    // customer_id = JumiaApplication.CUSTOMER.getIdAsString();
    // }
    // AdXTracker.trackAddToCart(context, priceAsString, customer_id,
    // simple.getAttributeByKey(ProductSimple.SKU_TAG),
    // JumiaApplication.SHOP_NAME);
    // }

    public final static void trackProductRemoveFromCart(Bundle params) {
        String sku = params.getString(SKU_KEY);
        String priceAsString = params.getString(PRICE_KEY);

        String customer_id = "";
        if (JumiaApplication.CUSTOMER != null) {
            customer_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.trackRemoveFromCart(context, priceAsString, customer_id, sku, JumiaApplication.SHOP_NAME);
    }

    public final static void trackCheckout(List<ShoppingCartItem> items) {
        MixpanelTracker.checkout(context, items);
        AnalyticsGoogle.get().trackCheckout(items);
    }

    public final static void trackViewCart(int numberItems) {
        MixpanelTracker.viewCart(context, numberItems);
    }

    public final static void trackItemShared(Intent intent) {
        MixpanelTracker.share(context, intent);
        String sku = intent.getExtras().getString(RestConstants.JSON_SKU_TAG);
        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.trackShare(context, sku, user_id, JumiaApplication.SHOP_NAME);
        AnalyticsGoogle.get().trackShare(context, sku, user_id, JumiaApplication.SHOP_NAME);
        Ad4PushTracker.get().trackSocialShare();
    }

    public final static void trackCategoryView(Bundle params) {
        // Data
        String category = params.getString(CATEGORY_KEY);
        int page = params.getInt(PAGE_NUMBER_KEY);
        String location = params.getString(LOCATION_KEY);
        // MIX
        MixpanelTracker.listCategory(context, category, page);
        // AD4Push
        Ad4PushTracker.get().trackCategorySelection();
        // GA
        AnalyticsGoogle.get().trackCategory(location, category);
    }

    public final static void trackItemReview(Bundle params) {

        CompleteProduct product = params.getParcelable(PRODUCT_KEY);
        ProductReviewCommentCreated review = params.getParcelable(REVIEW_KEY);
        @SuppressWarnings("unchecked")
        HashMap<String, Double> ratings = (HashMap<String, Double>) params.getSerializable(RATINGS_KEY);

        MixpanelTracker.rate(context, product, review, ratings);
        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.trackProductRate(context, product.getSku(), review, user_id, JumiaApplication.SHOP_NAME);
        for (Entry<String, Double> option : review.getRating().entrySet()) {
            Long priceLong;
            try {
                priceLong = option.getValue().longValue();
            } catch (NumberFormatException e) {
                priceLong = 0l;
            } catch (NullPointerException e) {
                priceLong = 0l;
            }
            AnalyticsGoogle.get().trackRateProduct(context, product.getSku(), priceLong, option.getKey());
        }

    }

    public final static void trackViewReview(Bundle params) {
        CompleteProduct product = params.getParcelable(PRODUCT_KEY);
        float rate = params.getFloat(RATING_KEY);

        MixpanelTracker.viewRate(context, product, rate);
    }

    public final static void trackSignupSuccessful(Bundle params) {
        Customer customer = params.getParcelable(CUSTOMER_KEY);

        String customer_id = "";
        if (customer != null) {
            customer_id = customer.getIdAsString();
        }
        AnalyticsGoogle.get().trackAccount(R.string.gcreatesuccess, customer_id);
        if (customer == null) {
            return;
        }

        AdXTracker.signup(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
        // PushManager.shared().setAlias(customer.getIdAsString());
        Ad4PushTracker.get().trackSignup(customer.getIdAsString(), customer.getGender().toString());
        storeSignupProcess(customer);
    }

    public static void trackSignupFailed() {
        AnalyticsGoogle.get().trackAccount(R.string.gcreatefailed, null);
    }

    public static void trackPurchase(final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                trackPurchaseInt(params);
            }

        }).run();
    }

    /**
     * Track Checkout Step
     * 
     * @param context
     * @param result
     * @param customer
     */
    public static void trackCheckoutStep(Bundle params) {

        String email = params.getString(EMAIL_KEY);
        final int gstep = params.getInt(GA_STEP_KEY);
        final int xstep = params.getInt(ADX_STEP_KEY);
        final int mixstep = params.getInt(MIXPANEL_STEP_KEY);

        final String hashedemail = Utils.cleanMD5(email);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user_id = hashedemail;
                if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null
                        && JumiaApplication.CUSTOMER.getIdAsString().length() > 0) {
                    user_id = JumiaApplication.CUSTOMER.getIdAsString();
                }
                AnalyticsGoogle.get().trackCheckoutStep(user_id, gstep);
                AdXTracker.trackCheckoutStep(context, JumiaApplication.SHOP_NAME, user_id, xstep);
                MixpanelTracker.trackCheckoutStep(context, user_id, mixstep);
            }

        }).run();
    }

    public static void trackSignUp(String email) {
        final String hashedemail = Utils.cleanMD5(email);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user = hashedemail;
                if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null
                        && JumiaApplication.CUSTOMER.getIdAsString().length() > 0) {
                    user = JumiaApplication.CUSTOMER.getIdAsString();
                }
                AnalyticsGoogle.get().trackSignUp(user);
                AdXTracker.trackSignUp(context, JumiaApplication.SHOP_NAME, user);
                MixpanelTracker.trackSignUp(context, user);
            }

        }).run();
    }

    /**
     * Track Payment Method
     * 
     * @param context
     * @param email
     * @param payment
     */
    public static void trackPaymentMethod(Bundle params) {
        String email = params.getString(EMAIL_KEY);
        final String payment = params.getString(PAYMENT_METHOD_KEY);

        final String hashedemail = Utils.cleanMD5(email);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user_id = hashedemail;
                if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null
                        && JumiaApplication.CUSTOMER.getIdAsString().length() > 0) {
                    user_id = JumiaApplication.CUSTOMER.getIdAsString();
                }
                AnalyticsGoogle.get().trackPaymentMethod(user_id, payment);
                AdXTracker.trackPaymentMethod(context, JumiaApplication.SHOP_NAME, user_id, payment);
                MixpanelTracker.trackPaymentMethod(context, user_id, payment);
            }

        }).run();
    }

    public static void trackNativeCheckoutError(Bundle params) {
        String email = params.getString(EMAIL_KEY);
        final String error = params.getString(ERROR_KEY);

        final String hashedemail = Utils.cleanMD5(email);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user_id = hashedemail;
                if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null
                        && JumiaApplication.CUSTOMER.getIdAsString().length() > 0) {
                    user_id = JumiaApplication.CUSTOMER.getIdAsString();
                }
                AnalyticsGoogle.get().trackNativeCheckoutError(user_id, error);
                AdXTracker.trackNativeCheckoutError(context, JumiaApplication.SHOP_NAME, user_id, error);
                MixpanelTracker.trackNativeCheckoutError(context, user_id, error);
            }

        }).run();
    }

    public static void trackPurchaseNativeCheckout(final Bundle params, final Map<String, ShoppingCartItem> mItems) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                trackNativeCheckoutPurchase(params, mItems);
            }

        }).run();
    }

    // Got checkout response

    private static void trackPurchaseInt(Bundle params) {
        JSONObject result = null;
        try {
            result = new JSONObject(params.getString(PURCHASE_KEY));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        Customer customer = params.getParcelable(CUSTOMER_KEY);

        Log.i(TAG, "TRACK SALE: STARTED");
        if (result == null) {
            return;
        }

        Log.d(TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());

        Log.d(TAG, "TRACK SALE: JSON " + result.toString());

        String orderNr;
        String value;
        JSONObject itemsJson;
        String coupon = "";
        try {
            orderNr = result.getString(JSON_TAG_ORDER_NR);
            value = result.getString(JSON_TAG_GRAND_TOTAL);
            itemsJson = result.getJSONObject(JSON_TAG_ITEMS_JSON);
            Log.d(TAG, "TRACK SALE: RESULT: ORDER=" + orderNr + " VALUE=" + value + " ITEMS=" + result.toString(2));
        } catch (JSONException e) {
            Log.e(TAG, "TRACK SALE: json parsing error: ", e);
            return;
        }

        Double averageValue = 0d;
        ArrayList<String> favoritesSKU = FavouriteTableHelper.getFavouriteSKUList();

        List<PurchaseItem> items = PurchaseItem.parseItems(itemsJson);
        ArrayList<String> skus = new ArrayList<String>();
        int favoritesCount = 0;
        for (PurchaseItem item : items) {
            skus.add(item.sku);
            averageValue += item.paidpriceAsDouble;
            favoritesCount += favoritesSKU.contains(item.sku) ? 1 : 0;
        }
        AnalyticsGoogle.get().trackSales(orderNr, value, items);

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // AdXTracker.trackSale(context, value);

            // Send the track sale without customer id
            String customerId = "";
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, false);

        } else {
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(customer);

            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);

            AdXTracker.trackSale(context, value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, customer.isGuest());
        }

        MixpanelTracker.trackSale(context, value, items);
        Ad4PushTracker.get().trackCheckoutEnded(orderNr, Double.parseDouble(value), averageValue, items.size(), coupon, favoritesCount);

    }

    private static void trackNativeCheckoutPurchase(Bundle params, Map<String, ShoppingCartItem> mItems) {
        String order_nr = params.getString(ORDER_NUMBER_KEY);
        String value = params.getString(VALUE_KEY);
        String email = params.getString(EMAIL_KEY);
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String coupon = params.getString(COUPON_KEY);

        Double averageValue = 0d;

        Log.i(TAG, "TRACK SALE: STARTED");
        Log.d(TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        Log.d(TAG, "TRACK SALE: JSON " + order_nr);

        ArrayList<String> favoritesSKU = FavouriteTableHelper.getFavouriteSKUList();

        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        ArrayList<String> skus = new ArrayList<String>();
        int favoritesCount = 0;
        for (PurchaseItem item : items) {
            skus.add(item.sku);
            averageValue += item.paidpriceAsDouble;
            favoritesCount += favoritesSKU.contains(item.sku) ? 1 : 0;
        }

        AnalyticsGoogle.get().trackSales(order_nr, value, items);

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // AdXTracker.trackSale(context, value);

            // Send the track sale without customer id
            String customerId = Utils.cleanMD5(email);
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, value, customerId, order_nr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, false);

        } else {
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(customer);

            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);

            AdXTracker.trackSale(context, value, customerId, order_nr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, customer.isGuest());
        }

        MixpanelTracker.trackSale(context, value, items);
        // String transactionId, Double cartValue, Double average, String
        // category, int orderCount, String coupon
        Ad4PushTracker.get().trackCheckoutEnded(order_nr, Double.parseDouble(value), averageValue, mItems.size(), coupon, favoritesCount);

    }

    public static void storeSignupProcess(Customer customer) {
        Log.d(TAG, "storing signup tags");
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SIGNUP_KEY_FOR_LOGIN, customer.getEmail()).putString(SIGNUP_KEY_FOR_CHECKOUT, customer.getEmail()).commit();
    }

    private static boolean checkLoginAfterSignup(Customer customer) {
        return checkKeyAfterSignup(customer, SIGNUP_KEY_FOR_LOGIN);
    }

    private static boolean checkCheckoutAfterSignup(Customer customer) {
        return checkKeyAfterSignup(customer, SIGNUP_KEY_FOR_CHECKOUT);
    }

    private static boolean checkKeyAfterSignup(Customer customer, String key) {
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        if (!prefs.contains(key)) {
            return false;
        }

        String email = prefs.getString(key, "");
        if (!email.equals(customer.getEmail())) {
            return false;
        }

        prefs.edit().remove(key).commit();
        return true;
    }

    /**
     * Tracking the continue shopping
     * 
     * @param context
     * @param userId
     */
    public static void trackCheckoutContinueShopping(String userId) {
        // GA
        AnalyticsGoogle.get().trackCheckoutContinueShopping(context, userId);
    }

    /**
     * Tracking the start of checkout
     * 
     * @param context
     * @param userId
     */
    public static void trackCheckoutStart(String userId) {
        // GA
        AnalyticsGoogle.get().trackCheckoutStart(context, userId);
        // AD4Push
        Ad4PushTracker.get().trackCheckoutStarted();
    }

    /**
     * Tracking a timing
     * 
     * @param location
     * @param start
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
     * @param bundle
     */
    public static void trackPage(TrackingPages screen) {
        AnalyticsGoogle.get().trackPage(screen);
        Ad4PushTracker.get().trackScreen(screen);
    }

    /**
     * Tracking a product added to cart
     * 
     * @param context
     * @param bundle
     */
    public static void trackProductAddedToCart(Bundle bundle) {
        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";

        // Data
        long price = bundle.getLong(PRICE_KEY);
        String sku = bundle.getString(SKU_KEY);
        String name = bundle.getString(NAME_KEY);
        String brand = bundle.getString(BRAND_KEY);
        String category = bundle.getString(CATEGORY_KEY);
        String location = bundle.getString(LOCATION_KEY);

        // if (null != product.getCategories() && 0 <
        // product.getCategories().size()) {
        // category = product.getCategories().get(0);
        // }
        // GA
        AnalyticsGoogle.get().trackAddToCart(sku, price);
        // Mix
        MixpanelTracker.productAddedToCart(context, sku, name, brand, category, (double) price, location);
        // Adx
        AdXTracker.trackAddToCart(context, "" + price, customerId, sku, JumiaApplication.SHOP_NAME);
        // AD4Push
        Ad4PushTracker.get().trackAddToCart(sku, (double) price, name, category);
    }

    /**
     * Tracking a complete product
     * 
     * @param context
     * @param bundle
     */
    public static void trackProduct(Bundle bundle) {
        // Data
        int prefix = bundle.getInt(SOURCE_KEY);
        String path = bundle.getString(PATH_KEY);
        String name = bundle.getString(NAME_KEY);
        String sku = bundle.getString(SKU_KEY);
        String url = bundle.getString(URL_KEY);
        CompleteProduct product = bundle.getParcelable(PRODUCT_KEY);
        String category = bundle.getString(CATEGORY_KEY);
        // GA
        AnalyticsGoogle.get().trackProduct(prefix, path, name, sku, url, product.getPriceAsDouble());
        // MIX
        MixpanelTracker.product(context, product, category);
    }

    /**
     * Tracking a campaign
     * 
     * @param utm
     */
    public static void trackCampaign(String utm) {
        // GA
        AnalyticsGoogle.get().setCampaign(utm);
    }

    /**
     * Tracking a campaign
     * @param name the name of campaign
     */
    public static void trackCampaignsView(String name) {
        // AD4Push
        Ad4PushTracker.get().trackCampaignsView();
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvents.SHOW_CAMPAIGN, name, 0l);
    }

    /**
     * Tracking add product to favorites
     * 
     * @param productSku
     */
    public static void trackAddToFavorites(String productSku) {
        Ad4PushTracker.get().trackAddToFavorites(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvents.ADD_TO_WISHLIST, productSku, 0l);
    }

    /**
     * Tracking remove product from favorites
     * 
     * @param productSku
     */
    public static void trackRemoveFromFavorites(String productSku) {
        Ad4PushTracker.get().trackRemoveFromWishlist(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvents.REMOVE_FROM_WISHLIST, productSku, 0l);
    }

    /**
     * Tracking a catalog filter
     * 
     * @param mCatalogFilterValues
     * @param searchQuery
     * @param searchQuery
     */
    public static void trackCatalogFilter(ContentValues catalogFilterValues) {
        // GA
        AnalyticsGoogle.get().trackCatalogFilter(catalogFilterValues);
        // AD4Push
        Ad4PushTracker.get().trackCatalogFilter(catalogFilterValues);

    }

    /**
     * Tracking newsletter subscription
     * 
     * @param subscribe
     */
    public static void trackNewsletterSubscription(boolean subscribe) {
        // User
        String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        // GA
        TrackingEvents event = (subscribe) ? TrackingEvents.SUBSCRIBE_NEWSLETTER : TrackingEvents.UNSUBSCRIBE_NEWSLETTER;
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
    }

    /**
     * Tracking a search query
     * 
     * @param query
     */
    public static void trackSearchSuggestions(String query) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvents.SEARCH_SUGGESTIONS, query, 0l);
    }

    public static void trackAppOpen() {
        //AD4Push
        Ad4PushTracker.get().trackAppFirstOpen();
    }
    
}
