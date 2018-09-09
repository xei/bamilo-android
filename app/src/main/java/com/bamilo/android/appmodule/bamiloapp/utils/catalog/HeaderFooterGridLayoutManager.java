package com.bamilo.android.appmodule.bamiloapp.utils.catalog;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * The grid layout manager for GridView.
 * Created by spereira on 2/27/15.
 *
 * // TODO: Improve the approach for header and footer views.
 *
 */
public class HeaderFooterGridLayoutManager extends GridLayoutManager {

    private final static int ONE_COLUMN = 1;

    private boolean hasFooterView = false;

    private boolean hasHeaderView = false;

    /**
     * Constructor
     * @param context - the application context
     * @param spanCount - the number of columns
     */
    public HeaderFooterGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    /**
     * Constructor
     * @param context - the application context
     * @param spanCount - the number of columns
     * @param orientation - the orientation
     * @param reverseLayout - the flag to reverse the layout (used to show RTL layout)
     */
    public HeaderFooterGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    /**
     * Show the header view.
     */
    public void showHeaderView() {
        // Mark
        hasHeaderView = true;
        // Validate
        if(!hasFooterView) setSpanSizeLookup(spanSizeLookup);
    }

    /**
     * Show the footer view.
     */
    public void showFooterView() {
        // Mark
        hasFooterView = true;
        // Validate
        if(!hasHeaderView) setSpanSizeLookup(spanSizeLookup);
    }

    /**
     * Hide the header view.
     */
    public void hideHeaderView() {
        // Mark
        hasHeaderView = false;
        // Validate
        if(!hasFooterView) setSpanSizeLookup(new DefaultSpanSizeLookup());
    }

    /**
     * Hide the header view.
     */
    public void hideFooterView() {
        // Mark
        hasFooterView = false;
        // Validate
        if(!hasHeaderView) setSpanSizeLookup(new DefaultSpanSizeLookup());
    }

    /**
     * Validate the current is the header view.
     * @param position - a list position
     * @return true or false
     */
    private boolean isHeaderPosition(int position) {
        return hasHeaderView && position == 0;
    }

    /**
     * Validate the current is the header view.
     * @param position - a list position
     * @return true or false
     */
    private boolean isFooterPosition(int position) {
        return hasFooterView && position == getItemCount() - 1;
    }

    /**
     * The span size for header and footer.
     */
    private SpanSizeLookup spanSizeLookup = new SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return isHeaderPosition(position) || isFooterPosition(position) ? getSpanCount() : ONE_COLUMN;
        }
    };

}
