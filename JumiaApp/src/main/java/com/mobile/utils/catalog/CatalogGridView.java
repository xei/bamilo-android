package com.mobile.utils.catalog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by spereira on 2/27/15.
 */
public class CatalogGridView extends RecyclerView {

    private static final String TAG = CatalogGridView.class.getSimpleName();

    public static final int NO_HEADER = -1;

    public static final int NO_FOOTER = -2;

    private CatalogGridLayoutManager mGridLayoutManager;

    public CatalogGridView(Context context) {
        super(context);
    }

    public CatalogGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CatalogGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridLayoutManager(int columns) {
        mGridLayoutManager = new CatalogGridLayoutManager(getContext(), columns);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        //
        if(adapter instanceof CatalogGridAdapter) {
            //showHeaderView();
            showFooterView();
        }

    }

    /* */
    public void showHeaderView() {
        mGridLayoutManager.showHeaderView();
        ((CatalogGridAdapter) getAdapter()).showHeaderView();
    }

    public void hideHeaderView() {
        mGridLayoutManager.hideHeaderView();
        ((CatalogGridAdapter) getAdapter()).hideHeaderView();
    }

    public void showFooterView() {
        mGridLayoutManager.showFooterView();
        ((CatalogGridAdapter) getAdapter()).showFooterView();
    }

    public void hideFooterView() {
        mGridLayoutManager.hideFooterView();
        ((CatalogGridAdapter) getAdapter()).hideFooterView();
    }

}
