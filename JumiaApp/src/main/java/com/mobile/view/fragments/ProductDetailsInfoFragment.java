package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.components.viewpager.RtlDynamicFragmentAdapter;
import com.mobile.components.viewpager.RtlViewPager;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Class that show a Product information, as in descriptions and specifications.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsInfoFragment extends BaseFragment {

    private static final String TAG = ProductDetailsInfoFragment.class.getSimpleName();

    private int mPositionToStart = IntConstants.DEFAULT_POSITION;

    private String mTitle;

    private boolean mHasSpecs = true;

    private boolean mHasDesc = true;

    /**
     * Get instance
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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.PRODUCT,
                R.layout.details_info_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);
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
        Bundle bundle = getArguments();
        if(bundle != null) {
            mPositionToStart = bundle.getInt(ConstantsIntentExtra.PRODUCT_INFO_POS);
            mTitle = bundle.getString(ConstantsIntentExtra.FLAG_1);
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
        RtlViewPager mProductInfoPager = (RtlViewPager) view.findViewById(R.id.product_info_pager);
        // Get tab pager
        SlidingTabLayout mProductInfoTabStrip = (SlidingTabLayout) view.findViewById(R.id.product_info_pager_tab);
        mProductInfoTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);
        // Validate the current view
        validateVisibleTabs();
        // Get titles
        List<String> titles = getFragmentTitleValues();
        // Validate adapter
        ProductInfoPagerAdapter mProductInfoPagerAdapter = (ProductInfoPagerAdapter) mProductInfoPager.getAdapter();
        if (mProductInfoPagerAdapter != null && mProductInfoPagerAdapter.getCount() > 0) {
            mProductInfoPager.setCurrentItem(mPositionToStart, true);
        } else {
            mProductInfoPagerAdapter = new ProductInfoPagerAdapter(getChildFragmentManager(), titles);
            mProductInfoPager.enableRtlSupport(ShopSelector.isRtl());
            mProductInfoPager.setAdapter(mProductInfoPagerAdapter);
            mProductInfoPager.setCurrentItem(mPositionToStart, true);
            mProductInfoTabStrip.setViewPager(mProductInfoPager);
        }
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
        // Set action title
        if (TextUtils.isNotEmpty(mTitle)) {
            getBaseActivity().setActionBarTitle(mTitle);
        }
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
    private void validateVisibleTabs() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if (parcelableProduct instanceof ProductComplete) {
                ProductComplete completeProduct = (ProductComplete) parcelableProduct;
                if (CollectionUtils.isEmpty(completeProduct.getProductSpecifications())) {
                    mHasSpecs = false;
                }
                if (TextUtils.isEmpty(completeProduct.getDescription()) && TextUtils.isEmpty(completeProduct.getShortDescription())) {
                    mHasDesc = false;
                }
            }
        }
    }


    /**
     * Class used as an simple pager adapter that represents each fragment
     *
     * @author Paulo Carvalho
     */
    private class ProductInfoPagerAdapter extends RtlDynamicFragmentAdapter {

        /**
         * Constructor
         *
         * @author Paulo Carvalho
         */
        public ProductInfoPagerAdapter(FragmentManager fm, List<String> titles) {
            super(fm, titles);
        }

        @Override
        protected Fragment createNewFragment(int position) {
//            if (TextUtils.equals(titleList.get(position), getString(R.string.description)) && mHasDesc) {
//                return ProductDetailsSummaryFragment.getInstance(getArguments());
//            } else if (TextUtils.equals(titleList.get(position), getString(R.string.product_specifications)) && mHasSpecs) {
//                return ProductDetailsSpecificationsFragment.getInstance(getArguments());
//            } else {
//                return ReviewsFragment.getInstance(getArguments()); //added: go to ratings page
//            }

            if (TextUtils.equals(titleList.get(position), "DESC") && mHasDesc) {
                return ProductDetailsSummaryFragment.getInstance(getArguments());
            } else if (TextUtils.equals(titleList.get(position), "SPEC") && mHasSpecs) {
                return ProductDetailsSpecificationsFragment.getInstance(getArguments());
            } else {
                return ReviewsFragment.getInstance(getArguments()); //added: go to ratings page
            }
        }

    }

    private List<String> getFragmentTitleValues() {
        ArrayList<String> titles = new ArrayList<>();
        if (mHasDesc) {
            titles.add("DESC");//getString(R.string.description));
        }
        if (mHasSpecs) {
            titles.add("SPEC");//getString(R.string.product_specifications));
        }
        titles.add("RAT");//getString(R.string.rat_rev));
        return titles;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, mPositionToStart);

    }

}
