package pt.rocket.testapp.activities;

import java.util.HashMap;

import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.testapp.helper.BaseHelper;
import pt.rocket.testapp.singelton.ServiceSingelton;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Base activity that controls all life cycle actions related to all activities
 * 
 * @author josedourado
 * 
 */
public abstract class BaseActivity extends Activity {
    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private IRemoteService mService;

    protected BaseActivity() {
        super();
        responseCallbacks = new HashMap<String, IResponseCallback>();
    }

    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param responseCallback
     * @return the md5 of the reponse
     */
    public String sendRequest(final BaseHelper helper, final IResponseCallback responseCallback) {
        Bundle bundle = helper.generateRequestBundle();
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        Log.d("TRACK", "sendRequest");
        responseCallbacks.put(md5, new IResponseCallback() {

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

        try {
            mService.sendRequest(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return md5;
    }

    /**
     * Method called then the activity is connected to the service
     */
    protected void onServiceActivation() {

    }

    /**
     * Handles correct responses
     * 
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestComplete(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Handles error responses
     * 
     * @param bundle
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestError(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Callback which deals with the IRemoteServiceCallback
     */
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

        @Override
        public void getError(Bundle response) throws RemoteException {
            Log.i(TAG, "Set target to handle error");
            handleError(response);
        }

        @Override
        public void getResponse(Bundle response) throws RemoteException {
            handleResponse(response);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
    }
    
    

    @Override
    protected void onStart() {
        super.onStart();
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mService = ServiceSingelton.getInstance().getService();
        
        try {
            mService.registerCallback(mCallback);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Callback for the response
     * 
     * @author Guilherme Silva
     * 
     */
    public interface IResponseCallback {
        /**
         * Handles the success request
         * 
         * @param bundle
         */
        public void onRequestComplete(Bundle bundle);

        /**
         * Handles the error request
         * 
         * @param bundle
         */
        public void onRequestError(Bundle bundle);
    }

}
