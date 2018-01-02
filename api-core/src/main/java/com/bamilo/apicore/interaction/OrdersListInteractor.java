package com.bamilo.apicore.interaction;

import com.bamilo.apicore.service.model.OrdersListResponse;

import rx.Observable;

/**
 * Created on 12/30/2017.
 */

public interface OrdersListInteractor extends BaseInteractor {
    Observable<OrdersListResponse> loadOrdersList(int itemsPerPage, int page);
}
