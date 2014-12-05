package pt.rocket.framework.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pt.rocket.framework.R;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.tracking.GTMEvents.GTMKeys;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.utils.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

        mTagManager.setVerboseLoggingEnabled(context.getResources().getBoolean(R.bool.gtm_debug));
        
        dataLayer = TagManager.getInstance(context).getDataLayer();
        
        CONTAINER_ID = context.getResources().getString(R.string.gtm_key);
        Log.e(TAG,"init id:"+CONTAINER_ID);
        PendingResult<ContainerHolder> pending = mTagManager.loadContainerPreferNonDefault(CONTAINER_ID,R.raw.gtm_default_container);
        
        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                Log.e(TAG, "onResult");
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                mContainer = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e(TAG, "failure loading container");
//                    displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(mContainer);
                containerHolder.setContainerAvailableListener(new ContainerAvailableListener() {
                    
                    @Override
                    public void onContainerAvailable(ContainerHolder arg0, String arg1) {
                        Log.e(TAG, "onContainerAvailable");
                        isContainerAvailable = true;      
                        processPendingEvents();
                    }
                });
                isContainerAvailable = true;  
//                startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
        
//        refreshContainer();

//        dataLayer = mTagManager.getDataLayer();
    }
 

    /**
     * This method tracks if either the application was opened either by push
     * notification or if the app was started directly
     * 
     * @param appOpenContext
     */
    public void gtmTrackAppOpen(String version, Bundle deviceInfo, String countryIso, String campaignId, String source, String medium, boolean isFromPush) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAppOpen ( cointair available ? " + isContainerAvailable + " )");
        Log.d(TAG, "gtmTrackAppOpen campaignId:"+campaignId);
        Log.d(TAG, "gtmTrackAppOpen source:"+source);
        Log.d(TAG, "gtmTrackAppOpen medium:"+medium);

        Map<String, Object> message = null;
        String operator = "";
        operator = deviceInfo.getString(Constants.INFO_SIM_OPERATOR);        
        
        String deviceBrand = "";
        deviceBrand = deviceInfo.getString(Constants.INFO_BRAND);

        
        boolean isPreInstall = false;
        isPreInstall = deviceInfo.getBoolean(Constants.INFO_PRE_INSTALL, false);
        Log.d(TAG, "gtmTrackAppOpen isPreInstall:"+isPreInstall);
        if(isPreInstall) source = GTMValues.PRE_INSTALL;
        else source = GTMValues.ORGANIC;
        
        if(isFromPush){
            source = GTMValues.PUSH;
        }
            
            Log.d(TAG, "gtmTrackAppOpen"+" campaignId:"+campaignId+" source:"+source+" countryIso:"+countryIso+" version:"+version+" deviceBrand:"+deviceBrand);
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMEvents.GTMKeys.CAMPAIGN, campaignId, GTMEvents.GTMKeys.SOURCE, source, GTMEvents.GTMKeys.SHOPCOUNTRY,
//                  countryIso, GTMEvents.GTMKeys.APPVERSION, version,GTMEvents.GTMKeys.DEVICEBRAND, deviceBrand);
            
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP,  GTMEvents.GTMKeys.SHOPCOUNTRY, countryIso,GTMEvents.GTMKeys.SOURCE, source,
                    GTMEvents.GTMKeys.APPVERSION, version,GTMEvents.GTMKeys.DEVICEBRAND, deviceBrand);
            
            if(!TextUtils.isEmpty(campaignId)){
                message.put(GTMEvents.GTMKeys.CAMPAIGN, campaignId);
            }
            
            if(!TextUtils.isEmpty(operator))
                message.put(GTMEvents.GTMKeys.OPERATOR, operator);
            
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
       
        long milliseconds = System.currentTimeMillis();
        if ( milliseconds < loadTime || loadTime <= 0 ) {
            Log.d( TAG, "trackTiming ERROR : start -> " + loadTime );
            return;
        }
        milliseconds = milliseconds - loadTime;
        
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewScreen - " + screenName +" "+  milliseconds);
        
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
        Log.d(TAG, "gtmTrackLogin"+" method:"+method+" location:"+location+" customer.getIdAsString():"+customer.getIdAsString()+" customer.getCreatedAt():"+customer.getCreatedAt()+" customer.getGender():"+customer.getGender());
      
      //working
      Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN, GTMKeys.LOGINMETHOD, method, GTMKeys.LOGINLOCATION,
      location, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(), GTMKeys.USERGENDER, customer.getGender().toString());

      sendEvent(message);
    }
    
    public void gtmTrackLoginFailed(String method, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLoginFailed -> location " + location);
        Log.d(TAG, "gtmTrackLoginFailed"+" method:"+method+" location:"+location);

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
        
        Log.d(TAG, "gtmTrackAutoLogin"+" customer.getIdAsString():"+customer.getIdAsString()+" customer.getCreatedAt():"+customer.getCreatedAt()+" customer.getGender():"+customer.getGender());

      Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN, GTMKeys.CUSTOMERID, customer.getIdAsString(),
              GTMKeys.ACCOUNTCREATIONDATE, customer.getCreatedAt(), GTMKeys.USERGENDER, customer.getGender().toString());

      sendEvent(message);
    }
    
    
    public void gtmTrackAutoLoginFailed() {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAutoLoginFailed -> ");
        
        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN_FAILED);

        sendEvent(message);
    }
    
    
    public void gtmTrackLogout(String customerId) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLogout");
        Log.d(TAG, "gtmTrackLogout"+"  GTMValues.LOGOUT:"+ GTMValues.LOGOUT+" customerId:"+customerId);

        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGOUT, GTMKeys.LOGOUTLOCATION, GTMValues.LOGOUT,
                GTMKeys.CUSTOMERID, customerId);
        sendEvent(message);

    }

    public void gtmTrackRegister(String customerId, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRegister");
        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackRegister"+" GTMValues.EMAILAUTH:"+GTMValues.EMAILAUTH+" location:"+location+" customerId:"+customerId);

            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER, GTMKeys.REGISTRATIONMETHOD, GTMValues.EMAILAUTH,
                    GTMKeys.REGISTRATIONLOCATION, location, GTMKeys.CUSTOMERID, customerId);
        
        sendEvent(message);

    }
    
    public void gtmTrackRegisterFailed(String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRegisterFailed");
        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackRegisterFailed"+" GTMValues.EMAILAUTH:"+GTMValues.EMAILAUTH+" location:"+location);

            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER_FAILED, GTMKeys.REGISTRATIONMETHOD, GTMValues.EMAILAUTH, GTMKeys.REGISTRATIONLOCATION, location);
        
        sendEvent(message);

    }
    
    public void gtmTrackSignUp(String subscriberId, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSignUp ");
        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackSignUp"+" GTMValues.EMAILAUTH:"+GTMValues.EMAILAUTH+" location:"+location+" subscriberId:"+subscriberId);
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SIGNUP, GTMKeys.SUBSCRIBERID, subscriberId, GTMKeys.SIGNUPLOCATION, location);
        
        sendEvent(message);

    }
    
    public void gtmTrackSearch(String searchTerm, long numberItems) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSearch");

        Log.d(TAG, "gtmTrackSearch"+" searchTerm:"+searchTerm+" numberItems:"+numberItems);
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH, GTMKeys.SEARCHTERM, searchTerm, GTMKeys.RESULTSNUMBER, numberItems);


        sendEvent(message);
    }

    
    public void gtmTrackTransaction(List<PurchaseItem> items,String currencyName, double transactionValue, String transactionId, String coupon,
            String paymentMethod, String shippingAmount, String taxAmount) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackTransaction ");

        ArrayList<Map<String, Object>> products = new ArrayList<Map<String,Object>>();
        for (PurchaseItem item : items) {
            Map<String, Object> productsData = DataLayer.mapOf(GTMKeys.NAME, item.name, GTMKeys.SKU, item.sku, GTMKeys.CATEGORY, item.category,
                    GTMKeys.PRICE, item.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.QUANTITY, item.quantityAsInt);
            products.add(productsData);
        }
        
        
        Map<String, Object> message = null;
//        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_TRANSACTION, GTMKeys.PREVIOUSPURCHASES, DataLayer.OBJECT_NOT_PRESENT, GTMKeys.TRANSACTIONID, transactionId, GTMKeys.TRANSACTIONAFFILIATION, DataLayer.OBJECT_NOT_PRESENT,
//                GTMKeys.TRANSACTIONTOTAL, transactionValue, GTMKeys.TRANSACTIONCURRENCY,currencyName , GTMKeys.TRANSACTIONPRODUCTS, products);
        
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_TRANSACTION, GTMKeys.TRANSACTIONID, transactionId,
                GTMKeys.TRANSACTIONTOTAL, transactionValue, GTMKeys.TRANSACTIONCURRENCY,currencyName , GTMKeys.TRANSACTIONPRODUCTS, products);
        
        if(!TextUtils.isEmpty(coupon))
            message.put( GTMKeys.VOUCHERAMOUNT, coupon);
        
        if(!TextUtils.isEmpty(paymentMethod))
            message.put(GTMKeys.PAYMENTMETHOD, paymentMethod);

        if(!TextUtils.isEmpty(shippingAmount))
            message.put(GTMKeys.TRANSACTIONSHIPPING, shippingAmount);

        if(!TextUtils.isEmpty(taxAmount))
            message.put(GTMKeys.TRANSACTIONTAX, taxAmount);
        
        sendEvent(message);

    }
    
    public void gtmTrackShare(String location, String productSKU, String category) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackShare " + " categoy " + category  + " SHARELOCATION " + location+" productSku "+productSKU);

        Log.d(TAG, "gtmTrackShare"+" productSKU:"+productSKU);

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SHARE_PRODUCT, GTMKeys.PRODUCTSKU, productSKU, GTMKeys.SHARELOCATION, GTMValues.PRODUCTDETAILPAGE);

            if(!TextUtils.isEmpty(category))
                message.put( GTMKeys.PRODUCTCATEGORY, category);
            
        sendEvent(message);

    }


    public void gtmTrackChangeCountry(String country) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackChangeCountry");
        Log.d(TAG, "gtmTrackChangeCountry"+" country:"+country);
        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_COUNTRY, GTMEvents.GTMKeys.SHOPCOUNTRY, country);

        sendEvent(message);

    }

    public void gtmTrackViewProduct(String productSKU, double productPrice, String productBrand, String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewProduct [productSKU: " + productSKU + ", productPrice:" + productPrice + "] currencyName " + currencyName);
        

        Log.d(TAG, "gtmTrackViewProduct"+" productSKU:"+productSKU+" productBrand:"+productBrand
                +" productPrice:"+productPrice+" currencyName:"+currencyName+" discount:"+discount);

        Map<String, Object> message = null;
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_PRODUCT, GTMKeys.PRODUCTSKU, productSKU, GTMKeys.PRODUCTBRAND, productBrand,
                      GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.CURRENCY, currencyName, GTMKeys.DISCOUNT, discount);
            
            if(productRating != -1d) 
                message.put(GTMKeys.PRODUCTRATING, productRating);
            
            if(!TextUtils.isEmpty(productCategory)) 
                message.put(GTMKeys.PRODUCTCATEGORY, productCategory);
            
            if(!TextUtils.isEmpty(productSubCategory)) 
                message.put(GTMKeys.PRODUCTSUBCATEGORY, productSubCategory);
            
        sendEvent(message);

    }

    
    
    public void gtmTrackAddToCart(String productSKU, double productPrice, String productBrand, String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory, String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAddToCart (categ: " + productSKU + "; subcateg: " + productPrice + ")");
        

        
        Log.d(TAG, "gtmTrackAddToCart"+" productSKU:"+productSKU+" productBrand:"+productBrand
                +" productPrice:"+productPrice+" currencyName:"+currencyName+" discount:"+discount);
        Map<String, Object> message = null;
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_CART,GTMKeys.PRODUCTSKU, productSKU, GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand, GTMKeys.CURRENCY, currencyName,
                GTMKeys.DISCOUNT, discount, GTMKeys.PRODUCTQUANTITY, 1, GTMKeys.LOCATION, location);

        
        
        if(productRating != -1d) 
            message.put(GTMKeys.AVERAGERATINGTOTAL, productRating);
        
        if(!TextUtils.isEmpty(productCategory))
            message.put(GTMKeys.PRODUCTCATEGORY, productCategory);
        
        if(!TextUtils.isEmpty(productSubCategory)) 
            message.put(GTMKeys.PRODUCTSUBCATEGORY, productSubCategory);
        
        sendEvent(message);
    }
    
    public void gtmTrackRemoveFromCart(String productSku, double averageRatingTotal, double productPrice, long quantity, String cartValue, String currencyName) {

        Log.i(TAG, " GTM TRACKING -> gtmTrackRemoveFromCart (categ: " + productSku + "; subcateg: " + productPrice + ")");
        Map<String, Object> message = null;

        Log.d(TAG, "gtmTrackRemoveFromCart"+" productPrice:"+productPrice+" currencyName:"+currencyName+" productSku:"+productSku+" productPrice:"+productPrice+" cartValue:"+cartValue+" quantity:"+quantity);
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_CART, GTMKeys.PRODUCTSKU, productSku, GTMKeys.PRODUCTPRICE, productPrice,
                GTMKeys.QUANTITYCART, quantity, GTMKeys.CARTVALUE, cartValue, GTMKeys.CURRENCY, currencyName);

        if(averageRatingTotal != -1d) 
            message.put(GTMKeys.AVERAGERATINGTOTAL, averageRatingTotal);
        
        sendEvent(message);
    }
    
    
    public void gtmTrackRateProduct(CompleteProduct product,String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRateProduct");
        Map<String, Object> message = null;
        String category = "";
        String subCategory = "";
        if(null != product && product.getCategories().size() > 0){
            category = product.getCategories().get(0);
            if(null != product && product.getCategories().size() > 1){
                subCategory = product.getCategories().get(1);
            }
        }
        
        
        Log.d(TAG, "gtmTrackRateProduct"+" currencyName:"+currencyName+" product.getSku():"+product.getSku()+
                " PRODUCTPRICE:"+product.getPriceForTracking()+" currencyName:"+currencyName+" PRODUCTRATING:"+product.getRatingsAverage());
//        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_RATE_PRODUCT, GTMKeys.PRODUCTCATEGORY,GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.RATINGPRICE, notPresent,
//                GTMKeys.RATINGAPPEARANCE, notPresent,GTMKeys.RATINGQUALITY, notPresent, GTMKeys.PRODUCTRATING, product.getRatingsAverage());
        
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_RATE_PRODUCT,GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(),
                GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.PRODUCTRATING, product.getRatingsAverage());
        
        if(!TextUtils.isEmpty(category)) 
            message.put(GTMKeys.PRODUCTCATEGORY, category);
            
        if(!TextUtils.isEmpty(subCategory)) 
            message.put(GTMKeys.PRODUCTSUBCATEGORY, subCategory);
        
        
        sendEvent(message);

    }
    
    public void gtmTrackViewRating(CompleteProduct product, String currencyName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewRating");

        Map<String, Object> message = null;
        String category = "";
        String subCategory = "";
        if(null != product && product.getCategories().size() > 0){
            category = product.getCategories().get(0);
            if(null != product && product.getCategories().size() > 1){
                subCategory = product.getCategories().get(1);
            }
        }
        
        
        Log.d(TAG, "gtmTrackViewRating"+" productSku:"+product.getSku()+" AVERAGERATINGTOTAL:"+product.getRatingsAverage()+" productPrice:"+product.getPriceForTracking()+" currencyName:"+currencyName);

//      message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_RATING, GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(), GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.AVERAGERATINGPRICE, notPresent,
//              GTMKeys.AVERAGERATINGAPPEARANCE, notPresent, GTMKeys.AVERAGERATINGQUALITY, notPresent, GTMKeys.AVERAGERATINGTOTAL, product.getRatingsAverage());

      message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_RATING, GTMKeys.PRODUCTSKU, product.getSku(), GTMKeys.PRODUCTPRICE, product.getPriceForTracking(),
              GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND, product.getBrand(), GTMKeys.AVERAGERATINGTOTAL, product.getRatingsAverage());
      
      if(!TextUtils.isEmpty(category)) 
          message.put(GTMKeys.PRODUCTCATEGORY, category);
          
      if(!TextUtils.isEmpty(subCategory)) 
          message.put(GTMKeys.PRODUCTSUBCATEGORY, subCategory);
      
      
        sendEvent(message);

    }
    
    
    public void gtmTrackCatalog(String category, String subCategory,int pageNumber) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewCatalog (" + category + "; " + subCategory + "; ) "+pageNumber);

        
        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackCatalog"+" pageNumber:"+pageNumber);
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CATALOG, GTMKeys.PAGENUMBER, pageNumber);

        if(!TextUtils.isEmpty(category)) 
            message.put(GTMKeys.CATEGORY, category);
            
        if(!TextUtils.isEmpty(subCategory)) 
            message.put(GTMKeys.SUBCATEGORY, subCategory);
        
        sendEvent(message);

    }

    public void gtmTrackFilterCatalog(String filterType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackFilterCatalog");

        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackFilterCatalog"+" filterType:"+filterType);
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_FILTER_CATALOG, GTMKeys.FILTERTYPE, filterType);
        sendEvent(message);
    }

    public void gtmTrackSortCatalog(String sortType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSortCatalog "+sortType);

        Map<String, Object> message = null;
        Log.d(TAG, "gtmTrackSortCatalog"+" sortType:"+sortType);
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SORT_CATALOG, GTMKeys.SORTTYPE, sortType);
        sendEvent(message);
    }

    public void gtmTrackAddToWishList(String productSku,String productBrand,double productPrice, double productRating,double productDiscount, String currency, String location, String category, String subCategory) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAddToWishList ");
        

        
        Map<String, Object> message = null;
        
        message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_WL, GTMKeys.PRODUCTSKU, productSku, GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand, GTMKeys.CURRENCY, currency,
                GTMKeys.DISCOUNT, productDiscount, GTMKeys.LOCATION, location, GTMKeys.AVERAGERATINGTOTAL, productRating);

        if(!TextUtils.isEmpty(category)) 
            message.put(GTMKeys.PRODUCTCATEGORY, category);
            
        if(!TextUtils.isEmpty(subCategory)) 
            message.put(GTMKeys.PRODUCTSUBCATEGORY, subCategory);
        
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



    private void sendEvent(Map<String, Object> event) {
        Log.i(TAG, " sendEvent");
        if (isContainerAvailable) {
            Log.i(TAG, " PUSH DATA:"+event.get(EVENT_TYPE));
            Log.i(TAG, " PUSH DATA PENDING SIZE:"+pendingEvents.size());

            if(pendingEvents != null && pendingEvents.size() > 0){
                processPendingEvents();
                pendingEvents.clear();
            }
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

    public static void saveCampaignParams(Context context, String key, String value) {
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
