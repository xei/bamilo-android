package pt.rocket.view;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.HoloFontLoader;
import pt.rocket.components.customfontviews.TextView;
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
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.configs.GetApiInfoHelper;
import pt.rocket.helpers.configs.GetCountriesGeneralConfigsHelper;
import pt.rocket.helpers.configs.GetCountryConfigsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.deeplink.DeepLinkManager;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.location.LocationHelper;
import pt.rocket.utils.maintenance.MaintenancePage;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class creates a splash screen. It also initializes hockey and the
 * backend
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author Michael Kröz
 * @modified Sergio Pereira
 * 
 * @date 25/04/2013
 * 
 * @description
 * 
 */

public class SplashScreenActivity extends FragmentActivity implements IResponseCallback, OnClickListener {

    private final static String TAG = LogTagHelper.create(SplashScreenActivity.class);

    private DialogGenericFragment dialog;

    private static boolean shouldHandleEvent = true;

    private String mUtm;

    private Bundle mDeepLinkBundle;

    private boolean isDeepLinkLaunch = false;

    private View jumiaMapImage;

    SharedPreferences sharedPrefs;

    private View mMainFallBackStub;
    
    private View mRetryFallBackStub;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        //set Font
        if(getApplicationContext().getResources().getBoolean(R.bool.is_shop_specific)){
            HoloFontLoader.initFont(true);
        } else {
            HoloFontLoader.initFont(false);
        }
        // Validate if is phone and force orientation
        setOrientationForHandsetDevices();
        // Set view
        setContentView(R.layout.splash_screen);
        // Get fall back layout
        mMainFallBackStub = findViewById(R.id.splash_screen_maintenance_stub);
        // Get retry layout
        mRetryFallBackStub = findViewById(R.id.splash_fragment_retry_stub);
        // Get prefs
        sharedPrefs = getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Get values from intent
        getDeepLinkView();
        // Tracking
        AdjustTracker.onResume(this);
        // Initialize application
        JumiaApplication.INSTANCE.init(false, initializationHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        //
        shouldHandleEvent = true;
        
        jumiaMapImage = findViewById(R.id.jumiaMap);
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animationFadeIn.setDuration(1250);
        jumiaMapImage.startAnimation(animationFadeIn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        // Validate dialog
        if (dialog != null)
            dialog.dismiss();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        SharedPreferences.Editor eD = sharedPrefs.edit();
        eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        eD.commit();
    }

    /*
     * (non-Javadoc)
     * 
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
    

    Handler initializationHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = (Bundle) msg.obj;
            ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
            EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

            Log.i(TAG, "code1configs received response : " + errorCode + " event type : " + eventType);
            if (eventType == EventType.INITIALIZE) {
                showDevInfo();
            }

            onRequestComplete(bundle);
        };
    };

    private void cleanIntent(Intent intent) {
        Log.d(TAG, "CLEAN NOTIFICATION");
        mUtm = null;
        mDeepLinkBundle = null;
        intent.putExtra(ConstantsIntentExtra.UTM_STRING, "");
        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, "");
        intent.putExtra(ConstantsIntentExtra.DEEP_LINK_TAG, "");
        intent.putExtra(ConstantsIntentExtra.CART_DEEP_LINK_TAG, "");
    }

    /**
     * Get values from intent, sent by push notification
     */
    private void getDeepLinkView() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        Log.d(TAG, "DEEP LINK RECEIVED INTENT: " + intent.toString());
        // ## DEEP LINK FROM EXTERNAL URIs ##
        if (hasDeepLinkFromURI(intent));
        // ## DEEP LINK FROM NOTIFICATION ##
        else hasDeepLinkFromGCM(intent);
    }
    
    /**
     * Validate deep link from External URI.
     * @param intent
     * @return true or false
     * @author sergiopereira
     */
    private boolean hasDeepLinkFromURI(Intent intent) {
        // Get intent action ACTION_VIEW
        String action = intent.getAction();
        // Get intent data
        Uri data = intent.getData();
        // ## DEEP LINK FROM EXTERNAL URIs ##
        if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW) && data != null) {
            mDeepLinkBundle = DeepLinkManager.loadExternalDeepLink(getApplicationContext(), data);
            return isDeepLinkLaunch = true;
        }
        Log.i(TAG, "DEEP LINK: NO EXTERNAL URI");
        return isDeepLinkLaunch = false; 
    }
    
    /**
     * Validate deep link from Push Notification.
     * @param intent
     * @return true or false
     * @author sergiopereira
     */
    private boolean hasDeepLinkFromGCM(Intent intent) {
        // ## DEEP LINK FROM NOTIFICATION ##
        Bundle payload = intent.getBundleExtra(BundleConstants.EXTRA_GCM_PAYLOAD);
        // Get Deep link
        if (null != payload) {
            // Get UTM
            mUtm = payload.getString(ConstantsIntentExtra.UTM_STRING);
            Log.i(TAG, "UTM FROM GCM: " + mUtm);
            // Get value from deep link key
            String deepLink = payload.getString(BundleConstants.DEEPLINKING_PAGE_INDICATION);
            Log.i(TAG, "DEEP LINK: GCM " + deepLink);
            // Validate deep link
            if (!TextUtils.isEmpty(deepLink)) {
                // Create uri from the value
                Uri data = Uri.parse(deepLink);
                Log.d(TAG, "DEEP LINK URI: " + data.toString() + " " + data.getPathSegments().toString());
                // Load deep link
                mDeepLinkBundle = DeepLinkManager.loadExternalDeepLink(getApplicationContext(), data);
                return isDeepLinkLaunch = true;
            }
        }
        Log.i(TAG, "DEEP LINK: NO GCM TAG");
        return isDeepLinkLaunch = false;
    }
    
    
    /**
     * Get the main class for mobile(Portrait) or tablet(Unspecified).
     * @return Class
     * @author sergiopereira
     */
    private Class<?> getActivityClassForDevice() {
        if (!getResources().getBoolean(R.bool.isTablet)) return MainFragmentActivity.class;
        else return MainFragmentTabletActivity.class;
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or
     * by the push notification.
     */
    public void selectActivity() {
        jumiaMapImage = findViewById(R.id.jumiaMap);
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animationFadeOut.setDuration(750);
        animationFadeOut.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                jumiaMapImage.setVisibility(View.GONE);
                // Validate deep link bundle    
                if (mDeepLinkBundle != null) {
                    startActivityWithDeepLink(mDeepLinkBundle);
                } else {
                    starMainActivity();
                }
                overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
                finish();
            }
        });
        jumiaMapImage.startAnimation(animationFadeOut);
    }
    
    /**
     * Start main fragment activity
     * @author sergiopereira
     */
    private void starMainActivity() {
        Log.d(TAG, "START MAIN FRAGMENT ACTIVITY");
        // Default Start
        Intent intent = new Intent(getApplicationContext(), getActivityClassForDevice());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    /**
     * Start deep view with the respective bundle and set the ADX event
     * 
     * @param bundle
     * @author sergiopereira
     */
    private void startActivityWithDeepLink(Bundle bundle) {
        // Get fragment type
        FragmentType fragmentType = (FragmentType) bundle.getSerializable(DeepLinkManager.FRAGMENT_TYPE_TAG);
        Log.d(TAG, "DEEP LINK FRAGMENT TYPE: " + fragmentType.toString());
        // Default Start
        Intent intent = new Intent(getApplicationContext(), getActivityClassForDevice());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Validate fragment type
        if (fragmentType != FragmentType.HOME || fragmentType != FragmentType.UNKNOWN) {
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, fragmentType);
            intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
        }
        // Start activity
        startActivity(intent);
    }

    /**
     * 
     */
    @SuppressLint("NewApi")
    private void showDevInfo() {
        if (!HockeyStartup.isSplashRequired(getApplicationContext())) {
            return;
        }

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
        // Device info
        devText.append("\nDevice Model: " + android.os.Build.MODEL);
        devText.append("\nDevice Manufacturer: " + android.os.Build.MANUFACTURER);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onUserLeaveHint()
     */
    @Override
    public void onUserLeaveHint() {
        shouldHandleEvent = false;
    }

    /**
     * ######## RESPONSES ########
     */

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle
     * )
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS RESPONSE");
        if (!shouldHandleEvent) {
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        Log.i(TAG, "code1configs : handleSuccessResponse : " + eventType + " errorcode : " + errorCode);

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
        else if (eventType == EventType.GET_COUNTRY_CONFIGURATIONS) onProcessCountryConfigsEvent();
        else if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) onProcessGlobalConfigsEvent(bundle);
        // Case error
        else if (errorCode == ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE) onProcessNoCountryConfigsError();
        else if (errorCode == ErrorCode.NO_COUNTRIES_CONFIGS) onProcessNoCountriesConfigsError();
        else if (errorCode == ErrorCode.AUTO_COUNTRY_SELECTION) onProcessAutoCountrySelection();
        else if (errorCode == ErrorCode.REQUIRES_USER_INTERACTION) onProcessRequiresUserError();
    }

    /**
     * Proccess the initialize event
     * 
     * @author sergiopereira
     */
    private void onProcessInitialize() {
        Log.i(TAG, "ON PROCESS: INITIALIZE");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, (IResponseCallback) this);
    }

    /**
     * Proccess the global configs event
     * 
     * @param bundle
     * @author sergiopereira
     */
    private void onProcessGlobalConfigsEvent(Bundle bundle) {
        Log.i(TAG, "ON PROCESS: GLOBAL CONFIGS");

        if (sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null) == null) {
            Log.i(TAG, "SELECETD COUNTRY ID IS NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
            } else {
                onRequestError(bundle);
            }

        } else {
            Log.i(TAG, "SELECETD COUNTRY ID IS NOT NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                JumiaApplication.INSTANCE.init(false, initializationHandler);
            } else {
                onRequestError(bundle);
            }
        }
    }

    /**
     * Proccess the country configs event
     * 
     * @param bundle
     */
    private void onProcessCountryConfigsEvent() {
        Log.i(TAG, "ON PROCESS COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.init(false, initializationHandler);
    }

    /**
     * Proccess the no country configs error
     * 
     * @param bundle
     * @author sergiopereira
     */
    private void onProcessNoCountryConfigsError() {
        Log.i(TAG, "ON PROCESS NO COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, (IResponseCallback) this);
    }

    /**
     * Proccess the no countries configs error
     * 
     * @author sergiopereira
     */
    private void onProcessNoCountriesConfigsError() {
        Log.i(TAG, "ON PROCESS NO COUNTRIES CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountriesGeneralConfigsHelper(), null, (IResponseCallback) this);
    }

    /**
     * Proccess the auto country selection
     * 
     * @author sergiopereira
     */
    private void onProcessAutoCountrySelection() {
        Log.i(TAG, "ON PROCESS AUTO_COUNTRY_SELECTION");
        LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
    }

    /**
     * Proccess the requires user interaction
     * 
     * @author sergiopereira
     */
    private void onProcessRequiresUserError() {
        jumiaMapImage = findViewById(R.id.jumiaMap);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animationFadeOut.setDuration(750);
        animationFadeOut.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                jumiaMapImage.setVisibility(View.GONE);
                Log.i(TAG, "ON PROCESS REQUIRES USER INTERACTION");
                // Show Change country
                Intent intent = new Intent(getApplicationContext(), getActivityClassForDevice());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHOOSE_COUNTRY);
                intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
                // Start activity
                startActivity(intent);
                finish();
            }
        });
        jumiaMapImage.startAnimation(animationFadeOut);

    }

    /**
     * Proccess the api md5 event
     * 
     * @param bundle
     */
    private void onProcessApiEvent(Bundle bundle) {
        Log.d(TAG, "ON PROCESS API EVENT");
        // Validate out dated sections
        if (bundle.getBoolean(Section.SECTION_NAME_COUNTRY_CONFIGS, false)) {
            Log.d(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
            JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, (IResponseCallback) this);
        } else {
            Log.d(TAG, "START MAIN ACTIVITY");
            // ## Google Analytics "General Campaign Measurement" ##
            TrackerDelegator.trackGACampaign(getApplicationContext(), mUtm);
            //track open app event for all tracker but Adjust
            TrackerDelegator.trackAppOpen(getApplicationContext(), isDeepLinkLaunch);
            // Show activity
            selectActivity();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON ERROR RESPONSE");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        
        @SuppressWarnings("unchecked")
        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        Log.i(TAG, "codeerror " + errorCode);

        if (errorCode.isNetworkError()) {
            switch (errorCode) {
            case SSL:
            case IO:
            case CONNECT_ERROR:
            case TIME_OUT:
            case HTTP_STATUS:
            case NO_NETWORK:
                showFragmentRetry();
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
                dialog = DialogGenericFragment.newInstance(true, true, false, getString(R.string.validation_title), dialogMsg,
                        getResources().getString(R.string.ok_label), "", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dialog.dismiss();
                                }

                            }

                        });

                dialog.show(getSupportFragmentManager(), null);
                break;
            default:
                if (dialog != null) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialog = DialogGenericFragment.createServerErrorDialog(SplashScreenActivity.this,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Re-send initialize event
                                retryRequest();
                                dialog.dismiss();
                            }
                        }, true);
                dialog.show(getSupportFragmentManager(), null);
                
                break;
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

    /*
     * ########### MAINTENANCE ###########
     */

    /**
     * Show maintenance page.
     * @param eventType
     * @author sergiopereira
     */
    private void setLayoutMaintenance(final EventType eventType) {
        // Inflate maintenance
        mMainFallBackStub.setVisibility(View.VISIBLE);
        // Get config
        boolean isBamilo = getResources().getBoolean(R.bool.is_bamilo_specific);
        // Case BAMILO
        if(isBamilo) MaintenancePage.setMaintenancePageBamilo(this, eventType, (OnClickListener) this);
        // Case JUMIA
        else MaintenancePage.setMaintenancePageSplashScreen(this, eventType, (OnClickListener) this);
    }
    
    /*
     * ########### RETRY ###########
     */
    
    /**
     * Show the retry view from the root layout
     * @param listener button
     * @author sergiopereira
     */
    protected void showFragmentRetry() {
        // Hide maintenance visibility
        if(mMainFallBackStub.getVisibility() == View.VISIBLE) mMainFallBackStub.setVisibility(View.GONE);
        // Show no network
        mRetryFallBackStub.setVisibility(View.VISIBLE);
        // Set view
        try {
            findViewById(R.id.fragment_root_retry_button).setOnClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }
    
    
    /*
     * ########### RESPONSES ###########
     */

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
     * Set orientation
     */
    public void setOrientationForHandsetDevices() {
        // Validate if is phone and force portrait orientation
        if (!getResources().getBoolean(R.bool.isTablet)) {
            Log.i(TAG, "IS PHONE: FORCE PORTRAIT ORIENTATION");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /*
     * ########### LISTENERS ###########
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case retry button from no network
        if(id == R.id.fragment_root_retry_button) onClickRetryNoNetwork();
        // Case retry button from maintenance
        else if( id == R.id.fallback_retry) onClickMaintenanceRetryButton(view);
        // Case choose country
        else if( id == R.id.fallback_change_country) onClickMaitenanceChooseCountry();
        // Case unknown
        else Log.w(TAG, "WARNING: UNEXPECTED CLICK ENVENT");
    }
    
    /**
     * Process the click on retry button from no connection layout.
     */
    private void onClickRetryNoNetwork() {
        retryRequest();
        Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.anim_rotate);
        findViewById(R.id.fragment_root_retry_spinning).setAnimation(animation);
    }
    
    /**
     * Retry request
     */
    protected void retryRequest(){
        JumiaApplication.INSTANCE.init(false, initializationHandler);
    }
    
    /**
     * Process the click on choose country button in maintenance page.
     * @author sergiopereira
     */
    private void onClickMaitenanceChooseCountry() {
        // Show Change country
        Intent intent = new Intent(getApplicationContext(), getActivityClassForDevice());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHOOSE_COUNTRY);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
        intent.putExtra(ConstantsIntentExtra.IN_MAINTANCE, true);
        // Start activity
        startActivity(intent);
        finish();
    }
    
    /**
     * Process the click on retry button in maintenance page.
     * @param eventType
     * @modified sergiopereira
     */
    private void onClickMaintenanceRetryButton(View view) {
        // Get tag
        String type = (String) view.getTag();
        // Get event type
        EventType eventType = EventType.valueOf(type);
        // Retry
        mMainFallBackStub.setVisibility(View.GONE);        
        JumiaApplication.INSTANCE.sendRequest(
                JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(eventType),
                JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(eventType),
                JumiaApplication.INSTANCE.getRequestsResponseList().get(eventType));
    }
    
    
}
