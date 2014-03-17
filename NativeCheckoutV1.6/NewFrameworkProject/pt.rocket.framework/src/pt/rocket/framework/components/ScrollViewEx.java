/**
 * 
 */
package pt.rocket.framework.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author nunocastro
 *
 */
public class ScrollViewEx extends ScrollView {

    public interface OnScrollBottomReachedListener {
        void OnScrollBottomReached();
    }
    
    private OnScrollBottomReachedListener onScrollBottomReached = null;
    
    /**
     * @param context
     */
    public ScrollViewEx(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    public ScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ScrollViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see android.view.View#onScrollChanged(int, int, int, int)
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));// Calculate the scrolldiff
        if( diff == 0 && null != onScrollBottomReached ){  // if diff is zero, then the bottom has been reached
            onScrollBottomReached.OnScrollBottomReached();
        }
    }    
    
    /**
     * @param onScrollBottomReached the onScrollBottomReached to set
     */
    public void setOnScrollBottomReached(OnScrollBottomReachedListener onScrollBottomReached) {
        this.onScrollBottomReached = onScrollBottomReached;
    }    
    
}
