package com.bamilo.android.core.service.model;

import com.bamilo.android.core.service.model.data.home.BaseComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/19/2017.
 */

public class HomeResponse extends ServerResponse {

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
    public EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (gson != null && jsonObject != null) {
            jsonObject = jsonObject.getAsJsonObject(JsonConstants.RestConstants.METADATA);
            if (jsonObject != null && jsonObject.has(JsonConstants.ELEMENT_DATA)) {
                components = new ArrayList<>();
                JsonArray array = jsonObject.get(JsonConstants.ELEMENT_DATA).getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject componentObject = array.get(i).getAsJsonObject();
                    String type = componentObject.get(JsonConstants.ELEMENT_TYPE).getAsString();
                    if (type != null) {
                        if (BaseComponent.componentsMap.containsKey(type)) {
                            components.add((BaseComponent) gson.fromJson(componentObject, BaseComponent.componentsMap.get(type)));
                        }
                    }
                }

                Map<String, Integer> baseComponents = new HashMap<>();
                for (BaseComponent component : components) {
                    String key = component.getType();
                    String componentType = component.getType();
                    if (baseComponents.containsKey(componentType)) {
                        baseComponents.put(key, baseComponents.get(key) + 1);
                    } else {
                        baseComponents.put(key, 0);
                    }
                    int indexOfComponent = baseComponents.get(componentType);
                    component.setTeaserId(indexOfComponent);
                }
            }
        }
    }

    public List<BaseComponent> getComponents() {
        return components;
    }

    public void setComponents(List<BaseComponent> components) {
        this.components = components;
    }
}
