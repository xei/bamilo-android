package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.OrderCancellationResponse;
import com.bamilo.apicore.service.model.data.ordercancellation.CancellationRequestBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created by mohsen on 1/31/18.
 */

public class OrderCancellationInteractorImpl implements OrderCancellationInteractor {
    private static final String FIELD_ORDER_NUMBER = "orderNumber",
            FIELD_DESCRIPTION = "description", FIELD_ITEMS = "items",
            FIELD_SIMPLE_SKU = "simpleSku", FIELD_REASON_ID = "reasonId", FIELD_QUANTITY = "quantity";

    private BamiloApiService apiService;
    private SchedulerProvider schedulerProvider;
    private Gson gson;

    private ReplaySubject<OrderCancellationResponse> cancellationReplaySubject;
    private Subscription cancellationSubscription;

    @Inject
    public OrderCancellationInteractorImpl(BamiloApiService apiService, SchedulerProvider schedulerProvider, Gson gson) {
        this.apiService = apiService;
        this.schedulerProvider = schedulerProvider;
        this.gson = gson;
    }

    @Override
    public Observable<OrderCancellationResponse> submitCancellationRequest(CancellationRequestBody requestBody) {
        if (cancellationSubscription == null || cancellationSubscription.isUnsubscribed()) {
            cancellationReplaySubject = ReplaySubject.create();
            Map<String, String> fields = new HashMap<>();
            fields.put(FIELD_ORDER_NUMBER, requestBody.getOrderNumber());
            fields.put(FIELD_DESCRIPTION, requestBody.getDescription() != null ? requestBody.getDescription() : "");
            fields.putAll(createItemsMap(requestBody.getItems()));
            cancellationSubscription = apiService
                    .submitOrderCancellation(fields)
                    .map(new Func1<JsonObject, OrderCancellationResponse>() {
                        @Override
                        public OrderCancellationResponse call(JsonObject jsonObject) {
                            return gson.fromJson(jsonObject, OrderCancellationResponse.class);
                        }
                    })
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .subscribe(cancellationReplaySubject);
        }
        return cancellationReplaySubject.asObservable();
    }

    @Override
    public void destroy() {
        if (cancellationSubscription != null && !cancellationSubscription.isUnsubscribed()) {
            cancellationSubscription.unsubscribe();
        }

        cancellationSubscription = null;
        cancellationReplaySubject = null;
    }

    private Map<String, String> createItemsMap(List<CancellationRequestBody.Item> items) {
        Map<String, String> itemMap = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            CancellationRequestBody.Item item = items.get(0);
            itemMap.put(String.format(Locale.US, "%s[%d][%s]", FIELD_ITEMS, i, FIELD_SIMPLE_SKU), item.getSimpleSku());
            itemMap.put(String.format(Locale.US, "%s[%d][%s]", FIELD_ITEMS, i, FIELD_QUANTITY), String.valueOf(item.getQuantity()));
            itemMap.put(String.format(Locale.US, "%s[%d][%s]", FIELD_ITEMS, i, FIELD_REASON_ID), item.getReasonId());
        }
        return itemMap;
    }
}
