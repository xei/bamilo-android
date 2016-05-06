package com.mobile.view.fragments.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.mobile.components.viewpager.SuperViewPager;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
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

    public static final int CONDITIONS = -1;
    public static final int REASON = 0;
    public static final int METHOD = 1;
    public static final int REFUND = 2;
    public static final int FINISH = 3;
    private int mSavedPosition;

    @IntDef({CONDITIONS, REASON, METHOD, REFUND, FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReturnStepType {}

    private SuperViewPager mPager;
    private Bundle mSubmittedData;

    /**
     * Empty constructor
     */
    public OrderReturnStepsMain() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout._def_order_return_step_main,
                R.string.my_orders_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        super.onCreateInstanceState(bundle);
        mSubmittedData = bundle.getBundle(ConstantsIntentExtra.ARG_1);
        mSavedPosition = bundle.getInt(ConstantsIntentExtra.ARG_2);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Validate received items

        // Set tab
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.order_return_main_tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable._gen_selector_tab_step_1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable._gen_selector_tab_step_2));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable._gen_selector_tab_step_3));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable._gen_selector_tab_step_4));

        nextStep(REASON);

//        // Get pager
//        mPager = (SuperViewPager) view.findViewById(R.id.order_return_main_pager);
//        mPager.disablePaging();
//        mPager.addOnPageChangeListener(new TabLayoutPageChangeListener(tabLayout));
//
//        // Now we'll add a tab selected listener to set ViewPager's current item
//        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));

//        // Validate the current view
//        OrderReturnStepsAdapter adapter = (OrderReturnStepsAdapter) mPager.getAdapter();
//        if(adapter != null && adapter.getCount() > 0) {
//            mPager.setCurrentItem(mSavedPosition, true);
//        } else {
//            mPager.setAdapter(new OrderReturnStepsAdapter(getChildFragmentManager()));
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save data
        outState.putBundle(ConstantsIntentExtra.ARG_1, mSubmittedData);
        // Save page
        if (mPager != null) {
            outState.putInt(ConstantsIntentExtra.ARG_2, mPager.getCurrentItem());
        }
    }

    public ArrayList<OrderTrackerItem> getOrderItems() {
        return (ArrayList<OrderTrackerItem>) this.mArgArray;
    }

    public String getOrderNumber() {
        return this.mArgId;
    }

//    public void nextStep(int nextStepId) {
//        mPager.setCurrentItem(nextStepId, true);
//    }

    public void nextStep(@ReturnStepType int nextStepId) {
        FragmentController.addChildFragment(this, R.id.order_return_main_container, getItem(nextStepId), String.valueOf(nextStepId));
    }

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
     * ##### STEP VALUES #####
     */

    /**
     * Validate submitted values
     */
    public boolean hasSubmittedValuesToFinish() {
        return mSubmittedData != null &&
                mSubmittedData.containsKey(String.valueOf(REASON)) &&
                mSubmittedData.containsKey(String.valueOf(METHOD)) &&
                mSubmittedData.containsKey(String.valueOf(REFUND));
    }

    /**
     * Save submitted values
     */
    public synchronized void saveSubmittedValuesFromStep(@ReturnStepType int mStep, @NonNull ContentValues values) {
        if (mSubmittedData == null) {
            mSubmittedData = new Bundle();
        }
        mSubmittedData.putParcelable(String.valueOf(mStep), values);
    }

    /**
     * Get submitted values
     */
    public ContentValues getSubmittedValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.putAll(getSubmittedValuesForStep(REASON));
        contentValues.putAll(getSubmittedValuesForStep(METHOD));
        contentValues.putAll(getSubmittedValuesForStep(REFUND));

        return contentValues;
    }

    /**
     * Get submitted values for step
     */
    public ContentValues getSubmittedValuesForStep(@ReturnStepType int mStep) {
        return mSubmittedData != null ? (ContentValues) mSubmittedData.getParcelable(String.valueOf(mStep)) : null;
    }

    /*
     * ##### BACK #####
     */
//    @Override
//    public boolean allowBackPressed() {
//        int step = mPager.getCurrentItem();
//        if (step > REASON) {
//            nextStep(--step);
//            return true;
//        } else {
//            return super.allowBackPressed();
//        }
//    }

    @Override
    public boolean allowBackPressed() {
        FragmentManager m = getChildFragmentManager();
        int size = m.getFragments().size();
        if (size > REASON) {
            getChildFragmentManager().popBackStackImmediate();
            return true;
        } else {
            return super.allowBackPressed();
        }
    }


    /*
     * ##### LISTENERS #####
     */

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


    /**
     * Class used as an simple pager adapter that represents each fragment
     * @author sergiopereira
     */
    private class OrderReturnStepsAdapter extends FragmentStatePagerAdapter {

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

    }

}
