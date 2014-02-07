package com.rocket.framework.testshell.test;

import java.util.HashMap;

import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.util.Log;

public class FrameworkServiceTests extends ServiceTestCase<RemoteService> {
    private static final String TAG = "FrameworkServiceTestCase";
    private Context mContext;
    protected static boolean contextBound = false;
    protected static boolean bound = false;
    protected static IRemoteService mService;
    public static HashMap<String, IResponseCallback> responseCallbacks;
    private static ServiceConnection mConnection;


    public FrameworkServiceTests() {
        super(RemoteService.class);
        Log.i(TAG, "FrameworkServiceTests Constructor");
    }

    /**
     * Called before each test to ensure that the service is binded
     */
    @Override
    public void setUp() {
        try {
            super.setUp();
            try {
                mContext = getContext().createPackageContext(this.getClass().getPackage().getName(), Context.CONTEXT_IGNORE_SECURITY);
                responseCallbacks = new HashMap<String, IResponseCallback>();
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            final IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

                @Override
                public void getError(Bundle response) throws RemoteException {
                    handleError(response);
                }

                @Override
                public void getResponse(Bundle response) throws RemoteException {
                    handleResponse(response);
                }
            };

            mConnection = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // TODO Auto-generated method stub
                    
                    contextBound = true;
                    mService = IRemoteService.Stub.asInterface(service);
                    try {
                        mService.registerCallback(mCallback);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    bound = true;
                    
                }
            };
            boolean Binded = getContext().bindService(new Intent(getContext(), RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG," service binded => "+Binded);
            assertTrue(Binded);
            // If the context is not bound we need to get it in order to
            // access
            // the rules xml file or json file

            // DO NOT ALLOWS TO PROCEED UNTIL THE SERVICE IS BOUND
            while (!bound) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called after each test to ensure garbage cleaning
     */
    @Override
    public void tearDown() {
        try {
            super.tearDown();
            mContext = null;
            bound = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles correct responses
     * 
     * @param bundle
     */
    private static void handleResponse(Bundle bundle) {
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
    private static void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestError(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param iResponseCallback
     * @return the md5 of the reponse
     */
    public String sendRequest(Bundle args, final BaseHelper helper, final pt.rocket.framework.testproject.interfaces.IResponseCallback iResponseCallback) {
        Bundle bundle = helper.generateRequestBundle(args);
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);

        responseCallbacks.put(md5, new IResponseCallback() {

            @Override
            public void onRequestComplete(Bundle bundle) {
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle, mContext);
                if (iResponseCallback != null) {
                    if ((ErrorCode) formatedBundle.getSerializable(Constants.BUNDLE_ERROR_KEY) == ErrorCode.NO_ERROR) {
                        iResponseCallback.onRequestComplete(formatedBundle);
                    } else {
                        iResponseCallback.onRequestError(formatedBundle);
                    }
                }
            }

            @Override
            public void onRequestError(Bundle bundle) {

                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.parseErrorBundle(bundle);
                if (iResponseCallback != null) {
                    iResponseCallback.onRequestError(formatedBundle);
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

}
