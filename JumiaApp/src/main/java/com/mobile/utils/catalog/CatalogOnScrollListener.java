package com.mobile.utils.catalog;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.widget.AbsListView;

import com.mobile.utils.imageloader.RocketImageLoader;

/**
 * Created by spereira on 2/27/15.
 */
public abstract class CatalogOnScrollListener extends OnScrollListener {

    private static final String TAG = CatalogOnScrollListener.class.getSimpleName();


    public CatalogOnScrollListener() {
        super();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        Log.i(TAG, "ON SCROLL STATE CHANGED: " + newState);
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            RocketImageLoader.getInstance().stopProcessingQueue();
        } else {
            RocketImageLoader.getInstance().startProcessingQueue();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);


    }

    public abstract void onLoadMore(int current_page);

}
