package com.bamilo.android.core.presentation;

import com.bamilo.android.core.view.ItemTrackingView;

/**
 * Created by mohsen on 1/27/18.
 */

public interface ItemTrackingPresenter extends BasePresenter<ItemTrackingView> {
    void loadOrderDetails(String orderNumber, boolean isConnected);
}
