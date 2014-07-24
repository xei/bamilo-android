/**
 * 
 */
package pt.rocket.controllers;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;
import pt.rocket.view.fragments.CatalogPageFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author nunocastro
 *
 */
public class CatalogPagerAdapter extends FragmentPagerAdapter {

    private Bundle fragmentParameters;
    private int parentId;
    private ArrayList<String> mSortOptions;
    private FragmentManager fragmentManager;
    
    /**
     * @param fm
     */
    public CatalogPagerAdapter(FragmentManager fm, int viewPagerId, ArrayList<String> sortOptions, Bundle parameters) {
        super(fm);
        this.fragmentManager = fm;
        this.parentId = viewPagerId;
        this.fragmentParameters = parameters;
        this.mSortOptions = sortOptions;
        Log.d("CatalogPagerAdaper", " ---> ViewPager ID ->" + viewPagerId);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        Log.d("CatalogPagerAdaper", " ---> GET ITEM #" + position);
        
        Bundle params = (Bundle) fragmentParameters.clone();
        params.putInt(CatalogPageFragment.PARAM_PAGE_INDEX, position);
        
        return CatalogPageFragment.newInstance(params);
    }

    /* (non-Javadoc)
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

    public void invalidateCatalogPages() {
        CatalogPageFragment pageFragment;
        for (int index = 0; index < mSortOptions.size(); index++) {
            pageFragment = (CatalogPageFragment)fragmentManager.findFragmentByTag(getFragmentTag(index));
            if (null != pageFragment) {
                pageFragment.invalidateData(fragmentParameters, true);
            }
        }
    }
    
    public int getCatalogPageTotalItems(int position) {
        int totalItems = 0;
        CatalogPageFragment pageFragment = (CatalogPageFragment)fragmentManager.findFragmentByTag(getFragmentTag(position));
        if (null != pageFragment) {
            totalItems = pageFragment.getTotalProducts();
        }
        
        return totalItems; 
    }
    
    private String getFragmentTag(int index) {
        return "android:switcher:" + parentId + ":" + index;
    }    
}
