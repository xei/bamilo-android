package com.bamilo.android.framework.components.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bamilo.android.framework.components.infiniteviewpager.InfiniteViewPager;
import com.bamilo.android.framework.service.utils.output.Print;

public class JumiaViewPagerWithZoom extends InfiniteViewPager {
    private boolean isPagingEnabled = true;
    private String TAG = JumiaViewPagerWithZoom.class.getSimpleName();

    public JumiaViewPagerWithZoom(Context context) {
        super(context);
    }

    public JumiaViewPagerWithZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isPagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                Print.e(TAG, "catched IllegalArgumentException JumiaViewPagerWithZoom onTouchEvent");
            }
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            // https://github.com/chrisbanes/PhotoView/issues/31#issuecomment-19803926
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException e) {
                Print.e(TAG, "catched IllegalArgumentException JumiaViewPagerWithZoom onInterceptTouchEvent");
            }
        }

        return false;
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

}
