package com.mobile.newFramework.utils.shop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by rsoares on 6/19/15.
 */
public class RtlAdapterService extends FragmentPagerAdapter{
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

    @Override
    public Fragment getItem(int position) {
        return fragmentList != null ? fragmentList.get(position) : null;
    }
}
