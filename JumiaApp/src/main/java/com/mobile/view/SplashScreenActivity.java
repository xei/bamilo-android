package com.mobile.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.a4s.sdk.plugins.annotations.UseA4S;
import com.crashlytics.android.Crashlytics;
import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetApiInfoHelper;
import com.mobile.helpers.configs.GetAvailableCountriesHelper;
import com.mobile.helpers.configs.GetCountryConfigsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.configs.RedirectPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.configs.AigRestContract;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.location.LocationHelper;
import com.mobile.utils.maintenance.MaintenancePage;
import com.mobile.utils.ui.ErrorLayoutFactory;
import io.fabric.sdk.android.Fabric;

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
@UseA4S
public class SplashScreenActivity extends FragmentActivity implements IResponseCallback, OnClickListener {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();

    private static final int SPLASH_DURATION_IN = 1250;

    private static final int SPLASH_DURATION_OUT = 750;

    private DialogGenericFragment dialog;

    private boolean shouldHandleEvent = true;

    private View mMainMapImage;

    private View mMainFallBackStub;

    private View mErrorFallBackStub;

    private BaseResponse mLastSuccessResponse;

    private ErrorLayoutFactory mErrorLayoutFactory;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Print.i(TAG, "ON CREATE");
        // Disable Accengage rich push notifications
        Ad4PushTracker.get().setPushNotificationLocked(true);
        // Set Font
        HoloFontLoader.initFont(getResources().getBoolean(R.bool.is_shop_specific));
        // Validate if is phone and force orientation
        DeviceInfoHelper.setOrientationForHandsetDevices(this);
        // Set view
        setContentView(R.layout.splash_screen);
        // Get map
        mMainMapImage = findViewById(R.id.splashMap);
        // Get fall back layout
        mMainFallBackStub = findViewById(R.id.splash_screen_maintenance_stub);
        // Get retry layout
        mErrorFallBackStub = findViewById(R.id.splash_fragment_retry_stub);
        // Intercept event
        shouldHandleEvent = true;
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
        Print.i(TAG, "ON START");
        // Intercept event
        shouldHandleEvent = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Intercept event
        shouldHandleEvent = true;
        // Show animated map
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animationFadeIn.setDuration(SPLASH_DURATION_IN);
        mMainMapImage.clearAnimation();
        mMainMapImage.startAnimation(animationFadeIn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON STOP");
        // Intercept event
        shouldHandleEvent = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        // Intercept event
        shouldHandleEvent = false;
    }


    Handler initializationHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            BaseResponse baseResponse = (BaseResponse) msg.obj;
            EventType eventType = baseResponse.getEventType();
            //Print.i(TAG, "code1configs received response : " + errorCode + " event type : " + eventType);
            if (eventType == EventType.INITIALIZE) {
                showDevInfo();
            }
            onRequestComplete(baseResponse);
        }
    };

    /**
     * Get the main class for mobile(Portrait) or tablet(Unspecified).
     *
     * @return Class
     * @author sergiopereira
     */
    protected Class<?> getActivityClassForDevice() {
        return !getResources().getBoolean(R.bool.isTablet) ? MainFragmentActivity.class : MainFragmentTabletActivity.class;
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push notification.
     */
    public void selectActivity() {
        Print.i(TAG, "START ANIMATION ACTIVITY");
        mMainMapImage.clearAnimation();
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animationFadeOut.setDuration(SPLASH_DURATION_OUT);
        animationFadeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Print.i(TAG, "ON ANIMATION START");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Print.i(TAG, "ON ANIMATION REPEAT");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Print.i(TAG, "ON ANIMATION END");
                mMainMapImage.setVisibility(View.GONE);
                // Validate deep link bundle
                startMainActivity();
                overridePendingTransition(R.animator.activityfadein, R.animator.splashfadeout);
                finish();
            }
        });
        mMainMapImage.startAnimation(animationFadeOut);
    }

    /**
     * Start main fragment activity
     *
     * @author sergiopereira
     */
    private void startMainActivity() {
        Print.d(TAG, "START MAIN FRAGMENT ACTIVITY");
        // Clone the current intent, but only the relevant parts for Deep Link (URI or GCM)
        Intent intent = (Intent) getIntent().clone();
        intent.setClass(getApplicationContext(), getActivityClassForDevice());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Show dev info
     */
    private void showDevInfo() {
        // Case dev
        if (JumiaApplication.INSTANCE.isDebuggable()) {
            String name = getString(getApplicationInfo().labelRes);
            String text = getString(R.string.first_new_line_second_placeholder, name, AigRestContract.REQUEST_HOST);
            ((TextView) findViewById(R.id.dev_text)).setText(text);
        }
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
    public void onRequestComplete(BaseResponse response) {
        if (!shouldHandleEvent) {
            Print.e(TAG, "shouldHandleEvent: " + shouldHandleEvent);
            return;
        }

        // Case error use the last success response to request the next step
        mLastSuccessResponse = response;

        EventType eventType = response.getEventType();
        int errorCode = response.getError() != null ? response.getError().getCode() : ErrorCode.NO_ERROR;
        Print.i(TAG, "ON SUCCESS RESPONSE: " + eventType);

        // Dismiss dialog
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
            onProcessApiEvent(response);
        } else if (eventType == EventType.GET_COUNTRY_CONFIGURATIONS) {
            onProcessCountryConfigsEvent(response);
        } else if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) {
            onProcessGlobalConfigsEvent(response);
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
        Print.i(TAG, "ON PROCESS: INITIALIZE");
        JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, this);
    }

    /**
     * Process the global configs event
     * @author sergiopereira
     */
    private void onProcessGlobalConfigsEvent(BaseResponse baseResponse) {
        Print.i(TAG, "ON PROCESS: GLOBAL CONFIGS");
        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null) == null) {
            Print.i(TAG, "SELECTED COUNTRY ID IS NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                // Validate if there is any country from deeplink when starting the app from clean slate
                if(!DeepLinkManager.validateCountryDeepLink(getApplicationContext(), getIntent(), initializationHandler)){
                    LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
                }
            } else {
                onRequestError(baseResponse);
            }
        } else {
            Print.i(TAG, "SELECTED COUNTRY ID IS NOT NULL");
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                JumiaApplication.INSTANCE.init(initializationHandler);
            } else {
                onRequestError(baseResponse);
            }
        }
    }

    /**
     * Process the country configs event
     */
    private void onProcessCountryConfigsEvent(BaseResponse response) {
        Print.i(TAG, "ON PROCESS COUNTRY CONFIGS");
        // Goes to saved redirect page otherwise continue
        if (!hasRedirectPage(((CountryConfigs) response.getContentData()).getRedirectPage())) {
            JumiaApplication.INSTANCE.init(initializationHandler);
        }
    }

    /**
     * Process the no country configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountryConfigsError() {
        Print.i(TAG, "ON PROCESS NO COUNTRY CONFIGS");
        JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    /**
     * Process the no countries configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountriesConfigsError() {
        Print.i(TAG, "ON PROCESS NO COUNTRIES CONFIGS");
        JumiaApplication.INSTANCE.sendRequest(new GetAvailableCountriesHelper(), null, this);
    }

    /**
     * Process the auto country selection
     *
     * @author sergiopereira
     */
    private void onProcessAutoCountrySelection() {
        Print.i(TAG, "ON PROCESS AUTO_COUNTRY_SELECTION");
        LocationHelper.getInstance().autoCountrySelection(getApplicationContext(), initializationHandler);
    }

    /**
     * Process the requires user interaction
     *
     * @author sergiopereira
     */
    private void onProcessRequiresUserError() {
        mMainMapImage.clearAnimation();
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
                mMainMapImage.setVisibility(View.GONE);
                Print.i(TAG, "ON PROCESS REQUIRES USER INTERACTION");
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
        mMainMapImage.startAnimation(animationFadeOut);

    }

    /**
     * Process the api md5 event
     */
    private void onProcessApiEvent(BaseResponse baseResponse) {
        Print.i(TAG, "ON PROCESS API EVENT");
        GetApiInfoHelper.ApiInformationStruct apiInformation = (GetApiInfoHelper.ApiInformationStruct) baseResponse.getContentData();
        // Validate out dated sections
        if (apiInformation.isSectionNameConfigurations()) {
            Print.i(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            triggerGetCountryConfigs();
        } else if(!CountryPersistentConfigs.checkCountryRequirements(getApplicationContext())){
            Print.i(TAG, "THE COUNTRY CONFIGS IS OUT DATED");
            triggerGetCountryConfigs();
        }
        // Goes to saved redirect page otherwise continue
        else if (!hasRedirectPage(CountryPersistentConfigs.getRedirectPage(getApplicationContext()))) {
            Print.i(TAG, "START MAIN ACTIVITY");
            selectActivity();
        }
    }

    /**
     * Trigger to get the country configurations
     */
    private void triggerGetCountryConfigs(){
        JumiaApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR RESPONSE");
        if (!shouldHandleEvent) {
            Print.e(TAG, "shouldHandleEvent: " + shouldHandleEvent);
            return;
        }
        // Get data
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();

        Print.i(TAG, "ERROR CODE: " + errorCode);
        if (ErrorCode.isNetworkError(errorCode)) {
            switch (errorCode) {
                case ErrorCode.IO:
                case ErrorCode.CONNECT_ERROR:
                case ErrorCode.HTTP_STATUS:
                    // Validate if has redirect page otherwise continue
                    if (!hasRedirectPage(CountryPersistentConfigs.getRedirectPage(getApplicationContext()))) {
                        showUnexpectedError();
                    }
                    break;
                case ErrorCode.TIME_OUT:
                case ErrorCode.NO_CONNECTIVITY:
                    showFragmentRetry();
                    break;
                case ErrorCode.SSL:
                case ErrorCode.SERVER_IN_MAINTENANCE:
                    setLayoutMaintenance(eventType);
                    break;
                case ErrorCode.REQUEST_ERROR:
                    // Generic error message
                    String msg = baseResponse.getErrorMessage();
                    if(TextUtils.isNotEmpty(baseResponse.getErrorMessage())) {
                        msg = getString(R.string.validation_errortext);
                    }

                    dialog = DialogGenericFragment.newInstance(
                            true,
                            false,
                            getString(R.string.validation_title),
                            msg,
                            getResources().getString(R.string.ok_label),
                            "",
                            new OnClickListener() {
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
                case ErrorCode.SERVER_OVERLOAD:
                    Print.w("SHOW OVERLOAD");
                    ActivitiesWorkFlow.showOverLoadErrorActivity(this);
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

        }
        else if (errorCode == ErrorCode.ERROR_PARSING_SERVER_DATA) {
            setLayoutMaintenance(eventType);
        }
        else if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) {
            if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
                //Print.i(TAG, "code1configs received response correctly!!!");
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
     */
    private void setLayoutMaintenance(EventType eventType) {
        // Inflate maintenance
        mMainFallBackStub.setVisibility(View.VISIBLE);
        // Show maintenance page
        if (ShopSelector.isRtl()) {
            MaintenancePage.setMaintenancePageBamilo(getWindow().getDecorView(), this);
        } else {
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
        if(mMainFallBackStub != null) {
            // Hide maintenance visibility
            if (mMainFallBackStub.getVisibility() == View.VISIBLE) {
                mMainFallBackStub.setVisibility(View.GONE);
            }
        }
        showErrorLayout(ErrorLayoutFactory.NO_NETWORK_LAYOUT, this);
    }

    /**
     * Show the unexpected error view from the root layout
     *
     * @author sergiopereira
     */
    protected void showUnexpectedError() {
        // Hide maintenance visibility
        if (mMainFallBackStub != null && mMainFallBackStub.getVisibility() == View.VISIBLE) {
            mMainFallBackStub.setVisibility(View.GONE);
        }
        showErrorLayout(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
    }

    protected void showErrorLayout(@ErrorLayoutFactory.LayoutErrorType int type, OnClickListener onClickListener){
        // Show no network
        if(mErrorFallBackStub instanceof ViewStub) {
            mErrorFallBackStub = ((ViewStub)mErrorFallBackStub).inflate();
            mErrorLayoutFactory = new ErrorLayoutFactory((ViewGroup) mErrorFallBackStub);
            mErrorLayoutFactory.showErrorLayout(type);
        } else {
            mErrorLayoutFactory.showErrorLayout(type);
        }
        // Set view
        try {
            View retryButton = findViewById(R.id.fragment_root_error_button);
            retryButton.setOnClickListener(onClickListener);
            retryButton.setTag(R.id.fragment_root_error_button, type);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
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
        // Case retry
        if (id == R.id.fragment_root_error_button) {
            checkRetryButtonBehavior(view);
        }
        // Case retry button from maintenance
        else if (id == R.id.fragment_root_retry_maintenance) {
            onClickMaintenanceRetryButton();
        }
        // Case choose country
        else if (id == R.id.fragment_root_cc_maintenance) {
            onClickMaintenanceChooseCountry();
        }
        // Case unknown
        else {
            Print.w(TAG, "WARNING: UNEXPECTED CLICK ENVENT");
        }
    }

    private void checkRetryButtonBehavior(View view) {
        if (view.getId() == R.id.fragment_root_error_button) {
            int type = (int)view.getTag(R.id.fragment_root_error_button);
            if(type == ErrorLayoutFactory.NO_NETWORK_LAYOUT){
                // Case retry button from no network
                onClickRetryNoNetwork();
            } else if(type == ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT){
                // Case retry button
                onClickErrorButton();
            }
        }
    }

    /**
     * Process the click on retry button from no connection layout.
     */
    private void onClickRetryNoNetwork() {
        retryRequest();
        Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.anim_rotate);
        findViewById(R.id.fragment_root_error_spinning).setAnimation(animation);
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
            Print.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
        }
    }

    /**
     * Retry request validating the last success response.
     */
    protected void retryRequest() {
        // Case first time
        if(mLastSuccessResponse == null) {
            JumiaApplication.INSTANCE.init(initializationHandler);
        }
        // Case received error from a request
        else {
            onRequestComplete(mLastSuccessResponse);
        }
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
     */
    private void onClickMaintenanceRetryButton() {
        // Retry
        mMainFallBackStub.setVisibility(View.GONE);
        // Retry
        retryRequest();
    }

    /**
     * Shows the redirect page.
     * @return true case valid redirect page
     */
    private boolean hasRedirectPage(RedirectPage redirect) {
        return CountryConfigs.isValidRedirectPage(redirect) && ActivitiesWorkFlow.showRedirectInfoActivity(this, redirect);
    }

}