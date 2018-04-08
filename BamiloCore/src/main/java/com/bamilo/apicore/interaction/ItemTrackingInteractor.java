package com.bamilo.apicore.interaction;

import com.bamilo.apicore.service.model.ItemTrackingResponse;

import rx.Observable;

/**
 * Created by mohsen on 1/27/18.
 */

public interface ItemTrackingInteractor extends BaseInteractor {
    Observable<ItemTrackingResponse> loadOrderDetails(String orderNumber);
}
