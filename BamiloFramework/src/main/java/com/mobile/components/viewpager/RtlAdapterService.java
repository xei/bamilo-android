package com.mobile.components.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobile.service.utils.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * <br><br>FragmentAdapter with RTL support. This adapter only supports static fragments
 * (the fragments must be initialized at creation of this adapter).
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/06/18
 */
public abstract class RtlAdapterService extends FragmentPagerAdapter{
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
        if(!CollectionUtils.isEmpty(titleList)){
            return titleList.get(position);
        } else{
            return super.getPageTitle(position);
        }
    }

    @Override
    public int getCount() {
        return (fragmentList != null) ? fragmentList.size() : 0;
    }

    public void enableRtl(boolean rtl) {
        if((rtl && !this.isRtl) || (!rtl && this.isRtl)){
            if(fragmentList != null){
                Collections.reverse(fragmentList);
            }
            if(!CollectionUtils.isEmpty(titleList)){
                Collections.reverse(titleList);
            }
        }

        isRtl = rtl;
    }

//    @Override
//    public Fragment getItem(int position) {
//        return fragmentList != null ? fragmentList.get(position) : null;
//    }
}
