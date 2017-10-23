package com.mobile.service.objects.home;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.home.model.BaseComponent;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/21/2017.
 */

public class HomePageComponents implements IJSONSerializable {
    private List<BaseComponent> components;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        jsonObject = jsonObject.optJSONObject(RestConstants.METADATA);
        JSONArray dataArray = jsonObject.optJSONArray(RestConstants.DATA);
        if (dataArray != null) {
            components = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                components.add(BaseComponent.createFromJson(dataArray.optJSONObject(i)));
            }
        }
        return false;
    }

    public List<BaseComponent> getComponents() {
        return components;
    }

    public void setComponents(List<BaseComponent> components) {
        this.components = components;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.COMPLETE_JSON;
    }
}
