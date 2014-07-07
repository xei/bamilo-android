/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.framework.components.androidslidingtabstrip.SlidingTabLayout;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show all campaigns
 * @author sergiopereira
 */
public class CampaignsFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CampaignsFragment.class);
    
    public static final String CAMPAIGNS_TAG = "campaigns";
    
    public static final String CAMPAIGN_POSITION_TAG = "campaign_position";
    
    private static CampaignsFragment sCampaignsFragment;

    private ViewPager mCampaignPager;

    private CampaignPagerAdapter mCampaignPagerAdapter;

    private ArrayList<TeaserCampaign> mCampaigns;

    private SlidingTabLayout mCampaignPagerTabStrip;
    
    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static CampaignsFragment newInstance(Bundle bundle) {
        sCampaignsFragment = new CampaignsFragment();
        return sCampaignsFragment;
    }

    /**
     * Empty constructor
     */
    public CampaignsFragment() {
        super(EnumSet.of(EventType.GET_CAMPAIGN_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Unknown, 
                0, 
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.campaign_fragment_main, viewGroup, false);
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
        mCampaigns = getArguments().getParcelableArrayList(CAMPAIGNS_TAG);
        // Get pre selection 
        int selectedPosition = getArguments().getInt(CAMPAIGN_POSITION_TAG);
        // Instantiate a ViewPager and a PagerAdapter.
        // Get view pager 
        mCampaignPager = (ViewPager) view.findViewById(R.id.campaign_pager);
        // Get tab pager
        mCampaignPagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.campaign_pager_tab);
        mCampaignPagerTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);
        // Validate the current view
        mCampaignPagerAdapter = (CampaignPagerAdapter) mCampaignPager.getAdapter();
        if(mCampaignPagerAdapter == null) {
            //Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mCampaignPagerAdapter = new CampaignPagerAdapter(getChildFragmentManager(), mCampaigns);
            mCampaignPager.setAdapter(mCampaignPagerAdapter);
            mCampaignPagerTabStrip.setViewPager(mCampaignPager);
        }
        // Show the pre selection
        mCampaignPager.setCurrentItem(selectedPosition, true);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
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
         * @param fm
         * @param campaigns
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
            bundle.putParcelable(CampaignFragment.TAG, this.mCampaigns.get(position)); 
            return CampaignFragment.getInstance(bundle);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return (mCampaigns != null) ? mCampaigns.size() : 0;
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

}
