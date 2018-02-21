package com.bamilo.apicore.presentation;

import com.bamilo.apicore.view.ItemTrackingView;

/**
 * Created by mohsen on 1/27/18.
 */

public interface ItemTrackingPresenter extends BasePresenter<ItemTrackingView> {
    void loadOrderDetails(String orderNumber, boolean isConnected);
}
