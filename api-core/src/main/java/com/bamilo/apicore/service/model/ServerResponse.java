package com.bamilo.apicore.service.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created on 12/19/2017.
 * All model objects should implement this interface
 */

public abstract class ServerResponse<T> {
    private T data;

    public ServerResponse(JsonObject jsonObject, Gson gson) {
        initializeWithJson(jsonObject, gson);
    }

    abstract EventType getEventType();

    abstract EventTask getEventTask();

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    protected abstract void initializeWithJson(JsonObject jsonObject, Gson gson);
}
