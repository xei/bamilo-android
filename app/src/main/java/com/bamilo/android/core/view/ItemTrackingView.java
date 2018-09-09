package com.bamilo.android.core.view;

import com.bamilo.android.core.service.model.data.itemtracking.CompleteOrder;

/**
 * Created by mohsen on 1/27/18.
 */

public interface ItemTrackingView extends BaseView {
    void performOrderDetails(CompleteOrder order);
}
