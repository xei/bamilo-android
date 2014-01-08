package pt.rocket.utils;

import java.util.EnumSet;
import java.util.HashMap;

import com.bugsense.trace.ExceptionCallback;

import pt.rocket.app.ApplicationComponent;
import pt.rocket.app.DarwinComponent;
import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.app.UrbanAirshipComponent;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.SingletonMap;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import android.app.Application;
import android.os.Bundle;
import android.os.RemoteException;
import de.akquinet.android.androlog.Log;

public class JumiaApplication extends Application implements ExceptionCallback {

    private static final String TAG = JumiaApplication.class.getSimpleName();

    public static JumiaApplication INSTANCE;

    public static final SingletonMap<ApplicationComponent> COMPONENTS =
            new SingletonMap<ApplicationComponent>(new UrbanAirshipComponent(),
                    new ImageLoaderComponent(), new DarwinComponent());
    
    private IRemoteService mService;
    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;

    private boolean isInitializing = false;

    @Override
    public void onCreate() {
        Log.init(getApplicationContext());
        INSTANCE = this;
        responseCallbacks = new HashMap<String, IResponseCallback>();
        EventManager.getSingleton().addResponseListener(EventType.INITIALIZE, this);
        init(false);
    }

    public synchronized void init(boolean isReInit) {
        isInitializing = true;
        Log.d(TAG, "Starting initialisation phase");
        AnalyticsGoogle.clearCheckoutStarted();
        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(this);
            if (result != ErrorCode.NO_ERROR) {
                handleEvent(new ResponseErrorEvent(new RequestEvent(EventType.INITIALIZE),
                        ErrorCode.REQUIRES_USER_INTERACTION));
                return;
            }
        }
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        InitializeEvent event = new InitializeEvent(EnumSet.noneOf(EventType.class));
        if ( isReInit ) {
            event.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, true);
        }
        EventManager.getSingleton().triggerRequestEvent( event );
        CheckVersion.init(getApplicationContext());
    }

    public synchronized void waitForInitResult(IRemoteServiceCallback listener, boolean isReInit) {
        if (lastInitEvent != null) {
            Log.d(TAG, "Informing listener about last init event " + lastInitEvent);
            listener.handleEvent(lastInitEvent);
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public synchronized void handleEvent(ResponseEvent event) {
        isInitializing = false;
        Log.d(TAG, "Handle initialization result: " + event);
        if (initListener != null) {
            Log.d(TAG, "Informing initialisation listener: " + initListener);
            initListener.handleEvent(event);
        } else {
            this.lastInitEvent = event;
        }
    }

    public void registerFragmentCallback(IRemoteServiceCallback mCallback){
        mService = ServiceSingleton.getInstance().getService();
        
        try {
            mService.registerCallback(mCallback);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
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

}
