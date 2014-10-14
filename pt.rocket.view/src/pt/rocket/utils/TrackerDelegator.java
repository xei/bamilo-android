package pt.rocket.utils;

import java.util.ArrayList;
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
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.Utils;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

public class TrackerDelegator {
    private final static String TAG = TrackerDelegator.class.getSimpleName();
    
    public static final boolean IS_AUTO_LOGIN = true;
    public static final boolean ISNT_AUTO_LOGIN = false;

    public static final String CUSTOMER_KEY = "customer";
    public static final String AUTOLOGIN_KEY = "auto_login";
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
    public static final String RELATED_ITEM = "related_item";

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SIGNUP_KEY_FOR_LOGIN = "signup_for_login";
    private static final String SIGNUP_KEY_FOR_CHECKOUT = "signup_for_checkout";

    private static final String JSON_TAG_ORDER_NR = "orderNr";
    private static final String JSON_TAG_GRAND_TOTAL = "grandTotal";
    private static final String JSON_TAG_GRAND_TOTAL_CONVERTED = "grandTotal_euroConverted";
    private static final String JSON_TAG_ITEMS_JSON = "itemsJson";

    private static final Context context = JumiaApplication.INSTANCE.getApplicationContext();

    
    public final static void trackLoginSuccessful(Bundle params) {
        TrackingEvent event;

        Customer customer = params.getParcelable(CUSTOMER_KEY);
        boolean wasAutologin = params.getBoolean(AUTOLOGIN_KEY);
        boolean wasFacebookLogin = params.getBoolean(FACEBOOKLOGIN_KEY);

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
        Log.d(TAG, "trackLoginSuccessul: loginAfterRegister = " + loginAfterRegister + " wasAutologin = " + wasAutologin);

        // PushManager.shared().setAlias(customer.getIdAsString());
        if (wasFacebookLogin) {
            AdXTracker.facebookLogin(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
            Ad4PushTracker.get().trackFacebookConnect(customer.getIdAsString());
        } else {
            AdXTracker.login(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
        }
        Ad4PushTracker.get().trackLogin(customer.getIdAsString(), customer.getFirstName());
    }
    
    /**
     * Track the normal/auto login
     * @param wasAutologin
     */
    public final static void trackLoginFailed(boolean wasAutologin) {
        Log.i(TAG, "trackAccount: autologin " + wasAutologin);
        // Case login
        TrackingEvent event = TrackingEvent.LOGIN_FAIL;
        // Case autologin
        if (wasAutologin) event = TrackingEvent.LOGIN_AUTO_FAIL;
        // Track
        AnalyticsGoogle.get().trackEvent(event, "", 0l);
    }

    public final static void trackLogoutSuccessful() {
        String customerId = "";
        if (JumiaApplication.CUSTOMER != null) {
            customerId = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.logout(context, JumiaApplication.SHOP_NAME, customerId);
        
        AnalyticsGoogle.get().trackEvent(TrackingEvent.LOGOUT_SUCCESS, customerId, 0l);

        JumiaApplication.CUSTOMER = null;
    }

    
    public final static void trackSearch(Bundle params) {
        String criteria = params.getString(SEARCH_CRITERIA_KEY);
        long results = params.getLong(SEARCH_RESULTS_KEY);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SEARCH, criteria, results);
        // AD4P
        Ad4PushTracker.get().trackSearch(criteria);
    }

    
    public final static void trackShopchanged() {
        Ad4PushTracker.get().trackCountryChange(ShopSelector.getShopId());
    }
    

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
        AnalyticsGoogle.get().trackCheckout(items);
    }

    
    public final static void trackItemShared(Intent intent) {
        String sku = intent.getExtras().getString(RestConstants.JSON_SKU_TAG);
        String userId = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            userId = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.trackShare(context, sku, userId, JumiaApplication.SHOP_NAME);
        AnalyticsGoogle.get().trackShare(context, sku, userId, JumiaApplication.SHOP_NAME);
        Ad4PushTracker.get().trackSocialShare();
    }

    public final static void trackCategoryView(Bundle params) {
        // Data
        String category = params.getString(CATEGORY_KEY);
        //int page = params.getInt(PAGE_NUMBER_KEY);
        TrackingEvent event = (TrackingEvent) params.getSerializable(LOCATION_KEY);
        // AD4Push
        Ad4PushTracker.get().trackCategorySelection();
        // GA
        AnalyticsGoogle.get().trackEvent(event, category, 0l);
    }

    
    public final static void trackItemReview(Bundle params) {

        CompleteProduct product = params.getParcelable(PRODUCT_KEY);
        ProductReviewCommentCreated review = params.getParcelable(REVIEW_KEY);

        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        AdXTracker.trackProductRate(context, product.getSku(), review, user_id, JumiaApplication.SHOP_NAME);
        for (Entry<String, Double> option : review.getRating().entrySet()) {
            Long ratingValue;
            try {
                ratingValue = option.getValue().longValue();
            } catch (NumberFormatException e) {
                ratingValue = 0l;
            } catch (NullPointerException e) {
                ratingValue = 0l;
            }
            
            AnalyticsGoogle.get().trackRateProduct(context, product.getSku(), ratingValue, option.getKey());
        }

    }


    public final static void trackSignupSuccessful(Bundle params) {
        Customer customer = params.getParcelable(CUSTOMER_KEY);

        String customer_id = "";
        if (customer != null) {
            customer_id = customer.getIdAsString();
        }
        
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_SUCCESS, customer_id, 0l);
        
        if (customer == null) {
            return;
        }

        AdXTracker.signup(context, JumiaApplication.SHOP_NAME, customer.getIdAsString());
        Ad4PushTracker.get().trackSignup(customer.getIdAsString(), customer.getGender().toString());
        storeSignupProcess(customer);
    }

    public static void trackSignupFailed() {
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_FAIL, null, 0l);
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
     * Track Checkout Step
     * 
     * @param context
     * @param result
     * @param customer
     */
    public static void trackCheckoutStep(Bundle params) {

        String email = params.getString(EMAIL_KEY);
        final TrackingEvent event = (TrackingEvent) params.getSerializable(GA_STEP_KEY);
        final int xstep = params.getInt(ADX_STEP_KEY);

        final String hashedemail = Utils.cleanMD5(email);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user_id = hashedemail;
                if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null
                        && JumiaApplication.CUSTOMER.getIdAsString().length() > 0) {
                    user_id = JumiaApplication.CUSTOMER.getIdAsString();
                }
                
                AnalyticsGoogle.get().trackEvent(event, user_id, 0l);
                
                AdXTracker.trackCheckoutStep(context, JumiaApplication.SHOP_NAME, user_id, xstep);
            }

        }).start();
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
                // GA
                AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP, user, 0l);
                // ADX
                AdXTracker.trackSignUp(context, JumiaApplication.SHOP_NAME, user);
            }

        }).start();
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
            }

        }).start();
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
            }

        }).start();
    }

    /**
     * For Native Checkout
     */
    public static void trackPurchaseNativeCheckout(final Bundle params, final Map<String, ShoppingCartItem> mItems) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                trackNativeCheckoutPurchase(params, mItems);
            }

        }).start();
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
        double value;
        JSONObject itemsJson;
        String coupon = "";
        double valueConverted = 0d;
        try {
            orderNr = result.getString(JSON_TAG_ORDER_NR);
            value = result.getDouble(JSON_TAG_GRAND_TOTAL);
            itemsJson = result.getJSONObject(JSON_TAG_ITEMS_JSON);
            valueConverted = result.optDouble(JSON_TAG_GRAND_TOTAL_CONVERTED, 0d);
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
        averageValue = averageValue / items.size();
        
        AnalyticsGoogle.get().trackPurchase(orderNr, valueConverted, items);

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // AdXTracker.trackSale(context, value);

            // Send the track sale without customer id
            String customerId = "";
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, "" + value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, false);

        } else {
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(customer);

            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);

            AdXTracker.trackSale(context, "" + value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, customer.isGuest());
        }

        Ad4PushTracker.get().trackCheckoutEnded(orderNr, value, averageValue, items.size(), coupon, favoritesCount);

    }

    private static void trackNativeCheckoutPurchase(Bundle params, Map<String, ShoppingCartItem> mItems) {
        String orderNr = params.getString(ORDER_NUMBER_KEY);
        double value = params.getDouble(VALUE_KEY);
        String email = params.getString(EMAIL_KEY);
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String coupon = params.getString(COUPON_KEY);

        Double averageValue = 0d;

        Log.i(TAG, "TRACK SALE: STARTED");
        Log.d(TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        Log.d(TAG, "TRACK SALE: JSON " + orderNr);

        ArrayList<String> favoritesSKU = FavouriteTableHelper.getFavouriteSKUList();

        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        ArrayList<String> skus = new ArrayList<String>();
        int favoritesCount = 0;
        for (PurchaseItem item : items) {
            skus.add(item.sku);
            averageValue += item.paidpriceAsDouble;
            favoritesCount += favoritesSKU.contains(item.sku) ? 1 : 0;
        }
        averageValue = averageValue / items.size();
        
        AnalyticsGoogle.get().trackPurchase(orderNr, value, items);

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // AdXTracker.trackSale(context, value);

            // Send the track sale without customer id
            String customerId = Utils.cleanMD5(email);
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, "" + value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, false);

        } else {
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(customer);

            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);

            AdXTracker.trackSale(context, "" + value, customerId, orderNr, skus, isFirstCustomer, JumiaApplication.SHOP_NAME, customer.isGuest());
        }

        // String transactionId, Double cartValue, Double average, String
        // category, int orderCount, String coupon
        Ad4PushTracker.get().trackCheckoutEnded(orderNr, value, averageValue, mItems.size(), coupon, favoritesCount);

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
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CHECKOUT_CONTINUE, userId, 0l);
    }

    /**
     * Tracking the start of checkout
     * 
     * @param context
     * @param userId
     */
    public static void trackCheckoutStart(TrackingEvent event, String userId) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
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
    public static void trackPage(TrackingPage screen) {
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
        double price = bundle.getDouble(PRICE_KEY);
        String sku = bundle.getString(SKU_KEY);
        String name = bundle.getString(NAME_KEY);
        String category = bundle.getString(CATEGORY_KEY);

        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_CART, sku, (long) price);
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
        String prefix = bundle.getString(SOURCE_KEY);
        String path = bundle.getString(PATH_KEY);
        String name = bundle.getString(NAME_KEY);
        String sku = bundle.getString(SKU_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        Boolean isRelatedItem = bundle.getBoolean(RELATED_ITEM, false);
        TrackingEvent event = !isRelatedItem ? TrackingEvent.SHOW_PRODUCT_DETAIL : TrackingEvent.SHOW_RELATED_PRODUCT_DETAIL;
        // GA
        AnalyticsGoogle.get().trackProduct(event, prefix, path, name, sku, price);
    }

    /**
     * Tracking a campaign
     * 
     * @param utm
     */
    public static void trackGACampaign(String utm) {
        // GA
        AnalyticsGoogle.get().setGACampaign(utm);
    }

    /**
     * Tracking a campaign
     * @param name the name of campaign
     */
    public static void trackCampaignView(String name) {
        // AD4Push
        Ad4PushTracker.get().trackCampaignsView();
        // GA
        AnalyticsGoogle.get().trackGenericPage(name);
    }

    /**
     * Tracking add product to favorites
     * 
     * @param productSku
     * @param price 
     */
    public static void trackAddToFavorites(String productSku, double price) {
        Ad4PushTracker.get().trackAddToFavorites(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_WISHLIST, productSku, (long) price);
    }

    /**
     * Tracking remove product from favorites
     * 
     * @param productSku
     */
    public static void trackRemoveFromFavorites(String productSku, double price) {
        Ad4PushTracker.get().trackRemoveFromWishlist(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.REMOVE_FROM_WISHLIST, productSku, (long) price);
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
        String filter = (catalogFilterValues != null) ? catalogFilterValues.toString() : "";
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_FILTER, filter, 0l);
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
        TrackingEvent event = (subscribe) ? TrackingEvent.SUBSCRIBE_NEWSLETTER : TrackingEvent.UNSUBSCRIBE_NEWSLETTER;
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
    }

    /**
     * Tracking a search query
     * 
     * @param query
     */
    public static void trackSearchSuggestions(String query) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SEARCH_SUGGESTIONS, query, 0l);
    }

    /**
     * 
     */
    public static void trackAppOpen() {
        //AD4Push
        Ad4PushTracker.get().trackAppFirstOpen();
    }
    
    /**
     * Tracking the click in overeflow menu item 
     * @param event
     */
    public static void trackOverflowMenu(TrackingEvent event) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, "", 0l);
    }

    /**
     * Tracking the switch layout in catalog
     * @param label 
     */
    public static void trackCatalogSwitchLayout(String label) {
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.CATALOG_SWITCH_LAYOUT, label, 0l);
    }    
    
}
