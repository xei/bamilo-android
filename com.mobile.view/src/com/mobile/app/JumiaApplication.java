package com.mobile.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.ad4screen.sdk.A4SApplication;
import com.mobile.components.NavigationListComponent;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.forms.Form;
import com.mobile.forms.FormData;
import com.mobile.forms.PaymentMethodForm;
import com.mobile.framework.Darwin;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.database.DarwinDatabaseHelper;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.CountryObject;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.objects.PaymentInfo;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.objects.VersionInfo;
import com.mobile.framework.service.IRemoteService;
import com.mobile.framework.service.IRemoteServiceCallback;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.ApptimizeTracking;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.ImageResolutionHelper;
import com.mobile.framework.utils.SingletonMap;
import com.mobile.helpers.BaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.preferences.ShopPreferences;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.ServiceSingleton;
import com.mobile.utils.imageloader.RocketImageLoader;

import de.akquinet.android.androlog.Log;

public class JumiaApplication extends A4SApplication {

    private static final String TAG = JumiaApplication.class.getSimpleName();

    public static String SHOP_ID = null;
    public static String SHOP_NAME = "";
    public static Customer CUSTOMER;

    public static JumiaApplication INSTANCE;
    public static boolean mIsBound = false;
    /**
     * Account Variables
     */
    private CustomerUtils mCustomerUtils;
    private boolean loggedIn = false;

    /**
     * General Persistent Variables
     */
    private HashMap<String, HashMap<String, String>> ratingOptions = null;
    private CompleteProduct currentProduct = null;
    private VersionInfo mVersionInfo;
    
    boolean resendInitializationSignal = false;

    /**
     * Cart
     */
    private Map<String, Map<String, String>> itemSimpleDataRegistry = new HashMap<String, Map<String, String>>();
    private ShoppingCart cart;

    /**
     * Forms
     */
    private HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();

    public static final SingletonMap<ApplicationComponent> COMPONENTS = new SingletonMap<ApplicationComponent>(new DarwinComponent());
    
    public static ArrayList<NavigationListComponent> navigationListComponents;
    
    // TODO : Validate recover
    private static ArrayList<EventType> requestOrder = new ArrayList<EventType>();

    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;

    private Handler resendHandler;
    private Handler resendMenuHandler;
    private Message resendMsg;

    /**
     * Fallback and retry backups
     */
    private HashMap<EventType, Bundle> requestsRetryBundleList = new HashMap<EventType, Bundle>();
    private HashMap<EventType, BaseHelper> requestsRetryHelperList = new HashMap<EventType, BaseHelper>();
    private HashMap<EventType, IResponseCallback> requestsResponseList = new HashMap<EventType, IResponseCallback>();

    private IRemoteServiceCallback callBackWaitingService;

    /**
     * Payment methods Info
     */
    private static HashMap<String, PaymentInfo> paymentsInfoList;

    // TODO use an alternative to persist form on rotation
    public Form registerForm;

    // TODO use an alternative to persist filled fields on rotation
    public Bundle registerSavedInstanceState;

    public int lastPaymentSelected = -1;

    public ArrayList<CountryObject> countriesAvailable = null;
    
    public boolean trackSearch = true;
    // for tracking
    public boolean trackSearchCategory = true;
    
    private PaymentMethodForm paymentMethodForm;
    
    private static ContentValues review;
    
    private static ContentValues rating;
    
    private static ContentValues ratingReviewValues;
    
    // TODO use an alternative to persist form on rotation
    public Form reviewForm;
    
    // TODO use an alternative to persist form on rotation
    public Form ratingForm;

    /*
     * (non-Javadoc)
     * @see com.ad4screen.sdk.A4SApplication#onApplicationCreate()
     */
    @Override
    public void onApplicationCreate() {
        Log.d(TAG, "ON CREATE");

        Log.init(getApplicationContext());

        INSTANCE = this;

        SharedPreferences sharedPrefs = this.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Service
        doBindService();
        // Init image loader
        RocketImageLoader.init(this);
        // Init apptimize
        ApptimizeTracking.startup(getApplicationContext());
        // Init darwin database, set the context
        DarwinDatabaseHelper.init(getApplicationContext());

        countriesAvailable = new ArrayList<CountryObject>();

        responseCallbacks = new HashMap<String, IResponseCallback>();
        // Get the current shop id
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        if (SHOP_ID != null) {
            SHOP_NAME = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        }
        setItemSimpleDataRegistry(new HashMap<String, Map<String, String>>());
        setCart(null);
        ImageResolutionHelper.init(this);
        setFormDataRegistry(new HashMap<String, FormData>());
        navigationListComponents = null;

        /**
         * Fix a crash report, when app try recover from brackground
         * https://rink.hockeyapp.net/manage/apps/33641/app_versions/109/crash_reasons/17098450
         * @author sergiopereira
         */
        Log.d(TAG, "INIT CURRENCY");
        String currencyCode = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, null);
        if(currencyCode != null) CurrencyFormatter.initialize(getApplicationContext(), currencyCode);
        
    }

    public synchronized void init(boolean isReInit, Handler initializationHandler) {
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

        SharedPreferences sharedPrefs = this.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, null);
        Log.i(TAG, "code1configs : SHOP_ID : " + SHOP_ID + " SHOP_NAME : " + SHOP_NAME);

        // Disabled for Samsung and Blackberry (check_version_enabled) 
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());

        handleEvent(ErrorCode.NO_ERROR, EventType.INITIALIZE, initializationHandler);

        // Disabled for Samsung and Blackberry (check_version_enabled) 
        CheckVersion.init(getApplicationContext());

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
        if((eventType == EventType.INITIALIZE || 
                errorType == ErrorCode.NO_COUNTRIES_CONFIGS || 
                errorType == ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE)
                && ServiceSingleton.getInstance().getService() == null ){
            Log.d(TAG, "ON HANDLE WITH ERROR");
            resendInitializationSignal = true;
            resendHandler = initializationHandler;
            resendMsg = msg;
            doBindService();
        } else {
            Log.d(TAG, "ON INIT HANDLE");
            initializationHandler.sendMessage(msg);
        }
    }

    public void registerFragmentCallback(IRemoteServiceCallback mCallback) {
        Log.d(TAG, "ON REGISTER CALL BACK FRAGMENT");
        if (mCallback == null) {
            Log.i(TAG, "mCallback is null");
        }
        if (ServiceSingleton.getInstance().getService() == null) {
            Log.i(TAG, "ServiceSingleton.getInstance().getService() is null");
            // Try connect with service
            doBindService();
            // Save the call back
            callBackWaitingService = mCallback;
            return;
        }
        try {
            ServiceSingleton.getInstance().getService().registerCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unRegisterFragmentCallback(IRemoteServiceCallback mCallback) {
        if (mCallback == null) {
            Log.i(TAG, "mCallback is null");
        }
        if (ServiceSingleton.getInstance().getService() != null) {
            try {
                ServiceSingleton.getInstance().getService().unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method used to register the call back that is waiting for service.
     * 
     * @author sergiopereira
     */
    private void registerCallBackIsWaiting() {
        try {
            // Validate the current call back waiting by service
            if (callBackWaitingService != null) {
                ServiceSingleton.getInstance().getService().registerCallback(callBackWaitingService);
                callBackWaitingService = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // TODO : Validate recover
    public String sendRequest(final BaseHelper helper, final Bundle args, final IResponseCallback responseCallback) {
        return sendRequest(helper, args, responseCallback, true);
    }

    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param responseCallback
     * @return the md5 of the reponse
     */
    // TODO : Validate recover
    public String sendRequest(final BaseHelper helper, final Bundle args, final IResponseCallback responseCallback, boolean addToRequestOrder) {
        if (helper == null) {
            return "";
        }
        final Bundle bundle = helper.generateRequestBundle(args);

        if (bundle.containsKey(Constants.BUNDLE_EVENT_TYPE_KEY)) {
            Log.i(TAG, "codesave saving : " + (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
            requestsRetryHelperList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), helper);
            requestsRetryBundleList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), args);
            requestsResponseList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), responseCallback);
            // TODO : Validate recover
            if (addToRequestOrder) {
                requestOrder.add((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
            }
        } else {
            Log.w(TAG, " MISSING EVENT TYPE from " + helper.toString());
        }
        final String md5 = bundle.getString(Constants.BUNDLE_MD5_KEY);

        Log.d("TRACK", "sendRequest");
        new Thread(new Runnable() {

            @Override
            public void run() {
                
                //Log.i(TAG, "############ RQ CURRENT THREAD ID: " + Thread.currentThread().getId());
                //Log.i(TAG, "############ RQ MAIN THREAD ID: " + Looper.getMainLooper().getThread().getId());
                
                JumiaApplication.INSTANCE.responseCallbacks.put(md5, new IResponseCallback() {

                    @Override
                    public void onRequestComplete(Bundle bundle) {
                        
                        /**
                         * ###################################################
                         * # FIXME: WARNING - THIS IS RUNNING IN MAIN THREAD #
                         * # - Alternative -> ParseSuccessAsyncTask          #
                         * # @author sergiopereira                           #
                         * ###################################################
                         */
                        //Log.i(TAG, "############ RP CURRENT THREAD ID: " + Thread.currentThread().getId());
                        //Log.i(TAG, "############ RP MAIN THREAD ID: " + Looper.getMainLooper().getThread().getId());                        
                        //new ParseSuccessAsyncTask(helper, bundle, responseCallback).execute();
                        
                        Log.d("TRACK", "onRequestComplete BaseActivity");
                        // We have to parse this bundle to the final one
                        Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle);
                        if (responseCallback != null) {
                            if (formatedBundle.getBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY)) {
                                responseCallback.onRequestError(formatedBundle);
                            } else {
                                responseCallback.onRequestComplete(formatedBundle);
                            }
                        }
                    }

                    @Override
                    public void onRequestError(Bundle bundle) {
                        Log.d("TRACK", "onRequestError  BaseActivity");
                        // We have to parse this bundle to the final one
                        Bundle formatedBundle = (Bundle) helper.parseErrorBundle(bundle);
                        if (responseCallback != null) {
                            responseCallback.onRequestError(formatedBundle);
                        }
                    }
                });

                
                // TODO : Validate recover
                if (!sendRequest(bundle)) {
                    Log.e(TAG, "SERVICE NOT AVAILABLE FOR EVENTTYPE " + bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
                    /*-bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.REQUEST_ERROR);
                    responseCallback.onRequestError(bundle);*/
                }
                
            }
        }).start();

        return md5;
    }

    // TODO : Validate recover
    public boolean sendRequest(Bundle bundle) {
        if(ServiceSingleton.getInstance().getService() != null){
            try {
                ServiceSingleton.getInstance().getService().sendRequest(bundle);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * Method called then the activity is connected to the service
     */
    protected void onServiceActivation() {

    }

    /**
     * @return the ratingOptions
     */
    public HashMap<String, HashMap<String, String>> getRatingOptions() {
        return ratingOptions;
    }

    /**
     * @param ratingOptions
     *            the ratingOptions to set
     */
    public void setRatingOptions(HashMap<String, HashMap<String, String>> ratingOptions) {
        this.ratingOptions = ratingOptions;
    }

    /**
     * @return the currentProduct
     */
    public CompleteProduct getCurrentProduct() {
        return currentProduct;
    }

    /**
     * @param currentProduct
     *            the currentProduct to set
     */
    public void setCurrentProduct(CompleteProduct currentProduct) {
        this.currentProduct = currentProduct;
    }

    /**
     * @return the mVersionInfo
     */
    public VersionInfo getVersionInfo() {
        return mVersionInfo;
    }

    /**
     * @param mVersionInfo
     *            the mVersionInfo to set
     */
    public void setVersionInfo(VersionInfo mVersionInfo) {
        this.mVersionInfo = mVersionInfo;
    }

    /**
     * @return the mCustomerUtils
     */
    public CustomerUtils getCustomerUtils() {
        if (mCustomerUtils == null) {
            mCustomerUtils = new CustomerUtils(getApplicationContext());
        }
        return mCustomerUtils;
    }

    /**
     * @param mCustomerUtils
     *            the mCustomerUtils to set
     */
    public void setCustomerUtils(CustomerUtils mCustomerUtils) {
        this.mCustomerUtils = mCustomerUtils;
    }

    /**
     * @return the cart
     */
    public ShoppingCart getCart() {
        return this.cart;
    }

    /**
     * @param cart
     *            the cart to set
     */
    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    /**
     * @return the itemSimpleDataRegistry
     */
    public Map<String, Map<String, String>> getItemSimpleDataRegistry() {
        return itemSimpleDataRegistry;
    }

    /**
     * @param itemSimpleDataRegistry
     *            the itemSimpleDataRegistry to set
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
     * @param formDataRegistry
     *            the formDataRegistry to set
     */
    public void setFormDataRegistry(HashMap<String, FormData> formDataRegistry) {
        this.formDataRegistry = formDataRegistry;
    }

    public void doBindService() {

        if (!mIsBound) {

            /**
             * Establish a connection with the service. We use an explicit class
             * name because we want a specific service implementation that we
             * know will be running in our own process (and thus won't be
             * supporting component replacement by other applications).
             */
            bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void doUnbindService() {
        if (mIsBound) {
            mIsBound = false;

            // Detach our existing connection.
            // unbindService(mConnection);
        }
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

    /**
     * @return the requestsRetryBundleList
     */
    public HashMap<EventType, Bundle> getRequestsRetryBundleList() {
        return requestsRetryBundleList;
    }

    /**
     * @param requestsRetryBundleList
     *            the requestsRetryBundleList to set
     */
    public void setRequestsRetryBundleList(HashMap<EventType, Bundle> requestsRetryBundleList) {
        this.requestsRetryBundleList = requestsRetryBundleList;
    }

    /**
     * @return the requestsRetryHelperList
     */
    public HashMap<EventType, BaseHelper> getRequestsRetryHelperList() {
        return requestsRetryHelperList;
    }

    /**
     * @param requestsRetryHelperList
     *            the requestsRetryHelperList to set
     */
    public void setRequestsRetryHelperList(HashMap<EventType, BaseHelper> requestsRetryHelperList) {
        this.requestsRetryHelperList = requestsRetryHelperList;
    }

    /**
     * @return the requestsResponseList
     */
    public HashMap<EventType, IResponseCallback> getRequestsResponseList() {
        return requestsResponseList;
    }
    
    
    // TODO : Validate recover
    public ArrayList<EventType> getRequestOrderList(){
        return requestOrder;
    }

    /**
     * @param requestsResponseList
     *            the requestsResponseList to set
     */
    public void setRequestsResponseList(HashMap<EventType, IResponseCallback> requestsResponseList) {
        this.requestsResponseList = requestsResponseList;
    }

    public void setResendHander(Handler mHandler) {
        resendInitializationSignal = true;
        resendMsg = new Message();
        resendHandler = mHandler;
    }

    public void setResendMenuHander(Handler mHandler) {
        resendMenuHandler = mHandler;
    }

    /**
     * Service Stuff
     */

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mIsBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            Log.i(TAG, "onServiceConnected");
            mIsBound = true;
            ServiceSingleton.getInstance().setService(IRemoteService.Stub.asInterface(service));
            
            // TODO : Validate recover
            // TODO uncomment this to re execute pending requests
            
            /*-if (requestOrder != null && requestOrder.size() > 0) {
                Log.i(TAG, " RE-EXECUTING PENDING REQUESTS " + requestOrder.size());
                for (int i = 0; i < requestOrder.size(); i++) {
                    Log.i(TAG, " RE-EXECUTING PENDING REQUESTS " + requestOrder.get(i).toString());
                    sendRequest(requestsRetryHelperList.get(requestOrder.get(i)), requestsRetryBundleList.get(requestOrder.get(i)), requestsResponseList.get(requestOrder.get(i)), false);
                }
                requestOrder.clear();
            } else {*/
            if (resendInitializationSignal) {
                resendHandler.sendMessage(resendMsg);
                resendInitializationSignal = false;
            }

            if (resendMenuHandler != null) {
                resendMenuHandler.sendEmptyMessage(0);
                resendMenuHandler = null;
                /* } */

            }
            // Register the fragment callback
            registerCallBackIsWaiting();
        }
    };

    public void setPaymentMethodForm(PaymentMethodForm paymentMethodForm) {
        this.paymentMethodForm = paymentMethodForm;
    }

    public PaymentMethodForm getPaymentMethodForm() {
        return this.paymentMethodForm;
    }
    
    //FIXME
    /**
     * clean and return last saved rating
     * 
     * @return last saved review
     */
    public static ContentValues getRatingReviewValues() {
        ContentValues currentRating = JumiaApplication.ratingReviewValues;
        return currentRating;
    }

    /**
     * clean current rating
     */
    public static void cleanRatingReviewValues() {
        JumiaApplication.ratingReviewValues = null;
    }

    public static void setRatingReviewValues(ContentValues ratingReviewValues) {
        
//        if(JumiaApplication.ratingReviewValues != null && ratingReviewValues != null && ratingReviewValues.size() > 0 && JumiaApplication.ratingReviewValues.size() > 0){
//            mergeRatingReviewValuesFormValues(ratingReviewValues);
//        } else {
            JumiaApplication.ratingReviewValues = ratingReviewValues;    
//        }
        
    }
    
    private static void mergeRatingReviewValuesFormValues(ContentValues ratingReviewValues){
        
        Set<Entry<String, Object>> s=ratingReviewValues.valueSet();
        Iterator itr = s.iterator();
        
        while(itr.hasNext())
        {
             Entry me = (Entry)itr.next(); 
             String key = me.getKey().toString();
             Object value =  me.getValue();
             JumiaApplication.ratingReviewValues.put(key, (String)value.toString());
        }
        
    }
    
//    /**
//     * clean and return last saved rating
//     * 
//     * @return last saved review
//     */
//    public static ContentValues getRating() {
//        ContentValues currentRating = JumiaApplication.rating;
//        return currentRating;
//    }
//
//    /**
//     * clean current rating
//     */
//    public static void cleanRating() {
//        JumiaApplication.rating = null;
//    }
//
//    public static void setRating(ContentValues rating) {
//        JumiaApplication.rating = rating;
//    }
//
//    
//    /**
//     * clean and return last saved review
//     * 
//     * @return last saved review
//     */
//    public static ContentValues getReview() {
//        ContentValues currentReview = JumiaApplication.review;
//        return currentReview;
//    }
//
//    /**
//     * clean current review
//     */
//    public static void cleanReview() {
//        JumiaApplication.review = null;
//    }
//
//    public static void setReview(ContentValues review) {
//        JumiaApplication.review = review;
//    }
    
    /**
     * @return the paymentsInfoList
     */
    public static HashMap<String, PaymentInfo> getPaymentsInfoList() {
        return paymentsInfoList;
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
        getCustomerUtils().clearCredentials();
        review = null;
        rating = null;
        CUSTOMER = null;        
        mCustomerUtils = null;
        currentProduct = null;
        cart = null;
        ratingOptions = null;
        navigationListComponents = null;
        paymentsInfoList = null;
        itemSimpleDataRegistry.clear();
        formDataRegistry.clear();
        requestOrder.clear();       
        responseCallbacks.clear();
        requestsRetryBundleList.clear();
        requestsRetryHelperList.clear();
        requestsResponseList.clear();
        countriesAvailable.clear();
        reviewForm = null;
        ratingForm = null;
        resetTransactionCount();
    }
    
    private void resetTransactionCount() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.commit();
    }  
    
    @SuppressWarnings("unused")
    @Deprecated
    private class ParseSuccessAsyncTask extends AsyncTask<Void, Void, Bundle> {

        private BaseHelper helper;
        private Bundle bundle;
        private IResponseCallback callback;

        private ParseSuccessAsyncTask(BaseHelper helper, Bundle bundle, IResponseCallback callback) {
            this.helper = helper;
            this.bundle = bundle;
            this.callback = callback;
        }
        
        @Override
        protected Bundle doInBackground(Void... params) {
            Log.i(TAG, "############ AS CURRENT THREAD ID: " + Thread.currentThread().getId());
            Log.i(TAG, "############ AS MAIN THREAD ID: " + Looper.getMainLooper().getThread().getId());
            return helper.checkResponseForStatus(bundle);
        }
        
        @Override
        protected void onPostExecute(Bundle result) {
            if (callback != null) {
                if (result.getBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY)) {
                    callback.onRequestError(result);
                } else {
                    callback.onRequestComplete(result);
                }
            }
        }
    }

}
