package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.BundleConstants;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.tracking.AdXTracker;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.configs.GetApiInfoHelper;
import pt.rocket.helpers.configs.GetCountriesConfigsHelper;
import pt.rocket.helpers.configs.GetCountriesGeneralConfigsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.DeepLinkManager;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.LocationHelper;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.imageloader.RocketImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bugsense.trace.BugSenseHandler;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

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
 * @modified Manuel Silva
 * 
 * @date 25/04/2013
 * 
 * @description
 * 
 */

public class SplashScreenActivity extends FragmentActivity implements IResponseCallback {
    
    private final static String TAG = LogTagHelper.create(SplashScreenActivity.class);
    
    private DialogGenericFragment dialog;

    private static boolean shouldHandleEvent = true;

    private String productUrl;
    
    private String utm;
    
    private boolean sendAdxLaunchEvent = false;

    private long mLaunchTime;

    private Bundle mDeepLinkBundle;

    private boolean isDeepLinkLaunch = false;

    SharedPreferences sharedPrefs;
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        
        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        Uri data = mIntent.getData();
        
        Log.i(TAG, "ON CREATE - Bundle -> " + (null != mBundle ? mBundle.keySet().toString() : "null"));
        Log.i(TAG, "ON CREATE - data -> " + data);
        
        // Set view
        setContentView(R.layout.splash_screen);
        // Get prefs
        sharedPrefs = getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Keep launch time to compare with newer timestamp later
        mLaunchTime = System.currentTimeMillis();
        // Get values from intent
        getPushNotifications();
        // Initialize application
        JumiaApplication.INSTANCE.init(false, initializationHandler);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
//        UAirship.shared().getAnalytics().activityStarted(this);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // 
        shouldHandleEvent = true; 
        // Adx launch event
        launchEvent();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        // Validate dialog
        if (dialog != null) dialog.dismiss();
        // Adx launch event
        sendAdxLaunchEvent = false;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
//        UAirship.shared().getAnalytics().activityStopped(this);
        SharedPreferences.Editor eD = sharedPrefs.edit();
        eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        eD.commit();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);
        // Clean push notifications
        cleanIntent(getIntent());
    }
    

    Handler initializationHandler = new Handler(){
       public void handleMessage(android.os.Message msg) {
           Bundle bundle = (Bundle) msg.obj;
           ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
           EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
           
           Log.i(TAG, "code1configs received response : "+errorCode + " event type : "+eventType);
           if(eventType == EventType.INITIALIZE){
               initBugSense();
               showDevInfo();
           }
          
           onRequestComplete(bundle);
       }; 
    };



    private void cleanIntent(Intent intent) {
        Log.d(TAG, "CLEAN NOTIFICATION");
        utm = null; 
        productUrl = null;
        mDeepLinkBundle = null;
        // setIntent(null);
        intent.putExtra(ConstantsIntentExtra.UTM_STRING, "");
        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, "");
        intent.putExtra(ConstantsIntentExtra.DEEP_LINK_TAG, "");
        intent.putExtra(ConstantsIntentExtra.CART_DEEP_LINK_TAG, "");
    }

    /**
     * Get values from intent, sent by push notification
     */
    private void getPushNotifications() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        Log.d(TAG, "DEEP LINK RECEIVED INTENT: " + intent.toString());
        // Get intent action ACTION_VIEW
        String action = intent.getAction();
        // Get intent data 
        Uri data = intent.getData();
        // ## DEEP LINK FROM EXTERNAL URIs ##
        if(!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW) && data != null) {
            mDeepLinkBundle = DeepLinkManager.loadExternalDeepLink(getApplicationContext(), data);
            isDeepLinkLaunch = true;
        } else {
            isDeepLinkLaunch = false;
            Log.i(TAG, "DEEP LINK: NO EXTERNAL URI");
        }
        
        // ## DEEP LINK FROM UA ##
        Bundle payload = getIntent().getBundleExtra(BundleConstants.EXTRA_GCM_PAYLOAD);
        String deepLink = "";
        if (null != payload) {
            deepLink = payload.getString(BundleConstants.DEEPLINKING_PAGE_INDICATION);
        }
        if(!TextUtils.isEmpty(deepLink)){
            isDeepLinkLaunch = true;
            // Create uri from the value 
            Uri uri = Uri.parse(deepLink);
            Log.d(TAG, "DEEP LINK URI: " +uri.toString() + " " + uri.getPathSegments().toString());
            // Load deep link
            mDeepLinkBundle = DeepLinkManager.loadExternalDeepLink(getApplicationContext(), uri);
        } else {
            Log.i(TAG, "DEEP LINK: NO UA TAG");
            isDeepLinkLaunch = false;
        }
        // ## Google Analytics "General Campaign Measurement" ##
        utm = getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING);
        // ## Product URL ##
        productUrl = getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_URL);
    }
    
    /**
     * Starts the Activity depending whether the app is started by the user, or by the push
     * notification.
     */
    public void selectActivity() {
        // ## Google Analytics "General Campaign Measurement" ##
        TrackerDelegator.trackCampaign(utm);
        // ## Product URL ##
        if (!TextUtils.isEmpty(productUrl)) {
            // Start with deep link to product detail
            startActivityWithDeepLink(ConstantsIntentExtra.CONTENT_URL, productUrl, FragmentType.PRODUCT_DETAILS);
        // ## Deep link via URIs
        } else if (mDeepLinkBundle != null) {
                startDeepView(mDeepLinkBundle);
        } else {
            // Default Start
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
        finish();
    }
    
    /**
     * Get the base URL
     * @return String
     */
    @SuppressWarnings("unused")
    private String getBaseURL(){
        return RestContract.HTTPS_PROTOCOL + "://" + RestContract.REQUEST_HOST + "/" + RestContract.REST_BASE_PATH;
    }

    /**
     * Start the main activity with a deep link
     * @param key the deep link key
     * @param value the deep link value
     * @param receiverType the fragment to receive the deep link
     * @author sergiopereira
     */
    private void startActivityWithDeepLink(String key, String value, FragmentType receiverType) {
        Log.d(TAG, "START DEEP LINK: KEY:" + key + " VALUE:" + value + " RECEIVER:" + receiverType.toString());
        // Create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        // Create intent with fragment type and bundle
        Intent intent = new Intent(this, MainFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, receiverType);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
        // Start activity
        startActivity(intent);
        TrackerDelegator.trackPushNotificationsEnabled(true);
    }
    
    /**
     * Start deep view with the respective bundle and set the ADX event 
     * @param bundle
     * @author sergiopereira
     */
    private void startDeepView(Bundle bundle) {
        // Get fragment type
        FragmentType fragmentType = (FragmentType) bundle.getSerializable(DeepLinkManager.FRAGMENT_TYPE_TAG);
        Log.d(TAG, "DEEP LINK FRAGMENT TYPE: " + fragmentType.toString());
        // Get and add Adx value
        String adxValue = bundle.getString(DeepLinkManager.ADX_ID_TAG);
        Log.d(TAG, "DEEP LINK ADX VALUE: " + adxValue);
        AdXTracker.deepLinkLaunch(getApplicationContext(), adxValue);
        // Default Start
        Intent intent = new Intent(this, MainFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Validate fragment type
        if(fragmentType != FragmentType.HOME || fragmentType != FragmentType.UNKNOWN) {
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, fragmentType);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
        }
        // Start activity
        startActivity(intent);
        TrackerDelegator.trackPushNotificationsEnabled(true);
    }
    
    /**
     * 
     */
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
            devText.append("\nInstallation: " + SimpleDateFormat.getInstance().format(new java.util.Date(pInfo.firstInstallTime)));
            devText.append("\nUpdate: " + SimpleDateFormat.getInstance().format(new java.util.Date(pInfo.lastUpdateTime)));
        }

        try {
            ZipFile zf = new ZipFile(getApplicationInfo().sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            zf.close();
            devText.append("\nBuild: " + SimpleDateFormat.getInstance().format(new java.util.Date(ze.getTime())));
            ze = null;
            zf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        devText.append("\nServer: " + RestContract.REQUEST_HOST);
//        devText.append("\nUrban AirShip Device APID: \n" + PushManager.shared().getAPID());
//        Log.i(TAG, "UrbanAirShip appid : " + PushManager.shared().getAPID());
        // Device info
        devText.append("\nDevice Model: " + android.os.Build.MODEL);
        devText.append("\nDevice Manufacturer: " + android.os.Build.MANUFACTURER);
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
     * ######## RESPONSES ######## 
     */
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG,"ON SUCCESS RESPONSE");
        if (!shouldHandleEvent) {
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        
        Log.i(TAG, "code1configs : handleSuccessResponse : "+eventType+" errorcode : "+errorCode);
        
        if (dialog != null && dialog.isVisible()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Case event
        if (eventType == EventType.INITIALIZE) onProcessInitialize();
        else if (eventType == EventType.GET_API_INFO) onProcessApiEvent(bundle);
        else if(eventType == EventType.GET_COUNTRY_CONFIGURATIONS) onProcessCountryConfigsEvent();
        else if(eventType == EventType.GET_GLOBAL_CONFIGURATIONS) onProcessGlobalConfigsEvent(bundle);
        // Case error
        else if (errorCode == ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE) onProcessNoCountryConfigsError();
        else if (errorCode == ErrorCode.NO_COUNTRIES_CONFIGS) onProcessNoCountriesConfigsError();
        else if (errorCode == ErrorCode.AUTO_COUNTRY_SELECTION) onProcessAutoCountrySelection();
        else if (errorCode == ErrorCode.REQUIRES_USER_INTERACTION) onProcessRequiresUserError();
    }
    
    /**
     * Proccess the initialize event
     * @author sergiopereira
     */
    private void onProcessInitialize(){
        Log.i(TAG, "ON PROCESS: INITIALIZE");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, (IResponseCallback) this);
    }
    
    /**
     * Proccess the global configs event
     * @param bundle
     * @author sergiopereira
     */
    private void onProcessGlobalConfigsEvent(Bundle bundle){
        Log.i(TAG, "ON PROCESS: GLOBAL CONFIGS");
        
        if(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null) == null) {
            Log.i(TAG, "SELECETD COUNTRY ID IS NULL");
            if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0){
                LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
            } else {
                onRequestError(bundle);
            }
            
        } else {
            Log.i(TAG, "SELECETD COUNTRY ID IS NOT NULL");
            if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0){
                JumiaApplication.INSTANCE.init(false, initializationHandler);
            } else {
                onRequestError(bundle);
            }
        }
    }
    
    /**
     * Proccess the country configs event
     * @param bundle
     */
    private void onProcessCountryConfigsEvent(){
        Log.i(TAG, "ON PROCESS COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.init(false, initializationHandler);
    }
    
    /**
     * Proccess the no country configs error
     * @param bundle
     * @author sergiopereira
     */
    private void onProcessNoCountryConfigsError(){
        Log.i(TAG, "ON PROCESS NO COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountriesConfigsHelper(), null, (IResponseCallback) this);
    }
    
    /**
     * Proccess the no countries configs error
     * @author sergiopereira
     */
    private void onProcessNoCountriesConfigsError(){
        Log.i(TAG, "ON PROCESS NO COUNTRIES CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountriesGeneralConfigsHelper() , null, (IResponseCallback) this);
    }
    
    /**
     * Proccess the auto country selection
     * @author sergiopereira
     */
    private void onProcessAutoCountrySelection(){
        Log.i(TAG, "ON PROCESS AUTO_COUNTRY_SELECTION");
        LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
    }
    
    /**
     * Proccess the requires user interaction
     * @author sergiopereira
     */
    private void onProcessRequiresUserError(){
        Log.i(TAG, "ON PROCESS REQUIRES USER INTERACTION");
        // Show Change country
        Intent intent = new Intent(this, MainFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHANGE_COUNTRY);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
        // Start activity
        startActivity(intent);
        finish();
    }
    
    /**
     * Proccess the api md5 event
     * @param bundle
     */
    private void onProcessApiEvent(Bundle bundle) {
        Log.d(TAG, "ON PROCESS API EVENT");
        //bundle.getParcelableArrayList(GetApiInfoHelper.API_INFO_OUTDATEDSECTIONS);
        // Validate out dated sections
        if(bundle.getBoolean(Section.SECTION_NAME_COUNTRY_CONFIGS, false)) {
            Log.d(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
            JumiaApplication.INSTANCE.sendRequest(new GetCountriesConfigsHelper(), null, (IResponseCallback) this);
        } else {
            Log.d(TAG, "START MAIN ACTIVITY");
            // Show activity
            selectActivity();
            finish();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG,"ON ERROR RESPONSE");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        @SuppressWarnings("unchecked")
        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        Log.i(TAG, "codeerror " + errorCode);

        if (errorCode.isNetworkError()) {
            switch (errorCode) {
            case CONNECT_ERROR:
            case TIME_OUT:
            case HTTP_STATUS:
            case NO_NETWORK:
                // Log.i(TAG, "code1 no network "+eventType);
                // Remove dialog if exist
                if (dialog != null) {
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
                                // Re-send initialize event
                                JumiaApplication.INSTANCE.init(false, initializationHandler);
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
                setLayoutMaintenance(eventType);
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
        } else if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) {
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                Log.i(TAG, "code1configs received response correctly!!!");
                // Auto country selection
                LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
            } else {
                setLayoutMaintenance(eventType);
            }
        }
    }
    
     
     
     /**
      * ########### MAINTENANCE ###########
      */
     
     private void setLayoutMaintenance(final EventType eventType) {
         
         findViewById(R.id.fallback_content).setVisibility(View.VISIBLE);
         Button retry = (Button) findViewById(R.id.fallback_retry);
         retry.setOnClickListener(new OnClickListener() {
             
             @Override
             public void onClick(View v) {
                 findViewById(R.id.fallback_content).setVisibility(View.GONE);
                JumiaApplication.INSTANCE.sendRequest(JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(eventType), 
                        JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(eventType), JumiaApplication.INSTANCE.getRequestsResponseList().get(eventType)); 
             }
         });
         
         ImageView mapBg = (ImageView) findViewById(R.id.fallback_country_map);
         RocketImageLoader.instance.loadImage(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""), mapBg);

         String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME,"");
         TextView fallbackBest = (TextView) findViewById(R.id.fallback_best);
         fallbackBest.setText(R.string.fallback_best);
         if (country.split(" ").length == 1) {
             TextView tView = (TextView) findViewById(R.id.fallback_country);
             tView.setVisibility(View.VISIBLE);
             TextView txView = (TextView) findViewById(R.id.fallback_options_bottom);
             txView.setVisibility(View.VISIBLE);
             txView.setText(country.toUpperCase());
             findViewById(R.id.fallback_country_double).setVisibility(View.GONE);
             tView.setText(country.toUpperCase());
         } else {
             TextView tView = (TextView) findViewById(R.id.fallback_country_top);
             tView.setText(country.split(" ")[0].toUpperCase());
             TextView tViewBottom = (TextView) findViewById(R.id.fallback_country_bottom);
             tViewBottom.setText(country.split(" ")[1].toUpperCase());
             fallbackBest.setTextSize(11.88f);
             TextView txView = (TextView) findViewById(R.id.fallback_options_bottom);
             txView.setVisibility(View.VISIBLE);
             txView.setText(country.toUpperCase());
             findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
             findViewById(R.id.fallback_country).setVisibility(View.GONE);

         }
         
         TextView mTextViewBT = (TextView) findViewById(R.id.fallback_country_bottom_text);
         mTextViewBT.setText(R.string.fallback_maintenance_text);
         
         TextView mTextViewBT2 = (TextView) findViewById(R.id.fallback_country_bottom_text2);
         mTextViewBT2.setText(R.string.fallback_maintenance_text_bottom);
         
         TextView mFallbackChoice = (TextView) findViewById(R.id.fallback_choice);
         mFallbackChoice.setText(R.string.fallback_choice);
         
         TextView mFallbackDoorstep = (TextView) findViewById(R.id.fallback_doorstep);
         mFallbackDoorstep.setText(R.string.fallback_doorstep);
         
         fallbackBest.setSelected(true);
        
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
    
    /**
     * Method used to send the adx launch event
     * @author sergiopereira
     */
    private void launchEvent(){
        SharedPreferences sharedPrefs = this.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Get the current shop id
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        // Validate shop id and launch the Adx event if is the same country on start app
        // First time
        if(JumiaApplication.SHOP_ID_FOR_ADX == null && shopId != JumiaApplication.SHOP_ID_FOR_ADX) {
            //Log.i(TAG, "ON LAUNCH EVENT: FIRST TIME");
            sendAdxLaunchEvent = true;
        }
        // Current shop is the same
        if(JumiaApplication.SHOP_ID_FOR_ADX != null && JumiaApplication.SHOP_ID_FOR_ADX.equalsIgnoreCase(shopId)) {
            //Log.i(TAG, "ON LAUNCH EVENT: SHOP IS SAME");
            sendAdxLaunchEvent = true;
        }
        
        //Log.i(TAG, "ON LAUNCH EVENT: " + JumiaApplication.SHOP_ID_FOR_ADX + " " + shopId);
        
        // Save current shop id
        JumiaApplication.SHOP_ID_FOR_ADX = shopId;
        
        // Validate deep link launch
        if(isDeepLinkLaunch){
            isDeepLinkLaunch = false;
            sendAdxLaunchEvent = true;
        }
        
        // Send launch
        if(sendAdxLaunchEvent) {
            generateAndPerformAdxTrack();
            sendAdxLaunchEvent = false;
        }
        
        TrackerDelegator.trackAppOpen();
    }

    /**
     * Generate and trigget the adx tracker for splash screen
     * @author sergiopereira
     */
    private void generateAndPerformAdxTrack(){
	    String duration = "";
        // Validate launch time
        if(mLaunchTime != 0) duration = "" + (System.currentTimeMillis() - mLaunchTime);
        // Reset the launch time to identify the launch was handled
        mLaunchTime = 0;
        // Trigger
        AdXTracker.launch(this, duration);
    }

}
