package com.bamilo.android.core.presentation;

import com.bamilo.android.core.service.model.data.ordercancellation.CancellationRequestBody;
import com.bamilo.android.core.view.OrderCancellationView;

/**
 * Created by mohsen on 1/31/18.
 */

public interface OrderCancellationPresenter extends BasePresenter<OrderCancellationView> {
    void submitCancellationRequest(boolean isConnected, CancellationRequestBody requestBody);
}
