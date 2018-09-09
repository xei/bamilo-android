package com.bamilo.android.core.interaction;

import com.bamilo.android.core.scheduler.SchedulerProvider;
import com.bamilo.android.core.service.BamiloApiService;
import com.bamilo.android.core.service.model.ItemTrackingResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
                            // TODO: 2/7/18 REMOVE THESE FUCKIN LINES AFTER RESOLVING SERVER ISSUE
                            /**
                             * it will remove inconsistent part of request body
                             */
                            try {
                                JsonObject metadata = jsonObject.get("metadata").getAsJsonObject();
                                JsonArray packages = metadata.get("packages").getAsJsonArray();
                                for (JsonElement pkgElement : packages) {
                                    JsonObject pkg;
                                    pkg = pkgElement.getAsJsonObject();
                                    JsonArray products = pkg.get("products").getAsJsonArray();
                                    for (JsonElement productElement : products) {
                                        JsonObject product = productElement.getAsJsonObject();
                                        JsonElement filters = product.get("filters");
                                        if (filters instanceof JsonArray) {
                                            product.add("filters", null);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
