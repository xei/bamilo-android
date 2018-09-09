package com.bamilo.android.core.interaction;

import com.bamilo.android.core.service.model.OrdersListResponse;

import rx.Observable;

/**
 * Created on 12/30/2017.
 */

public interface OrdersListInteractor extends BaseInteractor {
    Observable<OrdersListResponse> loadOrdersList(int itemsPerPage, int page);
}
