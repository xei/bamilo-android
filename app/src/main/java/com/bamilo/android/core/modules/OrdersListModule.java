package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.OrdersListInteractor;
import com.bamilo.android.core.interaction.OrdersListInteractorImpl;
import com.bamilo.android.core.presentation.OrdersListPresenter;
import com.bamilo.android.core.presentation.OrdersListPresenterImpl;
import com.bamilo.android.core.view.OrdersListView;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 12/30/2017.
 */

@Module
public class OrdersListModule {
    private OrdersListView ordersListView;

    public OrdersListModule(OrdersListView ordersListView) {
        this.ordersListView = ordersListView;
    }

    @Provides
    public OrdersListView provideOrdersListView() {
        return ordersListView;
    }

    @Provides
    public OrdersListPresenter provideOrdersListPresentater(OrdersListPresenterImpl ordersListPresentater) {
        ordersListPresentater.setView(ordersListView);
        return ordersListPresentater;
    }

    @Provides
    public OrdersListInteractor provideOrdersListInteractor(OrdersListInteractorImpl ordersListInteractor) {
        return ordersListInteractor;
    }
}
