package pt.rocket.components;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    
    /**
     * Constructor
     * @param context
     */
    public HorizontalListView(Context context) {
        super(context);
        init(context);
    }
    
    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    /**
     * Constructor
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    /**
     * 
     * @param context
     */
    private void init(Context context) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.HorizontalScrollView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
    
    /**
     * Set the selected item.<br>
     * The adpter must implement the interface {@link OnViewHolderSelected}. 
     * @param position
     * @author sergiopereira
     * @throws RuntimeException
     */
    public void setSelecetedItem(int position) {
        Adapter<?> adapter = getAdapter();
        if(adapter instanceof OnViewHolderSelected) {
            ((OnViewHolderSelected) adapter).setSelectedPosition(position);
            scrollToPosition(position);
        } else {
            throw new RuntimeException("The adpter must implement the interface 'OnViewHolderSelected' to set a selected position!");
        }
    }

    /**
     * Set the on item selected listener.<br>
     * The adpter must implement the interface {@link OnViewHolderSelected}.
     * @param listener
     */
    public void setOnItemSelectedListener(OnViewSelectedListener listener) {
        Adapter<?> adapter = getAdapter();
        if(adapter instanceof OnViewHolderSelected) {
            ((OnViewHolderSelected) adapter).setOnViewHolderSelected(listener);
        } else {
            throw new RuntimeException("The adpter must implement the interface OnViewHolderSelected to the on selected item listener!");
        }
    }
    
    /**
     * Interface for Adapter
     * @author sergiopereira
     *
     */
    public static interface OnViewHolderSelected {
        /**
         * Set the selected position.<br> 
         * @param position
         * @author sergiopereira
         */
        public void setSelectedPosition(int position);
        /**
         * Set the listener for on item selected.<br>
         * @param listener
         * @author sergiopereira
         */
        public void setOnViewHolderSelected(OnViewSelectedListener listener);
    }
    
    /**
     * Interface for Fragment
     * @author sergiopereira
     *
     */
    public static interface OnViewSelectedListener {
        /**
         * Receives the selected view and the respective position
         * @param view
         * @param position
         */
        public void onViewSelected(View view, int position, String string);
    }
    
}
