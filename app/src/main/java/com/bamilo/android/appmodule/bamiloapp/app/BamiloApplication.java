package com.bamilo.android.appmodule.bamiloapp.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.bamilo.android.appmodule.bamiloapp.di.components.DaggerMainComponent;
import com.bamilo.android.appmodule.bamiloapp.di.components.MainComponent;
import com.bamilo.android.appmodule.bamiloapp.di.modules.AndroidModule;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.preferences.PersistentSessionStore;
import com.bamilo.android.appmodule.bamiloapp.preferences.ShopPreferences;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckVersion;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.database.DarwinDatabaseHelper;
import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.objects.configs.VersionInfo;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.AigHttpClient;
import com.bamilo.android.framework.service.rest.cookies.ISessionCookie;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.tracking.AdjustTracker;
import com.bamilo.android.framework.service.tracking.AnalyticsGoogle;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.ImageResolutionHelper;
import com.bamilo.android.framework.service.utils.SingletonMap;
import com.bamilo.android.framework.service.utils.cache.WishListCache;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.webengage.sdk.android.WebEngage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import io.fabric.sdk.android.Fabric;

public class BamiloApplication extends MultiDexApplication {
    private static MainComponent component;

    private static final String TAG = BamiloApplication.class.getSimpleName();
    // Components
    private static final SingletonMap<ApplicationComponent> COMPONENTS = new SingletonMap<>(
            new DarwinComponent());
    // Application
    public static BamiloApplication INSTANCE;
    // Shop
    public static String SHOP_ID = null;
    public static String SHOP_NAME = "";
    // Save the Mobile API version
    private VersionInfo mMobApiVersionInfo;
    // Account variables
    public static Customer CUSTOMER;
    private PersistentSessionStore mCustomerUtils;
    // Cart
    private PurchaseEntity cart;
    // Forms
    public Form reviewForm;
    public Form ratingForm;
    // Countries
    public ArrayList<CountryObject> countriesAvailable = null;
    // Search
    public String mSavedSearchTerm;

    public static MainComponent getComponent() {
        return component;
    }

    public ArrayList<String> homepageTrackingItemsSkus = new ArrayList<>();

    /**
     * Create application
     */
    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

        INSTANCE = this;

        initFirebaseCrashlytics();
        initAnalyticsPlatforms();

        sendFcmTokenToWebEngage();

        // Setup Emarsys Mobile Engage
//        MobileEngageConfig config = new MobileEngageConfig.Builder()
//                .application(this)
//                .credentials(getString(R.string.Emarsys_ApplicationCode), getString(R.string.Emarsys_ApplicationPassword))
//                .statusListener(getStatusListener())
//                .statusListener(getStatusListener())
//                .disableDefaultChannel()
//                .build();
//        MobileEngage.setup(config);



        // init dagger component
        component = createComponent();

        // Init image loader
        //RocketImageLoader.init(getApplicationContext());
        ImageManager.initialize(getApplicationContext());
        // Init darwin database, set the context
        DarwinDatabaseHelper.init(getApplicationContext());
        // Init image resolution
        ImageResolutionHelper.init(this);
        // Get the current shop id and name
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        // Init cached data
        countriesAvailable = new ArrayList<>();
        setCart(null);

        //ShopSelector.setLocaleOnOrientationChanged();

//        Session.initialize(new AndroidStorage(this));

//        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before run.
//        session.setMerchantId(getApplicationContext().getResources().getString(R.string.Emarsys_MerchantId));

        //DataSource.initWithContext(getApplicationContext());

        /**
         * Fix a crash report, when app try recover from background
         * https://rink.hockeyapp.net/manage/apps/33641/app_versions/109/crash_reasons/17098450
         * @author sergiopereira
         */
        String currencyCode = ShopPreferences.getShopCountryCurrencyIso(getApplicationContext());
        if (!TextUtils.isEmpty(SHOP_ID) && !TextUtils.isEmpty(currencyCode)) {
            Darwin.initialize(getApplicationContext(), SHOP_ID);
            getCustomerUtils();
        }
    }

    private MainComponent createComponent() {
        return DaggerMainComponent
                .builder()
                .androidModule(new AndroidModule(this))
                .build();
    }

    public synchronized void init(Handler initializationHandler) {
        // isInitializing = true;
        AnalyticsGoogle.clearCheckoutStarted();

        for (ApplicationComponent component : COMPONENTS.values()) {
            int result = component.init(getApplicationContext());
            if (result != ErrorCode.NO_ERROR) {
                //Print.i(TAG, "code1configs : " + result);
                handleEvent(result, null, initializationHandler);
                return;
            }
        }

        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        //Print.i(TAG, "code1configs : SHOP_ID : " + SHOP_ID + " SHOP_NAME : " + SHOP_NAME);
        // Initialize check version, disabled for Samsung (check_version_enabled)
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        CheckVersion.init(getApplicationContext());
        //
        handleEvent(ErrorCode.NO_ERROR, EventType.INITIALIZE, initializationHandler);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ShopSelector.setLocaleOnOrientationChanged(getApplicationContext());
    }

    public synchronized void handleEvent(@ErrorCode.Code int errorType, EventType eventType, Handler initializationHandler) {
        Message msg = new Message();
        msg.obj = new BaseResponse<>(eventType, errorType);
        // Send result message
        initializationHandler.sendMessage(msg);
    }

    /**
     * Method used to indicate if is a debuggable app<br>
     * This is override by DebugApplication
     */
    public boolean isDebuggable() {
        return false;
    }

    /**
     * Triggers the request for a new api call
     */
    public void sendRequest(final SuperBaseHelper helper, final Bundle args, final IResponseCallback responseCallback) {
        helper.sendRequest(args, responseCallback);
    }

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
                cookieStore = AigHttpClient.getInstance(getApplicationContext()).getCurrentCookie();
            }
            mCustomerUtils = new PersistentSessionStore(getApplicationContext(), SHOP_ID, cookieStore);
        }
        return mCustomerUtils;
    }

    /**
     * @return the cart
     */
    @Nullable
    public PurchaseEntity getCart() {
        return this.cart;
    }

    /**
     * @param cart the cart to set
     */
    public void setCart(@Nullable PurchaseEntity cart) {
        this.cart = cart;
    }

    /**
     * Validate if customer is logged in (not null).
     */
    public static boolean isCustomerLoggedIn() {
        return CUSTOMER != null;
    }

    /**
     * Clean current memory.
     */
    public void cleanAllPreviousCountryValues() {
        cleanAllPreviousLanguageValues();
        setCart(null);
        CUSTOMER = null;
        getCustomerUtils().save();
        mCustomerUtils = null;
        cart = null;
        countriesAvailable.clear();
        reviewForm = null;
        ratingForm = null;
        WishListCache.clean();
        AdjustTracker.resetTransactionCount(getApplicationContext());
        SearchRecentQueriesTableHelper.deleteAllRecentQueries();
    }

    public void cleanAllPreviousLanguageValues() {
        try {
            AigHttpClient.clearCache(this);
        } catch (IOException ignored) {
        }
    }

    /**
     * Save last searched term
     */
    public void setSearchedTerm(String searchTerm) {
        mSavedSearchTerm = searchTerm;
    }

    /**
     * returns the saved searched term
     *
     * @return searched term
     */
    public String getSearchedTerm() {
        return mSavedSearchTerm;
    }

    private void initFirebaseCrashlytics() {
        Fabric.with(this, new Crashlytics());
    }

    private void initAnalyticsPlatforms() {
        EventTracker.INSTANCE.initialize(this);
    }

    /**
     * This will ensure that changes in user’s Firebase token are communicated to WebEngage.
     */
    private void sendFcmTokenToWebEngage () {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                WebEngage.get().setRegistrationID(deviceToken);
            }
        });
    }

    public void addSkuToHomepageTrackingItemsSkus(String sku) {
        homepageTrackingItemsSkus.add(sku);
    }
    public ArrayList<String> getHomepageTrackingItemsSkus() {
        return homepageTrackingItemsSkus;
    }
    public void clearHomepageTrackingItemsSkus(){
        homepageTrackingItemsSkus.clear();
    }
}