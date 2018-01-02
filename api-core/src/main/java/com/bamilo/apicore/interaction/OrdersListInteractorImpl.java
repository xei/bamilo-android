package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.OrdersListResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created on 12/30/2017.
 */

public class OrdersListInteractorImpl implements OrdersListInteractor {
    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<OrdersListResponse> ordersListReplaySubject;
    private Subscription ordersListSubscription;

    @Inject
    public OrdersListInteractorImpl(BamiloApiService bamiloApiService, SchedulerProvider schedulerProvider, Gson mGson) {
        this.bamiloApiService = bamiloApiService;
        this.schedulerProvider = schedulerProvider;
        this.mGson = mGson;
    }

    @Override
    public Observable<OrdersListResponse> loadOrdersList(int itemsPerPage, int page) {
        if (ordersListSubscription == null || ordersListSubscription.isUnsubscribed()) {
            ordersListReplaySubject = ReplaySubject.create();
            ordersListSubscription = bamiloApiService.loadOrdersList(itemsPerPage, page)
                    .map(new Func1<JsonObject, OrdersListResponse>() {
                        @Override
                        public OrdersListResponse call(JsonObject jsonObject) {
                            return new OrdersListResponse(jsonObject, mGson);
                        }
                    })
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .subscribe(ordersListReplaySubject);
        }
        return ordersListReplaySubject.asObservable();
    }

    @Override
    public void destroy() {
        if (ordersListSubscription != null && !ordersListSubscription.isUnsubscribed()) {
            ordersListSubscription.unsubscribe();
        }

        ordersListSubscription = null;
        ordersListReplaySubject = null;
    }
}
