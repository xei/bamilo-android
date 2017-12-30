package com.bamilo.apicore.service.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/19/2017.
 * All model objects should implement this interface
 */

public abstract class ServerResponse {

    @SerializedName("success")
    @Expose
    private boolean success;

    public ServerResponse(JsonObject jsonObject, Gson gson) {
        initializeWithJson(jsonObject, gson);
    }

    abstract EventType getEventType();

    abstract EventTask getEventTask();

    protected abstract void initializeWithJson(JsonObject jsonObject, Gson gson);

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
