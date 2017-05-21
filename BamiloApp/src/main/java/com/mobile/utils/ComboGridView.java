package com.mobile.utils;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by alexandrapires on 9/11/15.
 */
public class ComboGridView extends RecyclerView {

    public static final String TAG = ComboGridView.class.getSimpleName();

    public ComboGridView(Context context) {
        super(context);
        init();
    }

    public ComboGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComboGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Disable nested scrolling behaviour
        this.setNestedScrollingEnabled(false);
    }

    public void setGridLayoutManager(int columns) {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), columns);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

    }

}
