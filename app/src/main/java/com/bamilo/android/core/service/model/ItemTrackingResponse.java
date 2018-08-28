package com.bamilo.android.core.service.model;

import com.bamilo.android.core.service.model.data.itemtracking.CompleteOrder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class ItemTrackingResponse extends ServerResponse{

    @Expose
    @SerializedName(JsonConstants.RestConstants.METADATA)
    private CompleteOrder completeOrder;

    public ItemTrackingResponse() {
        super(null, null);
    }

    public ItemTrackingResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    @Override
    public EventType getEventType() {
        return EventType.TRACK_ORDER_EVENT;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (jsonObject != null && gson != null) {
            ItemTrackingResponse temp = gson.fromJson(jsonObject, getClass());
            this.setSuccess(temp.isSuccess());
            this.setMessages(temp.getMessages());
            this.completeOrder = temp.completeOrder;
        }
    }

    public CompleteOrder getCompleteOrder() {
        return completeOrder;
    }

    public void setCompleteOrder(CompleteOrder completeOrder) {
        this.completeOrder = completeOrder;
    }
}
