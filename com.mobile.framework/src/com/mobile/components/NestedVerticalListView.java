package com.mobile.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**	
 * 	ListView for nested environments (ScrollView)
 *
 */
public class NestedVerticalListView extends ListView {
    private float xDistance, yDistance, lastX, lastY;

    public NestedVerticalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        
        int count = (getAdapter()!= null)? getAdapter().getCount() : 0;

        // Case empty or not fill
        if(count == 0) {
            //Log.i(TAG, "Disallow");
            this.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        } else {
            //Log.i(TAG, "Allow");
            // For old versions is necessary disable the intercept of touch event in the parent.
            validateTouchEvent(ev);
            // Intercept touch event
            return super.onInterceptTouchEvent(ev);
        }
    }
    
    private void validateTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // Disable other horizontal scroll in parent (ViewPager)
            this.getParent().requestDisallowInterceptTouchEvent(true);
            xDistance = yDistance = 0f;
            lastX = ev.getX();
            lastY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            // Disable the current horizontal scroll for scroll vertical (ScrollView)
            final float curX = ev.getX();
            final float curY = ev.getY();
            xDistance += Math.abs(curX - lastX);
            yDistance += Math.abs(curY - lastY);
            lastX = curX;
            lastY = curY;
            if (xDistance > yDistance) this.getParent().requestDisallowInterceptTouchEvent(false);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            // Enable other horizontal scroll in parent (ViewPager)
            this.getParent().requestDisallowInterceptTouchEvent(false);
            break;
        }
    }
       
}