package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.OrdersListInteractor;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.OrdersListResponse;
import com.bamilo.apicore.view.OrdersListView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * Created on 12/30/2017.
 */

public class OrdersListPresenterImpl implements OrdersListPresenter {
    private OrdersListInteractor ordersListInteractor;

    private OrdersListView view;
    private Subscription subscription;

    @Inject
    public OrdersListPresenterImpl(OrdersListInteractor ordersListInteractor) {
        this.ordersListInteractor = ordersListInteractor;
        subscription = Subscriptions.empty();
    }

    @Override
    public void setView(OrdersListView view) {
        this.view = view;
    }

    @Override
    public void loadOrdersList(int itemsPerPage, int page, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(EventType.GET_MY_ORDERS_LIST_EVENT, true);
        }

        subscription = ordersListInteractor.loadOrdersList(itemsPerPage, page)
                .subscribe(new Action1<OrdersListResponse>() {
                    @Override
                    public void call(OrdersListResponse ordersListResponse) {
                        if (view != null) {
                            view.toggleProgress(EventType.GET_MY_ORDERS_LIST_EVENT, false);
                            view.performOrdersList(ordersListResponse);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(EventType.GET_MY_ORDERS_LIST_EVENT, false);
                        }

                        if (!isConnected) {
                            if (view != null) {
                                view.showOfflineMessage(EventType.GET_MY_ORDERS_LIST_EVENT);
                            }
                        } else if (throwable instanceof HttpException) {
                            view.showConnectionError(EventType.GET_MY_ORDERS_LIST_EVENT);
                        } else {
                            view.showRetry(EventType.GET_MY_ORDERS_LIST_EVENT);
                        }
                    }
                });
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = null;
        ordersListInteractor.destroy();

        view = null;
        ordersListInteractor = null;
    }
}
