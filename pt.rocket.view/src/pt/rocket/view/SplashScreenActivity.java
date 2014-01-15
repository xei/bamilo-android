package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetApiInfoHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
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
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.bugsense.trace.BugSenseHandler;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

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

public class SplashScreenActivity extends FragmentActivity {
    private final static String TAG = LogTagHelper.create(SplashScreenActivity.class);
    private DialogGenericFragment dialog;

    private static boolean shouldHandleEvent = true;

    private String productUrl;
    private String utm;
    private boolean isUrbainAirshipInitialized = false;


    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "code1 onCreate");
        setContentView(R.layout.splash_screen);
        
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "code1 onCreate");
        shouldHandleEvent = true; 
        
        JumiaApplication.INSTANCE.init(false, initializationHandler);
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isUrbainAirshipInitialized)
            UAirship.shared().getAnalytics().activityStarted(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UAirship.shared().getAnalytics().activityStopped(this);
        SharedPreferences sP = this.getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor eD = sP.edit();
        eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        eD.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanIntent(getIntent());
    }

    Handler initializationHandler = new Handler(){
       public void handleMessage(android.os.Message msg) {
           Bundle bundle = (Bundle) msg.obj;
           ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
           EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
           Log.i(TAG, "code1 received response : "+errorCode + " event type : "+eventType);
           if(eventType == null){
               isUrbainAirshipInitialized = true;
               getPushNotifications();
               initBugSense();
               Log.d(TAG, "Waiting for the registration process to finish");
           } else if(eventType == EventType.INITIALIZE){
               showDevInfo();
           }
          
           handleSuccessResponse(bundle);
       }; 
    };

    private void cleanIntent(Intent intent) {
        Log.d(TAG, "CLEAN NOTIFICATION");
        utm = null; 
        productUrl = null;
        // setIntent(null);
        intent.putExtra(ConstantsIntentExtra.UTM_STRING, "");
        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, "");
    }

    /**
     * Get values from intent, sent by push notification
     */
    private void getPushNotifications() {
        // ## Google Analytics "General Campaign Measurement" ##
        utm = getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING);
        // ## Product URL ##
        productUrl = getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        Log.d(TAG, " PRODUCT DETAILS " + productUrl);
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push
     * notification.
     */
    public void selectActivity() {
        // ## Google Analytics "General Campaign Measurement" ##
        AnalyticsGoogle.get().setCampaign(
                getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING));
        // Push Notification Start
        String productUrl = getIntent().getStringExtra(ConstantsIntentExtra.PRODUCT_URL);
        if (productUrl != null && !productUrl.equals("")) {
            // Start home with notification
            Log.d(TAG, "SHOW NOTIFICATION: PRODUCT DETAILS " + productUrl);
            // ActivitiesWorkFlow.homePageActivity(SplashScreen.this, productUrl,
            // R.string.gpush_prefix, "");
            // Create bundle for fragment
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, productUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");

            // Create intent with fragment type and bundle
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.PRODUCT_DETAILS);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
            // Start activity
            startActivity(intent);
            TrackerDelegator.trackPushNotificationsEnabled(getApplicationContext(), true);
        } else {
            // Default Start
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
        finish();
    }

    @SuppressLint("NewApi")
    private void showDevInfo() {
        if (!HockeyStartup.isSplashRequired(getApplicationContext())) {
            return;
        }

        BugSenseHandler.setLogging(true);
        BugSenseHandler.setExceptionCallback(JumiaApplication.INSTANCE);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
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
            zf.close();
            devText.append("\nBuild: "
                    + SimpleDateFormat.getInstance().format(new java.util.Date(ze.getTime())));
            ze = null;
            zf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        devText.append("\nServer: " + RestContract.REQUEST_HOST);
        devText.append("\nUrban AirShip Device APID: \n" + PushManager.shared().getAPID());
        Log.i(TAG, "UrbanAirShip appid : " + PushManager.shared().getAPID());

    }

    private void initBugSense() {
        if (HockeyStartup.isDevEnvironment(getApplicationContext()))
            return;

        try {
            ProxyConfiguration confHttp = ProxySettings.getCurrentHttpProxyConfiguration(this);
            if (confHttp.isValidConfiguration()) {
                BugSenseHandler.useProxy(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BugSenseHandler.initAndStartSession(getApplicationContext(),
                getString(R.string.bugsense_apikey));
    }

    @Override
    public void onUserLeaveHint() {
        shouldHandleEvent = false;
    }

    /**
     * Handles correct response
     * 
     * @param bundle
     */
    private void handleSuccessResponse(Bundle bundle) {
        Log.i(TAG,"on handleSuccessResponse");
        if (!shouldHandleEvent) {
            return;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (dialog != null && dialog.isVisible()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (eventType == EventType.INITIALIZE) {
            Log.d(TAG, "HANDLE EVENT: " + eventType.toString());
            /**
             * Get Api info
             */
            JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
            JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, responseCallback);

        } else if (eventType == EventType.GET_API_INFO) {
            Log.d(TAG, "HANDLE EVENT: " + eventType.toString());
            // Show activity
            selectActivity(); 
            finish();
        }  else if (errorCode == ErrorCode.REQUIRES_USER_INTERACTION) {

            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHANGE_COUNTRY);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
            // Start activity
            startActivity(intent);
            finish();
        }
    }

    
    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            handleErrorResponse(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            handleSuccessResponse(bundle);
        }
    };
    

    /**
     * Handles error responses
     * 
     * @param bundle
     */
    private void handleErrorResponse(Bundle bundle) {
        Log.i(TAG, "codeerror");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        Log.i(TAG, "codeerror "+errorCode);
        if (errorCode.isNetworkError()) {
            switch (errorCode) {
            case NO_NETWORK:
                Log.i(TAG, "code1 no network "+eventType);
                // Remove dialog if exist
                if (dialog != null){
                    try {
                        
                        
                        dialog.dismiss();    
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                dialog = DialogGenericFragment.createNoNetworkDialog(SplashScreenActivity.this,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                /**
                                 * Re-send initialize event result to get Api Info
                                 */
                                Bundle args = new Bundle();
                                args.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INITIALIZE);
                                handleSuccessResponse(args);
                                dialog.dismiss();
                            }
                        }, true);
                try {
                    dialog.show(getSupportFragmentManager(), null);
                    dialog.setCancelable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SERVER_IN_MAINTENANCE:
                // TODO implement fallback when server is in maintenance
                break;
            case REQUEST_ERROR:
                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                String dialogMsg = "";
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                }
                if (validateMessages != null) {
                    for (String message : validateMessages) {
                        dialogMsg += message + "\n";
                    }
                } else {
                    for (Entry<String, ? extends List<String>> entry : errorMessages.entrySet()) {
                        dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    }
                }
                if (dialogMsg.equals("")) {
                    dialogMsg = getString(R.string.validation_errortext);
                }
                dialog = DialogGenericFragment.newInstance(
                        true, true, false, getString(R.string.validation_title),
                        dialogMsg, getResources().getString(R.string.ok_label), "",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dialog.dismiss();
                                }

                            }

                        });

                dialog.show(getSupportFragmentManager(), null);
            }
        }
    }
    

    /**
     * Requests and Callbacks methods
     */
    
    /**
     * Callback which deals with the IRemoteServiceCallback
     */
    IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

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
    
    /**
     * Sends error responses to the target callback
     * 
     * @param bundle
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestError(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);

    }
    
    /**
     * Sends the correct responses to be handled by the target callback
     * 
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestComplete(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);

    }

}
