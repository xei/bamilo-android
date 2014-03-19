package pt.rocket.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.AdXTracker;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.MixpanelTracker;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.Utils;
import pt.rocket.view.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.android.Util;
import com.urbanairship.push.PushManager;

import de.akquinet.android.androlog.Log;

public class TrackerDelegator {
    private final static String TAG = TrackerDelegator.class.getSimpleName();

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SIGNUP_KEY_FOR_LOGIN = "signup_for_login";
    private static final String SIGNUP_KEY_FOR_CHECKOUT = "signup_for_checkout";

    private static final String JSON_TAG_ORDER_NR = "orderNr";
    private static final String JSON_TAG_GRAND_TOTAL = "grandTotal";
    private static final String JSON_TAG_ITEMS_JSON = "itemsJson";
    
    public final static void trackLoginSuccessful(Context context, Customer customer, boolean wasAutologin, String origin, boolean wasFacebookLogin) {
        String mOrigin;
        int resLogin;
        
        // TODO : Add this filter Signup stings
        // R.string.gfacebooksignupsuccess;
        // R.string.gsignupsuccess;
        
        if( wasFacebookLogin ){
            resLogin = R.string.gfacebookloginsuccess;
            mOrigin = origin;
        } else if ( wasAutologin ) {
            resLogin = R.string.gautologinsuccess;
            mOrigin = context.getString(R.string.mixprop_loginlocationautologin);            
        } else {
            resLogin = R.string.gloginsuccess;
            mOrigin = origin;
        }
        
        if(mOrigin == null || mOrigin.length() == 0){
            mOrigin = context.getString(R.string.mixprop_loginlocationsidemenu);
        }
        
        AnalyticsGoogle.get().trackAccount(resLogin, customer);

        if ( customer == null) {
            return;
        }
                
        boolean loginAfterRegister = checkLoginAfterSignup(context, customer);
        Log.d( TAG, "trackLoginSuccessul: loginAfterRegister = " + loginAfterRegister + " wasAutologin = " + wasAutologin );

        PushManager.shared().setAlias(customer.getIdAsString());
        if(wasFacebookLogin){
            MixpanelTracker.loginWithFacebook(context, customer.getIdAsString(), mOrigin, customer.getCreatedAt());
            AdXTracker.facebookLogin(context, customer.getIdAsString(), JumiaApplication.INSTANCE.SHOP_NAME);
        } else {
            MixpanelTracker.login(context, customer.getIdAsString(), mOrigin, customer.getCreatedAt());
            AdXTracker.login(context, customer.getIdAsString(), JumiaApplication.INSTANCE.SHOP_NAME);    
        }
    }

    public final static void trackLoginFailed(boolean wasAutologin) {
        int resLogin;
        if ( wasAutologin ) {
            resLogin = R.string.gautologinfailed;
        } else {
            resLogin = R.string.gloginfailed;
        }
        
        AnalyticsGoogle.get().trackAccount(resLogin, null);
    }
    
    public final static void trackLogoutSuccessful(Context context) {
        MixpanelTracker.logout(context);
        AdXTracker.logout(context, JumiaApplication.INSTANCE.CUSTOMER.getIdAsString(), JumiaApplication.INSTANCE.SHOP_NAME);
    }

    public final static void trackSearchMade(Context context, String criteria, long results) {
        MixpanelTracker.search(context, criteria, results);
    }
    
    public final static void trackSearchViewSortMade(Context context, String criteria, long results, String sort) {
        MixpanelTracker.searchViewSorted(context, criteria, results, sort);
    }    
    
    public final static void trackShopchanged(Context context) {
        MixpanelTracker.changeShop(context, ShopSelector.getShopId());
    }

    public final static void trackPushNotificationsEnabled(Context context, boolean enabled) {
        MixpanelTracker.setPushNotification(context, enabled);
    }

    public final static void trackProduct(Context context, CompleteProduct product, String category) {
        MixpanelTracker.product(context, product, category);
    }

    public final static void trackProductAddedToCart(Context context, CompleteProduct product, ProductSimple simple, Double price, String location) {
        MixpanelTracker.productAddedToCart(context, product, simple, price, location);
    }

    public final static void trackCheckout(Context context, List<ShoppingCartItem> items) {
        MixpanelTracker.checkout(context, items);
    }

    public final static void trackViewCart(Context context, int numberItems) {
        MixpanelTracker.viewCart(context, numberItems);
    }

    public final static void trackItemShared(Context context, Intent intent) {
        MixpanelTracker.share(context, intent);
    }

    public final static void trackCategoryView(Context context, String category, int page) {
        MixpanelTracker.listCategory(context, category, page);
    }

    public final static void trackItemReview(Context context, CompleteProduct product, ProductReviewCommentCreated review, HashMap<String, Double> ratings) {
        MixpanelTracker.rate(context, product, review, ratings);
    }

    public final static void trackViewReview(Context context, CompleteProduct product, float rate) {
        MixpanelTracker.viewRate(context, product, rate);
    }
    
    
    public final static void trackSignupSuccessful(Context context, Customer customer, String location) {
        AnalyticsGoogle.get().trackAccount(R.string.gcreatesuccess, customer);
        if ( customer == null) {
            return ;
        }
        
        AdXTracker.signup(context, customer.getIdAsString(), JumiaApplication.INSTANCE.SHOP_NAME);
        MixpanelTracker.signup(context, customer, location); 
        PushManager.shared().setAlias(customer.getIdAsString());
        storeSignupProcess(context, customer);
    }

    public static void trackSignupFailed() {
        AnalyticsGoogle.get().trackAccount(R.string.gcreatefailed, null);
    }
    
    public static void trackPurchase( final Context context, final JSONObject result, final Customer customer ) {
        
        new Thread( new Runnable() {
            @Override
            public void run() {
                trackPurchaseInt( context, result, customer);
            }
            
        }).run();        
    }
    
    /**
     * Track Checkout Step
     * @param context
     * @param result
     * @param customer
     */
    public static void trackCheckoutStep( final Context context, String email, final int gstep, final int xstep, final int mixstep ) {
        final String hashedemail = Utils.cleanMD5(email);
        new Thread( new Runnable() {
            @Override
            public void run() {
                
                AnalyticsGoogle.get().trackCheckoutStep(hashedemail, gstep);
                AdXTracker.trackCheckoutStep(context, hashedemail, xstep);
                MixpanelTracker.trackCheckoutStep(context, hashedemail, mixstep);
            }
            
        }).run();        
    }
    
    public static void trackSignUp( final Context context, String email) {
        final String hashedemail = Utils.cleanMD5(email);
        new Thread( new Runnable() {
            @Override
            public void run() {
                AnalyticsGoogle.get().trackSignUp(hashedemail);
                AdXTracker.trackSignUp(context, hashedemail, JumiaApplication.INSTANCE.SHOP_NAME);
                MixpanelTracker.trackSignUp(context, hashedemail);
            }
            
        }).run();        
    }
    
    /**
     * Track Payment Method
     * @param context
     * @param email
     * @param payment
     */
    public static void trackPaymentMethod( final Context context, String email, final String payment) {
        final String hashedemail = Utils.cleanMD5(email);
        new Thread( new Runnable() {
            @Override
            public void run() {
                AnalyticsGoogle.get().trackPaymentMethod(hashedemail, payment);
                AdXTracker.trackPaymentMethod(context, hashedemail, payment);
                MixpanelTracker.trackPaymentMethod(context, hashedemail, payment);
            }
            
        }).run();        
    }
    
    public static void trackNativeCheckoutError( final Context context, String email, final String error) {
        final String hashedemail = Utils.cleanMD5(email);
        new Thread( new Runnable() {
            @Override
            public void run() {
                AnalyticsGoogle.get().trackNativeCheckoutError(hashedemail, error);
                AdXTracker.trackNativeCheckoutError(context, hashedemail, error);
                MixpanelTracker.trackNativeCheckoutError(context, hashedemail, error);
            }
            
        }).run();        
    }
    
    public static void trackPurchaseNativeCheckout( final Context context, final String order_nr, final String value, final String email, final Map<String, ShoppingCartItem> mItems, final Customer customer ) {
        
        new Thread( new Runnable() {
            @Override
            public void run() {
                trackNativeCheckoutPurchase(context, order_nr, value, email, mItems, customer);
            }
            
        }).run();        
    }
    
//    Got checkout response

    private static void trackPurchaseInt(Context context, JSONObject result, Customer customer) {
        Log.i(TAG, "TRACK SALE: STARTED");
        if ( result == null) {
            return;
        }
        
        Log.d( TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        
        Log.d(TAG, "TRACK SALE: JSON " + result.toString());
                
        String orderNr;
        String value;
        JSONObject itemsJson;
        try {
            orderNr = result.getString(JSON_TAG_ORDER_NR);
            value = result.getString(JSON_TAG_GRAND_TOTAL);
            itemsJson = result.getJSONObject(JSON_TAG_ITEMS_JSON);
			Log.d(TAG, "TRACK SALE: RESULT: ORDER=" + orderNr + " VALUE=" + value + " ITEMS=" + result.toString(2));
        } catch (JSONException e) {
            Log.e(TAG, "TRACK SALE: json parsing error: ", e);
            return;
        }
        
        List<PurchaseItem> items = PurchaseItem.parseItems(itemsJson);
        
        AnalyticsGoogle.get().trackSales(orderNr, value, items);
        

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            //AdXTracker.trackSale(context, value);
            
            // Send the track sale without customer id
            String customerId = "";
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, value, customerId, orderNr, isFirstCustomer, JumiaApplication.INSTANCE.SHOP_NAME);
            
        } else { 
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(context, customer);
            
            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);
            
            AdXTracker.trackSale(context, value, customerId, orderNr, isFirstCustomer, JumiaApplication.INSTANCE.SHOP_NAME);
        }
        
        MixpanelTracker.trackSale(context, value, items);
        
    }    
    
    private static void trackNativeCheckoutPurchase(Context context, String order_nr, String value, String email, Map<String, ShoppingCartItem> mItems, Customer customer) {
        Log.i(TAG, "TRACK SALE: STARTED");
        
        Log.d( TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
        
        Log.d(TAG, "TRACK SALE: JSON " + order_nr);
                
        
        
        List<PurchaseItem> items = PurchaseItem.parseItems(mItems);
        
        AnalyticsGoogle.get().trackSales(order_nr, value, items);
        

        if (customer == null) {
            Log.w(TAG, "TRACK SALE: no customer - cannot track further without customerId");
            //AdXTracker.trackSale(context, value);
            
            // Send the track sale without customer id
            String customerId = Utils.cleanMD5(email);
            boolean isFirstCustomer = false;
            AdXTracker.trackSale(context, value, customerId, order_nr, isFirstCustomer, JumiaApplication.INSTANCE.SHOP_NAME);
            
        } else { 
            String customerId = customer.getIdAsString();
            boolean isFirstCustomer = checkCheckoutAfterSignup(context, customer);
            
            Log.d(TAG, "TRACK SALE: CUSTOMER ID: " + customerId + " IS FIRST TIME: " + isFirstCustomer);
            
            AdXTracker.trackSale(context, value, customerId, order_nr, isFirstCustomer, JumiaApplication.INSTANCE.SHOP_NAME);
        }
        
        MixpanelTracker.trackSale(context, value, items);
        
    }    

    public static void storeSignupProcess(Context context, Customer customer) {
        Log.d( TAG, "storing signup tags" );
        SharedPreferences prefs = context
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SIGNUP_KEY_FOR_LOGIN, customer.getEmail())
                .putString(SIGNUP_KEY_FOR_CHECKOUT, customer.getEmail()).commit();
    }

    private static boolean checkLoginAfterSignup(Context context, Customer customer) {
        return checkKeyAfterSignup(context, customer, SIGNUP_KEY_FOR_LOGIN);
    }

    private static boolean checkCheckoutAfterSignup(Context context, Customer customer) {
        return checkKeyAfterSignup(context, customer, SIGNUP_KEY_FOR_CHECKOUT);
    }

    private static boolean checkKeyAfterSignup(Context context, Customer customer, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);

        if ( !prefs.contains(key)) {
            return false ;
        }
        
        String email = prefs.getString(key, "");
        if ( !email.equals( customer.getEmail())) {
            return false;
        }
        
        prefs.edit().remove(key).commit();
        return true;
    }

}
