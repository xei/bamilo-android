package pt.rocket.utils;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
import pt.rocket.view.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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
        
        if(mOrigin == null || mOrigin.isEmpty()){
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
            AdXTracker.facebookLogin(context, customer.getIdAsString());
        } else {
            MixpanelTracker.login(context, customer.getIdAsString(), mOrigin, customer.getCreatedAt());
            AdXTracker.login(context, customer.getIdAsString());    
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
        
        AdXTracker.signup(context, customer.getIdAsString());
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
                trackPurchaseInt( context, result, customer );
            }
            
        }).run();        
    }

    private static void trackPurchaseInt(Context context, JSONObject result, Customer customer) {
        Log.d(TAG, "trackPurchase: started");
        if ( result == null) {
            return;
        }
        
        Log.d( TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
                
        String orderNr;
        String value;
        JSONObject itemsJson;
        try {
            orderNr = result.getString(JSON_TAG_ORDER_NR);
            value = result.getString(JSON_TAG_GRAND_TOTAL);
            itemsJson = result.getJSONObject(JSON_TAG_ITEMS_JSON);
            Log.d( TAG, result.toString(2));
        } catch (JSONException e) {
            Log.e(TAG, "json parsing error: ", e);
            return;
        }
        List<PurchaseItem> items = PurchaseItem.parseItems(itemsJson);
        AnalyticsGoogle.get().trackSales(orderNr, value, items);
        
        if (customer == null) {
            Log.w(TAG, "no customer - cannot track further without customerId");
            AdXTracker.trackSale(context, value);
        }else{
            String customerId = customer.getIdAsString();
            
            boolean isFirstCustomer = checkCheckoutAfterSignup(context, customer);
            Log.d( TAG, "trackPurchaseInt: isFirstCustomer = " + isFirstCustomer );
            AdXTracker.trackSale(context,value, customerId, orderNr, isFirstCustomer);
        }
        
        MixpanelTracker.trackSale(context, value, items);
        
    }    
    
//    private static void trackPurchaseInt(Context context, JSONObject result, Customer customer) {
//        Log.d(TAG, "trackPurchase: started");
//        if ( result == null) {
//            return;
//        }
//        
//        Log.d( TAG, "tracking for " + ShopSelector.getShopName() + " in country " + ShopSelector.getCountryName());
//                
//        String orderNr;
//        String value;
//        JSONObject itemsJson;
//        try {
//            orderNr = result.getString(JSON_TAG_ORDER_NR);
//            value = result.getString(JSON_TAG_GRAND_TOTAL);
//            itemsJson = result.getJSONObject(JSON_TAG_ITEMS_JSON);
//            Log.d( TAG, result.toString(2));
//        } catch (JSONException e) {
//            Log.e(TAG, "json parsing error: ", e);
//            return;
//        }
//        List<PurchaseItem> items = PurchaseItem.parseItems(itemsJson);
//        AnalyticsGoogle.get().trackSales(orderNr, value, items);
//        AdXTracker.trackSale(context, value);
//
//        if (customer == null) {
//            Log.w(TAG, "no customer - cannot track further without customerId");
//            return;
//        }
//
//        String customerId = customer.getIdAsString();
//        FlurryTracker.get().purchase(orderNr, customerId, value);
//        
//        boolean isFirstCustomer = checkCheckoutAfterSignup(context, customer);
//        Log.d( TAG, "trackPurchaseInt: isFirstCustomer = " + isFirstCustomer );
//        AdXTracker.trackSaleData(context, customerId, orderNr, isFirstCustomer);
//    }

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
