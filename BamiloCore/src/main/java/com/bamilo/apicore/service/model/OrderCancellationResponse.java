package com.bamilo.apicore.service.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by mohsen on 1/31/18.
 */

public class OrderCancellationResponse extends ServerResponse {

    public OrderCancellationResponse() {
        super(null, null);
    }

    public OrderCancellationResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    @Override
    public EventType getEventType() {
        return EventType.SUBMIT_ORDER_CANCELLATION;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {}
}
