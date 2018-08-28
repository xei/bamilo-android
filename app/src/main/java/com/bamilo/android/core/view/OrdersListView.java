package com.bamilo.android.core.view;

import com.bamilo.android.core.service.model.OrdersListResponse;

/**
 * Created on 12/30/2017.
 */

public interface OrdersListView extends BaseView {
    void performOrdersList(OrdersListResponse ordersListResponse);
}
