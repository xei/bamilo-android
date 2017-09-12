/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.service.objects.home.TeaserCampaign;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

/**
 * Class used to show all campaigns 
 * @author sergiopereira
 */
public class CampaignsFragment extends BaseFragment {

    private static final String TAG = CampaignsFragment.class.getSimpleName();
    
    public static final String CAMPAIGNS_TAG = "campaigns";
    
    public static final String CAMPAIGN_POSITION_TAG = "campaign_position";

    /**
     * Empty constructor
     */
    public CampaignsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.CAMPAIGN,
                R.layout.campaign_fragment_main,
                R.string.campaigns_label,
                NO_ADJUST_CONTENT);
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
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get campaigns from arguments
        ArrayList<TeaserCampaign> mCampaigns = getArguments().getParcelableArrayList(CAMPAIGNS_TAG);
        // Get pre selection 
        int selectedPosition = getArguments().getInt(CAMPAIGN_POSITION_TAG);

        // in order to make the list RTL
        Collections.reverse(mCampaigns);
        selectedPosition = mCampaigns.size() - selectedPosition - 1;

        // Instantiate a ViewPager and a PagerAdapter.
        // Get view pager 
        ViewPager mCampaignPager = (ViewPager) view.findViewById(R.id.campaign_pager);
        // Validate the current view
        CampaignPagerAdapter mCampaignPagerAdapter = (CampaignPagerAdapter) mCampaignPager.getAdapter();
        if(mCampaignPagerAdapter != null && mCampaignPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mCampaignPager.setCurrentItem(selectedPosition, true);
        } else {
            //Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mCampaignPagerAdapter = new CampaignPagerAdapter(getChildFragmentManager(), mCampaigns);
            mCampaignPager.setAdapter(mCampaignPagerAdapter);
            if (mCampaignPagerAdapter.getCount() < 2) {
                CharSequence title = mCampaignPagerAdapter.getPageTitle(0);
                if (title != null && !title.toString().trim().isEmpty()) {
                    getBaseActivity().setActionBarTitle(title.toString());
                }
            } else {
                setupViewPagerTabs(mCampaignPager);
            }
            // Show the pre selection
            mCampaignPager.setCurrentItem(selectedPosition, true);
        }
    }

    private void setupViewPagerTabs(ViewPager mCampaignPager) {
        getBaseActivity().setUpExtraTabLayout(mCampaignPager);
        TabLayout tabLayout = getBaseActivity().getExtraTabLayout();
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.orange_1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        HoloFontLoader.applyDefaultFont(tabLayout);
    }

    /*
     * (non-Javadoc)
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }
  
    
    /**
     * ########### DIALOGS ###########  
     */    

    
    /**
     * Class used as an simple pager adapter that represents each campaign fragment
     * @author sergiopereira
     */
    private class CampaignPagerAdapter extends FragmentPagerAdapter {
        
        private final ArrayList<TeaserCampaign> mCampaigns;
        
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
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
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
            return mCampaigns.get(position).getTitle().toUpperCase();
        }
        
    }

}
