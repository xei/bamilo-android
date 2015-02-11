/**
 * 
 */
package com.mobile.utils;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.mobile.view.R;

/**
 * @author Andre Lopes
 * 
 */
public class TipsOnPageChangeListener implements OnPageChangeListener {
    private View mView;
    private int[] mTipsPages;

    public TipsOnPageChangeListener(View view, int[] tips_pages) {
        mView = view;
        mTipsPages = tips_pages;
    }

    @Override
    public void onPageSelected(int position) {
        // set all items icons to bullit
        for (int i = 1; i <= mTipsPages.length; i++) {
            View indicator = mView.findViewWithTag("indicator" + i);
            if (indicator != null && indicator instanceof ImageView) {
                ((ImageView) indicator).setImageResource(R.drawable.bullit);
            }
        }
        // set current item icon to bullit_showing
        View currentIndicator = mView.findViewWithTag("indicator" + (position + 1));
        if (currentIndicator != null && currentIndicator instanceof ImageView) {
            ((ImageView) currentIndicator).setImageResource(R.drawable.bullit_showing);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

}
