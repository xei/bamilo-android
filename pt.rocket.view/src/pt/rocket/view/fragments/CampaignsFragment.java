/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CampaignsFragment extends BaseFragment implements OnPageChangeListener {

    private static final String TAG = LogTagHelper.create(CampaignsFragment.class);
    
    public static final String CAMPAIGNS_TAG = "campaigns";
    
    private static CampaignsFragment sCampaignFragment;

    private ViewPager mCampaignPager;

    private CampaignPagerAdapter mCampaignPagerAdapter;

    private ArrayList<TeaserCampaign> mCampaigns;

    private PagerTabStrip mCampaignPagerTabStrip;
    
    /**
     * 
     * @return
     */
    public static CampaignsFragment getInstance(Bundle bundle) {
        if(sCampaignFragment == null)
            sCampaignFragment = new CampaignsFragment();
        return sCampaignFragment;
    }



    /**
     * Empty constructor
     */
    public CampaignsFragment() {
        super(EnumSet.of(EventType.GET_CAMPAIGN_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH, MyMenuItem.SEARCH_BAR),
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
        //setRetainInstance(true);
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
        
        // Create campaigns
        // Get view pager
        // Create adapter
        // Show
        
        // Get campaign from extras
        String id = "deals-of-the-day";// "http://www.jumia.com.ng/mobapi/v1.0/campaign/get/?campaign_slug=deals-of-the-day";
        
        // Instantiate a ViewPager and a PagerAdapter.
        
        mCampaignPager = (ViewPager) view.findViewById(R.id.campaign_pager);
        mCampaignPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.campaign_pager_tab);
        setTabStripLayoutSpec();
        
        mCampaignPagerAdapter = (CampaignPagerAdapter) mCampaignPager.getAdapter();
        if(mCampaignPagerAdapter == null) {
            mCampaignPagerAdapter = new CampaignPagerAdapter(getChildFragmentManager(), mCampaigns);
            mCampaignPager.setAdapter(mCampaignPagerAdapter);
        }
        mCampaignPager.setOnPageChangeListener(this);
        //mViewPager.setCurrentItem(selectedPage, true);
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
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
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
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class CampaignPagerAdapter extends FragmentPagerAdapter {
        
        private ArrayList<TeaserCampaign> mCampaigns;

        public CampaignPagerAdapter(FragmentManager fm, ArrayList<TeaserCampaign> campaigns) {
            super(fm);
            this.mCampaigns = campaigns;
        }

        @Override
        public Fragment getItem(int position) {
            //return CampaignFragment2.getInstance(this.mCampaigns.get(position));
            return CampaignFragment.getInstance(this.mCampaigns.get(position));
        }

        @Override
        public int getCount() {
            return (mCampaigns != null) ? mCampaigns.size() : 0;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return mCampaigns.get(position).getTargetTitle().toUpperCase();
        }
        
        // FIXME
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            Log.d(TAG, "ON RESTORE STATE");
            
            if(state == null) Log.d(TAG, "STATE IS NULL");
            if(loader == null) Log.d(TAG, "LOADER IS NULL");
            
            if(state != null && loader != null)
                super.restoreState(state, loader);
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        
    }
    
    
    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;
    
    /**
     * Set some layout parameters that aren't possible by xml
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setTabStripLayoutSpec() {
        try {
           
            // Get text
            final TextView currTextView = (TextView) mCampaignPagerTabStrip.getChildAt(TAB_CURR_ID);
            final TextView nextTextView = (TextView) mCampaignPagerTabStrip.getChildAt(TAB_NEXT_ID);
            final TextView prevTextView = (TextView) mCampaignPagerTabStrip.getChildAt(TAB_PREV_ID);
    
            // Set Color
            currTextView.setPadding(0, 0, 0, 1);
    
            // Calculate the measures
            final float density = this.getResources().getDisplayMetrics().density;
            int mIndicatorHeight = (int) (TAB_INDICATOR_HEIGHT * density + 0.5f);
            int mFullUnderlineHeight = (int) (TAB_UNDERLINE_HEIGHT * density + 0.5f);
    
            // Set the indicator height
            Field field;
            field = mCampaignPagerTabStrip.getClass().getDeclaredField("mIndicatorHeight");
            field.setAccessible(true);
            field.set(mCampaignPagerTabStrip, mIndicatorHeight);
            // Set the underline height
            field = mCampaignPagerTabStrip.getClass().getDeclaredField("mFullUnderlineHeight");
            field.setAccessible(true);
            field.set(mCampaignPagerTabStrip, mFullUnderlineHeight);
            // Set the color of indicator
            Paint paint = new Paint();
            paint.setShader(new LinearGradient(0, 0, 0, mIndicatorHeight, getResources().getColor(
                    TAB_STRIP_COLOR), getResources().getColor(
                    TAB_STRIP_COLOR), Shader.TileMode.CLAMP));
            field = mCampaignPagerTabStrip.getClass().getDeclaredField("mTabPaint");
            field.setAccessible(true);
            field.set(mCampaignPagerTabStrip, paint);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }
    
}
