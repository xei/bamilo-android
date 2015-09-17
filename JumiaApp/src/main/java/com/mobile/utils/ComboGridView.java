package com.mobile.utils;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by alexandrapires on 9/11/15.
 */
public class ComboGridView extends RecyclerView {

    private static final String TAG = ComboGridView.class.getSimpleName();

    private GridLayoutManager mGridLayoutManager;

    public ComboGridView(Context context) {
        super(context);
    }

    public ComboGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ComboGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridLayoutManager(int columns) {
        mGridLayoutManager = new GridLayoutManager(getContext(), columns);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

    }


    public void addItemDecoration(int resourceDrawable) {
    /*    DividerItemDecoration divider = new DividerItemDecoration(getContext().getResources().getDrawable(resourceDrawable));
        if(divider != null)
            addItemDecoration(divider);*/
    }


}
