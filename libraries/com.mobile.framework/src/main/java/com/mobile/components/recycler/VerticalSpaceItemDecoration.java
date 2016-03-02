package com.mobile.components.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by msilva on 2/22/16.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceWidth;

    public VerticalSpaceItemDecoration(int verticalSpaceWidth) {
        this.mVerticalSpaceWidth = verticalSpaceWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.right = mVerticalSpaceWidth;
    }
}