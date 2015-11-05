package com.mobile.interfaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Interface to be used like on item click listener with RecyclerView.
 * @author sergiopereira
 */
public interface OnProductViewHolderClickListener {

    void onHeaderClick(String targetType, String url, String title);

    void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position);

    void onWishListClick(View view, RecyclerView.Adapter<?> adapter, int position);

}
