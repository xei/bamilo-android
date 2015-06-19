package com.mobile.components.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * <br><br>FragmentAdapter with RTL support. <br>This adapter supports dynamic fragments
 * (Fragments being initialized at #getItem). This adapter is abstract and must be implemented
 * in order to dynamic support get applied.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/06/18
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
