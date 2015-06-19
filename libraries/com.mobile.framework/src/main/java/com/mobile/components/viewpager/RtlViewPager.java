package com.mobile.components.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by rsoares on 6/19/15.
 */
public class RtlViewPager extends ViewPager {

    private boolean isRtlEnabled;

    public interface RtlService{
        public void invertItems();
    }

    public RtlViewPager(Context context) {
        super(context);
    }

    public RtlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void enableRtl(){
        if(getAdapter() instanceof  RtlService){
            ((RtlService) getAdapter()).invertItems();
            isRtlEnabled = true;
        }
    }

    public void disableRtl(){
        if(getAdapter() instanceof  RtlService){
            ((RtlService) getAdapter()).invertItems();
            isRtlEnabled = false;
        }
    }

}
