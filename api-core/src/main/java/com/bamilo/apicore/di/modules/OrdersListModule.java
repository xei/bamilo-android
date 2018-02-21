package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.interaction.OrdersListInteractor;
import com.bamilo.apicore.interaction.OrdersListInteractorImpl;
import com.bamilo.apicore.presentation.OrdersListPresenter;
import com.bamilo.apicore.presentation.OrdersListPresenterImpl;
import com.bamilo.apicore.view.OrdersListView;

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
