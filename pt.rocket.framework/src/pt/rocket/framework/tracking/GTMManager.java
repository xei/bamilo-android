package pt.rocket.framework.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pt.rocket.framework.R;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.tracking.GTMEvents.GTMKeys;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.Container.FunctionCallMacroCallback;
import com.google.android.gms.tagmanager.Container.FunctionCallTagCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.ContainerHolder.ContainerAvailableListener;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import de.akquinet.android.androlog.Log;


public class GTMManager {

    private final static String TAG = GTMManager.class.getSimpleName();

    volatile static Container mContainer;
    private static String EVENT_TYPE = "event";
    private String CONTAINER_ID = "";
    private static boolean isContainerAvailable = false;
    private boolean testMode = true;
    private static DataLayer dataLayer;
    private static GTMManager gtmTrackingManager;

    public static final String GA_PREFERENCES = "gaPreferences";
    public static final String CAMPAIGN_ID_KEY = "campaignId";
    public static final String CAMPAIGN_SOURCE = "campaignSource";
    public static final String CAMPAIGN_MEDIUM = "campaignMedium";
    public static final String IS_GTM_CAMPAIGN_SET = "isGtmCampaignSet";
    public static final String IS_GA_CAMPAIGN_SET = "isGaCampaignSet";
    public static final String IS_REFERRER_CAMPAIGN_SET = "isReferrerCampaignSet";

    private TagManager mTagManager;
    private Handler refreshHandler = new Handler();



    private final int REFRESH_INTERVAL = 1000 * 60 * 60; // 60 minutes

    private static ArrayList<Map<String, Object>> pendingEvents = new ArrayList<Map<String, Object>>();

    public static GTMManager get() {
        if (gtmTrackingManager == null) {
            return gtmTrackingManager = new GTMManager();
        } else
            return gtmTrackingManager;
    }

    private GTMManager() {
    };

    public static void init(Context context, String countryIso) {
        gtmTrackingManager = new GTMManager(context, countryIso);
    }

    @SuppressLint("NewApi")
    private GTMManager(Context context, String countryIso) {
        Log.i(TAG, " STARTING GTM TRACKING");
        mTagManager = TagManager.getInstance(context);
        isContainerAvailable = false;

        mTagManager.setVerboseLoggingEnabled(true);
        
        CONTAINER_ID = context.getResources().getString(R.string.gtm_key);
        
        PendingResult<ContainerHolder> pending = mTagManager.loadContainerPreferNonDefault(CONTAINER_ID,R.raw.gtm_default_container);
        
        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                Log.d("GTM", "onResult");
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                mContainer = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("GTM", "failure loading container");
//                    displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(mContainer);
                containerHolder.setContainerAvailableListener(new ContainerAvailableListener() {
                    
                    @Override
                    public void onContainerAvailable(ContainerHolder arg0, String arg1) {
                        Log.d("GTM", "onContainerAvailable");
                        isContainerAvailable = true;      
                        processPendingEvents();
                    }
                });
                isContainerAvailable = true;  
//                startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
        
//        refreshContainer();

        dataLayer = mTagManager.getDataLayer();
    }
 

    /**
     * This method tracks if either the application was opened either by push
     * notification or if the app was started directly
     * 
     * @param appOpenContext
     */
    public void gtmTrackAppOpen(Context context, String isPreinstalled, Bundle bundle, String countryIso, String campaignId, String source, String medium) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAppOpen ( cointair available ? " + isContainerAvailable + " )");
        Log.d(TAG, "gtmTrackAppOpen appOpenContext:"+isPreinstalled);
        Log.d(TAG, "gtmTrackAppOpen campaignId:"+campaignId);
        Log.d(TAG, "gtmTrackAppOpen source:"+source);
        Log.d(TAG, "gtmTrackAppOpen medium:"+medium);
        
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        
        
        PackageInfo pInfo;
        String version = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, "Error getting version info : ", e);
        }
        
        Map<String, Object> message = null;
        
//        if (TextUtils.isEmpty(campaignId) || TextUtils.isEmpty(getCampaignParams(context, IS_GTM_CAMPAIGN_SET))) {
            
            Log.d("BETA", "gtmTrackAppOpen 3");
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.SHOPCOUNTRY,
                  countryIso, GTMEvents.GTMKeys.APPVERSION, version);
            sendEvent(message);
            
//        }
        
//
//        Log.i(TAG, " GTM TRACKING -> gtmTrackAppOpen [" + appOpenContext + ", " + countryIso + ", " + version);
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(campaignId) || TextUtils.isEmpty(getCampaignParams(context, IS_GTM_CAMPAIGN_SET))) {
//            Log.d("BETA", "gtmTrackAppOpen 1");
//            Log.d(TAG, "GTM Campaign : " + campaignId + ", source : " + source + ", medium : " + medium);
//            Log.d("BETA", "GTM Campaign : " + campaignId + ", source : " + source + ", medium : " + medium);
//            if (TextUtils.isEmpty(getUserId())) {
//                Log.d("BETA", "gtmTrackAppOpen 2");
//                if (appOpenContext.equalsIgnoreCase(GTMValues.PRE_INSTALL)) {
//                    Log.d("BETA", "gtmTrackAppOpen PREINSTALL");
//                    message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.MEDIUM, medium,
//                            GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SHOPCOUNTRY, countryIso, GTMEvents.GTMKeys.APPVERSION, version,
//                            GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//                } else {
//                    Log.d("BETA", "gtmTrackAppOpen 3");
//                    message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, appOpenContext, GTMEvents.GTMKeys.SHOPCOUNTRY,
//                            countryIso, GTMEvents.GTMKeys.APPVERSION, version);
//                }
//
//            } else {
//                Log.d("BETA", "gtmTrackAppOpen 4");
//                if (appOpenContext.equalsIgnoreCase(GTMValues.PRE_INSTALL)) {
//                    Log.d("BETA", "gtmTrackAppOpen PREINSTALL");
//                    message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.MEDIUM, medium,
//                            GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SHOPCOUNTRY, countryIso, GTMEvents.GTMKeys.APPVERSION, version,
//                            GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//                } else {
//                    Log.d("BETA", "gtmTrackAppOpen 5");
//                    message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, appOpenContext, GTMEvents.GTMKeys.SHOPCOUNTRY,
//                            countryIso, GTMEvents.GTMKeys.APPVERSION, version, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//                }
//
//            }
//        } else {
//            Log.d("BETA", "gtmTrackAppOpen 6");
//            Log.d(TAG, "Saving GTM Campaign : " + campaignId + ", source : " + source + ", medium : " + medium);
//            Log.d("BETA", "Saving GTM Campaign : " + campaignId + ", source : " + source + ", medium : " + medium);
//            if (TextUtils.isEmpty(getUserId())) {
//                Log.d(TAG, "getUserId empty");
//                Log.d("BETA", "gtmTrackAppOpen 7");
//                message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.MEDIUM, medium,
//                        GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SHOPCOUNTRY, countryIso, GTMEvents.GTMKeys.APPVERSION, version);
//            } else {
//                Log.d(TAG, "getUserId not empty");
//                Log.d("BETA", "gtmTrackAppOpen 8");
//                message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.MEDIUM, medium,
//                        GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SHOPCOUNTRY, countryIso, GTMEvents.GTMKeys.APPVERSION, version,
//                        GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//            }
//        }
//
//        sendEvent(message);

    }

    public void gtmTrackViewScreen(String screenName, long loadTime) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewScreen - " + screenName);
        
        
        long milliseconds = System.currentTimeMillis();
        if ( milliseconds < loadTime || loadTime <= 0 ) {
            Log.d( TAG, "trackTiming ERROR : start -> " + loadTime );
            return;
        }
        milliseconds = milliseconds - loadTime;
        
        Map<String, Object> message = null;
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_SCREEN, GTMKeys.SCREENNAME, screenName, GTMKeys.LOADTIME, milliseconds);

        sendEvent(message);
    }

    public void gtmTrackLogin(Customer customer, TrackingEvent event, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLogin -> ");
        
        String method = GTMValues.EMAILAUTH;
        if(event == TrackingEvent.LOGIN_FB_SUCCESS) method = GTMValues.FACEBOOK;
//TODO       
//        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN, GTMKeys.LOGINMETHOD, method, GTMKeys.LOGINLOCATION,
//                location, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(), GTMKeys.USERAGE,
//                customer.getAge(), GTMKeys.USERGENDER, customer.getGender() ,GTMKeys.NUMBERPURCHASES, customer.getPurchaseNumber());
//
//        sendEvent(message);
        
      Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN, GTMKeys.LOGINMETHOD, method, GTMKeys.LOGINLOCATION,
      location, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(), GTMKeys.USERAGE,
      DataLayer.OBJECT_NOT_PRESENT, GTMKeys.USERGENDER, customer.getGender() ,GTMKeys.NUMBERPURCHASES, DataLayer.OBJECT_NOT_PRESENT);

      sendEvent(message);
    }
    
    public void gtmTrackLoginFailed(String method, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLoginFailed -> location " + location);
        
        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN_FAILED, GTMKeys.LOGINMETHOD, method, GTMKeys.LOGINLOCATION,
                location);

        sendEvent(message);
    }

    public void gtmTrackAutoLogin(Customer customer) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAutoLogin -> (created at: " + customer.getCreatedAt() + ") " );
//TODO  
//        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(),
//                GTMKeys.USERAGE,customer.getAge(), GTMKeys.USERGENDER, customer.getGender() ,GTMKeys.NUMBERPURCHASES, customer.getPurchaseNumber());
//
//        sendEvent(message);
      Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(),
      GTMKeys.USERAGE,DataLayer.OBJECT_NOT_PRESENT, GTMKeys.USERGENDER, customer.getGender() ,GTMKeys.NUMBERPURCHASES, DataLayer.OBJECT_NOT_PRESENT);

      sendEvent(message);
    }
    
    
    public void gtmTrackAutoLoginFailed() {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAutoLoginFailed -> ");
        
        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN_FAILED);

        sendEvent(message);
    }
    
    
    public void gtmTrackLogout(String customerId) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLogout");
        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGOUT, GTMKeys.LOGOUTLOCATION, GTMValues.LOGOUT,
                GTMKeys.CUSTOMERID, customerId);
        sendEvent(message);

    }

    public void gtmTrackRegister(String customerId, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRegister");
        Map<String, Object> message = null;

            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER, GTMKeys.REGISTRATIONMETHOD, GTMValues.EMAILAUTH,
                    GTMKeys.REGISTRATIONLOCATION, location, GTMKeys.CUSTOMERID, customerId);
        
        sendEvent(message);

    }
    
    public void gtmTrackRegisterFailed(String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRegisterFailed");
        Map<String, Object> message = null;

            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER_FAILED, GTMKeys.REGISTRATIONMETHOD, GTMValues.EMAILAUTH, GTMKeys.REGISTRATIONLOCATION, location);
        
        sendEvent(message);

    }
    
    public void gtmTrackSignUp(String subscriberId, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSignUp ");
        Map<String, Object> message = null;

            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SIGNUP, GTMKeys.SUBSCRIBERID, subscriberId, GTMKeys.SIGNUPLOCATION, location);
        
        sendEvent(message);

    }
    
    public void gtmTrackSearch(String searchTerm, long numberItems) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSearch");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH, GTMKeys.SEARCHTERM, searchTerm, GTMKeys.RESULTSNUMBER, numberItems);


        sendEvent(message);
    }

    
    public void gtmTrackTransaction(List<PurchaseItem> items,String currencyName, double transactionValue, String transactionId, String coupon,
            String paymentMethod, String shippingAmount, String taxAmount) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackTransaction ");

        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        ArrayList<Map<String, Object>> products = new ArrayList<Map<String,Object>>();
        for (PurchaseItem item : items) {
            Map<String, Object> productsData = DataLayer.mapOf(GTMKeys.NAME, item.name, GTMKeys.SKU, item.sku, GTMKeys.CATEGORY, item.category,
                    GTMKeys.PRICE, item.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.QUANTITY, item.quantityAsInt);
            products.add(productsData);
        }
        
        Object finalCupon;
        if(TextUtils.isEmpty(coupon)) finalCupon =  notPresent;
        else finalCupon = (String) coupon;
        
        Object finalPaymentMethod;
        if(TextUtils.isEmpty(paymentMethod)) finalPaymentMethod =  notPresent;
        else finalPaymentMethod = (String) paymentMethod;
        
        Object shipping;
        if(TextUtils.isEmpty(shippingAmount)) shipping =  notPresent;
        else shipping = (String) shippingAmount;
        
        Object tax;
        if(TextUtils.isEmpty(taxAmount)) tax =  notPresent;
        else tax = (String) taxAmount;
        
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_TRANSACTION, GTMKeys.PAYMENTMETHOD, finalPaymentMethod, GTMKeys.VOUCHERAMOUNT,
                    finalCupon, GTMKeys.PREVIOUSPURCHASES, DataLayer.OBJECT_NOT_PRESENT, GTMKeys.TRANSACTIONID, transactionId, GTMKeys.TRANSACTIONAFFILIATION, DataLayer.OBJECT_NOT_PRESENT,
                    GTMKeys.TRANSACTIONTOTAL, transactionValue, GTMKeys.TRANSACTIONSHIPPING, shipping, GTMKeys.TRANSACTIONTAX, tax,
                    GTMKeys.TRANSACTIONCURRENCY,currencyName , GTMKeys.TRANSACTIONPRODUCTS, products);

        sendEvent(message);

    }
    
    public void gtmTrackShare(String location, String productSKU, String category) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackShare " + " categoy " + category  + " SHARELOCATION " + location+" productSku "+productSKU);

        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Object finalCategory;
        if(TextUtils.isEmpty(category)) finalCategory =  notPresent;
        else finalCategory = (String) category;
        
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SHARE_PRODUCT, GTMKeys.SHARELOCATION, DataLayer.OBJECT_NOT_PRESENT, GTMKeys.PRODUCTSKU,
                    productSKU, GTMKeys.PRODUCTCATEGORY, finalCategory);

        sendEvent(message);

    }


    public void gtmTrackChangeCountry(String country) {
        Log.i("ChangeCountry", " GTM TRACKING -> gtmTrackChangeCountry");
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_COUNTRY, GTMEvents.GTMKeys.SHOPCOUNTRY, country);

        sendEvent(message);

    }

    public void gtmTrackViewProduct(String productSKU, double productPrice, String productBrand, String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewProduct [productSKU: " + productSKU + ", productPrice:" + productPrice + "] currencyName " + currencyName);
        
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Object finalCategory;
        if(TextUtils.isEmpty(productCategory)) finalCategory =  notPresent;
        else finalCategory = (String) productCategory;
        Object finalSubCategory;
        if(TextUtils.isEmpty(productSubCategory)) finalSubCategory =  notPresent;
        else finalSubCategory = (String) productSubCategory;
        
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_PRODUCT, GTMKeys.PRODUCTSKU, productSKU, GTMKeys.PRODUCTBRAND, productBrand,
                    GTMKeys.PRODUCTCATEGORY, finalCategory, GTMKeys.PRODUCTSUBCATEGORY, finalSubCategory, GTMKeys.PRODUCTPRICE, productPrice,
                    GTMKeys.CURRENCY, currencyName, GTMKeys.DISCOUNT, discount, GTMKeys.PRODUCTRATING, productRating);
            
        sendEvent(message);

    }

    
    
    public void gtmTrackAddToCart(String productSKU, double productPrice, String productBrand, String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAddToCart (categ: " + productSKU + "; subcateg: " + productPrice + ")");
        
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Object rating = productRating;
        if(productRating == -1d) rating = notPresent;
        
        Object finalCategory;
        if(TextUtils.isEmpty(productCategory)) finalCategory =  notPresent;
        else finalCategory = (String) productCategory;
        Object finalSubCategory;
        if(TextUtils.isEmpty(productSubCategory)) finalSubCategory =  notPresent;
        else finalSubCategory = (String) productSubCategory;
        
        Map<String, Object> message = null;
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_CART, GTMKeys.PRODUCTCATEGORY, finalCategory, GTMKeys.PRODUCTSUBCATEGORY, finalSubCategory,
                GTMKeys.PRODUCTSKU, productSKU, GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand, GTMKeys.CURRENCY, currencyName,
                GTMKeys.DISCOUNT, discount, GTMKeys.PRODUCTQUANTITY, 1, GTMKeys.LOCATION, location, GTMKeys.AVERAGERATINGTOTAL, rating);

        sendEvent(message);
    }
    
    public void gtmTrackRemoveFromCart(String productSku, double averageRatingTotal, double productPrice, long quantity, String cartValue, String currencyName) {

        Log.i(TAG, " GTM TRACKING -> gtmTrackRemoveFromCart (categ: " + productSku + "; subcateg: " + productPrice + ")");
        Map<String, Object> message = null;
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Object rating = averageRatingTotal;
        if(averageRatingTotal == -1d) rating = notPresent;
        
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_CART, GTMKeys.PRODUCTSKU, productSku, GTMKeys.PRODUCTPRICE, productPrice,
                GTMKeys.QUANTITYCART, quantity, GTMKeys.CARTVALUE, cartValue, GTMKeys.CURRENCY, currencyName, GTMKeys.AVERAGERATINGTOTAL, rating);

        sendEvent(message);
    }
    
    
    public void gtmTrackRateProduct(CompleteProduct product,String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRateProduct");
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Map<String, Object> message = null;
        String category = "";
        String subCategory = "";
        if(null != product && product.getCategories().size() > 0){
            category = product.getCategories().get(0);
            if(null != product && product.getCategories().size() > 1){
                subCategory = product.getCategories().get(1);
            }
        }

        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_RATE_PRODUCT, GTMKeys.PRODUCTCATEGORY, category, GTMKeys.PRODUCTSUBCATEGORY, subCategory,
                GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.RATINGPRICE, notPresent,
                GTMKeys.RATINGAPPEARANCE, notPresent,GTMKeys.RATINGQUALITY, notPresent, GTMKeys.PRODUCTRATING, product.getRatingsAverage());
        
        sendEvent(message);

    }
    
    public void gtmTrackViewRating(CompleteProduct product, String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewRating");

        Map<String, Object> message = null;
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        String category = "";
        String subCategory = "";
        if(null != product && product.getCategories().size() > 0){
            category = product.getCategories().get(0);
            if(null != product && product.getCategories().size() > 1){
                subCategory = product.getCategories().get(1);
            }
        }
        
      message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_RATING, GTMKeys.PRODUCTCATEGORY, category, GTMKeys.PRODUCTSUBCATEGORY, subCategory,
              GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.AVERAGERATINGPRICE, notPresent,
              GTMKeys.AVERAGERATINGAPPEARANCE, notPresent, GTMKeys.AVERAGERATINGQUALITY, notPresent, GTMKeys.AVERAGERATINGTOTAL, product.getRatingsAverage());

        sendEvent(message);

    }
    
    
    public void gtmTrackCatalog(String category, String subCategory,int pageNumber) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewCatalog (" + category + "; " + subCategory + "; ) "+pageNumber);
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        Object finalCategory;
        if(TextUtils.isEmpty(category)) finalCategory =  notPresent;
        else finalCategory = (String) category;
        Object finalSubCategory;
        if(TextUtils.isEmpty(subCategory)) finalSubCategory =  notPresent;
        else finalSubCategory = (String) subCategory;
        
        Map<String, Object> message = null;

      message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CATALOG, GTMKeys.CATEGORY, finalCategory, GTMKeys.SUBCATEGORY, finalSubCategory,
              GTMKeys.PAGENUMBER, pageNumber);

        
        sendEvent(message);

    }

    public void gtmTrackFilterCatalog(String filterType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackFilterCatalog");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_FILTER_CATALOG, GTMKeys.FILTERTYPE, filterType);
        sendEvent(message);
    }

    public void gtmTrackSortCatalog(String sortType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSortCatalog "+sortType);

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SORT_CATALOG, GTMKeys.SORTTYPE, sortType);
        sendEvent(message);
    }

    public void gtmTrackAddToWishList(String productSku,String productBrand,double productPrice, double productRating,double productDiscount, String currency, String location, String category, String subCategory) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAddToWishList ");
        Object notPresent = DataLayer.OBJECT_NOT_PRESENT;
        
        Object finalCategory;
        if(TextUtils.isEmpty(category)) finalCategory =  notPresent;
        else finalCategory = (String) category;
        Object finalSubCategory;
        if(TextUtils.isEmpty(subCategory)) finalSubCategory =  notPresent;
        else finalSubCategory = (String) subCategory;
        
        Map<String, Object> message = null;
        
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_WL, GTMKeys.PRODUCTCATEGORY, finalCategory, GTMKeys.PRODUCTSUBCATEGORY, finalSubCategory,
                GTMKeys.PRODUCTSKU, productSku, GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand, GTMKeys.CURRENCY, currency,
                GTMKeys.DISCOUNT, productDiscount, GTMKeys.LOCATION, location, GTMKeys.AVERAGERATINGTOTAL, productRating);

        sendEvent(message);
    }



    public void gtmTrackRemoveFromWishList(String productSKU, double productPrice, double averageRatingTotal, String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRemoveFromWishList");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_WL, GTMKeys.PRODUCTSKU, productSKU, GTMKeys.PRODUCTPRICE, productPrice,
                    GTMKeys.CURRENCY, currencyName, GTMKeys.AVERAGERATINGTOTAL, averageRatingTotal);

        sendEvent(message);
    }

    public void gtmTrackViewCart(int quantityCart, double cartValue, String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewCart");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.CARTVALUE, cartValue, GTMKeys.QUANTITYCART, quantityCart, GTMKeys.CURRENCY, currencyName);

        sendEvent(message);
    }
    
    public void gtmTrackStartCheckout(int quantityCart, double cartValue,String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackStartCheckout");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.CARTVALUE, cartValue, GTMKeys.QUANTITYCART, quantityCart, GTMKeys.CURRENCY, currencyName);

        sendEvent(message);
    }
    
    public void gtmTrackEnterAddress(boolean isCorrect) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackEnterAddress");

        Map<String, Object> message = null;
        
       if(isCorrect) message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.ADDRESSCORRECT, GTMValues.YES);
       else message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.ADDRESSCORRECT, GTMValues.NO);
        sendEvent(message);
    }
    
    public void gtmTrackChoosePayment(String paymentMethod) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackChoosePayment");

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHOOSE_PAYMENT, GTMKeys.PAYMENTMETHOD, paymentMethod);

        sendEvent(message);
    }
    
    public void gtmTrackFailedPayment(String paymentMethod, double transactionTotal, String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackFailedPayment");

        Map<String, Object> message = null;
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_FAILED_PAYMENT, GTMKeys.PAYMENTMETHOD, paymentMethod, GTMKeys.TRANSACTIONTOTAL, transactionTotal, GTMKeys.CURRENCY, currencyName);

        sendEvent(message);
    }
    
    public void gtmTrackAppClose() {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAppClose");

        Map<String, Object> message = null;
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CLOSE_APP, GTMEvents.GTMKeys.SCREENNAME, GTMValues.HOME);

        sendEvent(message);
    }

    public void gtmTrackFreshInstall(String type, String source, String medium, String campaign) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackFreshInstall");

//        Map<Object, Object> message = null;
//        Log.i(TAG, " GTM TRACKING type-> " + type);
//        if (type.equalsIgnoreCase(GTMValues.ORGANIC)) {
//
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_INSTALL_APP);            
//
//        } else if (type.equalsIgnoreCase(GTMValues.REFERRER)) {
//
//            Log.i(TAG, " GTM TRACKING INSTALL source-> " + source + " medium:" + medium + " campaign:" + campaign);
//
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_INSTALL_APP);
//        }
//
//        if (!CONTAINER_ID.equals(DEFAULT_CONTAINER)) {
//            sendEvent(message);
//        } else {
//            pendingEvents.add(message);
//        }
    }



    Runnable refreshHandlerTask = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, " GTM TRACKING -> refreshContainer()");
//            if (null != mTagManager.getContainer(CONTAINER_ID))
//                mTagManager.getContainer(CONTAINER_ID).refresh();

            ContainerHolderSingleton.getContainerHolder().refresh();
            
            refreshHandler.removeCallbacks(refreshHandlerTask);
            refreshHandler.postDelayed(refreshHandlerTask, REFRESH_INTERVAL);
        }
    };

    void refreshContainer() {
        refreshHandler.removeCallbacks(refreshHandlerTask);
        refreshHandlerTask.run();
    }

    private void sendEvent(Map<String, Object> event) {
        Log.i(TAG, " sendEvent");
        if (isContainerAvailable) {
            Log.i(TAG, " PUSH DATA");
            dataLayer.push(event);
        } else {
            Log.i(TAG, " PENDING");
            pendingEvents.add(event);
        }

    }

    private void processPendingEvents() {
        Log.i(TAG, " GTM TRACKING -> processPendingEvents()");
        try {
            if (pendingEvents != null) {

                for (Map<String, Object> event : pendingEvents) {
                    Log.i(TAG, " GTM TRACKING -> processPendingEvents() -> Event : " + event.get(EVENT_TYPE));
                    if (dataLayer == null)
                        dataLayer = mTagManager.getDataLayer();
                    dataLayer.push(event);
                }
                pendingEvents.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCampaignParams(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    private void saveCampaignParams(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }


    ////////////////////////////////////////////////////////////////////////////////
    
    
    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            // Register a custom function call tag to the container.
            container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
        }
    }
    
    
    private static class CustomMacroCallback implements FunctionCallMacroCallback {
        private int numCalls;

        @Override
        public Object getValue(String name, Map<String, Object> parameters) {
            if ("increment".equals(name)) {
                return ++numCalls;
            } else if ("mod".equals(name)) {
                return (Long) parameters.get("key1") % Integer.valueOf((String) parameters.get("key2"));
            } else {
                throw new IllegalArgumentException("Custom macro name: " + name + " is not supported.");
            }
        }
    }

    private static class CustomTagCallback implements FunctionCallTagCallback {
        @Override
        public void execute(String tagName, Map<String, Object> parameters) {
            // The code for firing this custom tag.
            Log.i(TAG, "Custom function call tag :" + tagName + " is fired.");
        }
    }
    
    
}