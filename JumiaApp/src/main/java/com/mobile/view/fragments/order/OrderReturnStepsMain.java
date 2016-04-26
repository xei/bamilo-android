package com.mobile.view.fragments.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.mobile.components.viewpager.SuperViewPager;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.BaseFragmentAutoState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Fragment used to show the online returns reason.
 * @author spereira
 */
public class OrderReturnStepsMain extends BaseFragmentAutoState {

    private SuperViewPager mPager;

    public static final int CONDITIONS = -1;
    public static final int REASON = 0;
    public static final int METHOD = 1;
    public static final int REFUND = 2;
    public static final int FINISH = 3;
    @IntDef({CONDITIONS, REASON, METHOD, REFUND, FINISH,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReturnStepType {}

    /**
     * Empty constructor
     */
    public OrderReturnStepsMain() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout._def_order_return_step_main,
                R.string.my_orders_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * ##### LIFECYCLE #####
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate received items

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.order_return_main_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("1"));
        tabLayout.addTab(tabLayout.newTab().setText("2"));
        tabLayout.addTab(tabLayout.newTab().setText("3"));
        tabLayout.addTab(tabLayout.newTab().setText("4"));
        // Get pager
        mPager = (SuperViewPager) view.findViewById(R.id.order_return_main_pager);
        mPager.disablePaging();
        mPager.addOnPageChangeListener(new TabLayoutPageChangeListener(tabLayout));
        // Validate the current view
        OrderReturnStepsAdapter adapter = (OrderReturnStepsAdapter) mPager.getAdapter();
        if(adapter != null && adapter.getCount() > 0) {
            // Show the pre selection
            mPager.setCurrentItem(0, true);
        } else {
            //Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            adapter = new OrderReturnStepsAdapter(getChildFragmentManager());
            mPager.setAdapter(adapter);
            // Show the pre selection
            mPager.setCurrentItem(0, true);
        }
    }

    private class TabLayoutPageChangeListener extends TabLayout.TabLayoutOnPageChangeListener {

        public TabLayoutPageChangeListener(TabLayout tabLayout) {
            super(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            //if (position < mPager.getCurrentItem()) {
            //    super.onPageSelected(position);
            //}
        }
    }

    public ArrayList<OrderTrackerItem> getOrderItems() {
        return (ArrayList<OrderTrackerItem>) this.mArgArray;
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    protected void onClickRetryButton(View view) {
        // TODO
    }

    public void nextStep(int nextStepId) {
        mPager.setCurrentItem(nextStepId, true);
    }

    @Override
    public boolean allowBackPressed() {
        int step = mPager.getCurrentItem();
        if (step > 0) {
            nextStep(--step);
            return true;
        } else {
            return super.allowBackPressed();
        }
    }

    /**
     * Class used as an simple pager adapter that represents each fragment
     * @author sergiopereira
     */
    private class OrderReturnStepsAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         */
        public OrderReturnStepsAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @SuppressLint("SwitchIntDef")
        @Override
        public Fragment getItem(@ReturnStepType int position) {
            Class<? extends BaseFragment> fClass;
            switch (position) {
                case REASON:
                    fClass = OrderReturnStep1Reason.class;
                    break;
                case METHOD:
                    fClass = OrderReturnStep2Method.class;
                    break;
                case REFUND:
                    fClass = OrderReturnStep3Refund.class;
                    break;
                case FINISH:
                default:
                    fClass = OrderReturnStep4Finish.class;
                    break;
            }
            return newInstance(getContext(), fClass, null);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return IntConstants.TAB_MAX_STEPS;
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return ""; //mCampaigns.get(position).getTitle().toUpperCase();
        }

    }

}
