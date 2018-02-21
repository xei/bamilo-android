package com.bamilo.apicore.presentation;

import com.bamilo.apicore.service.model.data.ordercancellation.CancellationRequestBody;
import com.bamilo.apicore.view.OrderCancellationView;

/**
 * Created by mohsen on 1/31/18.
 */

public interface OrderCancellationPresenter extends BasePresenter<OrderCancellationView> {
    void submitCancellationRequest(boolean isConnected, CancellationRequestBody requestBody);
}
