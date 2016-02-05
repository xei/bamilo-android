package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.teasers.GetHomeHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.database.CategoriesTableHelper;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.HockeyStartup;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.home.holder.BaseTeaserViewHolder;
import com.mobile.utils.home.holder.HomeMainTeaserHolder;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show the home page.
 *
 * @author sergiopereira
 */
public class HomePageFragment extends BaseFragment implements IResponseCallback, TargetLink.OnAppendDataListener, TargetLink.OnCampaignListener {

    private static final String TAG = HomePageFragment.class.getSimpleName();

    private ViewGroup mContainer;

    private HomePageObject mHomePage;

    private NestedScrollView mScrollView;

    private ArrayList<BaseTeaserViewHolder> mViewHolders;

    public static final String SCROLL_STATE_KEY = "scroll";

    public static final String POSITION_STATE_KEY = "position";

    private int[] mScrollSavedPosition;

    private String mRichRelevanceHash;
    /**
     * Constructor via bundle
     *
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static HomePageFragment newInstance(Bundle bundle) {
        HomePageFragment homePageFragment = new HomePageFragment();
        homePageFragment.setArguments(bundle);
        return homePageFragment;
    }

    /**
     * Empty constructor
     */
    public HomePageFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.HOME,
                R.layout.home_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
        // Init position
        HomeMainTeaserHolder.viewPagerPosition = HomeMainTeaserHolder.DEFAULT_POSITION;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Register Hockey
        HockeyStartup.register(getBaseActivity());
        // Get saved scroll position
        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_STATE_KEY)) {
            mScrollSavedPosition = savedInstanceState.getIntArray(SCROLL_STATE_KEY);
            HomeMainTeaserHolder.viewPagerPosition = savedInstanceState.getInt(POSITION_STATE_KEY);
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
        Print.i(TAG, "ON VIEW CREATED");
        // Get scroll view
        mScrollView = (NestedScrollView) view.findViewById(R.id.home_page_scroll);
        // Get recycler view
        mContainer = (ViewGroup) view.findViewById(R.id.home_page_container);
        // Validate shared prefs
        SharedPreferences sharedPrefs = getBaseActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        // Case app has shop id and not in maintenance
        if (!TextUtils.isEmpty(shopId) && !getBaseActivity().isInitialCountry()) {
            onResumeExecution();
        }
        // Case app not shop id and in maintenance country selection
        else {
            showFragmentErrorRetry();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Track page
        trackPage(false);
    }

    /**
     * Method used to resume the content for Home Fragment
     *
     * @author sergiopereira
     */
    public void onResumeExecution() {
        Print.i(TAG, "ON RESUME EXECUTION");
        // Validate current state
        if(mHomePage != null && mHomePage.hasTeasers()) {
            validateDataState();
        } else {
            triggerTeasers();
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
        Print.i(TAG, "ON SAVE INSTANCE");
        // Save the scroll state for rotation
        if (saveScrollState() && mScrollSavedPosition != null) {
            outState.putIntArray(SCROLL_STATE_KEY, mScrollSavedPosition);
        }

        if(!CollectionUtils.isEmpty(mViewHolders)) {
            for (BaseTeaserViewHolder baseTeaserViewHolder : mViewHolders) {
                if (baseTeaserViewHolder instanceof HomeMainTeaserHolder) {
                    outState.putInt(POSITION_STATE_KEY, ((HomeMainTeaserHolder) baseTeaserViewHolder).getViewPagerPosition());
                }
            }
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
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
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
        Print.i(TAG, "ON DESTROY");
        // Clean data
        mHomePage = null;
        mViewHolders = null;
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
        Print.i(TAG, "REBUILD HOME PAGE");
        // Update each view older
        for (BaseTeaserViewHolder viewHolder : mViewHolders) {
            // Update view
            viewHolder.onUpdate();
            // Add view
            mContainer.addView(viewHolder.itemView);
        }
        // Restore the scroll state
        //restoreScrollState();
        // Show mContainer
        showFragmentContentContainer();
    }

    /**
     * Create the home page
     */
    private void buildHomePage(HomePageObject homePage) {
        Print.i(TAG, "BUILD HOME PAGE");
        LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
        mViewHolders = new ArrayList<>();
        for (BaseTeaserGroupType baseTeaserType : homePage.getTeasers().values()) {

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
        //restoreScrollState();
        // Show mContainer
        showFragmentContentContainer();
    }

    /**
     * Method to save the current scroll state
     * @return int[]
     */
    private boolean saveScrollState() {
        Print.i(TAG, "ON SAVE SCROLL STATE");
        // Validate view
        if (mScrollView != null) {
            mScrollSavedPosition = new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()};
            return true;
        }
        return false;
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
        super.onClick(view);

        if(view.getId() == R.id.send_newsletter){
            if(view.getTag() instanceof BaseResponse){
                getBaseActivity().dismissProgress();
                final BaseResponse response = (BaseResponse) view.getTag();
                if(response.hadSuccess()) showWarningSuccessMessage(response.getSuccessMessage());
                else showWarningErrorMessage(response.getErrorMessage());
            } else {
                getBaseActivity().showProgress();
            }

        } else {
            /**
             * Try fix https://rink.hockeyapp.net/manage/apps/33641/app_versions/163/crash_reasons/108483846
             */
            try {
                // Validated clicked view
                onClickTeaserItem(view);
            } catch (NullPointerException e) {
                showUnexpectedErrorWarning();
            }
        }
    }

    /**
     * Process the click on teaser
     */
    private void onClickTeaserItem(View view) {
        Print.i(TAG, "ON CLICK TEASER ITEM");
        // Get title
        String title = (String) view.getTag(R.id.target_title);
        // Get target link
        @TargetLink.Type String link = (String) view.getTag(R.id.target_link);
        // Get origin id
        int id = (int) view.getTag(R.id.target_teaser_origin);
        Print.i(TAG, "CLICK TARGET: LINK:" + link + " TITLE:" + title + " ORIGIN:" + id);
        // Get teaser group type
        TeaserGroupType origin = TeaserGroupType.values()[id];
        if (view.getTag(R.id.target_list_position) != null) {
            origin.setTrackingPosition((int) view.getTag(R.id.target_list_position));
            TrackerDelegator.trackBannerClicked(origin, TargetLink.getIdFromTargetLink(link), (int) view.getTag(R.id.target_list_position));
        }

        if(origin == TeaserGroupType.TOP_SELLERS){
            // Get Rich Relevance hash
            mRichRelevanceHash = (String) view.getTag(R.id.target_rr_hash);
        } else {
            mRichRelevanceHash = "";
        }
        // Parse target link
        boolean result = new TargetLink(getWeakBaseActivity(), link)
                .addTitle(title)
                .setOrigin(origin)
                .addAppendListener(this)
                .addCampaignListener(this)
                .retainBackStackEntries()
                .run();
        // Validate result
        if(!result) {
            showUnexpectedErrorWarning();
        }
    }

    /**
     * Append some data
     */
    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle bundle) {
        if(next == FragmentType.PRODUCT_DETAILS) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            if(TextUtils.isNotEmpty(mRichRelevanceHash)){
                bundle.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRichRelevanceHash );
            }
        }
        else if(next == FragmentType.CATALOG) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            CategoriesTableHelper.updateCategoryCounter(id, title);
        }
    }

    /**
     * Process create the bundle for campaigns.
     */
    @NonNull
    @Override
    public Bundle onTargetCampaign(String title, String id, TeaserGroupType origin) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, origin);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, createCampaignsData(title, id, origin));
        return bundle;
    }

    /**
     * Create a list with campaigns.
     */
    @NonNull
    private ArrayList<TeaserCampaign> createCampaignsData(@NonNull String title, @NonNull String id, TeaserGroupType group) {
        Print.i(TAG, "GOTO CAMPAIGN PAGE: " + title + " " + id);
        // Object
        ArrayList<TeaserCampaign> campaigns;
        // Get group
        BaseTeaserGroupType campaignGroup = mHomePage.getTeasers().get(group.getType());
        // Case from campaigns
        if (group == TeaserGroupType.CAMPAIGNS) {
            //Print.i(TAG, "code1campaigns group == TeaserGroupType.CAMPAIGNS");
            campaigns = TargetLink.createCampaignList(campaignGroup);
        }
        // Case from other
        else {
            //Print.i(TAG, "code1campaigns createCampaignList");
            campaigns = TargetLink.createCampaignList(title, id);
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
        // Fix: java.lang.IllegalStateException
        // The specified child already has a parent.
        // You must call removeView() on the child's parent first.
        TeaserViewFactory.onDetachedViewHolder(mViewHolders);
        // Reload
        onResumeExecution();
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
        Print.d(TAG, "ON TRIGGER: GET TEASERS");
        // Get teaser collection
        triggerContentEvent(new GetHomeHelper(), null, this);
    }

    /*
     * ########### RESPONSES ###########
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT:
                Print.i(TAG, "ON SUCCESS RESPONSE: GET_HOME_EVENT");
                HomePageObject homePage = (HomePageObject) baseResponse.getContentData();
                if (homePage != null && homePage.hasTeasers()) {
                    Print.i(TAG, "SHOW HOME PAGE: " + homePage.hasTeasers());
                    // Save home page
                    mHomePage = homePage;
                    // Build home page
                    buildHomePage(homePage);
                } else {
                    Print.i(TAG, "SHOW FALL BAK");
                    showFragmentFallBack();
                }
                break;
            default:
                break;
        }

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
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Check base errors
        if (super.handleErrorEvent(baseResponse)) return;
        // Check home types
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT:
                Print.i(TAG, "ON ERROR RESPONSE: GET_HOME_EVENT");
                showFragmentFallBack();
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
            Print.w(TAG, "WARNING: ISE ON TRACK PAGE ADJUST");
        }
    }

}
