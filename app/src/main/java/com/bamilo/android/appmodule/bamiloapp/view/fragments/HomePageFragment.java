package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.teasers.GetHomeHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.BaseTeaserViewHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeMainTeaserHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeNewsletterTeaserHolder;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.database.CategoriesTableHelper;
import com.bamilo.android.framework.service.objects.home.HomePageObject;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.AdjustTracker;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.util.ArrayList;

/**
 * Class used to show the home page.
 *
 * @author sergiopereira
 */
public class HomePageFragment extends BaseFragment implements IResponseCallback,
        TargetLink.OnAppendDataListener, TargetLink.OnCampaignListener {

    private static final String TAG = HomePageFragment.class.getSimpleName();

    private ViewGroup mContainer;

    private HomePageObject mHomePage;

    private NestedScrollView mScrollView;
//    RecommendationsHolder recommendationsTeaserHolder;
    private boolean recommendationsTeaserHolderAdded = false;

    private ArrayList<BaseTeaserViewHolder> mViewHolders;

    public static final String SCROLL_STATE_KEY = "scroll";

    public static final String POSITION_STATE_KEY = "position";
    public static final String NEWSLETTER_EMAIL_KEY = "newsletter_email_key";
    public static final String NEWSLETTER_GENDER_KEY = "newsletter_gender_key";

    private int[] mScrollSavedPosition;

    private String mRichRelevanceHash;

    //DROID-10
    private long mGABeginRequestMillis;
//    private RecommendManager recommendManager;

    /**
     * Empty constructor
     */
    public HomePageFragment() {
        /*super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET),
                NavigationAction.HOME,
                R.layout.home_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);*/
        super(true, R.layout.home_fragment_main);
        // Init position
        HomeMainTeaserHolder.sViewPagerPosition = HomeMainTeaserHolder.DEFAULT_POSITION;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get saved scroll position
        if (savedInstanceState != null) {
            mScrollSavedPosition = savedInstanceState.getIntArray(SCROLL_STATE_KEY);
            HomeMainTeaserHolder.sViewPagerPosition = savedInstanceState.getInt(POSITION_STATE_KEY);
            HomeNewsletterTeaserHolder.sInitialValue = savedInstanceState
                    .getString(NEWSLETTER_EMAIL_KEY);
            HomeNewsletterTeaserHolder.sInitialGender = savedInstanceState
                    .getInt(NEWSLETTER_GENDER_KEY);
        } else {
            HomeNewsletterTeaserHolder.sInitialValue = null;
            HomeNewsletterTeaserHolder.sInitialGender = IntConstants.INVALID_POSITION;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mScrollView != null) {
                getBaseActivity().syncSearchBarState(mScrollView.getScrollY());
            }
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
        // Get scroll view
        mScrollView = (NestedScrollView) view.findViewById(R.id.home_page_scroll);
        mScrollView.setClipToPadding(false);
        getBaseActivity().enableSearchBar(true, mScrollView);
        // Get recycler view
        mContainer = (ViewGroup) view.findViewById(R.id.home_page_container);
        // Validate shared prefs
        SharedPreferences sharedPrefs = getBaseActivity()
                .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        // Track page
        trackPage(false);

    }

    /**
     * Method used to resume the content for Home Fragment
     *
     * @author sergiopereira
     */
    public void onResumeExecution() {
        // Validate current state
        if (mHomePage != null && mHomePage.hasTeasers()) {
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
        // Save the scroll state for rotation
        if (saveScrollState() && mScrollSavedPosition != null) {
            outState.putIntArray(SCROLL_STATE_KEY, mScrollSavedPosition);
        }

        if (!CollectionUtils.isEmpty(mViewHolders)) {
            for (BaseTeaserViewHolder baseTeaserViewHolder : mViewHolders) {
                if (baseTeaserViewHolder instanceof HomeMainTeaserHolder) {
                    outState.putInt(POSITION_STATE_KEY,
                            ((HomeMainTeaserHolder) baseTeaserViewHolder).getViewPagerPosition());
                } else if (baseTeaserViewHolder instanceof HomeNewsletterTeaserHolder) {
                    outState.putString(NEWSLETTER_EMAIL_KEY,
                            ((HomeNewsletterTeaserHolder) baseTeaserViewHolder).getEditedText());
                    outState.putInt(NEWSLETTER_GENDER_KEY,
                            ((HomeNewsletterTeaserHolder) baseTeaserViewHolder)
                                    .getSelectedGender());
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
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        if (CollectionUtils.isNotEmpty(mViewHolders)) {
            rebuildHomePage(mViewHolders);
        } else {
            buildHomePage(mHomePage);
        }
    }

    /**
     * Rebuild the home updating all views.<br> Warning: To perform this method is necessary
     * detached view from parent.
     */
    private void rebuildHomePage(ArrayList<BaseTeaserViewHolder> mViewHolders) {
        // Update each view older
        for (BaseTeaserViewHolder viewHolder : mViewHolders) {
            // Update view
            viewHolder.onUpdate();
            // Add view
            mContainer.addView(viewHolder.itemView);
        }

//        recommendManager = new RecommendManager();
//        sendRecommend();

        showFragmentContentContainer();


    }

    /**
     * Create the home page
     */
    private void buildHomePage(HomePageObject homePage) {
        LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
        mViewHolders = new ArrayList<>();
        for (BaseTeaserGroupType baseTeaserType : homePage.getTeasers().values()) {
            // Create view
            BaseTeaserViewHolder viewHolder = TeaserViewFactory
                    .onCreateViewHolder(inflater, baseTeaserType.getType(), mContainer, this);
            if (viewHolder != null) {
                // Set view
                viewHolder.onBind(baseTeaserType);
                // Add to container
                mContainer.addView(viewHolder.itemView);
                // Save
                mViewHolders.add(viewHolder);
            }
        }

//        recommendManager = new RecommendManager();
//        sendRecommend();

        showFragmentContentContainer();
    }

    /**
     * Method to save the current scroll state
     *
     * @return int[]
     */
    private boolean saveScrollState() {
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

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.send_newsletter) {
            getBaseActivity().showProgress();
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
        // Get title
        String title = (String) view.getTag(R.id.target_title);
        // Get target link
        @TargetLink.Type String link = (String) view.getTag(R.id.target_link);
        // Get origin id
        int id = (int) view.getTag(R.id.target_teaser_origin);
        // Get teaser group type
        TeaserGroupType origin = TeaserGroupType.values()[id];
        if (view.getTag(R.id.target_list_position) != null) {
            origin.setTrackingPosition((int) view.getTag(R.id.target_list_position));
        }

        if (origin == TeaserGroupType.TOP_SELLERS) {
            // Get Rich Relevance hash
            mRichRelevanceHash = (String) view.getTag(R.id.target_rr_hash);
        } else {
            mRichRelevanceHash = "";
        }
        // Parse target link
        new TargetLink(getWeakBaseActivity(), link)
                .addTitle(title)
                .setOrigin(origin)
                .addAppendListener(this)
                .addCampaignListener(this)
                .retainBackStackEntries()
                .enableWarningErrorMessage()
                .run();
    }

    /**
     * Append some data
     */
    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle bundle) {
        if (next == FragmentType.PRODUCT_DETAILS) {
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            if (TextUtils.isNotEmpty(mRichRelevanceHash)) {
                bundle.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRichRelevanceHash);
            }
        } else if (next == FragmentType.CATALOG) {
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
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG,
                createCampaignsData(title, id, origin));
        return bundle;
    }

    /**
     * Create a list with campaigns.
     */
    @NonNull
    private ArrayList<TeaserCampaign> createCampaignsData(@NonNull String title, @NonNull String id,
            TeaserGroupType group) {
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
        // Get teaser collection
        triggerContentEvent(new GetHomeHelper(), null, this);
    }

    /*
     * ########### RESPONSES ###########
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT:
                HomePageObject homePage = (HomePageObject) baseResponse.getContentData();
                if (homePage != null && homePage.hasTeasers()) {
                    // Save home page
                    mHomePage = homePage;
                    // Build home page
                    buildHomePage(homePage);
                } else {
                    showFragmentFallBack();
                }

                break;
            case SUBMIT_FORM:// Newsletter Form Response
                getBaseActivity().dismissProgress();
                showWarningSuccessMessage(baseResponse.getSuccessMessage());
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Check base errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Check home types
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_HOME_EVENT:
                showFragmentFallBack();
                break;
            case SUBMIT_FORM:
                getBaseActivity().dismissProgress();
                // Newsletter Form
                if (CollectionUtils.isEmpty(baseResponse.getValidateMessages())) {
                    showWarningErrorMessage(baseResponse.getErrorMessage());
                }
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
        trackPageAdjust();
    }

    /**
     * Track Page only for adjust
     */
    private void trackPageAdjust() {
        try {
            if (isAdded()) {
                Bundle bundle = new Bundle();
                bundle.putString(AdjustTracker.COUNTRY_ISO, BamiloApplication.SHOP_ID);
                bundle.putLong(AdjustTracker.BEGIN_TIME, mLoadTime);
                bundle.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
                if (BamiloApplication.CUSTOMER != null) {
                    bundle.putParcelable(AdjustTracker.CUSTOMER, BamiloApplication.CUSTOMER);
                }
            }
        } catch (IllegalStateException e) {
        }
    }

//    private void sendRecommend() {
//        recommendManager.sendHomeRecommend((category, data) -> {
//            if (data == null || data.size() == 0) {
//                return;
//            }
//            LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
//
//            if (recommendationsTeaserHolder == null) {
//                recommendationsTeaserHolder = new RecommendationsHolder(getBaseActivity(),
//                        inflater.inflate(R.layout.recommendation, mContainer, false), null);
//            }
//            if (recommendationsTeaserHolder != null) {
//                try {
//                    // Set view
//                    mContainer.removeView(recommendationsTeaserHolder.itemView);
//                    recommendationsTeaserHolder = new RecommendationsHolder(getBaseActivity(),
//                            inflater.inflate(R.layout.recommendation, mContainer, false), null);
//                    recommendationsTeaserHolder.onBind(data);
//                    //recommendationsTeaserHolder.itemView.
//                    mContainer.addView(recommendationsTeaserHolder.itemView,
//                            mContainer.getChildCount() - 1);
//                } catch (Exception ex) {
//                    int tmp = 1;
//                }
//
//                recommendationsTeaserHolderAdded = true;
//            }
//        });
//    }
}
