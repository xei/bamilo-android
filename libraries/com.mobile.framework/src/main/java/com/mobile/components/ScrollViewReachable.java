package com.mobile.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author nunocastro
 * @modified sergiopereira
 */
public class ScrollViewReachable extends ScrollView {

    public interface OnScrollBottomReachedListener {
        void onScrollBottomReached();
    }

    private OnScrollBottomReachedListener onScrollBottomReached = null;

    /**
     * Constructor
     */
    public ScrollViewReachable(Context context) {
        super(context);
    }

    /**
     * Constructor
     */
    public ScrollViewReachable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     */
    public ScrollViewReachable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* (non-Javadoc)
     * @see android.view.View#onScrollChanged(int, int, int, int)
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));// Calculate the scrolldiff
        if (diff == 0 && null != onScrollBottomReached) {  // if diff is zero, then the bottom has been reached
            onScrollBottomReached.onScrollBottomReached();
        }
    }

    /**
     * @param onScrollBottomReached the onScrollBottomReached to set
     */
    public void setOnScrollBottomReached(OnScrollBottomReachedListener onScrollBottomReached) {
        this.onScrollBottomReached = onScrollBottomReached;
    }

}
