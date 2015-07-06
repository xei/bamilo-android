/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class that show a Product information, as in descriptions and specifications.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsInfoFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductDetailsInfoFragment.class);

    private ViewPager mProductInfoPager;

    private ProductInfoPagerAdapter mProductInfoPagerAdapter;

    private SlidingTabLayout mProductInfoTabStrip;

    public static int mPositionToStart = 0;

    private int mTabsCount = 2;

    private boolean mHasSummary = true;

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
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        // Get view pager
        mProductInfoPager = (ViewPager) view.findViewById(R.id.product_info_pager);
        // Get tab pager
        mProductInfoTabStrip = (SlidingTabLayout) view.findViewById(R.id.product_info_pager_tab);

        int layout = R.layout.tab_simple_half_item;
        if(DeviceInfoHelper.isTabletDevice(getBaseActivity().getApplicationContext())){
            layout = R.layout.tab_simple_item;
        }
        mProductInfoTabStrip.setCustomTabView(layout, R.id.tab);
        // Validate the current view
        validateVisibleTabs();
        mProductInfoPagerAdapter = (ProductInfoPagerAdapter) mProductInfoPager.getAdapter();
        if (mProductInfoPagerAdapter != null && mProductInfoPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mProductInfoPager.setCurrentItem(mPositionToStart, true);
        } else {
            // Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mProductInfoPagerAdapter = new ProductInfoPagerAdapter(getChildFragmentManager());
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
     * method that validates and controls which tab will be shown to the user
     */
    private void validateVisibleTabs(){
        Bundle arguments = getArguments();

        if(arguments != null){
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof CompleteProduct){
                CompleteProduct completeProduct = (CompleteProduct) parcelableProduct;

                if(CollectionUtils.isEmpty(completeProduct.getProductSpecifications())){
//                    hasSpecification = false;
                    mTabsCount--;
                }
                if(TextUtils.isEmpty(completeProduct.getDescription()) && TextUtils.isEmpty(completeProduct.getShortDescription())){
                    mHasSummary = false;
                    mTabsCount--;
                }
            }
        }
    }


    /**
     * Class used as an simple pager adapter that represents each fragment
     * 
     * @author Paulo Carvalho
     */
    private class ProductInfoPagerAdapter extends FragmentPagerAdapter {

        /**
         * Constructor
         * 
         * @param fm
         * @author Paulo Carvalho
         */
        public ProductInfoPagerAdapter(FragmentManager fm) {
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
                return chooseFragment(position);
            case 1:
                return chooseFragment(position);
            default:
                return ProductDetailsSummaryFragment.getInstance(getArguments());

            }

        }

        /**
         * control which fragment to inflate acordingly to which information is available
         * @param position
         * @return
         */
        private Fragment chooseFragment(int position){
            Fragment fragment = ProductDetailsSummaryFragment.getInstance(getArguments());

            if(position == 0){
                if(!mHasSummary){
                    fragment = ProductDetailsSpecificationsFragment.getInstance(getArguments());
                }
            } else {
                fragment = ProductDetailsSpecificationsFragment.getInstance(getArguments());
            }

            return fragment;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return mTabsCount;
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
                if(mHasSummary){
                    return getString(R.string.product_desc_summary_title).toUpperCase();
                } else {
                    return getString(R.string.product_specifications).toUpperCase();
                }
            case 1:
                return getString(R.string.product_specifications).toUpperCase();
            default:
                return getString(R.string.product_desc_summary_title).toUpperCase();
            }
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, mPositionToStart);

    }

}
