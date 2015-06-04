package com.mobile.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ad4screen.sdk.A4SApplication;
import com.mobile.framework.Darwin;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.database.DarwinDatabaseHelper;
import com.mobile.framework.objects.PaymentInfo;
import com.mobile.framework.rest.ICurrentCookie;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.ApptimizeTracking;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.ImageResolutionHelper;
import com.mobile.framework.utils.SingletonMap;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.objects.configs.VersionInfo;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.objects.user.Customer;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.preferences.PersistentSessionStore;
import com.mobile.preferences.ShopPreferences;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.imageloader.RocketImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.akquinet.android.androlog.Log;

//import com.mobile.framework.rest.RestClientSingleton;
//import com.mobile.framework.service.IRemoteServiceCallback;

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
    private ShoppingCart cart;

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
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;
    boolean resendInitializationSignal = false;
    private Handler resendHandler;
    //private Handler resendMenuHandler;
    private Message resendMsg;

    //private IRemoteServiceCallback callBackWaitingService;

    /**
     * Payment methods Info
     */
    private static HashMap<String, PaymentInfo> paymentsInfoList;
    public int lastPaymentSelected = -1;

    public ArrayList<CountryObject> countriesAvailable = null;

    // for tracking
    public boolean trackSearch = true;
    public boolean trackSearchCategory = true;
    //    private ArrayList<String> bannerSkus = new ArrayList<>();
    private HashMap<String, TeaserGroupType> bannerSkus = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see com.ad4screen.sdk.A4SApplication#onApplicationCreate()
     */
    @Override
    public void onApplicationCreate() {
        Log.i(TAG, "ON APPLICATION CREATE");
        Log.init(getApplicationContext());
        INSTANCE = this;

        // TODO : REMOVE OLD FRAMEWORK
        // Service
        doBindService();

        // Init image loader
        RocketImageLoader.init(this);
        // Init apptimize
        ApptimizeTracking.startup(getApplicationContext());
        // Init darwin database, set the context
        DarwinDatabaseHelper.init(getApplicationContext());
        countriesAvailable = new ArrayList<>();
        responseCallbacks = new HashMap<>();
        // Get the current shop id and name
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        //
        setItemSimpleDataRegistry(new HashMap<String, Map<String, String>>());
        setCart(null);
        ImageResolutionHelper.init(this);
        setFormDataRegistry(new HashMap<String, FormData>());

        /**
         * Fix a crash report, when app try recover from background
         * https://rink.hockeyapp.net/manage/apps/33641/app_versions/109/crash_reasons/17098450
         * @author sergiopereira
         */
        Log.i(TAG, "INIT CURRENCY");
        String currencyCode = ShopPreferences.getShopCountryCurrencyIso(getApplicationContext());
        if (!TextUtils.isEmpty(currencyCode)) {
            CurrencyFormatter.initialize(getApplicationContext(), currencyCode);
        }
    }

    public synchronized void init(Handler initializationHandler) {
        Log.d(TAG, "ON INIT");
        // isInitializing = true;
        AnalyticsGoogle.clearCheckoutStarted();

        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(getApplicationContext());
            if (result != ErrorCode.NO_ERROR) {
                Log.i(TAG, "code1configs : " + result);
                handleEvent(result, null, initializationHandler);
                return;
            }
        }

        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = ShopPreferences.getShopName(getApplicationContext());
        Log.i(TAG, "code1configs : SHOP_ID : " + SHOP_ID + " SHOP_NAME : " + SHOP_NAME);
        // Disabled for Samsung and Blackberry (check_version_enabled)
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        // Disabled for Samsung and Blackberry (check_version_enabled) 
        CheckVersion.init(getApplicationContext());
        //
        handleEvent(ErrorCode.NO_ERROR, EventType.INITIALIZE, initializationHandler);
    }

    public synchronized void handleEvent(ErrorCode errorType, EventType eventType, Handler initializationHandler) {
        Log.d(TAG, "ON HANDLE");
        // isInitializing = false;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, errorType);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
        Log.d(TAG, "Handle initialization result: " + errorType);
        Message msg = new Message();
        msg.obj = bundle;
        if (eventType == EventType.INITIALIZE || errorType == ErrorCode.NO_COUNTRIES_CONFIGS || errorType == ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE) {

            // TODO : REMOVE OLD FRAMEWORK
            //&& ServiceSingleton.getInstance().getService() == null) {

            Log.d(TAG, "ON HANDLE WITH ERROR");
            resendInitializationSignal = true;
            resendHandler = initializationHandler;
            resendMsg = msg;

            // TODO : REMOVE OLD FRAMEWORK
            doBindService();

        } else {
            Log.d(TAG, "ON INIT HANDLE");
            initializationHandler.sendMessage(msg);
        }
    }

    // TODO: REMOVE OLD FRAMEWORK
//    public void registerFragmentCallback(IRemoteServiceCallback mCallback) {
//        Log.d(TAG, "ON REGISTER CALL BACK FRAGMENT");
//        if (mCallback == null) {
//            Log.i(TAG, "mCallback is null");
//        }
//        if (ServiceSingleton.getInstance().getService() == null) {
//            Log.i(TAG, "ServiceSingleton.getInstance().getService() is null");
//
//            // Try connect with service
//            doBindService();
//
//            // Save the call back
//            callBackWaitingService = mCallback;
//            return;
//        }
//        try {
//            ServiceSingleton.getInstance().getService().registerCallback(mCallback);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

//    public void unRegisterFragmentCallback(IRemoteServiceCallback mCallback) {
//        if (mCallback == null) {
//            Log.i(TAG, "mCallback is null");
//        }
//        if (ServiceSingleton.getInstance().getService() != null) {
//            try {
//                ServiceSingleton.getInstance().getService().unregisterCallback(mCallback);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    /**
//     * Method used to register the call back that is waiting for service.
//     *
//     * @author sergiopereira
//     */
//    private void registerCallBackIsWaiting() {
//        try {
//            // Validate the current call back waiting by service
//            if (callBackWaitingService != null) {
//                ServiceSingleton.getInstance().getService().registerCallback(callBackWaitingService);
//                callBackWaitingService = null;
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

    /*
    ########################### TODO: NEW FAMEWORK
     */

    public void sendRequest(final SuperBaseHelper helper, final Bundle args, final IResponseCallback responseCallback) {
        helper.sendRequest(args, responseCallback);
    }
    /*
    #################################
     */

//    /**
//     * Triggers the request for a new api call
//     * @return the md5 of the reponse
//     */
//    public String sendRequest(final BaseHelper helper, final Bundle args, final IResponseCallback responseCallback) {
//
//        if (helper == null) {
//            return "";
//        }
//        final Bundle requestBundle = helper.newRequestBundle(args);
//
//        final String md5 = requestBundle.getString(Constants.BUNDLE_MD5_KEY);
//
//        Log.d("TRACK", "sendRequest");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                //Log.i(TAG, "############ RQ CURRENT THREAD ID: " + Thread.currentThread().getId());
//                //Log.i(TAG, "############ RQ MAIN THREAD ID: " + Looper.getMainLooper().getThread().getId());
//
//                JumiaApplication.INSTANCE.responseCallbacks.put(md5, new IResponseCallback() {
//
//                    @Override
//                    public void onRequestComplete(Bundle bundle) {
//
//                        /**
//                         * ###################################################
//                         * # FIXME: WARNING - THIS IS RUNNING IN MAIN THREAD #
//                         * # - Alternative -> ParseSuccessAsyncTask          #
//                         * # @author sergiopereira                           #
//                         * ###################################################
//                         */
//                        //Log.i(TAG, "############ RP CURRENT THREAD ID: " + Thread.currentThread().getId());
//                        //Log.i(TAG, "############ RP MAIN THREAD ID: " + Looper.getMainLooper().getThread().getId());
//                        //new ParseSuccessAsyncTask(helper, bundle, responseCallback).execute();
//
//                        Log.d("TRACK", "onRequestComplete BaseActivity");
//                        // We have to parse this bundle to the final one
//                        Bundle responseBundle = helper.checkResponseForStatus(bundle);
//                        if (responseCallback != null) {
//                            // CASE: Error parsing
//                            if (responseBundle.getBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY)) {
//                                // Remove request from cache
//                                String url = requestBundle.getString(Constants.BUNDLE_URL_KEY);
//                                EventType type = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//                                helper.removeRequestFromHttpCache(url, type);
//                                // Callback
//                                responseCallback.onRequestError(responseBundle);
//                            }
//                            // CASE: Success
//                            else {
//                                responseCallback.onRequestComplete(responseBundle);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRequestError(Bundle bundle) {
//                        Log.d("TRACK", "onRequestError  BaseActivity");
//                        // We have to parse this bundle to the final one
//                        Bundle responseBundle = helper.parseErrorBundle(bundle);
//                        if (responseCallback != null) {
//                            responseCallback.onRequestError(responseBundle);
//                        }
//                    }
//                });
//
//                if (!sendRequest(requestBundle)) {
//                    Log.e(TAG, "SERVICE NOT AVAILABLE FOR EVENT TYPE " + requestBundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
//                }
//            }
//        }).start();
//
//        return md5;
//    }
//
//    public boolean sendRequest(Bundle bundle) {
//        if(ServiceSingleton.getInstance().getService() != null){
//            try {
//                ServiceSingleton.getInstance().getService().sendRequest(bundle);
//                return true;
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }

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
            ICurrentCookie cookieStore = null;
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            if (sharedPrefs.contains(Darwin.KEY_SELECTED_COUNTRY_ID)) {
                // TODO: GET COOKIES FROM NEW FRAMEWORK : TEST IT
                cookieStore = AigHttpClient.getInstance(getApplicationContext()).getCurrentCookie();
            }
            mCustomerUtils = new PersistentSessionStore(getApplicationContext(), SHOP_ID, cookieStore);
        }
        return mCustomerUtils;
    }

    /**
     * @return the cart
     */
    public ShoppingCart getCart() {
        return this.cart;
    }

    /**
     * @param cart the cart to set
     */
    public void setCart(ShoppingCart cart) {
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

    // TODO : REMOVE OLD FRAMEWORK
    public void doBindService() {

        if (resendInitializationSignal) {
            resendHandler.sendMessage(resendMsg);
            resendInitializationSignal = false;
        }

//        if (!mIsBound) {
//            /**
//             * Establish a connection with the service. We use an explicit class
//             * name because we want a specific service implementation that we
//             * know will be running in our own process (and thus won't be
//             * supporting component replacement by other applications).
//             */
//            bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    /**
     * @return the loggedIn
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @param loggedIn
     *            the loggedIn to set
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }


//    public void setResendHandler(Handler mHandler) {
//        resendInitializationSignal = true;
//        resendMsg = new Message();
//        resendHandler = mHandler;
//    }

//    /**
//     * Service Stuff
//     */
//
//    public ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.i(TAG, "onServiceDisconnected");
//            mIsBound = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // This is called when the connection with the service has been
//            // established, giving us the service object we can use to
//            // interact with the service. We are communicating with our
//            // service through an IDL interface, so get a client-side
//            // representation of that from the raw service object.
//            Log.i(TAG, "onServiceConnected");
//            mIsBound = true;
//            ServiceSingleton.getInstance().setService(IRemoteService.Stub.asInterface(service));
//
//            if (resendInitializationSignal) {
//                resendHandler.sendMessage(resendMsg);
//                resendInitializationSignal = false;
//            }
//
//            if (resendMenuHandler != null) {
//                resendMenuHandler.sendEmptyMessage(0);
//                resendMenuHandler = null;
//            }
//            // Register the fragment callback
//            registerCallBackIsWaiting();
//        }
//    };

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
     * clean and return last saved seller review
     *
     * @return last saved review
     */
    public static ContentValues getSellerReviewValues() {
        return JumiaApplication.sellerReviewValues;
    }

    /**
     * clean current rating
     */
    public static void cleanSellerReviewValues() {
        JumiaApplication.sellerReviewValues = null;
    }

    public static void setSellerReviewValues(ContentValues sellerReviewValues) {
        JumiaApplication.sellerReviewValues = sellerReviewValues;
    }

    /**
     * flag to control if it is showing seller review, ou product review
     */
    public static void setIsSellerReview(boolean mIsSellerReview) {
        JumiaApplication.isSellerReview = mIsSellerReview;
    }

    /**
     * flag to control if it is showing seller review, ou product review
     */
    public static boolean getIsSellerReview() {
        return JumiaApplication.isSellerReview;
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
        setCart(null);
        setFormDataRegistry(new HashMap<String, FormData>());
        registerForm = null;
        paymentMethodForm = null;
        registerSavedInstanceState = null;
        CUSTOMER = null;
        getCustomerUtils().save();
        mCustomerUtils = null;
        cart = null;
        paymentsInfoList = null;
        itemSimpleDataRegistry.clear();
        formDataRegistry.clear();
        responseCallbacks.clear();
        countriesAvailable.clear();
        reviewForm = null;
        ratingForm = null;
        mSellerReviewForm = null;
        isSellerReview = false;
        ratingReviewValues = null;
        sellerReviewValues = null;
        sFormReviewValues = null;
        AdjustTracker.resetTransactionCount(getApplicationContext());
        clearBannerFlowSkus();
    }

    /**
     * add a sku to a list of sku products that were added from a banner flow
     */
    public void setBannerFlowSkus(String sku,TeaserGroupType groupType) {
        if(bannerSkus == null){
            bannerSkus = new HashMap<>();
        }

        if(!TextUtils.isEmpty(sku)){
            if(bannerSkus.size() == 0){
                bannerSkus.put(sku, groupType);
            } else {
                if(!bannerSkus.containsKey(sku)){
                    bannerSkus.put(sku, groupType);
                }
            }
        }
    }

    /**
     * returns a list of skus of products that were added to cart from a banner flow
     *
     * @return list of skus
     */
    public HashMap<String,TeaserGroupType> getBannerFlowSkus() {
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

