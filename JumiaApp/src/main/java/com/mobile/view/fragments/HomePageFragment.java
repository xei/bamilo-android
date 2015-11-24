package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.widget.NestedScrollView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.teasers.GetHomeHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.database.CategoriesTableHelper;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.objects.home.type.TeaserTargetType;
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
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.home.holder.BaseTeaserViewHolder;
import com.mobile.utils.home.holder.HomeMainTeaserHolder;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show the home page.
 *
 * @author sergiopereira
 */
public class HomePageFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = HomePageFragment.class.getSimpleName();

    private ViewGroup mContainer;

    private HomePageObject mHomePage;

    private NestedScrollView mScrollView;

    private ArrayList<BaseTeaserViewHolder> mViewHolders;

    public static final String SCROLL_STATE_KEY = "scroll";

    public static final String POSITION_STATE_KEY = "position";

    private int[] mScrollSavedPosition;

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

//    /**
//     * Restore the saved scroll position
//     * @author sergiopereira
//     */
//    private void restoreScrollState() {
//        Print.i(TAG, "ON RESTORE SCROLL SAVED STATE");
//        // Has saved position
//        if (mScrollSavedPosition != null) {
//            // Wait until my scrollView is ready and scroll to saved position
//            try {
//                mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @SuppressWarnings("deprecation")
//                    @Override
//                    public void onGlobalLayout() {
//                        mScrollView.scrollTo(mScrollSavedPosition[0], mScrollSavedPosition[1]);
//                        mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    }
//                });
//            } catch (NullPointerException | IllegalStateException e) {
//                Log.w(TAG, "WARNING: EXCEPTION ON SCROLL TO SAVED STATE", e);
//            }
//        }
//    }

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
        Print.i(TAG, "ON CLICK TEASER ITEM");
        // Flag to mark as intercepted
        boolean intercepted = true;
        // Get type
        String targetType = (String) view.getTag(R.id.target_type);
        // validate the state of the view when clicking on the retry button on the Home page
        if(TextUtils.isEmpty(targetType)){
          return false;
        }
        // Get url
        String targetUrl = (String) view.getTag(R.id.target_url);
        // Get title
        String targetTitle = (String) view.getTag(R.id.target_title);
        // Get origin id
        int origin = (int) view.getTag(R.id.target_teaser_origin);
        // Get Sku
        String targetSku = (String) view.getTag(R.id.target_sku);
        // Get teaser group type
        TeaserGroupType originGroupType = TeaserGroupType.values()[origin];
        if(view.getTag(R.id.target_list_position) != null){
            originGroupType.setTrackingPosition((int) view.getTag(R.id.target_list_position));
            TrackerDelegator.trackBannerClicked(originGroupType, targetUrl, (int) view.getTag(R.id.target_list_position));
        }
        Print.i(TAG, "CLICK TARGET: TYPE:" + targetType + " TITLE:" + targetTitle + " URL:" + targetUrl);
        // Get target type
        TeaserTargetType target = TeaserTargetType.byString(targetType);
        switch (target) {
            case CATALOG:
                gotoCatalog(targetTitle, targetUrl, originGroupType);
                break;
            case CAMPAIGN:
                gotoCampaignPage(targetTitle, targetUrl, originGroupType);
                break;
            case STATIC_PAGE:
                gotoStaticPage(targetTitle, targetUrl, originGroupType);
                break;
            case PRODUCT_DETAIL:
                //TODO this validation is only temporary, and should be removed in next release
                if(TextUtils.isEmpty(targetSku))
                    targetSku = getSkuFromUrl(targetUrl);
                gotoProductDetail(targetSku, originGroupType);
                break;
            case UNKNOWN:
            default:
                intercepted = false;
                Print.w(TAG, "WARNING: RECEIVED UNKNOWN TARGET TYPE: " + targetType);
                break;
        }
        return intercepted;
    }

    /**
     * Goto catalog page
     */
    private void gotoCatalog(String title, String url, TeaserGroupType groupType) {
        Print.i(TAG, "GOTO CATALOG PAGE: " + title + " " + url);
        // Update counter for tracking
        CategoriesTableHelper.updateCategoryCounter(url, title);
        // Go to bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
        bundle.putBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES, false);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, groupType);
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Goto product detail using Sku
     */
    private void gotoProductDetail(String sku, TeaserGroupType groupType) {
        Print.i(TAG, "GOTO PRODUCT DETAIL: " + sku);
        if(TextUtils.isNotEmpty(sku)){
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, sku);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, groupType);
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_fb_permission_not_granted));
        }
    }

    /**
     * Goto static page
     */
    private void gotoStaticPage(String title, String url, TeaserGroupType groupType) {
        Print.i(TAG, "GOTO STATIC PAGE: " + title + " " + url);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, groupType);
        getBaseActivity().onSwitchFragment(FragmentType.INNER_SHOP, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Goto campaign page
     */
    private void gotoCampaignPage(String targetTitle, String targetUrl, TeaserGroupType groupType) {
        Print.i(TAG, "GOTO CAMPAIGN PAGE: " + targetTitle + " " + targetUrl);
        // Get group
        BaseTeaserGroupType group = mHomePage.getTeasers().get(groupType.ordinal());
        // Case campaign origin
        ArrayList<TeaserCampaign> campaigns;
        if (groupType == TeaserGroupType.CAMPAIGNS) {
            campaigns = createCampaign(group);
        }
        // Case other origin
        else {
            campaigns = new ArrayList<>();
            TeaserCampaign campaign = new TeaserCampaign();
            campaign.setTitle(targetTitle);
            campaign.setUrl(targetUrl);
            campaigns.add(campaign);
        }
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, campaigns);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, groupType);
        // Switch
        getBaseActivity().onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);
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
                HomePageObject homePage = (HomePageObject) baseResponse.getMetadata().getData();
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
