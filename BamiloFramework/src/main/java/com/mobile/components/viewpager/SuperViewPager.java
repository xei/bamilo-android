package com.mobile.components.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Class used to represent a super view pager for all project.
 * @author spereira
 */
public class SuperViewPager extends ViewPager {

    // Flag used to disable the swipe
    private boolean isPagingEnabled = true;

    public SuperViewPager(Context context) {
        super(context);
    }

    public SuperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return isPagingEnabled && super.onTouchEvent(event) ;
    }

    public void disablePaging() {
        this.isPagingEnabled = false;
    }

}
