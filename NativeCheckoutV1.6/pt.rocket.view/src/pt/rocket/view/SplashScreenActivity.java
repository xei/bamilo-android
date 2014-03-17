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
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.AdXTracker;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.DarwinRegex;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetApiInfoHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.preferences.ShopPreferences;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.LocationHelper;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

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
    
    private boolean sendAdxLaunchEvent = false;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.splash_screen);
        JumiaApplication.INSTANCE.init(false, initializationHandler);
        
        // Get values from intent
        getPushNotifications();
        
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
        shouldHandleEvent = true; 
        
        // Adx launch event
        launchEvent();

        
    }
     
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
//        if(isUrbainAirshipInitialized){
//            isUrbainAirshipInitialized = false;
            UAirship.shared().getAnalytics().activityStarted(this);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UAirship.shared().getAnalytics().activityStopped(this);
        SharedPreferences sP = this.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        
        // Adx launch event
        sendAdxLaunchEvent = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);
        // Clean push notifications
        cleanIntent(getIntent());
    }

    Handler initializationHandler = new Handler(){
       public void handleMessage(android.os.Message msg) {
           Bundle bundle = (Bundle) msg.obj;
           ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
           EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
           Log.i(TAG, "received response : "+errorCode + " event type : "+eventType);
           if(eventType == EventType.INITIALIZE){
               initBugSense();
               showDevInfo();
           }
          
           handleSuccessResponse(bundle);
       }; 
    };

    private String mCatalogDeepLink;

    private String mCartDeepLink;

    private void cleanIntent(Intent intent) {
        Log.d(TAG, "CLEAN NOTIFICATION");
        utm = null; 
        productUrl = null;
        mCatalogDeepLink = null;
        mCartDeepLink = null;
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
        // ## Google Analytics "General Campaign Measurement" ##
        utm = getIntent().getStringExtra(ConstantsIntentExtra.UTM_STRING);
        // ## Product URL ##
        productUrl = getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        // ## DEEP LINK ##
        String deepLink = getIntent().getStringExtra(ConstantsIntentExtra.DEEP_LINK_TAG);
        if(!TextUtils.isEmpty(deepLink)){
            // ## Cart URL ##
            mCartDeepLink = (deepLink.contains(DarwinRegex.CART_DEEP_LINK)) ? deepLink : "";
            // ## Catalog URL ##
            mCatalogDeepLink = (deepLink.contains(DarwinRegex.CATALOG_DEEP_LINK)) ? deepLink : "";
        }

    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push
     * notification.
     */
    public void selectActivity() {
        // ## Google Analytics "General Campaign Measurement" ##
        AnalyticsGoogle.get().setCampaign(utm);
        // ## Product URL ##
        if (!TextUtils.isEmpty(productUrl)) {
            // Start with deep link to product detail
            startActivityWithDeepLink(ConstantsIntentExtra.CONTENT_URL, productUrl, FragmentType.PRODUCT_DETAILS);
            
        } else if (!TextUtils.isEmpty(mCatalogDeepLink)) {
            // Receives like this: <country code>/c/<category url key> - ex: ke/c/womens-casual-shoes
            String[] catalogSplit = mCatalogDeepLink.split(DarwinRegex.DL_DELIMITER);
            // 0 - country; 1 - tag; 2 - data;
            String countryCode = catalogSplit[0];
            String tag = catalogSplit[1];
            String catalogUrl = getBaseURL() +  "/" +catalogSplit[2] + "/";
            Log.i(TAG, "DEEP LINK: " + countryCode + " " + tag + " " + catalogUrl);
            // Start with deep link to catalog 
            startActivityWithDeepLink(ConstantsIntentExtra.CONTENT_URL, catalogUrl, FragmentType.PRODUCT_LIST);
            
        } else if (!TextUtils.isEmpty(mCartDeepLink)) {
            // Receives like this: <country code>/cart/<sku1_sku2_sku3> - ex: ke/cart/ or ke/cart/
            String[] cartSplit = mCartDeepLink.split(DarwinRegex.DL_DELIMITER);
            // 0 - country; 1 - tag; 2 - data;
            String countryCode = cartSplit[0];
            String tag = cartSplit[1];
            String simpleSkuArray = (cartSplit.length > 2) ? cartSplit[2] : "";
            Log.i(TAG, "DEEP LINK: " + countryCode + " " + tag + " " + simpleSkuArray);
            // Validate
            if(TextUtils.isEmpty(simpleSkuArray)){
                startActivityWithDeepLink(ConstantsIntentExtra.CONTENT_URL, null, FragmentType.SHOPPING_CART);
            } else {
                startActivityWithDeepLink(ConstantsIntentExtra.CONTENT_URL, simpleSkuArray, FragmentType.HEADLESS_CART);
            }
            
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
        TrackerDelegator.trackPushNotificationsEnabled(getApplicationContext(), true);
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
        }  else if (errorCode == ErrorCode.AUTO_COUNTRY_SELECTION) {
            // Auto country selection
            LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
        }  else if (errorCode == ErrorCode.REQUIRES_USER_INTERACTION) {
            // Show Change country
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHANGE_COUNTRY);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
            // Start activity
            startActivity(intent);
            finish();
        }
    }

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
        ImageView mapBg = (ImageView) findViewById(R.id.home_fallback_country_map);
        SharedPreferences sharedPrefs = getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int position = sharedPrefs.getInt(ChangeCountryFragmentActivity.KEY_COUNTRY, 0);

        mapBg.setImageDrawable(this.getResources().obtainTypedArray(R.array.country_fallback_map)
                .getDrawable(position));

        String country = this.getResources().obtainTypedArray(R.array.country_names)
                .getString(position);
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
            ((TextView) findViewById(R.id.fallback_best)).setTextSize(11.88f);
            TextView txView = (TextView) findViewById(R.id.fallback_options_bottom);
            txView.setVisibility(View.VISIBLE);
            txView.setText(country.toUpperCase());
            findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
            findViewById(R.id.fallback_country).setVisibility(View.GONE);

        }
        findViewById(R.id.fallback_best).setSelected(true);
       
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
        @SuppressWarnings("unchecked")
        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        Log.i(TAG, "codeerror "+errorCode);
        if (errorCode.isNetworkError()) {
            switch (errorCode) {
            case NO_NETWORK:
//                Log.i(TAG, "code1 no network "+eventType);
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
    
    /**
     * Method used to send the adx launch event
     * @author sergiopereira
     */
    private void launchEvent(){
        // Get the current shop id
        int shopId = ShopPreferences.getShopId(getApplicationContext());
        // Validate shop id and launch the Adx event if is the same country on start app
        // First time
        if(JumiaApplication.SHOP_ID_FOR_ADX == -1 && shopId > JumiaApplication.SHOP_ID_FOR_ADX) {
            sendAdxLaunchEvent = true;
        }
        // Current shop is the same
        if(JumiaApplication.SHOP_ID_FOR_ADX == shopId) {
            sendAdxLaunchEvent = true;
        }
        // Save current shop id
        JumiaApplication.SHOP_ID_FOR_ADX = shopId;
        // Send launch
        if(sendAdxLaunchEvent ) {
            AdXTracker.launch(this);
            sendAdxLaunchEvent = false;
        }
    }

}
