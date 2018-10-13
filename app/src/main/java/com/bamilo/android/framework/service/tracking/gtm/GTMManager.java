package com.bamilo.android.framework.service.tracking.gtm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.objects.checkout.PurchaseItem;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.objects.product.pojo.ProductComplete;
import com.bamilo.android.framework.service.tracking.AbcBaseTracker;
import com.bamilo.android.framework.service.tracking.TrackingEvent;
import com.bamilo.android.framework.service.utils.Constants;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.Container.FunctionCallMacroCallback;
import com.google.android.gms.tagmanager.Container.FunctionCallTagCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.ContainerHolder.ContainerAvailableListener;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class GTMManager extends AbcBaseTracker {

    private final static String TAG = GTMManager.class.getSimpleName();

    volatile static Container mContainer;
    private static String EVENT_TYPE = "event";
    private static final String EVENT_TYPE_TRANSACTION = "transaction";
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
    private String mCurrentGAID;
    private Context mContext;

    private static ArrayList<Map<String, Object>> pendingEvents = new ArrayList<>();

    public static GTMManager get() {
        if (gtmTrackingManager == null) {
            return gtmTrackingManager = new GTMManager();
        } else {
            return gtmTrackingManager;
        }
    }

    private GTMManager() {
    }

    public static void init(Context context) {
        gtmTrackingManager = new GTMManager(context);
    }

    /**
     * FIXME : https://rink.hockeyapp.net/manage/apps/33641/app_versions/164/crash_reasons/112840256?type=crashes
     */
    @SuppressLint("NewApi")
    private GTMManager(Context context) {
        mTagManager = TagManager.getInstance(context);
        isContainerAvailable = false;
        mContext = context;
        setCurrentGAID();

        dataLayer = TagManager.getInstance(context).getDataLayer();

        // Load container id
        PendingResult<ContainerHolder> pending = mTagManager
                .loadContainerPreferNonDefault(getId(context), 0);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(containerHolder -> {
            mContainer = containerHolder.getContainer();
            if (!containerHolder.getStatus().isSuccess()) {
                return;
            }
            ContainerLoadedCallback.registerCallbacksForContainer(mContainer);
            containerHolder.setContainerAvailableListener((arg0, arg1) -> {
                isContainerAvailable = true;
                processPendingEvents();
            });
            isContainerAvailable = true;
        }, 2, TimeUnit.SECONDS);
    }

    /*
     * ######### BASE TRACKER #########
     */

    @Override
    public String getId(@NonNull Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Darwin.KEY_SELECTED_COUNTRY_GTM_ID, "");
    }

    @Override
    public void debugMode(@NonNull Context context, boolean enable) {
        if (enable) {
            mTagManager.setVerboseLoggingEnabled(true);
        } else {
            mTagManager.setVerboseLoggingEnabled(false);
        }
    }

    /*
     * ######### TRACKER #########
     */

    /**
     * This method tracks if either the application was opened either by push notification or if the
     * app was started directly
     */
    public void gtmTrackAppOpen(Bundle deviceInfo, String countryIso, String campaignId,
            String source, String medium, boolean isFromPush) {

        if (countryIso == null) {
            //for the first ever app open event when theres no selected country
            return;
        }
        String version = deviceInfo.getString(Constants.INFO_BUNDLE_VERSION);
        if (version == null) {
            version = "";
        }

        String operator = deviceInfo.getString(Constants.INFO_SIM_OPERATOR);
        if (operator == null) {
            operator = "";
        }
        String deviceBrand = deviceInfo.getString(Constants.INFO_BRAND);
        if (deviceBrand == null) {
            deviceBrand = "";
        }

        boolean isPreInstall = deviceInfo.getBoolean(Constants.INFO_PRE_INSTALL, false);
        if (isPreInstall) {
            source = GTMValues.PRE_INSTALL;
        } else {
            source = GTMValues.ORGANIC;
        }

        if (isFromPush) {
            source = GTMValues.PUSH;
        }

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_APP, GTMKeys.SHOPCOUNTRY, countryIso,
                        GTMKeys.SOURCE, source,
                        GTMKeys.APPVERSION, version, GTMKeys.DEVICEBRAND, deviceBrand);

        if (!TextUtils.isEmpty(campaignId)) {
            message.put(GTMKeys.CAMPAIGN, campaignId);
        }

        if (!TextUtils.isEmpty(operator)) {
            message.put(GTMKeys.OPERATOR, operator);
        }

        sendEvent(message);
    }

    public void gtmTrackViewScreen(String screenName, long loadTime) {

        long milliseconds = System.currentTimeMillis();
        if (milliseconds < loadTime || loadTime <= 0) {
            return;
        }
        milliseconds = milliseconds - loadTime;

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_OPEN_SCREEN, GTMKeys.SCREENNAME, screenName,
                        GTMKeys.LOADTIME, milliseconds);

        sendEvent(message);
    }

    public void gtmTrackLogin(Customer customer, TrackingEvent event, String location) {

        String method = GTMValues.EMAILAUTH;

        //working
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN, GTMKeys.LOGINMETHOD, method,
                        GTMKeys.LOGINLOCATION,
                        location, GTMKeys.CUSTOMERID, customer.getIdAsString(), GTMKeys.USERGENDER,
                        customer.getGender());

        sendEvent(message);
    }

    public void gtmTrackLoginFailed(String location, String method) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_LOGIN_FAILED, GTMKeys.LOGINMETHOD, method,
                        GTMKeys.LOGINLOCATION,
                        location);

        sendEvent(message);
    }

    public void gtmTrackAutoLogin(Customer customer) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN, GTMKeys.CUSTOMERID,
                        customer.getIdAsString(), GTMKeys.USERGENDER, customer.getGender());

        sendEvent(message);
    }


    public void gtmTrackAutoLoginFailed() {

        Map<String, Object> message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_AUTOLOGIN_FAILED);

        sendEvent(message);
    }


    public void gtmTrackLogout(String customerId) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_LOGOUT, GTMKeys.LOGOUTLOCATION, GTMValues.LOGOUT,
                        GTMKeys.CUSTOMERID, customerId);
        sendEvent(message);

    }

    public void gtmTrackRegister(String customerId, String location) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER, GTMKeys.REGISTRATIONMETHOD,
                        GTMValues.EMAILAUTH,
                        GTMKeys.REGISTRATIONLOCATION, location, GTMKeys.CUSTOMERID, customerId);

        sendEvent(message);

    }

    public void gtmTrackRegisterFailed(String location) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_REGISTER_FAILED, GTMKeys.REGISTRATIONMETHOD,
                        GTMValues.EMAILAUTH, GTMKeys.REGISTRATIONLOCATION, location);

        sendEvent(message);

    }

    public void gtmTrackSignUp(String subscriberId, String location) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_SIGNUP, GTMKeys.SUBSCRIBERID, subscriberId,
                        GTMKeys.SIGNUPLOCATION, location);

        sendEvent(message);

    }

    public void gtmTrackSearch(String searchTerm, long numberItems) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_SEARCH, GTMKeys.SEARCHTERM, searchTerm,
                        GTMKeys.RESULTSNUMBER, numberItems);
        sendEvent(message);
    }


    public void gtmTrackTransaction(List<PurchaseItem> items, String currencyName,
            double transactionValue, String transactionId, String coupon,
            String paymentMethod, String shippingAmount, String taxAmount) {

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        for (PurchaseItem item : items) {
            Map<String, Object> productsData = DataLayer
                    .mapOf(GTMKeys.NAME, item.name, GTMKeys.SKU, item.sku, GTMKeys.CATEGORY,
                            item.category,
                            GTMKeys.PRICE, item.getPriceForTracking(), GTMKeys.CURRENCY,
                            currencyName, GTMKeys.QUANTITY, item.quantity);
            products.add(productsData);
        }

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE_TRANSACTION, GTMEvents.GTM_TRANSACTION, GTMKeys.TRANSACTIONID,
                        transactionId,
                        GTMKeys.TRANSACTIONTOTAL, transactionValue, GTMKeys.TRANSACTIONCURRENCY,
                        currencyName, GTMKeys.TRANSACTIONPRODUCTS, products);

        if (!TextUtils.isEmpty(coupon)) {
            message.put(GTMKeys.VOUCHERAMOUNT, coupon);
        }

        if (!TextUtils.isEmpty(paymentMethod)) {
            message.put(GTMKeys.PAYMENTMETHOD, paymentMethod);
        }

        if (!TextUtils.isEmpty(shippingAmount)) {
            message.put(GTMKeys.TRANSACTIONSHIPPING, shippingAmount);
        }

        if (!TextUtils.isEmpty(taxAmount)) {
            message.put(GTMKeys.TRANSACTIONTAX, taxAmount);
        }

        sendEvent(message);

    }

    public void gtmTrackShare(String location, String productSKU, String category) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_SHARE_PRODUCT, GTMKeys.PRODUCTSKU, productSKU,
                        GTMKeys.SHARELOCATION, GTMValues.PRODUCTDETAILPAGE);

        if (!TextUtils.isEmpty(category)) {
            message.put(GTMKeys.PRODUCTCATEGORY, category);
        }

        sendEvent(message);

    }


    public void gtmTrackChangeCountry(String country) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_CHANGE_COUNTRY, GTMKeys.SHOPCOUNTRY, country);

        sendEvent(message);
        // Clean the Google Analytics Id when the user changes country
        mCurrentGAID = "";

    }

    public void gtmTrackViewProduct(String productSKU, double productPrice, String productBrand,
            String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_PRODUCT, GTMKeys.PRODUCTSKU, productSKU,
                        GTMKeys.PRODUCTBRAND, productBrand,
                        GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.CURRENCY, currencyName,
                        GTMKeys.DISCOUNT, discount);

        if (productRating != -1d) {
            message.put(GTMKeys.PRODUCTRATING, productRating);
        }

        if (!TextUtils.isEmpty(productCategory)) {
            message.put(GTMKeys.PRODUCTCATEGORY, productCategory);
        }

        if (!TextUtils.isEmpty(productSubCategory)) {
            message.put(GTMKeys.PRODUCTSUBCATEGORY, productSubCategory);
        }

        sendEvent(message);

    }


    public void gtmTrackAddToCart(String productSKU, double productPrice, String productBrand,
            String currencyName, double discount, double productRating,
            String productCategory, String productSubCategory, String location) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_CART, GTMKeys.PRODUCTSKU, productSKU,
                        GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand,
                        GTMKeys.CURRENCY, currencyName,
                        GTMKeys.DISCOUNT, discount, GTMKeys.PRODUCTQUANTITY, 1, GTMKeys.LOCATION,
                        location);

        if (productRating != -1d) {
            message.put(GTMKeys.AVERAGERATINGTOTAL, productRating);
        }

        if (!TextUtils.isEmpty(productCategory)) {
            message.put(GTMKeys.PRODUCTCATEGORY, productCategory);
        }

        if (!TextUtils.isEmpty(productSubCategory)) {
            message.put(GTMKeys.PRODUCTSUBCATEGORY, productSubCategory);
        }

        sendEvent(message);
    }

    public void gtmTrackRemoveFromCart(String productSku, double averageRatingTotal,
            double productPrice, long quantity, String cartValue, String currencyName) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_CART, GTMKeys.PRODUCTSKU, productSku,
                        GTMKeys.PRODUCTPRICE, productPrice,
                        GTMKeys.QUANTITYCART, quantity, GTMKeys.CARTVALUE, cartValue,
                        GTMKeys.CURRENCY, currencyName);

        if (averageRatingTotal != -1d) {
            message.put(GTMKeys.AVERAGERATINGTOTAL, averageRatingTotal);
        }

        sendEvent(message);
    }


    public void gtmTrackRateProduct(ProductComplete product, String currencyName) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_RATE_PRODUCT, GTMKeys.PRODUCTSKU, product.getSku(),
                        GTMKeys.PRODUCTPRICE, product.getPriceForTracking(),
                        GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND,
                        product.getBrandName(), GTMKeys.PRODUCTRATING, product.getAvgRating());

        sendEvent(message);

    }

    public void gtmTrackViewRating(ProductComplete product, String currencyName) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_RATING, GTMKeys.PRODUCTSKU, product.getSku(),
                        GTMKeys.PRODUCTPRICE, product.getPriceForTracking(),
                        GTMKeys.CURRENCY, currencyName, GTMKeys.PRODUCTBRAND,
                        product.getBrandName(), GTMKeys.AVERAGERATINGTOTAL, product.getAvgRating());

        sendEvent(message);

    }

    public void gtmTrackFilterCatalog(String filterType) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_FILTER_CATALOG, GTMKeys.FILTERTYPE, filterType);
        sendEvent(message);
    }

    public void gtmTrackSortCatalog(String sortType) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_SORT_CATALOG, GTMKeys.SORTTYPE, sortType);
        sendEvent(message);
    }

    public void gtmTrackAddToWishList(String productSku, String productBrand, double productPrice,
            double productRating, double productDiscount, String currency, String location,
            String category, String subCategory) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_ADD_TO_WL, GTMKeys.PRODUCTSKU, productSku,
                        GTMKeys.PRODUCTPRICE, productPrice, GTMKeys.PRODUCTBRAND, productBrand,
                        GTMKeys.CURRENCY, currency,
                        GTMKeys.DISCOUNT, productDiscount, GTMKeys.LOCATION, location,
                        GTMKeys.AVERAGERATINGTOTAL, productRating);

        if (!TextUtils.isEmpty(category)) {
            message.put(GTMKeys.PRODUCTCATEGORY, category);
        }

        if (!TextUtils.isEmpty(subCategory)) {
            message.put(GTMKeys.PRODUCTSUBCATEGORY, subCategory);
        }

        sendEvent(message);
    }


    public void gtmTrackRemoveFromWishList(String productSKU, double productPrice,
            double averageRatingTotal, String currencyName) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_REMOVE_FROM_WL, GTMKeys.PRODUCTSKU, productSKU,
                        GTMKeys.PRODUCTPRICE, productPrice,
                        GTMKeys.CURRENCY, currencyName, GTMKeys.AVERAGERATINGTOTAL,
                        averageRatingTotal);

        sendEvent(message);
    }

    public void gtmTrackViewCart(int quantityCart, double cartValue, String currencyName) {
        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.CARTVALUE, cartValue,
                        GTMKeys.QUANTITYCART, quantityCart, GTMKeys.CURRENCY, currencyName);

        sendEvent(message);
    }

    public void gtmTrackEnterAddress(boolean isCorrect) {
        Map<String, Object> message;
        if (isCorrect) {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.ADDRESSCORRECT,
                    GTMValues.YES);
        } else {
            message = DataLayer.mapOf(EVENT_TYPE, GTMEvents.GTM_VIEW_CART, GTMKeys.ADDRESSCORRECT,
                    GTMValues.NO);
        }
        sendEvent(message);
    }

    public void gtmTrackChoosePayment(String paymentMethod) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_CHOOSE_PAYMENT, GTMKeys.PAYMENTMETHOD,
                        paymentMethod);

        sendEvent(message);
    }

    public void gtmTrackFailedPayment(String paymentMethod, double transactionTotal,
            String currencyName) {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_FAILED_PAYMENT, GTMKeys.PAYMENTMETHOD,
                        paymentMethod, GTMKeys.TRANSACTIONTOTAL, transactionTotal, GTMKeys.CURRENCY,
                        currencyName);

        sendEvent(message);
    }

    public void gtmTrackAppClose() {

        Map<String, Object> message = DataLayer
                .mapOf(EVENT_TYPE, GTMEvents.GTM_CLOSE_APP, GTMKeys.SCREENNAME, GTMValues.HOME);

        sendEvent(message);
    }

    /**
     * Set Google Analytics Id
     */
    public String setCurrentGAID() {
        if (mContext != null && TextUtils.isEmpty(mCurrentGAID)) {
            //GA Id
            SharedPreferences mSharedPreferences = mContext
                    .getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            mCurrentGAID = mSharedPreferences.getString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, null);
        }
        return mCurrentGAID;
    }

    /**
     * Add Google Analytics Id to Google Tag Manager
     */
    public Map<String, Object> addCurrentGAID(Map<String, Object> event) {
        if (!TextUtils.isEmpty(mCurrentGAID)) {
            event.put(GTMKeys.GAPROPERTYID, mCurrentGAID);
        }
        return event;
    }


    private void sendEvent(Map<String, Object> event) {
        // Add current GA ID to all GTM events being fired
        event = addCurrentGAID(event);
        if (isContainerAvailable) {
            if (pendingEvents != null && pendingEvents.size() > 0) {
                processPendingEvents();
                pendingEvents.clear();
            }
            dataLayer.push(event);
        } else {
            pendingEvents.add(event);
        }

    }

    private void processPendingEvents() {
        try {
            if (pendingEvents != null) {
                for (Map<String, Object> event : pendingEvents) {
                    if (dataLayer == null) {
                        dataLayer = mTagManager.getDataLayer();
                    }
                    dataLayer.push(event);
                }
                pendingEvents.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCampaignParams(Context context, String key, String value) {
        SharedPreferences settings = context
                .getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    ////////////////////////////////////////////////////////////////////////////////


    private static class ContainerLoadedCallback implements
            ContainerHolder.ContainerAvailableListener {

        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            if (container != null) {
                registerCallbacksForContainer(container);
            }
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
                return (Long) parameters.get("key1") % Integer
                        .valueOf((String) parameters.get("key2"));
            } else {
                throw new IllegalArgumentException(
                        "Custom macro name: " + name + " is not supported.");
            }
        }
    }

    private static class CustomTagCallback implements FunctionCallTagCallback {

        @Override
        public void execute(String tagName, Map<String, Object> parameters) {
            // The code for firing this custom tag.
        }
    }

    /**
     * method responsible for sending the adjust install information to GTM
     */
    public static void trackAdjustInstallSource(Context context) {
        if (context == null) {
            return;
        }

        HashMap<String, Object> parameters = new HashMap<>();

        SharedPreferences settings = context
                .getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(GTMKeys.INSTALLNETWORK)
                && settings.getString(GTMKeys.INSTALLNETWORK, null) != null) {
            parameters.put(GTMKeys.INSTALLNETWORK, settings.getString(GTMKeys.INSTALLNETWORK, ""));
        }

        if (settings.contains(GTMKeys.INSTALLADGROUP)
                && settings.getString(GTMKeys.INSTALLADGROUP, null) != null) {
            parameters.put(GTMKeys.INSTALLADGROUP, settings.getString(GTMKeys.INSTALLADGROUP, ""));
        }

        if (settings.contains(GTMKeys.INSTALLCAMPAIGN)
                && settings.getString(GTMKeys.INSTALLCAMPAIGN, null) != null) {
            parameters
                    .put(GTMKeys.INSTALLCAMPAIGN, settings.getString(GTMKeys.INSTALLCAMPAIGN, ""));
        }

        if (settings.contains(GTMKeys.INSTALLCREATIVE)
                && settings.getString(GTMKeys.INSTALLCREATIVE, null) != null) {
            parameters
                    .put(GTMKeys.INSTALLCREATIVE, settings.getString(GTMKeys.INSTALLCREATIVE, ""));
        }

        if (dataLayer != null) {
            dataLayer.pushEvent(GTMEvents.GTM_APP_INSTALL, parameters);
        }
    }


    public static String getUtmParams(Context context, String key) {
        SharedPreferences settings = context
                .getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void saveUtmParameters(Context context, String key, String value) {
        SharedPreferences settings = context
                .getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
