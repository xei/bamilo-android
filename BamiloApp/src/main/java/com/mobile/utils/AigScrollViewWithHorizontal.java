package com.mobile.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class AigScrollViewWithHorizontal extends ScrollView {
	private float xDistance, yDistance, lastX, lastY;

	private Handler mHandler;
	private View view;
	
	public AigScrollViewWithHorizontal(Context context) {
		super(context);
	}

	public AigScrollViewWithHorizontal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AigScrollViewWithHorizontal(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean mScrollable = true;

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }
	
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }
    
    
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    if (!mScrollable) return false;
	    
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - lastX);
			yDistance += Math.abs(curY - lastY);
			lastX = curX;
			lastY = curY;
			if (xDistance > yDistance)
				return false;
		}

		return super.onInterceptTouchEvent(ev);
	}
	
    @Override 
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(this.view != null && this.view.isShown()){
            this.mHandler.sendEmptyMessage(0);
        }
        
    }
    
    public void sendListenerAndView(Handler handler, View v){
        this.view = v;
        this.mHandler = handler;
    }
	
}
