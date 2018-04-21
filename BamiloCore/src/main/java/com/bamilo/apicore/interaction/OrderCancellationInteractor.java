package com.bamilo.apicore.interaction;

import com.bamilo.apicore.service.model.OrderCancellationResponse;
import com.bamilo.apicore.service.model.data.ordercancellation.CancellationRequestBody;

import rx.Observable;

/**
 * Created by mohsen on 1/31/18.
 */

public interface OrderCancellationInteractor extends BaseInteractor {
    Observable<OrderCancellationResponse> submitCancellationRequest(CancellationRequestBody requestBody);
}
