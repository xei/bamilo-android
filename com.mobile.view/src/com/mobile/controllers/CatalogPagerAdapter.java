/**
 * 
 */
package com.mobile.controllers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobile.view.fragments.CatalogPageFragment;

/**
 * @author nunocastro
 * 
 */
public class CatalogPagerAdapter extends FragmentPagerAdapter {

    private Bundle fragmentParameters;
    private int parentId;
    private boolean isLandscape = false;
    private ArrayList<String> mSortOptions;
    private FragmentManager fragmentManager;

    /**
     * @param fm
     */
    public CatalogPagerAdapter(FragmentManager fm, int viewPagerId, ArrayList<String> sortOptions, Bundle parameters, boolean landscapeMode) {
        super(fm);
        this.fragmentManager = fm;
        this.parentId = viewPagerId;
        this.fragmentParameters = parameters;
        this.mSortOptions = sortOptions;
        this.isLandscape = landscapeMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        Bundle params = (Bundle) fragmentParameters.clone();
        params.putInt(CatalogPageFragment.PARAM_PAGE_INDEX, position);
        params.putBoolean(CatalogPageFragment.PARAM_IS_LANDSCAPE, isLandscape);
        return CatalogPageFragment.newInstance(params);
    }

    
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mSortOptions.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSortOptions.get(position).toUpperCase();
    }

    public void updateParametersBundle(Bundle params) {
        fragmentParameters = params;
    }

    public void setLandscapeMode(boolean isLandscape) {
        this.isLandscape = isLandscape;
    }

//    public void invalidateCatalogPages() {
//        CatalogPageFragment pageFragment;
//        Bundle params = (Bundle) fragmentParameters.clone();
//        params.putBoolean(CatalogPageFragment.PARAM_IS_LANDSCAPE, isLandscape);
//
//        for (int index = 0; index < mSortOptions.size(); index++) {
//            pageFragment = (CatalogPageFragment) fragmentManager.findFragmentByTag(getFragmentTag(index));
//            if (null != pageFragment) {
//                pageFragment.invalidateData(params, true);
//            }
//        }
//    }  
    
    public void resetClearProduct() {
        CatalogPageFragment pageFragment;
        Bundle params = (Bundle) fragmentParameters.clone();
        params.putBoolean(CatalogPageFragment.PARAM_IS_LANDSCAPE, isLandscape);

        for (int index = 0; index < mSortOptions.size(); index++) {
            pageFragment = (CatalogPageFragment) fragmentManager.findFragmentByTag(getFragmentTag(index));
            if (null != pageFragment) {
                pageFragment.setProductClear(true);
            }
        }
    }  
    
    public void restoreFilters(ContentValues filters) {
        CatalogPageFragment pageFragment;
        for (int index = 0; index < mSortOptions.size(); index++) {
            pageFragment = (CatalogPageFragment) fragmentManager.findFragmentByTag(getFragmentTag(index));
            if (null != pageFragment) {
                pageFragment.restoreFilters(filters);
            }
        }
    }

    public int getCatalogPageTotalItems(int position) {
        int totalItems = 0;
        CatalogPageFragment pageFragment = (CatalogPageFragment) fragmentManager.findFragmentByTag(getFragmentTag(position));
        if (null != pageFragment) {
            totalItems = pageFragment.getTotalProducts();
        }

        return totalItems;
    }

    private String getFragmentTag(int index) {
        return "android:switcher:" + parentId + ":" + index;
    }
}
