package com.mobile.view.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.adapters.SimplePagerAdapter;
import com.mobile.service.pojo.IntConstants;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FrontPageFragment extends BaseFragment{

    private static float TAB_LAYOUT_HEIGHT_DP = 38, TAB_TEXT_SIZE_SP = 24;

    public FrontPageFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET),
                NavigationAction.HOME,
                R.layout.fragment_front_page,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager pager = (ViewPager) view.findViewById(R.id.vpFrontPage);

        List<BaseFragment> fragments = new ArrayList<>();
        final BaseFragment fragment1, fragment2;
        fragment1 = new MyBamiloFragment();
        fragment2 = new HomeFragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);

        getBaseActivity().setUpExtraTabLayout(pager);
        final TabLayout tabLayout = getBaseActivity().getExtraTabLayout();
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.orange_lighter));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabLayout.getLayoutParams();
        params.height =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TAB_LAYOUT_HEIGHT_DP, getResources().getDisplayMetrics());
        tabLayout.setLayoutParams(params);
        tabLayout.getTabAt(0).setCustomView(R.layout.front_page_tab_my_bamilo);
        tabLayout.getTabAt(1).setCustomView(R.layout.front_page_tab_home);
        pager.setCurrentItem(1);
    }
}
