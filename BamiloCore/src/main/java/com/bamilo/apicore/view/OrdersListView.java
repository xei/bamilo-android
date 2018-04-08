package com.bamilo.apicore.view;

import com.bamilo.apicore.service.model.OrdersListResponse;
import com.bamilo.apicore.service.model.data.orders.OrderListItem;

import java.util.List;

/**
 * Created on 12/30/2017.
 */

public interface OrdersListView extends BaseView {
    void performOrdersList(OrdersListResponse ordersListResponse);
}
