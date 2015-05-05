package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.Darwin;
import com.mobile.framework.objects.Promotion;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.objects.home.HomePageObject;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.TeaserGroupType;
import com.mobile.framework.objects.home.type.TeaserTargetType;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.TrackingPage;
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
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.home.holder.BaseTeaserViewHolder;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show the home page.
 *
 * @author sergiopereira
 */
public class HomePageFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(HomePageFragment.class);

    private ViewGroup mContainer;

    private HomePageObject mHomePage;

    private ScrollView mScrollView;

    private ArrayList<BaseTeaserViewHolder> mViewHolders;

    public static final String SCROLL_STATE_KEY = "scroll";

    private int[] mScrollSavedPosition;

    /**
     * Constructor via bundle
     *
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    /**
     * Empty constructor
     */
    public HomePageFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Home,
                R.layout.home_fragment_main,
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
        // Register Hockey
        HockeyStartup.register(getBaseActivity());
        // Get saved scroll position
        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_STATE_KEY)) {
            mScrollSavedPosition = savedInstanceState.getIntArray(SCROLL_STATE_KEY);
            Log.i(TAG, "SCROLL POS: " + mScrollSavedPosition[0] + " " + mScrollSavedPosition[1]);
        }
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
        // Get scroll view
        mScrollView = (ScrollView) view.findViewById(R.id.home_page_scroll);
        // Get recycler view
        mContainer = (ViewGroup) view.findViewById(R.id.home_page_container);

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
    private Handler mServiceConnectedHandler = new Handler() {
        @Override
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
     * Method used to resume the content for Home Fragment
     *
     * @author sergiopereira
     */
    public void onResumeExecution() {
        Log.i(TAG, "ON RESUME EXECUTION");
        // Disabled for Samsung and Blackberry (check_version_enabled)
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(getActivity());
        }
        // Validate current state
        if(mHomePage != null && mHomePage.hasTeasers()) {
            validateDataState();
        } else {
            triggerTeasers();
        }
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
        // Save the scroll state for rotation
        if (saveScrollState() && mScrollSavedPosition != null) {
            outState.putIntArray(SCROLL_STATE_KEY, mScrollSavedPosition);
        }
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
        // Save the scroll state
        saveScrollState();
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
        // Remove parent from views
        TeaserViewFactory.onDetachedViewHolder(mViewHolders);
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
        // Clean data
        mHomePage = null;
        mViewHolders = null;
        mServiceConnectedHandler = null;
    }

    /*
     * ########### LAYOUT ###########
     */

    /**
     * Validate the current data
     */
    private void validateDataState() {
        if(CollectionUtils.isNotEmpty(mViewHolders)) {
            rebuildHomePage(mViewHolders);
        } else {
            buildHomePage(mHomePage);
        }
    }

    /**
     * Rebuild the home updating all views.<br>
     * Warning: To perform this method is necessary detached view from parent.
     */
    private void rebuildHomePage(ArrayList<BaseTeaserViewHolder> mViewHolders) {
        Log.i(TAG, "REBUILD HOME PAGE");
        // Update each view older
        for (BaseTeaserViewHolder viewHolder : mViewHolders) {
            // Update view
            viewHolder.onUpdate();
            // Add view
            mContainer.addView(viewHolder.itemView);
        }
        // Restore the scroll state
        restoreScrollState();
        // Show mContainer
        showFragmentContentContainer();
    }

    /**
     * Create the home page
     */
    private void buildHomePage(HomePageObject homePage) {
        Log.i(TAG, "BUILD HOME PAGE");
        LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
        mViewHolders = new ArrayList<>();
        for (BaseTeaserGroupType baseTeaserType : homePage.getTeasers()) {
            // Create view
            BaseTeaserViewHolder viewHolder = TeaserViewFactory.onCreateViewHolder(inflater, baseTeaserType.getType(), mContainer, this);
            if (viewHolder != null) {
                // Set view
                viewHolder.onBind(baseTeaserType);
                // Add to container
                mContainer.addView(viewHolder.itemView);
                // Save
                mViewHolders.add(viewHolder);
            }
        }
        // Restore the scroll state
        restoreScrollState();
        // Show mContainer
        showFragmentContentContainer();
    }

    /**
     * Method to save the current scroll state
     * @return int[]
     */
    private boolean saveScrollState() {
        Log.i(TAG, "ON SAVE SCROLL STATE");
        // Validate view
        if (mScrollView != null) {
            mScrollSavedPosition = new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()};
            return true;
        }
        return false;
    }

    /**
     * Restore the saved scroll position
     * @author sergiopereira
     */
    private void restoreScrollState() {
        Log.i(TAG, "ON RESTORE SCROLL SAVED STATE");
        // Validate state
        if (mScrollSavedPosition != null) {
            // Scroll to saved position
            mScrollView.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "SCROLL TO POS: " + mScrollSavedPosition[0] + " " + mScrollSavedPosition[1]);
                    mScrollView.scrollTo(mScrollSavedPosition[0], mScrollSavedPosition[1]);
                }
            });
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
        // Validated clicked view
        if(!onClickTeaserItem(view)) {
            super.onClick(view);
        }
    }

    /**
     * Process the click on teaser
     */
    private boolean onClickTeaserItem(View view) {
        Log.i(TAG, "ON CLICK TEASER ITEM");
        // Flag to mark as intercepted
        boolean intercepted = true;
        // Get type
        String targetType = (String) view.getTag(R.id.target_type);
        // Get url
        String targetUrl = (String) view.getTag(R.id.target_url);
        // Get title
        String targetTitle = (String) view.getTag(R.id.target_title);
        Log.i(TAG, "CLICK TARGET: TYPE:" + targetType + " TITLE:" + targetTitle + " URL:" + targetUrl);
        // Get target type
        TeaserTargetType target = TeaserTargetType.byString(targetType);
        switch (target) {
            case CATALOG:
                gotoCatalog(targetUrl, targetTitle);
                break;
            case CAMPAIGN:
                gotoCampaignPage();
                break;
            case STATIC_PAGE:
                gotoStaticPage(targetTitle, targetUrl);
                break;
            case PDV:
                gotoProductDetail(targetUrl);
                break;
            case UNKNOWN:
            default:
                intercepted = false;
                Log.w(TAG, "WARNING: RECEIVED UNKNOWN TARGET TYPE: " + targetType);
                break;
        }
        return intercepted;
    }

    /**
     * Goto catalog page
     */
    private void gotoCatalog(String title, String url) {
        Log.i(TAG, "GOTO CATALOG PAGE: " + title + " " + url);
        // Validate url
        if (!TextUtils.isEmpty(url)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            bundle.putBoolean(ConstantsIntentExtra.REMOVE_ENTRIES, false);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Goto product detail
     */
    private void gotoProductDetail(String url) {
        Log.i(TAG, "GOTO PRODUCT DETAIL: " + url);
        // Validate url
        if (!TextUtils.isEmpty(url)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Goto static page
     */
    private void gotoStaticPage(String title, String url) {
        Log.i(TAG, "GOTO STATIC PAGE: " + title + " " + url);
        // Validate url
        if (!TextUtils.isEmpty(url)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
            getBaseActivity().onSwitchFragment(FragmentType.INNER_SHOP, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Goto campaign page
     */
    private void gotoCampaignPage() {
        // Get campaign group
        int position = TeaserGroupType.CAMPAIGN_TEASERS.ordinal();
        // Validate
        if(mHomePage != null && mHomePage.hasTeasers()) {
            // Campaign group
            BaseTeaserGroupType group = mHomePage.getTeasers().get(position);
            ArrayList<TeaserCampaign> list = createCampaign(group);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, list);
            getBaseActivity().onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Create an array with a single campaign
     * @return ArrayList with one campaign
     * @author sergiopereira
     */
    private ArrayList<TeaserCampaign> createCampaign(BaseTeaserGroupType group) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        for (BaseTeaserObject baseTeaserObject : group.getData()) {
            TeaserCampaign campaign = new TeaserCampaign();
            campaign.setTitle(baseTeaserObject.getTitle());
            campaign.setUrl(baseTeaserObject.getUrl());
            campaigns.add(campaign);
        }
        return campaigns;
    }

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
        triggerContentEvent(new GetHomeHelper(), null, this);
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
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
            case GET_HOME_EVENT:
                Log.i(TAG, "ON GET_HOME_EVENT");
                HomePageObject homePage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                if (homePage != null && homePage.hasTeasers()) {
                    Log.i(TAG, "SHOW HOME PAGE: " + homePage.hasTeasers());
                    // Save home page
                    mHomePage = homePage;
                    // Build home page
                    buildHomePage(homePage);
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
     * Show promotions
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
        // Check home types
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
