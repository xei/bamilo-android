package com.mobile.interfaces;

import android.view.View;

/**
 * Created by rsoares on 10/2/15.
 */
public interface OnWishListViewHolderClickListener {
    void onItemClick(View view);
    void onClickDeleteItem(View view);
    void onClickAddToCart(View view);
    void onClickVariation(View view);

}
