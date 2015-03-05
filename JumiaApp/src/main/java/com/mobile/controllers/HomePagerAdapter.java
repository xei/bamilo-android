package com.mobile.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobile.framework.objects.Homepage;
import com.mobile.view.fragments.HomePageFragment;

import java.util.ArrayList;

/**
 * Class used as an simple pager adapter that represents each campaign fragment
 *
 * @author sergiopereira
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Homepage> mHomePages;

    /**
     * Constructor
     *
     * @param fm
     * @param collection
     * @author sergiopereira
     */
    public HomePagerAdapter(FragmentManager fm, ArrayList<Homepage> collection) {
        super(fm);
        this.mHomePages = collection;
    }

    /**
     * @param collection
     */
    public void updateCollection(ArrayList<Homepage> collection) {
        this.mHomePages = collection;
        this.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(HomePageFragment.HOME_PAGE_KEY, this.mHomePages.get(position));
        return HomePageFragment.getInstance(bundle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return (mHomePages != null) ? mHomePages.size() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mHomePages.get(position).getTitle().toUpperCase();
    }

}