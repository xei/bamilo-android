package com.mobile.newFramework.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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
import com.mobile.newFramework.objects.checkout.PurchaseItem;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerGender;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.tracking.gtm.GTMKeys;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    public static final String MAIN_CATEGORY = "main_category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String BRAND_ID = "brand_id";
    public static final String TREE = "tree";
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
        public static final String BRAND = "brand";
        public static final String QUERY = "query";
        public static final String SIZE = "size";
        public static final String APP_PRE_INSTALL = Constants.INFO_PRE_INSTALL;
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

    private static boolean isEnabled = false;
    
    private static AdjustTracker sInstance;
    
    public static final String NOT_AVAILABLE = "n.a.";

    public final static String ADJUST_FIRST_TIME_KEY = "adjust_first_time";

    private static Context mContext;
    
    private static final String EURO_CURRENCY = "EUR";

    private static final String PRODUCT_CONTENT_TYPE = "product";

    private static final String PRICE_DECIMAL_FORMAT = "0.00";

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
            eventPDVScreen.addCallbackParameter(AdjustKeys.PRODUCT, prod.getSku());
            eventPDVScreen.addPartnerParameter(AdjustKeys.PRODUCT, prod.getSku());

            // Add category ID
            eventPDVScreen.addCallbackParameter(AdjustKeys.CATEGORY_ID, prod.getCategoryId());
            eventPDVScreen.addPartnerParameter(AdjustKeys.CATEGORY_ID, prod.getCategoryId());

            // Add brand ID
            if(prod.getBrandId() != 0){
                eventPDVScreen.addCallbackParameter(BRAND_ID, String.valueOf(prod.getBrandId()));
                eventPDVScreen.addPartnerParameter(BRAND_ID, String.valueOf(prod.getBrandId()));
            }


            Adjust.trackEvent(eventPDVScreen);

            // FB - View Product
            AdjustEvent eventPDVScreenFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_product));
            // format sku as array
            String fbSkuList = convertParameterToStringArray(prod.getSku());
            //format price with 2 c.d.
            String formattedPrice = formatPriceForTracking(prod.getPriceForTracking(),PRICE_DECIMAL_FORMAT);

            eventPDVScreenFB = getFBTrackerBaseParameters(eventPDVScreenFB, bundle);

            eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
            eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
            eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, fbSkuList);
            eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, fbSkuList);

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

            if(bundle.containsKey(AdjustKeys.CATEGORY_ID)){
                // Add category ID
                eventCatalogSorted.addCallbackParameter(AdjustKeys.CATEGORY_ID, bundle.getString(AdjustKeys.CATEGORY_ID));
                eventCatalogSorted.addPartnerParameter(AdjustKeys.CATEGORY_ID, bundle.getString(AdjustKeys.CATEGORY_ID));
            }
            if(bundle.containsKey(BRAND_ID) ){
                // Add brand ID
                eventCatalogSorted.addCallbackParameter(BRAND_ID, bundle.getString(BRAND_ID));
                eventCatalogSorted.addPartnerParameter(BRAND_ID, bundle.getString(BRAND_ID));
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
            eventCatalogSortedFB.addCallbackParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(MAIN_CATEGORY));
            eventCatalogSortedFB.addPartnerParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(MAIN_CATEGORY));

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

            Adjust.trackEvent(eventCartLoaded);
            break;
        
        default:
            
            break;
        }
    }


    /**
     * Return a string representing an array of string parameters  - only for facebook tracking purposes
     * */
    private String convertParameterToStringArray(String parameter){
        return new StringBuilder().append("[\"").append(parameter).append("\"]").toString();
    }


    /**
     * Return a string representing an array of string parameters provided in a List parameter - only for facebook tracking purposes
     * */
    private String convertListParameterToStringArray(List <String> parametersList){
        StringBuilder sb = new StringBuilder();

        if(CollectionUtils.isNotEmpty(parametersList)){
            sb.append("[");
            for (String sku : parametersList) {
                sb.append("\"").append(sku).append("\"").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }

        return sb.toString();
    }


    /**
     * Returns a string price formated with format
     * */
    public String formatPriceForTracking(double priceForTracking, String format){
        return new DecimalFormat(format).format(priceForTracking);

    }



    public void trackEvent(TrackingEvent eventTracked, Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        Print.i(TAG, " Tracked Event --> " + eventTracked);

        switch (eventTracked) {

            case APP_OPEN:
                Print.i(TAG, "APP_OPEN:" + Adjust.isEnabled());
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

                    //convert list to an array in a string
                    String skuList = convertListParameterToStringArray(bundle.getStringArrayList(TRANSACTION_ITEM_SKUS));
                    //format price with 2 c.d.
                    String formattedPrice = formatPriceForTracking(bundle.getDouble(TRANSACTION_VALUE),PRICE_DECIMAL_FORMAT);

                    // Track Revenue (Sale or Guest Sale)
                    String eventString = bundle.getBoolean(IS_GUEST_CUSTOMER) ? mContext.getString(R.string.adjust_token_guest_sale) : mContext.getString(R.string.adjust_token_sale);
                    AdjustEvent eventRevenue = new AdjustEvent(eventString);
                    eventRevenue = getBaseParameters(eventRevenue, bundle);
                    eventRevenue.addCallbackParameter(AdjustKeys.SKUS, skuList);
                    eventRevenue.addPartnerParameter(AdjustKeys.SKUS, skuList);

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

                    }
                    Adjust.trackEvent(eventTransaction);

                    AdjustEvent eventTransactionFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation));

                    eventTransactionFB = getFBTrackerBaseParameters(eventTransactionFB, bundle);
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, skuList);
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, skuList);
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

                AdjustEvent eventAddToCartFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_add_to_cart));
                //convert string to array string
                String skuArray = convertParameterToStringArray(bundle.getString(PRODUCT_SKU));
                // format price to 2 c.d.
                String formattedPrice = formatPriceForTracking(bundle.getDouble(VALUE),PRICE_DECIMAL_FORMAT);

                eventAddToCartFB = getFBTrackerBaseParameters(eventAddToCartFB, bundle);
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, skuArray);
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, skuArray);
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
                break;
            default:
                break;
        }

    }

    public boolean enabled() {
        return true;
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

    /**
     * sets the SHOP_COUNTRY, APP_VERSION, FB_CONTENT_TYPE, FB_CURRENCY fields for the specific event.
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

    private void increaseTransactionCount() {
        SharedPreferences settings = mContext.getSharedPreferences(ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        int purchasesNumber = settings.getInt(PURCHASE_NUMBER, 0);
        purchasesNumber = purchasesNumber +1;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PURCHASE_NUMBER, purchasesNumber);
        editor.apply();
    }

    public static void resetTransactionCount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.apply();
    }

    /**
     * function that handles deeplinking reattributions
     */
    public static void deepLinkReAttribution(Uri data){
        Adjust.appWillOpenUrl(data);
        //http://app.adjust.io/3tjw0j_7k6u7c?deep_link=DARAZ://pk/c/nike&adjust_tracker=f0ob4r&adjust_campaign=CampaignName&adjust_adgroup=AdGroupName&adjust_creative=CreativeName
        //data:DARAZ://pk/c/nike?adjust_reftag=c2z2rOt
    }

}
