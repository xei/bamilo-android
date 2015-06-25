/**
 *
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.components.viewpager.RtlViewPager;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.components.viewpager.RtlDynamicFragmentAdapter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Paulo Carvalho
 *
 */
public class MyOrdersFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(MyOrdersFragment.class);

    private RtlViewPager mMyOrdersPager;

    private MyOrdersPagerAdapter mMyOrdersPagerAdapter;

    private SlidingTabLayout mMyOrdersPagerTabStrip;

    private int mPositionToStart = 0;

    /**
     * Get instance
     *
     * @return
     */
    public static MyOrdersFragment getInstance(Bundle bundle) {
        MyOrdersFragment fragment = new MyOrdersFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        // Get arguments
        Bundle arguments = getArguments();

        if (arguments != null) {
            if(arguments.containsKey(TrackerDelegator.LOGIN_KEY)){
                mPositionToStart = ShopSelector.isRtl() ? 0 : 1;
            }
        } else {
            mPositionToStart = ShopSelector.isRtl() ? 1: 0;
        }
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
        Print.i(TAG, "ON VIEW CREATED");

        // Get view pager
        mMyOrdersPager = (RtlViewPager) view.findViewById(R.id.my_orders_pager);
        // Get tab pager
        mMyOrdersPagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.my_orders_pager_tab);

        int layout = R.layout.tab_simple_half_item;
        if(DeviceInfoHelper.isTabletDevice(getBaseActivity().getApplicationContext())){
            layout = R.layout.tab_simple_item;
        }

        mMyOrdersPagerTabStrip.setCustomTabView(layout, R.id.tab);
        // Validate the current view
        mMyOrdersPagerAdapter = (MyOrdersPagerAdapter) mMyOrdersPager.getAdapter();
        if (mMyOrdersPagerAdapter != null && mMyOrdersPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mMyOrdersPager.setCurrentItem(mPositionToStart, true);
        } else {
            // Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mMyOrdersPagerAdapter = new MyOrdersPagerAdapter(getChildFragmentManager());
            mMyOrdersPager.setAdapter(mMyOrdersPagerAdapter);
            if(ShopSelector.isRtl()){
                mMyOrdersPager.enableRtl();
            }
            mMyOrdersPagerTabStrip.setViewPager(mMyOrdersPager);
            // Show the pre selection
            mMyOrdersPager.setCurrentItem(mPositionToStart, true);
        }
    }

    public void setPagerPosition(int pos) {
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
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
        // mPositionToStart = 0;
    }

    /**
     * Class used as an simple pager adapter that represents each fragment
     *
     * @author Paulo Carvalho
     */
    private class MyOrdersPagerAdapter extends RtlDynamicFragmentAdapter implements RtlViewPager.RtlService{

        /**
         * Constructor
         *
         * @param fm
         * @author Paulo Carvalho
         */
        public MyOrdersPagerAdapter(FragmentManager fm) {
            super(fm, MyOrdersFragment.this, getFragmentTitleValues());
        }

        @Override
        protected Fragment createNewFragment(int position) {
            return (titlesPageInt.get(position) == R.string.my_order_history_label) ?
                    OrderHistoryFragment.getInstance() :
                    TrackOrderFragment.getInstance(getArguments());
        }

        @Override
        public void invertItems() {
            enableRtl(!isRtl);
        }
    }

    private List<Integer> getFragmentTitleValues(){
        Integer[] titles = {R.string.my_order_tracking_label, R.string.my_order_history_label};
        return Arrays.asList(titles);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsIntentExtra.MY_ORDER_POS, mPositionToStart);

    }

}
