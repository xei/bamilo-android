/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.objects.home.type.TeaserGroupType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show all campaigns 
 * @author sergiopereira
 */
public class CampaignsFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CampaignsFragment.class);
    
    public static final String CAMPAIGNS_TAG = "campaigns";
    
    public static final String CAMPAIGN_POSITION_TAG = "campaign_position";

    /*
    private ViewPager mCampaignPager;

    private CampaignPagerAdapter mCampaignPagerAdapter;

    private ArrayList<TeaserCampaign> mCampaigns;

    private SlidingTabLayout mCampaignPagerTabStrip;
    */
    
    private boolean isFromBanner;

    private TeaserGroupType mGroupType;

    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static CampaignsFragment newInstance(Bundle bundle) {
        CampaignsFragment fragment = new CampaignsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public CampaignsFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Unknown,
                R.layout.campaign_fragment_main,
                R.string.campaigns_label,
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
        // Verify if campaign page was open via a banner
        Bundle args = getArguments();
        if(args != null) {
            isFromBanner = args.getBoolean(ConstantsIntentExtra.BANNER_TRACKING);
            if(args.getSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE) != null){
                mGroupType =(TeaserGroupType) args.getSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE);
            }
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
        // Get campaigns from arguments
        ArrayList<TeaserCampaign> mCampaigns = getArguments().getParcelableArrayList(CAMPAIGNS_TAG);
        // Get pre selection 
        int selectedPosition = getArguments().getInt(CAMPAIGN_POSITION_TAG);
        // Instantiate a ViewPager and a PagerAdapter.
        // Get view pager 
        ViewPager mCampaignPager = (ViewPager) view.findViewById(R.id.campaign_pager);
        // Get tab pager
        SlidingTabLayout mCampaignPagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.campaign_pager_tab);
        mCampaignPagerTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);
        // Validate the current view
        CampaignPagerAdapter mCampaignPagerAdapter = (CampaignPagerAdapter) mCampaignPager.getAdapter();
        if(mCampaignPagerAdapter != null && mCampaignPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mCampaignPager.setCurrentItem(selectedPosition, true);
        } else {
            //Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mCampaignPagerAdapter = new CampaignPagerAdapter(getChildFragmentManager(), mCampaigns);
            mCampaignPager.setAdapter(mCampaignPagerAdapter);
            mCampaignPagerTabStrip.setViewPager(mCampaignPager);
            // Show the pre selection
            mCampaignPager.setCurrentItem(selectedPosition, true);
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
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
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
     * ########### DIALOGS ###########  
     */    

    
    /**
     * Class used as an simple pager adapter that represents each campaign fragment
     * @author sergiopereira
     */
    private class CampaignPagerAdapter extends FragmentPagerAdapter {
        
        private ArrayList<TeaserCampaign> mCampaigns;
        
        /**
         * Constructor
         * @param fm The fragment manager
         * @param campaigns The list of campaigns
         * @author sergiopereira
         */
        public CampaignPagerAdapter(FragmentManager fm, ArrayList<TeaserCampaign> campaigns) {
            super(fm);
            this.mCampaigns = campaigns;
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ConstantsIntentExtra.BANNER_TRACKING, isFromBanner);
            bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
            bundle.putParcelable(CampaignPageFragment.TAG, this.mCampaigns.get(position));
            return CampaignPageFragment.getInstance(bundle);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return mCampaigns != null ? mCampaigns.size() : 0;
        }
        
        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mCampaigns.get(position).getTargetTitle().toUpperCase();
        }
        
    }

    /*
    @Override
    protected void onClickMaintenanceRetryButton() {
        mCampaignPagerAdapter = new CampaignPagerAdapter(getChildFragmentManager(), mCampaigns);
        mCampaignPager.setAdapter(mCampaignPagerAdapter);
        mCampaignPagerTabStrip.setViewPager(mCampaignPager);
    }
    */
}
