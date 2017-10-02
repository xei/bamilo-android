package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.service.pojo.IntConstants;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

public class HomeFragment extends BaseFragment {
    public HomeFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET),
                NavigationAction.HOME,
                R.layout.home_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
