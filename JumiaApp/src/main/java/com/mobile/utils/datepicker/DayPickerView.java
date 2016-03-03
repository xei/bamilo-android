/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobile.utils.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.mobile.utils.datepicker.DatePickerDialog.OnDateChangedListener;
import com.mobile.utils.datepicker.SimpleMonthAdapter.CalendarDay;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.akquinet.android.androlog.Log;

/**
 * This displays a list of months in a calendar format with selectable days.
 */
public class DayPickerView extends ListView implements OnScrollListener, OnDateChangedListener {

    private static final String TAG = "MonthFragment";

    // Affects when the month selection will change while scrolling up
    protected static final int SCROLL_HYST_WEEKS = 2;
    // How long the GoTo fling animation should last
    protected static final int GOTO_SCROLL_DURATION = 250;
    // How long to wait after receiving an onScrollStateChanged notification
    // before acting on it
    protected static final int SCROLL_CHANGE_DELAY = 40;
    // The number of days to display in each week
    public static final int DAYS_PER_WEEK = 7;
    public static int LIST_TOP_OFFSET = -1; // so that the top line will be
    // under the separator
    // You can override these numbers to get a different appearance
    protected int mNumWeeks = 6;
    protected boolean mShowWeekNumber = false;
    protected int mDaysPerWeek = 7;
    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    // These affect the scroll speed and feel
    protected float mFriction = 1.0f;

    protected Context mContext;
    protected Handler mHandler;

    // highlighted time
    protected CalendarDay mSelectedDay = new CalendarDay();
    protected SimpleMonthAdapter mAdapter;

    protected CalendarDay mTempDay = new CalendarDay();

    private static float mScale = 0;
    // When the week starts; numbered like Time.<WEEKDAY> (e.g. SUNDAY=0).
    protected int mFirstDayOfWeek;
    // The last name announced by accessibility
    protected CharSequence mPrevMonthName;
    // which month should be displayed/highlighted [0-11]
    protected int mCurrentMonthDisplayed;
    // used for tracking during a scroll
    protected long mPreviousScrollPosition;
    // used for tracking what state listview is in
    protected int mPreviousScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    // used for tracking what state listview is in
    protected int mCurrentScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private final DatePickerController mController;
    private boolean mPerformingScroll;

    public DayPickerView(Context context, DatePickerController controller) {
        super(context);
        mHandler = new Handler();
        mController = controller;
        mController.registerOnDateChangedListener(this);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setDrawSelectorOnTop(false);
        init(context);
        onDateChanged();
        installAccessibilityDelegate();
    }

    public void init(Context context) {
        mContext = context;
        setUpListView();
        setUpAdapter();
        setAdapter(mAdapter);
    }

    public void onChange() {
        setUpAdapter();
        setAdapter(mAdapter);
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected void setUpAdapter() {
        if (mAdapter == null) {
            mAdapter = new SimpleMonthAdapter(getContext(), mController);
        } else {
            mAdapter.setSelectedDay(mSelectedDay);
            mAdapter.notifyDataSetChanged();
        }
        // refresh the view with the new parameters
        mAdapter.notifyDataSetChanged();
    }

    /*
     * Sets all the required fields for the list view. Override this method to
     * set a different list view behavior.
     */
    @SuppressLint("NewApi")
    protected void setUpListView() {
        // Transparent background on scroll
        setCacheColorHint(0);
        // No dividers
        setDivider(null);
        // Items are clickable
        setItemsCanFocus(true);
        // The thumb gets in the way, so disable it
        setFastScrollEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(this);
        setFadingEdgeLength(0);

        // Make the scrolling behavior nicer
        setFriction(ViewConfiguration.getScrollFriction() * mFriction);

    }

    /**
     * This moves to the specified time in the view. If the time is not already
     * in range it will move the list so that the first of the month containing
     * the time is at the top of the view. If the new time is already in view
     * the list will not be scrolled unless forceScroll is true. This time may
     * optionally be highlighted as selected as well.
     *
     * @param animate
     *            Whether to scroll to the given time or just redraw at the
     *            new location
     * @param setSelected
     *            Whether to set the given time as selected
     * @param forceScroll
     *            Whether to recenter even if the time is already
     *            visible
     * @return Whether or not the view animated to the new location
     */
    @SuppressLint("NewApi")
    public boolean goTo(CalendarDay day, boolean animate, boolean setSelected, boolean forceScroll) {

        // Set the selected day
        if (setSelected) {
            mSelectedDay.set(day);
        }

        mTempDay.set(day);
        final int position = (day.year - mController.getMinYear())
                * SimpleMonthAdapter.MONTHS_IN_YEAR + day.month;

        View child;
        int i = 0;
        int top = 0;
        // Find a child that's completely in the view
        do {
            child = getChildAt(i++);
            if (child == null) {
                break;
            }
            top = child.getTop();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "child at " + (i - 1) + " has top " + top);
            }
        } while (top < 0);

        // Compute the first and last position visible
        int selectedPosition;
        if (child != null) {
            selectedPosition = getPositionForView(child);
        } else {
            selectedPosition = 0;
        }

        if (setSelected) {
            mAdapter.setSelectedDay(mSelectedDay);
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "GoTo position " + position);
        }
        // Check if the selected day is now outside of our visible range
        // and if so scroll to the month that contains it
        if (position != selectedPosition || forceScroll) {
            setMonthDisplayed(mTempDay);
            mPreviousScrollState = OnScrollListener.SCROLL_STATE_FLING;
            if (animate) {
                smoothScrollToPositionFromTop(position, LIST_TOP_OFFSET, GOTO_SCROLL_DURATION);
                return true;
            } else {
                postSetSelection(position);
            }
        } else if (setSelected) {
            setMonthDisplayed(mSelectedDay);
        }
        return false;
    }

    public void postSetSelection(final int position) {
        clearFocus();
        post(new Runnable() {

            @Override
            public void run() {
                setSelection(position);
            }
        });
        onScrollStateChanged(this, OnScrollListener.SCROLL_STATE_IDLE);
    }

    /**
     * Updates the title and selected month if the view has moved to a new
     * month.
     */
    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        SimpleMonthView child = (SimpleMonthView) view.getChildAt(0);
        if (child == null) {
            return;
        }

        // Figure out where we are
        long currScroll = view.getFirstVisiblePosition() * child.getHeight() - child.getBottom();
        mPreviousScrollPosition = currScroll;
        mPreviousScrollState = mCurrentScrollState;
    }

    /**
     * Sets the month displayed at the top of this view based on time. Override
     * to add custom events when the title is changed.
     */
    protected void setMonthDisplayed(CalendarDay date) {
        mCurrentMonthDisplayed = date.month;
        invalidateViews();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // use a post to prevent re-entering onScrollStateChanged before it
        // exits
        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    protected ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable();

    protected class ScrollStateRunnable implements Runnable {
        private int mNewState;

        /**
         * Sets up the runnable with a short delay in case the scroll state
         * immediately changes again.
         *
         * @param view
         *            The list view that changed state
         * @param scrollState
         *            The new state it changed to
         */
        public void doScrollStateChange(AbsListView view, int scrollState) {
            mHandler.removeCallbacks(this);
            mNewState = scrollState;
            mHandler.postDelayed(this, SCROLL_CHANGE_DELAY);
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            mCurrentScrollState = mNewState;
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "new scroll state: " + mNewState + " old state: " + mPreviousScrollState);
            }
            // Fix the position after a scroll or a fling ends
            if (mNewState == OnScrollListener.SCROLL_STATE_IDLE
                    && mPreviousScrollState != OnScrollListener.SCROLL_STATE_IDLE
                    && mPreviousScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                mPreviousScrollState = mNewState;
                int i = 0;
                View child = getChildAt(i);
                while (child != null && child.getBottom() <= 0) {
                    child = getChildAt(++i);
                }
                if (child == null) {
                    // The view is no longer visible, just return
                    return;
                }
                int firstPosition = getFirstVisiblePosition();
                int lastPosition = getLastVisiblePosition();
                boolean scroll = firstPosition != 0 && lastPosition != getCount() - 1;
                final int top = child.getTop();
                final int bottom = child.getBottom();
                final int midpoint = getHeight() / 2;
                if (scroll && top < LIST_TOP_OFFSET) {
                    if (bottom > midpoint) {
                        smoothScrollBy(top, GOTO_SCROLL_DURATION);
                    } else {
                        smoothScrollBy(bottom, GOTO_SCROLL_DURATION);
                    }

                }
            } else {
                mPreviousScrollState = mNewState;
            }
        }
    }

    /**
     * Gets the position of the view that is most prominently displayed within the list view.
     */
    public int getMostVisiblePosition() {
        final int firstPosition = getFirstVisiblePosition();
        final int height = getHeight();

        int maxDisplayedHeight = 0;
        int mostVisibleIndex = 0;
        int i = 0;
        int bottom = 0;
        while (bottom < height) {
            View child = getChildAt(i);
            if (child == null) {
                break;
            }
            bottom = child.getBottom();
            int displayedHeight = Math.min(bottom, height) - Math.max(0, child.getTop());
            if (displayedHeight > maxDisplayedHeight) {
                mostVisibleIndex = i;
                maxDisplayedHeight = displayedHeight;
            }
            i++;
        }
        return firstPosition + mostVisibleIndex;
    }

    @Override
    public void onDateChanged() {
        goTo(mController.getSelectedDay(), false, true, true);
    }

    /**
     * Attempts to return the date that has accessibility focus.
     *
     * @return The date that has accessibility focus, or {@code null} if no date
     *         has focus.
     */
    private CalendarDay findAccessibilityFocus() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof SimpleMonthView) {
                final CalendarDay focus = ((SimpleMonthView) child).getAccessibilityFocus();
                if (focus != null) {
                    // Clear focus to avoid ListView bug in Jelly Bean MR1.
                    ((SimpleMonthView) child).clearAccessibilityFocus();
                    return focus;
                }
            }
        }

        return null;
    }

    /**
     * Attempts to restore accessibility focus to a given date. No-op if {@code day} is {@code null}
     * .
     *
     * @param day
     *            The date that should receive accessibility focus
     * @return {@code true} if focus was restored
     */
    private boolean restoreAccessibilityFocus(CalendarDay day) {
        if (day == null) {
            return false;
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof SimpleMonthView) {
                if (((SimpleMonthView) child).restoreAccessibilityFocus(day)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void layoutChildren() {
        final CalendarDay focusedDay = findAccessibilityFocus();
        super.layoutChildren();
        if (mPerformingScroll) {
            mPerformingScroll = false;
        } else {
            restoreAccessibilityFocus(focusedDay);
        }
    }

    private void installAccessibilityDelegate() {
        // The accessibility delegate enables customizing accessibility behavior
        // via composition as opposed as inheritance. The main benefit is that
        // one can write a backwards compatible application by setting the delegate
        // only if the API level is high enough i.e. the delegate is part of the APIs.
        // The easiest way to achieve that is by using the support library which
        // takes the burden of checking API version and knowing which API version
        // introduced the delegate off the developer.
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() {

            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                // Note that View.onInitializeAccessibilityNodeInfo was introduced in
                // ICS and we would like to tweak a bit the text that is reported to
                // accessibility services via the AccessibilityNodeInfo.
                event.setItemCount(-1);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(View host,
                                                          AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                // Note that View.onInitializeAccessibilityNodeInfo was introduced in
                // ICS and we would like to tweak a bit the text that is reported to
                // accessibility services via the AccessibilityNodeInfo.
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);
            }

            @Override
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action != AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD &&
                        action != AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD) {
                    return super.performAccessibilityAction(host, action, args);
                }

                // Figure out what month is showing.
                int firstVisiblePosition = getFirstVisiblePosition();
                int month = firstVisiblePosition % 12;
                int year = firstVisiblePosition / 12 + mController.getMinYear();
                CalendarDay day = new CalendarDay(year, month, 1);

                // Scroll either forward or backward one month.
                if (action == AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD) {
                    day.month++;
                    if (day.month == 12) {
                        day.month = 0;
                        day.year++;
                    }
                } else if (action == AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD) {
                    View firstVisibleView = getChildAt(0);
                    // If the view is fully visible, jump one month back. Otherwise, we'll just jump
                    // to the first day of first visible month.
                    if (firstVisibleView != null && firstVisibleView.getTop() >= -1) {
                        // There's an off-by-one somewhere, so the top of the first visible item
                        // will
                        // actually be -1 when it's at the exact top.
                        day.month--;
                        if (day.month == -1) {
                            day.month = 11;
                            day.year--;
                        }
                    }
                }

                // Go to that month.
                Utils.tryAccessibilityAnnounce(host, getMonthAndYearString(day));
                goTo(day, true, false, true);
                mPerformingScroll = true;
                return true;
            }
        });
    }

    private String getMonthAndYearString(CalendarDay day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.year, day.month, day.day);

        StringBuffer sbuf = new StringBuffer();
        sbuf.append(new DateFormatSymbols(Locale.getDefault()).getWeekdays()[cal
                .get(Calendar.MONTH)]);
        sbuf.append(" ");
        sbuf.append(YEAR_FORMAT.format(cal.getTime()));
        return sbuf.toString();
    }
}
