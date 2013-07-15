package pt.rocket.controllers;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePageViewPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> genericFragments;
    
    public HomePageViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.genericFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return this.genericFragments.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.genericFragments.size();
    }

}
