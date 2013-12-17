package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.events.GetResolutionsEvent;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.TrackerDelegator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import org.holoeverywhere.widget.TextView;

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

public class SplashScreenActivity extends Activity implements ResponseListener {
    private final static String TAG = LogTagHelper.create(SplashScreenActivity.class);
    private DialogGeneric dialog;
    
    private static boolean shouldHandleEvent = true;

    private String productUrl;
    private String utm;
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getPushNotifications();
        initBugSense();
        Log.d(TAG, "Waiting for the registration process to finish");
        JumiaApplication.INSTANCE.waitForInitResult(this, false);
        showDevInfo();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        shouldHandleEvent = true;
    }
    
    
    @Override
    protected void onStart() {
        super.onStart();
        UAirship.shared().getAnalytics().activityStarted(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        JumiaApplication.INSTANCE.clearInitListener();
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
        if(dialog!=null){
            dialog.dismiss();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JumiaApplication.INSTANCE.clearInitListener();
		cleanIntent(getIntent());
    }
    
    private void cleanIntent(Intent intent){
        Log.d(TAG, "CLEAN NOTIFICATION");
        utm = null;
        productUrl = null;
        // setIntent(null);
        intent.putExtra(ConstantsIntentExtra.UTM_STRING,"");
        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, "");
    }
    
    /**
     * Get values from intent, sent by push notification
     */
    private void getPushNotifications(){
        // ## Google Analytics "General Campaign Measurement" ##
        utm  = getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING);
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
        AnalyticsGoogle.get().setCampaign(getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING));
        // Push Notification Start
        String productUrl = getIntent().getStringExtra(ConstantsIntentExtra.PRODUCT_URL);
        if (productUrl != null && !productUrl.equals("")) {
            // Start home with notification 
            Log.d(TAG, "SHOW NOTIFICATION: PRODUCT DETAILS " + productUrl);
            // ActivitiesWorkFlow.homePageActivity(SplashScreen.this, productUrl, R.string.gpush_prefix, "");
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
        if ( !HockeyStartup.isSplashRequired(getApplicationContext())) {
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
        Log.i(TAG, "UrbanAirShip appid : "+PushManager.shared().getAPID());

    }
    
    private void initBugSense() {
        if ( HockeyStartup.isDevEnvironment(getApplicationContext()))
            return;
        
        try {
            ProxyConfiguration confHttp = ProxySettings.getCurrentHttpProxyConfiguration(this);
            if ( confHttp.isValidConfiguration()) {
                BugSenseHandler.useProxy(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //==================================================================================================
        //TODO: Comment out the BugSenseHandler initialization once we have the correct key.
        //---------------------------------------------------------------------------------------------------
        BugSenseHandler.initAndStartSession(getApplicationContext(), getString( R.string.bugsense_apikey));
        //==================================================================================================
    }
    
    /**
     * TODO: Add this call to initialize
     */
    private void getSupportedImageResolutions(){
        EventManager.getSingleton().addResponseListener(EventType.GET_RESOLUTIONS, this);
        EventManager.getSingleton().triggerRequestEvent( new GetResolutionsEvent() );
    }

    @Override
    public void onUserLeaveHint() {
        shouldHandleEvent = false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public void handleEvent(final ResponseEvent event) {
        if(!shouldHandleEvent){
            return;
        }
        Log.i(TAG, "Got initialization result: " + event);
        if(dialog!=null && dialog.isShowing()){
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (event.getSuccess() && event.getType() == EventType.INITIALIZE) {
            Log.d(TAG, "HANDLE EVENT: " + event.getType().toString());
            
            /*
             * Get image resolutions supported by server
             */
            getSupportedImageResolutions();

            
        } else if (event.getSuccess() && event.getType() == EventType.GET_RESOLUTIONS) {
            Log.d(TAG, "HANDLE EVENT: " + event.getType().toString());
            // Show activity
            selectActivity();
            finish();
            
        } else if (event.errorCode == ErrorCode.REQUIRES_USER_INTERACTION) {
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHANGE_COUNTRY);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
            // Start activity
            startActivity(intent);
            finish();
        } else if (event.errorCode.isNetworkError()) {
            dialog = DialogGeneric.createNoNetworkDialog(SplashScreenActivity.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreenActivity.this, true);
                            dialog.dismiss();
                        }
                    }, true);
            dialog.setCancelable(false);
            try {
                dialog.show();    
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.errorCode == ErrorCode.REQUEST_ERROR && event.errorMessages != null) {
            String message = "";
            List<String> errors = event.errorMessages.get(RestConstants.JSON_ERROR_TAG);
            for (String error : errors) {
                message += "\n" + error;
            }
            message = message.replaceFirst("\n", "");
            dialog = DialogGeneric.createServerErrorDialog(message, SplashScreenActivity.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreenActivity.this, true);
                            dialog.dismiss();
                        }
                    }, true);
            dialog.setCancelable(false);
            try {
                dialog.show();    
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else {
            dialog = DialogGeneric.createServerErrorDialog(SplashScreenActivity.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreenActivity.this, true);
                            dialog.dismiss();
                        }
                    }, true);
            dialog.setCancelable(false);
            try {
                dialog.show();    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#removeAfterHandlingEvent()
     */
    @Override
    public boolean removeAfterHandlingEvent() {
        return true;
    }

    @Override
    public String getMD5Hash() {
        // TODO Auto-generated method stub
        return null;
    }
}
