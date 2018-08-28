package com.bamilo.android.framework.components.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by spereira on 4/27/15.
 * @author spereira
 */
public class FadePageTransformer implements ViewPager.PageTransformer {

    public static final String TAG = FadePageTransformer.class.getSimpleName();

    private static final float MIN_ALPHA = 0.4f;

    private static final float MAX_ALPHA = 1.0f;

    private static final float LEFT_POINT = -1.0f;

    private static final float RIGHT_POINT = 1.0f;

    private static final float CENTER_POINT = 0.0f;

    private final ViewPager mViewPager;

    /**
     * Constructor
     * @param pager The associated view pager
     */
    public FadePageTransformer(ViewPager pager) {
        mViewPager = pager;
    }

    /**
     * Method used to transform a view pager
     * @param page The page
     * @param position The view position based in the scroll
     */
    @Override
    public void transformPage(View page, float position) {
        // Calculate the page position (The received position has the padding left value)
        final float transformPos = (float) (page.getLeft() - mViewPager.getScrollX() - mViewPager.getPaddingLeft()) / getClientWidth();
        // Case out of range
        if (transformPos <= LEFT_POINT || transformPos >= RIGHT_POINT) {
            page.setAlpha(MIN_ALPHA);
        }
        // Case center
        else if (transformPos == CENTER_POINT) {
            page.setAlpha(MAX_ALPHA);
        }
        // Case position is between -1.0f & 0.0f OR 0.0f & 1.0f
        else {
            float alpha = MAX_ALPHA - Math.abs(transformPos);
            page.setAlpha(alpha < MIN_ALPHA ? MIN_ALPHA : alpha);
        }

    }

    /**
     * The method used in the class {#ViewPager}
     * @return int
     */
    private int getClientWidth() {
        return mViewPager.getMeasuredWidth() - mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
    }

}
