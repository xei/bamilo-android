/**
 * 
 */
package com.mobile.newFramework.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.mobile.framework.R;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.PurchaseItem;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerGender;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.tracking.gtm.GTMKeys;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.NetworkConnectivity;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author nunocastro
 *
 */
public class AdjustTracker {
    
    private final static String TAG = AdjustTracker.class.getSimpleName();
    
    public static final String BEGIN_TIME = "beginTime";
    public static final String USER_ID = "userId";
    public static final String CURRENCY_ISO = "currencyIso";
    public static final String COUNTRY_ISO = "countryIso";
    public static final String CUSTOMER = "customer";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_ITEM_SKUS = "transactionItemSkus";
    public static final String TRANSACTION_VALUE = "transactionValue";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_SKU = "productSku";
    public static final String VALUE = "value";
    public static final String IS_GUEST_CUSTOMER = "isGuestCustomer";
    public static final String IS_FIRST_CUSTOMER = "isFirstCustomer";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String CART = "cart";
    public static final String DEVICE = "device";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String TREE = "tree";
    public static final String FAVORITES = "favorites";
    public static final String PRODUCT_SIZE = "size";
    
    public final static String ADJUST_PREFERENCES = "AdjustPreferences";
    public final static String PURCHASE_NUMBER = "aggregatedNumberOfPurchases";

    protected static class AdjustKeys {
        public static final String SHOP_COUNTRY = "shop_country";
        public static final String APP_VERSION = "app_version";
        public static final String DISPLAY_SIZE = "display_size";

        public static final String USER_ID = "user_id";
        public static final String DURATION = "duration";
        public static final String DEVICE = "device";
        public static final String DEVICE_MANUFACTURER = "device_manufacturer";
        public static final String DEVICE_MODEL = "device_model";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String CURRENCY_CODE = "currency_code";
        public static final String CURRENCY = "currency";
        public static final String PRICE = "price";
        public static final String CATEGORY_TREE = "tree";
        public static final String CATEGORY = "category";
        public static final String CATEGORY_ID = "category_id";
        public static final String QUANTITY = "quantity";
        public static final String GENDER = "gender";
        public static final String SKU = "sku";
        public static final String SKUS = "skus";
        public static final String PRODUCTS = "products";
        public static final String PRODUCT = "product";
        public static final String KEYWORDS = "keywords";
        public static final String NEW_CUSTOMER = "new_customer";   
        public static final String AMOUNT_TRANSACTIONS = "amount_transactions";
        public static final String AMOUNT_SESSIONS = "amount_sessions";
        public static final String REGION = "region";
        public static final String CITY = "city";
        public static final String DISCOUNT = "discount";
        public static final String BRAND = "brand";
        public static final String QUERY = "query";
        public static final String SIZE = "size";
        public static final String COLOUR = "colour";
        public static final String TOTAL_WISHLIST = "total_wishlist";
//        public static final String WISHLIST_CURRENCY_CODE = "currency_code1";
        public static final String TOTAL_CART = "total_cart";
//        public static final String CART_CURRENCY_CODE = "cart_currency_code";//
        public static final String TOTAL_TRANSACTION = "total_transaction"; 
//        public static final String TRANSACTION_CURRENCY = "transaction_currency";
//        public static final String VARIATION = "variation";
        public static final String APP_PRE_INSTALL = Constants.INFO_PRE_INSTALL;
//        public static final String INFO_BRAND = Constants.INFO_BRAND;
        public static final String DEVICE_SIM_OPERATOR = Constants.INFO_SIM_OPERATOR;

        // New Adjust Facebook Audience keys
        public static final String FB_VALUE_TO_SUM = "_valueToSum";
        public static final String FB_CONTENT_ID = "fb_content_id";
        public static final String FB_CONTENT_TYPE = "fb_content_type";
        public static final String FB_CURRENCY = "fb_currency";
        public static final String FB_CONTENT_CATEGORY = "content_category";

    }

    
    private static final String TABLET = "Tablet";
    private static final String PHONE = "Phone";

    private static final String TRACKING_PREFS = "tracking_prefs";
    private static final String SESSION_COUNTER = "sessionCounter";

    private static boolean isEnabled = false;
    
    private static AdjustTracker sInstance;
    
    private static final String NOT_AVAILABLE = "n.a.";

//    public final static String ENCODING_SCHEME = "UTF-8";

    public final static String ADJUST_FIRST_TIME_KEY = "adjust_first_time";
    
//    private static double ADJUST_CENT_VALUE = 100d;

    private static Context mContext;
    
    private static final String EURO_CURRENCY = "EUR";

    private static final String PRODUCT_CONTENT_TYPE = "product";
    public static AdjustTracker get() {
        if (sInstance == null) {
            sInstance = new AdjustTracker();
        }
        return sInstance;
    }
    
    public static void startup(Context context) {
        Print.d(TAG, "Adjust Startup");
        sInstance = new AdjustTracker(context);
    }    
    
    public AdjustTracker() {
        isEnabled = false;
        if(mContext != null){
            isEnabled = mContext.getResources().getBoolean(R.bool.adjust_enabled);
        }
    }
    
    public AdjustTracker(Context context) {
        super();

        isEnabled = context.getResources().getBoolean(R.bool.adjust_enabled);


        mContext = context;
        if (isEnabled) {
            initAdjustInstance();
        }
        
    }

    private void initAdjustInstance() {
        // Where the 2nd parameter is update should be set to
        // true if the App has been run before and false if it has
        // never been run before. Setting this correctly will allow
        // us to distinguish which installs are current users
        // updating to include the Ad-X SDK from new organic
        // downloads. The third parameter sets the logging level for
        // debugging. Set to zero for production version of the App.
//        boolean isupdate;
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        boolean firstTimeAdjust = sharedPrefs.getBoolean(ADJUST_FIRST_TIME_KEY, true);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(ADJUST_FIRST_TIME_KEY, false);
        editor.apply();

//        if (firstTimeAdjust) {
//            isupdate = false;
//        } else {
//            isupdate = true;
//        }
        
        Print.i(TAG, "ADJUST is APP_LAUNCH " + Adjust.isEnabled());
    }

    /**
     * initialized Adjust tracker
     */
    public static void initializeAdjust(final Context context) {
        // Get adjust app token
        String appToken = context.getString(R.string.adjust_app_token);
        // Get adjust environment and log level
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        LogLevel logLevel = LogLevel.INFO;
        if(!context.getResources().getBoolean(R.bool.adjust_is_production_env)) {
            environment = AdjustConfig.ENVIRONMENT_SANDBOX;
            //logLevel = LogLevel.VERBOSE;
        }
        // Create adjust config
        AdjustConfig config = new AdjustConfig(context, appToken, environment);
        config.setLogLevel(logLevel);
        // Set pre install default tracker
        if (!TextUtils.isEmpty(context.getString(R.string.adjust_default_tracker))) {
            config.setDefaultTracker(context.getString(R.string.adjust_default_tracker));
        }
        // Set listener
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                AdjustTracker.saveResponseDataInfo(context, attribution.adgroup, attribution.network, attribution.campaign, attribution.creative);
            }
        });
        // Create adjust using configs
        Adjust.onCreate(config);
    }


    public static void onResume() {
        if(Adjust.isEnabled()){
            Adjust.onResume();
        }

    }

    public static void onPause(){
        if(Adjust.isEnabled()){
            Adjust.onPause();
        }

    }
    
    public static void saveResponseDataInfo(Context context,String adGroup, String network, String campaign, String creative){
        //
        if(context == null) return;
        // 
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(GTMKeys.INSTALLNETWORK, network);
        editor.putString(GTMKeys.INSTALLADGROUP, adGroup);
        editor.putString(GTMKeys.INSTALLCAMPAIGN, campaign);
        editor.putString(GTMKeys.INSTALLCREATIVE, creative);
        editor.apply();
    }
    
    public void trackScreen(TrackingPage screen, Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        Print.i(TAG, " Tracked Screen --> " + screen);
        switch (screen) {
        case HOME:
            AdjustEvent eventHomeScreen = new AdjustEvent(mContext.getString(R.string.adjust_token_home));

            //didn't used the baseParameters because some of the parameters aren't used on this event ,eg (SHOP_COUNTRY)
            if (bundle.containsKey(USER_ID) && !bundle.getString(USER_ID).equals("")) {
                eventHomeScreen.addCallbackParameter(AdjustKeys.USER_ID, bundle.getString(USER_ID));
                eventHomeScreen.addPartnerParameter(AdjustKeys.USER_ID, bundle.getString(USER_ID));
            } else if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                eventHomeScreen.addCallbackParameter(AdjustKeys.USER_ID, customer.getIdAsString());
                eventHomeScreen.addPartnerParameter(AdjustKeys.USER_ID, customer.getIdAsString());
            }
            eventHomeScreen.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
            eventHomeScreen.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
            eventHomeScreen.addCallbackParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
            eventHomeScreen.addPartnerParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
            eventHomeScreen.addCallbackParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
            eventHomeScreen.addPartnerParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
            eventHomeScreen.addCallbackParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
            eventHomeScreen.addPartnerParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
            eventHomeScreen.addCallbackParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
            eventHomeScreen.addPartnerParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                String gender = getGender(customer);
                if(!gender.equals(CustomerGender.UNKNOWN.name())){
                    eventHomeScreen.addCallbackParameter(AdjustKeys.GENDER, gender);
                    eventHomeScreen.addPartnerParameter(AdjustKeys.GENDER, gender);
                }

            }   
            Adjust.trackEvent(eventHomeScreen);
            
            //FB - View Homescreen
            AdjustEvent eventHomeScreenFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_home));
            eventHomeScreenFB = getFBBaseParameters(eventHomeScreenFB, bundle);
            Adjust.trackEvent(eventHomeScreenFB);
            
            break;

        case PRODUCT_DETAIL_LOADED:
            AdjustEvent eventPDVScreen = new AdjustEvent(mContext.getString(R.string.adjust_token_view_product));
            eventPDVScreen = getBaseParameters(eventPDVScreen, bundle);
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                String gender = getGender(customer);
                if(!gender.equals(CustomerGender.UNKNOWN.name())){
                    eventPDVScreen.addCallbackParameter(AdjustKeys.GENDER, gender);
                    eventPDVScreen.addPartnerParameter(AdjustKeys.GENDER, gender);
                }
            }   
            ProductComplete prod = bundle.getParcelable(PRODUCT);
//            parameters.put(AdjustKeys.SKU, prod.getSku());
            eventPDVScreen.addCallbackParameter(AdjustKeys.PRODUCT, prod.getSku());
            eventPDVScreen.addPartnerParameter(AdjustKeys.PRODUCT, prod.getSku());
            
            Adjust.trackEvent(eventPDVScreen);
            
            // FB - View Product
            AdjustEvent eventPDVScreenFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_product));

            eventPDVScreenFB = getFBTrackerBaseParameters(eventPDVScreenFB, bundle);

            eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(prod.getPriceForTracking()));
            eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(prod.getPriceForTracking()));
            eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, prod.getSku());
            eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, prod.getSku());

            //FIXME
//            eventPDVScreenFB = getFBBaseParameters(eventPDVScreenFB, bundle);
//            eventPDVScreenFB.addCallbackParameter(AdjustKeys.SKU, prod.getSku());
//            eventPDVScreenFB.addPartnerParameter(AdjustKeys.SKU, prod.getSku());
//            eventPDVScreenFB.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
//            eventPDVScreenFB.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
//            eventPDVScreenFB.addCallbackParameter(AdjustKeys.DISCOUNT, prod.hasDiscount() ? "y" : "n");
//            eventPDVScreenFB.addPartnerParameter(AdjustKeys.DISCOUNT, prod.hasDiscount() ? "y" : "n");
//            eventPDVScreenFB.addCallbackParameter(AdjustKeys.BRAND, prod.getBrand());
//            eventPDVScreenFB.addPartnerParameter(AdjustKeys.BRAND, prod.getBrand());
//            eventPDVScreenFB.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(prod.getPriceForTracking()));
//            eventPDVScreenFB.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(prod.getPriceForTracking()));
//            if (bundle.containsKey(AdjustTracker.PRODUCT_SIZE) && !TextUtils.isEmpty(bundle.getString(AdjustTracker.PRODUCT_SIZE))){
//                eventPDVScreenFB.addCallbackParameter(AdjustKeys.SIZE, bundle.getString(AdjustTracker.PRODUCT_SIZE));
//                eventPDVScreenFB.addPartnerParameter(AdjustKeys.SIZE, bundle.getString(AdjustTracker.PRODUCT_SIZE));
//            }
//            if (bundle.containsKey(TREE) && !TextUtils.isEmpty(bundle.getString(TREE))){
//                eventPDVScreenFB.addCallbackParameter(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
//                eventPDVScreenFB.addPartnerParameter(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
//            }
            Adjust.trackEvent(eventPDVScreenFB);
            
            break;
            
        case PRODUCT_LIST_SORTED:  //View Listing
            Print.d("ADJUST", "PRODUCT_LIST_SORTED");
            AdjustEvent eventCatalogSorted = new AdjustEvent(mContext.getString(R.string.adjust_token_view_listing));
            eventCatalogSorted = getBaseParameters(eventCatalogSorted, bundle);
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                String gender = getGender(customer);
                if(!gender.equals(CustomerGender.UNKNOWN.name())){
                    eventCatalogSorted.addCallbackParameter(AdjustKeys.GENDER, gender);
                    eventCatalogSorted.addPartnerParameter(AdjustKeys.GENDER, gender);
                }
            }
            ArrayList<ProductRegular> skus = bundle.getParcelableArrayList(TRANSACTION_ITEM_SKUS);
            StringBuilder sbSkus;
            sbSkus = new StringBuilder();
            if (skus.size() > 0) {
                sbSkus.append("[");
                final int skusLimit = 3;
                int skusCount = 0;
                
                for (ProductRegular sku : skus) {
                    sbSkus.append(sku.getSku()).append(",");
                    skusCount++;
                    if (skusLimit <= skusCount) {
                        break;
                    }
                }
                sbSkus.setLength(sbSkus.length() - 1);
                sbSkus.append("]");
                eventCatalogSorted.addCallbackParameter(AdjustKeys.PRODUCTS, sbSkus.toString());
                eventCatalogSorted.addPartnerParameter(AdjustKeys.PRODUCTS, sbSkus.toString());
                Adjust.trackEvent(eventCatalogSorted);
            }
            //FB - View Listing
            AdjustEvent eventCatalogSortedFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_listing));

            eventCatalogSortedFB.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
            eventCatalogSortedFB.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
            eventCatalogSortedFB.addCallbackParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
            eventCatalogSortedFB.addPartnerParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
            eventCatalogSortedFB.addCallbackParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(CATEGORY)); //FIXME
            eventCatalogSortedFB.addPartnerParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(CATEGORY));

            //FIXME
//            eventCatalogSortedFB = getFBBaseParameters(eventCatalogSortedFB, bundle);
//            if(!TextUtils.isEmpty(bundle.getString(CATEGORY))){
//                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.CATEGORY, bundle.getString(CATEGORY));
//                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.CATEGORY, bundle.getString(CATEGORY));
//            }
//
//            eventCatalogSortedFB.addCallbackParameter(AdjustKeys.SKUS, sbSkus.toString());
//            eventCatalogSortedFB.addPartnerParameter(AdjustKeys.SKUS, sbSkus.toString());
//            if (bundle.containsKey(CATEGORY_ID) && !TextUtils.isEmpty(bundle.getString(CATEGORY_ID))){
//                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
//                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
//            }
//            if (bundle.containsKey(TREE) && !TextUtils.isEmpty(bundle.getString(TREE))){
//                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
//                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
//            }
            Adjust.trackEvent(eventCatalogSortedFB);
            break;
            
        case CART_LOADED:
            AdjustEvent eventCartLoaded = new AdjustEvent(mContext.getString(R.string.adjust_token_view_cart));
            eventCartLoaded = getBaseParameters(eventCartLoaded, bundle);
            
            
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                String gender = getGender(customer);
                if(!gender.equals(CustomerGender.UNKNOWN.name())){
                    eventCartLoaded.addCallbackParameter(AdjustKeys.GENDER, gender);
                    eventCartLoaded.addPartnerParameter(AdjustKeys.GENDER, gender);
                }

            }   
  
            PurchaseEntity cart = bundle.getParcelable(CART);
            JSONObject json;
            
            int productCount = 0;
            String countString = "";
            for (PurchaseCartItem item : cart.getCartItems()) {
                AdjustEvent eventCartLoadedFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_cart));
                json = new JSONObject();
                try {
                    json.put(AdjustKeys.SKU, item.getConfigSKU());
                    json.put(AdjustKeys.CURRENCY, EURO_CURRENCY);
                    json.put(AdjustKeys.QUANTITY, item.getQuantity());
                    json.put(AdjustKeys.PRICE, item.getPriceForTracking());
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }               
//                   Log.e("Adjust","PRODUCT:"+json.toString());
                eventCartLoaded.addCallbackParameter(AdjustKeys.PRODUCT + countString, json.toString());
                eventCartLoaded.addPartnerParameter(AdjustKeys.PRODUCT + countString, json.toString());

                countString = String.valueOf(++productCount);

                //FB - View Cart
                eventCartLoadedFB = getFBBaseParameters(eventCartLoadedFB, bundle);
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.SKU, item.getConfigSKU());
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.SKU, item.getConfigSKU());
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.QUANTITY, String.valueOf(item.getQuantity()));
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.QUANTITY, String.valueOf(item.getQuantity()));
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.DISCOUNT, item.hasDiscount() ? "y" : "n");
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.DISCOUNT, item.hasDiscount() ? "y" : "n");
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(item.getPriceForTracking()));
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(item.getPriceForTracking()));
                eventCartLoadedFB.addCallbackParameter(AdjustKeys.TOTAL_CART, String.valueOf(cart.getCartValueEuroConverted()));
                eventCartLoadedFB.addPartnerParameter(AdjustKeys.TOTAL_CART, String.valueOf(cart.getCartValueEuroConverted()));
                
                // fbParameters.put(AdjustKeys.SIZE, item.getVariation());
                // fbParameters.put(AdjustKeys.COLOUR, item.getVariation());
                // fbParameters.put(AdjustKeys.VARIATION, item.getVariation());            
                // parameters.put(AdjustKeys.BRAND, item.getBrand());               
                
                Adjust.trackEvent(eventCartLoadedFB);
            }
            
            Adjust.trackEvent(eventCartLoaded);
            break;
        
        default:
            
            break;
        }
    }

    public void trackEvent(Context context, TrackingEvent eventTracked, Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        Print.i(TAG, " Tracked Event --> " + eventTracked);

        switch (eventTracked) {

            case APP_OPEN:
                Print.i(TAG, "APP_OPEN:" + Adjust.isEnabled());
                Print.i(TAG, "code1adjust is APP_OPEN " + Adjust.isEnabled());
                AdjustEvent eventAppOpen = new AdjustEvent(mContext.getString(R.string.adjust_token_launch));

                eventAppOpen.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
                eventAppOpen.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
                eventAppOpen.addCallbackParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
                eventAppOpen.addPartnerParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
                eventAppOpen.addCallbackParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
                eventAppOpen.addPartnerParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
                eventAppOpen.addCallbackParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
                eventAppOpen.addPartnerParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
                eventAppOpen.addCallbackParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
                eventAppOpen.addPartnerParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
                eventAppOpen.addCallbackParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
                eventAppOpen.addPartnerParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
                eventAppOpen.addCallbackParameter(AdjustKeys.APP_PRE_INSTALL, String.valueOf(bundle.getBoolean(Constants.INFO_PRE_INSTALL)));
                eventAppOpen.addPartnerParameter(AdjustKeys.APP_PRE_INSTALL, String.valueOf(bundle.getBoolean(Constants.INFO_PRE_INSTALL)));
                if (!TextUtils.isEmpty(bundle.getString(Constants.INFO_SIM_OPERATOR))) {
                    eventAppOpen.addCallbackParameter(AdjustKeys.DEVICE_SIM_OPERATOR, bundle.getString(Constants.INFO_SIM_OPERATOR));
                    eventAppOpen.addPartnerParameter(AdjustKeys.DEVICE_SIM_OPERATOR, bundle.getString(Constants.INFO_SIM_OPERATOR));
                }
                Adjust.trackEvent(eventAppOpen);
                break;

            case LOGIN_SUCCESS:
                AdjustEvent eventLogin = new AdjustEvent(mContext.getString(R.string.adjust_token_log_in));
                eventLogin = getBaseParameters(eventLogin, bundle);
                Adjust.trackEvent(eventLogin);
                break;

            case LOGOUT_SUCCESS:
                AdjustEvent eventLogout = new AdjustEvent(mContext.getString(R.string.adjust_token_log_out));
                eventLogout = getBaseParameters(eventLogout, bundle);
                Adjust.trackEvent(eventLogout);
                break;

            case SIGNUP_SUCCESS:
                AdjustEvent eventSignUp = new AdjustEvent(mContext.getString(R.string.adjust_token_sign_up));
                eventSignUp = getBaseParameters(eventSignUp, bundle);
                Adjust.trackEvent(eventSignUp);
                break;

            case CHECKOUT_FINISHED: // Sale
                try {

                    Print.d(TAG, " TRACK REVENEU --> " + bundle.getDouble(TRANSACTION_VALUE));
                    increaseTransactionCount();

                    if (bundle.getBoolean(IS_FIRST_CUSTOMER)) {
                        AdjustEvent eventFirstCustomer = new AdjustEvent(mContext.getString(R.string.adjust_token_customer));
                        eventFirstCustomer = getBaseParameters(eventFirstCustomer, bundle);
                        Adjust.trackEvent(eventFirstCustomer);
                    }

                    ArrayList<String> skus = bundle.getStringArrayList(TRANSACTION_ITEM_SKUS);
                    StringBuilder sbSkus = new StringBuilder();
                    sbSkus.append("[");
                    for (String sku : skus) {
                        sbSkus.append(sku).append(",");
                    }
                    sbSkus.deleteCharAt(sbSkus.length() - 1);
                    sbSkus.append("]");

                    // Track Revenue (Sale or Guest Sale)
                    String eventString = bundle.getBoolean(IS_GUEST_CUSTOMER) ? mContext.getString(R.string.adjust_token_guest_sale) : mContext.getString(R.string.adjust_token_sale);
                    AdjustEvent eventRevenue = new AdjustEvent(eventString);
                    eventRevenue = getBaseParameters(eventRevenue, bundle);
                    eventRevenue.addCallbackParameter(AdjustKeys.SKUS, sbSkus.toString());
                    eventRevenue.addPartnerParameter(AdjustKeys.SKUS, sbSkus.toString());

                    eventRevenue.addCallbackParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
                    eventRevenue.addPartnerParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));

                    eventRevenue.setRevenue(bundle.getDouble(TRANSACTION_VALUE), EURO_CURRENCY);
                    Adjust.trackEvent(eventRevenue);

                    AdjustEvent eventTransaction = new AdjustEvent(mContext.getString(R.string.adjust_token_transaction_confirmation));
                    eventTransaction = getBaseParameters(eventTransaction, bundle);
                    eventTransaction.addCallbackParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
                    eventTransaction.addPartnerParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
                    eventTransaction.addCallbackParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));
                    eventTransaction.addPartnerParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));
                    if (bundle.getParcelable(CUSTOMER) != null) {
                        Customer customer = bundle.getParcelable(CUSTOMER);
                        String gender = getGender(customer);
                        if (!gender.equals(CustomerGender.UNKNOWN.name())){
                            eventTransaction.addCallbackParameter(AdjustKeys.GENDER, gender);
                            eventTransaction.addPartnerParameter(AdjustKeys.GENDER, gender);
                        }
                    }

                    ArrayList<PurchaseItem> cartItems = bundle.getParcelableArrayList(CART);
                    JSONObject json;
                    int productCount = 0;
                    String countString = "";
                    for (PurchaseItem item : cartItems) {
//                      AdjustEvent eventTransactionFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation));

                        json = new JSONObject();
                        try {
                            json.put(AdjustKeys.SKU, item.sku);
                            json.put(AdjustKeys.CURRENCY, EURO_CURRENCY);
                            json.put(AdjustKeys.QUANTITY, item.quantity);
                            json.put(AdjustKeys.PRICE, item.getPriceForTracking());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        eventTransaction.addCallbackParameter(AdjustKeys.PRODUCT + countString, json.toString());
                        eventTransaction.addPartnerParameter(AdjustKeys.PRODUCT + countString, json.toString());
                        countString = String.valueOf(++productCount);

                        //FIXME
//                        //FB - Transaction
//                        eventTransactionFB = getFBBaseParameters(eventTransactionFB, bundle);
//
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.SKU, item.sku);
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.SKU, item.sku);
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO));
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO));
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.QUANTITY, String.valueOf(item.quantity));
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.QUANTITY, String.valueOf(item.quantity));
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));
//
//                        if (item.getPriceForTracking() > 0d) {
//                            eventTransactionFB.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(item.getPriceForTracking()));
//                            eventTransactionFB.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(item.getPriceForTracking()));
//                            eventTransactionFB.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
//                            eventTransactionFB.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
//                        }
//
//
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
//                        eventTransactionFB.addCallbackParameter(AdjustKeys.TOTAL_TRANSACTION, String.valueOf(bundle.getDouble(TRANSACTION_VALUE)));
//                        eventTransactionFB.addPartnerParameter(AdjustKeys.TOTAL_TRANSACTION, String.valueOf(bundle.getDouble(TRANSACTION_VALUE)));
//                        Adjust.trackEvent(eventTransactionFB);
                    }
                    Adjust.trackEvent(eventTransaction);

                    AdjustEvent eventTransactionFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation));

                    eventTransactionFB = getFBTrackerBaseParameters(eventTransactionFB, bundle);
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(bundle.getDouble(TRANSACTION_VALUE)));
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(bundle.getDouble(TRANSACTION_VALUE)));
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, sbSkus.toString());
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, sbSkus.toString());
                    Adjust.trackEvent(eventTransactionFB);

                } catch (Exception e) {
                    //XXX ADJUST INTERNAL CRASH
                    //FATAL EXCEPTION: Adjust
                    //java.util.ConcurrentModificationException
                }
                break;

            case ADD_TO_CART:
                AdjustEvent eventAddToCart = new AdjustEvent(mContext.getString(R.string.adjust_token_add_to_cart));
                eventAddToCart = getBaseParameters(eventAddToCart, bundle);
                eventAddToCart.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventAddToCart.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));

                if (bundle.getDouble(VALUE) > 0d) {
                    eventAddToCart.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventAddToCart.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventAddToCart.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                    eventAddToCart.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                }
                Adjust.trackEvent(eventAddToCart);

                //FIXME
                AdjustEvent eventAddToCartFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation));

                eventAddToCartFB = getFBTrackerBaseParameters(eventAddToCartFB, bundle);
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(bundle.getDouble(VALUE)));
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, String.valueOf(bundle.getDouble(VALUE)));
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, bundle.getString(PRODUCT_SKU));
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, bundle.getString(PRODUCT_SKU));
                Adjust.trackEvent(eventAddToCartFB);

                break;

            case REMOVE_FROM_CART:
                AdjustEvent eventRemoveFromCart = new AdjustEvent(mContext.getString(R.string.adjust_token_remove_from_cart));
                eventRemoveFromCart = getBaseParameters(eventRemoveFromCart, bundle);
                eventRemoveFromCart.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventRemoveFromCart.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));

                if (bundle.getDouble(VALUE) > 0d) {
                    eventRemoveFromCart.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventRemoveFromCart.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventRemoveFromCart.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                    eventRemoveFromCart.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                }
                Adjust.trackEvent(eventRemoveFromCart);
                break;

            case ADD_TO_WISHLIST:
                AdjustEvent eventAddToWishlist = new AdjustEvent(mContext.getString(R.string.adjust_token_add_to_wishlist));
                eventAddToWishlist = getBaseParameters(eventAddToWishlist, bundle);
                eventAddToWishlist.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventAddToWishlist.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                if (bundle.getDouble(VALUE) > 0d) {
                    eventAddToWishlist.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventAddToWishlist.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventAddToWishlist.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                    eventAddToWishlist.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                }
                Adjust.trackEvent(eventAddToWishlist);
                break;

            case REMOVE_FROM_WISHLIST:
                AdjustEvent eventRemoveFromWishlist = new AdjustEvent(mContext.getString(R.string.adjust_token_remove_from_wishlist));
                eventRemoveFromWishlist = getBaseParameters(eventRemoveFromWishlist, bundle);
                eventRemoveFromWishlist.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventRemoveFromWishlist.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));

                if (bundle.getDouble(VALUE) > 0d) {
                    eventRemoveFromWishlist.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventRemoveFromWishlist.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    eventRemoveFromWishlist.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                    eventRemoveFromWishlist.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                }
                Adjust.trackEvent(eventRemoveFromWishlist);
                break;

            case SHARE:
                AdjustEvent eventShare = new AdjustEvent(mContext.getString(R.string.adjust_token_social_share));
                eventShare = getBaseParameters(eventShare, bundle);
                eventShare.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventShare.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                Adjust.trackEvent(eventShare);
                break;

            case CALL:
                AdjustEvent eventCall = new AdjustEvent(mContext.getString(R.string.adjust_token_call));
                eventCall = getBaseParameters(eventCall, bundle);
                Adjust.trackEvent(eventCall);
                break;

            case ADD_REVIEW:
                AdjustEvent eventReview = new AdjustEvent(mContext.getString(R.string.adjust_token_product_rate));
                eventReview = getBaseParameters(eventReview, bundle);
                eventReview.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                eventReview.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                Adjust.trackEvent(eventReview);
                break;

            case LOGIN_FB_SUCCESS:
                AdjustEvent eventLoginFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_connect));
                eventLoginFB = getBaseParameters(eventLoginFB, bundle);
                Adjust.trackEvent(eventLoginFB);
                break;
            case SEARCH:
                AdjustEvent eventSearch = new AdjustEvent(mContext.getString(R.string.adjust_token_search));
                eventSearch = getBaseParameters(eventSearch, bundle);
                eventSearch.addCallbackParameter(AdjustKeys.KEYWORDS, bundle.getString(SEARCH_TERM));
                eventSearch.addPartnerParameter(AdjustKeys.KEYWORDS, bundle.getString(SEARCH_TERM));
                if (bundle.getParcelable(CUSTOMER) != null) {
                    Customer customer = bundle.getParcelable(CUSTOMER);
                    String gender = getGender(customer);
                    if (!gender.equals(CustomerGender.UNKNOWN.name())){
                        eventSearch.addCallbackParameter(AdjustKeys.GENDER, gender);
                        eventSearch.addPartnerParameter(AdjustKeys.GENDER, gender);
                    }
                }
                Adjust.trackEvent(eventSearch);

                //FB - Search
                AdjustEvent eventSearchFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_search));
                eventSearchFB = getFBBaseParameters(eventSearchFB, bundle);
                if (bundle.containsKey(CATEGORY) && !TextUtils.isEmpty(bundle.getString(CATEGORY))){
                    eventSearchFB.addCallbackParameter(AdjustKeys.CATEGORY, bundle.getString(CATEGORY));
                    eventSearchFB.addPartnerParameter(AdjustKeys.CATEGORY, bundle.getString(CATEGORY));
                }

                if (bundle.containsKey(CATEGORY_ID) && !TextUtils.isEmpty(bundle.getString(CATEGORY_ID))){
                    eventSearchFB.addCallbackParameter(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
                    eventSearchFB.addPartnerParameter(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
                }


                eventSearchFB.addCallbackParameter(AdjustKeys.QUERY, bundle.getString(SEARCH_TERM));
                eventSearchFB.addPartnerParameter(AdjustKeys.QUERY, bundle.getString(SEARCH_TERM));
                Adjust.trackEvent(eventSearchFB);
                break;

            case VIEW_WISHLIST:
                //FB - View Wishlist
                AdjustEvent eventViewWishlist = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_wishlist));
                eventViewWishlist = getFBBaseParameters(eventViewWishlist, bundle);

                ArrayList<ProductMultiple> favourites = bundle.getParcelableArrayList(FAVORITES);

                Double WishlistTotal = 0.0;

                if (null != favourites) {

                    for (ProductMultiple fav : favourites) {
                        WishlistTotal += fav.getPriceForTracking();
                    }

                    for (ProductMultiple fav : favourites) {

                        eventViewWishlist.addCallbackParameter(AdjustKeys.BRAND, fav.getBrand());
                        eventViewWishlist.addPartnerParameter(AdjustKeys.BRAND, fav.getBrand());
                        eventViewWishlist.addCallbackParameter(AdjustKeys.TOTAL_WISHLIST, WishlistTotal.toString());
                        eventViewWishlist.addPartnerParameter(AdjustKeys.TOTAL_WISHLIST, WishlistTotal.toString());

//                        if( null != fav.getAttributes() && !TextUtils.isEmpty(fav.getAttributes().get("color"))){
//                        	 parameters.put(AdjustKeys.COLOUR, fav.getAttributes().get("color"));
//                        }
//                        fbParams.put(AdjustKeys.SKU + countString, fav.getSku());
                        eventViewWishlist.addCallbackParameter(AdjustKeys.SKU, fav.getSku());
                        eventViewWishlist.addPartnerParameter(AdjustKeys.SKU, fav.getSku());
                        eventViewWishlist.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                        eventViewWishlist.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
                        eventViewWishlist.addCallbackParameter(AdjustKeys.QUANTITY, "1");
                        eventViewWishlist.addPartnerParameter(AdjustKeys.QUANTITY, "1");
                        eventViewWishlist.addCallbackParameter(AdjustKeys.DISCOUNT, fav.hasDiscount() ? "y" : "n");
                        eventViewWishlist.addPartnerParameter(AdjustKeys.DISCOUNT, fav.hasDiscount() ? "y" : "n");

                        if (fav.hasSelectedSimpleVariation()) {
                            try {
                                //noinspection ConstantConditions
                                eventViewWishlist.addCallbackParameter(AdjustKeys.SIZE, fav.getSelectedSimple().getVariationValue());
                                eventViewWishlist.addPartnerParameter(AdjustKeys.SIZE, fav.getSelectedSimple().getVariationValue());
                            } catch (NullPointerException e) {
                                // ...
                            }
                        }

                        eventViewWishlist.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(fav.getPriceForTracking()));
                        eventViewWishlist.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(fav.getPriceForTracking()));
                        Adjust.trackEvent(eventViewWishlist);
                    }
                } else {
                    Adjust.trackEvent(eventViewWishlist);
                }
                break;

            default:
                break;
        }

    }

//    /**
//     * Just to aind adjust tracking debug, will be removed before going to prod
//     * @param mp
//     */
//    public static void printParameters(Map mp) {
//        Print.e("Adjust", "init ----------");
//        Iterator it = mp.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pairs = (Map.Entry)it.next();
//            Print.e("Adjust", "key=" + pairs.getKey() + " value=" + pairs.getValue());
//        }
//    }
    
    
    
    public boolean enabled() {
        return true;
    }

    public void trackTiming(TrackingPage screen, Bundle bundle) {

    }

    private AdjustEvent getBaseParameters(AdjustEvent event, Bundle bundle) {

        event.addCallbackParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        event.addPartnerParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        if (bundle.containsKey(USER_ID) && !TextUtils.isEmpty(bundle.getString(USER_ID))) {
            event.addCallbackParameter(AdjustKeys.USER_ID, bundle.getString(USER_ID));
            event.addPartnerParameter(AdjustKeys.USER_ID, bundle.getString(USER_ID));
        } else if (bundle.getParcelable(CUSTOMER) != null) {
            Customer customer = bundle.getParcelable(CUSTOMER);
            event.addCallbackParameter(AdjustKeys.USER_ID, customer.getIdAsString());
            event.addPartnerParameter(AdjustKeys.USER_ID, customer.getIdAsString());
        }
        event.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
        event.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
        event.addCallbackParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
        event.addPartnerParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
        event.addCallbackParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        event.addPartnerParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        event.addCallbackParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
        event.addPartnerParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);

        return event;
    }

    private AdjustEvent getFBBaseParameters(AdjustEvent event, Bundle bundle) {
        //TODO Validate initialization
        event = getBaseParameters(event, bundle);
        event.addCallbackParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
        event.addPartnerParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
        Address address = getAddressFromLocation();
        if (null != address) {
            if(address.getAdminArea() != null && !"".equals(address.getAdminArea())){
                event.addCallbackParameter(AdjustKeys.REGION, address.getAdminArea());
                event.addPartnerParameter(AdjustKeys.REGION, address.getAdminArea());
            }
            if(address.getLocality() != null && !"".equals(address.getLocality())){
                event.addCallbackParameter(AdjustKeys.CITY, address.getLocality());
                event.addPartnerParameter(AdjustKeys.CITY, address.getLocality());
            }
        }
        
        if (bundle.getParcelable(CUSTOMER) != null) {
            Customer customer = bundle.getParcelable(CUSTOMER);
            String gender = getGender(customer);
            if(!gender.equals(CustomerGender.UNKNOWN.name())){
                event.addCallbackParameter(AdjustKeys.GENDER, gender);
                event.addPartnerParameter(AdjustKeys.GENDER, gender);
            }
        }
        event.addCallbackParameter(AdjustKeys.AMOUNT_TRANSACTIONS, getTransactionCount());
        event.addPartnerParameter(AdjustKeys.AMOUNT_TRANSACTIONS, getTransactionCount());
        event.addCallbackParameter(AdjustKeys.AMOUNT_SESSIONS, getSessionsCount());
        event.addPartnerParameter(AdjustKeys.AMOUNT_SESSIONS, getSessionsCount());
        
        return event;
    }

    /**
     * sets the SHOP_COUNTRY, APP_VERSION, FB_CONTENT_TYPE, FB_CURRENCY fields for the specific event.
     *
     * @param event
     * @param bundle
     * @return
     */

    private AdjustEvent getFBTrackerBaseParameters(AdjustEvent event, Bundle bundle) {
        event.addCallbackParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        event.addPartnerParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        event.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
        event.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
        event.addCallbackParameter(AdjustKeys.FB_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        event.addPartnerParameter(AdjustKeys.FB_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        event.addCallbackParameter(AdjustKeys.FB_CURRENCY, EURO_CURRENCY);
        event.addPartnerParameter(AdjustKeys.FB_CURRENCY, EURO_CURRENCY);

        return event;
    }

    private String getDuration(long begin) {

        long current = System.currentTimeMillis();
        long result = current - begin;

        return String.valueOf(result);
    }
    
    private String getGender(Customer customer){
        return customer != null ? customer.getGender() : "n.a";
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            return NOT_AVAILABLE;
        } else {
            return pInfo.versionName;
        }
    }

    private Float getScreenSizeInches() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return (float) Math.round(screenInches * 10) / 10;
    }
    
    private String getDeviceType(boolean isTablet) {
        return isTablet ? TABLET : PHONE; 
    }

    /**
     * gets the number of sessions in the device
     * 
     * @return String
     */
    private String getSessionsCount() {
        SharedPreferences settings = mContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        return String.valueOf(settings.getInt(SESSION_COUNTER, 0));
    }    

    
    private String getTransactionCount() {
        SharedPreferences settings = mContext.getSharedPreferences(ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        return String.valueOf(settings.getInt(PURCHASE_NUMBER, 0));
    }    
    
    private void increaseTransactionCount() {
        SharedPreferences settings = mContext.getSharedPreferences(ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        int purchasesNumber = settings.getInt(PURCHASE_NUMBER, 0);
        purchasesNumber = purchasesNumber +1;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PURCHASE_NUMBER, purchasesNumber);
        editor.commit();
    }    
    
    public Address getAddressFromLocation() {
        // From geo location
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // From last known geo location
        return getAddressFromLastKnownLocation(locationManager);
    }
    
    private Address getAddressFromLastKnownLocation(LocationManager locationManager) {
        Address address = null;
        try {
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider != null) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
                double lat = lastKnownLocation.getLatitude();
                double lng = lastKnownLocation.getLongitude();
                address = getAddressFromGeoCode(lat, lng);
            }
            
        } catch (Exception e) {
            Print.w(TAG, "GET ADDRESS EXCEPTION: " + e.getMessage());
        }
        
        return address;
        
    }
    
    private Address getAddressFromGeoCode(double lat, double lng) {
        Address geoAddress = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (0 < addresses.size()) {
                geoAddress = addresses.get(0);
            }
        } catch (Exception e) {
            Print.w(TAG, "GET ADDRESS EXCEPTION: " + e.getMessage());
        }
        
        return geoAddress;
    }
    
    /**
     * Get the best location provider GPS or Network 
     * @param locationManager
     * @return String
     * @author sergiopereira
     */
    private String getBestLocationProvider(LocationManager locationManager){
        // Get the best provider
        String bestProvider = null;
        // Validate if GPS is enabled
        boolean isConnected = NetworkConnectivity.isConnected(mContext);
        if(isConnected && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            bestProvider = LocationManager.NETWORK_PROVIDER;
        }    
        // Validate if GPS disabled, connection and Network provider
        if(bestProvider == null && isConnected && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            bestProvider = LocationManager.GPS_PROVIDER;
        }
        // Return provider
        Print.i(TAG, "SELECTED PROVIDER: " + bestProvider);
        return bestProvider;
    }

    public static void resetTransactionCount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.apply();
    }

    /**
     * function that handles deeplinking reattributions
     * @param data
     */
    public static void deepLinkReattribution(Uri data){
        Adjust.appWillOpenUrl(data);
        //http://app.adjust.io/3tjw0j_7k6u7c?deep_link=DARAZ://pk/c/nike&adjust_tracker=f0ob4r&adjust_campaign=CampaignName&adjust_adgroup=AdGroupName&adjust_creative=CreativeName
        //data:DARAZ://pk/c/nike?adjust_reftag=c2z2rOt
    }

}
