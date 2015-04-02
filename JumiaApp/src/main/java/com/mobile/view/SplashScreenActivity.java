package com.mobile.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.ad4screen.sdk.Tag;
import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.Darwin;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Section;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.rest.RestContract;
import com.mobile.framework.service.IRemoteServiceCallback;
import com.mobile.framework.tracking.Ad4PushTracker;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.configs.GetApiInfoHelper;
import com.mobile.helpers.configs.GetCountriesGeneralConfigsHelper;
import com.mobile.helpers.configs.GetCountryConfigsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.preferences.CountryConfigs;
import com.mobile.utils.HockeyStartup;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.location.LocationHelper;
import com.mobile.utils.maintenance.MaintenancePage;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.akquinet.android.androlog.Log;

/**
 * <p> This class creates a splash screen. It also initializes hockey and the backend </p> <p/> <p> Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p> <p/> <p> Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential. </p>
 *
 * @author Michael Kröz
 * @version 1.01
 * @project WhiteLabelRocket
 * @modified Sergio Pereira
 * @date 25/04/2013
 * @description
 */
@Tag(name = "SplashScreenActivity")
public class SplashScreenActivity extends FragmentActivity implements IResponseCallback, OnClickListener {

    private final static String TAG = LogTagHelper.create(SplashScreenActivity.class);

    private static final int SPLASH_DURATION_IN = 1250;

    private static final int SPLASH_DURATION_OUT = 750;

    private DialogGenericFragment dialog;

    private static boolean shouldHandleEvent = true;

    private View mJumiaMapImage;

    private View mMainFallBackStub;

    private View mRetryFallBackStub;

    private View mUnexpectedError;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Disable rich push notifications
        Ad4PushTracker.get().setPushNotificationLocked(true);
        // Set Font
        boolean isSpecificApp = getApplicationContext().getResources().getBoolean(R.bool.is_shop_specific);
        HoloFontLoader.initFont(isSpecificApp);
        // Validate if is phone and force orientation
        setOrientationForHandsetDevices();
        // Set view
        setContentView(R.layout.splash_screen);
        // Get map
        mJumiaMapImage = findViewById(R.id.jumiaMap);
        // Get fall back layout
        mMainFallBackStub = findViewById(R.id.splash_screen_maintenance_stub);
        // Get retry layout
        mRetryFallBackStub = findViewById(R.id.splash_fragment_retry_stub);
        // Get unexpected error layout
        mUnexpectedError = findViewById(R.id.fragment_unexpected_error_stub);
        // Tracking
        AdjustTracker.onResume(this);
        // Initialize application
        JumiaApplication.INSTANCE.init(initializationHandler);
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
        Ad4PushTracker.get().startActivity(this);
        //
        shouldHandleEvent = true;
        // Show animated map
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animationFadeIn.setDuration(SPLASH_DURATION_IN);
        mJumiaMapImage.clearAnimation();
        mJumiaMapImage.startAnimation(animationFadeIn);
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
        //
        Ad4PushTracker.get().stopActivity(this);
        // Validate dialog
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
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
        SharedPreferences sharedPrefs = getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor eD = sharedPrefs.edit();
        eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        eD.apply();
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
        // Mark data for GC
        dialog = null;
        mJumiaMapImage = null;
        mMainFallBackStub = null;
        mRetryFallBackStub = null;
        mUnexpectedError = null;
        initializationHandler = null;
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
        }
    };

    /**
     * Get the main class for mobile(Portrait) or tablet(Unspecified).
     *
     * @return Class
     * @author sergiopereira
     */
    private Class<?> getActivityClassForDevice() {
        return !getResources().getBoolean(R.bool.isTablet) ? MainFragmentActivity.class : MainFragmentTabletActivity.class;
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push notification.
     */
    public void selectActivity() {
        Log.i(TAG, "START ANIMATION ACTIVITY");
        mJumiaMapImage.clearAnimation();
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animationFadeOut.setDuration(SPLASH_DURATION_OUT);
        animationFadeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.i(TAG, "ON ANIMATION START");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.i(TAG, "ON ANIMATION REPEAT");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i(TAG, "ON ANIMATION END");
                mJumiaMapImage.setVisibility(View.GONE);
                // Validate deep link bundle
                startMainActivity();
                overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
                finish();
            }
        });
        mJumiaMapImage.startAnimation(animationFadeOut);
    }

    /**
     * Start main fragment activity
     *
     * @author sergiopereira
     */
    private void startMainActivity() {
        Log.d(TAG, "START MAIN FRAGMENT ACTIVITY");
        // Clone the current intent, but only the relevant parts for Deep Link (URI or GCM)
        Intent intent = (Intent) getIntent().clone();
        intent.setClass(getApplicationContext(), getActivityClassForDevice());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        PackageInfo pInfo;
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
        super.onUserLeaveHint();
        Log.e(TAG, "onUserLeaveHint");
        shouldHandleEvent = false;
    }

    /**
     * ######## RESPONSES ########
     */

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle
     * )
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS RESPONSE");
        if (!shouldHandleEvent) {
            Log.e(TAG,"shouldHandleEvent" + shouldHandleEvent);
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        Log.i(TAG, "code1configs : handleSuccessResponse : " + eventType + " errorcode : " + errorCode);

        if (dialog != null && dialog.isVisible()) {
            try {
                dialog.dismissAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Case event
        if (eventType == EventType.INITIALIZE) {
            onProcessInitialize();
        } else if (eventType == EventType.GET_API_INFO) {
            onProcessApiEvent(bundle);
        } else if (eventType == EventType.GET_COUNTRY_CONFIGURATIONS) {
            onProcessCountryConfigsEvent();
        } else if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) {
            onProcessGlobalConfigsEvent(bundle);
        }
        // Case error
        else if (errorCode == ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE) {
            onProcessNoCountryConfigsError();
        } else if (errorCode == ErrorCode.NO_COUNTRIES_CONFIGS) {
            onProcessNoCountriesConfigsError();
        } else if (errorCode == ErrorCode.AUTO_COUNTRY_SELECTION) {
            onProcessAutoCountrySelection();
        } else if (errorCode == ErrorCode.REQUIRES_USER_INTERACTION) {
            onProcessRequiresUserError();
        }
    }

    /**
     * Process the initialize event
     *
     * @author sergiopereira
     */
    private void onProcessInitialize() {
        Log.i(TAG, "ON PROCESS: INITIALIZE");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, this);
    }

    /**
     * Process the global configs event
     *
     * @param bundle
     * @author sergiopereira
     */
    private void onProcessGlobalConfigsEvent(Bundle bundle) {
        Log.i(TAG, "ON PROCESS: GLOBAL CONFIGS");
        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null) == null) {
            Log.i(TAG, "SELECTED COUNTRY ID IS NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
            } else {
                onRequestError(bundle);
            }
        } else {
            Log.i(TAG, "SELECTED COUNTRY ID IS NOT NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                JumiaApplication.INSTANCE.init(initializationHandler);
            } else {
                onRequestError(bundle);
            }
        }
    }

    /**
     * Process the country configs event
     */
    private void onProcessCountryConfigsEvent() {
        Log.i(TAG, "ON PROCESS COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.init(initializationHandler);
    }

    /**
     * Process the no country configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountryConfigsError() {
        Log.i(TAG, "ON PROCESS NO COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    /**
     * Process the no countries configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountriesConfigsError() {
        Log.i(TAG, "ON PROCESS NO COUNTRIES CONFIGS");
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountriesGeneralConfigsHelper(), null, this);
    }

    /**
     * Process the auto country selection
     *
     * @author sergiopereira
     */
    private void onProcessAutoCountrySelection() {
        Log.i(TAG, "ON PROCESS AUTO_COUNTRY_SELECTION");
        LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
    }

    /**
     * Process the requires user interaction
     *
     * @author sergiopereira
     */
    private void onProcessRequiresUserError() {
        mJumiaMapImage.clearAnimation();
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
                mJumiaMapImage.setVisibility(View.GONE);
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
        mJumiaMapImage.startAnimation(animationFadeOut);

    }

    /**
     * Process the api md5 event
     *
     * @param bundle
     */
    private void onProcessApiEvent(Bundle bundle) {
        Log.d(TAG, "ON PROCESS API EVENT");
        // Validate out dated sections
        if (bundle.getBoolean(Section.SECTION_NAME_COUNTRY_CONFIGS, false)) {
            Log.d(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            triggerGetCountryConfigs();
        } else if(!CountryConfigs.checkCountryRequirements(getApplicationContext())){
            Log.d(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            triggerGetCountryConfigs();
        } else {
            Log.d(TAG, "START MAIN ACTIVITY");
            // Show activity
            selectActivity();
        }
    }

    /**
     *
     */
    private void triggerGetCountryConfigs(){
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
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
                case IO:
                case CONNECT_ERROR:
                case HTTP_STATUS:
                    showUnexpectedError();
                case TIME_OUT:
                case NO_NETWORK:
                    showFragmentRetry();
                    break;
                case SSL:
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
                    dialog = DialogGenericFragment.newInstance(true, false, getString(R.string.validation_title), dialogMsg,
                            getResources().getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dialog.dismissAllowingStateLoss();
                                    }

                                }

                            });

                    dialog.show(getSupportFragmentManager(), null);
                    break;
                default:
                    if (dialog != null) {
                        try {
                            dialog.dismissAllowingStateLoss();
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
                                    dialog.dismissAllowingStateLoss();
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
     *
     * @param eventType
     * @author sergiopereira
     */
    private void setLayoutMaintenance(EventType eventType) {
        // Inflate maintenance
        mMainFallBackStub.setVisibility(View.VISIBLE);
        // Get config
        boolean isBamilo = getResources().getBoolean(R.bool.is_bamilo_specific);
        // Case BAMILO
        if (isBamilo) {
            MaintenancePage.setMaintenancePageBamilo(this, eventType, this);
        }
        // Case JUMIA
        else {
            MaintenancePage.setMaintenancePageWithChooseCountry(this, eventType, this);
        }
    }
    
    /*
     * ########### RETRY ###########
     */

    /**
     * Show the retry view from the root layout
     *
     * @author sergiopereira
     */
    protected void showFragmentRetry() {
        // Hide maintenance visibility
        if (mMainFallBackStub.getVisibility() == View.VISIBLE) {
            mMainFallBackStub.setVisibility(View.GONE);
        }
        // Show no network
        mRetryFallBackStub.setVisibility(View.VISIBLE);
        // Set view
        try {
            findViewById(R.id.fragment_root_retry_button).setOnClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }

    /**
     * Show the unexpected error view from the root layout
     *
     * @author sergiopereira
     */
    protected void showUnexpectedError() {
        // Hide maintenance visibility
        if (mMainFallBackStub.getVisibility() == View.VISIBLE) {
            mMainFallBackStub.setVisibility(View.GONE);
        }
        // Show no network
        mUnexpectedError.setVisibility(View.VISIBLE);
        // Set view
        try {
            findViewById(R.id.fragment_root_error_button).setOnClickListener(this);
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
        if (id == R.id.fragment_root_retry_button) {
            onClickRetryNoNetwork();
        }
        // Case retry button from maintenance
        else if (id == R.id.fallback_retry) {
            onClickMaintenanceRetryButton(view);
        }
        // Case choose country
        else if (id == R.id.fallback_change_country) {
            onClickMaintenanceChooseCountry();
        }
        // Case retry button
        else if (id == R.id.fragment_root_error_button) {
            onClickErrorButton();
        }
        // Case unknown
        else {
            Log.w(TAG, "WARNING: UNEXPECTED CLICK ENVENT");
        }
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
     * Process the click in continue shopping
     *
     * @author sergiopereira
     */
    protected void onClickErrorButton() {
        retryRequest();
        try {
            Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.anim_rotate);
            findViewById(R.id.fragment_root_error_spinning).setAnimation(animation);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
        }
    }

    /**
     * Retry request
     */
    protected void retryRequest() {
        JumiaApplication.INSTANCE.init(initializationHandler);
    }

    /**
     * Process the click on choose country button in maintenance page.
     *
     * @author sergiopereira
     */
    private void onClickMaintenanceChooseCountry() {
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
     *
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