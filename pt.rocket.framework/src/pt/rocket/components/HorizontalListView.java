package pt.rocket.components;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Class
 * TODO: <br>
 * - Create method for RTL from attrs<br>
 * - Create method to enable or disable touch interception.
 * @author sergiopereira
 *
 */
public class HorizontalListView extends RecyclerView {
    
    protected static final String TAG = HorizontalListView.class.getSimpleName();
    
    private float mDistanceY;
    
    private float mDistanceX;
    
    private float mLastX;
    
    private float mLastY;

//    private float mDistanceY;
//
//    private float mDistanceX;
//
//    private float mLastX;
//
//    private float mLastY;
    
    /**
     * Constructor
     * @param context
     */
    public HorizontalListView(Context context) {
        super(context);
    }
    
    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * Constructor
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.HorizontalScrollView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //boolean bool = ((LinearLayoutManager) getLayoutManager()).canScrollHorizontally();
        
        // Get layout manager
        int firstC = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        int lastC = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
        //int firstV = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        //int lastV = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int count = ((LinearLayoutManager) getLayoutManager()).getItemCount();
        //Log.i(TAG, "onInterceptTouchEvent: Complete: " + firstC + "-" + lastC + " Visible:" + firstV + "-" + lastV + " Count:" + count + " " + bool);

        // Case empty or not fill
        if(count == 0 || lastC - firstC == count - 1) {
            //Log.i(TAG, "Disallow");
            this.getParent().requestDisallowInterceptTouchEvent(false);
            //setHorizontalScrollBarEnabled(false);
            //setHorizontalFadingEdgeEnabled(false);
            return false;
        } else {
            //Log.i(TAG, "Allow");
            // For old versions is necessary disable the intercept of touch event in the parent.
            validateTouchEvent(ev);
            // Intercept touch event
            return super.onInterceptTouchEvent(ev);
        }
            
        
        
        // Valiate view group child
//        if (getChildCount() == 0) {
//            //Log.w(TAG, "WARNING: IS MISSING A VIEW GROUP CHILD");
            
//        }
//        // Get view group child
//        ViewGroup group = (ViewGroup) this.getChildAt(0);
//        // Validate min items to enable scroll
//        if (group.getWidth() > this.getWidth()) {
//            // For old versions is necessary disable the intercept of touch event in the parent.
//            validateTouchEvent(ev);
//            // Intercept touch event
//            return super.onInterceptTouchEvent(ev);
//        }
//        // No intercept the current touch event
//        else return false;
    }

    /**
     * Validate the current touch event to disable/enable other views, like ViewPager and ScrollView. 
     * For old versions is necessary disable the intercept of touch event in the parent.
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
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView#stopScroll()
     */
    @Override
    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException exception) {
            /**
             * The mLayout has been disposed of before the RecyclerView and this
             * stops the application from crashing.
             */
        }
    }

}
