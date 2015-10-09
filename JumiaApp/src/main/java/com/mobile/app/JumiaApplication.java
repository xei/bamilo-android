package com.mobile.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ad4screen.sdk.A4SApplication;
import com.facebook.FacebookSdk;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.database.DarwinDatabaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.forms.PaymentInfo;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.objects.configs.VersionInfo;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.cookies.ISessionCookie;
import com.mobile.newFramework.rest.errors.JumiaError;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.ApptimizeTracking;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.ImageResolutionHelper;
import com.mobile.newFramework.utils.SingletonMap;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.PersistentSessionStore;
import com.mobile.preferences.ShopPreferences;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.imageloader.RocketImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class JumiaApplication extends A4SApplication {

    private static final String TAG = JumiaApplication.class.getSimpleName();
    // Components
    private static final SingletonMap<ApplicationComponent> COMPONENTS = new SingletonMap<ApplicationComponent>(new DarwinComponent());
    // Application
    public static JumiaApplication INSTANCE;
    // Shop
    public static String SHOP_ID = null;
    public static String SHOP_NAME = "";
    // Save the Mobile API version
    private VersionInfo mMobApiVersionInfo;
    // Account variables
    public static Customer CUSTOMER;
    private PersistentSessionStore mCustomerUtils;
    private boolean loggedIn = false;

    /**
     * General Persistent Variables
     */
    /**
     * Cart
     */
    private Map<String, Map<String, String>> itemSimpleDataRegistry = new HashMap<>();
    private PurchaseEntity cart;

    /**
     * Forms
     */
    private HashMap<String, FormData> formDataRegistry = new HashMap<>();
    private PaymentMethodForm paymentMethodForm;
    public Form registerForm; // TODO use an alternative to persist form on rotation
    public Bundle registerSavedInstanceState; // TODO use an alternative to persist filled fields on rotation
    public Form reviewForm; // TODO use an alternative to persist form on rotation
    public Form ratingForm; // TODO use an alternative to persist form on rotation
    public Form mSellerReviewForm; // TODO use an alternative to persist form on rotation
    private static ContentValues ratingReviewValues;
    private static ContentValues sellerReviewValues;
    public static boolean isSellerReview = false;
    private static HashMap<String, String> sFormReviewValues = new HashMap<>();

    /**
     * Payment methods Info
     */
    private static HashMap<String, PaymentInfo> paymentsInfoList;
    public int lastPaymentSelected = -1;

    public ArrayList<CountryObject> countriesAvailable = null;

    // for tracking
    public boolean trackSearch = true;
    public boolean trackSearchCategory = true;
    private HashMap<String, String> bannerSkus = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see com.ad4screen.sdk.A4SApplication#onApplicationCreate()
     */
    @Override
    public void onApplicationCreate() {
        Print.initializeAndroidMode(getApplicationContext());
        Print.i(TAG, "ON APPLICATION CREATE");
        INSTANCE = this;
        // Init image loader
        RocketImageLoader.init(this);
        // Init apptimize
        ApptimizeTracking.startup(getApplicationContext());
        // Init darwin database, set the context
        DarwinDatabaseHelper.init(getApplicationContext());
        // Init image resolution
        ImageResolutionHelper.init(this);
        // Get the current shop id and name
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        // Init cached data
        countriesAvailable = new ArrayList<>();
        setItemSimpleDataRegistry(new HashMap<String, Map<String, String>>());
        setCart(null);
        setFormDataRegistry(new HashMap<String, FormData>());

        /**
         * Fix a crash report, when app try recover from background
         * https://rink.hockeyapp.net/manage/apps/33641/app_versions/109/crash_reasons/17098450
         * @author sergiopereira
         */
        Print.i(TAG, "INIT CURRENCY");
        String currencyCode = ShopPreferences.getShopCountryCurrencyIso(getApplicationContext());
        if(!TextUtils.isEmpty(SHOP_ID) && !TextUtils.isEmpty(currencyCode)) {
            Darwin.initialize(getApplicationContext(), SHOP_ID);
            getCustomerUtils();
        }
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }

    public synchronized void init(Handler initializationHandler) {
        Print.d(TAG, "ON INIT");
        // isInitializing = true;
        AnalyticsGoogle.clearCheckoutStarted();

        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(getApplicationContext());
            if (result != ErrorCode.NO_ERROR) {
                Print.i(TAG, "code1configs : " + result);
                handleEvent(result, null, initializationHandler);
                return;
            }
        }

        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        Print.i(TAG, "code1configs : SHOP_ID : " + SHOP_ID + " SHOP_NAME : " + SHOP_NAME);
        // Disabled for Samsung and Blackberry (check_version_enabled)
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        // Disabled for Samsung and Blackberry (check_version_enabled)
        CheckVersion.init(getApplicationContext());
        //
        handleEvent(ErrorCode.NO_ERROR, EventType.INITIALIZE, initializationHandler);
    }

    public synchronized void handleEvent(ErrorCode errorType, EventType eventType, Handler initializationHandler) {
        Print.d(TAG, "ON HANDLE");
        // isInitializing = false;
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, errorType);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);

        Print.d(TAG, "Handle initialization result: " + errorType);
        Message msg = new Message();
        msg.obj = new BaseResponse<>(eventType, errorType);
        // Send result message
        initializationHandler.sendMessage(msg);
    }

    /*
    ###########################
     */

    /**
     * Triggers the request for a new api call
     */
    public void sendRequest(final SuperBaseHelper helper, final Bundle args, final IResponseCallback responseCallback) {
        helper.sendRequest(args, responseCallback);
    }
    /*
    #################################
     */

    /**
     * @return the mMobApiVersionInfo
     */
    public VersionInfo getMobApiVersionInfo() {
        return mMobApiVersionInfo;
    }

    /**
     * @param mVersionInfo the mMobApiVersionInfo to set
     */
    public void setMobApiVersionInfo(VersionInfo mVersionInfo) {
        this.mMobApiVersionInfo = mVersionInfo;
    }

    /**
     * @return the mCustomerUtils
     */
    public PersistentSessionStore getCustomerUtils() {
        if (mCustomerUtils == null) {
            ISessionCookie cookieStore = null;
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            if (sharedPrefs.contains(Darwin.KEY_SELECTED_COUNTRY_ID)) {
                cookieStore = AigHttpClient.getInstance().getCurrentCookie();
            }
            mCustomerUtils = new PersistentSessionStore(getApplicationContext(), SHOP_ID, cookieStore);
        }
        return mCustomerUtils;
    }

    /**
     * @return the cart
     */
    public PurchaseEntity getCart() {
        return this.cart;
    }

    /**
     * @param cart the cart to set
     */
    public void setCart(PurchaseEntity cart) {
        this.cart = cart;
    }

    /**
     * @param itemSimpleDataRegistry the itemSimpleDataRegistry to set
     */
    public void setItemSimpleDataRegistry(Map<String, Map<String, String>> itemSimpleDataRegistry) {
        this.itemSimpleDataRegistry = itemSimpleDataRegistry;
    }

    /**
     * @return the formDataRegistry
     */
    public HashMap<String, FormData> getFormDataRegistry() {
        return formDataRegistry;
    }

    /**
     * @param formDataRegistry the formDataRegistry to set
     */
    public void setFormDataRegistry(HashMap<String, FormData> formDataRegistry) {
        this.formDataRegistry = formDataRegistry;
    }

    /**
     * @return the loggedIn
     * @deprecated This flag is not persisted on rotation.
     */
    @Deprecated
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @param loggedIn
     *            the loggedIn to set
     */
    @Deprecated
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * Validate if customer is logged in (not null).
     */
    public static boolean isCustomerLoggedIn() {
        return CUSTOMER != null;
    }

    public void setPaymentMethodForm(PaymentMethodForm paymentMethodForm) {
        this.paymentMethodForm = paymentMethodForm;
    }

    public PaymentMethodForm getPaymentMethodForm() {
        return this.paymentMethodForm;
    }

    /**
     * clean and return last saved rating
     *
     * @return last saved review
     */
    public static ContentValues getRatingReviewValues() {
        return JumiaApplication.ratingReviewValues;
    }

    /**
     * clean current rating
     */
    public static void cleanRatingReviewValues() {
        JumiaApplication.ratingReviewValues = null;
    }

    public static void setRatingReviewValues(ContentValues ratingReviewValues) {
        JumiaApplication.ratingReviewValues = ratingReviewValues;
    }

    /**
     * @return the paymentsInfoList
     */
    public static HashMap<String, PaymentInfo> getPaymentsInfoList() {
        return paymentsInfoList;
    }

    /**
     * get the values from the write review form
     * @return sFormReviewValues
     */
    public HashMap<String,String> getFormReviewValues(){
        return JumiaApplication.sFormReviewValues;
    }
    /**
     * HashMap used to store the values from the write review form
     */
    public void setFormReviewValues(HashMap<String, String> sFormReviewValues){
        JumiaApplication.sFormReviewValues = sFormReviewValues;
    }

    /**
     * @param paymentsInfoList
     *            the paymentsInfoList to set
     */
    public static void setPaymentsInfoList(HashMap<String, PaymentInfo> paymentsInfoList) {
        JumiaApplication.paymentsInfoList = paymentsInfoList;
    }

    /**
     * Clean current memory.
     */
    public void cleanAllPreviousCountryValues() {
        cleanAllPreviousLanguageValues();
        setCart(null);
        setFormDataRegistry(new HashMap<String, FormData>());
        CUSTOMER = null;
        getCustomerUtils().save();
        mCustomerUtils = null;
        cart = null;
        paymentsInfoList = null;
        itemSimpleDataRegistry.clear();
        formDataRegistry.clear();
        countriesAvailable.clear();
        reviewForm = null;
        ratingForm = null;
        isSellerReview = false;
        ratingReviewValues = null;
        sellerReviewValues = null;
        sFormReviewValues = null;
        WishListCache.clean();
        AdjustTracker.resetTransactionCount(getApplicationContext());
        clearBannerFlowSkus();
    }

    public void cleanAllPreviousLanguageValues(){
        try {
            AigHttpClient.clearCache(this);
        } catch (IOException e) {
            Print.e(TAG, "Error clearing requests cache", e);
        }
        registerForm = null;
        paymentMethodForm = null;
        registerSavedInstanceState = null;
        mSellerReviewForm = null;
    }

    /**
     * add a sku to a list of sku products that were added from a banner flow
     */
    public void setBannerFlowSkus(String sku,TeaserGroupType groupType) {
        if(bannerSkus == null){
            bannerSkus = new HashMap<>();
        }
        String category = getString(TrackerDelegator.getCategoryFromTeaserGroupType(groupType)) +"_"+groupType.getTrackingPosition();

        if(!TextUtils.isEmpty(sku)){
            if(bannerSkus.size() == 0){
                bannerSkus.put(sku, category);
            } else {
                if(!bannerSkus.containsKey(sku)){
                    bannerSkus.put(sku, category);
                }
            }
        }
    }

    /**
     * returns a list of skus of products that were added to cart from a banner flow
     *
     * @return list of skus
     */
    public HashMap<String,String> getBannerFlowSkus() {
        if(bannerSkus == null){
            bannerSkus = new HashMap<>();
        }
        return bannerSkus;
    }

    /**
     * clear all skus from banner flow
     */
    public void clearBannerFlowSkus() {
        bannerSkus = null;
    }

}

