package com.mobile.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobile.view.fragments.BaseFragment;

import java.util.List;

/**
 * A simple implementation of {@link FragmentPagerAdapter}
 */
public class SimplePagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;

    public SimplePagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments == null) {
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if (fragments == null) {
            return 0;
        }
        return fragments.size();
    }
}
