package pt.rocket.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.Ad4PushTracker;
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.tracking.AnalyticsGoogle;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.GTMManager;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.DeviceInfoHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.Utils;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
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
    private static final String SIGNUP_KEY_FOR_CHECKOUT = "signup_for_checkout";

    private static final String JSON_TAG_ORDER_NR = "orderNr";
    private static final String JSON_TAG_GRAND_TOTAL = "grandTotal";
    private static final String JSON_TAG_GRAND_TOTAL_CONVERTED = "grandTotal_euroConverted";
    private static final String JSON_TAG_ITEMS_JSON = "itemsJson";
    
    private static final String SESSION_COUNTER = "sessionCounter";
    private static final String LAST_SESSION_SAVED = "lastSessionSaved";
    private static final String EUR_CURRENCY = "EUR";
    
    public static final String CART_COUNT = "cartCount";
    public static final String GRAND_TOTAL = "grandTotal";

    private static final Context context = JumiaApplication.INSTANCE.getApplicationContext();

    
    public final static void trackLoginSuccessful(Bundle params) {
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
        Log.d(TAG, "trackLoginSuccessul: loginAfterRegister = " + loginAfterRegister + " wasAutologin = " + wasAutologin);

        if (wasFacebookLogin) {
            Ad4PushTracker.get().trackFacebookConnect(customer.getIdAsString());
        }
        
        Ad4PushTracker.get().trackLogin(customer.getIdAsString(), customer.getFirstName(), customer.getBirthday(), customer.getGender().toString());
        
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, event, bundle);
        
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
     * @param wasAutologin
     */
    public final static void trackLoginFailed(boolean wasAutologin, String location, String method) {
        Log.i(TAG, "trackLoginFailed: autologin " + wasAutologin);
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

    public final static void trackLogoutSuccessful() {
        String customerId = "";
        if (JumiaApplication.CUSTOMER != null) {
            customerId = JumiaApplication.CUSTOMER.getIdAsString();
        }
        
        AnalyticsGoogle.get().trackEvent(TrackingEvent.LOGOUT_SUCCESS, customerId, 0l);
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, TrackingEvent.LOGOUT_SUCCESS, bundle);
        
        //GTM
        GTMManager.get().gtmTrackLogout(customerId);
        JumiaApplication.CUSTOMER = null;
    }

    
    public final static void trackSearch(Bundle params) {
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
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.SEARCH_TERM, criteria);
        if(params.containsKey(AdjustTracker.CATEGORY))
            bundle.putString(AdjustTracker.CATEGORY, params.getString(AdjustTracker.CATEGORY));
        
        bundle.putString(AdjustTracker.CATEGORY_ID, params.getString(AdjustTracker.CATEGORY_ID));
        AdjustTracker.get().trackEvent(context, TrackingEvent.SEARCH, bundle);
        //GTM
        GTMManager.get().gtmTrackSearch(criteria, results);
    }

    
    public final static void trackShopChanged() {
        //GTM
        GTMManager.get().gtmTrackChangeCountry(ShopSelector.getShopId());
    }
    

    public final static void trackProductRemoveFromCart(Bundle params) {
        String sku = params.getString(SKU_KEY);

        String customer_id = "";
        if (JumiaApplication.CUSTOMER != null) {
            customer_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer_id);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));      
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, params.getDouble(PRICE_KEY));        
        AdjustTracker.get().trackEvent(context, TrackingEvent.REMOVE_FROM_CART, bundle);
        
        // GTM
        GTMManager.get().gtmTrackRemoveFromCart(sku, params.getDouble(RATING_KEY), params.getDouble(PRICE_KEY),
                params.getLong(QUANTITY_KEY), params.getString(CARTVALUE_KEY), EUR_CURRENCY);
    }

    public final static void trackCheckout(List<ShoppingCartItem> items) {
        AnalyticsGoogle.get().trackCheckout(items);
    }

    /**
     * 
     */
    public final static void trackItemShared(Intent intent, String category) {
        String sku = intent.getExtras().getString(RestConstants.JSON_SKU_TAG);
        String userId = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            userId = JumiaApplication.CUSTOMER.getIdAsString();
        }
        // GA
        AnalyticsGoogle.get().trackShare(context, sku, userId, JumiaApplication.SHOP_NAME);
        // Ad4
        Ad4PushTracker.get().trackSocialShare();
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, userId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));       
        bundle.putString(AdjustTracker.PRODUCT_SKU, sku);        
        AdjustTracker.get().trackEvent(context, TrackingEvent.SHARE, bundle);
        //GTM
        GTMManager.get().gtmTrackShare("", sku, category);
    }

    /**
     * 
     */
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

    /**
     * 
     */
    public final static void trackItemReview(Bundle params) {

        CompleteProduct product = params.getParcelable(PRODUCT_KEY);
        ProductReviewCommentCreated review = params.getParcelable(REVIEW_KEY);

        String user_id = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
            user_id = JumiaApplication.CUSTOMER.getIdAsString();
        }
        
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
        
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, user_id);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));       
        bundle.putString(AdjustTracker.PRODUCT_SKU, product.getSku());
        AdjustTracker.get().trackEvent(context, TrackingEvent.ADD_REVIEW, bundle);
        //GTM
        GTMManager.get().gtmTrackRateProduct(product,EUR_CURRENCY);
        // Ad4
        Ad4PushTracker.get().trackReviewCounter();
    }
    
    /**
     * 
     */
    public final static void trackViewReview(CompleteProduct product) {
        //GTM
        GTMManager.get().gtmTrackViewRating(product, EUR_CURRENCY);
    }

    /**
     * Track signup successful.
     * @param params
     */
    public final static void trackSignupSuccessful(Bundle params) {
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String location = params.getString(LOCATION_KEY);
        // Validate customer
        if (customer == null) return;
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SIGNUP_SUCCESS, customer.getIdAsString(), 0l);
        // AD4
        Ad4PushTracker.get().trackSignup(customer.getIdAsString(), customer.getGender().toString(), customer.getFirstName(), customer.getBirthday());
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, TrackingEvent.SIGNUP_SUCCESS, bundle);
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
     * Track Checkout Step
     * 
     * @param context
     * @param result
     * @param customer
     */
    public static void trackCheckoutStep(Bundle params) {

        String email = params.getString(EMAIL_KEY);
        final TrackingEvent event = (TrackingEvent) params.getSerializable(GA_STEP_KEY);

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
                //GTM
                GTMManager.get().gtmTrackChoosePayment(payment);
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
        //ArrayList<String> favoritesSKU = FavouriteTableHelper.getFavouriteSKUList();

        List<PurchaseItem> items = PurchaseItem.parseItems(itemsJson);
        ArrayList<String> skus = new ArrayList<String>();
        //int favoritesCount = 0;
        for (PurchaseItem item : items) {
            skus.add(item.sku);
            averageValue += item.getPriceForTracking();//paidpriceAsDouble;
            //favoritesCount += favoritesSKU.contains(item.sku) ? 1 : 0;
        }
        averageValue = averageValue / items.size();
        
        AnalyticsGoogle.get().trackPurchase(orderNr, valueConverted, items);

        boolean isFirstCustomer = false;
        
        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // Send the track sale without customer id
            isFirstCustomer = false;
        } else {
            isFirstCustomer = checkFirstCustomer(customer);
            
            if(isFirstCustomer)
                removeFirstCustomer(customer);
        }

        Ad4PushTracker.get().trackCheckoutEnded(orderNr, valueConverted, value, averageValue, items.size(), coupon);

        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));       
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, orderNr);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, (ArrayList<PurchaseItem>)items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, value);
        
        AdjustTracker.get().trackEvent(context, TrackingEvent.CHECKOUT_FINISHED, bundle);
        String paymentMethod = "";
        paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        //GTM
        GTMManager.get().gtmTrackTransaction(items,EUR_CURRENCY, value,orderNr, coupon, paymentMethod, "", "");
        
    }

    private static void trackNativeCheckoutPurchase(Bundle params, Map<String, ShoppingCartItem> mItems) {
        String orderNr = params.getString(ORDER_NUMBER_KEY);
        double grandTotal = params.getDouble(GRAND_TOTAL);
        double cartValue = params.getDouble(VALUE_KEY);
        Customer customer = params.getParcelable(CUSTOMER_KEY);
        String coupon = "";
        coupon = params.getString(COUPON_KEY);
        String paymentMethod = "";
        paymentMethod = params.getString(PAYMENT_METHOD_KEY);
        String shippingAmount = "";
        shippingAmount = params.getString(SHIPPING_KEY);
        String taxAmount = "";
        taxAmount = params.getString(TAX_KEY);
        
        int numberOfItems = params.getInt(TrackerDelegator.CART_COUNT);

        Double averageValue = 0d;
        try {
            averageValue = cartValue / numberOfItems;
        } catch (IllegalArgumentException e) {
            averageValue = 0d;
        }

        Log.i(TAG, "TRACK SALE: STARTED");
        Log.d(TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        Log.d(TAG, "TRACK SALE: JSON " + orderNr);

        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        ArrayList<String> skus = new ArrayList<String>();
        // Get number of items
        for (PurchaseItem item : items) {
            skus.add(item.sku);
        }

        boolean isFirstCustomer = false;
        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            // Send the track sale without customer id
            isFirstCustomer = false;
        } else {
            isFirstCustomer = checkFirstCustomer(customer);
            if(isFirstCustomer) removeFirstCustomer(customer);
        }

        // GA
        AnalyticsGoogle.get().trackPurchase(orderNr, cartValue, items);
        // Ad4
        Ad4PushTracker.get().trackCheckoutEnded(orderNr, grandTotal, cartValue, averageValue, numberOfItems, coupon);
        // Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putString(AdjustTracker.USER_ID, customer.getIdAsString());
        bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        bundle.putBoolean(AdjustTracker.IS_FIRST_CUSTOMER, isFirstCustomer);
        bundle.putString(AdjustTracker.TRANSACTION_ID, orderNr);
        bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, skus);
        bundle.putBoolean(AdjustTracker.IS_GUEST_CUSTOMER, customer.isGuest());
        bundle.putParcelableArrayList(AdjustTracker.CART, (ArrayList<PurchaseItem>)items);
        bundle.putDouble(AdjustTracker.TRANSACTION_VALUE, cartValue);
        AdjustTracker.get().trackEvent(context, TrackingEvent.CHECKOUT_FINISHED, bundle);
        //GTM
        GTMManager.get().gtmTrackTransaction(items,EUR_CURRENCY, cartValue,orderNr, coupon, paymentMethod, shippingAmount, taxAmount);
    }

    public static void storeSignupProcess(Customer customer) {
        Log.d(TAG, "storing signup tags");
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SIGNUP_KEY_FOR_LOGIN, customer.getEmail()).putString(SIGNUP_KEY_FOR_CHECKOUT, customer.getEmail()).commit();
    }
    
    public static void removeFirstCustomer(Customer customer) {
        Log.d(TAG, "remove first customer");
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(customer.getEmail(),false).commit();
    }

    public static void storeFirstCustomer(Customer customer) {
        Log.d(TAG, "store first customer");
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        
        boolean isNewCustomer = prefs.getBoolean(customer.getEmail(), true);
        if (isNewCustomer) {
            Log.d(TAG, "store first customer1");
            prefs.edit().putBoolean(customer.getEmail(),true).commit();
        }
        
    }
    
    private static boolean checkLoginAfterSignup(Customer customer) {        
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        if (!prefs.contains(SIGNUP_KEY_FOR_LOGIN)) {
            return false;
        }

        String email = prefs.getString(SIGNUP_KEY_FOR_LOGIN, "");
        if (!email.equals(customer.getEmail())) {
            return false;
        }

        prefs.edit().remove(SIGNUP_KEY_FOR_LOGIN).commit();
        return true;
    }

    private static boolean checkFirstCustomer(Customer customer) {
        SharedPreferences prefs = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        if (!prefs.contains(customer.getEmail())) {
            return true;
        }

        return prefs.getBoolean(customer.getEmail(),true);

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
    public static void trackCheckoutStart(TrackingEvent event, String userId, int cartQt, double cartValue) {
        // GA
        AnalyticsGoogle.get().trackEvent(event, userId, 0l);
        // AD4Push
        Ad4PushTracker.get().trackCheckoutStarted(cartQt, cartValue);
        // GTM
        GTMManager.get().gtmTrackStartCheckout(cartQt, cartValue, EUR_CURRENCY);
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
     */
    public static void trackPage(TrackingPage screen, long loadTime, boolean justGTM) {
        // GTM
        trackScreenGTM(screen,loadTime);
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
     * 
     * @param bundle
     */
    public static void trackPageForAdjust(TrackingPage screen, Bundle bundle) {
        if (null != bundle) {
            bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
            AdjustTracker.get().trackScreen(screen, bundle);
        }
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
        String brand = bundle.getString(BRAND_KEY);
        String category = "";
        category = bundle.getString(CATEGORY_KEY);
        String subCategory = "";
        subCategory = bundle.getString(SUBCATEGORY_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        double discount = bundle.getDouble(DISCOUNT_KEY);
        double rating = bundle.getDouble(RATING_KEY);
        String sku = bundle.getString(SKU_KEY);
        String name = bundle.getString(NAME_KEY);
        String location = bundle.getString(LOCATION_KEY);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_CART, sku, (long) price);
        // AD4Push
        if(TextUtils.equals(location, GTMValues.WISHLISTPAGE)) {
            Ad4PushTracker.get().trackAddToCartFromFav(sku, price, name, category);
        } else {
            Ad4PushTracker.get().trackAddToCart(sku, price, name, category);
        }
        
        //Adjust
        Bundle params = new Bundle();
        params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        params.putString(AdjustTracker.USER_ID, customerId);
        params.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        params.putString(AdjustTracker.PRODUCT_SKU, sku);
        params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        params.putDouble(AdjustTracker.VALUE, price);
        AdjustTracker.get().trackEvent(context, TrackingEvent.ADD_TO_CART, params);
        //GTM
        GTMManager.get().gtmTrackAddToCart(sku, price, brand, EUR_CURRENCY, discount, rating, category, subCategory, location);
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
        String brand = bundle.getString(BRAND_KEY);
        String category = "";
        category = bundle.getString(CATEGORY_KEY);
        String subCategory = "";
        subCategory = bundle.getString(SUBCATEGORY_KEY);
        double price = bundle.getDouble(PRICE_KEY);
        double discount = bundle.getDouble(DISCOUNT_KEY);
        double rating = bundle.getDouble(RATING_KEY);
        Boolean isRelatedItem = bundle.getBoolean(RELATED_ITEM, false);
        TrackingEvent event = !isRelatedItem ? TrackingEvent.SHOW_PRODUCT_DETAIL : TrackingEvent.SHOW_RELATED_PRODUCT_DETAIL;
        // GA
        AnalyticsGoogle.get().trackProduct(event, prefix, path, name, sku, price);
        //GTM
        GTMManager.get().gtmTrackViewProduct(sku, price, brand, EUR_CURRENCY, discount, rating, category, subCategory);
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
     * Track when the user views the favorites page (with the products)
     * 
     * @param favorites
     */
    public static void trackViewFavorites(Bundle args) {
        Bundle bundle = new Bundle();
        Customer customer = args.getParcelable(CUSTOMER_KEY);
        
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        if (null != customer) {
            bundle.putParcelable(AdjustTracker.CUSTOMER, customer);
        }       
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        bundle.putParcelableArrayList(AdjustTracker.FAVORITES, args.getParcelableArrayList(FAVOURITES_KEY));
        
        AdjustTracker.get().trackEvent(context, TrackingEvent.VIEW_WISHLIST, bundle);
    }
    
    /**
     * Tracking add product to favorites
     * 
     * @param productSku
     * @param price 
     */
    public static void trackAddToFavorites(String productSku, String productBrand, double productPrice, 
            double averageRating, double productDiscount, boolean fromCatalog, ArrayList<String> categories) {
        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        
        
        Ad4PushTracker.get().trackAddToFavorites(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.ADD_TO_WISHLIST, productSku, (long) productPrice);
        
        //Adjust 
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));       
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putDouble(AdjustTracker.VALUE, productPrice);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        
        AdjustTracker.get().trackEvent(context, TrackingEvent.ADD_TO_WISHLIST, bundle);
        String location = GTMValues.PRODUCTDETAILPAGE;
        if(fromCatalog) location = GTMValues.CATALOG;
        String category = "";
        String subCategory = "";
        if(CollectionUtils.isNotEmpty(categories)){
           category = categories.get(0);
            if(categories.size() > 1){
               subCategory = categories.get(1);
            }
        }
        
        //GTM
        GTMManager.get().gtmTrackAddToWishList(productSku, productBrand, productPrice, averageRating,
                productDiscount, CurrencyFormatter.getCurrencyCode(),location, category, subCategory);
    }

    /**
     * Tracking remove product from favorites
     * h375id
     * @param productSku
     */
    public static void trackRemoveFromFavorites(String productSku, double price, double averageRatingTotal) {
        // User
        String customerId = (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        
        Ad4PushTracker.get().trackRemoveFromWishlist(productSku);
        // GA
        AnalyticsGoogle.get().trackEvent(TrackingEvent.REMOVE_FROM_WISHLIST, productSku, (long) price);
        
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, customerId);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        bundle.putString(AdjustTracker.PRODUCT_SKU, productSku);
        bundle.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
        bundle.putDouble(AdjustTracker.VALUE, price);
        
        AdjustTracker.get().trackEvent(context, TrackingEvent.REMOVE_FROM_WISHLIST, bundle);
        
        //GTM
        if(averageRatingTotal != -1d)
            GTMManager.get().gtmTrackRemoveFromWishList(productSku, price, averageRatingTotal, EUR_CURRENCY);
    }

    
    /**
     * Tracking a view Catalog
     * 
     */
    public static void trackViewCatalog(String category, String subCategory, int pageNumber) {
        // GTM
        GTMManager.get().gtmTrackCatalog(category, subCategory, pageNumber);

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
        
        //GTM
        if(catalogFilterValues.containsKey(TrackerDelegator.CATALOG_FILTER_KEY)){
            String activeFilters = catalogFilterValues.getAsString(TrackerDelegator.CATALOG_FILTER_KEY);
            if (!TextUtils.isEmpty(activeFilters)) {
                String[] filters = activeFilters.split(",");
                for (String activefilter : filters) {
                    Log.d("GTM FILTER",":"+activefilter);
                    GTMManager.get().gtmTrackFilterCatalog(activefilter);
                }
            }
        }

    }
    
    /**
     * Tracking a catalog Sort
     * 
     * @param mCatalogSortValues
     * @param searchQuery
     * @param searchQuery
     */
    public static void trackCatalogSorter(String sortType) {
        // GTM
        GTMManager.get().gtmTrackSortCatalog(sortType);


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
    public static void trackFailedPayment(String paymentMethod, double transactionTotal) {
        // GTM
        GTMManager.get().gtmTrackFailedPayment(paymentMethod, transactionTotal, EUR_CURRENCY);

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
            screenName = context.getString(page.getName());
        }
        
        if(!"".equalsIgnoreCase(screenName))
            GTMManager.get().gtmTrackViewScreen(screenName,loadTime);
    }
    
    /**
     * Tracking newsletter subscription
     * 
     * @param subscribe
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
     * 
     * @param query
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
        String version = DeviceInfoHelper.getVersionName(context);
        GTMManager.get().gtmTrackAppOpen(version, info, ShopSelector.getShopId(), getUtmParams(context, GTMManager.CAMPAIGN_ID_KEY),
                getUtmParams(context, GTMManager.CAMPAIGN_SOURCE), getUtmParams(context, GTMManager.CAMPAIGN_MEDIUM), isFromPush);
        
        countSession();
    }
    
    /**
     * 
     */
    public static void trackAppOpenAdjust(Context context, long launchtime) {
        // Get device info
        Bundle info = DeviceInfoHelper.getInfo(context);     
        // Adjust
        Bundle params = new Bundle(info);
        params.putLong(AdjustTracker.BEGIN_TIME, launchtime);
        params.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));
        AdjustTracker.get().trackEvent(context, TrackingEvent.APP_OPEN, params);
       
    }
    
    /**
     * Tracking the call button
     * @param event
     */
    public static void trackCall(Context context, String user_id, String shop_country) {
        //Adjust
        Bundle bundle = new Bundle();
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putString(AdjustTracker.USER_ID, user_id);
        bundle.putBoolean(AdjustTracker.DEVICE, context.getResources().getBoolean(R.bool.isTablet));        
        AdjustTracker.get().trackEvent(context, TrackingEvent.CALL, bundle);

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
    
    private static void countSession() {
        SharedPreferences settings = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        int sessionCount = settings.getInt(SESSION_COUNTER, 0);
        long savedTimeStamp = settings.getLong(LAST_SESSION_SAVED, 0);
        long currentTimeStamp = System.currentTimeMillis();
        long diff = currentTimeStamp - savedTimeStamp;
        Log.i("TIME", " DIFF " + diff + " 30L * 60 * 1000 " + 2L * 60 * 1000 + " currentTimeStamp " + currentTimeStamp + " savedTimeStamp " + savedTimeStamp);
        if (diff >= 30 * 60 * 1000) {
            Log.i("TIME", " A MINUTE HAS PASSED");
            Editor editor = settings.edit();
            editor.putLong(LAST_SESSION_SAVED, currentTimeStamp);
            sessionCount++;
            editor.putInt(SESSION_COUNTER, sessionCount);
            editor.commit();

        } else {
            Log.i("TIME", " STILL TICKING");
        }

    }
    
    public static String getUtmParams(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    /**
     * Track the new cart for each user interaction, add or remove.
     * @param cartValue
     * @author sergiopereira
     */
    public static void trackCart(double cartValue, int cartCount) { 
        // Ad4
        Ad4PushTracker.get().trackCart(cartValue, cartCount);
    }

//    private static void saveUtmParams(Context context, String key, String value) {
//        Log.d(TAG, "saving saveUtmParams params, key: " + key + ", value : " + value);
//        Log.d("BETA", "saving saveUtmParams params, key: " + key + ", value : " + value);
//        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
}
