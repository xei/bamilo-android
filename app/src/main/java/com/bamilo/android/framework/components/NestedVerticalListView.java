package com.bamilo.android.framework.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ListView;

/**	
 * 	ListView for nested environments (ScrollView)
 *
 * @version 1.1
 * @modified Ricardo Soares
 */
public class NestedVerticalListView extends ListView {
    private float xDistance, yDistance, lastX, lastY;

    public NestedVerticalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(new OnScrollListener() {

            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                ViewParent viewParent = getParent();
                if(viewParent != null && scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Validates if reached down or top of list view
                    if (verifyReachedTop(firstVisibleItem,visibleItemCount,totalItemCount) ||
                            verifyReachedBottom(firstVisibleItem,visibleItemCount,totalItemCount)) {
                        viewParent.requestDisallowInterceptTouchEvent(false);
                    } else {
                        viewParent.requestDisallowInterceptTouchEvent(true);
                    }
                }
            }
        });
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
            // Validates if list view is all way down and user is trying to scroll down even more
            if(verifyReachedTop(getFirstVisiblePosition(), getCount() - getFirstVisiblePosition(), getCount()) &&
                    ev.getAction() == MotionEvent.ACTION_MOVE &&
                    verifyIsTouchScrollingDown(ev)){

                getParent().requestDisallowInterceptTouchEvent(false);

            } else
            // Validates if list view is all way up and user is trying to scroll up even more
            if(verifyReachedBottom(getFirstVisiblePosition(), getCount() - getFirstVisiblePosition(), getCount()) &&
                    ev.getAction() == MotionEvent.ACTION_MOVE &&
                    verifyIsTouchScrollingUp(ev)){

                getParent().requestDisallowInterceptTouchEvent(false);

            } else {
                validateTouchEvent(ev);
            }

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

    protected boolean verifyReachedBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        return totalItemCount == firstVisibleItem + visibleItemCount &&
                getChildAt(getChildCount()-1).getBottom() == getHeight();
    }

    protected boolean verifyReachedTop(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        return firstVisibleItem == 0 && getChildAt(0).getTop() >= 0;
    }

    protected boolean verifyIsTouchScrollingUp(MotionEvent ev){
        final float curY = ev.getY();
        return curY < lastY;
    }

    protected boolean verifyIsTouchScrollingDown(MotionEvent ev){
        final float curY = ev.getY();
        return curY > lastY;
    }
}