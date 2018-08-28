package com.bamilo.android.appmodule.bamiloapp.interfaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Interface to be used like on item click listener with RecyclerView.
 * @author sergiopereira
 */
public interface OnProductViewHolderClickListener {

    void onHeaderClick(String target, String title);

    void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position);

//    void onVariationClick(View view, RecyclerView.Adapter<?> adapter);

    /**
     * Generic method that allows processing a click on a specific view item (any kind of item) inside a  mother view
     * Usages:
     * whishlist: add to wiishlist
     * Combos Page: update total price when check/uncheck an item
     *
     * */
    void onViewHolderItemClick(View view, RecyclerView.Adapter<?> adapter, int position);

}
