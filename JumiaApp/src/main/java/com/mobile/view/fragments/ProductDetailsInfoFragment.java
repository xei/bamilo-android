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
import android.view.View.OnClickListener;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Class that show a Product information, as in descriptions and specifications.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsInfoFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductDetailsInfoFragment.class);

    private ViewPager mProductInfoPager;

    private MyOrdersPagerAdapter mProductInfoPagerAdapter;

    private SlidingTabLayout mProductInfoTabStrip;

    public static int mPositionToStart = 0;

    /**
     * Get instance
     *
     * @return
     */
    public static ProductDetailsInfoFragment getInstance(Bundle bundle) {
        ProductDetailsInfoFragment fragment = new ProductDetailsInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsInfoFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout._def_details_info_fragment_main,
                0,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void sendValuesToFragment(Object values) {
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
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");

        // Get view pager
        mProductInfoPager = (ViewPager) view.findViewById(R.id.product_info_pager);
        // Get tab pager
        mProductInfoTabStrip = (SlidingTabLayout) view.findViewById(R.id.product_info_pager_tab);

        int layout = R.layout.tab_simple_item;
//        if(DeviceInfoHelper.isTabletDevice(getBaseActivity().getApplicationContext())){
//            layout = R.layout.tab_simple_half_item;
//        }
        mProductInfoTabStrip.setCustomTabView(layout, R.id.tab);
        // Validate the current view
        mProductInfoPagerAdapter = (MyOrdersPagerAdapter) mProductInfoPager.getAdapter();
        if (mProductInfoPagerAdapter != null && mProductInfoPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mProductInfoPager.setCurrentItem(mPositionToStart, true);
        } else {
            // Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mProductInfoPagerAdapter = new MyOrdersPagerAdapter(getChildFragmentManager());
            mProductInfoPager.setAdapter(mProductInfoPagerAdapter);
            mProductInfoTabStrip.setViewPager(mProductInfoPager);
            // Show the pre selection
            mProductInfoPager.setCurrentItem(mPositionToStart, true);
        }
    }

    public void setPagerPosition(int pos) {
        mProductInfoPager.setCurrentItem(pos, true);

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
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
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
        // mPositionToStart = 0;
    }

    /**
     * Class used as an simple pager adapter that represents each fragment
     * 
     * @author Paulo Carvalho
     */
    private class MyOrdersPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         * 
         * @param fm
         * @author Paulo Carvalho
         */
        public MyOrdersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
            case 0:
                return ProductDetailsSummaryFragment.getInstance(getArguments());
            case 1:
                return ProductDetailsSpecificationsFragment.getInstance(getArguments());
            default:
                return ProductDetailsSummaryFragment.getInstance(getArguments());

            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return getString(R.string.product_desc_summary_title).toUpperCase();
            case 1:
                return getString(R.string.product_specifications).toUpperCase();
            default:
                return getString(R.string.product_desc_summary_title).toUpperCase();
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
        outState.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, mPositionToStart);

    }

}
