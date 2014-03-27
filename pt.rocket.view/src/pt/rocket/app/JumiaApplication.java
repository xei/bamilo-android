package pt.rocket.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.rocket.forms.FormData;
import pt.rocket.forms.PaymentMethodForm;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.objects.Address;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.PaymentInfo;
import pt.rocket.framework.objects.PaymentMethods;
import pt.rocket.framework.objects.ShippingMethods;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.ImageResolutionHelper;
import pt.rocket.framework.utils.SingletonMap;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.preferences.ShopPreferences;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.ServiceSingleton;
import pt.rocket.view.R;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.bugsense.trace.ExceptionCallback;

import de.akquinet.android.androlog.Log;

public class JumiaApplication extends Application implements ExceptionCallback {

    private static final String TAG = JumiaApplication.class.getSimpleName();
    
    public static int SHOP_ID = -1;
    public static String SHOP_NAME = "";
    public static Customer CUSTOMER;
    public static int SHOP_ID_FOR_ADX = -1;

    public static JumiaApplication INSTANCE;
    public static boolean mIsBound = false;
    /**
     * Account Variables
     */
    private CustomerUtils mCustomerUtils;
    private boolean loggedIn = false;
    private Integer shopId = null;

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
    private Map<String, Map<String, String>> itemSimpleDataRegistry =
            new HashMap<String, Map<String, String>>();
    private ShoppingCart cart;

    /**
     * Forms
     */
    private HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();

    public static final SingletonMap<ApplicationComponent> COMPONENTS =
            new SingletonMap<ApplicationComponent>(new UrbanAirshipComponent(),
                     new DarwinComponent());

    public static ArrayList<NavigationListComponent> navigationListComponents;
    

    
    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;

    private boolean isInitializing = false;
//    public boolean isUAInitialized = false;
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
     * Current categories
     */
    public static ArrayList<Category> currentCategories;
    
    /**
     * Payment methods Info
     */
    private static HashMap<String,PaymentInfo> paymentsInfoList;
    
    /**
     * Tracking Request performance
     */
//    public AndroidFileFunctions trackerFile;
//	public HashMap<EventType, Long> timeTrackerMap = new HashMap<EventType, Long>(); 
    
    @Override
    public void onCreate() {
        /**
         * Force UA clean the previous configurations.
         */
//        trackerFile = new AndroidFileFunctions();
//        UAirship.takeOff(this);
        doBindService();
        
        Log.init(getApplicationContext());
        Log.d(TAG, "onCreate");
        INSTANCE = this;
        
        /**
         * AQuery Configurations
         */
        BitmapAjaxCallback.setCacheLimit(50);
        //set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(2);
        
        responseCallbacks = new HashMap<String, IResponseCallback>();
        // Get the current shop id
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        if(SHOP_ID>-1)
            SHOP_NAME = getResources().getStringArray(R.array.language_codes)[SHOP_ID];
        setItemSimpleDataRegistry(new HashMap<String, Map<String, String>>());
        setCart(null);
        ImageResolutionHelper.init(this);
        setFormDataRegistry(new HashMap<String, FormData>());
        navigationListComponents = null;
        
        COMPONENTS.get(UrbanAirshipComponent.class).init(this);
    }

    @Override
    public void onLowMemory() {     
        super.onLowMemory();
        
        //clear all memory cached images when system is in low memory
        //note that you can configure the max image cache count, see CONFIGURATION
        BitmapAjaxCallback.clearCache();
    }
    
    public synchronized void init(boolean isReInit, Handler initializationHandler) {
        isInitializing = true;
        Log.d(TAG, "Starting initialisation phase");
        AnalyticsGoogle.clearCheckoutStarted();
        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(JumiaApplication.this);
//            Log.i(TAG, "code1 initializing component : "+component.getClass().getName());
            if (result != ErrorCode.NO_ERROR) {
//                Log.i(TAG, "code1 component : "+component.getClass().getName()+" error code : "+result);
                handleEvent(result, null, initializationHandler);
                return;
            }
        }
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        SHOP_NAME = getResources().getStringArray(R.array.language_codes)[SHOP_ID];
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        handleEvent(ErrorCode.NO_ERROR, EventType.INITIALIZE, initializationHandler);
        // InitializeEvent event = new InitializeEvent(EnumSet.noneOf(EventType.class));
        // if ( isReInit ) {
        // event.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, true);
        // }
        // EventManager.getSingleton().triggerRequestEvent( event );
        CheckVersion.init(getApplicationContext());
    }

    public synchronized void handleEvent(ErrorCode errorType, EventType eventType, Handler initializationHandler) {
        isInitializing = false;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, errorType);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, eventType);
        Log.d(TAG, "Handle initialization result: " + errorType);
        Message msg = new Message();
        msg.obj = bundle;
        if(eventType == EventType.INITIALIZE && errorType == ErrorCode.NO_ERROR && ServiceSingleton.getInstance().getService() == null ){
            resendInitializationSignal = true;
            resendHandler = initializationHandler;
            resendMsg = msg;
            doBindService();
        } else {
            initializationHandler.sendMessage(msg);    
        }
    }

    public void registerFragmentCallback(IRemoteServiceCallback mCallback) {
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
     * @author sergiopereira
     */
    private void registerCallBackIsWaiting() {
        try {
            // Validate the current call back waiting by service
            if (callBackWaitingService != null){
                ServiceSingleton.getInstance().getService().registerCallback(callBackWaitingService);
                callBackWaitingService = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param responseCallback
     * @return the md5 of the reponse
     */
    public String sendRequest(final BaseHelper helper, final Bundle args,
            final IResponseCallback responseCallback) {
        if(helper == null){
            return "";
        }
        final Bundle bundle = helper.generateRequestBundle(args);
        
        if(bundle.containsKey(Constants.BUNDLE_EVENT_TYPE_KEY)){
            Log.i(TAG, "codesave saving : "+(EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
            requestsRetryHelperList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), helper);
            requestsRetryBundleList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), args);
            requestsResponseList.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), responseCallback);
        } else {
            Log.w(TAG, " MISSING EVENT TYPE from "+helper.toString());
        }
        final String md5 = bundle.getString(Constants.BUNDLE_MD5_KEY);
        Log.d("TRACK", "sendRequest");
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                JumiaApplication.INSTANCE.responseCallbacks.put(md5, new IResponseCallback() {

                    @Override
                    public void onRequestComplete(Bundle bundle) {
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

                sendRequest(bundle);
                
            }
        }).start();
        
        

        return md5;
    }

    public void sendRequest(Bundle bundle) {
        long timeMillis = System.currentTimeMillis();
//        Log.i("REQUEST", "performing event type request : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" url : "+bundle.getString(Constants.BUNDLE_URL_KEY));
//        timeTrackerMap.put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), timeMillis);
        if(ServiceSingleton.getInstance().getService() != null){
            try {
                ServiceSingleton.getInstance().getService().sendRequest(bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }    
        }
        
    }

    /**
     * Method called then the activity is connected to the service
     */
    protected void onServiceActivation() {

    }

    @Override
    public void lastBreath(Exception arg0) {

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
        return cart;
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
        
        if(!mIsBound){
            
            /**
             *  Establish a connection with the service.  We use an explicit
             *  class name because we want a specific service implementation that
             *  we know will be running in our own process (and thus won't be
             *  supporting component replacement by other applications).
             */
            bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    
    public void doUnbindService() {
        if (mIsBound) {
            mIsBound = false;
            
            // Detach our existing connection.
            //            unbindService(mConnection);
        }
    }
    
    /**
     * @return the loggedIn
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @param loggedIn the loggedIn to set
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
     * @param requestsRetryBundleList the requestsRetryBundleList to set
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
     * @param requestsRetryHelperList the requestsRetryHelperList to set
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

    /**
     * @param requestsResponseList the requestsResponseList to set
     */
    public void setRequestsResponseList(HashMap<EventType, IResponseCallback> requestsResponseList) {
        this.requestsResponseList = requestsResponseList;
    }

    public void setResendHander(Handler mHandler){
        resendInitializationSignal = true;
        resendMsg = new Message();
        resendHandler = mHandler;
    }
    
    public void setResendMenuHander(Handler mHandler){
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
            if(resendInitializationSignal){
                resendHandler.sendMessage(resendMsg);
                resendInitializationSignal = false;
            }
            
            if(resendMenuHandler != null){
                resendMenuHandler.sendEmptyMessage(0);
                resendMenuHandler = null;
            }
            
            // Register the fragment callback
            registerCallBackIsWaiting();
        }
    };

    /**
     * ######### TODO: REMOVE ######### 
     */
    private Address shippingAddress;
    private Address billingAddress;
    private ContentValues shippingMethod;
    private ContentValues paymentMethod;
    private PaymentMethodForm paymentMethodForm;

    public Address getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(Address shippingAddress ) {
        this.shippingAddress = shippingAddress;
    }
    
    public Address getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(Address billingAddress ) {
        this.billingAddress = billingAddress;
    }
    
    public ContentValues getShippingMethod() {
        return shippingMethod;
    }
    
    public void setShippingMethod(ContentValues shippingMethod ) {
        this.shippingMethod = shippingMethod;
    }
    
    public ContentValues getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(ContentValues paymentMethod ) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setPaymentMethodForm(PaymentMethodForm paymentMethodForm ) {
        this.paymentMethodForm = paymentMethodForm;
    }
    
    public PaymentMethodForm getPaymentMethodForm() {
        return this.paymentMethodForm;
    }

    /**
     * @return the paymentsInfoList
     */
    public static HashMap<String,PaymentInfo> getPaymentsInfoList() {
        return paymentsInfoList;
    }

    /**
     * @param paymentsInfoList the paymentsInfoList to set
     */
    public static void setPaymentsInfoList(HashMap<String,PaymentInfo> paymentsInfoList) {
        JumiaApplication.paymentsInfoList = paymentsInfoList;
    }
    
    /**
     * Save tracking values to the file
     * Only for dev environment
     */
//    public void writeToTrackerFile(String value){
//        trackerFile.writeToFile(value, this, Context.MODE_APPEND);
//    }
}