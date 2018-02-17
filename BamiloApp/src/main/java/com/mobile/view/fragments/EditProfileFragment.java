package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created by mohsen on 2/17/18.
 */

public class EditProfileFragment extends BaseFragment {

    public EditProfileFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_USER_DATA,
                R.layout.fragment_edit_profile,
                R.string.myaccount_userdata,
                ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HoloFontLoader.applyDefaultFont(view);
    }
}
