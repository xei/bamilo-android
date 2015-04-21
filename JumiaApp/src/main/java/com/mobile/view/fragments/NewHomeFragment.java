/**
 *
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.framework.Darwin;
import com.mobile.framework.objects.Promotion;
import com.mobile.framework.objects.home.NewHomePage;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.tracking.gtm.GTMValues;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.configs.GetPromotionsHelper;
import com.mobile.helpers.teasers.GetHomeHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.HockeyStartup;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogPromotionFragment;
import com.mobile.utils.dialogfragments.WizardPreferences;
import com.mobile.utils.dialogfragments.WizardPreferences.WizardType;
import com.mobile.utils.home.HomeAdapter;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show all campaigns
 *
 * @author sergiopereira
 */
public class NewHomeFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(NewHomeFragment.class);

    private RecyclerView mListView;

    /**
     * Constructor via bundle
     *
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static NewHomeFragment newInstance() {
        return new NewHomeFragment();
    }

    /**
     * Empty constructor
     */
    public NewHomeFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Home,
                R.layout._def_home_fragment_main,
                R.string.home_label,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Register Hockey TODO ????
        HockeyStartup.register(getBaseActivity());
        // Track auto login failed if hasn't saved credentials TODO ????
        if (!JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials())
            TrackerDelegator.trackLoginFailed(TrackerDelegator.IS_AUTO_LOGIN, GTMValues.LOGIN, GTMValues.EMAILAUTH);

        // TODO Load from saved instance
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get recycler view
        mListView = (RecyclerView) view.findViewById(R.id.home_container);
        //
        mListView.setLayoutManager(new LinearLayoutManager(getBaseActivity()));

        /**
         * TODO: Validate this method is necessary to recover the app from
         * strange behavior In case Application is connected and has shop id
         * show HomePage Otherwise, waiting for connection and shop id WARNING:
         * THIS FRAGMENT CAN BE EXECUTED WITHOUT SHOP ID( HOME -> COUNTRY)
         * 
         * @author sergiopereira
         */
        SharedPreferences sharedPrefs = getBaseActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        // Case app is bound and has shop id and not in maintenance
        if (JumiaApplication.mIsBound && !TextUtils.isEmpty(shopId) && !getBaseActivity().isInitialCountry())
            onResumeExecution();
        // Case app is not bound and has shop id and not in maintenance
        else if (!JumiaApplication.mIsBound && !TextUtils.isEmpty(shopId) && !getBaseActivity().isInitialCountry())
            showFragmentErrorRetry();
        // Case app not bound and not shop id and in maintenance country selection
        else JumiaApplication.INSTANCE.setResendHandler(mServiceConnectedHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Track page
        trackPage(false);
    }


    /**
     * Handler used to receive a message from application
     */
    Handler mServiceConnectedHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            onReloadContent();
        }
    };

    /**
     * Method used to reload the collection in case some child is null
     *
     * @author sergiopereira
     */
    public void onReloadContent() {
        Log.i(TAG, "ON RELOAD CONTENT");
        onResumeExecution();
    }

    /**
     * Method used to resume the content for Home Frament
     *
     * @author sergiopereira
     */
    public void onResumeExecution() {
        Log.i(TAG, "ON RESUME EXECUTION");
        // Disabled for Samsung and Blackberry (check_version_enabled) 
        if (CheckVersion.needsToShowDialog()) CheckVersion.showDialog(getActivity());

        // Validate current state
        /*
        if (mHomePagerAdapter != null && mHomePagerAdapter.getCount() > 0) {
            Log.i(TAG, "ADAPTER IS NOT NULL");
            mHomePager.setAdapter(mHomePagerAdapter);
            mHomePagerTabStrip.setViewPager(mHomePager);
            // Show container
            showContent();
        } else {
            Log.i(TAG, "ADAPTER IS NULL");
            triggerTeasers();
        }
        */
        triggerTeasers();

        // Validate promotions
        SharedPreferences sP = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sP.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true)) {
            triggerPromotions();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        // Destroy adapter
        //mHomePagerAdapter = null;

        mServiceConnectedHandler = null;
    }

    /*
     * ########### LAYOUT ###########
     */

    /*
     * @param collection
     * @param mPagerSavedPosition

    private void onShowCollection(ArrayList<Homepage> collection, int defaultPosition) {
        Log.i(TAG, "ON SHOW");
        if (mHomePagerAdapter == null) {
            Log.i(TAG, "ADAPTER IS NULL");
            mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager(), collection);
            mHomePager.setAdapter(null);
            mHomePager.setAdapter(mHomePagerAdapter);
            mHomePagerTabStrip.setViewPager(mHomePager);
            // Valdiate the saved position
            if (mPagerSavedPosition != 0 && mPagerSavedPosition < mHomePagerAdapter.getCount())
                mHomePager.setCurrentItem(mPagerSavedPosition, false);
            else mHomePager.setCurrentItem(defaultPosition, false);
        } else {
            Log.i(TAG, "UPDATE ADAPTER");
            mHomePagerAdapter.updateCollection(collection);
        }
        // Validate if is to show home wizard
        showHomeWizard();
        // Show container
        showContent();
    }
    */

    private void showHome(NewHomePage homePage) {
        // Create adapter
        mListView.setAdapter(new HomeAdapter(getBaseActivity(), homePage.getTeasers()));
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Show only the content view
     *
     * @author sergiopereira
     */
    private void showContent() {
        //mHomePager.setVisibility(View.VISIBLE);
        //mHomePagerTabStrip.setVisibility(View.VISIBLE);
        showFragmentContentContainer();
    }

    /**
     * Show tips if is the first time the user uses the app.
     *
     * @author sergiopereira
     */
    private void showHomeWizard() {
        Log.i(TAG, "ON SHOW WIZARD");
        try {
            if (WizardPreferences.isFirstTime(getActivity(), WizardType.HOME)) {
                RelativeLayout homeTip = (RelativeLayout) getView().findViewById(R.id.home_tip);
                homeTip.setVisibility(View.VISIBLE);
                homeTip.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        WizardPreferences.changeState(getActivity(), WizardType.HOME);
                        v.setVisibility(View.GONE);
                        return false;
                    }
                });
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SHOW HOME WIZARD", e);
        }
    }

    /*
     * ########### LISTENERS ###########
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onReloadContent();
    }

    /*
     * ########### TRIGGERS ###########
     */

    /**
     * Trigger to get teasers
     *
     * @author sergiopereira
     */
    private void triggerTeasers() {
        Log.d(TAG, "ON TRIGGER: GET TEASERS");
        // Get teaser collection
        triggerContentEvent(new GetHomeHelper(), null, this); // TODO
    }

    /**
     * Trigger to get promotions
     *
     * @author sergiopereira
     */
    private void triggerPromotions() {
        Log.d(TAG, "ON TRIGGER: GET PROMOTIONS");
        triggerContentEventNoLoading(new GetPromotionsHelper(), null, this);
    }

    /*
     * ########### RESPONSES ###########
     */

    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS");

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            //if (eventType == EventType.GET_TEASERS_EVENT) mReceivedInBackgroundAndDiscarded = true;
            return;
        }

        switch (eventType) {
            case GET_HOME_EVENT:
                Log.i(TAG, "ON GET_HOME_EVENT");
                NewHomePage homePage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                if (homePage != null && homePage.hasTeasers()) {
                    Log.i(TAG, "SHOW HOME PAGE: " + homePage.hasTeasers());
                    showHome(homePage);
                } else {
                    Log.i(TAG, "SHOW FALL BAK");
                    showFragmentFallBack();
                }
                break;
            case GET_PROMOTIONS:
                Log.i(TAG, "ON SUCCESS RESPONSE: GET_TEASERS_EVENT");
                onGetPromotions(bundle);
                break;
            default:
                break;
        }

    }

    /**
     * @param bundle
     */
    private void onGetPromotions(Bundle bundle) {
        if (((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getIsStillValid()) {
            try {
                DialogPromotionFragment.newInstance((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).show(getChildFragmentManager(), null);
            } catch (IllegalStateException e) {
                Log.w(TAG, "promotion expired!" + ((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getEndDate());
            }
        } else {
            Log.i(TAG, "promotion expired!" + ((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getEndDate());
        }
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

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Check base errors
        if (super.handleErrorEvent(bundle)) return;

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
            case GET_HOME_EVENT:
                Log.i(TAG, "ON ERROR RESPONSE: GET_HOME_EVENT");
                showFragmentFallBack();
                break;
            case GET_PROMOTIONS:
                Log.i(TAG, "ON ERROR RESPONSE: GET_PROMOTIONS");
                break;
            default:
                break;
        }
    }
    
    /*
     * ########### TRACKING ###########  
     */

    /**
     * Track Page for Home
     */
    private void trackPage(boolean justGTM) {
        // Generic track page
        TrackerDelegator.trackPage(TrackingPage.HOME, mLoadTime, justGTM);
        // Adjust track page
        trackPageAdjust();
    }

    /**
     * Track Page only for adjust
     */
    private void trackPageAdjust() {
        try {
            if (isAdded()) {
                Bundle bundle = new Bundle();
                bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
                bundle.putLong(AdjustTracker.BEGIN_TIME, mLoadTime);
                bundle.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
                if (JumiaApplication.CUSTOMER != null) {
                    bundle.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
                }
                TrackerDelegator.trackPageForAdjust(TrackingPage.HOME, bundle);
            }
        } catch (IllegalStateException e) {
            Log.w(TAG, "WARNING: ISE ON TRACK PAGE ADJUST");
        }
    }

}
