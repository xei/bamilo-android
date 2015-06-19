package com.mobile.newFramework.utils.shop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by rsoares on 6/19/15.
 */
public abstract class RtlDynamicFragmentAdapter extends RtlAdapterService {

    protected List<String> fragments;

    public RtlDynamicFragmentAdapter(FragmentManager fm, List<String> fragments, List<String> titles) {
        super(fm, null, titles);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return createNewFragment(position);
    }

    protected abstract Fragment createNewFragment(int position);

    @Override
    public void enableRtl(boolean rtl) {
        if((rtl && !this.isRtl) || (!rtl && this.isRtl)){
            if(!CollectionUtils.isEmpty(fragments)){
                Collections.reverse(fragments);
            }
        }

        super.enableRtl(rtl);
    }
}
