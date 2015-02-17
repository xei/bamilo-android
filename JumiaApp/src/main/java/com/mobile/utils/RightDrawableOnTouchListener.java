package com.mobile.utils;


import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.mobile.components.customfontviews.TextView;

public abstract class RightDrawableOnTouchListener implements OnTouchListener {
    Drawable drawable;
    private int fuzz = 10;

    /**
     * @param keyword
     */
    public RightDrawableOnTouchListener(TextView view) {
        super();
        final Drawable[] drawables = view.getCompoundDrawables();
        if (drawables != null && drawables.length == 4)
            this.drawable = drawables[2];
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final Rect bounds = drawable.getBounds();
            if (x >= (v.getRight() - bounds.width() - fuzz) && x <= (v.getRight() - v.getPaddingRight() + fuzz)
                    && y >= (v.getPaddingTop() - fuzz) && y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {
                drawable.setState( new int[]{ + android.R.attr.state_pressed });
            	return onDrawableTouch(event);
            }
            
        } else if ( event.getAction() == MotionEvent.ACTION_UP  && drawable != null) {
            drawable.setState( new int[]{ - android.R.attr.state_pressed });

        }
        return false;
    }

    public abstract boolean onDrawableTouch(final MotionEvent event);

}