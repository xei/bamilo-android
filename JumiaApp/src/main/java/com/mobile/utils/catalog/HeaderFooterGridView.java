package com.mobile.utils.catalog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.mobile.newFramework.objects.catalog.Banner;
import com.mobile.view.fragments.CampaignPageFragment;

/**
 * Created by spereira on 2/27/15.
 */
public class HeaderFooterGridView extends RecyclerView {

    private static final String TAG = HeaderFooterGridView.class.getSimpleName();

    public static final int NO_HEADER = -1;

    public static final int NO_FOOTER = -2;

    private HeaderFooterGridLayoutManager mGridLayoutManager;

    public HeaderFooterGridView(Context context) {
        super(context);
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridLayoutManager(int columns) {
        mGridLayoutManager = new HeaderFooterGridLayoutManager(getContext(), columns);
        mGridLayoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    /* */
    public void showHeaderView() {
        Adapter adapter = getAdapter();
        if(adapter instanceof HeaderFooterInterface) {
            mGridLayoutManager.showHeaderView();
            ((HeaderFooterInterface) adapter).showHeaderView();
        }
    }

    public void hideHeaderView() {
        mGridLayoutManager.hideHeaderView();
        if(getAdapter() instanceof HeaderFooterInterface) {
            mGridLayoutManager.showHeaderView();
            ((HeaderFooterInterface) getAdapter()).showHeaderView();
        }
    }

    public void showFooterView() {
        Adapter adapter = getAdapter();
        if(adapter instanceof HeaderFooterInterface) {
            mGridLayoutManager.showFooterView();
            ((HeaderFooterInterface) adapter).showFooterView();
        }
    }

    public void hideFooterView() {
        Adapter adapter = getAdapter();
        if(adapter instanceof HeaderFooterInterface) {
            mGridLayoutManager.hideFooterView();
            ((HeaderFooterInterface) adapter).hideFooterView();
        }
    }

    public void setHeaderView(@Nullable Banner banner) {
        Adapter adapter = getAdapter();
        if(adapter instanceof HeaderFooterInterface) {
            mGridLayoutManager.showHeaderView();
            ((HeaderFooterInterface) adapter).setHeader(banner);
        }
    }

    public void setHeaderView(@Nullable View banner) {
        Adapter adapter = getAdapter();
        if(adapter instanceof HeaderFooterInterface) {
            mGridLayoutManager.showHeaderView();
            ((HeaderFooterInterface) adapter).setHeader(banner);
        }
    }

}
