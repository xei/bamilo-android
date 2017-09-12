package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mobile.components.androidslidingtabstrip.SlidingTabLayout;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.viewpager.RtlDynamicFragmentAdapter;
import com.mobile.components.viewpager.RtlViewPager;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.service.objects.product.pojo.ProductComplete;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
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

    private ViewPager mProductInfoPager;

    private int mPositionToStart = IntConstants.DEFAULT_POSITION;

    private int mTabsCount = 3;

    private String mTitle;

    private boolean mHasSpecs = true;

    private boolean mHasDesc = true;

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
        mProductInfoPager = (ViewPager) view.findViewById(R.id.product_info_pager);
        // Validate the current view
        validateVisibleTabs();
        ProductInfoPagerAdapter mProductInfoPagerAdapter = (ProductInfoPagerAdapter) mProductInfoPager.getAdapter();
        if (mProductInfoPagerAdapter != null && mProductInfoPagerAdapter.getCount() > 0) {
            // Show the pre selection
            mProductInfoPager.setCurrentItem(getFragmentPosition(mPositionToStart), true);
        } else {
            // Log.d(TAG, "CAMPAIGNS ADAPTER IS NULL");
            mProductInfoPagerAdapter = new ProductInfoPagerAdapter(getChildFragmentManager());
            mProductInfoPager.setAdapter(mProductInfoPagerAdapter);
            mProductInfoPager.setOffscreenPageLimit(mProductInfoPagerAdapter.getCount());
            //#RTL
            if(ShopSelector.isRtl()){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mProductInfoPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
            }
            setPagerPosition(getFragmentPosition(mPositionToStart));
            setupViewPagerTabs(mProductInfoPager);
            // Show the pre selection
            mProductInfoPager.setCurrentItem(getFragmentPosition(mPositionToStart), true);
        }
    }

    private void setupViewPagerTabs(ViewPager viewPager) {
        getBaseActivity().setUpExtraTabLayout(viewPager);
        TabLayout tabLayout = getBaseActivity().getExtraTabLayout();
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.orange_1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        HoloFontLoader.applyDefaultFont(tabLayout);
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
    private void validateVisibleTabs(){
        Bundle arguments = getArguments();
        mTabsCount = 3;
        if(arguments != null){
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof ProductComplete){
                ProductComplete completeProduct = (ProductComplete) parcelableProduct;

                if(CollectionUtils.isEmpty(completeProduct.getProductSpecifications())){
                    mHasSpecs = false;
                    mTabsCount--;
                }
                if(TextUtils.isEmpty(completeProduct.getDescription()) && TextUtils.isEmpty(completeProduct.getShortDescription())){
                    mHasDesc = false;
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
    private class ProductInfoPagerAdapter extends RtlDynamicFragmentAdapter implements RtlViewPager.RtlService{

        /**
         * Constructor
         * @author Paulo Carvalho
         */
        public ProductInfoPagerAdapter(FragmentManager fm) {
            super(fm, ProductDetailsInfoFragment.this, getFragmentTitleValues());
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

        @Override
        protected Fragment createNewFragment(int position) {
            if(titlesPageInt.get(position).equals(R.string.description) && mHasDesc) {
                return ProductDetailsSummaryFragment.getInstance(getArguments());
            }
            else if (titlesPageInt.get(position).equals(R.string.product_specifications) && mHasSpecs) {
                return ProductDetailsSpecificationsFragment.getInstance(getArguments());
            }
            else {
                return BaseFragment.newInstance(getBaseActivity(), ReviewsFragment.class, getArguments()); //added: go to ratings page
            }
        }

        @Override
        public void invertItems() {
            enableRtl(!isRtl);
        }
    }

    /**
     * gets the fragment position based on the id of the title
     */
    public int getFragmentPosition(int fragmentTitle){
        List<Integer> titles = getFragmentTitleValues();
        /*if(ShopSelector.isRtl()){
            Collections.reverse(titles);
        }*/
        if(titles.contains(fragmentTitle)){
            return titles.indexOf(fragmentTitle);
        } else {
            return 0;
        }
    }

    private List<Integer> getFragmentTitleValues(){

        ArrayList<Integer> titles = new ArrayList<>();
        if(mHasDesc){
            titles.add(R.string.rat_rev);
        }
        if(mHasSpecs){
            titles.add(R.string.product_specifications);
        }
        titles.add(R.string.description);
        return titles;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, mPositionToStart);

    }

}
