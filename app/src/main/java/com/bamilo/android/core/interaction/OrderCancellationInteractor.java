package com.bamilo.android.core.interaction;

import com.bamilo.android.core.service.model.OrderCancellationResponse;
import com.bamilo.android.core.service.model.data.ordercancellation.CancellationRequestBody;

import rx.Observable;

/**
 * Created by mohsen on 1/31/18.
 */

public interface OrderCancellationInteractor extends BaseInteractor {
    Observable<OrderCancellationResponse> submitCancellationRequest(CancellationRequestBody requestBody);
}
