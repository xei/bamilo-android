package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

import java.util.List;

/**
 * A simple implementation of {@link FragmentPagerAdapter}
 */
public class SimplePagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;
    private List<String> fragmentTitles;

    public SimplePagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public SimplePagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> fragmentTitles) {
        super(fm);
        this.fragments = fragments;
        this.fragmentTitles = fragmentTitles;
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

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragmentTitles != null && fragmentTitles.size() > position) {
            return fragmentTitles.get(position);
        }
        return super.getPageTitle(position);
    }

    public List<String> getFragmentTitles() {
        return fragmentTitles;
    }

    public void setFragmentTitles(List<String> fragmentTitles) {
        this.fragmentTitles = fragmentTitles;
    }
}
