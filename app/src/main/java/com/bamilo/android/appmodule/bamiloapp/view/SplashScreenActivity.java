package com.bamilo.android.appmodule.bamiloapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import com.adjust.sdk.Adjust;
import com.bamilo.android.BuildConfig;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetApiInfoHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetAvailableCountriesHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetCountryConfigsHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.ConfigurationWrapper;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.DeepLinkManager;
import com.bamilo.android.appmodule.bamiloapp.utils.location.LocationHelper;
import com.bamilo.android.appmodule.bamiloapp.utils.maintenance.MaintenancePage;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView;
import com.bamilo.android.appmodule.modernbamilo.launch.model.webservice.GetStartupConfigsResponse;
import com.bamilo.android.appmodule.modernbamilo.launch.model.webservice.GetStartupConfigsResponseKt;
import com.bamilo.android.appmodule.modernbamilo.launch.model.webservice.LaunchWebApi;
import com.bamilo.android.appmodule.modernbamilo.launch.model.webservice.VersionStatus;
import com.bamilo.android.appmodule.modernbamilo.update.ForceUpdateBottomSheet;
import com.bamilo.android.appmodule.modernbamilo.update.OptionalUpdateBottomSheet;
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper;
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.objects.configs.CountryConfigs;
import com.bamilo.android.framework.service.objects.configs.RedirectPage;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.configs.AigRestContract;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.crashlytics.android.Crashlytics;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.exception.PushwooshException;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Response;

/**
 * <p> This class creates a splash screen. It also initializes hockey and the backend </p> <p/> <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved </p> <p/> <p> Unauthorized copying of
 * this file, via any medium is strictly prohibited Proprietary and confidential. </p>
 *
 * @author Michael Kröz
 * @version 1.01
 * @project WhiteLabelRocket
 * @modified Sergio Pereira
 * @date 25/04/2013
 * @description
 */
public class SplashScreenActivity extends FragmentActivity implements IResponseCallback,
        OnClickListener {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();

    private static final int SPLASH_DURATION_IN = 500;

    private static final int SPLASH_DURATION_OUT = 500;

    private boolean shouldHandleEvent = true;

    private View mMainMapImage;

    private View mMainFallBackStub;

    private View mErrorFallBackStub;

    private BaseResponse mLastSuccessResponse;

    private ErrorLayoutFactory mErrorLayoutFactory;

    Call<ResponseWrapper<GetStartupConfigsResponse>> call;

    boolean waitForForceUpdate = true;
    private OptionalUpdateBottomSheet mOptionalUpdateBottomSheet;


    private static final long TIMESTAMP_BLACKFRIDAY_END = 1543104000000L;   // Nov 25

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long mCurrentTimeStamp = Calendar.getInstance().getTimeInMillis();
        if (mCurrentTimeStamp < TIMESTAMP_BLACKFRIDAY_END ) {
            setContentView(R.layout.launch_screen_black_friday);
        } else {
            setContentView(R.layout.splash_screen);
        }

    }

    private void checkForUpdate() {
        LaunchWebApi webApi = RetrofitHelper.makeWebApi(this, LaunchWebApi.class);
        call = null;
        call = webApi.getStartupConfigs("android", BuildConfig.VERSION_CODE);

        call.enqueue(new retrofit2.Callback<ResponseWrapper<GetStartupConfigsResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<GetStartupConfigsResponse>> call,
                    @NonNull Response<ResponseWrapper<GetStartupConfigsResponse>> response) {
                try {
                    switch (response.body().getMetadata().getVersionStatus().getState()) {
                        case GetStartupConfigsResponseKt.STATE_OPTIONAL_UPDATE:
                            showUpdateBottomSheet(
                                    response.body().getMetadata().getVersionStatus().getTitle(),
                                    response.body().getMetadata().getVersionStatus().getMessage(),
                                    response.body().getMetadata().getVersionStatus()
                                            .getLatestApkUrl()
                            );
                            break;
                        case GetStartupConfigsResponseKt.STATE_FORCED_UPDATE:
                            showForceUpdateDialog(response.body().getMetadata().getVersionStatus());
                            break;
                        default:
                            waitForForceUpdate = false;
                            initialBamilo();
                            break;
                    }
                } catch (Exception e) {
                    waitForForceUpdate = false;
                    initialBamilo();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<GetStartupConfigsResponse>> call,
                    @NonNull Throwable t) {
                waitForForceUpdate = false;
                initialBamilo();
            }
        });
    }

    private void initialBamilo() {
        waitForForceUpdate = false;
        initPushwoosh();

        DeviceInfoHelper.setOrientationForHandsetDevices(this);
        mMainMapImage = findViewById(R.id.splashMap);
        mMainFallBackStub = findViewById(R.id.splash_screen_maintenance_stub);
        mErrorFallBackStub = findViewById(R.id.splash_fragment_retry_stub);
        shouldHandleEvent = true;

        BamiloApplication.INSTANCE.init(initializationHandler);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animationFadeIn.setDuration(SPLASH_DURATION_IN);

        mMainMapImage.clearAnimation();
        mMainMapImage.startAnimation(animationFadeIn);
    }

    private void showUpdateBottomSheet(String title, String description, String latestApkUrl) {
        mOptionalUpdateBottomSheet = new OptionalUpdateBottomSheet()
                .setUpdateInfo(title, description, latestApkUrl)
                .setDismissListener(this::initialBamilo);

        mOptionalUpdateBottomSheet.show(getSupportFragmentManager(), "UpdateBottomSheet");
    }

    private void showForceUpdateDialog(VersionStatus versionStatus) {
        waitForForceUpdate = true;
        Fragment fragment =
                ForceUpdateBottomSheet.
                        Companion.newInstance(
                        versionStatus.getTitle(),
                        versionStatus.getMessage(),
                        versionStatus.getLatestApkUrl());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(R.anim.fragment_slide_up, 0, 0, R.anim.slide_down);

        ft.replace(R.id.splashScreen_frameLayout_container, fragment,
                ForceUpdateBottomSheet.class.getSimpleName());

        ft.commit();
        findViewById(R.id.splashScreen_frameLayout_container).setVisibility(View.VISIBLE);
    }

    private boolean isForceUpdateFragmentVisible() {
        ForceUpdateBottomSheet forceUpdateBottomSheet =
                (ForceUpdateBottomSheet) getSupportFragmentManager()
                        .findFragmentByTag(ForceUpdateBottomSheet.class.getSimpleName());

        return forceUpdateBottomSheet != null && forceUpdateBottomSheet.isVisible();
    }

    private boolean isOptionalUpdateFragmentVisible() {
        return mOptionalUpdateBottomSheet != null && mOptionalUpdateBottomSheet.isVisible();
    }

    @Override
    protected void onStart() {
        super.onStart();
        shouldHandleEvent = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialBamilo();
//        if (!isForceUpdateFragmentVisible() && !isOptionalUpdateFragmentVisible()) {
//            checkForUpdate();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        shouldHandleEvent = false;
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }

        shouldHandleEvent = false;

        mMainMapImage = null;
        mMainFallBackStub = null;
        mErrorFallBackStub = null;
        mLastSuccessResponse = null;
        mErrorLayoutFactory = null;
    }

    @SuppressLint("HandlerLeak")
    Handler initializationHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            BaseResponse baseResponse = (BaseResponse) msg.obj;
            EventType eventType = baseResponse.getEventType();
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
        return !getResources().getBoolean(R.bool.isTablet) ? MainFragmentActivity.class
                : MainFragmentTabletActivity.class;
    }

    /**
     * Starts the Activity depending whether the app is started by the user, or by the push
     * notification.
     */
    public void selectActivity() {
        mMainMapImage.clearAnimation();
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animationFadeOut.setDuration(SPLASH_DURATION_OUT);
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

                if (waitForForceUpdate) {
                    return;
                }

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
        if (BamiloApplication.INSTANCE.isDebuggable()) {
            String name = getString(getApplicationInfo().labelRes);
            String text = getString(R.string.first_new_line_second_placeholder, name,
                    AigRestContract.REQUEST_HOST);
            ((XeiTextView) findViewById(R.id.dev_text)).setText(text);
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        if (!shouldHandleEvent) {
            return;
        }

        // Case error use the last success response to request the next step
        mLastSuccessResponse = response;

        EventType eventType = response.getEventType();
        int errorCode =
                response.getError() != null ? response.getError().getCode() : ErrorCode.NO_ERROR;

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

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.attachBaseContext(
                    ConfigurationWrapper.wrapLocale(newBase, new Locale("fa", "ir")));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    /**
     * Process the initialize event
     *
     * @author sergiopereira
     */
    private void onProcessInitialize() {
        BamiloApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, this);
    }

    /**
     * Process the global configs event
     *
     * @author sergiopereira
     */
    private void onProcessGlobalConfigsEvent(BaseResponse baseResponse) {
        SharedPreferences sharedPrefs = getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null) == null) {
            if (BamiloApplication.INSTANCE.countriesAvailable != null
                    && BamiloApplication.INSTANCE.countriesAvailable.size() > 0) {
                // Validate if there is any country from deeplink when starting the app from clean slate
                if (!DeepLinkManager.validateCountryDeepLink(getApplicationContext(), getIntent(),
                        initializationHandler)) {
                    LocationHelper.getInstance()
                            .autoCountrySelection(getApplicationContext(), initializationHandler);
                }
            } else {
                onRequestError(baseResponse);
            }
        } else {
            if (BamiloApplication.INSTANCE.countriesAvailable != null
                    && BamiloApplication.INSTANCE.countriesAvailable.size() > 0) {
                BamiloApplication.INSTANCE.init(initializationHandler);
            } else {
                onRequestError(baseResponse);
            }
        }
    }

    /**
     * Process the country configs event
     */
    private void onProcessCountryConfigsEvent(BaseResponse response) {
        // Goes to saved redirect page otherwise continue
        if (!hasRedirectPage(((CountryConfigs) response.getContentData()).getRedirectPage())) {
            BamiloApplication.INSTANCE.init(initializationHandler);
        }
    }

    /**
     * Process the no country configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountryConfigsError() {
        BamiloApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    /**
     * Process the no countries configs error
     *
     * @author sergiopereira
     */
    private void onProcessNoCountriesConfigsError() {
        BamiloApplication.INSTANCE.sendRequest(new GetAvailableCountriesHelper(), null, this);
    }

    /**
     * Process the auto country selection
     *
     * @author sergiopereira
     */
    private void onProcessAutoCountrySelection() {
        LocationHelper.getInstance()
                .autoCountrySelection(getApplicationContext(), initializationHandler);
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
                if (waitForForceUpdate) {
                    return;
                }

                mMainMapImage.setVisibility(View.GONE);
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
        GetApiInfoHelper.ApiInformationStruct apiInformation = (GetApiInfoHelper.ApiInformationStruct) baseResponse
                .getContentData();
        // Validate out dated sections
        if (apiInformation.isSectionNameConfigurations()) {
            triggerGetCountryConfigs();
        } else if (!CountryPersistentConfigs.checkCountryRequirements(getApplicationContext())) {
            triggerGetCountryConfigs();
        }
        // Goes to saved redirect page otherwise continue
        else if (!hasRedirectPage(
                CountryPersistentConfigs.getRedirectPage(getApplicationContext()))) {
            selectActivity();
        }
    }

    /**
     * Trigger to get the country configurations
     */
    private void triggerGetCountryConfigs() {
        BamiloApplication.INSTANCE.sendRequest(new GetCountryConfigsHelper(), null, this);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        if (!shouldHandleEvent) {
            return;
        }
        int errorCode = baseResponse.getError().getCode();

        if (ErrorCode.isNetworkError(errorCode)) {
            handleNetworkError(errorCode);
        } else {
            handleRequestError(errorCode);
        }
    }

    /**
     * Handle network events.
     */
    public boolean handleNetworkError(int errorCode) {
        boolean result = true;
        switch (errorCode) {
            case ErrorCode.HTTP_STATUS:
            case ErrorCode.IO:
            case ErrorCode.CONNECT_ERROR:
            case ErrorCode.TIME_OUT:
                showFragmentNetworkErrorRetry();
                break;
            case ErrorCode.NO_CONNECTIVITY:
                showFragmentNoNetworkRetry();
                break;
            case ErrorCode.SSL:
            case ErrorCode.SERVER_IN_MAINTENANCE:
            case ErrorCode.SERVER_OVERLOAD:
                showFragmentServerMaintenanceRetry();
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * Handle request errors
     */
    public boolean handleRequestError(int errorCode) {
        switch (errorCode) {
            case ErrorCode.ERROR_PARSING_SERVER_DATA:
                showFragmentServerMaintenanceRetry();
                return true;
            default:
                return false;
        }
    }

    /**
     * Show the retry view from the root layout
     */
    protected void showFragmentNetworkErrorRetry() {
        showErrorLayout(ErrorLayoutFactory.NETWORK_ERROR_LAYOUT, this);
    }

    /**
     * Show the retry view from the root layout
     *
     * @author sergiopereira
     */
    protected void showFragmentNoNetworkRetry() {
        showErrorLayout(ErrorLayoutFactory.NO_NETWORK_LAYOUT, this);
    }

    /**
     * Show the retry view from the root layout
     *
     * @author sergiopereira
     */
    protected void showFragmentServerMaintenanceRetry() {
        showErrorLayout(ErrorLayoutFactory.MAINTENANCE_LAYOUT, this);
    }

    /**
     * Show maintenance page.
     */
    private void setLayoutMaintenance(EventType eventType) {
        mMainFallBackStub.setVisibility(View.VISIBLE);
        if (ShopSelector.isRtl()) {
            MaintenancePage.setMaintenancePageBamilo(getWindow().getDecorView(), this);
        }
    }

    /**
     * Show the retry view from the root layout
     *
     * @author sergiopereira
     */
    protected void showFragmentRetry() {
        if (mMainFallBackStub != null) {
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

    protected void showErrorLayout(@ErrorLayoutFactory.LayoutErrorType int type,
            OnClickListener onClickListener) {
        // Show no network
        if (mErrorFallBackStub instanceof ViewStub) {
            mErrorFallBackStub = ((ViewStub) mErrorFallBackStub).inflate();
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
        } catch (NullPointerException ignored) {
        }
    }

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
    }

    private void checkRetryButtonBehavior(View view) {
        if (view.getId() == R.id.fragment_root_error_button) {
            int type = (int) view.getTag(R.id.fragment_root_error_button);
            if (type == ErrorLayoutFactory.NO_NETWORK_LAYOUT) {
                // Case retry button from no network
                onClickRetryNoNetwork();
            } else if (type == ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT) {
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
        Animation animation = AnimationUtils
                .loadAnimation(SplashScreenActivity.this, R.anim.anim_rotate);
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
            Animation animation = AnimationUtils
                    .loadAnimation(SplashScreenActivity.this, R.anim.anim_rotate);
            findViewById(R.id.fragment_root_error_spinning).setAnimation(animation);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Retry request validating the last success response.
     */
    protected void retryRequest() {
        // Case first time
        if (mLastSuccessResponse == null) {
            BamiloApplication.INSTANCE.init(initializationHandler);
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
        if (waitForForceUpdate) {
            return;
        }

        Intent intent = new Intent(getApplicationContext(), getActivityClassForDevice());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHOOSE_COUNTRY);
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
        intent.putExtra(ConstantsIntentExtra.IN_MAINTANCE, true);

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
     *
     * @return true case valid redirect page
     */
    private boolean hasRedirectPage(RedirectPage redirect) {
        return CountryConfigs.isValidRedirectPage(redirect) && ActivitiesWorkFlow
                .showRedirectInfoActivity(this, redirect);
    }

    private void initPushwoosh() {
        Pushwoosh.getInstance().registerForPushNotifications(
                result -> {
                    if (result.isSuccess()) {
                        String token = result.getData();
                        Adjust.setPushToken(token, SplashScreenActivity.this);
                        Crashlytics.setUserIdentifier(Pushwoosh.getInstance().getHwid());
                    } else {
                        PushwooshException exception = result.getException();
                        // handle registration error
                    }
                });
    }
}
