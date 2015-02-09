//package com.mobile.utils;
//
///*
// * HorizontalListView.java v1.5
// *
// *
// * The MIT License
// * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// *
// */
//
//import java.util.LinkedList;
//import java.util.Queue;
//
//import com.mobile.framework.utils.LogTagHelper;
//import com.mobile.view.R;
//import android.content.Context;
//import android.database.DataSetObserver;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.GestureDetector.OnGestureListener;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListAdapter;
//import android.widget.Scroller;
//import de.akquinet.android.androlog.Log;
//
//public class HorizontalListView extends AdapterView<ListAdapter> {
//
//    private String TAG = LogTagHelper.create(HorizontalListView.class);
//    public final static int MOVE_TO_NO = 0;
//    public final static int MOVE_TO_DIRECTLY = 1;
//    public final static int MOVE_TO_SCROLLED = 2;
//    private static final int NO_SCROLL_FOR_ITEM = -1;
//
//    public boolean mAlwaysOverrideTouch = true;
//    protected ListAdapter mAdapter;
//    private int mLeftViewIndex = -1;
//    private int mRightViewIndex = 0;
//    protected int mCurrentX;
//    protected int mNextX;
//    private int mMaxX = Integer.MAX_VALUE;
//    private int mDisplayOffset = 0;
//    protected Scroller mScroller;
//    private GestureDetector mGesture;
//    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
//    private OnItemSelectedListener mOnItemSelected;
//    private OnItemClickListener mOnItemClicked;
//    private OnItemLongClickListener mOnItemLongClicked;
//    private boolean mDataChanged = false;
//
//    private int mChildWidth;
//    private int mChildHeight;
//
//    private int mSelected;
//    private int mOldSelected;
//
//    //private boolean mLayoutBlocksChanges;
//    
//    private int mContentWidth;
//    private Runnable mRequestLayoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            requestLayout();
//        }
//    };
//
//    public HorizontalListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView();
//    }
//
//    private synchronized void initView() {
//        mLeftViewIndex = -1;
//        mRightViewIndex = 0;
//        mDisplayOffset = 0;
//        mCurrentX = 0;
//        mNextX = 0;
//        mMaxX = Integer.MAX_VALUE;
//        mScroller = new Scroller(getContext());
//        mGesture = new GestureDetector(getContext(), mOnGesture);
//        mChildWidth = -1;
//        mChildHeight = -1;
//        mContentWidth = -1;
//        mSelected = -1;
//        mOldSelected = -1;
//    }
//
//    @Override
//    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
//        mOnItemSelected = listener;
//    }
//
//    @Override
//    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
//        mOnItemClicked = listener;
//    }
//
//    @Override
//    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
//        mOnItemLongClicked = listener;
//    }
//
//    private DataSetObserver mDataObserver = new DataSetObserver() {
//
//        @Override
//        public void onChanged() {
//            synchronized (HorizontalListView.this) {
//                mDataChanged = true;
//            }
//            clearSelection();
//            invalidate();
//            requestLayout();
//        }
//
//        @Override
//        public void onInvalidated() {
//            reset();
//            invalidate();
//            requestLayout();
//        }
//
//    };
//
//    @Override
//    public ListAdapter getAdapter() {
//        return mAdapter;
//    }
//
//    @Override
//    public View getSelectedView() {
//        return null;
//    }
//
//    @Override
//    public void setAdapter(ListAdapter adapter) {
//        if (mAdapter != null) {
//            mAdapter.unregisterDataSetObserver(mDataObserver);
//        }
//        mAdapter = adapter;
//        if(mAdapter != null)
//            mAdapter.registerDataSetObserver(mDataObserver);
//        reset();
//    }
//
//    private synchronized void reset() {
//        initView();
//        try {
//            removeAllViewsInLayout();
//        } catch (IllegalArgumentException e) {
//           e.printStackTrace();
//        }
//        
//        requestLayout();
//    }
//
//    private void clearSelection() {
//        mSelected = -1;
//        mOldSelected = -1;
//    }
//
//    @Override
//    public void setSelection(int position) {
//
//    }
//
//    private void addAndMeasureChild(final View child, int viewPos) {
//        LayoutParams params = child.getLayoutParams();
//        if (params == null) {
//            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        }
//
//        addViewInLayout(child, viewPos, params, true);
//        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
//                      MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthSize;
//        int heightSize;
//
//        int preferredHeight = 0;
//
//        if (mChildWidth != -1 && mChildHeight != -1) {
//            preferredHeight = mChildHeight;
//            mContentWidth = mChildWidth * mAdapter.getCount();
//        } else if (mAdapter != null && mAdapter.getCount() > 0) {
//            View view = mAdapter.getView(0, null, this);
//            measureChild(view, widthMeasureSpec, heightMeasureSpec);
//
//            mChildHeight = getChildHeight(view);
//            mChildWidth = getChildWidth(view);
//
//            preferredHeight = mChildHeight;
//            mContentWidth = mChildWidth * mAdapter.getCount();
//        }
//
//        preferredHeight = Math.max(preferredHeight, getSuggestedMinimumHeight());
//        int preferredWidth = Math.max(mContentWidth, getSuggestedMinimumWidth());
//        // Log.d( TAG, "onMeasure preferredWidth = " + preferredWidth );
//
//        heightSize = resolveSize(preferredHeight, heightMeasureSpec);
//        widthSize = resolveSize(preferredWidth, widthMeasureSpec);
//        // heightSize = resolveSizeAndState(preferredHeight, heightMeasureSpec, 0);
//        // widthSize = resolveSizeAndState(preferredWidth, widthMeasureSpec, 0);
//        setMeasuredDimension(widthSize, heightSize);
//    }
//
//    protected int getChildHeight(View child) {
//        return child.getMeasuredHeight();
//    }
//
//    protected int getChildWidth(View child) {
//        return child.getMeasuredWidth();
//    }
//
//    @Override
//    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        // Log.d( TAG, "onLayout" );
//        
//        //mLayoutBlocksChanges = true;
//        
//        if (mAdapter == null) {
//            return;
//        }
//
//        if (mDataChanged) {
//            int savedSelected = mSelected;
//            int oldCurrentX = mCurrentX;
//            initView();
//            try {
//                removeAllViewsInLayout();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//            mSelected = savedSelected;
//            mNextX = oldCurrentX;
//            mDataChanged = false;
//        }
//
//        if (mScroller.computeScrollOffset()) {
//            int scrollx = mScroller.getCurrX();
//            mNextX = scrollx;
//        }
//
//        if (mNextX <= 0) {
//            mNextX = 0;
//            mScroller.forceFinished(true);
//        }
//        if (mNextX >= mMaxX) {
//            mNextX = mMaxX;
//            mScroller.forceFinished(true);
//        }
//
//        int dx = mCurrentX - mNextX;
//
//        removeNonVisibleItems(dx);
//        fillList(dx);
//        positionItems(dx);
//        updateSelected();
//
//        mCurrentX = mNextX;
//
//        if (!mScroller.isFinished()) {
//            post(mRequestLayoutRunnable);
//        }
//
//        //mLayoutBlocksChanges = false;
//    }
//
//    private void fillList(final int dx) {
//        int edge = 0;
//        View child = getChildAt(getChildCount() - 1);
//        if (child != null) {
//            edge = child.getRight();
//        }
//        fillListRight(edge, dx);
//
//        edge = 0;
//        child = getChildAt(0);
//        if (child != null) {
//            edge = child.getLeft();
//        }
//        fillListLeft(edge, dx);
//
//    }
//
//    private void fillListRight(int rightEdge, final int dx) {
//
//        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
//
//            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
//            child.setTag(R.id.item_id, mAdapter.getItemId(mRightViewIndex));
//            if (child.isSelected()) {
//                Log.d(TAG, "fillListRight child from adapter is already selected - resetting");
//                child.setSelected(false);
//            }
//            addAndMeasureChild(child, -1);
//            rightEdge += child.getMeasuredWidth();
//
//            if (mRightViewIndex == mAdapter.getCount() - 1) {
//                mMaxX = mCurrentX + rightEdge - getWidth();
//            }
//
//            if (mMaxX < 0) {
//                mMaxX = 0;
//            }
//            mRightViewIndex++;
//        }
//
//    }
//
//    private void fillListLeft(int leftEdge, final int dx) {
//        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
//            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
//            child.setTag(R.id.item_id, mAdapter.getItemId(mLeftViewIndex));
//            if (child.isSelected()) {
//                Log.d(TAG, "fillListLeft child from adapter is already selected - resetting");
//                child.setSelected(false);
//            }
//            addAndMeasureChild(child, 0);
//            leftEdge -= child.getMeasuredWidth();
//            mLeftViewIndex--;
//            mDisplayOffset -= child.getMeasuredWidth();
//        }
//    }
//
//    private void removeNonVisibleItems(final int dx) {
//        View child = getChildAt(0);
//        while (child != null && child.getRight() + dx <= 0) {
//            mDisplayOffset += child.getMeasuredWidth();
//            mRemovedViewQueue.offer(child);
//            removeViewInLayout(child);
//            mLeftViewIndex++;
//            child = getChildAt(0);
//
//        }
//
//        child = getChildAt(getChildCount() - 1);
//        while (child != null && child.getLeft() + dx >= getWidth()) {
//            mRemovedViewQueue.offer(child);
//            removeViewInLayout(child);
//            mRightViewIndex--;
//            child = getChildAt(getChildCount() - 1);
//        }
//    }
//
//    private void positionItems(final int dx) {
//        if (getChildCount() > 0) {
//            mDisplayOffset += dx;
//            int left = mDisplayOffset;
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                int childWidth = child.getMeasuredWidth();
//                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
//                left += childWidth + child.getPaddingRight();
//            }
//        }
//    }
//
//    public synchronized void scrollTo(int x) {
//        scrollToInt(x);
//        requestLayout();
//    }
//
//    private void scrollToInt(int x) {
//        Log.d(TAG, "SCROLL TO INT: " + mNextX + " " + 0 + " " + (x - mNextX) + " " + 0);
//        // Case position zero
//        if(x == 0) mScroller.startScroll(0, 0, 0, 0);
//        // Default
//        else mScroller.startScroll(mNextX, 0, x - mNextX, 0);
//    }
//
//    public void setPosition(int x) {
//        if (x < 0) return;
//        setPositionInt(x);
//        requestLayout();
//    }
//
//    private void setPositionInt(int x) {
//        Log.d(TAG, "POSITION INT : " + x);
//        if (mDataChanged) mCurrentX = x;
//        else mNextX = x;
//    }
//
//    public synchronized int getPosition() {
//        return mCurrentX;
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean handled = super.dispatchTouchEvent(ev);
//        handled |= mGesture.onTouchEvent(ev);
//        return handled;
//    }
//
//    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        synchronized (HorizontalListView.this) {
//            mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
//        }
//        requestLayout();
//
//        return true;
//    }
//
//    protected boolean onDown(MotionEvent e) {
//        mScroller.forceFinished(true);
//        return true;
//    }
//
//    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return HorizontalListView.this.onDown(e);
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//
//            synchronized (HorizontalListView.this) {
//                mNextX += (int) distanceX;
//            }
//            requestLayout();
//
//            return true;
//        }
//
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                if (isEventWithinView(e, child)) {
//                    if (mOnItemClicked != null) {
//                        mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex
//                                + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
//                    }
//                    if (mOnItemSelected != null) {
//                        mOnItemSelected.onItemSelected(HorizontalListView.this, child,
//                                mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
//                    }
//                    break;
//                }
//
//            }
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            int childCount = getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View child = getChildAt(i);
//                if (isEventWithinView(e, child)) {
//                    if (mOnItemLongClicked != null) {
//                        mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child,
//                                mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
//                    }
//                    break;
//                }
//
//            }
//        }
//
//        private boolean isEventWithinView(MotionEvent e, View child) {
//            Rect viewRect = new Rect();
//            int[] childPosition = new int[2];
//            child.getLocationOnScreen(childPosition);
//            int left = childPosition[0];
//            int right = left + child.getWidth();
//            int top = childPosition[1];
//            int bottom = top + child.getHeight();
//            viewRect.set(left, top, right, bottom);
//            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
//        }
//    };
//
//    private boolean isChildFullyVisible(View child) {
//        if (child.getRight() >= getWidth() || child.getLeft() <= 0)
//            return false;
//        else
//            return true;
//    }
//
//    private int computeTargetPosition(int item) {
//        if (mAdapter == null || item >= mAdapter.getCount())
//            return NO_SCROLL_FOR_ITEM;
//
//        long itemId = mAdapter.getItemId(item);
//
//        int i;
//        for (i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            long foundItemId = (Long) child.getTag(R.id.item_id);
//            if (itemId == foundItemId && isChildFullyVisible(child))
//                return NO_SCROLL_FOR_ITEM;
//        }
//
//        // TODO: make this calculation of the target position more precise
//        // The problem is that the list width might be slightly different
//        // than the related childwith with the itemMultiplier
//        float itemMultiplier = item + 0.0f; //0.6f;
//        float itemOffset = mChildWidth * itemMultiplier;
//        int targetPosition = (int) (itemOffset - getWidth() / 2);
//        // Log.d( TAG, "targetPosition = " + targetPosition + " itemOffset = " + itemOffset +
//        // " itemMultiplier = " + itemMultiplier);
//        if (targetPosition < 0)
//            return 0;
//        else if (targetPosition >= mContentWidth - getWidth())
//            return mContentWidth - getWidth();
//        else
//            return targetPosition;
//    }
//
//    public synchronized void setSelectedItem(int selected, int moveToSelectedItem) {
//        Log.d( TAG, "setSelectedItem: selected = " + selected + " count: " + mAdapter.getCount() + " move: " + moveToSelectedItem );
//        boolean selectedChanged = setSelectItemInt(selected);
//        int targetPosition = computeTargetPosition(selected);
//        //Log.d( TAG, "setSelectedItem: targetPosition = " + targetPosition);
//        if (moveToSelectedItem == MOVE_TO_NO || targetPosition == NO_SCROLL_FOR_ITEM) {
//            Log.d( TAG, "NO SCROLL " + moveToSelectedItem + " " + targetPosition);
//            if (!selectedChanged) return;
//        }
//        //    
//        else if (moveToSelectedItem == MOVE_TO_SCROLLED) {
//            Log.d( TAG, "SCROLL " + moveToSelectedItem + " " + targetPosition);
//            if (selectedChanged) scrollToInt(targetPosition);
//        }
//        // 
//        else {
//            Log.d( TAG, "DIRECT " + targetPosition);
//            if (selectedChanged) setPositionInt(targetPosition);
//        }
//
//        requestLayout();
//    }
//
//    private boolean setSelectItemInt(int selected) {
//        // Log.d( TAG, "setSelectedItemInt: selected = " + selected + " mOldSelected = " +
//        // mOldSelected);
//
//        if (mSelected == selected) {
//            Log.d(TAG, "setSelectedItemInt: item already selected = " + selected);
//            return false;
//        }
//        mOldSelected = mSelected;
//        mSelected = selected;
//
//        return true;
//    }
//
//    private void updateSelected() {
//        // Log.d( TAG, "updateSelectedInt" );
//        if (mAdapter == null || mSelected < 0 || mSelected >= mAdapter.getCount()) {
//            Log.w(TAG, "updateSelected: adapter empty or selected out of range");
//            return;
//        }
//
//        // Log.d( TAG, "updateSelectedInt: mSelected = " + mSelected + " mOldSelected = " +
//        // mOldSelected + " childCount = " + getChildCount());
//        long selectedId = mAdapter.getItemId(mSelected);
//        long oldSelectedId = -1;
//
//        if (mOldSelected >= 0 || mOldSelected < mAdapter.getCount())
//            oldSelectedId = mAdapter.getItemId(mOldSelected);
//
//        int i;
//        for (i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            long itemId = (Long) child.getTag(R.id.item_id);
//            if (itemId == selectedId) {
//                // Log.d( TAG, "updateSelectedInt: setting selected: child index = " + i +
//                // " itemId = " + itemId );
//                child.setSelected(true);
//            } else if (itemId == oldSelectedId) {
//                child.setSelected(false);
//            }
//        }
//    }
//}
