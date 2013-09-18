package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaApplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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

public class SplashScreen extends Activity implements ResponseListener {
    private final static String TAG = LogTagHelper.create(SplashScreen.class);
    private DialogGeneric dialog;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initBugSense();
        Log.d(TAG, "Waiting for the registration process to finish");
        JumiaApplication.INSTANCE.waitForInitResult(this, false);
        showDevInfo();
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
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JumiaApplication.INSTANCE.clearInitListener();
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
            ActivitiesWorkFlow.productsDetailsActivity(SplashScreen.this, productUrl,
                    R.string.gpush_prefix, "");
        } else {
            // Default Start
            Intent intent = new Intent(SplashScreen.this, HomeFragmentActivity.class);
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public void handleEvent(final ResponseEvent event) {
        Log.i(TAG, "Got initialization result: " + event);
        if(dialog!=null && dialog.isShowing()){
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (event.getSuccess()) {
            selectActivity();
            finish();
        } else if (event.errorCode == ErrorCode.REQUIRES_USER_INTERACTION) {
            ActivitiesWorkFlow.changeCountryActivity(SplashScreen.this);
            finish();
        } else if (event.errorCode.isNetworkError()) {
            dialog = DialogGeneric.createNoNetworkDialog(SplashScreen.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreen.this, true);
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
            dialog = DialogGeneric.createServerErrorDialog(message, SplashScreen.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreen.this, true);
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
            dialog = DialogGeneric.createServerErrorDialog(SplashScreen.this,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JumiaApplication.INSTANCE.waitForInitResult(SplashScreen.this, true);
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
}
