package com.mobile.utils.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by alexandra pires 31/08/2015.
 * Gridview for variation products in variations page
 */
public class VariationProductsGridView extends RecyclerView {

    private static final String TAG = VariationProductsGridView.class.getSimpleName();

    private GridLayoutManager mGridLayoutManager;

    public VariationProductsGridView(Context context) {
        super(context);
    }

    public VariationProductsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VariationProductsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridLayoutManager(Context context,int columns) {
        mGridLayoutManager = new GridLayoutManager(context, columns);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(mGridLayoutManager);
    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

    }



}
