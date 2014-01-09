package pt.rocket.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oak.ObscuredSharedPreferences;

import com.bugsense.trace.ExceptionCallback;

import pt.rocket.app.ApplicationComponent;
import pt.rocket.app.DarwinComponent;
import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.app.UrbanAirshipComponent;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.SingletonMap;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.preferences.ShopPreferences;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import de.akquinet.android.androlog.Log;

public class JumiaApplication extends Application implements ExceptionCallback {

    private static final String TAG = JumiaApplication.class.getSimpleName();

    public static JumiaApplication INSTANCE;
    
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
    

    public static final SingletonMap<ApplicationComponent> COMPONENTS =
            new SingletonMap<ApplicationComponent>(new UrbanAirshipComponent(),
                    new ImageLoaderComponent(), new DarwinComponent());
    
    private IRemoteService mService;
    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;

    public static int SHOP_ID = -1;
    
    private boolean isInitializing = false;

    private IResponseCallback initListener;

    private Bundle lastInitEvent;

    @Override
    public void onCreate() {
        Log.init(getApplicationContext());
        bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
        INSTANCE = this;
        responseCallbacks = new HashMap<String, IResponseCallback>();
        init(false);
        
        // Get the current shop id
        SHOP_ID = ShopPreferences.getShopId(getApplicationContext());
        
    }

    public synchronized void init(boolean isReInit) {
        isInitializing = true;
        Log.d(TAG, "Starting initialisation phase");
        AnalyticsGoogle.clearCheckoutStarted();
        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(this);
            if (result != ErrorCode.NO_ERROR) {
                handleEvent(ErrorCode.REQUIRES_USER_INTERACTION);
                return;
            }
        }
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
//        InitializeEvent event = new InitializeEvent(EnumSet.noneOf(EventType.class));
//        if ( isReInit ) {
//            event.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, true);
//        }
//        EventManager.getSingleton().triggerRequestEvent( event );
        CheckVersion.init(getApplicationContext());
    }

    public synchronized void waitForInitResult(IResponseCallback listener, boolean isReInit) {
        if (lastInitEvent != null) {
            Log.d(TAG, "Informing listener about last init event " + lastInitEvent);
            listener.onRequestComplete(lastInitEvent);
            lastInitEvent = null;
            return;
        } else {
            this.initListener = listener;
            if (!isInitializing) {
                init(isReInit);
            }
        }
    }
    
    public synchronized void clearInitListener() {
        initListener = null;
    }

    public synchronized void handleEvent(ErrorCode errorType) {
        isInitializing = false;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, errorType);
        Log.d(TAG, "Handle initialization result: " + errorType);
        if (initListener != null) {
            Log.d(TAG, "Informing initialisation listener: " + initListener);
            initListener.onRequestComplete(bundle);
        } else {
            this.lastInitEvent = bundle;
        }
    }

    public void registerFragmentCallback(IRemoteServiceCallback mCallback){
        mService = ServiceSingleton.getInstance().getService();
        
        try {
            mService.registerCallback(mCallback);
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
    public String sendRequest(final BaseHelper helper, Bundle args,
            final IResponseCallback responseCallback) {
        Bundle bundle = helper.generateRequestBundle(args);
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        Log.d("TRACK", "sendRequest");
        JumiaApplication.INSTANCE.responseCallbacks.put(md5, new IResponseCallback() {

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.d("TRACK", "onRequestComplete BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestComplete(formatedBundle);
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

        return md5;
    }

    
    
    public void sendRequest(Bundle bundle){
        try {
            mService.sendRequest(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
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
     * @param ratingOptions the ratingOptions to set
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
     * @param currentProduct the currentProduct to set
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
     * @param mVersionInfo the mVersionInfo to set
     */
    public void setVersionInfo(VersionInfo mVersionInfo) {
        this.mVersionInfo = mVersionInfo;
    }


    /**
     * @return the mCustomerUtils
     */
    public CustomerUtils getCustomerUtils() {
        if(mCustomerUtils == null){
            mCustomerUtils = new CustomerUtils(getApplicationContext());
        }
        return mCustomerUtils;
    }

    /**
     * @param mCustomerUtils the mCustomerUtils to set
     */
    public void setCustomerUtils(CustomerUtils mCustomerUtils) {
        this.mCustomerUtils = mCustomerUtils;
    }


    /**
     * Service Stuff
     */
    
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            Log.i(TAG, "onServiceConnected");
            mService = IRemoteService.Stub.asInterface(service);
            
        }
    };
    


}
