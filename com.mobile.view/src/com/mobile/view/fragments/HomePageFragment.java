/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.TeasersFactory;
import com.mobile.framework.objects.Homepage;
import com.mobile.framework.objects.ITargeting.TargetType;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.objects.TeaserGroupCampaigns;
import com.mobile.framework.objects.TeaserGroupType;
import com.mobile.framework.objects.TeaserSpecification;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.ScrollViewWithHorizontal;
import com.mobile.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show an home page
 * @author sergiopereira
 */
public class HomePageFragment extends BaseFragment implements OnClickListener {

    public static final String TAG = LogTagHelper.create(HomePageFragment.class);
    
    public static final String HOME_PAGE_KEY = "homepage";
    
    public static final String SCROLL_STATE_KEY = "scroll";

    private LayoutInflater mInflater;

    private ScrollViewWithHorizontal mScrollViewWithHorizontal;

    private Homepage mHomePage;

    private int[] mScrollSavedPosition;

    private ArrayList<TeaserCampaign> mCampaigns;
    
    /**
     * Constructor via bundle
     * @return CampaignFragment
     * @author sergiopereira
     */
    public static HomePageFragment getInstance(Bundle bundle) {
        HomePageFragment homePageFragment = new HomePageFragment();
        homePageFragment.setArguments(bundle);
        return homePageFragment;
    }

    /**
     * Empty constructor
     */
    public HomePageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.home_page_fragment);
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
        // Get home page from arguments
        mHomePage = getArguments().getParcelable(HOME_PAGE_KEY);
        
        // Validate the saved state
        if(savedInstanceState != null && savedInstanceState.containsKey(HOME_PAGE_KEY)){
            Log.i(TAG, "ON GET SAVED STATE");
            if(mHomePage == null) mHomePage = savedInstanceState.getParcelable(HOME_PAGE_KEY);
        }
        
        // Get saved scroll position
        if(savedInstanceState != null && savedInstanceState.containsKey(SCROLL_STATE_KEY)) {
            mScrollSavedPosition = savedInstanceState.getIntArray(SCROLL_STATE_KEY);
            Log.d(TAG, "SCROLL POS: " + mScrollSavedPosition[0] + " " + mScrollSavedPosition[1]);
        }
       
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mInflater = LayoutInflater.from(getBaseActivity());
        // Get portrait container
        LinearLayout singleContainer = (LinearLayout) view.findViewById(R.id.home_page_single_container);
        // Get scroll view
        mScrollViewWithHorizontal = (ScrollViewWithHorizontal) view.findViewById(R.id.home_page_single_scrollview);
        
        // Get landscape containers
        ViewGroup leftContainer = (ViewGroup) view.findViewById(R.id.home_page_left_container);
        ViewGroup rightContainer = (ViewGroup) view.findViewById(R.id.home_page_right_container);
        ViewGroup rightContainerCategories = (ViewGroup) view.findViewById(R.id.home_page_right_container_categories);
        ViewGroup rightContainerBrands = (ViewGroup) view.findViewById(R.id.home_page_right_container_brands);
        
        // Validate current home 
        if (mHomePage != null) {
            // CASE portrait
            if(singleContainer != null) showHomePage(mHomePage, singleContainer);
            // CASE landscape
            else showHomePage(mHomePage, leftContainer, rightContainer, rightContainerCategories, rightContainerBrands);
        } else {
            // CASE homepage null
            Log.w(TAG, "WARNING: HOME PAGE IS NULL"); 
            showRetry(); 
        }
    }
        
    /*
     * (non-Javadoc)
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
        // Save the scroll state for background
        savedScrollState();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE: HOME PAGE");
        // Save home page
        outState.putParcelable(HOME_PAGE_KEY, mHomePage);
        // Save the scroll state for rotation
        if(savedScrollState() && mScrollSavedPosition != null) outState.putIntArray(SCROLL_STATE_KEY, mScrollSavedPosition);
    }
    
    /**
     * Method to save the current scroll state
     * @return int[]
     * @author sergiopereira
     */
    private boolean savedScrollState(){
        Log.i(TAG, "ON SAVE SCROLL STATE");
        // Validate view
        if(mScrollViewWithHorizontal == null) return false;
        // Save state
        mScrollSavedPosition = new int[]{ mScrollViewWithHorizontal.getScrollX(), mScrollViewWithHorizontal.getScrollY()};
        return true;
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }
    
    /**
     * Show the current home page
     * @param homePage
     * @param mainView
     * @author sergiopereira
     */
    private void showHomePage(Homepage homePage, LinearLayout mainView) {
        Log.i(TAG, "SHOW HOME WITH TEASERS");
        // Create and show each teaser
        createAndShowTeasers(homePage, mainView);
        // Restore the scroll state
        restoreScrollState();
        // Force to show content
        showContent();
    }
    
    
    /**
     * Show the current home page for landscape
     * @param homePage
     * @param mainView
     * @author sergiopereira
     * @param rightViewBrands 
     * @param rightViewCategories 
     * @author sergiopereira
     */
    private void showHomePage(Homepage homePage, ViewGroup leftView, ViewGroup rightView, ViewGroup rightViewCategories, ViewGroup rightViewBrands) {
        Log.i(TAG, "SHOW HOME WITH TEASERS");
        // Create the teaser factory
        TeasersFactory mTeasersFactory = new TeasersFactory(getBaseActivity(), mInflater, (OnClickListener) this);
        mTeasersFactory.setContainerWidthToLoadImage(DeviceInfoHelper.getWidth(getBaseActivity()) / 2); // For product list
        // For each teaser create a view and add to container
        for ( TeaserSpecification<?> teaser : homePage.getTeaserSpecification()) {
            switch (teaser.getType()) {
            // CASE LEFT SIDE
            case CAMPAIGNS_LIST:
                // Save campaigns
                mCampaigns = ((TeaserGroupCampaigns) teaser).getTeasers();
            case MAIN_ONE_SLIDE:
            case STATIC_BANNER:
            case BRANDS_LIST:
                leftView.addView(mTeasersFactory.getSpecificTeaser(leftView, teaser));
                break;
            // CASE RIGHT SIDE
            case PRODUCT_LIST:
                rightView.addView(mTeasersFactory.getSpecificTeaser(rightView, teaser));
                break;
            case CATEGORIES:
                rightViewCategories.addView(mTeasersFactory.getSpecificTeaser(rightViewCategories, teaser));
                rightViewCategories.setVisibility(View.VISIBLE);
                break;
            case TOP_BRANDS_LIST:
                rightViewBrands.addView(mTeasersFactory.getSpecificTeaser(rightViewBrands, teaser));
                rightViewBrands.setVisibility(View.VISIBLE);
                break;
            default:
                break;
            }
        }
        
        // Force to show content
        showContent();
    }
    
    /**
     * Create and add the each teaser to main container
     * @param homePage
     * @param mainView
     * @author sergiopereira
     */
    private void createAndShowTeasers(Homepage homePage, LinearLayout mainView){
        // Create the teaser factory
        //TeasersFactory mTeasersFactory = new TeasersFactory(getBaseActivity(), mInflater, (OnClickListener) this);
        TeasersFactory mTeasersFactory = TeasersFactory.getSingleton(getBaseActivity(), mInflater, this);
        // For each teaser create a view and add to container
        for ( TeaserSpecification<?> teaser : homePage.getTeaserSpecification()) {
            mainView.addView(mTeasersFactory.getSpecificTeaser(mainView, teaser));
            // Save campaigns
            if(teaser.getType() == TeaserGroupType.CAMPAIGNS_LIST) mCampaigns = ((TeaserGroupCampaigns) teaser).getTeasers();
        }
    }
    
    /**
     * Restore the saved scroll position
     * @author sergiopereira
     */
    private void restoreScrollState() {
        Log.i(TAG, "ON RESTORE SCROLL SAVED STATE");
        // Validate state 
        if(mScrollSavedPosition != null) {
            // Scroll to saved position
            mScrollViewWithHorizontal.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "SCROLL TO POS: " + mScrollSavedPosition[0] + " " + mScrollSavedPosition[1]);
                    mScrollViewWithHorizontal.scrollTo(mScrollSavedPosition[0], mScrollSavedPosition[1]);
                }
            });
        }
    }
    
    /**
     * Show only the content view
     * @author sergiopereira
     */
    private void showContent() {
        showFragmentContentContainer();
    }
    
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        showFragmentRetry((OnClickListener) this);
    }
    
    /**
     * ############# LISTENERS #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Retry button
        if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Teaser item
        else onClickTeaserItem(view);
    }
    
    /**
     * Process the click on retry button
     */
    private void onClickRetryButton() {
        // Send to parent reload content
        Log.i(TAG, "ON CLICK RETRY");
        Fragment parent = getParentFragment();
        // Validate parent
        if (parent != null && parent instanceof HomeFragment) ((HomeFragment) parent).onReloadContent();
    }
    
    /**
     * Process the click on teaser
     * @param view
     * @author sergiopereira
     */
    private void onClickTeaserItem(View view) {
        Log.i(TAG, "ON CLICK TEASER ITEM");
        // Get url
        String targetUrl = (String) view.getTag(R.id.target_url);
        // Get type
        TargetType targetType = (TargetType) view.getTag(R.id.target_type);
        // Get title
        String targetTitle = (String) view.getTag(R.id.target_title);
        // Get origin
        TeaserGroupType originType = (TeaserGroupType) view.getTag(R.id.origin_type);
        // Validate type
        if (targetType != null) {
            Bundle bundle = new Bundle();
            Log.d(TAG, "targetType = " + targetType.name() + " targetUrl = " + targetUrl);
            switch (targetType) {
            case CATEGORY:
                onClickCategory(targetUrl, bundle);
                break;
            case PRODUCT_LIST:
                onClickCatalog(targetUrl, targetTitle, bundle);
                break;
            case PRODUCT:
                onClickProduct(targetUrl, bundle);
                break;
            case BRAND:
                onClickBrand(targetUrl, bundle);
                break;
            case CAMPAIGN:
                onClickCampaign(view, originType, targetUrl, targetTitle, bundle);
                break;
            default:
                Log.w(TAG, "WARNING ON CLICK: UNKNOWN VIEW");
                break;
            }
        }
    }
    
    /**
     * Process the category click
     * @param targetUrl
     * @param bundle
     * @author sergiopereira
     */
    private void onClickCategory(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK CATEGORY");
        bundle.putString(ConstantsIntentExtra.CATEGORY_URL, targetUrl);
        getBaseActivity().onSwitchFragment(FragmentType.CATEGORIES, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the catalog click
     * @param targetUrl
     * @param targetTitle
     * @param bundle
     */
    private void onClickCatalog(String targetUrl, String targetTitle, Bundle bundle) {
        Log.i(TAG, "ON CLICK CATALOG");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, true);
        } else Log.w(TAG, "WARNING: URL IS NULL"); 
    }
    
    /**
     * Process the product click
     * @param targetUrl
     * @param bundle
     * @author sergiopereira
     */
    private void onClickProduct(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK PRODUCT");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else Log.i(TAG, "WARNING: URL IS NULL");
    }
    
    /**
     * Process the brand click
     * @param targetUrl
     * @param bundle
     */
    private void onClickBrand(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK BRAND");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetUrl);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, targetUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);                            
        } else Log.i(TAG, "WARNING: URL IS NULL");
    }
    
    /**
     * Process the campaign click.
     * @param view
     * @param targetUrl
     * @param targetTitle
     * @param bundle
     */
    private void onClickCampaign(View view, TeaserGroupType origin, String targetUrl, String targetTitle, Bundle bundle) {
        // Get selected position
        String targetPosition = view.getTag(R.id.position) != null ? view.getTag(R.id.position).toString() : "0";
        // Case teaser campaign
        if (origin == TeaserGroupType.CAMPAIGNS_LIST && hasSavedTeaserCampaigns()) {
            Log.i(TAG, "ON CLICK CAMPAIGN FROM CAMPAIGN TEASER: " + targetTitle + " " + targetUrl + " " + targetPosition);
            // Tracking event
            AnalyticsGoogle.get().trackEvent(TrackingEvent.SHOW_CAMPAIGN, targetTitle, 0l);
            // Show campaigns 
            gotoCampaigns(mCampaigns, targetPosition, bundle);
        }
        // Case teaser image
        else if((origin == TeaserGroupType.MAIN_ONE_SLIDE || origin == TeaserGroupType.STATIC_BANNER) && !TextUtils.isEmpty(targetUrl)) {
            Log.i(TAG, "ON CLICK CAMPAIGN FROM IMAGE TEASER: " + targetTitle + " " + targetUrl + " " + targetPosition);
            // Tracking event
            AnalyticsGoogle.get().trackEvent(TrackingEvent.SHOW_CAMPAIGN, targetTitle, 0l);
            // Create campaign using the URL
            ArrayList<TeaserCampaign> campaigns = createSignleCampaign(targetTitle, targetUrl);
            // Default position
            targetPosition = "0";
            // Show campaign
            gotoCampaigns(campaigns, targetPosition, bundle);
        }
        // Case unknown
        else Log.w(TAG, "WARNING: NPE OR UNKNOWN CLICK FOR CAMPAIGNS: " + targetTitle + " " + targetUrl + " " + targetPosition + " " + hasSavedTeaserCampaigns());
    }
    
    /**
     * Show {@link CampaignsFragment}
     * @param campaigns
     * @param targetPosition
     * @param bundle
     * @author sergiopereira
     */
    private void gotoCampaigns(ArrayList<TeaserCampaign> campaigns, String targetPosition, Bundle bundle) {
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, campaigns);
        bundle.putInt(CampaignsFragment.CAMPAIGN_POSITION_TAG, Integer.valueOf(targetPosition));
        getBaseActivity().onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);     
    }

    /**
     * Create an array with a single campaign
     * @param targetTitle
     * @param targetUrl
     * @return ArrayList with one campaign
     * @author sergiopereira
     */
    private ArrayList<TeaserCampaign> createSignleCampaign(String targetTitle, String targetUrl) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<TeaserCampaign>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(targetTitle);
        campaign.setUrl(targetUrl);
        campaigns.add(campaign);
        return campaigns; 
    }
    
    /**
     * Validate if the current homepage has campaigns
     * @return true or false
     * @author sergiopereira
     */
    private boolean hasSavedTeaserCampaigns() {
        return (mCampaigns != null) ? true : false;
    }
    
}
