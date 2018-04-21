package com.bamilo.apicore.view;

import com.bamilo.apicore.service.model.data.itemtracking.CompleteOrder;

/**
 * Created by mohsen on 1/27/18.
 */

public interface ItemTrackingView extends BaseView {
    void performOrderDetails(CompleteOrder order);
}
