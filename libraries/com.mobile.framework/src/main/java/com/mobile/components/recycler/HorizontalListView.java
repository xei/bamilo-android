package com.mobile.components.recycler;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class
 * - Create method for RTL from attrs<br>
 * - Create method to enable or disable touch interception.
 *
 * @author sergiopereira
 */
public class HorizontalListView extends RecyclerView {

    protected static final String TAG = HorizontalListView.class.getSimpleName();

    private float mDistanceY;

    private float mDistanceX;

    private float mLastX;

    private float mLastY;

    private boolean interceptTouchEvent;

    /**
     * Constructor
     */
    public HorizontalListView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor
     */
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Constructor
     */
    public HorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Initialize the view with horizontal linear layout manager.
     */
    private void init(Context context) {
        // Set manager
        HorizontalLinearLayout mLayoutManager = new HorizontalLinearLayout(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);
        // Disable nested scrolling behaviour
        this.setNestedScrollingEnabled(false);
    }

    /**
     * Enable the touch interception and disallow the parent.<br>
     * Used inside a view with horizontal scroll.
     */
    @SuppressWarnings("unused")
    public void enableTouchInterception() {
        interceptTouchEvent = true;
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see android.widget.HorizontalScrollView#onInterceptTouchEvent(android.view.MotionEvent)
//     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if(!interceptTouchEvent) return super.onInterceptTouchEvent(ev);
//        // Get layout manager
//        int firstC = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//        int lastC = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
//        int count = getLayoutManager().getItemCount();
//        // Case empty or not fill
//        if(count == 0 || lastC - firstC == count - 1) {
//            this.getParent().requestDisallowInterceptTouchEvent(false);
//            return false;
//        } else {
//            // For old versions is necessary disable the intercept of touch event in the parent.
//            validateTouchEvent(ev);
//            // Intercept touch event
//            return super.onInterceptTouchEvent(ev);
//        }
//    }

    /**
     * Validate the current touch event to disable/enable other views, like ViewPager and ScrollView.
     * For old versions is necessary disable the intercept of touch event in the parent.
     *
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
                if (mDistanceY > mDistanceX)
                    this.getParent().requestDisallowInterceptTouchEvent(false);
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
     * The adapter must implement the interface {@link OnViewHolderSelected}.
     *
     * @param position The selected position
     * @throws RuntimeException
     * @author sergiopereira
     */
    public void setSelectedItem(int position) {
        Adapter<?> adapter = getAdapter();
        if (adapter instanceof OnViewHolderSelected) {
            ((OnViewHolderSelected) adapter).setSelectedPosition(position);
            scrollToPosition(position);
        } else {
            throw new RuntimeException("The adapter must implement the interface 'OnViewHolderSelected' to set a selected position!");
        }
    }

    /**
     * Set the on item selected listener.<br>
     * The adapter must implement the interface {@link OnViewHolderSelected}.
     *
     * @param listener The selected listener
     */
    public void setOnItemSelectedListener(OnViewSelectedListener listener) {
        Adapter<?> adapter = getAdapter();
        if (adapter instanceof OnViewHolderSelected) {
            ((OnViewHolderSelected) adapter).setOnViewHolderSelected(listener);
        } else {
            throw new RuntimeException("The adpter must implement the interface OnViewHolderSelected to the on selected item listener!");
        }
    }

    /**
     * Method used to enable the reverse layout to support RTL direction.<br>
     *
     * @param rtl The RTL flag
     */
    public void enableRtlSupport(boolean rtl) {
        // Case RTL
        if (rtl) {
            // Case API < 17: set reverse layout as true
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((LinearLayoutManager) getLayoutManager()).setReverseLayout(true);
            }
            // Case API >= 17: use the native support
            else {
                setLayoutDirection(LAYOUT_DIRECTION_LOCALE);
            }
        }
    }

    /**
     * Interface for Adapter
     *
     * @author sergiopereira
     */
    public interface OnViewHolderSelected {
        /**
         * Set the selected position.<br>
         *
         * @author sergiopereira
         */
        void setSelectedPosition(int position);

        /**
         * Set the listener for on item selected.<br>
         *
         * @author sergiopereira
         */
        void setOnViewHolderSelected(OnViewSelectedListener listener);
    }

    /**
     * Interface for Fragment
     *
     * @author sergiopereira
     */
    public interface OnViewSelectedListener {
        /**
         * Receives the selected view and the respective position
         */
        void onViewSelected(View view, int position, String string);
    }

    /**
     * Horizontal Manager
     */
    public class HorizontalLinearLayout extends LinearLayoutManager {

        public HorizontalLinearLayout(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }

        @Override
        public boolean canScrollHorizontally() {
            return true;
        }
    }

}
