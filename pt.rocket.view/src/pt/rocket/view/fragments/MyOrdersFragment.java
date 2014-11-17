/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.components.androidslidingtabstrip.SlidingTabLayout;
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
import android.view.View;
import android.view.View.OnClickListener;
import de.akquinet.android.androlog.Log;

/**
 * @author Paulo Carvalho
 * 
 */
public class MyOrdersFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(MyOrdersFragment.class);

    private static MyOrdersFragment mMyOrdersFragment;

    private ViewPager mMyOrdersPager;

    private MyOrdersPagerAdapter mMyOrdersPagerAdapter;

    private SlidingTabLayout mMyOrdersPagerTabStrip;
    
    public static int mPositionToStart = 0;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyOrdersFragment getInstance(Bundle bundle) {
        if(bundle != null && bundle.containsKey(ConstantsIntentExtra.MY_ORDER_POS)){
            mPositionToStart = bundle.getInt(ConstantsIntentExtra.MY_ORDER_POS);
        }
        mMyOrdersFragment = new MyOrdersFragment();
        return mMyOrdersFragment;
    }

    /**
     * Empty constructor
     */
    public MyOrdersFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout.myorders_fragment_main,
                R.string.my_orders_label,
                KeyboardState.ADJUST_CONTENT);
        // R.string.nav_track_order
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
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
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Get view pager 
        mMyOrdersPager = (ViewPager) view.findViewById(R.id.my_orders_pager);
        // Get tab pager
        mMyOrdersPagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.my_orders_pager_tab);
        mMyOrdersPagerTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);
        // Validate the current view
        mMyOrdersPagerAdapter = (MyOrdersPagerAdapter) mMyOrdersPager.getAdapter();
        if(mMyOrdersPagerAdapter != null && mMyOrdersPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mMyOrdersPager.setCurrentItem(mPositionToStart, true);
        } else {
            //Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mMyOrdersPagerAdapter = new MyOrdersPagerAdapter(getChildFragmentManager());
            mMyOrdersPager.setAdapter(mMyOrdersPagerAdapter);
            mMyOrdersPagerTabStrip.setViewPager(mMyOrdersPager);
            // Show the pre selection
            mMyOrdersPager.setCurrentItem(mPositionToStart, true);
        }
    }

    public void setPagerPosition(int pos){
        mMyOrdersPager.setCurrentItem(pos, true);
        
    }
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
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
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
        mPositionToStart = 0;
    }
 
    
    /**
     * Class used as an simple pager adapter that represents each fragment
     * @author Paulo Carvalho
     */
    private class MyOrdersPagerAdapter extends FragmentPagerAdapter {
        
        
        /**
         * Constructor
         * @param fm
         * @author Paulo Carvalho
         */
        public MyOrdersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
            case 0:
                return TrackOrderFragment.getInstance(getArguments());
            case 1:
                return OrderHistoryFragment.getInstance();
            default:
                return TrackOrderFragment.getInstance(getArguments());
                
            }
            

        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return 2;
        }
        
        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return getString(R.string.my_order_tracking_label).toUpperCase();
            case 1:
                return getString(R.string.my_order_history_label).toUpperCase();
            default:
                return getString(R.string.my_orders_label).toUpperCase();
            }
        }
        
    }
    


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsIntentExtra.MY_ORDER_POS, mPositionToStart);

    }
    
}