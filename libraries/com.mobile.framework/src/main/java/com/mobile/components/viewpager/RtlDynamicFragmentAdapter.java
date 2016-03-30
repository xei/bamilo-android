package com.mobile.components.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mobile.newFramework.utils.CollectionUtils;

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
 * @version 1.1
 * @date 2015/06/18
 */
public abstract class RtlDynamicFragmentAdapter extends RtlAdapterService {

    public RtlDynamicFragmentAdapter(FragmentManager fm, List<String> titles) {
        super(fm, null, titles);
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(titleList);
    }

    @Override
    public Fragment getItem(int position) {
        return createNewFragment(position);
    }

    protected abstract Fragment createNewFragment(int position);

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
