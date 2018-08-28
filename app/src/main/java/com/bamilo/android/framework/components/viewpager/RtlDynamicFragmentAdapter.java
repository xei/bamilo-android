package com.bamilo.android.framework.components.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.bamilo.android.framework.service.utils.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
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

    protected List<Integer> titlesPageInt;

    protected final Fragment parent;

    public RtlDynamicFragmentAdapter(FragmentManager fm, Fragment parent, List<Integer> titlesPageInt) {
        super(fm, null, new LinkedList<String>());
        this.titlesPageInt = titlesPageInt;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return titlesPageInt.size();
    }

    @Override
    public Fragment getItem(int position) {
        return createNewFragment(position);
    }

    protected abstract Fragment createNewFragment(int position);

    @Override
    public CharSequence getPageTitle(int position) {
        if(titleList.size() <= position){
            titleList.add(parent.getString(titlesPageInt.get(position)));
        }
        return titleList.get(position);
    }

    @Override
    public void enableRtl(boolean rtl) {
        if((rtl && !this.isRtl) || (!rtl && this.isRtl)){
            if(!CollectionUtils.isEmpty(titlesPageInt)){
                Collections.reverse(titlesPageInt);
            }
        }

        super.enableRtl(rtl);
    }
}
