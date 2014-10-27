package pt.rocket.framework.tracking;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container.FunctionCallMacroCallback;
import com.google.android.gms.tagmanager.Container.FunctionCallTagCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.ContainerHolder.ContainerAvailableListener;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import pt.rocket.framework.R;
import pt.rocket.framework.tracking.GTMEvents.GTMKeys;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.text.TextUtils;

//import com.google.tagmanager.Container;
//import com.google.tagmanager.ContainerOpener;
//import com.google.tagmanager.ContainerOpener.OpenType;
//import com.google.tagmanager.DataLayer;
//import com.google.tagmanager.Logger.LogLevel;
//import com.google.tagmanager.TagManager;

import de.akquinet.android.androlog.Log;

public class GTMManager {

    private final static String TAG = GTMManager.class.getSimpleName();

    volatile static Container mContainer;
    private String EVENT_TYPE = "event";
    private final String DEFAULT_CONTAINER = "";
    private String CONTAINER_ID = "";
    private boolean isContainerAvailable = false;
    private boolean testMode = true;
    private DataLayer dataLayer;
    private static GTMManager gtmTrackingManager;

    public static final String GA_PREFERENCES = "gaPreferences";
    public static final String CAMPAIGN_ID_KEY = "campaignId";
    public static final String CAMPAIGN_SOURCE = "campaignSource";
    public static final String CAMPAIGN_MEDIUM = "campaignMedium";
    public static final String IS_GTM_CAMPAIGN_SET = "isGtmCampaignSet";
    public static final String IS_GA_CAMPAIGN_SET = "isGaCampaignSet";
    public static final String IS_REFERRER_CAMPAIGN_SET = "isReferrerCampaignSet";

    private static TagManager mTagManager;
    private Handler refreshHandler = new Handler();

    private String productCategory = "";
    private String productSubcategory = "";

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
//        PendingResult<ContainerHolder> pending = mTagManager.loadContainerPreferNonDefault(CONTAINER_ID, 0);
        
        
        
        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                mContainer = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("CuteAnimals", "failure loading container");
//                    displayErrorToUser(R.string.load_error);
                    return;
                }
               
                ContainerHolderSingleton.setContainerHolder(containerHolder);
//                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerAvailableListener() {
                    
                    @Override
                    public void onContainerAvailable(ContainerHolder arg0, String arg1) {
                        isContainerAvailable = true;      
                        processPendingEvents();
                    }
                });
//                startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
        
        
        
//        if (null != mContainer) {
//            mContainer.close();
//        }
//
//        if (!CONTAINER_ID.equals(DEFAULT_CONTAINER)) {
//            Container gtmContainer = mTagManager.getContainer(CONTAINER_ID);
//            if (null != gtmContainer)
//                gtmContainer.close();
//        }
//
//        CONTAINER_ID = context.getResources().getString(R.string.gtm_key);
//        Log.i(TAG, "CONTAINER ID " + CONTAINER_ID);
//
//        mTagManager.getLogger().setLogLevel(LogLevel.DEBUG);
//
//        if (!CONTAINER_ID.equals("")) {
//            // The container is returned to containerFuture when available.
//            ContainerOpener.openContainer(mTagManager, // TagManager instance.
//                    CONTAINER_ID, // Tag Manager Container ID.
//                    OpenType.PREFER_FRESH, // Prefer not to get the default
//                                           // PREFER_NON_DEFAULT
//                                           // container, but stale is OK.
//                    null, // Time to wait for saved container to load (ms).
//                          // Default
//                          // is 2000ms.
//                    new ContainerOpener.Notifier() { // Called when container
//                                                     // loads.
//                        @Override
//                        public void containerAvailable(Container container) {
//                            // Handle assignment in callback to avoid blocking
//                            // main
//                            // thread.
//                            if (null != container) {
//                                mContainer = container;
//                                isContainerAvailable = true;
//                                processPendingEvents();
//                                Log.i(TAG, " !!! GTM TRACKING -> openContainer returned container #" + mContainer.getContainerId());
//                            } else {
//                                Log.i(TAG, " !!! GTM TRACKING -> openContainer returned a NULL container !!! ");
//                            }
//                        }
//                    });
//
//            refreshContainer();
//        }

        dataLayer = mTagManager.getDataLayer();
    }

    
    

    
    
    
    
    
    
    
    
    
    /**
     * This method tracks if either the application was opened either by push
     * notification or if the app was started directly
     * 
     * @param appOpenContext
     */
    public void gtmTrackAppOpen(Context context, String appOpenContext, String countryIso, String campaignId, String source, String medium) {
        Log.i("BETA", " GTM TRACKING -> gtmTrackAppOpen ( cointair available ? " + isContainerAvailable + " )");
        Log.d("BETA", "gtmTrackAppOpen appOpenContext:"+appOpenContext);
        Log.d("BETA", "gtmTrackAppOpen campaignId:"+campaignId);
        Log.d("BETA", "gtmTrackAppOpen source:"+source);
        Log.d("BETA", "gtmTrackAppOpen medium:"+medium);
//        PackageInfo pInfo;
//        String version = "";
//        try {
//            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            version = pInfo.versionName;
//        } catch (Exception e) {
//            Log.e(TAG, "Error getting version info : ", e);
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

    public void gtmTrackViewScreen(String screenName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewScreen - " + screenName);
        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_SCREEN, GTMEvents.GTMKeys.SCREENNAME, screenName);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_SCREEN, GTMEvents.GTMKeys.SCREENNAME, screenName, GTMEvents.GTMKeys.CUSTOMERID,
                    getUserId());
        }

        sendEvent(message);
    }
//TODO
    public void gtmTrackLogin(Context context, String location, String customerID, String method, String accountCreationDate) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLogin -> (created at: " + accountCreationDate + ")  location " + location);
//        String numberOfLeads = String.valueOf(getTotalCallCount(context) + getTotalEmailSentCount(context));
//        Map<Object, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN, GTMEvents.GTMKeys.LOGINMETHOD, method, GTMEvents.GTMKeys.LOGINLOCATION,
//                location, GTMEvents.GTMKeys.CUSTOMERID, customerID, GTMEvents.GTMKeys.ACCOUNTCREATIONDATE, accountCreationDate, GTMEvents.GTMKeys.NUMBERLEADS,
//                numberOfLeads);
//
//        sendEvent(message);
    }

    public void gtmTrackLogout() {
        Log.i(TAG, " GTM TRACKING -> gtmTrackLogout");
        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_LOGOUT, GTMEvents.GTMKeys.LOGOUTLOCATION, GTMEvents.GTMValues.SIDEMENU);
        sendEvent(message);

    }

    public void gtmTrackRegister(String location) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRegister");
        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER, GTMEvents.GTMKeys.REGISTRATIONMETHOD, GTMEvents.GTMValues.EMAILAUTH,
                    GTMEvents.GTMKeys.REGISTRATIONLOCATION, location);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER, GTMEvents.GTMKeys.REGISTRATIONMETHOD, GTMEvents.GTMValues.EMAILAUTH,
                    GTMEvents.GTMKeys.REGISTRATIONLOCATION, location, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
        }
        sendEvent(message);

    }

    public void gtmTrackShare(String location, String productSKU, String searchType, String propertyType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackShare " + " searchType " + searchType + " propertyType " + propertyType + " SHARELOCATION " + location+" productSku "+productSKU);

        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SHARE_PRODUCT, GTMEvents.GTMKeys.SHARELOCATION, location, GTMEvents.GTMKeys.PRODUCTSKU,
                    productSKU, GTMEvents.GTMKeys.PRODUCTCATEGORY, searchType, GTMEvents.GTMKeys.PRODUCTSUBCATEGORY, propertyType);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SHARE_PRODUCT, GTMEvents.GTMKeys.SHARELOCATION, location, GTMEvents.GTMKeys.PRODUCTSKU,
                    productSKU, GTMEvents.GTMKeys.PRODUCTCATEGORY, searchType, GTMEvents.GTMKeys.PRODUCTSUBCATEGORY, propertyType,
                    GTMEvents.GTMKeys.CUSTOMERID, getUserId());
        }

        sendEvent(message);

    }


    public void gtmTrackChangeCountry(String country) {
        Log.i("ChangeCountryLanguage", " GTM TRACKING -> gtmTrackChangeCountry");
        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_COUNTRY, GTMEvents.GTMKeys.SHOPCOUNTRY, country);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_COUNTRY, GTMEvents.GTMKeys.SHOPCOUNTRY, country, GTMEvents.GTMKeys.CUSTOMERID,
                    getUserId());
        }

        sendEvent(message);

    }

    public void gtmTrackChangeLanguage(String language) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackChangeLanguage");
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_LANGUAGE, GTMEvents.GTMKeys.SHOPLANGUAGE, language);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_LANGUAGE, GTMEvents.GTMKeys.SHOPLANGUAGE, language, GTMEvents.GTMKeys.CUSTOMERID,
//                    getUserId());
//        }
//
//        sendEvent(message);

    }

    public void gtmTrackViewProduct(String productSKU, double productPrice, String productVehicleType, String currencyName, String productMake,
            String productModel, String productYear, String productRegion, String productCity, String productArea, String productCondition,
            String procutTransmission, String productFuel, String productColorFamilyId) {

        Log.i(TAG, " GTM TRACKING -> gtmTrackViewProduct [productSKU: " + productSKU + ", productPrice:" + productPrice + "] currencyName " + currencyName);

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_PRODUCT, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCT_VEHICLE_TYPE,
//                    productVehicleType, GTMEvents.GTMKeys.PRODUCT_CONDITION, productCondition, GTMEvents.GTMKeys.PRODUCT_PRICE, productPrice,
//                    GTMEvents.GTMKeys.CURRENCY, currencyName, GTMEvents.GTMKeys.PRODUCT_MAKE, productMake, GTMEvents.GTMKeys.PRODUCT_MODEL, productModel,
//                    GTMEvents.GTMKeys.PRODUCT_YEAR, productYear, GTMEvents.GTMKeys.PRODUCT_REGION, productRegion, GTMEvents.GTMKeys.PRODUCT_CITY, productCity,
//                    GTMEvents.GTMKeys.PRODUCT_AREA, productArea, GTMEvents.GTMKeys.PRODUCT_COLOR, productColorFamilyId, GTMEvents.GTMKeys.PRODUCT_TRANSMISSION,
//                    procutTransmission, GTMEvents.GTMKeys.PRODUCT_FUEL, productFuel);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_PRODUCT, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCT_VEHICLE_TYPE,
//                    productVehicleType, GTMEvents.GTMKeys.PRODUCT_CONDITION, productCondition, GTMEvents.GTMKeys.PRODUCT_PRICE, productPrice,
//                    GTMEvents.GTMKeys.CURRENCY, currencyName, GTMEvents.GTMKeys.PRODUCT_MAKE, productMake, GTMEvents.GTMKeys.PRODUCT_MODEL, productModel,
//                    GTMEvents.GTMKeys.PRODUCT_YEAR, productYear, GTMEvents.GTMKeys.PRODUCT_REGION, productRegion, GTMEvents.GTMKeys.PRODUCT_CITY, productCity,
//                    GTMEvents.GTMKeys.PRODUCT_AREA, productArea, GTMEvents.GTMKeys.PRODUCT_COLOR, productColorFamilyId, GTMEvents.GTMKeys.PRODUCT_TRANSMISSION,
//                    procutTransmission, GTMEvents.GTMKeys.PRODUCT_FUEL, productFuel, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);

    }

    public void gtmTrackCatalog(String make, String model, String yearRange, String priceRange, String region, String city, String area, String condition,
            String vehicleType, String sortType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackCatalog (" + productCategory + "; " + productSubcategory + "; ) "+sortType);
//
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CATALOG, GTMEvents.GTMKeys.CATALOG_MAKE, make, GTMEvents.GTMKeys.CATALOG_MODEL, model,
//                    GTMEvents.GTMKeys.CATALOG_YEAR_RANGE, yearRange, GTMEvents.GTMKeys.CATALOG_PRICE_RANGE, priceRange, GTMEvents.GTMKeys.CATALOG_REGION,
//                    region, GTMEvents.GTMKeys.CATALOG_CITY, city, GTMEvents.GTMKeys.CATALOG_AREA, area, GTMEvents.GTMKeys.CATALOG_CONDITION, condition,
//                    GTMEvents.GTMKeys.CATALOG_VEHICLE_TYPE, vehicleType, GTMEvents.GTMKeys.SORTTYPE, sortType);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CATALOG, GTMEvents.GTMKeys.CATALOG_MAKE, make, GTMEvents.GTMKeys.CATALOG_MODEL, model,
//                    GTMEvents.GTMKeys.CATALOG_YEAR_RANGE, yearRange, GTMEvents.GTMKeys.CATALOG_PRICE_RANGE, priceRange, GTMEvents.GTMKeys.CATALOG_REGION,
//                    region, GTMEvents.GTMKeys.CATALOG_CITY, city, GTMEvents.GTMKeys.CATALOG_AREA, area, GTMEvents.GTMKeys.CATALOG_CONDITION, condition,
//                    GTMEvents.GTMKeys.CATALOG_VEHICLE_TYPE, vehicleType, GTMEvents.GTMKeys.SORTTYPE, sortType, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);

    }

    public void gtmTrackFilterCatalog(String filterType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackFilterCatalog");

        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_FILTER_CATALOG, GTMEvents.GTMKeys.FILTERTYPE, filterType);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_FILTER_CATALOG, GTMEvents.GTMKeys.FILTERTYPE, filterType, GTMEvents.GTMKeys.CUSTOMERID,
                    getUserId());
        }

        sendEvent(message);
    }

    public void gtmTrackSortCatalog(String sortType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSortCatalog "+sortType);

        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SORT_CATALOG, GTMEvents.GTMKeys.SORTTYPE, sortType);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SORT_CATALOG, GTMEvents.GTMKeys.SORTTYPE, sortType, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
        }

        sendEvent(message);
    }

    public void gtmTrackAddToWishList(String productSKU, String offerType, double productPrice, String currency) {

        Log.i(TAG, " GTM TRACKING -> gtmTrackAddToWishList (categ: " + productSKU + "; subcateg: " + productPrice + ")");

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_WL, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCTCATEGORY,
//                    offerType, GTMEvents.GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.CURRENCY, currency);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_WL, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCTCATEGORY,
//                    offerType, GTMEvents.GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.CURRENCY, currency, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackCreateAlert(String creationDate, String make, String model, String yearRange, String priceRange, String region, String vehicleType) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackCreateAlert make "+make+" model "+model+" yearRange "+yearRange+" price range "+priceRange+" creationDate "+creationDate);

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CREATE_ALERT, GTMEvents.GTMKeys.CREATIONDATE, creationDate, GTMEvents.GTMKeys.ALERT_MAKE, make,
//                    GTMEvents.GTMKeys.ALERT_MODEL, model, GTMEvents.GTMKeys.ALERT_YEAR, yearRange, GTMEvents.GTMKeys.ALERT_PRICE, priceRange,
//                    GTMEvents.GTMKeys.ALERT_REGION, region, GTMEvents.GTMKeys.ALERT_VEHICLE_TYPE, vehicleType);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CREATE_ALERT, GTMEvents.GTMKeys.CREATIONDATE, creationDate, GTMEvents.GTMKeys.ALERT_MAKE, make,
//                    GTMEvents.GTMKeys.ALERT_MODEL, model, GTMEvents.GTMKeys.ALERT_YEAR, yearRange, GTMEvents.GTMKeys.ALERT_PRICE, priceRange,
//                    GTMEvents.GTMKeys.ALERT_REGION, region, GTMEvents.GTMKeys.ALERT_VEHICLE_TYPE, vehicleType, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackDeleteAlert(String deletationDate) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackDeleteAlert "+deletationDate);

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_DELETE_ALERT, GTMEvents.GTMKeys.DELETATIONDATE, deletationDate);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_DELETE_ALERT, GTMEvents.GTMKeys.DELETATIONDATE, deletationDate, GTMEvents.GTMKeys.CUSTOMERID,
//                    getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackViewAlertsList(int numSavedAlerts) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackViewAlertsList");

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_ALERTS_LIST, GTMEvents.GTMKeys.NUMSAVEDALERTS, numSavedAlerts);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_ALERTS_LIST, GTMEvents.GTMKeys.NUMSAVEDALERTS, numSavedAlerts,
//                    GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackResetSearch() {
        Log.i(TAG, " GTM TRACKING -> gtmTrackResetSearch");

//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_RESET_SEARCH);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_RESET_SEARCH, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackRemoveFromWishList(String productSKU, double productPrice) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackRemoveFromWishList");

        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_WL, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCTPRICE,
                    productPrice);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_WL, GTMEvents.GTMKeys.PRODUCTSKU, productSKU, GTMEvents.GTMKeys.PRODUCTPRICE,
                    productPrice, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
        }

        sendEvent(message);
    }

    public void gtmTrackAppClose(String screenName) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackAppClose");

        Map<String, Object> message = null;
        if (TextUtils.isEmpty(getUserId())) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CLOSE_APP, GTMEvents.GTMKeys.SCREENNAME, screenName);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_CLOSE_APP, GTMEvents.GTMKeys.SCREENNAME, screenName, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
        }

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

    public void gtmTrackSearch(String searchTerm, long numberItems) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackSearch");
//
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH_SERVER, GTMEvents.GTMKeys.SEARCHTERM, searchTerm, GTMEvents.GTMKeys.RESULTSNUMBER,
//                    numberItems);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH_SERVER, GTMEvents.GTMKeys.SEARCHTERM, searchTerm, GTMEvents.GTMKeys.RESULTSNUMBER,
//                    numberItems, GTMEvents.GTMKeys.CUSTOMERID, getUserId());
//        }
//
//        sendEvent(message);
    }

    public void gtmTrackSearch(String vehicleType, String priceRange, String make, String model, String yearRange, String region, String city, String area,
            String condition, String searchCount) {

        Log.i(TAG, " GTM TRACKING -> gtmTrackSearch "+region);
//
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH_BUTTON, GTMEvents.GTMKeys.AREA, area, GTMEvents.GTMKeys.SEARCH_VEHICLE_TYPE,
//                    vehicleType, GTMEvents.GTMKeys.PRICE_RANGE, priceRange, GTMEvents.GTMKeys.MAKE, make, GTMEvents.GTMKeys.MODEL, model,
//                    GTMEvents.GTMKeys.YEAR_RANGE, yearRange, GTMEvents.GTMKeys.REGION, region, GTMEvents.GTMKeys.CITY, city,
//                    GTMEvents.GTMKeys.SEARCH_CONDITION, condition,GTMEvents.GTMKeys.RESULTSNUMBER,searchCount);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH_BUTTON, GTMEvents.GTMKeys.AREA, area, GTMEvents.GTMKeys.SEARCH_VEHICLE_TYPE,
//                    vehicleType, GTMEvents.GTMKeys.PRICE_RANGE, priceRange, GTMEvents.GTMKeys.MAKE, make, GTMEvents.GTMKeys.MODEL, model,
//                    GTMEvents.GTMKeys.YEAR_RANGE, yearRange, GTMEvents.GTMKeys.REGION, region, GTMEvents.GTMKeys.CITY, city,
//                    GTMEvents.GTMKeys.SEARCH_CONDITION, condition, GTMEvents.GTMKeys.CUSTOMERID, getUserId(),GTMEvents.GTMKeys.RESULTSNUMBER,searchCount);
//        }
//
//        sendEvent(message);

    }

    public void gtmTrackPushNotificationStatus(String status) {
        Log.i(TAG, " GTM TRACKING -> gtmTrackPushNotificationStatus");
//
//        Map<Object, Object> message = null;
//        if (TextUtils.isEmpty(getUserId())) {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_PUSH_NOTIFICATION, GTMEvents.GTM_PUSH_NOTIFICATION, status);
//        } else {
//            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_PUSH_NOTIFICATION, GTMEvents.GTM_PUSH_NOTIFICATION, status, GTMEvents.GTMKeys.CUSTOMERID,
//                    getUserId());
//        }
//
//        sendEvent(message);
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

    //TODO
//    private static int getTotalCallCount(Context context) {
//        SharedPreferences settings = context.getSharedPreferences(Ad4PushManager.AD4PUSH_PREFERENCES, 0);
//        return settings.getInt(context.getResources().getString(R.string.AD4PUSH_TOTAL_NUMBER_OF_CALLS_LABEL), 0);
//    }
//
//    private static int getTotalEmailSentCount(Context context) {
//        SharedPreferences settings = context.getSharedPreferences(Ad4PushManager.AD4PUSH_PREFERENCES, 0);
//        return settings.getInt(context.getResources().getString(R.string.AD4PUSH_TOTAL_NUMBER_OF_EMAILS_SENT_LABEL), 0);
//    }

    private static String getUserId() {
//        if ((CustomApplication.INSTANCE != null) && (CustomApplication.INSTANCE.getCurrentCustomer() != null)) {
//            int customerId = CustomApplication.INSTANCE.getCurrentCustomer().getId();
//            if (customerId > 0) {
//                return String.valueOf(customerId);
//            }
//        }
        return "";
    }

}
