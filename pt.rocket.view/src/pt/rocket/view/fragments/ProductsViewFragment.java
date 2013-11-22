package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows all products for a respective category.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by sergiopereira, 19/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author sergiopereira
 * @modified Manuel Silva
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */
public class ProductsViewFragment extends BaseFragment {

    private final static String TAG = LogTagHelper.create(ProductsViewFragment.class);

    private PagerTabStrip pagerTabStrip;

    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;

    private static ProductsViewFragment mProductsViewFragment;

    public ProductsViewFragment() {
        // super(NavigationAction.Products,
        // EnumSet.of(MyMenuItem.SEARCH),
        // EnumSet.noneOf(EventType.class),
        // EnumSet.noneOf(EventType.class),
        // 0, R.layout.products_frame);
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class), EnumSet
                .of(MyMenuItem.SEARCH), NavigationAction.Products, 0);
    }

    public static ProductsViewFragment getInstance() {
//        if (mProductsViewFragment == null) {
            mProductsViewFragment = new ProductsViewFragment();
//        }
        return mProductsViewFragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.products_frame, null, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        AnalyticsGoogle.get().trackPage(R.string.gproductlist);
        ProductsListPagerAdapter mProductsListPagerAdapter = new ProductsListPagerAdapter(getChildFragmentManager());

        ViewPager mViewPager = (ViewPager) getView().findViewById(R.id.viewpager_products_list);
        pagerTabStrip = (PagerTabStrip) getView().findViewById(R.id.products_list_titles);
        mViewPager.setAdapter(mProductsListPagerAdapter);
        mViewPager.setCurrentItem(0, true);
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean all event listeners
     */
    @Override
    public void onPause() {
        super.onPause();

    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class ProductsListPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> mSortOptions = new ArrayList<String>(Arrays.asList(getResources()
                .getStringArray(R.array.products_picker)));
        public static final String ARG_OBJECT = "object";

        public ProductsListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ProductsFragment();
            Bundle args = getArguments();
            args.putInt(ProductsFragment.INTENT_POSITION_EXTRA, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return ProductSort.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSortOptions.get(position).toUpperCase();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    /**
     * Set some layout parameters that aren't possible by xml
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setLayoutSpec() throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        // Get text
        final TextView currTextView = (TextView) pagerTabStrip.getChildAt(TAB_CURR_ID);
        final TextView nextTextView = (TextView) pagerTabStrip.getChildAt(TAB_NEXT_ID);
        final TextView prevTextView = (TextView) pagerTabStrip.getChildAt(TAB_PREV_ID);

        // Set Color
        currTextView.setPadding(0, 0, 0, 1);

        // Calculate the measures
        final float density = this.getResources().getDisplayMetrics().density;
        int mIndicatorHeight = (int) (TAB_INDICATOR_HEIGHT * density + 0.5f);
        int mFullUnderlineHeight = (int) (TAB_UNDERLINE_HEIGHT * density + 0.5f);

        // Set the indicator height
        Field field;
        field = pagerTabStrip.getClass().getDeclaredField("mIndicatorHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mIndicatorHeight);
        // Set the underline height
        field = pagerTabStrip.getClass().getDeclaredField("mFullUnderlineHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mFullUnderlineHeight);
        // Set the color of indicator
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 0, mIndicatorHeight, getResources().getColor(
                TAB_STRIP_COLOR), getResources().getColor(
                TAB_STRIP_COLOR), Shader.TileMode.CLAMP));
        field = pagerTabStrip.getClass().getDeclaredField("mTabPaint");
        field.setAccessible(true);
        field.set(pagerTabStrip, paint);

    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }
}
