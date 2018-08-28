package com.bamilo.android.core.presentation;

import com.bamilo.android.core.view.OrdersListView;

/**
 * Created on 12/30/2017.
 */

public interface OrdersListPresenter extends BasePresenter<OrdersListView> {
    void loadOrdersList(int itemsPerPage, int page, boolean isConnected);
}
