package com.mobile.utils.scrolls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

/**
 * This class allows an horizontal scroll for a minimal number of items.
 * 
 * @author sergiopereira
 */
public class HorizontalScrollGroup extends HorizontalScrollView {

    protected static final String TAG = HorizontalScrollGroup.class.getSimpleName();

    private float mDistanceY;

    private float mDistanceX;

    private float mLastX;

    private float mLastY;
    
    private boolean isRTL = false;

    /**
     * Constructor
     * 
     * @param context
     * @author sergiopereira
     */
    public HorizontalScrollGroup(Context context) {
        super(context);
    }

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     * @author sergiopereira
     */
    public HorizontalScrollGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     * @param defStyle
     * @author sergiopereira
     */
    public HorizontalScrollGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.HorizontalScrollView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Valiate view group child
        if (getChildCount() == 0) {
            //Log.w(TAG, "WARNING: IS MISSING A VIEW GROUP CHILD");
            return super.onInterceptTouchEvent(ev);
        }
        // Get view group child
        ViewGroup group = (ViewGroup) this.getChildAt(0);
        // Validate min items to enable scroll
        if (group.getWidth() > this.getWidth()) {
            // For old versions is necessary disable the interception of touch event in the parent.
            validateTouchEvent(ev);
            // Intercept touch event
            return super.onInterceptTouchEvent(ev);
        }
        // No intercept the current touch event
        else return false;
    }

    /**
     * Validate the current touch event to disable/enable other views, like ViewPager and ScrollView. 
     * For old versions is necessary disable the interception of touch event in the parent.
     * @param event
     * @author sergiopereira
     */
    private void validateTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // Disable other horizontal scroll in parent (ViewPager)
            this.getParent().requestDisallowInterceptTouchEvent(true);
            mDistanceX = mDistanceY = 0f;
            mLastX = ev.getX();
            mLastY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            // Disable the current horizontal scroll for scroll vertical (ScrollView)
            final float curX = ev.getX();
            final float curY = ev.getY();
            mDistanceX += Math.abs(curX - mLastX);
            mDistanceY += Math.abs(curY - mLastY);
            mLastX = curX;
            mLastY = curY;
            if (mDistanceY > mDistanceX) this.getParent().requestDisallowInterceptTouchEvent(false);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            // Enable other horizontal scroll in parent (ViewPager)
            this.getParent().requestDisallowInterceptTouchEvent(false);
            break;
        }
    }
    
    /*
     * ########### RTL ###########
     */
    
    /**
     * Flag that represents if layout is in RTL.
     * @return true or false
     * @author sergiopereira
     */
    public boolean isReverseLayout(){
        return isRTL;
    }
    
    /**
     * Set the layout with RTL orientation or not. 
     * @param isRTL
     * @author sergiopereira
     */
    public void setReverseLayout(boolean isRTL) {
        // Save flag
        this.isRTL = isRTL;
        // Reverse layout case is RTL
        if(isRTL) reverseLayout();
    }
    
    /**
     * Simulate the reverse layout moving the focus to right.
     * @author sergiopereira
     */
    private void reverseLayout() {
        // Move focus to right
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

}
