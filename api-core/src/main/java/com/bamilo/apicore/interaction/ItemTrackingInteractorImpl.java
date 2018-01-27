package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.ItemTrackingResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created by mohsen on 1/27/18.
 */

public class ItemTrackingInteractorImpl implements ItemTrackingInteractor {
    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<ItemTrackingResponse> itemTrackingReplaySubject;
    private Subscription itemTrackingSubscription;

    @Inject
    public ItemTrackingInteractorImpl(BamiloApiService bamiloApiService, SchedulerProvider schedulerProvider, Gson mGson) {
        this.bamiloApiService = bamiloApiService;
        this.schedulerProvider = schedulerProvider;
        this.mGson = mGson;
    }

    @Override
    public Observable<ItemTrackingResponse> loadOrderDetails(String orderNumber) {
        if (itemTrackingSubscription == null || itemTrackingSubscription.isUnsubscribed()) {
            itemTrackingReplaySubject = ReplaySubject.create();
            itemTrackingSubscription = bamiloApiService.loadOrderDetails(orderNumber)
                    .map(new Func1<JsonObject, ItemTrackingResponse>() {
                        @Override
                        public ItemTrackingResponse call(JsonObject jsonObject) {
                            return new ItemTrackingResponse(jsonObject, mGson);
                        }
                    })
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .subscribe(itemTrackingReplaySubject);
        }
        return itemTrackingReplaySubject.asObservable();
    }

    @Override
    public void destroy() {
        if (itemTrackingSubscription != null && !itemTrackingSubscription.isUnsubscribed()) {
            itemTrackingSubscription.unsubscribe();
        }

        itemTrackingSubscription = null;
        itemTrackingReplaySubject = null;
    }
}
