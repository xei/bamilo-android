package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.helpers.GetLoginHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.ServiceSingleton;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

/**
 * <p>
 * This class creates a splash screen. It also initializes hockey and the backend
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author Michael Kröz
 * 
 * @date 25/04/2013
 * 
 * @description
 * 
 */

public class SplashScreen extends Activity {
    private final static String TAG = "";
    private DialogGeneric dialog;
    private String utm;
    private String productUrl;
    Context context;
    Boolean serviceBound = false;
    private IRemoteService mService;
    public static HashMap<String, IResponseCallback> responseCallbacks;
    private static final int SHOP_NOT_SELECTED = -1;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mService = ServiceSingleton.getInstance().getService();
            if (mService == null) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                try {
                    responseCallbacks = ServiceSingleton.getInstance().getResponseCallbacks();
                    mService.registerCallback(mCallback);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startRequest();
            }
        }
    };
    
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
    public static void handleError(Bundle bundle) {
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
            Log.i("d", "Set target to handle error");
            handleError(response);
        }

        @Override
        public void getResponse(Bundle response) throws RemoteException {
            handleResponse(response);
        }
    };

    private void startRequest() {
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

                int selected = sharedPrefs.getInt(ChangeCountryActivity.KEY_COUNTRY, SHOP_NOT_SELECTED);
                if (selected > SHOP_NOT_SELECTED) {
                    ActivitiesWorkFlow.homePageActivity(this);                   
                }else{
                    ActivitiesWorkFlow.changeCountryActivity(this);
                }
                

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        context = getApplicationContext();
        mHandler.sendEmptyMessage(0);

        Log.i(TAG, "SERVICE IS => " + mService);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Waiting for the registration process to finish");

        // MyActivity.sendRequest(new GetLoginHelper(), new MyActivity.IResponseCallback() {
        //
        // @Override
        // public void onRequestError(Bundle bundle) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void onRequestComplete(Bundle bundle) {
        // // TODO Auto-generated method stub
        // Intent intent = new Intent(SplashScreen.this, ChangeCountryActivity.class);
        // startActivity(intent);
        // }
        // });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ON DESTROY");
        // Clean push notifications
        Log.i(TAG, "Unbind Service called");

        cleanIntent(getIntent());
    }

    /**
     * Get values from intent, sent by push notification
     */
    private void getPushNotifications() {
        // ## Google Analytics "General Campaign Measurement" ##
        utm = getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING);
        Log.i("RECEIVED PUSH NOTIFICATION", " HERE " + utm);
        // ## Product URL ##
        productUrl = getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_URL);
    }

    private void cleanIntent(Intent intent) {
        Log.d(TAG, "CLEAN NOTIFICATION");
        utm = null;
        productUrl = null;
        intent.putExtra(ConstantsIntentExtra.UTM_STRING, "");
        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, "");
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push
     * notification.
     */
    public void selectActivity() {
        // Push Notification Start
        // ## Google Analytics "General Campaign Measurement" ##
        Log.i("RECEIVED PUSH NOTIFICATION", " HERE " + utm);

        Log.i("RECEIVED PUSH NOTIFICATION", " HERE " + productUrl);
        if (productUrl != null && !productUrl.equals("")) {
            // ActivitiesWorkFlow.productsDetailsActivity(SplashScreen.this, productUrl,
            // R.string.gpush_prefix, "");
        } else {
            // Default Start
            Intent intent = new Intent(this, TeaserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
    }

    @SuppressLint("NewApi")
    private void showDevInfo() {
        if (!getResources().getBoolean(R.bool.dev_env)) {
            return;
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return;
        }
        TextView devText = (TextView) findViewById(R.id.dev_text);
        devText.append("Name: " + getString(getApplicationInfo().labelRes));
        devText.append("\nPackage: " + getPackageName());

        devText.append("\nVersion Name: " + pInfo.versionName);
        devText.append("\nVersion Code: " + pInfo.versionCode);
        if (Build.VERSION.SDK_INT >= 9) {
            devText.append("\nInstallation: "
                    + SimpleDateFormat.getInstance().format(
                            new java.util.Date(pInfo.firstInstallTime)));
            devText.append("\nUpdate: "
                    + SimpleDateFormat.getInstance().format(
                            new java.util.Date(pInfo.lastUpdateTime)));
        }

        try {
            ZipFile zf = new ZipFile(getApplicationInfo().sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            devText.append("\nBuild: "
                    + SimpleDateFormat.getInstance().format(new java.util.Date(ze.getTime())));
        } catch (Exception e) {
        }
        devText.append("\nServer: " + RestContract.REQUEST_HOST);

    }

}
