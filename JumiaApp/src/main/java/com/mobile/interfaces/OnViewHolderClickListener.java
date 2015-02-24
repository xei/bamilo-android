package com.mobile.interfaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Interface to be used like on item click listener with RecyclerView.
 * @author sergiopereira
 */
public interface OnViewHolderClickListener {

    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, View view, int position, Object extra);

}
