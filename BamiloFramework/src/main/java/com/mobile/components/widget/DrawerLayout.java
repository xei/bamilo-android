package com.mobile.components.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Component used to fix the crash between DrawerLayout and PhotoView.
 * 
 * @author sergiopereira
 * 
 */
public class DrawerLayout extends android.support.v4.widget.DrawerLayout {

    public DrawerLayout(Context context) {
        this(context, null);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.widget.DrawerLayout#onInterceptTouchEvent(android.
     * view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // https://github.com/chrisbanes/PhotoView/issues/72
        try {
            return super.onInterceptTouchEvent(event);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

}
