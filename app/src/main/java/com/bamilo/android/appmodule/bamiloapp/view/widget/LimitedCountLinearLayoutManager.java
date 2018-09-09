package com.bamilo.android.appmodule.bamiloapp.view.widget;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class LimitedCountLinearLayoutManager extends LinearLayoutManager {
    private int itemMeasuredSize = 0;
    private int mItemsPerPage;
    @FloatRange(from=0.0,to=1.0)
    private float lastItemVisiblePartAmount = .5F;


    public LimitedCountLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int itemsPerPage) {
        super(context, orientation, reverseLayout);

        mItemsPerPage = itemsPerPage;
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return super.checkLayoutParams(lp) && lp.width == getItemSize();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return setProperItemSize(super.generateDefaultLayoutParams());
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return setProperItemSize(super.generateLayoutParams(lp));
    }

    private RecyclerView.LayoutParams setProperItemSize(RecyclerView.LayoutParams lp) {
        int itemSize = getItemSize();
        if (getOrientation() == HORIZONTAL) {
            lp.width = itemSize;
        } else {
            lp.height = itemSize;
        }
        this.itemMeasuredSize = itemSize;
        return lp;
    }

    private int getItemSize() {
        int pageSize = getOrientation() == HORIZONTAL ? getWidth() : getHeight();
        return Math.round((float) pageSize / (mItemsPerPage - 1 + lastItemVisiblePartAmount));
    }

    public void setLastItemVisiblePartAmount(@FloatRange(from=0.0,to=1.0) float lastItemVisiblePartAmount) {
        this.lastItemVisiblePartAmount = lastItemVisiblePartAmount;
    }

    public float getLastItemVisiblePartAmount() {
        return lastItemVisiblePartAmount;
    }

    public int getItemsPerPage() {
        return mItemsPerPage;
    }

    public int getItemMeasuredSize() {
        return itemMeasuredSize;
    }
}
