package com.bamilo.apicore.service.model;

import com.bamilo.apicore.service.model.data.BaseComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created on 12/19/2017.
 */

public class HomeResponse extends ServerResponse<List<BaseComponent>> {

    @SerializedName("data")
    @Expose
    private List<BaseComponent> components;

    public HomeResponse() {
        super(null, null);
    }

    public HomeResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    @Override
    EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

    @Override
    EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (jsonObject != null && jsonObject.has(JsonConstants.ELEMENT_DATA)) {
            components = new ArrayList<>();
            JsonArray array = jsonObject.get(JsonConstants.ELEMENT_DATA).getAsJsonArray();
            for (int i = 0; i < array.size() ; i++ ){
                JsonObject componentObject = array.get(i).getAsJsonObject();
                String type = componentObject.get(JsonConstants.ELEMENT_TYPE).getAsString();
                if (type != null) {
                    if (BaseComponent.componentsMap.containsKey(type)) {
                        components.add((BaseComponent) gson.fromJson(componentObject, BaseComponent.componentsMap.get(type)));
                    }
                }
            }
            setData(components);
        }
    }

    public List<BaseComponent> getComponents() {
        return components;
    }

    public void setComponents(List<BaseComponent> components) {
        this.components = components;
    }
}
