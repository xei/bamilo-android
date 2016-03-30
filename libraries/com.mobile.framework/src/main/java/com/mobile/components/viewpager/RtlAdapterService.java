package com.mobile.components.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobile.newFramework.utils.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * <p/>
 * <br><br>FragmentAdapter with RTL support. This adapter only supports static fragments
 * (the fragments must be initialized at creation of this adapter).
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/06/18
 */
public abstract class RtlAdapterService extends FragmentPagerAdapter {
    protected List<Fragment> fragmentList;
    protected List<String> titleList;
    protected boolean isRtl;

    public RtlAdapterService(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragmentList = fragments;
        this.titleList = titles;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (CollectionUtils.isNotEmpty(titleList)) {
            return titleList.get(position);
        } else {
            return super.getPageTitle(position);
        }
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(fragmentList);
    }

    public void enableRtl(boolean rtl) {
        if ((rtl && !this.isRtl) || (!rtl && this.isRtl)) {
            // Reverse titles
            if (CollectionUtils.isNotEmpty(titleList)) {
                Collections.reverse(titleList);
            }
            // Reverse fragments
            if (CollectionUtils.isNotEmpty(fragmentList)) {
                Collections.reverse(fragmentList);
            }
        }
        // Update current state
        isRtl = rtl;
    }

}
